/*****************************************************************************
 * RadialSlider.java
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

package vlcskineditor.items;

import vlcskineditor.*;
import java.awt.*;
import javax.swing.tree.*;

/**
 * RadialSlider item. 
 * <i>Not planned to be implemented for use</i><br>
 * If you know how RadialSliders actually work or are displayed, let me know or fill out the missing parts of this file.
 * @author Daniel Dreibrodt
 */
public class RadialSlider extends Item{
  
  public final String VALUE_DEFAULT = "none";
  public final String TOOLTIPTEXT_DEFAULT = "none";  
  public final int MINANGLE_DEFAULT = 0;
  public final int MAXANGLE_DEFAULT = 360;
  
  public String sequence;
  public int nbimages;
  public int minangle = MINANGLE_DEFAULT;
  public int maxangle = MAXANGLE_DEFAULT;
  public String value = VALUE_DEFAULT;
  public String tooltiptext = TOOLTIPTEXT_DEFAULT;
  
  /** Creates a new instance of RadialSlider */
  public RadialSlider(String xmlcode, Skin s_) {
    s = s_;
    sequence = XML.getValue(xmlcode,"sequence");
    nbimages = XML.getIntValue(xmlcode,"nbimages");
    if(xmlcode.indexOf("miangle=\"")!=-1) minangle = XML.getIntValue(xmlcode,"minangle");
    if(xmlcode.indexOf("maxangle=\"")!=-1) maxangle = XML.getIntValue(xmlcode,"maxangle");
    if(xmlcode.indexOf("value=\"")!=-1) value = XML.getValue(xmlcode,"value");
    if(xmlcode.indexOf("tooltiptext=\"")!=-1) tooltiptext = XML.getValue(xmlcode,"tooltiptext");
    if(xmlcode.indexOf("x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf("y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf("id=\"")!=-1) id = XML.getValue(xmlcode,"id");
    else id = "Unnamed radial slider #"+s.getNewId();
    if(xmlcode.indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf("ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
  }
  public RadialSlider(Skin s_) {
    s = s_;
    id = "Unnamed radial slider #"+s.getNewId();
    showOptions();
  }
  public void update() {
    
  }
  public void showOptions() {
    
  }
  public String returnCode(String indent) {
    String code = indent+"<RadialSlider";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    code+=" sequence=\""+sequence+"\" nbimages=\""+String.valueOf(nbimages)+"\"";
    if (minangle!=MINANGLE_DEFAULT) code+=" minangle=\""+String.valueOf(minangle)+"\"";
    if (maxangle!=MAXANGLE_DEFAULT) code+=" maxangle=\""+String.valueOf(maxangle)+"\"";
    if (!value.equals(VALUE_DEFAULT)) code+=" value=\""+value+"\"";
    if (!tooltiptext.equals(TOOLTIPTEXT_DEFAULT)) code+=" tooltiptext=\""+tooltiptext+"\"";
    if (!lefttop.equals(LEFTTOP_DEFAULT)) code+=" lefttop=\""+lefttop+"\"";
    if (!rightbottom.equals(RIGHTBOTTOM_DEFAULT)) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (!help.equals(HELP_DEFAULT)) code+=" help=\""+help+"\"";
    if (!visible.equals(VISIBLE_DEFAULT)) code+=" visible=\""+visible+"\"";
    code+="/>";
    return code;
  }
  public void draw(Graphics2D g, int z) {
    
  }
  public void draw(Graphics2D g, int x_, int y_, int z) {
    
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("RadialSlider: "+id);       
    return node;
  }
  
}
