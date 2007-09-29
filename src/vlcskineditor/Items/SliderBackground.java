/*****************************************************************************
 * SliderBackground.java
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

import vlcskineditor.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;

/**
 * SliderBackground item
 * @author Daniel Dreibrodt
 */
public class SliderBackground extends Item implements ActionListener{
  
  public final int NBHORIZ_DEFAULT = 1;
  public final int NBVERT_DEFAULT = 1;
  public final int PADHORIZ_DEFAULT = 0;
  public final int PADVERT_DEFAULT = 0;
  
  public String image;
  public int nbhoriz = NBHORIZ_DEFAULT;
  public int nbvert = NBVERT_DEFAULT;
  public int padhoriz = PADHORIZ_DEFAULT;
  public int padvert = PADVERT_DEFAULT;
  
  JFrame frame;
  JTextField id_tf, image_tf, nbhoriz_tf, nbvert_tf, padhoriz_tf, padvert_tf;
  JButton gen_btn, ok_btn, help_btn;
  BufferedImage bi = null;
  String bitmap_str = "";
  
    
  /** Creates a new instance of SliderBackground */
  public SliderBackground(String xmlcode, Skin s_) {
    s = s_;
    image = XML.getValue(xmlcode,"image");
    if(xmlcode.indexOf("nbhoriz=\"")!=-1) nbhoriz = XML.getIntValue(xmlcode,"nbhoriz");
    if(xmlcode.indexOf("nbvert=\"")!=-1) nbvert = XML.getIntValue(xmlcode,"nbvert");
    if(xmlcode.indexOf("padhoriz=\"")!=-1) padhoriz = XML.getIntValue(xmlcode,"padhoriz");
    if(xmlcode.indexOf("padvert=\"")!=-1) padvert = XML.getIntValue(xmlcode,"padvert");   
    if(xmlcode.indexOf("id=\"")!=-1) id = XML.getValue(xmlcode,"id");  
    else id = "Unnamed slider background #"+s.getNewId();
  }
  public SliderBackground(Skin s_) {
    s = s_;
    image = "none";
    id = "Unnamed slider background #"+s.getNewId();
    showOptions();
  }
  public void update() {
    id = id_tf.getText();
    image = image_tf.getText();
    nbhoriz = Integer.parseInt(nbhoriz_tf.getText());
    nbvert = Integer.parseInt(nbvert_tf.getText());
    padhoriz = Integer.parseInt(padhoriz_tf.getText());
    padvert = Integer.parseInt(padvert_tf.getText());       
    frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
  }
  public void prepareImage() {    
    bitmap_str = s.getBitmapImage(image).toString();
    bi = (BufferedImage)s.getBitmapImage(image);    
    bi = bi.getSubimage(0,0,(bi.getWidth()-padhoriz*(nbhoriz-1))/nbhoriz,(bi.getHeight()-padvert*(nbvert-1))/nbvert);
  }
  public void showOptions() {
    //TODO: Implement VLCSliderBGGen
    if(frame==null) {
      frame = new JFrame("Slider background settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();      
      gen_btn = new JButton("Open slider background wizard...");
      gen_btn.addActionListener(this);
      JLabel image_l = new JLabel("Image*:");
      image_tf = new JTextField();
      JLabel nbhoriz_l =  new JLabel("Horiz. Sub-images:");
      nbhoriz_tf = new JTextField();
      nbhoriz_tf.setDocument(new NumbersOnlyDocument());
      JLabel nbvert_l =  new JLabel("Vert. Sub-images:");
      nbvert_tf = new JTextField();
      nbvert_tf.setDocument(new NumbersOnlyDocument());
      JLabel padhoriz_l =  new JLabel("Horiz. padding:");
      padhoriz_tf = new JTextField();
      padhoriz_tf.setDocument(new NumbersOnlyDocument());
      JLabel padvert_l =  new JLabel("Vert. padding:");
      padvert_tf = new JTextField();
      padvert_tf.setDocument(new NumbersOnlyDocument());
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_l.setBounds(5,15,75,24);
      id_tf.setBounds(85,15,150,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));
      general.setMinimumSize(new Dimension(240,45));
      general.setPreferredSize(new Dimension(240,45));
      general.setMaximumSize(new Dimension(240,45));
      frame.add(general);
      
      frame.add(gen_btn);
      
      JPanel specific = new JPanel(null);
      specific.add(image_l);
      specific.add(image_tf);
      image_l.setBounds(5,15,75,24);
      image_tf.setBounds(85,15,150,24);
      specific.add(nbhoriz_l);
      specific.add(nbhoriz_tf);
      nbhoriz_l.setBounds(5,45,75,24);
      nbhoriz_tf.setBounds(85,45,150,24);
      specific.add(nbvert_l);
      specific.add(nbvert_tf);
      nbvert_l.setBounds(5,75,75,24);
      nbvert_tf.setBounds(85,75,150,24);      
      specific.add(padhoriz_l);
      specific.add(padhoriz_tf);
      padhoriz_l.setBounds(5,105,75,24);
      padhoriz_tf.setBounds(85,105,150,24);
      specific.add(padvert_l);
      specific.add(padvert_tf);
      padvert_l.setBounds(5,135,75,24);
      padvert_tf.setBounds(85,135,150,24);
      specific.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Background Attributes"));
      specific.setMinimumSize(new Dimension(240,165));
      specific.setPreferredSize(new Dimension(240,165));
      specific.setMaximumSize(new Dimension(240,165));
      frame.add(specific);
      
      frame.add(ok_btn);
      frame.add(help_btn);
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,320));
      frame.setPreferredSize(new Dimension(250,320));
      frame.setMaximumSize(new Dimension(250,320));
      
      frame.pack();
      
    }
    id_tf.setText(id);
    image_tf.setText(image);
    nbhoriz_tf.setText(String.valueOf(nbhoriz));
    nbvert_tf.setText(String.valueOf(nbvert));
    padhoriz_tf.setText(String.valueOf(padhoriz));
    padvert_tf.setText(String.valueOf(padvert));
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
      if(s.getResource(image_tf.getText())==null) {
        JOptionPane.showMessageDialog(frame,"The bitmap \""+image_tf.getText()+"\" does not exist!","Image not valid",JOptionPane.INFORMATION_MESSAGE);
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
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#SliderBackground"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
  }
  public String returnCode() {
    String code = "<SliderBackground";
    if (id!=ID_DEFAULT) code+=" id=\""+id+"\"";
    code+=" image=\""+image+"\"";
    if (nbhoriz!=NBHORIZ_DEFAULT) code+=" nbhoriz=\""+String.valueOf(nbhoriz)+"\"";
    if (nbvert!=NBVERT_DEFAULT) code+=" nbvert=\""+String.valueOf(nbvert)+"\"";
    if (padhoriz!=PADHORIZ_DEFAULT) code+=" padhoriz=\""+String.valueOf(padhoriz)+"\"";
    if (padvert!=PADVERT_DEFAULT) code+=" padvert=\""+String.valueOf(padvert)+"\"";
    code+="/>";
    return code;
  }
  public void draw(Graphics2D g) {
    draw(g,0,0);
  }
  public void draw(Graphics2D g, int x_, int y_) {
    if(bi==null || bitmap_str!=s.getBitmapImage(image).toString()) prepareImage();
    g.drawImage(bi,x+x_,y+y_,null);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect(x+x_,y+y_,bi.getWidth()-1,bi.getHeight()-1);
    }
  }
  public boolean contains(int x_,int y_) {
    if(bi==null || bitmap_str!=s.getBitmapImage(image).toString()) prepareImage();
    return (x_>=x+offsetx && x_<=x+bi.getWidth()+offsetx && y_>=y+offsety && y_<=y+bi.getHeight()+offsety);
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("SliderBackground: "+id);       
    return node;
  }
  
}
