/**
 * @author        Greg Pola
 * @version       1.0
 */
package com.gwp.util;

// JDK imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;




/**
 * A dialog class that allows graphical selection of a date
 */
public class DateTimeChooser
extends JDialog
implements Serializable {

  // Static Members
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final Dimension    FRAME_SIZE            = new Dimension( 225, 300 );
  
  private static final String[]     DAYS_OF_WEEK          = { "S", "M", "T", "W", "T", "F", "S" };

  private static final String[]     MONTHS                = {
    "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
    "November", "December" };

  private static final int[]        DAYS_IN_MONTH         = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };


  // Dynamic Members
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  private Color                     backgroundColor;

  GregorianCalendar                 savedDate = null;

  private int                       selectedDay;

  private int                       yearSpan;

  JComboBox                         monthCombo;

  private JComboBox                 yearCombo;

  private JLabel[]                  daysOfTheMonth;

  private JPanel                    buttonPanel;

  private JPanel                    daysPanel;

  private JPanel                    monthAndYearPanel;

  private JPanel                    timePanel;

  private ShortTimeOfDayField       timeOfDay;

  private boolean                   milTime;


  /**
   * Constructors
   */
  public DateTimeChooser( Dialog dialog, Date startingDate ) {
    this( dialog, startingDate, false );
    
  }

  
  public DateTimeChooser( Dialog dialog, Date startingDate, boolean militaryTime ) {
    super( dialog, "Select a date and time", true );

    // set members
    savedDate = new GregorianCalendar();
    savedDate.setTime( startingDate );
    yearSpan = 5;
    milTime = militaryTime;

    // add content
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addContent();
    setDate( startingDate );

    pack();
    setSize( FRAME_SIZE );
    setResizable( false );
    setLocationRelativeTo( dialog );
    
  }

  
  public DateTimeChooser( Frame frame, Date startingDate ) {
    this( frame, startingDate, false );
  }

  
  public DateTimeChooser( Frame frame, Date startingDate, boolean militaryTime ) {
    super( frame, "Select a date and time", true );

    // set members
    savedDate = new GregorianCalendar();
    savedDate.setTime( startingDate );
    yearSpan = 5;
    milTime = militaryTime;

    // add content
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addContent();
    setDate( startingDate );

    pack();
    setSize( FRAME_SIZE );
    setResizable( false );
    setLocationRelativeTo( frame );
    
  }


  /**
   * Adds the content to the dialog
   */
  private void addContent() {
    backgroundColor = getBackground();

    setVisible( false );
    JPanel selectionPanel = new JPanel( new BorderLayout( 5, 5 ) );
    selectionPanel.add( getMonthAndYearPanel(), BorderLayout.NORTH );
    selectionPanel.add( getDaysPanel(), BorderLayout.CENTER );
    selectionPanel.add( getTimePanel(), BorderLayout.SOUTH );

    JPanel mainPanel = new JPanel( new BorderLayout( 3, 3 ) );
    mainPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    mainPanel.add( selectionPanel, BorderLayout.CENTER );
    mainPanel.add( getButtonPanel(), BorderLayout.SOUTH );

    getContentPane().add( mainPanel );
    
  }

  
  /**
   * Creates/returns the button panel
   */
  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel( new ButtonLayout( ButtonLayout.X_AXIS, 10 ) );
      //buttonPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

      JButton okButton = new JButton( "OK" );
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // save the date info
          savedDate.set( getYear(), getMonth(), getDay(), getHour(), getMinute() );
          dispose();
        }
      });

      JButton cancelButton = new JButton( "Cancel" );
      cancelButton.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          savedDate = null;
          //setDate( savedDate.getTime() );
          dispose();
        }
      });

      // compile the panel
      buttonPanel.add( ButtonLayout.getGlue() );
      buttonPanel.add( okButton );
      buttonPanel.add( cancelButton );
      buttonPanel.add( ButtonLayout.getGlue() );

      // set default button
      getRootPane().setDefaultButton( okButton );

    }
    
    return buttonPanel;
    
  }


  /**
   * Returns the current day of the month
   */
  int getDay() {
    return selectedDay;
  }


  /**
   * Creates/returns the days of the month panel
   */
  private JPanel getDaysPanel() {
    if (daysPanel == null) {
      daysPanel = new JPanel( new GridLayout( 7, 7, 2, 2 ) );
      daysPanel.setBorder( BorderFactory.createCompoundBorder(
        BorderFactory.createEtchedBorder(),
        BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) ) );

      // add the weekday labels
      int i = 0;
      for (i = 0; i < 7; i++) {
        JLabel label = new JLabel(DAYS_OF_WEEK[i]);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBackground(getBackground().darker().darker());
        label.setOpaque(true);
        daysPanel.add(label);
      }

      daysOfTheMonth = new JLabel[42];
      for (i = 0; i < 42; i++) {
        daysOfTheMonth[i] = new JLabel("");
        daysOfTheMonth[i].setHorizontalAlignment(SwingConstants.RIGHT);
        daysOfTheMonth[i].addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            try {
              int value = Integer.parseInt(((JLabel)e.getSource()).getText());
              setDay(value);
              resetMonth();
            } catch (NumberFormatException nfe) {
            }
          }
        });
        daysPanel.add(daysOfTheMonth[i]);
      }
    }
    return daysPanel;
  }


  /**
   * Returns the current hour of the day displayed in the gui
   */
  int getHour() {
    return timeOfDay.getHour();
  }


  /**
   * Returns the current minute of the hour displayed in the gui
   */
  int getMinute() {
    return timeOfDay.getMinutes();
  }


  /**
   * Returns the current month of the year displayed in the gui
   */
  int getMonth() {
    return monthCombo.getSelectedIndex();
  }


  /**
   * Creates/returns the month and year panel
   */
  private JPanel getMonthAndYearPanel() {
    if (monthAndYearPanel == null) {
      monthAndYearPanel = new JPanel(new GridLayout(1, 2, 10, 5));
      //monthAndYearPanel = new JPanel(new ButtonLayout());
      monthAndYearPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      // month fo the year selection
      monthCombo = new JComboBox( MONTHS );
      monthCombo.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if ((e.getStateChange() == ItemEvent.SELECTED) && isVisible()) {
            setMonth(monthCombo.getSelectedIndex());
            resetMonth();
          }
        }
      });

      // year field
      yearCombo = new JComboBox();
      int startYear = savedDate.get( Calendar.YEAR );
      for ( int i = startYear ; i < startYear + yearSpan; i++ ) {
        yearCombo.addItem( String.valueOf( i ) );
      }
      yearCombo.addItemListener( new ItemListener() {
        public void itemStateChanged( ItemEvent e ) {
          String selectedItem = (String) e.getItem();
          if ( selectedItem != null ) {
            int newYear = Integer.parseInt( selectedItem );
            if ( newYear != getYear() ) {
              resetMonth();
            }
          }
        }
      });

      // compile the panel
      monthAndYearPanel.add( monthCombo );
      monthAndYearPanel.add( yearCombo );
    }
    return monthAndYearPanel;
  }


  /**
   * Returns the selected date
   */
  public Date getSelectedDate() {
    if ( savedDate == null ) {
      return null;
    }
    
    return savedDate.getTime();
  }


  /**
   * Creates and returns the time panel
   *
   * @return
   */
  private JPanel getTimePanel() {
    if ( timePanel == null ) {
      timePanel = new JPanel();
      timePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 10, 5 ) );
      
      GridBagLayout bag = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
      timePanel.setLayout( bag );
      
      gbc.insets = new Insets( 2, 2, 2, 5 );
      gbc.fill = GridBagConstraints.NONE;

      JLabel label = new JLabel( "Time:" );
      Utilities.buildConstraints( gbc, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST );
      bag.setConstraints( label, gbc );
      timePanel.add( label );

      timeOfDay = new ShortTimeOfDayField( milTime );
      Utilities.buildConstraints( gbc, 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST );
      bag.setConstraints( timeOfDay, gbc );
      timePanel.add( timeOfDay );

    }

    return timePanel;
  }


  /**
   * Returns the current year
   */
  int getYear() {
    String str = (String) yearCombo.getSelectedItem();
    int value = savedDate.get( Calendar.YEAR );
    try {
      value = Integer.parseInt( str );
    } catch ( NumberFormatException nfe ) {
    }

    return value;
  }

  /**
   * Refreshes the days of the month display based on the current month/year info
   */
  void resetMonth() {
    int month = getMonth();
    int year = getYear();
    Calendar newCalendar = Calendar.getInstance();

    newCalendar.set( year, month, 1 );
    //newCalendar.setTime(newCalendar.getTime());
    int weekday = newCalendar.get( Calendar.DAY_OF_WEEK );

    int maxDays = 30;
    if( ( month == Calendar.FEBRUARY ) && ( newCalendar instanceof GregorianCalendar ) &&
       ( ( (GregorianCalendar) newCalendar ).isLeapYear( year ) ) ) {
      maxDays = 29;
    } else {
      maxDays = DAYS_IN_MONTH[month];
    }
    if ( selectedDay > maxDays )
      selectedDay = maxDays;

    // set the days labels
    int j = 0;
    boolean currentDayPainted = false;
    for ( int i = 0; i < 42; i++ ) {
      if ( i >= ( weekday - 1 ) && j < maxDays ) {
        daysOfTheMonth[i].setText( String.valueOf( ++j ) );
      } else {
        daysOfTheMonth[i].setText( "" );
      }
      daysOfTheMonth[i].setOpaque( false );
      daysOfTheMonth[i].setBackground( backgroundColor );

      // look for the selected date
      if ( j == selectedDay && !currentDayPainted ) {
        currentDayPainted = true;
        daysOfTheMonth[i].setOpaque( true );
        daysOfTheMonth[i].setBackground( backgroundColor.darker() );
      }
    }
  }

  /**
   * Sets the date and time to display on the calendar
   *
   * @param newDate the new date to display
   */
  public void setDate( Date newDate ) {
    savedDate = new GregorianCalendar();
    savedDate.setTime( newDate );

    setYear( savedDate.get( Calendar.YEAR ) );
    setMonth( savedDate.get( Calendar.MONTH ) );
    setDay( savedDate.get( Calendar.DAY_OF_MONTH ) );
    setHour( savedDate.get( Calendar.HOUR_OF_DAY ) );
    setMinute( savedDate.get( Calendar.MINUTE ) );
  }

  
  /**
   * Sets the current day of the month
   */
  public void setDay( int newDay ) {
    // sanity check
    if ( newDay < savedDate.getMinimum( Calendar.DAY_OF_MONTH ) || newDay > savedDate.getMaximum( Calendar.DAY_OF_MONTH ) ) {
      return;
    }
    selectedDay = newDay;
    resetMonth();
  }


  /**
   * Sets the currently displayed hour of the day
   *
   * @param newValue
   */
  public void setHour( int newValue ) {
    if ( newValue < 0 || newValue > 23 ) return;
    timeOfDay.setHours( newValue );
  }


  /**
   * Sets the currently displayed minute of the hour
   *
   * @param newValue
   */
  public void setMinute( int newValue ) {
    if ( newValue < 0 || newValue > 59 ) return;
    timeOfDay.setMinutes( newValue );
  }


  /**
   * Sets the current month of the year
   */
  public void setMonth( int newMonth ) {
    // sanity check
    if ( newMonth < savedDate.getMinimum( Calendar.MONTH ) || newMonth > savedDate.getMaximum( Calendar.MONTH ) ) {
      return;
    }
    monthCombo.setSelectedIndex( newMonth );
    resetMonth();
  }


  /**
   * Sets the current year
   */
  public void setYear( int newYear ) {
    // sanity check
    if ( newYear < savedDate.getMinimum( Calendar.YEAR ) || newYear > savedDate.getMaximum( Calendar.YEAR ) ) {
      return;
    }
    yearCombo.setSelectedItem( String.valueOf( newYear ) );
    resetMonth();
  }


  /**
   * Sets the year span that can be selected
   */
  public void setYearSpan( int earliest, int latest ) {
    yearSpan = latest - earliest + 1;

    yearCombo.removeAllItems();
    for ( int i = earliest; i <= latest; i++ ) {
      yearCombo.addItem( String.valueOf( i ) );
    }
    setYear( savedDate.get( Calendar.YEAR ) );
  }

}