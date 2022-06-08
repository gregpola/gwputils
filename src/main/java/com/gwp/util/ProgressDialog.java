/**
 * @author
 * @version 1.0
 */
package com.gwp.util;

// JDK imports
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import javax.swing.border.BevelBorder;



/**
 * Dialog class that is used to display the progress status of long operations 
 * on the agent.
 */
public class ProgressDialog
extends JDialog {


  // Dynamic data members
  /////////////////////////////////////////////////////////////////////////////
  
  protected static final Dimension          MINIMUM_DIALOG_SIZE           = 
    new Dimension( 250, 150 );
  
  
  public static final int                   DEFAULT_INTERVAL              = 500; // ms


  // Dynamic data members
  /////////////////////////////////////////////////////////////////////////////

  private ActionListener                    autoIncrementTimerAction;

  
  protected boolean                         autoIncrement                 = false;
  
  private boolean showButton = true;

  
  /** The close button */
  private JPanel buttonPanel;

  /** Label to say what operation is currently in progress, e.g. "Copying File" */
  private JLabel        actionLabel;

  /** Label to say what the current object being worked on is, e.g. "c:\autoexec.bat" */
  private JLabel        objectLabel;

  /** The progress bar */
  JProgressBar          progressBar;

  /** A timeout for the dialog, which causes it to close after being shown for this long */
  private int            timeout;

  /** A expiration for the dialog, which causes it to close after being shown for this long */
  protected int                             expire;

  /** Tracks the last progres percent to see if the timeout Timer needs to be restarted */
  int                   lastProgress;

  /** Timer for checking timeout */
  Timer                 timer;
  
  
  protected Timer                           autoIncrementTimer;
  


  /**
   * Constructors
   */
  public ProgressDialog(Dialog owner) {
    this(owner, "Progress", false);
  }

  public ProgressDialog(Dialog owner, String title) {
    this(owner, title, false);
  }

  public ProgressDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
    addContent();
  }

  public ProgressDialog(Frame owner) {
    this(owner, "Progress", false);
  }

  public ProgressDialog(Frame owner, String title) {
    this(owner, title, false);
  }

  public ProgressDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    addContent();

    // Setup the auto increment timer
    autoIncrementTimerAction = new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        incrementProgress();
      }
    };
    autoIncrementTimer = new Timer( DEFAULT_INTERVAL, autoIncrementTimerAction );

  }

  /**
   * Adds the dialogs components
   */
  private void addContent() {
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout(5, 5));

    JPanel messagePanel = new JPanel(new GridLayout(3, 1, 2, 2));
    messagePanel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createEtchedBorder()),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    actionLabel = new JLabel("");
    messagePanel.add(actionLabel);

    objectLabel = new JLabel("");
    messagePanel.add(objectLabel);

    progressBar = new JProgressBar(0, 100);
    progressBar.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(1, 1, 1, 1), BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
    messagePanel.add(progressBar);

    cp.add(messagePanel, BorderLayout.NORTH);

    showButton(showButton);

    setTimeout(Integer.MAX_VALUE);
  }


  /**
   * Increments the progress bar to show that something is actaully happening
   */
  protected void incrementProgress() {
    
    SwingUtilities.invokeLater( new Runnable() {
      public void run() {
        int value = progressBar.getValue();
        value = ( value >= 99 ) ? 0 : value + 1;
        progressBar.setValue( value );
        progressBar.repaint();
      }
    });
    
  }

  
  /**
   * Returns whether or not this dialog is set to auto increment.
   * 
   * @return
   */
  public boolean isAutoIncrement() {
    return autoIncrement;
  }
  

  /**
   * Sets the action label
   */
  public void setActionLabel(String newAction) {
    if (newAction == null) {
      actionLabel.setText("");
    } else {
      actionLabel.setText(newAction);
    }
  }

  
  /**
   * Shows/Hides the close button
   * @param button whether the button should be displayed or not
   */
  public void showButton(boolean button) {
    showButton = button;

    if (button) {
      if (buttonPanel == null) {
        buttonPanel = new JPanel(new ButtonLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton okButton = new JButton("OK");
        okButton.addActionListener( new ActionListener() {
          public void actionPerformed( ActionEvent e ) {
            setVisible( false );
          }
        });
        buttonPanel.add(ButtonLayout.getGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(ButtonLayout.getGlue());
      }
      getContentPane().add( buttonPanel, BorderLayout.SOUTH );
      
    } else {
      getContentPane().add( new JPanel(), BorderLayout.SOUTH );
      
    }
    
  }
  
  
  /**
   * Sets the dialog to automatically increment the progress.
   * 
   * @param yes
   */
  public void setAutoIncrement( boolean auto ) {
    autoIncrement = auto;
  }
  
  

  /**
   * Sets the object label
   */
  public void setObjectLabel( String newObject ) {
    if ( newObject == null ) {
      objectLabel.setText( "" );
      
    } else {
      objectLabel.setText( newObject );
      
    }
    
  }
  

  /**
   * Sets the current progress
   */
  public void setProgress(int progress) {
    progressBar.setValue(progress);
    if (progress >= 100)
      setVisible( false );
  }

  
  /**
   * Sets the expiration in number of milliseconds. 
   * At the end of this interval the dialog is disposed.
   *
   * NOTE 
   *  This is like setTimeout except progress bar isn't updated.
   *  and the timer will be reset if any progress has been made
   *
   * @param expire  - The time to wait before disposing of the dialog box
   * @param error   - Error message to show before expiring dialog box
   */
  public void setExpire(int expire, final String error) {
    this.expire = expire;
    this.lastProgress = 0;

    // add the expiration checking timer
    timer = new Timer((expire), new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (lastProgress < progressBar.getValue()) {
          lastProgress = progressBar.getValue();
          timer.restart();
        }
        else
        {
          timer.stop();
          JOptionPane.showMessageDialog(ProgressDialog.this, error);
          setVisible( true );
        }
      }
    }); 
    timer.setRepeats(false);
    timer.start();
  }

  
  /**
   * Sets the timeout in number of milliseconds.
   * At the end of this interval the dialog is disposed.
   *
   * NOTE 
   *  This is like setExpire except progress bar is updated.
   *  and the dialog box is disposed reguardless of any progress
   *
   */
  public void setTimeout(int timeout) {
    this.timeout = timeout;

    // add the timeout checking timer
    timer = new Timer((timeout/101), new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setProgress(progressBar.getValue()+1);
      }
    }); 
    timer.start();
  }

  
  /**
   * Displays the dialog and brings it to the front
   */
  public void setVisible( boolean visible ) {
    
    if ( visible ) {
      
      // (re)Start the auto increment timer
      if ( isAutoIncrement() ) {
        
        if ( autoIncrementTimer.isRunning() ) {
          autoIncrementTimer.restart();
        } else {
          autoIncrementTimer.start();
        }
        
      }
      
      setTimeout( this.timeout );
      pack();
      
      // Check the minimum size
      int width = MINIMUM_DIALOG_SIZE.width;
      int height = MINIMUM_DIALOG_SIZE.height;
      
      if ( getWidth() > width ) {
        width = getWidth();
      }
      
      if ( getHeight() > height ) {
        height = getHeight();
      }
      setSize( width, height );
      
      setLocationRelativeTo( getParent() );
      
    } else {
      
      // Stop the auto increment timer
      if ( isAutoIncrement() ) {
        
        autoIncrementTimer.stop();
        
      }

    }
    
    super.setVisible( visible );
    
  }
  
  
  /**
   * Sets the dialog to automatically increment the progress.
   * 
   * @param yes
   */
  public void stopAutoIncrement() {
    autoIncrement = false;
    autoIncrementTimer.stop();
  }
  

}
