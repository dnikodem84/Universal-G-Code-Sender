/*
 * Copyright (C) 2025 fliptech
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
package com.willwinder.universalgcodesender;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author fliptech
 */
public class SerialTester {

    public static void main(String[] args) throws Exception {
        new EditFrame();
//        SerialPort comPort = SerialPort.getCommPorts()[0];
//        comPort.setBaudRate(250000);
//        comPort.openPort();
//        PrintStream ps = new PrintStream(comPort.getOutputStream());
//        ps.println("M114");

//        comPort.closePort();
    }
}

class EditFrame extends JFrame {

    DefaultListModel<String> dlm = new DefaultListModel<>();
    JList jlb = new JList(dlm);
    JPanel pnlEdit = new JPanel();
    JTextField line = new JTextField();
    JScrollPane jsp;
    JButton btn = new JButton(">");
    SerialPort comPort;
    PrintStream printStream;
    Thread runThread;
    static final String[] baud_rates = new String[]{ "9600","115200","250000" };
    
    JComboBox jcbPorts;// = new JComboBox(ports);
    JComboBox baudRates;// = new JComboBox(baud_rates);
    JLabel lblPort = new JLabel("Port:");
    JLabel lblBaud = new JLabel("Baud Rate:");
    JButton btnConnect = new JButton("Connect");            
    
    public JPanel getConnectionPanel() {
        JPanel result = new JPanel();
        result.setLayout(new FlowLayout());
        SerialPort[] ports = SerialPort.getCommPorts();
        jcbPorts = new JComboBox(ports);
        baudRates = new JComboBox(baud_rates);
        
        baudRates.setSelectedIndex(2);        
        result.add(lblPort);
        result.add(jcbPorts);
        result.add(lblBaud);
        result.add(baudRates);
        result.add(btnConnect);
        btnConnect.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isConnected) { 
                    doDisconnect();
                    btnConnect.setText("Connect");
                } else {
                    doConnect();
                    btnConnect.setText("Disconnect");
                }
            }
        });
        return result;
        
    }
    boolean isConnected = false;
    
    public void doConnect() {
        doDisconnect();
        comPort = SerialPort.getCommPorts()[this.jcbPorts.getSelectedIndex()];
        comPort.setBaudRate(Integer.parseInt((String)this.baudRates.getSelectedItem()));
        comPort.openPort();
        printStream = new PrintStream(comPort.getOutputStream());
        runThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        while (comPort.bytesAvailable() == 0) {
                            Thread.sleep(20);
                        }
                        byte[] readBuffer = new byte[comPort.bytesAvailable()];    
                        comPort.readBytes(readBuffer, readBuffer.length);
                        SwingUtilities.invokeLater(() -> {
                            addLine(readBuffer);
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        );
        runThread.start();    
        btn.setEnabled(true);
        line.setEnabled(true);
        lblBaud.setEnabled(false);
        lblPort.setEnabled(false);
        jcbPorts.setEnabled(false);
        baudRates.setEnabled(false);
        this.isConnected = true;
        
        
    }
    public void doDisconnect() {
        if (comPort != null) {
            comPort.closePort();
            comPort = null;
        }
        if (this.runThread != null) {
            this.runThread.interrupt();
        }            
        printStream = null;
        btn.setEnabled(false);
        line.setEnabled(false);
        lblBaud.setEnabled(true);
        lblPort.setEnabled(true);
        jcbPorts.setEnabled(true);
        baudRates.setEnabled(true);            
        this.isConnected = false;

    }
    
    public EditFrame() {
        super("Serial Terminal");
        setLayout(new BorderLayout());
        jsp = new JScrollPane(jlb);
        add(getConnectionPanel(), BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);

        pnlEdit.setLayout(new BorderLayout());
        pnlEdit.add(line, BorderLayout.CENTER);
        pnlEdit.add(btn, BorderLayout.EAST);
        
        add(pnlEdit, BorderLayout.SOUTH);


//        printStream = new PrintStream(comPort.getOutputStream());
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printStream.println(line.getText());
                line.setText("");
            }
        });

        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e
            ) {

            }

            @Override
            public void windowClosing(WindowEvent e
            ) {
                if (comPort != null) {
                    comPort.closePort();
                }
                System.exit(0);
            }

        }
        );
        setSize(
                640, 480);
        setVisible(
                true);
        doDisconnect();
    }
    public void addLine(byte[] readBuffer) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String tmpStr[] = new String(readBuffer).split("\n");
                for (String part : tmpStr) {
                    dlm.addElement(part);
                }
            }
        });
        JScrollBar vertical = jsp.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
}
