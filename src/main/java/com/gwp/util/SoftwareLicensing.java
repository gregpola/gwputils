/**
 * SoftwareLicensing.java
 * Created on Oct 26, 2011
 * 
 * Copyright (c) 2009-2010 Greg W. Pola. All rights reserved.
 * @author Greg
 */
package com.gwp.util;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * 
 * @author Greg
 *
 */
public class SoftwareLicensing {
  
  public static String getLicenseKey( String uuid ) {
    return DigestUtils.md5Hex( uuid );
  }
  

  /**
   * @param args
   */
  public static void main( String[] args ) {
    if ( ( args == null ) || ( args.length == 0 ) || ( args[0] == null ) || args[0].isEmpty() ) {
      System.out.println( "Invalid arguments!" );
    } else {
      System.out.println( SoftwareLicensing.getLicenseKey( args[0] ) );
    }
  }

}
