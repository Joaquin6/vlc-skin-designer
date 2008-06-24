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

package vlcskineditor.resources;

import vlcskineditor.*;
import vlcskineditor.history.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.w3c.dom.Element;
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
  /**
   * The font represented by this Font object as a Java AWT Font.
   */
  public java.awt.Font f;
  
  public Font(Element e, Skin s_) {
    type = "Font";
    s = s_;
    if(e.hasAttribute("id")) id = e.getAttribute("id");
    if(e.hasAttribute("file")) file = e.getAttribute("file");
    if(e.hasAttribute("size")) size = Integer.parseInt(e.getAttribute("size"));
  }
  
  /**
   * Creates a Font from XML.
   * @param xmlcode The XML code from which the Font should be created. One line per tag.
   * @param s_ The Skin in which the Font is used.
   */
  public Font(String xmlcode, Skin s_) {
    type = "Font";
    s = s_;
    id = XML.getValue(xmlcode,"id");
    file = XML.getValue(xmlcode,"file");
    if(xmlcode.indexOf("size=\"")!=-1) {
      size = Integer.parseInt(XML.getValue(xmlcode,"size"));
    }
    updateFont();
  }
  /**
   * Creates a new Font from the given attributes.
   * @param id_ The ID of the Font.
   * @param file_ The relative path to the font file.
   * @param size_ The Font's size.
   * @param s_ The Skin in which the Font is used.
   */
  public Font(String id_,String file_,int size_, Skin s_) {
    type="Font";
    s=s_;
    id=id_;
    file=file_;
    size=size_;
    updateFont();
  }
  /**
   * Creates a new Font from a given file.
   * @param s_ The skin in which the Font is used.
   * @param f_ The font file. TrueType or OpenType. Notice that only OpenType fonts in a TrueType container
   * can be displayed by the Skin Editor. VLC can display both.
   */
  public Font(Skin s_, File f_) {
    s = s_;
    type = "Font";
    String id_t = f_.getName().substring(0,f_.getName().lastIndexOf("."));
    if(s.idExists(id_t)) id_t += "_"+s.getNewId();
    id = id_t;
    file = f_.getPath().replace(s.skinfolder,"");    
    s.updateResources();
    s.expandResource(id);
    updateFont();
  }
  /**
   * Creates a new Font from user input.
   * @param s_ The skin in which the Font is used.
   */
  public Font(Skin s_) {
    s=s_;
    type="Font";
    id = "Unnamed font #"+s.getNewId();
    file="";
    s.updateResources();
    s.expandResource(id);
    showOptions();
  }
  public Integer updateFont() {
    try {      
      f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(s.skinfolder+file));
      f = f.deriveFont((float)size);      
    }
    catch(Exception e) {      
      if(file.indexOf(".otf")==-1) {
        //JOptionPane.showMessageDialog(frame,"Error while loading font file!\n Please choose another file\n","Font file not valid",JOptionPane.ERROR_MESSAGE);
        f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
        //showOptions();
        return 0;
      }
      else {
        //JOptionPane.showMessageDialog(frame,"You have chosen an OpenType font, VLC will display it correctly but the Skin Editor can not display it.\nYou will see another font instead.","Notice",JOptionPane.INFORMATION_MESSAGE);        
        try {      
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);
          f = f.deriveFont(12);
        }
        catch(Exception ex) {          
          ex.printStackTrace();
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);          
        }
        return 2;
      }      
    }
    return 1;
  }
  public void update() {
    FontEditEvent fe = new FontEditEvent(this);
    type="Font";    
    file=file_tf.getText();
    size=Integer.parseInt(size_tf.getText());        
    id=id_tf.getText();
    s.updateResources();
    s.expandResource(id);    
    fe.setNew();
    s.m.hist.addEvent(fe);
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Font settings");
      frame.setResizable(false);
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
      cancel_btn = new JButton("Cancel");
      cancel_btn.addActionListener(this);      
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);      
      JLabel attr_l = new JLabel("* Attributes marked with a star must be specified.");
      
      //Textfield distance to WEST border of container
      Component[] labels = { id_l, file_l, size_l };
      int tf_dx = Helper.maxWidth(labels)+10;               
      //Maximal textfield width
      int tf_wd = 200;
      //Button width
      int btn_wd = file_btn.getPreferredSize().width;
      
      JPanel general = new JPanel(null);      
      general.add(id_l);
      general.add(id_tf);     
      general.add(file_l);
      general.add(file_tf);
      file_tf.setPreferredSize(new Dimension(tf_wd-btn_wd,file_tf.getPreferredSize().height));
      general.add(file_btn);
      general.add(size_l);
      general.add(size_tf);
      
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));       
      
      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);
      
      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);     
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, id_tf, 0, SpringLayout.EAST, file_btn);
      
      general_layout.putConstraint(SpringLayout.NORTH, file_l, 10, SpringLayout.SOUTH, id_l);
      general_layout.putConstraint(SpringLayout.WEST, file_l, 5, SpringLayout.WEST, general);     
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, file_tf, 0, SpringLayout.VERTICAL_CENTER, file_l);      
      general_layout.putConstraint(SpringLayout.WEST, file_tf, tf_dx, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, file_btn, 0, SpringLayout.VERTICAL_CENTER, file_l);
      general_layout.putConstraint(SpringLayout.WEST, file_btn, 5, SpringLayout.EAST, file_tf);
      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, file_btn);
      
      general_layout.putConstraint(SpringLayout.NORTH, size_l, 10, SpringLayout.SOUTH, file_l);
      general_layout.putConstraint(SpringLayout.WEST, size_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, size_tf, 0, SpringLayout.VERTICAL_CENTER, size_l);
      general_layout.putConstraint(SpringLayout.WEST, size_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, size_tf, 0, SpringLayout.EAST, file_btn);
      
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, size_l);
      
      frame.add(general);     
      frame.add(attr_l);    
      frame.add(ok_btn); 
      frame.add(cancel_btn);
      frame.add(help_btn);    
      
      SpringLayout layout = new SpringLayout();
      frame.setLayout(layout);
      
      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      //layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, attr_l, 5, SpringLayout.SOUTH, general);
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
      update();
      int i = updateFont();
      if(i==0) JOptionPane.showMessageDialog(frame,"Error while loading font file!\n Please choose another file\n","Font file not valid",JOptionPane.ERROR_MESSAGE);
      else if(i==2) JOptionPane.showMessageDialog(frame,"Error while loading font file!\n Please choose another file\n","Font file not valid",JOptionPane.ERROR_MESSAGE);
      else if(i==1) {
        frame.setVisible(false);
        frame.dispose();
        frame = null;  
      }
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skinedhlp/res-font.html"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skinedhlp/res-font.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    else if(e.getSource().equals(cancel_btn)) {
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
  }
  public String returnCode(String indent) {
    String code=indent+"<Font id=\""+id+"\" file=\""+file+"\"";
    if (size!=SIZE_DEFAULT) code+=" size=\""+String.valueOf(size)+"\"";
    code+="/>\n";
    return code;
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Font: "+id);    
    return node;
  }
}
