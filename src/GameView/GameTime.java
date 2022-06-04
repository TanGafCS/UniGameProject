package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;

/**
 * Provides the elapsed time of the game. Reset on every new floor.
 */
public class GameTime {

    private static Clock elapsedClock;

    public static float getElapsedTime()
    {
        if (elapsedClock == null)
        {
            elapsedClock = new Clock();
        }

        Time et = elapsedClock.getElapsedTime();
        return et.asSeconds();
    }

    public static void restartTime()
    {
        if (elapsedClock != null)
        {
            elapsedClock.restart();
        }
    }
}
