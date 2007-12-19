/*****************************************************************************
 * PanelEditEvent.java
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

import vlcskineditor.Item;
import vlcskineditor.items.Panel;

/**
 * Represents the action of editing a Panel
 * @author Daniel Dreibrodt
 */
public class PanelEditEvent extends HistoryEvent{
  
  Panel p;
  private String id_old, id_new, visible_old, visible_new, lefttop_old, lefttop_new;
  private String rightbottom_old, rightbottom_new, help_old, help_new;
  private boolean xkeepratio_old, xkeepratio_new, ykeepratio_old, ykeepratio_new;
  private int x_old, x_new, y_old, y_new;
  private int width_old, width_new, height_old, height_new;
  
  public PanelEditEvent(Panel pan) {
    p = pan;
    
    id_old = p.id;
    visible_old = p.visible;
    lefttop_old = p.lefttop;
    rightbottom_old = p.rightbottom;
    xkeepratio_old = p.xkeepratio;
    ykeepratio_old = p.ykeepratio;
    x_old = p.x;
    y_old = p.y;
    help_old = p.help;
    
    width_old = p.width;
    height_old = p.height;
  }
  public void setNew() {
    id_new = p.id;
    visible_new = p.visible;
    lefttop_new = p.lefttop;
    rightbottom_new = p.rightbottom;
    xkeepratio_new = p.xkeepratio;
    ykeepratio_new = p.ykeepratio;
    x_new = p.x;
    y_new = p.y;
    help_new = p.help;
    
    width_new = p.width;
    height_new = p.height;
  }
  @Override
  public void undo() {
    p.id = id_old;
    p.visible = visible_old;
    p.lefttop = lefttop_old;
    p.rightbottom = rightbottom_old;
    p.xkeepratio = xkeepratio_old;
    p.ykeepratio = ykeepratio_old;
    p.x = x_old;
    p.y = y_old;
    p.help = help_old;
    
    p.width = width_old;
    p.height = height_old;
    
    for(Item i:p.items) {
      i.setOffset(x_old,y_old);
    }
  }
  @Override
  public void redo() {
    p.id = id_new;
    p.visible = visible_new;
    p.lefttop = lefttop_new;
    p.rightbottom = rightbottom_new;
    p.xkeepratio = xkeepratio_new;
    p.ykeepratio = ykeepratio_new;
    p.x = x_new;
    p.y = y_new;
    p.help = help_new;
    
    p.width = width_new;
    p.height = height_new;
    
    for(Item i:p.items) {
      i.setOffset(x_new,y_new);
    }
  }
  @Override
  public String getDescription() {
    return "Edit Panel";
  }
}
