package src.DungeonGen;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.system.Vector2i;
import java.util.ArrayList;

/**
 * A campaign is a collection of levels (floors) that the player will progress through.
 * A campaign is generated on game start,
 */
public class Campaign {
    private ArrayList<Floor> floors;        // List of floors in the campaign.
    private final int stages = 4;
    private int floorsPerStage;             // No. of floors per stage.
    private Vector2i floorSize;             // Dimensions of floor
    private DungeonGenerator gen;

    private int floorIndex;                 // Index of the floor we're currently using. 0 indexed.

    public Campaign(int floorsPerStage)
    {
        this.floorsPerStage = floorsPerStage;
        this.floorSize = new Vector2i(64, 64);
        this.floorIndex = -1;   // Initialise at -1, as getNext() increments this index before retrieving the floor.

        this.floors = new ArrayList<>();
        gen = new DungeonGenerator();

        generateCampaign();
    }

    public int getSize()
    {
        return floors.size();
    }

    public int getFloorIndex()
    {
        return floorIndex;
    }

    private void generateCampaign()
    {
        floors.add(new tutorialFloor());
        // Generate STAGES number of stages, each with floorsPerStage floors.
        for (int stage = 0; stage < stages; stage++)
        {
            for (int i = 0; i < floorsPerStage; i++)
            {
                // Pass in correct empire value for the stage.
                Floor floor = new Floor(floorSize.y, floorSize.x, Empire.values()[stage], i + 1);
                gen.DrunkardWalk(floor);
                floors.add(floor);
            }
            // Now generate the boss level for this stage
            floors.add(new BossFloor(Empire.values()[stage], floorsPerStage + 1));
        }
    }

    /**
     * Returns the next floor in the campaign.
     * @return next floor in the campaign.
     */
    public Floor getNext()
    {
        floorIndex++;
        floors.get(floorIndex).initialiseTileEntities();
        return floors.get(floorIndex);
    }


}
