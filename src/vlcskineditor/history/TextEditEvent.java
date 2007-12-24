/*****************************************************************************
 * TextEditEvent.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 * 
 * This file is part of vlcskineditor.history
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

package vlcskineditor.history;

import vlcskineditor.items.Text;

/**
 * Represents the action of editing a Text item
 * @author Daniel Dreibrodt
 */
public class TextEditEvent extends HistoryEvent{

  Text t;
  
  private String id_old, id_new, visible_old, visible_new, lefttop_old, lefttop_new;
  private String rightbottom_old, rightbottom_new, help_old, help_new;
  private boolean xkeepratio_old, xkeepratio_new, ykeepratio_old, ykeepratio_new;
  private int x_old, x_new, y_old, y_new;
  
  private String font_old, font_new, text_old, text_new, color_old, color_new;
  private int width_old, width_new;
  private String alignment_old, alignment_new, scrolling_old, scrolling_new;
  
  public TextEditEvent(Text tex) {
    t = tex;
    
    id_old = t.id;
    visible_old = t.visible;
    lefttop_old = t.lefttop;
    rightbottom_old = t.rightbottom;
    xkeepratio_old = t.xkeepratio;
    ykeepratio_old = t.ykeepratio;
    x_old = t.x;
    y_old = t.y;
    help_old = t.help;
    
    font_old = t.font;
    text_old = t.text;
    color_old = t.color;
    width_old = t.width;
    alignment_old = t.alignment;
    scrolling_old = t.scrolling;
  }
  public void setNew() {
    id_new = t.id;
    visible_new = t.visible;
    lefttop_new = t.lefttop;
    rightbottom_new = t.rightbottom;
    xkeepratio_new = t.xkeepratio;
    ykeepratio_new = t.ykeepratio;
    x_new = t.x;
    y_new = t.y;
    help_new = t.help;
    
    font_new = t.font;
    text_new = t.text;
    color_new = t.color;
    width_new = t.width;
    alignment_new = t.alignment;
    scrolling_new = t.scrolling;
  }
  @Override
  public void undo() {
    t.id = id_old;
    t.visible = visible_old;
    t.lefttop = lefttop_old;
    t.rightbottom = rightbottom_old;
    t.xkeepratio = xkeepratio_old;
    t.ykeepratio = ykeepratio_old;
    t.x = x_old;
    t.y = y_old;
    t.help = help_old;
    
    t.font = font_old;
    t.text = text_old;
    t.color = color_old;
    t.width = width_old;
    t.alignment = alignment_old;
    t.scrolling = scrolling_old;
  }
  @Override
  public void redo() {
    t.id = id_new;
    t.visible = visible_new;
    t.lefttop = lefttop_new;
    t.rightbottom = rightbottom_new;
    t.xkeepratio = xkeepratio_new;
    t.ykeepratio = ykeepratio_new;
    t.x = x_new;
    t.y = y_new;
    t.help = help_new;
    
    t.font = font_new;
    t.text = text_new;
    t.color = color_new;
    t.width = width_new;
    t.alignment = alignment_new;
    t.scrolling = scrolling_new;
  }
  @Override
  public String getDescription() {
    return "Edit Text";
  }  
}
