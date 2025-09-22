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

//import com.willwinder.universalgcodesender.firmware.grbl.commands.*;

//import static com.willwinder.universalgcodesender.GrblUtils.isGrblStatusString;

public class MarlinCheckCommand extends MarlinCommand {
    private boolean commandSupported = false;
    
    public MarlinCheckCommand(String command) {
        super(command);
    }

    public MarlinCheckCommand(String command, String originalCommand, String comment, int commandNumber) {
        super(command, originalCommand, comment, commandNumber);
    }

    @Override
    public void appendResponse(String response) {

        super.appendResponse(response);
        if (this.response.contains("echo:Unknown command")) {
            commandSupported = false;
            setDone(true);
            setOk(true);
        } else {
            commandSupported = true;
        }
    }
    public boolean isCommandSupported() {
        return this.commandSupported;
    }
}
