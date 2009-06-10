/*****************************************************************************
 * SliderBackground.java
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
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.Node;
import vlcskineditor.resources.ImageResource;

/**
 * SliderBackground item
 * @author Daniel Dreibrodt
 */
public class SliderBackground extends Item implements ActionListener {

  public final int NBHORIZ_DEFAULT = 1;
  public final int NBVERT_DEFAULT = 1;
  public final int PADHORIZ_DEFAULT = 0;
  public final int PADVERT_DEFAULT = 0;
  public String image;
  public int nbhoriz = NBHORIZ_DEFAULT;
  public int nbvert = NBVERT_DEFAULT;
  public int padhoriz = PADHORIZ_DEFAULT;
  public int padvert = PADVERT_DEFAULT;
  private JFrame frame;
  public JTextField id_tf,  image_tf,  nbhoriz_tf,  nbvert_tf,  padhoriz_tf,  padvert_tf;
  private JButton gen_btn, ok_btn, cancel_btn, help_btn;
  private ImageResource image_res;
  private BufferedImage bi = null;
  private String bitmap_str = "";
  private Slider parent;
  private float sliderVal = 0.5f;


  {
    type = Language.get("SLIDERBG");
  }

  /**
   * Parses a SliderBackground from a XML node
   * @param n The XML node
   * @param s_ The parent skin
   * @param pr The parent slider
   */
  public SliderBackground(Node n, Skin s_, Slider pr) {
    s = s_;
    parent = pr;

    id = XML.getStringAttributeValue(n, "id", Language.get("UNNAMED").replaceAll("%t", type).replaceAll("%i", String.valueOf(s.getNewId())));

    image = XML.getStringAttributeValue(n, "image", image);
    nbhoriz = XML.getIntAttributeValue(n, "nbhoriz", nbhoriz);
    nbvert = XML.getIntAttributeValue(n, "nbvert", nbvert);
    padhoriz = XML.getIntAttributeValue(n, "padhoriz", padhoriz);
    padvert = XML.getIntAttributeValue(n, "padvert", padvert);

    image_res = s.getImageResource(image);

    created = true;
  }

  /** Creates a new instance of SliderBackground
   * @param xmlcode The XML code
   * @param s_ The parent skin
   */
  public SliderBackground(String xmlcode, Skin s_) {
    s = s_;
    image = XML.getValue(xmlcode, "image");
    image_res = s.getImageResource(image);
    if(xmlcode.indexOf("nbhoriz=\"") != -1) {
      nbhoriz = XML.getIntValue(xmlcode, "nbhoriz");
    }
    if(xmlcode.indexOf("nbvert=\"") != -1) {
      nbvert = XML.getIntValue(xmlcode, "nbvert");
    }
    if(xmlcode.indexOf("padhoriz=\"") != -1) {
      padhoriz = XML.getIntValue(xmlcode, "padhoriz");
    }
    if(xmlcode.indexOf("padvert=\"") != -1) {
      padvert = XML.getIntValue(xmlcode, "padvert");
    }
    if(xmlcode.indexOf("id=\"") != -1) {
      id = XML.getValue(xmlcode, "id");
    } else {
      id = Language.get("UNNAMED").replaceAll("%t", type).replaceAll("%i", String.valueOf(s.getNewId()));
    }
    created = true;
  }

  public SliderBackground(Skin s_, Slider sl_) {
    s = s_;
    parent = sl_;
    image = "none";
    id = Language.get("UNNAMED").replaceAll("%t", type).replaceAll("%i", String.valueOf(s.getNewId()));
    showOptions();
  }

  public void update() {
    if(!created) {
      id = id_tf.getText();
      image = image_tf.getText();
      nbhoriz = Integer.parseInt(nbhoriz_tf.getText());
      nbvert = Integer.parseInt(nbvert_tf.getText());
      padhoriz = Integer.parseInt(padhoriz_tf.getText());
      padvert = Integer.parseInt(padvert_tf.getText());
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      created = true;

      ItemAddEvent sae = new ItemAddEvent(s.getParentListOf(id), this);
      s.m.hist.addEvent(sae);
    } else {
      SliderBackgroundEditEvent see = new SliderBackgroundEditEvent(this);

      id = id_tf.getText();
      image = image_tf.getText();
      nbhoriz = Integer.parseInt(nbhoriz_tf.getText());
      nbvert = Integer.parseInt(nbvert_tf.getText());
      padhoriz = Integer.parseInt(padhoriz_tf.getText());
      padvert = Integer.parseInt(padvert_tf.getText());

      see.setNew();
      s.m.hist.addEvent(see);
    }
  }

  public void showOptions() {
    if(frame == null) {
      frame = new JFrame(Language.get("WIN_SBG_TITLE"));
      frame.setIconImage(Main.edit_icon.getImage());
      frame.setResizable(false);
      if(!created) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      }
      JLabel id_l = new JLabel(Language.get("WIN_ITEM_ID"));
      id_tf = new JTextField();
      gen_btn = new JButton(Language.get("WIN_SBG_WIZARD"));
      gen_btn.addActionListener(this);
      JLabel image_l = new JLabel(Language.get("WIN_SBG_IMAGE"));
      image_tf = new JTextField();
      JLabel nbhoriz_l = new JLabel(Language.get("WIN_SBG_NBHORIZ"));
      nbhoriz_tf = new JTextField();
      nbhoriz_tf.setDocument(new NumbersOnlyDocument());
      JLabel nbvert_l = new JLabel(Language.get("WIN_SBG_NBVERT"));
      nbvert_tf = new JTextField();
      nbvert_tf.setDocument(new NumbersOnlyDocument());
      JLabel padhoriz_l = new JLabel(Language.get("WIN_SBG_PADHORIZ"));
      padhoriz_tf = new JTextField();
      padhoriz_tf.setDocument(new NumbersOnlyDocument());
      JLabel padvert_l = new JLabel(Language.get("WIN_SBG_PADVERT"));
      padvert_tf = new JTextField();
      padvert_tf.setDocument(new NumbersOnlyDocument());
      JLabel attr_l = new JLabel(Language.get("NOTE_STARRED"));
      ok_btn = new JButton(Language.get("BUTTON_OK"));
      ok_btn.addActionListener(this);
      cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
      cancel_btn.addActionListener(this);
      help_btn = new JButton(Language.get("BUTTON_HELP"));
      help_btn.addActionListener(this);

      //Distance of textfields to WEST edge of container
      Component[] labels = {id_l, image_l, nbhoriz_l, nbvert_l, padhoriz_l, padvert_l};
      int tf_dx = Helper.maxWidth(labels) + 10;
      //Max. textfield width
      int tf_wd = Main.TEXTFIELD_WIDTH;

      JPanel general = new JPanel(null);
      general.add(id_l);
      general.add(id_tf);
      id_tf.setPreferredSize(new Dimension(tf_wd, id_tf.getPreferredSize().height));
      general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_ITEM_GENERAL")));

      SpringLayout general_layout = new SpringLayout();
      general.setLayout(general_layout);

      general_layout.putConstraint(SpringLayout.NORTH, id_l, 5, SpringLayout.NORTH, general);
      general_layout.putConstraint(SpringLayout.WEST, id_l, 5, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.VERTICAL_CENTER, id_tf, 0, SpringLayout.VERTICAL_CENTER, id_l);
      general_layout.putConstraint(SpringLayout.WEST, id_tf, tf_dx, SpringLayout.WEST, general);

      general_layout.putConstraint(SpringLayout.EAST, general, 5, SpringLayout.EAST, id_tf);
      general_layout.putConstraint(SpringLayout.SOUTH, general, 10, SpringLayout.SOUTH, id_tf);

      frame.add(general);

      frame.add(gen_btn);

      JPanel bg_panel = new JPanel(null);
      bg_panel.add(image_l);
      bg_panel.add(image_tf);
      image_tf.setPreferredSize(new Dimension(tf_wd, image_tf.getPreferredSize().height));
      bg_panel.add(nbhoriz_l);
      bg_panel.add(nbhoriz_tf);
      bg_panel.add(nbvert_l);
      bg_panel.add(nbvert_tf);
      bg_panel.add(padhoriz_l);
      bg_panel.add(padhoriz_tf);
      bg_panel.add(padvert_l);
      bg_panel.add(padvert_tf);
      bg_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("WIN_SBG_ATTR")));

      SpringLayout bg_layout = new SpringLayout();

      bg_layout.putConstraint(SpringLayout.NORTH, image_l, 5, SpringLayout.NORTH, bg_panel);
      bg_layout.putConstraint(SpringLayout.WEST, image_l, 5, SpringLayout.WEST, bg_panel);

      bg_layout.putConstraint(SpringLayout.VERTICAL_CENTER, image_tf, 0, SpringLayout.VERTICAL_CENTER, image_l);
      bg_layout.putConstraint(SpringLayout.WEST, image_tf, tf_dx, SpringLayout.WEST, bg_panel);

      bg_layout.putConstraint(SpringLayout.NORTH, nbhoriz_l, 10, SpringLayout.SOUTH, image_tf);
      bg_layout.putConstraint(SpringLayout.WEST, nbhoriz_l, 5, SpringLayout.WEST, bg_panel);

      bg_layout.putConstraint(SpringLayout.VERTICAL_CENTER, nbhoriz_tf, 0, SpringLayout.VERTICAL_CENTER, nbhoriz_l);
      bg_layout.putConstraint(SpringLayout.WEST, nbhoriz_tf, tf_dx, SpringLayout.WEST, bg_panel);
      bg_layout.putConstraint(SpringLayout.EAST, nbhoriz_tf, 0, SpringLayout.EAST, image_tf);

      bg_layout.putConstraint(SpringLayout.NORTH, nbvert_l, 10, SpringLayout.SOUTH, nbhoriz_tf);
      bg_layout.putConstraint(SpringLayout.WEST, nbvert_l, 5, SpringLayout.WEST, bg_panel);

      bg_layout.putConstraint(SpringLayout.VERTICAL_CENTER, nbvert_tf, 0, SpringLayout.VERTICAL_CENTER, nbvert_l);
      bg_layout.putConstraint(SpringLayout.WEST, nbvert_tf, tf_dx, SpringLayout.WEST, bg_panel);
      bg_layout.putConstraint(SpringLayout.EAST, nbvert_tf, 0, SpringLayout.EAST, image_tf);

      bg_layout.putConstraint(SpringLayout.NORTH, padhoriz_l, 10, SpringLayout.SOUTH, nbvert_tf);
      bg_layout.putConstraint(SpringLayout.WEST, padhoriz_l, 5, SpringLayout.WEST, bg_panel);

      bg_layout.putConstraint(SpringLayout.VERTICAL_CENTER, padhoriz_tf, 0, SpringLayout.VERTICAL_CENTER, padhoriz_l);
      bg_layout.putConstraint(SpringLayout.WEST, padhoriz_tf, tf_dx, SpringLayout.WEST, bg_panel);
      bg_layout.putConstraint(SpringLayout.EAST, padhoriz_tf, 0, SpringLayout.EAST, image_tf);

      bg_layout.putConstraint(SpringLayout.NORTH, padvert_l, 10, SpringLayout.SOUTH, padhoriz_tf);
      bg_layout.putConstraint(SpringLayout.WEST, padvert_l, 5, SpringLayout.WEST, bg_panel);

      bg_layout.putConstraint(SpringLayout.VERTICAL_CENTER, padvert_tf, 0, SpringLayout.VERTICAL_CENTER, padvert_l);
      bg_layout.putConstraint(SpringLayout.WEST, padvert_tf, tf_dx, SpringLayout.WEST, bg_panel);
      bg_layout.putConstraint(SpringLayout.EAST, padvert_tf, 0, SpringLayout.EAST, image_tf);

      bg_layout.putConstraint(SpringLayout.EAST, bg_panel, 5, SpringLayout.EAST, image_tf);
      bg_layout.putConstraint(SpringLayout.SOUTH, bg_panel, 10, SpringLayout.SOUTH, padvert_tf);

      bg_panel.setLayout(bg_layout);

      frame.add(bg_panel);

      frame.add(ok_btn);
      frame.add(cancel_btn);
      frame.add(help_btn);
      frame.add(attr_l);

      SpringLayout layout = new SpringLayout();

      layout.putConstraint(SpringLayout.NORTH, general, 5, SpringLayout.NORTH, frame.getContentPane());
      layout.putConstraint(SpringLayout.WEST, general, 5, SpringLayout.WEST, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, gen_btn, 10, SpringLayout.SOUTH, general);
      layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, gen_btn, 0, SpringLayout.HORIZONTAL_CENTER, frame.getContentPane());

      layout.putConstraint(SpringLayout.NORTH, bg_panel, 10, SpringLayout.SOUTH, gen_btn);
      layout.putConstraint(SpringLayout.WEST, bg_panel, 5, SpringLayout.WEST, frame.getContentPane());
      layout.putConstraint(SpringLayout.EAST, bg_panel, 0, SpringLayout.EAST, general);

      layout.putConstraint(SpringLayout.NORTH, attr_l, 10, SpringLayout.SOUTH, bg_panel);
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
    image_tf.setText(image);
    nbhoriz_tf.setText(String.valueOf(nbhoriz));
    nbvert_tf.setText(String.valueOf(nbvert));
    padhoriz_tf.setText(String.valueOf(padhoriz));
    padvert_tf.setText(String.valueOf(padvert));
    frame.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(ok_btn)) {
      if(id_tf.getText().equals("")) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_ID_INVALID_MSG"), Language.get("ERROR_ID_INVALID_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        return;
      } else if(!id_tf.getText().equals(id)) {
        if(s.idExists(id_tf.getText())) {
          JOptionPane.showMessageDialog(frame, Language.get("ERROR_ID_EXISTS_MSG").replaceAll("%i", id_tf.getText()), Language.get("ERROR_ID_INVALID_TITLE"), JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
      image_res = s.getImageResource(image_tf.getText());
      if(image_res == null) {
        JOptionPane.showMessageDialog(frame, Language.get("ERROR_BITMAP_NEXIST").replaceAll("%i", image_tf.getText()), Language.get("ERROR_BITMAP_NEXIST_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        image_res = s.getImageResource(image);
        return;
      }
      update();
      s.updateItems();
      s.expandItem(id);
      frame.setVisible(false);
      frame.dispose();
      frame = null;
    } else if(e.getSource().equals(help_btn)) {
      Helper.browse("http://www.videolan.org/vlc/skinedhlp/i-sliderbg.html");
    } else if(e.getSource().equals(gen_btn)) {
      SliderBGGen sbgg = new SliderBGGen(this, s);
      sbgg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      sbgg.setVisible(true);
    } else if(e.getSource().equals(cancel_btn)) {
      frame.setVisible(false);
      frame.dispose();
      frame = null;
      if(!created) {
        parent.removeBG();
      }
    }
  }

  public String returnCode(String indent) {
    String code = indent + "<SliderBackground";
    if(!id.equals(ID_DEFAULT)) {
      code += " id=\"" + id + "\"";
    }
    code += " image=\"" + image + "\"";
    if(nbhoriz != NBHORIZ_DEFAULT) {
      code += " nbhoriz=\"" + String.valueOf(nbhoriz) + "\"";
    }
    if(nbvert != NBVERT_DEFAULT) {
      code += " nbvert=\"" + String.valueOf(nbvert) + "\"";
    }
    if(padhoriz != PADHORIZ_DEFAULT) {
      code += " padhoriz=\"" + String.valueOf(padhoriz) + "\"";
    }
    if(padvert != PADVERT_DEFAULT) {
      code += " padvert=\"" + String.valueOf(padvert) + "\"";
    }
    code += "/>";
    return code;
  }

  public void draw(Graphics2D g, int z) {
    draw(g, 0, 0, z);
  }

  public void draw(Graphics2D g, int x_, int y_, int z) {
    if(!created) {
      return;
    }
    bi = image_res.image;
    if(bi == null) {
      return;
    }
    int fwidth = (bi.getWidth() - padhoriz * (nbhoriz - 1)) / nbhoriz;
    int fheight = (bi.getHeight() - padvert * (nbvert - 1)) / nbvert;
    int fields = nbhoriz * nbvert;
    int n = (int) (fields * sliderVal);
    int fypos = n / nbhoriz - 1;
    if(fypos < 0) {
      fypos = 0;
    }
    int fxpos = n % nbhoriz;
    if(fxpos < 0) {
      fxpos = 0;
    }
    bi = bi.getSubimage(fxpos * fwidth + fxpos * padhoriz, fypos * fheight + fypos * padvert, fwidth, fheight);
    g.drawImage(bi, (x + x_) * z, (y + y_) * z, bi.getWidth() * z, bi.getHeight() * z, null);
    if(selected) {
      g.setColor(Color.RED);
      g.drawRect((x + x_) * z, (y + y_) * z, bi.getWidth() * z - 1, bi.getHeight() * z - 1);
    }
  }

  @Override
  public boolean contains(int x_, int y_) {
    if(bi == null) {
      return false;
    }
    return (x_ >= x + offsetx && x_ <= x + bi.getWidth() + offsetx && y_ >= y + offsety && y_ <= y + bi.getHeight() + offsety);
  }

  public DefaultMutableTreeNode getTreeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("SliderBackground: " + id);
    return node;
  }

  @Override
  public boolean uses(String id_) {
    return (image.equals(id_));
  }

  @Override
  public void updateToGlobalVariables() {
    sliderVal = s.gvars.getSliderValue();
  }

  @Override
  public void resourceRenamed(String oldid, String newid) {
    if(image.equals(oldid)) image = newid;
  }

  public Slider getParentSlider() {
    return parent;
  }
}
