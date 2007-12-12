/*****************************************************************************
 * ButtonEditEvent.java
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

import vlcskineditor.items.Button;

/**
 * Represents the action of editing a Button
 * @author Daniel Dreibrodt
 */
public class ButtonEditEvent extends HistoryEvent{
  
  private Button b;
  
  private String id_old, id_new, visible_old, visible_new, lefttop_old, lefttop_new;
  private String rightbottom_old, rightbottom_new, help_old, help_new;
  private boolean xkeepratio_old, xkeepratio_new, ykeepratio_old, ykeepratio_new;
  private int x_old, x_new, y_old, y_new;
  
  private String up_old, up_new, over_old, over_new, down_old, down_new;
  private String action_old, action_new, tooltiptext_old, tooltiptext_new;
  
  /** Creates a new instance of ButtonEditEvent */
  public ButtonEditEvent(Button but) {
    b = but;
    
    id_old = b.id;
    visible_old = b.visible;
    lefttop_old = b.lefttop;
    rightbottom_old = b.rightbottom;
    xkeepratio_old = b.xkeepratio;
    ykeepratio_old = b.ykeepratio;
    x_old = b.x;
    y_old = b.y;
    help_old = b.help;
    
    up_old = b.up;
    over_old = b.over;
    down_old = b.down;
    action_old = b.action;
    tooltiptext_old = b.tooltiptext;
  }
  public void setNew() {
    id_new = b.id;
    visible_new = b.visible;
    lefttop_new = b.lefttop;
    rightbottom_new = b.rightbottom;
    xkeepratio_new = b.xkeepratio;
    ykeepratio_new = b.ykeepratio;
    x_new = b.x;
    y_new = b.y;
    help_new = b.help;
    
    up_new = b.up;
    over_new = b.over;
    down_new = b.down;
    action_new = b.action;
    tooltiptext_new = b.tooltiptext;
  }
  public void undo() {
    b.id = id_old;
    b.visible = visible_old;
    b.lefttop = lefttop_old;
    b.rightbottom = rightbottom_old;
    b.xkeepratio = xkeepratio_old;
    b.ykeepratio = ykeepratio_old;
    b.x = x_old;
    b.y = y_old;
    b.help = help_old;
    
    b.up = up_old;
    b.over = over_old;
    b.down = down_old;
    b.action = action_old;
    b.tooltiptext = tooltiptext_old;
  }
  public void redo() {
    b.id = id_new;
    b.visible = visible_new;
    b.lefttop = lefttop_new;
    b.rightbottom = rightbottom_new;
    b.xkeepratio = xkeepratio_new;
    b.ykeepratio = ykeepratio_new;
    b.x = x_new;
    b.y = y_new;
    b.help = help_new;
    
    b.up = up_new;
    b.over = over_new;
    b.down = down_new;
    b.action = action_new;
    b.tooltiptext = tooltiptext_new;
  }
  public String getDescription() {
    return "Edit Button";
  }
}
