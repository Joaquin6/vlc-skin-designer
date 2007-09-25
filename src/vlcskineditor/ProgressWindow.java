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
 * ProgressWindow
 * @author Daniel Dreibrodt
 */
public class ProgressWindow extends JDialog{
  
    float position = 0;
  String title = "...";
  JProgressBar pbar;
  
  /** Creates a new instance of ProgressWindow */
  public ProgressWindow(JFrame p_, String t_) {
    super(p_,"",false);
    setLayout(new BorderLayout());
    pbar = new JProgressBar();
    add(pbar,BorderLayout.CENTER);
    setBounds(p_.getX()+p_.getWidth()/2,p_.getY()+p_.getHeight()/2,300,50);
    setResizable(false);    
    pbar.setString(t_);
    pbar.setStringPainted(true);
    pbar.setIndeterminate(true);
  }  
  public void setText(String s) {
    pbar.setString(s);
  }
}
