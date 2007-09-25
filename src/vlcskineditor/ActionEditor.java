/*****************************************************************************
 * ActionEditor.java
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
import java.awt.event.*;
import java.util.*;

/**
 * ActionEditor
 * @author Daniel Dreibrodt
 */
public class ActionEditor extends JFrame implements ActionListener{
  
  String[] actions;
  java.util.List<ActionPanel> aPanels = new ArrayList<ActionPanel>();
  SpringLayout layout = new SpringLayout();
  
  JScrollPane sPane = new JScrollPane();
  JButton ok_btn = new JButton("OK");
  
  /** Creates a new instance of ActionEditor */
  public ActionEditor(String a) {
    super("Edit Action");
    setLayout(layout);
    add(sPane);
    add(ok_btn);
    ok_btn.addActionListener(this);
    actions = a.split(";");
    for(String act:actions) act=act.trim();
  }
  public String showEditDialog() {
    aPanels.clear();
    
    return actions[0];
  }
  public void actionPerformed(ActionEvent e) {
    
  }
}
