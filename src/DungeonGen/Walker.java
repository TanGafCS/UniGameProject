package src.DungeonGen;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import java.util.*;

/**
 * When advance() is called on a walker, it will advance one step.
 */
public class Walker
{

    private Floor floor;            // Reference to the floor we're operating on.
    private Tile tile;              // Reference to the tile we're currently at.
    private CardinalDirection dir;  // Holds last direction moved in.
    private float changeDirChance;  // The chance (expressed 0 -> 1) that we change direction.

    public Walker(Floor floor, Tile tile, float changeDirChance)
    {
        this.floor = floor;
        this.tile = tile;
        this.changeDirChance = changeDirChance;

        //Assign a random direction as we instantiate
        this.dir = floor.getRandomDirection();
    }

    public void advance()
    {
        tile = floor.getRandomNeighbouringTile(tile, this);
        floor.setTile(tile.getXindex(), tile.getYindex(), new FloorTile(tile.getXindex(), tile.getYindex(), tile.getEmpire()));
    }

    public void setDir(CardinalDirection dir)
    {
        this.dir = dir;
    }

    public CardinalDirection getDir()
    {
        return dir;
    }

    public float getChangeDirChance()
    {
        return changeDirChance;
    }

    public int getX()
    {
        return tile.getXLocation() / 64;
    }

    public int getY()
    {
        return tile.getYLocation() / 64;
    }

    public Tile getTile()
    {
        return tile;
    }



}