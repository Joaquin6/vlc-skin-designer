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
  
  JSeparator horz_s;
  JButton prev_btn, next_btn, cancel_btn, finish_btn;
  
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
    
    horz_s = new JSeparator(SwingConstants.HORIZONTAL);
    prev_btn = new JButton("< Previous");
    prev_btn.addActionListener(this);    
    next_btn = new JButton("Next >");
    next_btn.addActionListener(this);
    cancel_btn = new JButton("Cancel");
    cancel_btn.addActionListener(this);
    finish_btn = new JButton("Finish");
    finish_btn.addActionListener(this);
    
    JLabel header_text_l = new JLabel("<html><body style=\"background:#babdb6;width:540px;\"><h1>&nbsp;Slider Background Generator</h1></body></html>");  
    
    header_text_l.setBounds(0,0,540,50);
    
    card_step1 = new JPanel(null);    
    card_step1.add(header_text_l);
    JLabel welcome_l = new JLabel("<html>Welcome to the slider background generator.<br>" +
                                  "As a first step, please choose whether the slider is going from left to right or from the bottom to the top.</html>");    
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
    
    card_step1.add(horz_s);
    card_step1.add(next_btn);
    card_step1.add(cancel_btn);
    
    card_step1.setSize(540,400);
    
    horz_s.setBounds(5,345,525,5);
    prev_btn.setBounds(290,350,75,20);
    next_btn.setBounds(370,350,75,20);
    cancel_btn.setBounds(450,350,75,20);
    finish_btn.setBounds(370,350,75,20);
    
    add(card_step1,STEP1);    
    setSize(540,400);
    setResizable(false);
  }
  public void actionPerformed(ActionEvent e) {
    
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
