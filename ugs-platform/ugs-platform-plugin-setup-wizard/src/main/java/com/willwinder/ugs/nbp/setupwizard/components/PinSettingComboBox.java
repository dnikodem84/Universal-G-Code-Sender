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
public class PinSettingComboBox extends JComboBox {

    private AbstractWizardPanel owner;
    private String currentSetting;

    public PinSettingComboBox(AbstractWizardPanel owner, String currentSetting) {
        super(new PinSettingComboModel(owner, currentSetting));
        setRenderer(new PinSettingComboRenderer(owner, currentSetting));
        this.currentSetting = currentSetting;
        this.owner = owner;
    }
}

class PinSettingComboRenderer extends BasicComboBoxRenderer {

    AbstractWizardPanel owner;
    String currentSetting;

    public PinSettingComboRenderer(AbstractWizardPanel owner, String currentSetting) {
        this.currentSetting = currentSetting;
        this.owner = owner;
    }

    private IFirmwareSettings getSettings() {
        if (owner.getBackend().getController() == null) {
            return null;
        }
        return owner.getBackend().getController().getFirmwareSettings();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String pinOwner = getPinOwner("" + value);
        if (pinOwner.equals(currentSetting)) {
            value = "<html><body><font color='green'>" + value + "</font> : <font color='gray' size='-1'>" + pinOwner + "</font></body></html>";
        } else if (!pinOwner.equals("")) {
            value = "<html><body><font color='red'>" + value + "</font> : <font color='gray' size='-1'>" + pinOwner + "</font></body></html>";
        }

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        return this;
    }

    public String getPinOwner(String pin) {
        String currentPinUser = getSettings().findSettingForGPIO(pin);
        return currentPinUser;
    }

}

class PinSettingComboModel implements ComboBoxModel<String> {

    private String currentSetting = "";

    private List<ListDataListener> listeners = new LinkedList<>();
    private AbstractWizardPanel owner;

    public PinSettingComboModel(AbstractWizardPanel owner, String currentSetting) {
        this.currentSetting = currentSetting;
        this.owner = owner;
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
            String newValue = "" + anItem;
            String currentPinUser = getSettings().findSettingForGPIO(newValue);
            if (currentPinUser.equals(currentSetting) || currentPinUser.equals("")) {
                Optional<FirmwareSetting> setting = getSettings().getSetting(currentSetting);
                if ( setting.isEmpty() || currentSetting.equalsIgnoreCase("NO_PIN") ) {
                    getSettings().setValue(currentSetting, newValue);
                } else {
                    String[] split = setting.get().getKey().split("[:]");
                    split[0] = newValue;                    
                    getSettings().setValue(currentSetting, String.join(":", split));
                }
            } else {
                throw new FirmwareSettingsException("Pin " + currentSetting + "Currently in use, Aborting!");
            }
        } catch (FirmwareSettingsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getSelectedItem() {
        if (getSettings() == null) {
            return null;
        }
        Optional<FirmwareSetting> setting = getSettings().getSetting(currentSetting);
        if (setting.isEmpty()) {
            return "NO_PIN";
        } else {
            String[] split = setting.get().getValue().split("[:]");
            return split[0];
        }
    }

    @Override
    public int getSize() {
        return FluidNCUtils.getAllGpioPins().size() + 1;
    }

    @Override
    public String getElementAt(int index) {
        if (index == 0) {
            return "NO_PIN";
        } else {
            return FluidNCUtils.getAllGpioPins().get(index - 1);
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
