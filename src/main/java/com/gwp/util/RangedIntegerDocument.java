package com.gwp.util;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;



/**
 * The <code>RangedIntegerDocument</code> class provides
 * a way to limit the input text to an integer range.
 *
 * @author  Greg Pola
 * @version 1.0
 */
public class RangedIntegerDocument extends PlainDocument {

    private Long max;
    private Long min;

    /**
     * Constructor.  Creates a PlainDocument used by Text Fields
     * to limit entries to numerics in a specified range.
     *
     * @param min the minimum value
     * @param max the maximum value
     */
    public RangedIntegerDocument(long min, long max) {
         this.min = new Long(min);
         this.max = new Long(max);
    }

    // Temp method for jdk1.1
    private int compareTo(Long arg1, Long arg2) {
        return (int)(arg1.longValue() - arg2.longValue());
    }

    /**
     * Only accept insert String if total length will be less than maxLength.
     */
    public void insertString(int offset, String string, AttributeSet attributeSet)
        throws BadLocationException  {

        if (string == null) {
        } else if ((offset == 0 && isNumeric(string)) || isAllDigits(string)) {
            String resultString = getText(0, offset) + string + getText(offset, getLength() - offset);
            try {
                Long resultValue = Long.valueOf(resultString);
                //JDK1.2 if (resultValue.compareTo(min) >= 0 && resultValue.compareTo(max) <= 0) {
                if (compareTo(resultValue, min) >= 0 && compareTo(resultValue, max) <= 0) {
                    super.insertString(offset, string, attributeSet);
                }
            } catch (NumberFormatException nfe) {
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private boolean isAllDigits(String s) {
        char[] chars = s.toCharArray();
        for (int i=0; i < chars.length; i++) {
            if(!Character.isDigit(chars[i]))
                return false;
        }
        return true;
    }

    private boolean isNumeric(String s) {
        try {
            Long.parseLong(s, 10);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void setMaximum(long p_max) {
      max = new Long(p_max);
    }

    public void setMinimum(long p_min) {
      min = new Long(p_min);
    }
}