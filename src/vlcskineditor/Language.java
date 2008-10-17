/*****************************************************************************
 * Language.java
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
import java.util.Hashtable;

/**
 * Class to manage the multi-language interface
 * @author Daniel Dreibrodt
 */
public class Language {
  
  private static Hashtable<String, String> strings = new Hashtable<String, String>();
  
  /**
   * Loads the current language from a file
   * @param f The language file, e.g. lang/English.txt
   */
  public static void load(File f) {
    try {
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      
      String line = "";
      while( (line = br.readLine()) != null) {
        if(line.startsWith("@include")) {
          String file = line.substring(9);
          load(new File(f.getParent()+File.separator+file));
        }        
        else if(!line.startsWith("#")) {
          String[] fields = line.split("\\|");
          if(fields.length==2) {
            strings.put(fields[0], fields[1].replaceAll("\\\\n", "\n"));
          }
        }
      }
      
      br.close();
      fr.close();            
    } catch(FileNotFoundException ex) {
      ex.printStackTrace();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Gets the string in the current language for the given key 
   * @param key the string key
   * @return the translated string
   */
  public static String get(String key) {
    String s = strings.get(key);    
    return (s!=null)?s:key;
  }
  
}
