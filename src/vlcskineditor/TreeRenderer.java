/*****************************************************************************
 * TreeRenderer.java
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

package vlcskineditor;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * Modifies the display of the resource/window/item tree
 * @author Daniel
 */
public class TreeRenderer extends DefaultTreeCellRenderer {
  
  ImageIcon bitmap_icon = createIcon("icons/bitmap.png");
  ImageIcon font_icon = createIcon("icons/font.png");
  ImageIcon bitmapfont_icon = createIcon("icons/bitmapfont.png");
  ImageIcon window_icon = createIcon("icons/window.png");
  ImageIcon layout_icon = createIcon("icons/layout.png");
  ImageIcon panel_icon = createIcon("icons/panel.png");
  ImageIcon group_icon = createIcon("icons/group.png");  
  ImageIcon playtree_icon = createIcon("icons/playtree.png");
  ImageIcon slider_icon = createIcon("icons/slider.png");
  ImageIcon sliderbackground_icon = createIcon("icons/sliderbackground.png");
  ImageIcon radialslider_icon = createIcon("icons/radialslider.png");
  ImageIcon image_icon = createIcon("icons/image.png");
  ImageIcon text_icon = createIcon("icons/text.png");
  ImageIcon video_icon = createIcon("icons/video.png");
  ImageIcon anchor_icon = createIcon("icons/anchor.png");
  ImageIcon button_icon = createIcon("icons/button.png");
  ImageIcon checkbox_icon = createIcon("icons/checkbox.png");
  
  /** Creates a new instance of TreeRenderer */
  public TreeRenderer() {
  }
  /** Returns how a given leaf should be rendered */
  @Override
  public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {    
    String name = value.toString();
    value = value.toString().substring(value.toString().indexOf(": ")+2);
    super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row,hasFocus);
    setIcon(getIcon(name));
    return this;
  }
  /**
   * Returns an icon for a leaf based on it's name
   * @param name  The name of the leaf
   * @return      Fitting ImageIcon
   */
  public ImageIcon getIcon(String name) {
    if(name.startsWith("Bitmap:")) return bitmap_icon; 
    if(name.startsWith("Root: "+Language.get("WIN_RES_BITMAPS"))) return bitmap_icon; 
    else if(name.startsWith("Font:")) return font_icon;
    else if(name.startsWith("Root: "+Language.get("WIN_RES_FONTS"))) return font_icon;
    else if(name.startsWith("Bitmapfont:")) return bitmapfont_icon;
    else if(name.startsWith("Window:")) return window_icon;    
    else if(name.startsWith("Layout:")) return layout_icon;
    else if(name.startsWith("Panel:")) return panel_icon;
    else if(name.startsWith("Group:")) return group_icon;
    else if(name.startsWith("Anchor:")) return anchor_icon;
    else if(name.startsWith("Button:")) return button_icon;
    else if(name.startsWith("Checkbox:")) return checkbox_icon;
    else if(name.startsWith("Image:")) return image_icon;    
    else if(name.startsWith("Playtree:")) return playtree_icon;
    else if(name.startsWith("Slider:")) return slider_icon;
    else if(name.startsWith("RadialSlider:")) return radialslider_icon;
    else if(name.startsWith("SliderBackground:")) return sliderbackground_icon;
    else if(name.startsWith("Video:")) return video_icon;
    else if(name.startsWith("Text:")) return text_icon;
    else return null;
    
  }
  /**
   * Creates an ImageIcon of an image included in the JAR
   * @param filename  The path to the image file inside the JAR
   * @return         An ImageIcon representing the given file
   */
  public ImageIcon createIcon(String filename) {
      Image img = null;
      try {
        img = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource(filename));        
        return new ImageIcon(img);  
      } catch (Exception e) {
        System.out.println(e);
        return null;
      }
  }

  
}
