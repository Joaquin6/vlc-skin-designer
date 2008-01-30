/*****************************************************************************
 * SliderBGGen.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of vlcskineditor
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import vlcskineditor.items.SliderBackground;

/**
 * A wizard that generates a slider background from several image files.
 * @author Daniel Dreibrodt
 */
public class SliderBGGen extends JFrame implements ActionListener{
  
  SliderBackground sbg;
  Skin s;
  
  CardLayout layout;
  
  final String STEP1 = "STEP1";
  final String STEP2 = "STEP2";
  
  JPanel card_step1;
  JRadioButton ltr_rb, btt_rb;
  JTextField width_tf, height_tf;
  JTextField margin_l_tf, margin_r_tf, margin_t_tf, margin_b_tf;
  
  JSeparator step1_horz_s;
  JButton step1_next_btn, step1_cancel_btn;
    
  JPanel card_step2;
  
  /** Contains the path to the background image file */
  JTextField bg_tf;
  /** Contains the path to the image file for the left or upper edge of the bar */
  JTextField e1_tf;
  /** Contains the path to the image file for the middle part of the bar */
  JTextField md_tf;
  /** Contains the path to the image file for the right or bottom edge of the bar */
  JTextField e2_tf;
  /** Sets whether the background image should be tiled to fill the slider background size */
  JRadioButton bgt_rb;
  /** Sets whether the background image should be stretched to fill the slider background size */
  JRadioButton bgs_rb;
  /** Sets whether the middle image should be tiled to fill the slider background size */
  JRadioButton mdt_rb;
  /** Sets whether the middle image should be stretched to fill the slider background size */
  JRadioButton mds_rb;  
  JButton bg_btn, e1_btn, md_btn, e2_btn;
  JFileChooser bg_fc, e1_fc, md_fc, e2_fc;
  JButton step2_prev_btn, step2_finish_btn, step2_cancel_btn;  
  
  ImageIcon editor_icon = createIcon("icons/editor.png");
  ImageIcon ltr_icon = createIcon("icons/sliderbg_ltr.png");
  ImageIcon btt_icon = createIcon("icons/sliderbg_btt.png");
  ImageIcon width_i = createIcon("icons/sbg_width.png");
  ImageIcon height_i = createIcon("icons/sbg_height.png");
  ImageIcon margin_l_i = createIcon("icons/sbg_margin_l.png");
  ImageIcon margin_r_i = createIcon("icons/sbg_margin_r.png");
  ImageIcon margin_t_i = createIcon("icons/sbg_margin_t.png");
  ImageIcon margin_b_i = createIcon("icons/sbg_margin_b.png");
  
  /**
   * Creates a new Slider Background Generator
   * @param sbg_ The SliderBackground object into which the generated slider background should be stored.
   * @param s_ The Skin in which the slider background will be used.
   */
  public SliderBGGen(SliderBackground sbg_, Skin s_) {    
    sbg=sbg_;
    s=s_;
    setIconImage(editor_icon.getImage());
    setTitle("Slider background generator");    
    layout = new CardLayout();
    setLayout(layout);
    
    Color header_bgc = getBackground().darker().darker(); //Adapts to the system style    
    String header_bgh = "#";
    if(header_bgc.getRed()<16) header_bgh+="0";
    header_bgh+=Integer.toHexString(header_bgc.getRed()).toUpperCase();
    if(header_bgc.getGreen()<16) header_bgh+="0";
    header_bgh+=Integer.toHexString(header_bgc.getGreen()).toUpperCase();
    if(header_bgc.getBlue()<16) header_bgh+="0";
    header_bgh+=Integer.toHexString(header_bgc.getBlue()).toUpperCase();   
    
    String header_str = "<html><body style=\"background:"+header_bgh+";color:#FFFFFF;width:540px;\"><h1>&nbsp;Slider Background Generator</h1></body></html>";
    
    card_step1 = new JPanel(null);    
    JLabel header1_l = new JLabel(header_str);     
    header1_l.setBounds(0,0,540,50);
    card_step1.add(header1_l); 
    
    JLabel welcome_l = new JLabel("<html><div style=\"width:400px;margin-top:2px\">Welcome to the slider background generator.<br>" +
                                  "As a first step, please choose whether the slider is going from left to right or from the bottom to the top and what size the background should have.</div></html>");    
    card_step1.add(welcome_l);    
    welcome_l.setBounds(5,25,630,100);
    ltr_rb = new JRadioButton("");    
    card_step1.add(ltr_rb);
    ltr_rb.setBounds(5,105,20,100);
    ltr_rb.setSelected(true);
    JLabel ltr_l = new JLabel(ltr_icon);
    ltr_l.setLabelFor(ltr_rb);
    card_step1.add(ltr_l);
    ltr_l.setBounds(30,105,100,100);
    
    btt_rb = new JRadioButton("");
    card_step1.add(btt_rb);
    btt_rb.setBounds(255,105,20,100);
    JLabel btt_l = new JLabel(btt_icon);
    btt_l.setLabelFor(btt_rb);
    card_step1.add(btt_l);
    btt_l.setBounds(280,105,100,100);
    
    ButtonGroup dir_bg = new ButtonGroup();
    dir_bg.add(ltr_rb);
    dir_bg.add(btt_rb);
    
    JSeparator horz2_s = new JSeparator(JSeparator.HORIZONTAL);
    card_step1.add(horz2_s);
    horz2_s.setBounds(5,215,525,215);    
    
    JLabel width_l = new JLabel("Width:",width_i,JLabel.RIGHT);
    width_tf = new JTextField();    
    width_tf.setDocument(new NumbersOnlyDocument(false));
    width_l.setLabelFor(width_tf);
    
    card_step1.add(width_l);
    width_l.setBounds(5,230,80,25);
    card_step1.add(width_tf);
    width_tf.setBounds(90,230,50,25);    
    
    JLabel height_l = new JLabel("Height:",height_i,JLabel.RIGHT);
    height_tf = new JTextField();
    height_tf.setDocument(new NumbersOnlyDocument(false));
    height_l.setLabelFor(height_tf);
    
    card_step1.add(height_l);
    height_l.setBounds(5,265,80,25);
    card_step1.add(height_tf);
    height_tf.setBounds(90,265,50,25);
    
    JLabel margin_l_l = new JLabel("Left margin:", margin_l_i, JLabel.RIGHT);
    margin_l_tf = new JTextField();
    margin_l_tf.setDocument(new NumbersOnlyDocument(false));
    margin_l_l.setLabelFor(margin_l_tf);
    JLabel margin_r_l = new JLabel("Right margin:", margin_r_i, JLabel.RIGHT);
    margin_r_tf = new JTextField();
    margin_r_tf.setDocument(new NumbersOnlyDocument(false));
    margin_r_l.setLabelFor(margin_r_tf);
    JLabel margin_t_l = new JLabel("Top margin:", margin_t_i, JLabel.RIGHT);
    margin_t_tf = new JTextField();
    margin_t_tf.setDocument(new NumbersOnlyDocument(false));
    margin_t_l.setLabelFor(margin_t_tf);
    JLabel margin_b_l = new JLabel("Bottom margin:", margin_b_i, JLabel.RIGHT);
    margin_b_tf = new JTextField();
    margin_b_tf.setDocument(new NumbersOnlyDocument(false));
    margin_b_l.setLabelFor(margin_b_tf);
    
    card_step1.add(margin_l_l);
    card_step1.add(margin_l_tf);
    margin_l_l.setBounds(150,230,115,25);
    margin_l_tf.setBounds(270,230,50,25);
    card_step1.add(margin_r_l);
    card_step1.add(margin_r_tf);
    margin_r_l.setBounds(150,265,115,25);
    margin_r_tf.setBounds(270,265,50,25);
    card_step1.add(margin_t_l);
    card_step1.add(margin_t_tf);
    margin_t_l.setBounds(325,230,115,25);
    margin_t_tf.setBounds(445,230,50,25);
    card_step1.add(margin_b_l);
    card_step1.add(margin_b_tf);
    margin_b_l.setBounds(325,265,115,25);
    margin_b_tf.setBounds(445,265,50,25);
    
    step1_horz_s = new JSeparator(JSeparator.HORIZONTAL);   
    step1_next_btn = new JButton("Next >");
    step1_next_btn.addActionListener(this);
    step1_cancel_btn = new JButton("Cancel");
    step1_cancel_btn.addActionListener(this);
    
    card_step1.add(step1_horz_s);
    card_step1.add(step1_next_btn);
    card_step1.add(step1_cancel_btn);
    
    step1_horz_s.setBounds(5,345,525,5);    
    step1_next_btn.setBounds(370,350,75,20);
    step1_cancel_btn.setBounds(450,350,75,20);    
    
    card_step1.setSize(540,400);   
    
    add(card_step1,STEP1);   
    
    card_step2 = new JPanel(null);
    JLabel header2_l = new JLabel(header_str);     
    header2_l.setBounds(0,0,540,50);
    card_step2.add(header2_l); 
    
    JPanel bg_p = new JPanel(null);
    JLabel bg_l = new JLabel("Image file:");
    bg_tf = new JTextField("");
    bg_btn = new JButton("Open...");
    
    
    JSeparator step2_horz_s = new JSeparator(JSeparator.HORIZONTAL);
    step2_prev_btn = new JButton("< Previous");
    step2_prev_btn.addActionListener(this);
    step2_finish_btn = new JButton("Finish");
    step2_finish_btn.addActionListener(this);
    step2_cancel_btn = new JButton("Cancel");
    step2_cancel_btn.addActionListener(this);    
    
    card_step2.add(step2_horz_s);
    card_step2.add(step2_prev_btn);
    card_step2.add(step2_finish_btn);
    card_step2.add(step2_cancel_btn);
    
    step2_horz_s.setBounds(5,345,525,5);    
    step2_prev_btn.setBounds(275,350,90,20);
    step2_finish_btn.setBounds(370,350,75,20);
    step2_cancel_btn.setBounds(450,350,75,20);    
  
    add(card_step2,STEP2);    
    
    setSize(540,400);
    setResizable(false);
  }
  public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals("Cancel")) {
      System.exit(1);
    }
    else if (e.getSource().equals(step1_next_btn)) {
      layout.show(getContentPane(),STEP2);
    }
    else if (e.getSource().equals(step2_prev_btn)) {
      layout.show(getContentPane(),STEP1);
    }
    else if (e.getSource().equals(step2_finish_btn)) {
      //Do something
    }
  }
  /**
   * Creates an ImageIcon of an image included in the JAR
   * @param filename The path to the image file inside the JAR
   * @return An ImageIcon representing the given file
   */
  public ImageIcon createIcon(String filename) {      
      java.awt.Image img = null;
      try {
        img = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource(filename));
        //img = ImageIO.read(file);
        return new ImageIcon(img);  
      } catch (Exception e) {
        System.out.println(e);
        return null;
      }
  }
  /**
   * For testing purposes
   */
  public static void main(String[] args) {
    try {	
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } 
    catch (Exception e) {
      
    }
    SliderBGGen sbgg = new SliderBGGen(null,null);
    sbgg.setVisible(true);
    sbgg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
