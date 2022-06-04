package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.IntRect;

public class EmptyTile extends Tile{
    private String path = "emptyTile1";

    public EmptyTile(int x, int y, Empire empire){
        super(x, y, empire);//location on board
        // Note about setting texture - we will probably need to check neighbouring tiles to pick the
        // correct "orientation" of the wall texture.
        this.set_sprite(path);//path is the file path to the texture to be used

    }

    @Override
    public String toString() {
        return " ";
    }

    public IntRect collision_box(){
        return this.get_rect();
    }
}
