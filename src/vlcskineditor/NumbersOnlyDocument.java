/*****************************************************************************
 * NumbersOnlyDocument.java
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

import javax.swing.text.*;

/**
 * NumbersOnlyDocument
 * allows only numbers to be entered into a component
 * @see javax.swing.text.Document
 * @author Daniel Dreibrodt
 */
public class NumbersOnlyDocument extends PlainDocument{

  boolean allow_negative = true;
  
  /** Creates a new instance of NumbersOnlyDocument */
  public NumbersOnlyDocument() {
    super();
  }
  /**
   * Creates a new instance of NumbersOnlyDocument that allows only positive numbers if <pre>negative</pre> is false*
   */
  public NumbersOnlyDocument(boolean negative) {
    super();
    allow_negative = negative;
  }  
  /** Does nothing if <pre>str</pre> is not a digit or a dash, otherwise calls the super function **/
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    if(str.matches("[-]") && allow_negative) {
      if(offset>0) return;
    }
    else if (str.matches("[^0-9]")) {      
      return;
    }
    super.insertString(offset,str,a);
  }
      
}
