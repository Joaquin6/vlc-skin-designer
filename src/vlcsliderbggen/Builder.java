/*  
This file is part of the VLC Slider Background Generator.

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
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;
      
public class Builder {
  Main main;
  Image bg,left,middle,right,overlay;
  int width, height, margin_top, margin_bottom, margin_left, margin_right, overlay_x, overlay_y;
  int nbframes, total_height;
  boolean usebg, useleft, useright, useoverlay, tile_bg, tile_middle, horizontal;
  int bg_width, left_width, middle_width, right_width, left_height, right_height, middle_height, bg_height;
  BufferedImage output;
  JFrame frame;
  JProgressBar progress;
 
  public Builder(Main main_) {    
    main = main_;   
    usebg = true;
    try {
       bg = ImageIO.read(main.img_bg);
       bg_width = bg.getWidth(main);
       bg_height = bg.getHeight(main);
    }
    catch (Exception e) {
      usebg = false;
    }
    useleft = true;
    try {
       left = ImageIO.read(main.img_left);
       left_width = left.getWidth(main);
       left_height = left.getHeight(main);
    }
    catch (Exception e) {
      useleft = false;
    }
    try {
      middle = ImageIO.read(main.img_middle);
      middle_width = middle.getWidth(main);
      middle_height = middle.getHeight(main);
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(main,"Could not load middle image!\n"+e.toString());
    }
    useright = true;
    try {
       right = ImageIO.read(main.img_right);
       right_width = right.getWidth(main);
       right_height = right.getHeight(main);
    }
    catch (Exception e) {
      useright = false;
    }
    useoverlay = true;
    try {
       overlay = ImageIO.read(main.img_overlay);       
    }
    catch (Exception e) {
      useoverlay = false;
    }
    if(useleft=false) {
      left = middle;      
    }
    if(useright=false) {
      right = middle;      
    }
    width = Integer.parseInt(main.size_w_text.getText());
    height = Integer.parseInt(main.size_h_text.getText());
    margin_top = Integer.parseInt(main.margin_t_text.getText());
    margin_bottom = Integer.parseInt(main.margin_b_text.getText());
    margin_left = Integer.parseInt(main.margin_l_text.getText());
    margin_right = Integer.parseInt(main.margin_r_text.getText());
    overlay_x = Integer.parseInt(main.overlay_x_text.getText());
    overlay_y = Integer.parseInt(main.overlay_y_text.getText());
    tile_bg = main.img_bg_tile.isSelected();
    tile_middle = main.img_bg_tile.isSelected();
    horizontal = main.horz_button.isSelected();   
        
    nbframes = width-margin_left-margin_right+1;
    total_height = nbframes*height;
    
    output = new BufferedImage(width,total_height,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = output.createGraphics();
    
    if (horizontal) {
      for (int i=0;i<=nbframes;i++) {
        System.out.println("Processing frame "+i+" of "+nbframes+"...");
        if(usebg) {
          if(tile_bg) {
              int bg_r = (int)(width/bg_width);
              int bg_d = width % bg_width;
              for (int x=0;x<bg_r;x++) {
                //System.out.println("Drawing BG-Tile #"+x);
                g.drawImage(bg,x*bg_width,height*i,main);            
              }
              g.drawImage(bg,bg_r*bg_width,height*i,bg_d,height,main);
          }    
          else {
            g.drawImage(bg,0,height*i,width,height,main);
          }  
        }      
        if (i>0) {                
          if(i<left_width) {          
            g.drawImage(left,margin_left,height*i+margin_top,i,height-margin_top-margin_bottom,main);
          }
          else if (i<left_width+middle_width) {          
            g.drawImage(left,margin_left,height*i+margin_top,main);
            g.drawImage(middle,margin_left+left_width,height*i+margin_top,i-left_width,height-margin_top-margin_bottom,main);
          }
          else if(i<left_width+middle_width+right_width) {          
            g.drawImage(left,margin_left,height*i+margin_top,main);
            g.drawImage(middle,margin_left+left_width,height*i+margin_top,main);
            g.drawImage(right,margin_left+left_width+middle_width,height*i+margin_top,i-left_width-middle_width,height-margin_top-margin_bottom,main);
          }
          else {          
            g.drawImage(left,margin_left,height*i+margin_top,main);
            if (tile_middle) {
              int gap = i-left_width-right_width;
              int middle_r = gap/middle_width;
              int middle_d = gap % middle_width;
              for (int x=0;x<middle_r;x++) {
                g.drawImage(middle,margin_left+left_width+x*middle_width,height*i+margin_top,main);            
              }
              g.drawImage(middle, margin_left+left_width+middle_r*middle_width, height*i+margin_top, middle_d, height-margin_top-margin_bottom, main);            
            }
            else {
              int gap = i-left_width-right_width;
              g.drawImage(middle,margin_left+left_width,height*i+margin_top,gap,height-margin_top-margin_bottom,main);
            }
            g.drawImage(right,margin_left+i-right_width,height*i+margin_top,main);
          }
        }      
        if(useoverlay) {
          g.drawImage(overlay,overlay_x,overlay_y+height*i,main);
        }
      } 
    }
  }
  public void save(File f) {
    try {
      ImageIO.write(output, "png", f);
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(main,"Could not save image!\n"+e.toString());
      return;
    }
    JOptionPane.showMessageDialog(main,"Image saved!\nnbhoriz: 0\nnbvert: "+nbframes+"\npadhorz:0\npadvert:0");
  }
  
}
