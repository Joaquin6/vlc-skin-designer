/*****************************************************************************
 * Group.java
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

package vlcskineditor.items;

import vlcskineditor.*;
import vlcskineditor.history.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;

/**
 * Represents a Group item
 * @author Daniel Dreibrodt
 */
public class Group extends Item implements ActionListener{
  
  /** The items contained in the group */
  public java.util.List<Item> items = new LinkedList<Item>();  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf;
  JButton ok_btn, cancel_btn, help_btn;  
  
  {
    type = Language.get("GROUP");
  }

  /**
   * Parses a Group from a given XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public Group(Node n, Skin s_) {
    s = s_;
    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    XML.parseChildItems(n, items, s);
    created=true;
  }

  /**
   * Creates a new empty Group and opens a dialog to edit it
   * @param s_ The skin to which the group belongs
   */
  public Group(Skin s_) {
    s=s_;
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();    
    s.updateItems();        
  }

  /**
   * Creates a copy of a group
   * @param g The group to copy
   */
  public Group(Group g) {
    super(g);
    Helper.copyItems(g.items, items);
  }

  @Override
  public void update() {
    if(!created) {
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      for(Item i:items) {
        i.setOffset(x+offsetx,y+offsety);
      }
      s.updateItems();    
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      created = true;
      
      ItemAddEvent gaa = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(gaa);
    }
    else {
      GroupEditEvent gee = new GroupEditEvent(this);
      id=id_tf.getText();
      x=Integer.parseInt(x_tf.getText());
      y=Integer.parseInt(y_tf.getText());
      for(Item i:items) {
        i.setOffset(x+offsetx,y+offsety);
      }
      s.updateItems();    
      s.expandItem(id);
      
      gee.setNew();
      s.m.hist.addEvent(gee);
    }    
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_GROUP_TITLE"));
      frame.setIconImage(Main.edit_icon.getImage());
      frame.setResizable(false);
      if(!created) frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel(Language.get("WIN_ITEM_ID"));
      id_tf = new JTextField();      
      JLabel x_l = new JLabel(Language.get("WIN_ITEM_X"));
      x_tf = new JTextField();      
      x_tf.setDocument(new NumbersOnlyDocument());
      JLabel y_l = new JLabel(Language.get("WIN_ITEM_Y"));
      y_tf = new JTextField();      
      y_tf.setDocument(new NumbersOnlyDocument());
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));

      //Textfield distance to WEST border of container
      Component[] labels = { id_l, x_l, y_l };
      int tf_dx = Helper.maxWidth(labels)+10;
      //Maximal textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      general.add(x_l);
      general.add(x_tf);
      general.add(y_l);
      general.add(y_tf);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));

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

      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, y_l);

      
      frame.add(general);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);
      

      SpringLayout layout = new SpringLayout();
      frame.setLayout(layout);

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      //layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, attr_l, 5, SpringLayout.SOUTH, general);
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
          JOptionPane.showMessageDialog(frame,Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i", id_tf.getText()),Language.get("ERROR_ID_INVALID_TITLE"),JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      update();
      frame.setVisible(false);
      frame.dispose();
      frame = null;      
    }
    else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-group.html");
    }
    else if(e.getSource().equals(cancel_btn)) {
      if(!created) {
        java.util.List<Item> l = s.getParentListOf(id);
        if(l!=null) l.remove(this);
      }
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    }
  }
  @Override
  public String returnCode(String indent) {
    String code = indent+"<Group";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";    
    code+=">";    
    for (int i=0;i<items.size();i++) {
      code+="\n"+items.get(i).returnCode(indent+Skin.indentation);
    }
    code+="\n"+indent+"</Group>";
    return code;
  }
  @Override
  public void draw(Graphics2D g, int z) {     
     draw(g,offsetx,offsety,z);
  }
  @Override
  public void draw(Graphics2D g,int x_,int y_, int z) {    
    for(Item i:items) {
      i.setOffset(x+x_,y+y_);
      i.draw(g,x+x,y+y_,z);
    }    
  }
  @Override
  public boolean contains(int x_, int y_) {    
    return (x_>=x+offsetx && y_>=y+offsety);
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Group: "+id);      
    for(int i=0;i<items.size();i++) {
      node.add(items.get(i).getTreeNode());
    }
    return node;
  }
  @Override
  public Item getItem(String id_) {
    if(id.equals(id_)) return this;
    else {
      for(Item i:items) if(i.getItem(id_)!=null) return i.getItem(id_);          
    }
    return null;
  }
  @Override
  public java.util.List<Item> getParentListOf(String id_) {
    for(Item i:items) {      
      if(i.id.equals(id_)) {        
        return items;        
      }
      if (i.getClass().equals(Group.class)||i.getClass().equals(vlcskineditor.items.Panel.class)) {
        java.util.List<Item> p = i.getParentListOf(id_);
        if (p!=null) return p;
      }
    }
    return null;
  }
  @Override
  public Item getParentOf(String id_) {
    for(Item i:items) {      
      if(i.id.equals(id_)) {        
        return this;        
      }
      Item it = i.getParentOf(id_);
      if (it!=null) return it;      
    }
    return null;
  }
  @Override
  public boolean uses(String id_) {
    for(Item i:items) {
      if(i.uses(id_)) return true;
    }
    return false;
  }
  @Override  
  public void renameForCopy(String p) {    
    String p_ = p;
    super.renameForCopy(p);
    for(Item i:items) {
        i.renameForCopy(p_);
    }
  }
  @Override
  public void updateToGlobalVariables() {
    for(Item i:items) i.updateToGlobalVariables();
  }
}
