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
import javax.swing.ImageIcon;

import java.util.*;

public class BossFloor extends Floor {

    private static final int roomLength = 24;

    public BossFloor(Empire emp, int floorNum)
    {
        super(roomLength, roomLength, emp, floorNum);
        setStartPos(getTile(roomLength / 2, roomLength - 4).getPos());
        setup();
    }

    private void setup() {
        // Set up floor tiles
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                this.setTile(x, y, new FloorTile(x, y, Empire.NONE));

                if (x == 0 || x == this.getWidth() - 1 || y == 0 || y == this.getHeight() - 1) {
                    this.setTile(x, y, new WallTile(x, y, Empire.NONE));
                }
            }
        }

        Boss boss = new Boss(this);
        boss.setPos(this.getTile(roomLength / 2, (roomLength / 2) - 4).getPos());
        this.registerEntity(boss);

        ArrayList<Vector2f> spawnerPositions = new ArrayList<>();
        spawnerPositions.add(this.getTile((int)roomLength/4, (int)roomLength/4).getPos());
        spawnerPositions.add(this.getTile((int)(roomLength/4) * 3, (int)(roomLength/4) * 2).getPos());
        spawnerPositions.add(this.getTile((int)roomLength/4, (int)(roomLength/4 * 2)).getPos());
        spawnerPositions.add(this.getTile((int)(roomLength/4) * 3, (int)roomLength/4).getPos());

        for (Vector2f pos: spawnerPositions)
        {
            Spawner spawner = new Spawner(this, EntityHelpers.getEnemySpawnTime());
            spawner.setPos(pos);
            this.registerEntity(spawner);
        }
    }
}

