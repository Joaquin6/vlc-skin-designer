/*****************************************************************************
 * Main.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * VLC Skin Editor
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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.imageio.*;
import vlcskineditor.Items.*;
import vlcskineditor.Resources.*;
import com.ice.tar.*;
import com.ice.jni.registry.*;


/**
 * The main class holds the GUI
 * @author Daniel
 */
public class Main extends javax.swing.JFrame implements ActionListener, TreeSelectionListener, WindowListener{
  final String VERSION = "0.3.5a";
  String vlc_dir = "";
  String vlc_skins_dir = "";
  JMenuBar mbar;
  JMenu m_file, m_edit, m_help;
  JMenuItem m_file_new, m_file_open, m_file_save, m_file_test, m_file_vlt, m_file_quit;
  JMenuItem m_edit_theme, m_edit_global;
  JMenuItem m_help_doc, m_help_about;  
  JDesktopPane jdesk;
  JInternalFrame resources,windows,items,current_window;  
  JTree res_tree,win_tree,items_tree;  
  DefaultTreeModel res_tree_model, win_tree_model, items_tree_model;
  JButton res_add_bitmap, res_add_font, res_edit, res_del;
  JPopupMenu res_add_bitmap_pu;
  JMenuItem res_add_bitmap_pu_b, res_add_bitmap_pu_s;
  JButton win_add_window, win_add_layout, win_edit, win_del;  
  JButton items_add, items_up, items_down, items_edit, items_del;
  JPopupMenu items_add_pu;  
  JMenu items_add_pu_tp;
  JMenuItem items_add_pu_tp_anchor, items_add_pu_tp_button, items_add_pu_tp_checkbox;
  JMenuItem items_add_pu_tp_image, items_add_pu_tp_panel;
  JMenuItem items_add_pu_tp_playtree, items_add_pu_tp_slider, items_add_pu_tp_text, items_add_pu_tp_video;
  JMenuItem items_add_pu_anchor, items_add_pu_button, items_add_pu_checkbox;
  JMenuItem items_add_pu_image, items_add_pu_panel;
  JMenuItem items_add_pu_playtree, items_add_pu_slider, items_add_pu_text, items_add_pu_video;  
  Skin s;
  ImageIcon add_bitmap_icon = createIcon("icons/add_bitmap.png");
  ImageIcon add_subbitmap_icon = createIcon("icons/add_subbitmap.png");
  ImageIcon add_font_icon = createIcon("icons/add_font.png");
  ImageIcon edit_icon = createIcon("icons/edit.png");
  ImageIcon delete_icon = createIcon("icons/delete.png");
  ImageIcon add_window_icon = createIcon("icons/add_window.png");
  ImageIcon add_layout_icon = createIcon("icons/add_layout.png");  
  ImageIcon add_icon = createIcon("icons/add.png");
  ImageIcon up_icon = createIcon("icons/move_up.png");
  ImageIcon down_icon = createIcon("icons/move_down.png");
  ImageIcon icon = createIcon("icons/icon.png");
  DefaultTreeCellRenderer tree_renderer = new TreeRenderer();  
  String selected_resource, selected_in_windows, selected_window, selected_layout, selected_item;
  JFileChooser fc, bitmap_adder, font_adder, vlt_saver;
  public boolean saved = false;
  int res_tree_sel_x, res_tree_sel_y;
  
  /** Launches the skin editor and initializes GUI and Skin DOM*/
  public Main() {
    setTitle("New skin - VLC Skin Editor "+VERSION);
    setDefaultCloseOperation(EXIT_ON_CLOSE);    
    addWindowListener(this);
    setSize(800,700);
    setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("icons/icon16.png")));
    try {	
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } 
    catch (Exception e) {
      
    }
    mbar = new JMenuBar();
    
    m_file = new JMenu("File");
    m_file.setMnemonic("F".charAt(0));
    m_file_new = new JMenuItem("New");
    m_file_new.setMnemonic("N".charAt(0));
    m_file_new.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
    m_file_new.addActionListener(this);
    m_file_open = new JMenuItem("Open");
    m_file_open.setMnemonic("O".charAt(0));
    m_file_open.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
    m_file_open.addActionListener(this);
    m_file_save = new JMenuItem("Save");
    m_file_save.setMnemonic("S".charAt(0));
    m_file_save.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));   
    m_file_save.addActionListener(this);
    m_file_test = new JMenuItem("Test skin in VLC...");
    m_file_test.setMnemonic("T".charAt(0));
    m_file_test.addActionListener(this);
    m_file_vlt = new JMenuItem("Export as VLT...");
    m_file_vlt.setMnemonic("V".charAt(0));
    m_file_vlt.setAccelerator(KeyStroke.getKeyStroke("ctrl v"));   
    m_file_vlt.addActionListener(this);
    m_file_quit = new JMenuItem("Quit");
    m_file_quit.setMnemonic("Q".charAt(0));
    m_file_quit.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
    m_file_quit.addActionListener(this);
    
    m_file.add(m_file_new);
    m_file.addSeparator();
    m_file.add(m_file_open);
    m_file.add(m_file_save);
    m_file.addSeparator();
    m_file.add(m_file_test);
    m_file.add(m_file_vlt);
    m_file.addSeparator();
    m_file.add(m_file_quit);
    
    m_edit = new JMenu("Edit");
    m_edit.setMnemonic("E".charAt(0));
    m_edit_theme = new JMenuItem("Theme settings");
    m_edit_theme.setMnemonic("I".charAt(0));
    m_edit_theme.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
    m_edit_theme.addActionListener(this);
    m_edit_global = new JMenuItem("Global variables");
    m_edit_global.setMnemonic("G".charAt(0));
    m_edit_global.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));
    m_edit_global.addActionListener(this);
    
    m_edit.add(m_edit_theme);
    m_edit.addSeparator();
    m_edit.add(m_edit_global);
        
    m_help = new JMenu("Help");
    m_help.setMnemonic("H".charAt(0));
    m_help_doc = new JMenuItem("Skins2 documentation");
    m_help_doc.setMnemonic("D".charAt(0));
    m_help_doc.addActionListener(this);
    m_help_doc.setAccelerator(KeyStroke.getKeyStroke("F1"));    
    m_help_about = new JMenuItem("About");
    m_help_about.setMnemonic("H".charAt(0));
    m_help_about.addActionListener(this);
    
    m_help.add(m_help_doc);
    m_help.add(m_help_about);
    
    mbar.add(m_file);
    mbar.add(m_edit);
    mbar.add(m_help);
    
    setJMenuBar(mbar);    
       
    jdesk = new JDesktopPane();
    
    s = new Skin(this);
    
    resources = new JInternalFrame("Resources",true,false);    
    resources.setMinimumSize(new Dimension(150,200));    
    SpringLayout res_layout = new SpringLayout();
    resources.setLayout(res_layout);     
    res_tree_model = new DefaultTreeModel(s.getResourcesTree());
    res_tree = new JTree(res_tree_model);    
    ToolTipManager.sharedInstance().registerComponent(res_tree);
    res_tree.setCellRenderer(tree_renderer);
    res_tree.setRootVisible(false);
    res_tree.setShowsRootHandles(true);
    res_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    res_tree.addTreeSelectionListener(this);
    JScrollPane res_tree_sp = new JScrollPane(res_tree);
    resources.add(res_tree_sp);       
    res_tree_sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));    
    res_add_bitmap = new JButton("",add_bitmap_icon);
    res_add_bitmap.setToolTipText("Add a new Bitmap or SubBitmap");
    res_add_bitmap.setMaximumSize(new Dimension(24,24));
    res_add_bitmap.setPreferredSize(new Dimension(24,24));
    res_add_bitmap.addActionListener(this);    
    res_add_font = new JButton("",add_font_icon);
    res_add_font.setToolTipText("Add a new font");
    res_add_font.setMaximumSize(new Dimension(24,24));
    res_add_font.setPreferredSize(new Dimension(24,24));
    res_add_font.addActionListener(this);
    res_edit = new JButton("",edit_icon);
    res_edit.setToolTipText("Edit the selected item");
    res_edit.setMaximumSize(new Dimension(24,24));
    res_edit.setPreferredSize(new Dimension(24,24));
    res_edit.addActionListener(this);
    res_del = new JButton("",delete_icon);
    res_del.setToolTipText("Delete the selected item");
    res_del.setMaximumSize(new Dimension(24,24));
    res_del.setPreferredSize(new Dimension(24,24));
    res_del.addActionListener(this);
    resources.add(res_add_bitmap);
    resources.add(res_add_font);
    resources.add(res_edit);
    resources.add(res_del);        
    
    res_layout.putConstraint(SpringLayout.WEST, res_tree_sp,5,SpringLayout.WEST, resources.getContentPane());   
    res_layout.putConstraint(SpringLayout.NORTH, res_tree_sp,5,SpringLayout.NORTH, resources.getContentPane());        
    res_layout.putConstraint(SpringLayout.NORTH, res_add_bitmap,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_add_font,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_edit,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_del,5,SpringLayout.SOUTH, res_tree_sp);    
    res_layout.putConstraint(SpringLayout.NORTH, res_add_bitmap,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_add_font,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_edit,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_del,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.WEST, res_add_bitmap,5,SpringLayout.WEST, resources.getContentPane());
    res_layout.putConstraint(SpringLayout.WEST, res_add_font,5,SpringLayout.EAST, res_add_bitmap);
    res_layout.putConstraint(SpringLayout.WEST, res_edit,5,SpringLayout.EAST, res_add_font);
    res_layout.putConstraint(SpringLayout.WEST, res_del,5,SpringLayout.EAST, res_edit);    
    res_layout.putConstraint(SpringLayout.SOUTH, resources.getContentPane(),24+5+5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, resources.getContentPane(),5,SpringLayout.NORTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.EAST, resources.getContentPane(),5,SpringLayout.EAST, res_tree_sp);
    
    resources.pack();
    resources.setSize(200,200);
    resources.setVisible(true);    
    
    windows = new JInternalFrame("Windows",true,false);  
    windows.setMinimumSize(new Dimension(150,150));   
    SpringLayout win_layout = new SpringLayout();
    windows.setLayout(win_layout);     
    win_tree_model = new DefaultTreeModel(s.getWindowsTree());
    win_tree = new JTree(win_tree_model);    
    ToolTipManager.sharedInstance().registerComponent(win_tree);
    win_tree.setCellRenderer(tree_renderer);
    win_tree.setRootVisible(false);
    win_tree.setShowsRootHandles(true);
    win_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    win_tree.addTreeSelectionListener(this);
    JScrollPane win_tree_sp = new JScrollPane(win_tree);
    windows.add(win_tree_sp);       
    win_tree_sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));    
    win_add_window = new JButton("",add_window_icon);
    win_add_window.setToolTipText("Add a new window");
    win_add_window.setMaximumSize(new Dimension(24,24));
    win_add_window.setPreferredSize(new Dimension(24,24));
    win_add_window.addActionListener(this);
    win_add_layout = new JButton("",add_layout_icon);
    win_add_layout.setToolTipText("Add a new layout to the selected window");
    win_add_layout.setMaximumSize(new Dimension(24,24));
    win_add_layout.setPreferredSize(new Dimension(24,24));
    win_add_layout.addActionListener(this);
    win_edit = new JButton("",edit_icon);
    win_edit.setToolTipText("Edit the selected resource");
    win_edit.setMaximumSize(new Dimension(24,24));
    win_edit.setPreferredSize(new Dimension(24,24));
    win_edit.addActionListener(this);
    win_del = new JButton("",delete_icon);
    win_del.setToolTipText("Delete the selected resource");
    win_del.setMaximumSize(new Dimension(24,24));
    win_del.setPreferredSize(new Dimension(24,24));
    win_del.addActionListener(this);
    windows.add(win_add_window);
    windows.add(win_add_layout);
    windows.add(win_edit);
    windows.add(win_del);        
    
    win_layout.putConstraint(SpringLayout.WEST, win_tree_sp,5,SpringLayout.WEST, windows.getContentPane());   
    win_layout.putConstraint(SpringLayout.NORTH, win_tree_sp,5,SpringLayout.NORTH, windows.getContentPane());        
    win_layout.putConstraint(SpringLayout.NORTH, win_add_window,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_add_layout,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_edit,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_del,5,SpringLayout.SOUTH, win_tree_sp);    
    win_layout.putConstraint(SpringLayout.NORTH, win_add_window,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_add_layout,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_edit,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_del,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.WEST, win_add_window,5,SpringLayout.WEST, windows.getContentPane());
    win_layout.putConstraint(SpringLayout.WEST, win_add_layout,5,SpringLayout.EAST, win_add_window);
    win_layout.putConstraint(SpringLayout.WEST, win_edit,5,SpringLayout.EAST, win_add_layout);
    win_layout.putConstraint(SpringLayout.WEST, win_del,5,SpringLayout.EAST, win_edit);    
    win_layout.putConstraint(SpringLayout.SOUTH, windows.getContentPane(),24+5+5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, windows.getContentPane(),5,SpringLayout.NORTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.EAST, windows.getContentPane(),5,SpringLayout.EAST, win_tree_sp);
    
    windows.pack();
    windows.setSize(200,150);
    windows.setVisible(true);   
    
    items = new JInternalFrame("Items",true,false);   
    items.setMinimumSize(new Dimension(150,150));   
    SpringLayout items_layout = new SpringLayout();
    items.setLayout(items_layout);     
    items_tree_model = new DefaultTreeModel(s.getItemsTree());
    items_tree = new JTree(items_tree_model);    
    ToolTipManager.sharedInstance().registerComponent(items_tree);
    items_tree.setCellRenderer(tree_renderer);
    items_tree.setRootVisible(false);
    items_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    items_tree.addTreeSelectionListener(this);
    items_tree.setShowsRootHandles(true);
    JScrollPane items_tree_sp = new JScrollPane(items_tree);
    items.add(items_tree_sp);       
    items_tree_sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));    
    items_add = new JButton("",add_icon);
    items_add.setToolTipText("Add a new item");
    items_add.setMaximumSize(new Dimension(24,24));
    items_add.setPreferredSize(new Dimension(24,24));
    items_add.addActionListener(this);
    items_up = new JButton("",up_icon);
    items_up.setToolTipText("Move selected item up in hierarchy");
    items_up.setMaximumSize(new Dimension(24,12));
    items_up.setPreferredSize(new Dimension(24,12));
    items_up.addActionListener(this);
    items_down = new JButton("",down_icon);
    items_down.setToolTipText("Move selected item down in hierarchy");
    items_down.setMaximumSize(new Dimension(24,12));
    items_down.setPreferredSize(new Dimension(24,12));
    items_down.addActionListener(this);
    items_edit = new JButton("",edit_icon);
    items_edit.setToolTipText("Edit the selected resource");
    items_edit.setMaximumSize(new Dimension(24,24));
    items_edit.setPreferredSize(new Dimension(24,24));
    items_edit.addActionListener(this);
    items_del = new JButton("",delete_icon);
    items_del.setToolTipText("Delete the selected resource");
    items_del.setMaximumSize(new Dimension(24,24));
    items_del.setPreferredSize(new Dimension(24,24));
    items_del.addActionListener(this);
    items.add(items_add);
    items.add(items_up);
    items.add(items_down);
    items.add(items_edit);
    items.add(items_del);        
    
    items_layout.putConstraint(SpringLayout.WEST, items_tree_sp,5,SpringLayout.WEST, items.getContentPane());   
    items_layout.putConstraint(SpringLayout.NORTH, items_tree_sp,5,SpringLayout.NORTH, items.getContentPane());        
    items_layout.putConstraint(SpringLayout.NORTH, items_add,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_up,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_edit,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_del,5,SpringLayout.SOUTH, items_tree_sp);    
    items_layout.putConstraint(SpringLayout.NORTH, items_add,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_up,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_down,0,SpringLayout.SOUTH, items_up);
    items_layout.putConstraint(SpringLayout.NORTH, items_edit,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_del,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.WEST, items_add,5,SpringLayout.WEST, items.getContentPane());
    items_layout.putConstraint(SpringLayout.WEST, items_up,5,SpringLayout.EAST, items_add);
    items_layout.putConstraint(SpringLayout.WEST, items_down,5,SpringLayout.EAST, items_add);
    items_layout.putConstraint(SpringLayout.WEST, items_edit,5,SpringLayout.EAST, items_up);
    items_layout.putConstraint(SpringLayout.WEST, items_edit,5,SpringLayout.EAST, items_down);
    items_layout.putConstraint(SpringLayout.WEST, items_del,5,SpringLayout.EAST, items_edit);    
    items_layout.putConstraint(SpringLayout.SOUTH, items.getContentPane(),24+5+5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items.getContentPane(),5,SpringLayout.NORTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.EAST, items.getContentPane(),5,SpringLayout.EAST, items_tree_sp);
    
    items.pack();
    items.setSize(200,200);
    items.setVisible(true); 
    
    jdesk.add(resources);
    jdesk.add(windows);
    jdesk.add(items);
    
    resources.setLocation(0,0);
    windows.setLocation(0,200);
    items.setLocation(0,350);
    
     res_add_bitmap_pu = new JPopupMenu();
    res_add_bitmap_pu_b = new JMenuItem("Add Bitmap/s");
    res_add_bitmap_pu_b.addActionListener(this);
    res_add_bitmap_pu.add(res_add_bitmap_pu_b);
    res_add_bitmap_pu_s = new JMenuItem("Add SubBitmap");
    res_add_bitmap_pu_s.addActionListener(this);
    res_add_bitmap_pu.add(res_add_bitmap_pu_s);
    jdesk.add(res_add_bitmap_pu);
    
    items_add_pu = new JPopupMenu();
    items_add_pu_tp = new JMenu("Add to selected Panel");    
    items_add_pu_tp_anchor = new JMenuItem("Anchor");
    items_add_pu_tp_anchor.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_anchor);
    items_add_pu_tp_button = new JMenuItem("Button");
    items_add_pu_tp_button.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_button);
    items_add_pu_tp_checkbox = new JMenuItem("Checkbox");
    items_add_pu_tp_checkbox.addActionListener(this);    
    items_add_pu_tp.add(items_add_pu_tp_checkbox);
    items_add_pu_tp_image = new JMenuItem("Image");    
    items_add_pu_tp_image.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_image);
    items_add_pu_tp_panel = new JMenuItem("Panel");    
    items_add_pu_tp_panel.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_panel);
    items_add_pu_tp_playtree = new JMenuItem("Playtree");
    items_add_pu_tp_playtree.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_playtree);
    items_add_pu_tp_slider = new JMenuItem("Slider");
    items_add_pu_tp_slider.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_slider);
    items_add_pu_tp_text = new JMenuItem("Text");
    items_add_pu_tp_text.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_text);
    items_add_pu_tp_video = new JMenuItem("Video");
    items_add_pu_tp_video.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_video);
    items_add_pu.add(items_add_pu_tp);
    items_add_pu.addSeparator();
    items_add_pu_anchor = new JMenuItem("Anchor");
    items_add_pu_anchor.addActionListener(this);
    items_add_pu.add(items_add_pu_anchor);
    items_add_pu_button = new JMenuItem("Button");
    items_add_pu_button.addActionListener(this);
    items_add_pu.add(items_add_pu_button);
    items_add_pu_checkbox = new JMenuItem("Checkbox");
    items_add_pu_checkbox.addActionListener(this);    
    items_add_pu.add(items_add_pu_checkbox);
    items_add_pu_image = new JMenuItem("Image");    
    items_add_pu_image.addActionListener(this);
    items_add_pu.add(items_add_pu_image);
    items_add_pu_panel = new JMenuItem("Panel");    
    items_add_pu_panel.addActionListener(this);
    items_add_pu.add(items_add_pu_panel);
    items_add_pu_playtree = new JMenuItem("Playtree");
    items_add_pu_playtree.addActionListener(this);
    items_add_pu.add(items_add_pu_playtree);
    items_add_pu_slider = new JMenuItem("Slider");
    items_add_pu_slider.addActionListener(this);
    items_add_pu.add(items_add_pu_slider);
    items_add_pu_text = new JMenuItem("Text");
    items_add_pu_text.addActionListener(this);
    items_add_pu.add(items_add_pu_text);
    items_add_pu_video = new JMenuItem("Video");
    items_add_pu_video.addActionListener(this);
    items_add_pu.add(items_add_pu_video);
    jdesk.add(items_add_pu);  
    
    jdesk.setMinimumSize(new Dimension(800,600));
    add(jdesk);    
    if(System.getProperty("os.name").indexOf("Windows")!=-1) {
      try {      
        Registry r = new Registry();               
        RegistryKey vlc_key = r.openSubkey(Registry.HKEY_LOCAL_MACHINE,"Software\\VideoLAN\\VLC",RegistryKey.ACCESS_READ);        
        String installDir = vlc_key.getStringValue("InstallDir");      
        vlc_dir = installDir+File.separator;
        vlc_skins_dir = vlc_dir+"skins";
      }
      catch (Exception e) {
        System.err.println(e);
        StackTraceElement[] stack = e.getStackTrace();
        for(int i=0;i<stack.length;i++) System.err.println("  "+stack[i]);
      }  
    }   
    else if(System.getProperty("os.name").indexOf("Linux")!=-1){            
      vlc_skins_dir = "~/.vlc/skins2/";
    }
    
    
    fc = new JFileChooser();
    fc.setFileFilter(new CustomFileFilter(fc,"xml","*.xml (VLC XML-Skin Files)",false,vlc_dir));
    fc.setCurrentDirectory(new File(vlc_skins_dir));
    
    setVisible(true);   
    
    Object[] options= {"Create a new skin", "Open an exisiting skin","Quit"};
    int n = JOptionPane.showOptionDialog(this,"What would you like to do?","Welcome to the VLC Skin Editor",
                                         JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
    if(n==0) {
      int returnVal=fc.showSaveDialog(this);        
      if (returnVal != JFileChooser.APPROVE_OPTION) System.exit(0); 
      File f = fc.getSelectedFile();
      if(!f.getPath().endsWith(".xml")) f = new File(f.getPath()+".xml");
      setTitle(f.getPath()+" - VLC Skin Editor "+VERSION);
      s.createnew(f);                  
      selected_resource = null;
      selected_in_windows = null;
      selected_window = null;
      selected_item = null;
      saved=false;
    }
    else if(n==1) {
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            setTitle(fc.getSelectedFile().toString()+" - VLC Skin Editor "+VERSION);
            s.open(fc.getSelectedFile());                  
            selected_resource = null;
            selected_in_windows = null;
            selected_window = null;
            selected_item = null;
            saved=true;
      }      
    }
    else {
      System.exit(0);
    } 
  }
  /** Reacts to GUI actions */
  public void actionPerformed(ActionEvent e) {
    // <editor-fold defaultstate="collapsed" desc="New File"> 
    if(e.getSource().equals(m_file_new)) {
      s = new Skin(this);
      int returnVal = JFileChooser.CANCEL_OPTION;;
      while (returnVal != JFileChooser.APPROVE_OPTION) {
        returnVal=fc.showSaveDialog(this);        
      } 
      File f = fc.getSelectedFile();
      if(!f.getPath().endsWith(".xml")) f = new File(f.getPath()+".xml");
      setTitle(f.getPath()+" - VLC Skin Editor "+VERSION);
      s.createnew(f);             
      selected_resource = null;
      selected_in_windows = null;
      selected_window = null;
      selected_item = null;
      saved=false;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Open File"> 
    else if(e.getSource().equals(m_file_open)) {
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            setTitle(fc.getSelectedFile().toString()+" - VLC Skin Editor "+VERSION);
            s.open(fc.getSelectedFile());                  
            selected_resource = null;
            selected_in_windows = null;
            selected_window = null;
            selected_item = null;
            saved=true;
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Save File ">
    else if(e.getSource().equals(m_file_save))  {
      s.save();
      saved=true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Test Skin">
    else if(e.getSource().equals(m_file_test))  {
      s.save();
      saved=true;            
      String[] command = { vlc_dir+"vlc", "-I", "skins2", "--skins2-last="+s.skinfile.toString(), "--skins2-config=\"\"","--skins2-systray" };
      try {
        Runtime.getRuntime().exec(command);
      }
      catch (IOException ex) {
        
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Export VLT">
    else if(e.getSource().equals(m_file_vlt))  {      
      if(vlt_saver==null) {
        vlt_saver = new JFileChooser();
        vlt_saver.setCurrentDirectory(new File(vlc_skins_dir));
        vlt_saver.setFileFilter(new CustomFileFilter(vlt_saver,"vlt",".vlt  (VideoLAN Theme)",false,""));
        vlt_saver.setAcceptAllFileFilterUsed(false);
      }
      int returnVal = vlt_saver.showSaveDialog(this);
      File f = null;
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        f = vlt_saver.getSelectedFile();
      }      
      else return;
      try {        
        TarGzOutputStream tgz = new TarGzOutputStream(new FileOutputStream(f));
        TarEntry sf = new TarEntry(s.skinfile);
        sf.setName("theme.xml");        
        tgz.putNextEntry(sf);
        FileInputStream xmlfis = new FileInputStream(s.skinfile);
        while(true) {
          int cb = xmlfis.read();
          if(cb==-1) break;
          tgz.write(cb);          
        }  
        xmlfis.close();
        tgz.closeEntry();
        ArrayList<String> files = new ArrayList<String>();
        for(int i=0;i<s.resources.size();i++) {
          if(s.resources.get(i).type.equals("Bitmap")) {
            try {
              Bitmap b = (Bitmap)s.resources.get(i);              
              String fn = s.skinfolder+b.file;
              if(!files.contains(fn)) { //To avoid double files (e.g. one font file used by several font objects)
                tgz.putNextEntry(new TarEntry(b.file));
                FileInputStream fis = new FileInputStream(fn);
                while(true) {
                  int cb = fis.read();
                  if(cb==-1) break;
                  tgz.write(cb);          
                }   
                fis.close();
                tgz.closeEntry();
                files.add(fn);
              }
            }
            catch (Exception ex) {};
          }
          else if(s.resources.get(i).type.equals("Font")) {
            try {
              vlcskineditor.Resources.Font fnt = (vlcskineditor.Resources.Font)s.resources.get(i);              
              String fn = s.skinfolder+fnt.file;              
              if(!files.contains(fn)) { //To avoid double files (e.g. one font file used by several font objects)
                tgz.putNextEntry(new TarEntry(fnt.file));
                FileInputStream fis = new FileInputStream(fn);
                while(true) {
                  int cb = fis.read();
                  if(cb==-1) break;
                  tgz.write(cb);          
                }   
                fis.close();
                tgz.closeEntry();
                files.add(fn);
              }
            }
            catch (Exception ex) {};
          }
        }   
        
        tgz.close();
      }
      catch (Exception ex) {
        String exp ="";
        for(int i=0;i<ex.getStackTrace().length;i++) {
          exp+=ex.getStackTrace()[i]+"\n";
        }         
        System.err.println(exp);
        return;
      }   
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Quit "> 
    else if(e.getSource().equals(m_file_quit)) {
      if(!saved) {
        Object[] options= { "Yes", "No", "Don't quit" };
        int n = JOptionPane.showOptionDialog(
              this,
              "Would you like to save your skin before exiting?",
              "Your changes were not saved",
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              options,
              options[1]
              );
        if(n==0) {
          s.save();
          System.exit(0);
        }
        else if(n==1) {
          System.exit(0);
        }       
      }
      else {
        System.exit(0);
      }
    }
    // </editor-fold>
    else if(e.getSource().equals(m_edit_theme)) s.showThemeOptions();
    else if(e.getSource().equals(m_edit_global)) return;
    // <editor-fold defaultstate="collapsed" desc="Open Help "> 
    else if(e.getSource().equals(m_help_doc)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="About"> 
    else if(e.getSource().equals(m_help_about)) {
      JOptionPane.showMessageDialog(this,"©2007 Daniel Dreibrodt\n" +
            "daniel.dreibrodt@gmx.de\n" +
            "http://www.d-gfx.net.tc\n\n" +
            "Released under terms of the GPL 2+\n" +
            "This program uses the Java Tar Package and the Windows Registry API Native Interface by Timothy Gerard Endres (time@gjt.org)\n" +
            "which are released under public domain.",
            "About VLC Skin Editor", JOptionPane.INFORMATION_MESSAGE,icon);
    }
    // </editor-fold>    
    else if(e.getSource().equals(res_add_bitmap)) res_add_bitmap_pu.show(res_add_bitmap,0,0);
    // <editor-fold defaultstate="collapsed" desc="Add Bitmap">
    else if(e.getSource().equals(res_add_bitmap_pu_b)) {
      if(bitmap_adder==null) {
        bitmap_adder = new JFileChooser();
        bitmap_adder.setFileFilter(new CustomFileFilter(bitmap_adder,"png","*.png (Portable Network Graphic) inside the XML file's directory",true,s.skinfolder));
        bitmap_adder.setCurrentDirectory(new File(s.skinfolder));   
        bitmap_adder.setAcceptAllFileFilterUsed(false);
        bitmap_adder.setMultiSelectionEnabled(true);
      }
      int returnVal = bitmap_adder.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File[] files = bitmap_adder.getSelectedFiles();
        for(int i=0;i<files.length;i++) {
          s.resources.add(new vlcskineditor.Resources.Bitmap(s,files[i]));
        }
      }      
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add SubBitmap">
    else if(e.getSource().equals(res_add_bitmap_pu_s)) {
      if(selected_resource!=null) {
        Resource r = s.getResource(selected_resource);
        if(r!=null) {
          if(r.type.equals("Bitmap")) {
            vlcskineditor.Resources.Bitmap b = (vlcskineditor.Resources.Bitmap)r;
            b.SubBitmaps.add(new vlcskineditor.Resources.SubBitmap(s,b.image));  
          }
          else {
            JOptionPane.showMessageDialog(this,"No parent bitmap selected!","Could not add sub bitmap",JOptionPane.INFORMATION_MESSAGE);
          }
        }
        else {
          JOptionPane.showMessageDialog(this,"No parent bitmap selected!","Could not add sub bitmap",JOptionPane.INFORMATION_MESSAGE);
        }
      }
      else {
        JOptionPane.showMessageDialog(this,"No parent bitmap selected!","Could not add sub bitmap",JOptionPane.INFORMATION_MESSAGE);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add font">
    else if(e.getSource().equals(res_add_font)) {
      if(font_adder==null) {
        font_adder = new JFileChooser();
        String[] ext = { "ttf" , "otf" };
        font_adder.setFileFilter(new CustomFileFilter(font_adder,ext,"*.otf/*.ttf (Open and true type fonts) inside the XML file's directory",true,s.skinfolder));
        font_adder.setCurrentDirectory(new File(s.skinfolder));   
        font_adder.setAcceptAllFileFilterUsed(false);
        font_adder.setMultiSelectionEnabled(true);
      }
      int returnVal = font_adder.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File[] files = font_adder.getSelectedFiles();
        for(int i=0;i<files.length;i++) {
          s.resources.add(new vlcskineditor.Resources.Font(s,files[i]));
        }
        s.updateResources();
      }    
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Resource edit">
    else if(e.getSource().equals(res_edit)) {
      if(selected_resource!=null) {
        Resource r = s.getResource(selected_resource);
        if(r!=null) r.showOptions();        
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Delete Resource">
    else if(e.getSource().equals(res_del)) {
      if(s.isUsed(selected_resource)) {
        JOptionPane.showMessageDialog(this,"Resource is in use by some item/s, thus it cannot be deleted","Could not delete resource",JOptionPane.INFORMATION_MESSAGE);
      }
      else {
        Object[] options= {"Yes","No"};
        int n = JOptionPane.showOptionDialog(this,"Do you really want to delete \""+selected_resource+"\"?","Confirmation",
                                       JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
        if(n==0) {
          s.resources.remove(s.getResource(selected_resource));
          s.updateResources();          
        }
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add window">
    else if(e.getSource().equals(win_add_window)) {
      s.windows.add(new Window(s));      
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add layout">
    else if(e.getSource().equals(win_add_layout)) {
      if (selected_window!=null) {
        Window w = s.getWindow(selected_window);
        if(w!=null) w.addLayout();
      }
      else {
        JOptionPane.showMessageDialog(this,"No parent window selected!","Could not add layout",JOptionPane.INFORMATION_MESSAGE);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Window edit">
    else if(e.getSource().equals(win_edit)) {
      if (selected_layout!=null && selected_window!=null) {
        Layout l = s.getWindow(selected_window).getLayout(selected_layout);
        if(l!=null) l.showOptions();
      }
      else if(selected_window!=null) {
        Window w = s.getWindow(selected_window);
        if(w!=null) w.showOptions();        
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Delete Window/Layout">
    else if(e.getSource().equals(win_del)) {
      if(selected_layout!=null) {        
        Window w = s.getWindow(selected_window);
        Layout l = w.getLayout(selected_layout);
        if(l!=null) {
          Object[] options= {"Yes","No"};
          int n = JOptionPane.showOptionDialog(this,"Do you really want to delete \""+l.id+"\"?","Confirmation",
                                         JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
          if(n==0) {
            w.layouts.remove(l);
            s.updateWindows();
            s.updateItems();
          }
        }
      }
      else if(selected_window!=null) {        
        Window w= s.getWindow(selected_window);
        if(w!=null) {
          Object[] options= {"Yes","No"};
          int n = JOptionPane.showOptionDialog(this,"Do you really want to delete \""+w.id+"\"?","Confirmation",
                                         JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
          if(n==0) {
            s.windows.remove(w);
            s.updateWindows();
          }
        }
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add item">
    else if(e.getSource().equals(items_add)) {
      if(selected_layout==null) return;
      items_add_pu_tp.setEnabled(false);
      if(selected_item!=null) {
        Item i = s.getItem(selected_item);        
        if(i!=null) {
          if(i.type.equals("Group")) items_add_pu_tp.setEnabled(true);                            
          if(i.type.equals("Panel")) items_add_pu_tp.setEnabled(true);                            
        }         
      }
      items_add_pu.show(items_add,0,0);  
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Item edit">
    else if(e.getSource().equals(items_edit)) {
      if(selected_item!=null) {
        Item i = s.getItem(selected_item);
        if(i!=null) i.showOptions();        
      }
    }
    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Add Item popups">
    else if(e.getSource().equals(items_add_pu_anchor)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new Anchor(s));           
    }
    else if(e.getSource().equals(items_add_pu_button)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new vlcskineditor.Items.Button(s));           
    }
    else if(e.getSource().equals(items_add_pu_checkbox)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new vlcskineditor.Items.Checkbox(s));           
    }
    else if(e.getSource().equals(items_add_pu_panel)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new vlcskineditor.Items.Panel(s));           
    }
    else if(e.getSource().equals(items_add_pu_image)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new vlcskineditor.Items.Image(s));           
    }
    else if(e.getSource().equals(items_add_pu_playtree)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new Playtree(s));           
    }
    else if(e.getSource().equals(items_add_pu_slider)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new Slider(s));           
    }
    else if(e.getSource().equals(items_add_pu_text)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new Text(s));           
    }
    else if(e.getSource().equals(items_add_pu_video)) {      
      java.util.List<Item> i;
      if (selected_item!=null ) i = s.getParentOf(selected_item);
      else i = s.getWindow(selected_window).getLayout(selected_layout).items;       
      i.add(new Video(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_anchor)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Anchor(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_button)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Button(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_checkbox)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Checkbox(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_image)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Image(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_panel)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Panel(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_playtree)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Playtree(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_slider)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Slider(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_text)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Text(s));           
    }
    else if(e.getSource().equals(items_add_pu_tp_video)) {      
      java.util.List<Item> l = s.getListOf(selected_item);
      if(l!=null) l.add(new vlcskineditor.Items.Video(s));           
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Item delete">
    else if(e.getSource().equals(items_del)) {
      if(selected_item!=null) {
        java.util.List<Item> p = s.getParentOf(selected_item);
        if(p!=null) {
          Object[] options= {"Yes","No"};
          int n = JOptionPane.showOptionDialog(this,"Do you really want to delete \""+selected_item+"\"?","Confirmation",
                                         JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
          if(n==0) {
            p.remove(s.getItem(selected_item));
            s.updateItems();
          }
        }       
      }
    }
    // </editor-fold>
  }
  
  /** Reacts to tree selections */
  public void valueChanged(TreeSelectionEvent e) {    
    if(e.getSource().equals(res_tree)) {
      String selection = e.getPath().getLastPathComponent().toString();
      selected_resource = selection.substring(selection.indexOf(": ")+2);      
    }
    else if(e.getSource().equals(win_tree)) {      
      selected_in_windows = e.getPath().getLastPathComponent().toString();
      Object[] path = e.getPath().getPath();
      for (int i=0;i<path.length;i++) {
        String type = path[i].toString().substring(0,path[i].toString().indexOf(": "));
        if(type.equals("Window")) {          
          selected_window = path[i].toString().substring(path[i].toString().indexOf(": ")+2);
          selected_layout = null;
          items_tree_model.setRoot(new DefaultMutableTreeNode("Root: Items"));                   
        }
        else if(type.equals("Layout")) {
          selected_layout = path[i].toString().substring(path[i].toString().indexOf(": ")+2);
          s.updateItems();
        }
      }
      
    }
    else if(e.getSource().equals(items_tree)) {
      String selection = e.getPath().getLastPathComponent().toString();
      selected_item = selection.substring(selection.indexOf(": ")+2);
    }
  }
  public void windowActivated(WindowEvent e) {}
  public void windowClosed(WindowEvent e) {}
  public void windowClosing(WindowEvent e) {
    if(!saved) {
      Object[] options= { "Yes", "No", "Don't quit" };
      int n = JOptionPane.showOptionDialog(
            this,
            "Would you like to save your skin before exiting?",
            "Your modifications were not saved",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]
            );
      if(n==0) {
        s.save();
        System.exit(0);
      }
      else if(n==1) {
        System.exit(0);
      }       
    }
    else {
      System.exit(0);
    }
  }
  public void windowDeactivated(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}
  /**
   * Creates an ImageIcon of an image included in the JAR
   * @param filename  The path to the image file inside the JAR
   * @return         An ImageIcon representing the given file
   */
  public ImageIcon createIcon(String filename) {
      java.awt.Image img = null;
      try {
        img = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource(filename));
        //img = ImageIO.read(file);
        return new ImageIcon(img);  
      } catch (Exception e) {
        System.out.println(e);
        return null;
      }
  }
  /**
   * Creates a new instance of Main and thus launches the editor
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new Main();  
  }
  
}
