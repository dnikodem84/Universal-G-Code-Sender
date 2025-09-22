/*
    Copyright 2025 Damian Nikodem

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

import com.willwinder.universalgcodesender.firmware.grbl.*;

/**
 * Marlin options are reported by the build info command (M503). This enum contains
 * the known options listed in the official documentation:
 * https://reprap.org/wiki/Firmware_Capabilities_Protocol
 */
public enum MarlinBuildOption {
    ARCS("ARCS"),
    AUTOLEVEL("AUTOLEVEL"),
    AUTOREPORT_POS("AUTOREPORT_POS"),
    AUTOREPORT_SD_STATUS("AUTOREPORT_SD_STATUS"),
    AUTOREPORT_TEMP("AUTOREPORT_TEMP"),
    BABYSTEPPING("BABYSTEPPING"),
    BEZIERS("BEZIERS"),
    BINARY_FILE_TRANSFER("BINARY_FILE_TRANSFER"),
    BUILD_PERCENT("BUILD_PERCENT"),
    CASE_LIGHT_BRIGHTNESS("CASE_LIGHT_BRIGHTNESS"),
    CONFIG_EXPORT("CONFIG_EXPORT"),
    CUSTOM_FIRMWARE_UPLOAD("CUSTOM_FIRMWARE_UPLOAD"),
    DIRECT_STEPPING("DIRECT_STEPPING"),
    DISPLAY("DISPLAY"),
    DOOR("DOOR"),
    DUAL_X_CARRIAGE("DUAL_X_CARRIAGE"),
    EEPROM("EEPROM"),
    EMERGENCY_PARSER("EMERGENCY_PARSER"),
    EP_BABYSTEP("EP_BABYSTEP"),
    EXTENDED_M20("EXTENDED_M20"),
    FIRMWARE_RETRACT("FIRMWARE_RETRACT"),
    HEATED_BED("HEATED_BED"),
    HOST_ACTION_COMMANDS("HOST_ACTION_COMMANDS"),
    HOST_RESCUE("HOST_RESCUE"),
    INPUT_SHAPING("INPUT_SHAPING"),
    LASER("LASER"),
    LEVELING_DATA("LEVELING_DATA"),
    LFN_WRITE("LFN_WRITE"),
    LINEAR_ADVANCE("LINEAR_ADVANCE"),
    LONG_FILENAME("LONG_FILENAME"),
    MEATPACK("MEATPACK"),
    MIXING_EXTRUDER("MIXING_EXTRUDER"),
    MOTION_MODES("MOTION_MODES"),
    MULTI_VOLUME("MULTI_VOLUME"),
    OUT_OF_ORDER("OUT_OF_ORDER"),
    PAREN_COMMENTS("PAREN_COMMENTS"),
    POWER_LOSS_RECOVERY("POWER_LOSS_RECOVERY"),
    PRINT_JOB("PRINT_JOB"),
    PROGRESS("PROGRESS"),
    PROMPT_SUPPORT("PROMPT_SUPPORT"),
    QUOTED_STRINGS("QUOTED_STRINGS"),
    REPEAT("REPEAT"),
    RUNOUT("RUNOUT"),
    SDCARD("SDCARD"),
    SD_WRITE("SD_WRITE"),
    SERIAL_XON_XOFF("SERIAL_XON_XOFF"),
    SINGLE_NOZZLE("SINGLE_NOZZLE"),
    SOFTWARE_POWER("SOFTWARE_POWER"),
    SPINDLE("SPINDLE"),
    THERMAL_PROTECTION("THERMAL_PROTECTION"),
    TOGGLE_LIGHTS("TOGGLE_LIGHTS"),
    VOLUMETRIC("VOLUMETRIC"),
    Z_PROBE("Z_PROBE");

    private final String code;

    MarlinBuildOption(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
