/*****************************************************************************
 * History.java
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

import vlcskineditor.Main;

/**
 * Remembers changes of objects.
 * Contains a double-sided linked list of HistoryEvents.
 * @author Daniel Dreibrodt
 */
public class History {
  
  private HistoryEvent main, current;
  private Main m;  
  
  
  /** Creates an empty history */
  public History(Main m_) {
    m = m_;
    m.setUndoEnabled(false);
    m.setRedoEnabled(false);
    main = new DummyEvent();
    current = main;
  }
  /** Adds an Event at the current point in the history list and removes any actions that could be redone*/
  public void addEvent(HistoryEvent h) {    
    current.setNext(h);
    h.setPrevious(current);
    current = current.getNext();
    m.setRedoEnabled(false);
    m.setUndoEnabled(true);
    m.setUndoString(current.getDescription());
  }
  /** Redoes the action that is next in the history list */
  public void redo() {    
    if(current.getNext()==null) return;
    current.getNext().redo();
    current = current.getNext();    
    m.setRedoEnabled(current.getNext()!=null);
    if(current.getNext()!=null) m.setRedoString(current.getNext().getDescription());
    else m.setRedoString("");
    m.setUndoEnabled(true);
    m.setUndoString(current.getDescription());
  }
  /** Undoes the current action */
  public void undo() {
    if(current==main) return;
    current.undo();
    current = current.getPrevious();
    m.setRedoEnabled(true);    
    m.setRedoString(current.getNext().getDescription());
    m.setUndoEnabled(current!=main);
    m.setUndoString(current.getDescription());
  }
}
