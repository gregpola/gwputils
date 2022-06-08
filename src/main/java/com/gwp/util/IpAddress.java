/**
 * @author Greg W Pola
 * @version 1.0
 */
package com.gwp.util;


// jdk imports
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;



/**
 * IpAddress
 * @author Evan R. Farrer
 * 
 *
 */
public class IpAddress {
  
  // Static data members
  //////////////////////////////////////////////////////////////////////

  public static final int         IP_ADDRESS_SIZE                         = 4;
  
  public static final byte        LAST_NODE_VALUE                         = (byte)( (int)255 &0xff );

  public static final byte[]      LOCAL_HOST_ADDRESS                      = 
    { 127, 0, 0, 1 };

  public static final byte[]      LOCAL_NETMASK_ADDRESS                   = 
    { LAST_NODE_VALUE, LAST_NODE_VALUE, LAST_NODE_VALUE, 0 };
  
  public static final byte[]      LAST_ADDRESS                            =
    { LAST_NODE_VALUE, LAST_NODE_VALUE, LAST_NODE_VALUE, LAST_NODE_VALUE };
  
  public static final IpAddress   FIRST_IP_ADDRESS                        = 
    new IpAddress();
  
  public static final IpAddress   LOCAL_HOST_IP_ADDRESS                   = 
    new IpAddress( LOCAL_HOST_ADDRESS );
  
  public static final IpAddress   LAST_IP_ADDRESS                         = 
    new IpAddress( LAST_ADDRESS );
  
  
  // Dynamic data members
  //////////////////////////////////////////////////////////////////////


  /** The ip addr */
  private byte[] address = new byte[IP_ADDRESS_SIZE];

  
  /**
   * Constructs an empty ip address
   */
  public IpAddress() {
    reset();
  }

  /**
   * Constructs an ip addr from a string
   *
   * @param addr: The ip addr
   */
  public IpAddress( byte[] addr) {
    System.arraycopy( addr, 0, address, 0, address.length );
  }

  /**
   * Constructs an ip addr from a string
   *
   * @param addr: The ip addr
   */
  public IpAddress( String addr ) throws ParseException {
    if ( ( addr == null ) || ( addr.length() < 7 ) ) {
      reset();
    } else {
      
      try {
        address = InetAddress.getByName(addr).getAddress();
      }catch ( java.net.UnknownHostException e ) {
        throw new ParseException("Unable to parse ip address", -1);
      }
    }
  }

  /**
   * Compares the ip address with the addr
   *
   * @param addr:  The ip address
   */
  public int compareTo( Object o ) {
    int result = 0;
    int multiplier = 256 * 256 * 256;

    if ( o instanceof IpAddress ) {
      IpAddress addr = (IpAddress) o;
      for ( int i = 0; i < IP_ADDRESS_SIZE; i++ ) {
        result += ( ( uByteToInt( address[i] ) - uByteToInt( addr.address[i] ) ) * multiplier );
        multiplier /= 256;
      }
    }

    return result;
  }

  
  /**
   * Returns the count of ip addresses in the range from begin to end inclusive.
   * 
   * @param begin
   * @param end
   * @return
   */
  public static int getCount( IpAddress begin, IpAddress end ) {
    if ( begin.compareTo( end ) >= 0 ) {
      return 0;
    }
    
    return end.compareTo( begin );
    
  }
  

  /**
   * Tests the IP address for equality with another IP address
   */
  public boolean equals( IpAddress otherIp ) {
    return ( compareTo( otherIp ) == 0 );
  }


  /**
   * Returns the ip address in byte[] format
   */
  public byte[] getBytes() {
    return address;
  }

  /**
   * Returns an IpAdress representing either the minimum or maximum ip address for a given address and netmask
   *
   * @param addr:  The ip adresss
   * @param netmask:  The netmask
   * @param min:  True if want the minimun address
   *
   * @return The min or max IpAdress
   */
  public static IpAddress getMaskedAddr( IpAddress addr, 
                                         IpAddress netmask, 
                                         boolean min ) throws ParseException {
    int netmask_max = 255;
    int unsigned_octet;
    IpAddress res = new IpAddress();
    res.address = new byte[ addr.address.length ];

    // Check the netmask
    // We only want valid netmasks regexp: 1*0*
    for( int i = 0; i < netmask.address.length; i++ ) {
      unsigned_octet = netmask.address[i];
      if (unsigned_octet < 0)
        unsigned_octet += 256;

      if ( unsigned_octet <= netmask_max ) {
        netmask_max = unsigned_octet;
      } else {
        // we have a hole in the netmask regexp: 1+0+1+
        throw new ParseException("Invalid netmask.", -1);
      }
      
      switch(netmask.address[i]) {
        case (byte)255:
        case (byte)254:
        case (byte)252:
        case (byte)248:
        case (byte)240:
        case (byte)224:
        case (byte)192:
        case (byte)128:
        case (byte)0:
          break;
        default:
          throw new ParseException("Invalid netmask.", -1);
      }
    }

    if ( min ) {
      for( int i = 0; i < addr.address.length; i++ ) {
        res.address[i] = (byte) (uByteToInt(addr.address[i]) & uByteToInt(netmask.address[i]));
      }
    } else {
      for( int i = 0; i < addr.address.length; i++ ) {
        res.address[i]   = (byte) (uByteToInt(addr.address[i]) | ~uByteToInt(netmask.address[i]));
      }
    }

    return res;
  }

  /**
   * Returns an IpAdress representing either the minimum or maximum ip address for a given address and netmask
   *
   * @param addr:  The ip adresss
   * @param netmask:  The netmask
   * @param min:  True if want the minimun address
   *
   * @return The min or max IpAdress
   */
  public static IpAddress getMaskedAddr( IpAddress addr, 
                                         int netmask, 
                                         boolean min ) throws ParseException {
    return getMaskedAddr( addr, getNetmask(netmask) , min );
  }

  
  /**
   * Create a IpAddress from netmask bits
   * @param data: the netmask
   */
  private static IpAddress getNetmask(int bits) {
    IpAddress netmask = new IpAddress();

    for( int i = 0; i < netmask.address.length; i++, bits-=8 ) {
      if ( bits >= 8 ) {
        netmask.address[i] = (byte) 255;
      } else if ( bits <= 0 ) {
        netmask.address[i] = (byte) 0;
      } else {
        do {
          netmask.address[i] |= (byte) (1<<(8-bits));
          bits--;
        }while(bits > 0);
      }
    }

    return netmask;
  }

  
  /**
   * Returns the range of ip addresses from begin to end inclusive.
   * 
   * @param begin
   * @param end
   * @return
   */
  public static IpAddress[] getRange( IpAddress begin, IpAddress end ) {
    ArrayList<IpAddress> result = new ArrayList<IpAddress>();
    IpAddress current = new IpAddress( begin.getBytes() );
    
    while( ( current != null ) && ( current.compareTo( end ) <= 0 ) ) {
      if ( !current.isReserved() ) {
        result.add( current );
      }
      current = current.next();
    }    
    
    return result.toArray( new IpAddress[result.size()] );
  }


  /**
   * Low-cost method to iterate over a range of IP addresses
   * 
   * @param current Contains current IP address (modified)
   * @param end     Last (inclusive) IP address in range
   * @param result  Buffer to receive the next next IP address
   * 
   * @return  Buffer containing next IP address or null
   */
  public static StringBuffer getNextInRange(IpAddress current, 
                                            IpAddress end, 
                                            StringBuffer result) {
    while (current.isReservedInRange()) {
      current.bumpToNext();
    }

    if (current.compareTo(end) > 0) {
      return null;
    }

    result.setLength(0);
    result.append(current.toString());
    current.bumpToNext();

    return result;
  }


  private boolean bumpToNext() {
    if (equals( LAST_IP_ADDRESS ) ) {
      return false;
    }
    
    for ( int i = IP_ADDRESS_SIZE - 1; i > -1; i-- ) {
      if ( address[i] == LAST_NODE_VALUE ) {
        address[i] = 0;
      } else {
        address[i]++;
        break;
      }
    }

    return true;
  }


  public boolean isLocalHost() {
    
    return equals( LOCAL_HOST_IP_ADDRESS );
    
  }
  

  /**
   * Returns whether or not this ip address is a reserved address.
   * 
   * @return
   */
  public boolean isReserved() {
    if ( equals( FIRST_IP_ADDRESS ) ) return true;
    
    if ( equals( LAST_IP_ADDRESS ) ) return true;
    
    if ( equals( LOCAL_HOST_IP_ADDRESS ) ) return true;
    
    if ( address[0] >= 224 ) return true;
    
    return false;    
  }


  /**
   * Returns whether or not this ip address is a reserved address.
   * 
   * @return
   */
  private boolean isReservedInRange() {
    if ( equals( FIRST_IP_ADDRESS ) ) return true;
    
    if ( equals( LAST_IP_ADDRESS ) ) return true;
    
    return false;    
  }

  
  
  /**
   * Tests whether the ip address is valid i.e. not all 0's
   */
  public boolean isValid() {
    if ( address.length != IP_ADDRESS_SIZE ) {
      return false;
    }

    for ( int i = 0; i < IP_ADDRESS_SIZE; i++ ) {
      if ( address[i] != 0 ) {
        return true;
      }
    }

    return false;
  }



  /**
   * Tests whether the ip address is a broadcast address
   */
  public boolean isBroadcast() {
    int firstOctet = uByteToInt(address[0]);
    return firstOctet == 255;
  }



  /**
   * Clears the value of the IP address
   */
  public void reset() {
    for ( int i = 0; i < IP_ADDRESS_SIZE; i++ ) {
      setByte( (byte)0, i );
    }
  }


  /**
   * Sets the value of the IP address
   */
  public void set( byte[] otherIp ) {
    for ( int i = 0; i < IP_ADDRESS_SIZE; i++ ) {
      setByte( otherIp[i], i );
    }
  }

  
  /**
   * Returns the next ip address by incrementing by one. This returns null if there is not another
   * address to increment to.
   * 
   * @return
   */
  public IpAddress next() {
    if ( equals( LAST_IP_ADDRESS ) ) {
      return null;
    }
    
    byte[] addr = new byte[IP_ADDRESS_SIZE];
    System.arraycopy( address, 0, addr, 0, addr.length );
    
    for ( int i = IP_ADDRESS_SIZE - 1; i > -1; i-- ) {
      if ( addr[i] == LAST_NODE_VALUE ) {
        addr[i] = 0;
      } else {
        addr[i]++;
        break;
      }
    }
    
    IpAddress result = new IpAddress( addr );
    return result;
  }
  

  /**
   * Sets the value of the IP address
   */
  public void set( IpAddress otherIp ) {
    byte[] otherData = otherIp.getBytes();

    for ( int i = 0; i < IP_ADDRESS_SIZE; i++ ) {
      setByte( otherData[i], i );
    }
  }


  /**
   * Sets the value of the IP address
   */
  public void set( String newValue ) throws ParseException {
    try {
      address = InetAddress.getByName( newValue ).getAddress();
    } catch ( java.net.UnknownHostException e ) {
      throw new ParseException( "Unable to parse ip address", -1 );
    }
  }


  /**
   * Sets an octet of the ip address
   */
  public void setByte( byte value, int index ) {
    address[index] = value;
  }


  /**
   * Creates a text representation of the addr
   * @return String representation
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();

    for( int i = 0; i != address.length; i++ ) {
      sb.append(uByteToInt(address[i]));
      if ( i != address.length - 1 ) {
        sb.append(".");
      }
    }

    return sb.toString();
  }

  
  /**
   * Utility function for converting a byte to a int
   * @param b: The byte to convert
   */
  public static int uByteToInt( byte b ) {
    return (int) b & 0xFF;
  }
  
  
  /**
   * Creates a text representation of the addr
   * with the leading zeros stripped out
   * Returns the input string on an exception
   * @return String representation unpadded
   */
  public static String toUnpadded( String paddedIP ) {
    try {
      IpAddress myIP = new IpAddress(paddedIP);
      return myIP.toString();
      
    } catch ( ParseException e ) {
    	return paddedIP;
    }
    
  }
  
}
