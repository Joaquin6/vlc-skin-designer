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

  private HistoryEvent root, current;
  private Main m;

  /**
   * Creates an empty history
   * @param m_ The main program instance
   */
  public History(Main m_) {
    m = m_;
    m.setUndoEnabled(false);
    m.setRedoEnabled(false);
    root = new DummyEvent();
    current = root;
  }

  /**
   * Adds an Event at the current point in the history list and removes any actions that could be redone
   * @param h The new history event
   */
  public void addEvent(HistoryEvent h) {
    current.setNext(h);
    h.setPrevious(current);
    current = current.getNext();
    m.setRedoEnabled(false);
    m.setRedoString("");
    m.setUndoEnabled(true);
    m.setUndoString(current.getDescription());

    //Remove any link to an event that is more than 50 edits ago
    HistoryEvent he = current.getPrevious();
    for(int i = 1; i < 50; i++) {
      if(he.getPrevious() == null) {
        break;
      }
      he = he.getPrevious();
    }
    if(he != root) {
      he.setPrevious(root);
    }
  }

  /**
   * Redoes the action that is next in the history list
   */
  public void redo() {
    if(current.getNext() == null) {
      return;
    }
    current.getNext().redo();
    current = current.getNext();
    m.setRedoEnabled(current.getNext() != null);
    if(current.getNext() != null) {
      m.setRedoString(current.getNext().getDescription());
    } else {
      m.setRedoString("");
    }
    m.setUndoEnabled(true);
    m.setUndoString(current.getDescription());

    String s_res = m.getSelectedResource();
    m.s.updateResources();
    m.s.expandResource(s_res);

    String s_win = m.getSelectedInWindows();
    m.s.updateWindows();
    m.s.expandLayout(s_win);

    String s_item = m.getSelectedItem();
    m.s.updateItems();
    m.s.expandItem(s_item);
  }

  /**
   * Undoes the current action
   */
  public void undo() {
    if(current == root) {
      return;
    }
    current.undo();
    current = current.getPrevious();
    m.setRedoEnabled(true);
    m.setRedoString(current.getNext().getDescription());
    m.setUndoEnabled(current != root);
    m.setUndoString(current.getDescription());

    String s_res = m.getSelectedResource();
    m.s.updateResources();
    m.s.expandResource(s_res);

    String s_win = m.getSelectedInWindows();
    m.s.updateWindows();
    m.s.expandLayout(s_win);

    String s_item = m.getSelectedItem();
    m.s.updateItems();
    m.s.expandItem(s_item);
  }

}
