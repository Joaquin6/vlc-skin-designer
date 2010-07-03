/*****************************************************************************
 * Playtree.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import vlcskineditor.*;
import vlcskineditor.history.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import vlcskineditor.resources.Font;
import vlcskineditor.resources.ImageResource;
import vlcskineditor.resources.ResourceChangeListener;
import vlcskineditor.resources.ResourceChangedEvent;

/**
 * Playtree item
 * @author Daniel Dreibrodt
 */
public class Playtree extends Item implements ActionListener, ResourceChangeListener {

  public final int WIDTH_DEFAULT = 0;
  public final int HEIGHT_DEFAULT = 0;
  public final String VAR_DEFAULT = "playlist";
  public final String BGIMAGE_DEFAULT = "none";
  public final String FGCOLOR_DEFAULT = "#000000";
  public final String PLAYCOLOR_DEFAULT = "#FF0000";
  public final String SELCOLOR_DEFAULT = "#0000FF";
  public final String BGCOLOR1_DEFAULT = "#FFFFFF";
  public final String BGCOLOR2_DEFAULT = "#FFFFFF";
  public final boolean FLAT_DEFAULT = false;
  public final String ITEMIMAGE_DEFAULT = "none";
  public final String OPENIMAGE_DEFAULT = "none";
  public final String CLOSEDIMAGE_DEFAULT = "none";
  public int width = WIDTH_DEFAULT;
  public int height = HEIGHT_DEFAULT;
  public String font;
  Font font_res;
  public String var = VAR_DEFAULT;
  public String bgimage = BGIMAGE_DEFAULT;
  public String fgcolor = FGCOLOR_DEFAULT;
  public String playcolor = PLAYCOLOR_DEFAULT;
  public String selcolor = SELCOLOR_DEFAULT;
  public String bgcolor1 = BGCOLOR1_DEFAULT;
  public String bgcolor2 = BGCOLOR2_DEFAULT;
  public boolean flat = FLAT_DEFAULT;
  public String itemimage = ITEMIMAGE_DEFAULT;
  public String openimage = OPENIMAGE_DEFAULT;
  public String closedimage = CLOSEDIMAGE_DEFAULT;
  Slider slider = null;
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, width_tf, height_tf;
  JTextField font_tf, bgimage_tf, itemimage_tf, openimage_tf, closedimage_tf;
  JTextField fgcolor_tf, playcolor_tf, selcolor_tf, bgcolor1_tf, bgcolor2_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb, flat_cb;
  JButton visible_btn, bgcolor1_btn, bgcolor2_btn, fgcolor_btn, playcolor_btn, selcolor_btn, slider_btn, ok_btn, help_btn;
  JButton cancel_btn;
  ImageResource bgimage_res, itemimage_res, openimage_res, closedimage_res;

  BufferedImage cache = null;


  {
    type = Language.get("PLAYTREE");
  }

  /**
   * Parses a playtree from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public Playtree(Node n, Skin s_) {
    s = s_;

    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t", type).replaceAll("%i", String.valueOf(s.getNewId())));

    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);

    width = XML.getIntAttributeValue(n, "width", width);
    height = XML.getIntAttributeValue(n, "height", height);

    font = XML.getStringAttributeValue(n, "font", font);
    bgimage = XML.getStringAttributeValue(n, "bgimage", bgimage);
    itemimage = XML.getStringAttributeValue(n, "itemimage", itemimage);
    openimage = XML.getStringAttributeValue(n, "openimage", openimage);
    closedimage = XML.getStringAttributeValue(n, "closedimage", closedimage);
    fgcolor = XML.getStringAttributeValue(n, "fgcolor", fgcolor);
    playcolor = XML.getStringAttributeValue(n, "playcolor", playcolor);
    bgcolor1 = XML.getStringAttributeValue(n, "bgcolor1", bgcolor1);
    bgcolor2 = XML.getStringAttributeValue(n, "bgcolor2", bgcolor2);
    selcolor = XML.getStringAttributeValue(n, "selcolor", selcolor);
    flat = XML.getBoolAttributeValue(n, "flat", flat);

    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);

    NodeList nodes = n.getChildNodes();
    for(int i = 0; i < nodes.getLength(); i++) {
      if(nodes.item(i).getNodeName().equals("Slider")) {
        slider = new Slider(nodes.item(i), s, true);
      }
    }

    bgimage_res = s.getImageResource(bgimage);
    openimage_res = s.getImageResource(openimage);
    closedimage_res = s.getImageResource(closedimage);
    itemimage_res = s.getImageResource(itemimage);
    try {
      font_res = (Font)s.getResource(font);
    } catch(Exception ex) {
      ex.printStackTrace();
    }

    if(bgimage_res!=null) bgimage_res.addResourceChangeListener(this);
    if(openimage_res!=null) openimage_res.addResourceChangeListener(this);
    if(closedimage_res!=null) closedimage_res.addResourceChangeListener(this);
    if(itemimage_res!=null) itemimage_res.addResourceChangeListener(this);
    if(font_res!=null) font_res.addResourceChangeListener(this);
    created = true;
  }

  /**
   * Creates a new empty playtree and shows a dialog to edit it
   * @param s_ The skin to which the playtree belongs
   */
  public Playtree(Skin s_) {
    s = s_;
    font = "defaultfont";
    id = Language.get("UNNAMED").replaceAll("%t", type).replaceAll("%i", String.valueOf(s.getNewId()));
    slider = new Slider(s, true);
    showOptions();
    s.updateItems();
  }

  /**
   * Creates a copy of a playtree
   * @param p The playtree to copy
   */
  public Playtree(Playtree p) {
    super(p);

    width = p.width;
    height = p.height;
    font = p.font;
    bgimage = p.bgimage;
    openimage = p.openimage;
    closedimage = p.closedimage;
    itemimage = p.itemimage;
    fgcolor = p.fgcolor;
    bgcolor1 = p.bgcolor1;
    bgcolor2 = p.bgcolor2;
    playcolor = p.playcolor;
    selcolor = p.selcolor;
    flat = p.flat;

    if(p.slider!=null) slider = new Slider(p.slider);

    bgimage_res = s.getImageResource(bgimage);
    openimage_res = s.getImageResource(openimage);
    closedimage_res = s.getImageResource(closedimage);
    itemimage_res = s.getImageResource(itemimage);
    try{
      font_res = (Font)s.getResource(font);
    } catch(Exception ex) {
      ex.printStackTrace();
    }

    if(bgimage_res!=null) bgimage_res.addResourceChangeListener(this);
    if(openimage_res!=null) openimage_res.addResourceChangeListener(this);
    if(closedimage_res!=null) closedimage_res.addResourceChangeListener(this);
    if(itemimage_res!=null) itemimage_res.addResourceChangeListener(this);
    if(font_res!=null) font_res.addResourceChangeListener(this);
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
      visible = visible_tf.getText().replaceAll("\"","'");
      help = help_tf.getText().replaceAll("\"","'");

      width = Integer.parseInt(width_tf.getText());
      height = Integer.parseInt(height_tf.getText());
      font = font_tf.getText();
      bgimage = bgimage_tf.getText();
      itemimage = itemimage_tf.getText();
      openimage = openimage_tf.getText();
      closedimage = closedimage_tf.getText();
      fgcolor = fgcolor_tf.getText().replaceAll("\"","'");
      selcolor = selcolor_tf.getText().replaceAll("\"","'");
      playcolor = playcolor_tf.getText().replaceAll("\"","'");
      bgcolor1 = bgcolor1_tf.getText().replaceAll("\"","'");
      bgcolor2 = bgcolor2_tf.getText().replaceAll("\"","'");
      flat = (Boolean) flat_cb.getSelectedItem();

      s.updateItems();
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      created = true;

      ItemAddEvent paa = new ItemAddEvent(s.getParentListOf(id), this);
      s.m.hist.addEvent(paa);
    } else {
      PlaytreeEditEvent pee = new PlaytreeEditEvent(this);

      id = id_tf.getText();
      x = Integer.parseInt(x_tf.getText());
      y = Integer.parseInt(y_tf.getText());
      lefttop = lefttop_cb.getSelectedItem().toString();
      rightbottom = rightbottom_cb.getSelectedItem().toString();
      xkeepratio = Boolean.parseBoolean(xkeepratio_cb.getSelectedItem().toString());
      ykeepratio = Boolean.parseBoolean(ykeepratio_cb.getSelectedItem().toString());
      visible = visible_tf.getText().replaceAll("\"","'");
      help = help_tf.getText().replaceAll("\"","'");

      width = Integer.parseInt(width_tf.getText());
      height = Integer.parseInt(height_tf.getText());
      font = font_tf.getText();
      bgimage = bgimage_tf.getText();
      itemimage = itemimage_tf.getText();
      openimage = openimage_tf.getText();
      closedimage = closedimage_tf.getText();
      fgcolor = fgcolor_tf.getText().replaceAll("\"","'");
      selcolor = selcolor_tf.getText().replaceAll("\"","'");
      playcolor = playcolor_tf.getText().replaceAll("\"","'");
      bgcolor1 = bgcolor1_tf.getText().replaceAll("\"","'");
      bgcolor2 = bgcolor2_tf.getText().replaceAll("\"","'");
      flat = (Boolean) flat_cb.getSelectedItem();

      s.updateItems();
      s.expandItem(id);

      pee.setNew();
      s.m.hist.addEvent(pee);
    }
    updateToGlobalVariables();
    updateCache();

    try{
      font_res = (Font)s.getResource(font);
    } catch(Exception ex) {
      ex.printStackTrace();
    }
    if(bgimage_res!=null) bgimage_res.addResourceChangeListener(this);
    if(openimage_res!=null) openimage_res.addResourceChangeListener(this);
    if(closedimage_res!=null) closedimage_res.addResourceChangeListener(this);
    if(itemimage_res!=null) itemimage_res.addResourceChangeListener(this);
    if(font_res!=null) font_res.addResourceChangeListener(this);
  }

  @Override
  public void showOptions() {
    if(bgimage_res!=null) bgimage_res.removeResourceChangeListener(this);
    if(openimage_res!=null) openimage_res.removeResourceChangeListener(this);
    if(closedimage_res!=null) closedimage_res.removeResourceChangeListener(this);
    if(itemimage_res!=null) itemimage_res.removeResourceChangeListener(this);
    if(font_res!=null) font_res.removeResourceChangeListener(this);

    if(frame == null) {
      frame = new JFrame(Language.get("WIN_PLAYTREE_TITLE"));
      frame.setIconImage(Main.edit_icon.getImage());
      frame.setResizable(false);
      if(!created) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      }
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
      Object[] bool_values = {true, false};
      JLabel xkeepratio_l = new JLabel(Language.get("WIN_ITEM_XKEEPRATIO"));
      xkeepratio_cb = new JComboBox(bool_values);
      xkeepratio_cb.setToolTipText(Language.get("WIN_ITEM_XKEEPRATIO_TIP"));
      JLabel ykeepratio_l = new JLabel(Language.get("WIN_ITEM_YKEEPRATIO"));
      ykeepratio_cb = new JComboBox(bool_values);
      ykeepratio_cb.setToolTipText(Language.get("WIN_ITEM_YKEEPRATIO_TIP"));
      JLabel visible_l = new JLabel(Language.get("WIN_ITEM_VISIBLE"));
      visible_tf = new JTextField();
      visible_btn = new JButton("", Main.help_icon);
      visible_btn.addActionListener(this);
      JLabel help_l = new JLabel(Language.get("WIN_ITEM_HELP"));
      help_tf = new JTextField();
      help_tf.setToolTipText(Language.get("WIN_ITEM_HELP_TIP"));

      JLabel width_l = new JLabel(Language.get("WIN_ITEM_WIDTH"));
      width_tf = new JTextField();
      width_tf.setDocument(new NumbersOnlyDocument(false));
      JLabel height_l = new JLabel(Language.get("WIN_ITEM_HEIGHT"));
      height_tf = new JTextField();
      height_tf.setDocument(new NumbersOnlyDocument(false));
      JLabel font_l = new JLabel(Language.get("WIN_PLAYTREE_FONT"));
      font_tf = new JTextField();
      JLabel bgimage_l = new JLabel(Language.get("WIN_PLAYTREE_BGIMAGE"));
      bgimage_tf = new JTextField();
      bgimage_tf.setToolTipText(Language.get("WIN_PLAYTREE_BGIMAGE_TIP"));
      JLabel bgcolor1_l = new JLabel(Language.get("WIN_PLAYTREE_BGCOLOR1"));
      bgcolor1_tf = new JTextField();
      bgcolor1_btn = new JButton(Language.get("WIN_PLAYTREE_CHOOSE"));
      bgcolor1_btn.addActionListener(this);
      JLabel bgcolor2_l = new JLabel(Language.get("WIN_PLAYTREE_BGCOLOR2"));
      bgcolor2_tf = new JTextField();
      bgcolor2_btn = new JButton(Language.get("WIN_PLAYTREE_CHOOSE"));
      bgcolor2_btn.addActionListener(this);
      JLabel selcolor_l = new JLabel(Language.get("WIN_PLAYTREE_SELCOLOR"));
      selcolor_tf = new JTextField();
      selcolor_btn = new JButton(Language.get("WIN_PLAYTREE_CHOOSE"));
      selcolor_btn.addActionListener(this);
      JLabel fgcolor_l = new JLabel(Language.get("WIN_PLAYTREE_FGCOLOR"));
      fgcolor_tf = new JTextField();
      fgcolor_btn = new JButton(Language.get("WIN_PLAYTREE_CHOOSE"));
      fgcolor_btn.addActionListener(this);
      JLabel playcolor_l = new JLabel(Language.get("WIN_PLAYTREE_PLAYCOLOR"));
      playcolor_tf = new JTextField();
      playcolor_btn = new JButton(Language.get("WIN_PLAYTREE_CHOOSE"));
      playcolor_btn.addActionListener(this);
      JLabel flat_l = new JLabel(Language.get("WIN_PLAYTREE_FLAT"));
      flat_cb = new JComboBox(bool_values);
      flat_cb.setToolTipText(Language.get("WIN_PLAYTREE_FLAT_TIP"));
      JLabel itemimage_l = new JLabel(Language.get("WIN_PLAYTREE_ITEMIMAGE"));
      itemimage_tf = new JTextField();
      JLabel openimage_l = new JLabel(Language.get("WIN_PLAYTREE_OPENIMAGE"));
      openimage_tf = new JTextField();
      JLabel closedimage_l = new JLabel(Language.get("WIN_PLAYTREE_CLOSEDIMAGE"));
      closedimage_tf = new JTextField();
      slider_btn = new JButton(Language.get("WIN_PLAYTREE_SLIDER"));
      slider_btn.addActionListener(this);
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);

      //Distance of textfields to WEST edge of container
      Component[] labels = {id_l, x_l, y_l, lefttop_l, rightbottom_l, xkeepratio_l, ykeepratio_l, visible_l, help_l, width_l, height_l, font_l, bgimage_l, bgcolor1_l, bgcolor2_l, selcolor_l, fgcolor_l, playcolor_l, flat_l, itemimage_l, openimage_l, closedimage_l};
      int tf_dx = Helper.maxWidth(labels) + 10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;

      JPanel general = new JPanel();
      general.add(id_l);
      general.add(id_tf);
      id_tf.setPreferredSize(new Dimension(tf_wd, id_tf.getPreferredSize().height));
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
      general.add(width_l);
      general.add(width_tf);
      general.add(height_l);
      general.add(height_tf);
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

      general_layout.putConstraint(SpringLayout.NORTH, width_l, 10, SpringLayout.SOUTH, help_tf);
      general_layout.putConstraint(SpringLayout.WEST, width_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, width_tf, 0, SpringLayout.VERTICAL_CENTER, width_l);
      general_layout.putConstraint(SpringLayout.WEST, width_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, width_tf, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, height_l, 10, SpringLayout.SOUTH, width_tf);
      general_layout.putConstraint(SpringLayout.WEST, height_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, height_tf, 0, SpringLayout.VERTICAL_CENTER, height_l);
      general_layout.putConstraint(SpringLayout.WEST, height_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, height_tf, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, height_tf);

      frame.add(general);

      JPanel ptp = new JPanel();
      ptp.add(font_l);
      ptp.add(font_tf);
      font_tf.setPreferredSize(new Dimension(tf_wd, font_tf.getPreferredSize().height));
      ptp.add(bgimage_l);
      ptp.add(bgimage_tf);
      ptp.add(bgcolor1_l);
      ptp.add(bgcolor1_tf);
      bgcolor1_tf.setPreferredSize(new Dimension(tf_wd / 2, bgcolor1_tf.getPreferredSize().height));
      ptp.add(bgcolor1_btn);
      ptp.add(bgcolor2_l);
      ptp.add(bgcolor2_tf);
      bgcolor2_tf.setPreferredSize(new Dimension(tf_wd / 2, bgcolor2_tf.getPreferredSize().height));
      ptp.add(bgcolor2_btn);
      ptp.add(selcolor_l);
      ptp.add(selcolor_tf);
      selcolor_tf.setPreferredSize(new Dimension(tf_wd / 2, selcolor_tf.getPreferredSize().height));
      ptp.add(selcolor_btn);
      ptp.add(fgcolor_l);
      ptp.add(fgcolor_tf);
      fgcolor_tf.setPreferredSize(new Dimension(tf_wd / 2, fgcolor_tf.getPreferredSize().height));
      ptp.add(fgcolor_btn);
      ptp.add(playcolor_l);
      ptp.add(playcolor_tf);
      playcolor_tf.setPreferredSize(new Dimension(tf_wd / 2, playcolor_tf.getPreferredSize().height));
      ptp.add(playcolor_btn);
      ptp.add(flat_l);
      ptp.add(flat_cb);
      ptp.add(itemimage_l);
      ptp.add(itemimage_tf);
      ptp.add(openimage_l);
      ptp.add(openimage_tf);
      ptp.add(closedimage_l);
      ptp.add(closedimage_tf);
      ptp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_PLAYTREE_ATTR")));

      SpringLayout ptp_layout = new SpringLayout();

      ptp_layout.putConstraint(SpringLayout.NORTH, font_l, 5, SpringLayout.NORTH, ptp);
      ptp_layout.putConstraint(SpringLayout.WEST, font_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, font_tf, 0, SpringLayout.VERTICAL_CENTER, font_l);
      ptp_layout.putConstraint(SpringLayout.WEST, font_tf, tf_dx, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.NORTH, bgimage_l, 10, SpringLayout.SOUTH, font_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, bgimage_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, bgimage_tf, 0, SpringLayout.VERTICAL_CENTER, bgimage_l);
      ptp_layout.putConstraint(SpringLayout.WEST, bgimage_tf, tf_dx, SpringLayout.WEST, ptp);
      ptp_layout.putConstraint(SpringLayout.EAST, bgimage_tf, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, bgcolor1_l, 10, SpringLayout.SOUTH, bgimage_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, bgcolor1_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, bgcolor1_tf, 0, SpringLayout.VERTICAL_CENTER, bgcolor1_l);
      ptp_layout.putConstraint(SpringLayout.WEST, bgcolor1_tf, tf_dx, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, bgcolor1_btn, 0, SpringLayout.VERTICAL_CENTER, bgcolor1_l);
      ptp_layout.putConstraint(SpringLayout.WEST, bgcolor1_btn, 5, SpringLayout.EAST, bgcolor1_tf);
      ptp_layout.putConstraint(SpringLayout.EAST, bgcolor1_btn, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, bgcolor2_l, 10, SpringLayout.SOUTH, bgcolor1_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, bgcolor2_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, bgcolor2_tf, 0, SpringLayout.VERTICAL_CENTER, bgcolor2_l);
      ptp_layout.putConstraint(SpringLayout.WEST, bgcolor2_tf, tf_dx, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, bgcolor2_btn, 0, SpringLayout.VERTICAL_CENTER, bgcolor2_l);
      ptp_layout.putConstraint(SpringLayout.WEST, bgcolor2_btn, 5, SpringLayout.EAST, bgcolor2_tf);
      ptp_layout.putConstraint(SpringLayout.EAST, bgcolor2_btn, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, selcolor_l, 10, SpringLayout.SOUTH, bgcolor2_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, selcolor_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, selcolor_tf, 0, SpringLayout.VERTICAL_CENTER, selcolor_l);
      ptp_layout.putConstraint(SpringLayout.WEST, selcolor_tf, tf_dx, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, selcolor_btn, 0, SpringLayout.VERTICAL_CENTER, selcolor_l);
      ptp_layout.putConstraint(SpringLayout.WEST, selcolor_btn, 5, SpringLayout.EAST, selcolor_tf);
      ptp_layout.putConstraint(SpringLayout.EAST, selcolor_btn, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, fgcolor_l, 10, SpringLayout.SOUTH, selcolor_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, fgcolor_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, fgcolor_tf, 0, SpringLayout.VERTICAL_CENTER, fgcolor_l);
      ptp_layout.putConstraint(SpringLayout.WEST, fgcolor_tf, tf_dx, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, fgcolor_btn, 0, SpringLayout.VERTICAL_CENTER, fgcolor_l);
      ptp_layout.putConstraint(SpringLayout.WEST, fgcolor_btn, 5, SpringLayout.EAST, fgcolor_tf);
      ptp_layout.putConstraint(SpringLayout.EAST, fgcolor_btn, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, playcolor_l, 10, SpringLayout.SOUTH, fgcolor_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, playcolor_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, playcolor_tf, 0, SpringLayout.VERTICAL_CENTER, playcolor_l);
      ptp_layout.putConstraint(SpringLayout.WEST, playcolor_tf, tf_dx, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, playcolor_btn, 0, SpringLayout.VERTICAL_CENTER, playcolor_l);
      ptp_layout.putConstraint(SpringLayout.WEST, playcolor_btn, 5, SpringLayout.EAST, playcolor_tf);
      ptp_layout.putConstraint(SpringLayout.EAST, playcolor_btn, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, flat_l, 10, SpringLayout.SOUTH, playcolor_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, flat_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, flat_cb, 0, SpringLayout.VERTICAL_CENTER, flat_l);
      ptp_layout.putConstraint(SpringLayout.WEST, flat_cb, tf_dx, SpringLayout.WEST, ptp);
      ptp_layout.putConstraint(SpringLayout.EAST, flat_cb, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, itemimage_l, 10, SpringLayout.SOUTH, flat_cb);
      ptp_layout.putConstraint(SpringLayout.WEST, itemimage_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, itemimage_tf, 0, SpringLayout.VERTICAL_CENTER, itemimage_l);
      ptp_layout.putConstraint(SpringLayout.WEST, itemimage_tf, tf_dx, SpringLayout.WEST, ptp);
      ptp_layout.putConstraint(SpringLayout.EAST, itemimage_tf, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, openimage_l, 10, SpringLayout.SOUTH, itemimage_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, openimage_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, openimage_tf, 0, SpringLayout.VERTICAL_CENTER, openimage_l);
      ptp_layout.putConstraint(SpringLayout.WEST, openimage_tf, tf_dx, SpringLayout.WEST, ptp);
      ptp_layout.putConstraint(SpringLayout.EAST, openimage_tf, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.NORTH, closedimage_l, 10, SpringLayout.SOUTH, openimage_tf);
      ptp_layout.putConstraint(SpringLayout.WEST, closedimage_l, 5, SpringLayout.WEST, ptp);

      ptp_layout.putConstraint(SpringLayout.VERTICAL_CENTER, closedimage_tf, 0, SpringLayout.VERTICAL_CENTER, closedimage_l);
      ptp_layout.putConstraint(SpringLayout.WEST, closedimage_tf, tf_dx, SpringLayout.WEST, ptp);
      ptp_layout.putConstraint(SpringLayout.EAST, closedimage_tf, 0, SpringLayout.EAST, font_tf);

      ptp_layout.putConstraint(SpringLayout.EAST, ptp, 5, SpringLayout.EAST, font_tf);
      ptp_layout.putConstraint(SpringLayout.SOUTH, ptp, 10, SpringLayout.SOUTH, closedimage_tf);

      ptp.setLayout(ptp_layout);

      frame.add(ptp);

      frame.add(slider_btn);

      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(attr_l);

      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, ptp, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, ptp, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, slider_btn, 10, SpringLayout.SOUTH, ptp);
      layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, slider_btn, 0, SpringLayout.HORIZONTAL_CENTER, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, slider_btn);
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

    width_tf.setText(String.valueOf(width));
    height_tf.setText(String.valueOf(height));
    font_tf.setText(font);
    bgimage_tf.setText(bgimage);
    fgcolor_tf.setText(fgcolor);
    selcolor_tf.setText(selcolor);
    playcolor_tf.setText(playcolor);
    bgcolor1_tf.setText(bgcolor1);
    bgcolor2_tf.setText(bgcolor2);
    flat_cb.setSelectedItem(flat);
    openimage_tf.setText(openimage);
    closedimage_tf.setText(closedimage);
    itemimage_tf.setText(itemimage);

    frame.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")||id.contains("\"")) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_ID_INVALID_MSG"), Language.get("ERROR_ID_INVALID_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        return;
      } else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame, Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i", id_tf.getText()), Language.get("ERROR_ID_INVALID_TITLE"), JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      bgimage_res = s.getImageResource(bgimage_tf.getText());
      if(!bgimage_tf.getText().equals("none") && bgimage_res == null) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", bgimage_tf.getText()), Language.get("ERROR_BITMAP_NEXIST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        bgimage_res = s.getImageResource(bgimage);
        return;
      }
      itemimage_res = s.getImageResource(itemimage_tf.getText());
      if(!itemimage_tf.getText().equals("none") && itemimage_res == null) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", itemimage_tf.getText()), Language.get("ERROR_BITMAP_NEXIST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        itemimage_res = s.getImageResource(itemimage);
        return;
      }
      openimage_res = s.getImageResource(openimage_tf.getText());
      if(!openimage_tf.getText().equals("none") && openimage_res == null) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", openimage_tf.getText()), Language.get("ERROR_BITMAP_NEXIST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        openimage_res = s.getImageResource(openimage);
        return;
      }
      closedimage_res = s.getImageResource(closedimage_tf.getText());
      if(!closedimage_tf.getText().equals("none") && closedimage_res == null) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", closedimage_tf.getText()), Language.get("ERROR_BITMAP_NEXIST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        closedimage_res = s.getImageResource(closedimage);
        return;
      }
      if(!font_tf.getText().equals("defaultfont") && s.getResource(font_tf.getText()) == null) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_FONT_NEXIST").replaceAll("%i", font_tf.getText()), Language.get("ERROR_FONT_NEXIST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      update();
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    } else if(e.getSource().equals(bgcolor1_btn)) {
      Color color = JColorChooser.showDialog(frame, Language.get("WIN_PLAYTREE_CHOOSER_TITLE"), Color.decode(bgcolor1_tf.getText()));
      if(color != null) {
        String hex = "#";
        if(color.getRed() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getRed()).toUpperCase();
        if(color.getGreen() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getGreen()).toUpperCase();
        if(color.getBlue() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getBlue()).toUpperCase();
        bgcolor1_tf.setText(hex);
      }
    } else if(e.getSource().equals(bgcolor2_btn)) {
      Color color = JColorChooser.showDialog(frame, Language.get("WIN_PLAYTREE_CHOOSER_TITLE"), Color.decode(bgcolor2_tf.getText()));
      if(color != null) {
        String hex = "#";
        if(color.getRed() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getRed()).toUpperCase();
        if(color.getGreen() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getGreen()).toUpperCase();
        if(color.getBlue() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getBlue()).toUpperCase();
        bgcolor2_tf.setText(hex);
      }
    } else if(e.getSource().equals(fgcolor_btn)) {
      Color color = JColorChooser.showDialog(frame, Language.get("WIN_PLAYTREE_CHOOSER_TITLE"), Color.decode(fgcolor_tf.getText()));
      if(color != null) {
        String hex = "#";
        if(color.getRed() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getRed()).toUpperCase();
        if(color.getGreen() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getGreen()).toUpperCase();
        if(color.getBlue() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getBlue()).toUpperCase();
        fgcolor_tf.setText(hex);
      }
    } else if(e.getSource().equals(selcolor_btn)) {
      Color color = JColorChooser.showDialog(frame, Language.get("WIN_PLAYTREE_CHOOSER_TITLE"), Color.decode(selcolor_tf.getText()));
      if(color != null) {
        String hex = "#";
        if(color.getRed() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getRed()).toUpperCase();
        if(color.getGreen() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getGreen()).toUpperCase();
        if(color.getBlue() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getBlue()).toUpperCase();
        selcolor_tf.setText(hex);
      }
    } else if(e.getSource().equals(playcolor_btn)) {
      Color color = JColorChooser.showDialog(frame, Language.get("WIN_PLAYTREE_CHOOSER_TITLE"), Color.decode(playcolor_tf.getText()));
      if(color != null) {
        String hex = "#";
        if(color.getRed() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getRed()).toUpperCase();
        if(color.getGreen() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getGreen()).toUpperCase();
        if(color.getBlue() < 16) {
          hex += "0";
        }
        hex += Integer.toHexString(color.getBlue()).toUpperCase();
        playcolor_tf.setText(hex);
      }
    } else if(e.getSource().equals(slider_btn)) {
      //frame.setEnabled(false);
      slider.showOptions();
    } else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-playtree.html");
    } else if(e.getSource().equals(visible_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/boolexpr.html");
    } else if(e.getSource().equals(cancel_btn)) {
      if(!created) {
        java.util.List<Item> l = s.getParentListOf(id);
        if(l != null) {
          l.remove(this);
        }
      }
      if(bgimage_res!=null) bgimage_res.addResourceChangeListener(this);
      if(openimage_res!=null) openimage_res.addResourceChangeListener(this);
      if(closedimage_res!=null) closedimage_res.addResourceChangeListener(this);
      if(itemimage_res!=null) itemimage_res.addResourceChangeListener(this);
      if(font_res!=null) font_res.addResourceChangeListener(this);
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
  }

  @Override
  public String returnCode(String indent) {
    String code = indent + "<Playtree";
    if(!id.equals(ID_DEFAULT)) {
      code += " id=\"" + id + "\"";
    }
    code += " font=\"" + font + "\"";
    if(!bgcolor1.equals(BGCOLOR1_DEFAULT)) {
      code += " bgcolor1=\"" + bgcolor1 + "\"";
    }
    if(!bgcolor2.equals(BGCOLOR2_DEFAULT)) {
      code += " bgcolor2=\"" + bgcolor2 + "\"";
    }
    if(!fgcolor.equals(FGCOLOR_DEFAULT)) {
      code += " fgcolor=\"" + fgcolor + "\"";
    }
    if(!selcolor.equals(SELCOLOR_DEFAULT)) {
      code += " selcolor=\"" + selcolor + "\"";
    }
    if(!playcolor.equals(PLAYCOLOR_DEFAULT)) {
      code += " playcolor=\"" + playcolor + "\"";
    }
    if(!bgimage.equals(BGIMAGE_DEFAULT)) {
      code += " bgimage=\"" + bgimage + "\"";
    }
    if(!itemimage.equals(ITEMIMAGE_DEFAULT)) {
      code += " itemimage=\"" + itemimage + "\"";
    }
    if(!openimage.equals(OPENIMAGE_DEFAULT)) {
      code += " openimage=\"" + openimage + "\"";
    }
    if(!closedimage.equals(CLOSEDIMAGE_DEFAULT)) {
      code += " closedimage=\"" + closedimage + "\"";
    }
    if(!var.equals(VAR_DEFAULT)) {
      code += " var=\"" + var + "\"";
    }
    if(flat != FLAT_DEFAULT) {
      code += " flat=\"" + String.valueOf(flat) + "\"";
    }
    if(x != X_DEFAULT) {
      code += " x=\"" + String.valueOf(x) + "\"";
    }
    if(y != Y_DEFAULT) {
      code += " y=\"" + String.valueOf(y) + "\"";
    }
    if(width != WIDTH_DEFAULT) {
      code += " width=\"" + String.valueOf(width) + "\"";
    }
    if(height != HEIGHT_DEFAULT) {
      code += " height=\"" + String.valueOf(height) + "\"";
    }
    if(!lefttop.equals(LEFTTOP_DEFAULT)) {
      code += " lefttop=\"" + lefttop + "\"";
    }
    if(!rightbottom.equals(RIGHTBOTTOM_DEFAULT)) {
      code += " rightbottom=\"" + rightbottom + "\"";
    }
    if(xkeepratio != XKEEPRATIO_DEFAULT) {
      code += " xkeepratio=\"" + String.valueOf(xkeepratio) + "\"";
    }
    if(ykeepratio != YKEEPRATIO_DEFAULT) {
      code += " ykeepratio=\"" + String.valueOf(ykeepratio) + "\"";
    }
    if(!help.equals(HELP_DEFAULT)) {
      code += " help=\"" + help + "\"";
    }
    if(!visible.equals(VISIBLE_DEFAULT)) {
      code += " visible=\"" + visible + "\"";
    }
    code += ">\n";
    code += slider.returnCode(indent + Skin.indentation);
    code += "\n" + indent + "</Playtree>";
    return code;
  }

  @Override
  public void draw(Graphics2D g, int z) {
    draw(g, 0, 0, z);
  }

  @Override
  public void draw(Graphics2D g_, int x_, int y_, int z) {
    if(width <= 0 || height <= 0) {
      return;
    }    
    if(!created) {
      return;
    }
    if(vis) {
      if(cache==null) updateCache();
      g_.drawImage(cache, (x + x_) * z, (y + y_) * z, width * z, height * z, null);
      slider.draw(g_, x_, y_, z);
    }
    if(selected) {
      g_.setColor(Color.RED);
      g_.drawRect((x + x_) * z, (y + y_) * z, width * z - 1, height * z - 1);

    }
  }

  /**
   * Updates the cached rendering
   */
  public void updateCache() {
    cache = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D)cache.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    java.awt.Font f = s.getFont(font);
    g.setFont(f);
    FontMetrics fm = g.getFontMetrics();
    if(!bgimage.equals("none")) {
      g.drawImage(bgimage_res.image.getSubimage(0, 0, width, height), 0, 0, null);
    } else {
      g.setColor(Color.decode(bgcolor1));
      g.fillRect(0, 0, width, height);
      for(int i = fm.getHeight(); i < height; i = i + fm.getHeight() * 2) {
        g.setColor(Color.decode(bgcolor2));
        g.fillRect(x, 0 + i, width, fm.getHeight());
      }
    }
    int liney = 0;
    BufferedImage cfi = null;
    if(closedimage_res != null) {
      cfi = closedimage_res.image;
    }
    BufferedImage ofi = null;
    if(openimage_res != null) {
      ofi = openimage_res.image;
    }
    BufferedImage iti = null;
    if(itemimage_res != null) {
      iti = itemimage_res.image;
    }
    int lineheight = fm.getHeight();
    int cfi_offset = 0, ofi_offset = 0, iti_offset = 0;
    if(cfi != null) {
      if(cfi.getHeight() > lineheight) {
        lineheight = cfi.getHeight();
      }
      cfi_offset = (lineheight - cfi.getHeight()) / 2;
    }
    if(ofi != null) {
      if(ofi.getHeight() > lineheight) {
        lineheight = ofi.getHeight();
      }
      ofi_offset = (lineheight - ofi.getHeight()) / 2;
    }
    if(iti != null) {
      if(iti.getHeight() > lineheight) {
        lineheight = iti.getHeight();
      }
      iti_offset = (lineheight - iti.getHeight()) / 2;
    }
    int text_offset = (lineheight - fm.getAscent()) / 2;

    g.setColor(Color.decode(fgcolor));
    if(cfi != null && !flat) {
      g.drawImage(cfi, 0, liney + cfi_offset, null);
      liney += lineheight;
      g.drawString("Closed folder", 0 + cfi.getWidth() + 2, liney - text_offset);
    }
    if(ofi != null && !flat) {
      g.drawImage(ofi, 0, liney + ofi_offset, null);
      liney += lineheight;
      g.drawString("Open folder", 0 + ofi.getWidth() + 2, liney - text_offset);
    }
    if(ofi != null && iti != null && !flat) {
      g.drawImage(iti, 0 + ofi.getWidth() + 2, liney + iti_offset, null);
      liney += lineheight;
      g.drawString("Normal item", 0 + ofi.getWidth() + iti.getWidth() + 4, liney - text_offset);
    } else if(iti != null) {
      g.drawImage(iti, 0, liney + iti_offset, null);
      liney += fm.getHeight();
      g.drawString("Normal item", 0 + iti.getWidth() + 4, liney - text_offset);
    } else {
      liney += fm.getHeight();
      g.drawString("Normal item", 0, liney - text_offset);
    }
    g.setColor(Color.decode(playcolor));
    if(ofi != null && iti != null && !flat) {
      g.drawImage(iti, 0 + ofi.getWidth() + 2, liney + iti_offset, null);
      liney += lineheight;
      g.drawString("Playing item", 0 + ofi.getWidth() + iti.getWidth() + 4, liney - text_offset);
    } else if(iti != null) {
      g.drawImage(iti, 0, liney + iti_offset, null);
      liney += lineheight;
      g.drawString("Playing item", 0 + iti.getWidth() + 2, liney - text_offset);
    } else {
      liney += lineheight;
      g.drawString("Playing item", 0, liney - text_offset);
    }
    g.setColor(Color.decode(selcolor));
    g.fillRect(0, liney, width, lineheight);
    g.setColor(Color.decode(fgcolor));
    if(ofi != null && iti != null && !flat) {
      g.drawImage(iti, 0 + ofi.getWidth() + 2, liney + iti_offset, null);
      liney += lineheight;
      g.drawString("Selected item", 0 + ofi.getWidth() + iti.getWidth() + 4, liney - text_offset);
    } else if(iti != null) {
      g.drawImage(iti, 0, liney + iti_offset, null);
      liney += lineheight;
      g.drawString("Selected item", 0 + iti.getWidth() + 2, liney - text_offset);
    } else {
      liney += lineheight;
      g.drawString("Selected item", 0, liney - text_offset);
    }
  }

  @Override
  public boolean contains(int x_, int y_) {
    return (x_ >= x + offsetx && x_ <= x + width + offsetx && y_ >= y + offsety && y_ <= y + height + offsety);
  }

  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Playtree: " + id);
    node.add(slider.getTreeNode());
    return node;
  }

  @Override
  public Item getItem(String id_) {
    if(id.equals(id_)) {
      return this;
    } else {
      return slider.getItem(id_);
    }
  }

  @Override
  public Item getParentOf(String id_) {
    if(slider != null) {
      if(slider.id.equals(id_)) {
        return this;
      } else {
        return slider.getParentOf(id_);
      }
    } else {
      return null;
    }
  }

  @Override
  public boolean uses(String id_) {
    return (((slider != null) ? slider.uses(id_) : false) || bgimage.equals(id_) || openimage.equals(id_) || closedimage.equals(id_) || itemimage.equals(id_) || font.equals(id_));
  }

  @Override
  public void renameForCopy(String p) {
    super.renameForCopy(p);
    slider.renameForCopy(p);
  }

  @Override
  public void updateToGlobalVariables() {
    super.updateToGlobalVariables();
    if(slider != null) {
      slider.updateToGlobalVariables();
    }
  }

  public void onResourceChanged(ResourceChangedEvent e) {
    if(bgimage.equals(e.getOldID())) bgimage = e.getResource().id;
    if(openimage.equals(e.getOldID())) openimage = e.getResource().id;
    if(closedimage.equals(e.getOldID())) closedimage = e.getResource().id;
    if(itemimage.equals(e.getOldID())) itemimage = e.getResource().id;
    if(font.equals(e.getOldID())) font = e.getResource().id;
    updateCache();
  }

}
