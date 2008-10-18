/*****************************************************************************
 * WindowEditEvent.java
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

import vlcskineditor.Language;
import vlcskineditor.Window;

/**
 * Represents the action of editing a Window
 * @author Daniel Dreibrodt
 */
public class WindowEditEvent extends HistoryEvent{
  
  private Window w;
  private String id_old, id_new;
  private int x_old, x_new;
  private int y_old, y_new;
  private boolean dragdrop_old, dragdrop_new;
  private boolean playondrop_old, playondrop_new;
  
  /** Creates a new instance of WindowEditEvent */
  public WindowEditEvent(Window win) {
    w = win;
    id_old = w.id;
    x_old = w.x;
    y_old = w.y;
    dragdrop_old = w.dragdrop;
    playondrop_old = w.playondrop;
  }
  public void setNew() {
    id_new = w.id;
    x_new = w.x;
    y_new = w.y;
    dragdrop_new = w.dragdrop;
    playondrop_new = w.playondrop;
  }
  public void undo() {
    w.id = id_old;
    w.x = x_old;
    w.y = y_old;
    w.dragdrop = dragdrop_old;
    w.playondrop = playondrop_old;
  }
  public void redo() {
    w.id = id_new;
    w.x = x_new;
    w.y = y_new;
    w.dragdrop = dragdrop_new;
    w.playondrop = playondrop_new;
  }
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",Language.get("WINDOW"));
  }
}
