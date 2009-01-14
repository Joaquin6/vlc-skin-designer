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

package vlcskineditor.items;

import vlcskineditor.*;
import vlcskineditor.history.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import vlcskineditor.resources.ImageResource;

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
  JButton visible_btn, action_btn, ok_btn, cancel_btn, help_btn;
  
  ActionEditor action_ae;

  ImageResource up_res, over_res, down_res;
  
  {
    type = Language.get("BUTTON");
  }
  
  /**
   * Creates a new button item from a XML node
   * @param n The XML node
   * @param s_ The parent skin manager
   */
  public Button(Node n, Skin s_) {
    s = s_;
    
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    up = XML.getStringAttributeValue(n, "up", up);
    down = XML.getStringAttributeValue(n, "down", down);
    over = XML.getStringAttributeValue(n, "over", over);    
    action = XML.getStringAttributeValue(n, "action", action);
    tooltiptext = XML.getStringAttributeValue(n, "tooltiptext", tooltiptext);

    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);

    up_res = s.getImageResource(up);
    over_res = s.getImageResource(over);
    down_res = s.getImageResource(down);
    
    created = true;   
  }
  
  /** Creates a new instance of Button
   * @param xmlcode The XML code
   * @param s_ The parent skin
   */
  public Button(String xmlcode, Skin s_) {
    s = s_;
    up = XML.getValue(xmlcode,"up");
    if(xmlcode.indexOf("down=\"")!=-1) down = XML.getValue(xmlcode,"down");
    if(xmlcode.indexOf("over=\"")!=-1) over = XML.getValue(xmlcode,"over");
    if(xmlcode.indexOf("action=\"")!=-1) action = XML.getValue(xmlcode,"action");
    if(xmlcode.indexOf("x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf("y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf("id=\"")!=-1) id = XML.getValue(xmlcode,"id"); 
    else id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    if(xmlcode.indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf("ykeepratio=\"")!=-1) ykeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
    if(xmlcode.indexOf("tooltiptext=\"")!=-1) tooltiptext = XML.getValue(xmlcode,"tooltiptext");
    if(xmlcode.indexOf(" visible=\"")!=-1) visible = XML.getValue(xmlcode,"visible");
    created = true;

    up_res = s.getImageResource(up);
    over_res = s.getImageResource(over);
    down_res = s.getImageResource(down);
  }
  public Button(Skin s_) {
    s=s_;
    up = "";
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

      up = up_tf.getText();
      over = over_tf.getText();
      down = down_tf.getText();
      action = action_tf.getText();
      tooltiptext = tooltiptext_tf.getText();
      
      ItemAddEvent bae = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(bae);

      s.updateItems();    
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      created = true;      
    }
    else {
      ButtonEditEvent bee = new ButtonEditEvent(this);
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
      
      bee.setNew();
      s.m.hist.addEvent(bee);
      
      s.updateItems();    
      s.expandItem(id);
    }
    updateToGlobalVariables();
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_BUTTON_TITLE"));
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
      
      JLabel up_l = new JLabel(Language.get("WIN_BUTTON_UP"));
      up_tf = new JTextField();
      JLabel over_l = new JLabel(Language.get("WIN_BUTTON_OVER"));
      over_tf = new JTextField();
      JLabel down_l = new JLabel(Language.get("WIN_BUTTON_DOWN"));
      down_tf = new JTextField();
      JLabel action_l = new JLabel(Language.get("WIN_BUTTON_ACTION"));
      action_tf = new JTextField();
      action_btn = new JButton("",Main.editor_icon);
      action_btn.addActionListener(this);
      JLabel tooltiptext_l = new JLabel(Language.get("WIN_ITEM_TOOLTIPTEXT"));
      tooltiptext_tf = new JTextField();
      
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);
      
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      
      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, lefttop_l, rightbottom_l, xkeepratio_l, ykeepratio_l, visible_l, help_l, up_l, over_l, down_l, action_l, tooltiptext_l};
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
      
      general.setLayout(general_layout);
      
      frame.add(general);
      
      JPanel button_p = new JPanel(null);
      button_p.add(up_l);
      button_p.add(up_tf);
      up_tf.setPreferredSize(new Dimension(tf_wd,up_tf.getPreferredSize().height));
      button_p.add(over_l);
      button_p.add(over_tf);
      button_p.add(down_l);
      button_p.add(down_tf);
      button_p.add(action_l);
      button_p.add(action_tf);
      button_p.add(action_btn);
      button_p.add(tooltiptext_l);
      button_p.add(tooltiptext_tf);
      button_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_BUTTON_ATTR")));
     
      SpringLayout button_layout = new SpringLayout();
      
      button_layout.putConstraint(SpringLayout.NORTH, up_l, 5, SpringLayout.NORTH, button_p);
      button_layout.putConstraint(SpringLayout.WEST, up_l, 5, SpringLayout.WEST, button_p);
      
      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, up_tf, 0, SpringLayout.VERTICAL_CENTER, up_l);
      button_layout.putConstraint(SpringLayout.WEST, up_tf, tf_dx, SpringLayout.WEST, button_p);     
      
      button_layout.putConstraint(SpringLayout.NORTH, over_l, 10, SpringLayout.SOUTH, up_tf);
      button_layout.putConstraint(SpringLayout.WEST, over_l, 5, SpringLayout.WEST, button_p);
      
      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, over_tf, 0, SpringLayout.VERTICAL_CENTER, over_l);
      button_layout.putConstraint(SpringLayout.WEST, over_tf, tf_dx, SpringLayout.WEST, button_p);
      button_layout.putConstraint(SpringLayout.EAST, over_tf, 0, SpringLayout.EAST, up_tf);
     
      button_layout.putConstraint(SpringLayout.NORTH, down_l, 10, SpringLayout.SOUTH, over_tf);
      button_layout.putConstraint(SpringLayout.WEST, down_l, 5, SpringLayout.WEST, button_p);
      
      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, down_tf, 0, SpringLayout.VERTICAL_CENTER, down_l);
      button_layout.putConstraint(SpringLayout.WEST, down_tf, tf_dx, SpringLayout.WEST, button_p);
      button_layout.putConstraint(SpringLayout.EAST, down_tf, 0, SpringLayout.EAST, up_tf);
      
      button_layout.putConstraint(SpringLayout.NORTH, action_l, 10, SpringLayout.SOUTH, down_tf);
      button_layout.putConstraint(SpringLayout.WEST, action_l, 5, SpringLayout.WEST, button_p);
      
      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action_tf, 0, SpringLayout.VERTICAL_CENTER, action_l);
      button_layout.putConstraint(SpringLayout.WEST, action_tf, tf_dx, SpringLayout.WEST, button_p);
      button_layout.putConstraint(SpringLayout.EAST, action_tf, -5, SpringLayout.WEST, action_btn);
      
      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action_btn, 0, SpringLayout.VERTICAL_CENTER, action_l);
      button_layout.putConstraint(SpringLayout.EAST, action_btn, 0, SpringLayout.EAST, up_tf);
      
      button_layout.putConstraint(SpringLayout.NORTH, tooltiptext_l, 10, SpringLayout.SOUTH, action_btn);
      button_layout.putConstraint(SpringLayout.WEST, tooltiptext_l, 5, SpringLayout.WEST, button_p);
      
      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, tooltiptext_tf, 0, SpringLayout.VERTICAL_CENTER, tooltiptext_l);
      button_layout.putConstraint(SpringLayout.WEST, tooltiptext_tf, tf_dx, SpringLayout.WEST, button_p);
      button_layout.putConstraint(SpringLayout.EAST, tooltiptext_tf, 0, SpringLayout.EAST, up_tf);
      
      button_layout.putConstraint(SpringLayout.EAST, button_p, 5, SpringLayout.EAST, up_tf);
      button_layout.putConstraint(SpringLayout.SOUTH, button_p, 10, SpringLayout.SOUTH, tooltiptext_tf);
      
      button_p.setLayout(button_layout);
      
      frame.add(button_p);
      
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);
      
      SpringLayout layout = new SpringLayout();
      
      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, button_p, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, button_p, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, button_p);
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
    
    up_tf.setText(up);
    over_tf.setText(over);
    down_tf.setText(down);
    action_tf.setText(action);
    tooltiptext_tf.setText(tooltiptext);
    
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
      up_res = s.getImageResource(up_tf.getText());
      if(up_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", up_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        up_res = s.getImageResource(up);
        return;
      }
      over_res = s.getImageResource(over_tf.getText());
      if(!over_tf.getText().equals("none") && over_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", over_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        over_res = s.getImageResource(over);
        return;
      }
      down_res = s.getImageResource(down_tf.getText());
      if(!down_tf.getText().equals("none") && down_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", down_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        down_res = s.getImageResource(down);
        return;
      }
      update();
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-button.html");
    }
    else if(e.getSource().equals(visible_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/boolexpr.html");
    }
    else if(e.getSource().equals(action_btn)) {
      if (action_ae==null) action_ae = new ActionEditor(this);
      action_ae.editAction(action_tf.getText());      
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
    if(ae==action_ae) action_tf.setText(ae.getCode());
  }
  @Override
  public String returnCode(String indent) {
    String code = indent+"<Button";
    code+=" up=\""+up+"\"";
    if (!down.equals(DOWN_DEFAULT)) code+=" down=\""+down+"\"";
    if (!over.equals(OVER_DEFAULT)) code+=" over=\""+over+"\"";
    if (!action.equals(ACTION_DEFAULT)) code+=" action=\""+action+"\"";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if (!lefttop.equals(LEFTTOP_DEFAULT)) code+=" lefttop=\""+lefttop+"\"";
    if (!rightbottom.equals(RIGHTBOTTOM_DEFAULT)) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (!help.equals(HELP_DEFAULT)) code+=" help=\""+help+"\"";
    if (!visible.equals(VISIBLE_DEFAULT)) code+=" visible=\""+visible+"\"";
    if (!tooltiptext.equals(TOOLTIPTEXT_DEFAULT)) code+=" tooltiptext=\""+tooltiptext+"\"";
    code+="/>";
    return code;
  }
  @Override
  public void draw(Graphics2D g, int z) {
    draw(g,offsetx,offsety,z);
  }
  @Override
  public void draw(Graphics2D g, int x_, int y_, int z) {
    if(!created) return;    
    java.awt.image.BufferedImage bi;
    if(!hovered || ( (over.equals("none") && !clicked)||(clicked && down.equals("none")) ) ) {
      bi = up_res.image;
    } 
    else if(!clicked || down.equals("none")) {
      bi = over_res.image;
    }
    else {
      bi = down_res.image;
    }
    if(vis) g.drawImage(bi,(x+x_)*z,(y+y_)*z,bi.getWidth()*z,bi.getHeight()*z,null);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect((x+x_)*z,(y+y_)*z,bi.getWidth()*z-1,bi.getHeight()*z-1);
    }
  }
  @Override
  public boolean contains(int x_, int y_) {
    java.awt.image.BufferedImage bi = up_res.image;
    return (x_>=x+offsetx && x_<=x+bi.getWidth()+offsetx && y_>=y+offsety && y_<=y+bi.getHeight()+offsety);
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Button: "+id);     
    return node;
  }  
  @Override
  public boolean uses(String id_) {
    return (up.equals(id_)||over.equals(id_)||down.equals(id_));
  }
}
