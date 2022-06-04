package src.Entities;

import org.jsfml.system.Vector2f;

public class EntityHelpers {
    /**
     * Empire to difficulty: Difficulty is a multiplier.
     * @param empire
     * @return difficulty multiplier.
     */
    public static float empToDiff(Empire empire)
    {
        switch (empire)
        {
            case VIKING:
                return 1f;
            case EGYPTIAN:
                return 1.5f;
            case GREEK:
                return 2f;
            case ROMAN:
                return 2.5f;
        }
        return 1f;
    }

    /**
     * Spawn a chaser every minute.
     * @return the time interval in seconds between chasers spawning.
     */
    public static float getChaserTime()
    {
        return 60f;
    }

    public static float getEnemySpawnTime()
    {
        return 13.5f;
    }

    public static Vector2f getVariedDestination(Vector2f source, Vector2f dest)
    {
        float distance = VectorHelpers.getDistance(source, dest);
        float variation = distance / 12f;

        float newX = quickRandom(dest.x, variation);
        float newY = quickRandom(dest.y, variation);

        Vector2f newDestination = new Vector2f(newX, newY);
        return newDestination;
    }

    public static float quickRandom(float num, float variationMax)
    {
        boolean isPositive = false;
        float val = (float) Math.random();
        if (val > 0.5f)   //positive
        {
            isPositive = true;
        }
        else // negative
        {
            isPositive = false;
        }

        val = (float) Math.random();

        if (!isPositive)
            val = -val;

        return num + (variationMax * val);
    }

    public static float randomiseWithinRange(float min, float max)
    {
        float rand = (float) Math.random();
        float maxChange = max - min;
        return min + (maxChange * rand);
    }
}
