/*****************************************************************************
 * FrameUpdater.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of vlcskineditor
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

import javax.swing.JPanel;

/**
 * FrameUpdater
 * @author Daniel Dreibrodt
 */
public class FrameUpdater extends Thread{
  
  JPanel c;
  int fps = 1;
  /** causes the thread to stop when set to true */
  private boolean run = false;
  
  /** Creates a new instance of FrameUpdater
   * @param c_ The panel to redraw
   * @param fps_ Frames per second
   */
  public FrameUpdater(JPanel c_, int fps_) {
    setName("FrameUpdater");
    c=c_;
    fps=fps_;
  }
  /** Starts the repainting loop **/
  @Override
  public void run () {
    run = true;
    while(run) {
      try {        
        c.repaint();
        sleep(1000/fps);        
      }
      catch (Exception e) {
        e.printStackTrace();
      }           
    }
    return;
  }

  /**
   * Stops the FrameUpdater
   */
  public void stopRunning() {
    run = false;
  }
}
