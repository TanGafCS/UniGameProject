package src.DungeonGen;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.*;
import java.util.concurrent.*;

public class DungeonGenerator
{
    private int ladderX;
    private int ladderY;
    private Floor floor;
    private ModifierFactory modFactory;
    private EntityFactory entFactory;

    public void DrunkardWalk(Floor floor)
    {
        DrunkardWalk(floor, 0.25f);
    }


    public void DrunkardWalk(Floor floor, float fillPercent)
    {
        this.floor = floor;
        this.modFactory = new ModifierFactory();
        this.entFactory = new EntityFactory();
        generateDungeonFloor(floor, fillPercent);
        generateDungeonWalls(floor);
        handleSingleWalls(floor);
        // next, "calcify" the level (replace walls with floors) and then regenerate walls.
        // This should eliminate lots of those pesky random pillars in rooms, and make all passages spacious.
        transformWallsToFloors(floor);
        generateDungeonWalls(floor);
        generateEntities(floor);
        handleSingleWalls(floor);
        // Next, lay out a wall around the perimeter so the player can't escape if the level generation bleeds into the edges.
        wrapWithWalls(floor);

        floor.getTile(ladderX, ladderY).addEntity(new InteractableLadder(floor,true));
    }

    private void generateEntities(Floor floor)
    {
        float mobCreateChance = 0.020f;
        float chestCreateChance = 0.001f;
        float shopCreateChance = 0.005f;
        float lootCreateChance = 0.003f;
        for (Tile tile: floor.getTiles())
        {
            // Don't generate on the right edge of the map. (Quick work around for chests which would otherwise clip with walls)
            if (tile.getXindex() >= floor.getWidth() - 2) continue;

            // Spawn enemies
            if (tile instanceof FloorTile && mobCreateChance > ThreadLocalRandom.current().nextFloat())
            {
               // If neighbouring tiles are floor, spawn an enemy
               if(areNeighbourTilesFloor(tile))
               {
                   tile.addEntity(entFactory.createRandomEnemy(floor));
                   //tile.addEntity(new MeleeEnemy(floor, floor.getEmpire()));
               }
            }
            // Spawn loot chests
            if (tile instanceof FloorTile && chestCreateChance > ThreadLocalRandom.current().nextFloat())
            {
                if(areNeighbourTilesFloor(tile))
                {
                    tile.addEntity(new LootChest(floor, modFactory.createRandomModifier(floor, false)));
                }
            }
            // Spawn shops
            if (tile instanceof FloorTile && shopCreateChance > ThreadLocalRandom.current().nextFloat())
            {
                if(areNeighbourTilesFloor(tile))
                {
                    // Add shopchest, scale cost with # of floors ascended and how many stages have been completed.
                    tile.addEntity(new ShopChest(floor, modFactory.createRandomModifier(floor, true), (int)((10 + floor.getFloorNum()) * EntityHelpers.empToDiff(floor.getEmpire()))));
                }
            }
            // Spawn loot
            if (tile instanceof FloorTile && lootCreateChance > ThreadLocalRandom.current().nextFloat())
            {
                if(areNeighbourTilesFloor(tile))
                {
                    // Add loot
                    tile.addEntity(entFactory.createRandomLoot(floor));
                }
            }
        }

        // Clean up enemies spawned too close to starting pos.
        removeEnemiesAround(floor.getStartingTile());
    }

    /**
     * Remove enemies in the attack radius of the spawn position.
     * @param tile the target tile to remove enemies around.
     */
    private void removeEnemiesAround(Tile tile)
    {
        int x = tile.getXindex();
        int y = tile.getYindex();

        // Clear ~20 tile side length square around spawnpoint of enemies.
        for (int i = -9; i < 10; i ++)
        {
            for (int j = -9; j < 10; j ++)
            {
                int effectiveXIndex = x + j;
                int effectiveYIndex = y + i;
                Tile tempTile = floor.getTile(effectiveXIndex, effectiveYIndex);
                if (tempTile != null)
                {
                    ArrayList<Entity> entities = tempTile.getEntities();
                    for (int entCount = 0; entCount < entities.size(); entCount++)
                    {
                        if (entities.get(entCount).getType() == Entity.Type.mob)
                        {
                            entities.remove(entCount);
                        }
                    }
                }
            }
        }
    }

    private void generateDungeonFloor(Floor floor, float fillPercent)
    {
        // If we decide these are things we'll want to tweak at runtime, we can move these
        // into the parameters of this function.

        int walkerCount = 1;
        int maxWalker = 5;
        float walkerRemoveChance = 0.05f;
        float walkerCreateChance = 0.05f;
        float walkerChangeDirChance = 0.3f;

        // Instantiate Walker list
        ArrayList<Walker> walkerList = new ArrayList<>();
        ArrayList<Walker> removeList = new ArrayList<>();

        // Instantiate starting walker
        Walker walkerInitial = new Walker(floor, floor.getStartingTile(), walkerChangeDirChance);
        walkerList.add(walkerInitial);
        // Set starting point by adding a descending ladder entity
        //walkerInitial.getTile().addEntity(new Ladder(false));
        floor.getTile(walkerInitial.getX(), walkerInitial.getY()).setLadderSprite();
        Vector2i initial = new Vector2i(walkerInitial.getTile().getXindex(), walkerInitial.getTile().getYindex());
        floor.setStartPos(new Vector2f(walkerInitial.getTile().getXLocation() + 0, walkerInitial.getTile().getYLocation() - 64));
        floor.setTile(walkerInitial.getX(), walkerInitial.getY(), new FloorTile(walkerInitial.getX(), walkerInitial.getY(), floor.getEmpire()));
        generateDungeonWalls(floor);
        transformWallsToFloors(floor);
        generateDungeonWalls(floor);
        transformWallsToFloors(floor);


        while (floor.getDungeonTiles() < (floor.getSize() * fillPercent))
        {
            //System.out.println("dungeon tiles: " + floor.getDungeonTiles() + ", floor.getSize(): " + floor.getSize() + "fillPercent: " + fillPercent);
            // Roll to see if we're adding a new walker
            if (walkerCreateChance > ThreadLocalRandom.current().nextFloat() && walkerCount < maxWalker)
            {
                Walker newWalker = new Walker(floor, floor.getTile(walkerInitial.getX(), walkerInitial.getY()), walkerChangeDirChance);
                walkerList.add(newWalker);
                walkerCount++;
            }

            for (Walker walker : walkerList)
            {
                // Expand map
                walker.advance();

                // Roll to see if we are removing this walker, (and whether we can,) mark for removal if so.
                if (walkerRemoveChance > ThreadLocalRandom.current().nextFloat() && walkerCount > 1)
                {
                    removeList.add(walker);
                    walkerCount--;
                }
            }

            // Remove walkers on the removeList
            walkerList.removeAll(removeList);
        }

        //floor.getTile(initial.x, initial.y).setLadderSprite();
        // Add an ascending ladder (floor end point)
        // Should change this to scan through the level and add the ladder in at a far away point.
        ladderX = walkerList.get(0).getTile().getXindex();
        ladderY = walkerList.get(0).getTile().getYindex();
        //walkerList.get(0).getTile().addEntity(new InteractableLadder(floor));
    }

    private void generateDungeonWalls(Floor floor)
    {
        int height = floor.getHeight();
        int width = floor.getWidth();

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                // For each tile, if a neighbouring tile (in 4 cardinal dirs) has a floor,
                // set this as a wall.
                Tile baseTile = floor.getTile(j, i);
                if (baseTile instanceof EmptyTile)
                {
                    int x = j;
                    int y = i;

                    int[][] neighbouringCoordinates = new int[2][4];

                    // get neighbouring tiles
                    // Could use a loop, but manual initialisation will be a lot clearer!
                    neighbouringCoordinates[0][0] = x + 1;
                    neighbouringCoordinates[1][0] = y;

                    neighbouringCoordinates[0][1] = x - 1;
                    neighbouringCoordinates[1][1] = y;

                    neighbouringCoordinates[0][2] = x;
                    neighbouringCoordinates[1][2] = y + 1;

                    neighbouringCoordinates[0][3] = x;
                    neighbouringCoordinates[1][3] = y - 1;



                    // Iterate through each neighbour.
                    for (int neighbourNum = 0; neighbourNum < 4; neighbourNum++)
                    {
                        // Check if each tile at the given coordinate is valid.
                        boolean success = validateCoordinates(neighbouringCoordinates[0][neighbourNum], neighbouringCoordinates[1][neighbourNum], floor);
                        if (success)    // if the tile is not out of bounds
                        {
                            Tile neighbouringTile = floor.getTile(neighbouringCoordinates[0][neighbourNum], neighbouringCoordinates[1][neighbourNum]);

                            if (neighbouringTile instanceof FloorTile)
                            {
                                floor.setTile(baseTile.getXindex(), baseTile.getYindex(), new WallTile(baseTile.getXindex(), baseTile.getYindex(), baseTile.getEmpire()));
                                break;
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * Replace walls surrounded by floors with floor.
     * Note: Lots of code duplication, alright for now, and probably more effort to fix than it's worth as we won't use this method elsewhere.
     */
    private void handleSingleWalls(Floor floor)
    {
        int height = floor.getHeight();
        int width = floor.getWidth();

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                // For each tile, if a neighbouring tile (in 4 cardinal dirs) has a floor,
                // set this as a wall.
                Tile baseTile = floor.getTile(j, i);
                if (baseTile instanceof WallTile)
                {
                    int x = j;
                    int y = i;

                    int[][] neighbouringCoordinates = new int[2][4];

                    // get neighbouring tiles
                    // Could use a loop, but manual initialisation will be a lot clearer!
                    neighbouringCoordinates[0][0] = x + 1;
                    neighbouringCoordinates[1][0] = y;

                    neighbouringCoordinates[0][1] = x - 1;
                    neighbouringCoordinates[1][1] = y;

                    neighbouringCoordinates[0][2] = x;
                    neighbouringCoordinates[1][2] = y + 1;

                    neighbouringCoordinates[0][3] = x;
                    neighbouringCoordinates[1][3] = y - 1;

                    int neighbouringFloorCount = 0;
                    // Iterate through each neighbour, and count neighbouring floors.
                    for (int neighbourNum = 0; neighbourNum < 4; neighbourNum++)
                    {
                        // Check if each tile at the given coordinate is valid.
                        boolean success = validateCoordinates(neighbouringCoordinates[0][neighbourNum], neighbouringCoordinates[1][neighbourNum], floor);
                        if (success)    // if the tile is not out of bounds
                        {
                            Tile neighbouringTile = floor.getTile(neighbouringCoordinates[0][neighbourNum], neighbouringCoordinates[1][neighbourNum]);

                            if (neighbouringTile instanceof FloorTile)
                            {
                                neighbouringFloorCount++;
                            }
                        }
                    }

                    // If we had 4 neighbouring floors, set this base tile to a floor too.
                    if (neighbouringFloorCount == 4)
                    {
                        floor.setTile(baseTile.getXindex(), baseTile.getYindex(), new FloorTile(baseTile.getXindex(), baseTile.getYindex(), baseTile.getEmpire()));
                    }

                }
            }
        }
    }

    /**
     * Validate a tile's coordiantes.
     * @return true if the coordinates provided map to a tile on the map. Return false otherwise.
     */
    private boolean validateCoordinates(int x, int y, Floor floor)
    {
        if (x < 0 || y < 0 || x > floor.getWidth() - 1 || y > floor.getHeight() - 1)
        {
            return false;
        }

        return true;
    }

    /**
     * Transform every wall tile on this floor to a floor tile.
     */
    private void transformWallsToFloors(Floor floor)
    {
        for (Tile tile: floor.getTiles())
        {
            if (tile instanceof WallTile)
            {
                floor.setTile(tile.getXindex(), tile.getYindex(), new FloorTile(tile.getXindex(), tile.getYindex(), tile.getEmpire()));
            }
        }
    }

    private void wrapWithWalls(Floor floor)
    {
        for (Tile tile: floor.getTiles())
        {
            if (tile.getXindex() == 0 || tile.getXindex() == floor.getWidth() - 1 || tile.getYindex() == 0 || tile.getYindex() == floor.getHeight() - 1)
            {
                WallTile boundaryTile = new WallTile(tile.getXindex(), tile.getYindex(), tile.getEmpire());
                boundaryTile.makeBoundary();
                floor.setTile(tile.getXindex(), tile.getYindex(), boundaryTile);
            }
        }
    }

    /**
     * Return true if neighbouring 8 tiles are floor
     * @return true if neighbouring tiles are floor tiles.
     */
    private boolean areNeighbourTilesFloor(Tile tile)
    {
        int x = tile.getXindex();
        int y = tile.getYindex();

        for (int i = -1; i < 2; i ++)
        {
            for (int j = -1; j < 2; j ++)
            {
                int effectiveXIndex = x + j;
                int effectiveYIndex = y + i;
                // if neighbouring tile is not within bounds return false
                if (!(effectiveYIndex >= 0 && effectiveYIndex <= floor.getHeight() - 1 && effectiveXIndex >= 0 && effectiveXIndex <= floor.getWidth() - 1)) return false;
                // if neighbourint tile is not a floor tile, return false
                if (!(floor.getTile(effectiveXIndex, effectiveYIndex) instanceof FloorTile)) return false;
            }
        }
        // if all entities are valid, return true.
        return true;
    }

    // /**
    //  * Take a floor as input, and run Drunkard's Walk generation algorithm with preset values.
    //  * @param floor
    //  */
    // public void DrunkardWalk(Floor floor)
    // {
    //     DrunkardWalk(floor, floor.getStartingTile(), 45);
    // }

    // /**
    //  * Take a floor as input, run Drunkard's Walk generation algorithm with custom values.
    //  * @param floor the floor to be changed
    //  * @param floorCoveragePercent The percentage of the floor that will be dungeon-ified
    //  */
    // public void DrunkardWalk(Floor floor, Tile tile, float floorCoveragePercent)
    // {
    //     int tilesToFill = (int)(floor.getSize() * (floorCoveragePercent / 100 ));
    //     tilesToFill = 30;
    //     int dungeonTiles = floor.getDungeonTiles();

    //     tile.setDungeonTile(true);

    //     if (dungeonTiles < tilesToFill)
    //         DrunkardWalk(floor, getRandomNeighbouringTile(floor, tile), floorCoveragePercent);
    // }
    // public void DrunkardWalk(Floor floor, Tile tile, float floorCoveragePercent)
    // {
    //     int tilesToFill = (int)(floor.getSize() * (floorCoveragePercent / 100 ));
    //     //int tilesToFill = 3;
    //     int dungeonTiles = floor.getDungeonTiles();

    //     // if we have more tiles to fill
    //     if (dungeonTiles < tilesToFill)
    //     {
    //         Tile nextTile = getRandomNeighbouringTile(floor, tile);
    //         nextTile.setDungeonTile(true);
    //         DrunkardWalk(floor, nextTile, floorCoveragePercent);
    //     }

    // }


}