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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
  public static void browse(final String url) {
    //Put in in a new thread so that the interface does not hang needlessly while launching the browser
    new Thread() {
      @Override
      public void run() {
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

    }.start();
  }
  
  /**
   * Taken from http://www.rgagnon.com/javadetails/java-0064.html
   * Licensed under Creative Commons BY-NC-SA 2.5
   * @author Real Gagnon
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

  /**
   * Unzips a ZIP file in the current working directory
   * @param zip The ZIP file
   * @throws java.io.FileNotFoundException
   * @throws java.io.IOException
   */
  public static void unzip(File zip) throws FileNotFoundException, IOException {
    ZipInputStream zin = new ZipInputStream(new FileInputStream(zip));
    ZipEntry entry;
    while( (entry=zin.getNextEntry()) !=null ) {
      if(!(System.getProperty("os.name").indexOf("Windows")==-1 && (entry.getName().endsWith("exe")||entry.getName().endsWith("dll")))){
        File outf = new File(entry.getName());
        System.out.println(outf.getAbsoluteFile());
        if(entry.isDirectory()) outf.mkdirs();
        else {
          outf.createNewFile();
          OutputStream out = new FileOutputStream(outf);
          byte[] buf = new byte[1024];
          int len;
          while ((len = zin.read(buf)) > 0) {
            out.write(buf, 0, len);
          }
          out.close();
        }
      }
    }
  }

}
