/*****************************************************************************
 * LayoutEditEvent.java
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
import vlcskineditor.Layout;

/**
 * Represents the action of editing a Layout
 * @author Daniel Dreibrodt
 */
public class LayoutEditEvent extends HistoryEvent {
  
  private Layout l;
  private String id_old, id_new;
  private int width_old, width_new;
  private int height_old, height_new;
  private int minwidth_old, minwidth_new;
  private int minheight_old, minheight_new;
  private int maxwidth_old, maxwidth_new;
  private int maxheight_old, maxheight_new;
  
  /** Creates a new instance of LayoutEditEvent */
  public LayoutEditEvent(Layout lay) {
    l = lay;
    id_old = l.id;
    width_old = l.width;
    height_old = l.height;
    minwidth_old = l.minwidth;
    minheight_old = l.minheight;
    maxwidth_old = l.maxwidth;
    maxheight_old = l.maxwidth;
  }
  public void setNew() {
    id_new = l.id;
    width_new = l.width;
    height_new = l.height;
    minwidth_new = l.minwidth;
    minheight_new = l.minheight;
    maxwidth_new = l.maxwidth;
    maxheight_new = l.maxwidth;
  }
  public void undo() {
    l.id = id_old;
    l.width = width_old;
    l.height = height_old;
    l.minwidth = minwidth_old;
    l.minheight = minheight_old;
    l.maxwidth = maxwidth_old;
    l.maxheight = maxheight_old;
  }
  public void redo() {
    l.id = id_new;
    l.width = width_new;
    l.height = height_new;
    l.minwidth = minwidth_new;
    l.minheight = minheight_new;
    l.maxwidth = maxwidth_new;
    l.maxheight = maxheight_new;
  }
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",l.type);
  }
}
