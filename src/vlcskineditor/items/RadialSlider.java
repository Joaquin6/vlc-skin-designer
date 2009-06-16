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
import javax.swing.JOptionPane;
import javax.swing.tree.*;
import org.w3c.dom.Node;

/**
 * RadialSlider item. 
 * <i>Not planned to be implemented for use as RadialSliders are not enough documented.</i><br>
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
  
  {
    type = Language.get("RADIALSLIDER");
  }

  /**
   * Parses a Radial Slider from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public RadialSlider(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);
    sequence = XML.getStringAttributeValue(n, "sequence", sequence);
    nbimages = XML.getIntAttributeValue(n, "nbimages", nbimages);
    minangle = XML.getIntAttributeValue(n, "minangle", minangle);
    maxangle = XML.getIntAttributeValue(n, "maxangle", maxangle);
    value = XML.getStringAttributeValue(n, "value", value);
    tooltiptext = XML.getStringAttributeValue(n, "tooltiptext", tooltiptext);
    created = true;
  } 

  /**
   * Creates an empty radial slider and shows a dialog to edit it
   * @param s_ The parent skin
   */
  public RadialSlider(Skin s_) {
    s = s_;
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();
  }

  /**
   * Creates a copy of a radial slider
   * @param r The slider to copy
   */
  public RadialSlider(RadialSlider r) {
    super(r);
    sequence = r.sequence;
    nbimages = r.nbimages;
    minangle = r.minangle;
    maxangle = r.maxangle;
    value = r.value;
    tooltiptext = r.tooltiptext;
  }

  @Override
  public void update() {
    //TODO
    created = true;
    updateToGlobalVariables();
  }

  @Override
  public void showOptions() {
    //TODO
    JOptionPane.showMessageDialog(s.m, Language.get("ERROR_RADIALSLIDER"),Language.get("ERROR_RADIALSLIDER_TITLE"),JOptionPane.ERROR_MESSAGE);
  }
  
  @Override
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

  @Override
  public void draw(Graphics2D g, int z) {
    draw(g,offsetx,offsety,z);
  }

  @Override
  public void draw(Graphics2D g, int x_, int y_, int z) {
    
  }
  
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("RadialSlider: "+id);       
    return node;
  }
  
}
