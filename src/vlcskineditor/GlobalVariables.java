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

/**
 * GlobalVariables
 * Simulates the global variables in VLC
 * @author Daniel Dreibrodt
 */
public class GlobalVariables {
  
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
  boolean vlc_isPaused = true;
  boolean vlc_isSeekable = true;
  boolean vlc_isMute = false;
  boolean vlc_isOnTop = false;
  boolean playlist_isRandom = false;
  boolean playlist_isLoop = false;
  boolean playlist_isRepeat = true;
  boolean dvd_isActive = false;
  
  
  
  /** Creates a new instance of GlobalVariables */
  public GlobalVariables() {
    
  }
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
  public boolean parseBoolean(String b) {
    /** help please*/
    return true;
  }
  
}
