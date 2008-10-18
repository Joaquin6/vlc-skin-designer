/*****************************************************************************
 * ImageEditEvent.java
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

import vlcskineditor.Language;
import vlcskineditor.items.Image;

/**
 * Represents the action of editing an Image
 * @author Daniel Dreibrodt
 */
public class ImageEditEvent extends HistoryEvent{
  
  private Image i;
  
  private String id_old, id_new, visible_old, visible_new, lefttop_old, lefttop_new;
  private String rightbottom_old, rightbottom_new, help_old, help_new;
  private boolean xkeepratio_old, xkeepratio_new, ykeepratio_old, ykeepratio_new;
  private int x_old, x_new, y_old, y_new;
  
  private String image_old, image_new, resize_old, resize_new;
  private String action_old, action_new, action2_old, action2_new;
  
  public ImageEditEvent(Image ima) {
    i = ima;
    
    id_old = i.id;
    visible_old = i.visible;
    lefttop_old = i.lefttop;
    rightbottom_old = i.rightbottom;
    xkeepratio_old = i.xkeepratio;
    ykeepratio_old = i.ykeepratio;
    x_old = i.x;
    y_old = i.y;
    help_old = i.help;
    
    image_old = i.image;
    resize_old = i.resize;
    action_old = i.action;
    action2_old = i.action2;
  }
  public void setNew() {
    id_new = i.id;
    visible_new = i.visible;
    lefttop_new = i.lefttop;
    rightbottom_new = i.rightbottom;
    xkeepratio_new = i.xkeepratio;
    ykeepratio_new = i.ykeepratio;
    x_new = i.x;
    y_new = i.y;
    help_new = i.help;
    
    image_new = i.image;
    resize_new = i.resize;
    action_new = i.action;
    action2_new = i.action2;
  }
  @Override
  public void undo() {
    i.id = id_old;
    i.visible = visible_old;
    i.lefttop = lefttop_old;
    i.rightbottom = rightbottom_old;
    i.xkeepratio = xkeepratio_old;
    i.ykeepratio = ykeepratio_old;
    i.x = x_old;
    i.y = y_old;
    i.help = help_old;
    
    i.image = image_old;
    i.resize = resize_old;
    i.action = action_old;
    i.action2 = action2_old;
  }
  @Override
  public void redo() {
    i.id = id_new;
    i.visible = visible_new;
    i.lefttop = lefttop_new;
    i.rightbottom = rightbottom_new;
    i.xkeepratio = xkeepratio_new;
    i.ykeepratio = ykeepratio_new;
    i.x = x_new;
    i.y = y_new;
    i.help = help_new;
    
    i.image = image_new;
    i.resize = resize_new;
    i.action = action_new;
    i.action2 = action2_new;
  }
  @Override
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",i.type);
  }
}
