/*****************************************************************************
 * BitmapEditEvent.java
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

/**
 * Represents the action of editing a Bitmap
 * @author Daniel Dreibrodt
 */
public class BitmapEditEvent extends HistoryEvent{
  
  private Bitmap b;
  private String file_old, file_new;
  private String alphacolor_old, alphacolor_new;
  private int nbframes_old, nbframes_new;
  private int fps_old, fps_new;  
  
  /** Creates a new instance of BitmapEditEvent */
  public BitmapEditEvent(Bitmap bmp) {
    b = bmp;
    file_old = b.file;
    alphacolor_old = b.alphacolor;
    nbframes_old = b.nbframes;
    fps_old = b.fps;
  }
  /** Sets the new state of the Bitmap */
  public void setNew() {
    file_new = b.file;
    alphacolor_new = b.alphacolor;
    nbframes_new = b.nbframes;
    fps_new = b.fps;
  }
  public void undo() {
    b.file = file_old;
    b.alphacolor = alphacolor_old;
    b.nbframes = nbframes_old;
    b.fps = fps_old;
    b.updateImage();
  }
  public void redo() {
    b.file = file_new;
    b.alphacolor = alphacolor_new;
    b.nbframes = nbframes_new;
    b.fps = fps_new;
    b.updateImage();
  }
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",b.type);
  }
}
