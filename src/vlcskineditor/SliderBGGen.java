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
import vlcskineditor.Items.SliderBackground;

/**
 * SliderBGGen
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
  
  JSeparator step1_horz_s;
  JButton step1_next_btn, step1_cancel_btn;
  JTextField width_tf, height_tf;
  
  JPanel card_step2;
  
  JTextField bg_tf, e1_tf, md_tf, e2_tf;
  JButton bg_btn, e1_btn, md_btn, e2_btn;
  JFileChooser bg_fc, e1_fc, md_fc, e2_fc;
  JButton step2_prev_btn, step2_finish_btn, step2_cancel_btn;
  
  public ImageIcon editor_icon = createIcon("icons/editor.png");
  public ImageIcon ltr_icon = createIcon("icons/sliderbg_ltr.png");
  public ImageIcon btt_icon = createIcon("icons/sliderbg_btt.png");  
  
  /** Creates a new instance of SliderBGGen */
  public SliderBGGen(SliderBackground sbg_, Skin s_) {    
    sbg=sbg_;
    s=s_;
    setIconImage(editor_icon.getImage());
    setTitle("Slider background generator");    
    layout = new CardLayout();
    setLayout(layout);
    
    Color header_bgc = getBackground().darker().darker();    
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
    
    JLabel welcome_l = new JLabel("<html>Welcome to the slider background generator.<br>" +
                                  "As a first step, please choose whether the slider is going from left to right or from the bottom to the top and what size the background should have.</html>");    
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
    
    JLabel width_l = new JLabel("Width:");
    width_tf = new JTextField();    
    
    card_step1.add(width_l);
    width_l.setBounds(5,230,75,25);
    card_step1.add(width_tf);
    width_tf.setBounds(80,230,150,25);
    
    JLabel height_l = new JLabel("Height:");
    height_tf = new JTextField();
    
    card_step1.add(height_l);
    height_l.setBounds(5,265,75,25);
    card_step1.add(height_tf);
    height_tf.setBounds(80,265,150,25);
    
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
    
  
    add(card_step2,STEP2);
    
    
    
    setSize(540,400);
    setResizable(false);
  }
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(step1_cancel_btn)) {
      System.exit(1);
    }
    else if (e.getSource().equals(step1_next_btn)) {
      layout.show(getContentPane(),STEP2);
    }
  }
  /**
   * Creates an ImageIcon of an image included in the JAR
   * @param filename  The path to the image file inside the JAR
   * @return         An ImageIcon representing the given file
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
    sbgg.setDefaultCloseOperation(sbgg.EXIT_ON_CLOSE);
  }
}
