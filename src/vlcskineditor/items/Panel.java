/*****************************************************************************
 * Panel.java
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;

/**
 * Represents a Panel item
 * @author Daniel Dreibrodt
 */
public class Panel extends Item implements ActionListener{
  
  /** The items contained in the panel */
  public java.util.List<Item> items = new LinkedList<Item>();
  
  public int width, height;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, width_tf, height_tf, help_tf, visible_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb;
  JButton ok_btn, cancel_btn, help_btn;
  
  {
    type = Language.get("PANEL");
  }

  public Panel(Node n, Skin s_) {
    s = s_;

    width = XML.getIntAttributeValue(n, "width", width);
    height = XML.getIntAttributeValue(n, "height", height);

    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));
    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);

    XML.parseChildItems(n, items, s);

    created = true;
  }

  /** Creates a new instance of Panel */
  public Panel(String xmlcode, Skin s_) {
    s = s_;
    String[] code = xmlcode.split("\n");
    width = Integer.parseInt(XML.getValue(code[0],"width"));
    width = Integer.parseInt(XML.getValue(code[0],"height"));
    if(code[0].indexOf("x=\"")!=-1) x = XML.getIntValue(code[0],"x");
    if(code[0].indexOf("y=\"")!=-1) y = XML.getIntValue(code[0],"y");
    if(code[0].indexOf("id=\"")!=-1) id = XML.getValue(code[0],"id");
    else id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    if(code[0].indexOf("lefttop=\"")!=-1) lefttop = XML.getValue(code[0],"lefttop");
    if(code[0].indexOf("rightbottom=\"")!=-1) rightbottom = XML.getValue(code[0],"rightbottom");
    if(code[0].indexOf("xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(code[0],"xkeepratio");
    if(code[0].indexOf("ykeepratio=\"")!=-1) ykeepratio = XML.getBoolValue(code[0],"ykeepratio");
    
    for(int i=0;i<code.length;i++) code[i] = code[i].trim();
    for(int i=1;i<code.length;i++) {      
      if (code[i].startsWith("<!--")) {
        while(code[i].indexOf("-->")==-1) {
          i++;
        }
      }
      if(code[i].startsWith("<Anchor")) items.add(new Anchor(code[i],s));
      else if(code[i].startsWith("<Button")) items.add(new Button(code[i],s));
      else if(code[i].startsWith("<Checkbox")) items.add(new Checkbox(code[i],s));
      else if(code[i].startsWith("<Image")) items.add(new Image(code[i],s));
      else if(code[i].startsWith("<Text")) items.add(new Text(code[i],s));
      else if(code[i].startsWith("<Video")) items.add(new Video(code[i],s));
      else if(code[i].startsWith("<RadialSlider")) items.add(new RadialSlider(code[i],s));
      else if(code[i].startsWith("<Playlist")) {
        String itemcode = code[i];
        i++;
        while(!code[i].startsWith("</Playlist>")) {          
          itemcode += "\n"+code[i];
          i++;
        }
        itemcode += "\n"+code[i];
        items.add(new Playtree(itemcode,s));        
      }
      else if(code[i].startsWith("<Playtree")) {
        String itemcode = code[i];
        i++;
        while(!code[i].startsWith("</Playtree>")) {          
          itemcode += "\n"+code[i];
          i++;
        }
        itemcode += "\n"+code[i];
        items.add(new Playtree(itemcode,s));        
      }
      else if(code[i].startsWith("<Slider")) {
        if(code[i].indexOf("/>")!=-1) {
          items.add(new Slider(code[i],s));
        }
        else {
          String itemcode = code[i];
          i++;
          while(!code[i].startsWith("</Slider>")) {          
            itemcode += "\n"+code[i];
            i++;
          }
          itemcode += "\n"+code[i];
          items.add(new Slider(itemcode,s));
        }                
      }
    }
    for(Item i:items) {
      i.setOffset(x+offsetx,y+offsety);
    }
    created = true;
  }
  public Panel(Skin s_) {
    s=s_;
    width = 0;
    height = 0;
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();
    s.updateItems();    
  }
  public void update() {
    if(!created) {     
      id = id_tf.getText();
      x = Integer.parseInt(x_tf.getText());
      y = Integer.parseInt(y_tf.getText());
      lefttop = lefttop_cb.getSelectedItem().toString();
      rightbottom = rightbottom_cb.getSelectedItem().toString();
      xkeepratio = Boolean.parseBoolean(xkeepratio_cb.getSelectedItem().toString());
      ykeepratio = Boolean.parseBoolean(ykeepratio_cb.getSelectedItem().toString());
      visible = visible_tf.getText();
      help = help_tf.getText();

      width = Integer.parseInt(width_tf.getText());
      height = Integer.parseInt(height_tf.getText());

      s.updateItems();    
      s.expandItem(id);
      for(Item i:items) {
        i.setOffset(x+offsetx,y+offsety);
      }
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      created = true;
      
      ItemAddEvent paa = new ItemAddEvent(s.getParentListOf(id),this);
      s.m.hist.addEvent(paa);
    }
    else {
      PanelEditEvent pee = new PanelEditEvent(this);
      
      id = id_tf.getText();
      x = Integer.parseInt(x_tf.getText());
      y = Integer.parseInt(y_tf.getText());
      lefttop = lefttop_cb.getSelectedItem().toString();
      rightbottom = rightbottom_cb.getSelectedItem().toString();
      xkeepratio = Boolean.parseBoolean(xkeepratio_cb.getSelectedItem().toString());
      ykeepratio = Boolean.parseBoolean(ykeepratio_cb.getSelectedItem().toString());
      visible = visible_tf.getText();
      help = help_tf.getText();

      width = Integer.parseInt(width_tf.getText());
      height = Integer.parseInt(height_tf.getText());

      s.updateItems();    
      s.expandItem(id);
      for(Item i:items) {
        i.setOffset(x+offsetx,y+offsety);
      }
      
      pee.setNew();
      s.m.hist.addEvent(pee);
    }
  }
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_PANEL_TITLE"));
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
      String[] align_values = {"lefttop", "leftbottom", "righttop", "rightbottom"};
      JLabel lefttop_l = new JLabel(Language.get("WIN_ITEM_LEFTTOP"));
      lefttop_cb = new JComboBox(align_values);
      lefttop_cb.setToolTipText(Language.get("WIN_ITEM_LEFTTOP_TIP"));
      JLabel rightbottom_l = new JLabel(Language.get("WIN_ITEM_RIGHTBOTTOM"));
      rightbottom_cb = new JComboBox(align_values);
      rightbottom_cb.setToolTipText(Language.get("WIN_ITEM_RIGHTBOTTOM_TIP"));
      Object[] bool_values = { true, false };
      JLabel xkeepratio_l = new JLabel(Language.get("WIN_ITEM_XKEEPRATIO"));
      xkeepratio_cb = new JComboBox(bool_values);
      xkeepratio_cb.setToolTipText(Language.get("WIN_ITEM_XKEEPRATIO_TIP"));
      JLabel ykeepratio_l = new JLabel(Language.get("WIN_ITEM_YKEEPRATIO"));
      ykeepratio_cb = new JComboBox(bool_values);
      ykeepratio_cb.setToolTipText(Language.get("WIN_ITEM_YKEEPRATIO_TIP"));
      JLabel visible_l = new JLabel(Language.get("WIN_ITEM_VISIBLE"));
      visible_tf = new JTextField();
      JLabel help_l = new JLabel(Language.get("WIN_ITEM_HELP"));
      help_tf = new JTextField();
      help_tf.setToolTipText(Language.get("WIN_ITEM_HELP_TIP"));
      JLabel width_l = new JLabel(Language.get("WIN_PANEL_WIDTH"));
      width_tf = new JTextField();
      width_tf.setDocument(new NumbersOnlyDocument());
      JLabel height_l = new JLabel(Language.get("WIN_PANEL_HEIGHT"));
      height_tf = new JTextField();
      height_tf.setDocument(new NumbersOnlyDocument());
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);

      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, lefttop_l, rightbottom_l, xkeepratio_l, ykeepratio_l, visible_l, help_l, width_l, height_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      general.add(x_l);
      general.add(x_tf);
      general.add(y_l);
      general.add(y_tf);
      general.add(lefttop_l);
      general.add(lefttop_cb);
      general.add(rightbottom_l);
      general.add(rightbottom_cb);
      general.add(xkeepratio_l);
      general.add(xkeepratio_cb);
      general.add(ykeepratio_l);
      general.add(ykeepratio_cb);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));

      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);

      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);

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

      general_layout.putConstraint(SpringLayout.NORTH, lefttop_l, 10, SpringLayout.SOUTH, y_tf);
      general_layout.putConstraint(SpringLayout.WEST, lefttop_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, lefttop_cb, 0, SpringLayout.VERTICAL_CENTER, lefttop_l);
      general_layout.putConstraint(SpringLayout.WEST, lefttop_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, lefttop_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, rightbottom_l, 10, SpringLayout.SOUTH, lefttop_cb);
      general_layout.putConstraint(SpringLayout.WEST, rightbottom_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, rightbottom_cb, 0, SpringLayout.VERTICAL_CENTER, rightbottom_l);
      general_layout.putConstraint(SpringLayout.WEST, rightbottom_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, rightbottom_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, xkeepratio_l, 10, SpringLayout.SOUTH, rightbottom_cb);
      general_layout.putConstraint(SpringLayout.WEST, xkeepratio_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, xkeepratio_cb, 0, SpringLayout.VERTICAL_CENTER, xkeepratio_l);
      general_layout.putConstraint(SpringLayout.WEST, xkeepratio_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, xkeepratio_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, ykeepratio_l, 10, SpringLayout.SOUTH, xkeepratio_cb);
      general_layout.putConstraint(SpringLayout.WEST, ykeepratio_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, ykeepratio_cb, 0, SpringLayout.VERTICAL_CENTER, ykeepratio_l);
      general_layout.putConstraint(SpringLayout.WEST, ykeepratio_cb, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, ykeepratio_cb, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, ykeepratio_cb);

      frame.add(general);
      
      JPanel dim = new JPanel(null);
      dim.add(width_l);
      dim.add(width_tf);
      width_tf.setPreferredSize(new Dimension(tf_wd,width_tf.getPreferredSize().height));
      dim.add(height_l);
      dim.add(height_tf);
      dim.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_PANEL_DIMENSIONS")));

      SpringLayout dim_layout = new SpringLayout();

      dim_layout.putConstraint(SpringLayout.NORTH, width_l, 5, SpringLayout.NORTH, dim);
      dim_layout.putConstraint(SpringLayout.WEST, width_l, 5, SpringLayout.WEST, dim);

      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, width_tf, 0, SpringLayout.VERTICAL_CENTER, width_l);
      dim_layout.putConstraint(SpringLayout.WEST, width_tf, tf_dx, SpringLayout.WEST, dim);

      dim_layout.putConstraint(SpringLayout.NORTH, height_l, 10, SpringLayout.SOUTH, width_tf);
      dim_layout.putConstraint(SpringLayout.WEST, height_l, 5, SpringLayout.WEST, dim);

      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, height_tf, 0, SpringLayout.VERTICAL_CENTER, height_l);
      dim_layout.putConstraint(SpringLayout.WEST, height_tf, tf_dx, SpringLayout.WEST, dim);
      dim_layout.putConstraint(SpringLayout.EAST, height_tf, 0, SpringLayout.EAST, width_tf);

      dim_layout.putConstraint(SpringLayout.EAST, dim, 5, SpringLayout.EAST, width_tf);
      dim_layout.putConstraint(SpringLayout.SOUTH, dim, 10, SpringLayout.SOUTH, height_tf);

      dim.setLayout(dim_layout);

      frame.add(dim);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);

      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, dim, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, dim, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, dim);
      layout.putConstraint(SpringLayout.WEST, attr_l, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, ok_btn, 10, SpringLayout.SOUTH, attr_l);
      layout.putConstraint(SpringLayout.WEST, ok_btn, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, cancel_btn, 0, SpringLayout.NORTH, ok_btn);
      layout.putConstraint(SpringLayout.WEST, cancel_btn, 5, SpringLayout.EAST, ok_btn);

      layout.putConstraint(SpringLayout.NORTH, help_btn, 0, SpringLayout.NORTH, cancel_btn);
      layout.putConstraint(SpringLayout.WEST, help_btn, 5, SpringLayout.EAST, cancel_btn);

      layout.putConstraint(SpringLayout.SOUTH, frame.getContentPane(), 10, SpringLayout.SOUTH, ok_btn);
      layout.putConstraint(SpringLayout.EAST, frame.getContentPane(), 5, SpringLayout.EAST, general);

      frame.setLayout(layout);
      
      frame.pack();
      
      frame.getRootPane().setDefaultButton(ok_btn);
    }
    id_tf.setText(id);    
    x_tf.setText(String.valueOf(x));
    y_tf.setText(String.valueOf(y));
    lefttop_cb.setSelectedItem(lefttop);
    rightbottom_cb.setSelectedItem(rightbottom);
    xkeepratio_cb.setSelectedItem(xkeepratio);
    ykeepratio_cb.setSelectedItem(ykeepratio);
    visible_tf.setText(visible);
    help_tf.setText(help);
    
    width_tf.setText(String.valueOf(width));
    height_tf.setText(String.valueOf(height));
    
    frame.setVisible(true);
  }  
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")) {
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
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-panel.html");
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
  public String returnCode(String indent) {
    String code = indent+"<Panel";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    code+=" width=\""+String.valueOf(width)+"\" height=\""+String.valueOf(height)+"\"";
    if (!lefttop.equals(LEFTTOP_DEFAULT)) code+=" lefttop=\""+lefttop+"\"";
    if (!rightbottom.equals(RIGHTBOTTOM_DEFAULT)) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    code+=">";
    for (int i=0;i<items.size();i++) {
      code+="\n"+items.get(i).returnCode(indent+Skin.indentation);
    }
    code+="\n"+indent+"</Panel>";
    return code;
  }
  public void draw(Graphics2D g, int z) {     
     draw(g, offsetx, offsety, z);
  }
  public void draw(Graphics2D g, int x_, int y_, int z) {    
    for(Item i:items) {
      i.setOffset(x+x_,y+y_);
      i.draw(g, x+x_, y+y_, z);
    }
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect(x+x_,y+y_,width-1,height-1);
    }
  }
  @Override
  public boolean contains(int x_, int y_) {    
    return (x_>=x+offsetx && x_<=x+width+offsetx && y_>=y+offsety && y_<=y+height+offsety);
  }
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Panel: "+id);       
    for(int i=0;i<items.size();i++) {
      node.add(items.get(i).getTreeNode());
    }    
    return node;
  }
  @Override
  public Item getItem(String id_) {
    if(id.equals(id_)) return this;
    else {
      for(Item i:items) {        
        if (i.getItem(id_)!=null) return i.getItem(id_);      
      }    
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
