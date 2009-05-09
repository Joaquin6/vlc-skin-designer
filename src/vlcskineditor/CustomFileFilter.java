/*****************************************************************************
 * CustomFileFilter.java
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
import javax.swing.filechooser.*;
import java.io.File;

/**
 * CustomFileFilter
 * @author Daniel Dreibrodt
 */
public class CustomFileFilter extends FileFilter{
  String root;
  JFileChooser fc;
  boolean onlyinroot;
  String desc;
  String[] ext;
  
  /** 
   * Creates a new custom FileFilter
   * @param fc_ The file chooser that uses this file filter
   * @param ext_ An array of Strings representing the accepted extensions. e.g { "bmp", "jpg", "jpeg"}
   * @param desc_ The description of the FileFilter that is displayed in the FileChooser
   * @param oir_  If true files can be only chosen inside the given root folder
   * @param r_ The root folder (only useful in connection with <pre>oir_</pre>)
   **/
  public CustomFileFilter(JFileChooser fc_, String[] ext_, String desc_, boolean oir_, String r_) {
    fc=fc_;
    ext=ext_;
    desc=desc_;
    onlyinroot=oir_;    
    root = r_;
  }
  /**
   * Creates a new custom FileFilter
   * @param fc_ The file chooser that uses this file filter
   * @param ext_ The accepted file extension
   * @param desc_ The description of the FileFilter that is displayed in the FileChooser
   * @param oir_ If true files can be only chosen inside the given root folder
   * @param r_ The root folder (only useful in connection with <pre>oir_</pre>)
   */
  public CustomFileFilter(JFileChooser fc_, String ext_, String desc_, boolean oir_, String r_) {
    fc=fc_;
    String[] extt = new String[1];
    extt[0] = ext_;
    ext=extt;
    desc=desc_;
    onlyinroot=oir_;    
    root = r_;
  }
  /**
   * Checks whether a file is acceptable or not
   * @param f The file to be checked
   * @return Whether the file should be shown in the file list or not
   */
  public boolean accept(File f) {
        if(!f.getPath().startsWith(root) && onlyinroot) {          
          fc.setCurrentDirectory(new File(root));      
          fc.repaint();
        }      
        if (f.isDirectory()) {                 
          return true;
        }         
        String extension = getExtension(f);   
        if(extension==null) return false;
        for(int i=0;i<ext.length;i++) {
          if(extension.toLowerCase().equals(ext[i].toLowerCase())) return true;
        }
        return false;
   }
  /**
   * Returns a description for this FileFilter
   * @return The FileFilter's description
   */
  public String getDescription() {
    return desc;
  }
  /**
   * Helper function to extract the extension of a File object
   * @param f File of which to find the extension
   * @return  Extension of <pre>f</pre>
   */
  public String getExtension(File f) {
    String x = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    
    if (i > 0 &&  i < s.length() - 1) {
      x = s.substring(i+1).toLowerCase();
    }
    return x;
  }
}