/*****************************************************************************
 * SliderBackgroundEditEvent.java
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
import vlcskineditor.items.SliderBackground;

/**
 * Represents the action of editing a slider background
 * @author Daniel Dreibrodt
 */
public class SliderBackgroundEditEvent extends HistoryEvent{
  
  SliderBackground s;
  
  private String id_old, id_new, image_old, image_new;
  private int nbhoriz_old, nbhoriz_new, nbvert_old, nbvert_new;
  private int padhoriz_old, padhoriz_new, padvert_old, padvert_new;
  
  public SliderBackgroundEditEvent(SliderBackground sli) {
    s = sli;
    
    id_old = s.id;
    image_old = s.image;
    nbhoriz_old = s.nbhoriz;
    nbvert_old = s.nbvert;
    padhoriz_old = s.padhoriz;
    padvert_old = s.padvert;
  }
  public void setNew() {
    id_new = s.id;
    image_new = s.image;
    nbhoriz_new = s.nbhoriz;
    nbvert_new = s.nbvert;
    padhoriz_new = s.padhoriz;
    padvert_new = s.padvert;
  }
  @Override
  public void undo() {
    s.id = id_old;
    s.image = image_old;
    s.nbhoriz = nbhoriz_old;
    s.nbvert = nbvert_old;
    s.padhoriz = padhoriz_old;
    s.padvert = padvert_old;
  }
  @Override
  public void redo() {
    s.id = id_new;
    s.image = image_new;
    s.nbhoriz = nbhoriz_new;
    s.nbvert = nbvert_new;
    s.padhoriz = padhoriz_new;
    s.padvert = padvert_new;
  }
  @Override
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",s.type);
  }
}
