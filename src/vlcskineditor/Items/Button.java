/*****************************************************************************
 * Button.java
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;

/**
 * Button item
 * @author Daniel Dreibrodt
 */
public class Button extends Item implements ActionListener{
  
  public final String DOWN_DEFAULT = "none";
  public final String OVER_DEFAULT = "none";
  public final String ACTION_DEFAULT = "none";
  public final String TOOLTIPTEXT_DEFAULT = "";
  public String up;
  public String down = DOWN_DEFAULT;
  public String over = OVER_DEFAULT;
  public String action = ACTION_DEFAULT;
  public String tooltiptext = TOOLTIPTEXT_DEFAULT;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, up_tf, down_tf, over_tf, action_tf, tooltiptext_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb;
  JButton visible_btn, action_btn, ok_btn, help_btn;
  
  ActionEditor action_ae;
  
  /** Creates a new instance of Button */
  public Button(String xmlcode, Skin s_) {
    s = s_;
    up = XML.getValue(xmlcode,"up");
    if(xmlcode.indexOf("down=\"")!=-1) down = XML.getValue(xmlcode,"down");
    if(xmlcode.indexOf("over=\"")!=-1) over = XML.getValue(xmlcode,"over");
    if(xmlcode.indexOf("action=\"")!=-1) action = XML.getValue(xmlcode,"action");
    if(xmlcode.indexOf("x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf("y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf("id=\"")!=-1) id = XML.getValue(xmlcode,"id"); 
    else id = "Unnamed button #"+s.getNewId();
    if(xmlcode.indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf("ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
    if(xmlcode.indexOf("tooltiptext=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"tooltiptext");
    if(xmlcode.indexOf(" visible=\"")!=-1) visible = XML.getValue(xmlcode,"visible");
  }
  public Button(Skin s_) {
    s=s_;
    up = "";
    id = "Unnamed button #"+s.getNewId();
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
    
    up = up_tf.getText();
    over = over_tf.getText();
    down = down_tf.getText();
    action = action_tf.getText();
    tooltiptext = tooltiptext_tf.getText();
    
    s.updateItems();    
    s.expandItem(id);
    frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Button settings");
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
      visible_btn = new JButton("",s.m.help_icon);
      visible_btn.addActionListener(this);
      JLabel help_l = new JLabel("Help Text:");
      help_tf = new JTextField();
      help_tf.setToolTipText("Help text for the current control. The variable '$H' will be expanded to this value when the mouse hovers the current control.");
      
      JLabel up_l = new JLabel("Normal image*:");
      up_tf = new JTextField();
      JLabel over_l = new JLabel("Mouse-over image:");
      over_tf = new JTextField();
      JLabel down_l = new JLabel("Mouse-click image:");
      down_tf = new JTextField();
      JLabel action_l = new JLabel("Action:");
      action_tf = new JTextField();
      action_btn = new JButton("",s.m.editor_icon);
      action_btn.addActionListener(this);
      JLabel tooltiptext_l = new JLabel("Tooltiptext:");
      tooltiptext_tf = new JTextField();
      
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
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));
      general.setMinimumSize(new Dimension(240,285));
      general.setPreferredSize(new Dimension(240,285));
      general.setMaximumSize(new Dimension(240,285));
      frame.add(general);
      
      JPanel button = new JPanel(null);
      button.add(up_l);
      button.add(up_tf);
      up_l.setBounds(5,15,75,24);
      up_tf.setBounds(85,15,150,24);
      button.add(over_l);
      button.add(over_tf);
      over_l.setBounds(5,45,75,24);
      over_tf.setBounds(85,45,150,24);
      button.add(down_l);
      button.add(down_tf);
      down_l.setBounds(5,75,75,24);
      down_tf.setBounds(85,75,150,24);
      button.add(action_l);
      button.add(action_tf);
      button.add(action_btn);
      action_l.setBounds(5,105,75,24);
      action_tf.setBounds(85,105,120,24);
      action_btn.setBounds(210,105,24,24);
      button.add(tooltiptext_l);
      button.add(tooltiptext_tf);
      tooltiptext_l.setBounds(5,135,75,24);
      tooltiptext_tf.setBounds(85,135,150,24);
      button.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Button Attributes"));
      button.setMinimumSize(new Dimension(240,165));
      button.setPreferredSize(new Dimension(240,165));
      button.setMaximumSize(new Dimension(240,165));
      frame.add(button);
      
      
      frame.add(ok_btn);
      frame.add(help_btn);      
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,520));
      frame.setPreferredSize(new Dimension(250,520));
      frame.setMaximumSize(new Dimension(250,520));
      
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
    
    up_tf.setText(up);
    over_tf.setText(over);
    down_tf.setText(down);
    action_tf.setText(action);
    tooltiptext_tf.setText(tooltiptext);
    
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
      if(s.getResource(up_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+up_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(!over_tf.getText().equals("none") && s.getResource(over_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+over_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(!down_tf.getText().equals("none") && s.getResource(down_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+down_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
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
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Button"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    else if(e.getSource().equals(visible_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#boolexpr"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    else if(e.getSource().equals(action_btn)) {
      if (action_ae==null) action_ae = new ActionEditor(this);
      action_ae.editAction(action_tf.getText());      
    }
  }
  public void actionWasEdited(ActionEditor ae) {
    if(ae==action_ae) action_tf.setText(ae.getCode());
  }
  public String returnCode() {
    String code = "<Button";
    code+=" up=\""+up+"\"";
    if (down!=DOWN_DEFAULT) code+=" down=\""+down+"\"";
    if (over!=OVER_DEFAULT) code+=" over=\""+over+"\"";
    if (action!=ACTION_DEFAULT) code+=" action=\""+action+"\"";
    if (id!=ID_DEFAULT) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if (lefttop!=LEFTTOP_DEFAULT) code+=" lefttop=\""+lefttop+"\"";
    if (rightbottom!=RIGHTBOTTOM_DEFAULT) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (help!=HELP_DEFAULT) code+=" help=\""+help+"\"";
    if (visible!=VISIBLE_DEFAULT) code+=" visible=\""+visible+"\"";
    if (tooltiptext!=TOOLTIPTEXT_DEFAULT) code+=" tooltiptext=\""+tooltiptext+"\"";
    code+="/>";
    return code;
  }
  public void draw(Graphics2D g) {
    draw(g,offsetx,offsety);
  }
  public void draw(Graphics2D g, int x_, int y_) {
    if(s.gvars.parseBoolean(visible)==false) return;
    java.awt.image.BufferedImage bi = s.getBitmapImage(up);
    if(!hovered) g.drawImage(bi,x+x_,y+y_,null);
    else if(!clicked) g.drawImage(s.getBitmapImage(over),x+x_,y+y_,null);
    else g.drawImage(s.getBitmapImage(down),x+x_,y+y_,null);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect(x+x_,y+y_,bi.getWidth()-1,bi.getHeight()-1);
    }
  }
  public boolean contains(int x_, int y_) {
    java.awt.image.BufferedImage bi = s.getBitmapImage(up);
    return (x_>=x+offsetx && x_<=x+bi.getWidth()+offsetx && y_>=y+offsety && y_<=y+bi.getHeight()+offsety);
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Button: "+id);     
    return node;
  }  
}
