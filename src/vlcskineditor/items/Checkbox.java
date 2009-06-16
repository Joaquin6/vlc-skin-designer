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

package vlcskineditor.items;

import vlcskineditor.*;
import vlcskineditor.history.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import vlcskineditor.resources.ImageResource;
import vlcskineditor.resources.ResourceChangeListener;
import vlcskineditor.resources.ResourceChangedEvent;

/**
 * Checkbox item
 * @author Daniel Dreibrodt
 */
public class Checkbox extends Item implements ActionListener, ResourceChangeListener {
  
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
  JButton visible_btn, action1_btn, action2_btn, state_btn, ok_btn, cancel_btn, help_btn;

  ImageResource up1_res, over1_res, down1_res, up2_res, over2_res, down2_res;

  ActionEditor action1_ae, action2_ae;

  private boolean state_bool = true;
  
  {
    type = Language.get("CHECKBOX");
  }

  /**
   * Creates a new Checkbox from a XML node
   * @param n The XML node
   * @param s_ The Skin to which the Checkbox belongs
   */
  public Checkbox(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    state = XML.getStringAttributeValue(n, "state", state);
    up1 = XML.getStringAttributeValue(n, "up1", up1);
    over1 = XML.getStringAttributeValue(n, "over1", over1);
    down1 = XML.getStringAttributeValue(n, "down1", down1);
    action1 = XML.getStringAttributeValue(n, "action1", action1);
    tooltiptext1 = XML.getStringAttributeValue(n, "tooltiptext1", tooltiptext1);    
    up2 = XML.getStringAttributeValue(n, "up2", up2);
    over2 = XML.getStringAttributeValue(n, "over2", over2);
    down2 = XML.getStringAttributeValue(n, "down2", down2);    
    action2 = XML.getStringAttributeValue(n, "action2", action2);
    tooltiptext2 = XML.getStringAttributeValue(n, "tooltiptext2", tooltiptext2);
    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);
    
    created = true;
    
    up1_res = s.getImageResource(up1);
    if(up1_res!=null) up1_res.addResourceChangeListener(this);
    over1_res = s.getImageResource(over1);
    if(over1_res!=null) over1_res.addResourceChangeListener(this);
    down1_res = s.getImageResource(down1);
    if(down1_res!=null) down1_res.addResourceChangeListener(this);
    up2_res = s.getImageResource(up2);
    if(up2_res!=null) up2_res.addResourceChangeListener(this);
    over2_res = s.getImageResource(over2);
    if(over2_res!=null) over2_res.addResourceChangeListener(this);
    down2_res = s.getImageResource(down2);
    if(down2_res!=null) down2_res.addResourceChangeListener(this);

    updateToGlobalVariables();
  }

  /**
   * Creates a new empty Checkbox and opens a dialog to edit it
   * @param s_ The skin to which the Checkbox belongs
   */
  public Checkbox(Skin s_) {
    s = s_;
    up1="none";
    up2="none";
    state="false";
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();
  }

  /**
   * Creates a copy of a Checkbox
   * @param c The Checkbox to copy
   */
  public Checkbox(Checkbox c) {
    super(c);

    state = c.state;

    up1 = c.up1;
    down1 = c.down1;
    over1 = c.over1;
    action1 = c.action1;
    tooltiptext1 = c.tooltiptext1;
    up2 = c.up2;
    down2 = c.down2;
    over2 = c.over2;
    action2 = c.action2;
    tooltiptext2 = c.tooltiptext2;

    up1_res = s.getImageResource(up1);
    if(up1_res!=null) up1_res.addResourceChangeListener(this);
    over1_res = s.getImageResource(over1);
    if(over1_res!=null) over1_res.addResourceChangeListener(this);
    down1_res = s.getImageResource(down1);
    if(down1_res!=null) down1_res.addResourceChangeListener(this);
    up2_res = s.getImageResource(up2);
    if(up2_res!=null) up2_res.addResourceChangeListener(this);
    over2_res = s.getImageResource(over2);
    if(over2_res!=null) over2_res.addResourceChangeListener(this);
    down2_res = s.getImageResource(down2);
    if(down2_res!=null) down2_res.addResourceChangeListener(this);

    updateToGlobalVariables();
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
      
      ItemAddEvent cae = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(cae);
      
      s.updateItems();   
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      created = true;
    }
    else {
      CheckboxEditEvent cee = new CheckboxEditEvent(this);
      
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
      
      cee.setNew();
      s.m.hist.addEvent(cee);
      
      s.updateItems();   
      s.expandItem(id);
    }
    updateToGlobalVariables();
    if(up1_res!=null) up1_res.addResourceChangeListener(this);
    if(over1_res!=null) over1_res.addResourceChangeListener(this);
    if(down1_res!=null) down1_res.addResourceChangeListener(this);
    if(up2_res!=null) up2_res.addResourceChangeListener(this);
    if(over2_res!=null) over2_res.addResourceChangeListener(this);
    if(down2_res!=null) down2_res.addResourceChangeListener(this);
  }
  
  @Override
  public void showOptions() {
    if(up1_res!=null) up1_res.removeResourceChangeListener(this);
    if(over1_res!=null) over1_res.removeResourceChangeListener(this);
    if(down1_res!=null) down1_res.removeResourceChangeListener(this);
    if(up2_res!=null) up2_res.removeResourceChangeListener(this);
    if(over2_res!=null) over2_res.removeResourceChangeListener(this);
    if(down2_res!=null) down2_res.removeResourceChangeListener(this);
    
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_CHECKBOX_TITLE"));
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
      
      JLabel up1_l = new JLabel(Language.get("WIN_CHECKBOX_UP"));
      up1_tf = new JTextField();
      JLabel over1_l = new JLabel(Language.get("WIN_CHECKBOX_OVER"));
      over1_tf = new JTextField();
      JLabel down1_l = new JLabel(Language.get("WIN_CHECKBOX_DOWN"));
      down1_tf = new JTextField();
      JLabel action1_l = new JLabel(Language.get("WIN_CHECKBOX_ACTION"));
      action1_tf = new JTextField();
      action1_btn = new JButton("",Main.editor_icon);
      action1_btn.addActionListener(this);
      JLabel tooltiptext1_l = new JLabel(Language.get("WIN_ITEM_TOOLTIPTEXT"));
      tooltiptext1_tf = new JTextField();
      JLabel up2_l = new JLabel(Language.get("WIN_CHECKBOX_UP"));
      up2_tf = new JTextField();
      JLabel over2_l = new JLabel(Language.get("WIN_CHECKBOX_OVER"));
      over2_tf = new JTextField();
      JLabel down2_l = new JLabel(Language.get("WIN_CHECKBOX_DOWN"));
      down2_tf = new JTextField();
      JLabel action2_l = new JLabel(Language.get("WIN_CHECKBOX_ACTION"));
      action2_tf = new JTextField();
      action2_btn = new JButton("",Main.editor_icon);
      action2_btn.addActionListener(this);
      JLabel tooltiptext2_l = new JLabel(Language.get("WIN_ITEM_TOOLTIPTEXT"));
      tooltiptext2_tf = new JTextField();
      
      JLabel state_l = new JLabel(Language.get("WIN_CHECKBOX_CONDITION"));
      state_tf = new JTextField();
      state_tf.setToolTipText(Language.get("WIN_CHECKBOX_CONDITION_TIP"));
      state_btn = new JButton("",Main.help_icon);
      state_btn.addActionListener(this);
      
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);

      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));

      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, lefttop_l, rightbottom_l, xkeepratio_l, ykeepratio_l, visible_l, help_l, state_l};
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
      
      JPanel state_panel = new JPanel(null);
      state_panel.add(state_l);
      state_panel.add(state_tf);
      //state_tf.setPreferredSize(new Dimension(tf_wd,state_tf.getPreferredSize().height));
      state_panel.add(state_btn);
      state_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_CHECKBOX_STATE")));

      SpringLayout state_layout = new SpringLayout();
      state_panel.setLayout(state_layout);

      state_layout.putConstraint(SpringLayout.NORTH, state_l, 5, SpringLayout.NORTH, state_panel);
      state_layout.putConstraint(SpringLayout.WEST, state_l, 5, SpringLayout.WEST, state_panel);

      state_layout.putConstraint(SpringLayout.VERTICAL_CENTER, state_tf, 0, SpringLayout.VERTICAL_CENTER, state_l);
      state_layout.putConstraint(SpringLayout.WEST, state_tf, tf_dx, SpringLayout.WEST, state_panel);

      state_layout.putConstraint(SpringLayout.VERTICAL_CENTER, state_btn, 0, SpringLayout.VERTICAL_CENTER, state_l);
      state_layout.putConstraint(SpringLayout.WEST, state_btn, 5, SpringLayout.EAST, state_tf);

      state_layout.putConstraint(SpringLayout.EAST, state_panel, 5, SpringLayout.EAST, state_btn);
      state_layout.putConstraint(SpringLayout.SOUTH, state_panel, 10, SpringLayout.SOUTH, state_tf);

      frame.add(state_panel);

      Component[] labels_states = { up1_l, over1_l, down1_l, action1_l, tooltiptext1_l};
      tf_dx = Helper.maxWidth(labels_states)+10;
      
      JPanel state1_panel = new JPanel(null);
      state1_panel.add(up1_l);
      state1_panel.add(up1_tf);
      up1_tf.setPreferredSize(new Dimension(tf_wd, up1_tf.getPreferredSize().height));
      state1_panel.add(over1_l);
      state1_panel.add(over1_tf);
      state1_panel.add(down1_l);
      state1_panel.add(down1_tf);
      state1_panel.add(action1_l);
      state1_panel.add(action1_tf);
      state1_panel.add(action1_btn);
      state1_panel.add(tooltiptext1_l);
      state1_panel.add(tooltiptext1_tf);
      state1_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_CHECKBOX_STATE1")));

      SpringLayout state1_layout = new SpringLayout();

      state1_layout.putConstraint(SpringLayout.NORTH, up1_l, 5, SpringLayout.NORTH, state1_panel);
      state1_layout.putConstraint(SpringLayout.WEST, up1_l, 5, SpringLayout.WEST, state1_panel);

      state1_layout.putConstraint(SpringLayout.VERTICAL_CENTER, up1_tf, 0, SpringLayout.VERTICAL_CENTER, up1_l);
      state1_layout.putConstraint(SpringLayout.WEST, up1_tf, tf_dx, SpringLayout.WEST, state1_panel);

      state1_layout.putConstraint(SpringLayout.NORTH, over1_l, 10, SpringLayout.SOUTH, up1_tf);
      state1_layout.putConstraint(SpringLayout.WEST, over1_l, 5, SpringLayout.WEST, state1_panel);

      state1_layout.putConstraint(SpringLayout.VERTICAL_CENTER, over1_tf, 0, SpringLayout.VERTICAL_CENTER, over1_l);
      state1_layout.putConstraint(SpringLayout.WEST, over1_tf, tf_dx, SpringLayout.WEST, state1_panel);
      state1_layout.putConstraint(SpringLayout.EAST, over1_tf, 0, SpringLayout.EAST, up1_tf);

      state1_layout.putConstraint(SpringLayout.NORTH, down1_l, 10, SpringLayout.SOUTH, over1_tf);
      state1_layout.putConstraint(SpringLayout.WEST, down1_l, 5, SpringLayout.WEST, state1_panel);

      state1_layout.putConstraint(SpringLayout.VERTICAL_CENTER, down1_tf, 0, SpringLayout.VERTICAL_CENTER, down1_l);
      state1_layout.putConstraint(SpringLayout.WEST, down1_tf, tf_dx, SpringLayout.WEST, state1_panel);
      state1_layout.putConstraint(SpringLayout.EAST, down1_tf, 0, SpringLayout.EAST, up1_tf);

      state1_layout.putConstraint(SpringLayout.NORTH, action1_l, 10, SpringLayout.SOUTH, down1_tf);
      state1_layout.putConstraint(SpringLayout.WEST, action1_l, 5, SpringLayout.WEST, state1_panel);

      state1_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action1_tf, 0, SpringLayout.VERTICAL_CENTER, action1_l);
      state1_layout.putConstraint(SpringLayout.WEST, action1_tf, tf_dx, SpringLayout.WEST, state1_panel);
      state1_layout.putConstraint(SpringLayout.EAST, action1_tf, -5, SpringLayout.WEST, action1_btn);

      state1_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action1_btn, 0, SpringLayout.VERTICAL_CENTER, action1_l);
      state1_layout.putConstraint(SpringLayout.EAST, action1_btn, 0, SpringLayout.EAST, up1_tf);

      state1_layout.putConstraint(SpringLayout.NORTH, tooltiptext1_l, 10, SpringLayout.SOUTH, action1_btn);
      state1_layout.putConstraint(SpringLayout.WEST, tooltiptext1_l, 5, SpringLayout.WEST, state1_panel);

      state1_layout.putConstraint(SpringLayout.VERTICAL_CENTER, tooltiptext1_tf, 0, SpringLayout.VERTICAL_CENTER, tooltiptext1_l);
      state1_layout.putConstraint(SpringLayout.WEST, tooltiptext1_tf, tf_dx, SpringLayout.WEST, state1_panel);
      state1_layout.putConstraint(SpringLayout.EAST, tooltiptext1_tf, 0, SpringLayout.EAST, up1_tf);

      state1_layout.putConstraint(SpringLayout.EAST, state1_panel, 5, SpringLayout.EAST, up1_tf);
      state1_layout.putConstraint(SpringLayout.SOUTH, state1_panel, 10, SpringLayout.SOUTH, tooltiptext1_tf);

      state1_panel.setLayout(state1_layout);

      frame.add(state1_panel);
      
      JPanel state2_panel = new JPanel(null);
      state2_panel.add(up2_l);
      state2_panel.add(up2_tf);
      up2_tf.setPreferredSize(new Dimension(tf_wd, up2_tf.getPreferredSize().height));
      state2_panel.add(over2_l);
      state2_panel.add(over2_tf);
      state2_panel.add(down2_l);
      state2_panel.add(down2_tf);
      state2_panel.add(action2_l);
      state2_panel.add(action2_tf);
      state2_panel.add(action2_btn);
      state2_panel.add(tooltiptext2_l);
      state2_panel.add(tooltiptext2_tf);
      state2_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_CHECKBOX_STATE2")));

      SpringLayout state2_layout = new SpringLayout();

      state2_layout.putConstraint(SpringLayout.NORTH, up2_l, 5, SpringLayout.NORTH, state2_panel);
      state2_layout.putConstraint(SpringLayout.WEST, up2_l, 5, SpringLayout.WEST, state2_panel);

      state2_layout.putConstraint(SpringLayout.VERTICAL_CENTER, up2_tf, 0, SpringLayout.VERTICAL_CENTER, up2_l);
      state2_layout.putConstraint(SpringLayout.WEST, up2_tf, tf_dx, SpringLayout.WEST, state2_panel);

      state2_layout.putConstraint(SpringLayout.NORTH, over2_l, 10, SpringLayout.SOUTH, up2_tf);
      state2_layout.putConstraint(SpringLayout.WEST, over2_l, 5, SpringLayout.WEST, state2_panel);

      state2_layout.putConstraint(SpringLayout.VERTICAL_CENTER, over2_tf, 0, SpringLayout.VERTICAL_CENTER, over2_l);
      state2_layout.putConstraint(SpringLayout.WEST, over2_tf, tf_dx, SpringLayout.WEST, state2_panel);
      state2_layout.putConstraint(SpringLayout.EAST, over2_tf, 0, SpringLayout.EAST, up2_tf);

      state2_layout.putConstraint(SpringLayout.NORTH, down2_l, 10, SpringLayout.SOUTH, over2_tf);
      state2_layout.putConstraint(SpringLayout.WEST, down2_l, 5, SpringLayout.WEST, state2_panel);

      state2_layout.putConstraint(SpringLayout.VERTICAL_CENTER, down2_tf, 0, SpringLayout.VERTICAL_CENTER, down2_l);
      state2_layout.putConstraint(SpringLayout.WEST, down2_tf, tf_dx, SpringLayout.WEST, state2_panel);
      state2_layout.putConstraint(SpringLayout.EAST, down2_tf, 0, SpringLayout.EAST, up2_tf);

      state2_layout.putConstraint(SpringLayout.NORTH, action2_l, 10, SpringLayout.SOUTH, down2_tf);
      state2_layout.putConstraint(SpringLayout.WEST, action2_l, 5, SpringLayout.WEST, state2_panel);

      state2_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action2_tf, 0, SpringLayout.VERTICAL_CENTER, action2_l);
      state2_layout.putConstraint(SpringLayout.WEST, action2_tf, tf_dx, SpringLayout.WEST, state2_panel);
      state2_layout.putConstraint(SpringLayout.EAST, action2_tf, -5, SpringLayout.WEST, action2_btn);

      state2_layout.putConstraint(SpringLayout.VERTICAL_CENTER, action2_btn, 0, SpringLayout.VERTICAL_CENTER, action2_l);
      state2_layout.putConstraint(SpringLayout.EAST, action2_btn, 0, SpringLayout.EAST, up2_tf);

      state2_layout.putConstraint(SpringLayout.NORTH, tooltiptext2_l, 10, SpringLayout.SOUTH, action2_btn);
      state2_layout.putConstraint(SpringLayout.WEST, tooltiptext2_l, 5, SpringLayout.WEST, state2_panel);

      state2_layout.putConstraint(SpringLayout.VERTICAL_CENTER, tooltiptext2_tf, 0, SpringLayout.VERTICAL_CENTER, tooltiptext2_l);
      state2_layout.putConstraint(SpringLayout.WEST, tooltiptext2_tf, tf_dx, SpringLayout.WEST, state2_panel);
      state2_layout.putConstraint(SpringLayout.EAST, tooltiptext2_tf, 0, SpringLayout.EAST, up2_tf);

      state2_layout.putConstraint(SpringLayout.EAST, state2_panel, 5, SpringLayout.EAST, up2_tf);
      state2_layout.putConstraint(SpringLayout.SOUTH, state2_panel, 10, SpringLayout.SOUTH, tooltiptext2_tf);

      state2_panel.setLayout(state2_layout);

      frame.add(state2_panel);      
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);

      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.EAST, general, 0, SpringLayout.EAST, state2_panel);

      layout.putConstraint(SpringLayout.NORTH, state_panel, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, state_panel, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.EAST, state_panel, 0, SpringLayout.EAST, state2_panel);

      layout.putConstraint(SpringLayout.NORTH, state1_panel, 10, SpringLayout.SOUTH, state_panel);
      layout.putConstraint(SpringLayout.WEST, state1_panel, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, state2_panel, 10, SpringLayout.SOUTH, state_panel);
      layout.putConstraint(SpringLayout.WEST, state2_panel, 5, SpringLayout.EAST, state1_panel);
      
      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, state2_panel);
      layout.putConstraint(SpringLayout.WEST, attr_l, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, ok_btn, 10, SpringLayout.SOUTH, attr_l);
      layout.putConstraint(SpringLayout.WEST, ok_btn, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, cancel_btn, 0, SpringLayout.NORTH, ok_btn);
      layout.putConstraint(SpringLayout.WEST, cancel_btn, 5, SpringLayout.EAST, ok_btn);

      layout.putConstraint(SpringLayout.NORTH, help_btn, 0, SpringLayout.NORTH, cancel_btn);
      layout.putConstraint(SpringLayout.WEST, help_btn, 5, SpringLayout.EAST, cancel_btn);

      layout.putConstraint(SpringLayout.SOUTH, frame.getContentPane(), 10, SpringLayout.SOUTH, ok_btn);
      layout.putConstraint(SpringLayout.EAST, frame.getContentPane(), 5, SpringLayout.EAST, state2_panel);

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
      if(state_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_NOSTATE"),Language.get("ERROR_NOSTATE_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      up1_res = s.getImageResource(up1_tf.getText());
      if(up1_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", up1_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        up1_res = s.getImageResource(up1);
        return;
      }
      over1_res = s.getImageResource(over1_tf.getText());
      if(!over1_tf.getText().equals("none") && over1_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", over1_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        over1_res = s.getImageResource(over1);
        return;
      }
      down1_res = s.getImageResource(down1_tf.getText());
      if(!down1_tf.getText().equals("none") && down1_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", down1_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        down1_res = s.getImageResource(down1);
        return;
      }
      up2_res = s.getImageResource(up2_tf.getText());
      if(up2_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", up2_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        up2_res = s.getImageResource(up2);
        return;
      }
      over2_res = s.getImageResource(over2_tf.getText());
      if(!over2_tf.getText().equals("none") && s.getResource(over2_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", over2_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        over2_res = s.getImageResource(over2);
        return;
      }
      down2_res = s.getImageResource(down2_tf.getText());
      if(!down2_tf.getText().equals("none") && down2_res==null) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", down2_tf.getText()),Language.get("ERROR_BITMAP_NEXIST_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        down2_res = s.getImageResource(down2);
        return;
      }
      update();
      frame.setVisible(false); 
      frame.dispose();
      frame = null;
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-checkbox.html");
    }
    else if(e.getSource().equals(state_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/boolexpr.html");
    }
    else if(e.getSource().equals(action1_btn)) {
      if(action1_ae==null) action1_ae = new ActionEditor(this);
      action1_ae.editAction(action1_tf.getText());
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
      up1_res = s.getImageResource(up1);
      if(up1_res!=null) up1_res.addResourceChangeListener(this);
      over1_res = s.getImageResource(over1);
      if(over1_res!=null) over1_res.addResourceChangeListener(this);
      down1_res = s.getImageResource(down1);
      if(down1_res!=null) down1_res.addResourceChangeListener(this);
      up2_res = s.getImageResource(up2);
      if(up2_res!=null) up2_res.addResourceChangeListener(this);
      over2_res = s.getImageResource(over2);
      if(over2_res!=null) over2_res.addResourceChangeListener(this);
      down2_res = s.getImageResource(down2);
      if(down2_res!=null) down2_res.addResourceChangeListener(this);
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
  }
  @Override
  public void actionWasEdited(ActionEditor ae) {
    if(ae==action1_ae) action1_tf.setText(action1_ae.getCode());
    else if(ae==action2_ae) action2_tf.setText(action2_ae.getCode());
  }
  @Override
  public String returnCode(String indent) {
    String code = indent+"<Checkbox";
    code+=" state=\""+state+"\" up1=\""+up1+"\" up2=\""+up2+"\"";
    if (!down1.equals(DOWN1_DEFAULT)) code+=" down1=\""+down1+"\"";
    if (!down2.equals(DOWN2_DEFAULT)) code+=" down2=\""+down2+"\"";
    if (!over1.equals(OVER1_DEFAULT)) code+=" over1=\""+over1+"\"";
    if (!over2.equals(OVER2_DEFAULT)) code+=" over2=\""+over2+"\"";
    if (!action1.equals(ACTION1_DEFAULT)) code+=" action1=\""+action1+"\"";
    if (!action2.equals(ACTION2_DEFAULT)) code+=" action2=\""+action2+"\"";
    if (!tooltiptext1.equals(TOOLTIPTEXT1_DEFAULT)) code+=" tooltiptext1=\""+tooltiptext1+"\"";
    if (!tooltiptext2.equals(TOOLTIPTEXT2_DEFAULT)) code+=" tooltiptext2=\""+tooltiptext2+"\"";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
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
    if(!created) return;    
    java.awt.image.BufferedImage bi = null; 
    if(state_bool) {
      if(!hovered || ( (over2.equals("none") && !clicked)||(clicked && down2.equals("none")) )) bi = up2_res.image;
      else if(!clicked || down2.equals("none")) bi = over2_res.image;
      else bi = down2_res.image;
    }
    else {
      if(!hovered || ( (over1.equals("none") && !clicked)||(clicked && down1.equals("none")) )) bi = up1_res.image;
      else if(!clicked || down1.equals("none")) bi = over1_res.image;
      else bi = down1_res.image;
    }
    if(vis)g.drawImage(bi,(x+x_)*z,(y+y_)*z,bi.getWidth()*z,bi.getHeight()*z,null);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect((x+x_)*z,(y+y_)*z,bi.getWidth()*z-1,bi.getHeight()*z-1);
    }
  }
  @Override
  public boolean contains(int x_, int y_) {
    java.awt.image.BufferedImage bi = up1_res.image;
    return (x_>=x+offsetx && x_<=x+bi.getWidth()+offsetx && y_>=y+offsety && y_<=y+bi.getHeight()+offsety);
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Checkbox: "+id);    
    return node;
  }
  @Override
  public boolean uses(String id_) {
    return (up1.equals(id_)||up2.equals(id_)||over1.equals(id_)||over2.equals(id_)||down1.equals(id_)||down2.equals(id_));
  }
  @Override
  public void updateToGlobalVariables() {
    vis = s.gvars.parseBoolean(visible);
    state_bool = s.gvars.parseBoolean(state);
  }

  public void onResourceChanged(ResourceChangedEvent e) {
    if(up1.equals(e.getOldID())) up1 = e.getResource().id;
    if(over1.equals(e.getOldID())) over1 = e.getResource().id;
    if(down1.equals(e.getOldID())) down1 = e.getResource().id;
    if(up2.equals(e.getOldID())) up2 = e.getResource().id;
    if(over2.equals(e.getOldID())) over2 = e.getResource().id;
    if(down2.equals(e.getOldID())) down2 = e.getResource().id;
  }

}
