/*****************************************************************************
 * ActionPanel.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of vlcskineditor
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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * A visual representation of a single action providing input components for modifying/deleting the represented action
 * @author Daniel Dreibrodt
 */
public class ActionPanel extends JPanel {
  ActionEditor ae;
  String action;
  String action_type = "static";
  JTextField param1,param2;
  JButton del_btn;
  
  JTextField windowid_tf, layoutid_tf;
  JComboBox bool_cb;
  
  /** Creates a new instance of ActionPanel */
  public ActionPanel(String act,ActionEditor ae_) {
    action=act;
    ae=ae_;
    windowid_tf = new JTextField();
    layoutid_tf = new JTextField();    
    setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
    
    //A dynamic action is an action whose parameters can be changed
    if(act.indexOf(".set")!=-1) action_type="dynamic"; // all playlist.setXXXX(boolean)
    else if (act.indexOf(".show()")!=-1) action_type="dynamic"; // show a window    
    else if (act.indexOf(".hide()")!=-1) action_type="dynamic"; // hide a window   
    else if (act.indexOf("maximize()")!=-1) action_type="dynamic"; // un-/maximize a window
    else if (act.indexOf(".setLayout")!=-1) action_type="dynamic"; //change window layout   
    
    if(action_type.equals("static")) { //static => the action has no parameters     
      JLabel desc = new JLabel(action);
      if (action.equals("none")) desc.setText(Language.get("ACTION_DESC_NONE"));
      else if (action.equals("dialogs.changeSkin()")) desc.setText(Language.get("ACTION_DESC_CHANGESKIN"));
      else if (action.equals("dialogs.fileSimple()")) desc.setText(Language.get("ACTION_DESC_FILESIMPLE"));
      else if (action.equals("dialogs.file()")) desc.setText(Language.get("ACTION_DESC_FILE"));
      else if (action.equals("dialogs.directory()")) desc.setText(Language.get("ACTION_DESC_DIRECTORY"));
      else if (action.equals("dialogs.disc()")) desc.setText(Language.get("ACTION_DESC_DISC"));
      else if (action.equals("dialogs.net()")) desc.setText(Language.get("ACTION_DESC_NET"));  
      else if (action.equals("dialogs.messages()")) desc.setText(Language.get("ACTION_DESC_MSG"));
      else if (action.equals("dialogs.prefs()")) desc.setText(Language.get("ACTION_DESC_PREFS"));
      else if (action.equals("dialogs.fileInfo()")) desc.setText(Language.get("ACTION_DESC_FILEINFO"));      
      else if (action.equals("dialogs.playlist()")) desc.setText(Language.get("ACTION_DESC_PLAYLIST"));
      else if (action.equals("dialogs.streamingWizard()")) desc.setText(Language.get("ACTION_DESC_WIZARD"));
      else if (action.equals("dialogs.popup()")) desc.setText(Language.get("ACTION_DESC_POPUP"));
      else if (action.equals("dialogs.audioPopup()")) desc.setText(Language.get("ACTION_DESC_AUDIOPU"));
      else if (action.equals("dialogs.videoPopup()")) desc.setText(Language.get("ACTION_DESC_VIDEOPU"));
      else if (action.equals("dialogs.miscPopup()")) desc.setText(Language.get("ACTION_DESC_MISCPU"));
      else if (action.equals("equalizer.enable()")) desc.setText(Language.get("ACTION_DESC_EQENABLE"));
      else if (action.equals("equalizer.disable()")) desc.setText(Language.get("ACTION_DESC_EQDISABLE"));
      else if (action.equals("vlc.play()")) desc.setText(Language.get("ACTION_DESC_PLAY"));
      else if (action.equals("vlc.pause()")) desc.setText(Language.get("ACTION_DESC_PAUSE"));
      else if (action.equals("vlc.stop()")) desc.setText(Language.get("ACTION_DESC_STOP"));
      else if (action.equals("vlc.faster()")) desc.setText(Language.get("ACTION_DESC_FASTER"));
      else if (action.equals("vlc.slower()")) desc.setText(Language.get("ACTION_DESC_SLOWER"));
      else if (action.equals("vlc.mute()")) desc.setText(Language.get("ACTION_DESC_MUTE"));
      else if (action.equals("vlc.volumeUp()")) desc.setText(Language.get("ACTION_DESC_VOLUP"));
      else if (action.equals("vlc.volumeDown()")) desc.setText(Language.get("ACTION_DESC_VOLDOWN"));
      else if (action.equals("vlc.fullscreen()")) desc.setText(Language.get("ACTION_DESC_FULLSCREEN"));
      else if (action.equals("vlc.snapshot()")) desc.setText(Language.get("ACTION_DESC_SNAPSHOT"));
      else if (action.equals("vlc.onTop()")) desc.setText(Language.get("ACTION_DESC_ONTOP"));
      else if (action.equals("vlc.minimize()")) desc.setText(Language.get("ACTION_DESC_MIN"));
      else if (action.equals("vlc.quit()")) desc.setText(Language.get("ACTION_DESC_QUIT"));
      else if (action.equals("playlist.add()")) desc.setText(Language.get("ACTION_PL_ADD"));
      else if (action.equals("playlist.del()")) desc.setText(Language.get("ACTION_PL_DEL"));
      else if (action.equals("playlist.next()")) desc.setText(Language.get("ACTION_PL_NEXT"));
      else if (action.equals("playlist.previous()")) desc.setText(Language.get("ACTION_PL_PREV"));
      else if (action.equals("playlist.sort()")) desc.setText(Language.get("ACTION_PL_SORT"));
      else if (action.equals("playlist.load()")) desc.setText(Language.get("ACTION_PL_LOAD"));
      else if (action.equals("playlist.save()")) desc.setText(Language.get("ACTION_PL_SAVE"));
      else if (action.equals("dvd.nextTitle()")) desc.setText(Language.get("ACTION_DVD_NEXTT"));      
      else if (action.equals("dvd.previousTitle()")) desc.setText(Language.get("ACTION_DVD_PREVT"));
      else if (action.equals("dvd.nextChapter()")) desc.setText(Language.get("ACTION_DVD_NEXTC"));
      else if (action.equals("dvd.previousChapter()")) desc.setText(Language.get("ACTION_DVD_NEXTT"));
      else if (action.equals("dvd.rootMenu()")) desc.setText(Language.get("ACTION_DVD_MENU"));      
      add(desc);      
    }
    else { // dynamic action => it needs parameters
      JLabel desc = new JLabel(action);
      if (action.indexOf(".setRandom")!=-1) {
        String[] bools = { Language.get("ACTION_ACTIVATE"), Language.get("ACTION_DEACTIVATE") };
        bool_cb = new JComboBox(bools);
        if(action.toUpperCase().indexOf("TRUE")!=-1) bool_cb.setSelectedIndex(0);
        else bool_cb.setSelectedIndex(1);
        add(bool_cb);        
        desc.setText(Language.get("ACTION_RANDOM"));
        add(desc);
      }
      else if (action.indexOf(".setLoop")!=-1) {
        String[] bools = { Language.get("ACTION_ACTIVATE"), Language.get("ACTION_DEACTIVATE") };
        bool_cb = new JComboBox(bools);
        if(action.toUpperCase().indexOf("TRUE")!=-1) bool_cb.setSelectedIndex(0);
        else bool_cb.setSelectedIndex(1);
        add(bool_cb);        
        desc.setText(Language.get("ACTION_LOOP"));
        add(desc);
      }
      else if (action.indexOf(".setRepeat")!=-1) {
        String[] bools = { Language.get("ACTION_ACTIVATE"), Language.get("ACTION_DEACTIVATE") };
        bool_cb = new JComboBox(bools);
        if(action.toUpperCase().indexOf("TRUE")!=-1) bool_cb.setSelectedIndex(0);
        else bool_cb.setSelectedIndex(1);
        add(bool_cb);        
        desc.setText(Language.get("ACTION_REPEAT"));
        add(desc);
      }
      else if (action.indexOf(".show()")!=-1) {
        desc.setText(Language.get("ACTION_SHOW"));
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".show()")));
      }
      else if (action.indexOf(".hide()")!=-1) {
        desc.setText(Language.get("ACTION_HIDE"));
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".hide()")));
      }
      else if (action.indexOf(".maximize()")!=-1) {
        desc.setText(Language.get("ACTION_MAXIMIZE"));
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".maximize()")));
      }
      else if (action.indexOf(".unmaximize()")!=-1) {
        desc.setText(Language.get("ACTION_UNMAXIMIZE"));
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".unmaximize()")));
      }
      else if (action.indexOf(".setLayout")!=-1) {
        desc.setText(Language.get("ACTION_LAYOUT_OF"));
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".setLayout")));
        add(new JLabel(Language.get("ACTION_LAYOUT_TO")));
        add(layoutid_tf);
        layoutid_tf.setText(action.substring(action.indexOf("(")+1,action.indexOf(")")));
      }
      else {        
        add(desc);      
      }
    }
    del_btn = new JButton(ae.delete_icon);
    del_btn.addActionListener(ae);
    add(Box.createHorizontalGlue()); //create a flexible space between the delete button and the other items, so that it is pushed to the left
    add(Box.createRigidArea(new Dimension(5,24))); //distance between delete button and other items
    add(del_btn);        
    setBackground(Color.decode("#fce94f"));
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));   
    setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
  }
  /** 
   * Generates the action string from the type of the represented action and from user modifications.
   * The string ends with a semicolon.
   * @return A string readable by VLC representing the action that is represented by the panel
   */
  public String getActionCode() {
    if(action_type.equals("static")) return action;
    else if (action.indexOf(".setRandom")!=-1) return "playlist.setRandom("+( (bool_cb.getSelectedIndex()==0) ? "true" : "false" )+")";
    else if (action.indexOf(".setLoop")!=-1) return "playlist.setLoop("+( (bool_cb.getSelectedIndex()==0) ? "true" : "false" )+")";
    else if (action.indexOf(".setRepeat")!=-1) return "playlist.setRepeat("+( (bool_cb.getSelectedIndex()==0) ? "true" : "false" )+")";
    else if (action.indexOf(".show()")!=-1) return windowid_tf.getText()+".show()";
    else if (action.indexOf(".hide()")!=-1) return windowid_tf.getText()+".hide()";
    else if (action.indexOf(".maximize()")!=-1) return windowid_tf.getText()+".maximize()";
    else if (action.indexOf(".unmaximize()")!=-1) return windowid_tf.getText()+".unmaximize()";
    else if (action.indexOf(".setLayout")!=-1) return windowid_tf.getText()+".setLayout("+layoutid_tf.getText()+")";
    else return "none";
  }
}
