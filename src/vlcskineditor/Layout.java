/*****************************************************************************
 * Layout.java
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

import vlcskineditor.items.*;
import vlcskineditor.history.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;

/**
 * Handles a windows' layout and it's content
 * @author Daniel
 */
public class Layout implements ActionListener{
  
  public final String ID_DEFAULT = "none";
  public final int MINWIDTH_DEFAULT = -1;
  public final int MINHEIGHT_DEFAULT = -1;
  public final int MAXWIDTH_DEFAULT = -1;
  public final int MAXHEIGHT_DEFAULT = -1;
  public String id = ID_DEFAULT;
  public int minwidth = MINWIDTH_DEFAULT;
  public int minheight = MINHEIGHT_DEFAULT;
  public int maxwidth = MAXWIDTH_DEFAULT;
  public int maxheight = MAXHEIGHT_DEFAULT;
  public int width, height;
  Skin s;
  Window parent;
  
  JFrame frame = null;
  JTextField id_tf, width_tf, height_tf, minwidth_tf, minheight_tf, maxwidth_tf, maxheight_tf;
  JButton ok_btn, cancel_btn, help_btn;
  
  java.util.List<Item> items = new LinkedList<Item>();
  
  String type = "List";
  
  boolean created = false;
  
  /**
   * Creates a new Layout from XML.
   * @param xmlcode The XML code from which the Layout should be created.
   * @param s_ The Skin in which the Layout is used.
   */
  public Layout(String xmlcode, Window w_, Skin s_) {
    s=s_;
    parent=w_;
    String[] code = xmlcode.split("\n");
    width = XML.getIntValue(code[0],"width");
    height = XML.getIntValue(code[0],"height");
    if(code[0].indexOf("id=\"")!=-1) id = XML.getValue(code[0],"id");
    else id = "Unnamed layout #"+s.getNewId();
    if(code[0].indexOf("minwidth=\"")!=-1) minwidth = XML.getIntValue(code[0],"minwidth");
    if(code[0].indexOf("maxwidth=\"")!=-1) maxwidth = XML.getIntValue(code[0],"maxwidth");
    if(code[0].indexOf("minheight=\"")!=-1) minheight = XML.getIntValue(code[0],"minheight");
    if(code[0].indexOf("maxheight=\"")!=-1) maxheight = XML.getIntValue(code[0],"maxheight");
    
    for(int i=0;i<code.length;i++) code[i] = code[i].trim();    
    for(int i=1;i<code.length;i++) {      
      if (code[i].startsWith("<!--")) {
        while(code[i].indexOf("-->")==-1) {
          i++;
        }
      }
      else if(code[i].startsWith("<Anchor")) items.add(new Anchor(code[i],s));
      else if(code[i].startsWith("<Button")) items.add(new vlcskineditor.items.Button(code[i],s));
      else if(code[i].startsWith("<Checkbox")) items.add(new vlcskineditor.items.Checkbox(code[i],s));
      else if(code[i].startsWith("<Image")) items.add(new vlcskineditor.items.Image(code[i],s));
      else if(code[i].startsWith("<Text")) items.add(new Text(code[i],s));
      else if(code[i].startsWith("<Video")) items.add(new Video(code[i],s));
      else if(code[i].startsWith("<RadialSlider")) items.add(new RadialSlider(code[i],s));
      else if(code[i].startsWith("<Group")) {
        String itemcode = code[i];
        i++;
        while(!code[i].startsWith("</Group>")) {          
          itemcode += "\n"+code[i];
          i++;
        }
        itemcode += "\n"+code[i];
        items.add(new Group(itemcode,s));        
      }
      else if(code[i].startsWith("<Panel")) {
        String itemcode = code[i];
        i++;
        while(!code[i].startsWith("</Panel>")) {          
          itemcode += "\n"+code[i];
          i++;
        }
        itemcode += "\n"+code[i];
        items.add(new vlcskineditor.items.Panel(itemcode,s));        
      }
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
    created = true;
  }
  /**
   * Creates a new Layout from user input.
   * @param s_ The Skin in which the Layout is used.
   */
  public Layout(Window w_, Skin s_) {
    s=s_;
    parent=w_;
    id = "Unnamed layout #"+s.getNewId();
    width=0;
    height=0;
    showOptions();
  }
  /**
   * Updates the Layout's attributes according to the user input.
   */
  public void update() {
    if(!created) {
      LayoutAddEvent lae = new LayoutAddEvent(parent,this);
      id=id_tf.getText();
      width=Integer.parseInt(width_tf.getText());
      height=Integer.parseInt(height_tf.getText());
      minwidth=Integer.parseInt(minwidth_tf.getText());
      minheight=Integer.parseInt(minheight_tf.getText());
      maxwidth=Integer.parseInt(maxwidth_tf.getText());
      maxheight=Integer.parseInt(maxheight_tf.getText());
      s.updateWindows();
      s.expandLayout(id);
      created = true;
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      s.m.hist.addEvent(lae);
    }
    else {
      LayoutEditEvent lee = new LayoutEditEvent(this);
      id=id_tf.getText();
      width=Integer.parseInt(width_tf.getText());
      height=Integer.parseInt(height_tf.getText());
      minwidth=Integer.parseInt(minwidth_tf.getText());
      minheight=Integer.parseInt(minheight_tf.getText());
      maxwidth=Integer.parseInt(maxwidth_tf.getText());
      maxheight=Integer.parseInt(maxheight_tf.getText());
      s.updateWindows();
      s.expandLayout(id);      
      lee.setNew();
      s.m.hist.addEvent(lee);
    }
  }
  /**
   * Shows a dialog to edit this Layout's attributes.
   */
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Layout settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
      if(!created) frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      JLabel id_l = new JLabel("ID*:");
      id_tf = new JTextField();
      id_tf.setToolTipText("Name of the layout (it may be used for actions). Two layouts cannot have the same id.");
      JLabel width_l = new JLabel("Initial width*:");
      width_tf = new JTextField();
      width_tf.setDocument(new NumbersOnlyDocument());
      width_tf.setToolTipText("Initial width of the layout. This value is required since VLC is not (yet?) able to calculate it using the sizes and positions of the controls.");
      JLabel height_l = new JLabel("Initial height*:");
      height_tf = new JTextField();
      height_tf.setDocument(new NumbersOnlyDocument());
      height_tf.setToolTipText("Initial height of the layout. This value is required since VLC is not (yet?) able to calculate it using the sizes and positions of the controls.");
      JLabel minwidth_l = new JLabel("Min. width:");
      minwidth_tf = new JTextField();
      minwidth_tf.setDocument(new NumbersOnlyDocument());
      minwidth_tf.setToolTipText("Minimum width of the layout. This value is only used when resizing the layout. If this value is set to \"-1\", the initial width (as specified by the width attribute) will be used as minimum width.");
      JLabel minheight_l = new JLabel("Min. height:");
      minheight_tf = new JTextField();
      minheight_tf.setDocument(new NumbersOnlyDocument());
      minheight_tf.setToolTipText("Minimum height of the layout. This value is only used when resizing the layout. If this value is set to \"-1\", the initial width (as specified by the width attribute) will be used as minimum width.");
      JLabel maxwidth_l = new JLabel("Max. width:");
      maxwidth_tf = new JTextField();
      maxwidth_tf.setDocument(new NumbersOnlyDocument());
      maxwidth_tf.setToolTipText("Maximum width of the layout. This value is only used when resizing the layout. If this value is set to \"-1\", the initial width (as specified by the width attribute) will be used as maximum width.");
      JLabel maxheight_l = new JLabel("Max. height:");
      maxheight_tf = new JTextField();
      maxheight_tf.setDocument(new NumbersOnlyDocument());
      maxheight_tf.setToolTipText("Maximum height of the layout. This value is only used when resizing the layout. If this value is set to \"-1\", the initial width (as specified by the width attribute) will be used as maximum width.");
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);
      cancel_btn = new JButton("Cancel");
      cancel_btn.addActionListener(this);
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      JLabel attr_l = new JLabel("* Attributes marked with a star must be specified.");
      
      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, width_l, height_l, minwidth_l, minheight_l, maxwidth_l, maxheight_l};
      int tf_dx = Helper.maxWidth(labels)+10;
      //Max. textfield width
      int tf_wd = 200;
      
      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);      
      id_tf.setPreferredSize(new Dimension(tf_wd,id_tf.getPreferredSize().height));
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "General Attributes"));
      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);
      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);      
      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, id_l);
      frame.add(general);
      
      JPanel dim = new JPanel(null);
      dim.add(width_l);
      dim.add(width_tf);     
      width_tf.setPreferredSize(new Dimension(tf_wd,width_tf.getPreferredSize().height));
      dim.add(height_l);
      dim.add(height_tf);
      height_tf.setPreferredSize(new Dimension(tf_wd,height_tf.getPreferredSize().height));
      dim.add(minwidth_l);      
      dim.add(minwidth_tf);
      minwidth_tf.setPreferredSize(new Dimension(tf_wd,minwidth_tf.getPreferredSize().height));
      dim.add(minheight_l);
      dim.add(minheight_tf);
      minheight_tf.setPreferredSize(new Dimension(tf_wd,minheight_tf.getPreferredSize().height));
      dim.add(maxwidth_l);
      dim.add(maxwidth_tf);
      maxwidth_tf.setPreferredSize(new Dimension(tf_wd,maxwidth_tf.getPreferredSize().height));
      dim.add(maxheight_l);
      dim.add(maxheight_tf);
      maxheight_tf.setPreferredSize(new Dimension(tf_wd,maxheight_tf.getPreferredSize().height));
      dim.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Dimensions"));
      SpringLayout dim_layout = new SpringLayout();
      dim.setLayout(dim_layout);
      dim_layout.putConstraint(SpringLayout.NORTH, width_l, 5, SpringLayout.NORTH, dim);
      dim_layout.putConstraint(SpringLayout.WEST, width_l, 5, SpringLayout.WEST, dim);
      
      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, width_tf, 0, SpringLayout.VERTICAL_CENTER, width_l);
      dim_layout.putConstraint(SpringLayout.WEST, width_tf, tf_dx, SpringLayout.WEST, dim);
      dim_layout.putConstraint(SpringLayout.EAST, dim, 5, SpringLayout.EAST, width_tf);
      
      dim_layout.putConstraint(SpringLayout.NORTH, height_l, 10, SpringLayout.SOUTH, width_l);
      dim_layout.putConstraint(SpringLayout.WEST, height_l, 5, SpringLayout.WEST, dim);
      
      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, height_tf, 0, SpringLayout.VERTICAL_CENTER, height_l);
      dim_layout.putConstraint(SpringLayout.WEST, height_tf, tf_dx, SpringLayout.WEST, dim);
      dim_layout.putConstraint(SpringLayout.EAST, height_tf, 0, SpringLayout.EAST, width_tf);
      
      dim_layout.putConstraint(SpringLayout.NORTH, minwidth_l, 10, SpringLayout.SOUTH, height_l);
      dim_layout.putConstraint(SpringLayout.WEST, minwidth_l, 5, SpringLayout.WEST, dim);
      
      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, minwidth_tf, 0, SpringLayout.VERTICAL_CENTER, minwidth_l);
      dim_layout.putConstraint(SpringLayout.WEST, minwidth_tf, tf_dx, SpringLayout.WEST, dim);
      dim_layout.putConstraint(SpringLayout.EAST, minwidth_tf, 0, SpringLayout.EAST, width_tf);
      
      dim_layout.putConstraint(SpringLayout.NORTH, minheight_l, 10, SpringLayout.SOUTH, minwidth_l);
      dim_layout.putConstraint(SpringLayout.WEST, minheight_l, 5, SpringLayout.WEST, dim);
      
      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, minheight_tf, 0, SpringLayout.VERTICAL_CENTER, minheight_l);
      dim_layout.putConstraint(SpringLayout.WEST, minheight_tf, tf_dx, SpringLayout.WEST, dim);
      dim_layout.putConstraint(SpringLayout.EAST, minheight_tf, 0, SpringLayout.EAST, width_tf);
      
      dim_layout.putConstraint(SpringLayout.NORTH, maxwidth_l, 10, SpringLayout.SOUTH, minheight_l);
      dim_layout.putConstraint(SpringLayout.WEST, maxwidth_l, 5, SpringLayout.WEST, dim);
      
      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, maxwidth_tf, 0, SpringLayout.VERTICAL_CENTER, maxwidth_l);
      dim_layout.putConstraint(SpringLayout.WEST, maxwidth_tf, tf_dx, SpringLayout.WEST, dim);
      dim_layout.putConstraint(SpringLayout.EAST, maxwidth_tf, 0, SpringLayout.EAST, width_tf);
      
      dim_layout.putConstraint(SpringLayout.NORTH, maxheight_l, 10, SpringLayout.SOUTH, maxwidth_l);
      dim_layout.putConstraint(SpringLayout.WEST, maxheight_l, 5, SpringLayout.WEST, dim);
      
      dim_layout.putConstraint(SpringLayout.VERTICAL_CENTER, maxheight_tf, 0, SpringLayout.VERTICAL_CENTER, maxheight_l);
      dim_layout.putConstraint(SpringLayout.WEST, maxheight_tf, tf_dx, SpringLayout.WEST, dim);
      dim_layout.putConstraint(SpringLayout.EAST, maxheight_tf, 0, SpringLayout.EAST, width_tf);
      
      dim_layout.putConstraint(SpringLayout.SOUTH, dim, 10, SpringLayout.SOUTH, maxheight_l);
      
      frame.add(dim);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(attr_l);      
      
      SpringLayout layout = new SpringLayout();
      frame.setLayout(layout);
      
      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, dim, 5, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, dim, 5, SpringLayout.WEST, frame.getContentPane());
      
      layout.putConstraint(SpringLayout.NORTH, attr_l, 5, SpringLayout.SOUTH, dim);
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
    width_tf.setText(String.valueOf(width));
    height_tf.setText(String.valueOf(height));
    minwidth_tf.setText(String.valueOf(minwidth));
    minheight_tf.setText(String.valueOf(minheight));
    maxwidth_tf.setText(String.valueOf(maxwidth));
    maxheight_tf.setText(String.valueOf(maxheight));
    frame.setVisible(true);
  }
  /**
   * Handles the user interaction with the editing dialog.
   */
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
      if(Integer.parseInt(width_tf.getText())<=0) {
        JOptionPane.showMessageDialog(frame,"Please enter a valid width!","Width not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if(Integer.parseInt(height_tf.getText())<=0) {
        JOptionPane.showMessageDialog(frame,"Please enter a valid height!","Height not valid",JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      frame.setVisible(false);
      update();
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skinedhlp/layout.html"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skinedhlp/layout.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
    else if(e.getSource().equals(cancel_btn)) {
      frame.setVisible(false);
      if(!created) {
        for (Window w:s.windows) {
          if(w.getLayout(id)!=null) w.layouts.remove(this);
        }
      }
    }
  }
  /**
   * Draws the Layout.
   * @param g The Graphics2D context onto which the Layout will be drawn.
   * @param z The zoom factor
   */
  public void draw(Graphics2D g, int z) {
    for(Item i:items) {
      i.draw(g, z);
    }
  }
  /**
   * Generates the XML code represented by this Layout.
   * @param ident Indentation
   * @return The XML code.
   */
  public String returnCode(String indent) {
    String code =indent+"<Layout";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    code+=" width=\""+String.valueOf(width)+"\" height=\""+String.valueOf(height)+"\"";
    if (minwidth!=MINWIDTH_DEFAULT) code+=" minwidth=\""+String.valueOf(minwidth)+"\"";
    if (maxwidth!=MAXWIDTH_DEFAULT) code+=" maxwidth=\""+String.valueOf(maxwidth)+"\"";
    if (minheight!=MINHEIGHT_DEFAULT) code+=" minheight=\""+String.valueOf(minheight)+"\"";
    if (maxheight!=MAXHEIGHT_DEFAULT) code+=" maxheight=\""+String.valueOf(maxheight)+"\"";
    code+=">";
    for (int i=0;i<items.size();i++) {
      code+="\n"+items.get(i).returnCode(indent+Skin.indentation);
    }
    code+="\n"+indent+"</Layout>";
    return code;
  }
  /**
   * Creates a TreeNode visualizing this Layout in the windows and layouts tree.
   * @return The TreeNode to display this Layout in a JTree.
   */
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Layout: "+id);    
    return node;
  }  
  /**
   * Gets the Item of the given id if it is contained in the Layout.
   * @param id_ The ID of the desired Item.
   * @return If the Item was found the Item object is returned. Otherwise <pre>null</pre> is returned.
   */
  public Item getItem(String id_) {    
    for(int x=0;x<items.size();x++) {
      Item i = items.get(x).getItem(id_);
      if (i!=null) return i;      
    }
    return null;
  }
  /**
   * Gets the List in which the Item specified by the given ID is stored.
   * @param id_ The ID of the desired Item.
   * @return If the Item was found in the Layout the List containing the Item is returned. Otherwise <pre>null</pre> is returned.
   * @see java.util.List
   */
  public java.util.List<Item> getParentListOf(String id_) {
    for(int x=0;x<items.size();x++) {
      Item i = items.get(x);
      if(i.id.equals(id_)) {        
        return items;        
      }
      if (i.type.equals("Group")||i.type.equals("Panel")) {
        java.util.List<Item> p = i.getParentListOf(id_);
        if (p!=null) return p;
      }
    }
    return null;
  }
  /**
   * Gets the parent Item of the Item specified by the given ID.
   * @param id_ The ID of the Item of which one wants the parent.
   * @return If the Item was found in the Layout its parent is returned. Otherwise <pre>null</pre> is returned.
   */
  public Item getParentOf(String id_) {
    for(int x=0;x<items.size();x++) {
      Item i = items.get(x);
      if(i.id.equals(id_)) {        
        return null;        
      }
      Item it = i.getParentOf(id_);
      if (it!=null) return it;      
    }
    return null;
  }
  /**
   * Checks whether an item in this layout uses the resource of the given ID
   * @param id_ The ID of the resource
   */
  public boolean uses(String id_) {
    for(Item i:items) {
      if(i.uses(id_)) return true;
    }
    return false;
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
    for(Item it:items) {
        it.renameForCopy(p_);
    }
  }
}
