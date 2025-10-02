/**
 * <p>Title:        Wavelink Utilities</p>
 * <p>Description:  A check box which is linked to some other component, so that when it is checked
 *                  the other component is enabled, and when unchecked the other component is disabled. </p>
 * <p>Copyright:    Copyright (c) 2002</p>
 * <p>Company:      Wavelink Corporation</p>
 * @author          Greg Pola
 * @version         1.0
 */
package com.gwp.util;


// jdk imports

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class LinkedCheckBox
extends JCheckBox {

  JComponent      linkedComponent;
  
  


  /**
   * Constructor
   */
  public LinkedCheckBox( JComponent linked ) {
    this( "", null, false, linked );
  }

  public LinkedCheckBox( Action action, JComponent linked ) {
    super( action );
    linkedComponent = linked;
  }

  public LinkedCheckBox( Icon icon, JComponent linked ) {
    this( "", icon, false, linked );
  }

  public LinkedCheckBox( Icon icon, boolean selected, JComponent linked ) {
    this( "", icon, selected, linked );
  }

  public LinkedCheckBox( String text, JComponent linked ) {
    this( text, null, false, linked );
  }

  public LinkedCheckBox( String text, boolean selected, JComponent linked ) {
    this( text, null, selected, linked );
  }

  public LinkedCheckBox( String text, Icon icon, JComponent linked ) {
    this( text, icon, false, linked );
  }

  public LinkedCheckBox( String text, Icon icon, boolean selected, JComponent linked ) {
    super( text, icon, selected );
    linkedComponent = linked;

    addItemListener( new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean enabled = ( e.getStateChange() == ItemEvent.SELECTED );
        linkedComponent.setEnabled( enabled );
        linkedComponent.setOpaque( enabled );
        if ( enabled ) {
          linkedComponent.requestFocus();
        }
        linkedComponent.repaint( new Rectangle( linkedComponent.getSize() ) );
      }
    } );

    setSelected( selected );
  }

}