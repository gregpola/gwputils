/**
 * @author Greg Pola
 * @version 1.0
 */
package com.gwp.util;

// JDK imports

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



/**
 * A dialog class that allows graphical selection of a date
 */
public class DateChooser
extends JDialog
implements Serializable {
  
  // static data members
  /////////////////////////////////////////////////////////////////////////////////////////////////

  private static final String[]     DAYS_OF_WEEK          = {"S", "M", "T", "W", "T", "F", "S"};

  private static final String[]     MONTHS                = {"January", "February", "March", "April", "May",
    "June", "July", "August", "September", "October", "November", "December"};

  private static final int[]        DAYS_IN_MONTH         = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  private static final int          DAY_INDEX_FEBRUARY    = 1;
  
  
  // dynamic data members
  /////////////////////////////////////////////////////////////////////////////////////////////////

  private Calendar                          savedDate;
  

  private Color                             backgroundColor;
  

  private DateFormat                        dateFormatter;
  

  private int                               currentDay;

  private int                               currentMonth;

  private int                               currentYear;

  private int                               earliestYear;

  private int                               latestYear;

  private int                               yearSpan;
  
  
  private JButton                           okButton;

  
  private JLabel[]                          daysOfTheMonth;

  
  private JPanel                            buttonPanel;

  private JPanel                            daysPanel;

  private JPanel                            monthAndYearPanel;

  
  private JSpinner                          monthSpinner;
  
  private JSpinner                          yearSpinner;
  
  
  private SpinnerNumberModel                yearSpinnerModel;
  
  
  
  
  /**
   * Constructor
   */
  public DateChooser( Frame frame, Date startingDate ) {
    this( frame, startingDate, 10 );
  }
  
  
  public DateChooser( Dialog dialog, Date startingDate ) {
    this( dialog, startingDate, 10 );
  }
  
  
  /**
   * Constructor
   */
  public DateChooser( Frame frame, Date startingDate, int span ) {
    super( frame, "Select a date", true );

    // set members
    yearSpan = span;
    savedDate = Calendar.getInstance();
    savedDate.setTime(startingDate);

    earliestYear = savedDate.get( Calendar.YEAR ) - ( yearSpan / 2 );
    latestYear = earliestYear + ( yearSpan / 2 );

    // add content
    addContent();
    setDate(startingDate);

    setResizable(false);
    pack();
    setLocationRelativeTo( frame );
    
  }

  
  public DateChooser( Dialog dialog, Date startingDate, int span ) {
    super(dialog, "Select a date", true);

    // set members
    yearSpan = span;
    savedDate = Calendar.getInstance();
    savedDate.setTime(startingDate);

    earliestYear = savedDate.get( Calendar.YEAR ) - ( yearSpan / 2 );
    latestYear = earliestYear + ( yearSpan / 2 );

    // add content
    addContent();
    setDate( startingDate );

    setResizable( false );
    pack();
    setLocationRelativeTo( dialog );
    
  }

  
  /**
   * Adds the content to the dialog
   */
  private void addContent() {
    backgroundColor = getBackground();
    if ( backgroundColor == null ) backgroundColor = Color.white;

    dateFormatter = DateFormat.getDateInstance( DateFormat.LONG );
    Calendar timeKeeper = Calendar.getInstance();
    dateFormatter.setCalendar( timeKeeper );

    JPanel mainPanel = new JPanel( new BorderLayout( 3, 3 ) );
    mainPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    mainPanel.add( getMonthAndYearPanel(), BorderLayout.NORTH );
    mainPanel.add( getDaysPanel(), BorderLayout.CENTER );
    mainPanel.add( getButtonPanel(), BorderLayout.SOUTH );
    getContentPane().add( mainPanel );
  }

  
  /**
   * Creates/returns the button panel
   */
  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel(new ButtonLayout(ButtonLayout.X_AXIS, 10));
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      okButton = new JButton( "OK" );
      okButton.setPreferredSize( new Dimension( 75, 25 ) );
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // save the date info
          savedDate.set(getYear(), getMonth(), getDay());
          dispose();
        }
      });

      JButton cancelButton = new JButton( "Cancel" );
      cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setDate(savedDate.getTime());
          dispose();
        }
      });
      cancelButton.setPreferredSize( new Dimension( 75, 25 ) );

      // compile the panel
      buttonPanel.add(ButtonLayout.getGlue());
      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);
      buttonPanel.add(ButtonLayout.getGlue());

      // set default button
      //getRootPane().setDefaultButton(okButton);
      //okButton.requestDefaultFocus();
    }
    return buttonPanel;
  }

  
  /**
   * Creates a date string from the current settings
   */
  public String getDateString() {
    StringBuffer result = new StringBuffer();
    result.append(MONTHS[savedDate.get(Calendar.MONTH)]).append(" ");
    result.append(savedDate.get(Calendar.DAY_OF_MONTH)).append(", ");
    result.append(savedDate.get(Calendar.YEAR));
    return result.toString();
  }

  
  /**
   * Returns the current day of the month
   */
  public int getDay() {
    return currentDay;
  }

  
  /**
   * Creates/returns the days of the month panel
   */
  private JPanel getDaysPanel() {
    if ( daysPanel == null ) {
      daysPanel = new JPanel(new GridLayout(7, 7, 2, 2));
      daysPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEtchedBorder(),
        BorderFactory.createEmptyBorder(3, 3, 3, 3)));

      // add the weekday labels
      int i = 0;
      for (i = 0; i < 7; i++) {
        JLabel label = new JLabel(DAYS_OF_WEEK[i]);
        label.setHorizontalAlignment( SwingConstants.CENTER );
        
        Color bg = ( getBackground() == null ) ? Color.gray : 
          getBackground().darker().darker();
        label.setBackground( bg );
        label.setOpaque( true );
        daysPanel.add( label );
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

              if ( e.getClickCount() >= 2 ) {
                okButton.doClick();
              }
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
   * Returns the current month of the year
   */
  public int getMonth() {
    return currentMonth;
  }

  
  /**
   * Creates/returns the month and year panel
   */
  private JPanel getMonthAndYearPanel() {
    if ( monthAndYearPanel == null ) {
      monthAndYearPanel = new JPanel( new GridLayout( 1, 2, 10, 5 ) );
      //monthAndYearPanel = new JPanel(new ButtonLayout());
      monthAndYearPanel.setBorder(BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

      // month of the year selection
      monthSpinner = new JSpinner( new SpinnerListModel( MONTHS ) );
      monthSpinner.addChangeListener( new ChangeListener() {
        @Override
        public void stateChanged( ChangeEvent e ) {
          String sValue = (String) monthSpinner.getValue();
          int month = getMonthValue( sValue );
          setMonth( month );
          resetMonth();
        }
      });

      // year field
      int year = savedDate.get( Calendar.YEAR );
      yearSpinnerModel = new SpinnerNumberModel( year, earliestYear, latestYear, 1 );
      yearSpinner = new JSpinner( yearSpinnerModel );
      JSpinner.NumberEditor jsne = new JSpinner.NumberEditor( yearSpinner, "0000" );
      yearSpinner.setEditor( jsne );
      yearSpinner.addChangeListener( new ChangeListener() {
        @Override
        public void stateChanged( ChangeEvent e ) {
          Integer iValue = (Integer) yearSpinner.getValue();
          int newYear = iValue.intValue();
          
          if ( newYear != getYear() ) {
            setYear( newYear );
            resetMonth();
          }
        }
      });

      // compile the panel
      monthAndYearPanel.add( monthSpinner );
      monthAndYearPanel.add( yearSpinner );
    }
    return monthAndYearPanel;
  }

  
  /**
   * Returns the int month value from the month name.
   * 
   * @param monthName
   * @return
   */
  private int getMonthValue( String monthName ) {
    int result = 0;
    for ( String m : MONTHS ) {
      if ( m.equals( monthName ) ) break;
      result++;
    }
    
    return result;
  }
  
  
  /**
   * Returns the selected date
   */
  public Date getSelectedDate() {
    return savedDate.getTime();
  }

  
  /**
   * Returns the current year
   */
  public int getYear() {
    return currentYear;
  }
  

  /**
   * Refreshes the days of the month display based on the current month/year info
   */
  void resetMonth() {
    int month = getMonth();
    int year = getYear();
    Calendar newCalendar = Calendar.getInstance();

    newCalendar.set(year, month, 1);
    //newCalendar.setTime(newCalendar.getTime());
    int weekday = newCalendar.get(Calendar.DAY_OF_WEEK);

    int maxDays = 30;
    if((month == DAY_INDEX_FEBRUARY) && (newCalendar instanceof GregorianCalendar) &&
       (((GregorianCalendar)newCalendar).isLeapYear(year))) {
      maxDays = 29;
    } else {
      maxDays = DAYS_IN_MONTH[month];
    }
    if (currentDay > maxDays)
      currentDay = maxDays;

    // set the days labels
    int j = 0;
    boolean currentDayPainted = false;
    for (int i = 0; i < 42; i++) {
      if (i >= (weekday - 1) && j < maxDays) {
        daysOfTheMonth[i].setText(String.valueOf(++j));
      } else {
        daysOfTheMonth[i].setText("");
      }
      daysOfTheMonth[i].setOpaque(false);
      daysOfTheMonth[i].setBackground(backgroundColor);

      // look for the selected date
      if (j == currentDay && !currentDayPainted) {
        currentDayPainted = true;
        daysOfTheMonth[i].setOpaque(true);
        daysOfTheMonth[i].setBackground(backgroundColor.darker());
      }
    }
  }

  /**
   * Sets the date to display on the calendar
   *
   * @param newDate the new date to display
   */
  public void setDate( Date newDate ) {
    Calendar timeKeeper = Calendar.getInstance();
    timeKeeper.setTime( newDate );
    setYear(timeKeeper.get( Calendar.YEAR ) );
    setMonth(timeKeeper.get( Calendar.MONTH ) );
    setDay(timeKeeper.get( Calendar.DAY_OF_MONTH ) );
    savedDate.setTime( newDate );

    // set the display
    monthSpinner.setValue( MONTHS[currentMonth] );
    yearSpinner.setValue( currentYear );
    resetMonth();
    

    int min = ( yearSpan / 2 );
    int max = yearSpan - min;
    yearSpinnerModel.setMinimum( currentYear - min );
    yearSpinnerModel.setMaximum( currentYear + max );
    yearSpinner.repaint();

  }

  /**
   * Sets the current day of the month
   */
  private void setDay(int newDay) {
    // sanity check
    if (newDay < savedDate.getMinimum(Calendar.DAY_OF_MONTH) || newDay > savedDate.getMaximum(Calendar.DAY_OF_MONTH)) {
      return;
    }
    currentDay = newDay;
  }

  /**
   * Sets the current month of the year
   */
  private void setMonth(int newMonth) {
    // sanity check
    if (newMonth < savedDate.getMinimum(Calendar.MONTH) || newMonth > savedDate.getMaximum(Calendar.MONTH)) {
      return;
    }
    currentMonth = newMonth;
  }

  
  /**
   * Sets the current year
   */
  private void setYear(int newYear) {
    // sanity check
    if (newYear < savedDate.getMinimum(Calendar.YEAR) || newYear > savedDate.getMaximum(Calendar.YEAR)) {
      return;
    }
    currentYear = newYear;
  }
  

  /**
   * Sets the year span that can be selected
   */
  public void setYearSpan( int span ) {
    yearSpan = span;
    int min = ( yearSpan / 2 );
    int max = yearSpan - min;
    yearSpinnerModel.setMinimum( currentYear - min );
    yearSpinnerModel.setMaximum( currentYear + max );
    yearSpinner.repaint();
  }

}