/**
 * GenericHeaderRenderer.java
 * Created on May 30, 2006
 * 
 * Copyright (c) 2006-2007 Greg Warren Pola
 * @author gpola
 * 
 * 
 */
package com.gwp.util;


// jdk imports
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import javax.swing.table.DefaultTableCellRenderer;




/**
 * GenericHeaderRenderer
 * @author gpola
 * 
 * 
 */
public class GenericHeaderRenderer extends DefaultTableCellRenderer {

  /** 
   * Default constructor
   */
  public GenericHeaderRenderer() {
    super();
  }
  
  
  /**
   * 
   */
  public Component getTableCellRendererComponent( JTable table, Object value, 
      boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex ) {
    
    // Set the text
    setText( value.toString() );
    setIcon( null );

    setBorder( UIManager.getBorder( "TableHeader.cellBorder" ) );
    
    setHorizontalAlignment( SwingConstants.CENTER );
    setHorizontalTextPosition( SwingConstants.LEFT );
    
    
    return this;
    
  }


}


