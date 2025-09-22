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
import java.util.HashMap;

/**
 * Parses options from the build info command (M503)
 * <a href="https://github.com/gnea/grbl/wiki/Grbl-v1.1-Interface#feedback-messages">Documentation</a>
 *
 * @author Damian Nikodem
 */
public class MarlinBuildOptions {
    private final HashMap<String,String> options;

    public MarlinBuildOptions() {
        this(new HashMap<>());
    }

    public MarlinBuildOptions(HashMap<String,String> options) {
        this.options = options;
    }

    public boolean isEnabled(String buildOption) {
        if (!this.options.containsKey(buildOption)) {
            return false;
        } else {
            return this.options.get(buildOption).equals("1");
        }
    }
}
