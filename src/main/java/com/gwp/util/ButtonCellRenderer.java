/**
 * A class that renders a button in a JTable
 */
package com.gwp.util;

// jdk imports
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import javax.swing.table.TableCellRenderer;



/**
 * @author Greg
 *
 */
public class ButtonCellRenderer 
extends JButton 
implements TableCellRenderer {

  
  
  /**
   * Default constructor
   */
  public ButtonCellRenderer() {
    super();
    setOpaque( true );
  }
  
  
  /**
   * Get the rendering component.
   * 
   * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
   */
  @Override
  public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, 
      boolean hasFocus, int row, int column ) {
    
    // Update the appearance
    /*
    if ( isSelected ) {
      setForeground( table.getSelectionForeground() );
      setBackground( table.getSelectionBackground() );
      
    } else {
      setForeground( table.getForeground() );
      setBackground( UIManager.getColor( "Button.background" ) );
      
    }
    */
    
    // Button attributes
    if ( value instanceof JButton ) {
      JButton button = (JButton) value;

      if ( button.getIcon() != null ) {
        setText( null );
      } else {
        setText( button.getText() );
      }
      
      setBorder( null );
      setContentAreaFilled( false );
      setEnabled( button.isEnabled() );
      setFocusable( false );
      setHorizontalTextPosition( SwingConstants.CENTER );
      setIcon( button.getIcon() );
      setToolTipText( button.getToolTipText() );
      setVerticalTextPosition( SwingConstants.BOTTOM );
      
    } else {
      setText( ( value == null ) ? "" : value.toString() );
      
    }
    
    return this;
  }

}
