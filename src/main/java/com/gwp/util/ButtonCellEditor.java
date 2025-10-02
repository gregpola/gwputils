/**
 * A class that renders a button editor in a JTable
 */
package com.gwp.util;

// jdk imports

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



/**
 * @author Greg
 *
 */
public class ButtonCellEditor 
extends DefaultCellEditor {

  // static data members
  /////////////////////////////////////////////////////////////////////////////
  

  // dynamic data members
  /////////////////////////////////////////////////////////////////////////////
  
  private Action                            action;
  
  
  private JButton                           theButton;
  
  
  
  
  /**
   * Constructor that represents a JButton.
   * 
   * @param checkBox
   */
  public ButtonCellEditor( JCheckBox checkBox ) {
    super( checkBox );
    
    action = checkBox.getAction();
    //actionListener = checkBox.getActionListeners()[0];
    
    theButton = new JButton( action );
    theButton.setOpaque( true );

    if ( theButton.getIcon() != null ) {
      theButton.setText( null );
    }
    
    theButton.setBorder( null );
    theButton.setContentAreaFilled( false );
    theButton.setFocusable( false );
    theButton.setHorizontalTextPosition( SwingConstants.CENTER );
    theButton.setVerticalTextPosition( SwingConstants.BOTTOM );

    theButton.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        fireEditingStopped();
      }
    });
    
  }
  
  
  /**
   * Returns the editor value
   */
  public Object getCellEditorValue() {
    return theButton;
  }
  
  
  /**
   * Returns the editing component
   */
  public Component getTableCellEditorComponent( JTable table, Object value,
      boolean isSelected, int row, int column ) {
    
    // Update the appearance
    /*
    if ( isSelected ) {
      theButton.setForeground( table.getSelectionForeground() );
      theButton.setBackground( table.getSelectionBackground() );
      
    } else {
      theButton.setForeground( table.getForeground() );
      theButton.setBackground( table.getBackground() );
      
    }
    */

    /*
    buttonLabel = ( value == null ) ? "" : value.toString();
    theButton.setText( buttonLabel );
    */
    
    return theButton;
  }

}
