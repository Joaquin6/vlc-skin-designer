/*****************************************************************************
 * ItemMoveEvent.java
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

/**
 * Represents the action of moving an Item
 * @author Daniel Dreibrodt
 */
public class ItemMoveEvent extends HistoryEvent{
  
  private Item i;
  private int x_old, x_new, y_old, y_new;
  
  /** Creates a new instance of ItemMoveEvent */
  public ItemMoveEvent(Item ite) {
    i = ite;
    x_old = i.x;
    y_old = i.y;
  }
  public void setNew() {
    x_new = i.x;
    y_new = i.y;
  }
  public void undo() {
    i.x = x_old;
    i.y = y_old;
  }
  public void redo() {
    i.x = x_new;
    i.y = y_new;
  }
  public String getDescription() {
    return "Move Item";
  }
}
