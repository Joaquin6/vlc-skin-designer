/*****************************************************************************
 * SubBitmapEditEvent.java
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

import vlcskineditor.resources.SubBitmap;

/**
 * Represents the action of editing a SubBitmap
 * @author Daniel Dreibrodt
 */
public class SubBitmapEditEvent extends HistoryEvent{
  
  private SubBitmap sb;
  private int x_old, x_new, y_old, y_new, width_old, width_new, height_old, height_new;
  private int nbframes_old, nbframes_new, fps_old, fps_new;
  /** Creates a new instance of SubBitmapEditEvent */
  public SubBitmapEditEvent(SubBitmap sbmp) {
    sb = sbmp;
    x_old = sb.x;
    y_old = sb.y;
    width_old = sb.width;
    height_old = sb.height;
    nbframes_old = sb.nbframes;
    fps_old = sb.fps;
  }
  public void setNew() {
    x_new = sb.x;
    y_new = sb.y;
    width_new = sb.width;
    height_new = sb.height;
    nbframes_new = sb.nbframes;
    fps_new = sb.fps;
  }
  public void undo() {
    sb.x = x_old;
    sb.y = y_old;
    sb.width = width_old;
    sb.height = height_old;
    sb.nbframes = nbframes_old;
    sb.fps = fps_old;
  }
  public void redo() {
    sb.x = x_new;
    sb.y = y_new;
    sb.width = width_new;
    sb.height = height_new;
    sb.nbframes = nbframes_new;
    sb.fps = fps_new;
  }
  public String getDescription() {
    return "Edit SubBitmap";
  }
}
