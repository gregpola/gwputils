package com.gwp.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;



/**
 * The <code>LengthLimitDocument</code> class provides
 * a way to limit the input text to a given length.
 *
 * @author  Greg Pola
 * @version 1.0
 */
public class LengthLimitDocument extends PlainDocument {

    private int maxLength;

    /**
     * Constructor.  Creates a PlainDocument used by Text Fields
     * to limit entries to a maximum length.
     *
     * @param max the maximum length
     */
    public LengthLimitDocument(int max) {
      setMaxLength(max);
    }

    /**
     * Only accept insert String if total length will be less than maxLength.
     */
    public void insertString(int offset, String string, AttributeSet attributeSet)
        throws BadLocationException  {

        if (string == null) {
        } else if ((getLength() + string.length()) <= maxLength) {
          super.insertString(offset, string, attributeSet);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public boolean isAllDigits( String s ) {
        char[] chars = s.toCharArray();
        for (int i=0; i < chars.length; i++) {
            if(!Character.isDigit(chars[i]))
                return false;
        }
        return true;
    }

    public boolean isNumeric(String s) {
        try {
            Long.parseLong(s, 10);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void setMaxLength(int p_max) {
      maxLength = p_max;
    }
}