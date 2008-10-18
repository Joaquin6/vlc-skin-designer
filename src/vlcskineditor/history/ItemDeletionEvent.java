/*****************************************************************************
 * ItemDeletionEvent.java
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
import vlcskineditor.Skin;
import java.util.List;

/**
 * Represents the action of deleting an item
 * @author Daniel Dreibrodt
 */
public class ItemDeletionEvent extends HistoryEvent {
  
  Item i;
  List<Item> parent;
  int index;
  Skin s;
  
  public ItemDeletionEvent(List<Item> lis, Item ite, int ind, Skin ski) {
    i = ite;
    parent = lis;
    index = ind;
    s = ski;
  }  
  @Override
  public void undo() {
    parent.add(index, i);
    s.updateItems();
    s.expandItem(i.id);
  }
  @Override
  public void redo() {
    parent.remove(i);
    s.updateItems();
  }
  @Override
  public String getDescription() {
    return Language.get("HIST_ITEM_DEL").replaceAll("%t", i.type);
  }
}
