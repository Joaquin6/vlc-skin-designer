/*****************************************************************************
 * Anchor.java
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
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;

/**
 * Anchor item
 * @author Daniel Dreibrodt
 */
public class Anchor extends Item implements ActionListener {
  
  
  public final String POINTS_DEFAULT = "(0,0)";
  public final int RANGE_DEFAULT = 10;
  public int priority;
  public String points = POINTS_DEFAULT;
  public int range = RANGE_DEFAULT;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, points_tf, range_tf, priority_tf;
  JComboBox lefttop_cb;
  JButton ok_btn, cancel_btn, help_btn;
  
  Bezier b;
  int[] xpos,ypos;
  
  {
    type = Language.get("ANCHOR");
  }
  
  /**
   * Creates an anchor item from a given XML node
   * @param n The XML node
   * @param s_ The parent skin manager
   */
  public Anchor(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    points = XML.getStringAttributeValue(n, "points", points);
    priority = XML.getIntAttributeValue(n, "priority", priority);
    range = XML.getIntAttributeValue(n, "range", range);
    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    updateBezier();
    created = true;
  }
  
  /**
   * Creates a new Anchor from user input.
   * @param s_ The Skin in which the Anchor is used.
   */
  public Anchor(Skin s_) {
    s = s_;
    priority = 0;
    id = type+" #"+s.getNewId();
    updateBezier();
    showOptions();
  }

  /**
   * Creates a copy of a given anchor
   * @param a The anchor to copy
   */
  public Anchor(Anchor a) {
    super(a);
    priority = a.priority;
    points = a.points;
    range = a.range;
    updateBezier();
  }

  public void updateBezier() {
    String[] pnts = points.split("\\),\\(");
    xpos = new int[pnts.length];
    ypos = new int[pnts.length];
    for(int i=0;i<pnts.length;i++) {
      String pnt = pnts[i];      
      String[] coords = pnt.split(",");        
      xpos[i] = Integer.parseInt(coords[0].replaceAll("\\(",""));
      ypos[i] = Integer.parseInt(coords[1].replaceAll("\\)",""));
    }          
    b = new Bezier(xpos,ypos,Bezier.kCoordsBoth);
  }
  @Override
  public void update() {
    if(!created) {
      id=id_tf.getText();
      priority=Integer.parseInt(priority_tf.getText());
      lefttop=lefttop_cb.getSelectedItem().toString();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      points=points_tf.getText();
      range=Integer.parseInt(range_tf.getText());
      updateBezier();
      s.updateItems();
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      created = true;
      ItemAddEvent aae = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(aae);
    } 
    else {
      AnchorEditEvent aee = new AnchorEditEvent(this);
      id=id_tf.getText();
      priority=Integer.parseInt(priority_tf.getText());
      lefttop=lefttop_cb.getSelectedItem().toString();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      points=points_tf.getText();
      range=Integer.parseInt(range_tf.getText());
      updateBezier();
      s.updateItems();
      s.expandItem(id);      
      aee.setNew();
      s.m.hist.addEvent(aee);
    }   
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_ANCHOR_TITLE"));
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
      JLabel points_l = new JLabel(Language.get("WIN_ANCHOR_POINTS"));
      points_tf = new JTextField();
      points_tf.setToolTipText(Language.get("WIN_ANCHOR_POINTS_TIP"));
      JLabel range_l = new JLabel(Language.get("WIN_ANCHOR_RANGE"));
      range_tf = new JTextField();
      range_tf.setToolTipText(Language.get("WIN_ANCHOR_RANGE_TIP"));
      range_tf.setDocument(new NumbersOnlyDocument());
      JLabel priority_l = new JLabel(Language.get("WIN_ANCHOR_PRIORITY"));
      priority_tf = new JTextField();
      priority_tf.setToolTipText(Language.get("WIN_ANCHOR_PRIORITY_TIP"));
      priority_tf.setDocument(new NumbersOnlyDocument());
      JLabel lefttop_l = new JLabel(Language.get("WIN_ITEM_LEFTTOP"));
      String[] lefttop_values = {"lefttop", "leftbottom", "righttop", "rightbottom"};
      lefttop_cb = new JComboBox(lefttop_values);
      lefttop_cb.setToolTipText(Language.get("WIN_ITEM_LEFTTOP_TIP"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);      
      JLabel star_l = new JLabel(Language.get("NOTE_STARRED"));
      
      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, priority_l, lefttop_l, x_l, y_l, range_l, points_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
      
      JPanel general = new JPanel();
      general.add(id_l);      
      id_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      general.add(id_tf);
      general.add(priority_l);
      general.add(priority_tf);
      general.add(lefttop_l);
      general.add(lefttop_cb);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));
      
      SpringLayout general_layout = new SpringLayout();
      
      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.NORTH, priority_l, 10, SpringLayout.SOUTH, id_tf);
      general_layout.putConstraint(SpringLayout.WEST, priority_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, priority_tf, 0, SpringLayout.VERTICAL_CENTER, priority_l);
      general_layout.putConstraint(SpringLayout.WEST, priority_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, priority_tf, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.NORTH, lefttop_l, 10, SpringLayout.SOUTH, priority_tf);
      general_layout.putConstraint(SpringLayout.WEST, lefttop_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, lefttop_cb, 0, SpringLayout.VERTICAL_CENTER, lefttop_l);
      general_layout.putConstraint(SpringLayout.WEST, lefttop_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, lefttop_cb, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, lefttop_cb);
      
      general.setLayout(general_layout);
      
      frame.add(general);
      
      JPanel bounds = new JPanel();
      bounds.add(x_l);
      x_tf.setPreferredSize(new Dimension(tf_wd,x_tf.getPreferredSize().height));
      bounds.add(x_tf);
      bounds.add(y_l);
      bounds.add(y_tf);
      bounds.add(points_l);
      bounds.add(points_tf);
      bounds.add(range_l);
      bounds.add(range_tf);
      bounds.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ANCHOR_BOUNDARIES")));       
      
      SpringLayout bounds_layout = new SpringLayout();
      
      bounds_layout.putConstraint(SpringLayout.NORTH, x_l, 5, SpringLayout.NORTH, bounds);
      bounds_layout.putConstraint(SpringLayout.WEST, x_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, x_l);
      bounds_layout.putConstraint(SpringLayout.WEST, x_tf, tf_dx, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.NORTH, y_l, 10, SpringLayout.SOUTH, x_tf);
      bounds_layout.putConstraint(SpringLayout.WEST, y_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, y_tf, 0, SpringLayout.VERTICAL_CENTER, y_l);
      bounds_layout.putConstraint(SpringLayout.WEST, y_tf, tf_dx, SpringLayout.WEST, bounds);
      bounds_layout.putConstraint(SpringLayout.EAST, y_tf, 0, SpringLayout.EAST, x_tf);
      
      bounds_layout.putConstraint(SpringLayout.NORTH, points_l, 10, SpringLayout.SOUTH, y_tf);
      bounds_layout.putConstraint(SpringLayout.WEST, points_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, points_tf, 0, SpringLayout.VERTICAL_CENTER, points_l);
      bounds_layout.putConstraint(SpringLayout.WEST, points_tf, tf_dx, SpringLayout.WEST, bounds);
      bounds_layout.putConstraint(SpringLayout.EAST, points_tf, 0, SpringLayout.EAST, x_tf);
      
      bounds_layout.putConstraint(SpringLayout.NORTH, range_l, 10, SpringLayout.SOUTH, points_tf);
      bounds_layout.putConstraint(SpringLayout.WEST, range_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, range_tf, 0, SpringLayout.VERTICAL_CENTER, range_l);
      bounds_layout.putConstraint(SpringLayout.WEST, range_tf, tf_dx, SpringLayout.WEST, bounds);
      bounds_layout.putConstraint(SpringLayout.EAST, range_tf, 0, SpringLayout.EAST, x_tf);
      
      bounds_layout.putConstraint(SpringLayout.EAST, bounds, 5, SpringLayout.EAST, x_tf);
      bounds_layout.putConstraint(SpringLayout.SOUTH, bounds, 10, SpringLayout.SOUTH, range_tf);
      
      bounds.setLayout(bounds_layout);
      
      frame.add(bounds);
      frame.add(star_l);
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      
      SpringLayout layout = new SpringLayout();
      
      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      //layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, bounds, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, bounds, 5, SpringLayout.WEST, frame.getContentPane());
      //layout.putConstraint(SpringLayout.EAST, bounds, 5, SpringLayout.EAST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, star_l, 10, SpringLayout.SOUTH, bounds);
      layout.putConstraint(SpringLayout.WEST, star_l, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, ok_btn, 10, SpringLayout.SOUTH, star_l);
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
    priority_tf.setText(String.valueOf(priority));
    lefttop_cb.setSelectedItem(lefttop);
    x_tf.setText(String.valueOf(x));
    y_tf.setText(String.valueOf(y));
    points_tf.setText(points);
    range_tf.setText(String.valueOf(range));
    frame.setVisible(true);
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")||id.contains("\"")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_INVALID_MSG"),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i", id_tf.getText()),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      if(points_tf.getText().equals("")||points.contains("\"")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_POINTS_INVALID"),Language.get("ERROR_POINTS_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      update();
      frame.setVisible(false);
      frame.dispose();
      frame = null;      
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-anchor.html");
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
    String code = indent+"<Anchor";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if(!points.equals(POINTS_DEFAULT)) code+=" points=\""+points+"\"";
    code+=" priority=\""+String.valueOf(priority)+"\"";
    if (!lefttop.equals(LEFTTOP_DEFAULT)) code+=" lefttop=\""+lefttop+"\"";    
    code+="/>";
    return code;
  }
  @Override
  public void draw(Graphics2D g, int z) {
    draw(g,offsetx,offsety,z);
  }
  @Override
  public void draw(Graphics2D g,int x_,int y_, int z) {
    if(!created) return;
    if(selected) {        
      g.setColor(Color.RED);
      for(float f=0f;f<=1f;f=f+0.1f) {
        Point2D.Float p1 = b.getPoint(f);
        Point2D.Float p2 = b.getPoint(f+0.1f);        
        g.drawLine((int)(p1.getX()+x+x_)*z,(int)(p1.getY()+y+y_)*z,(int)(p2.getX()+x+x_)*z,(int)(p2.getY()+y+y_)*z);
      }
      g.setColor(Color.YELLOW);
      for(int i=0;i<xpos.length;i++) {
        g.fillOval((xpos[i]+x+x_-1)*z,(ypos[i]+y+y_-1)*z,3,3);
      }
    }
  }
  @Override
  public boolean contains(int x_, int y_) {
    return true;
    /*int h = b.getHeight();
    int w = b.getWidth();
    return (x_>=x+offsetx && x_<=x+offsetx+w && y_>=y+offsety && y_<=y+offsety+h);*/
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Anchor: "+id);  
    return node;
  }
}
