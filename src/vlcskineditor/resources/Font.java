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
import org.w3c.dom.Node;
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
  
  {
    type = Language.get("FONT");
  }
  
  /**
   * Creates a Font from a XML node
   * @param n The XML node
   * @param s_ The parent skin manager
   */
  public Font(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", id);
    file = XML.getStringAttributeValue(n, "file", file);
    size = XML.getIntAttributeValue(n, "size", size);
    updateFont();
  }
  
  /**
   * Creates a new Font from a given file.
   * @param s_ The skin in which the Font is used.
   * @param f_ The font file. TrueType or OpenType. Notice that only OpenType fonts in a TrueType container
   * can be displayed by the Skin Editor. VLC can display both. This will be fixed in JRE 7
   */
  public Font(Skin s_, File f_) {
    s = s_;
    String id_t = f_.getName().substring(0,f_.getName().lastIndexOf("."));
    if(s.idExists(id_t)) id_t += "_"+s.getNewId();
    id = id_t;
    file = f_.getPath().replace(s.skinfolder,"").replaceAll("\\\\","/");
    s.updateResources();
    s.expandResource(id);
    updateFont();
  }
  
  /**
   * Creates a new Font from user input.
   * @param s_ The skin in which the Font is used.
   */
  public Font(Skin s_) {
    s = s_;
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    file = "";
    s.updateResources();
    s.expandResource(id);
    showOptions();
  }

  /**
   * Creates a copy of a font
   * @param f The font to copy
   */
  public Font(Font f) {
    s = f.s;
    id = f.id;
    file = f.file;
    size = f.size;
    updateFont();
  }

  public boolean updateFont() {
    try {      
      f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(s.skinfolder+file));      
      f = f.deriveFont((float)size);      
    }
    catch(Exception e) {
      e.printStackTrace();
      if(file.indexOf(".otf")==-1) {
        //Font file most probably corrupt, as its not OTF but still cannot be loaded by Java
        f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);        
        return false;
      }
      else {        
        try {
          f = java.awt.Font.createFont(java.awt.Font.TYPE1_FONT,new File(s.skinfolder+file));
          f = f.deriveFont((float)size);
        }
        catch(Exception ex) {
          JOptionPane.showMessageDialog(frame,Language.get("ERROR_OTF_MSG"),Language.get("ERROR_OTF_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          ex.printStackTrace();
          f = new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,size);          
        }
        return true;
      }
    }
    return true;
  }
  @Override
  public void update() {
    FontEditEvent fee = new FontEditEvent(this);
    file=file_tf.getText().replaceAll("\\\\","/");
    size=Integer.parseInt(size_tf.getText());        
    if(!id.equals(id_tf.getText())) {
      id=id_tf.getText();
      s.updateResources();
      s.expandResource(id);
    }
    fee.setNew();
    s.m.hist.addEvent(fee);
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_FONT_TITLE"));
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setIconImage(Main.edit_icon.getImage());
      frame.setResizable(false);
      JLabel id_l = new JLabel(Language.get("WIN_ITEM_ID"));
      id_tf = new JTextField();
      id_tf.setToolTipText(Language.get("WIN_ITEM_ID_TIP").replaceAll("%t",type));
      JLabel file_l = new JLabel(Language.get("WIN_FONT_FILE"));
      file_tf = new JTextField();
      file_tf.setToolTipText(Language.get("WIN_FONT_FILE_TIP"));
      file_btn = new JButton(Language.get("WIN_FONT_OPEN"));
      file_btn.addActionListener(this);
      JLabel size_l = new JLabel(Language.get("WIN_FONT_SIZE"));
      size_tf = new JTextField();
      size_tf.setDocument(new NumbersOnlyDocument());
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);      
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);      
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);      
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      
      //Textfield distance to WEST border of container
      Component[] labels = { id_l, file_l, size_l };
      int tf_dx = Helper.maxWidth(labels)+10;               
      //Maximal textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
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
      
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));       
      
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
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(file_btn)) {
      if(fc==null) {
        fc = new JFileChooser();
        String[] ext = { "ttf" , "otf" };
        fc.setFileFilter(new CustomFileFilter(fc,ext,"*.otf/*.ttf (OpenType/TrueType Font) "+Language.get("FILE_INDIR"),true,s.skinfolder));
        fc.setCurrentDirectory(new File(s.skinfolder));   
        fc.setAcceptAllFileFilterUsed(false);        
      }
      int returnVal = fc.showOpenDialog(frame);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        file_tf.setText(fc.getSelectedFile().getPath().replace(s.skinfolder,""));
      }
    }    
    else if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")||id.contains("\"")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_INVALID_MSG"),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i",id_tf.getText()),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      if(file_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_FILE_INVALID_MSG"),Language.get("ERROR_FILE_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(size_tf.getText().length()<1) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_SIZE_INVALID_MSG"),Language.get("ERROR_SIZE_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      update();      
      if(!updateFont()) JOptionPane.showMessageDialog(frame,Language.get("ERROR_FILE_INVALID_MSG"),Language.get("ERROR_FILE_INVALID_TITLE"),JOptionPane.ERROR_MESSAGE);      
      else {
        frame.setVisible(false);
        frame.dispose();
        frame = null;  
      }
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/res-font.html");
    }
    else if(e.getSource().equals(cancel_btn)) {
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
  }
  @Override
  public String returnCode(String indent) {
    String code=indent+"<Font id=\""+id+"\" file=\""+file+"\"";
    if (size!=SIZE_DEFAULT) code+=" size=\""+String.valueOf(size)+"\"";
    code+="/>\n";
    return code;
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Font: "+id);    
    return node;
  }
}
