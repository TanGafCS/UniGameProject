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

public class tutorialFloor extends Floor {

    public tutorialFloor(){
        super (8, 17, Empire.NONE, 1);
        setStartPos(getTile(4, 3).getPos());

        setup();
    }

    private void setup()
    {
        // Set up floor tiles
        for (int x = 0; x < this.getWidth(); x++)
        {
            for (int y = 0; y < this.getHeight(); y++)
            {
                this.setTile(x, y, new FloorTile(x, y, Empire.NONE));

                if (x == 0 || x == this.getWidth() - 1 || y == 0 || y == this.getHeight() - 1)
                {
                    this.setTile(x, y, new WallTile(x, y, Empire.NONE));
                }
            }
        }
        InteractableLadder ladder = new InteractableLadder(this, false);
        ladder.setPos(this.getTile(15, 3).getPos());
        this.registerEntity(ladder);
        InstructionsEntity instructions = new InstructionsEntity(this);
        instructions.setPos(this.getTile(1, 1).getPos());
        this.registerEntity(instructions);
        DummyEntity dummy = new DummyEntity(this, Empire.NONE);
        dummy.setPos(this.getTile(15, 5).getPos());
        this.registerEntity(dummy);
    }

//    // Create an enemy that's easy to destroy but acts the same so player can learn controls
//
//    public void createEmptyEnemy(Entity ent){
//
//        this.timeLastHurt = -20f;
//        this.moveSpeed = 120f;
//
//    }
//
//
//    public void onInteract(Entity interactingEnt)
//    {
//        if (interactingEnt == getFloor().getPlayer())
//        {
//            getFloor().getPlayer().addModifier(this.modiferRef);
//            getFloor().getPlayer().getGameView().advanceFloor();
//            getFloor().unregisterEntity(this);
//        }
//    }
//
//    private void advanceFloor(){
//
//        Floor nextFloor = campaign.getNext();
//        // Switch out statePainter's floor reference so it draws the correct floor
//        sPainter.setFloor(nextFloor);
//        // Move the player reference to the correct floor
//        nextFloor.registerEntity(player);
//        player.setFloor(nextFloor);
//        player.setPos(floor.getStartPos());
//        floor = nextFloor;
//    }
}

    // Allow player to move around 
    // Allow player to interact
    // Allow player to shoot at enemy
    // Allow player to take the ladder

