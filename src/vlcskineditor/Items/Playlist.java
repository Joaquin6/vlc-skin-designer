/*****************************************************************************
 * Playlist.java
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

package vlcskineditor.Items;

import vlcskineditor.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * Playlist item
 * @author Daniel Dreibrodt
 */
public class Playlist extends Item{
  
  /** Creates a new instance of Playlist */
  public Playlist(String xmlcode, Skin s_) {
    type = "Playlist";
    s = s_;
    if(xmlcode.indexOf("x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf("y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf("id=\"")!=-1) id = XML.getValue(xmlcode,"id");
    else id = "Unnamed playlist #"+s.getNewId()+" (deprecated)";
    if(xmlcode.indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf("ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
  }
  public Playlist(Skin s_) {
    s = s_;
    id = "Unnamed playlist #"+s.getNewId()+" (deprecated)";
    showOptions();
  }
  public void showOptions() {
    //Leave empty, maybe automatically convert playlists to flat playtrees
  }
  public String returnCode() {
    return "";
  }
  public void draw(Graphics2D g) {
    
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Playlist: "+id);       
    return node;
  }
  
}
