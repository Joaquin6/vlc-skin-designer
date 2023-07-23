/*****************************************************************************
 * Main.java
 *****************************************************************************
 * Copyright (C) 2007-2009 Daniel Dreibrodt
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

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.zip.*;
import vlcskineditor.items.*;
import com.ice.tar.*;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.plaf.metal.*;
import vlcskineditor.history.*;
import vlcskineditor.items.Checkbox;
import vlcskineditor.resources.Bitmap;
import vlcskineditor.resources.SubBitmap;


/**
 * The main class holds the GUI
 * @author Daniel Dreibrodt <daniel.dreibrodt@gmail.com>
 */
public class Main extends JFrame implements ActionListener, TreeSelectionListener, WindowListener, MouseListener {

  /** Require for all JFrames, as they are serializable */
  private static final long serialVersionUID = 86;

  /** The URL where to check for updates */
  private final String updateURL_s = "http://www.videolan.org/vlc/skineditor_update.php";

  /** The version identification of the current build. */
  public final static String VERSION = "0.8.6.dev";
  /** The directory in which the VLC executable is found */
  private String vlc_dir = "";
  /** The directory from which VLC loads its skins */
  private String vlc_skins_dir = "";

  /** The main window layout */
  BorderLayout layout;

  /** The menu bar */
  private JMenuBar mbar;
  /** The menus */
  private JMenu m_file, m_edit, m_help;
  /** The file menu's entries */
  private JMenuItem m_file_new, m_file_open, m_file_save, m_file_test, m_file_vlt, m_file_png, m_file_quit;
  /** The edit menu's entries */
  private JMenuItem m_edit_undo, m_edit_redo, m_edit_theme, m_edit_vars, m_edit_prefs, m_edit_up, m_edit_down, m_edit_right, m_edit_left, m_edit_del;
  /** The help menu's entries */
  private JMenuItem m_help_doc, m_help_about;

  /** The toolbar */
  private JToolBar tbar;
  /** The toolbar buttons */
  private JButton tbar_open_btn, tbar_save_btn, tbar_undo_btn, tbar_redo_btn, tbar_move_btn, tbar_path_btn;

  /** The desktop pane holding the resources, windows/layouts and items windows */
  private JDesktopPane jdesk;
  /** The resources, windows/layouts and items windows */
  private JInternalFrame resources,windows,items;
  /** The trees containing the resources/windows/items hierarchy */
  protected JTree res_tree,win_tree,items_tree;
  /** The tree models of the trees containing the resources/windows/items hierarchy */
  protected DefaultTreeModel res_tree_model, win_tree_model, items_tree_model;
  /** The tree renderer for the resources/window/items trees, responsible for showing the proper icon for each elements. */
  private DefaultTreeCellRenderer tree_renderer = new TreeRenderer();
  /** Buttons in the resources window */
  private JButton res_add_bitmap, res_add_font, res_duplicate, res_edit, res_del;
  /** Popup menu that is shown when the "add bitmap" button is clicked */
  private JPopupMenu res_add_bitmap_pu;
  /** Elements of the "add bitmap" popup menu */
  private JMenuItem res_add_bitmap_pu_b, res_add_bitmap_pu_s;
  /** Buttons in the windows/layouts window */
  private JButton win_add_window, win_add_layout, win_layout_up, win_layout_down, win_duplicate, win_edit, win_del;
  /** Buttons in the items window */
  private JButton items_add, items_up, items_down, items_duplicate, items_edit, items_del;
  /** The popup menu that is shown when the "add item" button is clicked */
  private JPopupMenu items_add_pu;
  private JMenuItem items_add_pu_anchor, items_add_pu_button, items_add_pu_checkbox;
  private JMenuItem items_add_pu_image, items_add_pu_panel;
  private JMenuItem items_add_pu_playtree, items_add_pu_slider, items_add_pu_text, items_add_pu_video;
  /** The submenu for adding items to an existing panel */
  private JMenu items_add_pu_tp;
  private JMenuItem items_add_pu_tp_anchor, items_add_pu_tp_button, items_add_pu_tp_checkbox;
  private JMenuItem items_add_pu_tp_image, items_add_pu_tp_panel;
  private JMenuItem items_add_pu_tp_playtree, items_add_pu_tp_slider, items_add_pu_tp_text, items_add_pu_tp_video;

  /** The currently opened skin */
  public Skin s;

  public static ImageIcon add_bitmap_icon = createIcon("icons/add_bitmap.png");
  public static ImageIcon add_font_icon = createIcon("icons/add_font.png");
  public static ImageIcon copy_icon = createIcon("icons/copy.png");
  public static ImageIcon edit_icon = createIcon("icons/edit.png");
  public static ImageIcon edit_undo_icon = createIcon("icons/edit-undo.png");
  public static ImageIcon edit_redo_icon = createIcon("icons/edit-redo.png");
  public static ImageIcon editor_icon = createIcon("icons/editor.png");
  public static ImageIcon delete_icon = createIcon("icons/delete.png");
  public static ImageIcon add_window_icon = createIcon("icons/add_window.png");
  public static ImageIcon add_layout_icon = createIcon("icons/add_layout.png");
  public static ImageIcon add_icon = createIcon("icons/add.png");
  public static ImageIcon up_icon = createIcon("icons/move_up.png");
  public static ImageIcon down_icon = createIcon("icons/move_down.png");
  public static ImageIcon help_icon = createIcon("icons/help.png");
  /** The VLC Skin Editor icon. */
  public static ImageIcon icon = createIcon("icons/icon.png");
  public static ImageIcon open_icon = createIcon("icons/open.png");
  public static ImageIcon save_icon = createIcon("icons/save.png");
  public static ImageIcon vlc_icon = createIcon("icons/vlc16x16.png");
  public static ImageIcon vlt_icon = createIcon("icons/vlt.png");
  public static ImageIcon new_icon = createIcon("icons/new.png");
  public static ImageIcon exit_icon = createIcon("icons/exit.png");
  public static ImageIcon resources_icon = createIcon("icons/resources.png");
  public static ImageIcon windows_icon = createIcon("icons/windows.png");
  public static ImageIcon items_icon = createIcon("icons/items.png");
  public static ImageIcon preview_icon = createIcon("icons/preview.png");

  /** IDs of selected objects */
  private String selected_resource, selected_in_windows, selected_window, selected_layout, selected_item;

  /** The file chosing dialogs */
  private JFileChooser base_fc, bitmap_adder, font_adder, vlt_fc;
  /** The preview window */
  protected PreviewWindow pvwin;
  /** Specifies whether changes were made without having saved the skin. */
  public boolean saved = false;
  /** Specifies whether a file is currently being opened */
  boolean opening = false;
  /** Specifies if a file was opened */
  boolean opened = false;
  /** Handles undoing and redoing of actions */
  public History hist;
  /** Textfield width for all editing dialogs */
  public static final int TEXTFIELD_WIDTH = 200;

   /**
   * Launches the skin editor and initializes the GUI.
   * @param args Command line arguments passed by the console.
   * If there exist one or more arguments, the first argument is intepreted as a file locator for a skin to be loaded.
   */
  public Main(String[] args) {
    Config.setMainInstance(this);

    setTitle("VLC Skin Editor "+VERSION);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(this);
    setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("icons/icon16.png")));

    if(System.getProperty("os.name").indexOf("Mac")!=-1) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TeacherTool Desktop");

        try {
            /* MacOSX specific shim to set the application logo in the Dock. */
            Object application = Class.forName("com.apple.eawt.Application")
                .getMethod("getApplication")
                .invoke(null);
            application.getClass()
                .getMethod("setDockIconImage")
                .invoke(application, createIcon("icons/icon.png").getImage());
        } catch(ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException e) {
            /* TODO: log that we cannot set icon */
        }
    }


    //Menubar creation
    mbar = new JMenuBar();

    //For cross-platform feel (CTRL on Win & Linux, APPLE/COMMAND on Mac OS)
    int mask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    //Initializing of the file menu
    m_file = new JMenu(Language.get("MENU_FILE"));
    m_file.setMnemonic(Language.get("MENU_FILE_MN").charAt(0));
    m_file_new = new JMenuItem(Language.get("MENU_FILE_NEW"));
    m_file_new.setIcon(new_icon);
    m_file_new.setMnemonic(Language.get("MENU_FILE_NEW_MN").charAt(0));
    m_file_new.setAccelerator(KeyStroke.getKeyStroke('N',mask));
    m_file_new.addActionListener(this);
    m_file_open = new JMenuItem(Language.get("MENU_FILE_OPEN"));
    m_file_open.setIcon(open_icon);
    m_file_open.setMnemonic(Language.get("MENU_FILE_OPEN_MN").charAt(0));
    m_file_open.setAccelerator(KeyStroke.getKeyStroke('O',mask));
    m_file_open.addActionListener(this);
    m_file_save = new JMenuItem(Language.get("MENU_FILE_SAVE"));
    m_file_save.setIcon(save_icon);
    m_file_save.setMnemonic(Language.get("MENU_FILE_SAVE_MN").charAt(0));
    m_file_save.setAccelerator(KeyStroke.getKeyStroke('S',mask));
    m_file_save.addActionListener(this);
    m_file_test = new JMenuItem(Language.get("MENU_FILE_TEST"));
    m_file_test.setIcon(vlc_icon);
    m_file_test.setMnemonic(Language.get("MENU_FILE_TEST_MN").charAt(0));
    m_file_test.setAccelerator(KeyStroke.getKeyStroke('T',mask+InputEvent.SHIFT_DOWN_MASK));
    m_file_test.addActionListener(this);
    m_file_vlt = new JMenuItem(Language.get("MENU_FILE_VLT"));
    m_file_vlt.setIcon(vlt_icon);
    m_file_vlt.setMnemonic(Language.get("MENU_FILE_VLT_MN").charAt(0));
    m_file_vlt.setAccelerator(KeyStroke.getKeyStroke('V',mask+InputEvent.SHIFT_DOWN_MASK));
    m_file_vlt.addActionListener(this);
    m_file_png = new JMenuItem(Language.get("MENU_FILE_PNG"));
    m_file_png.setMnemonic(Language.get("MENU_FILE_PNG_MN").charAt(0));
    m_file_png.addActionListener(this);
    m_file_png.setEnabled(false);
    if(System.getProperty("os.name").indexOf("Mac")==-1) {
      m_file_quit = new JMenuItem(Language.get("MENU_FILE_EXIT"));
      m_file_quit.setIcon(exit_icon);
      m_file_quit.setMnemonic(Language.get("MENU_FILE_EXIT_MN").charAt(0));
      m_file_quit.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
      m_file_quit.addActionListener(this);
    }

    m_file.add(m_file_new);
    m_file.addSeparator();
    m_file.add(m_file_open);
    m_file.add(m_file_save);
    m_file.addSeparator();
    m_file.add(m_file_test);
    m_file.add(m_file_vlt);
    m_file.add(m_file_png);
    if(System.getProperty("os.name").indexOf("Mac")==-1) {
        m_file.addSeparator();
        m_file.add(m_file_quit);
    }

    //Initializing of the edit menu
    m_edit = new JMenu(Language.get("MENU_EDIT"));
    m_edit.setMnemonic(Language.get("MENU_EDIT_MN").charAt(0));
    m_edit_undo = new JMenuItem(Language.get("MENU_EDIT_UNDO"));
    m_edit_undo.setIcon(edit_undo_icon);
    m_edit_undo.setMnemonic(Language.get("MENU_EDIT_UNDO_MN").charAt(0));
    m_edit_undo.setAccelerator(KeyStroke.getKeyStroke('Z',mask));
    m_edit_undo.addActionListener(this);
    m_edit_redo = new JMenuItem(Language.get("MENU_EDIT_REDO"));
    m_edit_redo.setIcon(edit_redo_icon);
    m_edit_redo.setMnemonic(Language.get("MENU_EDIT_REDO_MN").charAt(0));
    m_edit_redo.setAccelerator(KeyStroke.getKeyStroke('Y',mask));
    m_edit_redo.addActionListener(this);
    m_edit_theme = new JMenuItem(Language.get("MENU_EDIT_THEME"));
    m_edit_theme.setIcon(edit_icon);
    m_edit_theme.setMnemonic(Language.get("MENU_EDIT_THEME_MN").charAt(0));
    m_edit_theme.setAccelerator(KeyStroke.getKeyStroke('I',mask));
    m_edit_theme.addActionListener(this);
    m_edit_vars = new JMenuItem(Language.get("MENU_EDIT_VARS"));
    m_edit_vars.setMnemonic(Language.get("MENU_EDIT_VARS_MN").charAt(0));
    m_edit_vars.setAccelerator(KeyStroke.getKeyStroke('G',mask));
    m_edit_vars.addActionListener(this);
    m_edit_prefs = new JMenuItem(Language.get("MENU_EDIT_PREFS"));
    m_edit_prefs.setIcon(editor_icon);
    m_edit_prefs.setMnemonic(Language.get("MENU_EDIT_PREFS_MN").charAt(0));
    m_edit_prefs.addActionListener(this);
    m_edit_up = new JMenuItem(Language.get("MENU_EDIT_UP"));
    m_edit_up.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,mask));
    m_edit_up.addActionListener(this);
    m_edit_down = new JMenuItem(Language.get("MENU_EDIT_DOWN"));
    m_edit_down.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,mask));
    m_edit_down.addActionListener(this);
    m_edit_left = new JMenuItem(Language.get("MENU_EDIT_LEFT"));
    m_edit_left.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,mask));
    m_edit_left.addActionListener(this);
    m_edit_right = new JMenuItem(Language.get("MENU_EDIT_RIGHT"));
    m_edit_right.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,mask));
    m_edit_right.addActionListener(this);
    m_edit_del = new JMenuItem(Language.get("WIN_ITEMS_DELETE"));
    if(System.getProperty("os.name").indexOf("Mac")!=-1)
        m_edit_del.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,mask));
    else
        m_edit_del.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
    m_edit_del.addActionListener(this);

    m_edit.add(m_edit_undo);
    m_edit.add(m_edit_redo);
    m_edit.addSeparator();
    m_edit.add(m_edit_theme);
    m_edit.addSeparator();
    m_edit.add(m_edit_vars);
    m_edit.add(m_edit_prefs);
    m_edit.addSeparator();
    m_edit.add(m_edit_up);
    m_edit.add(m_edit_down);
    m_edit.add(m_edit_left);
    m_edit.add(m_edit_right);
    m_edit.addSeparator();
    m_edit.add(m_edit_del);

    //Initializing of the help menu
    m_help = new JMenu(Language.get("MENU_HELP"));
    m_help.setMnemonic(Language.get("MENU_HELP_MN").charAt(0));
    m_help_doc = new JMenuItem(Language.get("MENU_HELP_DOC"));
    m_help_doc.setIcon(help_icon);
    m_help_doc.setMnemonic(Language.get("MENU_HELP_DOC_MN").charAt(0));
    m_help_doc.addActionListener(this);
    m_help_doc.setAccelerator(KeyStroke.getKeyStroke("F1"));
    m_help_about = new JMenuItem(Language.get("MENU_HELP_ABOUT"));
    m_help_about.setMnemonic(Language.get("MENU_HELP_ABOUT_MN").charAt(0));
    m_help_about.addActionListener(this);

    m_help.add(m_help_doc);
    m_help.add(m_help_about);

    //Fill and set menubar
    mbar.add(m_file);
    mbar.add(m_edit);
    mbar.add(m_help);

    setJMenuBar(mbar);

    //Resources, window and item windows

    jdesk = new JDesktopPane();

    s = new Skin(this);

    resources = new JInternalFrame(Language.get("WIN_RES_TITLE"),true,false);
    resources.setFrameIcon(resources_icon);
    resources.setMinimumSize(new Dimension(190,200));
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
    res_tree.addMouseListener(this);
    JScrollPane res_tree_sp = new JScrollPane(res_tree);
    resources.add(res_tree_sp);
    res_tree_sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    res_add_bitmap = new JButton("",add_bitmap_icon);
    res_add_bitmap.setToolTipText(Language.get("WIN_RES_ADD_BMP"));
    res_add_bitmap.setMaximumSize(new Dimension(24,24));
    res_add_bitmap.setPreferredSize(new Dimension(24,24));
    res_add_bitmap.addActionListener(this);
    res_add_font = new JButton("",add_font_icon);
    res_add_font.setToolTipText(Language.get("WIN_RES_ADD_FONT"));
    res_add_font.setMaximumSize(new Dimension(24,24));
    res_add_font.setPreferredSize(new Dimension(24,24));
    res_add_font.addActionListener(this);
    res_duplicate = new JButton("",copy_icon);
    res_duplicate.setToolTipText(Language.get("WIN_RES_COPY"));
    res_duplicate.setPreferredSize(new Dimension(24,24));
    res_duplicate.addActionListener(this);
    res_edit = new JButton("",edit_icon);
    res_edit.setToolTipText(Language.get("WIN_RES_EDIT"));
    res_edit.setMaximumSize(new Dimension(24,24));
    res_edit.setPreferredSize(new Dimension(24,24));
    res_edit.addActionListener(this);
    res_del = new JButton("",delete_icon);
    res_del.setToolTipText(Language.get("WIN_RES_DELETE"));
    res_del.setMaximumSize(new Dimension(24,24));
    res_del.setPreferredSize(new Dimension(24,24));
    res_del.addActionListener(this);
    resources.add(res_add_bitmap);
    resources.add(res_add_font);
    resources.add(res_duplicate);
    resources.add(res_edit);
    resources.add(res_del);

    res_layout.putConstraint(SpringLayout.WEST, res_tree_sp,5,SpringLayout.WEST, resources.getContentPane());
    res_layout.putConstraint(SpringLayout.NORTH, res_tree_sp,5,SpringLayout.NORTH, resources.getContentPane());
    res_layout.putConstraint(SpringLayout.NORTH, res_add_bitmap,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_add_font,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_duplicate,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_edit,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_del,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_add_bitmap,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_add_font,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_edit,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, res_del,5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.WEST, res_add_bitmap,5,SpringLayout.WEST, resources.getContentPane());
    res_layout.putConstraint(SpringLayout.WEST, res_add_font,5,SpringLayout.EAST, res_add_bitmap);
    res_layout.putConstraint(SpringLayout.WEST, res_duplicate,5,SpringLayout.EAST, res_add_font);
    res_layout.putConstraint(SpringLayout.WEST, res_edit,5,SpringLayout.EAST, res_duplicate);
    res_layout.putConstraint(SpringLayout.WEST, res_del,5,SpringLayout.EAST, res_edit);
    res_layout.putConstraint(SpringLayout.SOUTH, resources.getContentPane(),24+5+5,SpringLayout.SOUTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.NORTH, resources.getContentPane(),5,SpringLayout.NORTH, res_tree_sp);
    res_layout.putConstraint(SpringLayout.EAST, resources.getContentPane(),5,SpringLayout.EAST, res_tree_sp);

    resources.pack();
    resources.setSize(Config.getInt("win.res.width"),Config.getInt("win.res.height"));
    resources.setVisible(true);

    windows = new JInternalFrame(Language.get("WIN_WIN_TITLE"),true,false);
    windows.setFrameIcon(windows_icon);
    windows.setMinimumSize(new Dimension(190,150));
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
    win_tree.addMouseListener(this);
    JScrollPane win_tree_sp = new JScrollPane(win_tree);
    windows.add(win_tree_sp);
    win_tree_sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    win_add_window = new JButton("",add_window_icon);
    win_add_window.setToolTipText(Language.get("WIN_WIN_ADD"));
    win_add_window.setMaximumSize(new Dimension(24,24));
    win_add_window.setPreferredSize(new Dimension(24,24));
    win_add_window.addActionListener(this);
    win_add_layout = new JButton("",add_layout_icon);
    win_add_layout.setToolTipText(Language.get("WIN_WIN_ADD_LAY"));
    win_add_layout.setMaximumSize(new Dimension(24,24));
    win_add_layout.setPreferredSize(new Dimension(24,24));
    win_add_layout.addActionListener(this);
    win_layout_up = new JButton("",up_icon);
    win_layout_up.setToolTipText(Language.get("WIN_WIN_MOVE_UP"));
    win_layout_up.setMaximumSize(new Dimension(24,12));
    win_layout_up.setPreferredSize(new Dimension(24,12));
    win_layout_up.addActionListener(this);
    win_layout_up.setEnabled(false);
    win_layout_down = new JButton("",down_icon);
    win_layout_down.setToolTipText(Language.get("WIN_WIN_MOVE_DOWN"));
    win_layout_down.setMaximumSize(new Dimension(24,12));
    win_layout_down.setPreferredSize(new Dimension(24,12));
    win_layout_down.addActionListener(this);
    win_layout_down.setEnabled(false);
    win_duplicate = new JButton("",copy_icon);
    win_duplicate.setToolTipText(Language.get("WIN_WIN_COPY"));
    win_duplicate.setMaximumSize(new Dimension(24,24));
    win_duplicate.setPreferredSize(new Dimension(24,24));
    win_duplicate.addActionListener(this);
    win_edit = new JButton("",edit_icon);
    win_edit.setToolTipText(Language.get("WIN_WIN_EDIT"));
    win_edit.setMaximumSize(new Dimension(24,24));
    win_edit.setPreferredSize(new Dimension(24,24));
    win_edit.addActionListener(this);
    win_del = new JButton("",delete_icon);
    win_del.setToolTipText(Language.get("WIN_WIN_DELETE"));
    win_del.setMaximumSize(new Dimension(24,24));
    win_del.setPreferredSize(new Dimension(24,24));
    win_del.addActionListener(this);
    windows.add(win_add_window);
    windows.add(win_add_layout);
    windows.add(win_duplicate);
    windows.add(win_edit);
    windows.add(win_layout_up);
    windows.add(win_layout_down);
    windows.add(win_del);

    win_layout.putConstraint(SpringLayout.WEST, win_tree_sp,5,SpringLayout.WEST, windows.getContentPane());
    win_layout.putConstraint(SpringLayout.NORTH, win_tree_sp,5,SpringLayout.NORTH, windows.getContentPane());
    win_layout.putConstraint(SpringLayout.NORTH, win_add_window,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_add_layout,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_layout_up,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_layout_down,0,SpringLayout.SOUTH, win_layout_up);
    win_layout.putConstraint(SpringLayout.NORTH, win_duplicate,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_edit,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, win_del,5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.WEST, win_add_window,5,SpringLayout.WEST, windows.getContentPane());
    win_layout.putConstraint(SpringLayout.WEST, win_add_layout,5,SpringLayout.EAST, win_add_window);
    win_layout.putConstraint(SpringLayout.WEST, win_layout_up,5,SpringLayout.EAST, win_add_layout);
    win_layout.putConstraint(SpringLayout.WEST, win_layout_down,5,SpringLayout.EAST, win_add_layout);
    win_layout.putConstraint(SpringLayout.WEST, win_duplicate,5,SpringLayout.EAST, win_layout_up);
    win_layout.putConstraint(SpringLayout.WEST, win_edit,5,SpringLayout.EAST, win_duplicate);
    win_layout.putConstraint(SpringLayout.WEST, win_del,5,SpringLayout.EAST, win_edit);
    win_layout.putConstraint(SpringLayout.SOUTH, windows.getContentPane(),24+5+5,SpringLayout.SOUTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.NORTH, windows.getContentPane(),5,SpringLayout.NORTH, win_tree_sp);
    win_layout.putConstraint(SpringLayout.EAST, windows.getContentPane(),5,SpringLayout.EAST, win_tree_sp);

    windows.pack();
    windows.setSize(Config.getInt("win.win.width"),Config.getInt("win.win.height"));
    windows.setVisible(true);

    items = new JInternalFrame(Language.get("WIN_ITEMS_TITLE"),true,false);
    items.setFrameIcon(items_icon);
    items.setMinimumSize(new Dimension(190,150));
    SpringLayout items_layout = new SpringLayout();
    items.setLayout(items_layout);
    items_tree_model = new DefaultTreeModel(s.getItemsTree());
    items_tree = new JTree(items_tree_model);
    ToolTipManager.sharedInstance().registerComponent(items_tree);
    items_tree.setCellRenderer(tree_renderer);
    items_tree.setRootVisible(false);
    items_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    items_tree.addTreeSelectionListener(this);
    items_tree.setDragEnabled(true);
    items_tree.setDropMode(DropMode.ON_OR_INSERT);
    items_tree.setTransferHandler(new ItemTransferHandler(s));
    items_tree.setShowsRootHandles(true);
    items_tree.addMouseListener(this);
    JScrollPane items_tree_sp = new JScrollPane(items_tree);
    items.add(items_tree_sp);
    items_tree_sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    items_add = new JButton("",add_icon);
    items_add.setToolTipText(Language.get("WIN_ITEMS_ADD"));
    items_add.setMaximumSize(new Dimension(24,24));
    items_add.setPreferredSize(new Dimension(24,24));
    items_add.addActionListener(this);
    items_up = new JButton("",up_icon);
    items_up.setToolTipText(Language.get("WIN_ITEMS_MOVE_UP"));
    items_up.setMaximumSize(new Dimension(24,12));
    items_up.setPreferredSize(new Dimension(24,12));
    items_up.addActionListener(this);
    items_down = new JButton("",down_icon);
    items_down.setToolTipText(Language.get("WIN_ITEMS_MOVE_DOWN"));
    items_down.setMaximumSize(new Dimension(24,12));
    items_down.setPreferredSize(new Dimension(24,12));
    items_down.addActionListener(this);
    items_duplicate = new JButton("",copy_icon);
    items_duplicate.setToolTipText(Language.get("WIN_ITEMS_COPY"));
    items_duplicate.setMaximumSize(new Dimension(24,24));
    items_duplicate.setPreferredSize(new Dimension(24,24));
    items_duplicate.addActionListener(this);
    items_edit = new JButton("",edit_icon);
    items_edit.setToolTipText(Language.get("WIN_ITEMS_EDIT"));
    items_edit.setMaximumSize(new Dimension(24,24));
    items_edit.setPreferredSize(new Dimension(24,24));
    items_edit.addActionListener(this);
    items_del = new JButton("",delete_icon);
    items_del.setToolTipText(Language.get("WIN_ITEMS_DELETE"));
    items_del.setMaximumSize(new Dimension(24,24));
    items_del.setPreferredSize(new Dimension(24,24));
    items_del.addActionListener(this);
    items.add(items_add);
    items.add(items_up);
    items.add(items_down);
    items.add(items_duplicate);
    items.add(items_edit);
    items.add(items_del);

    items_layout.putConstraint(SpringLayout.WEST, items_tree_sp,5,SpringLayout.WEST, items.getContentPane());
    items_layout.putConstraint(SpringLayout.NORTH, items_tree_sp,5,SpringLayout.NORTH, items.getContentPane());
    items_layout.putConstraint(SpringLayout.NORTH, items_add,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_up,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_down,0,SpringLayout.SOUTH, items_up);
    items_layout.putConstraint(SpringLayout.NORTH, items_duplicate,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_edit,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items_del,5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.WEST, items_add,5,SpringLayout.WEST, items.getContentPane());
    items_layout.putConstraint(SpringLayout.WEST, items_up,5,SpringLayout.EAST, items_add);
    items_layout.putConstraint(SpringLayout.WEST, items_down,5,SpringLayout.EAST, items_add);
    items_layout.putConstraint(SpringLayout.WEST, items_duplicate,5,SpringLayout.EAST, items_up);
    items_layout.putConstraint(SpringLayout.WEST, items_edit,5,SpringLayout.EAST, items_duplicate);
    items_layout.putConstraint(SpringLayout.WEST, items_del,5,SpringLayout.EAST, items_edit);
    items_layout.putConstraint(SpringLayout.SOUTH, items.getContentPane(),24+5+5,SpringLayout.SOUTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.NORTH, items.getContentPane(),5,SpringLayout.NORTH, items_tree_sp);
    items_layout.putConstraint(SpringLayout.EAST, items.getContentPane(),5,SpringLayout.EAST, items_tree_sp);

    items.pack();
    items.setSize(Config.getInt("win.items.width"),Config.getInt("win.items.height"));
    items.setVisible(true);

    pvwin = new PreviewWindow(this);

    jdesk.add(resources);
    jdesk.add(windows);
    jdesk.add(items);
    jdesk.add(pvwin.frame);

    resources.setLocation(Config.getInt("win.res.x"),Config.getInt("win.res.y"));
    windows.setLocation(Config.getInt("win.win.x"),Config.getInt("win.win.y"));
    items.setLocation(Config.getInt("win.items.x"),Config.getInt("win.items.y"));
    pvwin.frame.setLocation(250,0);

    res_add_bitmap_pu = new JPopupMenu();
    res_add_bitmap_pu_b = new JMenuItem(Language.get("WIN_RES_PU_ADD_BMP"));
    res_add_bitmap_pu_b.addActionListener(this);
    res_add_bitmap_pu.add(res_add_bitmap_pu_b);
    res_add_bitmap_pu_s = new JMenuItem(Language.get("WIN_RES_PU_ADD_SBMP"));
    res_add_bitmap_pu_s.addActionListener(this);
    res_add_bitmap_pu.add(res_add_bitmap_pu_s);
    jdesk.add(res_add_bitmap_pu);

    items_add_pu = new JPopupMenu();
    items_add_pu_tp = new JMenu(Language.get("WIN_ITEMS_PU_PANEL_ADD"));
    items_add_pu_tp_anchor = new JMenuItem(Language.get("ANCHOR"));
    items_add_pu_tp_anchor.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_anchor);
    items_add_pu_tp_button = new JMenuItem(Language.get("BUTTON"));
    items_add_pu_tp_button.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_button);
    items_add_pu_tp_checkbox = new JMenuItem(Language.get("CHECKBOX"));
    items_add_pu_tp_checkbox.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_checkbox);
    items_add_pu_tp_image = new JMenuItem(Language.get("IMAGE"));
    items_add_pu_tp_image.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_image);
    items_add_pu_tp_panel = new JMenuItem(Language.get("PANEL"));
    items_add_pu_tp_panel.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_panel);
    items_add_pu_tp_playtree = new JMenuItem(Language.get("PLAYTREE"));
    items_add_pu_tp_playtree.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_playtree);
    items_add_pu_tp_slider = new JMenuItem(Language.get("SLIDER"));
    items_add_pu_tp_slider.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_slider);
    items_add_pu_tp_text = new JMenuItem(Language.get("TEXT"));
    items_add_pu_tp_text.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_text);
    items_add_pu_tp_video = new JMenuItem(Language.get("VIDEO"));
    items_add_pu_tp_video.addActionListener(this);
    items_add_pu_tp.add(items_add_pu_tp_video);
    items_add_pu.add(items_add_pu_tp);
    items_add_pu.addSeparator();
    items_add_pu_anchor = new JMenuItem(Language.get("ANCHOR"));
    items_add_pu_anchor.addActionListener(this);
    items_add_pu.add(items_add_pu_anchor);
    items_add_pu_button = new JMenuItem(Language.get("BUTTON"));
    items_add_pu_button.addActionListener(this);
    items_add_pu.add(items_add_pu_button);
    items_add_pu_checkbox = new JMenuItem(Language.get("CHECKBOX"));
    items_add_pu_checkbox.addActionListener(this);
    items_add_pu.add(items_add_pu_checkbox);
    items_add_pu_image = new JMenuItem(Language.get("IMAGE"));
    items_add_pu_image.addActionListener(this);
    items_add_pu.add(items_add_pu_image);
    items_add_pu_panel = new JMenuItem(Language.get("PANEL"));
    items_add_pu_panel.addActionListener(this);
    items_add_pu.add(items_add_pu_panel);
    items_add_pu_playtree = new JMenuItem(Language.get("PLAYTREE"));
    items_add_pu_playtree.addActionListener(this);
    items_add_pu.add(items_add_pu_playtree);
    items_add_pu_slider = new JMenuItem(Language.get("SLIDER"));
    items_add_pu_slider.addActionListener(this);
    items_add_pu.add(items_add_pu_slider);
    items_add_pu_text = new JMenuItem(Language.get("TEXT"));
    items_add_pu_text.addActionListener(this);
    items_add_pu.add(items_add_pu_text);
    items_add_pu_video = new JMenuItem(Language.get("VIDEO"));
    items_add_pu_video.addActionListener(this);
    items_add_pu.add(items_add_pu_video);
    jdesk.add(items_add_pu);

    //Main window layout
    layout = new BorderLayout();
    setLayout(layout);
    add(jdesk, BorderLayout.CENTER);

    if(Boolean.parseBoolean(Config.get("toolbar"))) {
      showToolbar();
    } else {
      hideToolbar();
    }

    setMinimumSize(new Dimension(640,480));

    setSize(Config.getInt("win.main.width"),Config.getInt("win.main.height"));
    if(Config.get("win.main.x")==null) setLocationRelativeTo(null);
    else {
      setLocation(Config.getInt("win.main.x"),Config.getInt("win.main.y"));
    }
    if(Boolean.parseBoolean(Config.get("win.main.maximized"))) {
      setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    setVisible(true);

    update();

    try {
        getVLCdirectory();
    } catch(UnsatisfiedLinkError ex) {
        ex.printStackTrace();
    }

    if(args.length>0) {
      File f = new File(args[0]);
      openFile(f);
    }
    else showWelcomeDialog();
  }
  /**
   * On Windows it reads the VLC skins directory from registry
   * When on Linux it is set to ~/.vlc/skins2/
   */
  private void getVLCdirectory() {
    if(System.getProperty("os.name").indexOf("Windows")!=-1) {
      File f = new File(System.getProperty("ProgramFiles"), "VideoLAN/VLC");
      File exe = new File(f, "vlc.exe");
      if(f.exists() && exe.exists()) {
          vlc_dir = f.getPath();
          vlc_skins_dir = new File(f, "skins").getPath();
      } else {
    	 try {
    		// An improvement upon the unnecessary ICE_JNIRegistry.dll which may not work on all Windows systems (yikes!)
    		Process regProcess = Runtime.getRuntime().exec("REG QUERY \"HKEY_LOCAL_MACHINE\\Software\\VideoLAN\\VLC\" /v InstallDir"); 
    		BufferedReader reader = new BufferedReader(new InputStreamReader(regProcess.getInputStream()));
    		String cmdLine;
    		while((cmdLine=reader.readLine()) !=null) {
    			if(cmdLine.contains("InstallDir")) {
    				int index = cmdLine.lastIndexOf("REG_SZ");
    				if(index !=-1) {
    					vlc_dir = cmdLine.substring(index+6).trim();
    					vlc_skins_dir = vlc_dir+"skins\\";
    					break;
    				}
    			} 
    		}
    	 } catch (Exception e) {
            System.err.println("Could not read VLC installation directory from Registry. VLC might not be properly installed.");
            e.printStackTrace();
          }
      }
    }
    else if(System.getProperty("os.name").indexOf("Linux")!=-1){
      vlc_skins_dir = "/home/"+System.getProperty("user.name")+"/.local/share/vlc/skins2/";
    }

    if(vlc_skins_dir==null) vlc_skins_dir = "";

    if(base_fc==null) base_fc = new JFileChooser();
    if(Config.get("open.folder")==null) base_fc.setCurrentDirectory(new File(vlc_skins_dir));
    else base_fc.setCurrentDirectory(new File(Config.get("open.folder")));
  }

  public void showToolbar() {
    if(tbar!=null) remove(tbar);
    else initToolbar();
    tbar.setOrientation(Config.getInt("toolbar.orientation"));
    add(tbar, Config.get("toolbar.constraints"));
    layout.layoutContainer(getContentPane());
    tbar.doLayout();
    if(Boolean.parseBoolean(Config.get("toolbar.floating"))) {
      ((BasicToolBarUI)tbar.getUI()).setFloating(true, new Point(Config.getInt("toolbar.x"), Config.getInt("toolbar.y")));
      Component c = tbar;
      while( !c.getClass().toString().endsWith("ToolBarDialog") ) {
        c = c.getParent();
      }
      c.setLocation(Config.getInt("toolbar.x"), Config.getInt("toolbar.y"));
    }
  }

  public void hideToolbar() {
    if(tbar==null) return;
    saveToolbarState();
    if(((BasicToolBarUI)tbar.getUI()).isFloating()) {
      ((BasicToolBarUI)tbar.getUI()).setFloating(false, new Point(0,0));
    }
    remove(tbar);
    layout.layoutContainer(getContentPane());
  }

  private void initToolbar() {
    tbar = new JToolBar();
    //tbar.setFloatable(false);

    //Toolbar elements
    tbar_open_btn = new JButton();
    tbar_open_btn.setIcon(createIcon("icons/tbar_open.png"));
    tbar_open_btn.setToolTipText(Language.get("TOOLBAR_OPEN"));
    tbar_open_btn.addActionListener(this);
    tbar.add(tbar_open_btn);
    tbar_save_btn = new JButton();
    tbar_save_btn.setIcon(createIcon("icons/tbar_save.png"));
    tbar_save_btn.setToolTipText(Language.get("TOOLBAR_SAVE"));
    tbar_save_btn.addActionListener(this);
    tbar.add(tbar_save_btn);
    tbar.addSeparator();
    tbar_undo_btn = new JButton();
    tbar_undo_btn.setIcon(createIcon("icons/tbar_undo.png"));
    tbar_undo_btn.setToolTipText(Language.get("TOOLBAR_UNDO"));
    tbar_undo_btn.addActionListener(this);
    tbar.add(tbar_undo_btn);
    tbar_redo_btn = new JButton();
    tbar_redo_btn.setIcon(createIcon("icons/tbar_redo.png"));
    tbar_redo_btn.setToolTipText(Language.get("TOOLBAR_REDO"));
    tbar_redo_btn.addActionListener(this);
    tbar.add(tbar_redo_btn);
    tbar.addSeparator();
    tbar_move_btn = new JButton();
    tbar_move_btn.setIcon(createIcon("icons/tbar_move.png"));
    tbar_move_btn.setToolTipText(Language.get("TOOLBAR_MOVE"));
    tbar_move_btn.addActionListener(this);
    tbar_move_btn.setSelected(true);
    tbar.add(tbar_move_btn);
    tbar_path_btn = new JButton();
    tbar_path_btn.setIcon(createIcon("icons/tbar_path.png"));
    tbar_path_btn.setToolTipText(Language.get("TOOLBAR_PATH"));
    tbar_path_btn.addActionListener(this);
    tbar.add(tbar_path_btn);
  }

  private void saveToolbarState() {
    if(tbar!=null && layout.getConstraints(tbar)!=null && !layout.getConstraints(tbar).equals("null"))
      Config.set("toolbar.constraints", layout.getConstraints(tbar));

    Config.set("toolbar.orientation", tbar.getOrientation());

    if(((BasicToolBarUI)tbar.getUI()).isFloating()) {
      Component c = tbar;
      while( !c.getClass().toString().endsWith("ToolBarDialog") ) {
        c = c.getParent();
      }
      System.out.println(c.getClass().toString());
      System.out.println(c.getBounds());
      System.out.println(c.getX()+":"+c.getY());
      System.out.println(c.getParent().getClass().toString());
      System.out.println(c.getParent().getBounds());
      Config.set("toolbar.floating", true);
      Config.set("toolbar.x", c.getX());
      Config.set("toolbar.y", c.getY());
    } else {
      Config.set("toolbar.floating", false);
    }
  }

  /**
   * Shows a dialog from which the user can choose to either create a new skin, open an existing skin or quit the skin editor.
   */
  public void showWelcomeDialog() {
    Object[] options= {Language.get("WELCOME_NEW"), Language.get("WELCOME_OPEN"),Language.get("WELCOME_QUIT")};
    int n = JOptionPane.showOptionDialog(this,Language.get("WELCOME_MSG"),Language.get("WELCOME_TITLE"),
                                         JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
    if(n==0) {
      createNew();
    }
    else if(n==1) {
      openFile();
    }
    else {
      saved = true;
      exit();
    }
  }

  /**
   * Shows the "Open File" dialog.
   */
  private void openFile() {
    opening = true;
    String[] exts = { "xml","vlt" };
    if(System.getProperty("os.name").indexOf("Mac")==-1) {
      if(base_fc == null) {
        base_fc = new JFileChooser(); // Initialize base_fc if it's null
      }
      base_fc.setFileFilter(new CustomFileFilter(base_fc,exts,"*.xml (VLC XML-Skin), *.vlt (VLC Theme)",false,vlc_dir));
      int returnVal = base_fc.showOpenDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        openFile(base_fc.getSelectedFile());
        opening = false;
      }
      else {
        opening = false;
        if(!opened) showWelcomeDialog();
      }
    }
    else {
      FileDialog fd = new FileDialog(this);
      fd.setMode(FileDialog.LOAD);
      fd.setFilenameFilter(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.toUpperCase().endsWith(".XML");
        }
      });
      fd.setVisible(true);
      String f = fd.getFile();
      if(f!=null) {
        openFile(new File(fd.getDirectory(),fd.getFile()));
        opening = false;
      }
      else {
        opening = false;
        if(!opened) showWelcomeDialog();
      }

    }
  }

  /**
   * Opens the given file as a skin.
   * @param f The skin file.
   */
  private void openFile(File f) {
    if(!f.exists()) {
      JOptionPane.showMessageDialog(this,Language.get("ERROR_FILENEXIST_MSG").replaceAll("%f", f.getName()),Language.get("ERROR_FILENEXIST_TITLE"),JOptionPane.ERROR_MESSAGE);
      if(!opened) showWelcomeDialog();
      return;
    }
    opening = true;
    if(pvwin!=null) {
      pvwin.clearLayout();
    }
    ProgressWindow pwin = new ProgressWindow(this,"");
    if(f.toString().toLowerCase().endsWith(".vlt")) {
      String vltname = f.getName().replaceAll(".vlt","");
      Object[] options= {Language.get("VLT_EX_YES"), Language.get("VLT_EX_NO")};
      int n = JOptionPane.showOptionDialog(this,Language.get("VLT_EX_MSG").replaceAll("%f", vltname+"_unpacked"),Language.get("VLT_EX_TITLE"),
                         JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
      if(n!=0) {
        showWelcomeDialog();
      }
      File unpackfolder = new File(f.getParent(),vltname+"_unpacked");
      unpackfolder.mkdirs();
      boolean unpacked=false;
      pwin.setText(Language.get("VLT_EX_PROGRESS"));
      pwin.setVisible(true);
      // <editor-fold defaultstate="collapsed" desc="zip">
      try {
        ZipFile zip = new ZipFile(f);
        Enumeration entries = zip.entries();
        while(entries.hasMoreElements()) {
          ZipEntry ze = (ZipEntry)entries.nextElement();
          File zef = new File(unpackfolder,ze.getName());
          if(ze.getName().endsWith("theme.xml")) f=zef;
          if(ze.isDirectory()) zef.mkdirs();
          else {
            InputStream zeis = zip.getInputStream(ze);
            BufferedInputStream zebis = new BufferedInputStream(zeis);
            zef.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(zef);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            for(int b=0;(b=zebis.read())!=-1;) bos.write((byte)b);
            bos.close();
            fos.close();
            zebis.close();
            zeis.close();
          }
        }
        unpacked=true;
      }
      catch (IOException ex) {
        System.out.println("VLT file is not a valid ZIP file. Trying tar.gz now...");
      }
      // </editor-fold>
      // <editor-fold defaultstate="collapsed" desc="tar.gz">
      if(!unpacked) {

        try {
          FileInputStream fis = new FileInputStream(f);
          GZIPInputStream gzis = new GZIPInputStream(fis);
          //BufferedInputStream bis = new BufferedInputStream(gzis);
          TarInputStream tis = new TarInputStream(gzis);
          for(TarEntry te=null;(te=tis.getNextEntry())!=null;) {
            File tef = new File(unpackfolder,te.getName());
            if(te.getName().endsWith("theme.xml")) f=tef;
            if(te.isDirectory()) tef.mkdirs();
            else {
              BufferedInputStream tebis = new BufferedInputStream(tis);
              tef.getParentFile().mkdirs();
              FileOutputStream fos = new FileOutputStream(tef);
              BufferedOutputStream bos = new BufferedOutputStream(fos);
              for(int b=0;(b=tebis.read())!=-1;) bos.write((byte)b);
              bos.close();
              fos.close();
            }
          }
          tis.close();
          gzis.close();
          fis.close();
          unpacked=true;
        }
        catch(IOException ex) {
          ex.printStackTrace();
        }
      }
      //</editor-fold>
      if(!unpacked) {
        pwin.setVisible(false);
        JOptionPane.showMessageDialog(this,Language.get("ERROR_VLT_UNPACK_MSG"),Language.get("ERROR_VLT_UNPACK_TITLE"),JOptionPane.ERROR_MESSAGE);
        opening=false;
        return;
      }
      if(f==base_fc.getSelectedFile()) {
        pwin.setVisible(false);
        JOptionPane.showMessageDialog(this,Language.get("ERROR_VLT_NOTHEME_MSG"),Language.get("ERROR_VLT_NOTHEME_TITLE"),JOptionPane.ERROR_MESSAGE);
        opening=false;
        return;
      }
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    pwin.setText(Language.get("XML_PARSING_PROGRESS"));
    pwin.setVisible(true);
    setTitle(f.toString()+" - VLC Skin Editor "+VERSION);
    m_file_png.setEnabled(false);
    s = new Skin(this);
    s.open(f);
    selected_resource = null;
    selected_in_windows = null;
    selected_window = null;
    selected_item = null;
    saved = true;
    opening = false;
    opened = true;
    pwin.setVisible(false);
    pwin.dispose();
    hist = new History(this);
    s.gvars.sendUpdate();
  }

  /**
   * Shows a dialog to specify the new skin's location and creates an empty skin.
   */
  private void createNew() {
      base_fc.setFileFilter(new CustomFileFilter(base_fc,"xml","*.xml (VLC XML-Skin Files)",false,vlc_dir));
      int returnVal=base_fc.showSaveDialog(this);
      if(returnVal != JFileChooser.APPROVE_OPTION) {
        if(!opened) showWelcomeDialog();
      }
      else {
        File f = base_fc.getSelectedFile();
        if(!f.getPath().toLowerCase().endsWith(".xml")) f = new File(f.getPath()+".xml");
        setTitle(f.getPath()+" - VLC Skin Editor "+VERSION);
        s.createNew(f);
        selected_resource = null;
        selected_in_windows = null;
        selected_window = null;
        selected_item = null;
        saved = false;
        opened = true;
        hist = new History(this);
      }
  }

  /**
   * Reacts to GUI actions
   * @param e The performed action
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    // <editor-fold defaultstate="collapsed" desc="New File">
    if(e.getSource().equals(m_file_new)) {
      createNew();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Open File">
    else if(e.getSource().equals(m_file_open)||e.getSource().equals(tbar_open_btn)) {
      openFile();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Save File">
    else if(e.getSource().equals(m_file_save)||e.getSource().equals(tbar_save_btn))  {
      s.save();
      saved=true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Test Skin">
    else if(e.getSource().equals(m_file_test))  {
      s.save();
      saved=true;
      String[] command = { vlc_dir+"vlc", "-I", "skins2", "--skins2-last="+s.skinfile.toString(), /*"--skins2-config=\"\"",*/"--skins2-systray" };
      try {
        Runtime.getRuntime().exec(command);
      }
      catch (IOException ex) {
         JOptionPane.showMessageDialog(this,Language.get("ERROR_VLC_LAUNCH_MSG"),Language.get("ERROR_VLC_LAUNCH_ERROR"),JOptionPane.ERROR_MESSAGE);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Export VLT">
    else if(e.getSource().equals(m_file_vlt))  {
      if(vlt_fc==null) {
        vlt_fc = new JFileChooser();
        vlt_fc.setCurrentDirectory(new File(vlc_skins_dir));
        vlt_fc.setFileFilter(new CustomFileFilter(vlt_fc,"vlt",".vlt  (VLC Theme)",false,""));
        vlt_fc.setAcceptAllFileFilterUsed(false);
      }
      int returnVal = vlt_fc.showSaveDialog(this);
      File f = null;
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        f = vlt_fc.getSelectedFile();
        if(!f.getPath().toLowerCase().endsWith(".vlt")) f = new File(f.getPath()+".vlt");
      }
      else return;
      ProgressWindow pwin = new ProgressWindow(this,"");
      pwin.setText(Language.get("VLT_PROGRESS"));
      pwin.setVisible(true);
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
        java.util.List<String> files = new LinkedList<String>();
        for(int i=0;i<s.resources.size();i++) {
          if(s.resources.get(i).getClass().equals(Bitmap.class)) {
            try {
              Bitmap b = (Bitmap)s.resources.get(i);
              String fn = s.skinfolder+b.file;
              if(!files.contains(fn)) { //To avoid double files (e.g. one font file used by several font objects)
                File file = new File(fn);
                if(!file.exists()) {
                  if(System.getProperty("os.name").indexOf("Vista")!=-1) {
                    String pFiles = System.getenv("ProgramFiles");
                    if(!pFiles.endsWith("\\")) pFiles+="\\";
                    String vpFiles = System.getenv("USERPROFILE")+"\\AppData\\Local\\VirtualStore\\Program Files\\";
                    if(fn.indexOf(pFiles)!=-1) {
                      fn = fn.replaceFirst(pFiles, vpFiles);
                      file = new File(fn);
                      if(!file.exists()) System.err.println("Error while creating VLT: File not found, not even in VirtualStore! "+fn);
                    } else {
                     System.err.println("Error while creating VLT: File not found! "+fn);
                    }
                  } else {
                    System.err.println("Error while creating VLT: File not found! "+fn);
                  }
                }
                tgz.putNextEntry(new TarEntry(b.file));
                FileInputStream fis = new FileInputStream(file);
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
            catch (IOException ex) {
              ex.printStackTrace();
              pwin.setVisible(false);
              JOptionPane.showMessageDialog(this,Language.get("ERROR_VLT_MSG"),Language.get("ERROR_VLT_TILE"),JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception ex){}
          }
          else if(s.resources.get(i).getClass().equals(vlcskineditor.resources.Font.class)) {
            try {
              vlcskineditor.resources.Font fnt = (vlcskineditor.resources.Font)s.resources.get(i);
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
            catch (IOException ex) {
              ex.printStackTrace();
              pwin.setVisible(false);
              JOptionPane.showMessageDialog(this,Language.get("ERROR_VLT_MSG"),Language.get("ERROR_VLT_TILE"),JOptionPane.ERROR_MESSAGE);
            }
          }
        }

        tgz.close();
        pwin.setVisible(false);
        JOptionPane.showMessageDialog(this,Language.get("VLT_SUCCESS_MSG"),Language.get("VLT_SUCCESS_TITLE"),JOptionPane.INFORMATION_MESSAGE);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        pwin.setVisible(false);
        JOptionPane.showMessageDialog(this,Language.get("ERROR_VLT_MSG"),Language.get("ERROR_VLT_TILE"),JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Save Preview">
    else if(e.getSource().equals(m_file_png)) {
      JFileChooser png_fc = new JFileChooser();
      png_fc.setAcceptAllFileFilterUsed(false);
      png_fc.setFileFilter(new CustomFileFilter(png_fc, "png", "*.PNG (Portable Network Graphics)", false, ""));
      int i = png_fc.showSaveDialog(this);
      if(i==JFileChooser.APPROVE_OPTION) {
        File f = png_fc.getSelectedFile();
        if(!f.getPath().toLowerCase().endsWith(".png"))
          f = new File(f.getPath()+".png");
        pvwin.savePNG(f);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Quit">
    else if(e.getSource().equals(m_file_quit)) {
      exit();
    }
    // </editor-fold>
    else if(e.getSource().equals(m_edit_theme)) s.showThemeOptions();
    else if(e.getSource().equals(m_edit_vars)) s.gvars.showOptions();
    else if(e.getSource().equals(m_edit_prefs)) Config.showOptions();
    // <editor-fold defaultstate="collapsed" desc="Open Help">
    else if(e.getSource().equals(m_help_doc)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/");
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="About">
    else if(e.getSource().equals(m_help_about)) {
      JOptionPane.showMessageDialog(this,Language.get("ABOUT_MSG").replaceAll("%w", "http://www.videolan.org/vlc/skineditor.html").replaceAll("%y", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).replaceAll("%v", VERSION)),
            Language.get("ABOUT_TITLE").replaceAll("%v", VERSION), JOptionPane.INFORMATION_MESSAGE,icon);
    }
    // </editor-fold>
    else if(e.getSource().equals(res_add_bitmap)) res_add_bitmap_pu.show(res_add_bitmap,0,0);
    // <editor-fold defaultstate="collapsed" desc="Add Bitmap">
    else if(e.getSource().equals(res_add_bitmap_pu_b)) {
      if(bitmap_adder==null) {
        bitmap_adder = new JFileChooser();
        bitmap_adder.setFileFilter(new CustomFileFilter(bitmap_adder,"png",Language.get("ADD_BMP_FILE_FILTER_DESC"),true,s.skinfolder));
        bitmap_adder.setCurrentDirectory(new File(s.skinfolder));
        bitmap_adder.setAcceptAllFileFilterUsed(false);
        bitmap_adder.setMultiSelectionEnabled(true);
      }
      int returnVal = bitmap_adder.showOpenDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        File[] files = bitmap_adder.getSelectedFiles();
        vlcskineditor.resources.Bitmap last_added = null;
        for(int i=0;i<files.length;i++) {
          s.resources.add(last_added = new vlcskineditor.resources.Bitmap(s,files[i]));
        }
        s.updateResources();
        if(last_added!=null) s.expandItem(last_added.id);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add SubBitmap">
    else if(e.getSource().equals(res_add_bitmap_pu_s)) {
      if(getSelectedResource()!=null) {
        Resource r = s.getResource(getSelectedResource());
        if(r!=null) {
          if(r.getClass()==Bitmap.class) {
            Bitmap b = (Bitmap)r;
            b.SubBitmaps.add(new vlcskineditor.resources.SubBitmap(s,b));
          }
          else if(r.getClass()==SubBitmap.class) {
            SubBitmap sb = (SubBitmap)r;
            Bitmap b = sb.getParentBitmap();
            b.SubBitmaps.add(new vlcskineditor.resources.SubBitmap(s,b));
          }
          else {
            JOptionPane.showMessageDialog(this,Language.get("ERROR_ADD_SBMP_NOTBMP"),Language.get("ERROR_ADD_SBMP_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          }
        }
        else {
          JOptionPane.showMessageDialog(this,Language.get("ERROR_ADD_SBMP_NOBMP"),Language.get("ERROR_ADD_SBMP_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        }
      }
      else {
        JOptionPane.showMessageDialog(this,Language.get("ERROR_ADD_SBMP_NOBMP"),Language.get("ERROR_ADD_SBMP_TITLE"),JOptionPane.INFORMATION_MESSAGE);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add font">
    else if(e.getSource().equals(res_add_font)) {
      if(font_adder==null) {
        font_adder = new JFileChooser();
        String[] ext = { "ttf" , "otf" };
        font_adder.setFileFilter(new CustomFileFilter(font_adder,ext,Language.get("ADD_FONT_FILE_FILTER_DESC"),true,s.skinfolder));
        font_adder.setCurrentDirectory(new File(s.skinfolder));
        font_adder.setAcceptAllFileFilterUsed(false);
        font_adder.setMultiSelectionEnabled(true);
      }
      int returnVal = font_adder.showOpenDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        File[] files = font_adder.getSelectedFiles();
        vlcskineditor.resources.Font last_added = null;
        for(int i=0;i<files.length;i++) {
          s.resources.add(last_added = new vlcskineditor.resources.Font(s,files[i]));
        }
        s.updateResources();
        if(last_added!=null) s.expandItem(last_added.id);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Duplicate resource">
    else if(e.getSource().equals(res_duplicate)) {
      if(getSelectedResource()==null) return;
      Resource r = s.getResource(getSelectedResource());
      if(r==null) return;
      String p = JOptionPane.showInputDialog(this, Language.get("DUPLICATE_MSG"), "%oldid%_copy");
      if(r.getClass()==Bitmap.class) {
        Bitmap b = (Bitmap)r;
        Bitmap b2 = new Bitmap((Bitmap)b);
        b2.renameForCopy(p);
        s.resources.add(b2);
        s.updateResources();
        s.expandResource(b.id);
      }
      else if(r.getClass()==SubBitmap.class) {
        SubBitmap sb = (SubBitmap)r;
        SubBitmap sb2 = new SubBitmap(sb);
        sb2.renameForCopy(p);
        sb.getParentBitmap().SubBitmaps.add(sb2);
        s.updateResources();
        s.expandResource(sb.id);
      }
      else {
        vlcskineditor.resources.Font f = (vlcskineditor.resources.Font)r;
        vlcskineditor.resources.Font f2 = new vlcskineditor.resources.Font((vlcskineditor.resources.Font)f);
        f2.renameForCopy(p);
        s.resources.add(f2);
        s.updateResources();
        s.expandResource(f.id);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Duplicate window/layout">
    else if(e.getSource().equals(win_duplicate)) {
        if(getSelectedWindow()==null) return;
        String p = JOptionPane.showInputDialog(this, Language.get("DUPLICATE_MSG"), "%oldid%_copy");
        if(getSelectedLayout()==null) {
            Window w = s.getWindow(getSelectedWindow());
            if(w==null) return;
            Window wCopy = new Window(w);
            wCopy.renameForCopy(p);
            s.windows.add(wCopy);
            s.updateWindows();
        }
        else {
            Window w = s.getWindow(getSelectedWindow());
            Layout l = w.getLayout(getSelectedLayout());
            Layout lCopy = new Layout(l);
            lCopy.renameForCopy(p);
            w.layouts.add(lCopy);
            s.updateWindows();
            s.expandLayout(lCopy.id);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Duplicate item">
    else if(e.getSource().equals(items_duplicate)) {
        if(getSelectedItem()==null) return;
        String p = JOptionPane.showInputDialog(this, Language.get("DUPLICATE_MSG"), "%oldid%_copy");
        Item i = s.getItem(getSelectedItem());
        if(i==null) return;
        if(i.getClass()==Anchor.class) {
            Anchor a = new Anchor((Anchor)i);
            a.renameForCopy(p);
            s.getParentListOf(i.id).add(a);
            s.updateItems();
            s.expandItem(a.id);
        }
        else if(i.getClass()==vlcskineditor.items.Button.class) {
            vlcskineditor.items.Button b = new vlcskineditor.items.Button((vlcskineditor.items.Button)i);
            b.renameForCopy(p);
            s.getParentListOf(i.id).add(b);
            s.updateItems();
            s.expandItem(b.id);
        }
        else if(i.getClass()==vlcskineditor.items.Checkbox.class) {
            Checkbox c = new Checkbox((Checkbox)i);
            c.renameForCopy(p);
            s.getParentListOf(i.id).add(c);
            s.updateItems();
            s.expandItem(c.id);
        }
        else if(i.getClass()==Group.class) {
            Group g = new Group((Group)i);
            g.renameForCopy(p);
            s.getParentListOf(i.id).add(g);
            s.updateItems();
            s.expandItem(g.id);
        }
        else if(i.getClass()==vlcskineditor.items.Image.class) {
            vlcskineditor.items.Image im = new vlcskineditor.items.Image((vlcskineditor.items.Image)i);
            im.renameForCopy(p);
            s.getParentListOf(i.id).add(im);
            s.updateItems();
            s.expandItem(im.id);
        }
        else if(i.getClass()==vlcskineditor.items.Panel.class) {
            vlcskineditor.items.Panel pa = new vlcskineditor.items.Panel((vlcskineditor.items.Panel)i);
            pa.renameForCopy(p);
            s.getParentListOf(i.id).add(pa);
            s.updateItems();
            s.expandItem(pa.id);
        }
        else if(i.getClass()==Playtree.class) {
            Playtree pl = new Playtree((Playtree)i);
            pl.renameForCopy(p);
            s.getParentListOf(i.id).add(pl);
            s.updateItems();
            s.expandItem(pl.id);
        }
        else if(i.getClass()==RadialSlider.class) {
            RadialSlider r = new RadialSlider((RadialSlider)i);
            r.renameForCopy(p);
            s.getParentListOf(i.id).add(r);
            s.updateItems();
            s.expandItem(r.id);
        }
        else if(i.getClass()==Slider.class) {
            Slider sl = new Slider((Slider)i);
            sl.renameForCopy(p);
            java.util.List<Item> l = s.getParentListOf(i.id);
            if(l!=null) {
                l.add(sl);
                s.updateItems();
                s.expandItem(sl.id);
            }
            else {
                //The selected slider seems to be in a playtree
                JOptionPane.showMessageDialog(this,"A PlayTree cannot contain more than one slider!","Slider could not be duplicated",JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else if(i.getClass()==SliderBackground.class) {
           //A slider can only have one slider background!
           JOptionPane.showMessageDialog(this,"A slider cannot contain more than one background!","SliderBackgrounds cannot be duplicated",JOptionPane.INFORMATION_MESSAGE);
        }
        else if(i.getClass()==Text.class) {
            Text t = new Text((Text)i);
            t.renameForCopy(p);
            s.getParentListOf(i.id).add(t);
            s.updateItems();
            s.expandItem(t.id);
        }
        else if(i.getClass()==Video.class) {
            Video v = new Video((Video)i);
            v.renameForCopy(p);
            s.getParentListOf(i.id).add(v);
            s.updateItems();
            s.expandItem(v.id);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Resource edit">
    else if(e.getSource().equals(res_edit)) {
      if(getSelectedResource()!=null) {
        Resource r = s.getResource(getSelectedResource());
        if(r!=null) r.showOptions();
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Delete Resource">
    else if(e.getSource().equals(res_del)) {
      deleteSelectedResource();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add window">
    else if(e.getSource().equals(win_add_window)) {
      s.windows.add(new Window(s));
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add layout">
    else if(e.getSource().equals(win_add_layout)) {
      if(getSelectedWindow()!=null) {
        Window w = s.getWindow(getSelectedWindow());
        if(w!=null) w.addLayout();
      }
      else {
        JOptionPane.showMessageDialog(this,Language.get("ERROR_ADD_LAYOUT_MSG"),Language.get("ERROR_ADD_LAYOUT_TITLE"),JOptionPane.INFORMATION_MESSAGE);
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Move layout">
    else if(e.getSource().equals(win_layout_up)) {
      Window w = s.getWindow(getSelectedWindow());
      Layout l = w.getLayout(getSelectedLayout());
      if(l==null) return;
      int index = w.layouts.indexOf(l);
      if(index<=0) return;
      w.layouts.set(index,w.layouts.set(index-1,l));
      s.updateWindows();
      s.expandLayout(l.id);
    }
    else if(e.getSource().equals(win_layout_down)) {
      Window w = s.getWindow(getSelectedWindow());
      Layout l = w.getLayout(getSelectedLayout());
      if(l==null) return;
      int index = w.layouts.indexOf(l);
      if(index>=w.layouts.size()-1) return;
      w.layouts.set(index,w.layouts.set(index+1,l));
      s.updateWindows();
      s.expandLayout(l.id);
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Window edit">
    else if(e.getSource().equals(win_edit)) {
      if(getSelectedLayout()!=null && getSelectedWindow()!=null) {
        Layout l = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout());
        if(l!=null) l.showOptions();
      }
      else if(getSelectedWindow()!=null) {
        Window w = s.getWindow(getSelectedWindow());
        if(w!=null) w.showOptions();
      }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Delete Window/Layout">
    else if(e.getSource().equals(win_del)) {
      deleteSelectedLayoutOrWindow();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add item">
    else if(e.getSource().equals(items_add)) {
      if(getSelectedLayout()==null) return;
      items_add_pu_tp.setEnabled(false);
      if(getSelectedItem()!=null) {
        Item i = s.getItem(getSelectedItem());
        if(i!=null) {
          if(i.getClass().equals(Group.class)) items_add_pu_tp.setEnabled(true);
          if(i.getClass().equals(vlcskineditor.items.Panel.class)) items_add_pu_tp.setEnabled(true);
        }
      }
      items_add_pu.setSelected(null);
      items_add_pu.show(items_add,0,0);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Move item">
    else if(e.getSource().equals(items_up)) {
      Item i = s.getItem(getSelectedItem());
      if(i==null) return;
      java.util.List<Item> l = s.getParentListOf(getSelectedItem());
      if(l==null) return;
      int index = l.indexOf(i);
      if(index<=0) return;
      l.set(index,l.set(index-1,i));
      s.updateItems();
      s.expandItem(getSelectedItem());
    }
    else if(e.getSource().equals(items_down)) {
      Item i = s.getItem(getSelectedItem());
      if(i==null) return;
      java.util.List<Item> l = s.getParentListOf(getSelectedItem());
      int index = l.indexOf(i);
      if(index>=l.size()-1) return;
      l.set(index,l.set(index+1,i));
      s.updateItems();
      s.expandItem(getSelectedItem());
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Item edit">
    else if(e.getSource().equals(items_edit)) {
      if(getSelectedItem()!=null) {
        Item i = s.getItem(getSelectedItem());
        if(i!=null) i.showOptions();
      }
    }
    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Add Item popups">
    else if(e.getSource().equals(items_add_pu_anchor)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new Anchor(s));
    }
    else if(e.getSource().equals(items_add_pu_button)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new vlcskineditor.items.Button(s));
    }
    else if(e.getSource().equals(items_add_pu_checkbox)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new vlcskineditor.items.Checkbox(s));
    }
    else if(e.getSource().equals(items_add_pu_panel)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new vlcskineditor.items.Panel(s));
    }
    else if(e.getSource().equals(items_add_pu_image)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new vlcskineditor.items.Image(s));
    }
    else if(e.getSource().equals(items_add_pu_playtree)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new Playtree(s));
    }
    else if(e.getSource().equals(items_add_pu_slider)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new Slider(s));
    }
    else if(e.getSource().equals(items_add_pu_text)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new Text(s));
    }
    else if(e.getSource().equals(items_add_pu_video)) {
      java.util.List<Item> i = null;
      if(getSelectedItem()!=null ) i = s.getParentListOf(getSelectedItem());
      if(i==null) i = s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()).items;
      i.add(new Video(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_anchor)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Anchor(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_button)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Button(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_checkbox)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Checkbox(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_image)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Image(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_panel)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Panel(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_playtree)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Playtree(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_slider)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Slider(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_text)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Text(s));
    }
    else if(e.getSource().equals(items_add_pu_tp_video)) {
      java.util.List<Item> l = s.getListOf(getSelectedItem());
      if(l!=null) l.add(new vlcskineditor.items.Video(s));
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Item delete">
    else if(e.getSource().equals(items_del)) {
      deleteSelectedItem();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Move selected item">
    else if(e.getSource().equals(m_edit_up)) pvwin.moveItem(0,-1);
    else if(e.getSource().equals(m_edit_down)) pvwin.moveItem(0,1);
    else if(e.getSource().equals(m_edit_right)) pvwin.moveItem(1,0);
    else if(e.getSource().equals(m_edit_left)) pvwin.moveItem(-1,0);
    // </editor-fold>
    else if(e.getSource().equals(m_edit_del)) {
      if(items.isSelected()) {
        deleteSelectedItem();
      } else if(windows.isSelected()) {
        deleteSelectedLayoutOrWindow();
      } else if(resources.isSelected()) {
        deleteSelectedResource();
      }
    }
    else if(e.getSource().equals(m_edit_undo)||e.getSource().equals(tbar_undo_btn)) {
      if(hist!=null) hist.undo();
    }
    else if(e.getSource().equals(m_edit_redo)||e.getSource().equals(tbar_redo_btn)) {
      if(hist!=null) hist.redo();
    }
    else if(e.getSource().equals(tbar_move_btn)) {
      tbar_move_btn.setSelected(true);
      tbar_path_btn.setSelected(false);
      if(pvwin!=null) pvwin.setCursorMode(PreviewWindow.CURSOR_MOVE);
    }
    else if(e.getSource().equals(tbar_path_btn)) {
      tbar_move_btn.setSelected(false);
      tbar_path_btn.setSelected(true);
      if(pvwin!=null) pvwin.setCursorMode(PreviewWindow.CURSOR_PATH);
    }
  }

  private void deleteSelectedItem() {
    if(getSelectedItem()!=null) {
      Item i = s.getItem(getSelectedItem());
      if(i.getClass().equals(SliderBackground.class)) {
        Object[] options= {Language.get("CHOICE_YES"),Language.get("CHOICE_NO")};
          int n = JOptionPane.showOptionDialog(this,Language.get("DEL_CONFIRM_MSG").replaceAll("%n",getSelectedItem()),Language.get("DEL_CONFIRM_TITLE"),
                                       JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
        if(n==0) {
          SliderBackground sbg = (SliderBackground)i;
          Slider parentSlider = sbg.getParentSlider();
          //TODO make undoable
          parentSlider.removeBG();
          s.updateItems();
        }
      } else {
        java.util.List<Item> p = s.getParentListOf(getSelectedItem());
        if(p!=null) {
          Object[] options= {Language.get("CHOICE_YES"),Language.get("CHOICE_NO")};
          int n = JOptionPane.showOptionDialog(this,Language.get("DEL_CONFIRM_MSG").replaceAll("%n",getSelectedItem()),Language.get("DEL_CONFIRM_TITLE"),
                                       JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
          if(n==0) {
            ItemDeletionEvent ide = new ItemDeletionEvent(p,i,p.indexOf(i),s);
            p.remove(s.getItem(getSelectedItem()));
            s.updateItems();
            hist.addEvent(ide);
          }
        }
      }
    }
  }

  private void deleteSelectedLayoutOrWindow() {
    if(getSelectedLayout()!=null) {
      Window w = s.getWindow(getSelectedWindow());
      Layout l = w.getLayout(getSelectedLayout());
      if(l!=null) {
        Object[] options= {Language.get("CHOICE_YES"),Language.get("CHOICE_NO")};
      int n = JOptionPane.showOptionDialog(this,Language.get("DEL_CONFIRM_MSG").replaceAll("%n", l.id),Language.get("DEL_CONFIRM_TITLE"),
                                     JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
        if(n==0) {
          LayoutDeletionEvent lde = new LayoutDeletionEvent(w, l, w.layouts.indexOf(l), s);
          pvwin.clearLayout();
          m_file_png.setEnabled(false);
          w.layouts.remove(l);
          s.updateWindows();
          s.updateItems();
          hist.addEvent(lde);
        }
      }
    }
    else if(getSelectedWindow()!=null) {
      Window w= s.getWindow(getSelectedWindow());
      if(w!=null) {
        Object[] options= {Language.get("CHOICE_YES"),Language.get("CHOICE_NO")};
      int n = JOptionPane.showOptionDialog(this,Language.get("DEL_CONFIRM_MSG").replaceAll("%n", w.id),Language.get("DEL_CONFIRM_TITLE"),
                                     JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
        if(n==0) {
          WindowDeletionEvent wde = new WindowDeletionEvent(w, s, s.windows.indexOf(w));
          s.windows.remove(w);
          s.updateWindows();
          hist.addEvent(wde);
        }
      }
    }
  }

  private void deleteSelectedResource() {
    if(s.isUsed(getSelectedResource())) {
      JOptionPane.showMessageDialog(this,Language.get("ERROR_RES_DEL_INUSE"),Language.get("ERROR_RES_DEL_TITLE"),JOptionPane.INFORMATION_MESSAGE);
    }
    else {
      Object[] options= {Language.get("CHOICE_YES"),Language.get("CHOICE_NO")};
      int n = JOptionPane.showOptionDialog(this,Language.get("DEL_CONFIRM_MSG").replaceAll("%n",getSelectedResource()),Language.get("DEL_CONFIRM_TITLE"),
                                     JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
      if(n==0) {
        Resource r = s.getResource(getSelectedResource());
        Bitmap parent = null;
        if(r.getClass().equals(SubBitmap.class)) {
          for(Resource i:s.resources) {
            if(i.getClass().equals(Bitmap.class)) {
              if(((Bitmap)i).SubBitmaps.contains(r)) parent = (Bitmap)i;
            }
          }
          SubBitmapDeletionEvent sde = new SubBitmapDeletionEvent(s,parent,(SubBitmap)r,parent.SubBitmaps.indexOf(r));
          hist.addEvent(sde);
          parent.SubBitmaps.remove(r);
        } else {
          ResourceDeletionEvent rde = new ResourceDeletionEvent(s,r,s.resources.indexOf(r));
          hist.addEvent(rde);
          s.resources.remove(r);
        }
        s.updateResources();
      }
    }
  }

  /**
   * Reacts to tree selections
   */
  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if(opening) return;
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
          pvwin.clearLayout();
          items_tree_model.setRoot(new DefaultMutableTreeNode("Root: Items"));
          m_file_png.setEnabled(false);
          win_layout_up.setEnabled(false);
          win_layout_down.setEnabled(false);
        }
        else if(type.equals("Layout")) {
          selected_layout = path[i].toString().substring(path[i].toString().indexOf(": ")+2);
          pvwin.setLayout(s.getWindow(getSelectedWindow()),s.getWindow(getSelectedWindow()).getLayout(getSelectedLayout()));
          s.updateItems();
          m_file_png.setEnabled(true);
          win_layout_up.setEnabled(true);
          win_layout_down.setEnabled(true);
        }
      }
    }
    else if(e.getSource().equals(items_tree)) {
      String selection = e.getPath().getLastPathComponent().toString();
      selected_item = selection.substring(selection.indexOf(": ")+2);
      pvwin.selectItem(s.getItem(getSelectedItem()));
    }
  }
  @Override
  public void windowClosing(WindowEvent e) {
    exit();
  }
  @Override
  public void windowClosed(WindowEvent e) {}
  @Override
  public void windowActivated(WindowEvent e) {
    if(pvwin==null) return;
    if(pvwin.fu==null) {
      pvwin.fu = new FrameUpdater(pvwin,5);
      pvwin.fu.start();
    }
  }
  @Override
  public void windowDeactivated(WindowEvent e) {
    if(pvwin==null || pvwin.fu==null) return;
    pvwin.fu.stopRunning();
    pvwin.fu = null;
  }
  @Override
  public void windowDeiconified(WindowEvent e) {
    if(pvwin.fu==null) {
      pvwin.fu = new FrameUpdater(pvwin,5);
      pvwin.fu.start();
    }
  }
  @Override
  public void windowIconified(WindowEvent e) {
    if(pvwin==null || pvwin.fu==null) return;
    pvwin.fu.stopRunning();
    pvwin.fu = null;
  }
  @Override
  public void windowOpened(WindowEvent e) {}
  @Override
  public void mouseClicked(MouseEvent e) {
    if(e.getClickCount()>1) {
      if(e.getSource().equals(res_tree)) actionPerformed(new ActionEvent(res_edit,ActionEvent.ACTION_FIRST,"Doubleclick"));
      else if(e.getSource().equals(win_tree)) actionPerformed(new ActionEvent(win_edit,ActionEvent.ACTION_FIRST,"Doubleclick"));
      else if(e.getSource().equals(items_tree)) actionPerformed(new ActionEvent(items_edit,ActionEvent.ACTION_FIRST,"Doubleclick"));
    }
    else {
      if(e.getSource().equals(res_tree)) {
        TreePath tp = res_tree.getPathForLocation(e.getX(),e.getY());
        if(tp==null) return;
        if(res_tree.isExpanded(tp)) res_tree.collapsePath(tp);
        else res_tree.expandPath(tp);
      }
      else if(e.getSource().equals(win_tree)) {
        TreePath tp = win_tree.getPathForLocation(e.getX(),e.getY());
        if(tp==null) return;
        if(win_tree.isExpanded(tp)) win_tree.collapsePath(tp);
        else win_tree.expandPath(tp);
      }
      else if(e.getSource().equals(items_tree)) {
        TreePath tp = items_tree.getPathForLocation(e.getX(),e.getY());
        if(tp==null) return;
        if(items_tree.isExpanded(tp)) items_tree.collapsePath(tp);
        else items_tree.expandPath(tp);
      }
    }
  }
  @Override
  public void mousePressed(MouseEvent e) {}
  @Override
  public void mouseReleased(MouseEvent e) {}
  @Override
  public void mouseEntered(MouseEvent e) {}
  @Override
  public void mouseExited(MouseEvent e) {}

  /** Sets the activity state of the undo menu item to the given argument
   * @param enabled Activity state
   */
  public void setUndoEnabled(boolean enabled) {
    m_edit_undo.setEnabled(enabled);
    if(tbar_undo_btn!=null) tbar_undo_btn.setEnabled(enabled);
  }

  /** Sets the activity state of the redo menu item to the given argument
   * @param enabled Activity state
   */
  public void setRedoEnabled(boolean enabled) {
    m_edit_redo.setEnabled(enabled);
    if(tbar_redo_btn!=null) tbar_redo_btn.setEnabled(enabled);
  }

  /** Sets the action description that can be undone
   * @param s Action description
   */
  public void setUndoString(String s) {
    if(s.isEmpty()) m_edit_undo.setText(Language.get("MENU_EDIT_UNDO"));
    else m_edit_undo.setText(Language.get("MENU_EDIT_UNDO")+": "+s);
  }

  /** Sets the action description that can be redone
   * @param s Action description
   */
  public void setRedoString(String s) {
    if(s.isEmpty()) m_edit_redo.setText(Language.get("MENU_EDIT_REDO"));
    else m_edit_redo.setText(Language.get("MENU_EDIT_REDO")+": "+s);
  }

  /**
   * Creates an ImageIcon of an image included in the JAR
   * @param filename The path to the image file inside the JAR
   * @return An ImageIcon representing the given file
   */
  public static ImageIcon createIcon(String filename) {
      java.awt.Image img = null;
      try {
        img = Toolkit.getDefaultToolkit().createImage(Main.class.getResource(filename));
        return new ImageIcon(img);
      } catch (Exception ex) {
        ex.printStackTrace();
        return null;
      }
  }

  /**
   * Checks for updates and downloads and installs them if chosen by user
   */
  private void update() {
    if(VERSION.contains("dev")) return; //Development build won't be updated
    try {
      URL updateURL = new URL(updateURL_s+"?v="+URLEncoder.encode(VERSION,"UTF-8")+"&os="+URLEncoder.encode(System.getProperty("os.name"),"UTF-8"));
      InputStream uis = updateURL.openStream();
      InputStreamReader uisr = new InputStreamReader(uis);
      BufferedReader ubr = new BufferedReader(uisr);
      String header = ubr.readLine();
      if(header.equals("SKINEDITORUPDATEPAGE")) {
        String cver = ubr.readLine();
        String cdl = ubr.readLine();
        if(!cver.equals(VERSION)) {
          System.out.println("Update available!");
          int i = JOptionPane.showConfirmDialog(this, Language.get("UPDATE_MSG").replaceAll("%v",VERSION).replaceAll("%c", cver), Language.get("UPDATE_TITLE"), JOptionPane.YES_NO_OPTION);
          if(i==0) {
            URL url = new URL(cdl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Connect to server.
            connection.connect();
            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
              throw new Exception("Server error! Response code: "+connection.getResponseCode());
            }
            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
              throw new Exception("Invalid content length!");
            }

            int size = contentLength;

            File tempfile = File.createTempFile("vlcse_update", ".zip");
            tempfile.deleteOnExit();
            RandomAccessFile file = new RandomAccessFile(tempfile,"rw");


            InputStream stream = connection.getInputStream();
            int downloaded = 0;
            ProgressWindow pwin = new ProgressWindow(this,Language.get("DOWNLOAD_PROGRESS"));
            pwin.setVisible(true);
            pwin.setProgress(0);
            pwin.setText(Language.get("CONNECT_PROGRESS"));
            while (downloaded<size) {
              /* Size buffer according to how much of the
                 file is left to download. */
              byte buffer[];
              if (size - downloaded > 1024) {
                buffer = new byte[1024];
              } else {
                buffer = new byte[size - downloaded];
              }

              int read = stream.read(buffer);
              if (read == -1)
                break;

              // Write buffer to file.
              file.write(buffer, 0, read);
              downloaded += read;
              pwin.setProgress(downloaded/size);
            }

            file.close();
            System.out.println("Downloaded file to "+tempfile.getAbsolutePath());
            pwin.setVisible(false);
            pwin.dispose();
            pwin = null;

            Helper.unzip(tempfile);

            JOptionPane.showMessageDialog(this, Language.get("UPDATE_SUCCESS_MSG"),Language.get("UPDATE_SUCCESS_TITLE"), JOptionPane.INFORMATION_MESSAGE);

            setVisible(false);
            if(System.getProperty("os.name").indexOf("Windows")!=-1) {
              Runtime.getRuntime().exec("VLCSkinEditor.exe");
            } else {
              Runtime.getRuntime().exec("java -jar VLCSkinEditor.jar");
            }
            System.exit(0);

          } else {
            //DO NOTHING!
          }
        }
        ubr.close();
        uisr.close();
        uis.close();
      } else {
        ubr.close();
        uisr.close();
        uis.close();
        throw new Exception("Update page had invalid header: "+header);
      }
    } catch(Exception ex) {
      JOptionPane.showMessageDialog(this, ex.toString(), Language.get("ERROR_UPDATE_TITLE"), JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }

  /**
   * Checks wheter the skin is saved and asks back if not. Then exits or aborts when user chooses to do so.
   */
  private void exit() {
    if(!saved) {
      Object[] options= { Language.get("CHOICE_YES"), Language.get("CHOICE_NO"), Language.get("CHOICE_CANCEL") };
      int n = JOptionPane.showOptionDialog(
            this,
            Language.get("EXIT_CONFIRM_MSG"),
            Language.get("EXIT_CONFIRM_TITLE"),
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]
            );
      if(n==0) {
        s.save();
        doExit();
      }
      else if(n==1) {
        doExit();
      }
      else {
        return;
      }
    }
    else {
      doExit();
    }
  }

  /**
   * Saves the configuration and quits the program
   */
  private void doExit() {
    if(getExtendedState()!=JFrame.MAXIMIZED_BOTH) {
      Config.set("win.main.x",getX());
      Config.set("win.main.y",getY());
      Config.set("win.main.width", getWidth());
      Config.set("win.main.height", getHeight());
      Config.set("win.main.maximized","false");
    } else {
      Config.set("win.main.maximized","true");
    }
    Config.set("win.res.x",resources.getX());
    Config.set("win.res.y",resources.getY());
    Config.set("win.res.width",resources.getWidth());
    Config.set("win.res.height",resources.getHeight());
    Config.set("win.win.x",windows.getX());
    Config.set("win.win.y",windows.getY());
    Config.set("win.win.width",windows.getWidth());
    Config.set("win.win.height",windows.getHeight());
    Config.set("win.items.x",items.getX());
    Config.set("win.items.y",items.getY());
    Config.set("win.items.width",items.getWidth());
    Config.set("win.items.height",items.getHeight());
    if(base_fc!=null) Config.set("open.folder",base_fc.getCurrentDirectory().getAbsolutePath());
    saveToolbarState();
    Config.save();
    System.exit(0);
  }

  /**
   * Creates a new instance of Main and thus launches the editor
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    //Post update code, only executed after update
    if(new File(".updated").exists()) {

      new File(".updated").delete();
    }

    //Normal program code
    Config.load();
    Language.loadLanguageByCode(Config.get("language"));

    try {
      String laf = Config.get("swing.laf");
      String lafClass = laf;
      if(laf!=null) {
        if(laf.equals("System")) {
          lafClass = UIManager.getSystemLookAndFeelClassName();
        }
        if(laf.equals("Metal: Steel")) {
          lafClass = UIManager.getCrossPlatformLookAndFeelClassName();
          MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        } else if(laf.equals("Metal: Ocean")) {
          lafClass = UIManager.getCrossPlatformLookAndFeelClassName();
          MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        }
      }
      UIManager.setLookAndFeel(lafClass);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    JFrame.setDefaultLookAndFeelDecorated(true);

    new Main(args);
  }

  /**
   * @return the selected resource in the resources window
   */
  public String getSelectedResource() {
    return selected_resource;
  }

  /**
   * @return the object selected in the windows/layouts window
   */
  public String getSelectedInWindows() {
    return selected_in_windows;
  }

  /**
   * @return the selected window
   */
  public String getSelectedWindow() {
    return selected_window;
  }

  /**
   * @return the selected layout
   */
  public String getSelectedLayout() {
    return selected_layout;
  }

  /**
   * @return the selected item in the items window
   */
  public String getSelectedItem() {
    return selected_item;
  }
}
