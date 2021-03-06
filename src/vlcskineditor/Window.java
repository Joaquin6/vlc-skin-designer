/*****************************************************************************
 * Window.java
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

import vlcskineditor.history.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles windows and their layouts
 * @author Daniel
 */
public class Window implements ActionListener{
  
  final String ID_DEFAULT = "none";  
  final String VISIBLE_DEFAULT = "true";
  final int X_DEFAULT = 0;
  final int Y_DEFAULT = 0;
  final boolean DRAGDROP_DEFAULT = true;
  final boolean PLAYONDROP_DEFAULT = true;
  
  public String id=ID_DEFAULT;
  public String visible = VISIBLE_DEFAULT;
  public int x = X_DEFAULT;
  public int y = Y_DEFAULT;
  public boolean dragdrop = DRAGDROP_DEFAULT;
  public boolean playondrop = PLAYONDROP_DEFAULT;
  
  public java.util.List<Layout> layouts = new LinkedList<Layout>();
  
  public Skin s;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, visible_tf, dragdrop_tf, playondrop_tf;
  JButton ok_btn, cancel_btn, help_btn;
  boolean created = false;
  
  public String type = Language.get("WINDOW");
  
  /**
   * Parses a window's structure from a given XML node
   * @param n The window node
   * @param s_ The parent skin manager
   */
  public Window(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    visible = XML.getStringAttributeValue(n, "visible", visible);
    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    dragdrop = XML.getBoolAttributeValue(n, "dragdrop", dragdrop);
    playondrop = XML.getBoolAttributeValue(n, "playondrop", playondrop);
    NodeList nodes = n.getChildNodes();
    for(int i=0;i<nodes.getLength();i++) {     
      if(nodes.item(i).getNodeName().equals("Layout"))
        layouts.add(new Layout(nodes.item(i), this, s));
    }
    created = true;
  }  

  /**
   * Creates a new empty window and opens a dialog to edit it
   * @param s_ The skin to which the window belongs
   */
  public Window(Skin s_) {
    s=s_;
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    s.updateWindows();
    showOptions();
  }

  /**
   * Creates a copy of a Window
   * @param w The window to copy
   */
  public Window(Window w) {
    s = w.s;
    id = w.id;
    visible = w.visible;
    x = w.x;
    y = w.y;
    dragdrop = w.dragdrop;
    playondrop = w.playondrop;
    for(Layout l:w.layouts)
      layouts.add(new Layout(l));
    created = true;
  }
  
  public void update() {
    if(!created) {
      WindowAddEvent wae = new WindowAddEvent(s,this);
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      visible=visible_tf.getText().replaceAll("\"","'");
      dragdrop=Boolean.parseBoolean(dragdrop_tf.getText());
      playondrop=Boolean.parseBoolean(playondrop_tf.getText());
      s.updateWindows();    
      created = true;
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      
      s.m.hist.addEvent(wae);
    }
    else {
      WindowEditEvent wee = new WindowEditEvent(this);
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      visible=visible_tf.getText().replaceAll("\"","'");
      dragdrop=Boolean.parseBoolean(dragdrop_tf.getText());
      playondrop=Boolean.parseBoolean(playondrop_tf.getText());
      s.updateWindows();    
      wee.setNew();
      s.m.hist.addEvent(wee);
    }    
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_WINDOW_TITLE"));
      frame.setIconImage(Main.edit_icon.getImage());
      frame.setResizable(false);
      if(!created) frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel(Language.get("WIN_ITEM_ID"));
      id_tf = new JTextField();
      id_tf.setToolTipText(Language.get("WIN_ITEM_ID_TIP").replaceAll("%t",type));
      JLabel x_l = new JLabel(Language.get("WIN_ITEM_X"));
      x_tf = new JTextField();
      x_tf.setToolTipText(Language.get("WIN_WINDOW_X_TIP"));
      x_tf.setDocument(new NumbersOnlyDocument());
      JLabel y_l = new JLabel(Language.get("WIN_ITEM_Y"));
      y_tf = new JTextField();
      y_tf.setToolTipText(Language.get("WIN_WINDOW_Y_TIP"));
      y_tf.setDocument(new NumbersOnlyDocument());
      JLabel visible_l = new JLabel(Language.get("WIN_ITEM_VISIBLE"));
      visible_tf = new JTextField();
      visible_tf.setToolTipText(Language.get("WIN_WINDOW_VISIBLE_TIP"));
      JLabel dragdrop_l = new JLabel(Language.get("WIN_WINDOW_DD"));
      dragdrop_tf = new JTextField();
      dragdrop_tf.setToolTipText(Language.get("WIN_WINDOW_DD_TIP"));
      JLabel playondrop_l = new JLabel(Language.get("WIN_WINDOW_PD"));
      playondrop_tf = new JTextField();
      playondrop_tf.setToolTipText(Language.get("WIN_WINDOW_PD_TIP"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);      
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);      
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);      
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      
      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, visible_l, dragdrop_l, playondrop_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      general.add(x_l);
      general.add(x_tf);
      x_tf.setPreferredSize(new Dimension(tf_wd,x_tf.getPreferredSize().height));
      general.add(y_l);
      general.add(y_tf);
      y_tf.setPreferredSize(new Dimension(tf_wd,y_tf.getPreferredSize().height));
      general.add(visible_l);
      general.add(visible_tf);
      visible_tf.setPreferredSize(new Dimension(tf_wd,visible_tf.getPreferredSize().height));
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));
      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);
      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);  
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.NORTH, x_l, 10, SpringLayout.SOUTH, id_tf);
      general_layout.putConstraint(SpringLayout.WEST, x_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, x_tf, 0, SpringLayout.VERTICAL_CENTER, x_l);
      general_layout.putConstraint(SpringLayout.WEST, x_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, x_tf, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.NORTH, y_l, 10, SpringLayout.SOUTH, x_tf);
      general_layout.putConstraint(SpringLayout.WEST, y_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, y_tf, 0, SpringLayout.VERTICAL_CENTER, y_l);
      general_layout.putConstraint(SpringLayout.WEST, y_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, y_tf, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.NORTH, visible_l, 10, SpringLayout.SOUTH, y_tf);
      general_layout.putConstraint(SpringLayout.WEST, visible_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, visible_tf, 0, SpringLayout.VERTICAL_CENTER, visible_l);
      general_layout.putConstraint(SpringLayout.WEST, visible_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, visible_tf, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, visible_tf);      
      frame.add(general);
      
      JPanel options = new JPanel(null);
      options.add(dragdrop_l);
      options.add(dragdrop_tf);
      dragdrop_tf.setPreferredSize(new Dimension(tf_wd,dragdrop_tf.getPreferredSize().height));
      options.add(playondrop_l);
      options.add(playondrop_tf);
      playondrop_tf.setPreferredSize(new Dimension(tf_wd,playondrop_tf.getPreferredSize().height));
      options.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_WINDOW_DD_PANEL")));
      SpringLayout options_layout = new SpringLayout();
      options.setLayout(options_layout);
      options_layout.putConstraint(SpringLayout.NORTH, dragdrop_l, 5, SpringLayout.NORTH, options);
      options_layout.putConstraint(SpringLayout.WEST, dragdrop_l, 5, SpringLayout.WEST, options);
      
      options_layout.putConstraint(SpringLayout.VERTICAL_CENTER, dragdrop_tf, 0, SpringLayout.VERTICAL_CENTER, dragdrop_l);
      options_layout.putConstraint(SpringLayout.WEST, dragdrop_tf, tf_dx, SpringLayout.WEST, options);      
      options_layout.putConstraint(SpringLayout.EAST, options, 5, SpringLayout.EAST, dragdrop_tf);
      
      options_layout.putConstraint(SpringLayout.NORTH, playondrop_l, 10, SpringLayout.SOUTH, dragdrop_tf);
      options_layout.putConstraint(SpringLayout.WEST, playondrop_l, 5, SpringLayout.WEST, options);
      
      options_layout.putConstraint(SpringLayout.VERTICAL_CENTER, playondrop_tf, 0, SpringLayout.VERTICAL_CENTER, playondrop_l);
      options_layout.putConstraint(SpringLayout.WEST, playondrop_tf, tf_dx, SpringLayout.NORTH, options);      
      options_layout.putConstraint(SpringLayout.EAST, playondrop_tf, 0, SpringLayout.EAST, dragdrop_tf);
      
      options_layout.putConstraint(SpringLayout.SOUTH, options, 10, SpringLayout.SOUTH, playondrop_tf);
      frame.add(options);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(attr_l);
      
      SpringLayout layout = new SpringLayout();
      frame.setLayout(layout);
      
      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, options, 5, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, options, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, attr_l, 5, SpringLayout.SOUTH, options);
      layout.putConstraint(SpringLayout.WEST, attr_l, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.EAST, attr_l, 5, SpringLayout.EAST, frame.getContentPane());
      
      
      layout.putConstraint(SpringLayout.NORTH, ok_btn, 5, SpringLayout.SOUTH, attr_l);
      layout.putConstraint(SpringLayout.NORTH, cancel_btn, 5, SpringLayout.SOUTH, attr_l);
      layout.putConstraint(SpringLayout.NORTH, help_btn, 5, SpringLayout.SOUTH, attr_l);
      
      layout.putConstraint(SpringLayout.WEST, ok_btn, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, cancel_btn, 5, SpringLayout.EAST, ok_btn);
      layout.putConstraint(SpringLayout.WEST, help_btn, 5, SpringLayout.EAST, cancel_btn);
      
      layout.putConstraint(SpringLayout.SOUTH, frame.getContentPane(), 5, SpringLayout.SOUTH, ok_btn);
      layout.putConstraint(SpringLayout.EAST, frame.getContentPane(), 5, SpringLayout.EAST, general);
      
      frame.pack();
      
      frame.getRootPane().setDefaultButton(ok_btn);
    }
    id_tf.setText(id);
    x_tf.setText(String.valueOf(x));
    y_tf.setText(String.valueOf(y));
    visible_tf.setText(visible);
    dragdrop_tf.setText(String.valueOf(dragdrop));
    playondrop_tf.setText(String.valueOf(playondrop));
    frame.setVisible(true);
    
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")||id.contains("\"")) {
        JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_INVALID_MSG"),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i",id_tf.getText()),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      frame.setVisible(false);
      update();
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/window.html");
    }
    else if(e.getSource().equals(cancel_btn)) {
      frame.setVisible(false);
      if(!created) s.windows.remove(this);
    }
  }
  public void addLayout() {
    layouts.add(new Layout(this,s));
  }
  public String returnCode(String indent) {
    String code = indent+"<Window";
    if(!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if(!visible.equals(VISIBLE_DEFAULT)) code+=" visible=\""+String.valueOf(visible)+"\"";
    if(x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if(y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if(dragdrop!=DRAGDROP_DEFAULT) code+=" dragdrop=\""+String.valueOf(dragdrop)+"\"";
    if(playondrop!=PLAYONDROP_DEFAULT) code+=" playondrop=\""+String.valueOf(playondrop)+"\"";
    code+=">";
    for (int i=0;i<layouts.size();i++) {
      code+="\n"+layouts.get(i).returnCode(indent+Skin.indentation);
    }
    code+="\n"+indent+"</Window>\n";    
    return code;
  }
  public Layout getLayout(String id_) {
    Layout l = null;
    for(int i=0;i<layouts.size();i++) {
      if(layouts.get(i).id.equals(id_)) {
        l=layouts.get(i);
      }
    }
    return l;
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Window: "+id);    
    for(int i=0;i<layouts.size();i++) {
      node.add(layouts.get(i).getTreeNode());
    }
    return node;
  }
  /** Renames the Layout and all its content after the copy process **/
  public void renameForCopy(String p) { 
    String p_ = p;
    p = p.replaceAll("%oldid%", id);     
    String newid_base = p;     
    String newid = newid_base;
    int i = 1;
    while(s.idExists(newid)) {
      i++;
      newid = newid_base+"_"+String.valueOf(i);
    }
    id = newid;
    for(Layout l:layouts) {
        l.renameForCopy(p_);
    }
  }
}
