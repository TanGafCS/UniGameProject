package src.Modifiers;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.Sprite;

public class ModifierYellowPill extends Modifier{
    public ModifierYellowPill()
    {
        super(0.2f, 0.1f, 0.0f, 0.3f);
    }

    public Drawable getDrawable(){
        s = new Sprite(Textures.getTexture("yellowPill"));//declare new sprite
        s.setPosition(getPos());
        //s.scale(2f,2f);
        return s;
    }
}
