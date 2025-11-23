/*
    Copyright 2022-2024 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.universalgcodesender.firmware.fluidnc;

import com.willwinder.universalgcodesender.IController;
import com.willwinder.universalgcodesender.IFileService;
import com.willwinder.universalgcodesender.Utils;
import com.willwinder.universalgcodesender.firmware.FirmwareSetting;
import com.willwinder.universalgcodesender.firmware.FirmwareSettingsException;
import com.willwinder.universalgcodesender.firmware.IFirmwareSettings;
import com.willwinder.universalgcodesender.firmware.IFirmwareSettingsListener;
import com.willwinder.universalgcodesender.firmware.fluidnc.commands.FluidNCCommand;
import com.willwinder.universalgcodesender.firmware.fluidnc.commands.GetFirmwareSettingsCommand;
import com.willwinder.universalgcodesender.firmware.fluidnc.commands.GetSetCurrentConfigFilename;
import com.willwinder.universalgcodesender.firmware.fluidnc.commands.SystemCommand;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.Motor;
import com.willwinder.universalgcodesender.model.UnitUtils;
import com.willwinder.universalgcodesender.types.CommandException;
import com.willwinder.universalgcodesender.utils.ControllerUtils;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Joacim Breiler
 */
public class FluidNCSettings implements IFirmwareSettings {
    private static final Logger LOGGER = Logger.getLogger(FluidNCSettings.class.getName());

    private final Map<String, FirmwareSetting> settings = new ConcurrentHashMap<>();
    private final IController controller;
    private final Set<IFirmwareSettingsListener> listeners = Collections.synchronizedSet(new HashSet<>());
    
    public final static String NO_PIN = "NO_PIN";
    
    public FluidNCSettings(IController controller) {
        this.controller = controller;
    }

    public void refresh() throws FirmwareSettingsException, CommandException {
        GetFirmwareSettingsCommand firmwareSettingsCommand = new GetFirmwareSettingsCommand();

        try {
            ControllerUtils.sendAndWaitForCompletion(controller, firmwareSettingsCommand,6000*3);
        } catch (InterruptedException e) {
            throw new FirmwareSettingsException("Timed out waiting for the controller settings", e);
        }


        if (firmwareSettingsCommand.isOk()) {
            Map<String, String> responseSettings = firmwareSettingsCommand.getSettings();
            responseSettings.keySet().forEach(key -> {
                String value = responseSettings.get(key);
                FirmwareSetting firmwareSetting = new FirmwareSetting(key, value, "", "", "");
                settings.put(key.toLowerCase(), firmwareSetting);                   

                listeners.forEach(l -> l.onUpdatedFirmwareSetting(firmwareSetting));
            });
        }
    }

    @Override
    public Optional<FirmwareSetting> getSetting(String key) {
        return Optional.ofNullable(settings.get(key));
    }
    
    @Override
    public void saveFirmwareSettings() throws FirmwareSettingsException {
        try {
            System.err.println("saveFirmwareSettings");
            FluidNCConfigYamlSettingsFile settings = new FluidNCConfigYamlSettingsFile(getConfigFilename(), controller);
            settings.applyAllSettings(this);
            if (!settings.hasUgsFlag()) {
                settings.addUgsFlagIfNeeded();                
            }
            settings.uploadConfig();
            System.err.println("saveFirmwareSettings FINISHED");
            
        } catch (Exception e) {
            throw new FirmwareSettingsException("Couldn't save settings to the controller", e);
        }                
    }
//    public boolean patchFluidNcConfig(String key, String value) throws FirmwareSettingsException, InterruptedException, IOException {        
//        GetFirmwareSettingsCommand getConfigCommand = new GetFirmwareSettingsCommand();
//        ControllerUtils.sendAndWaitForCompletion(controller, getConfigCommand);
//        Map<String,Object> config = getConfigCommand.getSettingsTree();
//        Optional<FirmwareSetting> meta = this.getSetting("meta");
////        if ( meta.isPresent() && meta.get().getValue().endsWith(" [UGS]")) {
////            // UGS Controlled config. 
////        } else {
////            return false;
////        }
//       // config.put(key, value);
//        String m = "" + config.get("meta");
//        if (m.endsWith(" [UGS]")) {
//            
//        }else {
//            config.put("meta",m + " [UGS]");
//        }
//        String split[] = key.split("[/]");
//        int idx = 0;
//        Map<String,Object> insertPoint = config;
//        boolean going = true;
//        while (going) {
//            String subKeyName = split[idx];
//            if (idx == (split.length-1)) {
//                // End of list. Do Insert.
//                insertPoint.put(subKeyName, value);
//            } else {
//                if ( insertPoint.containsKey(subKeyName) ) {                    
//                    insertPoint = (Map<String,Object>)insertPoint.get(subKeyName);
//                } else {
//                    Map<String,Object> newKey = new LinkedHashMap<>();
//                    insertPoint.put(subKeyName, newKey);
//                    insertPoint = newKey;
//                }
//            }
//            idx++;
//            if (idx == (split.length)) {
//                going = false;
//            }
//            
//        }
//        final DumperOptions options = new DumperOptions();
//        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        options.setPrettyFlow(true);
//        Yaml yaml = new Yaml(options);
//        String newConfig = yaml.dump(config);
////        String configFilename = getConfigFilename();
//        System.err.println("-NEW CONFIG----------------------------------------------------");
//        System.err.println(newConfig);
//        System.err.println("---------------------------------------------------------------");
//        System.err.println("About to upload");
//        IFileService fileservice = controller.getFileService();
//        fileservice.uploadFile("/localfs/"+getConfigFilename(), newConfig.getBytes());
//        setConfigFilename(getConfigFilename());
//        System.err.println("Finished Upload");
//         GetFirmwareSettingsCommand getConfigCommandB = new GetFirmwareSettingsCommand();
//        ControllerUtils.sendAndWaitForCompletion(controller, getConfigCommandB);
//        System.err.println("-Controller Config---------------------------------------------");
//        System.err.println(getConfigCommandB.getResponse());
//        System.err.println("---------------------------------------------------------------");
//        
//        
//        // TODO: 
////        1) patch uart1/passhthrough_port so its valid. 
////        2) reset config filename then reboot controller ()
////        try {
////            controller.issueSoftReset();
////        } catch (Exception e) {
////            throw new FirmwareSettingsException("Error performing soft reset");
////        }
//        
////        System.err.println(newConfig);
////        System.out.println(newConfig);
//        return true;
//    }
    @Override
    public FirmwareSetting setValue(String key, String value) throws FirmwareSettingsException {
        try {
            key = key.toLowerCase();
            System.err.println(">>>>>>>>>>>setValue("+key+","+value+")");
            if (!settings.containsKey(key) || !settings.get(key).getValue().equals(value)) {
                FluidNCCommand systemCommand = new FluidNCCommand("$/" + key + "=" + value);
                ControllerUtils.sendAndWaitForCompletion(controller, systemCommand,6000);
                System.err.println ("Response:" + systemCommand.getResponse());
                if (systemCommand.isOk()) {
                    if (systemCommand.getResponse().contains("Runtime setting of Pin objects is not supported")) {                        
                        Logger.getLogger(FluidNCSettings.class.getName()).log(Level.WARNING, "Runtime setting of Pin objects is not supported : settings will only be applied after controller reboot");
                    }

                    FirmwareSetting firmwareSetting = new FirmwareSetting(key, value, "", "", "");
                    settings.put(key, firmwareSetting);
                    listeners.forEach(l -> l.onUpdatedFirmwareSetting(firmwareSetting));
                }                
            }
        } catch (Exception e) {
            throw new FirmwareSettingsException("Couldn't store setting", e);
        }

        return getSetting(key).orElse(null);
    }
    @Override
    public String getConfigFilename() throws FirmwareSettingsException {
        try {
            GetSetCurrentConfigFilename cmd = new GetSetCurrentConfigFilename();
            ControllerUtils.sendAndWaitForCompletion(controller, cmd,6000);
            return cmd.GetFilename();
        } catch ( Exception e) {
            throw new FirmwareSettingsException("Couldn't get Config Filename", e);    
        }        
    }
    @Override
    public void setConfigFilename(String newFilename) throws FirmwareSettingsException {
        try {
            GetSetCurrentConfigFilename cmd = new GetSetCurrentConfigFilename(newFilename);
            ControllerUtils.sendAndWaitForCompletion(controller, cmd, 6000);            
        } catch ( Exception e) {
            throw new FirmwareSettingsException("Couldn't get Config Filename", e);    
        }        
    }    
    
    @Override
    public void addListener(IFirmwareSettingsListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(IFirmwareSettingsListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public boolean isHomingEnabled() throws FirmwareSettingsException {
        return true;
    }

    @Override
    public void setHomingEnabled(boolean enabled) {

    }

    @Override
    public UnitUtils.Units getReportingUnits() {
        FirmwareSetting firmwareSetting = getSetting("report_inches").orElse(new FirmwareSetting("report_inches", "false"));
        if (firmwareSetting.getValue().equalsIgnoreCase("false")) {
            return UnitUtils.Units.MM;
        } else {
            return UnitUtils.Units.INCH;
        }
    }

    @Override
    public List<FirmwareSetting> getAllSettings() {
        return new ArrayList<>(settings.values());
    }

    @Override
    public boolean isHardLimitsEnabled() {
        return settingEquals("axes/x/motor0/hard_limits",true) ||
               settingEquals("axes/x/motor1/hard_limits",true) ||
               settingEquals("axes/y/motor0/hard_limits",true) ||
               settingEquals("axes/y/motor1/hard_limits",true) ||
               settingEquals("axes/z/motor0/hard_limits",true) ||
               settingEquals("axes/z/motor1/hard_limits",true);
    }
    
    private boolean checkMotorHasEndstop( Axis axis, Motor motor) {
        return settingNotEqualsIgnoreCase("axes/"+axis.name().toLowerCase()+"/"+motor+"/limit_neg_pin", NO_PIN) ||
               settingNotEqualsIgnoreCase("axes/"+axis.name().toLowerCase()+"/"+motor+"/limit_pos_pin", NO_PIN) ||
               settingNotEqualsIgnoreCase("axes/"+axis.name().toLowerCase()+"/"+motor+"/limit_all_pin", NO_PIN);
    }  
    
    @Override
    public boolean hasX0() {
        return checkMotorHasEndstop(Axis.X, Motor.M0);
    }
    
    @Override
    public boolean hasX1() {
        return checkMotorHasEndstop(Axis.X, Motor.M1);
    }
    
    @Override
    public boolean hasY0() {
        return checkMotorHasEndstop(Axis.Y, Motor.M0);
    }
    
    @Override
    public boolean hasY1() {
        return checkMotorHasEndstop(Axis.Y, Motor.M1);
    }
    
    @Override
    public boolean hasZ0() {
        return checkMotorHasEndstop(Axis.Z, Motor.M0);
    }
    
    @Override
    public boolean hasZ1() {
        return checkMotorHasEndstop(Axis.Z, Motor.M1);
    }    
    
    private void setHardLimitEnabled(Axis axis,Motor motor,boolean enabled) throws FirmwareSettingsException {
        if (checkMotorHasEndstop(axis, motor)) {
            setValue("axes/"+axis.name().toLowerCase()+"/"+motor+"/hard_limits", enabled ? "true" : "false");
        }
    }
    
    public Set<String> getUsedGpio() {
        // Syntax: gpio.14:high:pd or gpio.14:low:pu.
        Set<String> result = new HashSet<>();
        
        for (FirmwareSetting s: getAllSettings()) {
            if (s.getValue().toLowerCase().contains("gpio")) {
                result.add(s.getValue().toLowerCase().split("[:]")[0]);
            }
        }
        return result;
    }
   public String findSettingForGPIO(String gpio) {
        // Syntax: gpio.14:high:pd or gpio.14:low:pu.
        String result = "";
        if (!gpio.equalsIgnoreCase("NO_PIN")) {
            for (FirmwareSetting s: getAllSettings()) {            
                if (s.getValue().toLowerCase().contains(gpio.toLowerCase())) {
                    return s.getKey();
                }
            }
        }
        return result;
    }    
    @Override
    public void setHardLimitsEnabled(boolean enabled) {
        // Only set Hard limits on 
        try {
            setHardLimitEnabled(Axis.X,Motor.M0,enabled);
            setHardLimitEnabled(Axis.X,Motor.M1,enabled);
            setHardLimitEnabled(Axis.Y,Motor.M0,enabled);
            setHardLimitEnabled(Axis.Y,Motor.M1,enabled);
            setHardLimitEnabled(Axis.Z,Motor.M0,enabled);
            setHardLimitEnabled(Axis.Z,Motor.M1,enabled);         
        } catch (FirmwareSettingsException ex) {
            Logger.getLogger(FluidNCSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean settingEqualsIgnoreCase(String aSetting,String aExpectedValue) {
        return getSetting(aSetting).filter(s -> StringUtils.equalsIgnoreCase(aExpectedValue, s.getValue())).isPresent();
    }
    private boolean settingNotEqualsIgnoreCase(String aSetting,String aExpectedValue) {
        return getSetting(aSetting).filter(s -> !StringUtils.equalsIgnoreCase(aExpectedValue, s.getValue())).isPresent();
    }    
    private boolean settingEquals(String aSetting,boolean aExpectedValue) {
        return settingEqualsIgnoreCase(aSetting,aExpectedValue?"true":"false");
    }
    
    @Override
    public boolean isSoftLimitsEnabled() throws FirmwareSettingsException {
        return settingEquals("axes/x/soft_limits",true) || settingEquals("axes/y/soft_limits",true) || settingEquals("axes/z/soft_limits",true);
    }

    @Override
    public void setSoftLimitsEnabled(boolean enabled) {
        try {
            setValue("axes/x/soft_limits", enabled ? "true" : "false");
            setValue("axes/y/soft_limits", enabled ? "true" : "false");
            setValue("axes/z/soft_limits", enabled ? "true" : "false");
        } catch (FirmwareSettingsException ex) {
            Logger.getLogger(FluidNCSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isInvertDirection(Axis axis) {
        return false;
    }

    @Override
    public void setInvertDirection(Axis axis, boolean inverted) {

    }

    @Override
    public void setStepsPerMillimeter(Axis axis, double stepsPerMillimeter) {

    }

    @Override
    public double getStepsPerMillimeter(Axis axis) {
        return 0;
    }

    @Override
    public void setSoftLimit(Axis axis, double limit) throws FirmwareSettingsException {
        setValue("axes/" + axis.name().toLowerCase() + "/max_travel_mm", Utils.formatter.format(limit));
    }

    @Override
    public double getSoftLimit(Axis axis) throws FirmwareSettingsException {
        return getSetting("axes/" + axis.name().toLowerCase() + "/max_travel_mm")
                .map(s -> {
                    try {
                        return Utils.formatter.parse(s.getValue()).doubleValue();
                    } catch (ParseException e) {
                        return 0d;
                    }
                })
                .orElse(0d);
    }
    @Override
    public void setMposMM(Axis axis, double limit) throws FirmwareSettingsException {
        String keyName = "axes/" + axis.name().toLowerCase() + "/homing/mpos_mm";
        
        setValue(keyName, Utils.formatter.format(limit));
    }

    @Override
    public double getMposMM(Axis axis) throws FirmwareSettingsException {
        String keyName = "axes/" + axis.name().toLowerCase() + "/homing/mpos_mm";
        
        return getSetting(keyName)
                .map(s -> {
                    try {
                        return Utils.formatter.parse(s.getValue()).doubleValue();
                    } catch (ParseException e) {
                        return 0d;
                    }
                })
                .orElse(0d);
    }
    @Override
    public void setPulloffMM(Axis axis, Motor aMotor, double limit) throws FirmwareSettingsException {
        setValue("axes/" + axis.name().toLowerCase() + "/"+aMotor+"/pulloff_mm", Utils.formatter.format(limit));
    }

    @Override
    public double getPulloffMM(Axis axis, Motor aMotor) throws FirmwareSettingsException {
        return getSetting("axes/" + axis.name().toLowerCase() + "/"+aMotor+"/pulloff_mm")
                .map(s -> {
                    try {
                        return Utils.formatter.parse(s.getValue()).doubleValue();
                    } catch (ParseException e) {
                        return 0d;
                    }
                })
                .orElse(0d);
    }
    @Override
    public boolean isHomingDirectionInverted(Axis axis) {
        String key = "axes/" + axis.name().toLowerCase() + "/homing/positive_direction";
        FirmwareSetting firmwareSetting = getSetting(key).orElse(new FirmwareSetting(key, "false"));
        return firmwareSetting.getValue().equalsIgnoreCase("false");
    }

    @Override
    public void setHomingDirectionInverted(Axis axis, boolean inverted) {
        String key = "axes/" + axis.name().toLowerCase() + "/homing/positive_direction";
        try {
            setValue(key, inverted ? "false" : "true");
        } catch (FirmwareSettingsException ex) {
            Logger.getLogger(FluidNCSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isHardLimitsInverted() {
        return false;
    }

    @Override
    public void setHardLimitsInverted(boolean inverted) {

    }

    @Override
    public void setSettings(List<FirmwareSetting> settings) throws FirmwareSettingsException {
        settings.forEach(setting -> {
            try {
                setValue(setting.getKey().toLowerCase(), setting.getValue());
            } catch (FirmwareSettingsException e) {
                LOGGER.warning("Couldn't set the firmware setting " + setting.getKey() + " to value " + setting.getValue() + ". Error message: " + e.getMessage());
            }
        });
    }

    @Override
    public double getMaximumRate(Axis axis) {
        return 0;
    }

    private Optional<SpeedMap> getSpeedMap(String speedMapSetting) {
        FirmwareSetting value = settings.get(speedMapSetting);
        return Optional.ofNullable(value)
                .map(FirmwareSetting::getValue)
                .map(SpeedMap::new);
    }

    @Override
    public int getMaxSpindleSpeed() throws FirmwareSettingsException {
        // Huanyang YL620 H100 NowForever SiemensV20 ModbusVFD hbridge laser 10V pwm dac relay
        return Stream.of(getSpeedMap("laser/speed_map"),
                        getSpeedMap("10V/speed_map"),
                        getSpeedMap("pwm/speed_map"),
                        getSpeedMap("dac/speed_map"),           
                        getSpeedMap("relay/speed_map"),       
                        getSpeedMap("Huanyang/speed_map"),
                        getSpeedMap("YL620/speed_map"),           
                        getSpeedMap("H100/speed_map"),   
                        getSpeedMap("NowForever/speed_map"),
                        getSpeedMap("SiemensV20/speed_map"),           
                        getSpeedMap("ModbusVFD/speed_map"),   
                        getSpeedMap("hbridge/speed_map"),                           
                        getSpeedMap("besc/speed_map")) // Depricated
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(SpeedMap::getMax)
                .findFirst()
                .orElseThrow(() -> new FirmwareSettingsException("Could not find setting for max speed"));
    }
    
    @Override
    public void refreshFirmwareSettings() throws FirmwareSettingsException, CommandException {
        this.refresh();
    }
}

class FluidNCConfigYamlSettingsFile {
        private final IController controller;
        private Map<String,Object> outputConfigData;
        private String filename;
        private final static String UGS_FLAG = " [UGS]";

        public FluidNCConfigYamlSettingsFile(String filename, IController controller){
            this.filename = filename;
            this.controller = controller;
        }
        
        private void queryControllerForCurrentSettings() throws FirmwareSettingsException, InterruptedException, IOException {            
            GetFirmwareSettingsCommand getConfigFileCommand = ControllerUtils.sendAndWaitForCompletion(controller, new GetFirmwareSettingsCommand(filename));
            outputConfigData = GetFirmwareSettingsCommand.getSettingsTree(getConfigFileCommand.getResponse());
            System.err.println("-------");
            System.err.print(getConfigFileCommand.getResponse());
            System.err.println("-------");
            
        }
        private void refreshFromController() throws FirmwareSettingsException, InterruptedException, IOException {
            if (outputConfigData == null) {
                queryControllerForCurrentSettings();
            }
            if (outputConfigData == null) {
                throw new FirmwareSettingsException("Could not download original config. ");
            }    
        }
        
        public void applyAllSettings(IFirmwareSettings settings) throws FirmwareSettingsException, InterruptedException, IOException {
            for ( FirmwareSetting f : settings.getAllSettings() ) {
                setValue(f.getKey(), f.getValue());
            }
        }
        
        public void setValue(String key, String value) throws FirmwareSettingsException, InterruptedException, IOException {
            refreshFromController();
            String split[] = key.split("[/]");
            int idx = 0;
            Map<String,Object> insertPoint = outputConfigData;
            boolean going = true;
            while (going) {
                String subKeyName = split[idx];
                if (idx == (split.length-1)) {
                    // End of list. Do Insert.
                    insertPoint.put(subKeyName, value);
                } else {
                    if ( insertPoint.containsKey(subKeyName) ) {                    
                        insertPoint = (Map<String,Object>)insertPoint.get(subKeyName);
                    } else {
                        Map<String,Object> newKey = new LinkedHashMap<>();
                        insertPoint.put(subKeyName, newKey);
                        insertPoint = newKey;
                    }
                }
                idx++;
                if (idx == (split.length)) {
                    going = false;
                }
            }
        }
        
        public boolean hasUgsFlag() throws FirmwareSettingsException, InterruptedException, IOException {
            refreshFromController();
            String meta = ""+outputConfigData.get("meta");
            return (meta.endsWith(UGS_FLAG));
        }
        
        public void addUgsFlagIfNeeded() throws FirmwareSettingsException, InterruptedException, IOException {
            System.err.println("addUgsFlagIfNeeded");
            if (!hasUgsFlag()) {
                outputConfigData.put("meta", ""+outputConfigData.get("meta") + UGS_FLAG);
                String split[] = filename.split("[.]");
                split[0] = split[0] + "-ugs";
                filename = String.join(".", split);
                System.err.println("Updated Filename: " + filename);
            }            
        }
        public void patchConfigBugs() {
            // //
            // FluidNC does not properly init "uart1/passthrough_mode" field in memory
            // it appears to default to "80.0" which when re-saved via $CD= causes the
            // controller to panic in the next reboot and not finish reading its entire 
            // config.yaml.
            if (outputConfigData.containsKey("uart1")) {
                Map<String,Object> uart1 = (Map<String,Object>)outputConfigData.get("uart1");
                if (uart1.containsKey("passthrough_mode")) {
                    String toPatch = "" + uart1.get("passthrough_mode");
                    if ( (toPatch.length() != 3) || (toPatch.length() != 5)) {
                        uart1.put("passthrough_mode", "8N1");
                    }
                }
            }
        }
        private String configAsString() {
            final DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);
            return yaml.dump(outputConfigData);
        }
        
        public void uploadConfig() throws Exception, IOException, FirmwareSettingsException {            
            IFileService fileservice = controller.getFileService();
            patchConfigBugs();
            String dataToUpload=configAsString();
            fileservice.uploadFile("/localfs/"+filename, dataToUpload.getBytes());
            controller.getFirmwareSettings().setConfigFilename(filename);
            controller.issueHardReset();
        }

}