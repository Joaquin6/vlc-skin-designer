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
import java.io.File;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import vlcskineditor.items.SliderBackground;
import vlcskineditor.resources.Bitmap;

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
  /** Contains the path to the image file for the overlay image */
  JTextField ol_tf;
  /** Sets whether the background image should be tiled to fill the slider background size */
  JRadioButton bgt_rb;
  /** Sets whether the background image should be stretched to fill the slider background size */
  JRadioButton bgs_rb;
  /** Sets whether the middle image should be tiled to fill the slider background size */
  JRadioButton mdt_rb;
  /** Sets whether the middle image should be stretched to fill the slider background size */
  JRadioButton mds_rb;  
  JButton bg_btn, e1_btn, md_btn, e2_btn, ol_btn;  
  
  JLabel bg_l, e1_l, md_l, e2_l, ol_l;
  JPanel bg_p, e1_p, md_p, e2_p, ol_p;
  
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
  ImageIcon bg_h = createIcon("icons/sbg_bg_h.png");
  ImageIcon bg_v = createIcon("icons/sbg_bg_v.png");
  ImageIcon left = createIcon("icons/sbg_left.png");
  ImageIcon right = createIcon("icons/sbg_right.png");
  ImageIcon top = createIcon("icons/sbg_top.png");
  ImageIcon bottom = createIcon("icons/sbg_bottom.png");
  ImageIcon middle_h = createIcon("icons/sbg_middle_h.png");
  ImageIcon middle_v = createIcon("icons/sbg_middle_v.png");
  ImageIcon tile = createIcon("icons/sbg_tile.png");
  ImageIcon stretch = createIcon("icons/sbg_stretch.png");
  
  /**
   * Creates a new Slider Background Generator
   * @param sbg_ The SliderBackground object into which the generated slider background should be stored.
   * @param s_ The Skin in which the slider background will be used.
   */
  public SliderBGGen(SliderBackground sbg_, Skin s_) {    
    sbg=sbg_;
    s=s_;
    setIconImage(editor_icon.getImage());
    setTitle(Language.get("SBGGEN_TITLE"));    
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
    
    String header_str = "<html><body style=\"background:"+header_bgh+";color:#FFFFFF;width:540px;\"><h1>&nbsp;"+Language.get("SBGGEN_TITLE")+"</h1></body></html>";
    
    card_step1 = new JPanel(null);    
    JLabel header1_l = new JLabel(header_str);     
    header1_l.setBounds(0,0,540,50);
    card_step1.add(header1_l); 
    
    JLabel welcome_l = new JLabel("<html><div style=\"width:400px;margin-top:2px\">"+Language.get("SBGGEN_DESCR")+"<br>"
                                  + Language.get("SBGGEN_FIRST")+"</div></html>");
    card_step1.add(welcome_l);    
    welcome_l.setBounds(5,30,630,100);
    ltr_rb = new JRadioButton("");    
    card_step1.add(ltr_rb);
    ltr_rb.setBounds(5,125,20,60);
    ltr_rb.setSelected(true);
    JLabel ltr_l = new JLabel(ltr_icon);
    ltr_l.setLabelFor(ltr_rb);
    card_step1.add(ltr_l);
    ltr_l.setBounds(30,125,100,60);
    
    btt_rb = new JRadioButton("");
    card_step1.add(btt_rb);
    btt_rb.setBounds(255,125,20,60);
    JLabel btt_l = new JLabel(btt_icon);
    btt_l.setLabelFor(btt_rb);
    card_step1.add(btt_l);
    btt_l.setBounds(280,105,100,100);
    
    btt_rb.setEnabled(false);
    btt_l.setEnabled(false);
    
    ButtonGroup dir_bg = new ButtonGroup();
    dir_bg.add(ltr_rb);
    ltr_rb.addActionListener(this);
    dir_bg.add(btt_rb);
    btt_rb.addActionListener(this);
    
    JSeparator horz2_s = new JSeparator(JSeparator.HORIZONTAL);
    card_step1.add(horz2_s);
    horz2_s.setBounds(5,215,525,215);    
    
    JLabel width_l = new JLabel(Language.get("SBGGEN_WIDTH"),width_i,JLabel.TRAILING);
    width_l.setHorizontalAlignment(JLabel.LEFT);
    width_tf = new JTextField();    
    width_tf.setDocument(new NumbersOnlyDocument(false));
    width_l.setLabelFor(width_tf);
    
    card_step1.add(width_l);
    width_l.setBounds(5,230,80,25);
    card_step1.add(width_tf);
    width_tf.setBounds(90,230,50,25);    
    
    JLabel height_l = new JLabel(Language.get("SBGGEN_HEIGHT"),height_i,JLabel.TRAILING);
    height_l.setHorizontalAlignment(JLabel.LEFT);
    height_tf = new JTextField();
    height_tf.setDocument(new NumbersOnlyDocument(false));
    height_l.setLabelFor(height_tf);
    
    card_step1.add(height_l);
    height_l.setBounds(5,265,80,25);
    card_step1.add(height_tf);
    height_tf.setBounds(90,265,50,25);
    
    JLabel margin_l_l = new JLabel(Language.get("SBGGEN_LEFT_M"), margin_l_i, JLabel.TRAILING);
    margin_l_l.setHorizontalAlignment(JLabel.LEFT);
    margin_l_tf = new JTextField();
    margin_l_tf.setDocument(new NumbersOnlyDocument(false));
    margin_l_l.setLabelFor(margin_l_tf);
    JLabel margin_r_l = new JLabel(Language.get("SBGGEN_RIGHT_M"), margin_r_i, JLabel.TRAILING);
    margin_r_l.setHorizontalAlignment(JLabel.LEFT);
    margin_r_tf = new JTextField();
    margin_r_tf.setDocument(new NumbersOnlyDocument(false));
    margin_r_l.setLabelFor(margin_r_tf);
    JLabel margin_t_l = new JLabel(Language.get("SBGGEN_TOP_M"), margin_t_i, JLabel.TRAILING);
    margin_t_l.setHorizontalAlignment(JLabel.LEFT);
    margin_t_tf = new JTextField();
    margin_t_tf.setDocument(new NumbersOnlyDocument(false));
    margin_t_l.setLabelFor(margin_t_tf);
    JLabel margin_b_l = new JLabel(Language.get("SBGGEN_BTM_M"), margin_b_i, JLabel.TRAILING);
    margin_b_l.setHorizontalAlignment(JLabel.LEFT);
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
    
    JSeparator separator2 = new JSeparator(JSeparator.HORIZONTAL);
    JLabel info_l = new JLabel("<html>"+Language.get("SBGGEN_SECOND")+"<br>"+Language.get("SBGGEN_NOTE")+"</html>");
    
    card_step1.add(separator2);
    card_step1.add(info_l);
    
    separator2.setBounds(5,300,525,5);
    info_l.setBounds(5,310,525,80);
    
    step1_horz_s = new JSeparator(JSeparator.HORIZONTAL);   
    step1_next_btn = new JButton(Language.get("SBGGEN_NEXT"));
    step1_next_btn.addActionListener(this);
    step1_cancel_btn = new JButton(Language.get("SBGGEN_CANCEL"));
    step1_cancel_btn.setActionCommand("cancel");
    step1_cancel_btn.addActionListener(this);
    
    card_step1.add(step1_horz_s);
    card_step1.add(step1_next_btn);
    card_step1.add(step1_cancel_btn);
    
    step1_horz_s.setBounds(5,390,525,5);    
    step1_next_btn.setBounds(370, 395,75,25);
    step1_cancel_btn.setBounds(450,395,75,25);    
    
    card_step1.setSize(540,400);   
    
    add(card_step1,STEP1);   
    
    card_step2 = new JPanel(null);
    JLabel header2_l = new JLabel(header_str);     
    header2_l.setBounds(0,0,540,50);
    card_step2.add(header2_l); 
    
    bg_p = new JPanel(null);
    bg_l = new JLabel(Language.get("SBGGEN_IMAGE"),bg_h,JLabel.TRAILING);
    bg_tf = new JTextField("");
    bg_btn = new JButton(Language.get("SBGGEN_OPEN"));
    bg_btn.addActionListener(this);
    JLabel bg_rs_l = new JLabel(Language.get("SBGGEN_RESIZE"),JLabel.TRAILING);
    ButtonGroup bg_rs_g = new ButtonGroup();
    bgt_rb = new JRadioButton("",true);
    JLabel bgt_l = new JLabel(Language.get("SBGGEN_TILE"),tile,JLabel.TRAILING);
    bgt_l.setHorizontalAlignment(JLabel.LEFT);
    bg_rs_g.add(bgt_rb);
    bgs_rb = new JRadioButton("",false);
    JLabel bgs_l = new JLabel(Language.get("SBGGEN_STRETCH"),stretch,JLabel.TRAILING);
    bgs_l.setHorizontalAlignment(JLabel.LEFT);
    bg_rs_g.add(bgs_rb);
    bg_p.add(bg_l);
    bg_p.add(bg_tf);
    bg_p.add(bg_btn);    
    bg_p.add(bg_rs_l);
    bg_p.add(bgt_rb);
    bg_p.add(bgt_l);
    bg_p.add(bgs_rb);
    bg_p.add(bgs_l);    
    bg_l.setBounds(10,20,90,25);
    bg_tf.setBounds(105,20,300,25);
    bg_btn.setBounds(410,20,100,25);
    bg_rs_l.setBounds(0,50,100,25);
    bgt_rb.setBounds(105,50,25,25);
    bgt_l.setBounds(130,50,100,25);
    bgs_rb.setBounds(210,50,25,25);
    bgs_l.setBounds(240,50,100,25);    
    bg_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_BG")));    
    card_step2.add(bg_p);
    bg_p.setBounds(5,50,520,85);    
    
    e1_p = new JPanel(null);
    e1_l = new JLabel(Language.get("SBGGEN_IMAGE"),left,JLabel.TRAILING);
    e1_tf = new JTextField();
    e1_btn = new JButton(Language.get("SBGGEN_OPEN"));
    e1_btn.addActionListener(this);
    e1_p.add(e1_l);
    e1_p.add(e1_tf);
    e1_p.add(e1_btn);
    e1_l.setBounds(0,20,90,25);
    e1_tf.setBounds(105,20,300,25);
    e1_btn.setBounds(410,20,100,25);
    e1_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_LEFT_E")));    
    card_step2.add(e1_p);
    e1_p.setBounds(5,135,520,55);
    
    md_p = new JPanel(null);
    md_l = new JLabel(Language.get("SBGGEN_IMAGE"),middle_h,JLabel.TRAILING);
    md_tf = new JTextField("");
    md_btn = new JButton(Language.get("SBGGEN_OPEN"));
    md_btn.addActionListener(this);
    JLabel md_rs_l = new JLabel(Language.get("SBGGEN_RESIZE"),JLabel.TRAILING);
    ButtonGroup md_rs_g = new ButtonGroup();
    mdt_rb = new JRadioButton("",true);
    JLabel mdt_l = new JLabel(Language.get("SBGGEN_TILE"),tile,JLabel.TRAILING);
    mdt_l.setHorizontalAlignment(JLabel.LEFT);
    md_rs_g.add(mdt_rb);
    mds_rb = new JRadioButton("",false);
    JLabel mds_l = new JLabel(Language.get("SBGGEN_STRETCH"),stretch,JLabel.TRAILING);
    mds_l.setHorizontalAlignment(JLabel.LEFT);
    md_rs_g.add(mds_rb);
    md_p.add(md_l);
    md_p.add(md_tf);
    md_p.add(md_btn);    
    md_p.add(md_rs_l);
    md_p.add(mdt_rb);
    md_p.add(mdt_l);
    md_p.add(mds_rb);
    md_p.add(mds_l);    
    md_l.setBounds(0,20,90,25);
    md_tf.setBounds(105,20,300,25);
    md_btn.setBounds(410,20,100,25);
    md_rs_l.setBounds(0,50,100,25);
    mdt_rb.setBounds(105,50,25,25);
    mdt_l.setBounds(130,50,100,25);
    mds_rb.setBounds(210,50,25,25);
    mds_l.setBounds(240,50,100,25);    
    md_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_MIDDLE")));    
    card_step2.add(md_p);
    md_p.setBounds(5,190,520,85);    
    
    e2_p = new JPanel(null);
    e2_l = new JLabel(Language.get("SBGGEN_IMAGE"),right,JLabel.TRAILING);
    e2_tf = new JTextField();
    e2_btn = new JButton(Language.get("SBGGEN_OPEN"));
    e2_btn.addActionListener(this);
    e2_p.add(e2_l);
    e2_p.add(e2_tf);
    e2_p.add(e2_btn);
    e2_l.setBounds(0,20,90,25);
    e2_tf.setBounds(105,20,300,25);
    e2_btn.setBounds(410,20,100,25);
    e2_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_RIGHT_E")));    
    card_step2.add(e2_p);
    e2_p.setBounds(5,275,520,55);
    
    ol_p = new JPanel(null);
    ol_l = new JLabel(Language.get("SBGGEN_IMAGE"),JLabel.TRAILING);
    ol_tf = new JTextField();
    ol_btn = new JButton(Language.get("SBGGEN_OPEN"));
    ol_btn.addActionListener(this);
    ol_p.add(ol_l);
    ol_p.add(ol_tf);
    ol_p.add(ol_btn);
    ol_l.setBounds(0,20,90,25);
    ol_tf.setBounds(105,20,300,25);
    ol_btn.setBounds(410,20,100,25);
    ol_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_OVERLAY")));    
    card_step2.add(ol_p);
    ol_p.setBounds(5,330,520,55);    
    
    JSeparator step2_horz_s = new JSeparator(JSeparator.HORIZONTAL);
    step2_prev_btn = new JButton(Language.get("SBGGEN_PREV"));
    step2_prev_btn.addActionListener(this);
    step2_finish_btn = new JButton(Language.get("SBGGEN_FINISH"));
    step2_finish_btn.addActionListener(this);
    step2_cancel_btn = new JButton(Language.get("SBGGEN_CANCEL"));
    step2_cancel_btn.setActionCommand("cancel");
    step2_cancel_btn.addActionListener(this);    
    
    card_step2.add(step2_horz_s);
    card_step2.add(step2_prev_btn);
    card_step2.add(step2_finish_btn);
    card_step2.add(step2_cancel_btn);
    
    step2_horz_s.setBounds(5,390,525,5);    
    step2_prev_btn.setBounds(275, 395,90,25);
    step2_finish_btn.setBounds(370,395,75,25);
    step2_cancel_btn.setBounds(450,395,75,25);    
  
    add(card_step2,STEP2);    
    
    setSize(540,450);
    setResizable(false);
  }
  public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals("cancel")) {
      setVisible(false);
      dispose();
    }
    else if (e.getSource().equals(step1_next_btn)) {
      layout.show(getContentPane(),STEP2);
    }
    else if (e.getSource().equals(step2_prev_btn)) {
      layout.show(getContentPane(),STEP1);
    }
    else if (e.getSource().equals(step2_finish_btn)) {
      File f = new File(s.skinfolder+sbg.id_tf.getText()+".png");
      int i = 1;
      while(f.exists()) {
          f = new File(s.skinfolder+sbg.id_tf.getText()+"_"+String.valueOf(i)+".png");
          i++;
      }
      SliderBGBuilder sbgb = new SliderBGBuilder(this);
      if(sbgb.cancontinue) {
          sbgb.build();
          sbgb.save(f);
          Bitmap b = new Bitmap(s, f);
          s.resources.add(b);
          sbg.image_tf.setText(b.id);
          sbg.nbvert_tf.setText(String.valueOf(sbgb.nbframes));
          sbg.nbhoriz_tf.setText("1");
          sbg.padhoriz_tf.setText("0");
          sbg.padvert_tf.setText("0");          
          setVisible(false);
          dispose();
      }      
    }
    else if(e.getSource().equals(ltr_rb)) {
        bg_l.setIcon(bg_h);
        e1_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_LEFT_E")));
        e1_l.setIcon(left);
        md_l.setIcon(middle_h);
        e2_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_RIGHT_E")));
        e2_l.setIcon(right);
    }
    else if(e.getSource().equals(btt_rb)) {
        bg_l.setIcon(bg_v);
        e1_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_TOP_E")));
        e1_l.setIcon(top);
        md_l.setIcon(middle_v);
        e2_p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Language.get("SBGGEN_BTM_E")));
        e2_l.setIcon(bottom);
    }
    else if(e.getSource().equals(bg_btn)) {
        JFileChooser fc = new JFileChooser(new File(s.skinfolder));
        int i = fc.showOpenDialog(this);
        if(i==JFileChooser.APPROVE_OPTION && fc.getSelectedFile().exists()) {
            bg_tf.setText(fc.getSelectedFile().getPath());
        }        
    }
    else if(e.getSource().equals(e1_btn)) {
        JFileChooser fc = new JFileChooser(new File(s.skinfolder));
        int i = fc.showOpenDialog(this);
        if(i==JFileChooser.APPROVE_OPTION && fc.getSelectedFile().exists()) {
            e1_tf.setText(fc.getSelectedFile().getPath());
        }        
    }
    else if(e.getSource().equals(md_btn)) {
        JFileChooser fc = new JFileChooser(new File(s.skinfolder));
        int i = fc.showOpenDialog(this);
        if(i==JFileChooser.APPROVE_OPTION && fc.getSelectedFile().exists()) {
            md_tf.setText(fc.getSelectedFile().getPath());
        }        
    }
    else if(e.getSource().equals(e2_btn)) {
        JFileChooser fc = new JFileChooser(new File(s.skinfolder));
        int i = fc.showOpenDialog(this);
        if(i==JFileChooser.APPROVE_OPTION && fc.getSelectedFile().exists()) {
            e2_tf.setText(fc.getSelectedFile().getPath());
        }        
    }
    else if(e.getSource().equals(ol_btn)) {
        JFileChooser fc = new JFileChooser(new File(s.skinfolder));
        int i = fc.showOpenDialog(this);
        if(i==JFileChooser.APPROVE_OPTION && fc.getSelectedFile().exists()) {
            ol_tf.setText(fc.getSelectedFile().getPath());
        }        
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
    Config.load();
    Language.load(new java.io.File("lang"+java.io.File.separator+Config.get("language")+".txt"));
    
    try {	
      String laf = Config.get("swing.laf");
      String lafClass = laf;
      if(laf!=null) {
        if(laf.equals("System")) {
          lafClass = UIManager.getSystemLookAndFeelClassName();          
        }
        if(laf.equals("Metal: Steel")) {
          lafClass = UIManager.getCrossPlatformLookAndFeelClassName();
          javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
        } else if(laf.equals("Metal: Ocean")) {
          lafClass = UIManager.getCrossPlatformLookAndFeelClassName();
          javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.OceanTheme());
        }
      }      
      UIManager.setLookAndFeel(lafClass);
    } 
    catch (Exception ex) {
      ex.printStackTrace();
    }
    JFrame.setDefaultLookAndFeelDecorated(true);
    SliderBGGen sbgg = new SliderBGGen(null,null);
    sbgg.setVisible(true);
    sbgg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
