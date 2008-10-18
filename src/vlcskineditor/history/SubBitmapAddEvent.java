/*****************************************************************************
 * SubBitmapAddEvent.java
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
import vlcskineditor.resources.Bitmap;
import vlcskineditor.resources.SubBitmap;

/**
 * Represents the action of adding a SubBitmap
 * @author Daniel Dreibrodt
 */
public class SubBitmapAddEvent extends HistoryEvent{
  
  private Bitmap b;
  private SubBitmap sb;
  
  /** Creates a new instance of SubBitmapAddEvent */
  public SubBitmapAddEvent(Bitmap bmp, SubBitmap sbmp) {
    b = bmp;
    sb = sbmp;
  }
  public void undo() {
    b.SubBitmaps.remove(sb);
    b.s.updateResources();
    b.s.expandItem(b.id);
  }
  public void redo() {
    b.SubBitmaps.add(sb);
    b.s.updateResources();
    b.s.expandItem(sb.id);
  }
  public String getDescription() {
    return Language.get("HIST_ITEM_ADD").replaceAll("%t",b.type);
  }
}
