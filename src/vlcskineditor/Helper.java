/*****************************************************************************
 * Helper.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of VLC Skin Editor
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

package vlcskineditor;

import java.awt.Component;

/**
 * Provides helper method
 * @author Daniel Dreibrodt
 */
public class Helper {

  /**
   * Gets the greatest width of the given components
   * @param compos The components that should be checked
   * @return The maximum width
   */
  public static int maxWidth(Component[] compos) {
        int w = 0;
        for(Component c:compos) {
          int cw = c.getPreferredSize().width;
          if(cw>w) w = cw;
        }
        return w;
  }
}
