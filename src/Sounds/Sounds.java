package src.Sounds;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.Texture;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

/**
 * Use this class to get references to cached JSFML Sounds. Created this to avoid read operations
 * from the disk every frame. To add a sound to the cache, add a unique key (string) and
 * sound path (string, assuming we are in the /Game210 directory.) separated by a colon inside the sounds.txt file.
 * Example Line:
 * footstep:/src/Sounds/stoneFootstep.wav
 * We can then call Sounds.getSound("footstep") to access the created sound at any point.
 * N.B. If adding a tile, or any repeating texture, make sure to include the word "tile" in the string key! (e.g. floorTile/floortile)
 */
public class Sounds {

    private static SoundCache soundCache;

    public static SoundBuffer getSound(String soundName)
    {
        // instantiate inner class if reference is null
        if (soundCache == null)
        {
            soundCache = new SoundCache();
        }

        // return requested texture
        return soundCache.cache.get(soundName);
    }

    /**
     * Inner class holding the dictionary of textures.
     */
    private static class SoundCache
    {
        private Hashtable<String, SoundBuffer> cache;       // Key is a string identifier from sounds.txt

        public SoundCache()
        {
            cache = new Hashtable<>();

            loadSounds();
        }

        /**
         * Create a sound and return it from a given path.
         * @param path string path to file to create sound from
         * @return JSFML sound for given path.
         */
        private SoundBuffer createSound(String path)
        {
            SoundBuffer returnSound = new SoundBuffer();

            String str_path = System.getProperty("user.dir") + path;
            Path file_path = Paths.get(str_path);

            try {
                returnSound.loadFromFile(file_path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnSound;
        }

        /**
         * load sounds from disk, and cache them in a dictionary.
         */
        private void loadSounds()
        {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(
                        System.getProperty("user.dir") + "/src/Sounds/sounds.txt"));
                String ln = reader.readLine();
                while (ln != null) {
                    // Process line
                    String[] groups = ln.split(":");
                    String key = groups[0];
                    String path = groups[1];

                    SoundBuffer s = createSound(path);

                    cache.put(key, s);

                    // read next line
                    ln = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
