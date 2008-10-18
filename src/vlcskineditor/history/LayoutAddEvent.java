/*****************************************************************************
 * LayoutAddEvent.java
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

import vlcskineditor.Window;
import vlcskineditor.Language;
import vlcskineditor.Layout;

/**
 * Represents the action of adding a Layout
 * @author Daniel Dreibrodt
 */
public class LayoutAddEvent extends HistoryEvent{
  
  private Window w;
  private Layout l;
  
  /** Creates a new instance of LayoutAddEvent */
  public LayoutAddEvent(Window win, Layout lay) {
    w = win;
    l = lay;
  }
  public void undo() {
    w.layouts.remove(l);
    w.s.updateWindows();
  }
  public void redo() {
    w.layouts.add(l);
    w.s.updateWindows();
    w.s.expandLayout(l.id);
  }
  public String getDescription() {
    return Language.get("HIST_ITEM_ADD").replaceAll("%t",Language.get("LAYOUT"));
  }
}
