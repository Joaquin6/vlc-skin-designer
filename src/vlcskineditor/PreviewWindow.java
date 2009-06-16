/*****************************************************************************
 * PreviewWindow.java
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

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import vlcskineditor.history.*;
import vlcskineditor.items.Slider;


/**
 * Handles the preview window and user interaction with it.
 * @author Daniel Dreibrodt
 */
public class PreviewWindow extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

  public static final long serialVersionUID = 85;

  /** The JFrame in which a Layout of a Skin will be shown. */
  public JInternalFrame frame;
  /** The scroll pane holding the preview rendering */
  private JScrollPane scroll_pane;
  /** The panel holding the zoom controls */
  private JPanel zoom_panel;
  /** The zoom control buttons */
  private JButton zoom_more, zoom_less;
  /** The label showing the zoom factor */
  private JLabel zoom_label;
  /** The zoom factor */
  private int z = 1;
  /** The currently rendered layout */
  private Layout l;
  /** The frame updater who is responsible for updating the preview rendering */
  protected FrameUpdater fu;
  /** The currently selected item in the items window's tree */
  private Item selected_item;
  /** Indicates whether the cursors is in a drag action */
  private boolean starteddragging = false;
  /** The starting coordinates of the drag action */
  private int dragstartx, dragstarty, dragstartitemx, dragstartitemy;
  /** The move event added to the history when the dragging action has finished */
  private ItemMoveEvent ime = null;
  /** The slider edit event added to the history when the slider point dragging action has finished */
  private SliderEditEvent see = null;
  /** The main program */
  private Main m;

  private Cursor move_normal_c, path_normal_c, path_active_c, path_add_c, path_del_c;

  /** The cursor mode constant indicating that the cursor should move the selected item */
  public static final int CURSOR_MOVE = 1;
  /** The cursor mode constant indicating that the cursor should edit the path of the selected item */
  public static final int CURSOR_PATH = 2;
  /** The current cursor mode */
  private int mode = CURSOR_MOVE;

  /**
   * Creates a new PreviewWindow that is initially hidden.
   * @param m_ the Main instance of the running Skin Editor
   */
  public PreviewWindow(Main m_) {   
    m = m_;
    frame = new JInternalFrame();
    frame.setLayout(new BorderLayout());
    zoom_panel = new JPanel();
    zoom_panel.setLayout(new FlowLayout());
    zoom_less = new JButton("-");   
    zoom_less.addActionListener(this);
    zoom_panel.add(zoom_less);
    zoom_label = new JLabel(Language.get("ZOOM_FACTOR")+" 1x");
    zoom_panel.add(zoom_label);
    zoom_more = new JButton("+");
    zoom_more.addActionListener(this);
    zoom_panel.add(zoom_more);
    zoom_panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);    
    frame.add(zoom_panel, BorderLayout.PAGE_START);
    scroll_pane = new JScrollPane(this);   
    frame.add(scroll_pane,BorderLayout.CENTER);     
    frame.pack();
    frame.setMinimumSize(zoom_panel.getPreferredSize());
    frame.setVisible(false);
    frame.setResizable(true);        
    frame.setFrameIcon(Main.preview_icon);
    addMouseListener(this);
    addMouseMotionListener(this);
    
    move_normal_c = new Cursor(Cursor.MOVE_CURSOR);
    path_normal_c = Helper.createImageCursor("icons/path_cursor.gif", 4, 0, this);
    path_active_c = Helper.createImageCursor("icons/path_cursor_active.gif", 4, 0, this);
    path_add_c = Helper.createImageCursor("icons/path_cursor_add.gif", 4, 0, this);
    path_del_c = Helper.createImageCursor("icons/path_cursor_del.gif", 4, 0, this);
  }
  /**
   * Is invoked when the user deselects a layout.
   * All references to the old layout are cleared and the window is hidden.
   * Additionally the FrameUpdater is stopped.
   */
  public void clearLayout() {
    l=null;
    if(selected_item!=null) selected_item.setSelected(false);
    selected_item=null;
    frame.setVisible(false);
    if(fu!=null) fu.stopRunning();
    fu=null;
  }
  /**
   * Invoked when the user selects a Layout.
   * Shows the preview window and there draws the selected layout.
   * @param w_ The Window containing the Layout.
   * @param l_ The Layout that should be displayed.
   */
  public void setLayout(Window w_, Layout l_) {
    if(l_==null) {
      clearLayout();
      return;
    }
    l=l_;    
    setPreferredSize(new Dimension(l.width*z,l.height*z));
    int spane_w = l.width*z+scroll_pane.getBorder().getBorderInsets(scroll_pane).left+scroll_pane.getBorder().getBorderInsets(scroll_pane).right;
    int spane_h = l.height*z;//+scroll_pane.getBorder().getBorderInsets(scroll_pane).top+scroll_pane.getBorder().getBorderInsets(scroll_pane).bottom;
    scroll_pane.setPreferredSize(new Dimension(spane_w, spane_h));    
    frame.setTitle("Window: "+w_.id + " - Layout: " + l.id);    
    frame.pack();
    frame.setVisible(true);        
    if(fu==null) {
      fu = new FrameUpdater(this,5);
      fu.start();      
    }    
  }
  /**
   * Invoked when a user selects an Item. This tells the PreviewWindow which item should be moved when the user interacts with the window.
   * @param i The Item selected by the user.
   */
  public void selectItem(Item i) {    
    if(selected_item!=null) selected_item.setSelected(false);
    if(i==null) return;
    selected_item = i;
    selected_item.setSelected(true);
  }
  /**
   * Paints the selected layout into the window.
   * @param g The Graphics context into which is drawn.
   */
  @Override
  public void paint(Graphics g) {  
    if(l==null) return;   
    BufferedImage bi = (BufferedImage) createImage(getWidth(),getHeight());    
    Graphics2D g2d = bi.createGraphics();
    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0,0,getWidth(),getHeight());
    g2d.setColor(Color.LIGHT_GRAY);
    for(int x=0;x<getWidth();x=x+20) {
      for(int y=0;y<getHeight();y=y+20) {
        g2d.fillRect(x,y,10,10);
        g2d.fillRect(x+10,y+10,10,10);
      }
    }
    try {
      l.draw(g2d, z);
    }
    //In case any exception occurs while trying to draw the layout (e.g. missing bitmap/negative or zero width of items)
    catch(Exception e) {
      e.printStackTrace();
    }
    g.drawImage(bi, 0, 0, this);
  }
  @Override
  public void update(Graphics g) {
    //Needs to be overriden so that repainting is smooth
    //paint(g);
  }  
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(zoom_less)) {
      if(z>1) z--;
      zoom_label.setText(Language.get("ZOOM_FACTOR")+" "+z+"x");
      setSize(l.width*z, l.height*z);
      setPreferredSize(new Dimension(l.width*z, l.height*z));
    }
    else if(e.getSource().equals(zoom_more)) {
      if(z<16) z++;
      zoom_label.setText(Language.get("ZOOM_FACTOR")+" "+z+"x");
      setSize(l.width*z, l.height*z);
      setPreferredSize(new Dimension(l.width*z, l.height*z));
    }
  }
  public void mouseClicked(MouseEvent e) {
    if(mode==CURSOR_PATH) {
      if(selected_item.getClass().equals(Slider.class)) {
        Slider s = (Slider)selected_item;
        int mx = e.getX()/z-s.offsetx-s.x;
        int my = e.getY()/z-s.offsety-s.y;
        int over = -1;
        for(int i=0; i<s.getControlPointNum(); i++) {
          int dx = Math.abs(mx-s.getControlX(i));
          int dy = Math.abs(my-s.getControlY(i));
          if(dx<2 && dy<2) over = i;
        }
        if(over!=-1) {
          if(e.isAltDown()) {
            s.removeControlPoint(over);
          } else {
            setCursor(path_active_c);
          }
        } else {
          if(e.isShiftDown()) {
            s.addControlPoint(mx, my);
          } else {
            setCursor(path_normal_c);
          }
        }
      }
    }
  }
  public void mouseDragged(MouseEvent e) {
    if(selected_item==null) return;
    if(mode==CURSOR_MOVE) {
      if(!starteddragging && selected_item.contains(e.getX()/z,e.getY()/z)) {
        dragstartx=e.getX()/z;
        dragstarty=e.getY()/z;
        ime = new ItemMoveEvent(selected_item);
        dragstartitemx=selected_item.x;
        dragstartitemy=selected_item.y;
        starteddragging=true;
      }
      else if(starteddragging) {
        selected_item.x=dragstartitemx+e.getX()/z-dragstartx;
        selected_item.y=dragstartitemy+e.getY()/z-dragstarty;
      }
    } else {
      //TODO path editing mouseDragged
    }
  }  
  public void mouseEntered(MouseEvent e) {
    if(mode==CURSOR_PATH) {
      setCursor(path_normal_c);
    }
  }
  public void mouseExited(MouseEvent e) {
    if(mode==CURSOR_PATH) {
      setCursor(Cursor.getDefaultCursor());
    }
  }
  public void mouseMoved(MouseEvent e) {
    if(selected_item==null) return;
    if(mode==CURSOR_MOVE) {
      if(selected_item.contains(e.getX()/z,e.getY()/z)) {
        selected_item.setHover(true);
        setCursor(move_normal_c);
      }
      else {
        selected_item.setHover(false);
        setCursor(Cursor.getDefaultCursor());
      }
    } else {
      if(selected_item.getClass().equals(Slider.class)) {
        Slider s = (Slider)selected_item;
        int mx = e.getX()/z-s.offsetx-s.x;
        int my = e.getY()/z-s.offsety-s.y;
        int over = -1;
        for(int i=0; i<s.getControlPointNum(); i++) {
          int dx = Math.abs(mx-s.getControlX(i));
          int dy = Math.abs(my-s.getControlY(i));
          if(dx<2 && dy<2) over = i;
        }
        if(over!=-1) {
          if(e.isAltDown()) {
            setCursor(path_del_c);
          } else {
            setCursor(path_active_c);
          }
        } else {
          if(e.isShiftDown()) {
            setCursor(path_add_c);
          } else {
            setCursor(path_normal_c);
          }
        }
        //System.out.println(getCursor().getName());
      }
    }
  }
  public void mousePressed(MouseEvent e) {
    fu.fps=25;
    if(mode==CURSOR_MOVE) {
      if(selected_item!=null) selected_item.setClicked(true);
    } else {
      //TODO path editing mousePressed
    }
  }
  public void mouseReleased(MouseEvent e) {
    fu.fps=5;
    if(mode==CURSOR_MOVE) {
      if(starteddragging) {
        ime.setNew();
        m.hist.addEvent(ime);
        ime = null;
      }
      starteddragging=false;
      if(selected_item!=null) selected_item.setClicked(false);
      else return;
      dragstartx=selected_item.x+selected_item.offsetx;
      dragstarty=selected_item.y+selected_item.offsety;
      dragstartitemx=selected_item.x;
      dragstartitemy=selected_item.y;
    } else {
      //TODO path editing mouseReleased
    }
  }
  /**
   * Moves the currently selected item about the given distance.
   * @param x Movement along the X-Axis.
   * @param y Movement along the Y-Axis.
   */
  public void moveItem(int x, int y) {    
    try {
      if(ime==null) {
        ime = new ItemMoveEvent(selected_item);
        m.hist.addEvent(ime);
      }      
      selected_item.x+=x;
      selected_item.y+=y;
      if(ime.getNext()==null) ime.setNew();
      else ime = null;
      repaint();
    }
    catch (NullPointerException ex) {
      /* empty */
    }
  }
  /**
   * Saves a preview of the selected layout to a file
   */
  public void savePNG(File f) {
    BufferedImage bi = new BufferedImage(l.width, l.height, BufferedImage.TYPE_INT_ARGB);
    if(selected_item!=null) selected_item.setSelected(false);
    l.draw(bi.createGraphics(), 1);
    if(selected_item!=null) selected_item.setSelected(true);
    try {
      ImageIO.write(bi, "png", f);
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(m,Language.get("ERROR_SAVEPNG_MSG")+"\n"+e.toString(),Language.get("ERROR_SAVEPNG_TITLE"),JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  /**
   * Sets the cursor mode
   * @param c One of the cursor mode constants
   */
  public void setCursorMode(int c) {
    mode = c;
  }

  /**
   * Gets the current cursor mode
   * @return The cursor mode constants indicating the current cursor mode
   */
  public int getCursorMode() {
    return mode;
  }
}
