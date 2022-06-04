package src.Entities;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

public class InstructionsEntity extends Entity
{
    public InstructionsEntity(Floor floor)
    {
        super(floor);
    }

    public Drawable getDrawable()
    {
        Texture t = Textures.getTexture("tutorialLevelScreen");

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(0.5f,0.5f);

        return sprite;
    }

    public Type getType()
    {
        return Type.instructions;
    }
}
