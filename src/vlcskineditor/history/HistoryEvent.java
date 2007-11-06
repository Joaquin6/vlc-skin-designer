/*****************************************************************************
 * HistoryEvent.java
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

/**
 * Abstract super class for events in the history
 * @author Daniel Dreibrodt
 */
public abstract class HistoryEvent {
  
  protected HistoryEvent next, previous;
  
  /** Creates a new HistoryEvent */
  public HistoryEvent() {
    next = null;
    previous = null;
  }
  /** Undoes the action represented by this object */
  public abstract void undo();
  /** Redoes the action represented by this object */
  public abstract void redo();
  /** Gets a description of the action represented by this object */
  public abstract String getDescription();
  /** Gets the next action in the history */
  public HistoryEvent getNext() {
    return next;
  }
  /** Gets the previous action in the history */
  public HistoryEvent getPrevious() {
    return previous;
  }
  /** Sets the next action in the history */
  public void setNext(HistoryEvent he) {
    next = he;
  }
  /** Sets the previous action in the history */
  public void setPrevious(HistoryEvent he) {
    previous = he;
  }
}
