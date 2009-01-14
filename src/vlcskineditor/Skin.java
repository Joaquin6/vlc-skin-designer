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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
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
import vlcskineditor.resources.BitmapFont;
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
  Window active_window = null;
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
      //parse(f);
      parseXML(f);
    } 
    catch (Exception ex) {
      ex.printStackTrace();
      String stackTrace ="";
      /*for (int i=0;i<ex.getStackTrace().length;i++) {
        stackTrace+=ex.getStackTrace()[i].toString()+"\n";
      }*/
      JOptionPane.showMessageDialog(null,ex.toString()+"\n\n"+stackTrace,ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
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
   * Parses the given file line by line, expecting each xml-tag to be in his own single line
   * @deprecated parseXML(File f) should now be used, as it is more flexible
   * @param f The file that should be parsed
   * @throws java.lang.Exception
   */
  private void parse(File f) throws Exception {
    //System.out.println("Creating Buffered Reader...");
    BufferedReader br = new BufferedReader(new FileReader(f));
    //System.out.println("Ready...");
    //System.out.println("Reading Header...");
    String header = br.readLine();
    //System.out.println("Header read");
    if (header.indexOf("//VideoLAN//DTD VLC Skins")==-1) {
      br.close();        
      System.err.println("Invalid header:\n"+header);
      throw new Exception(Language.get("ERROR_SKIN_INVALID"));        

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
      if (line.startsWith("<!--")) {
        while(line.indexOf("-->")==-1) {
          line = br.readLine();
        }
      }
      //<editor-fold defaultstate="collapsed" desc=" ThemeInfo tag "> 
      else if(line.startsWith("<ThemeInfo")) {         
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
      else if(line.startsWith("<Font")) resources.add(new Font(line,this));
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
      FileWriter writer = new FileWriter(skinfile);
      writer.write(returnCode());
      writer.flush();
      writer.close();
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
      theme_frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      
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
      theme_ok_btn.setPreferredSize(new Dimension(70,25));
      theme_cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      theme_cancel_btn.addActionListener(this);
      theme_cancel_btn.setPreferredSize(new Dimension(70,25));
      theme_help_btn = new JButton(Language.get("BUTTON_HELP"));
      theme_help_btn.addActionListener(this);
      theme_help_btn.setPreferredSize(new Dimension(70,25));
      
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
      themeinfo_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_THEME_INFO_TITLE")));
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
      theme_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_THEME_ATTR_TITLE")));
      theme_p.setMinimumSize(new Dimension(315,105));
      theme_p.setMaximumSize(new Dimension(315,105));
      theme_p.setPreferredSize(new Dimension(315,105));
      
      theme_frame.add(theme_p);
      theme_frame.add(theme_ok_btn);
      theme_frame.add(theme_cancel_btn);
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
  
  /**
   * Returns the image object of a bitmap
   * @deprecated Use getImageResource instead
   * @param id The id of the Bitmap resource
   * @return the BufferedImage represented by the resource looked for
   */
  public BufferedImage getBitmapImage(String id) {
    Resource r = getResource(id);
    if(r==null) {
      return null;
    }
    else {
      if(r.getClass()==Bitmap.class) {        
        Bitmap b = (Bitmap)r;
        return b.image;        
      }
      else if(r.getClass()==SubBitmap.class) {
        SubBitmap sb = (SubBitmap)r;
        return sb.image;
      }
      else return null;
    }
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
    else wtp = findInTree(m.win_tree,m.selected_window);
    if(wtp==null) return;
    m.win_tree.expandPath(wtp);
    
    TreePath ltp = findInTree(m.win_tree,id);
    if(ltp==null) return;
    m.win_tree.setSelectionPath(ltp);
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
  /** Make an item of the given id visible in the tree (expand the TreePath) **/
  public void expandItem(String id) {
    java.util.List<String> parents = new LinkedList<String>();
    Item p = getItem(id);
    if(p==null) return;
    while((p=active_layout.getParentOf(p.id))!=null) {      
      parents.add(p.id);
    }
    for(int i=parents.size()-1;i>=0;i--) {      
      TreePath tp = findInTree(m.items_tree,parents.get(i));
      if(tp==null) {
        System.err.println("Could not find Parent: "+parents.get(i));
        return;
      }
      m.items_tree.expandPath(tp);
      TreePath stp = findInTree(m.items_tree,id);
      if(stp==null) return;
      m.items_tree.setSelectionPath(stp);
    }    
  }
  /** Finds the first occurence of id in the first expanded tree in the given JTree**/
  public TreePath findInTree(JTree jt, String id) {    
    int max = jt.getRowCount();		
    int row = 0;
    do {
        TreePath path = jt.getPathForRow(row);
        String text = path.getLastPathComponent().toString();
        if (text.toUpperCase().indexOf(id.toUpperCase())!=-1) return path;
        row = (row + 1 + max) % max;
    } while (row != 0);
    return null;
  }
  /** Updates everything **/
  public void update() {
    updateResources();
    updateWindows();
    updateItems();    
    m.saved=false;
  }
}
