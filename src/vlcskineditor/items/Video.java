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
  
  /** Creates a new instance of Video */
  public Video(String xmlcode, Skin s_) {
    s = s_;
    if(xmlcode.indexOf(" width=\"")!=-1) width = XML.getIntValue(xmlcode,"width");
    if(xmlcode.indexOf(" height=\"")!=-1) height = XML.getIntValue(xmlcode,"height");
    if(xmlcode.indexOf(" autoresize=\"")!=-1) autoresize = XML.getBoolValue(xmlcode,"autoresize");
    if(xmlcode.indexOf(" x=\"")!=-1) x = XML.getIntValue(xmlcode,"x");
    if(xmlcode.indexOf(" y=\"")!=-1) y = XML.getIntValue(xmlcode,"y");
    if(xmlcode.indexOf(" id=\"")!=-1) id = XML.getValue(xmlcode,"id");
    else id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    if(xmlcode.indexOf(" lefttop=\"")!=-1) lefttop = XML.getValue(xmlcode,"lefttop");
    if(xmlcode.indexOf(" rightbottom=\"")!=-1) rightbottom = XML.getValue(xmlcode,"rightbottom");
    if(xmlcode.indexOf(" xkeepratio=\"")!=-1) xkeepratio = XML.getBoolValue(xmlcode,"xkeepratio");
    if(xmlcode.indexOf(" ykeepratio=\"")!=-1) ykeepratio = XML.getBoolValue(xmlcode,"ykeepratio");
    if(xmlcode.indexOf(" visible=\"")!=-1) visible = XML.getValue(xmlcode,"visible");
    created = true;
  }
  public Video(Skin s_) {
    s = s_;    
    id = Language.get("UNNAMED").replaceAll("%t",type).replaceAll("%i",String.valueOf(s.getNewId()));
    showOptions();
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
      visible = visible_tf.getText();
      help = help_tf.getText();

      width = Integer.parseInt(width_tf.getText());  
      height = Integer.parseInt(height_tf.getText());  
      autoresize = Boolean.parseBoolean(autoresize_cb.getSelectedItem().toString());

      s.updateItems();   
      s.expandItem(id);
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
      visible = visible_tf.getText();
      help = help_tf.getText();

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
      frame = new JFrame("Video settings");
      frame.setResizable(false);
      frame.setLayout(new FlowLayout());
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
      lefttop_cb.setToolTipText("Indicate to which corner of the Layout the top-left-hand corner of this item is attached, in case of resizing.");
      JLabel rightbottom_l = new JLabel(Language.get("WIN_ITEM_RIGHTBOTTOM"));
      rightbottom_cb = new JComboBox(align_values);
      rightbottom_cb.setToolTipText("Indicate to which corner of the Layout the bottom-right-hand corner of this item is attached, in case of resizing.");
      Object[] bool_values = { true, false };
      JLabel xkeepratio_l = new JLabel(Language.get("WIN_ITEM_XKEEPRATIO"));
      xkeepratio_cb = new JComboBox(bool_values);
      xkeepratio_cb.setToolTipText(Language.get("WIN_ITEM_XKEEPRATIO_TIP"));
      JLabel ykeepratio_l = new JLabel(Language.get("WIN_ITEM_YKEEPRATIO"));
      ykeepratio_cb = new JComboBox(bool_values);
      ykeepratio_cb.setToolTipText(Language.get("WIN_ITEM_YKEEPRATIO_TIP"));
      JLabel visible_l = new JLabel(Language.get("WIN_ITEM_VISIBLE"));
      visible_tf = new JTextField();
      visible_btn = new JButton("",s.m.help_icon);
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
      JLabel autoresize_l = new JLabel("Autoresize:");
      autoresize_cb = new JComboBox(bool_values);
      autoresize_cb.setToolTipText("Indicate whether the layout should be automatically resized to fit the dimensions of the played video.");
      
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      ok_btn.setPreferredSize(new Dimension(70,25));
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      cancel_btn.setPreferredSize(new Dimension(70,25));
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);
      help_btn.setPreferredSize(new Dimension(70,25));
      
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
      general.add(lefttop_l);
      general.add(lefttop_cb);
      lefttop_l.setBounds(5,105,75,24);
      lefttop_cb.setBounds(85,105,150,24);
      general.add(rightbottom_l);
      general.add(rightbottom_cb);
      rightbottom_l.setBounds(5,135,75,24);
      rightbottom_cb.setBounds(85,135,150,24);
      general.add(xkeepratio_l);
      general.add(xkeepratio_cb);
      xkeepratio_l.setBounds(5,165,75,24);
      xkeepratio_cb.setBounds(85,165,150,24);
      general.add(ykeepratio_l);
      general.add(ykeepratio_cb);
      ykeepratio_l.setBounds(5,195,75,24);
      ykeepratio_cb.setBounds(85,195,150,24);
      general.add(visible_l);
      general.add(visible_tf);
      general.add(visible_btn);
      visible_l.setBounds(5,225,75,24);
      visible_tf.setBounds(85,225,120,24);
      visible_btn.setBounds(210,225,24,24);
      general.add(help_l);
      general.add(help_tf);
      help_l.setBounds(5,255,75,24);
      help_tf.setBounds(85,255,150,24);
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));
      general.setMinimumSize(new Dimension(240,285));
      general.setPreferredSize(new Dimension(240,285));
      general.setMaximumSize(new Dimension(240,285));
      frame.add(general);
      
      JPanel video = new JPanel(null);
      video.add(width_l);
      video.add(width_tf);
      width_l.setBounds(5,15,75,24);
      width_tf.setBounds(85,15,150,24);
      video.add(height_l);
      video.add(height_tf);
      height_l.setBounds(5,45,75,24);
      height_tf.setBounds(85,45,150,24);
      video.add(autoresize_l);
      video.add(autoresize_cb);
      autoresize_l.setBounds(5,75,75,24);
      autoresize_cb.setBounds(85,75,150,24);
      video.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Video Attributes"));
      video.setMinimumSize(new Dimension(240,105));
      video.setPreferredSize(new Dimension(240,105));
      video.setMaximumSize(new Dimension(240,105));
      frame.add(video);
      
      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);      
      frame.add(new JLabel(Language.get("NOTE_STARRED")));
      
      frame.setMinimumSize(new Dimension(250,460));
      frame.setPreferredSize(new Dimension(250,460));
      frame.setMaximumSize(new Dimension(250,460));
      
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
