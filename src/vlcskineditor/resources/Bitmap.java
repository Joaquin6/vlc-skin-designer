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

package vlcskineditor.resources;

import vlcskineditor.*;
import vlcskineditor.history.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles Bitmap resources
 * @author Daniel
 */
public class Bitmap extends ImageResource implements ActionListener{
  
  public String file;
  public final String ALPHACOLOR_DEFAULT = "#FF00FF";
  public String alphacolor = ALPHACOLOR_DEFAULT;
  final int NBFRAMES_DEFAULT = 1;
  public int nbframes = NBFRAMES_DEFAULT;
  public final int FPS_DEFAULT = 0;
  public int fps = FPS_DEFAULT;
  //The list containing the bitmap's SubBitmaps  
  public java.util.List<SubBitmap> SubBitmaps = new LinkedList<SubBitmap>();  
  
  private JFrame frame = null;
  private JTextField id_tf, file_tf, alphacolor_tf, nbframes_tf, fps_tf;
  private JButton file_btn, alphacolor_btn, ok_btn, cancel_btn, help_btn;
  private JFileChooser fc;
  
  {
    type = Language.get("BITMAP");
  }
  
  /**
   * Creates a new Bitmap from a XML node
   * @param n The XML node
   * @param s_ The skin in which the Bitmap is used
   */  
  public Bitmap(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", id);
    file = XML.getStringAttributeValue(n, "file", file);
    alphacolor = XML.getStringAttributeValue(n, "alphacolor", alphacolor);
    nbframes = XML.getIntAttributeValue(n, "nbframes", nbframes);
    fps = XML.getIntAttributeValue(n, "fps", fps);
    updateImage();
    NodeList children = n.getChildNodes();
    for(int i=0;i<children.getLength();i++) {
      Node child = children.item(i);     
      if(child.getNodeType()==Node.ELEMENT_NODE) {        
        if(child.getNodeName().equals("SubBitmap"))
          SubBitmaps.add(new SubBitmap(child, s, this));
      }
    }
  }
  
  /**
   * Creates a new Bitmap from xml code
   * @param xmlcode The XML code from which the Bitmap should be created. One line per tag.
   * @param s_ The skin in which the Bitmap is used. This is necessary in order that the image file can be located relatively to the skin file.
   */  
  public Bitmap(String xmlcode, Skin s_) { 
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
        if(lines[i].startsWith("<SubBitmap")) SubBitmaps.add(new SubBitmap(lines[i],s,this));
      }
    }    
    updateImage();
  }
  /**
   * Creates a new Bitmap from given attributes
   * @param id_ The ID of the new Bitmap
   * @param file_ The relative path to the image file.
   * @param alphacolor_ The alphacolor of the Bitmap. Format is #RRGGBB.
   * @param nbframes_ If the Bitmap is animated this defines the number of frames.
   * @param fps_ If the Bitmap is animated this defines the frames displayed per second.
   * @param s_ The skin in which the Bitmap is used.
   * This is necessary in order that the image file can be located relatively to the skin file.
   */
  public Bitmap(String id_, String file_, String alphacolor_, int nbframes_, int fps_, Skin s_) {
    s=s_;
    id=id_;
    file=file_;
    alphacolor=alphacolor_;
    nbframes=nbframes_;
    fps=fps_;        
    updateImage();
  }
  /**
   * Creates a new Bitmap from a given file.
   * @param s_ The skin in which the Bitmap is used.
   * This is necessary in order that the image file can be located relatively to the skin file.
   * @param f_ The image file represented by this Bitmap.
   */
  public Bitmap(Skin s_, File f_) {
    s = s_;
    //Gets the relative path to the bitmap file
    file = f_.getPath().replace(s.skinfolder,"");
    //Sets the bitmap's id according to the pattern subfolder_filename
    String id_t = file.replaceAll("\\\\","_").replaceAll("/","_").substring(0,file.lastIndexOf("."));
    if(s.idExists(id_t)) id_t += "_"+s.getNewId();
    id = id_t;
    s.updateResources();    
    updateImage();
  } 
  /**
   * Regenerates the image represented by the Bitmap object.
   */
  public boolean updateImage() {
    try {
      image = ImageIO.read(new File(s.skinfolder+file));       
      image = image.getSubimage(0,0,image.getWidth(),image.getHeight()/nbframes);         
      if(image.getType()!=13) { //If PNG is not indexed
        BufferedImage bi = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics g2d = bi.createGraphics();      
        int alphargb = Color.decode(alphacolor).getRGB();
        for(int x=0;x<image.getWidth();x++) {
          for(int y=0;y<image.getHeight();y++) {
            int argb = image.getRGB(x,y);
            int red = (argb >> 16) & 0xff;
            int green = (argb >> 8) & 0xff;
            int blue = argb & 0xff;
            int alpha = (argb >> 24) & 0xff;            
            if(image.getRGB(x,y)!=alphargb && alpha>=255) {              
              g2d.setColor(new Color(red, green, blue, alpha));
              g2d.drawRect(x,y,0,0);
            } 
            else if(image.getRGB(x,y)!=alphargb && alpha>0) {
              float amount = alpha;
              amount = amount/255;              
              g2d.setColor(new Color((int)(red*amount), (int)(green*amount), (int)(blue*amount)));              
              g2d.drawRect(x,y,0,0);
            }
          }        
        }
        image = bi;
      }
      for(int i=0;i<SubBitmaps.size();i++) {
        SubBitmaps.get(i).updateImage();
      }
    }
    catch(Exception ex) {      
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null,Language.get("ERROR_BITMAP_LOAD").replaceAll("%i",id),Language.get("ERROR_BITMAP_LOAD_TITLE"),JOptionPane.ERROR_MESSAGE); 
      //showOptions();
      image = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D)image.getGraphics();
      g.setColor(new Color(255,0,0,128));
      g.fillRect(0, 0, 32, 32);
      return false;
    }
    return true;
  }
  @Override
  public void update() {
    BitmapEditEvent be = new BitmapEditEvent(this);
    file=file_tf.getText();
    alphacolor=alphacolor_tf.getText();
    nbframes=Integer.parseInt(nbframes_tf.getText());
    fps=Integer.parseInt(fps_tf.getText());   
    if(!id_tf.getText().equals(id)) {
      id=id_tf.getText();
      s.updateResources();
      s.expandResource(id);
    }    
    be.setNew();
    s.m.hist.addEvent(be);
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_BITMAP_TITLE"));
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());      
      JLabel id_l = new JLabel(Language.get("WIN_ITEM_ID"));
      id_tf = new JTextField();
      id_tf.setToolTipText(Language.get("WIN_ITEM_ID_TIP".replaceAll("%t", type)));
      JLabel file_l = new JLabel(Language.get("WIN_BITMAP_FILE"));
      file_tf = new JTextField();
      file_tf.setToolTipText(Language.get("WIN_BITMAP_FILE_TIP"));
      file_btn = new JButton(Language.get("WIN_BITMAP_OPEN"));
      file_btn.addActionListener(this);
      JLabel alphacolor_l = new JLabel(Language.get("WIN_BITMAP_ALPHACOLOR"));
      alphacolor_tf = new JTextField();
      alphacolor_tf.setToolTipText(Language.get("WIN_BITMAP_ALPHACOLOR_TIP"));
      alphacolor_btn = new JButton(Language.get("WIN_BITMAP_CHOOSE"));
      alphacolor_btn.addActionListener(this);
      JLabel nbframes_l = new JLabel(Language.get("WIN_BITMAP_NBFRAMES"));
      nbframes_tf = new JTextField();
      nbframes_tf.setDocument(new NumbersOnlyDocument());
      nbframes_tf.setToolTipText(Language.get("WIN_BITMAP_NBFRAMES_TIP"));
      JLabel fps_l = new JLabel(Language.get("WIN_BITMAP_FPS"));
      fps_tf = new JTextField();
      fps_tf.setDocument(new NumbersOnlyDocument());
      fps_tf.setToolTipText(Language.get("WIN_BITMAP_FPS_TIP"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this); 
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      
      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, file_l, alphacolor_l, nbframes_l, fps_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = 200;
      //Width of buttons
      Component[] btns = { file_btn, alphacolor_btn };
      int btn_wd = Helper.maxWidth(btns);      
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      general.add(file_l);
      general.add(file_tf);
      general.add(file_btn);
      general.add(alphacolor_l);
      general.add(alphacolor_tf);
      general.add(alphacolor_btn);
      file_tf.setPreferredSize(new Dimension(tf_wd-btn_wd,file_tf.getPreferredSize().height));
      file_btn.setPreferredSize(new Dimension(btn_wd,file_btn.getPreferredSize().height));
      alphacolor_tf.setPreferredSize(new Dimension(tf_wd-btn_wd,alphacolor_tf.getPreferredSize().height));
      alphacolor_btn.setPreferredSize(new Dimension(btn_wd,alphacolor_btn.getPreferredSize().height));
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
      
      general_layout.putConstraint(SpringLayout.NORTH, alphacolor_l, 10, SpringLayout.SOUTH, file_l);
      general_layout.putConstraint(SpringLayout.WEST, alphacolor_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, alphacolor_tf, 0, SpringLayout.VERTICAL_CENTER, alphacolor_l);
      general_layout.putConstraint(SpringLayout.WEST, alphacolor_tf, tf_dx, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, alphacolor_btn, 0, SpringLayout.VERTICAL_CENTER, alphacolor_l);
      general_layout.putConstraint(SpringLayout.WEST, alphacolor_btn, 5, SpringLayout.EAST, alphacolor_tf);
      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, alphacolor_btn);
      
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, alphacolor_l);
      frame.add(general);
      
      JPanel animation = new JPanel(null);
      animation.add(nbframes_l);
      animation.add(nbframes_tf);
      animation.add(fps_l);
      animation.add(fps_tf);      
      animation.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_BITMAP_ANIMATION")));             
      
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
      
      layout.putConstraint(SpringLayout.NORTH, animation, 5, SpringLayout.SOUTH, general);
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
    file_tf.setText(file);
    alphacolor_tf.setText(alphacolor);
    nbframes_tf.setText(String.valueOf(nbframes));
    fps_tf.setText(String.valueOf(fps));
    frame.setVisible(true);
    
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(file_btn)) {
      if(fc==null) {
        fc = new JFileChooser();        
        fc.setFileFilter(new CustomFileFilter(fc,"png","*.png (Portable Network Graphics) "+Language.get("FILE_INDIR"),true,s.skinfolder));
        fc.setCurrentDirectory(new File(s.skinfolder));   
        fc.setAcceptAllFileFilterUsed(false);
        
      }
      int returnVal = fc.showOpenDialog(frame);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        file_tf.setText(fc.getSelectedFile().getPath().replace(s.skinfolder,""));
      }
    }
    else if (e.getSource().equals(alphacolor_btn)) {
      Color color = JColorChooser.showDialog(frame,Language.get("WIN_BITMAP_CHOOSER_TITLE"),Color.decode(alphacolor_tf.getText()));
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
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_INVALID_MSG"),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i", id_tf.getText()),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      if(file_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_FILE_INVALID_MSG"),Language.get("ERROR_FILE_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(nbframes_tf.getText())<1) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_NBFRAMES_MSG"),Language.get("ERROR_NBFRAMES_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }      
      update();
      if(updateImage()) {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
      }
      else {        
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_BITMAP_INVALID_MSG"),Language.get("ERROR_FILE_INVALID_TITLE"),JOptionPane.ERROR_MESSAGE);
      }
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/res-bitmap.html");
    }
    else if(e.getSource().equals(cancel_btn)) {
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
  }
  @Override
  public String returnCode(String indent) {
    String code = indent+"<Bitmap id=\""+id+"\" file=\""+file+"\"";
    code+=" alphacolor=\""+alphacolor+"\"";
    if (nbframes!=NBFRAMES_DEFAULT) code+=" nbframes=\""+String.valueOf(nbframes)+"\"";
    if (fps!=FPS_DEFAULT) code+=" fps=\""+String.valueOf(fps)+"\"";    
    if(SubBitmaps.size()>0) {
      code+=">\n";
      for (int i=0;i<SubBitmaps.size();i++) {
        code+=SubBitmaps.get(i).returnCode(indent+Skin.indentation);
      }
      code+=indent+"</Bitmap>\n";
    }
    else {
      code+="/>\n";  
    }    
    return code;
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Bitmap: "+id);    
    for(int i=0;i<SubBitmaps.size();i++) {
      node.add(SubBitmaps.get(i).getTreeNode());
    }
    return node;
  }
  @Override
  public Resource getParentOf(String id_) {
    for(SubBitmap sbmp:SubBitmaps) {
      if(sbmp.id.equals(id_)) return this;
    }
    return null;
  }
  @Override
  public void renameForCopy(String p) {
    String p_ = p;
    super.renameForCopy(p);
    for(SubBitmap sb:SubBitmaps) sb.renameForCopy(p_);
  }
}
