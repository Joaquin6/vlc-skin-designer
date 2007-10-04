/*****************************************************************************
 * GlobalVariables.java
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

import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * GlobalVariables
 * Simulates the global variables in VLC
 * @author Daniel Dreibrodt
 */
public class GlobalVariables implements ActionListener{
  
  /** The audio stream bitrate **/
  public String $B = "128";
  /** Value of the volume **/
  public String $V = "50";
  /** Current time **/
  public String $T = "0:55:55";
  public String $t = "55:55";
  /** Remaining time **/
  public String $L = "0:44:44";
  public String $l = "44:44";
  /** Duration **/
  public String $D = "0:99:99";
  public String $d = "99:99";
  /** Value of the help attribute **/
  public String $H = "Help text";
  /** Name of the stream that is played **/
  public String $N = "Artist - Title";
  /** Full name of stream with path **/
  public String $F = "http://www.example.com/Artist - Title.mp3";
  /** The audio sample rate (in kHz) **/
  public String $S = "44";
  
  boolean equalizer_isEnabled = false;
  boolean vlc_hasVout = false;
  boolean vlc_hasAudio = true;
  boolean vlc_isFullscreen = false;
  boolean vlc_isPlaying = false;
  boolean vlc_isStopped = false;
  boolean vlc_isPaused = true;
  boolean vlc_isSeekable = true;
  boolean vlc_isMute = false;
  boolean vlc_isOnTop = false;
  boolean playlist_isRandom = false;
  boolean playlist_isLoop = false;
  boolean playlist_isRepeat = true;
  boolean dvd_isActive = false;
  
  float slider_value = 0f;
  
  JFrame frame;
  JComboBox eq_cb,vout_cb,audio_cb,fullscreen_cb,playing_cb,stopped_cb,
            paused_cb,seekable_cb,mute_cb,ontop_cb,random_cb,loop_cb,repeat_cb,dvd_cb;
  JSlider slider_s;
  JButton ok_btn,help_btn;
  
  /** Creates a new instance of GlobalVariables */
  public GlobalVariables() {
    
  }
  /**
   * Parses text variables in a string
   * @param p The string to parse
   * @return The parsed string
   */
  public String parseString(String p) {
    p = p.replaceAll("\\$B",$B);
    p = p.replaceAll("\\$V",$V);
    p = p.replaceAll("\\$T",$T);
    p = p.replaceAll("\\$t",$t);
    p = p.replaceAll("\\$L",$L);
    p = p.replaceAll("\\$l",$l);
    p = p.replaceAll("\\$D",$D);
    p = p.replaceAll("\\$d",$d);
    p = p.replaceAll("\\$H",$H);
    p = p.replaceAll("\\$N",$N);
    p = p.replaceAll("\\$F",$F);
    p = p.replaceAll("\\$S",$S);
    return p;    
  }
  /**
   * Parses a boolean condition and finds out whether it is true
   * Based on vlc/trunk/modules/gui/skins2/parser/interpreter.cpp
   * Original code by Cyril Deguet (asmax_at_via.ecp.fr)
   * @param bool The boolean expression to parse
   * @return The evaluated expression
   */
  public boolean parseBoolean(String bool) {
    String rName = bool;
    rName = rName.replaceAll("equalizer.isEnabled",String.valueOf(equalizer_isEnabled));
    rName = rName.replaceAll("vlc.hasVout",String.valueOf(vlc_hasVout));
    rName = rName.replaceAll("vlc.hasAudio",String.valueOf(vlc_hasAudio));
    rName = rName.replaceAll("vlc.isFullscreen",String.valueOf(vlc_isFullscreen));
    rName = rName.replaceAll("vlc.isPlaying",String.valueOf(vlc_isPlaying));
    rName = rName.replaceAll("vlc.isPaused",String.valueOf(vlc_isPaused));
    rName = rName.replaceAll("vlc.isSeekable",String.valueOf(vlc_isSeekable));
    rName = rName.replaceAll("vlc.isMute",String.valueOf(vlc_isMute));
    rName = rName.replaceAll("vlc.isOnTop",String.valueOf(vlc_isOnTop));
    rName = rName.replaceAll("playlist.isRandom",String.valueOf(playlist_isRandom));
    rName = rName.replaceAll("playlist.isLoop",String.valueOf(playlist_isLoop));
    rName = rName.replaceAll("playlist.isRepeat",String.valueOf(playlist_isRepeat));
    rName = rName.replaceAll("dvd.isActive",String.valueOf(dvd_isActive));
    
    //Now all expressions that VLC Skin Editor supports are replaced by true or false
    //The remaining expressions like WindowID.isVisible are automatically resolved to false
    
    List<Boolean> varStack = new ArrayList<Boolean>();
    
    // Convert the expression into Reverse Polish Notation
    BooleanExpressionEvaluator evaluator = new BooleanExpressionEvaluator();
    evaluator.parse(rName);
    // Get the first token from the RPN stack
    String token = evaluator.getToken();
    while(!token.isEmpty()) {
      //System.out.println("  current token: "+token);
      if(token.equals("and")) {
        // Get the 2 last variables on the stack
        if(varStack.isEmpty()) {
          System.err.println("invalid boolean expression: "+rName);
          return false;
        }
        Boolean pVar1 = varStack.get(varStack.size()-1);
        varStack.remove(varStack.get(varStack.size()-1));
        if(varStack.isEmpty()) {
          System.err.println("invalid boolean expression: "+rName);
          return false;
        }
        Boolean pVar2 = varStack.get(varStack.size()-1);
        varStack.remove(varStack.get(varStack.size()-1));
        //Add the result of (pVar1 && pVar2) to the varStack
        if(pVar1.booleanValue() && pVar2.booleanValue()) {          
          varStack.add(Boolean.TRUE);
        }
        else {          
          varStack.add(Boolean.FALSE);
        }        
      }
      else if(token.equals("or")) {
        // Get the 2 last variables on the stack
        if(varStack.isEmpty()) {          
          return false;
        }
        Boolean pVar1 = varStack.get(varStack.size()-1);
        varStack.remove(varStack.get(varStack.size()-1));
        if(varStack.isEmpty()) {          
          return false;
        }
        Boolean pVar2 = varStack.get(varStack.size()-1);
        varStack.remove(varStack.get(varStack.size()-1));
        //Add the result of (pVar1 || pVar2) to the varStack
        if(pVar1.booleanValue() || pVar2.booleanValue()) {          
          varStack.add(Boolean.TRUE);
        }
        else {          
          varStack.add(Boolean.FALSE);
        }  
      }
      else if(token.equals("not")) {
        // Get the last variables on the stack
        if(varStack.isEmpty()) {
          System.err.println("invalid boolean expression: "+rName);
          return false;
        }
        Boolean pVar1 = varStack.get(varStack.size()-1);
        varStack.remove(varStack.get(varStack.size()-1));        
        //Add the result of (!pVar1) to the varStack
        if(!pVar1.booleanValue()) {          
          varStack.add(Boolean.TRUE);
        }
        else {          
          varStack.add(Boolean.FALSE);
        }  
      }
      else {        
        varStack.add(new Boolean(token));
      }
      // Get the first token from the RPN stack
      token = evaluator.getToken();
    }
    // The stack should contain a single variable
    if(varStack.size()!=1) {
      System.err.println("invalid boolean expression: "+rName);
      return false;
    }
    //System.out.println("The boolean expression: "+bool+" was resolved to "+rName+" and parsed as "+varStack.get(varStack.size()-1).toString());
    return varStack.get(varStack.size()-1).booleanValue();
  }
  /** Creates and shows the dialog to modify the global variables */
  public void showOptions() {
    if(frame==null) {
      frame = new JFrame("Global variables");
      frame.setResizable(false);
      frame.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
      
      JLabel desc_l = new JLabel("These variables only affect the preview.");
      Object[] bool_values = { true, false };
      JLabel eq_l = new JLabel("equalizer.isEnabled");
      eq_cb = new JComboBox(bool_values);
      JLabel vout_l = new JLabel("vlc.hasVout");
      vout_cb = new JComboBox(bool_values);
      JLabel audio_l = new JLabel("vlc.hasAudio");
      audio_cb = new JComboBox(bool_values);
      JLabel fullscreen_l = new JLabel("vlc.isFullscreen");
      fullscreen_cb = new JComboBox(bool_values);
      JLabel playing_l = new JLabel("vlc.isPlaying");
      playing_cb = new JComboBox(bool_values);
      JLabel stopped_l = new JLabel("vlc.isStopped");
      stopped_cb = new JComboBox(bool_values);
      JLabel paused_l = new JLabel("vlc.isPaused");
      paused_cb = new JComboBox(bool_values);
      JLabel seekable_l = new JLabel("vlc.isSeekable");
      seekable_cb = new JComboBox(bool_values);
      JLabel mute_l = new JLabel("vlc.isMute");
      mute_cb = new JComboBox(bool_values);
      JLabel ontop_l = new JLabel("vlc.isOnTop");
      ontop_cb = new JComboBox(bool_values);
      JLabel random_l = new JLabel("playlist.isRandom");
      random_cb = new JComboBox(bool_values);
      JLabel loop_l = new JLabel("vlc.isLoop");
      loop_cb = new JComboBox(bool_values);
      JLabel repeat_l = new JLabel("vlc.isRepeat");
      repeat_cb = new JComboBox(bool_values);
      JLabel dvd_l = new JLabel("dvd.isActive");
      dvd_cb = new JComboBox(bool_values);
      JLabel slider_l = new JLabel("Slider position");
      slider_s = new JSlider(JSlider.HORIZONTAL,0,100,0);
      slider_s.setMajorTickSpacing(10);
      slider_s.setMinorTickSpacing(5);
      slider_s.setPaintTicks(true);
      slider_s.setSize(100,20);

      
      ok_btn = new JButton("OK");
      ok_btn.addActionListener(this);
      help_btn = new JButton("Help");
      help_btn.addActionListener(this);
      
      frame.add(desc_l);
      JPanel panel = new JPanel(new GridLayout(0,2,5,5));      
      panel.add(eq_l);
      panel.add(eq_cb);
      panel.add(vout_l);
      panel.add(vout_cb);
      panel.add(audio_l);
      panel.add(audio_cb);
      panel.add(fullscreen_l);
      panel.add(fullscreen_cb);
      panel.add(playing_l);
      panel.add(playing_cb);
      panel.add(stopped_l);
      panel.add(stopped_cb);
      panel.add(paused_l);
      panel.add(paused_cb);
      panel.add(seekable_l);
      panel.add(seekable_cb);
      panel.add(mute_l);
      panel.add(mute_cb);
      panel.add(ontop_l);
      panel.add(ontop_cb);
      panel.add(random_l);
      panel.add(random_cb);
      panel.add(loop_l);
      panel.add(loop_cb);
      panel.add(repeat_l);
      panel.add(repeat_cb);
      panel.add(dvd_l);
      panel.add(dvd_cb);
      panel.add(slider_l);
      panel.add(slider_s);
      panel.add(ok_btn);
      panel.add(help_btn);      
      frame.add(panel);
      frame.pack();
      
      //frame.setSize(frame.getWidth()/2,frame.getHeight()+20);
    }
    eq_cb.setSelectedItem(equalizer_isEnabled);
    vout_cb.setSelectedItem(vlc_hasVout);
    audio_cb.setSelectedItem(vlc_hasAudio);
    fullscreen_cb.setSelectedItem(vlc_isFullscreen);
    playing_cb.setSelectedItem(vlc_isPlaying);
    stopped_cb.setSelectedItem(vlc_isStopped);
    paused_cb.setSelectedItem(vlc_isPaused);
    seekable_cb.setSelectedItem(vlc_isSeekable);
    mute_cb.setSelectedItem(vlc_isMute);
    ontop_cb.setSelectedItem(vlc_isOnTop);
    random_cb.setSelectedItem(playlist_isRandom);
    loop_cb.setSelectedItem(playlist_isLoop);
    repeat_cb.setSelectedItem(playlist_isRepeat);
    dvd_cb.setSelectedItem(dvd_isActive);
    slider_s.setValue((int)(slider_value*100));
    frame.setVisible(true);
  }
  /**
   * Sets the variables to the selected values
   */
  public void update() {
    equalizer_isEnabled = Boolean.parseBoolean(eq_cb.getSelectedItem().toString());
    vlc_hasVout = Boolean.parseBoolean(vout_cb.getSelectedItem().toString());
    vlc_hasAudio = Boolean.parseBoolean(audio_cb.getSelectedItem().toString());
    vlc_isFullscreen = Boolean.parseBoolean(fullscreen_cb.getSelectedItem().toString());
    vlc_isPlaying = Boolean.parseBoolean(playing_cb.getSelectedItem().toString());
    vlc_isStopped = Boolean.parseBoolean(stopped_cb.getSelectedItem().toString());
    vlc_isPaused = Boolean.parseBoolean(paused_cb.getSelectedItem().toString());
    vlc_isSeekable = Boolean.parseBoolean(seekable_cb.getSelectedItem().toString());
    vlc_isMute = Boolean.parseBoolean(mute_cb.getSelectedItem().toString());
    vlc_isOnTop = Boolean.parseBoolean(ontop_cb.getSelectedItem().toString());
    playlist_isRandom = Boolean.parseBoolean(random_cb.getSelectedItem().toString());
    playlist_isLoop = Boolean.parseBoolean(loop_cb.getSelectedItem().toString());
    playlist_isRepeat = Boolean.parseBoolean(repeat_cb.getSelectedItem().toString());
    dvd_isActive = Boolean.parseBoolean(dvd_cb.getSelectedItem().toString());
    slider_value = (float)(slider_s.getValue())/100;
  }
  /**
   * Handles actions triggered by components listened to
   */
  public void actionPerformed(ActionEvent e) {
    if(e.getSource().equals(ok_btn)) {
      update();
      frame.setVisible(false);
    }
    else if(e.getSource().equals(help_btn)) {
      Desktop desktop;
      if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              desktop.browse(new java.net.URI("http://www.videolan.org/vlc/skins2-create.html#boolexpr"));
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(null,ex.toString(),ex.getMessage(),JOptionPane.ERROR_MESSAGE);    
            }
      }
      else {
        JOptionPane.showMessageDialog(null,"Could not launch Browser","Go to the following URL manually:\nhttp://www.videolan.org/vlc/skins2-create.html",JOptionPane.WARNING_MESSAGE);    
      }
    }
  }
  /**
   * Gets the slider value
   */
  public float getSliderValue() {
    return slider_value;
  }
}
