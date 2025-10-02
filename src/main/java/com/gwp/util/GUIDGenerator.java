/**
 * 
 */
package com.gwp.util;

import com.sun.security.auth.module.NTSystem;

import java.io.*;
import java.util.UUID;

/**
 * A class that generates a consistent GUID for the installation machine.
 * 
 * @author Greg
 *
 */
public class GUIDGenerator {
  
  private static String SERIAL_NUMBER = null;
  
  
  public static final String getSerialNumber() {
    if ( SERIAL_NUMBER != null ) return SERIAL_NUMBER;
    
    if ( OSChecker.isMac() ) {
      OutputStream os = null;
      InputStream is = null;

      Runtime runtime = Runtime.getRuntime();
      Process process = null;
      try {
        process = runtime.exec(new String[] { "/usr/sbin/system_profiler", "SPHardwareDataType" });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      os = process.getOutputStream();
      is = process.getInputStream();

      try {
        os.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
      String line = null;
      String marker = "Serial Number:";
      try {
        while ((line = br.readLine()) != null) {
          if (line.indexOf(marker) != -1) {
            SERIAL_NUMBER = line.split(marker)[1].trim();
            break;
          }
        }
        
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        
        try {
          br.close();
        } catch (IOException e) {}
        
        try {
          is.close();
        } catch (IOException e) {}
        
      }

      if ( SERIAL_NUMBER == null ) {
        System.err.println( "Cannot find computer SN" );
      }

      
    } else if ( OSChecker.isSolaris() ) {
      SERIAL_NUMBER = UUID.randomUUID().toString();
      
      
    } else if ( OSChecker.isUnix() ) {
      OutputStream os = null;
      InputStream is = null;

      Runtime runtime = Runtime.getRuntime();
      Process process = null;
      try {
        process = runtime.exec( new String[] { "dmidecode", "-t", "system" } );
      } catch ( IOException e ) {
        throw new RuntimeException(e);
      }

      os = process.getOutputStream();
      is = process.getInputStream();

      try {
        os.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      BufferedReader br = new BufferedReader(new InputStreamReader( is ) );
      String line = null;
      String marker = "Serial Number:";
      try {
        while ((line = br.readLine()) != null) {
          if (line.indexOf(marker) != -1) {
            SERIAL_NUMBER = line.split(marker)[1].trim();
            break;
          }
        }
        
      } catch ( IOException e ) {
        throw new RuntimeException( e );
        
      } finally {
        try {
          br.close();
        } catch ( IOException e ) {}
        
        try {
          is.close();
        } catch ( IOException e ) {}
      }

      if ( SERIAL_NUMBER == null ) {
        System.err.println( "Cannot find computer SN" );
      }

      
    } else if ( OSChecker.isWindows() ) {
      NTSystem ntSystem = new NTSystem();
      SERIAL_NUMBER = ntSystem.getDomainSID();

    } else {
      // Default just generate a UUID
      SERIAL_NUMBER = UUID.randomUUID().toString();
      
    }
    
    return SERIAL_NUMBER;
  }

  public static String getMotherboardSN() {
    String result = "";
      try {
        File file = File.createTempFile("realhowto",".vbs");
        file.deleteOnExit();
        FileWriter fw = new java.io.FileWriter(file);

        String vbs =
           "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
          + "Set colItems = objWMIService.ExecQuery _ \n"
          + "   (\"Select * from Win32_BaseBoard\") \n"
          + "For Each objItem in colItems \n"
          + "    Wscript.Echo objItem.SerialNumber \n"
          + "    exit for  ' do the first cpu only! \n"
          + "Next \n";

        fw.write(vbs);
        fw.close();
        Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        BufferedReader input =
          new BufferedReader
            (new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
           result += line;
        }
        input.close();
      }
      catch(Exception e){
          e.printStackTrace();
      }
      return result.trim();
    }
}
