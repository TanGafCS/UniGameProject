package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.Color;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.*;
import org.jsfml.window.Window;
import org.jsfml.window.event.*;

import org.jsfml.graphics.*;
import org.jsfml.window.event.Event;

import javax.naming.Context;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The visual interface with the game. Draw current state of the game, every frame.
 */
public class GameView {
    private final int DEFAULT_REFRESH_RATE;
    private StatePainter sPainter;          // Paints the state of the game
    private Minimap minimap;                // Much like sPainter, paints state of game in form of minimap
    private RenderWindow window;            // The window we're painting onto.
    private RectangleShape rectGUI;         // The backing panel to GUI elements
    private CircleShape mapGUI;            // The panel for minimap elements
    private Player player;                  // reference to the player, whom we will centre the camera on.
    private Campaign campaign;
    private Floor floor;
    private GuiPainter guiElements;
    private int floorsPerStage;
    private boolean isGameOver;
    private float continueTime;

    private float endTime;
    private boolean isGameWon;

    private boolean up, down, left, right;  // true when the player is moving in the direction, false otherwise.
    private boolean m1, m2;                 // true when mouse 1/2 are held down, false otherwise.

    public GameView() {
        this.endTime = 999999999f;
        this.continueTime = 0;
        this.isGameOver = false;
        this.isGameWon = false;
        this.floorsPerStage = 2;
        campaign = new Campaign(floorsPerStage);
        floor = campaign.getNext();

        player = new Player(floor, this);
        DEFAULT_REFRESH_RATE = getRefreshRate();

        window = new RenderWindow();
        window.create(new VideoMode(1920, 1080), "Empire Escape", Window.CLOSE);
        window.setFramerateLimit(DEFAULT_REFRESH_RATE);
        //System.out.println(DEFAULT_REFRESH_RATE);

        player.setPosX(floor.getStartPos().x);
        player.setPosY(floor.getStartPos().y);

        sPainter = new StatePainter(floor);
        minimap = new Minimap(floor);

        // The Panel at the top of the screen, which we will use as a BG for GUI elements (minimap, modifiers, etc.)
        rectGUI = new RectangleShape(new Vector2f(1920f, 150f));
        rectGUI.setFillColor(Color.BLACK);

        //The Panel at the top right of the screen, used for minimap

        mapGUI = new CircleShape(70f);
        mapGUI.setOutlineColor(new Color(150,150,150));
        mapGUI.setOutlineThickness(5);
        mapGUI.setFillColor(new Color(180,180,180));

        this.run();
    }

    // Update game state
    private void stateUpdate(float deltaTime) {
        // validate deltaTime
        float maxFrameWindow = 12f * (1f / DEFAULT_REFRESH_RATE); // maximum acceptable passage of time per frame - higher times will be "discarded"
        if (deltaTime > maxFrameWindow)
        {
            deltaTime = Float.MIN_VALUE;
        }

        player.update(deltaTime, (left || right), (up || down));

        // Update each entity
        for (int i = 0; i < floor.getEntities().size(); i++)
        {
            floor.getEntities().get(i).update(deltaTime);
        }
    }

    public void run() {
        //Keep clock to check deltaTime (time since last frame)
        Clock deltaClock = new Clock();
        //Main loop for drawing
        while (window.isOpen()) {
            // get DeltaTime
            Time dt = deltaClock.restart();
            float deltaTime = dt.asSeconds();
            window.clear(new Color(100,100,100));

            if (isGameOver)
            {
                drawGameOverGraphic();
                if (GameTime.getElapsedTime() >= continueTime)
                {
                    setGameOver(false, 0f);
                }
            }
            else if (isGameWon)
            {
                drawGameWonGraphic();
                if (GameTime.getElapsedTime() >= endTime)
                {
                    setGameWon(false, 9999f);
                    // reset
                    resetGame();
                    player.resetState();

                }
            }
            else {
                // Call for game state updates. (physics updates, etc.)
                stateUpdate(deltaTime);

                // Centre gameView around the player
                centreGameView();

                // Draw floor and all entities on it.
                sPainter.paint(window);
                //Draw GUI elements last, so that they are on top.
                window.draw(rectGUI);
                window.draw(mapGUI);
                guiElements = new GuiPainter((window), getTopLeftVector(), player);
                // For the GUI, could use a class akin to StatePainter as such:
                // guiPainter.paint(window);
                minimap.paint(window, getTopLeftMinimapVector());
            }

            window.display();

            //Handle events (update player with given inputs)
            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    //The user pressed the close button
                    window.close();
                }

                // Player controls
                if (event.type == Event.Type.KEY_PRESSED) {
                    switch (event.asKeyEvent().key) {
                        case W:
                            up = true;
                            break;
                        case A:
                            left = true;
                            break;
                        case S:
                            down = true;
                            break;
                        case D:
                            right = true;
                            break;
                        case F:
                            player.tryInteract();
                            break;
                        case NUM1:
                            player.setSelectedModifier(1);
                            break;
                        case NUM2:
                            player.setSelectedModifier(2);
                            break;
                        case NUM3:
                            player.setSelectedModifier(3);
                            break;
                        case X:
                            player.placeBomb();
                        case C:
                            player.placeBarrel();
                    }
                }

                if (event.type == Event.Type.KEY_RELEASED) {
                    switch (event.asKeyEvent().key) {
                        case W:
                            up = false;
                            break;
                        case A:
                            left = false;
                            break;
                        case S:
                            down = false;
                            break;
                        case D:
                            right = false;
                            break;
                    }
                }

                if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
                    MouseButtonEvent mbe = event.asMouseButtonEvent();
                    Mouse.Button mb = mbe.button;
                    if (mb == Mouse.Button.LEFT)
                    {
                        m1 = true;
                    }
                    else if (mb == Mouse.Button.RIGHT)
                    {
                        m2 = true;
                    }
                    /* else if (mb == Mouse.Button.MIDDLE)
                    {
                        Vector2f position = window.mapPixelToCoords(mbe.position); //new Vector2f(mbe.position.x,mbe.position.y);
                        new Projectile(floor,position,player.getCentre(),20,30,false);
                    } */
                }

                if (event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
                    MouseButtonEvent mbe = event.asMouseButtonEvent();
                    Mouse.Button mb = mbe.button;
                    if (mb == Mouse.Button.LEFT)
                    {
                        m1 = false;
                    }
                    else if (mb == Mouse.Button.RIGHT)
                    {
                        m2 = false;
                    }
                    /* else if (mb == Mouse.Button.MIDDLE)
                    {
                        Vector2f position = window.mapPixelToCoords(mbe.position); //new Vector2f(mbe.position.x,mbe.position.y);
                        new Projectile(floor,position,player.getCentre(),20,30,false);
                    } */
                }
            }

            // Attacks
            Vector2i mousePos = Mouse.getPosition(window);
            if (m1)
            {
                Vector2f position = window.mapPixelToCoords(mousePos); //new Vector2f(mbe.position.x,mbe.position.y);
                player.attack(position);
            }
            else if (m2)
            {
                Vector2f position = window.mapPixelToCoords(mousePos);
                player.melee(position);
            }

            // Apply movement
            if (up)
                player.moveUp(deltaTime);
            if (down)
                player.moveDown(deltaTime);
            if (left)
                player.moveLeft(deltaTime);
            if (right)
                player.moveRight(deltaTime);

            player.clampVel();
        }
    }

    /**
     * Advance the player to the next floor - called when the player interacts with the ladder.
     */
    public void advanceFloor()
    {
        if (campaign.getFloorIndex() + 1 >= campaign.getSize())
        {
            endGame(true);
        }
        else
        {
            Floor nextFloor = campaign.getNext();
            // Switch out statePainter's floor reference so it draws the correct floor
            sPainter.setFloor(nextFloor);
            // Move the player reference to the correct floor
            nextFloor.registerEntity(player);
            player.setFloor(nextFloor);
            player.setPos(nextFloor.getStartPos());
            floor = nextFloor;
        }

    }


    /**
     * The game has ended.
     * @param isWin true if the player won the game, false otherwise.
     */
    private void endGame(boolean isWin)
    {
        // Display end of game screen
        System.out.println("congratulations!");
        setGameWon(true, GameTime.getElapsedTime() + 2f);
    }

    /**
     * Centre the JSFML view on the player character when called.
     */
    private void centreGameView()
    {
        //Get the window's default view
        ConstView defaultView = window.getView();

        //Create a new view by copying the window's default view
        View view = new View(defaultView.getCenter(), defaultView.getSize());

        // Centre screen on player's CENTRE
        view.setCenter(player.getPosX() + 32, player.getPosY() + 32);

        // Set UI's pos
        rectGUI.setPosition((view.getCenter().x - view.getSize().x/2),
                view.getCenter().y - view.getSize().y/2);

        mapGUI.setPosition(((view.getCenter().x-155) + view.getSize().x/2),
                ((view.getCenter().y + 5) - view.getSize().y/2));
        //Make the window use the view
        window.setView(view);
    }

    /**
     * Return a Vector2f of the top left of the screen in world-space.
     */
    private Vector2f getTopLeftVector()
    {
        ConstView view = window.getView();
        return new Vector2f( (view.getCenter().x - view.getSize().x/2),
                (view.getCenter().y - view.getSize().y/2) );
    }

    /* Return a Vector 2f of the top left of the minimap excluding outline in world-space. */
    private Vector2f[] getTopLeftMinimapVector()
    {
        ConstView view = window.getView();
        Vector2f[] values = new Vector2f[2];
        values[0] = new Vector2f((view.getCenter().x-155) + view.getSize().x/2,(view.getCenter().y + 5) - view.getSize().y/2);
        values[1] = new Vector2f(view.getSize().x,view.getSize().y);
        return values;
    }


    /**
     * Gets the refresh rate of the user's monitor (or main monitor if they have multiple)
     * @return refresh rate (in hertz)
     */
    private int getRefreshRate()
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screen = env   .getScreenDevices();
        return screen[0].getDisplayMode().getRefreshRate();
    }

    /**
     * Reset game state, starting from the tutorial level.
     */
    public void resetGame()
    {
        this.campaign = new Campaign(floorsPerStage);
        advanceFloor();
    }

    /**
     * Set the state of the game. Also pass in a continue time, to lift the game over status.
     * @param isGameOver
     * @param continueTime
     */
    public void setGameOver(boolean isGameOver, float continueTime)
    {
        this.continueTime = continueTime;
        this.isGameOver = isGameOver;
    }

    public void setGameWon(boolean isGameWon, float endTime)
    {
        this.endTime = endTime;
        this.isGameWon = isGameWon;
    }


    private void drawGameOverGraphic()
    {
        Texture t = Textures.getTexture("gameOverScreen");
        Sprite s = new Sprite(t);
        s.scale(2f,2f);
        System.out.println(s.getGlobalBounds().width);
        float emptyX = 1920 - s.getGlobalBounds().width;
        float emptyY = 1080 - s.getGlobalBounds().height;
        s.setPosition(new Vector2f(getTopLeftVector().x + (emptyX / 2), getTopLeftVector().y + (emptyY / 2)));
        window.draw(s);
    }

    private void drawGameWonGraphic()
    {
        Texture t = Textures.getTexture("winScreen");
        Sprite s = new Sprite(t);
        s.scale(2f,2f);
        System.out.println(s.getGlobalBounds().width);
        float emptyX = 1920 - s.getGlobalBounds().width;
        float emptyY = 1080 - s.getGlobalBounds().height;
        s.setPosition(new Vector2f(getTopLeftVector().x + (emptyX / 2), getTopLeftVector().y + (emptyY / 2)));
        window.draw(s);
    }

}