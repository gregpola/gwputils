/**
 * Project: GWP Utilities
 * File:    AutoFillTableTextField.java
 * Created on Aug 24, 2007
 * 
 * @author gpola
 * @copyright 2001-2007 Greg W Pola
 */
package com.gwp.util;

// jdk imports

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;



/**
 * A text field that performs auto-filling similar to MS Excel.
 * 
 * @author gpola
 *
 */
public class AutoFillTableTextField 
extends JTextField {
  
  // static data members
  /////////////////////////////////////////////////////////////////////////////
  
  
  // dynamic data members
  /////////////////////////////////////////////////////////////////////////////
  
  JTable                                    myTable;
  
  
  
  /**
   * Constructor
   */
  public AutoFillTableTextField( JTable table ) {
    super();
    myTable = table;
    setDocument( new AutoFillDocument( this ) );
  
  }
  
  
  /**
   * Inner class that represents the auto-fill document
   * @author gpola
   *
   */
  public class AutoFillDocument extends PlainDocument {
    
    private AutoFillTableTextField          myField;
    
    private int                             completionPos;
    
    private String                          completionText;
    
    
    /**
     * Default Constructor
     */
    public AutoFillDocument( AutoFillTableTextField field ) {
      super();
      myField = field;
      completionText = null;
      completionPos = -1;
      
      
      field.addFocusListener( new FocusListener() {
        public void focusGained( FocusEvent e ) {
        }
        
        public void focusLost( FocusEvent e ) {
          completionPos = -1;
          completionText = null;
          myField.setSelectionStart( 0 );
          myField.setSelectionEnd( 0 );
        }
        
      });
      
    }
  
    
    /**
     * Returns the auto complete text or null if none matches.
     * 
     * @param text
     * @return
     */
    public String autoComplete( String prefix ) {
      // check all the field values above this one for a match
      int row = myField.myTable.getEditingRow();
      int col = myField.myTable.getEditingColumn();
      
      while ( --row >= 0 ) {
        Object obj = myField.myTable.getValueAt( row, col );
        if ( obj != null ) {
          String sValue = obj.toString();
        
          if ( sValue.startsWith( prefix ) ) {
            return  sValue.substring( prefix.length() );
          }
          
        }
      }
      
      return null;
    }
    
    
    /**
     * Inserts some content into the document.
     */
    public void insertString( int offs, String str, AttributeSet a ) throws BadLocationException  {

      // strip off the old auto-complete text
      if ( ( completionText != null ) && ( completionPos > -1 ) ) {
        super.remove( completionPos, completionText.length() );
      }
      
      // check for proper position to use auto-complete
      boolean useAutoComplete = ( offs == getLength() );

      // ok now insert the new text
      super.insertString( offs, str, a );
      
      // check for auto-complete text
      if ( useAutoComplete ) {
        completionPos = offs + str.length();
        completionText = autoComplete( getText( 0, getLength() ) );
        
        // add auto-complete text if applicable
        if ( completionText != null ) {
          super.insertString( completionPos, completionText, a );
          myField.setCaretPosition( completionPos );
          myField.setSelectionStart( completionPos );
          myField.setSelectionEnd( completionPos + completionText.length() );
          
        } else {
          completionPos = -1;
          
        }
      
      } else {
        completionPos = -1;
        completionText = null;
        
      }
      
    }
    
    
    /**
     * Override to clear completion text.
     */
    public void remove( int offs, int len ) throws BadLocationException {
      // strip off the old auto-complete text
      if ( ( completionText != null ) && ( completionPos > -1 ) ) {
        super.remove( completionPos, completionText.length() );
      }
      completionPos = -1;
      completionText = null;

      super.remove( offs, len );
    }
    
    
    /**
     * Override for completion text handling.
     */
    public void replace( int offs, int len, String text, AttributeSet a ) throws BadLocationException {
      completionPos = -1;
      completionText = null;

      super.replace( offs, len, text, a );
    }
    
  }
  
}
