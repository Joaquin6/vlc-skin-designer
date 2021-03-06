/*****************************************************************************
 * Video.java
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
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import vlcskineditor.history.ItemAddEvent;

/**
 * Video item
 * @author Daniel Dreibrodt
 */
public class Video extends Item implements ActionListener{
  
  public final int WIDTH_DEFAULT = 0;
  public final int HEIGHT_DEFAULT = 0;
  public final boolean AUTORESIZE_DEFAULT = true;
  
  public int width = WIDTH_DEFAULT;
  public int height = HEIGHT_DEFAULT;
  public boolean autoresize = AUTORESIZE_DEFAULT;
  
  JFrame frame = null;
  JTextField id_tf, x_tf, y_tf, help_tf, visible_tf, width_tf, height_tf;
  JComboBox lefttop_cb, rightbottom_cb, xkeepratio_cb, ykeepratio_cb, autoresize_cb;
  JButton visible_btn, ok_btn, cancel_btn, help_btn;
  
  {
    type = Language.get("VIDEO");
  }

  /**
   * Parses a video from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   */
  public Video(Node n, Skin s_) {
    s = s_;

    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId())));

    width = XML.getIntAttributeValue(n, "width", width);
    height = XML.getIntAttributeValue(n, "height", height);
    autoresize = XML.getBoolAttributeValue(n, "autoresize", autoresize);

    x = XML.getIntAttributeValue(n, "x", x);
    y = XML.getIntAttributeValue(n, "y", y);
    lefttop = XML.getStringAttributeValue(n, "lefttop", lefttop);
    rightbottom = XML.getStringAttributeValue(n, "rightbottom", rightbottom);
    xkeepratio = XML.getBoolAttributeValue(n, "xkeepratio", xkeepratio);
    ykeepratio = XML.getBoolAttributeValue(n, "ykeepratio", ykeepratio);
    visible = XML.getStringAttributeValue(n, "visible", visible);
    help = XML.getStringAttributeValue(n, "help", help);

    created = true;
  }

  /**
   * Creates a new empty video and shows its options dialog
   * @param s_ The parent skin
   */
  public Video(Skin s_) {
    s = s_;    
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();
  }

  /**
   * Creates a copy of a video
   * @param v The video to copy
   */
  public Video(Video v) {
    super(v);
    width = v.width;
    height = v.height;
    autoresize = v.autoresize;
  }

  @Override
  public void update() {
    if(!created) {
      id = id_tf.getText();
      x = Integer.parseInt(x_tf.getText());
      y = Integer.parseInt(y_tf.getText());
      lefttop = lefttop_cb.getSelectedItem().toString();
      rightbottom = rightbottom_cb.getSelectedItem().toString();
      xkeepratio = Boolean.parseBoolean(xkeepratio_cb.getSelectedItem().toString());
      ykeepratio = Boolean.parseBoolean(ykeepratio_cb.getSelectedItem().toString());
      visible = visible_tf.getText().replaceAll("\"","'");
      help = help_tf.getText().replaceAll("\"","'");

      width = Integer.parseInt(width_tf.getText());  
      height = Integer.parseInt(height_tf.getText());  
      autoresize = Boolean.parseBoolean(autoresize_cb.getSelectedItem().toString());

      s.updateItems();   
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      created = true;
      
      ItemAddEvent vae = new ItemAddEvent(s.getParentListOf(id), this);
      s.m.hist.addEvent(vae);
    }
    else {
      VideoEditEvent vee = new VideoEditEvent(this);
      
      id = id_tf.getText();
      x = Integer.parseInt(x_tf.getText());
      y = Integer.parseInt(y_tf.getText());
      lefttop = lefttop_cb.getSelectedItem().toString();
      rightbottom = rightbottom_cb.getSelectedItem().toString();
      xkeepratio = Boolean.parseBoolean(xkeepratio_cb.getSelectedItem().toString());
      ykeepratio = Boolean.parseBoolean(ykeepratio_cb.getSelectedItem().toString());
      visible = visible_tf.getText().replaceAll("\"","'");
      help = help_tf.getText().replaceAll("\"","'");

      width = Integer.parseInt(width_tf.getText());  
      height = Integer.parseInt(height_tf.getText());  
      autoresize = Boolean.parseBoolean(autoresize_cb.getSelectedItem().toString());

      s.updateItems();   
      s.expandItem(id);
      
      vee.setNew();
      s.m.hist.addEvent(vee);
    }
    updateToGlobalVariables();
  }
  @Override
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame(Language.get("WIN_VIDEO_TITLE"));
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
      lefttop_cb.setToolTipText(Language.get("WIN_ITEM_LEFTTOP_TITLE"));
      JLabel rightbottom_l = new JLabel(Language.get("WIN_ITEM_RIGHTBOTTOM"));
      rightbottom_cb = new JComboBox(align_values);
      rightbottom_cb.setToolTipText(Language.get("WIN_ITEM_RIGHTBOTTOM_TITLE"));
      Object[] bool_values = { true, false };
      JLabel xkeepratio_l = new JLabel(Language.get("WIN_ITEM_XKEEPRATIO"));
      xkeepratio_cb = new JComboBox(bool_values);
      xkeepratio_cb.setToolTipText(Language.get("WIN_ITEM_XKEEPRATIO_TIP"));
      JLabel ykeepratio_l = new JLabel(Language.get("WIN_ITEM_YKEEPRATIO"));
      ykeepratio_cb = new JComboBox(bool_values);
      ykeepratio_cb.setToolTipText(Language.get("WIN_ITEM_YKEEPRATIO_TIP"));
      JLabel visible_l = new JLabel(Language.get("WIN_ITEM_VISIBLE"));
      visible_tf = new JTextField();
      visible_btn = new JButton("",Main.help_icon);
      visible_btn.addActionListener(this);
      JLabel help_l = new JLabel(Language.get("WIN_ITEM_HELP"));
      help_tf = new JTextField();
      help_tf.setToolTipText(Language.get("WIN_ITEM_HELP_TIP"));
      
      JLabel width_l = new JLabel(Language.get("WIN_ITEM_WIDTH"));
      width_tf = new JTextField();
      width_tf.setDocument(new NumbersOnlyDocument());
      JLabel height_l = new JLabel(Language.get("WIN_ITEM_HEIGHT"));
      height_tf = new JTextField();
      height_tf.setDocument(new NumbersOnlyDocument());
      JLabel autoresize_l = new JLabel(Language.get("WIN_VIDEO_AUTORESIZE"));
      autoresize_cb = new JComboBox(bool_values);
      autoresize_cb.setToolTipText(Language.get("WIN_VIDEO_AUTORESIZE_TITLE"));

      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);

      //Distance of textfields to WEST edge of container
      Component[] labels = { id_l, x_l, y_l, lefttop_l, rightbottom_l, xkeepratio_l, ykeepratio_l, visible_l, help_l, width_l, height_l, autoresize_l};
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
      general.add(visible_l);
      general.add(visible_tf);
      general.add(visible_btn);
      general.add(help_l);
      general.add(help_tf);
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

      general_layout.putConstraint(SpringLayout.NORTH, visible_l, 10, SpringLayout.SOUTH, ykeepratio_cb);
      general_layout.putConstraint(SpringLayout.WEST, visible_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, visible_tf, 0, SpringLayout.VERTICAL_CENTER, visible_l);
      general_layout.putConstraint(SpringLayout.WEST, visible_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, visible_tf, -5, SpringLayout.WEST, visible_btn);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, visible_btn, 0, SpringLayout.VERTICAL_CENTER, visible_l);
      general_layout.putConstraint(SpringLayout.EAST, visible_btn, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.NORTH, help_l, 10, SpringLayout.SOUTH, visible_btn);
      general_layout.putConstraint(SpringLayout.WEST, help_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, help_tf, 0, SpringLayout.VERTICAL_CENTER, help_l);
      general_layout.putConstraint(SpringLayout.WEST, help_tf, tf_dx, SpringLayout.WEST, general);
      general_layout.putConstraint(SpringLayout.EAST, help_tf, 0, SpringLayout.EAST, id_tf);

      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, help_tf);

      frame.add(general);
      
      JPanel video_panel = new JPanel(null);
      video_panel.add(width_l);
      video_panel.add(width_tf);
      width_tf.setPreferredSize(new Dimension(tf_wd, width_tf.getPreferredSize().height));
      video_panel.add(height_l);
      video_panel.add(height_tf);
      video_panel.add(autoresize_l);
      video_panel.add(autoresize_cb);
      video_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_VIDEO_ATTR")));

      SpringLayout video_layout = new SpringLayout();
      video_panel.setLayout(video_layout);

      video_layout.putConstraint(SpringLayout.NORTH, width_l, 5, SpringLayout.NORTH, video_panel);
      video_layout.putConstraint(SpringLayout.WEST, width_l, 5, SpringLayout.WEST, video_panel);

      video_layout.putConstraint(SpringLayout.VERTICAL_CENTER, width_tf, 0, SpringLayout.VERTICAL_CENTER, width_l);
      video_layout.putConstraint(SpringLayout.WEST, width_tf, tf_dx, SpringLayout.WEST, video_panel);

      video_layout.putConstraint(SpringLayout.NORTH, height_l, 10, SpringLayout.SOUTH, width_tf);
      video_layout.putConstraint(SpringLayout.WEST, height_l, 5, SpringLayout.WEST, video_panel);

      video_layout.putConstraint(SpringLayout.VERTICAL_CENTER, height_tf, 0, SpringLayout.VERTICAL_CENTER, height_l);
      video_layout.putConstraint(SpringLayout.WEST, height_tf, tf_dx, SpringLayout.WEST, video_panel);
      video_layout.putConstraint(SpringLayout.EAST, height_tf, 0, SpringLayout.EAST, width_tf);

      video_layout.putConstraint(SpringLayout.NORTH, autoresize_l, 10, SpringLayout.SOUTH, height_tf);
      video_layout.putConstraint(SpringLayout.WEST, autoresize_l, 5, SpringLayout.WEST, video_panel);

      video_layout.putConstraint(SpringLayout.VERTICAL_CENTER, autoresize_cb, 0, SpringLayout.VERTICAL_CENTER, autoresize_l);
      video_layout.putConstraint(SpringLayout.WEST, autoresize_cb, tf_dx, SpringLayout.WEST, video_panel);
      video_layout.putConstraint(SpringLayout.EAST, autoresize_cb, 0, SpringLayout.EAST, width_tf);

      video_layout.putConstraint(SpringLayout.EAST, video_panel, 5, SpringLayout.EAST, width_tf);
      video_layout.putConstraint(SpringLayout.SOUTH, video_panel, 10, SpringLayout.SOUTH, autoresize_cb);

      frame.add(video_panel);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(attr_l);
      
      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, video_panel, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.WEST, video_panel, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, video_panel);
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
    autoresize_cb.setSelectedItem(autoresize);
    
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
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-video.html");
    }
    else if(e.getSource().equals(visible_btn)) {
     Helper.browse("http://www.videolan.org/vlc/skinedhlp/boolexpr.html");
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
    String code = indent+"<Video";
    if (!id.equals(ID_DEFAULT)) code+=" id=\""+id+"\"";
    if (x!=X_DEFAULT) code+=" x=\""+String.valueOf(x)+"\"";
    if (y!=Y_DEFAULT) code+=" y=\""+String.valueOf(y)+"\"";
    if (width!=WIDTH_DEFAULT) code+=" width=\""+String.valueOf(width)+"\"";
    if (height!=HEIGHT_DEFAULT) code+=" height=\""+String.valueOf(height)+"\"";
    if (autoresize!=AUTORESIZE_DEFAULT) code+=" autoresize=\""+String.valueOf(autoresize)+"\"";
    if (!lefttop.equals(LEFTTOP_DEFAULT)) code+=" lefttop=\""+lefttop+"\"";
    if (!rightbottom.equals(RIGHTBOTTOM_DEFAULT)) code+=" rightbottom=\""+rightbottom+"\"";
    if (xkeepratio!=XKEEPRATIO_DEFAULT) code+=" xkeepratio=\""+String.valueOf(xkeepratio)+"\"";
    if (ykeepratio!=YKEEPRATIO_DEFAULT) code+=" ykeepratio=\""+String.valueOf(ykeepratio)+"\"";
    if (!help.equals(HELP_DEFAULT)) code+=" help=\""+help+"\"";
    if (!visible.equals(VISIBLE_DEFAULT)) code+=" visible=\""+visible+"\"";
    code+="/>";
    return code;
  }
  @Override
  public void draw(Graphics2D g, int z) {
    draw(g,offsetx,offsety, z);
  }
  @Override
  public void draw(Graphics2D g, int x_, int y_, int z) {
    if(!created) return;
    
    g.setColor(Color.BLACK);
    if(vis) g.fillRect((x+x_)*z,(y+y_)*z,width*z,height*z);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect((x+x_)*z,(y+y_)*z,width*z-1,height*z-1);
    }
  }
  @Override
  public boolean contains(int x_, int y_) {
    return (x_>=x+offsetx && x_<=x+width+offsetx && y_>=y+offsety && y_<=y+height+offsety);
  }
  @Override
  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Video: "+id);      
    return node;
  }
  
}
