/*****************************************************************************
 * SliderEditEvent.java
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
import vlcskineditor.items.Slider;

/**
 * Represents the action of editing a Slider
 * @author Daniel Dreibrodt
 */
public class SliderEditEvent extends HistoryEvent{
  
  Slider s;
  
  private String id_old, id_new, visible_old, visible_new, lefttop_old, lefttop_new;
  private String rightbottom_old, rightbottom_new, help_old, help_new;
  private boolean xkeepratio_old, xkeepratio_new, ykeepratio_old, ykeepratio_new;
  private int x_old, x_new, y_old, y_new;
  private String up_old, up_new, over_old, over_new, down_old, down_new;
  private String points_old, points_new, value_old, value_new, tooltiptext_old, tooltiptext_new;
  private int thickness_old, thickness_new;
  
  public SliderEditEvent(Slider sli) {
    s = sli;
    
    id_old = s.id;
    visible_old = s.visible;
    lefttop_old = s.lefttop;
    rightbottom_old = s.rightbottom;
    xkeepratio_old = s.xkeepratio;
    ykeepratio_old = s.ykeepratio;
    x_old = s.x;
    y_old = s.y;
    help_old = s.help;
    
    up_old = s.up;
    over_old = s.over;
    down_old = s.down;
    points_old = s.points;
    value_old = s.value;
    tooltiptext_old = s.tooltiptext;
    thickness_old = s.thickness;
  }
  public void setNew() {
    id_new = s.id;
    visible_new = s.visible;
    lefttop_new = s.lefttop;
    rightbottom_new = s.rightbottom;
    xkeepratio_new = s.xkeepratio;
    ykeepratio_new = s.ykeepratio;
    x_new = s.x;
    y_new = s.y;
    help_new = s.help;
    
    up_new = s.up;
    over_new = s.over;
    down_new = s.down;
    points_new = s.points;
    value_new = s.value;
    tooltiptext_new = s.tooltiptext;
    thickness_new = s.thickness;
  }
  @Override
  public void undo() {
    s.id = id_old;
    s.visible = visible_old;
    s.lefttop = lefttop_old;
    s.rightbottom = rightbottom_old;
    s.xkeepratio = xkeepratio_old;
    s.ykeepratio = ykeepratio_old;
    s.x = x_old;
    s.y = y_old;
    s.help = help_old;
    
    s.up = up_old;
    s.over = over_old;
    s.down = down_old;
    s.points = points_old;
    s.value = value_old;
    s.tooltiptext = tooltiptext_old;
    s.thickness = thickness_old;
    
    s.updateBezier();
  }
  @Override
  public void redo() {
    s.id = id_new;
    s.visible = visible_new;
    s.lefttop = lefttop_new;
    s.rightbottom = rightbottom_new;
    s.xkeepratio = xkeepratio_new;
    s.ykeepratio = ykeepratio_new;
    s.x = x_new;
    s.y = y_new;
    s.help = help_new;
    
    s.up = up_new;
    s.over = over_new;
    s.down = down_new;
    s.points = points_new;
    s.value = value_new;
    s.tooltiptext = tooltiptext_new;
    s.thickness = thickness_new;
    
    s.updateBezier();
  }
  @Override
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",s.type);
  }
}
