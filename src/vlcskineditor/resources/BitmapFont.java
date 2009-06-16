/*****************************************************************************
 * BitmapFont.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of VLC Skin Editor
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

package vlcskineditor.resources;

import vlcskineditor.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.w3c.dom.Node;

/**
 * Represents a BitmapFont
 * <i>This class exists only to show BitmapFonts in the resources tree.
 * You can't create or edit a BitmapFont in the editor nor are they displayed in the preview!</i>
 * @author Daniel Dreibrodt
 */
public class BitmapFont extends Resource {
  String file;
  final String TYPE_DEFAULT = "digits";
  String ftype = TYPE_DEFAULT;
  private boolean created = false;
  
  {
    type = "BitmapFont";
  }

  /**
   * Creates a BitmapFont from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public BitmapFont(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", id);
    file = XML.getStringAttributeValue(n, "file", file);
    ftype = XML.getStringAttributeValue(n, "type", ftype);
    created = true;
  }

  /**
   * Creates a copy of a BitmapFont
   * @param f The BitmapFont to copy
   */
  public BitmapFont(BitmapFont f) {
    s = f.s;
    id = f.id;
    file = f.file;
    ftype = f.ftype;
    created = true;
  }


  public void update() {
    
  }

  public void showOptions() {
    JOptionPane.showMessageDialog(s.m, "BitmapFonts are not yet supported.");
  }
  public String returnCode(String indent) {
    String code=indent+"<BitmapFont id=\""+id+"\" file=\""+file+"\"";
    if (!ftype.equals(TYPE_DEFAULT)) code+=" type=\""+ftype+"\"";
    code+="/>\n";
    return code;
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("BitmapFont: "+id);    
    return node;
  }
  
}
