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

package vlcskineditor.Items;

import vlcskineditor.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.tree.*;

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
  JButton color_btn, ok_btn, help_btn;
  
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
    else id = "Unnamed text #"+s.getNewId();
    if(xmlcode.indexOf(" lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf(" rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf(" xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf(" ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
  }
  public Text(Skin s_) {
    s = s_;
    font = "defaultfont";
    id = "Unnamed text #"+s.getNewId();
    showOptions();
  }
  public void update() {
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
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Text settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();      
      JLabel x_l = new JLabel("X:");
      x_tf = new JTextField();      
      x_tf.setDocument(new NumbersOnlyDocument());
      JLabel y_l = new JLabel("Y:");
      y_tf = new JTextField();      
      y_tf.setDocument(new NumbersOnlyDocument());
      String[] align_values = {"lefttop", "leftbottom", "righttop", "rightbottom"};
      JLabel lefttop_l = new JLabel("Lefttop:");
      lefttop_cb = new JComboBox(align_values);
      lefttop_cb.setToolTipText("Indicate to which corner of the Layout the top-left-hand corner of this item is attached, in case of resizing.");
      JLabel rightbottom_l = new JLabel("Rightbottom:");
      rightbottom_cb = new JComboBox(align_values);
      rightbottom_cb.setToolTipText("Indicate to which corner of the Layout the bottom-right-hand corner of this item is attached, in case of resizing.");
      Object[] bool_values = { true, false };
      JLabel xkeepratio_l = new JLabel("Keep X Ratio:");
      xkeepratio_cb = new JComboBox(bool_values);
      xkeepratio_cb.setToolTipText("When set to true, the behaviour of the horizontal resizing is changed. For example, if initially the space to the left of the control is twice as big as the one to its right, this will stay the same during any horizontal resizing. The width of the control stays constant.");
      JLabel ykeepratio_l = new JLabel("Keep Y Ratio:");
      ykeepratio_cb = new JComboBox(bool_values);
      ykeepratio_cb.setToolTipText("When set to true, the behaviour of the vertical resizing is changed. For example, if initially the space to the top of the control is twice as big as the one to its bottom, this will stay the same during any vertical resizing. The height of the control stays constant.");
      JLabel visible_l = new JLabel("Visibility:");
      visible_tf = new JTextField();
      JLabel help_l = new JLabel("Help Text:");
      help_tf = new JTextField();
      help_tf.setToolTipText("Help text for the current control. The variable '$H' will be expanded to this value when the mouse hovers the current control.");
      
      JLabel text_l = new JLabel("Text:");
      text_tf = new JTextField();
      JLabel font_l = new JLabel("Font:");
      font_tf = new JTextField();
      JLabel color_l = new JLabel("Color:");
      color_tf = new JTextField();
      color_btn = new JButton("Choose...");
      color_btn.addActionListener(this);
      JLabel width_l = new JLabel("Width:");
      width_tf = new JTextField();
      width_tf.setDocument(new NumbersOnlyDocument());
      JLabel alignment_l = new JLabel("Alignment:");
      String[] alignment_values = { "left","center","right" };
      alignment_cb = new JComboBox(alignment_values);
      JLabel scrolling_l = new JLabel("Scrolling:");
      String[] scrolling_values = { "none","auto","manual" };      
      scrolling_cb = new JComboBox(scrolling_values);
      
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      
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
      y_tf.setBounds(85,75,75,24);      
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
      visible_l.setBounds(5,225,75,24);
      visible_tf.setBounds(85,225,150,24);
      general.add(help_l);
      general.add(help_tf);
      help_l.setBounds(5,255,75,24);
      help_tf.setBounds(85,255,150,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "General Attributes"));
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
      txt.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Text Attributes"));
      txt.setMinimumSize(new Dimension(240,195));
      txt.setPreferredSize(new Dimension(240,195));
      txt.setMaximumSize(new Dimension(240,195));
      frame.add(txt);
      
      frame.add(ok_btn);
      frame.add(help_btn);      
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,550));
      frame.setPreferredSize(new Dimension(250,550));
      frame.setMaximumSize(new Dimension(250,550));
      
      frame.pack();
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
  public void actionPerformed(ActionEvent e) {    
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,"Please enter a valid ID!","ID not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame,"The ID \""+id_tf.getText()+"\" already exists, please choose another one.","ID not valid",JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }      
      if(!font_tf.getText().equals("defaultfont") && s.getResource(font_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The Font \""+font_tf.getText()+"\" does not exist.","Font not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      update();
      frame.setVisible(false);      
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Text"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    else if (e.getSource().equals(color_btn)) {
      Color color = JColorChooser.showDialog(frame,"Choose text color",Color.decode(color_tf.getText()));
      if (color != null) {
        String hex = "#";
        if(color.getRed()<16) hex+="0";
        hex+=Integer.toHexString(color.getRed()).toUpperCase();
        if(color.getGreen()<16) hex+="0";
        hex+=Integer.toHexString(color.getGreen()).toUpperCase();
        if(color.getBlue()<16) hex+="0";
        hex+=Integer.toHexString(color.getBlue()).toUpperCase();
        color_tf.setText(hex);
      }
    }    
  }
  public String returnCode() {
    String code = "<Text";
    code+=" text=\""+text+"\"";
    code+=" font=\""+font+"\"";
    if (color!=COLOR_DEFAULT) code+=" color=\""+color+"\"";
    if (id!=ID_DEFAULT) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if (lefttop!=LEFTTOP_DEFAULT) code+=" lefttop=\""+lefttop+"\"";
    if (rightbottom!=RIGHTBOTTOM_DEFAULT) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (help!=HELP_DEFAULT) code+=" help=\""+help+"\"";
    code+="/>";
    return code;
  }
  public void draw(Graphics2D g) {
    draw(g,0,0);
  }
  public void draw(Graphics2D g, int x_, int y_) {
    Font f = s.getFont(font);
    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    g.setFont(f);
    g.setColor(Color.decode(color));
    BufferedImage bi;
    if(width==0) {
      bi = new BufferedImage((int)g.getFontMetrics().getStringBounds(text,g).getWidth(),g.getFontMetrics().getHeight(),BufferedImage.TYPE_INT_ARGB);
    }
    else bi = new BufferedImage(width,g.getFontMetrics().getHeight(),BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = bi.createGraphics();    
    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    g2d.setFont(f);
    g2d.setColor(Color.decode(color));
    if(width!=0 && alignment.equals("right")) {
      g2d.drawString(text,(int)(width-g2d.getFontMetrics().getStringBounds(text,g).getWidth()),0+g2d.getFontMetrics().getAscent());
    }    
    else if(width!=0 && alignment.equals("center")) {
      g2d.drawString(text,(int)((width-g2d.getFontMetrics().getStringBounds(text,g).getWidth())/2),0+g2d.getFontMetrics().getAscent());
    }
    else {
      g2d.drawString(text,0,0+g2d.getFontMetrics().getAscent());  
    }   
    g.drawImage(bi,x+x_,y+y_,null);         
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Text: "+id);      
    return node;
  }
  
}
