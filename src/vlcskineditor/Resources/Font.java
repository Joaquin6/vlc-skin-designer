/*****************************************************************************
 * Font.java
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
import javax.swing.border.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
/**
 * Handles font resources
 * @author Daniel Dreibrodt
 */
public class Font extends Resource implements ActionListener{
  
  public String file;
  public final int SIZE_DEFAULT = 12;
  public int size=SIZE_DEFAULT;
  
  JFrame frame = null;
  JTextField id_tf, file_tf, size_tf;
  JButton file_btn, ok_btn, cancel_btn, help_btn;
  JFileChooser fc;
  public java.awt.Font f;
  
  /** Creates a new instance of Font */
  public Font(String xmlcode, Skin s_) {
    type = "Font";
    s = s_;
    id = XML.getValue(xmlcode,"id");
    file = XML.getValue(xmlcode,"file");
    if(xmlcode.indexOf("size=\"")!=-1) {
      size = Integer.parseInt(XML.getValue(xmlcode,"size"));
    }
    try {      
      f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(s.skinfolder+file));
      f = f.deriveFont((float)size);
    }
    catch(Exception e) {
      e.printStackTrace();
      if(file.indexOf(".otf")==-1) {
        JOptionPane.showMessageDialog(frame,"Error while loading font file!\n Please choose another file\n","Font file not valid",JOptionPane.ERROR_MESSAGE);
        f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        showOptions();
      }
      else {
        JOptionPane.showMessageDialog(frame,"You have chosen an OpenType font, VLC will display it correctly but the Skin Editor can not display it.\nYou will see another font instead.","Notice",JOptionPane.INFORMATION_MESSAGE);
        try {      
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
          f = f.deriveFont(12);
        }
        catch(Exception ex) {
          ex.printStackTrace();
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        }
      }      
    }
  }
  public Font(String id_,String file_,int size_, Skin s_) {
    type="Font";
    s=s_;
    id=id_;
    file=file_;
    size=size_;
    try {      
      f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(s.skinfolder+file));
      f = f.deriveFont((float)size);
    }
    catch(Exception e) {
      e.printStackTrace();
      if(file.indexOf(".otf")==-1) {
        JOptionPane.showMessageDialog(frame,"Error while loading font file!\n Please choose another file\n","Font file not valid",JOptionPane.ERROR_MESSAGE);
        f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        showOptions();
      }
      else {
        JOptionPane.showMessageDialog(frame,"You have chosen an OpenType font, VLC will display it correctly but the Skin Editor can not display it.\nIn the Skin Editor you will see instead of the chosen font the default font FreeSans","Notice",JOptionPane.INFORMATION_MESSAGE);
        try {      
          f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(Main.class.getResource("FreeSans.ttf").toURI()));
          f = f.deriveFont(12);
        }
        catch(Exception ex) {
          ex.printStackTrace();
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        }
      }  
    }
  }
  public Font(Skin s_, File f_) {
    s = s_;
    type = "Font";
    String id_t = f_.getName().substring(0,f_.getName().lastIndexOf("."));
    if(s.idExists(id_t)) id_t += "_"+s.getNewId();
    id = id_t;
    file = f_.getPath().replace(s.skinfolder,"");    
    s.updateResources();
    s.expandResource(id);
    try {      
      f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(s.skinfolder+file));
      f = f.deriveFont((float)size);
    }
    catch(Exception e) {
      e.printStackTrace();
      if(file.indexOf(".otf")==-1) {
        JOptionPane.showMessageDialog(frame,"Error while loading font file!\n Please choose another file\n","Font file not valid",JOptionPane.ERROR_MESSAGE);
        f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        showOptions();
      }
      else {
        JOptionPane.showMessageDialog(frame,"You have chosen an OpenType font, VLC will display it correctly but the Skin Editor can not display it.\nIn the Skin Editor you will see instead of the chosen font the default font FreeSans","Notice",JOptionPane.INFORMATION_MESSAGE);
        try {      
          f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(Main.class.getResource("FreeSans.ttf").toURI()));
          f = f.deriveFont(12);
        }
        catch(Exception ex) {
          ex.printStackTrace();
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        }
      }  
    }    
  }
  public Font(Skin s_) {
    s=s_;
    type="Font";
    id = "Unnamed font #"+s.getNewId();
    file="";
    s.updateResources();
    s.expandResource(id);
    showOptions();
  }
  public void update() {
    type="Font";    
    file=file_tf.getText();
    size=Integer.parseInt(size_tf.getText());        
    id=id_tf.getText();
    s.updateResources();
    s.expandResource(id);
    try {      
      f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(s.skinfolder+file));
      f = f.deriveFont((float)size);
    }
    catch(Exception e) {
      e.printStackTrace();
      if(file.indexOf(".otf")==-1) {
        JOptionPane.showMessageDialog(frame,"Error while loading font file!\n Please choose another file\n","Font file not valid",JOptionPane.ERROR_MESSAGE);
        f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        showOptions();
      }
      else {
        JOptionPane.showMessageDialog(frame,"You have chosen an OpenType font, VLC will display it correctly but the Skin Editor can not display it.\nIn the Skin Editor you will see instead of the chosen font the default font FreeSans","Notice",JOptionPane.INFORMATION_MESSAGE);
        try {      
          f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(Main.class.getResource("FreeSans.ttf").toURI()));
          f = f.deriveFont(12);
        }
        catch(Exception ex) {
          ex.printStackTrace();
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        }
      }  
    }    
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Font settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());      
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();
      id_tf.setToolTipText("Identifiant of the font that will be used with controls.");
      JLabel file_l = new JLabel("File*:");
      file_tf = new JTextField();
      file_tf.setToolTipText("This is the file containing a TrueType or OpenType font..");
      file_btn = new JButton("Open...");
      file_btn.addActionListener(this);
      JLabel size_l = new JLabel("Size:");
      size_tf = new JTextField();
      size_tf.setDocument(new NumbersOnlyDocument());
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
      general.add(file_l);
      general.add(file_tf);
      general.add(file_btn);
      file_l.setBounds(5,45,75,24);
      file_tf.setBounds(85,45,150,24);
      file_btn.setBounds(240,45,100,24);
      general.add(size_l);
      general.add(size_tf);
      size_l.setBounds(5,75,75,24);
      size_tf.setBounds(85,75,150,24);
      
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));       
      general.setMinimumSize(new Dimension(345,110));
      general.setPreferredSize(new Dimension(345,110));
      general.setMaximumSize(new Dimension(345,110));
      frame.add(general);         
     
      frame.add(ok_btn); 
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(new JLabel("Attributes marked with a star must be specified."));
      
      frame.setMinimumSize(new Dimension(355,175));     
      frame.setPreferredSize(new Dimension(355,175));
      frame.setMaximumSize(new Dimension(355,175));
      
      frame.pack();      
    }
    id_tf.setText(id);
    file_tf.setText(file);
    size_tf.setText(String.valueOf(size));
    frame.setVisible(true);
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(file_btn)) {
      if(fc==null) {
        fc = new JFileChooser();
        String[] ext = { "ttf" , "otf" };
        fc.setFileFilter(new CustomFileFilter(fc,ext,"*.otf/*.ttf (Open and true type fonts) inside the XML file's directory",true,s.skinfolder));
        fc.setCurrentDirectory(new File(s.skinfolder));   
        fc.setAcceptAllFileFilterUsed(false);        
      }
      int returnVal = fc.showOpenDialog(frame);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        file_tf.setText(fc.getSelectedFile().getPath().replace(s.skinfolder,""));
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
      frame.setVisible(false);
      update();      
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Font"));
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
    }
  }
  public String returnCode() {
    String code="<Font id=\""+id+"\" file=\""+file+"\"";
    if (size!=SIZE_DEFAULT) code+=" size=\""+String.valueOf(size)+"\"";
    code+="/>\n";
    return code;
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Font: "+id);    
    return node;
  }
}
