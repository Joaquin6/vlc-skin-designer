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
      if (action.equals("none")) desc.setText("Do nothing");
      else if (action.equals("dialogs.changeSkin()")) desc.setText("Show a dialog box to load a new skin.");
      else if (action.equals("dialogs.fileSimple()")) desc.setText("Show the simple \"Open File\" dialog box.");
      else if (action.equals("dialogs.file()")) desc.setText("Show the extended \"Open File\" dialog box.");
      else if (action.equals("dialogs.directory()")) desc.setText("Show the \"Open Directory\" dialog box.");
      else if (action.equals("dialogs.disc()")) desc.setText("Show the \"Open Disc\" dialog box.");
      else if (action.equals("dialogs.net()")) desc.setText("Show the \"Open Network Stream\" dialog box.");  
      else if (action.equals("dialogs.messages()")) desc.setText("Show the message log dialog box.");
      else if (action.equals("dialogs.prefs()")) desc.setText("Show the preferences dialog box.");
      else if (action.equals("dialogs.fileInfo()")) desc.setText("Show the file information dialog box.");      
      else if (action.equals("dialogs.playlist()")) desc.setText("Show the default (unskinned) playlist window.");
      else if (action.equals("dialogs.streamingWizard()")) desc.setText("Show the \"Streaming Wizard\" dialog box.");
      else if (action.equals("dialogs.popup()")) desc.setText("Show the full popup menu.");
      else if (action.equals("dialogs.audioPopup()")) desc.setText("Show the audio settings popup menu.");
      else if (action.equals("dialogs.videoPopup()")) desc.setText("Show the video settings popup menu.");
      else if (action.equals("dialogs.miscPopup()")) desc.setText("Show a popup menu containing playback control and general options.");
      else if (action.equals("equalizer.enable()")) desc.setText("Enable the equalizer audio filter.");
      else if (action.equals("equalizer.disable()")) desc.setText("Disable the equalizer audio filter.");
      else if (action.equals("vlc.play()")) desc.setText("Play the current playlist item.");
      else if (action.equals("vlc.pause()")) desc.setText("Pause the current playlist item.");
      else if (action.equals("vlc.stop()")) desc.setText("Stop playback of the current playlist item.");
      else if (action.equals("vlc.faster()")) desc.setText("Play the current playlist item faster.");
      else if (action.equals("vlc.slower()")) desc.setText("Play the current playlist item slower.");
      else if (action.equals("vlc.mute()")) desc.setText("Toggle audio muting.");
      else if (action.equals("vlc.volumeUp()")) desc.setText("Increase the volume.");
      else if (action.equals("vlc.volumeDown()")) desc.setText("Reduce the volume.");
      else if (action.equals("vlc.fullscreen()")) desc.setText("Toggle the fullscreen mode.");
      else if (action.equals("vlc.snapshot()")) desc.setText("Take a snapshot.");
      else if (action.equals("vlc.onTop()")) desc.setText("Toggle the \"always on top\" status.");
      else if (action.equals("vlc.minimize()")) desc.setText("Minimize VLC.");
      else if (action.equals("vlc.quit()")) desc.setText("Quit VLC.");
      else if (action.equals("playlist.add()")) desc.setText("Add a new item to the playlist.");
      else if (action.equals("playlist.del()")) desc.setText("Remove the selected items from the playlist.");
      else if (action.equals("playlist.next()")) desc.setText("Play the next item in the playlist.");
      else if (action.equals("playlist.previous()")) desc.setText("Play the previous item in the playlist.");
      else if (action.equals("playlist.sort()")) desc.setText("Sort the playlist alphabetically.");
      else if (action.equals("playlist.load()")) desc.setText("Load an external playlist file.");
      else if (action.equals("playlist.save()")) desc.setText("Save the current playlist to a file.");
      else if (action.equals("dvd.nextTitle()")) desc.setText("Go to the next title on the DVD.");      
      else if (action.equals("dvd.previousTitle()")) desc.setText("Go to the previous title on the DVD.");
      else if (action.equals("dvd.nextChapter()")) desc.setText("Go to the next chapter of the DVD.");
      else if (action.equals("dvd.previousChapter()")) desc.setText("Go to the previous chapter on the DVD.");
      else if (action.equals("dvd.rootMenu()")) desc.setText("Go to the root menu of the DVD.");      
      add(desc);      
    }
    else { // dynamic action => it needs parameters
      JLabel desc = new JLabel(action);
      if (action.indexOf(".setRandom")!=-1) {
        String[] bools = { "Activate", "Deactive" };
        bool_cb = new JComboBox(bools);
        if(action.toUpperCase().indexOf("TRUE")!=-1) bool_cb.setSelectedIndex(0);
        else bool_cb.setSelectedIndex(1);
        add(bool_cb);        
        desc.setText(" that the items in the playlist are played in random order.");
        add(desc);
      }
      else if (action.indexOf(".setLoop")!=-1) {
        String[] bools = { "Activate", "Deactive" };
        bool_cb = new JComboBox(bools);
        if(action.toUpperCase().indexOf("TRUE")!=-1) bool_cb.setSelectedIndex(0);
        else bool_cb.setSelectedIndex(1);
        add(bool_cb);        
        desc.setText(" that the playlist is repeated when its end is reached.");
        add(desc);
      }
      else if (action.indexOf(".setRepeat")!=-1) {
        String[] bools = { "Activate", "Deactive" };
        bool_cb = new JComboBox(bools);
        if(action.toUpperCase().indexOf("TRUE")!=-1) bool_cb.setSelectedIndex(0);
        else bool_cb.setSelectedIndex(1);
        add(bool_cb);        
        desc.setText(" that the current item is repeated after its end is reached");
        add(desc);
      }
      else if (action.indexOf(".show()")!=-1) {
        desc.setText("Show the window with the ID ");
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".show()")));
      }
      else if (action.indexOf(".hide()")!=-1) {
        desc.setText("Hide the window with the ID ");
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".hide()")));
      }
      else if (action.indexOf(".maximize()")!=-1) {
        desc.setText("Maximize the window with the ID ");
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".maximize()")));
      }
      else if (action.indexOf(".unmaximize()")!=-1) {
        desc.setText("Unmaximize the window with the ID ");
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".unmaximize()")));
      }
      else if (action.indexOf(".setLayout")!=-1) {
        desc.setText("Set the Layout of the window with the ID ");
        add(desc);
        add(windowid_tf);
        windowid_tf.setText(action.substring(0,action.indexOf(".setLayout")));
        add(new JLabel(" to "));
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
    setMaximumSize(new Dimension(445,30));
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
