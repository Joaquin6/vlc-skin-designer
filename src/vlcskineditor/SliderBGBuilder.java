/*****************************************************************************
 * SliderBGBuilder.java
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

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;
      
public class SliderBGBuilder {
  SliderBGGen sbgg;
  BufferedImage bg,e1,middle,e2,overlay;
  int width, height, margin_top, margin_bottom, margin_left, margin_right, overlay_x, overlay_y;
  public int nbframes;
  int total_height;
  boolean usebg, usee1, usee2, useoverlay, tile_bg, tile_middle, horizontal;
  int bg_width, e1_width, middle_width, e2_width, e1_height, e2_height, middle_height, bg_height;
  BufferedImage output;
  JFrame frame;
  ProgressWindow pg_win;
  public boolean cancontinue = true;;
 
  public SliderBGBuilder(SliderBGGen sbgg_) {    
    sbgg = sbgg_;   
    usebg = true;
    try {
       bg = ImageIO.read(new File(sbgg.bg_tf.getText()));
       bg_width = bg.getWidth(null);
       bg_height = bg.getHeight(null);
    }
    catch (Exception e) {
      usebg = false;
    }
    usee1 = true;
    try {
       e1 = ImageIO.read(new File(sbgg.e1_tf.getText()));
       e1_width = e1.getWidth(null);
       e1_height = e1.getHeight(null);
    }
    catch (Exception e) {
      usee1 = false;
    }
    try {
      middle = ImageIO.read(new File(sbgg.md_tf.getText()));
      middle_width = middle.getWidth(null);
      middle_height = middle.getHeight(null);
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(sbgg,Language.get("ERROR_SBGGEN_MIDDLE_MSG"),e.getLocalizedMessage(),JOptionPane.ERROR_MESSAGE);
      cancontinue=false;
    }
    usee2 = true;
    try {
       e2 = ImageIO.read(new File(sbgg.e2_tf.getText()));
       e2_width = e2.getWidth(null);
       e2_height = e2.getHeight(null);
    }
    catch (Exception e) {
      usee2 = false;
    }
    useoverlay = true;
    try {
       overlay = ImageIO.read(new File(sbgg.ol_tf.getText()));
    }
    catch (Exception e) {
      useoverlay = false;
    }    
    if(usee1=false) {
      e1 = middle;      
    }
    if(usee2=false) {
      e2 = middle;      
    }
    width = Integer.parseInt(sbgg.width_tf.getText());
    height = Integer.parseInt(sbgg.height_tf.getText());
    margin_top = Integer.parseInt(sbgg.margin_t_tf.getText());
    margin_bottom = Integer.parseInt(sbgg.margin_b_tf.getText());
    margin_left = Integer.parseInt(sbgg.margin_l_tf.getText());
    margin_right = Integer.parseInt(sbgg.margin_r_tf.getText());
    //overlay_x = Integer.parseInt(sbgg.overlay_x_tf.getText());
    //overlay_y = Integer.parseInt(sbgg.overlay_y_tf.getText());
    tile_bg = sbgg.bgt_rb.isSelected();
    tile_middle = sbgg.mdt_rb.isSelected();
    horizontal = sbgg.ltr_rb.isSelected();  
  }
  public void build() {    
    if (horizontal && cancontinue) {
      nbframes = width-margin_left-margin_right+1;
      total_height = nbframes*height;   
      output = new BufferedImage(width,total_height,BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = output.createGraphics();
      pg_win = new ProgressWindow(sbgg, Language.get("SBGGEN_PROGRESS"));
      for (int i=0;i<=nbframes;i++) {
        System.out.println("Processing frame "+i+" of "+nbframes+"...");
        pg_win.setProgress(nbframes/100*i);
        if(usebg) {
          if(tile_bg) {
              int bg_r = (int)(width/bg_width);
              int bg_d = width % bg_width;
              for (int x=0;x<bg_r;x++) {
                //System.out.println("Drawing BG-Tile #"+x);
                g.drawImage(bg,x*bg_width,height*i,sbgg);            
              }
              g.drawImage(bg,bg_r*bg_width,height*i,bg_d,height,sbgg);
          }    
          else {
            g.drawImage(bg,0,height*i,width,height,sbgg);
          }  
        }      
        if (i>0) {                
          if(i<e1_width) {
            BufferedImage tmp = e1.getSubimage(0, 0, i, e1_height);
            g.drawImage(tmp,margin_left,height*i+margin_top,i,height-margin_top-margin_bottom,sbgg);
          }
          else if (i<e1_width+middle_width) {          
            g.drawImage(e1,margin_left,height*i+margin_top,sbgg);
            g.drawImage(middle,margin_left+e1_width,height*i+margin_top,i-e1_width,height-margin_top-margin_bottom,sbgg);
          }
          else if(i<e1_width+middle_width+e2_width) {          
            g.drawImage(e1,margin_left,height*i+margin_top,sbgg);
            g.drawImage(middle,margin_left+e1_width,height*i+margin_top,sbgg);
            g.drawImage(e2,margin_left+e1_width+middle_width,height*i+margin_top,i-e1_width-middle_width,height-margin_top-margin_bottom,sbgg);
          }
          else {          
            g.drawImage(e1,margin_left,height*i+margin_top,sbgg);
            if (tile_middle) {
              int gap = i-e1_width-e2_width;
              int middle_r = gap/middle_width;
              int middle_d = gap % middle_width;
              for (int x=0;x<middle_r;x++) {
                g.drawImage(middle,margin_left+e1_width+x*middle_width,height*i+margin_top,sbgg);            
              }
              g.drawImage(middle, margin_left+e1_width+middle_r*middle_width, height*i+margin_top, middle_d, height-margin_top-margin_bottom, sbgg);            
            }
            else {
              int gap = i-e1_width-e2_width;
              g.drawImage(middle,margin_left+e1_width,height*i+margin_top,gap,height-margin_top-margin_bottom,sbgg);
            }
            g.drawImage(e2,margin_left+i-e2_width,height*i+margin_top,sbgg);
          }
        }      
        if(useoverlay) {
          g.drawImage(overlay,overlay_x,overlay_y+height*i,sbgg);
        }
      } 
    }
    else if(!horizontal && cancontinue) {
        JOptionPane.showMessageDialog(sbgg,"Sorry, generating of vertical sliders has not yet been implemented.");
    }
  }
  public void save(File f) {
    try {
      ImageIO.write(output, "png", f);
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(sbgg,Language.get("ERROR_SAVEPNG_MSG")+"\n"+e.toString(),Language.get("ERROR_SAVEPNG_TITLE"),JOptionPane.ERROR_MESSAGE);
      return;
    }
  }
  
}
