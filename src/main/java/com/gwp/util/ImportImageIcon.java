package com.gwp.util;

import javax.swing.*;
import java.net.URL;



/**
 * ImportImageIcon.java <p>
 *
 * A utility class for loading icons from a .jar file. <p>
 *
 * Copyright (c) 2001 Wavelink Corporation
 * @author Jim Wang
 * @version 1.0
 */

public class ImportImageIcon
{

  /**
   * Constructor.
   */
    private ImportImageIcon()
    {
    }

    /**
     * Returns a static instance of this class.
     *
     * @return com.wavelink.common.ImportImageIcon.
     */
    static public ImportImageIcon getImportImageIcon( )
    {
        if (m_importImageIcon == null)
        {
            m_importImageIcon = new ImportImageIcon();
        }
        return m_importImageIcon;
    }

    
    /**
     * Returns an <code>ImageIcon</code> from the given path to the image.
     *
     * @param java.lang.String  the path to the image.
     * @return javax.swing.ImageIcon.
     */
	public ImageIcon getImageResource( String path ) {
		ImageIcon image = null;
    if ( ( path != null ) && ( path.length() > 0 ) ) {
      URL url = this.getClass().getResource( path );
      
      if ( url == null ) {
        // Use bootstrap
        url = ClassLoader.getSystemResource( path );
      }

      if ( url != null ) {
        image = new ImageIcon( url );
      }
    }

		return( image );
	}

    /**
     * Static instance of this class.
     */
    private static ImportImageIcon m_importImageIcon = null;
}
