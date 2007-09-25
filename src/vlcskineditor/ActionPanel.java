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

/**
 * ActionPanel
 * @author Daniel Dreibrodt
 */
public class ActionPanel extends JPanel {
  ActionEditor ae;
  String action;
  String action_type = "static";
  JTextField param1,param2;
  /** Creates a new instance of ActionPanel */
  public ActionPanel(String act,ActionEditor ae_) {
    action=act;
    ae=ae_;
    if(act.indexOf(".set")!=-1) action_type="dynamic"; // all playlist.setXXXX(boolean)
    else if(act.indexOf(".show()")!=-1) action_type="dynamic";    
    else if(act.indexOf(".hide()")!=-1) action_type="dynamic";    
    else if(act.indexOf("maximize()")!=-1) action_type="dynamic";    //.unmaximize() and .maximize();
    else if(act.indexOf(".setLayout")!=-1) action_type="dynamic";    
    if(action_type=="static") {
      JLabel desc = new JLabel(action);
      if(action=="none") desc.setText("No action");
      else if(action=="dialogs.changeSkin()") desc.setText("Show a dialog box to load a new skin.");
      else if(action=="dialogs.fileSimple()") desc.setText("Show the simple \"Open File\" dialog box.");
      else if(action=="dialogs.file()") desc.setText("Show the extended \"Open File\" dialog box.");
      else if(action=="dialogs.directory()") desc.setText("Show the \"Open Directory\" dialog box.");
      else if(action=="dialogs.disc()") desc.setText("Show the \"Open Disc\" dialog box.");
      else if(action=="dialogs.net()") desc.setText("Show the \"Open Network Stream\" dialog box.");  
      else if(action=="dialogs.messages()") desc.setText("Show the message log dialog box.");
      else if(action=="dialogs.prefs()") desc.setText("Show the preferences dialog box.");
      else if(action=="dialogs.fileInfo()") desc.setText("Show the file information dialog box.");      
      else if(action=="dialogs.playlist()") desc.setText("Show the default (unskinned) playlist window.");
      else if(action=="dialogs.streamingWizard()") desc.setText("Show the \"Streaming Wizard\" dialog box.");
      else if(action=="dialogs.popup()") desc.setText("Show the full popup menu.");
      else if(action=="dialogs.audioPopup()") desc.setText("Show the audio settings popup menu.");
      else if(action=="dialogs.videoPopup()") desc.setText("Show the video settings popup menu.");
      else if(action=="dialogs.miscPopup()") desc.setText("Show a popup menu containing playback control and general options.");
      else if(action=="equalizer.enable()") desc.setText("Enable the equalizer audio filter");
      else if(action=="equalizer.disable()") desc.setText("Enable the equalizer audio filter");
      else if(action=="vlc.play()") desc.setText("Play the current playlist item.");
      else if(action=="vlc.pause()") desc.setText("Pause the current playlist item.");
      else if(action=="vlc.stop()") desc.setText("Stop playback of the current playlist item.");
      else if(action=="vlc.faster()") desc.setText("Play the current playlist item faster.");
      else if(action=="vlc.slower()") desc.setText("Play the current playlist item slower.");
      else if(action=="vlc.mute()") desc.setText("Toggle audio muting.");
      else if(action=="vlc.volumeUp()") desc.setText("Increase the volume.");
      else if(action=="vlc.volumeDown()") desc.setText("Reduce the volume.");
    }
    else {
      
    }
  }
  public String getActionCode() {
    if(action_type=="static") return action;
    else return "";
  }
}
