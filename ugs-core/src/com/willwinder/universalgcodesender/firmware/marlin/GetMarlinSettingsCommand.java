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
import java.util.List;
import java.util.stream.Collectors;

public class GetMarlinSettingsCommand extends MarlinCommand {
    public GetMarlinSettingsCommand() {
        super("M503");
    }
// M503
//echo:  G21    ; Units in mm (mm)
//echo:  M149 C ; Units in Celsius
//
//echo:Filament settings: Disabled
//echo:  M200 D3.00
//echo:  M200 D0
//echo:Steps per unit:
//echo: M92 X100.00 Y100.00 Z400.00 E100.00
//echo:Maximum feedrates (units/s):
//echo:  M203 X120.00 Y120.00 Z30.00 E25.00
//echo:Maximum Acceleration (units/s2):
//echo:  M201 X400.00 Y400.00 Z100.00 E2000.00
//echo:Acceleration (units/s2): P<print_accel> R<retract_accel> T<travel_accel>
//echo:  M204 P400.00 R3000.00 T400.00
//echo:Advanced: B<min_segment_time_us> S<min_feedrate> T<min_travel_feedrate> J<junc_dev>
//echo:  M205 B20000.00 S0.00 T0.00 J0.10
//echo:Home offset:
//echo:  M206 X0.00 Y0.00 Z0.00
//echo:Material heatup parameters:
//echo:  M145 S0 H196 B92 F0
//echo:  M145 S1 H240 B110 F0
//echo:PID settings:
//echo:  M301 P17.98 I0.98 D83

     @Override
    public void appendResponse(String response) {
        super.appendResponse(response);
        if (response.endsWith("ok")) {
            setDone(true);
        } else {
//            setError(true);
        }
        
    }
    public List<String> getSettings() {
        return Arrays.stream(StringUtils.split(getResponse(), "\n")).filter(line -> line.startsWith("echo:  ")).map(line -> {
            String[] split = line.split(":", 2);            
            return split[1].trim();
        }).collect(Collectors.toList());
    }
}
