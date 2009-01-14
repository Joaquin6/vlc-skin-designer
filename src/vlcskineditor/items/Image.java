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

package vlcskineditor.items;

import vlcskineditor.*;
import vlcskineditor.history.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import vlcskineditor.resources.ImageResource;

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
  JButton visible_btn, action2_btn, ok_btn, cancel_btn, help_btn;
  
  ActionEditor action2_ae;

  ImageResource image_res;
  
  {
    type = Language.get("IMAGE");
  }

  /**
   * Parses an image from an XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public Image(Node n, Skin s_) {
    s = s_;
    
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));

    image = XML.getStringAttributeValue(n, "image", image);    
    action = XML.getStringAttributeValue(n, "action", action);
    action2 = XML.getStringAttributeValue(n, "action2", action2);
    resize = XML.getStringAttributeValue(n, "resize", resize);

    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);

    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);

    image_res = s.getImageResource(image);

    created = true;
  }
  
  /** Creates a new instance of Image
   * @param xmlcode The XML code
   * @param s_ The parent skin
   */
  public Image(String xmlcode, Skin s_) {
    s=s_;
    image = XML.getValue(xmlcode,"image");
    image_res = s.getImageResource(image);
    if(xmlcode.indexOf("resize=\"")!=-1) resize = XML.getValue(xmlcode,"resize");
    if(xmlcode.indexOf("action=\"")!=-1) action = XML.getValue(xmlcode,"action");
    if(xmlcode.indexOf("action2=\"")!=-1) action2 = XML.getValue(xmlcode,"action2");
    
    
    if(xmlcode.indexOf("x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf("y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf("id=\"")!=-1) id = XML.getValue(xmlcode,"id"); 
    else id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    if(xmlcode.indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf("ykeepratio=\"")!=-1) ykeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
    if(xmlcode.indexOf(" visible=\"")!=-1) visible = XML.getValue(xmlcode,"visible");
    created = true;
  }
  public Image(Skin s_) {
    s = s_;
    image = "";
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();
  }
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

      image = image_tf.getText();
      resize = resize_cb.getSelectedItem().toString();
      action = action_cb.getSelectedItem().toString();
      action2 = action2_tf.getText();   

      s.updateItems();      
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      created = true;
      
      ItemAddEvent iae = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(iae);
    }
    else {
      ImageEditEvent iee = new ImageEditEvent(this);
      
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
      
      iee.setNew();
      s.m.hist.addEvent(iee);
    }
    updateToGlobalVariables();
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_IMAGE_TITLE"));
      frame.setIconImage(Main.edit_icon.getImage());
      frame.setResizable(false);
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
      
      JLabel image_l = new JLabel(Language.get("WIN_IMAGE_IMAGE"));
      image_tf = new JTextField();
      image_tf.setToolTipText(Language.get("WIN_IMAGE_IMAGE_TIP"));
      JLabel resize_l = new JLabel(Language.get("WIN_IMAGE_RESIZE"));
      String[] resize_values = { "mosaic" , "scale" };
      resize_cb = new JComboBox(resize_values);
      resize_cb.setToolTipText(Language.get("WIN_IMAGE_RESIZE_TIP"));
      JLabel action_l = new JLabel(Language.get("WIN_IMAGE_ACTION"));
      String[] action_values = { "none", "move","resizeE","resizeS","resizeSE" };
      action_cb = new JComboBox(action_values);
      action_cb.setToolTipText(Language.get("WIN_IMAGE_ACTION_TIP"));
      JLabel action2_l = new JLabel(Language.get("WIN_IMAGE_ACTION2"));
      action2_tf = new JTextField();
      action2_tf.setToolTipText(Language.get("WIN_IMAGE_ACTION2_TIP"));
      action2_btn = new JButton("",Main.editor_icon);
      action2_btn.addActionListener(this);
      
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);

      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));

      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, lefttop_l, rightbottom_l, xkeepratio_l, ykeepratio_l, visible_l, help_l, image_l, resize_l, action_l, action2_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      general.add(x_l);
      general.add(x_tf);
      general.add(y_l);
      general.add(y_tf); 
      general.add(lefttop_l);
      general.add(lefttop_cb);
      general.add(rightbottom_l);
      general.add(rightbottom_cb);
      general.add(xkeepratio_l);
      general.add(xkeepratio_cb);
      general.add(ykeepratio_l);
      general.add(ykeepratio_cb);
      general.add(visible_l);
      general.add(visible_tf);
      general.add(visible_btn);
      general.add(help_l);
      general.add(help_tf);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));

      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);

      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.NORTH, x_l, 10, SpringLayout.SOUTH, id_tf);
      general_layout.putConstraint(SpringLayout.WEST, x_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, x_tf, 0, SpringLayout.VERTICAL_CENTER, x_l);
      general_layout.putConstraint(SpringLayout.WEST, x_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, x_tf, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, y_l, 10, SpringLayout.SOUTH, x_tf);
      general_layout.putConstraint(SpringLayout.WEST, y_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, y_tf, 0, SpringLayout.VERTICAL_CENTER, y_l);
      general_layout.putConstraint(SpringLayout.WEST, y_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, y_tf, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, lefttop_l, 10, SpringLayout.SOUTH, y_tf);
      general_layout.putConstraint(SpringLayout.WEST, lefttop_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, lefttop_cb, 0, SpringLayout.VERTICAL_CENTER, lefttop_l);
      general_layout.putConstraint(SpringLayout.WEST, lefttop_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, lefttop_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, rightbottom_l, 10, SpringLayout.SOUTH, lefttop_cb);
      general_layout.putConstraint(SpringLayout.WEST, rightbottom_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, rightbottom_cb, 0, SpringLayout.VERTICAL_CENTER, rightbottom_l);
      general_layout.putConstraint(SpringLayout.WEST, rightbottom_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, rightbottom_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, xkeepratio_l, 10, SpringLayout.SOUTH, rightbottom_cb);
      general_layout.putConstraint(SpringLayout.WEST, xkeepratio_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, xkeepratio_cb, 0, SpringLayout.VERTICAL_CENTER, xkeepratio_l);
      general_layout.putConstraint(SpringLayout.WEST, xkeepratio_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, xkeepratio_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, ykeepratio_l, 10, SpringLayout.SOUTH, xkeepratio_cb);
      general_layout.putConstraint(SpringLayout.WEST, ykeepratio_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, ykeepratio_cb, 0, SpringLayout.VERTICAL_CENTER, ykeepratio_l);
      general_layout.putConstraint(SpringLayout.WEST, ykeepratio_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, ykeepratio_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, visible_l, 10, SpringLayout.SOUTH, ykeepratio_cb);
      general_layout.putConstraint(SpringLayout.WEST, visible_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, visible_tf, 0, SpringLayout.VERTICAL_CENTER, visible_l);
      general_layout.putConstraint(SpringLayout.WEST, visible_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, visible_tf, -5, SpringLayout.WEST, visible_btn);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, visible_btn, 0, SpringLayout.VERTICAL_CENTER, visible_l);
      general_layout.putConstraint(SpringLayout.EAST, visible_btn, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, help_l, 10, SpringLayout.SOUTH, visible_btn);
      general_layout.putConstraint(SpringLayout.WEST, help_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, help_tf, 0, SpringLayout.VERTICAL_CENTER, help_l);
      general_layout.putConstraint(SpringLayout.WEST, help_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, help_tf, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, help_tf);

      frame.add(general);
      
      JPanel image_panel = new JPanel(null);
      image_panel.add(image_l);
      image_panel.add(image_tf);
      image_tf.setPreferredSize(new Dimension(tf_wd,image_tf.getPreferredSize().height));
      image_panel.add(resize_l);
      image_panel.add(resize_cb);
      image_panel.add(action_l);
      image_panel.add(action_cb);
      image_panel.add(action2_l);
      image_panel.add(action2_tf);
      image_panel.add(action2_btn);
      image_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_IMAGE_ATTR")));

      SpringLayout image_layout = new SpringLayout();
      
      image_layout.putConstraint(SpringLayout.NORTH, image_l, 5, SpringLayout.NORTH, image_panel);
      image_layout.putConstraint(SpringLayout.WEST, image_l, 5, SpringLayout.WEST, image_panel);

      image_layout.putConstraint(SpringLayout.VERTICAL_CENTER, image_tf, 0, SpringLayout.VERTICAL_CENTER, image_l);
      image_layout.putConstraint(SpringLayout.WEST, image_tf, tf_dx, SpringLayout.WEST, image_panel);

      image_layout.putConstraint(SpringLayout.NORTH, resize_l, 10, SpringLayout.SOUTH, image_tf);
      image_layout.putConstraint(SpringLayout.WEST, resize_l, 5, SpringLayout.WEST, image_panel);

      image_layout.putConstraint(SpringLayout.VERTICAL_CENTER, resize_cb, 0, SpringLayout.VERTICAL_CENTER, resize_l);
      image_layout.putConstraint(SpringLayout.WEST, resize_cb, tf_dx, SpringLayout.WEST, image_panel);
      image_layout.putConstraint(SpringLayout.EAST, resize_cb, 0, SpringLayout.EAST, image_tf);
      
      image_layout.putConstraint(SpringLayout.NORTH, action_l, 10, SpringLayout.SOUTH, resize_cb);
      image_layout.putConstraint(SpringLayout.WEST, action_l, 5, SpringLayout.WEST, image_panel);

      image_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action_cb, 0, SpringLayout.VERTICAL_CENTER, action_l);
      image_layout.putConstraint(SpringLayout.WEST, action_cb, tf_dx, SpringLayout.WEST, image_panel);
      image_layout.putConstraint(SpringLayout.EAST, action_cb, 0, SpringLayout.EAST, image_tf);
      
      image_layout.putConstraint(SpringLayout.NORTH, action2_l, 10, SpringLayout.SOUTH, action_cb);
      image_layout.putConstraint(SpringLayout.WEST, action2_l, 5, SpringLayout.WEST, image_panel);

      image_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action2_tf, 0, SpringLayout.VERTICAL_CENTER, action2_l);
      image_layout.putConstraint(SpringLayout.WEST, action2_tf, tf_dx, SpringLayout.WEST, image_panel);
      image_layout.putConstraint(SpringLayout.EAST, action2_tf, -5, SpringLayout.WEST, action2_btn);

      image_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action2_btn, 0, SpringLayout.VERTICAL_CENTER, action2_l);
      image_layout.putConstraint(SpringLayout.EAST, action2_btn, 0, SpringLayout.EAST, image_tf);

      image_layout.putConstraint(SpringLayout.EAST, image_panel, 5, SpringLayout.EAST, image_tf);
      image_layout.putConstraint(SpringLayout.SOUTH, image_panel, 10, SpringLayout.SOUTH, action2_btn);

      image_panel.setLayout(image_layout);      

      frame.add(image_panel);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);
      
      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, image_panel, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, image_panel, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, image_panel);
      layout.putConstraint(SpringLayout.WEST, attr_l, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, ok_btn, 10, SpringLayout.SOUTH, attr_l);
      layout.putConstraint(SpringLayout.WEST, ok_btn, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, cancel_btn, 0, SpringLayout.NORTH, ok_btn);
      layout.putConstraint(SpringLayout.WEST, cancel_btn, 5, SpringLayout.EAST, ok_btn);

      layout.putConstraint(SpringLayout.NORTH, help_btn, 0, SpringLayout.NORTH, cancel_btn);
      layout.putConstraint(SpringLayout.WEST, help_btn, 5, SpringLayout.EAST, cancel_btn);

      layout.putConstraint(SpringLayout.SOUTH, frame.getContentPane(), 10, SpringLayout.SOUTH, ok_btn);
      layout.putConstraint(SpringLayout.EAST, frame.getContentPane(), 5, SpringLayout.EAST, general);

      frame.setLayout(layout);

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
    
    image_tf.setText(image);
    resize_cb.setSelectedItem(resize);
    action_cb.setSelectedItem(action);
    action2_tf.setText(action2);
    
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
      image_res = s.getImageResource(image_tf.getText());
      if(image_res==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+image_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        image_res = s.getImageResource(image);
        return;
      }
      update();
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-image.html");
    }
    else if(e.getSource().equals(action2_btn)) {
      if(action2_ae==null) action2_ae = new ActionEditor(this);
      action2_ae.editAction(action2_tf.getText());
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
  public void actionWasEdited(ActionEditor ae) {
    if(ae==action2_ae) action2_tf.setText(action2_ae.getCode());
  }
  @Override
  public String returnCode(String indent) {
    String code = indent+"<Image";    
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    
    code+=" image=\""+image+"\"";
    if (!action.equals(ACTION_DEFAULT)) code+=" action=\""+action+"\"";
    if (!action2.equals(ACTION2_DEFAULT)) code+=" action2=\""+action2+"\"";

    if (!resize.equals(RESIZE_DEFAULT)) code+=" resize=\""+resize+"\"";
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
    draw(g,offsetx,offsety, z);
  }
  @Override
  public void draw(Graphics2D g,int x_, int y_, int z) {
    if(!created) return;    
    BufferedImage bi = image_res.image;
    if(vis) g.drawImage(bi,(x+x_)*z,(y+y_)*z,bi.getWidth()*z,bi.getHeight()*z,null);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect((x+x_)*z,(y+y_)*z,bi.getWidth()*z-1,bi.getHeight()*z-1);
    }
  }
  @Override
  public boolean contains(int x_, int y_) {
    BufferedImage bi = image_res.image;
    return (x_>=x+offsetx && x_<=x+bi.getWidth()+offsetx && y_>=y+offsety && y_<=y+bi.getHeight()+offsety);
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Image: "+id);         
    return node;
  }
  @Override
  public boolean uses(String id_) {
    return (image.equals(id_));
  }
}
