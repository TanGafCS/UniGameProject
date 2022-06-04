package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.IntRect;

public class WallTile extends Tile{
    private String path;

    public WallTile(int x, int y, Empire empire){
        super(x,y, empire);//location on board
        setCorrectPath();
        this.set_sprite(path);
    }

    private void setCorrectPath()
    {
        switch (getEmpire())
        {
            case NONE:
                path = "wallTile1";
                break;
            case VIKING:
                path = "vikingWall";
                break;
            case EGYPTIAN:
                path = "egyptianWall";
                break;
            case GREEK:
                path = "greekWall";
                break;
            case ROMAN:
                path = "romanWall";
                break;
        }
    }

    @Override
    public String toString() {
        return "#";
    }
    @Override
    public boolean isCollideable()
    {
        return true;
    }

    public void makeBoundary()
    {
        this.set_sprite("boundaryTile1");
    }

    public IntRect collision_box(){
        return this.get_rect();
    }
}
