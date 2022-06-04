package src.LaunchGUI;


import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.Box;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class MainMenu extends JFrame implements MouseListener {

    ImageIcon picture;
    JLabel pictureLbl;

    public MainMenu()
    {
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setContentPane(new JLabel(new ImageIcon("sources/placeholderMM.png")));

        picture = new ImageIcon("src/Textures/displayScreens/mainMenuScreen.png");
        pictureLbl = new JLabel(picture);

        Container x = Box.createHorizontalBox();
        Container y = Box.createVerticalBox();
        y.add(pictureLbl);
        y.add(x);
        this.add(y);

        addMouseListener(this);
        this.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int x = me.getX();
        int y = me.getY();
        //System.out.println("Clicked " + x + " " + y);
        int playTop = 189;
        int playBot = 257;
        int playLeft = 165;
        int playRight = 313;
        if(y > playTop && y < playBot && x > playLeft && x < playRight)
        {
            // Will likely change to launch into
            dispose();
            GameView gameView = new GameView();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // added as mart of MouseListener interface, not needed for this implementation
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // added as mart of MouseListener interface, not needed for this implementation
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // added as mart of MouseListener interface, not needed for this implementation
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // added as mart of MouseListener interface, not needed for this implementation
    }
}