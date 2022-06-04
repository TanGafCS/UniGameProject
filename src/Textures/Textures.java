package src.Textures;

import org.jsfml.graphics.Font;
import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

/**
 * Use this class to get references to cached JSFML Textures. Created this to avoid read operations
 * from the disk every frame. To add a texture to the cache, add a unique key (string) and
 * texture path (string, assuming we are in the /Game210 directory.) separated by a colon inside the textures.txt file.
 * Example Line:
 * Player:/src/Player/player.png
 * We can then call Textures.getTexture("Player") to access the created texture at any point.
 * N.B. If adding a tile, or any repeating texture, make sure to include the word "tile" in the string key! (e.g. floorTile/floortile)
 */
public class Textures {

    private static Font font;
    private static TextureCache textureCache;

    public static Texture getTexture(String textureName)
    {
        // instantiate inner class if reference is null
        if (textureCache == null)
        {
            textureCache = new TextureCache();
        }

        // return requested texture
        return textureCache.cache.get(textureName);
    }

    public static Font getFont()
    {
        // instantiate inner class if reference is null
        if (font == null)
        {
            font = new Font();
            try
            {
                font.loadFromFile(Paths.get("src/Textures/dogicapixel.otf"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // return requested texture
        return font;
    }

    /**
     * Inner class holding the dictionary of textures.
     */
    private static class TextureCache
    {
        private Hashtable<String, Texture> cache;       // Key is a string identifier from textures.txt

        public TextureCache()
        {
            cache = new Hashtable<>();

            loadTextures();
        }

        /**
         * Create a texture and return it from a given path.
         * @param path string path to image to create texture from
         * @return JSFML texture for given path.
         */
        private Texture createTexture(String path)
        {
            Texture returnTex = new Texture();

            String str_path = System.getProperty("user.dir") + path;
            Path file_path = Paths.get(str_path);

            try {
                returnTex.loadFromFile(file_path);
                //t.setRepeated(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnTex;
        }

        /**
         * load textures from disk, and cache them in a dictionary.
         */
        private void loadTextures()
        {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(
                        System.getProperty("user.dir") + "/src/Textures/textures.txt"));
                String ln = reader.readLine();
                while (ln != null) {
                    // Process line
                    // skip line if it's a comment
                    if (ln.startsWith("//"))
                    {
                        ln = reader.readLine();
                        continue;
                    }

                    String[] groups = ln.split(":");
                    String key = groups[0];
                    String path = groups[1];
                    boolean setRepeating = false;
                    if (key.split("[tT][iI][lL][eE]") != null)
                    {
                        setRepeating = true;
                    }

                    Texture t = createTexture(path);

                    if (setRepeating)
                    {
                        t.setRepeated(true);
                    }

                    cache.put(key, t);

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
