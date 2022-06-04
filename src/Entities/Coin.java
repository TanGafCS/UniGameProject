package src.Entities;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

public class Coin extends Entity implements Interactable
{
    private int value;

    public Coin(Floor floor, Vector2f pos, int value)
    {
        super(floor);
        this.value = value;
        this.setPos(pos);
    }

    public void onInteract(Entity interactingEntity)
    {
        if (interactingEntity instanceof Player)
        {
            getFloor().getPlayer().addCoins(this.value);
            getFloor().unregisterEntity(this);
        }
    }

    public boolean isAutoInteractable()
    {
        return true;
    }

    @Override
    public Drawable getDrawable()
    {
        // set coin-value dependent textures (bronze for 1, silver for 2, gold for >3, diamond if >10 etc.)
        Texture t = Textures.getTexture("denari");

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.scale(2f,2f);
        sprite.setPosition(getPosX(), getPosY());

        return sprite;
    }

    public Type getType()
    {
        return Type.item;
    }

    public void onInstantiate() {;}
}
