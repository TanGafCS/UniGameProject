package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;

public class FloorTile extends Tile{
    private String path;

    public FloorTile(int x, int y, Empire empire) {
        super(x,y, empire);
        setCorrectPath();
        this.set_sprite(path);
    }

    private void setCorrectPath()
    {
        double rand = Math.random();
        switch (getEmpire())
        {
            case NONE:
                path = "floorTile1";
                break;
            case VIKING:
                if(rand <= 0.2)
                {
                    path = "vikingFloor2";
                }
                else if(rand > 0.2 && rand <= 0.4)
                {
                    path = "vikingFloor3";
                }
                else
                {
                    path = "vikingFloor1";
                }
                break;
            case EGYPTIAN:
                if(rand <= 0.33)
                {
                    path = "egyptianFloor2";
                }
                else
                {
                    path = "egyptianFloor1";
                }
                break;
            case GREEK:
                if(rand <= 0.33)
                {
                    path = "greekFloor2";
                }
                else
                {
                    path = "greekFloor1";
                }
                break;
            case ROMAN:
                if(rand <= 0.33)
                {
                    path = "romanFloor2";
                }
                else
                {
                    path = "romanFloor";
                }
                break;
        }
    }

    @Override
    public String toString() {
        return ".";
    }
}
