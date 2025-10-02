/**
 * @author Greg W Pola
 * @version 1.0
 */
package com.gwp.util;

// JDK imports

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

/**
 * Document that displays a time value in the format
 * HH:MM:SS AM
 */
public class TimeDocument extends PlainDocument {

  /**
   * Constructor
   */
  public TimeDocument() {
    super();
  }

  /**
   * Inserts some content into the document. Inserting content causes a write lock to be held while the
   * actual changes are taking place, followed by notification to the observers on the thread that
   * grabbed the write lock.
   */
  public void insertString(int offset, String string, AttributeSet attributeSet) throws BadLocationException {
    boolean error = false;

    // sanity check
    if (string == null) return;

    // Build the result string
    String resultString = ((offset > 0) ? getText(0, offset) : "") + string.toUpperCase()
      + getText(offset, getLength() - offset);

    String insertString = string.toUpperCase();

    // Test the input
    if (resultString.length() > 11) {
      error = true;
    } else {
      if (!validateValue(resultString)) {
        error = true;
      }
    }

    // Perform result action
    if (error) {
      Toolkit.getDefaultToolkit().beep();
    } else {
      super.insertString(offset, insertString, attributeSet);
    }
  }

  /**
   * Removes some content from the document. Removing content causes a write lock to be held while the actual
   * changes are taking place. Observers are notified of the change on the thread that called this method.
   */
  public void remove(int offset, int len) throws BadLocationException {
    boolean error = false;

    if (len < 1) return;

    // See if they are trying to remove a static value
    char[] chars = getText(offset, len).toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == ':' || chars[i] == ' ' || chars[i] == 'M') {
        error = true;
      }
    }

    if (error) {
      Toolkit.getDefaultToolkit().beep();
    } else {
      super.remove(offset, len);
    }
  }

  /**
   * Validates the value based on the defined format: HH:MM:SS ?M, where ? can be A or P
   */
  private boolean validateValue(String value) {
    char[] chars = value.toCharArray();
    int hours;
    int minutes;
    int seconds;

    if (value.length() < 9) {
      return false;
    }

    int firstColonIndex = value.indexOf(':');
    int secondColonIndex = value.indexOf(':', firstColonIndex + 1);
    int spaceIndex = value.lastIndexOf(' ');

    // test the colons and space
    if ((firstColonIndex < 0) || (secondColonIndex < 0) || (spaceIndex < 0)) {
      return false;
    }

    // test the number formats
    try {
      int length = firstColonIndex;
      hours = (length < 1) ? 0 : Integer.parseInt(new String(chars, 0, length));
      length = secondColonIndex - firstColonIndex - 1;
      minutes = (length < 1) ? 0 : Integer.parseInt(new String(chars, firstColonIndex + 1, length));
      length = spaceIndex - secondColonIndex - 1;
      seconds = (length < 1) ? 0 : Integer.parseInt(new String(chars, secondColonIndex + 1, length));
    } catch (NumberFormatException nfe) {
      return false;
    }

    // test the number values
    if (hours < 1 || hours > 12) {
      return false;
    }

    if (minutes < 0 || minutes > 59) {
      return false;
    }

    if (seconds < 0 || seconds > 59) {
      return false;
    }

    // test the meridiem
    if ((chars[spaceIndex + 1] != 'A' && chars[spaceIndex + 1] != 'P') || (chars[spaceIndex + 2] != 'M')) {
      return false;
    }

    return true;
  }
}