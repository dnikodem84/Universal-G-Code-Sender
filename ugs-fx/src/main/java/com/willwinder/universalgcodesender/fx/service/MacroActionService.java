/*
    Copyright 2025 Joacim Breiler

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
package com.willwinder.universalgcodesender.fx.service;

import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.universalgcodesender.fx.actions.MacroAction;
import com.willwinder.universalgcodesender.model.BackendAPI;

public class MacroActionService {
    public static void registerMacros() {
        BackendAPI backend = CentralLookup.getDefault().lookup(BackendAPI.class);
        backend.getSettings().getMacros().forEach(macro -> ActionRegistry.getInstance().registerAction(new MacroAction(macro)));
    }
}
