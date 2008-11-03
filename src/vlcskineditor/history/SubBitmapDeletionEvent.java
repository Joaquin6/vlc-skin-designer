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
import vlcskineditor.resources.*;

/**
 * Represents the action of deleting a SubBitmap
 * @author Daniel Dreibrodt
 */
public class SubBitmapDeletionEvent extends HistoryEvent{
  
  Skin s;
  Bitmap p;
  SubBitmap sub;
  int i;
  
  public SubBitmapDeletionEvent(Skin ski, Bitmap parent, SubBitmap sbmp, int ind) {
    s = ski;
    p = parent;
    sub = sbmp;
    i = ind;
  }
  @Override
  public void undo() {
    p.SubBitmaps.add(i, sub);
    s.updateResources();
    s.expandResource(sub.id);
  }
  @Override
  public void redo() {
    p.SubBitmaps.remove(sub);
    s.updateResources();
  }
  @Override
  public String getDescription() {
    return Language.get("HIST_RES_DEL").replaceAll("%t", sub.type).replaceAll("%i", sub.id);
  }
}
