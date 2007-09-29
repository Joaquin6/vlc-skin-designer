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
  
  /** Creates a new instance of Window */
  final String ID_DEFAULT="none";  
  final String VISIBLE_DEFAULT = "true";
  final int X_DEFAULT = 0;
  final int Y_DEFAULT = 0;
  final boolean DRAGDROP_DEFAULT=true;
  final boolean PLAYONDROP_DEFAULT=true;
  
  String id=ID_DEFAULT;
  String visible = VISIBLE_DEFAULT;
  int x = X_DEFAULT;
  int y = Y_DEFAULT;
  boolean dragdrop = DRAGDROP_DEFAULT;
  boolean playondrop = PLAYONDROP_DEFAULT;
  
  java.util.List<Layout> layouts = new ArrayList<Layout>();
  
  Skin s;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, visible_tf, dragdrop_tf, playondrop_tf;
  JButton ok_btn, help_btn;
  
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
    for (int i=1;i<code.length;i++) {
      if(code[i].startsWith("<Layout")) {        
        layoutcode = code[i];
      }
      else if(code[i].startsWith("</Layout>")) {
        layoutcode+="\n"+code[i];
        layouts.add(new Layout(layoutcode,s));
        layoutcode = "";
      }
      else {
        layoutcode+="\n"+code[i];
      }
    }
  }
  public Window(Skin s_) {
    s=s_;
    id = "Unnamed window #"+s.getNewId();
    s.updateWindows();
    showOptions();
  }
  public void update(String id_, int x_, int y_, String v_, boolean dd_, boolean pod_) {
    id=id_;
    x=x_;
    visible=v_;
    dragdrop=dd_;
    playondrop=pod_;
    s.updateWindows();
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Window settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
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
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_l.setBounds(5,15,75,24);
      id_tf.setBounds(85,15,150,24);
      general.add(x_l);
      general.add(x_tf);
      x_l.setBounds(5,45,75,24);
      x_tf.setBounds(85,45,150,24);
      general.add(y_l);
      general.add(y_tf);
      y_l.setBounds(5,75,75,24);
      y_tf.setBounds(85,75,150,24);
      general.add(visible_l);
      general.add(visible_tf);
      visible_l.setBounds(5,105,75,24);
      visible_tf.setBounds(85,105,75,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));
      general.setMinimumSize(new Dimension(240,140));
      general.setPreferredSize(new Dimension(240,140));
      general.setMaximumSize(new Dimension(240,140));
      frame.add(general);
      
      JPanel options = new JPanel(null);
      options.add(dragdrop_l);
      options.add(dragdrop_tf);
      dragdrop_l.setBounds(5,15,75,24);
      dragdrop_tf.setBounds(85,15,150,24);
      options.add(playondrop_l);
      options.add(playondrop_tf);
      playondrop_l.setBounds(5,45,75,24);
      playondrop_tf.setBounds(85,45,150,24);
      options.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Drag and drop options"));
      options.setMinimumSize(new Dimension(240,85));
      options.setPreferredSize(new Dimension(240,85));
      options.setMaximumSize(new Dimension(240,85));
      frame.add(options);
      
      frame.add(ok_btn);
      frame.add(help_btn);
      frame.add(new JLabel("* required attribute"));
      
      frame.setMinimumSize(new Dimension(250,310));
      frame.setPreferredSize(new Dimension(250,310));
      frame.setMaximumSize(new Dimension(250,310));
      
      frame.pack();
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
      update(id_tf.getText(),Integer.parseInt(x_tf.getText()),Integer.parseInt(y_tf.getText()),visible_tf.getText(),Boolean.parseBoolean(dragdrop_tf.getText()),Boolean.parseBoolean(playondrop_tf.getText()));
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#Window"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
  }
  public void addLayout() {
    layouts.add(new Layout(s));
  }
  public String returnCode() {
    String code = "<Window";
    if(id!=ID_DEFAULT) code+=" id=\""+id+"\"";
    if(visible!=VISIBLE_DEFAULT) code+=" visible=\""+String.valueOf(visible)+"\"";
    if(x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if(y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if(dragdrop!=DRAGDROP_DEFAULT) code+=" dragdrop=\""+String.valueOf(dragdrop)+"\"";
    if(playondrop!=PLAYONDROP_DEFAULT) code+=" playondrop=\""+String.valueOf(playondrop)+"\"";
    code+=">";
    for (int i=0;i<layouts.size();i++) {
      code+="\n"+layouts.get(i).returnCode();
    }
    code+="\n</Window>\n";    
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
}
