/*****************************************************************************
 * Checkbox.java
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
import javax.swing.*;
import javax.swing.tree.*;

/**
 * Checkbox item
 * @author Daniel Dreibrodt
 */
public class Checkbox extends Item implements ActionListener{
  
  public final String DOWN1_DEFAULT = "none";
  public final String DOWN2_DEFAULT = "none";
  public final String OVER1_DEFAULT = "none";
  public final String OVER2_DEFAULT = "none";  
  public final String ACTION1_DEFAULT = "none";
  public final String ACTION2_DEFAULT = "none";
  public final String TOOLTIPTEXT1_DEFAULT = "";
  public final String TOOLTIPTEXT2_DEFAULT = "";
  
  public String up1, up2;  
  public String down1 = DOWN1_DEFAULT;
  public String down2 = DOWN2_DEFAULT;
  public String over1 = OVER1_DEFAULT;
  public String over2 = OVER2_DEFAULT;
  public String state;
  public String action1 = ACTION1_DEFAULT;
  public String action2 = ACTION2_DEFAULT;
  public String tooltiptext1 = TOOLTIPTEXT1_DEFAULT;
  public String tooltiptext2 = TOOLTIPTEXT2_DEFAULT;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, up1_tf, down1_tf, over1_tf, action1_tf, tooltiptext1_tf;
  JTextField up2_tf, down2_tf, over2_tf, action2_tf, tooltiptext2_tf, state_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb;
  JButton ok_btn, help_btn;
  
  /** Creates a new instance of Checkbox */
  public Checkbox(String xmlcode, Skin s_) {
    s = s_;
    up1 = XML.getValue(xmlcode,"up1");
    up2 = XML.getValue(xmlcode,"up2");
    state = XML.getValue(xmlcode,"state");
    if(xmlcode.indexOf(" over1=\"")!=-1) over1 = XML.getValue(xmlcode,"over1");
    if(xmlcode.indexOf(" over2=\"")!=-1) over2 = XML.getValue(xmlcode,"over2");
    if(xmlcode.indexOf(" down1=\"")!=-1) down1 = XML.getValue(xmlcode,"down1");
    if(xmlcode.indexOf(" down2=\"")!=-1) down2 = XML.getValue(xmlcode,"down2");
    if(xmlcode.indexOf(" up1=\"")!=-1) up1 = XML.getValue(xmlcode,"up1");
    if(xmlcode.indexOf(" up2=\"")!=-1) up2 = XML.getValue(xmlcode,"up2");
    if(xmlcode.indexOf(" action1=\"")!=-1) action1 = XML.getValue(xmlcode,"action1");
    if(xmlcode.indexOf(" action2=\"")!=-1) action2 = XML.getValue(xmlcode,"action2");
    if(xmlcode.indexOf(" tooltiptext1=\"")!=-1) tooltiptext1 = XML.getValue(xmlcode,"tooltiptext1");
    if(xmlcode.indexOf(" tooltiptext2=\"")!=-1) tooltiptext2 = XML.getValue(xmlcode,"tooltiptext2");
    if(xmlcode.indexOf(" x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf(" y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf(" id=\"")!=-1) id = XML.getValue(xmlcode,"id");
    else id = "Unnamed checkbox #"+s.getNewId();
    if(xmlcode.indexOf(" lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf(" rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf(" xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf(" ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
  }
  public Checkbox(Skin s_) {
    s = s_;
    up1="none";
    up2="none";
    state="false";
    id = "Unnamed checkbox #"+s.getNewId();
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
    
    up1 = up1_tf.getText();
    over1 = over1_tf.getText();
    down1 = down1_tf.getText();
    action1 = action1_tf.getText();
    tooltiptext1 = tooltiptext1_tf.getText();
    up2 = up2_tf.getText();
    over2 = over2_tf.getText();
    down2 = down2_tf.getText();
    action2 = action2_tf.getText();
    tooltiptext2 = tooltiptext2_tf.getText();
    state = state_tf.getText();
    
    s.updateItems();   
    s.expandItem(id);
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Checkbox settings");
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
      
      JLabel up1_l = new JLabel("Normal image*:");
      up1_tf = new JTextField();
      JLabel over1_l = new JLabel("Mouse-over image:");
      over1_tf = new JTextField();
      JLabel down1_l = new JLabel("Mouse-click image:");
      down1_tf = new JTextField();
      JLabel action1_l = new JLabel("Action:");
      action1_tf = new JTextField();
      JLabel tooltiptext1_l = new JLabel("Tooltiptext:");
      tooltiptext1_tf = new JTextField();
      JLabel up2_l = new JLabel("Normal image*:");
      up2_tf = new JTextField();
      JLabel over2_l = new JLabel("Mouse-over image:");
      over2_tf = new JTextField();
      JLabel down2_l = new JLabel("Mouse-click image:");
      down2_tf = new JTextField();
      JLabel action2_l = new JLabel("Action:");
      action2_tf = new JTextField();
      JLabel tooltiptext2_l = new JLabel("Tooltiptext:");
      tooltiptext2_tf = new JTextField();
      
      JLabel state_l = new JLabel("Condition:");
      state_tf = new JTextField();
      state_tf.setToolTipText("Boolean expression specifying the state of the checkbox: if the expression resolves to 'false', the first state will be used, and if it resolves to 'true' the second state will be used. Example for a checkbox showing/hiding a window whose id is \"playlist_window\": state=\"playlist_window.isVisible\" (or state=\"not playlist_window.isVisible\", depending on the states you chose).");
      
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
      y_l.setBounds(240,45,75,24);
      y_tf.setBounds(325,45,150,24);      
      general.add(lefttop_l);
      general.add(lefttop_cb);
      lefttop_l.setBounds(5,75,75,24);
      lefttop_cb.setBounds(85,75,150,24);
      general.add(rightbottom_l);
      general.add(rightbottom_cb);
      rightbottom_l.setBounds(240,75,75,24);
      rightbottom_cb.setBounds(325,75,150,24);
      general.add(xkeepratio_l);
      general.add(xkeepratio_cb);
      xkeepratio_l.setBounds(5,105,75,24);
      xkeepratio_cb.setBounds(85,105,150,24);
      general.add(ykeepratio_l);
      general.add(ykeepratio_cb);
      ykeepratio_l.setBounds(240,105,75,24);
      ykeepratio_cb.setBounds(325,105,150,24);
      general.add(visible_l);
      general.add(visible_tf);
      visible_l.setBounds(5,135,75,24);
      visible_tf.setBounds(85,135,150,24);
      general.add(help_l);
      general.add(help_tf);
      help_l.setBounds(240,135,75,24);
      help_tf.setBounds(325,135,150,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "General Attributes"));
      general.setMinimumSize(new Dimension(495,165));
      general.setPreferredSize(new Dimension(495,165));
      general.setMaximumSize(new Dimension(495,165));
      frame.add(general);
      
      JPanel state = new JPanel(null);
      state.add(state_l);
      state.add(state_tf);
      state_l.setBounds(5,15,75,24);
      state_tf.setBounds(85,15,150,24);
      state.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Checkbox state"));
      state.setMinimumSize(new Dimension(495,45));
      state.setPreferredSize(new Dimension(495,45));
      state.setMaximumSize(new Dimension(495,45));
      frame.add(state);
      
      JPanel state1 = new JPanel(null);
      state1.add(up1_l);
      state1.add(up1_tf);
      up1_l.setBounds(5,15,75,24);
      up1_tf.setBounds(85,15,150,24);
      state1.add(over1_l);
      state1.add(over1_tf);
      over1_l.setBounds(5,45,75,24);
      over1_tf.setBounds(85,45,150,24);
      state1.add(down1_l);
      state1.add(down1_tf);
      down1_l.setBounds(5,75,75,24);
      down1_tf.setBounds(85,75,150,24);
      state1.add(action1_l);
      state1.add(action1_tf);
      action1_l.setBounds(5,105,75,24);
      action1_tf.setBounds(85,105,150,24);
      state1.add(tooltiptext1_l);
      state1.add(tooltiptext1_tf);
      tooltiptext1_l.setBounds(5,135,75,24);
      tooltiptext1_tf.setBounds(85,135,150,24);
      state1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "If state is false:"));
      state1.setMinimumSize(new Dimension(240,165));
      state1.setPreferredSize(new Dimension(240,165));
      state1.setMaximumSize(new Dimension(240,165));
      frame.add(state1);
      
      JPanel state2 = new JPanel(null);
      state2.add(up2_l);
      state2.add(up2_tf);
      up2_l.setBounds(5,15,75,24);
      up2_tf.setBounds(85,15,150,24);
      state2.add(over2_l);
      state2.add(over2_tf);
      over2_l.setBounds(5,45,75,24);
      over2_tf.setBounds(85,45,150,24);
      state2.add(down2_l);
      state2.add(down2_tf);
      down2_l.setBounds(5,75,75,24);
      down2_tf.setBounds(85,75,150,24);
      state2.add(action2_l);
      state2.add(action2_tf);
      action2_l.setBounds(5,105,75,24);
      action2_tf.setBounds(85,105,150,24);
      state2.add(tooltiptext2_l);
      state2.add(tooltiptext2_tf);
      tooltiptext2_l.setBounds(5,135,75,24);
      tooltiptext2_tf.setBounds(85,135,150,24);
      state2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "If state is true:"));
      state2.setMinimumSize(new Dimension(240,165));
      state2.setPreferredSize(new Dimension(240,165));
      state2.setMaximumSize(new Dimension(240,165));
      frame.add(state2);
      
      
      frame.add(ok_btn);
      frame.add(help_btn);      
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(505,450));
      frame.setPreferredSize(new Dimension(505,450));
      frame.setMaximumSize(new Dimension(505,450));
      
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
    
    state_tf.setText(state);
    up1_tf.setText(up1);
    over1_tf.setText(over1);
    down1_tf.setText(down1);
    action1_tf.setText(action1);
    tooltiptext1_tf.setText(tooltiptext1);
    up2_tf.setText(up2);
    over2_tf.setText(over2);
    down2_tf.setText(down2);
    action2_tf.setText(action2);
    tooltiptext2_tf.setText(tooltiptext2);
    
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
      if(state_tf.getText()=="") {
        JOptionPane.showMessageDialog(frame,"Please provide the state condition!","State not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(s.getResource(up1_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+up1_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(!over1_tf.getText().equals("none") && s.getResource(over1_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+over1_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(!down1_tf.getText().equals("none") && s.getResource(down1_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+down1_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(s.getResource(up2_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+up2_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(!over2_tf.getText().equals("none") && s.getResource(over2_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+over2_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(!down2_tf.getText().equals("none") && s.getResource(down2_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+down2_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
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
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Checkbox"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
  }
  public String returnCode() {
    String code = "<Checkbox";
    code+=" state=\""+state+"\" up1=\""+up1+"\" up2=\""+up2+"\"";
    if (down1!=ID_DEFAULT) code+=" down1=\""+down1+"\"";
    if (down2!=ID_DEFAULT) code+=" down2=\""+down2+"\"";
    if (over1!=ID_DEFAULT) code+=" over1=\""+over1+"\"";
    if (over2!=ID_DEFAULT) code+=" over2=\""+over2+"\"";
    if (action1!=ID_DEFAULT) code+=" action1=\""+action1+"\"";
    if (action2!=ID_DEFAULT) code+=" action2=\""+action2+"\"";
    if (tooltiptext1!=ID_DEFAULT) code+=" tooltiptext1=\""+tooltiptext1+"\"";
    if (tooltiptext2!=ID_DEFAULT) code+=" tooltiptext2=\""+tooltiptext2+"\"";
    if (id!=ID_DEFAULT) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if (lefttop!=LEFTTOP_DEFAULT) code+=" lefttop=\""+lefttop+"\"";
    if (rightbottom!=RIGHTBOTTOM_DEFAULT) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (help!=HELP_DEFAULT) code+=" help=\""+help+"\"";
    if (visible!=VISIBLE_DEFAULT) code+=" visible=\""+visible+"\"";
    code+="/>";
    return code;
  }
  public void draw(Graphics2D g) {
    draw(g,offsetx,offsety);
  }
  public void draw(Graphics2D g, int x_, int y_) {
    if(s.gvars.parseBoolean(visible)==false) return;
    java.awt.image.BufferedImage bi = s.getBitmapImage(up2);
    if(s.gvars.parseBoolean(state)) {      
      if(!hovered) g.drawImage(bi,x+x_,y+y_,null,null);
      else if(!clicked) g.drawImage(s.getBitmapImage(over2),x+x_,y+y_,null);
      else g.drawImage(s.getBitmapImage(down2),x+x_,y+y_,null);      
    }
    else {
      if(!hovered) g.drawImage(s.getBitmapImage(up1),x+x_,y+y_,null);
      else if(!clicked) g.drawImage(s.getBitmapImage(over1),x+x_,y+y_,null);
      else g.drawImage(s.getBitmapImage(down1),x+x_,y+y_,null);      
    }
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect(x+x_,y+y_,bi.getWidth()-1,bi.getHeight()-1);
    }
  }
  public boolean contains(int x_, int y_) {
    java.awt.image.BufferedImage bi = s.getBitmapImage(up1);
    return (x_>=x+offsetx && x_<=x+bi.getWidth()+offsetx && y_>=y+offsety && y_<=y+bi.getHeight()+offsety);
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Checkbox: "+id);    
    return node;
  }
  
}
