package com.willwinder.universalgcodesender;

import com.willwinder.universalgcodesender.communicator.ICommunicator;
import com.willwinder.universalgcodesender.communicator.MarlinCommunicator;
import com.willwinder.universalgcodesender.connection.ConnectionDriver;
import com.willwinder.universalgcodesender.firmware.IFirmwareSettings;
import com.willwinder.universalgcodesender.firmware.IOverrideManager;
import com.willwinder.universalgcodesender.firmware.grbl.GrblCapabilitiesConstants;
import com.willwinder.universalgcodesender.firmware.grbl.GrblCommandLogger;
import com.willwinder.universalgcodesender.firmware.grbl.GrblOverrideManager;
import com.willwinder.universalgcodesender.firmware.marlin.MarlinCommand;
import com.willwinder.universalgcodesender.firmware.marlin.MarlinCommandCreator;
import com.willwinder.universalgcodesender.firmware.marlin.MarlinFirmwareSettings;
//import com.willwinder.universalgcodesender.gcode.GcodeCommandCreator;
import com.willwinder.universalgcodesender.gcode.ICommandCreator;
import com.willwinder.universalgcodesender.gcode.util.Code;
import com.willwinder.universalgcodesender.gcode.util.GcodeUtils;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.ControllerStatus;
import com.willwinder.universalgcodesender.listeners.ControllerStatusBuilder;
import com.willwinder.universalgcodesender.listeners.MessageType;
import com.willwinder.universalgcodesender.listeners.OverridePercents;
import com.willwinder.universalgcodesender.model.Axis;
import static com.willwinder.universalgcodesender.model.CommunicatorState.COMM_CHECK;
import com.willwinder.universalgcodesender.model.Overrides;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.UnitUtils;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import com.willwinder.universalgcodesender.types.MarlinGcodeCommand;
import com.willwinder.universalgcodesender.utils.ThreadHelper;
import org.apache.commons.lang3.StringUtils;

import javax.swing.Timer;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Logger;
import static com.willwinder.universalgcodesender.model.CommunicatorState.COMM_IDLE;
import com.willwinder.universalgcodesender.model.PartialPosition;
import static com.willwinder.universalgcodesender.model.UnitUtils.Units.MM;
import java.util.logging.Level;

public class MarlinController extends AbstractController {

    private static final Logger logger = Logger.getLogger(MarlinController.class.getSimpleName());
    private final DecimalFormat decimalFormatter = new DecimalFormat("0.0000", Localization.dfs);
    private final MarlinFirmwareSettings firmwareSettings;
//    private final Capabilities capabilities;
    private Capabilities capabilities = new Capabilities();
    private final String firmwareVersion;
    private ControllerStatus controllerStatus
            //            = ControllerStatusBuilder.newInstance()
            //            .setState(ControllerState.DISCONNECTED)
            //            .setWorkCoord(Position.ZERO)
            //            .setMachineCoord(Position.ZERO)
            //            .setOverrides(OverridePercents.EMTPY_OVERRIDE_PERCENTS)
            //            .build();
            = ControllerStatusBuilder.newInstance()
                    .setState(ControllerState.DISCONNECTED)
                    .setWorkCoord(Position.ZERO)
                    .setMachineCoord(Position.ZERO)
                    .build();
    private ControllerState controllerState;
    private StatusPollTimer positionPollTimer;
    private int outstandingPolls;
    private IControllerInitializer initializer;
    private IOverrideManager overrideManager;
    private GrblCommandLogger commandLogger;
    public MarlinController() {
        this(new MarlinCommunicator(), new MarlinCommandCreator());
    }

    public MarlinController(ICommunicator comm, ICommandCreator commandCreator) { //MarlinCommunicator marlinCommunicator) {
        super(comm, commandCreator);
        this.initializer = new MarlinControllerInitializer(this);

        firmwareSettings = new MarlinFirmwareSettings();
        controllerState = ControllerState.UNKNOWN;

        firmwareVersion = "Marlin unknown version";
        positionPollTimer = new StatusPollTimer(this);
        this.overrideManager = new GrblOverrideManager(this, comm, messageService);
        this.commandLogger = new GrblCommandLogger(messageService);
        addListener(commandLogger);

    }

    @Override
    protected Boolean isIdleEvent() {
        if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
            return getCommunicatorState() == COMM_IDLE || getCommunicatorState() == COMM_CHECK;
        }
        // Otherwise let the abstract controller decide.
        return true;
    }

    @Override
    protected void closeCommBeforeEvent() {
        positionPollTimer.stop();
    }

    @Override
    protected void closeCommAfterEvent() {
        initializer.reset();
    }

    @Override
    protected void cancelSendBeforeEvent() throws Exception {
        boolean paused = isPaused();
        // The cancel button is left enabled at all times now, but can only be
        // used for some versions of GRBL.
        if (paused && !this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
            throw new Exception("Cannot cancel while paused with this version of Marlin. Reconnect to reset Marlin.");
        }

        // If we're canceling a "jog" state
        if (capabilities.hasJogging() && controllerStatus != null &&
                controllerStatus.getState() == ControllerState.JOG) {
            dispatchConsoleMessage(MessageType.VERBOSE, String.format(">>> %s\n", MarlinUtils.MARLIN_JOG_CANCEL_COMMAND));
            sendCommandImmediately(new MarlinCommand(MarlinUtils.MARLIN_JOG_CANCEL_COMMAND));     
        }
        // Otherwise, check if we can get fancy with a soft reset.
        else if (!paused && this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
            try {
                this.pauseStreaming();
            } catch (Exception e) {
                // Oh well, was worth a shot.
                System.out.println("Exception while trying to issue a soft reset: " + e.getMessage());
            }
        }
    }

    @Override
    protected void cancelSendAfterEvent() {
        if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME) && this.getStatusUpdatesEnabled()) {
            // Trigger the position listener to watch for the machine to stop.
//            this.attemptsRemaining = 50;
//            this.isCanceling = true;
//            this.lastLocation = null;
        }
    }

    @Override
    public void cancelJog() throws Exception {
        if (capabilities.hasCapability(GrblCapabilitiesConstants.HARDWARE_JOGGING)) {
            dispatchConsoleMessage(MessageType.VERBOSE, String.format(">>> %s\n", MarlinUtils.MARLIN_JOG_CANCEL_COMMAND));
            sendCommandImmediately(new MarlinCommand(MarlinUtils.MARLIN_JOG_CANCEL_COMMAND));     
        } else {
            cancelSend();
        }
    }

    @Override
    protected void pauseStreamingEvent() throws Exception {
        if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
            sendCommandImmediately(new MarlinCommand(MarlinUtils.MARLIN_PAUSE_COMMAND));            
        }
    }

    @Override
    protected void resumeStreamingEvent() throws Exception {
        if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
            sendCommandImmediately(new MarlinCommand(MarlinUtils.MARLIN_RESUME_COMMAND));
        }
    }

    @Override
    protected void isReadyToSendCommandsEvent() throws ControllerException {
        if (!isCommOpen()) {
            throw new ControllerException(Localization.getString("controller.exception.booting"));
        }
    }

    @Override
    protected void isReadyToStreamCommandsEvent() throws Exception {
        isReadyToSendCommandsEvent();
        if (this.controllerStatus != null && this.controllerStatus.getState() == ControllerState.ALARM) {
            throw new Exception(Localization.getString("grbl.exception.Alarm"));
        }
    }

    @Override
    public Boolean openCommPort(ConnectionDriver connectionDriver, String port, int portRate) throws Exception {
        if (isCommOpen()) {
            throw new Exception("Comm port is already open.");
        }

        initializer.reset();
        positionPollTimer.stop();
        comm.connect(connectionDriver, port, portRate);
        setControllerState(ControllerState.CONNECTING);
        messageService.dispatchMessage(MessageType.INFO, "*** Connecting to " + connectionDriver.getProtocol() + port + ":" + portRate + "\n");

        initialize();
        return isCommOpen();
    }

    private void initialize() {

        if (comm.areActiveCommands()) {
            messageService.dispatchMessage(MessageType.INFO, "*** Canceling current stream\n");
            cancelCommands();
            resetBuffers();
        }

        setControllerState(ControllerState.CONNECTING);
        if (initializer.isInitialized()) {
            return;
        }

        ThreadHelper.invokeLater(() -> {
            positionPollTimer.stop();
            if (!initializer.initialize()) {
                return;
            }

            capabilities = MarlinUtils.getMarlinStatusCapabilities(2, 'd', null);
            logger.info("Identified controller capabilities: " + capabilities);

            // Toggle the state to force UI update
            setControllerState(ControllerState.CONNECTING);
            positionPollTimer.start();
        });
    }

    @Override
    protected void rawResponseHandler(String response) {
        if (response.endsWith("start")) {
            handleStartMessage();
        } else if (getActiveCommand().isPresent()) {
            String commandString = getActiveCommand().get().getCommandString();
            if (commandString.startsWith("M114")) {
                handleStatusMessage(response);
                if (getActiveCommand().get().getCommandNumber() >= 0 && !getActiveCommand().get().isGenerated()) {
                    dispatchConsoleMessage(MessageType.INFO, "SPECIAL STATUS HANDLER"+commandString + ": " + response + "\n");
                }
            } else {
                dispatchConsoleMessage(MessageType.INFO, commandString + ": " + response + "\n");
            }

            if (MarlinGcodeCommand.isOkErrorResponse(response)) {
                try {
                    commandComplete();//commandComplete(response);
                } catch (Exception e) {
                    this.dispatchConsoleMessage(MessageType.ERROR, Localization.getString("controller.error.response")
                            + " <" + response + ">: " + e.getMessage());
                }
            }
        } else if (MarlinGcodeCommand.isEchoResponse(response)) {
            dispatchConsoleMessage(MessageType.INFO, "< " + response + "\n");
        } else if (response.startsWith("FIRMWARE_NAME:")) {
            logger.info("READY: " + response);
        } else if (MarlinGcodeCommand.isOkErrorResponse(response)) {
            logger.info(response + getActiveCommand().orElse(null));
        } else if (StringUtils.isNotEmpty(response)) {
            logger.info(response + getActiveCommand().orElse(null));
            dispatchConsoleMessage(MessageType.INFO, "Unknown response: " + response + "\n");

            //listeners.forEach(l -> l.messageForConsole(ControllerListener.MessageType.INFO, command.getCommandString() + " -> " + command.getResponse() + "\n"));
        }
    }

    private void handleStatusMessage(String response) {
        outstandingPolls = 0;
        if (response.contains("X:") && response.contains("Y:") && response.contains("Z:")) {
            try {
                double x = decimalFormatter.parse(StringUtils.substringBetween(response, "X:", " ")).doubleValue();
                double y = decimalFormatter.parse(StringUtils.substringBetween(response, "Y:", " ")).doubleValue();
                double z = decimalFormatter.parse(StringUtils.substringBetween(response, "Z:", " ")).doubleValue();
                controllerStatus = ControllerStatusBuilder.newInstance(controllerStatus)
                        .setMachineCoord(new Position(x, y, z, UnitUtils.Units.MM))
                        .setWorkCoord(new Position(x, y, z, UnitUtils.Units.MM))
                        .setState(controllerState)
                        .build();

                dispatchStatusString(controllerStatus);
            } catch (ParseException e) {
                // Never mind
            }
        }
    }

    private void handleStartMessage() {
        dispatchConsoleMessage(MessageType.INFO, "[ready]\n");
        setCurrentState(COMM_IDLE);
        controllerState = ControllerState.IDLE;
        ThreadHelper.invokeLater(() -> {
//            try {
//                comm.queueCommand(new GcodeCommand("M211"));
//                comm.queueCommand(new GcodeCommand("M503"));
//                comm.queueCommand(new GcodeCommand("M121"));
//                comm.queueCommand(new GcodeCommand("M302 S1"));
//                comm.streamCommands();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            positionPollTimer.stop();//stopPollingPosition();
            positionPollTimer.start();// = createPositionPollTimer();
//            beginPollingPosition();
        }, 5000);
    }
//
//    @Override
//    protected void statusUpdatesEnabledValueChanged(boolean enabled) {
//
//    }
//
//    @Override
//    protected void statusUpdatesRateValueChanged(int rate) {
//
//    }
//
//    @Override
//    public void sendOverrideCommand(Overrides command) {
//
//    }
//
//    @Override
//    public Boolean handlesAllStateChangeEvents() {
//        return false;
//    }

    @Override
    public Capabilities getCapabilities() {
        return capabilities;
    }

    @Override
    public IFirmwareSettings getFirmwareSettings() {
        return firmwareSettings;
    }

    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public ControllerStatus getControllerStatus() {
        return controllerStatus;
    }

//    /**
//     * Create a timer which will execute GRBL's position polling mechanism.
//     */
//    private Timer createPositionPollTimer() {
//        // Action Listener for polling mechanism.
//        ActionListener actionListener = actionEvent -> EventQueue.invokeLater(() -> {
//            try {
//                if (outstandingPolls == 0) {
//                    outstandingPolls++;
//                    comm.queueCommand(new GcodeCommand("M114", "M114", null, 0, true));
//                    comm.streamCommands();
//                } else {
//                    // If a poll is somehow lost after 20 intervals,
//                    // reset for sending another.
//                    outstandingPolls++;
//                    if (outstandingPolls >= 20) {
//                        outstandingPolls = 0;
//                    }
//                }
//            } catch (Exception ex) {
//                dispatchConsoleMessage(MessageType.INFO, Localization.getString("controller.exception.sendingstatus")
//                        + " (" + ex.getMessage() + ")\n");
//                ex.printStackTrace();
//            }
//        });
//
//        return new Timer(2000, actionListener);
//    }
//    /**
//     * Begin issuing GRBL status request commands.
//     */
//    private void beginPollingPosition() {
//        // Start sending '?' commands if supported and enabled.
//        if (this.getStatusUpdatesEnabled()) {
//            if (!positionPollTimer.isRunning()) {
//                outstandingPolls = 0;
//                positionPollTimer.start();
//            }
//        }
//    }
    @Override
    public void requestStatusReport() throws Exception {
        if (!this.isCommOpen()) {
            throw new RuntimeException("Not connected to the controller");
        }
        comm.queueCommand(new GcodeCommand("M114", "M114", "", 0, true));
//        new MarlinS
        comm.streamCommands();
//        comm.sendByteImmediately(GrblUtils.GRBL_STATUS_COMMAND);
    }
//    /**
//     * Stop issuing GRBL status request commands.
//     */
//    private void stopPollingPosition() {
//        if (positionPollTimer.isRunning()) {
//            positionPollTimer.stop();
//        }
//    }

    @Override
    public void jogMachine(PartialPosition distance, double feedRate) throws Exception {
        GcodeCommand unitCmd = createCommand(GcodeUtils.unitCommand(distance.getUnits()));      
        GcodeCommand moveStyleCommand = createCommand("G91");      
        
        GcodeCommand command = createCommand(GcodeUtils.generatePlainMoveCommand("G0", feedRate, distance));
        command.setTemporaryParserModalChange(true);
        sendCommandImmediately(unitCmd);
        sendCommandImmediately(moveStyleCommand);
        sendCommandImmediately(command);
        
        restoreParserModalState();
    }
    @Override
    public void jogMachineTo(PartialPosition position, double feedRate) throws Exception {
        GcodeCommand unitCmd = createCommand(GcodeUtils.unitCommand(position.getUnits()));      
        GcodeCommand moveStyleCommand = createCommand("G90");      
        
        GcodeCommand command = createCommand(GcodeUtils.generatePlainMoveCommand("G0", feedRate, position));
        command.setTemporaryParserModalChange(true);
        sendCommandImmediately(unitCmd);
        sendCommandImmediately(moveStyleCommand);
        sendCommandImmediately(command);
        
        restoreParserModalState();
    }
    @Override
    public void performHomingCycle() throws Exception {
        sendCommandImmediately(new GcodeCommand("G28"));
    }

    @Override
    public void returnToHome(double safetyHeightInMm) throws Exception {
        if (isIdle()) {
            if (true) {
                throw new RuntimeException("Currently Unsupported");
            }
            // Convert the safety height to the same units as the current gcode state
            UnitUtils.Units currentUnit = getCurrentGcodeState().getUnits();
            double safetyHeight = safetyHeightInMm * UnitUtils.scaleUnits(MM, currentUnit);

            // If Z is less than zero, raise it before further movement.
            double currentZPosition = getControllerStatus().getWorkCoord().getPositionIn(currentUnit).get(Axis.Z);
            if (currentZPosition < safetyHeight) {
                String moveToSafetyHeightCommand = GcodeUtils.GCODE_RETURN_TO_Z_ZERO_LOCATION;
                if (safetyHeight > 0) {
                    moveToSafetyHeightCommand = GcodeUtils.generateMoveCommand("G90 G0", 0, new PartialPosition(null, null, safetyHeight, currentUnit));
                }
                sendCommandImmediately(createCommand(moveToSafetyHeightCommand));
            }
            sendCommandImmediately(createCommand(GcodeUtils.GCODE_RETURN_TO_XY_ZERO_LOCATION));
            sendCommandImmediately(createCommand(GcodeUtils.GCODE_RETURN_TO_Z_ZERO_LOCATION));
        }
    }

    @Override
    public void pauseStreaming() throws Exception {
        super.pauseStreaming();
    }

    @Override
    protected void setControllerState(ControllerState controllerState) {
        controllerStatus = ControllerStatusBuilder
                .newInstance(controllerStatus)
                .setState(controllerState)
                .build();

        dispatchStatusString(controllerStatus);
    }

    @Override
    public boolean getStatusUpdatesEnabled() {
        return positionPollTimer.isEnabled();
    }

    @Override
    public void setStatusUpdatesEnabled(boolean enabled) {
        positionPollTimer.setEnabled(enabled);
    }

    @Override
    public int getStatusUpdateRate() {
        return positionPollTimer.getUpdateInterval();
    }

    @Override
    public void setStatusUpdateRate(int rate) {
        positionPollTimer.setUpdateInterval(rate);
    }

    @Override
    public IOverrideManager getOverrideManager() {
        return overrideManager;
    }
}
