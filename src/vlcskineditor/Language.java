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
import java.util.LinkedList;
import java.util.List;

/**
 * Class to manage the multi-language interface
 * @author Daniel Dreibrodt
 */
public class Language {
  
  private static Hashtable<String, String> strings = new Hashtable<String, String>();

  private static LinkedList<Language> langs = new LinkedList<Language>();

  static {
    try {
      File index = new File("lang/languages.txt");
      FileReader fr = new FileReader(index);
      BufferedReader br = new BufferedReader(fr);
      String line = "";
      while( (line = br.readLine()) != null) {
        line = new String(line.getBytes(),"UTF-8");
        if(!line.startsWith("#")) {
          String[] fields = line.split("\\|");
          if(fields.length==3) {
            langs.add(new Language(fields[0], fields[1], fields[2]));
          }
        }
      }
      br.close();
      fr.close();
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private String name, code;
  private File file;

  /**
   * Creates a new language
   * @param code
   * @param name
   * @param file
   */
  private Language(String code, String name, String file) {
    this.name = name;
    this.code = code;
    this.file = new File("lang",file);
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public File getFile() {
    return file;
  }

  @Override
  public String toString() {
    return name+" ("+code+")";
  }
  
  /**
   * Loads the current language from a file
   * @param f The language file, e.g. lang/English.txt
   */
  private static void load(File f) {
    try {
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      
      String line = "";
      while( (line = br.readLine()) != null) {
        line = new String(line.getBytes(),"UTF-8");
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

  public static void loadLanguage(Language l) {
    load(l.getFile());
  }

  public static void loadLanguageByCode(String code) {
    for(Language l:getAvailableLanguages()) {
      if(l.getCode().equals(code)) loadLanguage(l);
    }
  }

  public static List<Language> getAvailableLanguages() {
    return langs;
  }

  public static boolean isLanguageAvailable(String code) {
    for(Language l:getAvailableLanguages()) {
      if(l.getName().equals(code)) return true;
    }
    return false;
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
