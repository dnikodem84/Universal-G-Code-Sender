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
import com.willwinder.ugs.nbp.setupwizard.panels.WizardPanelProbeSetup;
import com.willwinder.universalgcodesender.firmware.FirmwareSetting;
import com.willwinder.universalgcodesender.firmware.FirmwareSettingsException;
import com.willwinder.universalgcodesender.firmware.IFirmwareSettings;
import com.willwinder.universalgcodesender.firmware.fluidnc.FluidNCUtils;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author Damian Nikodem
 */
public class HighLowSettingComboBox extends JComboBox {

    private AbstractWizardPanel owner;
    private String currentSetting;
    
    public HighLowSettingComboBox(AbstractWizardPanel owner, String currentSetting, int defaultValue) {
        super(new HighLowSettingComboBoxModel(owner, currentSetting,defaultValue));

        this.currentSetting = currentSetting;
        this.owner = owner;
    }
}

class HighLowSettingComboBoxModel implements ComboBoxModel<String> {

    private String currentSetting = "";
    private String trueValue = "High";
    private String falseValue = "Low";
    private String unsetValue = "";

    private final List<ListDataListener> listeners = new LinkedList<>();
    private final AbstractWizardPanel owner;
    private final int defaultValue;
    
    public HighLowSettingComboBoxModel(AbstractWizardPanel owner, String currentSetting, int defaultValue) {
        this.currentSetting = currentSetting;
        this.owner = owner;
        this.defaultValue = defaultValue;
    }

    private IFirmwareSettings getSettings() {
        if (owner.getBackend().getController() == null) {
            return null;
        }
        return owner.getBackend().getController().getFirmwareSettings();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        try {
            String value = "" + anItem;
            if (value.equalsIgnoreCase(trueValue) ) {
                value="high";
            } else if (value.equalsIgnoreCase(falseValue) ) {
                value="low";
            } else {
                value = "";
            }
            Optional<FirmwareSetting> setting = getSettings().getSetting(currentSetting);
            if (setting.isPresent()) {
                String originalValue = setting.get().getValue();
                if (originalValue.equals("") || originalValue.equalsIgnoreCase("NO_PIN")) {
                    return;
                }                
                
                if (originalValue.contains(":")) {
                    String split[] = originalValue.split("[:]");
                    split[1] = value;
                    value = String.join(":", split);                        
                } else {
                    value = originalValue+":" + value;
                }

                if (value.endsWith(":")) {
                    value = value.substring(0, value.length() - 1);
                }
                value = value.replace("::", ":");
                
                getSettings().setValue(currentSetting, value);
            }
            
                  
        } catch (FirmwareSettingsException e) {
            e.printStackTrace();
        }
    }    
    
    public String getDefaultValue() {
        switch (defaultValue) {
            case 0: return unsetValue;
            case 1: return trueValue;
            case 2: return falseValue;
            default: return unsetValue;
        }        
    }
    
    @Override
    public Object getSelectedItem() {
        if (getSettings() == null) {
            return null;
        }
        Optional<FirmwareSetting> setting = getSettings().getSetting(currentSetting);
        if (setting.isEmpty()) {
            return unsetValue;
        } else {
            String val = setting.get().getValue().toLowerCase();
            if (val.contains(":high")) {
                return trueValue;
            } else if (val.contains(":low")) {
                return falseValue;
            } else {
                return unsetValue;
            }

        }
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public String getElementAt(int index) {
        switch (index) {
            case 0: return unsetValue;
            case 1: return trueValue;
            case 2: return falseValue;
            default: return unsetValue;
        }  
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public void fireListDataListeners(ListDataEvent e) {
        for (ListDataListener l : listeners) {
            l.contentsChanged(e);
        }
    }
}
