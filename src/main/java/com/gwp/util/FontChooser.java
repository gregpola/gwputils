/**
 * Project: Gwp_Utils
 * File:		FontChooser.java
 * Created on Jul 18, 2007
 * 
 * @author gpola
 * @copyright 2001-2007 Greg W. Pola
 */
package com.gwp.util;


// jdk imports
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


// Wavelink imports


// Project imports




/**
 * FontChooser
 * @author gpola
 * 
 * 
 */
public class FontChooser 
extends JDialog {

  // Static data members
  /////////////////////////////////////////////////////////////////////////////
  
  private static final int                  HEIGHT                        = 335;
  
  private static final int                  WIDTH                         = 430;
  
  
  private static final String[]             SIZE_LIST                     = {
    "6", "8", "9", "10", "11", "12", "13", "14", "16", "18", "20", "22", "24",
    "26", "28", "36", "48", "60", "72" };
    
  private static final String[]             STYLE_LIST                      = {
    "Regular", "Bold", "Italic", "Bold Italic" };
  
  

  // Dynamic data members
  /////////////////////////////////////////////////////////////////////////////
  
  private Font                              font;
  
  
  private JButton                           cancelButton;
  
  private JButton                           okButton;
  
  
  private JLabel                            sampleLabel;
  
  
  private JList                             fontNameList;
  
  private JList                             fontStyleList;
  
  private JList                             fontSizeList;
  
  
  private JTextField                        fontNameField;
  
  private JTextField                        fontStyleField;
  
  private JTextField                        fontSizeField;
  
  
  
  /**
   * Constructor
   */
  public FontChooser( Frame owner ) {
    super( owner, "Font", true );
    
    setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );

    addContent();
    setSize( WIDTH, HEIGHT );
    setResizable( false );
    setLocationRelativeTo( owner );
  }
  
  
  /**
   * Adds the components to the dialog
   *
   */
  private void addContent() {
    
    // Content pane setup
    /////////////////////////////////////////////////////////////////
    
    JPanel contentPanel = new JPanel( null );
    setContentPane( contentPanel );

    int margin = 10;
    int lineHeight = 25;

    int x = margin;
    int y = margin;
    
    
    // Font Name
    /////////////////////////////////////////////////////////////////
    
    JLabel label = new JLabel( "Font:" );
    label.setBounds( x, y, 150, 18 );
    contentPanel.add( label );
    
    y += 18;
    
    fontNameField = new JTextField();
    fontNameField.setEditable( false );
    fontNameField.setBounds( x, y, 150, lineHeight );
    contentPanel.add( fontNameField );
    
    y += lineHeight;
    
    fontNameList = new JList( GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames() );
    fontNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    fontNameList.addListSelectionListener( new ListSelectionListener() {
      public void valueChanged( ListSelectionEvent e ) {
        if ( !e.getValueIsAdjusting() ) {
          fontNameField.setText( (String) fontNameList.getSelectedValue() );
          updateSample();
        }
      }
    });
    JScrollPane scroller = new JScrollPane( fontNameList );
    scroller.setBounds( x, y, 150, lineHeight * 6 );
    contentPanel.add( scroller );
    
    y += ( lineHeight * 6 ) + margin;
    
    
    // Sample
    /////////////////////////////////////////////////////////////////
    
    JPanel samplePanel = new JPanel( new BorderLayout( 10, 10 ) );
    samplePanel.setBorder( BorderFactory.createCompoundBorder( 
        BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), 
            "Sample" ),
        BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );
    
    sampleLabel = new JLabel( "AaBbYyZz" );
    sampleLabel.setBorder( BorderFactory.createCompoundBorder( 
        BorderFactory.createLoweredBevelBorder(), 
        BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) ) );
    sampleLabel.setHorizontalAlignment( SwingConstants.CENTER );
    sampleLabel.setVerticalAlignment( SwingConstants.CENTER );
    samplePanel.add( sampleLabel, BorderLayout.NORTH );
    
    samplePanel.setBounds( x, y, WIDTH - 2 * margin, 80 );
    contentPanel.add( samplePanel );
    
    
    // Font Style
    /////////////////////////////////////////////////////////////////
    
    x += ( 150 + margin );
    y = margin;
    
    label = new JLabel( "Style:" );
    label.setBounds( x, y, 100, 18 );
    contentPanel.add( label );
    
    y += 18;
    
    fontStyleField = new JTextField();
    fontStyleField.setEditable( false );
    fontStyleField.setBounds( x, y, 100, lineHeight );
    contentPanel.add( fontStyleField );
    
    y += lineHeight;
    
    fontStyleList = new JList( STYLE_LIST );
    fontStyleList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    fontStyleList.addListSelectionListener( new ListSelectionListener() {
      public void valueChanged( ListSelectionEvent e ) {
        if ( !e.getValueIsAdjusting() ) {
          String choice = (String) fontStyleList.getSelectedValue();
          fontStyleField.setText( choice );
          updateSample();
        }
      }
    });
    scroller = new JScrollPane( fontStyleList );
    scroller.setBounds( x, y, 100, lineHeight * 6 );
    contentPanel.add( scroller );    

    
    // Font Size
    /////////////////////////////////////////////////////////////////
    
    x += ( 100 + margin );
    y = margin;
    
    label = new JLabel( "Size:" );
    label.setBounds( x, y, 50, 18 );
    contentPanel.add( label );
    
    y += 18;
    
    fontSizeField = new JTextField();
    fontSizeField.setBounds( x, y, 50, lineHeight );
    contentPanel.add( fontSizeField );
    
    y += lineHeight;
    
    fontSizeList = new JList( SIZE_LIST );
    fontSizeList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    fontSizeList.addListSelectionListener( new ListSelectionListener() {
      public void valueChanged( ListSelectionEvent e ) {
        if ( !e.getValueIsAdjusting() ) {
          String choice = (String) fontSizeList.getSelectedValue();
          fontSizeField.setText( choice );
          updateSample();
        }
      }
    });
    scroller = new JScrollPane( fontSizeList );
    scroller.setBounds( x, y, 50, lineHeight * 6 );
    contentPanel.add( scroller );    

    
    // Buttons
    /////////////////////////////////////////////////////////////////
    
    x += ( 50 + margin );
    y = margin + 18;

    okButton = new JButton( "OK" );
    okButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        String name = getFontName();
        int style = getFontStyle();
        int size = getFontSize();
        
        font = new Font( name, style, size );
        setVisible( false );
      }
    });
    okButton.setBounds( x, y, 75, lineHeight );
    contentPanel.add( okButton );    
    
    y += lineHeight;

    cancelButton = new JButton( "Cancel" );
    cancelButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        font = null;
        setVisible( false );
      }
    });
    cancelButton.setBounds( x, y, 75, lineHeight );
    contentPanel.add( cancelButton );    
    
    
    getRootPane().setDefaultButton( okButton );
  }
  
  
  /**
   * Displays the dialog and returns the selected font.
   * 
   */
  public Font getFont() {
    return font;
  }
  
  
  /**
   * Returns the currently selected font name.
   *
   * @return
   */
  public String getFontName() {
    return fontNameField.getText().trim();
  }
  
  
  /**
   * Returns the currently selected font size.
   *
   * @return
   */
  public int getFontSize() {
    int result = 10;
    
    try {
      result = Integer.parseInt( fontSizeField.getText().trim() );
    } catch ( Exception e ) {
    }
    
    return result;
  }
  
  
  /**
   * Returns the currently selected font style.
   *
   * @return
   */
  public int getFontStyle() {
    int result = Font.PLAIN;
    String choice = fontStyleField.getText().trim();
    
    if ( choice.equals( "Bold" ) ) result = Font.BOLD;
    else if ( choice.equals( "Italic" ) ) result = Font.ITALIC;
    else if ( choice.equals( "Bold Italic" ) ) result = Font.BOLD | Font.ITALIC;
    
    return result;
  }
  
  
  /**
   * Sets the selected font.
   */
  public void setFont( Font font ) {
    fontNameList.setSelectedValue( font.getFamily(), true );
    fontSizeList.setSelectedValue( String.valueOf( font.getSize() ), true );
    
    switch ( font.getStyle() ) {
      
      case Font.BOLD:
        fontStyleList.setSelectedValue( "Bold", true );
        break;
        
      case Font.ITALIC:
        fontStyleList.setSelectedValue( "Italic", true );
        break;
        
      case ( Font.BOLD | Font.ITALIC ):
        fontStyleList.setSelectedValue( "Bold Italic", true );
        break;
        
      default:
        fontStyleList.setSelectedValue( "Regular", true );
        
    }
    
  }
  
  
  /**
   * Updates the sample display
   *
   */
  private void updateSample() {
    String name = getFontName();
    int style = getFontStyle();
    int size = getFontSize();
    
    sampleLabel.setFont( new Font( name, style, size ) );
    sampleLabel.repaint();
  }
  
}


