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
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import vlcskineditor.resources.ImageResource;

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
  
  Bezier b;
  int[] xpos,ypos;

  private Point2D.Float sliderPos;
  private Point2D.Float[] bezierPoints;
  
  /** Creates a new instance of Slider
   * @param xmlcode The XML code
   * @param s_ The parent skin
   */
  public Slider(String xmlcode, Skin s_) {
    type = "Slider";
    s = s_;
    String[] code = xmlcode.split("\n");
    up = XML.getValue(code[0],"up");
    up_res = s.getImageResource(up);
    if(code[0].indexOf(" down=\"")!=-1) {
      down = XML.getValue(code[0],"down");
      down_res = s.getImageResource(down);
    }
    if(code[0].indexOf(" over=\"")!=-1) {
      over = XML.getValue(code[0],"over");
      over_res = s.getImageResource(over);
    }
    points = XML.getValue(code[0],"points");  
    updateBezier();
    if(code[0].indexOf(" thickness=\"")!=-1) thickness = XML.getIntValue(code[0],"thickness");
    if(code[0].indexOf(" value=\"")!=-1) value = XML.getValue(code[0],"value");
    if(code[0].indexOf(" tooltiptext=\"")!=-1) tooltiptext = XML.getValue(code[0],"tooltiptext");
    if(code[0].indexOf(" x=\"")!=-1) x = XML.getIntValue(code[0],"x");
    if(code[0].indexOf(" y=\"")!=-1) y = XML.getIntValue(code[0],"y");
    if(code[0].indexOf(" id=\"")!=-1) id = XML.getValue(code[0],"id");
    else id = "Unnamed slider #"+s.getNewId();
    if(code[0].indexOf(" lefttop=\"")!=-1) lefttop = XML.getValue(code[0],"lefttop");
    if(code[0].indexOf(" rightbottom=\"")!=-1) rightbottom = XML.getValue(code[0],"rightbottom");
    if(code[0].indexOf(" xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(code[0],"xkeepratio");
    if(code[0].indexOf(" ykeepratio=\"")!=-1) ykeepratio = XML.getBoolValue(code[0],"ykeepratio");
    if(xmlcode.indexOf(" visible=\"")!=-1) visible = XML.getValue(xmlcode,"visible");
    if(code.length>1) {
      for(int i=0;i<code.length;i++) {
        code[i] = code[i].trim();
        if (code[i].startsWith("<!--")) {
          while(code[i].indexOf("-->")==-1) {
            i++;
          }
        }
        else if(code[i].startsWith("<SliderBackground")) {
          sbg = new SliderBackground(code[i],s);
          break;
        } 
      }        
    }        
    created=true;
  }
  public Slider(String xmlcode, Skin s_, boolean ipt) {
    type = "Slider";
    s = s_;
    String[] code = xmlcode.split("\n");
    inPlaytree = ipt;
    up = XML.getValue(code[0],"up");
    up_res = s.getImageResource(up);
    if(code[0].indexOf("down=\"")!=-1) down = XML.getValue(code[0],"down");
    if(code[0].indexOf("over=\"")!=-1) over = XML.getValue(code[0],"over");
    points = XML.getValue(code[0],"points");
    updateBezier();
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
    created=true;
  }
  public Slider(Skin s_) {
    type = "Slider";
    s = s_;
    up = "none";
    id = "Unnamed slider #"+s.getNewId();
    updateBezier();
    showOptions();    
    s.updateItems();
    s.expandItem(id);
  }
  public Slider(Skin s_, boolean ipt) {
    type = "Slider";
    s = s_;
    up = "none";
    id = "Unnamed slider #"+s.getNewId();
    inPlaytree = ipt;
    created=true;
  }
  public void updateBezier() {
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
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      
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
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Slider settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      if(!created) frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
      visible_btn = new JButton("",s.m.help_icon);
      visible_btn.addActionListener(this);
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
        String[] values = { "time" , "volume", "equalizer.preamp", "equalizer.band(0)", "equalizer.band(1)", "equalizer.band(2)", "equalizer.band(3)", "equalizer.band(4)", "equalizer.band(5)", "equalizer.band(6)",  "equalizer.band(7)",  "equalizer.band(8)",  "equalizer.band(9)"};
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
      general.add(x_l);
      general.add(x_tf);
      x_l.setBounds(5,45,75,24);
      x_tf.setBounds(85,45,150,24);
      general.add(y_l);
      general.add(y_tf);
      y_l.setBounds(5,75,75,24);
      y_tf.setBounds(85,75,150,24);      
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
      general.add(visible_btn);
      visible_l.setBounds(5,225,75,24);
      visible_tf.setBounds(85,225,120,24);
      visible_btn.setBounds(210,225,24,24);
      general.add(help_l);
      general.add(help_tf);
      help_l.setBounds(5,255,75,24);
      help_tf.setBounds(85,255,150,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));
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
      button.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Slider Attributes"));
      button.setMinimumSize(new Dimension(240,225));
      button.setPreferredSize(new Dimension(240,225));
      button.setMaximumSize(new Dimension(240,225));
      frame.add(button);
      
      JPanel back = new JPanel(null);
      back.add(sbg_chb);
      back.add(sbg_btn);
      sbg_chb.setBounds(5,15,150,24);
      sbg_btn.setBounds(160,15,75,24);
      back.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Slider background"));
      back.setMinimumSize(new Dimension(240,45));
      back.setPreferredSize(new Dimension(240,45));
      back.setMaximumSize(new Dimension(240,45));
      frame.add(back);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,630));
      frame.setPreferredSize(new Dimension(250,630));
      frame.setMaximumSize(new Dimension(250,630));
      
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
      up_res = s.getImageResource(up_tf.getText());
      if(up_res==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+up_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        up_res = s.getImageResource(up);
        return;
      }
      over_res = s.getImageResource(over_tf.getText());
      if(!over_tf.getText().equals("none") && over_res==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+over_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        over_res = s.getImageResource(over);
        return;
      }
      down_res = s.getImageResource(down_tf.getText());
      if(!down_tf.getText().equals("none") && down_res==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+down_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
        down_res = s.getImageResource(down);
        return;
      }
      if(points_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,"Please enter valid points!","Points not valid",JOptionPane.INFORMATION_MESSAGE);
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
  public String returnCode(String indent) {
    String code = indent+"<Slider";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    code+=" points=\""+points+"\"";
    if (thickness!=THICKNESS_DEFAULT) code+=" thickness=\""+String.valueOf(thickness)+"\"";
    if (!value.equals(VALUE_DEFAULT)) code+=" value=\""+value+"\"";
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
  public void draw(Graphics2D g, int z) {
    draw(g,0,0,z);
  }
  public void draw(Graphics2D g, int x_, int y_, int z) {    
    if(!created) return;
    offsetx=x_;
    offsety=y_;
    //boolean vis = s.gvars.parseBoolean(visible);
    if(sbg!=null && vis) {
      sbg.draw(g,x+x_,y+y_,z);
      sbg.setOffset(x+offsetx,y+offsety);
    }    
    java.awt.image.BufferedImage si = up_res.image;
    if(vis && si!=null) g.drawImage(si,(int)(sliderPos.getX()+x+x_-si.getWidth()/2)*z,(int)(sliderPos.getY()+y+y_-si.getHeight()/2)*z,si.getWidth()*z,si.getHeight()*z,null);
    if(selected) {
      g.setColor(Color.RED);
      /*for(float f=0f;f<=1f;f=f+0.1f) {
        Point2D.Float p1 = b.getPoint(f);
        Point2D.Float p2 = b.getPoint(f+0.1f);        
        g.drawLine((int)(p1.getX()+x+x_)*z,(int)(p1.getY()+y+y_)*z,(int)(p2.getX()+x+x_)*z,(int)(p2.getY()+y+y_)*z);
      }*/
      for(int i=0;i<=10;i++) {
        Point2D.Float p1 = bezierPoints[i];
        Point2D.Float p2 = bezierPoints[i+1];
        g.drawLine((int)(p1.getX()+x+x_)*z,(int)(p1.getY()+y+y_)*z,(int)(p2.getX()+x+x_)*z,(int)(p2.getY()+y+y_)*z);
      }
      g.setColor(Color.ORANGE);
      for(int i=0;i<xpos.length;i++) {
        g.fillOval((xpos[i]+x+x_-1)*z,(ypos[i]+y+y_-1)*z,3,3);
      }
    }
  }
  @Override
  public boolean contains(int x_,int y_) {    
    int h = b.getHeight();
    int w = b.getWidth();
    return (x_>=x+offsetx && x_<=x+offsetx+w && y_>=y+offsety && y_<=y+offsety+h);   
  }
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
}
