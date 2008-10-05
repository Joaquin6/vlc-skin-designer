/*****************************************************************************
 * SubBitmap.java
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

package vlcskineditor.resources;

import vlcskineditor.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import org.w3c.dom.Element;
import vlcskineditor.history.SubBitmapAddEvent;
/**
 * Handles SubBitmap resources
 * @see Bitmap
 * @author Daniel
 */
public class SubBitmap extends ImageResource implements ActionListener{
  
  public int x, y, width, height;
  final int NBFRAMES_DEFAULT = 1;
  public int nbframes = NBFRAMES_DEFAULT;
  final int FPS_DEFAULT = 0;
  public int fps = FPS_DEFAULT;  
  //The parent bitmap
  private Bitmap parent;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, width_tf, height_tf, nbframes_tf, fps_tf;
  JButton ok_btn, cancel_btn, help_btn;
  JFileChooser fc;
  
  boolean created = false;
  
  SubBitmapEditWindow sbew = null;
  /**
   * Creates a new SubBitmap from a DOM Element
   * @param e The DOM Element
   * @param s_ The skin in which the SubBitmap is used
   * @param parent_ The parent Bitmap. This is necessary to create the image represented by the SubBitmap.
   */
  
  public SubBitmap(Element e, Skin s_, Bitmap parent_) {
    s = s_;
    parent = parent_;
    type = "Bitmap";
    if(e.hasAttribute("id")) id = e.getAttribute("id");
    if(e.hasAttribute("x")) x = Integer.parseInt(e.getAttribute("x"));
    if(e.hasAttribute("y")) y = Integer.parseInt(e.getAttribute("y"));
    if(e.hasAttribute("width")) width = Integer.parseInt(e.getAttribute("width"));
    if(e.hasAttribute("height")) height = Integer.parseInt(e.getAttribute("height"));
    created = true;
  }
  
  /**
   * Creates a new SubBitmap from XML.
   * @param xmlcode The XML code from which the SubBitmap should be created. One line per tag.
   * @param s_ The skin in which the SubBitmap is used.
   * @param parent_ The parent Bitmap. This is necessary to create the image represented by the SubBitmap.
   */
  public SubBitmap(String xmlcode, Skin s_, Bitmap parent_) {
    type="Bitmap";
    parent=parent_;
    s=s_;
    id = XML.getValue(xmlcode,"id");
    x = XML.getIntValue(xmlcode,"x");
    y = XML.getIntValue(xmlcode,"y");
    width = XML.getIntValue(xmlcode,"width");
    height = XML.getIntValue(xmlcode,"height");
    created = true;
  }  
  /**
   * Creates a new SubBitmap from user input.
   * @param s_ The skin in which the SubBitmap is used.
   * @param parent_ The parent Bitmap.
   */
  public SubBitmap(Skin s_,Bitmap parent_) {
    s=s_;
    parent=parent_;
    type="Bitmap";
    id = "Unnamed subbitmap #"+s.getNewId();
    x = 0;
    y = 0;
    width = 1;
    height = 1;
    showOptions();        
  }
  /**
   * Regenerates the image represented by the SubBitmap object.
   */
  public void updateImage() {
    if(parent.image != null) image = parent.image.getSubimage(x,y,width,height);
  }  
  public void update() {    
    if(!created) {      
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      width=Integer.parseInt(width_tf.getText());
      height=Integer.parseInt(height_tf.getText());
      nbframes=Integer.parseInt(nbframes_tf.getText());
      fps=Integer.parseInt(fps_tf.getText());   
      //frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);    
      updateImage();
      s.updateResources();
      s.expandResource(id);
      SubBitmapAddEvent sae = new SubBitmapAddEvent(parent,this);
      s.m.hist.addEvent(sae);
      created = true;
    }
    else {
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      width=Integer.parseInt(width_tf.getText());
      height=Integer.parseInt(height_tf.getText());
      nbframes=Integer.parseInt(nbframes_tf.getText());
      fps=Integer.parseInt(fps_tf.getText());   
      //frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);    
      updateImage();
      s.updateResources();
      s.expandResource(id);
    }    
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Subbitmap settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      if(!created) frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();
      id_tf.setToolTipText("Identifiant of the font that will be used with controls..");
      JLabel x_l = new JLabel("X:");
      x_tf = new JTextField();
      x_tf.setDocument(new NumbersOnlyDocument(false));
      JLabel y_l = new JLabel("Y:");
      y_tf = new JTextField();
      y_tf.setDocument(new NumbersOnlyDocument(false));
      JLabel width_l = new JLabel("Width:");
      width_tf = new JTextField();
      width_tf.setDocument(new NumbersOnlyDocument(false));
      JLabel height_l = new JLabel("Height:");
      height_tf = new JTextField();
      height_tf.setDocument(new NumbersOnlyDocument(false));
      JLabel nbframes_l = new JLabel("Number of frames:");
      nbframes_tf = new JTextField();
      nbframes_tf.setDocument(new NumbersOnlyDocument(false));
      nbframes_tf.setToolTipText("This attribute is needed to define animated bitmaps; it is the number of frames (images) contained in your animation. All the different frames are just images laid vertically in the bitmap.");
      JLabel fps_l = new JLabel("Frames per second:");
      fps_tf = new JTextField();
      fps_tf.setDocument(new NumbersOnlyDocument(false));
      fps_tf.setToolTipText("Only used in animated bitmaps; it is the number of frames (images) per seconds of the animation.");
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);      
      cancel_btn = new JButton("Cancel");
      cancel_btn.addActionListener(this);      
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      JLabel attr_l = new JLabel("* Attributes marked with a star must be specified.");
      
      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, width_l, height_l, nbframes_l, fps_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = 200;
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));
      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);
      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, id_l);
      frame.add(general);
      
      JPanel bounds = new JPanel(null);
      bounds.add(x_l);
      bounds.add(x_tf);
      x_tf.setPreferredSize(new Dimension(tf_wd,x_tf.getPreferredSize().height));
      bounds.add(y_l);
      bounds.add(y_tf);
      y_tf.setPreferredSize(new Dimension(tf_wd,y_tf.getPreferredSize().height));
      bounds.add(width_l);
      bounds.add(width_tf);
      width_tf.setPreferredSize(new Dimension(tf_wd,width_tf.getPreferredSize().height));
      bounds.add(height_l);
      bounds.add(height_tf);
      height_tf.setPreferredSize(new Dimension(tf_wd,height_tf.getPreferredSize().height));
      bounds.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Position and Dimension Attributes"));       
      SpringLayout bounds_layout = new SpringLayout();
      bounds.setLayout(bounds_layout);
      bounds_layout.putConstraint(SpringLayout.NORTH, x_l, 5, SpringLayout.NORTH, bounds);
      bounds_layout.putConstraint(SpringLayout.WEST, x_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, x_tf, 0, SpringLayout.VERTICAL_CENTER, x_l);
      bounds_layout.putConstraint(SpringLayout.WEST, x_tf, tf_dx, SpringLayout.WEST, bounds);
      bounds_layout.putConstraint(SpringLayout.EAST, bounds, 5, SpringLayout.EAST, x_tf);
      
      bounds_layout.putConstraint(SpringLayout.NORTH, y_l, 10, SpringLayout.SOUTH, x_l);
      bounds_layout.putConstraint(SpringLayout.WEST, y_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, y_tf, 0, SpringLayout.VERTICAL_CENTER, y_l);
      bounds_layout.putConstraint(SpringLayout.WEST, y_tf, tf_dx, SpringLayout.WEST, bounds);
      bounds_layout.putConstraint(SpringLayout.EAST, y_tf, 0, SpringLayout.EAST, x_tf);
      
      bounds_layout.putConstraint(SpringLayout.NORTH, width_l, 10, SpringLayout.SOUTH, y_l);
      bounds_layout.putConstraint(SpringLayout.WEST, width_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, width_tf, 0, SpringLayout.VERTICAL_CENTER, width_l);
      bounds_layout.putConstraint(SpringLayout.WEST, width_tf, tf_dx, SpringLayout.WEST, bounds);
      bounds_layout.putConstraint(SpringLayout.EAST, width_tf, 0, SpringLayout.EAST, x_tf);
      
      bounds_layout.putConstraint(SpringLayout.NORTH, height_l, 10, SpringLayout.SOUTH, width_l);
      bounds_layout.putConstraint(SpringLayout.WEST, height_l, 5, SpringLayout.WEST, bounds);
      
      bounds_layout.putConstraint(SpringLayout.VERTICAL_CENTER, height_tf, 0, SpringLayout.VERTICAL_CENTER, height_l);
      bounds_layout.putConstraint(SpringLayout.WEST, height_tf, tf_dx, SpringLayout.WEST, bounds);
      bounds_layout.putConstraint(SpringLayout.EAST, height_tf, 0, SpringLayout.EAST, x_tf);
      
      bounds_layout.putConstraint(SpringLayout.SOUTH, bounds, 10, SpringLayout.SOUTH, height_l);
      frame.add(bounds);
      
      JPanel animation = new JPanel(null);
      animation.add(nbframes_l);
      animation.add(nbframes_tf);
      animation.add(fps_l);
      animation.add(fps_tf);     
      animation.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Animation Attributes"));
      SpringLayout ani_layout = new SpringLayout();
      animation.setLayout(ani_layout);      
      nbframes_tf.setPreferredSize(new Dimension(tf_wd,nbframes_tf.getPreferredSize().height));
      ani_layout.putConstraint(SpringLayout.NORTH, nbframes_l, 5, SpringLayout.NORTH, animation);
      ani_layout.putConstraint(SpringLayout.WEST, nbframes_l, 5, SpringLayout.WEST, animation);
      
      ani_layout.putConstraint(SpringLayout.VERTICAL_CENTER, nbframes_tf, 0, SpringLayout.VERTICAL_CENTER, nbframes_l);
      ani_layout.putConstraint(SpringLayout.WEST, nbframes_tf, tf_dx, SpringLayout.WEST, animation);
      ani_layout.putConstraint(SpringLayout.EAST, animation, 5, SpringLayout.EAST, nbframes_tf);
      
      ani_layout.putConstraint(SpringLayout.NORTH, fps_l, 10, SpringLayout.SOUTH, nbframes_l);
      ani_layout.putConstraint(SpringLayout.WEST, fps_l, 5, SpringLayout.WEST, animation);
      
      ani_layout.putConstraint(SpringLayout.VERTICAL_CENTER, fps_tf, 0, SpringLayout.VERTICAL_CENTER, fps_l);
      ani_layout.putConstraint(SpringLayout.WEST, fps_tf, tf_dx, SpringLayout.WEST, animation);
      ani_layout.putConstraint(SpringLayout.EAST, fps_tf, 0, SpringLayout.EAST, nbframes_tf);
      
      ani_layout.putConstraint(SpringLayout.SOUTH, animation, 10, SpringLayout.SOUTH, fps_l);     
      frame.add(animation);
     
      frame.add(ok_btn); 
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(attr_l);
      
      SpringLayout layout = new SpringLayout();
      frame.setLayout(layout);
      
      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, bounds, 5, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, bounds, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, animation, 5, SpringLayout.SOUTH, bounds);
      layout.putConstraint(SpringLayout.WEST, animation, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, attr_l, 5, SpringLayout.SOUTH, animation);
      layout.putConstraint(SpringLayout.WEST, attr_l, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.EAST, attr_l, 5, SpringLayout.EAST, frame.getContentPane());
      
      
      layout.putConstraint(SpringLayout.NORTH, ok_btn, 5, SpringLayout.SOUTH, attr_l);
      layout.putConstraint(SpringLayout.NORTH, cancel_btn, 5, SpringLayout.SOUTH, attr_l);
      layout.putConstraint(SpringLayout.NORTH, help_btn, 5, SpringLayout.SOUTH, attr_l);
      
      layout.putConstraint(SpringLayout.WEST, ok_btn, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, cancel_btn, 5, SpringLayout.EAST, ok_btn);
      layout.putConstraint(SpringLayout.WEST, help_btn, 5, SpringLayout.EAST, cancel_btn);
      
      layout.putConstraint(SpringLayout.SOUTH, frame.getContentPane(), 5, SpringLayout.SOUTH, ok_btn);
      layout.putConstraint(SpringLayout.EAST, frame.getContentPane(), 5, SpringLayout.EAST, general);
      
      frame.pack();      
      
      frame.getRootPane().setDefaultButton(ok_btn);
    }
    id_tf.setText(id);    
    x_tf.setText(String.valueOf(x));
    y_tf.setText(String.valueOf(y));
    width_tf.setText(String.valueOf(width));
    height_tf.setText(String.valueOf(height));
    nbframes_tf.setText(String.valueOf(nbframes));
    fps_tf.setText(String.valueOf(fps));
    frame.setVisible(true);    
    sbew = new SubBitmapEditWindow(parent,this);    
    x_tf.addKeyListener(sbew);
    y_tf.addKeyListener(sbew);
    width_tf.addKeyListener(sbew);
    height_tf.addKeyListener(sbew);
    frame.addWindowListener(sbew);
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
      if(Integer.parseInt(height_tf.getText())<1) {
        JOptionPane.showMessageDialog(frame,"height must be greater 0!","height not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(height_tf.getText())<1) {
        JOptionPane.showMessageDialog(frame,"Height must be greater 0!","Height not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(x_tf.getText())+Integer.parseInt(width_tf.getText())>parent.image.getWidth()) {
        JOptionPane.showMessageDialog(frame,"Specified dimensions are outside the parent bitmap!","Dimension not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(y_tf.getText())+Integer.parseInt(height_tf.getText())>parent.image.getHeight()) {
        JOptionPane.showMessageDialog(frame,"Specified dimensions are outside the parent bitmap!","Dimension not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      update();      
      frame.setVisible(false);        
      destroyEditWindow();
      frame.dispose();
      frame = null;      
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/res-subbitmap.html");
    }
    else if(e.getSource().equals(cancel_btn)) {
      frame.setVisible(false);      
      destroyEditWindow();
      frame.dispose();
      frame = null;
      if(!created) parent.SubBitmaps.remove(this);      
    }
  }
  public void destroyEditWindow() {
    sbew.frame.setVisible(false);
    x_tf.removeKeyListener(sbew);
    y_tf.removeKeyListener(sbew);
    height_tf.removeKeyListener(sbew);
    height_tf.removeKeyListener(sbew);
    frame.removeWindowListener(sbew);
    sbew.frame.dispose();
    sbew.frame = null;
    sbew = null;
  }
  public String returnCode(String indent) {
    String code=indent+"<SubBitmap id=\""+id+"\" x=\""+String.valueOf(x)+"\" y=\""+String.valueOf(y)+"\"";
    code+=" height=\""+String.valueOf(height)+"\" width=\""+String.valueOf(width)+"\"";    
    if (nbframes!=NBFRAMES_DEFAULT) code+=" nbframes=\""+String.valueOf(nbframes)+"\"";
    if (fps!=FPS_DEFAULT) code+=" fps=\""+String.valueOf(fps)+"\"";  
    code+="/>\n";
    return code;
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Bitmap: "+id);   
    return top;
  }
  public Bitmap getParentBitmap() {
    return parent;
  }
}
