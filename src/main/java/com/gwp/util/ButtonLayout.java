package com.gwp.util;

import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.AbstractButton;


public class ButtonLayout
implements LayoutManager, java.io.Serializable {

    /**
     * Specifies that the buttons should be laid out left to right.
     */
    public static final int X_AXIS = 0;

    /**
     * Specifies that the buttons should be laid out top to bottom.
     */
    public static final int Y_AXIS = 1;

    /** The alignment of this layout. */
    private int axis;

    /** The gap between buttons of this layout. */
    private int gap;

    public ButtonLayout() {
        this(X_AXIS);
    }

    public ButtonLayout(int axis) {
        this( axis, 5 );
    }

    public ButtonLayout(int axis, int gap) {
        this.axis = axis;
        this.gap = gap;
    }

    public static Glue getGlue() {
        return new Glue();
    }

    /**
     * Not used by this class.
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    private int getButtonCount(Container parent) {
        int result = 0;
        int count = parent.getComponentCount();

        for (int i=0; i < count; i++) {
            Component c = parent.getComponent(i);
            if (c instanceof Button || c instanceof AbstractButton) {
                result++;
            }
        }

        return result;
    }

    private int getGlueCount(Container parent) {
        int count = parent.getComponentCount();
        int result = 0;

        for (int i=0; i < count; i++) {
            Component c = parent.getComponent(i);
            if (c instanceof ButtonLayout.Glue) {
                result++;
            }
        }

        return result;
    }

    private Dimension getPreferredButtonSize(Container parent) {
        Dimension result = new Dimension(0, 0);
        int count = parent.getComponentCount();

        for (int i=0; i < count; i++) {
            Component c = parent.getComponent(i);
            if (c instanceof Button || c instanceof AbstractButton) {
                int currentHeight = c.getPreferredSize().height;
                int currentWidth = c.getPreferredSize().width;
                result.height = Math.max(result.height, currentHeight);
                result.width = Math.max(result.width, currentWidth);
            }
        }

        return result;
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {

            Dimension space = parent.getSize();
            //Dimension prefs =  preferredLayoutSize(parent);
            Insets insets = parent.getInsets();
            int count = parent.getComponentCount();
            int bcount = getButtonCount(parent);
            int gcount = getGlueCount(parent);
            Dimension readableButtonSize = getPreferredButtonSize(parent);

            // size of button components
            Dimension button = new Dimension();

            // size of glue components
            Dimension glue = new Dimension();

            // starting draw position
            int x = insets.left;
            int y = insets.top;

            space.width -= (insets.left + insets.right);
            space.height -= (insets.top + insets.bottom);
            //prefs.width -= (insets.left + insets.right);
            //prefs.height -= (insets.top + insets.bottom);

            if (axis == X_AXIS) {
                button.height = space.height;
              if (bcount > 0)
                button.width = Math.min(readableButtonSize.width,
                                        (space.width - ((count - 1) * gap)) / bcount);
              else
                button.width = 0;

                glue.height = Math.max(0, space.height);
                int buttonWidth = Math.max(0, (count - gcount) * button.width);
                glue.width = (gcount == 0) ? 0 : Math.max(0,
                    (space.width - buttonWidth - ((count - 1) * gap)) / gcount);

            } else {
              button.width = space.width;
              
              if ( bcount > 0 ) {
                button.height = Math.min( readableButtonSize.height, 
                    ( space.height - ( ( count - 1 ) * gap)) / bcount );
              } else {
                button.height = 0;
              }
              
              int buttonHeight = Math.max(0, (count - gcount) * button.height);
              glue.height = (gcount == 0) ? 0 :
                Math.max( 0, ( space.height - buttonHeight - ( ( count - 1 ) * gap ) ) / gcount );
              glue.width = Math.max( 0, space.width );
            }

            // set component bounds
            for (int i=0; i < count ; i++) {
                Component c = parent.getComponent(i);
                // check for glue
                if (c instanceof ButtonLayout.Glue) {
                    c.setBounds(x, y, glue.width, glue.height);
                    if (axis == X_AXIS) {
                        x += glue.width + gap;
                    } else {
                        y += glue.height + gap;
                    }
                } else {
                    c.setBounds(x, y, button.width, button.height);
                    if (axis == X_AXIS) {
                        x += button.width + gap;
                    } else {
                        y += button.height + gap;
                    }
                }
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
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {

            Dimension result = new Dimension();
            Insets insets = parent.getInsets();
            int count = parent.getComponentCount();
            int height = 0;
            int width = 0;

            for (int i=0; i < count; i++) {
                Component comp = parent.getComponent(i);
                Dimension d = comp.getMinimumSize();

                if (axis == X_AXIS) {
                    width += d.width;
                    height = height < d.height ? d.height : height;
                } else {
                    width = width < d.width ? d.width : width;
                    height += d.height;
                }
            }

            result.height = height + insets.top + insets.bottom;
            result.width = width + insets.left + insets.right;

            if (axis == X_AXIS) {
                result.width += gap * (count - 1);
            } else {
                result.height += gap * (count - 1);
            }

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
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {

            Dimension result = new Dimension();
            Insets insets = parent.getInsets();
            int count = parent.getComponentCount();
            int height = 0;
            int width = 0;

            for (int i=0; i < count; i++) {
                Component comp = parent.getComponent(i);
                Dimension d = comp.getPreferredSize();

                if (axis == X_AXIS) {
                    width += d.width;
                    height = height < d.height ? d.height : height;
                } else {
                    width = width < d.width ? d.width : width;
                    height += d.height;
                }
            }

            result.height = height + insets.top + insets.bottom;
            result.width = width + insets.left + insets.right;

            if (axis == X_AXIS) {
                result.width += gap * (count - 1);
            } else {
                result.height += gap * (count - 1);
            }

            return result;
        }
    }

    /**
     * Not used by this class.
     *
     * @param comp the component
     */
    public void removeLayoutComponent(Component comp) {
    }

    private static class Glue
    extends Component {

        Glue() {
        }
    }
}


