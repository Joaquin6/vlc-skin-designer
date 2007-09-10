/*****************************************************************************
 * Image.java
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
 * Image item
 * @author Daniel Dreibrodt
 */
public class Image extends Item implements ActionListener{
  
  public final String RESIZE_DEFAULT = "mosaic";
  public final String ACTION_DEFAULT = "none";
  public final String ACTION2_DEFAULT = "none";
  public String image;
  public String resize = RESIZE_DEFAULT;
  public String action = ACTION_DEFAULT;
  public String action2 = ACTION2_DEFAULT;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, image_tf, action2_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb, resize_cb, action_cb;
  JButton ok_btn, help_btn;
  
  /** Creates a new instance of Image */
  public Image(String xmlcode, Skin s_) {
    s=s_;
    image = XML.getValue(xmlcode,"image");
    if(xmlcode.indexOf("resize=\"")!=-1) resize = XML.getValue(xmlcode,"resize");
    if(xmlcode.indexOf("action=\"")!=-1) action = XML.getValue(xmlcode,"action");
    if(xmlcode.indexOf("action2=\"")!=-1) action2 = XML.getValue(xmlcode,"action2");
    
    
    if(xmlcode.indexOf("x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf("y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf("id=\"")!=-1) id = XML.getValue(xmlcode,"id"); 
    else id = "Unnamed image #"+s.getNewId();
    if(xmlcode.indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf("ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
  }
  public Image(Skin s_) {
    s = s_;
    image = "";
    id = "Unnamed image #"+s.getNewId();
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
    
    image = image_tf.getText();
    resize = resize_cb.getSelectedItem().toString();
    action = action_cb.getSelectedItem().toString();
    action2 = action2_tf.getText();   
    
    s.updateItems();      
    s.expandItem(id);
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Image settings");
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
      
      JLabel image_l = new JLabel("Image*:");
      image_tf = new JTextField();
      image_tf.setToolTipText("ID of a Bitmap.");
      JLabel resize_l = new JLabel("Resizing:");
      String[] resize_values = { "mosaic" , "scale" };
      resize_cb = new JComboBox(resize_values);
      resize_cb.setToolTipText("Specify the behaviour of the image when it is resized. Possible values are 'mosaic' (the image is repeated as many times as necessary to reach the wanted dimensions) and 'scale' (the image is actually rescaled). Beware that the 'scale' behaviour is much slower than the 'mosaic' one, so make sure to use it only when it's really needed.");
      JLabel action_l = new JLabel("Action:");
      String[] action_values = { "none", "move","resizeE","resizeS","resizeSE" };
      action_cb = new JComboBox(action_values);
      action_cb.setToolTipText("Action triggered by a click on the control. Possible values are 'move', to move the window, 'resizeE', to resize horizontally, 'resizeS' to resize vertically, and 'resizeSE' to resize both horizontally and vertically.");
      JLabel action2_l = new JLabel("Double-click Action:");
      action2_tf = new JTextField();
      action2_tf.setToolTipText("Action triggered by a double-click on the control.");
      
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
      
      JPanel image = new JPanel(null);
      image.add(image_l);
      image.add(image_tf);
      image_l.setBounds(5,15,75,24);
      image_tf.setBounds(85,15,150,24);
      image.add(resize_l);
      image.add(resize_cb);
      resize_l.setBounds(5,45,75,24);
      resize_cb.setBounds(85,45,150,24);
      image.add(action_l);
      image.add(action_cb);
      action_l.setBounds(5,75,75,24);
      action_cb.setBounds(85,75,150,24);
      image.add(action2_l);
      image.add(action2_tf);
      action2_l.setBounds(5,105,75,24);
      action2_tf.setBounds(85,105,150,24);
      image.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Image Attributes"));
      image.setMinimumSize(new Dimension(240,135));
      image.setPreferredSize(new Dimension(240,135));
      image.setMaximumSize(new Dimension(240,135));
      frame.add(image);
      
      
      frame.add(ok_btn);
      frame.add(help_btn);      
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,490));
      frame.setPreferredSize(new Dimension(250,490));
      frame.setMaximumSize(new Dimension(250,490));
      
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
    
    image_tf.setText(image);
    resize_cb.setSelectedItem(resize);
    action_cb.setSelectedItem(action);
    action2_tf.setText(action2);
    
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
      if(s.getResource(image_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+image_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
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
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Image"));
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
    String code = "<Image";
    if (id!=ID_DEFAULT) code+=" id=\""+id+"\"";
    code+=" image=\""+image+"\"";
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
    draw(g,offsetx,offsety);
  }
   public void draw(Graphics2D g,int x_, int y_) {
    java.awt.image.BufferedImage bi = s.getBitmapImage(image);
    g.drawImage(bi,x+x_,y+y_,Color.BLACK,null);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect(x+x_,y+y_,bi.getWidth()-1,bi.getHeight()-1);
    }
  }
   public boolean contains(int x_, int y_) {
    java.awt.image.BufferedImage bi = s.getBitmapImage(image);
    return (x_>=x+offsetx && x_<=x+bi.getWidth()+offsetx && y_>=y+offsety && y_<=y+bi.getHeight()+offsety);
   }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Image: "+id);         
    return node;
  }
}
