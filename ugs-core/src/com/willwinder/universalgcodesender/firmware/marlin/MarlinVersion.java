package com.willwinder.universalgcodesender.firmware.marlin;

//import com.willwinder.universalgcodesender.firmware.grbl.*;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a GRBL version string
 */
public class MarlinVersion {
    public static final MarlinVersion NO_VERSION = new MarlinVersion("");

    private double protocol=0.0;
    private String machineType="";  
    private String firmwareName="";
    
    /**
     * Parses the Marlin version string from the format []
     *
     * @param versionString the version string
     */
    public MarlinVersion(String versionString) {
        String match = "[ ^][A-Z_]*:";
        String split[] = versionString.split("(?<="+match+")|(?="+match+")");
        if (!split[0].isBlank()) {
            firmwareName = split[0];
        } else {
            firmwareName = "MARLIN";
        }
        for (int x = 1; x < split.length; x+=2) {
            String name = split[x].trim();
            String value = split[x+1].trim();
            if (name.equalsIgnoreCase("PROTOCOL_VERSION:")) {
                protocol=Double.parseDouble(value);
            } else if (name.equalsIgnoreCase("MACHINE_TYPE:")) {
                machineType=value;
            }                       
        }
    }

    @Override
    public String toString() {
        String result = firmwareName;
        if(!machineType.isBlank()) {
            result = result + " [" + machineType + "]";
        }
        if(protocol != 0.0) {
            result = result + " [Protocol Version: " + protocol + "]";
        }

        return result;
    }
}
