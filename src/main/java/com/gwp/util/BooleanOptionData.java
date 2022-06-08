package com.gwp.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * <p>Title: BooleanOptionData</p>
 * <p>Description: Data object to hold selected info for a tree node that
 * can be selected or not selected.</p>
 * @author Greg W Pola
 * @version 1.0
 */

public class BooleanOptionData {


  public Object object = null;
  private boolean booleanValue = false;
  private boolean enabled = true;
  private PropertyChangeSupport propSupport = null;
  public static final String SELECTION_CHANGE = "optionSelectionChanged";


  /**
   * Constructor.
   *
   * @param java.lang.Object.
   * @param boolean  whether the option is selected or not.
   */
  public BooleanOptionData( Object object , boolean value ) {

    this.object = object;
    booleanValue = value;

    propSupport = new PropertyChangeSupport( this );

  }

  public BooleanOptionData( Object object , boolean value , boolean setEnable) {
    this(object, value);
    enabled = setEnable;
  }


  /**
   * Returns whether or not this object is selected or not.
   *
   * @return boolean.
   */
  public boolean isSelected() {

    return booleanValue;

  }


  /**
   * Sets the selected value of this object.
   *
   * @param boolean.
   */
  public void setSelected( boolean sel ) {

    if ( booleanValue != sel ) {

      booleanValue = sel;
      propSupport.firePropertyChange( SELECTION_CHANGE, !booleanValue, booleanValue );

    }
  }


  /**
   * Returns the object wrapped by this object.
   *
   * @return java.lang.Object.
   */
  public Object getObject() { return object; }


  /**
   * Enable or disable editability of this item.
   */
  public void setEnabled(boolean setenable) {
    enabled = setenable;
  }
  

  /**
   * Enable or disable editability of this item.
   */
  public boolean isEnabled() {
    return enabled;
  }
  

  /**
   * Overrides method in Object base class.
   *
   * @return java.lang.String.
   */
  public String toString() {
    
    return object.toString();
    
  }


  /**
   * PropertyChangeSupport.
   *
   * @param java.beans.PropertyChangeListener.
   */
  public void addPropertyChangeListener( PropertyChangeListener l ) {

    propSupport.addPropertyChangeListener( l );

  }


} // end BooleanOptionData class