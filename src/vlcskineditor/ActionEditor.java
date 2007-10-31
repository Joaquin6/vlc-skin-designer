/*****************************************************************************
 * ActionEditor.java
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
import java.awt.event.*;
import java.util.*;

/**
 * A window representing a specific action chain, that allows the user to add
 * actions to the chain. Actions are edited within the ActionPanels generated
 * for each action in the chain
 * @see ActionPanel
 * @author Daniel Dreibrodt
 */
public class ActionEditor extends JFrame implements ActionListener{
  
  //The actions in the chain, respectively the Panels representing them 
  java.util.List<ActionPanel> aPanels = new LinkedList<ActionPanel>();  
  
  public ImageIcon add_icon = createIcon("icons/add.png");
  public ImageIcon delete_icon = createIcon("icons/delete.png");  
  
  //The panel that handels the scrolling through the list of actions
  JScrollPane sPane;
  //The panel containing the actual ActionPanels
  JPanel actions_p = new JPanel();
  //Opens a PopupMenu from which the user can add an action to the end of the chain
  JButton add_btn = new JButton(add_icon);  
  JButton ok_btn = new JButton("OK");
  
  JPopupMenu actions_pu = new JPopupMenu("Actions");
  JMenu actions_dialogs = new JMenu("Show a dialog");
  JMenu actions_playlist = new JMenu("Playlist");
  JMenu actions_dvd = new JMenu("DVD");
  JMenu actions_windows = new JMenu("Skin");  
  JMenuItem actions_vlc_play = new JMenuItem("Play");
  JMenuItem actions_vlc_pause = new JMenuItem("Pause");
  JMenuItem actions_vlc_stop = new JMenuItem("Stop");
  JMenuItem actions_vlc_faster = new JMenuItem("Play faster");
  JMenuItem actions_vlc_slower = new JMenuItem("Play slower");
  JMenuItem actions_vlc_mute = new JMenuItem("Mute the volume");
  JMenuItem actions_vlc_volumeUp = new JMenuItem("Increase the volume");
  JMenuItem actions_vlc_volumeDown = new JMenuItem("Reduce the volume");
  JMenuItem actions_vlc_fullscreen = new JMenuItem("Toggle fullscreen mode");
  JMenuItem actions_vlc_snapshot = new JMenuItem("Take a snapshot");
  JMenuItem actions_vlc_onTop = new JMenuItem("Toggle always on top");
  JMenuItem actions_vlc_minimize = new JMenuItem("Minimize VLC");
  JMenuItem actions_vlc_quit = new JMenuItem("Quit VLC");  
  JMenuItem actions_eq_enable = new JMenuItem("Enable the equalizer");
  JMenuItem actions_eq_disable = new JMenuItem("Disable the equalizer");
  JMenuItem actions_dialogs_changeSkin = new JMenuItem("Change skin");
  JMenuItem actions_dialogs_fileSimple = new JMenuItem("Open file (simple)");
  JMenuItem actions_dialogs_file = new JMenuItem("Open file (extended)");
  JMenuItem actions_dialogs_directory = new JMenuItem("Open directory");
  JMenuItem actions_dialogs_disc = new JMenuItem("Open disc");
  JMenuItem actions_dialogs_net = new JMenuItem("Open network stream");
  JMenuItem actions_dialogs_messages = new JMenuItem("Message log");
  JMenuItem actions_dialogs_prefs = new JMenuItem("Preferences");
  JMenuItem actions_dialogs_fileInfo = new JMenuItem("File information");
  JMenuItem actions_dialogs_playlist = new JMenuItem("Unskinned playlist");
  JMenuItem actions_dialogs_streamingWizard = new JMenuItem("Streaming Wizard");
  JMenuItem actions_dialogs_popup = new JMenuItem("Full popup menu");
  JMenuItem actions_dialogs_audioPopup = new JMenuItem("Audio settings popup menu");
  JMenuItem actions_dialogs_videoPopup = new JMenuItem("Video settings popup menu");
  JMenuItem actions_dialogs_miscPopup = new JMenuItem("Miscellaneous settings popup menu");
  JMenuItem actions_playlist_add = new JMenuItem("Add a new item");
  JMenuItem actions_playlist_del = new JMenuItem("Remove selected items");
  JMenuItem actions_playlist_next = new JMenuItem("Play next item");
  JMenuItem actions_playlist_previous = new JMenuItem("Play previous item");
  JMenuItem actions_playlist_setRandom = new JMenuItem("Play in random/normal order");
  JMenuItem actions_playlist_setLoop = new JMenuItem("Loop playlist/play it once");
  JMenuItem actions_playlist_setRepeat = new JMenuItem("Repeat current item/play it once");
  JMenuItem actions_playlist_sort = new JMenuItem("Sort alphabetically");
  JMenuItem actions_playlist_load = new JMenuItem("Load from file");
  JMenuItem actions_playlist_save = new JMenuItem("Save to file");
  JMenuItem actions_dvd_nextTitle = new JMenuItem("Next title");
  JMenuItem actions_dvd_previousTitle = new JMenuItem("Previous title");
  JMenuItem actions_dvd_nextChapter = new JMenuItem("Next chapter");
  JMenuItem actions_dvd_previousChapter = new JMenuItem("Previous Chapter");
  JMenuItem actions_dvd_rootMenu = new JMenuItem("Main menu");
  JMenuItem actions_windows_show = new JMenuItem("Show a window");
  JMenuItem actions_windows_hide = new JMenuItem("Hide a window");
  JMenuItem actions_windows_maximize = new JMenuItem("Maximize a window");
  JMenuItem actions_windows_unmaximize = new JMenuItem("Unmaximize a window");
  JMenuItem actions_windows_setLayout = new JMenuItem("Change the layout of a window");
  
  //The skin item to which the represented action chain belongs
  Item parent;
  
  /** Creates a new instance of ActionEditor */
  public ActionEditor(Item p) {
    super("Edit action sequence");  
        
    setLayout(null);    
    setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("icons/editor.png")));
    
    parent = p;
    
    actions_pu.add(actions_vlc_play);
    actions_vlc_play.addActionListener(this);
    actions_vlc_play.setActionCommand("vlc.play()");
    actions_pu.add(actions_vlc_pause);
    actions_vlc_pause.addActionListener(this);
    actions_vlc_pause.setActionCommand("vlc.pause()");
    actions_pu.add(actions_vlc_stop);
    actions_vlc_stop.addActionListener(this);
    actions_vlc_stop.setActionCommand("vlc.stop()");
    actions_pu.add(actions_vlc_faster);
    actions_vlc_faster.addActionListener(this);
    actions_vlc_faster.setActionCommand("vlc.faster()");
    actions_pu.add(actions_vlc_slower);
    actions_vlc_slower.addActionListener(this);
    actions_vlc_slower.setActionCommand("vlc.slower()");
    actions_pu.add(actions_vlc_mute);
    actions_vlc_mute.addActionListener(this);
    actions_vlc_mute.setActionCommand("vlc.mute()");
    actions_pu.add(actions_vlc_volumeUp);
    actions_vlc_volumeUp.addActionListener(this);
    actions_vlc_volumeUp.setActionCommand("vlc.volumeUp()");
    actions_pu.add(actions_vlc_volumeDown);
    actions_vlc_volumeDown.addActionListener(this);
    actions_vlc_volumeDown.setActionCommand("vlc.volumeDown()");
    actions_pu.add(actions_vlc_fullscreen);
    actions_vlc_fullscreen.addActionListener(this);
    actions_vlc_fullscreen.setActionCommand("vlc.fullscreen()");
    actions_pu.add(actions_vlc_snapshot);
    actions_vlc_snapshot.addActionListener(this);
    actions_vlc_snapshot.setActionCommand("vlc.snapshot()");
    actions_pu.add(actions_vlc_minimize);
    actions_vlc_minimize.addActionListener(this);
    actions_vlc_minimize.setActionCommand("vlc.minimize()");
    actions_pu.add(actions_vlc_quit);
    actions_vlc_quit.addActionListener(this);
    actions_vlc_quit.setActionCommand("vlc.quit()");
    actions_pu.add(actions_eq_enable);
    actions_eq_enable.addActionListener(this);
    actions_eq_enable.setActionCommand("equalizer.enable()");
    actions_pu.add(actions_eq_disable);
    actions_eq_disable.addActionListener(this);
    actions_eq_disable.setActionCommand("equalizer.disable()");
    
    actions_pu.addSeparator();
    
    actions_dialogs.add(actions_dialogs_changeSkin);
    actions_dialogs_changeSkin.addActionListener(this);
    actions_dialogs_changeSkin.setActionCommand("dialogs.changeSkin()");
    actions_dialogs.add(actions_dialogs_fileSimple);
    actions_dialogs_fileSimple.addActionListener(this);    
    actions_dialogs_fileSimple.setActionCommand("dialogs.fileSimple()");
    actions_dialogs.add(actions_dialogs_file);
    actions_dialogs_file.addActionListener(this);
    actions_dialogs_file.setActionCommand("dialogs.file()");
    actions_dialogs.add(actions_dialogs_disc);
    actions_dialogs_disc.addActionListener(this);
    actions_dialogs_disc.setActionCommand("dialogs.disc()");
    actions_dialogs.add(actions_dialogs_net);
    actions_dialogs_net.addActionListener(this);
    actions_dialogs_net.setActionCommand("dialogs.net()");
    actions_dialogs.add(actions_dialogs_directory);
    actions_dialogs_directory.addActionListener(this);
    actions_dialogs_directory.setActionCommand("dialogs.directory()");
    actions_dialogs.add(actions_dialogs_messages);
    actions_dialogs_messages.addActionListener(this);
    actions_dialogs_messages.setActionCommand("dialogs.messages()");
    actions_dialogs.add(actions_dialogs_fileInfo);
    actions_dialogs_fileInfo.addActionListener(this);
    actions_dialogs_fileInfo.setActionCommand("dialogs.fileInfo()");
    actions_dialogs.add(actions_dialogs_prefs);
    actions_dialogs_prefs.addActionListener(this);
    actions_dialogs_prefs.setActionCommand("dialogs.prefs()");
    actions_dialogs.add(actions_dialogs_playlist);
    actions_dialogs_playlist.addActionListener(this);
    actions_dialogs_playlist.setActionCommand("dialogs.playlist()");
    actions_dialogs.add(actions_dialogs_streamingWizard);
    actions_dialogs_streamingWizard.addActionListener(this);
    actions_dialogs_streamingWizard.setActionCommand("dialogs.streamingWizard()");
    actions_dialogs.add(actions_dialogs_popup);
    actions_dialogs_popup.addActionListener(this);
    actions_dialogs_popup.setActionCommand("dialogs.popup()");
    actions_dialogs.add(actions_dialogs_audioPopup);
    actions_dialogs_audioPopup.addActionListener(this);
    actions_dialogs_audioPopup.setActionCommand("dialogs.audioPopup()");
    actions_dialogs.add(actions_dialogs_videoPopup);
    actions_dialogs_videoPopup.addActionListener(this);
    actions_dialogs_videoPopup.setActionCommand("dialogs.videoPopup()");
    actions_dialogs.add(actions_dialogs_miscPopup);
    actions_dialogs_miscPopup.addActionListener(this);    
    actions_dialogs_miscPopup.setActionCommand("dialogs.miscPopup()");
    
    actions_pu.add(actions_dialogs);
    
    actions_playlist.add(actions_playlist_add);
    actions_playlist_add.addActionListener(this);
    actions_playlist_add.setActionCommand("playlist.add()");
    actions_playlist.add(actions_playlist_del);
    actions_playlist_del.addActionListener(this);
    actions_playlist_del.setActionCommand("playlist.del()");
    actions_playlist.add(actions_playlist_next);
    actions_playlist_next.addActionListener(this);
    actions_playlist_next.setActionCommand("playlist.next()");
    actions_playlist.add(actions_playlist_previous);
    actions_playlist_previous.addActionListener(this);
    actions_playlist_previous.setActionCommand("playlist.previous()");
    actions_playlist.add(actions_playlist_setRandom);
    actions_playlist_setRandom.addActionListener(this);
    actions_playlist_setRandom.setActionCommand("playlist.setRandom(true)");
    actions_playlist.add(actions_playlist_setLoop);
    actions_playlist_setLoop.addActionListener(this);
    actions_playlist_setLoop.setActionCommand("playlist.setLoop(true)");
    actions_playlist.add(actions_playlist_setRepeat);
    actions_playlist_setRepeat.addActionListener(this);
    actions_playlist_setRepeat.setActionCommand("playlist.setRepeat(true)");
    actions_playlist.add(actions_playlist_sort);
    actions_playlist_sort.addActionListener(this);
    actions_playlist_sort.setActionCommand("playlist.sort()");
    actions_playlist.add(actions_playlist_load);
    actions_playlist_load.addActionListener(this);
    actions_playlist_load.setActionCommand("playlist.load()");
    actions_playlist.add(actions_playlist_save);
    actions_playlist_save.addActionListener(this);
    actions_playlist_save.setActionCommand("playlist.save()");    
          
    actions_pu.add(actions_playlist);
    
    actions_dvd.add(actions_dvd_nextTitle);
    actions_dvd_nextTitle.addActionListener(this);
    actions_dvd_nextTitle.setActionCommand("dvd.nextTitle()");
    actions_dvd.add(actions_dvd_previousTitle);
    actions_dvd_previousTitle.addActionListener(this);
    actions_dvd_previousTitle.setActionCommand("dvd.previousTitle()");
    actions_dvd.add(actions_dvd_nextChapter);
    actions_dvd_nextChapter.addActionListener(this);
    actions_dvd_nextChapter.setActionCommand("dvd.nextChapter()");
    actions_dvd.add(actions_dvd_previousChapter);
    actions_dvd_previousChapter.addActionListener(this);
    actions_dvd_previousChapter.setActionCommand("dvd.previousChapter()");
    actions_dvd.add(actions_dvd_rootMenu);    
    actions_dvd_rootMenu.addActionListener(this);
    actions_dvd_rootMenu.setActionCommand("dvd.rootMenu()");
    
    actions_pu.add(actions_dvd);
    
    actions_windows.add(actions_windows_show);
    actions_windows_show.addActionListener(this);
    actions_windows_show.setActionCommand(".show()");
    actions_windows.add(actions_windows_hide);
    actions_windows_hide.addActionListener(this);
    actions_windows_hide.setActionCommand(".hide()");
    actions_windows.add(actions_windows_maximize);
    actions_windows_maximize.addActionListener(this);
    actions_windows_maximize.setActionCommand(".maximize()");
    actions_windows.add(actions_windows_unmaximize);
    actions_windows_unmaximize.addActionListener(this);
    actions_windows_unmaximize.setActionCommand(".unmaximize()");
    actions_windows.add(actions_windows_setLayout);
    actions_windows_setLayout.addActionListener(this);
    actions_windows_setLayout.setActionCommand(".setLayout()");
    
    actions_pu.add(actions_windows);
    
    actions_p.setLayout(new BoxLayout(actions_p,BoxLayout.Y_AXIS));       
   
    sPane = new JScrollPane(actions_p);    
    ok_btn.addActionListener(this);
    add_btn.addActionListener(this);  
    
    add(sPane);    
    add(add_btn);
    add(ok_btn);   
    
    sPane.setBounds(5,5,445,320);
    add_btn.setBounds(5,330,50,25);
    ok_btn.setBounds(400,330,50,25);
    setSize(460,385);    
    setResizable(false);    
  }
  /**
   * Generates the code by calling the getActionCode() function of each ActionPanel
   * @see ActionPanel#getActionCode()
   * @return A string readable by VLC representing the action chain
   */
  public String getCode() {     
    String code = "";
    for(ActionPanel act:aPanels) code+=act.getActionCode()+";";
    return code;
  }
  /**
   * Replaces the current action chain by a new one
   * @param a The action string
   */
  public void updateActions(String a) {
    actions_p.removeAll();
    aPanels.clear();
    String[] actions = a.split(";");
    for(String act:actions) addAction(act);
    actions_p.updateUI();
  }
  /**
   * Adds an action to the chain
   * @param act The action string
   */
  public void addAction(String act) {
    act=act.trim();
    act=act.replaceAll(";","");
    aPanels.add(new ActionPanel(act,this));
    actions_p.add(aPanels.get(aPanels.size()-1));
    actions_p.updateUI();
  }
  /**
   * Shows the dialog for editing the given action
   * @param a The action string to be edited
   */
  public void editAction(String a) {
    updateActions(a);
    setVisible(true);
  }
  /**
   * Reacts to user interaction
   */
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(add_btn)) actions_pu.show(add_btn,0,0);
    else if (e.getSource().equals(ok_btn)) {
      parent.actionWasEdited(this);
      setVisible(false);
    }
    else if (e.getSource().getClass().equals(JMenuItem.class)) {
      addAction(e.getActionCommand());
    }
    else {
      for(ActionPanel ap:aPanels) {
        if(e.getSource().equals(ap.del_btn)) {
          actions_p.remove(ap);
          aPanels.remove(ap);
          actions_p.updateUI();
          break;
        }
      }
    }
  }
  /**
   * Creates an ImageIcon out of a file
   */
  public ImageIcon createIcon(String filename) {
      java.awt.Image img = null;
      try {
        img = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource(filename));
        //img = ImageIO.read(file);
        return new ImageIcon(img);  
      } catch (Exception e) {
        System.out.println(e);
        return null;
      }
  }
  /**
   * For testing purposes
   */
  public static void main (String[] args) {
    try {	
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } 
    catch (Exception e) {
      
    }
    JFrame.setDefaultLookAndFeelDecorated(true);
    ActionEditor ae = new ActionEditor(null);
    ae.setDefaultCloseOperation(ae.EXIT_ON_CLOSE);
    /*ae.editAction("none;dialogs.changeSkin();dialogs.fileSimple();dialogs.file();dialogs.directory();dialogs.disc();dialogs.net();dialogs.messages();dialogs.prefs();dialogs.fileInfo();dialogs.playlist();dialogs.streamingWizard();dialogs.popup();dialogs.audioPopup();dialogs.videoPopup();dialogs.miscPopup();equalizer.enable();equalizer.disable();vlc.play();vlc.pause();vlc.stop();vlc.faster();vlc.slower();vlc.mute();" +
                                       "vlc.volumeUp();vlc.volumeDown();vlc.fullscreen();vlc.snapshot();vlc.onTop();vlc.minimize();vlc.quit();playlist.add();playlist.del();playlist.next();playlist.previous();playlist.sort();playlist.setRandom(true);playlist.setLoop(true);playlist.setRepeat(true);playlist.load();playlist.save();dvd.nextTitle();dvd.previousTitle();dvd.nextChapter();dvd.previousChapter();dvd.rootMenu();WindowID.show();WindowID.hide();" +
                                       "WindowID.maximize();WindowID.unmaximize();WindowID.setLayout(LayoutID)");
    */
    ae.editAction("none");
  }
}
