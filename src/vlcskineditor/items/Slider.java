/*****************************************************************************
 * Slider.java
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
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import vlcskineditor.resources.ImageResource;
import vlcskineditor.resources.ResourceChangeListener;
import vlcskineditor.resources.ResourceChangedEvent;

/**
 * Slider item
 * @author Daniel Dreibrodt
 */
public class Slider extends Item implements ActionListener, ResourceChangeListener {
  
  public final String DOWN_DEFAULT = "none";
  public final String OVER_DEFAULT = "none";  
  public final int THICKNESS_DEFAULT = 10;
  public final String VALUE_DEFAULT = "none";
  public final String TOOLTIPTEXT_DEFAULT = "none";
 
  public String up;
  public String down = DOWN_DEFAULT;
  public String over = OVER_DEFAULT;
  public String points ="(0,0)";
  public int thickness = THICKNESS_DEFAULT;
  public String value = VALUE_DEFAULT;
  public String tooltiptext = TOOLTIPTEXT_DEFAULT;
  
  public SliderBackground sbg = null;
  
  boolean inPlaytree = false;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, up_tf, down_tf, over_tf, points_tf, thickness_tf, tooltiptext_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb, resize_cb, action_cb, value_cb;
  JCheckBox sbg_chb;
  JButton visible_btn, ok_btn, cancel_btn, help_btn, sbg_btn;

  ImageResource up_res, over_res, down_res;

  /** This object manages the maths around the bezier curve of the slider */
  private Bezier b;
  /** The slider control points */
  private int[] xpos,ypos;

  private Point2D.Float sliderPos;
  private Point2D.Float[] bezierPoints;
  
  {
    type = Language.get("SLIDER");
  }

  /**
   * Parses a slider from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public Slider(Node n, Skin s_) {
    s = s_;

    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    up = XML.getStringAttributeValue(n, "up", up);
    down = XML.getStringAttributeValue(n, "down", down);
    over = XML.getStringAttributeValue(n, "over", over);

    points = XML.getStringAttributeValue(n, "points", points);
    thickness = XML.getIntAttributeValue(n, "thickness", thickness);
    value = XML.getStringAttributeValue(n, "value", value);

    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    tooltiptext = XML.getStringAttributeValue(n, "tooltiptext", tooltiptext);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);

    NodeList nodes = n.getChildNodes();
    for(int i=0;i<nodes.getLength();i++) {
      if(nodes.item(i).getNodeName().equals("SliderBackground"))
        sbg = new SliderBackground(nodes.item(i),s,this);
    }

    up_res = s.getImageResource(up);
    over_res = s.getImageResource(over);
    down_res = s.getImageResource(down);

    if(up_res!=null) up_res.addResourceChangeListener(this);
    if(over_res!=null) over_res.addResourceChangeListener(this);
    if(down_res!=null) down_res.addResourceChangeListener(this);

    updateToGlobalVariables();

    created = true;
  }

  /**
   * Parses a slider contained in a Playtree from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   * @param isInPlaytree The boolean value indicating whether the slider is inside a playtree or not
   */
  public Slider(Node n, Skin s_, boolean isInPlaytree) {
    this(n, s_);
    inPlaytree = isInPlaytree;
  }

  /**
   * Creates a copy of a slider
   * @param s The slider to copy
   */
  public Slider(Slider sl) {
    super(sl);
    up = sl.up;
    down = sl.down;
    over = sl.over;
    points = sl.points;
    thickness = sl.thickness;
    value = sl.value;
    inPlaytree = sl.isInPlaytree();

    up_res = s.getImageResource(up);
    over_res = s.getImageResource(over);
    down_res = s.getImageResource(down);

    if(up_res!=null) up_res.addResourceChangeListener(this);
    if(over_res!=null) over_res.addResourceChangeListener(this);
    if(down_res!=null) down_res.addResourceChangeListener(this);

    updateToGlobalVariables();

    if(sl.sbg!=null) sbg = new SliderBackground(sl.sbg);
  }

  /**
   * Creates an empty slider and shows a dialog to edit it
   * @param s_ The skin to which the slider belongs
   */
  public Slider(Skin s_) {
    this(s_, false);
  }

  /**
   * Creates an empty slider for use in a playtree
   * @param s_
   * @param ipt This should be true
   */
  public Slider(Skin s_, boolean ipt) {
    if(ipt) {
      s = s_;
      up = "none";
      id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
      updateToGlobalVariables();
      inPlaytree = ipt;
      created=true;
    } else {
      s = s_;
      up = "none";
      id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
      updateToGlobalVariables();
      showOptions();
      s.updateItems();
      s.expandItem(id);
    }
  }

  public void updateBezier() {
    if(points==null) return;
    String[] pnts = points.split("\\),\\(");
    xpos = new int[pnts.length];
    ypos = new int[pnts.length];
    for(int i=0;i<pnts.length;i++) {
      String pnt = pnts[i];      
      String[] coords = pnt.split(",");        
      xpos[i] = Integer.parseInt(coords[0].replaceAll("\\(","").trim());
      ypos[i] = Integer.parseInt(coords[1].replaceAll("\\)","").trim());
    }          
    b = new Bezier(xpos,ypos,Bezier.kCoordsBoth);
    sliderPos = b.getPoint(s.gvars.getSliderValue());
    bezierPoints = new Point2D.Float[11];
    for(float f=0;f<=100;f+=10) {
      bezierPoints[(int)(f/10)] = b.getPoint(f/100);
    }
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
      points = points_tf.getText();
      thickness = Integer.parseInt(thickness_tf.getText());
      if(!inPlaytree) value = (String)value_cb.getSelectedItem();
      tooltiptext = tooltiptext_tf.getText();

      updateBezier();

      s.updateItems();   
      s.expandItem(id);
      created=true;
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
      ItemAddEvent sae = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(sae);
    }
    else {
      SliderEditEvent see = new SliderEditEvent(this);
      
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
      points = points_tf.getText();
      thickness = Integer.parseInt(thickness_tf.getText());
      if(!inPlaytree) value = (String)value_cb.getSelectedItem();
      tooltiptext = tooltiptext_tf.getText();

      updateBezier();

      s.updateItems();   
      s.expandItem(id);
      
      see.setNew();
      s.m.hist.addEvent(see);      
    }
    updateToGlobalVariables();
  }

  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_SLIDER_TITLE"));
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
      
      JLabel up_l = new JLabel(Language.get("WIN_SLIDER_UP"));
      up_tf = new JTextField();
      JLabel over_l = new JLabel(Language.get("WIN_SLIDER_OVER"));
      over_tf = new JTextField();
      JLabel down_l = new JLabel(Language.get("WIN_SLIDER_DOWN"));
      down_tf = new JTextField();
      JLabel points_l = new JLabel(Language.get("WIN_SLIDER_POINTS"));
      points_tf = new JTextField();
      JLabel thickness_l = new JLabel(Language.get("WIN_SLIDER_THICKNESS"));
      thickness_tf = new JTextField();      
      JLabel value_l = new JLabel(Language.get("WIN_SLIDER_VALUE"));
      if (inPlaytree) {
        String[] values = { Language.get("WIN_SLIDER_VALUE_SCROLL") };
        value_cb = new JComboBox(values);
      }
      else {
        String[] values = { "time" , "volume", "equalizer.preamp", "equalizer.band(0)", "equalizer.band(1)", "equalizer.band(2)", "equalizer.band(3)", "equalizer.band(4)", "equalizer.band(5)", "equalizer.band(6)",  "equalizer.band(7)",  "equalizer.band(8)",  "equalizer.band(9)"};
        value_cb = new JComboBox(values);
      }      
      JLabel tooltiptext_l = new JLabel(Language.get("WIN_ITEM_TOOLTIPTEXT"));
      tooltiptext_tf = new JTextField();     
      
      sbg_chb = new JCheckBox(Language.get("WIN_SLIDER_BG_ENABLE"));
      sbg_chb.addActionListener(this);
      sbg_btn = new JButton(Language.get("WIN_SLIDER_BG_EDIT"));
      sbg_btn.addActionListener(this);
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);

      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, lefttop_l, rightbottom_l, xkeepratio_l, ykeepratio_l, visible_l, help_l, up_l, over_l, down_l, points_l, thickness_l, tooltiptext_l, value_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
      
      JPanel general = new JPanel();
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
      
      JPanel button = new JPanel();
      button.add(up_l);
      button.add(up_tf);
      up_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      button.add(over_l);
      button.add(over_tf);
      button.add(down_l);
      button.add(down_tf);
      button.add(points_l);
      button.add(points_tf);
      button.add(thickness_l);
      button.add(thickness_tf);
      button.add(value_l);
      button.add(value_cb);
      button.add(tooltiptext_l);
      button.add(tooltiptext_tf);
      button.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_SLIDER_ATTR")));

      SpringLayout button_layout = new SpringLayout();
      button.setLayout(button_layout);

      button_layout.putConstraint(SpringLayout.NORTH, up_l, 5, SpringLayout.NORTH, button);
      button_layout.putConstraint(SpringLayout.WEST, up_l, 5, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, up_tf, 0, SpringLayout.VERTICAL_CENTER, up_l);
      button_layout.putConstraint(SpringLayout.WEST, up_tf, tf_dx, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.NORTH, over_l, 10, SpringLayout.SOUTH, up_tf);
      button_layout.putConstraint(SpringLayout.WEST, over_l, 5, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, over_tf, 0, SpringLayout.VERTICAL_CENTER, over_l);
      button_layout.putConstraint(SpringLayout.WEST, over_tf, tf_dx, SpringLayout.WEST, button);
      button_layout.putConstraint(SpringLayout.EAST, over_tf, 0, SpringLayout.EAST, up_tf);

      button_layout.putConstraint(SpringLayout.NORTH, down_l, 10, SpringLayout.SOUTH, over_tf);
      button_layout.putConstraint(SpringLayout.WEST, down_l, 5, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, down_tf, 0, SpringLayout.VERTICAL_CENTER, down_l);
      button_layout.putConstraint(SpringLayout.WEST, down_tf, tf_dx, SpringLayout.WEST, button);
      button_layout.putConstraint(SpringLayout.EAST, down_tf, 0, SpringLayout.EAST, up_tf);

      button_layout.putConstraint(SpringLayout.NORTH, points_l, 10, SpringLayout.SOUTH, down_tf);
      button_layout.putConstraint(SpringLayout.WEST, points_l, 5, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, points_tf, 0, SpringLayout.VERTICAL_CENTER, points_l);
      button_layout.putConstraint(SpringLayout.WEST, points_tf, tf_dx, SpringLayout.WEST, button);
      button_layout.putConstraint(SpringLayout.EAST, points_tf, 0, SpringLayout.EAST, up_tf);

      button_layout.putConstraint(SpringLayout.NORTH, thickness_l, 10, SpringLayout.SOUTH, points_tf);
      button_layout.putConstraint(SpringLayout.WEST, thickness_l, 5, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, thickness_tf, 0, SpringLayout.VERTICAL_CENTER, thickness_l);
      button_layout.putConstraint(SpringLayout.WEST, thickness_tf, tf_dx, SpringLayout.WEST, button);
      button_layout.putConstraint(SpringLayout.EAST, thickness_tf, 0, SpringLayout.EAST, up_tf);

      button_layout.putConstraint(SpringLayout.NORTH, value_l, 10, SpringLayout.SOUTH, thickness_tf);
      button_layout.putConstraint(SpringLayout.WEST, value_l, 5, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, value_cb, 0, SpringLayout.VERTICAL_CENTER, value_l);
      button_layout.putConstraint(SpringLayout.WEST, value_cb, tf_dx, SpringLayout.WEST, button);
      button_layout.putConstraint(SpringLayout.EAST, value_cb, 0, SpringLayout.EAST, up_tf);

      button_layout.putConstraint(SpringLayout.NORTH, tooltiptext_l, 10, SpringLayout.SOUTH, value_cb);
      button_layout.putConstraint(SpringLayout.WEST, tooltiptext_l, 5, SpringLayout.WEST, button);

      button_layout.putConstraint(SpringLayout.VERTICAL_CENTER, tooltiptext_tf, 0, SpringLayout.VERTICAL_CENTER, tooltiptext_l);
      button_layout.putConstraint(SpringLayout.WEST, tooltiptext_tf, tf_dx, SpringLayout.WEST, button);
      button_layout.putConstraint(SpringLayout.EAST, tooltiptext_tf, 0, SpringLayout.EAST, up_tf);

      button_layout.putConstraint(SpringLayout.EAST, button, 5, SpringLayout.EAST, up_tf);
      button_layout.putConstraint(SpringLayout.SOUTH, button, 10, SpringLayout.SOUTH, tooltiptext_tf);

      frame.add(button);
      
      JPanel back = new JPanel(null);
      back.add(sbg_chb);
      back.add(sbg_btn);
      back.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_SLIDER_BG")));

      SpringLayout back_layout = new SpringLayout();
      back.setLayout(back_layout);

      back_layout.putConstraint(SpringLayout.NORTH, sbg_chb, 5, SpringLayout.NORTH, back);
      back_layout.putConstraint(SpringLayout.WEST, sbg_chb, 5, SpringLayout.WEST, back);

      back_layout.putConstraint(SpringLayout.NORTH, sbg_btn, 5, SpringLayout.NORTH, back);
      back_layout.putConstraint(SpringLayout.WEST, sbg_btn, 5, SpringLayout.EAST, sbg_chb);

      back_layout.putConstraint(SpringLayout.EAST, back, 5, SpringLayout.EAST, sbg_btn);
      back_layout.putConstraint(SpringLayout.SOUTH, back, 10, SpringLayout.SOUTH, sbg_btn);

      frame.add(back);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);

      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, button, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, button, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, back, 10, SpringLayout.SOUTH, button);
      layout.putConstraint(SpringLayout.WEST, back, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.EAST, back, 0, SpringLayout.EAST, general);

      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, back);
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
    thickness_tf.setText(String.valueOf(thickness));
    points_tf.setText(points);
    if(!inPlaytree) value_cb.setSelectedItem(value);
    tooltiptext_tf.setText(tooltiptext);    
    
    sbg_chb.setSelected(sbg!=null);
    sbg_btn.setEnabled(sbg!=null);         
    
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
      if(points_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_POINTS_INVALID"),Language.get("ERROR_POINTS_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      update();
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-slider.html");
    }
    else if(e.getSource().equals(sbg_chb)) {
      if(sbg_chb.isSelected()) {
        sbg = new SliderBackground(s,this);
        sbg_btn.setEnabled(true);
      }
      else {
        sbg=null;
        sbg_btn.setEnabled(false);
      }
    }
    else if(e.getSource().equals(sbg_btn)) {
      if(sbg!=null) sbg.showOptions();
    }
    else if(e.getSource().equals(visible_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-slider.html");
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

  public void removeBG() {
    if(sbg==null) return;    
    sbg = null;
    sbg_chb.setSelected(false);
    sbg_btn.setEnabled(false);
  }

  @Override
  public String returnCode(String indent) {
    String code = indent+"<Slider";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    code+=" points=\""+points+"\"";
    if (thickness!=THICKNESS_DEFAULT) code+=" thickness=\""+String.valueOf(thickness)+"\"";
    if(!inPlaytree) if (!value.equals(VALUE_DEFAULT)) code+=" value=\""+value+"\"";
    if (!tooltiptext.equals(TOOLTIPTEXT_DEFAULT)) code+=" tooltiptext=\""+tooltiptext+"\"";
    code+=" up=\""+up+"\"";
    if (!down.equals(DOWN_DEFAULT)) code+=" down=\""+down+"\"";
    if (!over.equals(OVER_DEFAULT)) code+=" over=\""+over+"\"";
    if (!lefttop.equals(LEFTTOP_DEFAULT)) code+=" lefttop=\""+lefttop+"\"";
    if (!rightbottom.equals(RIGHTBOTTOM_DEFAULT)) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (!help.equals(HELP_DEFAULT)) code+=" help=\""+help+"\"";
    if (!visible.equals(VISIBLE_DEFAULT)) code+=" visible=\""+visible+"\"";
    if (sbg==null) {
      code+="/>";
    }
    else {
      code+=">\n"+sbg.returnCode(indent+Skin.indentation);
      code+="\n"+indent+"</Slider>";
    }
    return code;
  }

  @Override
  public void draw(Graphics2D g, int z) {
    draw(g,0,0,z);
  }

  @Override
  public void draw(Graphics2D g, int x_, int y_, int z) {    
    if(!created) return;
    offsetx=x_;
    offsety=y_;
    //boolean vis = s.gvars.parseBoolean(visible);
    if(sbg!=null && vis) {
      sbg.setOffset(x+x_,y+y_);
      sbg.draw(g,x+x_,y+y_,z);      
    }    
    BufferedImage si = up_res.image;
    if(vis && si!=null) {
      g.drawImage(si,(int)(sliderPos.getX()+x+x_-si.getWidth()/2)*z,(int)(sliderPos.getY()+y+y_-si.getHeight()/2)*z,si.getWidth()*z,si.getHeight()*z,null);
    }
    if(selected) {
      g.setColor(Color.RED);
      /*for(float f=0f;f<=1f;f=f+0.1f) {
        Point2D.Float p1 = b.getPoint(f);
        Point2D.Float p2 = b.getPoint(f+0.1f);        
        g.drawLine((int)(p1.getX()+x+x_)*z,(int)(p1.getY()+y+y_)*z,(int)(p2.getX()+x+x_)*z,(int)(p2.getY()+y+y_)*z);
      }*/
      for(int i=0;i<10;i++) {
        Point2D.Float p1 = bezierPoints[i];
        Point2D.Float p2 = bezierPoints[i+1];
        g.drawLine((int)(p1.getX()+x+x_)*z,(int)(p1.getY()+y+y_)*z,(int)(p2.getX()+x+x_)*z,(int)(p2.getY()+y+y_)*z);
      }      
      for(int i=0;i<xpos.length;i++) {
        g.setColor(Color.BLACK);
        g.fillOval((xpos[i]+x+x_-2)*z,(ypos[i]+y+y_-2)*z,4,4);
        g.setColor(Color.YELLOW);
        g.fillOval((xpos[i]+x+x_-1)*z,(ypos[i]+y+y_-1)*z,2,2);
      }
    }
  }

  @Override
  public boolean contains(int x_,int y_) {    
    int h = b.getHeight();
    int w = b.getWidth();
    return (x_>=x+offsetx && x_<=x+offsetx+w && y_>=y+offsety && y_<=y+offsety+h);   
  }

  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Slider: "+id);
    if(sbg!=null) node.add(sbg.getTreeNode());
    return node;
  }

  @Override
  public Item getItem(String id_) {
    if(id.equals(id_)) return this;
    else if(sbg==null) return null;
    else if(sbg.id.equals(id_)) return sbg;
    else return null;
  }

  @Override
  public Item getParentOf(String id_) {
   if(sbg!=null) {
     if(sbg.id.equals(id_)) return this;
     else return null;
   }
   else return null;
  }

  @Override
  public boolean uses(String id_) {
    return(((sbg!=null)?sbg.uses(id_):false)||up.equals(id_)||over.equals(id_)||down.equals(id_));
  }

  @Override  
  public void renameForCopy(String p) {    
    String p_ = p;
    super.renameForCopy(p);
    if(sbg!=null) sbg.renameForCopy(p_);
  }

  @Override
  public void updateToGlobalVariables() {
    vis = s.gvars.parseBoolean(visible);
    updateBezier();
    if(sbg!=null) sbg.updateToGlobalVariables();
  }

  public boolean isInPlaytree() {
    return inPlaytree;
  }

  public Bezier getBezier() {
    return b;
  }

  public int getControlX(int index) {
    return xpos[index];
  }

  public int getControlY(int index) {
    return ypos[index];
  }

  public int getControlPointNum() {
    return xpos.length;
  }

  public void removeControlPoint(int index) {
    SliderEditEvent see = new SliderEditEvent(this);
    if(index>=xpos.length) return;
    String points_new = "";
    for(int i=0;i<xpos.length;i++) {
      if(i!=index) {
        if(points_new.length()>0) points_new+=",";
        points_new+="("+xpos[i]+","+ypos[i]+")";
      }
    }
    points = points_new;
    updateBezier();
    see.setNew();
    s.m.hist.addEvent(see);
  }

  public void addControlPoint(int x, int y) {
    SliderEditEvent see = new SliderEditEvent(this);
    if(points.length()>0) points+=",";
    points+="("+x+","+y+")";
    updateBezier();
    see.setNew();
    s.m.hist.addEvent(see);
  }

  public void moveControlPointTo(int index, int x, int y) {
    xpos[index] = x;
    ypos[index] = y;
    String points_new = "";
    for(int i=0;i<xpos.length;i++) {
      if(points_new.length()>0) points_new+=",";
      points_new+="("+xpos[i]+","+ypos[i]+")";
    }
    points = points_new;
    updateBezier();
  }

  public void onResourceChanged(ResourceChangedEvent e) {
    if(up.equals(e.getOldID())) {
      up = e.getResource().id;
    }
    if(over.equals(e.getOldID())) {
      over = e.getResource().id;
    }
    if(down.equals(e.getOldID())) {
      down = e.getResource().id;
    }
  }
}
