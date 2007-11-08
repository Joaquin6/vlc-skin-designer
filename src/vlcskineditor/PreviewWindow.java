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
import javax.swing.*;
import vlcskineditor.history.*;


/**
 * Handles the preview window and user interaction with it.
 * @author Daniel Dreibrodt
 */
public class PreviewWindow extends JPanel implements MouseListener, MouseMotionListener {
  
  /**
   * The JFrame in which a Layout of a Skin will be shown.
   */
  public JInternalFrame frame;
  Layout l;
  FrameUpdater fu;  
  Item selected_item;
  boolean starteddragging = false;
  int dragstartx, dragstarty, dragstartitemx, dragstartitemy;
  
  JMenuItem up, down, right, left;
  ItemMoveEvent ime = null;
  Main m;
  
  /**
   * Creates a new PreviewWindow that is initially hidden.
   */
  public PreviewWindow(Main m_) {   
    m = m_;
    frame = new JInternalFrame("No Layout selected");
    frame.add(this);
    frame.setVisible(false);
    frame.setResizable(false);        
    frame.setFrameIcon(createIcon("icons/preview.png"));
    addMouseListener(this);
    addMouseMotionListener(this);    
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
    //Somehow there are always added 4 pixels to the actual height of the layout
    setPreferredSize(new Dimension(l.width,l.height-4));
    setMinimumSize(new Dimension(l.width,l.height-4));
    setMaximumSize(new Dimension(l.width,l.height-4));
    setSize(l.width,l.height-4);
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
  public void paint(Graphics g) {  
    if(l==null) return;   
    BufferedImage bi = (BufferedImage) createImage(getWidth(),getHeight());    
    Graphics2D g2d = bi.createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0,0,getWidth(),getHeight());
    g2d.setColor(Color.LIGHT_GRAY);
    for(int x=0;x<getWidth();x=x+20) {
      for(int y=0;y<getHeight();y=y+20) {
        g2d.fillRect(x,y,10,10);
        g2d.fillRect(x+10,y+10,10,10);
      }
    }
    l.draw(g2d);
    g.drawImage(bi, 0, 0, this);
  }
  public void update(Graphics g) {
    paint(g);
  }  
  public void mouseClicked(MouseEvent e) {    
  }
  public void mouseDragged(MouseEvent e) {
    if(selected_item==null) return;
    if(!starteddragging && selected_item.contains(e.getX(),e.getY())) {
      dragstartx=e.getX();
      dragstarty=e.getY();
      ime = new ItemMoveEvent(selected_item);
      dragstartitemx=selected_item.x;
      dragstartitemy=selected_item.y;
      starteddragging=true;
    }
    else {
      selected_item.x=dragstartitemx+e.getX()-dragstartx;
      selected_item.y=dragstartitemy+e.getY()-dragstarty;
    }
  }  
  public void mouseEntered(MouseEvent e) {
    
  }
  public void mouseExited(MouseEvent e) {
    
  }
  public void mouseMoved(MouseEvent e) {
    if(selected_item==null) return;
    if(selected_item.contains(e.getX(),e.getY())) {
      selected_item.setHover(true);
      setCursor(new Cursor(Cursor.MOVE_CURSOR));
    }
    else {
      selected_item.setHover(false);
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }
  public void mousePressed(MouseEvent e) {
    fu.fps=25;
    if(selected_item!=null) selected_item.setClicked(true);
  }
  public void mouseReleased(MouseEvent e) {
    fu.fps=5;    
    if(starteddragging) {
      ime.setNew();
      m.hist.addEvent(ime);
    }
    starteddragging=false;
    if(selected_item!=null) selected_item.setClicked(false);
    else return;
    dragstartx=selected_item.x+selected_item.offsetx;
    dragstarty=selected_item.y+selected_item.offsety;
    dragstartitemx=selected_item.x;
    dragstartitemy=selected_item.y;
  }
  /**
   * Moves the currently selected item about the given distance.
   * @param x Movement along the X-Axis.
   * @param y Movement along the Y-Axis.
   */
  public void moveItem(int x, int y) {    
    try {
      selected_item.x+=x;
      selected_item.y+=y;
      repaint();
    }
    catch (NullPointerException ex) {
      /* empty */
    }
  }
  /**
   * Creates an ImageIcon from a file.
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
}
