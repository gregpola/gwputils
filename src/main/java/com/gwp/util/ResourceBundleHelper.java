/**
 * ResourceBundleHelper.java
 * Created on January 22, 2009
 * 
 * Copyright (c) 2009 Greg Warren Pola
 */
package com.gwp.util;

// jdk imports
import java.util.ResourceBundle;


/**
 * @author gpola
 *
 */
public class ResourceBundleHelper {
  
  // static data members
  /////////////////////////////////////////////////////////////////////////////

  
  // dynamic data members
  /////////////////////////////////////////////////////////////////////////////

  private ResourceBundle                    resourceBundle;


  
  /**
   * Constructor.
   * 
   * @param bundle
   */
  public ResourceBundleHelper( ResourceBundle bundle ) {
    resourceBundle = bundle;
  }
  
  
  /**
   * Returns the value for the key from the resource bundle. If the key is not 
   * found or the bundle is null, the defaultValue is returned.
   * 
   * @param key
   * @param defaultValue
   * @return
   */
  public String getString( String key, String defaultValue ) {
    String result = defaultValue;
    
    try {
      String sVal = resourceBundle.getString( key );
      
      if ( ( sVal != null ) && ( sVal.length() > 0 ) ) {
        result = sVal;
      }
    } catch ( Exception e ) {}
    
    return result;
  }
  
}
