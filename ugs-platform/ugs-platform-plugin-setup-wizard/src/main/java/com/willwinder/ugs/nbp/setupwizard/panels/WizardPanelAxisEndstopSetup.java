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
package com.willwinder.ugs.nbp.setupwizard.panels;

import com.willwinder.ugs.nbp.setupwizard.AbstractWizardPanel;
import com.willwinder.ugs.nbp.setupwizard.WizardUtils;
import com.willwinder.ugs.nbp.setupwizard.components.BooleanSettingComboBox;
import com.willwinder.ugs.nbp.setupwizard.components.InputPinComboBoxPanel;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.events.ControllerStatusEvent;
import com.willwinder.universalgcodesender.model.events.FirmwareSettingEvent;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * A wizard step panel for configuring the Probe on a FluidNC controller
 *
 * @author Damian Nikodem
 */
public class WizardPanelAxisEndstopSetup extends AbstractWizardPanel implements UGSEventListener {
    private JPanel pnlMainM0;
    private JPanel pnlMainM1;
    
    private InputPinComboBoxPanel pnlLimitMinM0;
    private InputPinComboBoxPanel pnlLimitMaxM0;    
    private InputPinComboBoxPanel pnlLimitAllM0;
    
    private InputPinComboBoxPanel pnlLimitMinM1;
    private InputPinComboBoxPanel pnlLimitMaxM1;    
    private InputPinComboBoxPanel pnlLimitAllM1;
    
    
//    private BooleanSettingComboBox comboboxHardStop;
//    private BooleanSettingComboBox comboboxProbeHardLimit;
    
    private Axis axis;
    
    private JPanel makeProbePanel(String label, JComponent combo) {
        JPanel pnlComboA = new JPanel(new BorderLayout());
        pnlComboA.add(new JLabel(label),BorderLayout.WEST);
        JPanel pnlWrap = new JPanel(new FlowLayout());
        pnlWrap.add(combo);
        pnlComboA.add(pnlWrap,BorderLayout.EAST);
        return pnlComboA;
    }
    private JLabel makeDescrLabel(String text) {
        JLabel result = new JLabel("<html><body><font color='darkGrey' size='-1'><i>"+text+"</i></font></body></html>");
        return result;
        
    }
    
    public WizardPanelAxisEndstopSetup(BackendAPI backend, Axis axis) {
        super(backend, Localization.getString("platform.plugin.setupwizard.probe-panel.title"));
        this.axis = axis;
        initComponents();
        initLayout();
    }

    private void initLayout() {
//        pnlMain.add(makeProbePanel(Localization.getString("platform.plugin.setupwizard.probe-panel.probe-pin-label"), pnlProbePin));
//        pnlMain.add(makeDescrLabel(Localization.getString("platform.plugin.setupwizard.probe-panel.probe-pin-desc")));
//        pnlMain.add(makeProbePanel(Localization.getString("platform.plugin.setupwizard.probe-panel.toolsetter-pin-label"), pnlProbeToolsetterPin));
//        pnlMain.add(makeDescrLabel(Localization.getString("platform.plugin.setupwizard.probe-panel.toolsetter-pin-desc")));
//        pnlMain.add(makeProbePanel(Localization.getString("platform.plugin.setupwizard.probe-panel.checkmodestart-label"), comboboxCheckModeStart));        
//        pnlMain.add(makeDescrLabel(Localization.getString("platform.plugin.setupwizard.probe-panel.checkmodestart-desc")));      
//        pnlMain.add(makeProbePanel(Localization.getString("platform.plugin.setupwizard.probe-panel.hardstop-label"), comboboxHardStop));        
//        pnlMain.add(makeDescrLabel(Localization.getString("platform.plugin.setupwizard.probe-panel.hardstop-desc")));        
//        pnlMain.add(makeProbePanel(Localization.getString("platform.plugin.setupwizard.probe-panel.probehardlimit-label"), comboboxProbeHardLimit));        
//        pnlMain.add(makeDescrLabel(Localization.getString("platform.plugin.setupwizard.probe-panel.probehardlimit-desc")));
//        
//        getPanel().add(pnlMain, "grow");

        setValid(true);
    }


    private void initComponents() {
//            private JPanel pnlMainM0;
//    private JPanel pnlMainM1;
    pnlMainM0 = new JPanel(new GridLayout(6,1));   
    pnlMainM1 = new JPanel(new GridLayout(6,1));   
    
    pnlLimitMinM0=new InputPinComboBoxPanel(this,"axes/"+axis.name().toLowerCase()+"/motor0/limit_min_pin");
    pnlLimitMaxM0=new InputPinComboBoxPanel(this,"axes/"+axis.name().toLowerCase()+"/motor0/limit_max_pin");
    pnlLimitAllM0=new InputPinComboBoxPanel(this,"axes/"+axis.name().toLowerCase()+"/motor0/limit_all_pin");
    pnlLimitMinM1=new InputPinComboBoxPanel(this,"axes/"+axis.name().toLowerCase()+"/motor1/limit_min_pin");
    pnlLimitMaxM1=new InputPinComboBoxPanel(this,"axes/"+axis.name().toLowerCase()+"/motor1/limit_max_pin");
    pnlLimitAllM1=new InputPinComboBoxPanel(this,"axes/"+axis.name().toLowerCase()+"/motor1/limit_all_pin");
//        pnlProbePin = new InputPinComboBoxPanel(this,"probe/pin");
//        pnlProbeToolsetterPin = new InputPinComboBoxPanel(this,"probe/toolsetter_pin");        
//        pnlMain = new JPanel(new GridLayout(10,1));        
//        comboboxCheckModeStart = new BooleanSettingComboBox(this,"probe/check_mode_start", true);
//        comboboxHardStop = new BooleanSettingComboBox(this,"probe/hard_stop", false);
//        comboboxProbeHardLimit = new BooleanSettingComboBox(this,"probe/probe_hard_limit", false);
    }


    @Override
    public void initialize() {
        getBackend().addUGSEventListener(this);
        refeshControls();
    }

    private void refeshControls() {
        // Not really needed.
      pnlLimitMinM0.repaint();
      pnlLimitMaxM0.repaint();
      pnlLimitAllM0.repaint();
      pnlLimitMinM1.repaint();
      pnlLimitMaxM1.repaint();
      pnlLimitAllM1.repaint();
      
    }


    @Override
    public boolean isEnabled() {
        return getBackend().isConnected() &&
                getBackend().getController().getCapabilities().hasSetupWizardSupport();
    }

    @Override
    public void destroy() {
        getBackend().removeUGSEventListener(this);
    }

    @Override
    public void UGSEvent(UGSEvent event) {
        if (getBackend().getController() != null && getBackend().isConnected() && event instanceof ControllerStatusEvent) {
            WizardUtils.killAlarm(getBackend());
        } else if (event instanceof FirmwareSettingEvent) {
            refeshControls();
        }

    }
}
