/*****************************************************************************
 * XML.java
 *****************************************************************************
 * Copyright (C) 2006 Daniel Dreibrodt
 *
 * This file is part of __PACKAGE_NAME__
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

/**
 * XML Handler
 * @author Daniel Dreibrodt
 */
public class XML {
  
  /** Creates a new instance of XML */
  public XML() {
  }
  /**
   * Returns the value of a xml attribute from a given line of xml code
   * @param line  The XML Code
   * @param field The name of the attribute whose value is to be retrieved
   * @return      The attribute's value
   */
  public static String getValue(String line, String field) {    
    int value_start = line.indexOf(" "+field+"=\"") + field.length() +3;
    int value_end = line.indexOf("\"",value_start);
    return line.substring(value_start,value_end);
  }
  public static int getIntValue(String line, String field) {
    int i = 0;
    try {
      i = Integer.parseInt(getValue(line,field));
    }
    catch (Exception e) {
      System.out.println("Could not parse int from getValue("+line+","+field+"): "+getValue(line,field));
    }
    return i;
  }
  public static boolean getBoolValue(String line, String field) {
    boolean b = false;
    try {
      b = Boolean.parseBoolean(getValue(line,field));
    }
    catch (Exception e) {
      System.out.println("Could not parse boolean from getValue("+line+","+field+"): "+getValue(line,field));
    }
    return b;
  }
}
