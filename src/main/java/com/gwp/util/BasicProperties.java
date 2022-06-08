/**
 * Title:        
 * Description:
 * Copyright:    
 * Company:      
 * @author Greg W. Pola
 * @version 1.0
 */
package com.gwp.util;

// JDK imports
import java.awt.Color;
import java.awt.Font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.ParseException;

import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;



/**
 * A class the defines some strong typed properties
 */
public class BasicProperties
extends Properties {
  
  public static final long                  serialVersionUID              = 1357908642L;
    

  static private final byte[]           blowfish_key =
  {
    (byte)0xb7,(byte)0xbb,(byte)0x02,(byte)0xf8,(byte)0x25,(byte)0x4b,(byte)0x80,(byte)0xa8,
    (byte)0x53,(byte)0xdc,(byte)0xed,(byte)0x36,(byte)0xef,(byte)0x67,(byte)0x70,(byte)0xc9,
    (byte)0xf1,(byte)0xe9,(byte)0x34,(byte)0x6a,(byte)0x69,(byte)0x9a,(byte)0x3a,(byte)0x22,
    (byte)0x56,(byte)0x8d,(byte)0xb9,(byte)0xa7,(byte)0xb6,(byte)0x97,(byte)0x68,(byte)0xf7
  };

  private Blowfish                      encryptionAgent;


  /**
   * Default constructor
   */
  public BasicProperties() {
    this( null );
  }

  
  /**
   * Constrcutor that takes a set of default properties.
   */
  public BasicProperties(Properties defaultProperties) {
    super(defaultProperties);
    encryptionAgent   = new Blowfish();
    encryptionAgent.initialize( blowfish_key, 32 );
  }

  
  /**
   * Pseudo clone method
   * 
   * @return 
   */
  public BasicProperties makeClone() {
    BasicProperties theCopy = new BasicProperties(null);

    Enumeration<?> e = propertyNames();

    while (e.hasMoreElements()) {
      String s = (String)e.nextElement();
      theCopy.setString(s,getString(s));
    }

    return theCopy;
  }

  
  /**
   * Populates the properties from data stored in the byte array
   * @param  data the property object encoded as a byte array
   * @param  offset the offset to start parsing in the byte array
   * @return The number of bytes consumed by the method
   * @see BasicProperties#toByteArray
   */
  public int fromByteArray( byte[] data, int offset ) {
    int length = 0;
    int result = 0;

    try {
      while (data[length+offset] != '\0') {
        length++;
      }
    } catch ( ArrayIndexOutOfBoundsException e ) {
    } finally {
      result = fromByteArray( data, offset, length );
    }
    
    return result;
  }


  /**
   * Populates the properties from data stored in the byte array
   * 
   * @param data   the property object encoded as a byte array
   * @param offset the offset to start parsing in the byte array
   * @param length
   * 
   * @return The number of bytes consumed by the method
   * @see BasicProperties#toByteArray
   */
  public int fromByteArray( byte[] data, int offset, int length ) {

    try {
      load( new ByteArrayInputStream( data, offset, length ) );
      return length;
      
    } catch ( IOException e ) {
      return 0;
      
    } catch ( ArrayIndexOutOfBoundsException e ) {
      return 0;
    }
  }


  /**
   * Gets a boolean property value
   */
  public boolean getBoolean( String key ) {
    return getBoolean(key, false);
  }


  /**
   * Gets a boolean property value
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    String result = getProperty(key);
    if (result == null) {
      return defaultValue;
    } else if ( result.equals( "1" ) ) {
      return true;
    } else if ( result.equals( "0" ) ) {
      return false;
    } else {
      return Boolean.valueOf( result ).booleanValue();
    }
  }


  /**
   * Gets a byte property value
   */
  public byte getByte( String key ) {
    return getByte( key, (byte) 0 );
  }


  /**
   * Gets a byte property value
   */
  public byte getByte( String key, byte defaultValue ) {
    String result = getProperty(key);
    if (result == null) {
      return defaultValue;
    } else {
      return Byte.valueOf(result).byteValue();
    }
  }


  /**
   * Gets a color value in the form of an rgb trio
   *
   * @param key
   * @return
   */
  public Color getColor( String key ) {
    String value = getString( key );
    int[] rgb = new int[3];
    int index = 0;

    if ( ( value == null ) || ( value.length() < 1 ) ) {
      return null;
    } else {
      StringTokenizer tokens = new StringTokenizer( value, "," );
      while ( tokens.hasMoreElements() ) {
        try {
          rgb[index] = Integer.parseInt( tokens.nextToken() );
        } catch ( NumberFormatException nfe ) {
        }
        index++;
      }

      return ( new Color( rgb[0], rgb[1], rgb[2] ) );
    }
  }


  /**
   * Gets a byte value
   */
  public String getEncryptedString(String key) {
    byte input[];
    byte padded[];
    int size;
    String value;

    value = getString(key);
    if (value==null)
      return "";
    try {
      input = Utilities.hexStringToBytes(value);
    } catch (NumberFormatException e) {
      return "";
    }
    size = encryptionAgent.getOutputLength(input.length);
    padded = new byte[size];
    java.util.Arrays.fill(padded, (byte) 0);
    System.arraycopy(input, 0, padded, 0, input.length);

    encryptionAgent.decrypt(padded, 0, padded, 0, size);

    String resString;

    try {
      resString = new String(padded,"UTF-8");
    } catch (java.io.UnsupportedEncodingException uee) {
      resString = new String(padded);
    }

    return resString.trim();
  }

  

  /**
   * Returns a float value property.
   */
  public float getFloat( String key ) {
    return getFloat( key, 0.0f );
  }


  /**
   * Returns a float value property.
   */
  public float getFloat( String key, float defaultValue ) {
    String result = getProperty( key );
    
    if ( result == null || result.equals( "" ) ) {
      return defaultValue;
      
    } else {
      try {
        return Float.valueOf( result ).floatValue();
        
      } catch ( NumberFormatException e ) {
      }
    }

    return defaultValue;
  }


  /**
   * Gets a font value
   *
   * @param key
   * @return
   */
  public Font getFont( String key ) {
    String value = getString( key );

    // sanity check
    if ( value == null ) return null;

    // Parse the font constructor arguments
    StringTokenizer tokens = new StringTokenizer( value, "," );
    if ( tokens.countTokens() < 3 ) return null;
    
    String name = tokens.nextToken().trim();
    String style = tokens.nextToken().trim();
    String size = tokens.nextToken().trim();

    // parse the style
    int iStyle = 0;
    try {
      iStyle = Integer.parseInt( style );
    } catch ( Exception e ) {
      return null;
    }
    
    // parse the size
    int iSize = 0;
    try {
      iSize = Integer.parseInt( size );
    } catch ( Exception e ) {
      return null;
    }
  
    // return the font
    return new Font( name, iStyle, iSize );
  }


  /**
   * Returns an InetAddress value property.
   */
  public InetAddress getInetAddress( String key ) {
    String result = getProperty(key);
    try {
      return InetAddress.getByName(result);
    } catch (UnknownHostException uhe) {
      return null;
    }
  }


  /**
   * Returns an <code>IPAddress</code> value property
   * @param key
   * @return
   */
  public IpAddress getIpAddress( String key ) {
    String result = getProperty( key );
    try {
      return ( new IpAddress( result ) );
    } catch ( ParseException pe ) {
    }

    return null;
  }


  /**
   * Returns an integer value property.
   */
  public int getInt( String key ) {
    return getInt( key, 0 );
  }


  /**
   * Returns an integer value property.
   */
  public int getInt( String key, int defaultValue ) {
    String result = getProperty( key );
    if (result == null || result.equals("")) {
      return defaultValue;
    } else {
      try {
      return Integer.valueOf(result).intValue();
      } catch (NumberFormatException e) {
        if (result.equals("true")) {
          return 1;
        }
        if (result.equals("false")) {
          return 0;
        }
        return defaultValue;
      }
    }
  }


  /**
   * Returns the int array value property or null if it doesn't exist.
   * 
   * @param key
   * @return
   */
  public int[] getIntArray( String key ) {
    int[] result = null;
    
    String val = getString( key, null );
    if ( val != null ) {
      StringTokenizer tokens = new StringTokenizer( val, "," );
      result = new int[tokens.countTokens()];
      
      for ( int index = 0; tokens.hasMoreTokens(); index++ ) {
        result[index] = 0;
        try {
          result[index] = Integer.parseInt( tokens.nextToken() );
        } catch ( NumberFormatException pe ) {}
      }
    }
    
    return result;
  }
  

  /**
   * Returns a long value property.
   */
  public long getLong(String key) {
    return getLong(key, 0);
  }


  /**
   * Returns a long value property.
   */
  public long getLong(String key, long defaultValue) {
    String result = getProperty(key);
    if (result == null || result.equals("")) {
      return defaultValue;
    } else {
      try {
      return Long.valueOf(result).longValue();
      } catch (NumberFormatException e) {
        if (result.equals("true")) {
          return 1;
        }
        if (result.equals("false")) {
          return 0;
        }
        return defaultValue;
      }
    }
  }



  /**
   * Returns a short value property
   */
  public short getShort(String key) {
    return getShort(key, (short)0);
  }


  public short getShort(String key, short defaultValue) {
    String result = getProperty(key);
    if (result == null) {
      return defaultValue;
    } else {
      return Short.valueOf(result).shortValue();
    }
  }


  /**
   * Returns a String value property
   */
  public String getString( String key ) {
    return getString( key, "" );
  }


  /**
   * Returns a String value property
   */
  public String getString( String key, String defaultValue ) {
    String result = getProperty( key );
    if ( result == null ) {
      return defaultValue;
    } else {
      return result;
    }
  }


  /**
   * Returns the string array value property or null if it doesn't exist.
   * 
   * @param key
   * @return
   */
  public String[] getStringArray( String key ) {
    String[] result = null;
    
    String val = getString( key, null );
    if ( val != null ) {
      StringTokenizer tokens = new StringTokenizer( val, "," );
      result = new String[tokens.countTokens()];
      
      for ( int index = 0; tokens.hasMoreTokens(); index++ ) {
        result[index] = tokens.nextToken();
      }
    }
    
    return result;
  }
  

  /**
   * Retrieve and decode a obfuscated byte array
   * 
   * @param key    Property key
   * 
   * @return The decoded string
   */
  public byte[] getVeiledBytes(String key) {
    String value = getString(key);
    if (value==null)
      return new byte[0];

    return decryptBytes(value);
  }

  /**
   * Retrieve and decode a obfuscated string property
   * 
   * @param key    Property key
   * 
   * @return The decoded string
   */
  public String getVeiledString(String key) {
    String value = getString(key);
    if (value==null)
      return "";

    return decryptString(value);
  }



  /**
   * Loads properties from a file
   */
  public void load( String filename ) {
    FileInputStream input = null;

    try {
      input = new FileInputStream( filename );
    } catch ( FileNotFoundException fnf1 ) {
      return;
    }

    try {
      load( input );
    } catch ( IOException ioe1 ) {
    }

    try {
      input.close();
    } catch ( IOException ioe2 ) {
    }
  }


  /**
   * Merge this instance's property set into the specified set
   * 
   * @param props  Recipient set
   */
  public void mergeInto(BasicProperties props) {
    Enumeration<?> e = propertyNames();
    while (e.hasMoreElements()) {
      String s = (String)e.nextElement();
      props.setString(s,getString(s));
    }

  }

  
  /**
   * Prints the properties to the print stream.
   * 
   * @param output
   */
  public void print( PrintStream output ) {
    Enumeration<?> e = propertyNames();
    
    while ( e.hasMoreElements() ) {
      String key = (String) e.nextElement();
      String value = getString( key );
      output.println( key + " = " + value );
    }

  }


  /**
   * Delete all properties with the specified key prefix
   * 
   * @param prefix The prefix
   */
  public void removePrefix(String prefix) {

    Enumeration<?> e = propertyNames();

    while (e.hasMoreElements()) {
      String s = (String)e.nextElement();
      if (s.startsWith(prefix)) {
        remove(s);
      }
    }
  }


  /**
   * Stores a boolean value
   */
  public boolean setBoolean( String key, boolean value ) {
    String val = value ? String.valueOf( 1 ) : String.valueOf( 0 );
    String oldValue = (String) setProperty( key, val );
    if ( oldValue == null ) {
      return value;
    } else {
      if ( oldValue.equals( "1" ) ) {
        return true;
      } else if ( oldValue.equals( "0" ) ) {
        return false;
      } else {
        return Boolean.valueOf( oldValue ).booleanValue();
      }
    }
  }


  /**
   * Stores a byte value
   */
  public short setByte( String key, byte value ) {
    String oldValue = (String) setProperty( key, String.valueOf( value ) );
    if ( oldValue == null ) {
      return value;
    } else {
      return Byte.valueOf( oldValue ).byteValue();
    }
  }


  /**
   * Stores a color value
   *
   * @param key
   * @param color
   */
  public Color setColor( String key, Color color ) {
    // sanity check
    if ( color == null ) return color;
    
    String newValue = new String( String.valueOf( color.getRed() ) + "," + String.valueOf( color.getGreen() )
      + "," + String.valueOf( color.getBlue() ) );
    Color oldValue = getColor( key );

    if ( setProperty( key, newValue ) == null ) {
      return color;
    } else {
      return oldValue;
    }
  }


  /**
   * Stores a byte value
   */
  public String setEncryptedString(String key, String value) {
    byte output[];
    int size;

    byte[] u8;

    try {
      u8 = value.getBytes("UTF-8");
    } catch (java.io.UnsupportedEncodingException uee) {
      u8 = value.getBytes();
    }

    size = encryptionAgent.getOutputLength(u8.length);
    output = new byte[size];
    java.util.Arrays.fill(output, (byte) 0);
    System.arraycopy(u8, 0, output, 0, u8.length);

    encryptionAgent.encrypt(output, 0, output, 0, size);
    try {
      return setString( key, Utilities.hexBytesToString( output ) );
    } catch (NumberFormatException e) {
      return setString(key, "");
    }
  }


  /**
   * Stores a font value
   *
   * @param key
   * @param color
   */
  public Font setFont( String key, Font font ) {
    // sanity check
    if ( ( key == null ) || ( font == null ) ) return font;
    
    String newValue = new String( font.getFamily() + "," + 
        String.valueOf( font.getStyle() ) + "," + String.valueOf( font.getSize() ) );
    Font oldValue = getFont( key );

    if ( setProperty( key, newValue ) == null ) {
      return font;
    } else {
      return oldValue;
    }
    
  }


  /**
   * Stores an InetAddress value
   */
  public InetAddress setInetAddress( String key, InetAddress value ) {
    if (value != null) {
      String oldValue = (String) setProperty(key, value.getHostAddress());
      try {
        return InetAddress.getByName(oldValue);
      } catch (UnknownHostException uhe) {
      }
    }

    return null;
  }


  /**
   * Stores an <code>IpAddress</code> value
   *
   * @param key
   * @param value
   * @return
   */
  public IpAddress setIpAddress( String key, IpAddress value ) {
    if ( value != null ) {
      String oldValue = (String) setProperty( key, value.toString() );
      try {
        return ( new IpAddress( oldValue ) );
      } catch ( ParseException pe ) {
      }
    }

    return null;
  }


  /**
   * Stores an float value
   */
  public float setFloat( String key, float value ) {
    String oldValue = (String) setProperty( key, String.valueOf( value ) );
    
    if ( oldValue == null ) {
      return value;
    } else {
      return Float.valueOf( oldValue ).floatValue();
    }
  }


  /**
   * Stores an integer value
   */
  public int setInt(String key, int value) {
    String oldValue = (String) setProperty(key, String.valueOf(value));
    if (oldValue == null) {
      return value;
    } else {
      return Integer.valueOf(oldValue).intValue();
    }
  }

  
  /**
   * Stores a string array value
   * 
   * @param key
   * @param value
   * @return
   */
  public int[] setIntArray( String key, int[] values ) {
    int[] oldValue = getIntArray( key );

    // Clear check
    if ( ( values == null ) || ( values.length == 0 ) ) {
      remove( key );
    } else {
      boolean noSep = true;
      StringBuffer buffer = new StringBuffer();
      
      for ( int val : values ) {
        if ( noSep ) {
          buffer.append( val );
          noSep = false;
        } else {
          buffer.append( "," ).append( val );
        }        
      }
      
      setProperty( key, buffer.toString() );
    }
    
    return oldValue;    
  }
  

  /**
   * Stores a long value
   */
  public long setLong(String key, long value) {
    String oldValue = (String) setProperty(key, String.valueOf(value));
    if (oldValue == null) {
      return value;
    } else {
      return Long.valueOf(oldValue).longValue();
    }
  }



  /**
   * Stores an short value
   */
  public short setShort(String key, short value) {
    String oldValue = (String) setProperty(key, String.valueOf(value));
    if (oldValue == null) {
      return value;
    } else {
      return Short.valueOf(oldValue).shortValue();
    }
  }

  
  /**
   * Stores a string value
   * 
   * @param key
   * @param value
   * @return
   */

  public String setString(String key, String value) {
    return (String) setProperty(key, value);
  }

  
  /**
   * Stores a string array value
   * 
   * @param key
   * @param value
   * @return
   */
  public String[] setStringArray( String key, String[] value ) {
    String[] oldValue = getStringArray( key );

    // Clear check
    if ( ( value == null ) || ( value.length == 0 ) ) {
      remove( key );
    } else {
      int index = 0;
      StringBuffer buffer = new StringBuffer( value[index++] );
      for ( ; index < value.length; index++ ) {
        buffer.append( "," ).append( value[index] );
      }
      
      setProperty( key, buffer.toString() );
    }
    
    return oldValue;    
  }
  

  /**
   * Store a byte array as an obfuscated string
   * 
   * @param key    Property key
   * @param value  Property value
   * 
   * @return  The encoded string
   */
  public String setVeiledBytes(String key, byte[] value) {
    String s = encryptBytes(value);
    return setString(key, s);
  }



  /**
   * Store a string as an obfuscated string
   * 
   * @param key    Property key
   * @param value  Property value
   * 
   * @return  The encoded string
   */
  public String setVeiledString(String key, String value) {
    String s = encryptString(value);
    return setString(key, s);
  }


  /**
   * Stores the properties to file.
   * 
   * @param filename Name of file to store the properties in
   * @param header   Property file header comment or null
   * 
   * @return true on success
   */
  public boolean store(String filename, String header) {
    FileOutputStream output = null;
    boolean ok = true;

    try {
      output = new FileOutputStream(filename);
    } catch (FileNotFoundException fnf1) {
      return false;
    }

    try {
      store(output, header);
    } catch (IOException ioe1) {
      ok = false;
    }

    try {
      output.close();
    } catch (IOException ioe2) {
      ok = false;
    }

    return ok;
  }
  

  /**
   * Serializes the properties to a byte array
   * @return The property object encoded as an byte array
   * @see BasicProperties#fromByteArray
   */
  public byte[] toByteArray() {
    ByteArrayOutputStream baos;

    try {
      baos = new ByteArrayOutputStream( size()*25 );
      store( baos, "" );
      baos.write('\0');
    
      return baos.toByteArray();
      
    } catch ( IOException e ) {
      return new byte[0];
      
    }
  }



  /**
   * Greg's obsfuscation encryptor
   * 
   * This version is compatible with the Agents' version.
   * 
   * NOTE: Do not use this method to encrypt byte arrays.
   * Use encryptBytes() instead.  Using this method for
   * bytes will result in loss of data due to byte-char
   * conversions.
   * 
   * @param data   The string to encrypt
   * 
   * @return The encrypted string
   */
  public static final String encryptString( String data )
  {
    StringBuffer temp = new StringBuffer();
    int residue = 0x7e71;

    for (int i=0; i < data.length(); i++) {
      char c1 = data.charAt(i);
      int v1 = ((c1 >> 4) + 'A' + (residue)%10);
      int v2 = ((c1 & 0x0f) + 'A' + (residue>>1)%10);
      residue += ((residue >> 4) + c1);
      residue = residue & 0xffff;

      temp.append((char)v1);
      temp.append((char)v2);
    }

    return temp.toString();
  }


  /**
   * Greg's obsfuscation decryptor
   * 
   * This version is compatible with the Agents' version
   * 
   * @param data   The string to decrypt.
   * 
   * @return The decrypted string
   */
  public static final String decryptString( String data ) {

    StringBuffer temp = new StringBuffer();
    int residue = 0x7e71;

    for (int i=0; i < (data.length()/2); i++) {

      int v = ((data.charAt(i*2) - 'A' - (residue)%10) << 4) +
              (data.charAt(i*2+1) - 'A' - (residue>>1)%10);
      temp.append((char)v);
      residue += (residue >> 4) + (char)v;
      residue = residue & 0xffff;
    }

    return temp.toString();
  }




  /**
   * Greg's obsfuscation encryptor
   * 
   * This version is compatible with the Agents' version
   * 
   * @param data   The byte array to encrypt
   * 
   * @return The encrypted string
   */
  private static final String encryptBytes(byte[] data)
  {
    StringBuffer temp = new StringBuffer();
    int residue = 0x7e71;

    for (int i=0; i < data.length; i++) {
      int c1 = data[i] & 0xff;
      int v1 = ((c1 >> 4) + 'A' + (residue)%10);
      int v2 = ((c1 & 0x0f) + 'A' + (residue>>1)%10);
      residue += ((residue >> 4) + c1);
      residue = residue & 0xffff;

      temp.append((char)v1);
      temp.append((char)v2);
    }

    return temp.toString();
  }




  /**
   * Greg's obsfuscation decryptor
   * 
   * This version is compatible with the Agents' version
   * 
   * @param data   The encrypted string
   * 
   * @return The decrypted bytes
   */
  private static final byte[] decryptBytes(String data) {

    StringBuffer temp = new StringBuffer();
    int residue = 0x7e71;
    byte[] result = new byte[data.length()/2];

    for (int i=0; i < (result.length); i++) {

      int v = ((data.charAt(i*2) - 'A' - (residue)%10) << 4) +
              (data.charAt(i*2+1) - 'A' - (residue>>1)%10);
      result[i] = (byte)(v & 0xff);
      temp.append((char)v);
      residue += (residue >> 4) + (char)v;
      residue = residue & 0xffff;
    }

    return result;
  }


}
