/**
 * @author Greg W. Pola
 * @version 1.0
 */
package com.gwp.util;

// JDK imports
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import javax.swing.event.DocumentListener;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


// Wavelink imports



/**
 * Class that allows displaying and editing of time of day in the format: HH:MM:SS AM
 */
public class ShortTimeOfDayField
extends JPanel {

  private static final String       LEGAL_MERIDIEM_CHARACTERS         = "aApPmM";
  private static final String       LEGAL_TIME_CHARACTERS             = "0123456789";
  private static final String       LEGAL_FIRST_MINUTE_CHARACTERS     = "012345";
  private static final String       LEGAL_MILITARY2_CHARACTERS        = "0123";

  private static final int          NODE_LENGTH                       = 2;

  int                               MAX_HOUR;

  int                               MIN_HOUR;

  private static final String       TIME_NODE_SEPARATOR               = ":";

  private DateFormat                TIME_FORMATTER;

  Calendar                          currentTime                       = Calendar.getInstance();

  JTextField                        focusNode;

  private JTextField                hoursField;

  private JTextField                meridiemField;

  private JTextField                minutesField;

  private JTextField[]              nodeSeparatorFields;

  boolean                           military;


  
  /**
   * Constructors
   */
  public ShortTimeOfDayField() {
    this( new Date(), false );
  }

  /**
   * Constructors
   * @param mil Whether military time should be used
   */
  public ShortTimeOfDayField(boolean mil) {
    this( new Date(), mil );
  }

  /**
   * Constructors
   * @param currentTime The start time to use
   */
  public ShortTimeOfDayField( long time ) {
    this( new Date( time ), false );
  }

  /**
   * Constructors
   * @param currentTime The start time to use
   */
  public ShortTimeOfDayField( Date time ) {
    this( time, false );
  }

  /**
   * Constructors
   * @param currentTime The start time to use
   * @param mil Whether military time should be used
   */
  public ShortTimeOfDayField( Date time, boolean mil ) {
    super();

    setLayout( new GridLayout( 1, mil ? 3 : 5, 1, 1 ) );
    setBorder( BorderFactory.createCompoundBorder( 
        UIManager.getBorder( "TextField.border" ),
        BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) ) );
    setBackground( SystemColor.window );

    military = mil;
    if ( military ) {
      MAX_HOUR            = 23;
      MIN_HOUR            = 0;
      TIME_FORMATTER      = new SimpleDateFormat("HH:mm");
      nodeSeparatorFields = new JTextField[1];
    } else {
      MAX_HOUR            = 12;
      MIN_HOUR            = 1;
      TIME_FORMATTER      = new SimpleDateFormat("hh:mm a");
      nodeSeparatorFields = new JTextField[2];
    }

    // init the nodes
    ////////////////////////////////////////////////////////////
    focusNode = null;
    int sepCount = 0;

    // hours
    hoursField = createHourField();
    add( hoursField );
    nodeSeparatorFields[sepCount] = getNodeSeparatorField( TIME_NODE_SEPARATOR );
    add( nodeSeparatorFields[sepCount++] );

    // minutes
    minutesField = createMinuteField();
    add( minutesField );

    // meridiem
    if ( !isMilitary() ) {
      nodeSeparatorFields[sepCount] = getNodeSeparatorField( " " );
      add( nodeSeparatorFields[sepCount++] );
      meridiemField = createMeridiemField();
      add( meridiemField );
    }

    // Set the current value
    ////////////////////////////////////////////////////////////
    setValue( time );

    
    
    if ( military ) {
      setToolTipText( "Must be of the format: HH:MM" );
    } else {
      setToolTipText( "Must be of the format: HH:MM AM" );
    }
  }


  /**
   * Adds a document listener to the time node
   *
   * @param l
   */
  public void addDocumentListener( DocumentListener l ) {
    hoursField.getDocument().addDocumentListener( l );
    minutesField.getDocument().addDocumentListener( l );
    if ( !isMilitary() ) {
      meridiemField.getDocument().addDocumentListener( l );
    }
  }


  /**
   * Adds a key listener to the time node
   *
   * @param l
   */
  public void addKeyListener( KeyListener l ) {
    hoursField.addKeyListener( l );
    minutesField.addKeyListener( l );
    if ( !isMilitary() ) {
      meridiemField.addKeyListener( l );
    }
  }


  /**
   * Configures and returns a text field to serve as an editable area in the meridiem portion of the field.
   *
   * @return JTextField
   */
  private JTextField createMeridiemField() {
    JTextField tf = new JTextField( NODE_LENGTH );
    tf.setDocument(new MeridiemNodeDocument() );
    tf.setHorizontalAlignment( SwingConstants.LEFT );
    tf.setBorder( null );
    tf.setOpaque( false );

    // Add a key listener
    tf.addKeyListener( new KeyAdapter() {
      public void keyTyped( KeyEvent evt ) {
        if ( evt.getKeyChar() != 'a' && evt.getKeyChar() != 'A' && evt.getKeyChar() != 'p' && evt.getKeyChar() != 'P' ) {
          evt.consume();
        }
      }

      public void keyPressed( KeyEvent evt ) {

        if ( evt.getKeyCode() == KeyEvent.VK_RIGHT ) {
          // do nothing
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT ) {
          if ( evt.getModifiers() != InputEvent.SHIFT_MASK ) {
            moveToPreviousNode();
          }
        } else if ((evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
          setMeridiem( !isAm() );
        }

      }
        
    });

    // Add a focus listener
    tf.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        focusNode = (JTextField) e.getSource();
      }

      public void focusLost( FocusEvent e ) {
        JTextField field = (JTextField) e.getSource();
        String text = field.getText();
        if ( text.length() == 0 ) {
          setMeridiem( currentTime.get( Calendar.AM_PM ) == Calendar.AM );
        }
      }
    });

    return tf;
  }


  /**
   * Configures and returns a text field to serve as an editable area in the hour portion of the time field.
   *
   * @return JTextField
   */
  private JTextField createHourField() {

    JTextField tf = new JTextField( NODE_LENGTH );
    tf.setDocument( new HourNodeDocument() );
    tf.setHorizontalAlignment( SwingConstants.CENTER );
    tf.setBorder( null );
    tf.setOpaque( false );

    // Add a key listener
    tf.addKeyListener( new KeyAdapter() {
      public void keyTyped( KeyEvent evt ) {
        JTextField field = (JTextField) evt.getSource();
        String text = field.getText();
        char c = evt.getKeyChar();

        // field selection values
        boolean textSelected = (field.getSelectedText() != null );
        int selectionStart = field.getSelectionStart();
        int selectionLength = textSelected ? field.getSelectedText().length() : 0;

        // Build the new hour value
        int position = textSelected ? selectionStart : field.getCaretPosition();
        StringBuffer newHour = new StringBuffer();

        if ( position == 0 ) {
          newHour.append( c );
          if ( textSelected ) {
            if ( ( selectionLength < 2 ) && ( text.length() > 1 ) ) {
              newHour.append( text.charAt( 1 ) );
            }
          } else {
            newHour.append( text );
          }
        } else { // position should be 1
          if ( textSelected ) {
            if ( selectionLength < 2 ) {
              newHour.append( text.charAt( 0 ) );
            }
            newHour.append( c );
          } else {
            newHour.append( text );
            newHour.append( c );
          }
        }

        // Validate key event
        if ( c == ' ' ) {
          evt.consume();
        } else if ( c == KeyEvent.CHAR_UNDEFINED ) {
          return;
        } else if ( c == KeyEvent.VK_UP || c == KeyEvent.VK_DOWN ) {
          return;
        } else if ( c == KeyEvent.VK_BACK_SPACE ) {
          return;
        } else if ( !isLegalHourValue( newHour.toString() ) ) {
          Toolkit.getDefaultToolkit().beep();
          evt.consume();
        }
      }

      /**
       * Key pressed
       * @param evt
       */
      public void keyPressed( KeyEvent evt ) {
        JTextField field  = (JTextField) evt.getSource();
        String text = field.getText();
        int value = 0;
        try {
          value = Integer.parseInt( text );
        } catch ( NumberFormatException nfe ) {
          return;
        }

        if ( evt.getKeyCode() == KeyEvent.VK_SPACE ) {
          if ( evt.getModifiers() != InputEvent.SHIFT_MASK ) {
            moveToNextNode();
          } else {
            moveToPreviousNode();
          }
        } else if ( evt.getKeyCode() == KeyEvent.VK_RIGHT ){
          if ( evt.getModifiers() != InputEvent.SHIFT_MASK ) {
            moveToNextNode();
          }
        } else if ( evt.getKeyCode() == KeyEvent.VK_LEFT ) {
          if ( evt.getModifiers() != InputEvent.SHIFT_MASK ) {
            moveToPreviousNode();
          }
        } else if ( evt.getKeyCode() == KeyEvent.VK_UP ) {
          // increment
          value++;
          if ( value > MAX_HOUR ) {
            value = MIN_HOUR;
          }
          if ( !military && value == 12 ) {
            setMeridiem( !isAm() );
          }
          setHours( value );
        } else if ( evt.getKeyCode() == KeyEvent.VK_DOWN ) {
          // decrement
          value--;
          if ( value < MIN_HOUR ) {
            value = MAX_HOUR;
          }
          if (!military) {
            if ( value == 11 ) {
              setMeridiem( !isAm() );
            } else if ( value == 0 ) {
              if ( !isAm() ) {
                value = 12;
              }
            }
          }
          setHours( value );
        }
      }
    });

    // Add a focus listener
    tf.addFocusListener( new FocusAdapter() {
      public void focusGained( FocusEvent e ) {
        focusNode = (JTextField) e.getSource();
      }

      public void focusLost( FocusEvent e ) {
        JTextField field = (JTextField) e.getSource();
        String text = field.getText();
        if ( text.length() == 0 ) {
          setHours( currentTime.get( Calendar.HOUR ) );
        }
      }
    });

    return tf;
  }


  /**
   * Configures and returns a text field to serve as an editable area in the minute portion of the time field.
   *
   * @return JTextField
   */
  private JTextField createMinuteField() {

    JTextField tf = new JTextField( NODE_LENGTH );
    tf.setDocument( new MinuteNodeDocument() );
    tf.setHorizontalAlignment( SwingConstants.CENTER );
    tf.setBorder( null );
    tf.setOpaque( false );

    // Add a key listener
    tf.addKeyListener( new KeyAdapter() {

      // Key Typed Event
      //////////////////////////////////////////////////////////////////////
      public void keyTyped( KeyEvent evt ) {
        JTextField field = (JTextField) evt.getSource();
        String text = field.getText();
        char c = evt.getKeyChar();

        // field selection values
        boolean textSelected = (field.getSelectedText() != null );
        int selectionStart = field.getSelectionStart();
        int selectionLength = textSelected ? field.getSelectedText().length() : 0;

        // Build the new minute value
        int position = textSelected ? selectionStart : field.getCaretPosition();
        StringBuffer newMinute = new StringBuffer();

        if ( position == 0 ) {
          newMinute.append( c );
          if ( textSelected ) {
            if ( ( selectionLength < 2 ) && ( text.length() > 1 ) ) {
              newMinute.append( text.charAt( 1 ) );
            }
          } else {
            newMinute.append( text );
          }
        } else { // position should be 1
          if ( textSelected ) {
            if ( selectionLength < 2 ) {
              newMinute.append( text.charAt( 0 ) );
            }
            newMinute.append( c );
          } else {
            newMinute.append( text );
            newMinute.append( c );
          }
        }

        // handle the key event
        if ( c == ' ' ) {
          evt.consume();
        } else if ( c == KeyEvent.CHAR_UNDEFINED ) {
          return;
        } else if ( c == KeyEvent.VK_UP || c == KeyEvent.VK_DOWN ) {
          return;
        } else if ( c == KeyEvent.VK_BACK_SPACE ) {
          return;
        } else if ( !isLegalMinuteValue( newMinute.toString() ) ) {
          Toolkit.getDefaultToolkit().beep();
          evt.consume();
        }
      }

      // Key Pressed Event
      //////////////////////////////////////////////////////////////////////
      public void keyPressed( KeyEvent evt ) {
        JTextField field  = (JTextField) evt.getSource();
        String text = field.getText();
        int value = 0;
        try {
          value = Integer.parseInt( text );
        } catch ( NumberFormatException nfe ) {
          return;
        }

        // Space?
        if ( evt.getKeyCode() == KeyEvent.VK_SPACE ) {
          if ( evt.getModifiers() != InputEvent.SHIFT_MASK ) {
            moveToNextNode();
          } else {
            moveToPreviousNode();
          }
        } else if ( evt.getKeyCode() == KeyEvent.VK_RIGHT ){
          if ( evt.getModifiers() != InputEvent.SHIFT_MASK ) {
            moveToNextNode();
          }
        } else if ( evt.getKeyCode() == KeyEvent.VK_LEFT ) {
          if ( evt.getModifiers() != InputEvent.SHIFT_MASK ) {
            moveToPreviousNode();
          }
        } else if ( evt.getKeyCode() == KeyEvent.VK_UP ) {
          // increment
          value++;
          if ( value > 59 ) {
            value = 0;
          }
            setMinutes( value );
        } else if ( evt.getKeyCode() == KeyEvent.VK_DOWN ) {
          // decrement
          value--;
          if ( value < 0 ) {
            value = 59;
          }
          setMinutes( value );
        }
      }
    });

    // Add a focus listener
    //////////////////////////////////////////////////////////////////////
    tf.addFocusListener(new FocusAdapter() {
      public void focusGained( FocusEvent e ) {
        focusNode = (JTextField) e.getSource();
      }

      public void focusLost( FocusEvent e ) {
        JTextField field = (JTextField) e.getSource();
        String text = field.getText();
        if ( text.length() == 0 ) {
          setMinutes( currentTime.get( Calendar.MINUTE ) );
        }
      }
    });

    return tf;
  }


  /**
   * Returns the current hour of the day
   *
   * @return
   */
  public int getHour() throws NumberFormatException {
    int hour = Integer.parseInt( hoursField.getText().trim() );

    if (military) {
    } else {
      if ( hour == 12 ) {
        if ( isAm() ) {
          hour = 0;
        }
      } else if ( !isAm() ) {
        hour += 12;
      }
    }

    return hour;
  }


  /**
   * returns the current minutes
   *
   * @return
   */
  public int getMinutes() throws NumberFormatException {
    return Integer.parseInt( minutesField.getText().trim() );
  }


  /**
   * Configures and returns a text field to serve as a separator.
   *
   * @return javax.swing.JTextField.
   */
  private JTextField getNodeSeparatorField(String nodeSeparator) {

    JTextField sep = new JTextField( nodeSeparator ) {
      public boolean isFocusTraversable() {
        return false;
      }
    };

    sep.setOpaque( false );
    sep.setBorder( null );
    sep.setEditable( false );
    sep.setRequestFocusEnabled( false );
    sep.setHorizontalAlignment( SwingConstants.CENTER );
    sep.setMargin( new Insets( 0, 0, 0, 0 ) );

    return sep;
  }


  /**
   * Returns the text value of the field
   */
  public String getText() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(hoursField.getText());
    buffer.append(TIME_NODE_SEPARATOR);
    buffer.append(minutesField.getText());
    if (!military) {
      buffer.append(" ");
      buffer.append(meridiemField.getText());
    }
    return buffer.toString();
  }


  /**
   * Returns the Date object representing the time
   */
  public Date getTime() {
    Date date = null;
    try {
      date = TIME_FORMATTER.parse(getText());
    } catch (java.text.ParseException pe) {
    }
    return date;
  }


  /**
   * Returns the time represented by the widget in seconds after midnight
   *
   * @return
   */
  public long getTimeInSeconds() {
    long result = 0L;

    try {
      result += getHour() * 3600L;
    } catch (NumberFormatException e) { };
    try {
      result += getMinutes() * 60L;
    } catch (NumberFormatException e) { };

    return result;
  }


  /**
   * Tests whether the time is AM or PM
   */
  public boolean isAm() {
    if ( military ) {
      return  (currentTime.get( Calendar.HOUR ) < 12);
    } else {
      return ( meridiemField.getText().startsWith( "A" ) );
    }
  }


  /**
   * Tests whether a value is allowed for the hour node
   * @param position
   * @param input
   * @return
   */
  public boolean isLegalHourValue( String value ) {
    if ( value == null ) return false;

    if ( value.length() == 1 ) {
      if ( LEGAL_TIME_CHARACTERS.indexOf( value.charAt( 0 ) ) >= 0 ) {
        return true;
      }
    } else if ( value.length() == 2 ) {
      char one = value.charAt( 0 );
      char two = value.charAt( 1 );

      // military time
      if ( isMilitary() ) {
        if ( ( one == '0' ) || ( one == '1' ) ) {
          if ( LEGAL_TIME_CHARACTERS.indexOf( two ) >= 0 ) {
            return true;
          }
        } else if ( one == '2' ) {
          if ( LEGAL_MILITARY2_CHARACTERS.indexOf( two ) >= 0 ) {
            return true;
          }
        }
      } else { // common time
        if ( one == '0' ) {
          if ( LEGAL_TIME_CHARACTERS.indexOf( two ) > 0 ) {
            return true;
          }
        } else if ( one == '1' ) {
          if ( ( two == '0' ) || ( two == '1' ) || ( two == '2' ) ) {
            return true;
          }
        }
      }
    }

    return false;
  }


  /**
   * Tests whether a value is allowed for the minute node
   *
   * @param position
   * @param input
   * @return
   */
  public boolean isLegalMinuteValue( String value ) {
    if ( value == null ) return false;

    if ( value.length() == 1 ) {
      if ( LEGAL_TIME_CHARACTERS.indexOf( value.charAt( 0 ) ) >= 0 ) {
        return true;
      }
    } else if ( value.length() == 2 ) {
      char one = value.charAt( 0 );
      char two = value.charAt( 1 );

      if ( ( LEGAL_FIRST_MINUTE_CHARACTERS.indexOf( one ) >= 0 ) && ( LEGAL_TIME_CHARACTERS.indexOf( two ) >= 0 ) ) {
        return true;
      }
    }

    return false;
  }


  /**
   * Tests whether this time field is set up in 24 hour time or not
   *
   * @return
   */
  public boolean isMilitary() {
    return military;
  }


  /**
   * Moves the caret to the next node in the field
   */
  void moveToNextNode() {
    // get the next node
    JTextField nextNode = null;
    if ( focusNode == hoursField ) {
      nextNode = minutesField;
    } else if ( focusNode == minutesField && !military ) {
      nextNode = meridiemField;
    }

    if ( nextNode != null ) {
      // change the focus
      nextNode.requestFocus();

      // change the caret
      if ( nextNode.getText().length() == 0 ) {
        nextNode.setCaretPosition( 0 );
      } else {
        nextNode.selectAll();
      }
    }
  }


  /**
   * Moves the caret to the previous node in the field
   */
  void moveToPreviousNode() {
    // get the previous node
    JTextField previousNode = null;
    if ( focusNode == meridiemField ) {
      previousNode = minutesField;
    } else if ( focusNode == minutesField ) {
      previousNode = hoursField;
    }

    if ( previousNode != null ) {
      // change the focus
      previousNode.requestFocus();

      // change the caret
      if ( previousNode.getText().length() == 0 ) {
        previousNode.setCaretPosition( 0 );
      } else {
        previousNode.selectAll();
      }
    }
  }


  /**
   * Sets the foreground color for the text when disabled
   *
   * @param color
   */
  public void setDisabledTextColor( Color color ) {
    hoursField.setDisabledTextColor( color );
    if ( !military ) {
      meridiemField.setDisabledTextColor( color );
    }
    minutesField.setDisabledTextColor( color );

    for ( int i = 0; i < nodeSeparatorFields.length; i++ ) {
      nodeSeparatorFields[i].setDisabledTextColor( color );
    }
  }


  /**
   * Turns on or off editability
   */
  public void setEnabled( boolean enabled ) {
    hoursField.setEnabled( enabled );
    if ( !isMilitary() ) {
      meridiemField.setEnabled( enabled );
      meridiemField.setOpaque( enabled );
    }
    minutesField.setEnabled( enabled );
    minutesField.setOpaque( enabled );

    for ( int i = 0; i < nodeSeparatorFields.length; i++ ) {
      nodeSeparatorFields[i].setEnabled( enabled );
      nodeSeparatorFields[i].setOpaque( enabled );
    }

    setOpaque( enabled );
  }


  /**
   * Sets the value of the hours node
   */
  public void setHours( int hours ) {
    String text = String.valueOf( hours );

    if ( isMilitary() ) {
      hoursField.setText( hours < 10 ? "0" + text : text );
    } else {
      if ( hours > 12 ) {
        hours = hours % 12;
        text = String.valueOf( hours );
      }
      hoursField.setText( hours < 10 ? "0" + text : text );
    }
    hoursField.repaint();
  }


  /**
   * Sets the value of the meridiem
   */
  public void setMeridiem( boolean isAm ) {
    if ( isAm ) {
      if (!military) {
        meridiemField.setText( "AM" );
      }
    } else {
      if (!military) {
        meridiemField.setText( "PM" );
      }
    }
  }


  /**
   * Sets the value of the minutes node
   */
  public void setMinutes( int minutes ) {
    String text = String.valueOf( minutes );
    minutesField.setText( minutes < 10 ? "0" + text : text );
    minutesField.repaint();
  }


  /**
   * Sets the time displayed on the widget to the number of seconds after midnight
   *
   * @param seconds
   */
  public void setTimeInSeconds( long seconds ) {
    currentTime.setTime( new Date( System.currentTimeMillis() ) );

    int temp = (int) ( seconds / 3600L );
    currentTime.set( Calendar.HOUR_OF_DAY, temp );

    temp = (int) ( seconds / 60L );
    currentTime.set( Calendar.MINUTE, temp % 60 );

    setValue( currentTime.getTime() );
  }


  /**
   * Sets the value displayed by the field
   */
  public void setValue( Date value ) {
    int temp;

    if ( value == null ) {
      currentTime.setTime( new Date( System.currentTimeMillis() ) );
      hoursField.setText( "" );
      minutesField.setText( "" );
      if ( !isMilitary() ) {
        meridiemField.setText( "" );
      }
    } else {
      currentTime.setTime( value );
      if ( isMilitary() ) {
        temp = currentTime.get( Calendar.HOUR_OF_DAY );
      } else {
        temp = currentTime.get( Calendar.HOUR );
        if ( temp == 0 ) {
          temp = 12;
        }
        setMeridiem( currentTime.get( Calendar.AM_PM ) == Calendar.AM );
      }
      setHours( temp );
      setMinutes( currentTime.get( Calendar.MINUTE ) );
    }
  }


  ////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  //  Inner Class HourNodeDocument
  //
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  class HourNodeDocument extends PlainDocument {

    /**
     * Test insertion data before allowing it
     */
    public void insertString ( int offset, String  str, AttributeSet attr ) throws BadLocationException {
      if ( ( str == null ) || ( str.length() < 1 ) ) return;

      String finalText = this.getText( 0, offset ) + str + this.getText( offset, getLength() - offset );
      if ( isLegalHourValue( finalText ) ) {
        super.insertString( offset, str, attr );
      }
    }
  }


  ////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  //  Inner Class MinuteNodeDocument
  //
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  class MinuteNodeDocument extends PlainDocument {

    /**
     * Test insertion data befaore alowing it
     */
    public void insertString (int offset, String  str, AttributeSet attr) throws BadLocationException {
      if ( ( str == null ) || ( str.length() < 1 ) ) return;

      String finalText = this.getText( 0, offset ) + str + this.getText( offset, getLength() - offset );
      if ( isLegalMinuteValue( finalText ) ) {
        super.insertString( offset, str, attr );
      }
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  //  Inner Class MeridiemNodeDocument
  //
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  class MeridiemNodeDocument extends PlainDocument {

    /**
     * Test insertion data befaore alowing it
     */
    public void insertString (int offset, String  str, AttributeSet attr) throws BadLocationException {
      if (str == null || str.length() == 0) return;

      // make sure only legal characters are inserted
      int finalLength = 0;
      for ( ; finalLength < str.length(); finalLength++ ) {
        char pos = str.charAt(finalLength);
        if ( LEGAL_MERIDIEM_CHARACTERS.indexOf(pos) == -1 ) {
          Toolkit.getDefaultToolkit().beep();
          break;
        }
      }

      // Did we insert anything legal?
      if ( finalLength > 0 ) {
        str = str.substring(0, finalLength);
        if ( ( getLength() + finalLength ) <= NODE_LENGTH ) {
          str = str.toUpperCase();

          if ( offset == 0 ) {
            if ( str.charAt(0) == 'A' || str.charAt(0) == 'P' ) {
              super.insertString( offset, str, attr );
            }
          } else {
            if ( str.charAt(0) == 'M' ) {
              super.insertString( offset, str, attr );
            }
          }

          // Test for removal of the 'M'
          if ( getLength() < 2 ) {
            super.insertString( 1, "M", null );
          }
        }
      }
    }
  }
}
