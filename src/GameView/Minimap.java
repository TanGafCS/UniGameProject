package src.GameView;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import src.Entities.*;
import src.GameView.*;
import src.DungeonGen.*;

import java.util.ArrayList;
import java.util.Collections;

public class Minimap {

    private Floor floor;
    private Player player;
    private RectangleShape playerRect;
    private RectangleShape currentEnt;
    private static final float range = 1000f;
    private static final float scale = range/70;



    public Minimap(Floor floor){
        this.floor = floor;
        this.player = floor.getPlayer();
        this.playerRect = new RectangleShape(new Vector2f(5f, 5f));
        this.playerRect.setFillColor(Color.WHITE);

        this.currentEnt = new RectangleShape(new Vector2f(5f, 5f));
    }

    public void paint(RenderWindow targetWindow, Vector2f[] values){
        Vector2f centrePos = new Vector2f((values[0].x+67.5f), (values[0].y+67.5f));

        ArrayList<Entity> allEntities = new ArrayList<>();
        allEntities.addAll(player.getNearbyEntities(range));
        Collections.sort(allEntities);

        playerRect.setPosition(centrePos.x, centrePos.y);

        Vector2f entPos;

        for(Entity ent: allEntities){
            entPos = VectorHelpers.getVectorDistance(ent.getCentralPos(), player.getCentralPos());
            currentEnt.setPosition(centrePos.x - entPos.x/scale, centrePos.y - entPos.y/scale);

            if (ent.getType() == Entity.Type.mob){
                currentEnt.setFillColor(Color.RED);
            }
            else if (ent.getType() == Entity.Type.item){
                currentEnt.setFillColor(Color.YELLOW);
            }
            else if (ent.getType() == Entity.Type.projectile){
                currentEnt.setFillColor(Color.TRANSPARENT);
            }
            else if (ent.getType() == Entity.Type.fixed){
                currentEnt.setFillColor(Color.GREEN);
            }
            else if (ent.getType() == Entity.Type.instructions){
                currentEnt.setFillColor(Color.TRANSPARENT);
            }
            targetWindow.draw(currentEnt);
        }
        targetWindow.draw(playerRect);
    }
}
