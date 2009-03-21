/*****************************************************************************
 * ToolbarButton.java
 *****************************************************************************
 * Copyright (C) 2009 Daniel Dreibrodt
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * A toolbar image button, with hover effects
 * @author Daniel Dreibrodt
 */
public class ToolbarButton extends JButton implements MouseListener {
  
  ImageIcon normal, over, down;
  
  boolean hover = false;
  
  boolean mac = System.getProperty("os.name").indexOf("Mac")!=-1;
  
  public ToolbarButton(Image img) {
    try {      

      normal = new ImageIcon(img);
      
      BufferedImage icon = new BufferedImage(normal.getIconWidth(), normal.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
      icon.getGraphics().drawImage(normal.getImage(), 0, 0, this);

      if(!mac) {
        BufferedImage ovbi = new BufferedImage(icon.getWidth(this),icon.getHeight(this),BufferedImage.TYPE_INT_ARGB);
        for(int x=0;x<icon.getWidth(this);x++) {
          for(int y=0;y<icon.getHeight(this);y++) {
            Graphics g = ovbi.getGraphics();
            int argb = icon.getRGB(x,y);
            int red = (argb >> 16) & 0xff;
            int green = (argb >> 8) & 0xff;
            int blue = argb & 0xff;
            int alpha = (argb >> 24) & 0xff;
            g.setColor(brighter(new Color(red,green,blue,alpha),0.92));
            g.fillRect(x, y, 1, 1);
          }
        }
        over = new ImageIcon(ovbi);

        BufferedImage dobi = new BufferedImage(icon.getWidth(this),icon.getHeight(this),BufferedImage.TYPE_INT_ARGB);
        for(int x=0;x<icon.getWidth(this);x++) {
          for(int y=0;y<icon.getHeight(this);y++) {
            Graphics g = dobi.getGraphics();
            int argb = icon.getRGB(x,y);
            int red = (argb >> 16) & 0xff;
            int green = (argb >> 8) & 0xff;
            int blue = argb & 0xff;
            int alpha = (argb >> 24) & 0xff;
            g.setColor(darker(new Color(red,green,blue,alpha),0.92));
            g.fillRect(x, y, 1, 1);
          }
        }
        down = new ImageIcon(dobi);
      }

      setIcon(normal);
      setBorderPainted(false);
      setContentAreaFilled(false);
      setFocusPainted(false);
      addMouseListener(this);
      setPreferredSize(new Dimension(normal.getIconWidth()+8,normal.getIconHeight()+8));
    } catch(Exception ex) {
      ex.printStackTrace();
      setText("  ");
    }    
  }
  public void mouseClicked(MouseEvent e) {
    
  }
  public void mousePressed(MouseEvent e) {
    if(mac) return;

    setIcon(down);
    repaint();
  }
  public void mouseReleased(MouseEvent e) {
    if(mac) return;

    if(hover) setIcon(over);
    else setIcon(normal);
    repaint();
  }
  public void mouseEntered(MouseEvent e) {
    if(mac) return;

    hover = true;
    setIcon(over);
    repaint();
  }
  public void mouseExited(MouseEvent e) {
    if(mac) return;

    hover = false;
    setIcon(normal);
    repaint();
  }
  
  public Color brighter(Color c, double factor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int a = c.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
           return new Color(i, i, i, a);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/factor), 255),
                         Math.min((int)(g/factor), 255),
                         Math.min((int)(b/factor), 255),
                         a);
    }
  
    public Color darker(Color c, double factor) {
      return new Color(Math.max((int)(c.getRed()  *factor), 0),
                       Math.max((int)(c.getGreen()*factor), 0),
                       Math.max((int)(c.getBlue() *factor), 0),
                                      c.getAlpha());
    }
  
}
