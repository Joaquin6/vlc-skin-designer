/*****************************************************************************
 * Config.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * VLC Skin Editor
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.*;

/**
 * Class to manage the configuration
 * @author Daniel Dreibrodt
 */
public class Config {
  
  private static Hashtable<String, String> strings = new Hashtable<String, String>();
  private static File configFile;

  static {
    //Default values
    strings.put("autoupdate", "true");
    strings.put("language", "en");
    if(Language.isLanguageAvailable(System.getProperty("user.language"))) strings.put("language",System.getProperty("user.language"));
    strings.put("win.main.width","800");
    strings.put("win.main.height","600");
    strings.put("win.main.maximized","false");
    strings.put("win.res.x","0");
    strings.put("win.res.y","0");
    strings.put("win.res.width","190");
    strings.put("win.res.height","200");
    strings.put("win.win.x","0");
    strings.put("win.win.y","200");
    strings.put("win.win.width","190");
    strings.put("win.win.height","150");
    strings.put("win.items.x","0");
    strings.put("win.items.y","350");
    strings.put("win.items.width","190");
    strings.put("win.items.height","200");
    strings.put("swing.laf","System");
    strings.put("toolbar","true");

    //Locate the config file
    if(System.getProperty("os.name").indexOf("Windows")!=-1) {
      File parent = new File(System.getenv("APPDATA"),"vlc");
      if(!parent.exists()) parent.mkdirs();
      configFile = new File(parent,"VLCSkinEditor.cfg");
    } else {
      File parent = new File("~/.vlc");
      if(!parent.exists()) parent.mkdirs();
      configFile = new File(parent,"VLCSkinEditor.cfg");
    }
  }
  
  /**
   * Loads the configuration
   */
  public static void load() {
    try {
      FileReader fr = new FileReader(configFile);
      BufferedReader br = new BufferedReader(fr);         
      
      String line = "";
      while( (line = br.readLine()) != null) {
        if(!line.startsWith("#")) {
          String[] fields = line.split("\\|");
          if(fields.length==2) {
            strings.put(fields[0], fields[1].replaceAll("\\\\n", "\n"));
          }
        }
      }
      
      br.close();
      fr.close();      
    } catch(FileNotFoundException ex) {
      System.out.println("Configuration does not yet exist. Creating it...");
      save();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Gets the configured value of a certain key
   * @param key The key to look for
   * @return The key's value
   */
  public static String get(String key) {
    return strings.get(key);    
  }
  /**
   * Gets the configured value of a certain key as an integer
   * @param key The key to look for
   * @return The key's value
   */
  public static int getInt(String key) {
    String s = strings.get(key);    
    int i = 0;
    try {
      i = Integer.parseInt(s);
    } catch(Exception ex) {
      
    }
    return i;
  }
  
  /**
   * Configures a certain key
   * @param key The key to set
   * @param value The key's new value
   */
  public static void set(String key, Object value) {
    strings.put(key, String.valueOf(value));
  }
  
  /**
   * Saves the configuration
   */
  public static void save() {
    try {
      FileWriter fw = new FileWriter(configFile);
      BufferedWriter bw = new BufferedWriter(fw);
      Enumeration<String> keys = strings.keys();
      while(keys.hasMoreElements()) {
        String key = keys.nextElement();
        String value = strings.get(key);
        bw.write(key+"|"+value);
        bw.newLine();
      }
      bw.close();
      fw.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
    
  }
  
  /**
   * Shows a dialog to edit the configuration
   */
  public static void showOptions() {
    final JFrame frame = new JFrame(Language.get("WIN_PREFS_TITLE"));
    
    JLabel update_l = new JLabel(Language.get("WIN_PREFS_UPDATE_L"));
    final JCheckBox update_cb = new JCheckBox();
    update_cb.setSelected(Boolean.parseBoolean(Config.get("autoupdate")));
    
    JLabel lang_l = new JLabel(Language.get("WIN_PREFS_LANG_L"));
    Object[] lang_choices = Language.getAvailableLanguages().toArray();
    final JComboBox lang_cb = new JComboBox(lang_choices);
    for(int i=0;i<lang_choices.length;i++) {
      if( ((Language)lang_choices[i]).getCode().equals(get("language")) ) {
        lang_cb.setSelectedIndex(i);
      }
    }
    
    JLabel laf_l = new JLabel(Language.get("WIN_PREFS_LAF_L"));
    String[] lafs = { "System" , "Metal: Steel", "Metal: Ocean" };
    final JComboBox laf_cb = new JComboBox(lafs);
    laf_cb.setSelectedItem(Config.get("swing.laf"));

    JLabel tbar_l = new JLabel(Language.get("WIN_PREFS_TBAR_L"));
    final JCheckBox tbar_cb = new JCheckBox();
    tbar_cb.setSelected(Boolean.parseBoolean(Config.get("toolbar")));

    JLabel restart_l = new JLabel(Language.get("WIN_PREFS_RESTART_L"));
    
    JButton ok_btn = new JButton(Language.get("BUTTON_OK"));
    ok_btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        strings.put("autoupdate", String.valueOf(update_cb.isSelected()));
        strings.put("language", ((Language)lang_cb.getSelectedItem()).getCode());
        strings.put("swing.laf", (String)laf_cb.getSelectedItem());
        strings.put("toolbar", String.valueOf(update_cb.isSelected()));
        
        
        frame.setVisible(false);
        frame.dispose();
      }
    });
    
    JButton cancel_btn = new JButton(Language.get("BUTTON_CANCEL"));
    cancel_btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        frame.setVisible(false);
        frame.dispose();
      }
    });
    
    frame.add(lang_l);
    frame.add(lang_cb);
    frame.add(update_l);
    frame.add(update_cb);
    frame.add(laf_l);
    frame.add(laf_cb);
    frame.add(tbar_l);
    frame.add(tbar_cb);
    frame.add(restart_l);
    frame.add(ok_btn);
    frame.add(cancel_btn);
    
    Component[] labels = { lang_l, update_l, laf_l};
    int tf_dx = Helper.maxWidth(labels)+10;   
    
    int tf_wd = restart_l.getPreferredSize().width+5 - tf_dx;
    if(tf_wd < lang_cb.getPreferredSize().width) tf_wd = lang_cb.getPreferredSize().width;
    
    lang_cb.setPreferredSize(new Dimension(tf_wd,lang_cb.getPreferredSize().height));
    
    SpringLayout layout = new SpringLayout();
    layout.putConstraint(SpringLayout.NORTH, lang_l, 5, SpringLayout.NORTH, frame.getContentPane());
    layout.putConstraint(SpringLayout.WEST, lang_l, 5, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.VERTICAL_CENTER, lang_cb, 0, SpringLayout.VERTICAL_CENTER, lang_l);
    layout.putConstraint(SpringLayout.WEST, lang_cb, tf_dx, SpringLayout.WEST, frame.getContentPane());
    //layout.putConstraint(SpringLayout.EAST, lang_cb, 5, SpringLayout.EAST, frame.getContentPane());
    
    layout.putConstraint(SpringLayout.NORTH, update_l, 10, SpringLayout.SOUTH, lang_l);
    layout.putConstraint(SpringLayout.WEST, update_l, 5, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.VERTICAL_CENTER, update_cb, 0, SpringLayout.VERTICAL_CENTER, update_l);
    layout.putConstraint(SpringLayout.WEST, update_cb, tf_dx, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.EAST, update_cb, 0, SpringLayout.EAST, lang_cb);
    
    layout.putConstraint(SpringLayout.NORTH, laf_l, 10, SpringLayout.SOUTH, update_l);
    layout.putConstraint(SpringLayout.WEST, laf_l, 5, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.VERTICAL_CENTER, laf_cb, 0, SpringLayout.VERTICAL_CENTER, laf_l);
    layout.putConstraint(SpringLayout.WEST, laf_cb, tf_dx, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.EAST, laf_cb, 0, SpringLayout.EAST, lang_cb);

    layout.putConstraint(SpringLayout.NORTH, tbar_l, 10, SpringLayout.SOUTH, laf_l);
    layout.putConstraint(SpringLayout.WEST, tbar_l, 5, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.VERTICAL_CENTER, tbar_cb, 0, SpringLayout.VERTICAL_CENTER, tbar_l);
    layout.putConstraint(SpringLayout.WEST, tbar_cb, tf_dx, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.EAST, tbar_cb, 0, SpringLayout.EAST, lang_cb);

    layout.putConstraint(SpringLayout.NORTH, restart_l, 10, SpringLayout.SOUTH, tbar_l);
    layout.putConstraint(SpringLayout.WEST, restart_l, 5, SpringLayout.WEST, frame.getContentPane());
    
    layout.putConstraint(SpringLayout.NORTH, ok_btn, 10, SpringLayout.SOUTH, restart_l);
    layout.putConstraint(SpringLayout.WEST, ok_btn, 5, SpringLayout.WEST, frame.getContentPane());
    layout.putConstraint(SpringLayout.WEST, cancel_btn, 5, SpringLayout.EAST, ok_btn);
    layout.putConstraint(SpringLayout.NORTH, cancel_btn, 0, SpringLayout.NORTH, ok_btn);
    
    layout.putConstraint(SpringLayout.SOUTH, frame.getContentPane(), 10, SpringLayout.SOUTH, ok_btn);
    layout.putConstraint(SpringLayout.EAST, frame.getContentPane(), 5, SpringLayout.EAST, lang_cb);
    
    
    frame.setLayout(layout);
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);
  }
  
}