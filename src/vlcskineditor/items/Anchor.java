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

/**
 * Anchor item
 * @author Daniel Dreibrodt
 */
public class Anchor extends Item implements ActionListener{
  
  
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
  
  /**
   * Creates a new Anchor from XML.
   * @param xmlcode The XML code from which the Anchor should be created. One line per tag.
   * @param s_ The skin in which the Anchor is used.
   */
  public Anchor(String xmlcode, Skin s_) {
    type = "Anchor";
    s=s_;
    if (xmlcode.indexOf(" points=\"")!=-1) points = XML.getValue(xmlcode,"points");
    updateBezier();
    priority = XML.getIntValue(xmlcode,"priority");
    if (xmlcode.indexOf(" range=\"")!=-1) range = XML.getIntValue(xmlcode,"range");
    if (xmlcode.indexOf(" x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if (xmlcode.indexOf(" y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf(" id=\"")!=-1) id = XML.getValue(xmlcode,"id");
    else id = "Anchor #"+s.getNewId();
    if(xmlcode.indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");  
    created = true;
  }
  /**
   * Creates a new Anchor from user input.
   * @param s_ The Skin in which the Anchor is used.
   */
  public Anchor(Skin s_) {
    type = "Anchor";
    s = s_;
    priority = 0;
    id = "Anchor #"+s.getNewId();
    updateBezier();
    showOptions();
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
      frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
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
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Anchor settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      if(!created) frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID:");
      id_tf = new JTextField();
      JLabel x_l = new JLabel("X:");
      x_tf = new JTextField();
      x_tf.setDocument(new NumbersOnlyDocument());
      JLabel y_l = new JLabel("Y:");
      y_tf = new JTextField();
      y_tf.setDocument(new NumbersOnlyDocument());
      JLabel points_l = new JLabel("Points:");
      points_tf = new JTextField();
      points_tf.setToolTipText("Points defining the Bezier curve followed by the anchor. You don't need to change this parameter if all you want is a punctual anchor.");
      JLabel range_l = new JLabel("Range:");
      range_tf = new JTextField();
      range_tf.setToolTipText("Range of action of the anchor, in pixels.");
      range_tf.setDocument(new NumbersOnlyDocument());
      JLabel priority_l = new JLabel("Priority*:");
      priority_tf = new JTextField();
      priority_tf.setToolTipText("Each anchor has a priority (priority attribute), and the anchor with the highest priority is the winner, which means that when moving its window all the other anchored windows will move too. To break the effect of 2 anchored windows, you need to move the window whose anchor has the lower priority.");
      priority_tf.setDocument(new NumbersOnlyDocument());
      JLabel lefttop_l = new JLabel("Lefttop:");
      String[] lefttop_values = {"lefttop", "leftbottom", "righttop", "rightbottom"};
      lefttop_cb = new JComboBox(lefttop_values);
      lefttop_cb.setToolTipText("Indicate to which corner of the Layout the top-left-hand corner of this anchor is attached, in case of resizing.");
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);
      ok_btn.setPreferredSize(new Dimension(70,25));
      cancel_btn = new JButton("Cancel");
      cancel_btn.addActionListener(this);
      cancel_btn.setPreferredSize(new Dimension(70,25));
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      help_btn.setPreferredSize(new Dimension(70,25));
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_l.setBounds(5,15,75,24);
      id_tf.setBounds(85,15,150,24);
      general.add(priority_l);
      general.add(priority_tf);
      priority_l.setBounds(5,45,75,24);
      priority_tf.setBounds(85,45,150,24);
      general.add(lefttop_l);
      general.add(lefttop_cb);
      lefttop_l.setBounds(5,75,75,24);
      lefttop_cb.setBounds(85,75,150,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));       
      general.setMinimumSize(new Dimension(240,110));
      general.setPreferredSize(new Dimension(240,110));
      general.setMaximumSize(new Dimension(240,110));
      frame.add(general);
      
      JPanel bounds = new JPanel(null);
      bounds.add(x_l);
      bounds.add(x_tf);
      x_l.setBounds(5,15,75,24);
      x_tf.setBounds(85,15,150,24);
      bounds.add(y_l);
      bounds.add(y_tf);
      y_l.setBounds(5,45,75,24);
      y_tf.setBounds(85,45,150,24);
      bounds.add(points_l);
      bounds.add(points_tf);
      points_l.setBounds(5,75,75,24);
      points_tf.setBounds(85,75,150,24);
      bounds.add(range_l);
      bounds.add(range_tf);
      range_l.setBounds(5,105,75,24);
      range_tf.setBounds(85,105,150,24);
      bounds.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Boundaries"));       
      bounds.setMinimumSize(new Dimension(240,140));
      bounds.setPreferredSize(new Dimension(240,140));
      bounds.setMaximumSize(new Dimension(240,140));
      frame.add(bounds);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,345));
      frame.setPreferredSize(new Dimension(250,345));
      frame.setMaximumSize(new Dimension(250,345));
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
      frame.setVisible(false);
      update();
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Anchor"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    else if(e.getSource().equals(cancel_btn)) {
      if(!created) {
        java.util.List<Item> l = s.getParentListOf(id);
        if(l!=null) l.remove(this);
      }
      frame.setVisible(false);
    }
  }
  public String returnCode() {
    String code = "<Anchor";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    code+=" priority=\""+String.valueOf(priority)+"\"";
    if (lefttop!=LEFTTOP_DEFAULT) code+=" lefttop=\""+lefttop+"\"";    
    code+="/>";
    //if (id!=ID_DEFAULT) code+="<!-- id=\""+id+"\" -->";
    return code;
  }
  public void draw(Graphics2D g) {
    draw(g,offsetx,offsety);
  }
  public void draw(Graphics2D g,int x_,int y_) {
    if(!created) return;
    if(selected) {        
      g.setColor(Color.RED);
      for(float f=0f;f<=1f;f=f+0.1f) {
        Point2D.Float p1 = b.getPoint(f);
        Point2D.Float p2 = b.getPoint(f+0.1f);        
        g.drawLine((int)p1.getX()+x+x_,(int)p1.getY()+y+y_,(int)p2.getX()+x+x_,(int)p2.getY()+y+y_);
      }
      for(int i=0;i<xpos.length;i++) {
        g.fillOval(xpos[i]+x+x_-1,ypos[i]+y+y_-1,3,3);
      }
    }
  }
  public boolean contains(int x_, int y_) {
    int h = b.getHeight();
    int w = b.getWidth();
    return (x_>=x+offsetx && x_<=x+offsetx+w && y_>=y+offsety && y_<=y+offsety+h);  
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Anchor: "+id);  
    return node;
  }
}
