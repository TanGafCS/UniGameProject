package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class Tile{

    private static final int TILE_WIDTH = 64;

    private int xLocation;
    private int yLocation;
    private IntRect rect;
    private Texture t;
    private Sprite s;
    private ArrayList<Entity> entities;             // Entities composed with this tile.
    private Empire empire;

    public Tile(int x, int y, Empire empire){//constructor
        this.xLocation = x * TILE_WIDTH;
        this.yLocation = y * TILE_WIDTH;
        // This causes the visual glitch along the bottom and right side of the map where the tiles are duplicated, but these values are necessary for the hitbox.
        Vector2i size = new Vector2i(TILE_WIDTH, TILE_WIDTH);
        Vector2i location = new Vector2i(xLocation, yLocation);
        this.rect = new IntRect(location,size);
        this.entities = new ArrayList<Entity>();
        this.empire = empire;
    }

    public void addEntity(Entity incomingEnt)
    {
        // If the tile already has an entity of this type on it, don't add a second one.
        // (e.g. avoid the situation where a loot chest and shop chest spawn on top of each other)
        for (Entity ent: entities)
        {
            if (ent.getType() == incomingEnt.getType())
            {
                System.out.println("Attempted to add multiple similar entities to the same tile.");
                return;
            }
        }
        entities.add(incomingEnt);
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }

    public void set_sprite(String text_path){//needs logic from game view to pass in current level
        t = Textures.getTexture(text_path);

        s = new Sprite(t,rect);//declare new sprite
        s.setPosition(xLocation,yLocation);
        s.scale(TILE_WIDTH/32,TILE_WIDTH/32);// Scale 32x tile sprite to 64x

        //testing to show if sprites load in correct position

        /*
        if (yLocation % 2 == 0)
        {
            if (xLocation % 2 == 1)
            {
                s.setColor(Color.BLUE);
            }
        }
        else
        {
            if (xLocation % 2 == 0)
            {
                s.setColor(Color.BLUE);
            }
        }
        */
    }

    public boolean isCollideable()
    {
        return false;
    }

    public Drawable getDrawable(){
        return s;
    }

    public IntRect get_rect(){
        return rect;
    }

    public int getXindex(){
        return xLocation/64;
    }

    public int getYindex(){
        return yLocation/64;
    }

    public int getXLocation(){
        return xLocation;
    }

    public int getYLocation(){
        return yLocation;
    }

    public Empire getEmpire()
    {
        return empire;
    }

    public Vector2f getPos() { return new Vector2f(xLocation, yLocation);}

    /**
     * This is temporary.
     */
    public void setLadderSprite()
    {
        this.set_sprite("ladderTile1");
    }
}
