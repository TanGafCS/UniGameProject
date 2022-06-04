package src.DungeonGen;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.Drawable;
import org.jsfml.system.Vector2f;

import java.util.*;

//import Tile.Type;

/**
 * A floor, containing a grid of tiles.
 */
public class Floor
{
    private Player playerRef;                       // Reference to the player. (Also held in entities list)
    private Tile[][] grid;
    private ArrayList<Entity> entities;             // Composition of entities on this floor.
    private ArrayList<Drawable> visualsToDraw;      // List of visual to draw, which are cleared after every draw.
    private Empire empire;
    private int floorNum;                           // 1 -> N, reset per empire. Used for printing stage/floor in-game.

    private int height;
    private int width;
    private int size;           // Number of tiles on this floor

    private Vector2f startPos;

    public Floor(int height, int width, Empire empire, int floorNum)
    {
        this.height = height;
        this.width = width;
        this.size = height * width;
        this.startPos = new Vector2f(0,0);
        this.empire = empire;
        this.floorNum = floorNum;

        this.entities = new ArrayList<>();
        this.visualsToDraw = new ArrayList<>();
        grid = new Tile[height][width];

        initialiseTiles();
    }

    /**
     * Add entity ent to list of entities exposed by getEntities() - unless
     * passed in entity is the player, make a reference to it available through getPlayer().
     * @param ent
     */
    public void registerEntity(Entity ent)
    {
        if (ent instanceof Player)
        {
            this.playerRef = (Player) ent;
            return;
        }

        this.entities.add(ent);
    }

    /**
     * Unregister Entity ent from this floor.
     * e.g., used when interacting with a modifier (and therefore picking it up off the floor, into player)
     * @param ent
     */
    public void unregisterEntity(Entity ent)
    {
        if (!entities.remove((Object) ent))
        {
            //System.out.println("Attempt to remove non-registered entity from entities list.");
            if (ent.getFloor() != this)
            {
                System.out.println("Attempt failed, as the entity's on a different floor to this one!");
            }
        }
    }

    /**
     * @return list of entities on the floor except the player.
     */
    public ArrayList<Entity> getEntities()
    {
        return entities;
    }

    /**
     * Get all entities, including Player.
     * @return all entities on this floor.
     */
    public ArrayList<Entity> getAllEntities()
    {
        ArrayList<Entity> allEntities = new ArrayList<>();
        allEntities.addAll(entities);
        if (getPlayer() != null)
        {
            allEntities.add(getPlayer());
        }
        return allEntities;
    }

    /**
     * @return list of visuals on the floor.
     */
    public ArrayList<Drawable> getVisuals()
    {
        return visualsToDraw;
    }

    /**
     * @return list of all interactable entities on this floor.
     */
    public ArrayList<Interactable> getInteractables()
    {
        ArrayList<Interactable> interactables = new ArrayList<>();
        for (Entity ent : entities)
        {
            if (ent instanceof Interactable) interactables.add((Interactable)ent);
        }
        return interactables;
    }

    /**
     * @return reference to the player.
     */
    public Player getPlayer()
    {
        if (playerRef != null) return playerRef;
        System.out.println("getPlayer() returned NULL! Make sure you've registered the player entity with the floor.");
        return null;
    }

    /**
     * Initialise Tile entities composed with all tiles on the floor.
     * (Turn tile entities into normal entity coutnerparts)
     */
    public void initialiseTileEntities()
    {
        for (Tile tile: getTiles())
        {
            for (Entity ent: tile.getEntities())
            {
                ent.setPos(tile.getPos());
                registerEntity(ent);
            }
        }
    }

    private void initialiseTiles()
    {
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                grid[i][j] = new EmptyTile(j, i, empire);
            }
        }
    }

    public String toString()
    {
        String returnStr = "";

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                returnStr += grid[i][j].toString();
            }
            returnStr += "\n";
        }


        return returnStr;
    }

    public int getSize()
    {
        return size;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setTile(int x, int y, Tile tile)
    {
        //System.out.println("x: " + x + " y: " + y);
        grid[y][x] = tile;
    }

    public Tile getTile(int x, int y)
    {
        //System.out.println("x: " + x + ", y: " + y);
        // validate tile
        if (x < 0 || y < 0 || x > this.getWidth() - 1 || y > this.getHeight() - 1) return null;
        return grid[y][x];
    }

    public Tile getStartingTile()
    {
        Tile returnTile = getTile(getWidth()/2, getHeight()/2);
        return returnTile;
    }

    public ArrayList<Tile> getTiles()
    {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Tile[] tileRows : grid)
        {
            for (Tile tile : tileRows)
            {
                tiles.add(tile);
            }
        }
        return tiles;
    }

    /**
     * Get a tile from a world position (vector2f)
     */
    public Tile getTileFromPos(Vector2f worldPos)
    {
        double tileSize = 64;

        // Get the tile's row/cols indices
        int tileX = (int)Math.floor(worldPos.x / tileSize);
        int tileY = (int)Math.floor(worldPos.y / tileSize);

        // Force-validate tileY and tileX. print out error.
        boolean isValidated = false;
        if (tileY < 0) { tileY = 0; isValidated = true; }
        else if (tileY >= getHeight()) { tileY = getHeight() - 1; isValidated = true; }
        if (tileX < 0) { tileX = 0; isValidated = true; }
        else if (tileX >= getWidth()) { tileX = getWidth() - 1; isValidated = true; }

        if (isValidated)
        {
//            System.out.print("Validated result from getTileFromPos(" + worldPos.toString() +
//                    "). Result is [" + tileX + ", " + tileY + "]");
        }

        return grid[tileY][tileX];
    }

    public Vector2f getPosFromTile(Tile tile)
    {
        float tileSize = 64;

        if (tile != null)
        {
            tile.getPos();
        }

        return null;
    }

    /**
     * Given a world position, return a list containing a reference to the tile lying on that world pos and the
     * surrounding tiles within its search area.
     * N.B. the same tile may be returned more than once around world edges.
     * size is processed such that the returned search area has a width and length of 2 * size + 1
     * @return 9 neighbouring tiles in a list, with the worldPos lying on the centre tile
     */
    public ArrayList<Tile> getTilesAroundPos(Vector2f worldPos, int size)
    {
        ArrayList<Tile> returnList = new ArrayList<>();

        // move worldPos up and left by one tile
        worldPos = new Vector2f(worldPos.x - (64 * size), worldPos.y - (64 * size) );

        for (int y = 0; y < 2 * size + 1; y++)
        {
            for (int x = 0; x < 2 * size + 1; x++)
            {
                returnList.add(getTileFromPos(new Vector2f(worldPos.x + x * 64, worldPos.y + y * 64)));
            }
        }

        return returnList;
    }

    public int getDungeonTiles()
    {
        int dungeonTiles = 0;

        for (Tile[] tileRows : grid)
        {
            for (Tile tile : tileRows)
            {
                if (tile instanceof FloorTile)
                {
                    dungeonTiles++;
                }
            }
        }
        return dungeonTiles;
    }

    /**
     * Get position of the starting point for this floor.
     * @return Vector2f of starting pos.
     */
    public Vector2f getStartPos()
    {
        return startPos;
    }

    public void setStartPos(Vector2f vec)
    {
        this.startPos = vec;
    }


    // public Tile getRandomNeighbouringTile(Tile tile, Walker walker)
    // {
    //     Tile returnTile;

    //     float randomFloat = java.util.concurrent.ThreadLocalRandom.current().nextFloat();

    //     if (randomFloat < walker.getChangeDirChance())
    //     {
    //         // reroll direction
    //     }

    //     walker.getDir();
        
    //     int x = 0;
    //     int y = 0;

    //     while (x < 2 || y < 2 || x > getWidth() - 2 || y >= getHeight() - 2)
    //     {
    //         x = tile.getPos().getX();
    //         y = tile.getPos().getY();

    //         int randomNum = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 4);

    //         switch (randomNum) {
    //             case 0:
    //                 x++;
    //                 break;
    //             case 1:
    //                 x--;
    //                 break;
    //             case 2:
    //                 y++;
    //                 break;
    //             case 3:
    //                 y--;
    //                 System.out.println("yup!");
    //                 break;
    //         }

    //     }

    //     returnTile = getTile(x, y);

    //     //System.out.println("x: " + x + " , y: " + y);

    //     return returnTile;
    // }

    public Tile getRandomNeighbouringTile(Tile tile, Walker walker)
    {
        Tile returnTile;

        float randomFloat = java.util.concurrent.ThreadLocalRandom.current().nextFloat();

        if (randomFloat < walker.getChangeDirChance())
        {
            // reroll direction
            walker.setDir(getRandomDirection(walker.getDir()));
        }

        // Get returnTile position
        int returnX = tile.getXindex();
        int returnY = tile.getYindex();

        switch (walker.getDir()) {
            case N:
                returnY--;
                break;
            case E:
                returnX++;
                break;
            case S:
                returnY++;
                break;
            case W:
                returnX--;
                break;
        }

        if (returnX < 2 || returnY < 2 || returnX > getWidth() - 2 || returnY >= getHeight() - 2)
        {
            // If the tile wasn't within bounds, return same tile.
            return tile;
        }

        // If the target tile is within bounds, return it.
        return getTile(returnX, returnY);
    }

    public CardinalDirection getRandomDirection()
    {
        int randomNum = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 4);

        switch (randomNum)
        {
            case 0:
                return CardinalDirection.N;
            case 1:
                return CardinalDirection.E;
            case 2:
                return CardinalDirection.S;
            case 3:
                return CardinalDirection.W;
        }

        return CardinalDirection.UNDEFINED;
    }

    /**
     * Get a random direction, different to what was passed in.
     * @param curDirection
     * @return a cardinal direction that is different to the input.
     */
    public CardinalDirection getRandomDirection(CardinalDirection curDirection)
    {
        CardinalDirection returnDir = CardinalDirection.UNDEFINED;
        
        // while returnDir is undefined or the same as what was passed in, regenerate direction.
        while (returnDir == CardinalDirection.UNDEFINED || returnDir == curDirection)
        {
            int randomNum = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 4);
            switch (randomNum)
            {
                case 0:
                    returnDir = CardinalDirection.N;
                    break;
                case 1:
                    returnDir = CardinalDirection.E;
                    break;
                case 2:
                    returnDir = CardinalDirection.S;
                    break;
                case 3:
                    returnDir = CardinalDirection.W;
                    break;
            }
        }

        return returnDir;
    }

    public int getFloorNum()
    {
        return floorNum;
    }

    public Empire getEmpire()
    {
        return empire;
    }
}