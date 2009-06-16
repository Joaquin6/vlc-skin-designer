/*****************************************************************************
 * ResourceChangedEvent.java
 *****************************************************************************
 * Copyright (C) 2009 Daniel Dreibrodt
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
package vlcskineditor.resources;

import vlcskineditor.Resource;

/**
 * The event triggered when a resource has changed
 * @author Daniel Dreibrodt
 */
public class ResourceChangedEvent {
  private Resource r;
  private String id;

  /**
   * Creates a new event
   * @param id The ID of the resource before the change
   * @param r The changed resource
   */
  public ResourceChangedEvent(String id, Resource r) {
    this.r = r;
    this.id = id;
  }

  /**
   * Gets the resource
   * @return The changed resource
   */
  public Resource getResource() {
    return r;
  }

  /**
   * Gets the resource's ID before it was changed
   * @return The resource's old ID
   */
  public String getOldID() {
    return id;
  }
}
