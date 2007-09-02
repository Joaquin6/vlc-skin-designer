/*  
VLC Slider Background Generator
Copyright (C) 2007 Daniel Dreibrodt

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package vlcsliderbggen;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;


public class Main extends JFrame implements ActionListener{  
  File img_bg, img_left, img_middle, img_right, img_overlay, save_file;
  JFileChooser fc_bg, fc_left, fc_middle, fc_right, fc_overlay, fc_save;
  JRadioButton horz_button, vert_button, img_bg_tile, img_bg_stretch, img_middle_tile, img_middle_stretch;
  JButton img_bg_open, img_left_open, img_middle_open, img_right_open, preview_button, save_button;
  ButtonGroup dir_buttons, img_bg_buttons, img_middle_buttons;
  JLabel size_w_label, size_h_label, margin_l_label, margin_r_label, margin_t_label, margin_b_label;
  JLabel img_bg_label, img_bg_resize, img_left_label, img_middle_label, img_middle_resize, img_right_label;
  JTextField size_w_text, size_h_text ,margin_l_text, margin_r_text, margin_t_text, margin_b_text;
  JTextField img_bg_text, img_left_text, img_middle_text, img_right_text;
  JPanel dir_panel, size_panel, margin_panel, img_bg_panel, img_left_panel, img_middle_panel, img_right_panel;
  JLabel copyright;
  JLabel overlay_label, overlay_x_label, overlay_y_label;
  JPanel overlay_panel;
  JTextField overlay_text, overlay_x_text, overlay_y_text;
  JButton overlay_open;
  Preview pv;
  
  public Main() {
    super("VLC Slider Background Generator");
       
    fc_bg = new JFileChooser();
    fc_left = new JFileChooser();
    fc_middle = new JFileChooser();
    fc_right = new JFileChooser();
    fc_overlay = new JFileChooser();
    fc_save = new JFileChooser();
    
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);    
    horz_button = new JRadioButton("Left to right");
    horz_button.setSelected(true);
    vert_button = new JRadioButton("Bottom to top"); 
    horz_button.addActionListener(this);
    vert_button.addActionListener(this);     
    
    dir_buttons = new ButtonGroup();
    dir_buttons.add(horz_button);
    dir_buttons.add(vert_button);
       
    dir_panel = new JPanel(new GridLayout(1,0,5,5));
    dir_panel.add(horz_button);
    dir_panel.add(vert_button);
    
    add(dir_panel);
    
    size_w_label = new JLabel("Width:");
    size_h_label = new JLabel("Height:");
    size_w_text = new JTextField("0",5);
    size_h_text = new JTextField("0",5);
    size_w_label.setLabelFor(size_w_text);
    size_h_label.setLabelFor(size_h_text);
    
    size_panel = new JPanel(new GridLayout(1,0,5,5));
    size_panel.add(size_w_label);
    size_panel.add(size_w_text);
    size_panel.add(size_h_label);
    size_panel.add(size_h_text);
    
    add(size_panel);
    
    margin_l_label = new JLabel("Left:");
    margin_r_label = new JLabel("Right:");
    margin_t_label = new JLabel("Top:");
    margin_b_label = new JLabel("Bottom:");
    
    margin_l_text = new JTextField("0",5);    
    margin_r_text = new JTextField("0",5);
    margin_t_text = new JTextField("0",5);
    margin_b_text = new JTextField("0",5);
    
    margin_panel = new JPanel(new GridLayout(1,0,5,5));
    margin_panel.add(margin_l_label);
    margin_panel.add(margin_l_text);
    margin_panel.add(margin_r_label);
    margin_panel.add(margin_r_text);
    margin_panel.add(margin_t_label);
    margin_panel.add(margin_t_text);
    margin_panel.add(margin_b_label);
    margin_panel.add(margin_b_text);
    
    
    
    add(margin_panel);
    
    img_bg_label = new JLabel("Background (optional):");
    img_bg_text = new JTextField("",50);
    img_bg_open = new JButton("Open");
    img_bg_open.addActionListener(this);
    img_bg_resize = new JLabel("Resize Behavior:");
    img_bg_tile = new JRadioButton("Tile");
    img_bg_tile.addActionListener(this);
    img_bg_tile.setSelected(true);
    img_bg_stretch = new JRadioButton("Stretch");
    img_bg_stretch.addActionListener(this);
    img_bg_buttons = new ButtonGroup();
    img_bg_buttons.add(img_bg_tile);
    img_bg_buttons.add(img_bg_stretch);       
    
    img_bg_panel = new JPanel(new GridLayout(2,3,5,5));
    img_bg_panel.add(img_bg_label);
    img_bg_panel.add(img_bg_text);
    img_bg_panel.add(img_bg_open);
    img_bg_panel.add(img_bg_resize);
    img_bg_panel.add(img_bg_tile);
    img_bg_panel.add(img_bg_stretch);      
    
    add(img_bg_panel);    
    
    img_left_label = new JLabel("Left edge of the bar (optional):");
    img_left_text = new JTextField("",50);
    img_left_open = new JButton("Open");
    img_left_open.addActionListener(this);
    
    img_left_panel = new JPanel(new GridLayout(1,3,5,5));
    img_left_panel.add(img_left_label);
    img_left_panel.add(img_left_text);
    img_left_panel.add(img_left_open);
    
    add(img_left_panel);
    
    img_middle_label = new JLabel("Middle:");
    img_middle_text = new JTextField("",50);
    img_middle_open = new JButton("Open");
    img_middle_open.addActionListener(this);
    img_middle_resize = new JLabel("Resize Behavior:");
    img_middle_tile = new JRadioButton("Tile");
    img_middle_tile.addActionListener(this);
    img_middle_tile.setSelected(true);
    img_middle_stretch = new JRadioButton("Stretch");
    img_middle_stretch.addActionListener(this);
    img_middle_buttons = new ButtonGroup();
    img_middle_buttons.add(img_middle_tile);
    img_middle_buttons.add(img_middle_stretch);       
    
    img_middle_panel = new JPanel(new GridLayout(2,3,5,5));
    img_middle_panel.add(img_middle_label);
    img_middle_panel.add(img_middle_text);
    img_middle_panel.add(img_middle_open);
    img_middle_panel.add(img_middle_resize);
    img_middle_panel.add(img_middle_tile);
    img_middle_panel.add(img_middle_stretch);      
    
    add(img_middle_panel);  
    
    img_right_label = new JLabel("Right edge of the bar(optional):");
    img_right_text = new JTextField("",50);
    img_right_open = new JButton("Open");
    img_right_open.addActionListener(this);
    
    img_right_panel = new JPanel(new GridLayout(1,3,5,5));
    img_right_panel.add(img_right_label);
    img_right_panel.add(img_right_text);
    img_right_panel.add(img_right_open);
    
    add(img_right_panel);
    
    overlay_label = new JLabel("Overlay image(optional):");
    overlay_text = new JTextField("",50);
    overlay_open = new JButton("Open");
    overlay_open.addActionListener(this);
    
    overlay_x_label = new JLabel("X-Position:");
    overlay_x_text = new JTextField("0",5);
    overlay_y_label = new JLabel("Y-Position:");
    overlay_y_text = new JTextField("0",5);
    
    overlay_panel = new JPanel(new GridLayout(2,1,5,5));
    JPanel overlay_panel_1 = new JPanel(new GridLayout(1,3,5,5));
    overlay_panel_1.add(overlay_label);
    overlay_panel_1.add(overlay_text);
    overlay_panel_1.add(overlay_open);
    JPanel overlay_panel_2 = new JPanel(new GridLayout(1,4,5,5));
    overlay_panel_2.add(overlay_x_label);
    overlay_panel_2.add(overlay_x_text);
    overlay_panel_2.add(overlay_y_label);
    overlay_panel_2.add(overlay_y_text);
    overlay_panel.add(overlay_panel_1);
    overlay_panel.add(overlay_panel_2);
    
    add(overlay_panel);
    
    copyright = new JLabel("VLC Slider Background Generator v.0.2.0 (C)2007 Daniel Dreibrodt");
    
    add(copyright);
    
    preview_button = new JButton("Preview");
    preview_button.addActionListener(this);
    add(preview_button);
    save_button = new JButton("Save");
    save_button.addActionListener(this);
    add(save_button);
    
    setLayout(null);
    
    dir_panel.setBounds(5,5,300,50);
    dir_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Direction"));    
    size_panel.setBounds(305,5,320,50);      
    size_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Size"));
    margin_panel.setBounds(5,55,620,50);      
    margin_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Margin"));
    img_bg_panel.setBounds(5,110,620,80);      
    img_bg_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Background Image"));
    img_left_panel.setBounds(5,195,620,50);      
    img_left_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Left Image"));
    img_middle_panel.setBounds(5,250,620,80);      
    img_middle_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Middle Image"));
    img_right_panel.setBounds(5,335,620,50);      
    img_right_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Right Image"));
    overlay_panel.setBounds(5,390,620,80);
    overlay_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Overlay Image"));
    copyright.setBounds(5,475,410,20);
    preview_button.setBounds(420,475,100,20);
    save_button.setBounds(525,475,100,20);
    
    setSize(640,525);    
    
    setLocationRelativeTo(null);
    setResizable(false);
    setVisible(true);
  }
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(vert_button)) {
      img_left_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Top Image"));
      img_left_label.setText("Top edge of the bar(optional):");
      img_right_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Bottom Image"));
      img_right_label.setText("Bottom edge of the bar(optional):");
    }   
    if(e.getSource().equals(horz_button)) {
      img_left_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Left Image"));
      img_left_label.setText("Left edge of the bar(optional):");
      img_right_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Right Image"));
      img_right_label.setText("Right edge of the bar(optional):");
    }
    if(e.getSource().equals(img_bg_open)) {      
      int returnVal = fc_bg.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            img_bg = fc_bg.getSelectedFile();            
            img_bg_text.setText(img_bg.getAbsolutePath());
      }
    }
    if(e.getSource().equals(img_left_open)) {      
      int returnVal = fc_left.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            img_left = fc_left.getSelectedFile();            
            img_left_text.setText(img_left.getAbsolutePath());
      }
    }
    if(e.getSource().equals(img_middle_open)) {      
      int returnVal = fc_middle.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            img_middle = fc_middle.getSelectedFile();            
            img_middle_text.setText(img_middle.getAbsolutePath());
      }
    }
    if(e.getSource().equals(img_right_open)) {      
      int returnVal = fc_right.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            img_right = fc_right.getSelectedFile();            
            img_right_text.setText(img_right.getAbsolutePath());
      }
    }
    if(e.getSource().equals(overlay_open)) {      
      int returnVal = fc_overlay.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            img_overlay = fc_overlay.getSelectedFile();            
            overlay_text.setText(img_overlay.getAbsolutePath());
      }
    }
    if(e.getSource().equals(preview_button)) {
      pv = new Preview(this);
      pv.start();
    }
    if(e.getSource().equals(save_button)) {
      int returnVal = fc_save.showSaveDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            save_file = fc_save.getSelectedFile();            
            Builder b = new Builder(this);
            b.save(save_file);
      }
    }
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new Main();
  }
  
}
