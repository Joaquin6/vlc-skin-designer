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

import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import vlcskineditor.items.*;

/**
 * XML Handler
 * @author Daniel Dreibrodt
 */
public class XML {
  
  
  /**
   * Returns the value of a xml attribute from a given line of xml code
   * @deprecated Now the DOM model is used
   * @param line  The XML Code
   * @param field The name of the attribute whose value is to be retrieved
   * @return      The attribute's value
   */
  @Deprecated
  public static String getValue(String line, String field) {    
    int value_start = line.indexOf(" "+field+"=\"") + field.length() +3;
    int value_end = line.indexOf("\"",value_start);
    return line.substring(value_start,value_end);
  }
  
  /**
   * Returns the value of a xml attribute from a given line of xml code as an integer
   * @deprecated Now the DOM model is used
   * @param line
   * @param field
   * @return Integer value of the attribute
   */
  @Deprecated
  public static int getIntValue(String line, String field) {
    int i = 0;
    try {
      i = Integer.parseInt(getValue(line,field).trim());
    }
    catch (Exception e) {
      System.err.println("Could not parse int from getValue("+line+","+field+"): "+getValue(line,field));
    }
    return i;
  }
  
  /**
   * Returns the value of a xml attribute from a given line of xml code as a boolean value
   * @deprecated Now the DOM model is used
   * @param line
   * @param field
   * @return Boolean value of the attribute
   */
  @Deprecated
  public static boolean getBoolValue(String line, String field) {
    boolean b = false;
    try {
      b = Boolean.parseBoolean(getValue(line,field).trim());
    }
    catch (Exception e) {
      System.err.println("Could not parse boolean from getValue("+line+","+field+"): "+getValue(line,field));
    }
    return b;
  }
  
  /**
   * Gets the value of an XML node's attribute if it exists
   * @param n The XML node
   * @param name The name of the attribute
   * @param oldvalue The value which is returned if the attribute is not set in the given node
   * @return If the attribute is set, the attribute's value is returned. Otherwise the given old value.
   */
  public static String getStringAttributeValue(Node n, String name, String oldvalue) {
    if(n.getAttributes().getNamedItem(name)!=null) {
      String value = n.getAttributes().getNamedItem(name).getNodeValue();
      //The DTD turns all unset id-attributes to "none", but the skin editor needs unique ids
      if(name.equals("id") && value.equals("none")) return oldvalue;
      else return value;
    }
    else return oldvalue;
  }
  
  /**
   * Gets the value of an XML node's attribute as an integer if it exists
   * @param n The XML node
   * @param name The name of the attribute
   * @param oldvalue The value which is returned if the attribute is not set in the given node
   * @return If the attribute is set, the attribute's value is returned. Otherwise the given old value.
   */
  public static int getIntAttributeValue(Node n, String name, int oldvalue) {
    try {
      if(n.getAttributes().getNamedItem(name)!=null) return Integer.parseInt(n.getAttributes().getNamedItem(name).getNodeValue());
    } catch(NumberFormatException ex) {
      System.err.println("Tried to get a node's attribute as an integer, although it is no integer. It's value is "+n.getAttributes().getNamedItem(name).getNodeValue());
    }
    return oldvalue;
  }
  
  /**
   * Gets the value of an XML node's attribute as a boolean value if it exists
   * @param n The XML node
   * @param name The name of the attribute
   * @param oldvalue The value which is returned if the attribute is not set in the given node
   * @return If the attribute is set, the attribute's value is returned. Otherwise the given old value.
   */
  public static boolean getBoolAttributeValue(Node n, String name, boolean oldvalue) {
    if(n.getAttributes().getNamedItem(name)!=null) return Boolean.parseBoolean(n.getAttributes().getNamedItem(name).getNodeValue());    
    else return oldvalue;
  }

  /**
   * Parses all child items of a XML node (used by Layouts/Groups/Panels)
   * @param n The XML node
   * @param children The list to which the children will be added
   * @param s The parent Skin
   */
  public static void parseChildItems(Node n, List<Item> children, Skin s) {
    NodeList nodes = n.getChildNodes();
    for(int i=0;i<nodes.getLength();i++) {
      if(nodes.item(i).getNodeName().equals("Anchor"))
        children.add(new Anchor(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Button"))
        children.add(new Button(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Checkbox"))
        children.add(new Checkbox(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Group"))
        children.add(new Group(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Image"))
        children.add(new Image(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Panel"))
        children.add(new Panel(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Playtree"))
        children.add(new Playtree(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Playlist")) {
        ((Element)nodes.item(i)).setAttribute("flat", "true");
        children.add(new Playtree(nodes.item(i), s));
      }
      else if(nodes.item(i).getNodeName().equals("RadialSlider"))
        children.add(new RadialSlider(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Slider"))
        children.add(new Slider(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Text"))
        children.add(new Text(nodes.item(i), s));
      else if(nodes.item(i).getNodeName().equals("Video"))
        children.add(new Video(nodes.item(i), s));
    }
  }
}
