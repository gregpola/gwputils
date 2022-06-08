package com.gwp.util;

import javax.swing.text.*;

public class TextFieldLimit extends PlainDocument {
    private int limit;
    private boolean toUppercase = false;

    public TextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    public TextFieldLimit(int limit, boolean upper) {
        super();
        this.limit = limit;
        toUppercase = upper;
    }

    public void insertString (int offset, String  str, AttributeSet attr)
        throws BadLocationException {
        if (str == null) return;

        if ((getLength() + str.length()) <= limit) {
            if (toUppercase)
                str = str.toUpperCase();
            super.insertString(offset, str, attr);
        }
    }
    public int getLimit() { return limit; }
}

