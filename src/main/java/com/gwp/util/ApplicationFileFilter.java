package com.gwp.util;
/**
 * Title:        Avalanche Package Builder
 * Description:  A utility that provides a graphical means of building Avalanche packages.
 * Copyright:    Copyright (c) 2001
 * Company:      Wavelink Corporation
 * @author Greg W. Pola
 * @version 1.0
 */

import java.io.File;
import javax.swing.filechooser.FileFilter;


public class ApplicationFileFilter extends FileFilter {

  private static final String DESCRIPTION = "Applications (*.exe, *.bat, *.com)";
  private static String[]     EXTENSIONS  = {"exe", "com", "bat"};
  
  public ApplicationFileFilter() {
    java.util.Arrays.sort(EXTENSIONS);
  }
  
  public boolean accept(File file) {
    if (file != null) {
      if (file.isDirectory()) {
        return true;
      }
      
      String extension = getExtension(file);
      if ((extension != null) && 
          (java.util.Arrays.binarySearch(EXTENSIONS, extension) >= 0)) {
        return true;
      }
    }
    return false;
  }
  
  public String getDescription() {
    return DESCRIPTION;
  }
  
  public String getExtension(File file) {
    if (file != null) {
      String filename = file.getName();
      int i = filename.lastIndexOf('.');
      
      if (i > 0 && i < filename.length() - 1) {
        return filename.substring(i + 1).toLowerCase();
      }
    }
    return null;                    
  }
}