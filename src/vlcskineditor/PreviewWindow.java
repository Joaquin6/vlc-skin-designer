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
import javax.swing.*;


/**
 * PreviewWindow
 * The WYSIWYG renderer
 * @author Daniel Dreibrodt
 */
public class PreviewWindow extends Canvas{
  
  public JInternalFrame frame;
  Layout l;
  FrameUpdater fu;
  BufferedImage bi;
  
  /** Creates a new instance of PreviewWindow */
  public PreviewWindow() {
    frame = new JInternalFrame("No Layout selected");
    frame.add(this);
    frame.setVisible(false);
    frame.setResizable(false);    
  }
  public void clearLayout() {
    l=null;
    frame.setVisible(false);
    fu=null;
  }
  public void setLayout(Window w_, Layout l_) {
    l=l_;
    frame.setSize(l.width,l.height);
    setSize(l.width,l.height);
    setPreferredSize(new Dimension(l.width,l.height));
    setMinimumSize(new Dimension(l.width,l.height));
    setMaximumSize(new Dimension(l.width,l.height));
    frame.setTitle(w_.id + "-" + l.id);
    frame.pack();
    frame.setVisible(true);    
    if(fu==null) {
      fu = new FrameUpdater(this,25);
      fu.start();
    }
  }
  public void prepare() {
    bi = (BufferedImage) createImage(getWidth(),getHeight());
    Graphics2D g = bi.createGraphics();
    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    l.draw(g);
  }
  public void paint(Graphics g_) {  
    if(l==null || bi==null) return;        
    g_.drawImage(bi, 0, 0, this);
  }
}
