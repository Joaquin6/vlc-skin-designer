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

package vlcskineditor.Items;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import vlcskineditor.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * Slider item
 * @author Daniel Dreibrodt
 */
public class Slider extends Item implements ActionListener{
  
  public final String DOWN_DEFAULT = "none";
  public final String OVER_DEFAULT = "none";  
  public final int THICKNESS_DEFAULT = 10;
  public final String VALUE_DEFAULT = "none";
  public final String TOOLTIPTEXT_DEFAULT = "none";
 
  public String up;
  public String down = DOWN_DEFAULT;
  public String over = OVER_DEFAULT;
  public String points;
  public int thickness = THICKNESS_DEFAULT;
  public String value = VALUE_DEFAULT;
  public String tooltiptext = TOOLTIPTEXT_DEFAULT;
  
  public SliderBackground sbg = null;
  
  boolean inPlaytree = false;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, up_tf, down_tf, over_tf, points_tf, thickness_tf, tooltiptext_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb, resize_cb, action_cb, value_cb;
  JCheckBox sbg_chb;
  JButton ok_btn, help_btn, sbg_btn;
  
  /** Creates a new instance of Slider */
  public Slider(String xmlcode, Skin s_) {
    type = "SliderBackground";
    s = s_;
    String[] code = xmlcode.split("\n");
    up = XML.getValue(code[0],"up");
    if(code[0].indexOf("down=\"")!=-1) down = XML.getValue(code[0],"down");
    if(code[0].indexOf("over=\"")!=-1) over = XML.getValue(code[0],"over");
    points = XML.getValue(code[0],"points");
    if(code[0].indexOf("thickness=\"")!=-1) thickness = XML.getIntValue(code[0],"thickness");
    if(code[0].indexOf("value=\"")!=-1) value = XML.getValue(code[0],"value");
    if(code[0].indexOf("tooltiptext=\"")!=-1) tooltiptext = XML.getValue(code[0],"tooltiptext");
    if(code[0].indexOf("x=\"")!=-1) x = XML.getIntValue(code[0],"x");
    if(code[0].indexOf("y=\"")!=-1) y = XML.getIntValue(code[0],"y");
    if(code[0].indexOf("id=\"")!=-1) id = XML.getValue(code[0],"id");
    else id = "Unnamed slider #"+s.getNewId();
    if(code[0].indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(code[0],"lefttop");
    if(code[0].indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(code[0],"rightbottom");
    if(code[0].indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(code[0],"xkeepratio");
    if(code[0].indexOf("ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(code[0],"ykeepratio");
    if(code.length>1) {
      for(int i=0;i<code.length;i++) {
        if(code[i].startsWith("<SliderBackground")) {
          sbg = new SliderBackground(code[i],s);
          break;
        } 
      }        
    }    
  }
  public Slider(String xmlcode, Skin s_, boolean ipt) {
    type = "SliderBackground";
    s = s_;
    String[] code = xmlcode.split("\n");
    inPlaytree = ipt;
    up = XML.getValue(code[0],"up");
    if(code[0].indexOf("down=\"")!=-1) down = XML.getValue(code[0],"down");
    if(code[0].indexOf("over=\"")!=-1) over = XML.getValue(code[0],"over");
    points = XML.getValue(code[0],"points");
    if(code[0].indexOf("thickness=\"")!=-1) thickness = XML.getIntValue(code[0],"thickness");
    //if(code[0].indexOf("value=\"")!=-1) value = XML.getValue(code[0],"value");
    if(code[0].indexOf("tooltiptext=\"")!=-1) tooltiptext = XML.getValue(code[0],"tooltiptext");
    if(code[0].indexOf("x=\"")!=-1) x = XML.getIntValue(code[0],"x");
    if(code[0].indexOf("y=\"")!=-1) y = XML.getIntValue(code[0],"y");
    if(code[0].indexOf("id=\"")!=-1) id = XML.getValue(code[0],"id");
    else id = "Unnamed slider #"+s.getNewId();
    if(code[0].indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(code[0],"lefttop");
    if(code[0].indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(code[0],"rightbottom");
    if(code[0].indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(code[0],"xkeepratio");
    if(code[0].indexOf("ykeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(code[0],"ykeepratio");
    if(code.length>1) {
      for(int i=0;i<code.length;i++) {
        if(code[i].startsWith("<SliderBackground")) {
          sbg = new SliderBackground(code[i],s);
          break;
        } 
      }        
    }        
  }
  public Slider(Skin s_) {
    s = s_;
    up = "none";
    id = "Unnamed slider #"+s.getNewId();
    showOptions();
    s.updateItems();
    s.expandItem(id);
  }
  public Slider(Skin s_, boolean ipt) {
    s = s_;
    up = "none";
    id = "Unnamed slider #"+s.getNewId();
    inPlaytree = ipt;
    //showOptions();
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
    points = points_tf.getText();
    thickness = Integer.parseInt(thickness_tf.getText());
    if(!inPlaytree) value = (String)value_cb.getSelectedItem();
    tooltiptext = tooltiptext_tf.getText();
    
    s.updateItems();   
    s.expandItem(id);
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Slider settings");
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
      
      JLabel up_l = new JLabel("Normal image*:");
      up_tf = new JTextField();
      JLabel over_l = new JLabel("Mouse-over image:");
      over_tf = new JTextField();
      JLabel down_l = new JLabel("Mouse-click image:");
      down_tf = new JTextField();
      JLabel points_l = new JLabel("Points*:");
      points_tf = new JTextField();
      JLabel thickness_l = new JLabel("Thickness:");
      thickness_tf = new JTextField();
      thickness_tf.setToolTipText("Thickness of the slider curve. This attribute is used to determine whether the mouse is over the slider (hence whether a mouse click will have an effect on the cursor position).");
      JLabel value_l = new JLabel("Value*:");      
      if (inPlaytree) {
        String[] values = { "Playtree scrolling" };
        value_cb = new JComboBox(values);
      }
      else {
        String[] values = { "time" , "volume", "equalizer.band(0)", "equalizer.band(1)", "equalizer.band(2)", "equalizer.band(3)", "equalizer.band(4)", "equalizer.band(5)", "equalizer.band(6)",  "equalizer.band(7)",  "equalizer.band(8)",  "equalizer.band(9)"};
        value_cb = new JComboBox(values);
      }      
      JLabel tooltiptext_l = new JLabel("Tooltiptext:");
      tooltiptext_tf = new JTextField();
      
      
      
      sbg_chb = new JCheckBox("Enable Background");
      sbg_chb.addActionListener(this);
      sbg_btn = new JButton("Edit...");
      sbg_btn.addActionListener(this);
      
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
      button.add(points_l);
      button.add(points_tf);
      points_l.setBounds(5,105,75,24);
      points_tf.setBounds(85,105,150,24);
      button.add(thickness_l);
      button.add(thickness_tf);
      thickness_l.setBounds(5,135,75,24);
      thickness_tf.setBounds(85,135,150,24);
      button.add(value_l);
      button.add(value_cb);
      value_l.setBounds(5,165,75,24);
      value_cb.setBounds(85,165,150,24);
      button.add(tooltiptext_l);
      button.add(tooltiptext_tf);
      tooltiptext_l.setBounds(5,195,75,24);
      tooltiptext_tf.setBounds(85,195,150,24);
      button.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Slider Attributes"));
      button.setMinimumSize(new Dimension(240,225));
      button.setPreferredSize(new Dimension(240,225));
      button.setMaximumSize(new Dimension(240,225));
      frame.add(button);
      
      JPanel back = new JPanel(null);
      back.add(sbg_chb);
      back.add(sbg_btn);
      sbg_chb.setBounds(5,15,150,24);
      sbg_btn.setBounds(160,15,75,24);
      back.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Slider background"));
      back.setMinimumSize(new Dimension(240,45));
      back.setPreferredSize(new Dimension(240,45));
      back.setMaximumSize(new Dimension(240,45));
      frame.add(back);
      
      frame.add(ok_btn);
      frame.add(help_btn);      
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,630));
      frame.setPreferredSize(new Dimension(250,630));
      frame.setMaximumSize(new Dimension(250,630));
      
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
    thickness_tf.setText(String.valueOf(thickness));
    points_tf.setText(points);
    if(!inPlaytree) value_cb.setSelectedItem(value);
    tooltiptext_tf.setText(tooltiptext);    
    
    sbg_chb.setSelected(sbg!=null);
    sbg_btn.setEnabled(sbg!=null);         
    
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
      if(points_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,"Please enter valid points!","Points not valid",JOptionPane.INFORMATION_MESSAGE);
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
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Slider"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    else if(e.getSource().equals(sbg_chb)) {
      if(sbg_chb.isSelected()) {
        sbg = new SliderBackground(s);
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
  }
  public String returnCode() {
    String code = "<Slider";
    if (id!=ID_DEFAULT) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    code+=" points=\""+points+"\"";
    if (thickness!=THICKNESS_DEFAULT) code+=" thickness=\""+String.valueOf(thickness)+"\"";
    if (value!=VALUE_DEFAULT) code+=" value=\""+value+"\"";
    if (tooltiptext!=TOOLTIPTEXT_DEFAULT) code+=" tooltiptext=\""+tooltiptext+"\"";
    code+=" up=\""+up+"\"";
    if (down!=DOWN_DEFAULT) code+=" down=\""+down+"\"";
    if (over!=OVER_DEFAULT) code+=" over=\""+over+"\"";
    if (lefttop!=LEFTTOP_DEFAULT) code+=" lefttop=\""+lefttop+"\"";
    if (rightbottom!=RIGHTBOTTOM_DEFAULT) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (help!=HELP_DEFAULT) code+=" help=\""+help+"\"";
    if (sbg==null) {
      code+="/>";
    }
    else {
      code+=">\n"+sbg.returnCode();
      code+="\n</Slider>";
    }
    return code;
  }
  public void draw(Graphics2D g) {
    draw(g,0,0);
  }
  public void draw(Graphics2D g, int x_, int y_) {
    if(s.gvars.parseBoolean(visible)==false) return;
    if(sbg!=null) {
      sbg.draw(g,x+x_,y+y_);
      sbg.setOffset(x+offsetx,y+offsety);
    }    
    if(selected) {        
      String[] pnts = points.split("\\),\\(");
      int[] xpos = new int[pnts.length];
      int[] ypos = new int[pnts.length];
      for(int i=0;i<pnts.length;i++) {
        String pnt = pnts[i];      
        String[] coords = pnt.split(",");        
        xpos[i] = Integer.parseInt(coords[0].replaceAll("\\(",""))+x+x_;
        ypos[i] = Integer.parseInt(coords[1].replaceAll("\\)",""))+y+y_;
      }
      g.setColor(Color.RED);
      g.drawPolyline(xpos,ypos,pnts.length);
    }
  }
  public boolean contains(int x_,int y_) {
    if(sbg!=null) return sbg.contains(x_,y_);
    else {
      String[] pnts = points.split("\\),\\(");
      Path2D.Double path = new Path2D.Double();
      for(int i=0;i<pnts.length;i++) {
        String pnt = pnts[i];      
        String[] coords = pnt.split(",");        
        int xpos = Integer.parseInt(coords[0].replaceAll("\\(",""))+x+x_;
        int ypos = Integer.parseInt(coords[1].replaceAll("\\)",""))+y+y_;
        if(i==0) path.moveTo(xpos,ypos);
        else path.lineTo(xpos,ypos);
      }
      return(path.getBounds().contains(x_,y_));
    }
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Slider: "+id);
    if(sbg!=null) node.add(sbg.getTreeNode());
    return node;
  }
  public Item getItem(String id_) {
    if(id.equals(id_)) return this;
    else if(sbg==null) return null;
    else if(sbg.id.equals(id_)) return sbg;
    else return null;
  }
  public Item getParentOf(String id_) {
   if(sbg!=null) {
     if(sbg.id.equals(id_)) return this;
     else return null;
   }
   else return null;
  }
}
