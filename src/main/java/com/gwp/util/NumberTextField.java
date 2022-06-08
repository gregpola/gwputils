/**
 * @author Greg W Pola
 * @version 1.0
 */
package com.gwp.util;

// jdk imports
import java.awt.Toolkit;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


public class NumberTextField
extends JTextField {

  long                      maxValue;

  private long              minValue;

  private NumberDocument    numberDoc;
  
  

  /**
   * Default contructor
   */
  public NumberTextField() {
    this( Long.MIN_VALUE, Long.MAX_VALUE );
  }

  public NumberTextField( long min, long max ) {
    this( "", 0, min, max );
  }

  /**
   * Constructs a object using the specified doc, text and columns
   * @param doc
   * @param text
   * @param columns
   */
  public NumberTextField(String text, int columns) {
    this( text, columns, Long.MIN_VALUE, Long.MAX_VALUE );
  }

  /**
   * Constructs a object using the specified doc, text and columns
   * @param columns
   */
  public NumberTextField( int columns ) {
    this( null, columns );
  }

  /**
   * Constructs a object using the specified text
   * @param text
   */
  public NumberTextField( String text ) {
    this( text, 0 );
  }

  public NumberTextField( String text, int columns, long min, long max ) {
    minValue = min;
    maxValue = max;
    setDocument( new NumberDocument() );
    setText( text );
    setColumns( columns );
    setHorizontalAlignment( RIGHT );

    addFocusListener( new FocusAdapter() {
      public void focusLost( FocusEvent e ) {
        long value = longValue();
        setText( String.valueOf( value ) );
      }
    });
  }


  /**
   * Specified the NumberDocument Document
   * @return
   */
  protected Document createDefaultModel() {
    if ( numberDoc == null ) {
      numberDoc = new NumberDocument();
    }
    return numberDoc;
  }


  /**
   * Returns the integer value of the text
   */
  public int intValue() {
    int value = 0;
    try {
      value = Integer.parseInt( getText() );
      if ( value < minValue ) {
        value = (int) minValue;
      } else if ( value > maxValue ) {
        value = (int) maxValue;
      }
    } catch ( NumberFormatException nfe ) {
    }

    return value;
  }


  /**
   * Returns the long value of the text
   */
  public long longValue() {
    long value = 0;
    try {
      value = Long.parseLong( getText() );
      if ( value < minValue ) {
        value = minValue;
      } else if ( value > maxValue ) {
        value = maxValue;
      }
    } catch ( NumberFormatException nfe ) {
    }

    return value;
  }



  /**
   * Sets whether hexidecimal characters are allowed
   * @param allowHex:  Whether to allow hexadecimal values
   */
  public void setAllowHex(boolean allowHex) {
    numberDoc.setAllowHex(allowHex);
  }
  
  
  /**
   * Sets the maximum value that can be displayed in the field.
   * 
   * @param max
   */
  public void setMaximumValue( long max ) {
    maxValue = max;
  }
  
  
  /**
   * Sets the value displayed by this field.
   * 
   * @param newValue
   */
  public void setValue( int newValue ) {
    setText( String.valueOf( newValue ) );
  }

  
  /**
   * Sets the value displayed by this field.
   * 
   * @param newValue
   */
  public void setValue( Long newValue ) {
    setText( String.valueOf( newValue ) );
  }


  ////
  //  NumberDocument inner class that allows only numbers to be typed
  //
  //
  ////
  class NumberDocument extends PlainDocument {

    private boolean allowHex = false;

    /**
     * Contructor
       */
    public NumberDocument() {
      super();
    }

    /**
     * Sets whether hexidecimal characters are allowed
     * @param allowHex:  Whether to allow hexadecimal values
     */
    public void setAllowHex(boolean allowHex) {
      this.allowHex = allowHex;
    }
    /**
     * Called whenever text is inserted into the control
     * @param offs: Offset into control
     * @param str:  The inserted text
     * @param a:    The attribute set
     * @throws BadLocationException
     */
    public void insertString(int offs, String str, AttributeSet a)
          throws BadLocationException {
      int i, j;

      if (str == null) {
        return;
      }

      char[] text = str.toCharArray();
      char[] num = new char[text.length];
      for (i = 0, j = 0; j < text.length; j++) {
        if ( Character.isDigit(text[j]) ||
             text[j] == '-' ||
             (allowHex && Character.digit( Character.toUpperCase(text[j]), 16) != -1) ) {
          num[i++] = text[j];
        } else {
          Toolkit.getDefaultToolkit().beep();
          break;
        }
      }
      String addString = new String( num, 0, i );

      // Check bounds
      try {
        long value = Long.parseLong( getText( 0, offs ) + addString + getText( offs, getLength() - offs ) );
        if ( value <= maxValue ) {
          super.insertString( offs, addString, a );
        }
      } catch ( NumberFormatException nfe ) {
      }
    }
  }
}

