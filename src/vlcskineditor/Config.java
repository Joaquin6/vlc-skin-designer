/*****************************************************************************
 * Config.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * VLC Skin Editor
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package vlcskineditor;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Class to manage the configuration
 * @author Daniel Dreibrodt
 */
public class Config {
  
  private static Hashtable<String, String> strings = new Hashtable<String, String>();
  private static File configFile = new File("VLCSkinEditor.cfg");
  
  static {
    strings.put("autoupdate", "true");
    strings.put("language", "English");
  }
  
  public static void loadConfig() {
    try {
      FileReader fr = new FileReader(configFile);
      BufferedReader br = new BufferedReader(fr);         
      
      String line = "";
      while( (line = br.readLine()) != null) {
        if(!line.startsWith("#")) {
          String[] fields = line.split("\\|");
          if(fields.length==2) {
            strings.put(fields[0], fields[1].replaceAll("\\\\n", "\n"));
          }
        }
      }
      
      br.close();
      fr.close();      
    } catch(FileNotFoundException ex) {
      System.out.println("Configuration does not yet exist. Creating it...");
      saveConfig();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }
  
  public static String getValue(String key) {
    String s = strings.get(key);    
    return (s!=null)?s:key;
  }
  
  public static void setValue(String key, String value) {
    strings.put(key, value);
  }
  
  public static void saveConfig() {
    try {
      FileWriter fw = new FileWriter(configFile);
      BufferedWriter bw = new BufferedWriter(fw);
      Enumeration<String> keys = strings.keys();
      while(keys.hasMoreElements()) {
        String key = keys.nextElement();
        String value = strings.get(key);
        bw.write(key+"|"+value);
        bw.newLine();
      }
      bw.close();
      fw.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
    
  }
  
}


