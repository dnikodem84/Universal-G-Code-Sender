/*
    Copyright 2023 Will Winder

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
package com.willwinder.universalgcodesender.firmware.marlin;

import com.willwinder.universalgcodesender.firmware.grbl.commands.*;
import com.willwinder.universalgcodesender.GrblUtils;
import com.willwinder.universalgcodesender.MarlinUtils;
import com.willwinder.universalgcodesender.firmware.FirmwareSetting;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GetMarlinBuildInfoCommand extends MarlinCommand {

    public GetMarlinBuildInfoCommand() {
        super("M115");
    }

    public MarlinVersion getVersion() {
        for (FirmwareSetting s : getSettings()) {
            if (s.getKey().equals("FIRMWARE_NAME")) {
                return new MarlinVersion(s.getValue());
            }
        }
        return null;
    }
    
    public boolean hasVersion() {
       for (FirmwareSetting s : getSettings()) {
            if (s.getKey().equals("FIRMWARE_NAME")) {
                return true;
            }
        } 
       return false;
    }
    public MarlinBuildOptions getBuildOptions() {
        return new MarlinBuildOptions(getSettingsMap());
    }
// M115
//FIRMWARE_NAME:Marlin 414 bugfix-2.0.x (GitHub) SOURCE_CODE_URL:https://github.com/MarlinFirmware/Marlin PROTOCOL_VERSION:1.0 MACHINE_TYPE:V1 E CNC EXTRUDER_COUNT:1 UUID:cede2a2f-41a2-4748-9b12-c55c62f367ff
//Cap:SERIAL_XON_XOFF:0
//Cap:BINARY_FILE_TRANSFER:0
//Cap:EEPROM:0
//Cap:VOLUMETRIC:1
//Cap:AUTOREPORT_TEMP:1
//Cap:PROGRESS:0
//Cap:PRINT_JOB:1
//Cap:AUTOLEVEL:0
//Cap:Z_PROBE:0
//Cap:LEVELING_DATA:0
//Cap:BUILD_PERCENT:0
//Cap:SOFTWARE_POWER:0
//Cap:TOGGLE_LIGHTS:0
//Cap:CASE_LIGHT_BRIGHTNESS:0
//Cap:EMERGENCY_PARSER:0
//Cap:PROMPT_SUPPORT:0
//Cap:AUTOREPORT_SD_STATUS:0
//Cap:THERMAL_PROTECTION:1
//Cap:MOTION_MODES:1
//Cap:CHAMBER_TEMPERATURE:0
//ok
    @Override
    public void appendResponse(String response) {
        super.appendResponse(response);
        if (response.endsWith("ok")) {
            setDone(true);
        } else {
//            setError(true);
        }

    }
    
    public HashMap<String,String> getSettingsMap() {
        HashMap<String,String> result = new HashMap<> ();
        for (FirmwareSetting fw : getSettings()) {
            result.put(fw.getKey().trim().replaceAll("[:]", ""), fw.getValue().trim());
        }
        return result;
    }
    
    public List<FirmwareSetting> getSettings() {
        return Arrays.stream(StringUtils.split(getResponse(), "\n")).filter(line -> line.startsWith("Cap:") || line.startsWith("FIRMWARE_NAME:")).map(line -> {
            String[] split = line.split(":", 2);
            if (split[0].equalsIgnoreCase("FIRMWARE_NAME")) {
                return new FirmwareSetting(split[0], split[1]);
            } else {
                split = line.split(":", 3);
                return new FirmwareSetting(split[1], split[2]);
            }
        }).collect(Collectors.toList());
    }
}
