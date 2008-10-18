/*****************************************************************************
 * ThemeInfoEditEvent.java
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
import vlcskineditor.Skin;

/**
 * ThemeInfoEditEvent
 * @author Daniel Dreibrodt
 */
public class ThemeEditEvent extends HistoryEvent{
  
  private Skin s;
  private int magnet_old, magnet_new;
  private int alpha_old, alpha_new;
  private int movealpha_old, movealpha_new;
  private String name_old, name_new;
  private String author_old, author_new;
  private String email_old, email_new;
  private String web_old, web_new;
  
  /** Creates a new instance of ThemeInfoEditEvent */
  public ThemeEditEvent(Skin ski) {
    s = ski;
    magnet_old = s.theme_magnet;
    alpha_old = s.theme_alpha;
    movealpha_old = s.theme_movealpha;
    name_old = s.themeinfo_name;
    author_old = s.themeinfo_author;
    email_old = s.themeinfo_email;
    web_old = s.themeinfo_webpage;
  }
  public void setNew() {
    magnet_new = s.theme_magnet;
    alpha_new = s.theme_alpha;
    movealpha_new = s.theme_movealpha;
    name_new = s.themeinfo_name;
    author_new = s.themeinfo_author;
    email_new = s.themeinfo_email;
    web_new = s.themeinfo_webpage;
  }
  public void undo() {
    s.theme_magnet = magnet_old;
    s.theme_alpha = alpha_old;
    s.theme_movealpha = movealpha_old;
    s.themeinfo_name = name_old;
    s.themeinfo_author = author_old;
    s.themeinfo_email = email_old;
    s.themeinfo_webpage = web_old;
  }
  public void redo() {
    s.theme_magnet = magnet_new;
    s.theme_alpha = alpha_new;
    s.theme_movealpha = movealpha_new;
    s.themeinfo_name = name_new;
    s.themeinfo_author = author_new;
    s.themeinfo_email = email_new;
    s.themeinfo_webpage = web_new;
  }
  public String getDescription() {
    return Language.get("HIST_ITEM_EDIT").replaceAll("%t",Language.get("THEME"));
  }
}
