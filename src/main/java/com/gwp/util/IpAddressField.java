/**
 * @author Greg W. Pola
 * @version 1.0
 */
package com.gwp.util;

// JDK Imports

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

// Wavelink imports




/**
 * Class that allows edit and display of a MAC address
 */
public class IpAddressField
extends JPanel {

  private static final int                  NODE_LENGTH                   = 3;

  private static final String               LEGAL_CHARACTERS              = "0123456789";

  protected ArrayList<JTextField>           nodes;

  protected int                             nodeFocusIndex;

  protected String                          nodeSeparator;


  /**
   * Constructors
   */
  public IpAddressField() {
    this( new IpAddress() );
  }

  public IpAddressField( IpAddress val ) {
    nodeSeparator = ".";

    // create the nodes
    nodes = new ArrayList<JTextField>( IpAddress.IP_ADDRESS_SIZE );
    nodeFocusIndex = 0;

    JTextField field = null;
    for ( int i = 0; i < IpAddress.IP_ADDRESS_SIZE; i++ ) {
      field = createNodeField();
      nodes.add( field );
      add(field);
      if ( i < IpAddress.IP_ADDRESS_SIZE - 1 ) {
        add( getNodeSeparatorTextField() );
      }
    }

    setValue(val);

    // set characteristics
    setBorder( BorderFactory.createCompoundBorder( 
        UIManager.getBorder( "TextField.border" ),
        BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) ) );
    setBackground( SystemColor.window );
    setLayout( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
    setMaximumSize( getPreferredSize() );
    setMinimumSize( getPreferredSize() );
  }


  /**
   * Adds a document listener
   */
  public void addDocumentListener( DocumentListener listener ) {
    for ( int i = 0; i < nodes.size(); i++ ) {
      JTextField node = (JTextField) nodes.get( i );
      if ( node != null ) {
        node.getDocument().addDocumentListener( listener );
      }
    }
  }


  /**
   * Adds a key listener for this object
   */
  public void addKeyListener( KeyAdapter l ) {
    for (int i = 0; i < nodes.size(); i++) {
      JTextField node = (JTextField) nodes.get( i );
      if ( node != null ) {
        node.addKeyListener( l );
      }
    }
  }

  /**
   * Converts a byte to a int
   */
  public static final int byteToInt(byte data) {
    int result = 0;
    result |= ((int)data)&0xFF;

    return result;
  }


  /**
   * Configures and returns a text field to serve as an editable area.
   *
   * @return JTextField
   */
  private JTextField createNodeField() {

    JTextField tf = new JTextField( NODE_LENGTH );
    tf.setDocument( new IpAddressDocument() );
    tf.setHorizontalAlignment( SwingConstants.CENTER );
    tf.setBorder( null );
    tf.setOpaque( false );

    // Add a key listener
    tf.addKeyListener( new KeyAdapter() {
      public void keyTyped( KeyEvent evt ) {
        JTextField field  = (JTextField) evt.getSource();
        String text       = field.getText();
        char c            = evt.getKeyChar();

        // if there is selected text, just process normally
        if ( field.getSelectedText() != null ) {
          return;
        } else if ( c == KeyEvent.CHAR_UNDEFINED || c == KeyEvent.VK_BACK_SPACE ) {
          return;
        } else if ( c == nodeSeparator.charAt( 0 ) && text.length() == 0 ) {
          return;
        } else if ( ( nodeSeparator.length() == 1 ) && ( c == nodeSeparator.charAt( 0 ) ) ) {
          // typed the node separator character
          moveToNextNode();
        } else if ( text.length() >= NODE_LENGTH ) {
          // see if we should move to the next field
          moveToNextNode();
        }
      }

      public void keyPressed( KeyEvent evt ) {
        if ( evt.getKeyCode() == KeyEvent.VK_SPACE ) {
          moveToNextNode();
        //} else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
          //moveToPreviousNode();
        }
      }
    });

    // Add a focus listener
    tf.addFocusListener( new FocusAdapter() {
      public void focusGained( FocusEvent e ) {
        nodeFocusIndex = nodes.indexOf( e.getSource() );
        ( ( JTextField ) e.getSource() ).selectAll();
      }
      
      public void focusLost( FocusEvent e ) {
        int value = -1;
        JTextField field = (JTextField) e.getSource();
        try {
          value = Integer.parseInt( field.getText() );
        } catch ( NumberFormatException nfe ) {
        }

        if ( ( value < 0 ) || ( value > 255 ) ) {
          field.setText( "0" );
        }
      }
    } );

    return tf;
  }


  /**
   * Returns the node that currently has the focus
   */
  private JTextField getNode(int index) {
    if ( index < 0 || index >= IpAddress.IP_ADDRESS_SIZE ) return null;
    return (JTextField) nodes.get(index);
  }


  /**
   * Configures and returns a text field to serve as a separator.
   *
   * @return javax.swing.JTextField.
   */
  private JTextField getNodeSeparatorTextField() {

    JTextField sep = new JTextField( nodeSeparator ) {
      public boolean isFocusTraversable() {
        return false;
      }
    };

    sep.setOpaque( false );
    sep.setBorder( null );
    sep.setEditable( false );
    sep.setRequestFocusEnabled( false );

    return sep;
  }


  /**
   * Returns the value of the given node
   */
  public byte getNodeValue(int index) {
    JTextField node = getNode( index );
    if ( node != null ) {
      String textValue = node.getText();
      try {
        return (byte) Integer.parseInt( textValue );
      } catch ( NumberFormatException nfe ) {
        return (byte) 0;
      }
    } else {
      return (byte) 0;
    }
  }


  /**
   * Returns the value of the field
   */
  public IpAddress getValue() {
    byte[] data = new byte[IpAddress.IP_ADDRESS_SIZE];

    for ( int i = 0; i < IpAddress.IP_ADDRESS_SIZE; i++ ) {
      data[i] = getNodeValue(i);
    }

    return new IpAddress(data);
  }


  /**
   * Moves the caret to the next node in the field
   */
  void moveToNextNode() {
    if (nodeFocusIndex < nodes.size() - 1) {
      JTextField node = getNode(nodeFocusIndex + 1);
      node.requestFocus();
      if ( node.getText().length() == 0 ) {
        node.setCaretPosition( 0 );
      } else {
        node.selectAll();
      }
    }
  }


  /**
   * Removes a document listener
   */
  public void removeDocumentListener( DocumentListener listener ) {
    for ( int i = 0; i < nodes.size(); i++ ) {
      JTextField node = (JTextField) nodes.get( i );
      if ( node != null ) {
        node.getDocument().removeDocumentListener( listener );
      }
    }
  }
  
  
  /**
   * Override to request focus in the first octet.
   * 
   */
  public void requestFocus() {
    JTextField node = nodes.get( 0 );
    
    if ( node != null ) {
      node.requestFocus();
    }
    
  }
  
  
  /**
   * Resets the value to 0.0.0.0
   *
   */
  public void reset() {
    for ( int i = 0; i < IpAddress.IP_ADDRESS_SIZE; i++ ) {
      setNodeValue( i, (byte)0 );
    }
  }


  /**
   * Selects the entire field
   *
   */
  public void selectAll() {
    for ( int i = 0; i < nodes.size(); i++ ) {
      JTextField node = (JTextField) nodes.get( i );
      if ( node != null ) {
        node.selectAll();
      }
    }
  }
  
  
  /**
   * Sets the enabled state of the component
   */
  public void setEnabled( boolean enabled ) {
    super.setEnabled( enabled );
    setBackground( enabled ? SystemColor.window : SystemColor.control );

    for ( int i = 0; i < nodes.size(); i++ ) {
      getNode( i ).setEnabled( enabled );
    }
  }

  /**
   * Sets the value of the node at the given index
   */
  public void setNodeValue(int index, byte value) {
    JTextField node = getNode( index );
    if ( node != null ) {
      node.setText( String.valueOf( byteToInt( value ) ) );
    }
  }


  /**
   * Disables the specified octets
   *
   * @param nodes
   */
  public void setOctetsEnabled( int[] octets, boolean enabled ) {
    for ( int i = 0; i < octets.length; i++ ) {
      if ( octets[i] >= 0 && octets[i] < nodes.size() ) {
        getNode( octets[i] ).setEnabled( enabled );
      }
    }
  }


  /**
   * Sets the ip address value by string
   *
   * @param ipText
   */
  public void setText( String ipText ) {
    try {
      IpAddress ip = new IpAddress( ipText );
      setValue ( ip );
    } catch ( Exception e ) {
    }
  }


  /**
   * Sets the value displayed by the field
   */
  public void setValue( IpAddress value ) {
    byte[] valBytes = value.getBytes();
    for ( int i = 0; i < IpAddress.IP_ADDRESS_SIZE; i++ ) {
      setNodeValue( i, valBytes[i] );
    }
  }


  ////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  //  Inner Class MacAddressDocument
  //
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  class IpAddressDocument extends PlainDocument {

    /**
     * Test insertion data before alowing it
     */
    public void insertString ( int offset, String  str, AttributeSet attr ) throws BadLocationException {
      if (str == null) return;

      // don't allow the field separator to be entered - it will be processed
      // as a key that moves cursor to next editable field
      int index = str.indexOf(nodeSeparator);
      if (index != -1) {
        str = str.substring(0, index);
      }

      // make sure only legal characters are inserted
      int finalLength = 0;
      for ( ; finalLength < str.length(); finalLength++) {
        char pos = str.charAt(finalLength);
        if ( LEGAL_CHARACTERS.indexOf(pos) == -1 ) {
          Toolkit.getDefaultToolkit().beep();
          break;
        }
      }
      str = str.substring(0, finalLength);

      // make sure the node value is valid
      String resultString = getText( 0, offset ) + str + getText( offset, getLength() - offset );
      int value = -1;
      try {
        value = Integer.parseInt( resultString );
      } catch ( NumberFormatException nfe ) {
      }

      if ( ( value < 0 ) || ( value > 255 ) ) {
        str = "0";
      }

      // make sure the final length is not too long
      if ( ( offset + finalLength ) <= NODE_LENGTH ) {
        str = str.toUpperCase();
        super.insertString( offset, str, attr );
      }
    }
  }
}
