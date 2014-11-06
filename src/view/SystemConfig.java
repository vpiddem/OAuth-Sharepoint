/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 *
 * @author Srinivas
 */
public class SystemConfig {

    public Properties prop;
    static String ConfigFileName = "config.cfg";
    static  FileInputStream is;
    static  OutputStream os;
    
    public static String getProperty(String key) {
        return new SystemConfig().prop.getProperty(key);
    }

    public static String getHostName() {
        String name = "localhost";
        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return name;
    }

    public static void saveSettings(String googleTokenURl,String authKey) {
        try {
            Properties prop = new Properties();
            is = new FileInputStream(ConfigFileName);
            prop.load(is);
            prop.setProperty("GOOGLE_ACCESSURL", googleTokenURl);
            prop.setProperty("AUTH_TOKEN", authKey);
            os = new FileOutputStream(ConfigFileName);
            prop.store(os, "***************** System Configuration ********************");
            is.close();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SystemConfig() {
        InputStream is = null;
        OutputStream os = null;
        InputStream input = null;
        try {
            prop = new Properties();
            is = new FileInputStream(ConfigFileName);
            prop.load(is);
            is.close();
        } catch (IOException ex) {
            try {
                prop = new Properties();
                os = new FileOutputStream(ConfigFileName);
                prop.setProperty("GOOGLE_ACCESSURL", "www.google.com");
                prop.setProperty("AUTH_TOKEN", "www.google.com");
                prop.store(os, "************SYSTEM CONFIGURATION*************");
                os.close();
                input = new FileInputStream(ConfigFileName);
                prop.load(input);
                input.close();
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
        }
    }
}
