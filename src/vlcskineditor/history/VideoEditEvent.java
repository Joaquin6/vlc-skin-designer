/*****************************************************************************
 * VideoEditEvent.java
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
import vlcskineditor.items.Video;

/**
 * Represents the action of editing a Video
 * @author Daniel Dreibrodt
 */
public class VideoEditEvent extends HistoryEvent {
  
  Video v;
  
  private String id_old, id_new, visible_old, visible_new, lefttop_old, lefttop_new;
  private String rightbottom_old, rightbottom_new, help_old, help_new;
  private boolean xkeepratio_old, xkeepratio_new, ykeepratio_old, ykeepratio_new;
  private int x_old, x_new, y_old, y_new;
  
  private int width_old, width_new, height_old, height_new;
  private boolean autoresize_old, autoresize_new;
  
  public VideoEditEvent(Video vid) {
    v = vid;
    
    id_old = v.id;
    visible_old = v.visible;
    lefttop_old = v.lefttop;
    rightbottom_old = v.rightbottom;
    xkeepratio_old = v.xkeepratio;
    ykeepratio_old = v.ykeepratio;
    x_old = v.x;
    y_old = v.y;
    help_old = v.help;
    
    width_old = v.width;
    height_old = v.height;
    autoresize_old = v.autoresize;
  }
  public void setNew() {
    id_new = v.id;
    visible_new = v.visible;
    lefttop_new = v.lefttop;
    rightbottom_new = v.rightbottom;
    xkeepratio_new = v.xkeepratio;
    ykeepratio_new = v.ykeepratio;
    x_new = v.x;
    y_new = v.y;
    help_new = v.help;
    
    width_new = v.width;
    height_new = v.height;
    autoresize_new = v.autoresize;
  }
  @Override
  public void undo() {
    v.id = id_old;
    v.visible = visible_old;
    v.lefttop = lefttop_old;
    v.rightbottom = rightbottom_old;
    v.xkeepratio = xkeepratio_old;
    v.ykeepratio = ykeepratio_old;
    v.x = x_old;
    v.y = y_old;
    v.help = help_old;
    
    v.width = width_old;
    v.height = height_old;
    v.autoresize = autoresize_old;
  }
  @Override
  public void redo() {
    v.id = id_new;
    v.visible = visible_new;
    v.lefttop = lefttop_new;
    v.rightbottom = rightbottom_new;
    v.xkeepratio = xkeepratio_new;
    v.ykeepratio = ykeepratio_new;
    v.x = x_new;
    v.y = y_new;
    v.help = help_new;
    
    v.width = width_new;
    v.height = height_new;
    v.autoresize = autoresize_new;
  }
  @Override
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",v.type);
  }
}
