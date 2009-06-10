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
  boolean usebg, usee1, usee2, useoverlay, tile_bg, tile_middle, horizontal;
  int bg_width = 0, e1_width = 0, middle_width = 0, e2_width = 0, e1_height = 0, e2_height = 0, middle_height = 0, bg_height = 0;
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
    System.out.println("tile_bg: "+tile_bg);
    System.out.println("tile_middle: "+tile_middle);
  }

  public void build() {    
    if (horizontal && cancontinue) {
      //Calculate the number of frames
      nbframes = width-margin_left-margin_right+1;
      //Calculate the result image's height
      int total_height = nbframes*height;
      //Create the result image
      output = new BufferedImage(width,total_height,BufferedImage.TYPE_INT_ARGB);
      //Create the graphics surface to draw on
      Graphics2D g = output.createGraphics();
      //Show the progress window
      pg_win = new ProgressWindow(sbgg, Language.get("SBGGEN_PROGRESS"));
      for (int i=0;i<=nbframes;i++) { //One increment is one pixel column drawn more, thus i = slider filling width
        System.out.println("Processing frame "+i+" of "+nbframes+"...");
        //Update progress window
        pg_win.setProgress(nbframes/100*i);
        if(usebg) { //If a background is used
          if(tile_bg) { //If the background is to be tiled
              int bg_num = (int)(width/bg_width); //Number of full images fitting into the slider area
              int bg_rest_width = width % bg_width; //Pixels that have to be filled with part of the background
              for (int x=0;x<bg_num;x++) {
                //System.out.println("Drawing BG-Tile #"+x);
                g.drawImage(bg,x*bg_width,height*i,sbgg);            
              }
              g.drawImage(bg.getSubimage(0, 0, bg_rest_width, height),bg_num*bg_width,height*i,sbgg);
          }    
          else {
            //If background resizing is set to stretch, do so
            g.drawImage(bg,0,height*i,width,height,sbgg);
          }  
        }      
        if (i>0) {
          if(i<=e1_width) { 
            //If the right edge image's width has not yet been fully reached
            //Say the edge looks like <000, then at this point only e.g. <00 is drawn
            g.drawImage(e1.getSubimage(0, 0, i, e1_height), margin_left, height*i+margin_top, sbgg);
          }
          else if (i<=e1_width+middle_width) { 
            //If the end of the middle image has not yet been reached
            //Say right edge + middle image look like <000===, then now only e.g. <000== is drawn
            if(e1!=null) g.drawImage(e1,margin_left,height*i+margin_top,sbgg); //Draw the full right edge
            if(!tile_middle) {
              //If the middle image is to be stretched
              g.drawImage(middle,margin_left+e1_width,height*i+margin_top,i-e1_width,height-margin_top-margin_bottom,sbgg);
            }
            else {
              //If the middle image is to be tiled, draw a subportion of it
              g.drawImage(middle.getSubimage(0, 0, i-e1_width,height-margin_top-margin_bottom),
                      margin_left+e1_width, height*i+margin_top, sbgg);
            }
          }
          else if(i<=e1_width+middle_width+e2_width) { 
            //If the width of the full slider (right edge + middle + left edge) hast not yet been fully drawn
            //Say the full slider looks like this: <000===000>
            //Then now only something like this is drawn: <000===00
            g.drawImage(e1,margin_left,height*i+margin_top,sbgg);
            g.drawImage(middle,margin_left+e1_width,height*i+margin_top,sbgg);
            g.drawImage(e2.getSubimage(0, 0, i-e1_width-middle_width, height-margin_top-margin_bottom),
                    margin_left+e1_width+middle_width, height*i+margin_top, sbgg);
          }
          else {
            //Now the slider frames can be drawn normally, full width of right edge + middle + left edge has been reached
            //Now only the middle image needs to be resized/tiled, both edges are drawn fully
            //Say the right edge looks like this: <000 the left like this: 000> and the midle like this ===
            //Then now is drawn something like <000=========000>
            g.drawImage(e1,margin_left,height*i+margin_top,sbgg); //Draw the full right edge
            if (tile_middle) {
              //If the middle image has to be tiled;
              int gap = i-e1_width-e2_width; //The width of the space between right and left edge, that has to be filled by the middle image
              int middle_num = gap/middle_width; //Number of full middle images fitting into the gap
              int middle_rest_width = gap % middle_width; //Rest pixels that need to be filled with the middle image
              for (int x=0;x<middle_num;x++) { //Draw the full middle images
                g.drawImage(middle,margin_left+e1_width+x*middle_width,height*i+margin_top,sbgg);
              }
              //Draw the rest
              g.drawImage(middle.getSubimage(0, 0, middle_rest_width, height-margin_top-margin_bottom),
                      margin_left+e1_width+middle_num*middle_width, height*i+margin_top, sbgg);
              }
            else {
              //If the middle image is to be stretched
              int gap = i-e1_width-e2_width; //The width of the space between right and left edge, that has to be filled by the middle image
              g.drawImage(middle,margin_left+e1_width,height*i+margin_top,gap,height-margin_top-margin_bottom,sbgg);
            }
            g.drawImage(e2,margin_left+i-e2_width,height*i+margin_top,sbgg); //Draw the full left edge
          }
        }      
        if(useoverlay) {
          //Draw the overlay image. It is not resized to fit the slider.
          g.drawImage(overlay,overlay_x,overlay_y+height*i,sbgg);
        }
      } 
    }
    else if(!horizontal && cancontinue) {
      //Calculate the number of frames
      nbframes = height-margin_top-margin_bottom+1;
      //Calculate the result image's width
      int total_width = nbframes*width;
      //Create the result image
      output = new BufferedImage(total_width,height,BufferedImage.TYPE_INT_ARGB);
      //Create the graphics surface to draw on
      Graphics2D g = output.createGraphics();
      //Show the progress window
      pg_win = new ProgressWindow(sbgg, Language.get("SBGGEN_PROGRESS"));
       for (int i=0;i<=nbframes;i++) { //One increment is one pixel row drawn more, thus i = slider filling height
        System.out.println("Processing frame "+i+" of "+nbframes+"...");
        //Update progress window
        pg_win.setProgress(nbframes/100*i);
        if(usebg) { //If a background is used
          if(tile_bg) { //If the background is to be tiled
              int bg_num = (int)(height/bg_height); //Number of full images fitting into the slider area
              int bg_rest_height = height % bg_height; //Pixels that have to be filled with part of the background
              for (int y=0;y<bg_num;y++) {
                //System.out.println("Drawing BG-Tile #"+x);
                g.drawImage(bg,width*i,bg_height*y,sbgg);
              }
              g.drawImage(bg.getSubimage(0, bg_height-bg_rest_height, bg_width, bg_rest_height),width*i,bg_height*bg_num,sbgg);
          }
          else {
            //If background resizing is set to stretch, do so
            g.drawImage(bg,0,height*i,width,height,sbgg);
          }
        }
        if (i>0) {
          if(i<=e2_height) { //If the bottom edge image's height has not yet been fully reached
            //Draw a subimage of the bottom edge's image
            g.drawImage(e2.getSubimage(0, e2_height-i, e2_width, i), width*i+margin_right, height-margin_bottom-i, sbgg);
          }
          else if (i<=e2_height+middle_height) { //If the end of the middle image has not yet been reached
            if(e2!=null) g.drawImage(e2, width*i+margin_left, height-margin_bottom-e2_height,sbgg); //Draw the full bottom edge
            if(!tile_middle) {
              //If the middle image is to be stretched
              g.drawImage(middle, margin_left+width*i, height-margin_bottom-i, middle_width, i-e1_height,sbgg);
            }
            else {
              //If the middle image is to be tiled, draw a subportion of it
              g.drawImage(middle.getSubimage(0, middle_height-(i-e1_height), middle_width, i-e1_height), margin_left+width*i, height-margin_bottom-i, sbgg);
            }
          }
          else if(i<=e2_height+middle_height+e1_height) { //If the height of the full slider (bottom edge + middle + tpü edge) hast not yet been fully drawn
            if(e2!=null) g.drawImage(e2, width*i+margin_left, height-margin_bottom-e2_height,sbgg); //Draw the full bottom edge
            g.drawImage(middle, width*i+margin_left, height-margin_bottom-middle_height-e2_height, sbgg); //Draw the full middle image
            g.drawImage(e1.getSubimage(0, e1_height-(i-e2_height-middle_height), e1_width, i-e2_height-middle_height),
                    margin_left+width*i, height-margin_bottom-i, sbgg); //Draw a part of the top edge
          }
          else { //Now the slider frames can be drawn normally, full height of right edge + middle + left edge has been reached
            if(e2!=null) g.drawImage(e2, width*i+margin_left, height-margin_bottom-e2_height,sbgg); //Draw the full bottom edge
            if (tile_middle) {
              //If the middle image has to be tiled;
              int gap = i-e1_height-e2_height; //The height of the space between top and bottom edge, that has to be filled by the middle image
              int middle_num = gap/middle_height; //Number of full middle images fitting into the gap
              int middle_rest_height = gap % middle_height; //Rest pixels that need to be filled with the middle image
              for (int y=0;y<middle_num;y++) { //Draw the full middle images
                g.drawImage(middle, margin_left+width*i, height-margin_bottom-e2_height-(y+1)*middle_height, sbgg);
              }
              //Draw the rest
              if(middle_rest_height>0) g.drawImage(middle.getSubimage(0, 0, middle_width, middle_rest_height),
                      margin_left+width*i, height-margin_bottom-e2_height-middle_num*middle_height-middle_rest_height,
                      middle_width, middle_rest_height, sbgg);
              }
            else {
              //If the middle image is to be stretched
              int gap = i-e1_height-e2_height; //The height of the space between top and bottom edge, that has to be filled by the middle image
              g.drawImage(middle,margin_left+width*i, height-i+e1_height ,middle_width, gap, sbgg);
            }
            g.drawImage(e1, width*i+margin_left, height-(i+1), sbgg); //Draw the full top edge
          }
        }
        if(useoverlay) {
          //Draw the overlay image. It is not resized to fit the slider.
          g.drawImage(overlay,overlay_x+width*i,overlay_y,sbgg);
        }
      }
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
