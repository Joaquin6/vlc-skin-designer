/*****************************************************************************
 * GroupEditEvent.java
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
import vlcskineditor.Language;
import vlcskineditor.items.Group;

/**
 * Represents the action of editing a Group
 * @author Daniel
 */
public class GroupEditEvent extends HistoryEvent{
  private Group g;
  private String id_old, id_new;
  private int x_old, x_new, y_old, y_new;
    
  public GroupEditEvent(Group gro) {
    g = gro;      
    id_old = g.id;
    x_old = g.x;
    y_old = g.y;
  }
  public void setNew() {
    id_new = g.id;
    x_new = g.x;
    y_new = g.y;
  }
  @Override
  public void undo() {
    g.id = id_old;
    g.x = x_old;
    g.y = y_old;
    
    for(Item i:g.items) {
      i.setOffset(x_old,y_old);
    }
  }
  @Override
  public void redo() {
    g.id = id_new;
    g.x = x_new;
    g.y = y_new;
    
    for(Item i:g.items) {
      i.setOffset(x_new,y_new);
    }
  }
  @Override
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",g.type);
  }
}
