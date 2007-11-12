/*****************************************************************************
 * AnchorEditEvent.java
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

import vlcskineditor.items.Anchor;

/**
 * Represents the action of editing an Anchor
 * @author Daniel Dreibrodt
 */
public class AnchorEditEvent extends HistoryEvent{
  
  private Anchor a;
  private String points_old, points_new;
  private int priority_old, priority_new;
  private int range_old, range_new;
  
  /** Creates a new instance of AnchorEditEvent */
  public AnchorEditEvent(Anchor anc) {
    a = anc;
    points_old = a.points;
    priority_old = a.priority;
    range_old = a.range;
  }
  public void setNew() {   
    points_new = a.points;
    priority_new = a.priority;
    range_new = a.range;
  }
  public void undo() {
    a.points = points_old;
    a.priority = priority_old;
    a.range = range_old;
  }
  public void redo() {
    a.points = points_new;
    a.priority = priority_new;
    a.range = range_new;
  }
  public String getDescription() {
    return "Edit Anchor";
  }
}
