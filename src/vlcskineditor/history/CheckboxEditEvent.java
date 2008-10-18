/*****************************************************************************
 * CheckboxEditEvent.java
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
import vlcskineditor.items.Checkbox;

/**
 * CheckboxEditEvent
 * @author Daniel Dreibrodt
 */
public class CheckboxEditEvent extends HistoryEvent{
  
  private Checkbox c;
  
  private String id_old, id_new, visible_old, visible_new, lefttop_old, lefttop_new;
  private String rightbottom_old, rightbottom_new, help_old, help_new;
  private boolean xkeepratio_old, xkeepratio_new, ykeepratio_old, ykeepratio_new;
  private int x_old, x_new, y_old, y_new;
  
  private String down1_old, down1_new, down2_old, down2_new;
  private String up1_old, up1_new, up2_old, up2_new;
  private String over1_old, over1_new, over2_old, over2_new;
  private String action1_old, action1_new, action2_old, action2_new;
  private String tooltiptext1_old, tooltiptext1_new, tooltiptext2_old, tooltiptext2_new;
  private String state_old, state_new;
  
  /** Creates a new instance of CheckboxEditEvent */
  public CheckboxEditEvent(Checkbox che) {    
    c = che;
    
    id_old = c.id;
    visible_old = c.visible;
    lefttop_old = c.lefttop;
    rightbottom_old = c.rightbottom;
    xkeepratio_old = c.xkeepratio;
    ykeepratio_old = c.ykeepratio;
    x_old = c.x;
    y_old = c.y;
    help_old = c.help;
    
    down1_old = c.down1;
    down2_old = c.down2;
    up1_old = c.up1;
    up2_old = c.up2;
    over1_old = c.over1;
    over2_old = c.over2;
    action1_old = c.action1;
    action2_old = c.action2;
    tooltiptext1_old = c.tooltiptext1;
    tooltiptext2_old = c.tooltiptext2;
    state_old = c.state;
  }
  public void setNew() {
    id_new = c.id;
    visible_new = c.visible;
    lefttop_new = c.lefttop;
    rightbottom_new = c.rightbottom;
    xkeepratio_new = c.xkeepratio;
    ykeepratio_new = c.ykeepratio;
    x_new = c.x;
    y_new = c.y;
    help_new = c.help;
    
    down1_new = c.down1;
    down2_new = c.down2;
    up1_new = c.up1;
    up2_new = c.up2;
    over1_new = c.over1;
    over2_new = c.over2;
    action1_new = c.action1;
    action2_new = c.action2;
    tooltiptext1_new = c.tooltiptext1;
    tooltiptext2_new = c.tooltiptext2;
    state_new = c.state;
  }
  public void undo() {
    c.id = id_old;
    c.visible = visible_old;
    c.lefttop = lefttop_old;
    c.rightbottom = rightbottom_old;
    c.xkeepratio = xkeepratio_old;
    c.ykeepratio = ykeepratio_old;
    c.x = x_old;
    c.y = y_old;    
    c.help = help_old;
    
    c.down1 = down1_old;
    c.down2 = down2_old;
    c.up1 = up1_old;
    c.up2 = up2_old;
    c.over1 = over1_old;
    c.over2 = over2_old;
    c.action1 = action1_old;
    c.action2 = action2_old;
    c.tooltiptext1 = tooltiptext1_old;
    c.tooltiptext2 = tooltiptext2_old;
    c.state = state_old;
  }
  public void redo() {
    c.id = id_new;
    c.visible = visible_new;
    c.lefttop = lefttop_new;
    c.rightbottom = rightbottom_new;
    c.xkeepratio = xkeepratio_new;
    c.ykeepratio = ykeepratio_new;
    c.x = x_new;
    c.y = y_new;  
    c.help = help_new;
    
    c.down1 = down1_new;
    c.down2 = down2_new;
    c.up1 = up1_new;
    c.up2 = up2_new;
    c.over1 = over1_new;
    c.over2 = over2_new;
    c.action1 = action1_new;
    c.action2 = action2_new;
    c.tooltiptext1 = tooltiptext1_new;
    c.tooltiptext2 = tooltiptext2_new;
    c.state = state_new;
  }
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",c.type);
  }
}
