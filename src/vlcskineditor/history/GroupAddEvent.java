/*****************************************************************************
 * GroupAddEvent.java
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
import vlcskineditor.items.Group;
import java.util.List;

/**
 * Represents the action of adding a Group
 * @author Daniel Dreibrodt
 */
public class GroupAddEvent extends HistoryEvent{
  
  private List<Item> parent;
  private Group g;
  
  /** Creates a new instance of GroupAddEvent */
  public GroupAddEvent(List<Item> par, Group gro) {
    parent = par;
    g = gro;
  }
  public void undo() {
    parent.remove(g);
    g.s.updateItems();
  }
  public void redo() {
    parent.add(g);
    g.s.updateItems();
    g.s.expandItem(g.id);
  }
  public String getDescription() {
    return "Add Group";
  }
}
