package com.gwp.util;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


/**
 * SplashScreen.java
 *
 * A window object to serve as a splash screen displayed while a Wavelink
 * application is initializing. <p>
 *
 * Copyright (c) 2001 Wavelink Corporation
 * @author Bijan Marashi
 * @version 1.0
 */

public class SplashScreen extends Window {


  /**
   * Constructor.  This window must be made visible by the instantiating
   * class.
   *
   * @param java.awt.Frame  the parent application frame.
   * @param java.lang.String  the String representing the path to the icon.
   */
  public SplashScreen( Frame parent, String imageFileName ) {
    super( ( parent == null ) ? new Frame() : parent );

    // set the cursor for this window to always be the wait cursor
    setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

    // window closing stuff
    addWindowListener( new WindowAdapter() {
      public void windowClosed( WindowEvent evt ) {

        close();

      }
    });

    ImageIcon image = Utilities.loadIcon( imageFileName );

    JLabel imageContainer = new JLabel( image );

    setLayout( new BorderLayout() );
    add( imageContainer, BorderLayout.CENTER );

    setSize( imageContainer.getPreferredSize() );

    centerWindow();

  }

/******************************************************************************/

  /**
   * Closes this window.
   */
  public void close() { dispose(); }

/******************************************************************************/

  /**
   * This method will take the size of the display screen and make sure the
   * dialog is centered in that display when opened.
   */

  private void centerWindow() {

    Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();

    // determine how to center the frame by taking the
    // screen size and subtracting the size of frame
    int width  = getSize().width;
    int height = getSize().height;

    setLocation( scrnSize.width/2 - width/2, scrnSize.height/2 - height/2 );

  }

/******************************************************************************/

} // end WLSplashScreen class