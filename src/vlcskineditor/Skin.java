/*****************************************************************************
 * Skin.java
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

package vlcskineditor;

import vlcskineditor.Resources.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;

/**
 *
 * @author Daniel
 */
public class Skin implements ActionListener{
  
  java.util.List<Resource> resources = new ArrayList<Resource>();
  java.util.List<Window> windows = new ArrayList<Window>();
  final String HEADER = "<!DOCTYPE Theme PUBLIC \"-//VideoLAN//DTD VLC Skins V2.0//EN\" \"skin.dtd\">";
  final String THEME_VERSION_DEFAULT = "2.0";
  final String THEME_TOOLTIPFONT_DEFAULT = "defaultfont";
  final int THEME_MAGNET_DEFAULT = 15;
  final int THEME_ALPHA_DEFAULT = 255;
  final int THEME_MOVEALPHA_DEFAULT = 255;
  String theme_version = THEME_VERSION_DEFAULT;
  String theme_tooltipfont = THEME_TOOLTIPFONT_DEFAULT;
  int theme_magnet = THEME_MAGNET_DEFAULT;
  int theme_alpha = THEME_ALPHA_DEFAULT;
  int theme_movealpha = THEME_MOVEALPHA_DEFAULT;
  final String THEMEINFO_NAME_DEFAULT = "Unnamed Theme";
  final String THEMEINFO_AUTHOR_DEFAULT = "Unknown Author";
  final String THEMEINFO_EMAIL_DEFAULT = "Unknown";
  final String THEMEINFO_WEBPAGE_DEFAULT = "http://www.videolan.org/vlc/";
  String themeinfo_name = THEMEINFO_NAME_DEFAULT;
  String themeinfo_author = THEMEINFO_AUTHOR_DEFAULT;
  String themeinfo_email = THEMEINFO_EMAIL_DEFAULT;
  String themeinfo_webpage = THEMEINFO_EMAIL_DEFAULT;
  File skinfile;
  public String skinfolder;
  public Main m;
  Window active_window = null;
  Layout active_layout = null;
  int unnamed_ids = 0;  
  
  JFrame theme_frame;
  JTextField themeinfo_name_tf, themeinfo_author_tf, themeinfo_email_tf, themeinfo_webpage_tf;
  JTextField theme_magnet_tf, theme_alpha_tf, theme_movealpha_tf;
  JButton theme_ok_btn, theme_help_btn;
  
  public Skin(Main m_) {
    m=m_;
  }
  /** Creates the file in which to save the new skin **/
  public void createNew(File f) {
    skinfile=f;
    skinfolder = f.getParentFile().getAbsolutePath()+f.separator;    
    try {
      skinfile.createNewFile();
    }
    catch(Exception ex) {
      JOptionPane.showMessageDialog(null,ex.toString(),"Error while creating new skin file!",JOptionPane.ERROR_MESSAGE);
    }
  }
  /** Parses an XML file **/
  public void open(File f) {    
    resources.clear();
    windows.clear();
    skinfile=f;
    skinfolder = f.getParentFile().getAbsolutePath()+f.separator;    
    try {
      //System.out.println("Creating Buffered Reader...");
      BufferedReader br = new BufferedReader(new FileReader(f));
      //System.out.println("Ready...");
      //System.out.println("Reading Header...");
      String header = br.readLine();
      //System.out.println("Header read");
      if (header.indexOf("//VideoLAN//DTD VLC Skins")==-1) {
        br.close();        
        //System.out.println("Invalid header");
        throw new Exception("File appears not to be a valid VLC Theme!\nInvalid Header:\n"+header);        
      }
      //System.out.println("Valid header");
      boolean eof = false;
      String line = "";  
      while(!eof) {              
        try {
          //System.out.println("Reading line");
          line = br.readLine();          
          if (line==null) break;
          line = line.trim();
        }        
        catch(Exception e) {
          eof = true;
          break;
        }    
        //<editor-fold defaultstate="collapsed" desc=" ThemeInfo tag "> 
        if(line.startsWith("<ThemeInfo")) {         
          if(line.indexOf("name")!=-1) themeinfo_name = XML.getValue(line,"name");
          if(line.indexOf("author")!=-1) themeinfo_author = XML.getValue(line,"author");
          if(line.indexOf("email")!=-1) themeinfo_email = XML.getValue(line,"email");
          if(line.indexOf("webpage")!=-1) themeinfo_webpage = XML.getValue(line,"webpage");
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc=" Theme tag "> 
        else if(line.startsWith("<Theme")) {
          if(line.indexOf("version")!=-1) theme_version = XML.getValue(line,"version");          
          if(line.indexOf("tooltipfont")!=-1) theme_tooltipfont = XML.getValue(line,"tooltipfont");
          if(line.indexOf("magnet")!=-1) theme_magnet = Integer.parseInt(XML.getValue(line,"magnet"));
          if(line.indexOf(" alpha")!=-1) theme_alpha = Integer.parseInt(XML.getValue(line,"alpha"));
          if(line.indexOf("movealpha")!=-1) theme_movealpha = Integer.parseInt(XML.getValue(line,"movealpha"));
        }
        //</editor-fold>                
        //<editor-fold defaultstate="collapsed" desc=" Bitmap tag "> 
        else if(line.startsWith("<Bitmap")) {          
          if(line.indexOf("/>")!=-1) {
            resources.add(new Bitmap(line,this));
            //System.out.println("Bitmap identified: "+line);
          }
          else {
            //System.out.println("Bitmap with Subbitmaps identified: "+line);
            boolean tagclosed=false;
            String curline = "";
            while(!tagclosed) {
              try {
                curline = br.readLine();
                curline = curline.trim();           
              }
              catch (Exception e) {
                tagclosed=true;
              }
              if(curline.startsWith("</Bitmap>"))  {
                line +="\n"+curline;
                tagclosed = true;
              }
              else {
                line+="\n"+curline;
              }
            }
            resources.add(new Bitmap(line,this));
            //System.out.println("Bitmap with Subbmitmaps completely identified");
          }
        }
        //</editor-fold>
        else if(line.startsWith("<Font")) resources.add(new vlcskineditor.Resources.Font(line,this));
        else if(line.startsWith("<BitmapFont")) resources.add(new BitmapFont(line,this));        
        //<editor-fold defaultstate="collapsed" desc=" Window tag "> 
        else if(line.startsWith("<Window")) {
          boolean tagclosed=false;
          String curline="";
          while(!tagclosed) {
            try {
              curline = br.readLine();
              curline = curline.trim();               
            }
            catch (Exception e) {
              tagclosed=true;
            }
            if(curline.startsWith("</Window>"))  {
              line +="\n"+curline;
              tagclosed = true;
            }
            else {
              line+="\n"+curline;
            }
          }
          windows.add(new Window(line,this));
        }
        //</editor-fold>
      }
      br.close();
      //System.out.println("Buffered Reader was closed");
    } 
    catch (Exception ex) {
      String stackTrace ="";
      for (int i=0;i<ex.getStackTrace().length;i++) {
        stackTrace+=ex.getStackTrace()[i].toString()+"\n";
      }
      JOptionPane.showMessageDialog(null,ex.toString()+"\n\n"+stackTrace,ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
      update();
    }    
    update();           
  }
  /** Parses the XML Code into the skinfile **/
  public void save() {   
    try {
      FileWriter writer = new FileWriter(skinfile);
      writer.write(returnCode());
      writer.flush();
      writer.close();
    }
    catch(Exception ex) {
      JOptionPane.showMessageDialog(null,ex.toString(),"Error while saving skin file!",JOptionPane.ERROR_MESSAGE);
    }
   
  }
  /** Show the theme setting editing dialog **/
  public void showThemeOptions() {
    if(theme_frame==null) {
      theme_frame = new JFrame("Theme settings");
      theme_frame.setResizable(false);
      theme_frame.setLayout(new FlowLayout());
      theme_frame.setDefaultCloseOperation(theme_frame.HIDE_ON_CLOSE);
      
      JLabel themeinfo_name_l = new JLabel("Name:");
      themeinfo_name_tf = new JTextField();
      JLabel themeinfo_author_l = new JLabel("Author:");
      themeinfo_author_tf = new JTextField();
      JLabel themeinfo_email_l = new JLabel("Author's E-Mail:");
      themeinfo_email_tf = new JTextField();
      JLabel themeinfo_webpage_l = new JLabel("Author's webpage:");
      themeinfo_webpage_tf = new JTextField();
      JLabel theme_magnet_l = new JLabel("Magnet:");
      theme_magnet_tf = new JTextField();
      theme_magnet_tf.setDocument(new NumbersOnlyDocument(false));
      theme_magnet_tf.setToolTipText("When the distance between the border of the screen and an anchor of a window is less than this value, the window will stick to the border");
      JLabel theme_alpha_l = new JLabel("Opacity:");
      theme_alpha_tf = new JTextField();
      theme_alpha_tf.setDocument(new NumbersOnlyDocument(false));
      theme_alpha_tf.setToolTipText("Sets the alpha transparency of the windows. The value must be between 1 (nearly total transparency) and 255 (total opacity). Low values should be avoided.");
      JLabel theme_movealpha_l = new JLabel("Opacity when moving:");
      theme_movealpha_tf = new JTextField();
      theme_alpha_tf.setDocument(new NumbersOnlyDocument(false));
      theme_alpha_tf.setToolTipText("Sets the alpha transparency of the windows when they are moved.");
      theme_ok_btn = new JButton("OK");
      theme_ok_btn.addActionListener(this);
      theme_help_btn = new JButton("Help");
      theme_help_btn.addActionListener(this);
      
      JPanel themeinfo_p = new JPanel(null);
      themeinfo_p.add(themeinfo_name_l);
      themeinfo_p.add(themeinfo_name_tf);      
      themeinfo_name_l.setBounds(5,15,150,24);
      themeinfo_name_tf.setBounds(160,15,150,24);
      themeinfo_p.add(themeinfo_author_l);
      themeinfo_p.add(themeinfo_author_tf);      
      themeinfo_author_l.setBounds(5,45,150,24);
      themeinfo_author_tf.setBounds(160,45,150,24);
      themeinfo_p.add(themeinfo_email_l);
      themeinfo_p.add(themeinfo_email_tf);      
      themeinfo_email_l.setBounds(5,75,150,24);
      themeinfo_email_tf.setBounds(160,75,150,24);
      themeinfo_p.add(themeinfo_webpage_l);
      themeinfo_p.add(themeinfo_webpage_tf);      
      themeinfo_webpage_l.setBounds(5,105,150,24);
      themeinfo_webpage_tf.setBounds(160,105,150,24);      
      themeinfo_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Theme Information"));
      themeinfo_p.setMinimumSize(new Dimension(315,135));
      themeinfo_p.setMaximumSize(new Dimension(315,135));
      themeinfo_p.setPreferredSize(new Dimension(315,135));
      theme_frame.add(themeinfo_p);
      
      JPanel theme_p = new JPanel(null);
      theme_p.add(theme_magnet_l);
      theme_p.add(theme_magnet_tf);
      theme_magnet_l.setBounds(5,15,150,24);
      theme_magnet_tf.setBounds(160,15,150,24);
      theme_p.add(theme_alpha_l);
      theme_p.add(theme_alpha_tf);
      theme_alpha_l.setBounds(5,45,150,24);
      theme_alpha_tf.setBounds(160,45,150,24);
      theme_p.add(theme_movealpha_l);
      theme_p.add(theme_movealpha_tf);
      theme_movealpha_l.setBounds(5,75,150,24);
      theme_movealpha_tf.setBounds(160,75,150,24);
      theme_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Theme Attributes"));
      theme_p.setMinimumSize(new Dimension(315,105));
      theme_p.setMaximumSize(new Dimension(315,105));
      theme_p.setPreferredSize(new Dimension(315,105));
      
      theme_frame.add(theme_p);
      theme_frame.add(theme_ok_btn);
      theme_frame.add(theme_help_btn);
      
      theme_frame.setMinimumSize(new Dimension(325,330));
      theme_frame.setPreferredSize(new Dimension(325,330));
      theme_frame.setMaximumSize(new Dimension(325,330));
      
      theme_frame.pack();      
    }
    themeinfo_name_tf.setText(themeinfo_name);
    themeinfo_author_tf.setText(themeinfo_author);
    themeinfo_email_tf.setText(themeinfo_email);
    themeinfo_webpage_tf.setText(themeinfo_webpage);
    theme_magnet_tf.setText(String.valueOf(theme_magnet));
    theme_alpha_tf.setText(String.valueOf(theme_alpha));
    theme_movealpha_tf.setText(String.valueOf(theme_movealpha));
    theme_frame.setVisible(true);
  }
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(theme_ok_btn)) {      
      if(Integer.parseInt(theme_alpha_tf.getText())>255 || Integer.parseInt(theme_alpha_tf.getText())<1) {
        JOptionPane.showMessageDialog(theme_frame,"Please enter a valid alpha value!","Alpha value not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(theme_movealpha_tf.getText())>255 || Integer.parseInt(theme_movealpha_tf.getText())<1) {
        JOptionPane.showMessageDialog(theme_frame,"Please enter a valid movealpha value!","Movealpha value not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      themeinfo_name = themeinfo_name_tf.getText();
      themeinfo_author = themeinfo_author_tf.getText();
      themeinfo_email = themeinfo_email_tf.getText();
      themeinfo_webpage = themeinfo_webpage_tf.getText();
      theme_magnet = Integer.parseInt(theme_magnet_tf.getText());
      theme_alpha = Integer.parseInt(theme_alpha_tf.getText());
      theme_movealpha = Integer.parseInt(theme_movealpha_tf.getText());
      theme_frame.setVisible(false);
    }
    else if(e.getSource().equals(theme_help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Theme"));
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
  /** Generates a new number for unnamed items so that every id is unique **/  
  public int getNewId() {
    unnamed_ids++;
    return unnamed_ids;
  }
  /** Checks whether an id exists already **/
  public boolean idExists(String id) {
    for (int i=0;i<resources.size();i++) {
      if(resources.get(i).id.equals(id)) return true;
    }
    for (int i=0;i<windows.size();i++) {
      if(windows.get(i).id.equals(id)) return true;
      for (int l=0;l<windows.get(i).layouts.size();l++) {
        if(windows.get(i).layouts.get(l).id.equals(id)) return true;
        for(int x=0;x<windows.get(i).layouts.get(l).items.size();x++) {
          if(windows.get(i).layouts.get(l).items.get(x).id.equals(id)) return true;
        }
      }
    }
    return false;
  }
  /** Returns the resource represented by the given id **/
  public Resource getResource(String id) {
    Resource r = null;
    for (int i=0;i<resources.size();i++) {
      if(resources.get(i).id.equals(id)) r=resources.get(i);
      if(resources.get(i).type.equals("Bitmap")) {
        Bitmap bmp = (Bitmap)resources.get(i);
        for(int x=0;x<bmp.SubBitmaps.size();x++) {
          if(bmp.SubBitmaps.get(x).id.equals(id)) r=bmp.SubBitmaps.get(x);
        }
      }
    }
    return r;
  }
  /** Returns the image object of a bitmap **/
  public BufferedImage getBitmapImage(String id) {
    Resource r = getResource(id);
    if(r==null) {
      return null;
    }
    else {
      if(r.type.equals("Bitmap")) {
        try {
          Bitmap b = (Bitmap)r;
          return b.image;
        }
        catch(Exception e) {
          try {
            SubBitmap sb = (SubBitmap)r;
            return sb.image;
          }
          catch(Exception e2) {
            
          }
        }
        
      }
      else return null;
    }
    return null;
  }
  public java.awt.Font getFont(String id) {
    if(id.equals("defaultfont")) {
      java.awt.Font f = null;
      try {      
        f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,new File(Main.class.getResource("FreeSans.ttf").toURI()));
        f = f.deriveFont(12);
      }
      catch(Exception e) {
        e.printStackTrace();
        f=null;
      }
      return f;
    }
    Resource r = getResource(id);
    if(r==null) return null;
    try {
      if(r.type.equals("Font")) {
        vlcskineditor.Resources.Font fr = (vlcskineditor.Resources.Font)r;
        return fr.f;
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
    return null;
  }
  /** Returns the window represented by the given id **/
  public Window getWindow(String id) {
    Window win = null;
    for(int i=0;i<windows.size();i++) {
      if(windows.get(i).id.equals(id)) {
        win = windows.get(i);
      }
    }
    return win;
  }
  /** Returns <b>true</b> if the resource with the given id is in use by any item of the skin **/
  public boolean isUsed(String id_) {
    //TODO: Maybe or maybe not
    return false;
  }
  /** Returns the parent element that contains the item of the given id **/
  public java.util.List<Item> getParentOf(String id_) {
    return active_layout.getParentOf(id_);    
  }
  /** Returns the list of the given Group/Panel that contains the items **/
  public java.util.List<Item> getListOf(String id_) {
    Item i = getItem(id_);    
    java.util.List<Item> l = null;          
    try {
      vlcskineditor.Items.Panel p = (vlcskineditor.Items.Panel)i;
      l = p.items;
    }
    catch(Exception ex1) {
      try {
        vlcskineditor.Items.Group g = (vlcskineditor.Items.Group)i;
        l = g.items;
      }
      catch(Exception ex2) {
        System.out.println(ex2);
      }
    }
    return l;
  }
  /** Return the item with the given id **/
  public Item getItem(String id_) {    
    active_window = null;
    active_layout = null;
    if(m.selected_window!=null && m.selected_layout!=null) {
      active_window = getWindow(m.selected_window);
      if(active_window!=null) {
        active_layout = active_window.getLayout(m.selected_layout);
      }
    }
    if(active_layout!=null) {
      return active_layout.getItem(id_);
    }
    return null;
  }
  /** Creates the resources hierarchy **/
  public DefaultMutableTreeNode getResourcesTree() {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root: Resources");     
    DefaultMutableTreeNode bitmaps_node = new DefaultMutableTreeNode("Root: Bitmaps");
    DefaultMutableTreeNode fonts_node = new DefaultMutableTreeNode("Root: Fonts");
    for(int i=0;i<resources.size();i++) {
      if(resources.get(i).type=="Bitmap") {
        bitmaps_node.add(resources.get(i).getTreeNode());
      }
    }
    for(int i=0;i<resources.size();i++) {
      if(resources.get(i).type=="Font") {
        DefaultMutableTreeNode node = resources.get(i).getTreeNode();       
        fonts_node.add(node);
      }
    }
    top.add(bitmaps_node);
    top.add(fonts_node);
    return top;
  }
  /** Creates the windows hierarchy **/
  public DefaultMutableTreeNode getWindowsTree() {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root: Windows");         
    for(int i=0;i<windows.size();i++) {
      top.add(windows.get(i).getTreeNode());      
    }
    return top;
  }  
  /** Creates the layout's items hierarchy **/
  public DefaultMutableTreeNode getItemsTree() {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root: Items");
    if(m.selected_window!=null && m.selected_layout!=null) {
      active_window = getWindow(m.selected_window);
      if(active_window!=null) {
        active_layout = active_window.getLayout(m.selected_layout);
      }
    }
    if(active_layout!=null) {
      for (int i=0;i<active_layout.items.size();i++) {
        top.add(active_layout.items.get(i).getTreeNode());
      }
    }
    return top;
  }
  /** Creates the XML of the skin **/
  public String returnCode() {
    String code ="";
    code += HEADER+"\n";
    code += "<Theme version=\""+theme_version+"\"";
    if(theme_tooltipfont!=THEME_TOOLTIPFONT_DEFAULT) code+=" tooltipfont=\""+theme_tooltipfont+"\"";
    if(theme_magnet!=THEME_MAGNET_DEFAULT) code+=" magnet=\""+String.valueOf(theme_magnet)+"\"";
    if(theme_alpha!=THEME_ALPHA_DEFAULT) code+=" alpha=\""+String.valueOf(theme_alpha)+"\"";
    if(theme_movealpha!=THEME_MOVEALPHA_DEFAULT) code+=" movealpha=\""+String.valueOf(theme_movealpha)+"\"";
    code+=">\n";
    code+="<ThemeInfo";
    code+=" name=\""+themeinfo_name+"\"";
    code+=" author=\""+themeinfo_author+"\"";
    code+=" email=\""+themeinfo_email+"\"";
    code+=" webpage=\""+themeinfo_webpage+"\"";
    code+="/>\n";    
    code+="<!-- This skin was created using the VLC Skin Editor by Daniel Dreibrodt (http://forum.videolan.org/viewtopic.php?f=15&t=38973)-->\n";
    for (int i=0;i<resources.size();i++) {
      code+=resources.get(i).returnCode();
    }
    code+="\n";
    for (int i=0;i<windows.size();i++) {
      code+=windows.get(i).returnCode();      
    }
    code += "\n</Theme>";
    return code;
  }
  /** Recreates the resource hierarchies **/
  public void updateResources() {    
    m.res_tree_model.setRoot(getResourcesTree());    
    m.saved=false;
  }
  /** Recreates the window hierarchies **/
  public void updateWindows() {    
    m.win_tree_model.setRoot(getWindowsTree());
    m.saved=false;
  }
  /** Recreates the item hierarchies **/
  public void updateItems() {    
    m.items_tree_model.setRoot(getItemsTree());
    m.saved=false;
  }
  /** Redraws the preview window (deprecated already before it was used)
   *  @Deprecated useless! Preview is now planned to be updated automatically every 1000/25 ms
   **/
  @Deprecated public void updatePreview() {
    /**empty*/
  }
  /** Updates everything **/
  public void update() {
    updateResources();
    updateWindows();
    updateItems();
    updatePreview();
    m.saved=false;
  }
}
