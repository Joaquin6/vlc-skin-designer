/*****************************************************************************
 * Text.java
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
import vlcskineditor.history.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;

/**
 * Text item
 * @author Daniel Dreibrodt
 */
public class Text extends Item implements ActionListener{
  
  public final String TEXT_DEFAULT = "";
  public final String COLOR_DEFAULT = "#000000";
  public final int WIDTH_DEFAULT = 0;
  public final String ALIGNMENT_DEFAULT = "left";
  public final String SCROLLING_DEFAULT = "auto";
  
  public String font;
  public String text = TEXT_DEFAULT;
  public String color = COLOR_DEFAULT;
  public int width = WIDTH_DEFAULT;
  public String alignment = ALIGNMENT_DEFAULT;
  public String scrolling = SCROLLING_DEFAULT;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, text_tf, font_tf, color_tf, width_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb, alignment_cb, scrolling_cb;
  JButton visible_btn, color_btn, ok_btn, cancel_btn, help_btn;
  
  {
    type = Language.get("TEXT");
  }

  /**
   * Parses a text item from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public Text(Node n, Skin s_) {
    s = s_;
    
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));

    font = XML.getStringAttributeValue(n, "font", font);
    text = XML.getStringAttributeValue(n, "text", text);
    alignment = XML.getStringAttributeValue(n, "alignment", alignment);
    scrolling = XML.getStringAttributeValue(n, "scrolling", scrolling);
    color = XML.getStringAttributeValue(n, "color", color);
    width = XML.getIntAttributeValue(n, "width", width);

    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);

    created = true;
  }
  
  /** Creates a new instance of Text */
  public Text(String xmlcode, Skin s_) {
    s = s_;
    font = XML.getValue(xmlcode,"font");
    if(xmlcode.indexOf(" text=\"")!=-1) text = XML.getValue(xmlcode,"text");
    if(xmlcode.indexOf(" alignment=\"")!=-1) alignment = XML.getValue(xmlcode,"alignment");
    if(xmlcode.indexOf(" scrolling=\"")!=-1) scrolling = XML.getValue(xmlcode,"scrolling");
    if(xmlcode.indexOf(" color=\"")!=-1) color = XML.getValue(xmlcode,"color");
    if(xmlcode.indexOf(" x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf(" y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf(" width=\"")!=-1) width = XML.getIntValue(xmlcode,"width");    
    if(xmlcode.indexOf(" id=\"")!=-1) id = XML.getValue(xmlcode,"id");
    else id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    if(xmlcode.indexOf(" lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf(" rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf(" xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf(" ykeepratio=\"")!=-1) ykeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
    if(xmlcode.indexOf(" visible=\"")!=-1) visible = XML.getValue(xmlcode,"visible");
    created = true;
  }
  public Text(Skin s_) {
    s = s_;
    font = "defaultfont";
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();
  }
  @Override
  public void update() {
    if(!created) {
      id = id_tf.getText();
      x = Integer.parseInt(x_tf.getText());
      y = Integer.parseInt(y_tf.getText());
      lefttop = lefttop_cb.getSelectedItem().toString();
      rightbottom = rightbottom_cb.getSelectedItem().toString();
      xkeepratio = Boolean.parseBoolean(xkeepratio_cb.getSelectedItem().toString());
      ykeepratio = Boolean.parseBoolean(ykeepratio_cb.getSelectedItem().toString());
      visible = visible_tf.getText();
      help = help_tf.getText();

      text = text_tf.getText();
      font = font_tf.getText();
      color = color_tf.getText();
      width = Integer.parseInt(width_tf.getText());
      alignment = alignment_cb.getSelectedItem().toString();
      scrolling = scrolling_cb.getSelectedItem().toString();

      s.updateItems();
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      created = true;
      
      ItemAddEvent tae = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(tae);
    }
    else {      
      TextEditEvent tee = new TextEditEvent(this);
      
      id = id_tf.getText();
      x = Integer.parseInt(x_tf.getText());
      y = Integer.parseInt(y_tf.getText());
      lefttop = lefttop_cb.getSelectedItem().toString();
      rightbottom = rightbottom_cb.getSelectedItem().toString();
      xkeepratio = Boolean.parseBoolean(xkeepratio_cb.getSelectedItem().toString());
      ykeepratio = Boolean.parseBoolean(ykeepratio_cb.getSelectedItem().toString());
      visible = visible_tf.getText();
      help = help_tf.getText();

      text = text_tf.getText();
      font = font_tf.getText();
      color = color_tf.getText();
      width = Integer.parseInt(width_tf.getText());
      alignment = alignment_cb.getSelectedItem().toString();
      scrolling = scrolling_cb.getSelectedItem().toString();

      s.updateItems();
      s.expandItem(id);
      
      tee.setNew();
      s.m.hist.addEvent(tee);      
    }
    updateToGlobalVariables();
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_TEXT_TITLE"));
      frame.setIconImage(Main.edit_icon.getImage());
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      if(!created) frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel(Language.get("WIN_ITEM_ID"));
      id_tf = new JTextField();      
      JLabel x_l = new JLabel(Language.get("WIN_ITEM_X"));
      x_tf = new JTextField();      
      x_tf.setDocument(new NumbersOnlyDocument());
      JLabel y_l = new JLabel(Language.get("WIN_ITEM_Y"));
      y_tf = new JTextField();      
      y_tf.setDocument(new NumbersOnlyDocument());
      String[] align_values = {"lefttop", "leftbottom", "righttop", "rightbottom"};
      JLabel lefttop_l = new JLabel(Language.get("WIN_ITEM_LEFTTOP"));
      lefttop_cb = new JComboBox(align_values);
      lefttop_cb.setToolTipText(Language.get("WIN_ITEM_LEFTTOP_TIP"));
      JLabel rightbottom_l = new JLabel(Language.get("WIN_ITEM_RIGHTBOTTOM"));
      rightbottom_cb = new JComboBox(align_values);
      rightbottom_cb.setToolTipText(Language.get("WIN_ITEM_RIGHTBOTTOM_TIP"));
      Object[] bool_values = { true, false };
      JLabel xkeepratio_l = new JLabel(Language.get("WIN_ITEM_XKEEPRATIO"));
      xkeepratio_cb = new JComboBox(bool_values);
      xkeepratio_cb.setToolTipText(Language.get("WIN_ITEM_XKEEPRATIO_TIP"));
      JLabel ykeepratio_l = new JLabel(Language.get("WIN_ITEM_YKEEPRATIO"));
      ykeepratio_cb = new JComboBox(bool_values);
      ykeepratio_cb.setToolTipText(Language.get("WIN_ITEM_YKEEPRATIO_TIP"));
      JLabel visible_l = new JLabel(Language.get("WIN_ITEM_VISIBLE"));
      visible_tf = new JTextField();
      visible_btn = new JButton("",Main.help_icon);
      visible_btn.addActionListener(this);      
      JLabel help_l = new JLabel(Language.get("WIN_ITEM_HELP"));
      help_tf = new JTextField();
      help_tf.setToolTipText(Language.get("WIN_ITEM_HELP_TIP"));
      
      JLabel text_l = new JLabel(Language.get("WIN_TEXT_TEXT"));
      text_tf = new JTextField();
      JLabel font_l = new JLabel(Language.get("WIN_TEXT_FONT"));
      font_tf = new JTextField();
      JLabel color_l = new JLabel(Language.get("WIN_TEXT_COLOR"));
      color_tf = new JTextField();
      color_btn = new JButton(Language.get("WIN_TEXT_CHOOSE"));
      color_btn.addActionListener(this);
      JLabel width_l = new JLabel(Language.get("WIN_ITEM_WIDTH"));
      width_tf = new JTextField();
      width_tf.setDocument(new NumbersOnlyDocument());
      JLabel alignment_l = new JLabel(Language.get("WIN_TEXT_ALIGNMENT"));
      String[] alignment_values = { "left","center","right" };
      alignment_cb = new JComboBox(alignment_values);
      JLabel scrolling_l = new JLabel(Language.get("WIN_TEXT_SCROLLING"));
      String[] scrolling_values = { "none","auto","manual" };      
      scrolling_cb = new JComboBox(scrolling_values);

      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      ok_btn.setPreferredSize(new Dimension(70,25));
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      cancel_btn.setPreferredSize(new Dimension(70,25));
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);
      help_btn.setPreferredSize(new Dimension(70,25));
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_l.setBounds(5,15,75,24);
      id_tf.setBounds(85,15,150,24);
      general.add(x_l);
      general.add(x_tf);
      x_l.setBounds(5,45,75,24);
      x_tf.setBounds(85,45,150,24);
      general.add(y_l);
      general.add(y_tf);
      y_l.setBounds(5,75,75,24);
      y_tf.setBounds(85,75,150,24);      
      general.add(lefttop_l);
      general.add(lefttop_cb);
      lefttop_l.setBounds(5,105,75,24);
      lefttop_cb.setBounds(85,105,150,24);
      general.add(rightbottom_l);
      general.add(rightbottom_cb);
      rightbottom_l.setBounds(5,135,75,24);
      rightbottom_cb.setBounds(85,135,150,24);
      general.add(xkeepratio_l);
      general.add(xkeepratio_cb);
      xkeepratio_l.setBounds(5,165,75,24);
      xkeepratio_cb.setBounds(85,165,150,24);
      general.add(ykeepratio_l);
      general.add(ykeepratio_cb);
      ykeepratio_l.setBounds(5,195,75,24);
      ykeepratio_cb.setBounds(85,195,150,24);
      general.add(visible_l);
      general.add(visible_tf);
      general.add(visible_btn);
      visible_l.setBounds(5,225,75,24);
      visible_tf.setBounds(85,225,120,24);
      visible_btn.setBounds(210,225,24,24);
      general.add(help_l);
      general.add(help_tf);
      help_l.setBounds(5,255,75,24);
      help_tf.setBounds(85,255,150,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));
      general.setMinimumSize(new Dimension(240,285));
      general.setPreferredSize(new Dimension(240,285));
      general.setMaximumSize(new Dimension(240,285));
      frame.add(general);
      
      JPanel txt = new JPanel(null);
      txt.add(text_l);
      txt.add(text_tf);
      text_l.setBounds(5,15,75,24);
      text_tf.setBounds(85,15,150,24);
      txt.add(font_l);
      txt.add(font_tf);
      font_l.setBounds(5,45,75,24);
      font_tf.setBounds(85,45,150,24);
      txt.add(color_l);
      txt.add(color_tf);
      txt.add(color_btn);
      color_l.setBounds(5,75,75,24);
      color_tf.setBounds(85,75,50,24);
      color_btn.setBounds(140,75,95,24);
      txt.add(width_l);
      txt.add(width_tf);
      width_l.setBounds(5,105,75,24);
      width_tf.setBounds(85,105,150,24);
      txt.add(alignment_l);
      txt.add(alignment_cb);
      alignment_l.setBounds(5,135,75,24);
      alignment_cb.setBounds(85,135,150,24);
      txt.add(scrolling_l);
      txt.add(scrolling_cb);
      scrolling_l.setBounds(5,165,75,24);
      scrolling_cb.setBounds(85,165,150,24);
      txt.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_TEXT_ATTR")));
      txt.setMinimumSize(new Dimension(240,195));
      txt.setPreferredSize(new Dimension(240,195));
      txt.setMaximumSize(new Dimension(240,195));
      frame.add(txt);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);
      
      frame.setMinimumSize(new Dimension(250,550));
      frame.setPreferredSize(new Dimension(250,550));
      frame.setMaximumSize(new Dimension(250,550));
      
      frame.pack();
      
      frame.getRootPane().setDefaultButton(ok_btn);
    }
    id_tf.setText(id);    
    x_tf.setText(String.valueOf(x));
    y_tf.setText(String.valueOf(y));
    lefttop_cb.setSelectedItem(lefttop);
    rightbottom_cb.setSelectedItem(rightbottom);
    xkeepratio_cb.setSelectedItem(xkeepratio);
    ykeepratio_cb.setSelectedItem(ykeepratio);
    visible_tf.setText(visible);
    help_tf.setText(help);
    
    text_tf.setText(text);
    font_tf.setText(font);
    color_tf.setText(color);
    width_tf.setText(String.valueOf(width));
    alignment_cb.setSelectedItem(alignment);
    scrolling_cb.setSelectedItem(scrolling);
    
    frame.setVisible(true);
  }
  @Override
  public void actionPerformed(ActionEvent e) {    
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_INVALID_MSG"),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i", id_tf.getText()),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }      
      if(!font_tf.getText().equals("defaultfont") && s.getResource(font_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_FONT_NEXIST").replaceAll("%i", font_tf.getText()),Language.get("ERROR_FONT_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      update();
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-text.html");
    }
    else if (e.getSource().equals(color_btn)) {
      Color color_c = JColorChooser.showDialog(frame,Language.get("WIN_TEXT_CHOOSER_TITLE"),Color.decode(color_tf.getText()));
      if (color_c != null) {
        String hex = "#";
        if(color_c.getRed()<16) hex+="0";
        hex+=Integer.toHexString(color_c.getRed()).toUpperCase();
        if(color_c.getGreen()<16) hex+="0";
        hex+=Integer.toHexString(color_c.getGreen()).toUpperCase();
        if(color_c.getBlue()<16) hex+="0";
        hex+=Integer.toHexString(color_c.getBlue()).toUpperCase();
        color_tf.setText(hex);
      }
    }    
    else if(e.getSource().equals(visible_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/boolexpr.html");
    }
    else if(e.getSource().equals(cancel_btn)) {
      if(!created) {
        java.util.List<Item> l = s.getParentListOf(id);
        if(l!=null) l.remove(this);
      }
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
  }
  @Override
  public String returnCode(String indent) {
    String code = indent+"<Text";
    code+=" text=\""+text+"\"";
    code+=" font=\""+font+"\"";
    if (!alignment.equals(ALIGNMENT_DEFAULT)) code+=" alignment=\""+alignment+"\"";
    if (!color.equals(COLOR_DEFAULT)) code+=" color=\""+color+"\"";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if (width!=WIDTH_DEFAULT) code+=" width=\""+String.valueOf(width)+"\"";
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
    draw(g,0,0,z);
  }
  @Override
  public void draw(Graphics2D g, int x_, int y_, int z) {
    if(!created || font==null) return;
    
    Font f = s.getFont(font);
    if(f==null) {
      Resource fr = s.getResource(font);
      if(fr.type.equals("Font")) {
        vlcskineditor.resources.Font fnt = (vlcskineditor.resources.Font)fr;
        f = new Font(Font.SANS_SERIF,Font.PLAIN,fnt.size);
      }
      else {
        f = new Font(Font.SANS_SERIF,Font.PLAIN,12);
      }      
    }    
    if(vis) {
      BufferedImage bi;
      String ptext = s.gvars.parseString(text);
      if(width==0) {
        bi = new BufferedImage((int)g.getFontMetrics().getStringBounds(ptext,g).getWidth(),g.getFontMetrics().getHeight(),BufferedImage.TYPE_INT_ARGB);
      }
      else bi = new BufferedImage(width,g.getFontMetrics().getHeight(),BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = bi.createGraphics();
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      g2d.setFont(f);
      g2d.setColor(Color.decode(color));
      if(width!=0 && alignment.equals("right")) {
        g2d.drawString(ptext,(int)(width-g2d.getFontMetrics().getStringBounds(ptext,g).getWidth()),0+g2d.getFontMetrics().getAscent());
      }
      else if(width!=0 && alignment.equals("center")) {
        g2d.drawString(ptext,(int)((width-g2d.getFontMetrics().getStringBounds(ptext,g).getWidth())/2),0+g2d.getFontMetrics().getAscent());
      }
      else {
        g2d.drawString(ptext,0,0+g2d.getFontMetrics().getAscent());
      }
      g.drawImage(bi,(x+x_)*z,(y+y_)*z,bi.getWidth()*z, bi.getHeight()*z,null);
    }
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect((x+x_)*z,(y+y_)*z,width*z,f.getSize()*z);
    }
  }
  @Override
  public boolean contains(int x_, int y_) {
    Font f = s.getFont(font);
    return (x_>=x+offsetx && x_<=x+width+offsetx && y_>=y+offsety && y_<=y+f.getSize()+offsety);    
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Text: "+id);      
    return node;
  }  
  @Override
  public boolean uses(String id_) {
    return (font.equals(id_));
  }
}
