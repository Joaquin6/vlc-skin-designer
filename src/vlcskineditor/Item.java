/*****************************************************************************
 * Item.java
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

import java.awt.Graphics2D;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Abstract superclass representing layout elements
 * @author Daniel Dreibrodt
 */
public abstract class Item {

  /** Represents the skin to which an item belongs */
  public Skin s;
  /*
   * Common attributes
   * See the skins2 documentation
   */
  /** Default ID value */
  public final String ID_DEFAULT = "Unnamed";
  /** Default visible value */
  public final String VISIBLE_DEFAULT = "true";
  /** Default X value */
  public final int X_DEFAULT = 0;
  /** Default Y value */
  public final int Y_DEFAULT = 0;
  /** Default lefttop value */
  public final String LEFTTOP_DEFAULT = "lefttop";
  /** Default rightbottom value */
  public final String RIGHTBOTTOM_DEFAULT = "lefttop";
  /** Default xkeepratio value */
  public final boolean XKEEPRATIO_DEFAULT = false;
  /** Default ykeepratio value */
  public final boolean YKEEPRATIO_DEFAULT = false;
  /** Default help value */
  public final String HELP_DEFAULT = "";
  /** The Item's ID attribute */
  public String id = ID_DEFAULT;
  /** The Item's visible attribute */
  public String visible = VISIBLE_DEFAULT;
  /** The Item's x attribute */
  public int x = X_DEFAULT;
  /** The Item's y attribute */
  public int y = Y_DEFAULT;
  /** The lefttop attribute */
  public String lefttop = LEFTTOP_DEFAULT;
  /** The Item's rightbottom attribute */
  public String rightbottom = RIGHTBOTTOM_DEFAULT;
  /** The Item's xkeepratio attribute */
  public boolean xkeepratio = XKEEPRATIO_DEFAULT;
  /** The Item's ykeepratio attribute */
  public boolean ykeepratio = YKEEPRATIO_DEFAULT;
  /** The Item's help attrtibute */
  public String help = HELP_DEFAULT;
  /** The Item's type  */
  public String type = "undefined";
  /** Indicates whether this Item is selected in the item list */
  protected boolean selected = false;
  /** Indicates whether this Item is hovered by the mouse in the preview window */
  protected boolean hovered = false;
  /** Indicates whether this Item is clicked on in the preview window */
  protected boolean clicked = false;
  /** The absolute x coordinates of the parent which contains this Item */
  public int offsetx = 0;
  /** The absolute y coordinates of the parent which contains this Item */
  public int offsety = 0;
  /** Store for the parsed visible attribute, indicating whether the Item is drawn or not */
  protected boolean vis = true;
  /** Determines whether the process of creating the object has finished **/
  public boolean created = false;

  /** Show a dialog to modify the items's parameters */
  public abstract void showOptions();

  /** Update the Item's attributes according to the user input */
  public abstract void update();

  /** Creates the XML code representing the item
   * @param indent The indentation string
   * @return The indented XML code
   */
  public abstract String returnCode(String indent);

  /** Draws the item to a graphics context
   * @param g graphics context
   * @param z zoom factor
   */
  public abstract void draw(Graphics2D g, int z);

  /** Draws the item to a graphics context with the offset x,y
   * @param g Graphics context
   * @param x X offset
   * @param y Y offset
   * @param z Zoom factor
   */
  public abstract void draw(Graphics2D g, int x, int y, int z);

  /** Creates a DefaultMutableTreeNode to be displayed in the items tree
   * @return The DefaultMutableTreeNode representation of this Item
   */
  public abstract DefaultMutableTreeNode getTreeNode();

  /**
   * Gets an Item of the given ID
   * @param id_ The ID of the desired Item
   * @return If this Item or one of its children contains an Item with the given ID it is returned, otherwise null is returned
   */
  public Item getItem(String id_) {
    if (id.equals(id_)) {
      return this;
    } else {
      return null;
    }
  }

  /**
   * Gets the list containing the Item with the given ID
   * @param id_ ID of the item whose parent list is looked for
   * @return If this Item or one of its children contains an Item with the given ID its parent list is returned, otherwise null is returned
   */
  public java.util.List<Item> getParentListOf(String id_) {
    return null;
  }

  /**
   * Gets the parent Item of the Item with the given ID
   * @param id_ ID of the item whose parent is looked for
   * @return If this Item or one of its children contains an Item with the given ID its parent item is returned, otherwise null is returned
   */
  public Item getParentOf(String id_) {
    return null;
  }

  /**
   * Sets whether the Item is selected in the tree or not
   * @param s Selected or not
   */
  public void setSelected(boolean s) {
    selected = s;
  }

  /**
   * Sets whether the Item is hovered by the mouse in the preview or not
   * @param h Hovered or not
   */
  public void setHover(boolean h) {
    hovered = h;
  }

  /**
   * Sets whether the Item is clicked in the preview or not
   * @param c Clicked or not
   */
  public void setClicked(boolean c) {
    clicked = c;
  }

  /** 
   * Checks whether the given point is inside the Item
   * @param x_ The point's x coordinate
   * @param y_ The point's y coordinate
   * @return True when the point is contained in the Item, false otherwise
   */
  public boolean contains(int x_, int y_) {
    return (x == x_ && y == y_);
  }

  /** 
   * Sets the absolut offset of the Item's parent
   * @param x_ The parents absolute x location
   * @param y_ The parents absolute y location
   */
  public void setOffset(int x_, int y_) {
    offsetx = x_;
    offsety = y_;
  }

  /**
   * Tells the Item that an action attribute has been changed by the ActionEditor <code>ae</code>
   * @param ae The ActionEditor who changed the action attribute
   */
  public void actionWasEdited(ActionEditor ae) {
  }

  /**
   * Checks whether the resource with the given ID is used by this Item or one of its subitems
   * @param id_ The resource's id
   * @return True if used, false otherwise
   */
  public boolean uses(String id_) {
    return false;
  }

  /** 
   * Renames the Item after the copy process
   * @param p The rename pattern, where %oldid% will be replaced by the Item's old ID
   */
  public void renameForCopy(String p) {
    p = p.replaceAll("%oldid%", id);
    String newid_base = p;
    String newid = newid_base;
    int i = 1;
    while (s.idExists(newid)) {
      i++;
      newid = newid_base + "_" + String.valueOf(i);
    }
    id = newid;
  }

  /**
   * Invokes the item to update its variables that depend on the global variables,
   * that is e.g. visibility
   */
  public void updateToGlobalVariables() {
    vis = s.gvars.parseBoolean(visible);
  }

}
