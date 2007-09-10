/*****************************************************************************
 * Bitmap.java
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

package vlcskineditor.Resources;

import vlcskineditor.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;

/**
 * Handles Bitmap resources
 * @author Daniel
 */
public class Bitmap extends Resource implements ActionListener{
  
  /** Creates a new instance of Bitmap */  
  public String file;
  public final String ALPHACOLOR_DEFAULT = "#000000";
  public String alphacolor = ALPHACOLOR_DEFAULT;
  final int NBFRAMES_DEFAULT = 1;
  public int nbframes = NBFRAMES_DEFAULT;
  public final int FPS_DEFAULT = 0;
  public int fps = FPS_DEFAULT;
  public java.util.List<SubBitmap> SubBitmaps = new ArrayList<SubBitmap>();
  public BufferedImage image;
  
  JFrame frame = null;
  JTextField id_tf, file_tf, alphacolor_tf, nbframes_tf, fps_tf;
  JButton file_btn, alphacolor_btn, ok_btn, help_btn;
  JFileChooser fc;
  
  
  public Bitmap(String xmlcode, Skin s_) {    
    type = "Bitmap";
    s = s_;
    id = XML.getValue(xmlcode,"id"); 
    file = XML.getValue(xmlcode,"file");         
    
    if(xmlcode.indexOf("alphacolor=\"")!=-1) {
      alphacolor = XML.getValue(xmlcode,"alphacolor");        
    }
    
    if(xmlcode.indexOf("nbframes=\"")!=-1) {      
      nbframes = Integer.parseInt(XML.getValue(xmlcode,"nbframes"));        
    }
    
    if(xmlcode.indexOf("fps=\"")!=-1) {
     fps = Integer.parseInt(XML.getValue(xmlcode,"fps"));          
    }
    if (xmlcode.indexOf("\n")!=-1) {
      String[] lines = xmlcode.split("\n");
      for(int i=1;i<lines.length-1;i++) {
        if(lines[i].startsWith("<SubBitmap")) SubBitmaps.add(new SubBitmap(lines[i],s));
      }
    }    
    update();
  }
  public Bitmap(String id_, String file_, String alphacolor_, int nbframes_, int fps_, Skin s_) {
    type="Bitmap";
    s=s_;
    id=id_;
    file=file_;
    alphacolor=alphacolor_;
    nbframes=nbframes_;
    fps=fps_;        
    update();
  }
  public Bitmap(Skin s_, File f_) {
    s = s_;
    type = "Bitmap";
    id = f_.getName().substring(0,f_.getName().lastIndexOf("."));
    file = f_.getPath().replace(s.skinfolder,"");
    s.updateResources();    
    update();
  }
  public Bitmap(Skin s_) {
    s = s_;
    type = "Bitmap";
    id = "Unnamed bitmap #"+s.getNewId();
    file = "";
    s.updateResources();
    s.expandResource(id);
    showOptions();
  }
  public void update() {
    try {
      image = ImageIO.read(new File(s.skinfolder+file)); 
      image = image.getSubimage(0,0,image.getWidth(),image.getHeight()/nbframes); 
      BufferedImage bi = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
      Graphics g2d = bi.createGraphics();      
      int alphargb = Color.decode(alphacolor).getRGB();
      for(int x=0;x<image.getWidth();x++) {
        for(int y=0;y<image.getHeight();y++) {
          if(image.getRGB(x,y)!=alphargb) {
            g2d.setColor(new Color(image.getRGB(x,y)));
            g2d.drawRect(x,y,0,0);
          }
        }        
      }
      image = bi;
      for(int i=0;i<SubBitmaps.size();i++) {
        SubBitmaps.get(i).update(image);
      }
    }
    catch(Exception ex) {      
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null,ex.getMessage()+"\n"+s.skinfolder+file,"Bitmap \""+id+"\" caused an error",JOptionPane.ERROR_MESSAGE); 
      showOptions();
      return;
    }    
  }
  public void update(String id_, String file_, String alphacolor_, int nbframes_, int fps_) {
    file=file_;
    alphacolor=alphacolor_;
    nbframes=nbframes_;
    fps=fps_;    
    if(!id_.equals(id)) {
      id=id_;
      s.updateResources();
      s.expandResource(id);
    }
    update();
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Bitmap settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();
      id_tf.setToolTipText("Identifiant of the bitmap that will be used with controls. Two bitmaps cannot have the same id.");
      JLabel file_l = new JLabel("File*:");
      file_tf = new JTextField();
      file_tf.setToolTipText("Indicates the path and name of the bitmap file used.");
      file_btn = new JButton("Open...");
      file_btn.addActionListener(this);
      JLabel alphacolor_l = new JLabel("Alphacolor:");
      alphacolor_tf = new JTextField();
      alphacolor_tf.setToolTipText("Transparency color of the bitmap in hexadecimal format. If your PNG file specifies a transparency mask, it will be taken into account too.");
      alphacolor_btn = new JButton("Choose...");
      alphacolor_btn.addActionListener(this);
      JLabel nbframes_l = new JLabel("Number of frames:");
      nbframes_tf = new JTextField();
      nbframes_tf.setDocument(new NumbersOnlyDocument());
      nbframes_tf.setToolTipText("This attribute is needed to define animated bitmaps; it is the number of frames (images) contained in your animation. All the different frames are just images laid vertically in the bitmap.");
      JLabel fps_l = new JLabel("Frames per second:");
      fps_tf = new JTextField();
      fps_tf.setDocument(new NumbersOnlyDocument());
      fps_tf.setToolTipText("Only used in animated bitmaps; it is the number of frames (images) per seconds of the animation.");
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_l.setBounds(5,15,75,24);
      id_tf.setBounds(85,15,150,24);
      general.add(file_l);
      general.add(file_tf);
      general.add(file_btn);
      file_l.setBounds(5,45,75,24);
      file_tf.setBounds(85,45,150,24);
      file_btn.setBounds(240,45,100,24);
      general.add(alphacolor_l);
      general.add(alphacolor_tf);
      general.add(alphacolor_btn);
      alphacolor_l.setBounds(5,75,75,24);
      alphacolor_tf.setBounds(85,75,150,24);
      alphacolor_btn.setBounds(240,75,100,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "General Attributes"));       
      general.setMinimumSize(new Dimension(345,110));
      general.setPreferredSize(new Dimension(345,110));
      general.setMaximumSize(new Dimension(345,110));
      frame.add(general);
      
      JPanel animation = new JPanel(null);
      animation.add(nbframes_l);
      animation.add(nbframes_tf);
      animation.add(fps_l);
      animation.add(fps_tf);
      nbframes_l.setBounds(5,15,150,24);
      nbframes_tf.setBounds(160,15,150,24);
      fps_l.setBounds(5,45,150,24);
      fps_tf.setBounds(160,45,150,24);
      animation.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Animation Attributes"));       
      animation.setMinimumSize(new Dimension(345,80));
      animation.setPreferredSize(new Dimension(345,80));
      animation.setMaximumSize(new Dimension(345,80));      
      frame.add(animation);
     
      frame.add(ok_btn); 
      frame.add(help_btn);
      frame.add(new JLabel("Attributes marked with a star must be specified."));
      
      frame.setMinimumSize(new Dimension(355,260));     
      frame.setPreferredSize(new Dimension(355,260));
      frame.setMaximumSize(new Dimension(355,260));
      
      frame.pack();
      
    }
    id_tf.setText(id);
    file_tf.setText(file);
    alphacolor_tf.setText(alphacolor);
    nbframes_tf.setText(String.valueOf(nbframes));
    fps_tf.setText(String.valueOf(fps));
    frame.setVisible(true);
    
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(file_btn)) {
      if(fc==null) {
        fc = new JFileChooser();        
        fc.setFileFilter(new CustomFileFilter(fc,"png","*.png (Portable Network Graphic) inside the XML file's directory",true,s.skinfolder));
        fc.setCurrentDirectory(new File(s.skinfolder));   
        fc.setAcceptAllFileFilterUsed(false);
        
      }
      int returnVal = fc.showOpenDialog(frame);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        file_tf.setText(fc.getSelectedFile().getPath().replace(s.skinfolder,""));
      }
    }
    else if (e.getSource().equals(alphacolor_btn)) {
      Color color = JColorChooser.showDialog(frame,"Choose alphacolor",Color.decode(alphacolor_tf.getText()));
      if (color != null) {
        String hex = "#";
        if(color.getRed()<16) hex+="0";
        hex+=Integer.toHexString(color.getRed()).toUpperCase();
        if(color.getGreen()<16) hex+="0";
        hex+=Integer.toHexString(color.getGreen()).toUpperCase();
        if(color.getBlue()<16) hex+="0";
        hex+=Integer.toHexString(color.getBlue()).toUpperCase();
        alphacolor_tf.setText(hex);
      }
    }    
    else if(e.getSource().equals(ok_btn)) {
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
      if(file_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,"Please choose a file!","File not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(nbframes_tf.getText())<1) {
        JOptionPane.showMessageDialog(frame,"The number of frames cannot be smaller than 1","Nbframes not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }      
      frame.setVisible(false);
      update(id_tf.getText(),file_tf.getText(),alphacolor_tf.getText(),Integer.parseInt(nbframes_tf.getText()),Integer.parseInt(fps_tf.getText()));      
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
  }
  public String returnCode() {
    String code = "<Bitmap id=\""+id+"\" file=\""+file+"\"";
    if (alphacolor!=ALPHACOLOR_DEFAULT) code+=" alphacolor=\""+alphacolor+"\"";
    if (nbframes!=NBFRAMES_DEFAULT) code+=" nbframes=\""+String.valueOf(nbframes)+"\"";
    if (fps!=FPS_DEFAULT) code+=" fps=\""+String.valueOf(fps)+"\"";    
    if(SubBitmaps.size()>0) {
      code+=">\n";
      for (int i=0;i<SubBitmaps.size();i++) {
        code+=SubBitmaps.get(i).returnCode();
      }
      code+="</Bitmap>\n";
    }
    else {
      code+="/>\n";  
    }    
    return code;
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Bitmap: "+id);    
    for(int i=0;i<SubBitmaps.size();i++) {
      node.add(SubBitmaps.get(i).getTreeNode());
    }
    return node;
  }
  public Resource getParentOf(String id_) {
    for(SubBitmap sbmp:SubBitmaps) {
      if(sbmp.id.equals(id_)) return this;
    }
    return null;
  }
}
