/*
 * Copyright (C) 2025 Damian Nikodem
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.nbp.setupwizard.components;

import com.willwinder.ugs.nbp.setupwizard.AbstractWizardPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 *
 * @author Damian Nikodem
 */
public class InputPinComboBoxPanel extends JPanel {
    public InputPinComboBoxPanel(AbstractWizardPanel owner, String currentSetting) {
        super(new BorderLayout());
        add(new PinSettingComboBox(owner,currentSetting),BorderLayout.CENTER);
        JPanel pnlMid = new JPanel(new GridLayout(1,2));        
        pnlMid.add(new HighLowSettingComboBox(owner,currentSetting,0));
        pnlMid.add(new PullUpPullDownSettingComboBox(owner,currentSetting,0));
        add(pnlMid,BorderLayout.EAST);        
    }
}
