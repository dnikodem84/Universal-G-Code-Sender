package com.willwinder.universalgcodesender;

import com.willwinder.universalgcodesender.firmware.FirmwareSetting;
import com.willwinder.universalgcodesender.firmware.grbl.GrblBuildOptions;
import com.willwinder.universalgcodesender.firmware.marlin.GetMarlinBuildInfoCommand;
import com.willwinder.universalgcodesender.firmware.marlin.GetMarlinSettingsCommand;
import com.willwinder.universalgcodesender.firmware.marlin.GetMarlinStatusCommand;
import com.willwinder.universalgcodesender.firmware.marlin.MarlinBuildOptions;
import com.willwinder.universalgcodesender.firmware.marlin.MarlinCommand;
import com.willwinder.universalgcodesender.firmware.marlin.MarlinVersion;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.MessageType;
import static com.willwinder.universalgcodesender.utils.ControllerUtils.sendAndWaitForCompletion;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that implements an initialization protocol for GRBL and keeps an internal state of the
 * connection process. The query process will not require the controller to be reset which is needed
 * for controllers such as grblHAL or GRBL_ESP32.
 * <p/>
 * 1. It will first to query the machine for a status report 10 times, if the status is HOLD or ALARM
 * a blank line will be sent to see if the controller is responsive
 * 2. Fetch the build info for the controller
 * 3. Fetch the parser state
 * 4. Start the status poller
 *
 * @author Joacim Breiler
 */
public class MarlinControllerInitializer implements IControllerInitializer {
    private static final Logger LOGGER = Logger.getLogger(MarlinControllerInitializer.class.getSimpleName());
    private final AtomicBoolean isInitializing = new AtomicBoolean(false);
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final MarlinController controller;
    private MarlinVersion version = MarlinVersion.NO_VERSION;
    private MarlinBuildOptions options = new MarlinBuildOptions();

    public MarlinControllerInitializer(MarlinController controller) {
        this.controller = controller;
    }

    @Override
    public boolean initialize() throws ControllerException {
        // Only allow one initialization at a time
        if (isInitializing.get() || isInitialized.get()) {
            return false;
        }

        controller.resetBuffers();

        controller.setControllerState(ControllerState.CONNECTING);
        isInitializing.set(true);
        try {
            // Some controllers need this wait before we can query its status
            Thread.sleep(2000);
            if (!MarlinUtils.isControllerResponsive(controller)) {
                isInitializing.set(false);
                controller.getMessageService().dispatchMessage(MessageType.INFO, "*** Device is in a holding or alarm state and needs to be reset\n");
                controller.issueSoftReset();
                return false;
            }

            // Some controllers need this wait before we can query the rest of its information
            Thread.sleep(2000);
            fetchControllerVersion();
            fetchControllerState();
            sendAndWaitForCompletion(controller, new MarlinCommand("M211"),2000);
            sendAndWaitForCompletion(controller, new MarlinCommand("M121"),2000);
            sendAndWaitForCompletion(controller, new MarlinCommand("M302 S1"),2000);
            controller.getMessageService().dispatchMessage(MessageType.INFO, String.format("*** Connected to: %s\n", version.toString()));
            isInitialized.set(true);
            isInitializing.set(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            isInitialized.set(false);
            isInitializing.set(false);
            closeConnection();
            throw new ControllerException(e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            controller.closeCommPort();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not properly close the connection", e);
        }
    }

    private void fetchControllerState() throws InterruptedException {
        controller.getMessageService().dispatchMessage(MessageType.INFO, "*** Fetching device settings\n");
        GetMarlinSettingsCommand settings = sendAndWaitForCompletion(controller, new GetMarlinSettingsCommand(),5000);
        controller.getMessageService().dispatchMessage(MessageType.INFO, "*** Fetching device state\n");
        GetMarlinStatusCommand status = sendAndWaitForCompletion(controller, new GetMarlinStatusCommand());
        
    }

    private void fetchControllerVersion() throws InterruptedException {
        controller.getMessageService().dispatchMessage(MessageType.INFO, "*** Fetching device version\n");
        GetMarlinBuildInfoCommand buildInfo = sendAndWaitForCompletion(controller, new GetMarlinBuildInfoCommand(),5000);
        if (buildInfo.hasVersion()) {
            version = buildInfo.getVersion();
        } else {
            controller.getMessageService().dispatchMessage(MessageType.ERROR, "*** Could not detect the Marlin version\n");
            throw new ControllerException("Could not detect the Marlin version");            
        }
        
        options = buildInfo.getBuildOptions();        
    }

    @Override
    public void reset() {
        isInitializing.set(false);
        isInitialized.set(false);
        version = MarlinVersion.NO_VERSION;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized.get();
    }

    public boolean isInitializing() {
        return isInitializing.get();
    }

    public MarlinVersion getVersion() {
        return version;
    }

    public MarlinBuildOptions getOptions() {
        return options;
    }
}
