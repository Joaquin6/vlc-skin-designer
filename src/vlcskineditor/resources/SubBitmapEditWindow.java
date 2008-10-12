/*****************************************************************************
 * SubBitmapEditWindow.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of vlcskineditor.resources
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

import java.awt.*;
import java.awt.event.*;
import vlcskineditor.FrameUpdater;
import javax.swing.*;
import vlcskineditor.Language;

/**
 * Gives the user the possibilty to edit a SubBitmap visually
 * @author Daniel Dreibrodt
 */
public class SubBitmapEditWindow extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener, WindowListener{
  
  public JFrame frame;
  JScrollPane scroll_pane;
  JPanel zoom_panel;
  JButton zoom_more, zoom_less;
  JLabel zoom_label;
  int z_fact = 1;
  Bitmap b;
  SubBitmap sb;
  FrameUpdater fu;
  
  boolean starteddragging = false;
  int x1, y1, x2, y2;
  int x1_org, y1_org, x2_org, y2_org;
  int p_width, p_height;
  int drawcount;
  
  
  /** Creates a new instance of SubBitmapEditWindow */
  public SubBitmapEditWindow(Bitmap b_, SubBitmap sb_) {
    b = b_;
    sb = sb_;
    x1_org = x1 = sb.x;
    y1_org = y1 = sb.y;
    x2_org = x2 = sb.x+sb.width;
    y2_org = y2 = sb.y+sb.height;
    frame = new JFrame(Language.get("WIN_SBMP_EDIT_TITLE"));
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
    p_width = b.image.getWidth();
    p_height = b.image.getHeight();    
    setPreferredSize(new Dimension(p_width,p_height));  
    scroll_pane = new JScrollPane(this);    
    scroll_pane.setPreferredSize(new Dimension(p_width+20,p_height+20));
    frame.add(scroll_pane,BorderLayout.CENTER);     
    frame.pack();
    frame.setLocation(sb.frame.getX()+sb.frame.getWidth()+5,sb.frame.getY());
    frame.setMinimumSize(new Dimension(200,100));
    frame.setVisible(true);
    fu = new FrameUpdater(this,25);    
    fu.start();
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  @Override
  public void paint(Graphics g) {    
    if(b.image==null) return;
    if(drawcount>=30) drawcount = 0;
    g.clearRect(0,0,getWidth(),getHeight());
    g.drawImage(b.image, 0, 0, p_width*z_fact, p_height*z_fact, frame);    
    g.setColor(Color.RED);
    int[] x = new int[4];
    x[0] = x1*z_fact;
    x[1] = x2*z_fact;
    x[2] = x2*z_fact;
    x[3] = x1*z_fact;
    int[] y = new int[4];
    y[0] = y1*z_fact;
    y[1] = y1*z_fact;
    y[2] = y2*z_fact;
    y[3] = y2*z_fact;
    if(drawcount<=20 || starteddragging)
    g.drawPolygon(x,y,4);    
    drawcount++;
  }
  @Override
  public void update(Graphics g) {
    paint(g);
  }  
  public void mouseDragged(MouseEvent e) {    
    if(!starteddragging) {
      if(!contains(e.getPoint())) return;
      starteddragging = true;
      x1_org = x1;
      y1_org = y1;
      x2_org = x2;
      y2_org = y2;
      x2 = x1 = e.getX()/z_fact;
      y2 = y1 = e.getY()/z_fact;
    }    
    else {      
      if(e.getX()/z_fact>=p_width) x2=p_width;
      else if(e.getX()/z_fact<0) x2=0;
      else x2 = e.getX()/z_fact;
      if(e.getY()/z_fact>=p_height) y2=p_height;
      else if(e.getY()/z_fact<0) y2=0;
      else y2 = e.getY()/z_fact;
    }
  }
  public void mouseMoved(MouseEvent e) {
  }
  public void mouseClicked(MouseEvent e) {
  }
  public void mousePressed(MouseEvent e) {
  }
  public void mouseReleased(MouseEvent e) {
    if(starteddragging) {
      starteddragging = false;
      if(x2>x1) {
        sb.x_tf.setText(String.valueOf(x1));
        sb.width_tf.setText(String.valueOf(x2-x1));
      }
      else {
        sb.x_tf.setText(String.valueOf(x2));
        sb.width_tf.setText(String.valueOf(x1-x2));
      }
      if(y2>y1) {
        sb.y_tf.setText(String.valueOf(y1));
        sb.height_tf.setText(String.valueOf(y2-y1));
      }
      else {
        sb.y_tf.setText(String.valueOf(y2));
        sb.height_tf.setText(String.valueOf(y1-y2));
      }      
    }
  }
  public void mouseEntered(MouseEvent e) {
  }
  public void mouseExited(MouseEvent e) {
  }   
  public void updateDimensions() {
    if(!starteddragging) {
      try {
        x1 = Integer.parseInt(sb.x_tf.getText());
        x2 = x1 + Integer.parseInt(sb.width_tf.getText());
        y1 = Integer.parseInt(sb.y_tf.getText());
        y2 = y1 + Integer.parseInt(sb.height_tf.getText()); 
      }
      catch(NumberFormatException ex) {
        /*empty*/
      }
    }
  }
  public void keyTyped(KeyEvent e) {    
  }
  public void keyPressed(KeyEvent e) {
    updateDimensions();
  }
  public void keyReleased(KeyEvent e) {
    updateDimensions();
  }
  public void windowOpened(WindowEvent e) {    
  }
  public void windowClosing(WindowEvent e) {
    sb.destroyEditWindow();
  }
  public void windowClosed(WindowEvent e) {
  }
  public void windowIconified(WindowEvent e) {
  }
  public void windowDeiconified(WindowEvent e) {
  }
  public void windowActivated(WindowEvent e) {
  }
  public void windowDeactivated(WindowEvent e) {
  }
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(zoom_less)) {
      if(z_fact>1) z_fact--;
      zoom_label.setText(Language.get("ZOOM_FACTOR")+" "+z_fact+"x");
      setSize(p_width*z_fact, p_height*z_fact);
      setPreferredSize(new Dimension(p_width*z_fact, p_height*z_fact));
    }
    else if(e.getSource().equals(zoom_more)) {
      if(z_fact<16) z_fact++;
      zoom_label.setText(Language.get("ZOOM_FACTOR")+" "+z_fact+"x");
      setSize(p_width*z_fact, p_height*z_fact);
      setPreferredSize(new Dimension(p_width*z_fact, p_height*z_fact));
    }
  }
}
