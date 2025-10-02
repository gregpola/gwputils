/**
 * @author Greg W. Pola
 * @version 1.0
 */
package com.gwp.util;

import java.awt.*;

/**
 * Properly lays out a status bar
 */
public class StatusBarLayout
implements LayoutManager, java.io.Serializable {

  private int horizontalGap;

  private int topMargin;

  public StatusBarLayout() {
    this( 1, 1 );
  }

  public StatusBarLayout( int gap ) {
    this( gap, 1 );
  }

  public StatusBarLayout( int gap, int margin ) {
    horizontalGap = gap;
    topMargin = margin;
  }


  ////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  //  LayoutManager interface methods
  //
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Not used by this class.
   */
  public void addLayoutComponent(String name, Component comp) {
  }


  public void layoutContainer( Container parent ) {
    synchronized ( parent.getTreeLock() ) {

      Dimension space = parent.getSize();
      Insets insets = parent.getInsets();
      int count = parent.getComponentCount();

      // starting draw position
      int x = insets.left;
      int y = insets.top + topMargin;

      // Some starting values
      space.width -= ( insets.left + insets.right );
      space.height -= ( insets.top + insets.bottom + topMargin );
      int width = 0;
      int height = space.height;

      // set component's bounds
      for ( int i = 0; i < count ; i++ ) {
        Component c = parent.getComponent( i );

        // calculate the preferred remaining space
        int remaining = space.width - x;
        for ( int j = i + 1; j < count; j++ ) {
          Component next = parent.getComponent( j );
          remaining -= next.getMinimumSize().width;
        }

        // calculate the components width
        Dimension maxSize = c.getMaximumSize();
        width = Math.min( maxSize.width, remaining );
        width = ( width < 0 ) ? 0 : width;

        c.setBounds( x, y, width, height );
        x += ( width + horizontalGap );
      }
    }
  }


  /**
   * Returns the minimum dimensions needed to lay out the components
   * contained in the specified target container.
   *
   * @param target  the container that needs to be laid out
   * @return the dimensions >= 0 && <= Integer.MAX_VALUE
   * @exception AWTError  if the target isn't the container specified to the
   *                      BoxLayout constructor
   */
  public Dimension minimumLayoutSize( Container parent ) {
    synchronized ( parent.getTreeLock() ) {

      Dimension result = new Dimension();
      Insets insets = parent.getInsets();
      int count = parent.getComponentCount();
      int height = 0;
      int width = 0;

      for ( int i = 0; i < count; i++ ) {
        Component comp = parent.getComponent( i );
        Dimension d = comp.getMinimumSize();
        width += d.width;
        height = height < d.height ? d.height : height;
      }

      result.height = height + insets.top + insets.bottom;
      result.width = width + insets.left + insets.right;

      result.width += horizontalGap * ( count - 1 );

      return result;
    }
  }


  /**
   * Returns the preferred dimensions for this layout, given the components
   * in the specified target container.
   *
   * @param target  the container that needs to be laid out
   * @return the dimensions >= 0 && <= Integer.MAX_VALUE
   * @exception AWTError  if the target isn't the container specified to the
   *                      BoxLayout constructor
   */
  public Dimension preferredLayoutSize( Container parent ) {
    synchronized ( parent.getTreeLock() ) {

      Dimension result = new Dimension();
      Insets insets = parent.getInsets();
      int count = parent.getComponentCount();
      int height = 0;
      int width = 0;

      for ( int i = 0; i < count; i++ ) {
        Component comp = parent.getComponent(i);
        Dimension d = comp.getPreferredSize();
        width += d.width;
        height = height < d.height ? d.height : height;
      }

      result.height = height + insets.top + insets.bottom;
      result.width = width + insets.left + insets.right;
      result.width += horizontalGap * ( count - 1 );

      return result;
    }
  }


  /**
   * Not used by this class.
   *
   * @param comp the component
   */
  public void removeLayoutComponent( Component comp ) {
  }
}