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

/**
 * Gives the user the possibilty to edit a SubBitmap visually
 * @author Daniel Dreibrodt
 */
public class SubBitmapEditWindow extends JPanel implements MouseListener, MouseMotionListener{
  
  public JFrame frame;
  Bitmap b;
  SubBitmap sb;
  FrameUpdater fu;
  
  boolean starteddragging = false;
  int x1, y1, x2, y2;
  int x1_org, y1_org, x2_org, y2_org;
  int maxx, maxy;
  
  
  /** Creates a new instance of SubBitmapEditWindow */
  public SubBitmapEditWindow(Bitmap b_, SubBitmap sb_) {
    b = b_;
    sb = sb_;
    x1_org = x1 = sb.x;
    y1_org = y1 = sb.y;
    x2_org = x2 = sb.x+sb.width;
    y2_org = y2 = sb.y+sb.height;
    frame = new JFrame("Edit SubBitmap");
    frame.setLayout(new GridLayout(1,1));
    frame.add(this);
    maxx = b.image.getWidth();
    maxy = b.image.getHeight();
    setMinimumSize(new Dimension(maxx,maxy));
    setPreferredSize(new Dimension(maxx,maxy));    
    frame.pack();
    frame.setLocation(sb.frame.getX()+sb.frame.getWidth()+5,sb.frame.getY());
    frame.setResizable(false);
    frame.setVisible(true);
    fu = new FrameUpdater(this,25);    
    fu.start();
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  public void paint(Graphics g) {
    if(b.image==null) return;
    g.clearRect(0,0,getWidth(),getHeight());
    g.drawImage(b.image,0,0,frame);
    g.setColor(Color.RED);
    int[] x = new int[4];
    x[0] = x1;
    x[1] = x2;
    x[2] = x2;
    x[3] = x1;
    int[] y = new int[4];
    y[0] = y1;
    y[1] = y1;
    y[2] = y2;
    y[3] = y2;
    g.drawPolygon(x,y,4);
  }
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
      x2 = x1 = e.getX();
      y2 = y1 = e.getY();
    }    
    else {      
      if(e.getX()>=maxx) x2=maxx;
      else if(e.getX()<0) x2=0;
      else x2 = e.getX();
      if(e.getY()>=maxy) y2=maxy;
      else if(e.getY()<0) y2=0;
      else y2 = e.getY();
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
}
