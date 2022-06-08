/**
 * @author Greg W. Pola
 * @version 1.0
 */
package com.gwp.util;


import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import javax.swing.filechooser.FileFilter;


/**
 * Text field which enabled path selection by double clicking or hitting enter.
 */
public class PathTextField
extends JTextField
implements ActionListener, MouseListener {

  protected boolean     enabledState;

  protected File        selectedDirectory;

  protected File        selectedFile;

  protected FileFilter  fileFilter = (new JFileChooser()).getFileFilter();

  protected int         fileSelectionMode = JFileChooser.DIRECTORIES_ONLY;

  
  
  /**
   * Constructor
   * 
   * @param columns The width of the field
   */
  public PathTextField( int columns) {
    this( "", columns);
  }

  /**
   * Constructor
   * 
   * @param text   Preset path for the field
   */
  public PathTextField(String text) {
    super( text );

    setMargin(new Insets(1, 2, 1, 1));

    addActionListener(this);
    addMouseListener(this);
  }

  /**
   * Constructor
   * 
   * @param text    Preset path for the field
   * @param columns Column-width for the field
   */
  public PathTextField(String text, int columns) {
    super( text, columns );

    setMargin(new Insets(1, 2, 1, 1));

    addActionListener(this);
    addMouseListener(this);
  }

  public void actionPerformed(ActionEvent ae) {
    if (enabledState) {
      chooseThePath();
    }
  }

  public void chooseThePath() {
    chooseThePath( null );
  }

  public void chooseThePath(String path) {
    String searchPath = ( path == null ) ? getText() : path;

    JFileChooser chooser = new JFileChooser(searchPath);
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileFilter(fileFilter);
    chooser.setFileSelectionMode(fileSelectionMode);
    chooser.setDialogTitle("Select the path");

    // show the file chooser
    int result = chooser.showDialog( this, "Select" );

    // User made a selection
    if (result == JFileChooser.APPROVE_OPTION) {
      selectedDirectory = chooser.getCurrentDirectory();
      selectedFile = chooser.getSelectedFile();
      if (selectedFile != null) {
        setText(selectedFile.getPath());
      }
    }
  }

  public File getSelectedDirectory() {
    return selectedDirectory;
  }

  public File getSelectedFile() {
    return selectedFile;
  }

  public void setEnabled(boolean enabled) {
    enabledState = enabled;
    setOpaque(enabled);
    super.setEnabled(enabled);
  }

  public void mouseClicked(MouseEvent me) {
    if ((me.getClickCount() == 2) && enabledState) {
      chooseThePath();
    }
  }

  public void mouseEntered(MouseEvent me) {
  }

  public void mouseExited(MouseEvent me) {
  }

  public void mousePressed(MouseEvent me) {
  }

  public void mouseReleased(MouseEvent me) {
  }

  public FileFilter getFileFilter() {
    return fileFilter;
  }

  public void setFileFilter(FileFilter filter) {
    fileFilter = filter;
  }

  public int getFileSelectionMode() {
    return fileSelectionMode;
  }

  public void setFileSelectionMode(int mode) {
    fileSelectionMode = mode;
  }
}