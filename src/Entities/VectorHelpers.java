package src.Entities;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.system.Vector2f;

/**
 * Class providing Helper functions for Vectors.
 */
public class VectorHelpers
{
    /**
     * Get distance between two Vector2f as float.
     * @param v1
     * @param v2
     */
    public static float getDistance(Vector2f v1, Vector2f v2)
    {
        return getMagnitude(Vector2f.sub(v2, v1));
    }

    public static float getMagnitude(Vector2f v)
    {
        return (float)Math.sqrt(v.x * v.x + v.y * v.y );
    }

    public static Vector2f getVectorDistance(Vector2f v1, Vector2f v2){ return Vector2f.sub(v2,v1); }
}
