/*****************************************************************************
 * Language.java
 *****************************************************************************
 * Copyright (C) 2009 Daniel Dreibrodt
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
      FileInputStream fis = new FileInputStream(index);
      String text = "";
      byte[] chars = new byte[(int)index.length()];
      while(fis.read(chars)!=-1) text+=new String(chars, "UTF-8");

      String[] lines = text.split("\\n");

      for(String line:lines) {
        line = line.trim();
        if(!line.startsWith("#")) {
          String[] fields = line.split("\\|");
          if(fields.length == 3) {
            langs.add(new Language(fields[0], fields[1], fields[2]));
          }
        }
      }
      fis.close();
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private String name,  code;
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
    this.file = new File("lang", file);
  }

  /**
   * Gets the languages ISO code
   * @return The ISO language code
   */
  public String getCode() {
    return code;
  }

  /**
   * Gets the language's name
   * @return The native name of the language
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the language file
   * @return The language file
   */
  public File getFile() {
    return file;
  }

  @Override
  public String toString() {
    return name + " (" + code + ")";
  }

  /**
   * Loads the current language from a file
   * @param f The language file, e.g. lang/English.txt
   */
  private static void load(File f) {
    try {
      FileInputStream fis = new FileInputStream(f);
      String text = "";
      byte[] chars = new byte[(int)f.length()];
      while(fis.read(chars)!=-1) text+=new String(chars, "UTF-8");

      String[] lines = text.split("\\n");

      for(String line:lines) {
        line = line.trim();
        if(line.startsWith("@include")) {
          String file = line.substring(9);
          load(new File(f.getParent() + File.separator + file));
        } else if(!line.startsWith("#")) {
          String[] fields = line.split("\\|");
          if(fields.length == 2) {
            strings.put(fields[0], fields[1].replaceAll("\\\\n", "\n"));
          }
        }
      }

      fis.close();
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
    return (s != null) ? s : key;
  }

  /**
   * Loads a given language.
   * Subsequent calls to Language.get() will give the strings defined in the given language.
   * @param l The language to load
   */
  public static void loadLanguage(Language l) {
    load(l.getFile());
  }

  /**
   * Loads a given language by its ISO language code.
   * Subsequent calls to Language.get() will give the strings defined in the given language.
   * @param code The ISO language code
   */
  public static void loadLanguageByCode(String code) {
    for(Language l : getAvailableLanguages()) {
      if(l.getCode().equals(code)) {
        loadLanguage(l);
      }
    }
  }

  /**
   * List available translations
   * @return The available languages in the lang folder
   */
  public static List<Language> getAvailableLanguages() {
    return langs;
  }

  /**
   * Checks whether a translation for a given language is available
   * @param code The languages ISO language code
   * @return True if a translation is available, false otherwise
   */
  public static boolean isLanguageAvailable(String code) {
    for(Language l : getAvailableLanguages()) {
      if(l.getName().equals(code)) {
        return true;
      }
    }
    return false;
  }

}
