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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import vlcskineditor.history.ThemeEditEvent;
import vlcskineditor.resources.Bitmap;
import vlcskineditor.resources.Font;
import vlcskineditor.resources.ImageResource;
import vlcskineditor.resources.SubBitmap;

/**
 * Represents a skin file. Stores all Resources and Windows, which in turn store the Layouts that contain the items.
 * @author Daniel
 */
public class Skin implements ActionListener{
  
  public java.util.List<Resource> resources = new LinkedList<Resource>();
  public java.util.List<Window> windows = new LinkedList<Window>();
  final String HEADER = "<!DOCTYPE Theme PUBLIC \"-//VideoLAN//DTD VLC Skins V2.0//EN\" \"skin.dtd\">";
  final String THEME_VERSION_DEFAULT = "2.0";
  final String THEME_TOOLTIPFONT_DEFAULT = "defaultfont";
  final int THEME_MAGNET_DEFAULT = 15;
  final int THEME_ALPHA_DEFAULT = 255;
  final int THEME_MOVEALPHA_DEFAULT = 255;
  String theme_version = THEME_VERSION_DEFAULT;
  String theme_tooltipfont = THEME_TOOLTIPFONT_DEFAULT;
  public int theme_magnet = THEME_MAGNET_DEFAULT;
  public int theme_alpha = THEME_ALPHA_DEFAULT;
  public int theme_movealpha = THEME_MOVEALPHA_DEFAULT;
  final String THEMEINFO_NAME_DEFAULT = Language.get("THEMEINFO_NAME_DEFAULT");
  final String THEMEINFO_AUTHOR_DEFAULT = Language.get("THEMEINFO_AUTHOR_DEFAULT");
  final String THEMEINFO_EMAIL_DEFAULT = Language.get("THEMEINFO_EMAIL_DEFAULT");
  final String THEMEINFO_WEBPAGE_DEFAULT = "http://www.videolan.org/vlc/";
  public String themeinfo_name = THEMEINFO_NAME_DEFAULT;
  public String themeinfo_author = THEMEINFO_AUTHOR_DEFAULT;
  public String themeinfo_email = THEMEINFO_EMAIL_DEFAULT;
  public String themeinfo_webpage = THEMEINFO_WEBPAGE_DEFAULT;
  File skinfile;
  public String skinfolder;
  public Main m;
  //Currently selected window or parent window of currently selected Layout
  Window active_window = null;
  //Currently selected layout
  Layout active_layout = null;
  int unnamed_ids = 0;  
  
  JFrame theme_frame;
  JTextField themeinfo_name_tf, themeinfo_author_tf, themeinfo_email_tf, themeinfo_webpage_tf;
  JTextField theme_magnet_tf, theme_alpha_tf, theme_movealpha_tf;
  JButton theme_ok_btn, theme_cancel_btn, theme_help_btn;
  
  //Manager of the global variables
  public GlobalVariables gvars = new GlobalVariables(this);
  
  //Default indentation of generated XML code (2 spaces)
  public static String indentation = "  ";
  
  /**
   * Constructs a new skin manager
   * @param m_ The current instance of the main interface, needed for GUI interaction
   */
  public Skin(Main m_) {
    m=m_;    
  }
  
  /** 
   * Creates a new empty skins in the given file
   * @param f The target file
   **/
  public void createNew(File f) {
    skinfile=f;
    skinfolder = f.getParentFile().getAbsolutePath()+File.separator;    
    try {
      skinfile.createNewFile();
    }
    catch(Exception ex) {
      JOptionPane.showMessageDialog(null,Language.get("ERROR_NEWSKIN_TITLE")+"\n\n"+ex.toString(),Language.get("ERROR_NEWSKIN_TITLE"),JOptionPane.ERROR_MESSAGE);
    }
    themeinfo_author = System.getProperty("user.name");
  }
  
  /**
   * Handles the parsing of a given file into the skin structure
   * @param f
   */
  public void open(File f) {    
    resources.clear();
    windows.clear();
    skinfile=f;
    skinfolder = f.getParentFile().getAbsolutePath()+File.separator;    
    try {
      parseXML(f);
    } 
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
      update();
      m.showWelcomeDialog();
    }    
    update();
    for(Window w:windows) {
      for(Layout l:w.layouts) {
        for(Item i:l.items) {
          i.updateToGlobalVariables();
        }
      }
    }
  }
  
  /**
   * Parses the skin file using Java's built-in XML support
   * @param f The file that should be parsed
   * @throws java.lang.Exception
   */
  private void parseXML(File f) throws Exception{    
    //Workaround for DTD loading
    File dtd = new File(f.getParent(),"skin.dtd");
    if(!dtd.exists()) {
      File included_dtd = new File("skin.dtd");
      if(included_dtd.exists()) {
        Helper.copyFile(included_dtd,dtd);
        dtd.deleteOnExit();
      }
    }
    //Now that the DTD is in the right place parse the XML
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setIgnoringComments(true);
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(f);
    //Parse all the nodes
    NodeList nodes = doc.getChildNodes();
    for(int i=0;i<nodes.getLength();i++) {
      parseNode(nodes.item(i));
    }
  }
  
  /**
   * Parses all the children of a DOM node
   * @param n The node to parse
   * @throws java.lang.Exception
   */
  private void parseNodeChildren(Node n) throws Exception{
    NodeList nodes = n.getChildNodes();
    for(int i=0;i<nodes.getLength();i++) {     
      parseNode(nodes.item(i));
    }
  }
  
  /**
   * Parses a DOM node
   * @param n The node to parse
   * @throws java.lang.Exception
   */
  private void parseNode(Node n) throws Exception{
    if(n.getNodeType()==Node.DOCUMENT_TYPE_NODE) {
      if(!n.getNodeName().equals("Theme")) {
        throw new Exception(Language.get("ERROR_SKIN_INVALID"));
      }
    } else if(n.getNodeType()==Node.ELEMENT_NODE) {
      if(n.getNodeName().equals("Theme")) {
        if(n.getAttributes().getNamedItem("version")!=null) {
          theme_version = n.getAttributes().getNamedItem("version").getNodeValue();
          if(Double.parseDouble(theme_version)!=2.0)
            throw new Exception(Language.get("ERROR_VERSION_UNSUPPORTED"));
          parseNodeChildren(n);
        }
      } else if(n.getNodeName().equals("ThemeInfo")) {
        if(n.getAttributes().getNamedItem("author")!=null) 
          themeinfo_author = n.getAttributes().getNamedItem("author").getNodeValue();
        if(n.getAttributes().getNamedItem("name")!=null) 
          themeinfo_name = n.getAttributes().getNamedItem("name").getNodeValue();
        if(n.getAttributes().getNamedItem("email")!=null) 
          themeinfo_email = n.getAttributes().getNamedItem("email").getNodeValue();
        if(n.getAttributes().getNamedItem("webpage")!=null) 
          themeinfo_webpage = n.getAttributes().getNamedItem("webpage").getNodeValue();
      } else if(n.getNodeName().equals("Bitmap")) {
        resources.add(new Bitmap(n,this));
      } else if(n.getNodeName().equals("Font")) {
        resources.add(new Font((Element)n,this));
      } else if(n.getNodeName().equals("Window")) {
        windows.add(new Window(n, this));
      }
    }    
  }  

  /** Saves the XML Code into the skinfile **/
  public void save() {   
    try {
      FileOutputStream fos = new FileOutputStream(skinfile);
      fos.write(returnCode().getBytes("UTF-8"));
      fos.close();
    }
    catch(Exception ex) {
      JOptionPane.showMessageDialog(null,Language.get("ERROR_SAVE_MSG")+"\n\n"+ex.toString(),Language.get("ERROR_SAVE_TITLE"),JOptionPane.ERROR_MESSAGE);
    }
   
  }
  /** Show the theme setting editing dialog **/
  public void showThemeOptions() {
    if(theme_frame==null) {
      theme_frame = new JFrame(Language.get("WIN_THEME_TITLE"));
      theme_frame.setResizable(false);
      theme_frame.setLayout(new FlowLayout());
      theme_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
      JLabel themeinfo_name_l = new JLabel(Language.get("WIN_THEME_NAME"));
      themeinfo_name_tf = new JTextField();
      JLabel themeinfo_author_l = new JLabel(Language.get("WIN_THEME_AUTHOR"));
      themeinfo_author_tf = new JTextField();
      JLabel themeinfo_email_l = new JLabel(Language.get("WIN_THEME_EMAIL"));
      themeinfo_email_tf = new JTextField();
      JLabel themeinfo_webpage_l = new JLabel(Language.get("WIN_THEME_WEB"));
      themeinfo_webpage_tf = new JTextField();
      JLabel theme_magnet_l = new JLabel(Language.get("WIN_THEME_MAGNET"));
      theme_magnet_tf = new JTextField();
      theme_magnet_tf.setDocument(new NumbersOnlyDocument(false));
      theme_magnet_tf.setToolTipText(Language.get("WIN_THEME_MAGNET_TIP"));
      JLabel theme_alpha_l = new JLabel(Language.get("WIN_THEME_ALPHA"));
      theme_alpha_tf = new JTextField();
      theme_alpha_tf.setDocument(new NumbersOnlyDocument(false));
      theme_alpha_tf.setToolTipText(Language.get("WIN_THEME_ALPHA_TIP"));
      JLabel theme_movealpha_l = new JLabel(Language.get("WIN_THEME_MOVEALPHA"));
      theme_movealpha_tf = new JTextField();
      theme_alpha_tf.setDocument(new NumbersOnlyDocument(false));
      theme_alpha_tf.setToolTipText(Language.get("WIN_THEME_MOVEALPHA_TIP"));
      theme_ok_btn = new JButton(Language.get("BUTTON_OK"));
      theme_ok_btn.addActionListener(this);
      theme_cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      theme_cancel_btn.addActionListener(this);
      theme_help_btn = new JButton(Language.get("BUTTON_HELP"));
      theme_help_btn.addActionListener(this);

      //Distance of textfields to WEST edge of container
      Component[] labels = { themeinfo_name_l, themeinfo_author_l, themeinfo_email_l, themeinfo_webpage_l, theme_magnet_l, theme_alpha_l, theme_movealpha_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
      
      JPanel themeinfo_panel = new JPanel(null);
      themeinfo_panel.add(themeinfo_name_l);
      themeinfo_panel.add(themeinfo_name_tf);
      themeinfo_name_tf.setPreferredSize(new Dimension(tf_wd, themeinfo_name_tf.getPreferredSize().height));
      themeinfo_panel.add(themeinfo_author_l);
      themeinfo_panel.add(themeinfo_author_tf);
      themeinfo_panel.add(themeinfo_email_l);
      themeinfo_panel.add(themeinfo_email_tf);
      themeinfo_panel.add(themeinfo_webpage_l);
      themeinfo_panel.add(themeinfo_webpage_tf);
      themeinfo_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_THEME_INFO_TITLE")));

      SpringLayout themeinfo_layout = new SpringLayout();
      themeinfo_panel.setLayout(themeinfo_layout);

      themeinfo_layout.putConstraint(SpringLayout.NORTH, themeinfo_name_l, 5, SpringLayout.NORTH, themeinfo_panel);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_name_l, 5, SpringLayout.WEST, themeinfo_panel);

      themeinfo_layout.putConstraint(SpringLayout.VERTICAL_CENTER, themeinfo_name_tf, 0, SpringLayout.VERTICAL_CENTER, themeinfo_name_l);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_name_tf, tf_dx, SpringLayout.WEST, themeinfo_panel);

      themeinfo_layout.putConstraint(SpringLayout.NORTH, themeinfo_author_l, 10, SpringLayout.SOUTH, themeinfo_name_tf);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_author_l, 5, SpringLayout.WEST, themeinfo_panel);

      themeinfo_layout.putConstraint(SpringLayout.VERTICAL_CENTER, themeinfo_author_tf, 0, SpringLayout.VERTICAL_CENTER, themeinfo_author_l);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_author_tf, tf_dx, SpringLayout.WEST, themeinfo_panel);
      themeinfo_layout.putConstraint(SpringLayout.EAST, themeinfo_author_tf, 0, SpringLayout.EAST, themeinfo_name_tf);

      themeinfo_layout.putConstraint(SpringLayout.NORTH, themeinfo_email_l, 10, SpringLayout.SOUTH, themeinfo_author_tf);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_email_l, 5, SpringLayout.WEST, themeinfo_panel);

      themeinfo_layout.putConstraint(SpringLayout.VERTICAL_CENTER, themeinfo_email_tf, 0, SpringLayout.VERTICAL_CENTER, themeinfo_email_l);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_email_tf, tf_dx, SpringLayout.WEST, themeinfo_panel);
      themeinfo_layout.putConstraint(SpringLayout.EAST, themeinfo_email_tf, 0, SpringLayout.EAST, themeinfo_name_tf);

      themeinfo_layout.putConstraint(SpringLayout.NORTH, themeinfo_webpage_l, 10, SpringLayout.SOUTH, themeinfo_email_tf);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_webpage_l, 5, SpringLayout.WEST, themeinfo_panel);

      themeinfo_layout.putConstraint(SpringLayout.VERTICAL_CENTER, themeinfo_webpage_tf, 0, SpringLayout.VERTICAL_CENTER, themeinfo_webpage_l);
      themeinfo_layout.putConstraint(SpringLayout.WEST, themeinfo_webpage_tf, tf_dx, SpringLayout.WEST, themeinfo_panel);
      themeinfo_layout.putConstraint(SpringLayout.EAST, themeinfo_webpage_tf, 0, SpringLayout.EAST, themeinfo_name_tf);

      themeinfo_layout.putConstraint(SpringLayout.EAST, themeinfo_panel, 5, SpringLayout.EAST, themeinfo_name_tf);
      themeinfo_layout.putConstraint(SpringLayout.SOUTH, themeinfo_panel, 10, SpringLayout.SOUTH, themeinfo_webpage_tf);

      theme_frame.add(themeinfo_panel);
      
      JPanel theme_panel = new JPanel(null);
      theme_panel.add(theme_magnet_l);
      theme_panel.add(theme_magnet_tf);
      theme_magnet_tf.setPreferredSize(new Dimension(tf_wd, theme_magnet_tf.getPreferredSize().height));
      theme_panel.add(theme_alpha_l);
      theme_panel.add(theme_alpha_tf);
      theme_panel.add(theme_movealpha_l);
      theme_panel.add(theme_movealpha_tf);
      theme_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_THEME_ATTR_TITLE")));

      SpringLayout theme_layout = new SpringLayout();
      theme_panel.setLayout(theme_layout);

      theme_layout.putConstraint(SpringLayout.NORTH, theme_magnet_l, 5, SpringLayout.NORTH, theme_panel);
      theme_layout.putConstraint(SpringLayout.WEST, theme_magnet_l, 5, SpringLayout.WEST, theme_panel);

      theme_layout.putConstraint(SpringLayout.VERTICAL_CENTER, theme_magnet_tf, 0, SpringLayout.VERTICAL_CENTER, theme_magnet_l);
      theme_layout.putConstraint(SpringLayout.WEST, theme_magnet_tf, tf_dx, SpringLayout.WEST, theme_panel);

      theme_layout.putConstraint(SpringLayout.NORTH, theme_alpha_l, 10, SpringLayout.SOUTH, theme_magnet_tf);
      theme_layout.putConstraint(SpringLayout.WEST, theme_alpha_l, 5, SpringLayout.WEST, theme_panel);

      theme_layout.putConstraint(SpringLayout.VERTICAL_CENTER, theme_alpha_tf, 0, SpringLayout.VERTICAL_CENTER, theme_alpha_l);
      theme_layout.putConstraint(SpringLayout.WEST, theme_alpha_tf, tf_dx, SpringLayout.WEST, theme_panel);
      theme_layout.putConstraint(SpringLayout.EAST, theme_alpha_tf, 0, SpringLayout.EAST, theme_magnet_tf);

      theme_layout.putConstraint(SpringLayout.NORTH, theme_movealpha_l, 10, SpringLayout.SOUTH, theme_alpha_tf);
      theme_layout.putConstraint(SpringLayout.WEST, theme_movealpha_l, 5, SpringLayout.WEST, theme_panel);

      theme_layout.putConstraint(SpringLayout.VERTICAL_CENTER, theme_movealpha_tf, 0, SpringLayout.VERTICAL_CENTER, theme_movealpha_l);
      theme_layout.putConstraint(SpringLayout.WEST, theme_movealpha_tf, tf_dx, SpringLayout.WEST, theme_panel);
      theme_layout.putConstraint(SpringLayout.EAST, theme_movealpha_tf, 0, SpringLayout.EAST, theme_magnet_tf);

      theme_layout.putConstraint(SpringLayout.EAST, theme_panel, 5, SpringLayout.EAST, theme_magnet_tf);
      theme_layout.putConstraint(SpringLayout.SOUTH, theme_panel, 10, SpringLayout.SOUTH, theme_movealpha_tf);
      
      theme_frame.add(theme_panel);
      theme_frame.add(theme_ok_btn);
      theme_frame.add(theme_cancel_btn);
      theme_frame.add(theme_help_btn);
      
      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, themeinfo_panel, 5, SpringLayout.NORTH, theme_frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, themeinfo_panel, 5, SpringLayout.WEST, theme_frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, theme_panel, 10, SpringLayout.SOUTH, themeinfo_panel);
      layout.putConstraint(SpringLayout.WEST, theme_panel, 5, SpringLayout.WEST, theme_frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, theme_ok_btn, 10, SpringLayout.SOUTH, theme_panel);
      layout.putConstraint(SpringLayout.WEST, theme_ok_btn, 5, SpringLayout.WEST, theme_frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, theme_cancel_btn, 0, SpringLayout.NORTH, theme_ok_btn);
      layout.putConstraint(SpringLayout.WEST, theme_cancel_btn, 5, SpringLayout.EAST, theme_ok_btn);

      layout.putConstraint(SpringLayout.NORTH, theme_help_btn, 0, SpringLayout.NORTH, theme_cancel_btn);
      layout.putConstraint(SpringLayout.WEST, theme_help_btn, 5, SpringLayout.EAST, theme_cancel_btn);

      layout.putConstraint(SpringLayout.SOUTH, theme_frame.getContentPane(), 10, SpringLayout.SOUTH, theme_ok_btn);
      layout.putConstraint(SpringLayout.EAST, theme_frame.getContentPane(), 5, SpringLayout.EAST, themeinfo_panel);

      theme_frame.setLayout(layout);
      
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
        JOptionPane.showMessageDialog(theme_frame,Language.get("ERROR_ALPHA_MSG"),Language.get("ERROR_ALPHA_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(theme_movealpha_tf.getText())>255 || Integer.parseInt(theme_movealpha_tf.getText())<1) {
        JOptionPane.showMessageDialog(theme_frame,Language.get("ERROR_ALPHA_MSG"),Language.get("ERROR_ALPHA_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      ThemeEditEvent tee = new ThemeEditEvent(this);
      themeinfo_name = themeinfo_name_tf.getText();
      themeinfo_author = themeinfo_author_tf.getText();
      themeinfo_email = themeinfo_email_tf.getText();
      themeinfo_webpage = themeinfo_webpage_tf.getText();
      theme_magnet = Integer.parseInt(theme_magnet_tf.getText());
      theme_alpha = Integer.parseInt(theme_alpha_tf.getText());
      theme_movealpha = Integer.parseInt(theme_movealpha_tf.getText());
      theme_frame.setVisible(false);
      theme_frame.dispose();
      theme_frame = null;
      tee.setNew();
      m.hist.addEvent(tee);
    }
    else if(e.getSource().equals(theme_help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/theme.html");
    }
    else if(e.getSource().equals(theme_cancel_btn)) {
      theme_frame.setVisible(false);
      theme_frame.dispose();
      theme_frame = null;
    }
  }
  /** Generates a new number for unnamed items so that every id is unique **/  
  public int getNewId() {
    unnamed_ids++;
    return unnamed_ids;
  }
  /** Checks whether an id exists already **/
  public boolean idExists(String id) {
    if(getResource(id)!=null) return true;
    for(Window w:windows) {
      if(w.id.equals(id)) return true;
      for(Layout l:w.layouts) {
        if(l.id.equals(id)) return true;
        if(l.getItem(id)!=null) return true;
      }
    }
    return false;
  }

  public ImageResource getImageResource(String id) {
    if(id==null) return null;
    for (Resource r:resources) {
      if(r.getClass()==Bitmap.class) {
        if(r.id.equals(id)) return (ImageResource)r;
        Bitmap bmp = (Bitmap)r;
        for(SubBitmap s:bmp.SubBitmaps) {
          if(s.id.equals(id)) return (ImageResource)s;
        }
      }
    }
    return null;
  }
  
  /** Returns the resource represented by the given id **/
  public Resource getResource(String id) {    
    for (Resource r:resources) {
      if(r.id.equals(id)) return r;
      if(r.getClass()==Bitmap.class) {
        Bitmap bmp = (Bitmap)r;
        for(SubBitmap s:bmp.SubBitmaps) {
          if(s.id.equals(id)) return s;
        }
      }
    }
    return null;
  }
  
  public java.awt.Font getFont(String id) {
    if(id.equals("defaultfont")) {
      return (new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,12));
      }
    Resource r = getResource(id);
    if(r==null) return (new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,12));
    try {
      if(r.getClass()==vlcskineditor.resources.Font.class) {
        vlcskineditor.resources.Font fr = (vlcskineditor.resources.Font)r;
        return fr.f;
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      return (new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,12));
    }
    return (new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN,12));
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
    for(Window w:windows) {
      for(Layout l:w.layouts) {
        if(l.uses(id_)) return true;
      }
    }
    return false;
  }

  /** Returns the parent element that contains the item of the given id **/
  public java.util.List<Item> getParentListOf(String id_) {
    return active_layout.getParentListOf(id_);    
  }
  /** Returns the list of the given Group/Panel that contains the items **/
  public java.util.List<Item> getListOf(String id_) {
    Item i = getItem(id_);    
    java.util.List<Item> l = null;          
    try {
      vlcskineditor.items.Panel p = (vlcskineditor.items.Panel)i;
      l = p.items;
    }
    catch(Exception ex1) {
      try {
        vlcskineditor.items.Group g = (vlcskineditor.items.Group)i;
        l = g.items;
      }
      catch(Exception ex2) {
        ex2.printStackTrace();
      }
    }
    return l;
  }
  /** Return the item with the given id **/
  public Item getItem(String id_) {    
    active_window = null;
    active_layout = null;
    if(m.getSelectedWindow()!=null && m.getSelectedLayout()!=null) {
      active_window = getWindow(m.getSelectedWindow());
      if(active_window!=null) {
        active_layout = active_window.getLayout(m.getSelectedLayout());
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
    DefaultMutableTreeNode bitmaps_node = new DefaultMutableTreeNode("Root: "+Language.get("WIN_RES_BITMAPS"));
    DefaultMutableTreeNode fonts_node = new DefaultMutableTreeNode("Root: "+Language.get("WIN_RES_FONTS"));
    for(Resource r:resources) {
      if(r.getClass()==Bitmap.class) {
        bitmaps_node.add(r.getTreeNode());
      }
    }
    for(Resource r:resources) {
      if(r.getClass()==vlcskineditor.resources.Font.class) {
        DefaultMutableTreeNode node = r.getTreeNode();       
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
    if(m.getSelectedWindow()!=null && m.getSelectedLayout()!=null) {
      active_window = getWindow(m.getSelectedWindow());
      if(active_window!=null) {
        active_layout = active_window.getLayout(m.getSelectedLayout());
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
    if(!theme_tooltipfont.equals(THEME_TOOLTIPFONT_DEFAULT)) code+=" tooltipfont=\""+theme_tooltipfont+"\"";
    if(theme_magnet!=THEME_MAGNET_DEFAULT) code+=" magnet=\""+String.valueOf(theme_magnet)+"\"";
    if(theme_alpha!=THEME_ALPHA_DEFAULT) code+=" alpha=\""+String.valueOf(theme_alpha)+"\"";
    if(theme_movealpha!=THEME_MOVEALPHA_DEFAULT) code+=" movealpha=\""+String.valueOf(theme_movealpha)+"\"";
    code+=">\n";
    code+=Skin.indentation+"<ThemeInfo";
    code+=" name=\""+themeinfo_name+"\"";
    code+=" author=\""+themeinfo_author+"\"";
    code+=" email=\""+themeinfo_email+"\"";
    code+=" webpage=\""+themeinfo_webpage+"\"";
    code+="/>\n\n";    
    code+=Skin.indentation+"<!-- Created using the VLC Skin Editor "+Main.VERSION+" (http://www.videolan.org/vlc/skineditor.html)-->\n\n";
    for (int i=0;i<resources.size();i++) {
      code+=resources.get(i).returnCode(Skin.indentation);
    }
    code+="\n";
    for (int i=0;i<windows.size();i++) {
      code+=windows.get(i).returnCode(Skin.indentation);      
    }
    code += "\n</Theme>";
    return code;
  }
  /** Recreates the resource hierarchies **/
  public void updateResources() {  
    m.res_tree_model.setRoot(getResourcesTree());
    m.saved=false;
    m.res_tree.repaint();
  }
  /** Recreates the window hierarchies **/
  public void updateWindows() {
    m.win_tree_model.setRoot(getWindowsTree());
    m.saved=false;
    m.win_tree.repaint();
  }
  /** Recreates the item hierarchies **/
  public void updateItems() {
    m.items_tree_model.setRoot(getItemsTree());
    m.saved=false;
    m.items_tree.repaint();
  }
  /** Updates everything **/
  public void update() {
    updateResources();
    updateWindows();
    updateItems();
    m.saved=false;
  }
  /** Make an Resource of the given id visible in the tree (expand the TreePath) **/
  public void expandResource(String id) {   
    Resource r = getResource(id);
    if(r==null) return;
    if(ImageResource.class.isAssignableFrom(r.getClass())) {
      TreePath tp = findInTree(m.res_tree,"Root: "+Language.get("WIN_RES_BITMAPS"));
      m.res_tree.expandPath(tp);
    }
    else if(r.getClass().equals(Font.class)) {
      TreePath tp = findInTree(m.res_tree,"Root: "+Language.get("WIN_RES_FONTS"));
      m.res_tree.expandPath(tp);
    }
    else {
      System.err.println("Error encountered while trying to expand a resource:");
      System.err.println("  Resource of the given id is neither a Font nor a Bitmap its a "+r.type);
      return;
    }     
    
    Resource pr = r;
    for(Resource res:resources) {
      if(res.getParentOf(id)!=null) {
        TreePath tp = findInTree(m.res_tree,res.id);
        if(tp==null) {
          System.err.println("Could not find Parent: "+res.id);
          return;
        }
        m.res_tree.expandPath(tp);
        TreePath stp = findInTree(m.res_tree,id);
        if(stp==null) return;
        m.res_tree.setSelectionPath(stp);
        break;
      }
    }
  }
  /** Make a Layout of the given id visible **/
  public void expandLayout(String id) {       
    TreePath wtp = null;
    if(active_window!=null) wtp = findInTree(m.win_tree,active_window.id);
    else wtp = findInTree(m.win_tree,m.getSelectedLayout());
    if(wtp==null) return;
    m.win_tree.expandPath(wtp);
    
    TreePath ltp = findInTree(m.win_tree,id);
    if(ltp==null) return;
    m.win_tree.setSelectionPath(ltp);
  }  
  /**
   * Make an item of the given id visible in the tree (expand the TreePath)
   * @param id The id of the item to expand
   **/
  public void expandItem(String id) {
    java.util.List<String> parents = new LinkedList<String>();
    Item p = getItem(id);
    if(p==null) return;
    //Trace parents
    while((p=active_layout.getParentOf(p.id))!=null) {      
      parents.add(p.id);
    }
    //Open each parent, starting with the highest one in hierarchy
    for(int i=parents.size()-1;i>=0;i--) {      
      TreePath tp = findInTree(m.items_tree,parents.get(i));
      if(tp==null) {
        System.err.println("Could not find Parent: "+parents.get(i));
        return;
      }
      m.items_tree.expandPath(tp);
    }
    //Finding selection path
    TreePath stp = findInTree(m.items_tree,id);
    if(stp==null) return;
    m.items_tree.setSelectionPath(stp);
  }

  /**
   * Finds the first occurence of id in the first expanded tree in the given JTree
   * @param jt The JTree in which to search
   * @param id The ID of the item to look for
   * @return The path to the element in the tree which has the given ID. If the
   * item cannot be found in the tree <b>null</b> is returned.
   **/
  public TreePath findInTree(JTree jt, String id) {
    //Get number of rows in tree
    int max = jt.getRowCount();
    //Set row index to 0
    //int row = 0;
    //Check each row of the tree
    //do {
    for(int row=0;row<max;row++) {
        //Get path to row
        TreePath path = jt.getPathForRow(row);
        //Get name of path's last element
        String text = path.getLastPathComponent().toString();
        //If this row contains the desired item return the path to it.
        if (text.toUpperCase().endsWith(id.toUpperCase()))
            return path;
    }
        //row = (row + 1 + max) % max;
    //} while (row != 0);
    return null;
  }  
}
