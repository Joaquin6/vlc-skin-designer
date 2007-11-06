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
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import vlcskineditor.history.SubBitmapAddEvent;
/**
 * Handles SubBitmap resources
 * @see Bitmap
 * @author Daniel
 */
public class SubBitmap extends Resource implements ActionListener{
  
  public int x, y, width, height;
  final int NBFRAMES_DEFAULT = 1;
  public int nbframes = NBFRAMES_DEFAULT;
  final int FPS_DEFAULT = 0;
  public int fps = FPS_DEFAULT;
  //The image represented by the SubBitmap
  public BufferedImage image; 
  //The parent bitmap
  private Bitmap parent;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, width_tf, height_tf, nbframes_tf, fps_tf;
  JButton file_btn, ok_btn, cancel_btn, help_btn;
  JFileChooser fc;
  
  boolean created = false;
  
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
      frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);    
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
      frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);    
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
      if(!created) frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();
      id_tf.setToolTipText("Identifiant of the font that will be used with controls..");
      JLabel x_l = new JLabel("x:");
      x_tf = new JTextField();
      x_tf.setDocument(new NumbersOnlyDocument(false));
      JLabel y_l = new JLabel("y:");
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
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));       
      general.setMinimumSize(new Dimension(345,50));
      general.setPreferredSize(new Dimension(345,50));
      general.setMaximumSize(new Dimension(345,50));
      frame.add(general);
      
      JPanel bounds = new JPanel(null);
      bounds.add(x_l);
      bounds.add(x_tf);
      bounds.add(y_l);
      bounds.add(y_tf);
      bounds.add(width_l);
      bounds.add(width_tf);
      bounds.add(height_l);
      bounds.add(height_tf);
      x_l.setBounds(5,15,75,24);
      x_tf.setBounds(85,15,150,24);
      y_l.setBounds(5,45,75,24);
      y_tf.setBounds(85,45,150,24);
      width_l.setBounds(5,75,75,24);
      width_tf.setBounds(85,75,150,24);
      height_l.setBounds(5,105,75,24);
      height_tf.setBounds(85,105,150,24);
      bounds.setMinimumSize(new Dimension(345,145));
      bounds.setPreferredSize(new Dimension(345,145));
      bounds.setMaximumSize(new Dimension(345,145));
      bounds.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Position and Dimension Attributes"));       
      frame.add(bounds);
      
      JPanel animation = new JPanel(null);
      animation.add(nbframes_l);
      animation.add(nbframes_tf);
      animation.add(fps_l);
      animation.add(fps_tf);
      nbframes_l.setBounds(5,15,150,24);
      nbframes_tf.setBounds(160,15,150,24);
      fps_l.setBounds(5,45,150,24);
      fps_tf.setBounds(160,45,150,24);
      animation.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Animation Attributes"));       
      animation.setMinimumSize(new Dimension(345,80));
      animation.setPreferredSize(new Dimension(345,80));
      animation.setMaximumSize(new Dimension(345,80));      
      frame.add(animation);
     
      frame.add(ok_btn); 
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(new JLabel("* must be specified."));
      
      frame.setMinimumSize(new Dimension(355,355));     
      frame.setPreferredSize(new Dimension(355,355));
      frame.setMaximumSize(new Dimension(355,355));
      
      frame.pack();
      
    }
    id_tf.setText(id);    
    x_tf.setText(String.valueOf(x));
    y_tf.setText(String.valueOf(y));
    width_tf.setText(String.valueOf(width));
    height_tf.setText(String.valueOf(height));
    nbframes_tf.setText(String.valueOf(nbframes));
    fps_tf.setText(String.valueOf(fps));
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
      if(Integer.parseInt(width_tf.getText())<1) {
        JOptionPane.showMessageDialog(frame,"Width must be greater 0!","Width not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(height_tf.getText())<1) {
        JOptionPane.showMessageDialog(frame,"Height must be greater 0!","Height not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      frame.setVisible(false);
      update();      
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Bitmap"));
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
      frame.setVisible(false);
      if(!created) parent.SubBitmaps.remove(this);
    }
  }
  public String returnCode() {
    String code="<SubBitmap id=\""+id+"\" x=\""+String.valueOf(x)+"\" y=\""+String.valueOf(y)+"\"";
    code+=" width=\""+String.valueOf(width)+"\"  height=\""+String.valueOf(height)+"\"";
    if (nbframes!=NBFRAMES_DEFAULT) code+=" nbframes=\""+String.valueOf(nbframes)+"\"";
    if (fps!=FPS_DEFAULT) code+=" fps=\""+String.valueOf(fps)+"\"";  
    code+="/>\n";
    return code;
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Bitmap: "+id);   
    return top;
  }
}