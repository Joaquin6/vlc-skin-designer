/*****************************************************************************
 * Resource.java
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

import javax.swing.*;
import javax.swing.tree.*;
/**
 * Abstract superclass representing a Bitmap, SubBitmap or Font
 * @author Daniel Dreibrodt
 */
public abstract class Resource {
  
  /** Value should be either Bitmap or Font */
  public String id;
  public String type;
  /** Represents the skin to which the resource belongs */
  public Skin s;
  public Resource() {
  }  
  /** Show a dialog to modify the resource's parameters */
  public abstract void showOptions();  
  /** Update the Resource's attributes according to user input */
  public abstract void update();
  /** Creates the XML code representing the resource */  
  public abstract String returnCode();
  /** Creates a DefaultMutableTreeNode to be displayed in the resources tree */
  public abstract DefaultMutableTreeNode getTreeNode();  
  /** If a Resource does contain another Resource of the given id (e.g. a SubBitmap) the containing Resource is returned **/
  public Resource getParentOf(String id_) {
    return null;
  }
  /** Renames the Resource after the copy process **/
  public void renameForCopy() {
    if(id.indexOf("_copy")!=-1) {
      id = id.substring(0,id.indexOf("_copy"));
    } 
    String newid_base = id+="_copy";     
    String newid = newid_base;
    int i = 1;
    while(s.idExists(newid)) {
      i++;
      newid = newid_base+"_"+String.valueOf(i);
    }
    id = newid;
  }
}
