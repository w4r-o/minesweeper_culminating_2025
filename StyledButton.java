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
    
    public StyledButton(String text) {
        super(text);
        setFont(UITheme.FONT_PRIMARY);
        setBackground(UITheme.BUTTON_IDLE);
        setForeground(UITheme.TEXT_PRIMARY);
        setOpaque(true);
        setBorderPainted(false);
        setFocusPainted(false);
        
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (isEnabled()) setBackground(UITheme.BUTTON_HOVER); }
            @Override public void mouseExited(MouseEvent e) { if (isEnabled()) setBackground(UITheme.BUTTON_IDLE); }
            @Override public void mousePressed(MouseEvent e) { if (isEnabled()) setBackground(UITheme.BUTTON_PRESSED); }
            @Override public void mouseReleased(MouseEvent e) { if (isEnabled()) setBackground(UITheme.BUTTON_HOVER); }
        });
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (b) {
            setBackground(UITheme.BUTTON_IDLE);
            setForeground(UITheme.TEXT_PRIMARY);
        } else {
            setBackground(UITheme.PANEL_BACKGROUND);
            setForeground(UITheme.TEXT_DISABLED);
        }
    }
}
