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

public class DummyEntity extends Enemy
{
    public DummyEntity(Floor floor, Empire empire)
    {
        super(floor, empire);

        coins = 0;
        // Make dummy entity unkillable (for the faint of heart)
        this.setHealth(99999);
    }


    // Change update to hold dummy entity still
    @Override
    public void update(float deltaTime)
    {
        // Don't update if staggered
        if (GameTime.getElapsedTime() - getTimeLastHurt() < getStaggerTime()) return;

        moveRectToPos();
    }

    public Drawable getDrawable()
    {
        Texture t = Textures.getTexture("dummy");

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(2f,2f);

        return sprite;
    }
}