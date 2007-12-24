/*****************************************************************************
 * ResourceDeletionEvent.java
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

import vlcskineditor.*;

/**
 * Represents the action of deleting a resource
 * @author Daniel Dreibrodt
 */
public class ResourceDeletionEvent extends HistoryEvent{
  
  Skin s;
  Resource r;
  int i;
  
  public ResourceDeletionEvent(Skin ski, Resource res, int ind) {
    s = ski;
    r = res;
    i = ind;
  }
  @Override
  public void undo() {
    s.resources.add(i, r);
    s.updateResources();
    s.expandResource(r.id);
  }
  @Override
  public void redo() {
    s.resources.remove(r);
    s.updateResources();
  }
  @Override
  public String getDescription() {
    return "Delete Resource \""+r.id+"\"";
  }
}
