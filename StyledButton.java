/**
 * A custom JButton class that handles all modern styling and hover/press animations.
 * This replaces the default, ugly Swing button.
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {
    
    /**
     * Constructs a new StyledButton with the specified text.
     * Applies custom styling, including font, colors, and hover/press effects.
     *
     * @param text The text to display on the button.
     */
    public StyledButton(String text) {
        super(text); // Call the JButton superclass constructor with the text
        setFont(UITheme.FONT_PRIMARY); // Set the font from the UI theme
        setBackground(UITheme.BUTTON_IDLE); // Set the default background color (idle state)
        setForeground(UITheme.TEXT_PRIMARY); // Set the text color
        setOpaque(true); // Make the button opaque so its background color is visible
        setBorderPainted(false); // Do not paint the default button border
        setFocusPainted(false); // Do not paint the focus border
        
        // Add a MouseListener to handle hover and press effects
        addMouseListener(new MouseAdapter() {
            /**
             * Changes background to hover color when mouse enters, if enabled.
             * @param e The MouseEvent.
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(UITheme.BUTTON_HOVER);
                }
            }

            /**
             * Changes background back to idle color when mouse exits, if enabled.
             * @param e The MouseEvent.
             */
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(UITheme.BUTTON_IDLE);
                }
            }

            /**
             * Changes background to pressed color when mouse is pressed, if enabled.
             * @param e The MouseEvent.
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(UITheme.BUTTON_PRESSED);
                }
            }

            /**
             * Changes background to hover color when mouse is released, if enabled.
             * @param e The MouseEvent.
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(UITheme.BUTTON_HOVER);
                }
            }
        });
    }

    /**
     * Overrides the setEnabled method to apply custom styling for enabled/disabled states.
     * @param b True to enable the button, false to disable.
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b); // Call the superclass method to change enabled state
        if (b) {
            // If enabled, set to idle background and primary text color
            setBackground(UITheme.BUTTON_IDLE);
            setForeground(UITheme.TEXT_PRIMARY);
        } else {
            // If disabled, set to panel background and disabled text color
            setBackground(UITheme.PANEL_BACKGROUND);
            setForeground(UITheme.TEXT_DISABLED);
        }
    }
}
