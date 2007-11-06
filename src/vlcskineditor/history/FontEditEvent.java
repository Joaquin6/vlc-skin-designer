/*****************************************************************************
 * FontEditEvent.java
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

import vlcskineditor.resources.Font;

/**
 * Represents the action of editing a Font
 * @author Daniel Dreibrodt
 */
public class FontEditEvent extends HistoryEvent{
  
  private Font f;
  private String file_old, file_new;
  private int size_old, size_new;
  
  /** Creates a new instance of FontEditEvent */
  public FontEditEvent(Font fnt) {
    f = fnt;
    file_old = f.file;
    size_old = f.size;
  }
  /** Sets the new state of the Font*/
  public void setNew() {
    file_new = f.file;
    size_new = f.size;
  }
  public void undo() {
    f.file = file_old;
    f.size = size_old;
    f.updateFont();
  }
  public void redo() {
    f.file = file_new;
    f.size = size_new;
    f.updateFont();
  }
  public String getDescription() {
    return "Edit Font";
  }
}
