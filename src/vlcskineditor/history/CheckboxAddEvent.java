/*****************************************************************************
 * CheckboxAddEvent.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of vlcskinAddor.history
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
import vlcskineditor.items.Checkbox;
import java.util.List;

/**
 * CheckboxAddEvent
 * @author Daniel Dreibrodt
 */
public class CheckboxAddEvent extends HistoryEvent{
  
  private List<Item> parent;
  private Checkbox c;
  
  /** Creates a new instance of CheckboxAddEvent */
  public CheckboxAddEvent(List<Item> par, Checkbox che) {
    parent = par;
    c = che;
  }
  public void undo() {
    parent.remove(c);
    c.s.updateItems();
  }
  public void redo() {
    parent.add(c);
    c.s.updateItems();
    c.s.expandItem(c.id);
  }
  public String getDescription() {
    return "Add Checkbox";
  }
}
