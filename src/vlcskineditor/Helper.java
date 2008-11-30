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
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.JOptionPane;

/**
 * Provides helper method
 * @author Daniel Dreibrodt
 */
public class Helper {
  
  private static Desktop desktop;
  
  static {
    if(Desktop.isDesktopSupported())
            desktop = Desktop.getDesktop();
  }

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
  
  /**
   * Opens the given webpage in the systems default browser
   * @param url The URL to the webpage
   */
  public static void browse(String url) {
     if(desktop!=null) {
        try {
          desktop.browse(new java.net.URI(url));
        }
        catch (Exception ex) {
          JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
        }
      }
      else {
        JOptionPane.showMessageDialog(null,Language.get("ERROR_BROWSE_MSG").replaceAll("%u", url),Language.get("ERROR_BROWSE_TITLE"),JOptionPane.WARNING_MESSAGE);    
      }
  }
  
  /**
   * Taken from http://www.rgagnon.com/javadetails/java-0064.html
   * @param in Source File
   * @param out Destination File
   * @throws java.io.IOException
   */  
  public static void copyFile(File in, File out) throws IOException {
    FileChannel inChannel = new FileInputStream(in).getChannel();
    FileChannel outChannel = new FileOutputStream(out).getChannel();
    try {
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } 
    catch (IOException e) {
      throw e;
    }
    finally {
      if (inChannel != null) inChannel.close();
      if (outChannel != null) outChannel.close();
    }
  }
}
