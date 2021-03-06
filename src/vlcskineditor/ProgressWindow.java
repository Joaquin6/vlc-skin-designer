/*****************************************************************************
 * ProgressWindow.java
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

import javax.swing.*;
import java.awt.*;

/**
 * A window displaying a progress bar running back and forth.
 * @author Daniel Dreibrodt
 */
public class ProgressWindow extends JDialog{
  
  float position = 0;
  JProgressBar pbar;
  
  /** Creates a new instance of ProgressWindow
   * @param p_ parent frame
   * @param t_ window title
   */
  public ProgressWindow(JFrame p_, String t_) {
    super(p_,t_,false);
    setLayout(new BorderLayout());
    pbar = new JProgressBar();
    add(pbar,BorderLayout.CENTER);
    if(p_ != null && p_.isVisible()) setBounds(p_.getX()+p_.getWidth()/2-150,p_.getY()+p_.getHeight()/2-25,300,50);
    else setSize(300,50);
    setResizable(false);
    pbar.setString("...");
    pbar.setStringPainted(true);
    pbar.setIndeterminate(true);
  }  
  public void setProgress(double d) {
    pbar.setIndeterminate(false);
    pbar.setMaximum(100);
    pbar.setMinimum(0);
    pbar.setValue((int)(100*d));
    pbar.setString(pbar.getValue()+"%");
  }
  /** Sets the text displayed on the progress bar */
  public void setText(String s) {
    pbar.setString(s);
  }
}
