/**
 * @author Greg W. Pola
 * @version 1.0
 */
package com.gwp.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;




/**
 * A set of commonly used utility and convenience methods.
 */
public class Utilities {

  // Note: Map to lower-case a...f to provide consistent result strings
  private static final String HEXCHARS[] = {"0","1","2","3","4","5","6","7","8","9", "a", "b", "c", "d", "e","f"};

  /**
   * Converts a hex value to a byte
   */
  public static final byte hexToByte( String value ) {
    return (byte) Integer.parseInt( value, 16 );
  }
  
  /**
   * Converts a byte value to a hex value
   */
  public static final String byteToHex(byte value) {
    int iTemp = (int) value;
    if (iTemp < 0) {
      iTemp += 256;
    }
    return intToHex(iTemp);
  }

  /**
   * Converts a int value to a hex value
   */
  public static final String intToHex(int value) {
    String result = Integer.toHexString(value).toUpperCase();
    if (result.length() == 1)
      result = "0" + result;
    return result;
  }

  /**
   * Add a label-value pair to a container that uses
   * GridBagLayout.
   */
  public static void addParameterRow(Container container,
                                     JLabel label,
                                     Component component) {
    GridBagLayout gridbag = null;
    try {
        gridbag = (GridBagLayout)(container.getLayout());
    } catch (Exception e) {
        System.err.println("Hey!  You called addRow with"
                           + " a container that doesn't "
                           + " use GridBagLayout!");
        return;
    }

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    //c.weighty = 1.0;
    c.insets = new Insets(0, 5, 0, 5);

    gridbag.setConstraints(label, c);
    container.add(label);
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    gridbag.setConstraints(component, c);
    container.add(component);
  }

  /**
   * Adds the contents of the specified directory and if
   * so specified, that of its subdirectories to the archive.
   * 
   * The method tries to preserve empty directories by explicity
   * adding directory entries if a source dir is empty.
   * 
   * The method fails to add empty directory entries for a directory
   * that contains files but excludes them via the filter callback.
   * 
   * Will need to rewrite this function if this flaw becomes a
   * problem.
   * 
   * @param out        the ZipOutputStream to write the entries to.
   * @param file       the file to write
   * @param parentPath Path to prefix to the zip entries
   * @param recurse    true to recurse directories, false otherwise
   * @param parentSuffix
   *                   Optional suffix to be added to the parent path
   * @param filter     File selection filter instance or null.
   *                   If specified, it will be called for each candiate
   *                   file.
   */
  public static final void addZipEntries(ZipOutputStream out,
                                         File file,
                                         String parentPath,
                                         boolean recurse,
                                         String parentSuffix,
                                         java.io.FileFilter filter) {
    ZipEntry      entry     = null;
    StringBuffer  entryPath = new StringBuffer(parentPath.replace('\\','/'));

    if ((entryPath.length() > 0) && !entryPath.toString().endsWith("/")) {
      entryPath.append("/");
    }
    entryPath.append(file.getName());
    entryPath.append(parentSuffix);

    if (file.isDirectory() && recurse) {
      if (!entryPath.toString().endsWith("/")) {
        entryPath.append("/");
      }

      File[] listing = file.listFiles();

      if (listing.length == 0) {
        // Add directory entry if it is a leaf with 
        // no further files or dirs
        entry = new ZipEntry(entryPath.toString());
        // Have to set all this stuff to mimick winzip
        entry.setMethod(ZipEntry.STORED);
        entry.setSize(0);
        entry.setCompressedSize(0);
        entry.setCrc(0);
        try {
          out.putNextEntry(entry);
        } catch (IOException ioe) {
          System.out.println("addZipEntries ioe " + ioe); 
        }
      }

      for (int j=0; j < listing.length; j++) {
        addZipEntries(out, 
                      listing[j], 
                      entryPath.toString(), 
                      recurse, "",
                      filter);
      }
    } else {
      if ((filter == null) || 
          filter.accept(new File(entryPath.toString())))  {
        entry = new ZipEntry(entryPath.toString());
        entry.setSize(file.length());
        entry.setTime(file.lastModified());
        entry.setCrc(getCRC32(file));

        try {
          out.putNextEntry(entry);
          addZipFileData(out, file);
        } catch (IOException ioe) {
        } finally {
          try {
            out.closeEntry();
          } catch (IOException ioe2) {
          }
        }
      }
    }
  }

  public static final void addZipEntries(ZipOutputStream out,
                                         File file,
                                         String parentPath,
                                         boolean recurse,
                                         String parentSuffix) {
    addZipEntries(out,file,parentPath,recurse,parentSuffix,null);
  }


  /**
   * Adds the file data to a zip stream.
   */
  public static final void addZipFileData(ZipOutputStream out, File file) {
    BufferedInputStream input = null;
    byte[] data           = new byte[1024];
    int readLength        = 0;

    try {
      input = new BufferedInputStream(new FileInputStream(file));

      readLength = input.read(data);
      while (readLength > -1) {
        out.write(data, 0, readLength);
        readLength = input.read(data);
      }
    } catch (IOException ioe) {
    } finally {
      try {
        input.close();
      } catch (IOException ioe2) {
      }
    }
  }

  /**
   * Calculates the position of a popup menu.
   * It ensures that the complete menu will be visible (Not extending past the edge of the screen).
   * @param mousePnt The mouse click point (relative to comp)
   * @param menuDim  The dimensions of the popup menu
   * @param comp     The component that was clicked
   */
  public static java.awt.Point calculatePopupLocation(java.awt.Point mousePnt,
                                                      java.awt.Dimension menuDim,
                                                      java.awt.Component comp) {
    // NOTE taskBar is a KLUDGE to prevent popup menu's from being covered by the Window's Taskbar
    // It may also be usefull on X window managers (KDE, Gnome).
    int taskBar = 50;
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

    if (mousePnt.x + comp.getLocationOnScreen().x + menuDim.width > screenSize.width)
      mousePnt.x = screenSize.width - menuDim.width - comp.getLocationOnScreen().x;
    if (mousePnt.y + comp.getLocationOnScreen().y + menuDim.height > screenSize.height - taskBar)
      mousePnt.y = screenSize.height - menuDim.height - comp.getLocationOnScreen().y - taskBar;

    return mousePnt;
  }

  /**
   * Centers the dialog in the parent frame.
   */
  public static void centerDialog(Dialog owner, JDialog dialog) {
    int x, y, width, height;
    if (owner == null) {
      Dimension wsize = Toolkit.getDefaultToolkit().getScreenSize();
      x = 0;
      y = 0;
      width = wsize.width;
      height = wsize.height;
    } else {
      x = owner.getX();
      y = owner.getY();
      width = owner.getWidth();
      height = owner.getHeight();
    }
    dialog.setLocation(x + ((width - dialog.getWidth()) / 2),
                       y + ((height - dialog.getHeight()) / 2));
  }

  /**
   * Centers the dialog in the parent frame.
   */
  public static void centerDialog(Frame owner, JDialog dialog) {
    int x, y, width, height;
    if (owner == null) {
      Dimension wsize = Toolkit.getDefaultToolkit().getScreenSize();
      x = 0;
      y = 0;
      width = wsize.width;
      height = wsize.height;
    } else {
      x = owner.getX();
      y = owner.getY();
      width = owner.getWidth();
      height = owner.getHeight();
    }
    dialog.setLocation(x + ((width - dialog.getWidth()) / 2),
                       y + ((height - dialog.getHeight()) / 2));
  }

  /**
   * Centers the frame in the parent frame.
   */
  public static void centerDialog(Frame owner, JFrame dialog) {
    int x, y, width, height;
    if (owner == null) {
      Dimension wsize = Toolkit.getDefaultToolkit().getScreenSize();
      x = 0;
      y = 0;
      width = wsize.width;
      height = wsize.height;
    } else {
      x = owner.getX();
      y = owner.getY();
      width = owner.getWidth();
      height = owner.getHeight();
    }
    dialog.setLocation(x + ((width - dialog.getWidth()) / 2),
                       y + ((height - dialog.getHeight()) / 2));
  }

  /**
   * Copies a single file
   */
  public static void copyFile(File source, File destination)
  throws IOException {
    // create the input stream
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));

    // create the output stream
    if (!destination.exists()) {
      destination.getParentFile().mkdirs();
      destination.createNewFile();
    }
    BufferedOutputStream out  = new BufferedOutputStream(new FileOutputStream(destination));

    byte[] buffer = new byte[1024];
    int read = -1;
    while ((read = in.read(buffer, 0, 1024)) != -1) {
      out.write(buffer, 0, read);
    }

    out.flush();
    out.close();
    in.close();

    // set the modification time
    destination.setLastModified(source.lastModified());
  }


  /**
   * Copied mostly from the JDK JToolBar class.
   * 
   * @param a the <code>Action</code> for the button to be added
   * @return the newly created button
   * @see Action
   */
  public static JButton createActionStyleButton( Action a ) {
    if ( a == null ) return null;
    
    Icon icon = (Icon) a.getValue( Action.SMALL_ICON );
    String text = ( icon == null ) ? (String)a.getValue( Action.NAME ) : null;
    boolean enabled = a!=null ? a.isEnabled() : true;
    String tooltip = a!=null ? (String)a.getValue( Action.SHORT_DESCRIPTION ) : null;
    
    JButton b = new JButton( text, icon );
    
    if ( icon != null ) {
      b.putClientProperty( "hideActionText", Boolean.TRUE );
    }
    
    b.setHorizontalTextPosition( SwingConstants.CENTER );
    b.setVerticalTextPosition( SwingConstants.BOTTOM );
    b.setEnabled(enabled);
    b.setToolTipText(tooltip);
    b.setBorder( null );
    b.setOpaque( false );
    b.setContentAreaFilled( false );
    b.setFocusable( false );
    
    b.setAction( a );
    
    return b;
  }


  /**
   * Deletes the all matching files from a directory
   *
   * @param directory the directoy to delete the files from
   * @param filter    a filter for matching files
   */
  public static final void deleteFiles( File directory, java.io.FileFilter filter ) {
    // Sanity check
    if ( directory == null ) return;

    // Delete all the matching files
    File[] listing = directory.listFiles();
    if ( listing != null ) {
  
      for ( int i=0; i < listing.length; i++ ) {
        
        if ( listing[i].isFile() ) {

          if ( ( filter == null ) || ( filter.accept( listing[i] ) ) ) {
            listing[i].delete();
          }
          
        }

      }
      
    }
    
  }


  /**
   * Extract the named file into the specified byte array
   * 
   * @param zip       The zip file to extract from
   * @param entryName The file name
   * @param output    The result byte array stream
   * 
   * @return 
   */
  public static final boolean extractEntryFromZip( File zip, String entryName, 
      ByteArrayOutputStream output ) {

    output.reset();

    // open the zip file
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(zip);
    } catch (ZipException ze1) {
      return false;
    } catch (IOException ioe1) {
      return false;
    }

    // Find the entry
    ZipEntry entry = findZipEntry( zipFile, entryName );
    if ( entry == null ) {
      return false;
    }

    
    // Copy the data into the provided array
    try {
      InputStream zipInput = zipFile.getInputStream(entry);
      byte[] fileData = new byte[4096];

      int readSize = 0;
      while ((readSize = zipInput.read(fileData)) != -1) {
        output.write(fileData, 0, readSize);
      }
      zipInput.close();
    } catch (IOException ioe3) {
      return false;
    }

    return true;
  }


  /**
   * Extracts the named file from a zip file to the specified directory
   */
  public static final boolean extractEntryFromZip(File zip, 
                                                  String entryName, 
                                                  File destination) {
    return extractEntryFromZip( zip, entryName, destination, null );
  }


  /**
   * Extracts the named file from a zip file to the specified directory
   */
  public static final boolean extractEntryFromZip( File zip, String entryName, File destination, String stripPath ) {
    // open the zip file
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile( zip );
    } catch ( ZipException ze1 ) {
      return false;
    } catch ( IOException ioe1 ) {
      return false;
    }

    // Find the entry
    ZipEntry entry = findZipEntry( zipFile, entryName );
    if ( entry != null ) {
      return extractZipEntry( zipFile, entry, destination, stripPath, null );
    }

    return false;
    
  }


  /**
   * Extracts the entry from the zip file
   */
  private static final boolean extractZipEntry( ZipFile zipFile, 
                                                ZipEntry entry, 
                                                File destination,
                                                String stripPrefix,
                                                java.io.FileFilter filter) {
    byte[] fileData = new byte[64*1024];
    int readSize = 0;
    long fileProgress = 0L;
    
    String itemPath = entry.getName();
    
    if ( stripPrefix != null ) {

      if ( itemPath.startsWith( stripPrefix ) ) {
        itemPath = itemPath.substring( stripPrefix.length() );
      }
    }

    File entryFile = new File( destination, itemPath );
    BufferedOutputStream fileOutput = null;
    InputStream zipInput = null;

    if ( entry.isDirectory() ) {
      return entryFile.mkdirs();
    } else {
      // make the files destination path
      if ( !entryFile.getParentFile().exists() ) {
        if ( !entryFile.getParentFile().mkdirs() ) {
          return false;
        }
      }
      if (filter != null) {
        if (!filter.accept(entryFile)) {
          return true;
        }
      }

      // get the input stream
      try {
        zipInput = new BufferedInputStream(zipFile.getInputStream(entry));
      } catch ( IOException ioe2 ) {
        return false;
      }

      // Create the file
      if ( entryFile.exists() ) {
        entryFile.delete();
      }
      try {
        entryFile.createNewFile();
      } catch ( IOException ioe3 ) {
        try {
          zipInput.close();
        } catch ( IOException ioe4 ) {
        }
        return false;
      }

      // Create the file output stream
      try {
        fileOutput = new BufferedOutputStream(new FileOutputStream(entryFile));
      } catch ( FileNotFoundException fnfe1 ) {
        return false;
      }

      // get the entry data and write it out to the file
      while ( fileProgress < entry.getSize() ) {
        try {
          readSize = zipInput.read( fileData );
          fileOutput.write( fileData, 0, readSize );
        } catch ( IOException ioe5 ) {
          break;
        }

        if ( readSize > 0 ) {
          fileProgress += readSize;
        } else if ( readSize == -1 ) {
          // eof
          break;
        }

        // give the thread a break
        try {
          Thread.sleep( 10 );
        } catch ( InterruptedException ie ) {
        }
      } // while extracting file data

      // Close the zip input stream
      try {
        zipInput.close();
      } catch ( IOException ioe4 ) {
      }

      // Close the file output stream
      try {
        fileOutput.close();
        // set the file modification time
        entryFile.setLastModified( entry.getTime() );
      } catch ( IOException ioe3 ) {
      }
    }

    return true;
  }


  /**
   * Extracts the files in a zip file into the specified directory
   * 
   * @param zip    The zip file path
   * @param destinationDir
   *               The directory into which to extract the files
   * @param stripPrefix
   *               Prefix to strip from the zip entry file path. null if not used
   * @param filter Filter callback
   * 
   * @return false on error
   */
  public static final boolean extractZipFile(File zip, 
                                             File destinationDir,
                                             String stripPrefix,
                                             java.io.FileFilter filter) {
    ZipFile zipFile = null;

    try {
      zipFile = new ZipFile(zip);
    } catch (ZipException ze1) {
      return false;
    } catch (IOException ioe1) {
      return false;
    }

    // extract the entries
    ZipEntry entry = null;
    Enumeration<?> enumValue = zipFile.entries();

    while (enumValue.hasMoreElements()) {
      entry = (ZipEntry) enumValue.nextElement();
      if (entry != null) {
        extractZipEntry(zipFile, 
                        entry, 
                        destinationDir, 
                        stripPrefix, 
                        filter);
      } // have an entry
    } // while there are more elements

    // close the zip file
    try {
      zipFile.close();
    } catch (IOException ioe9) {
    }

    return true;
  }

  public static final boolean extractZipFile(File zip, 
                                             File destinationDir) {
    return extractZipFile(zip,destinationDir,null,null);
  }


  /**
   * Convenience method for locating an entry in a zip file.
   * 
   * @return
   */
  private static ZipEntry findZipEntry( ZipFile zipFile, String entryName ) {

    // Find the entry
    Enumeration<?> entries = zipFile.entries();
    ZipEntry entry = null;
    int nLen = entryName.length();

    while ( entries.hasMoreElements() ) {
      ZipEntry entry2 = (ZipEntry)entries.nextElement();
      String name = entry2.getName();
      if (name.length() < nLen) {
        continue;
      }
      
      String ending = name.substring( name.length() - nLen );
      if ( ending.equalsIgnoreCase( entryName ) ) {
        entry = entry2;
        break;
      }
    }
    
    return entry;
    
  }
  

  /**
   * Method to locate a file along this programs classpath
   * 
   * @param fileName Simple filename of file to locate
   * 
   * @return null or the path to the first instance of the file
   */
  public static String findFileInClasspath( String fileName ) {

    if (fileName == null || fileName.isEmpty()) {
      return null;
    }
    
    if (!fileName.startsWith("/")) {
      fileName = "/" + fileName;
    }
    
    Utilities dummy = new Utilities();
    URL classUrl = dummy.getClass().getResource( fileName );

    if (classUrl == null) {
      return null;
    }

    // converting the path to a URI eliminates %20 sequences in blank paths
    try {
      String answer = classUrl.toURI().getPath();
      return answer;
    } catch ( URISyntaxException ex ) {
    }
    
    return null;
  }

  /**
   * Calculates and returns a data chunks CRC32
   */
  public static long getCRC32(byte[] data, int startIndex, int count) {
    CRC32 crc = new CRC32();
    crc.reset();
    crc.update(data, startIndex, count);
    return crc.getValue();
  }

  /**
   * Calculates and returns a files data CRC32
   */
  public static long getCRC32(File file) {
    CRC32 crc = new CRC32();
    crc.reset();

    BufferedInputStream in  = null;
    byte[] data         = new byte[1024];
    int readLength      = 0;

    try {
      in =  new BufferedInputStream(new FileInputStream(file));

      readLength = in.read(data);
      while (readLength > -1) {
        crc.update(data, 0, readLength);
        readLength = in.read(data);
      }
    } catch (IOException ioe1) {
    } finally {
      try {
        in.close();
      } catch (Exception e) {
      }
    }

    return crc.getValue();
  }

  /**
   * Returns a files' extension, or the string after the last period '.' in the
   * file name.
   */
  public static String getExtension(File file) {
    if (file != null) {
      String filename = file.getName();
      int i = filename.lastIndexOf('.');

      if (i > 0 && i < filename.length() - 1) {
        return filename.substring(i + 1).toLowerCase();
      }
    }
    return null;
  }


  /**
   * Calculates the x and y point to display the given popup menu.
   *
   * @param javax.swing.JPopupMenu.
   * @param java.awt.event.MouseEvent.
   * @return java.awt.Point.
   */
  public static Point getMenuDisplayPoint( JPopupMenu popup, MouseEvent evt ) {

    int menuHeight = popup.getPreferredSize().height;
    int menuWidth  = popup.getPreferredSize().width;

    Point p = evt.getPoint();
    SwingUtilities.convertPointToScreen( p, ( JComponent ) evt.getSource() );

    int x   = p.x;
    int y   = p.y;

    Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
    int hBounds = scrn.height - 35; // account for possible task bar
    int wBounds = scrn.width;

    if ( y + menuHeight > hBounds ) {

      y -= menuHeight;

    }

    if ( x + menuWidth > wBounds ) {

      x -= menuWidth;

    }

    Point newPoint = new Point( x, y );
    SwingUtilities.convertPointFromScreen( newPoint, ( JComponent ) evt.getSource() );

    return newPoint;

  }


  /**
   * Convenience method to convert a hex character string to a
   * byte array
   * 
   * @param value  The hex string
   * 
   * @return The byte array
   */
  static public byte[] hexStringToBytes(String value) {
    byte[] result = new byte[value.length()/2];
    for (int i = 0; i < result.length; i++) {
      String group = value.substring(i+i,i+i+2);
      result[i] = (byte)(Integer.parseInt(group,16) & 0xff);
    }

    return result;
  }

  /**
   * Convenicene method to convert a byte array into
   * a printable string
   * 
   * @param value  The byte array
   * 
   * @return The printable string
   */
  static public String hexBytesToString(byte[] value) {
    StringBuffer sb = new StringBuffer(value.length*2);
    int ch;

    for (int i = 0; i < value.length; i++) {
      ch = (int)(value[i] & 0xF0);
      ch = (ch >>> 4);
      sb.append(HEXCHARS[ch]);
      ch = (int)(value[i] & 0x0F); 
      sb.append(HEXCHARS[ch]);
    }

    return sb.toString();
  }



  /**
   * Loads an ImageIcon using a URL
   */
  static public ImageIcon loadIcon(String path) {
    ImageIcon icon = null;
    URL imageURL = ClassLoader.getSystemResource(path);
    if (imageURL != null) {
      icon = new ImageIcon(imageURL);
    }
    return icon;
  }


  public static boolean nullSafeEquals( String s1, String s2 ) {
    if ( s1 == null ) return ( s2 == null );
    return s1.equals( s2 );
  }

  public static boolean nullSafeEquals( Long l1, Long l2 ) {
    if ( l1 == null ) return ( l2 == null );
    return l1.equals( l2 );
  }

  public static int nullSafeCompare( Long s1, Long s2 ) {
    if ( ( s1 == null ) && ( s2 == null ) ) {
      return 0;
    }
    else if ( s1 == null ) {
      return -1;
    }
    else if ( s2 == null ) {
      return 1;
    }
    else {
      return s1.compareTo( s2 );
    }
  }

  public static int nullSafeCompare( Integer s1, Integer s2 ) {
    if ( ( s1 == null ) && ( s2 == null ) ) {
      return 0;
    }
    else if ( s1 == null ) {
      return -1;
    }
    else if ( s2 == null ) {
      return 1;
    }
    else {
      return s1.compareTo( s2 );
    }
  }

  public static int nullSafeCompare( Float s1, Float s2 ) {
    if ( ( s1 == null ) && ( s2 == null ) ) {
      return 0;
    }
    else if ( s1 == null ) {
      return -1;
    }
    else if ( s2 == null ) {
      return 1;
    }
    else {
      return s1.compareTo( s2 );
    }
  }

  public static int nullSafeCompare( Double s1, Double s2 ) {
    if ( ( s1 == null ) && ( s2 == null ) ) {
      return 0;
    }
    else if ( s1 == null ) {
      return -1;
    }
    else if ( s2 == null ) {
      return 1;
    }
    else {
      return s1.compareTo( s2 );
    }
  }

  public static int nullSafeCompare( String s1, String s2 ) {
    if ( ( s1 == null ) && ( s2 == null ) ) {
      return 0;
    }
    else if ( s1 == null ) {
      return -1;
    }
    else if ( s2 == null ) {
      return 1;
    }
    else {
      return s1.compareTo( s2 );
    }
  }

  public static int nullSafeCompare( Date s1, Date s2 ) {
    if ( ( s1 == null ) && ( s2 == null ) ) {
      return 0;
    }
    else if ( s1 == null ) {
      return -1;
    }
    else if ( s2 == null ) {
      return 1;
    }
    else {
      return s1.compareTo( s2 );
    }
  }

  /**
   * Convenience method for getting a file selection from the user
   *
   * @param parent  the parent component for the file chooser
   * @param title   the title for the file chooser dialog
   * @param type    the type of file choice i.e. JFileChooser.OPEN_DIALOG, JFileChooser.SAVE_DIALOG
   * @param directory the start directory for the file choice
   * @param initial the initially selected file
   * @param fileFilter  the file chooser filter
   *
   * @returns the file selected or null
   */
  static public final File getFileSelection(Component parent, String title, int type, String buttonText,
                                            File directory, File initial, 
                                            javax.swing.filechooser.FileFilter fileFilter) {
    // create the file chooser
    JFileChooser chooser = new JFileChooser( directory );
    chooser.setDialogTitle( title );
    chooser.setDialogType( type );
    chooser.setFileFilter( fileFilter );
    chooser.setSelectedFile( initial );
    chooser.setMultiSelectionEnabled( false );
    chooser.addPropertyChangeListener( new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("SelectedFileChangedProperty")) {
        	File file = (File)e.getNewValue();
        	if ( file != null ) {
	          if ( file.isDirectory() ) {
	            ( (JFileChooser)e.getSource() ).setSelectedFile( file );
	          }
        	}
        }
      }
    } );

    // show the dialog
    int choice = chooser.showDialog( parent, buttonText );
    if ( choice == JFileChooser.APPROVE_OPTION ) {
      return chooser.getSelectedFile();
    } else {
      return null;
    }
  }


  /**
   * Recursively copies a directory
   *
   * @param source The source directory tree to copy
   * @param destination
   *               The destination directory tree to copy the tree to.  The subtree
   *               will be copied underneath the destination tree
   *
   * @return <CODE>false</CODE> on error
   */
  public static final boolean recursiveCopyDirectory(File source, File destination) {
    boolean error = false;

    if (source == null) {
        return false;
    }

    File[] listing = source.listFiles();

    if (listing == null) {
        System.err.println("recursiveCopyDirectory() called with invalid source dir: " + source);
        return false;
    }


    for (int i=0; i < listing.length; i++) {
      if (listing[i].isDirectory()) {
        //marcel todo: should use the return value to set 'error'
        //marcel need to check usage because some caller may depend on current
        //marcel behavior.
        if (!recursiveCopyDirectory(listing[i], new File(destination, listing[i].getName()))) {
          error = true;
        }
      } else {
        try {
          copyFile(listing[i], new File(destination, listing[i].getName()));
        } catch(IOException ioe) {
          error = true;
        }
      }
    }

    if (error) {
      return false;
    }
    return true;
  }

  /**
   * Deletes the directory and all its contents
   *
   * @param directory The directory subtree to delete
   *
   * @return <CODE>false</CODE> on error
   */
  public static final boolean recursiveDeleteDirectory( File directory, 
      java.io.FileFilter filter ) {
    boolean error = false;

    if (directory == null) {
      return false;
    }

    // Delete all the directory files
    File[] listing = directory.listFiles();
    if ( listing != null ) {
  
      for (int i=0; i < listing.length; i++) {
        if (listing[i].isDirectory()) {
          recursiveDeleteDirectory(listing[i],filter);
        } else {
          if ((filter == null) || (filter.accept(listing[i]))) {
            if (!listing[i].delete()) {
              System.err.println("Unable to delete file/dir: " + listing[i]);
              error = true;
            }
          }
        }
      }
    }

    if ( !directory.delete() ) {
      error = true;
    }

    return !error;
    
  }

  public static final boolean recursiveDeleteDirectory(File directory) {
    return recursiveDeleteDirectory(directory,null);
  }

  /**
   * Generate a list of all the files in the
   * specified tree
   * 
   * @param directory
   * @param filter
   * @param files
   */
  public static final void recursiveListDirectory( File directory, 
      java.io.FileFilter filter, Vector<File> files ) {

    if ( directory == null ) {
      return;
    }

    File[] listing = directory.listFiles();

    if ( listing == null ) {
      return;
    }

    for ( int i=0; i < listing.length; i++ ) {
      if ( listing[i].isDirectory() ) {
        recursiveListDirectory( listing[i], filter, files );
        
      } else {
        if ( ( filter == null ) || ( filter.accept( listing[i] ) ) ) {
          files.addElement( listing[i] );
        }
      }
    }

  }



  /**
   * Convenience method to set a JComponent's size to a constant value.
   *
   */
  public static void setJComponentSize( JComponent comp, Dimension size ) {
    comp.setMinimumSize( size );
    comp.setMaximumSize( size );
    comp.setPreferredSize( size );
  }


  /**
   * Updates a single file.
   *
   * Compares the source file date and size to the destination file and if
   * either are different it copies the file, otherwise it does nothing.
   */
  public static boolean updateFile(File source, File destination)
  throws IOException {
    if ((source.lastModified() != destination.lastModified()) ||
        (source.length() != destination.length())) {
      copyFile(source, destination);
      return true;
    }
    return false;
  }


  /**
   * Utility method for building GridBagConstraints.
   *
   * @param gbc GridBagConstraints
   * @param gx grid x position
   * @param gy grid y position
   * @param gw grid width
   * @param gh grid height
   * @param wx horizontal weight
   * @param wy vertical weight
   * @param anchor anchor point
   */
  public static void buildConstraints( GridBagConstraints gbc, int gx, int gy, int gw,
    int gh, double wx, double wy, int anchor ) {
    
    buildConstraints( gbc, gx, gy, gw, gh, wx, wy, anchor, GridBagConstraints.NONE );

  }


  /**
   * Utility method for building GridBagConstraints.
   *
   * @param gbc GridBagConstraints
   * @param gx grid x position
   * @param gy grid y position
   * @param gw grid width
   * @param gh grid height
   * @param wx horizontal weight
   * @param wy vertical weight
   * @param anchor anchor point
   * @param fill object fill
   */
  public static void buildConstraints( GridBagConstraints gbc, int gx, int gy, int gw,
    int gh, double wx, double wy, int anchor, int fill ) {

    gbc.gridx      = gx;
    gbc.gridy      = gy;
    gbc.gridwidth  = gw;
    gbc.gridheight = gh;
    gbc.weightx    = wx;
    gbc.weighty    = wy;
    gbc.anchor     = anchor;
    gbc.fill       = fill;

  }


  /**
   * Quick converter for valid dotted IP addresses
   * 
   * @param ipAddr Dotted decimal IP address
   * 
   * @return The IP address in a binary array
   */
  public static byte[] convertIPAddress(String ipAddr) {

    byte result[] = new byte[4];
    int ix = 0;

    StringTokenizer st = new StringTokenizer( ipAddr, "." );
    while (st.hasMoreTokens()) {
      String data = st.nextToken().trim();
      int entry = Integer.parseInt(data);
      if (entry < 0 || entry > 255 ) {
        entry = 0;
      }

      result[ix++] = (byte)(entry & 0xff);
    }

    return result;

  }


/******************************************************************************/

  /**
   * Method to determine whether or not the given String represents a valid
   * IP address.
   *
   * @param java.lang.String.
   * @return boolean.
   */
  public static boolean isValidIPAddress( String ipAddr ) {

    StringTokenizer st = new StringTokenizer( ipAddr, "." );
    if ( st.countTokens() != 4 ) {

      return false;

    }

    int entry;

    while ( st.hasMoreTokens() ) {

      try {

        String data = st.nextToken().trim();
        entry       = Integer.parseInt( data );

        if ( entry < 0 || entry > 255 ) {

          throw new Exception();

        }

      } catch ( Exception ex ) {

        return false;

      }
    }

    return true;

  }


}
