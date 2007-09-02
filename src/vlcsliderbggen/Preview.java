/*  
This file is part of the VLC Slider Background Generator.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package vlcsliderbggen;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;


public class Preview extends Thread{
  Main main;
  Builder builder;
  
  public Preview(Main main_) {
    main = main_;
    builder = new Builder(main);
  }
  public void run() {
    JFrame frame = new JFrame("Preview");
    Canvas c = new Canvas(){      
      int i = 0;      
      public void paint(Graphics g) {
        BufferedImage bi = builder.output.getSubimage(0,i*builder.height,builder.width,builder.height);
        g.drawImage(bi,0,0,main);
        i++;
        if (i==builder.nbframes) {
          i=0;
        }
      }
    };
    frame.add(c);
    frame.setLayout(null);
    c.setBounds(0,0,builder.width,builder.height);
    frame.setResizable(false);
    frame.setSize(builder.width+10,builder.height+30);    
    //frame.setBounds(0,0,builder.width,builder.height);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    while(frame.isShowing()) {
      c.repaint();
      try {
        sleep(1000/24);
      }
      catch (Exception e) {
        
      }      
    }
    interrupt();
  }
  
}
