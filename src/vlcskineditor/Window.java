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

/**
 * Handles windows and their layouts
 * @author Daniel
 */
public class Window implements ActionListener{
  
  final String ID_DEFAULT="none";  
  final String VISIBLE_DEFAULT = "true";
  final int X_DEFAULT = 0;
  final int Y_DEFAULT = 0;
  final boolean DRAGDROP_DEFAULT=true;
  final boolean PLAYONDROP_DEFAULT=true;
  
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
  
  /** Creates a new instance of Window */
  public Window(String xmlcode, Skin s_) {
    s = s_;
    String[] code = xmlcode.split("\n");
    if(code[0].indexOf("id=\"")!=-1) id = XML.getValue(code[0],"id");
    else id = "Unnamed window #"+s.getNewId();
    if(code[0].indexOf("visible=\"")!=-1) visible = XML.getValue(code[0],"visible");
    if(code[0].indexOf("x=\"")!=-1) x = XML.getIntValue(code[0],"x");
    if(code[0].indexOf("y=\"")!=-1) y = XML.getIntValue(code[0],"y");
    if(code[0].indexOf("dragdrop=\"")!=-1) dragdrop = XML.getBoolValue(code[0],"dragdrop");
    if(code[0].indexOf("playondrop=\"")!=-1) playondrop = XML.getBoolValue(code[0],"playondrop");
    String layoutcode = "";
    for(int i=0;i<code.length;i++) code[i] = code[i].trim();
    for (int i=1;i<code.length;i++) {      
      if (code[i].startsWith("<!--")) {
        while(code[i].indexOf("-->")==-1) {
          i++;
        }
      }
      else if(code[i].startsWith("<Layout")) {        
        layoutcode = code[i];
      }
      else if(code[i].startsWith("</Layout>")) {
        layoutcode+="\n"+code[i];
        layouts.add(new Layout(layoutcode,this,s));
        layoutcode = "";
      }
      else {
        layoutcode+="\n"+code[i];
      }
    }
    created = true;
  }
  public Window(Skin s_) {
    s=s_;
    id = "Unnamed window #"+s.getNewId();
    s.updateWindows();
    showOptions();
  }
  public void update() {
    if(!created) {
      WindowAddEvent wae = new WindowAddEvent(s,this);
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      visible=visible_tf.getText();
      dragdrop=Boolean.parseBoolean(dragdrop_tf.getText());
      playondrop=Boolean.parseBoolean(playondrop_tf.getText());
      s.updateWindows();    
      created = true;
      frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);      
      s.m.hist.addEvent(wae);
    }
    else {
      WindowEditEvent wee = new WindowEditEvent(this);
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      visible=visible_tf.getText();
      dragdrop=Boolean.parseBoolean(dragdrop_tf.getText());
      playondrop=Boolean.parseBoolean(playondrop_tf.getText());
      s.updateWindows();    
      wee.setNew();
      s.m.hist.addEvent(wee);
    }    
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Window settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      if(!created) frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();
      id_tf.setToolTipText("Name of the window (it may be used for actions). Two windows cannot have the same id.");
      JLabel x_l = new JLabel("X:");
      x_tf = new JTextField();
      x_tf.setToolTipText("Initial left position of the window.");
      x_tf.setDocument(new NumbersOnlyDocument());
      JLabel y_l = new JLabel("Y:");
      y_tf = new JTextField();
      y_tf.setToolTipText("Initial top position of the window.");
      y_tf.setDocument(new NumbersOnlyDocument());
      JLabel visible_l = new JLabel("Visible:");
      visible_tf = new JTextField();
      visible_tf.setToolTipText("Indicates whether the window should appear when VLC is started. Since VLC remembers the skin windows position and visibility, this attribute will only be used the first time the skin is started.");
      JLabel dragdrop_l = new JLabel("Drag and drop:");
      dragdrop_tf = new JTextField();
      dragdrop_tf.setToolTipText("Indicates whether drag and drop of media files is allowed on this window.");
      JLabel playondrop_l = new JLabel("Play on drop:");
      playondrop_tf = new JTextField();
      playondrop_tf.setToolTipText("Indicates whether a dropped file is played directly (true) or only enqueued (false). This attribute has no effect if dragdrop is set to \"false\".");
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);      
      cancel_btn = new JButton("Cancel");
      cancel_btn.addActionListener(this);      
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);      
      JLabel attr_l = new JLabel("* Attributes marked with a star must be specified.");
      
      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, visible_l, dragdrop_l, playondrop_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = 200;
      
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
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));
      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);
      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);  
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.NORTH, x_l, 10, SpringLayout.SOUTH, id_l);
      general_layout.putConstraint(SpringLayout.WEST, x_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, x_tf, 0, SpringLayout.VERTICAL_CENTER, x_l);
      general_layout.putConstraint(SpringLayout.WEST, x_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, x_tf, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.NORTH, y_l, 10, SpringLayout.SOUTH, x_l);
      general_layout.putConstraint(SpringLayout.WEST, y_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, y_tf, 0, SpringLayout.VERTICAL_CENTER, y_l);
      general_layout.putConstraint(SpringLayout.WEST, y_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, y_tf, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.NORTH, visible_l, 10, SpringLayout.SOUTH, y_l);
      general_layout.putConstraint(SpringLayout.WEST, visible_l, 5, SpringLayout.WEST, general);
      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, visible_tf, 0, SpringLayout.VERTICAL_CENTER, visible_l);
      general_layout.putConstraint(SpringLayout.WEST, visible_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, visible_tf, 0, SpringLayout.EAST, id_tf);
      
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, visible_l);      
      frame.add(general);
      
      JPanel options = new JPanel(null);
      options.add(dragdrop_l);
      options.add(dragdrop_tf);
      dragdrop_tf.setPreferredSize(new Dimension(tf_wd,dragdrop_tf.getPreferredSize().height));
      options.add(playondrop_l);
      options.add(playondrop_tf);
      playondrop_tf.setPreferredSize(new Dimension(tf_wd,playondrop_tf.getPreferredSize().height));
      options.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Drag and drop options"));
      SpringLayout options_layout = new SpringLayout();
      options.setLayout(options_layout);
      options_layout.putConstraint(SpringLayout.NORTH, dragdrop_l, 5, SpringLayout.NORTH, options);
      options_layout.putConstraint(SpringLayout.WEST, dragdrop_l, 5, SpringLayout.WEST, options);
      
      options_layout.putConstraint(SpringLayout.VERTICAL_CENTER, dragdrop_tf, 0, SpringLayout.VERTICAL_CENTER, dragdrop_l);
      options_layout.putConstraint(SpringLayout.WEST, dragdrop_tf, tf_dx, SpringLayout.WEST, options);      
      options_layout.putConstraint(SpringLayout.EAST, options, 5, SpringLayout.EAST, dragdrop_tf);
      
      options_layout.putConstraint(SpringLayout.NORTH, playondrop_l, 10, SpringLayout.SOUTH, dragdrop_l);
      options_layout.putConstraint(SpringLayout.WEST, playondrop_l, 5, SpringLayout.WEST, options);
      
      options_layout.putConstraint(SpringLayout.VERTICAL_CENTER, playondrop_tf, 0, SpringLayout.VERTICAL_CENTER, playondrop_l);
      options_layout.putConstraint(SpringLayout.WEST, playondrop_tf, tf_dx, SpringLayout.NORTH, options);      
      options_layout.putConstraint(SpringLayout.EAST, playondrop_tf, 0, SpringLayout.EAST, dragdrop_tf);
      
      options_layout.putConstraint(SpringLayout.SOUTH, options, 10, SpringLayout.SOUTH, playondrop_l);
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
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame,"Please enter a valid ID!","ID not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame,"The ID \""+id_tf.getText()+"\" already exists, please choose another one.","ID not valid",JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      frame.setVisible(false);
      update();
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skinedhlp/window.html"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skinedhlp/window.html",JOptionPane.WARNING_MESSAGE);    
      }
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
