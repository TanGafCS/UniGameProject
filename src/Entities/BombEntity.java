package src.Entities;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import src.DungeonGen.Floor;
import src.Textures.Textures;

public class BombEntity extends Entity implements Interactable {

    private int value;

    public BombEntity(Floor floor, Vector2f pos, int value)
    {
        super(floor);
        this.value = value;
        this.setPos(pos);
    }

    public void onInteract(Entity interactingEntity)
    {
        if (interactingEntity instanceof Player)
        {
            getFloor().getPlayer().addBombs(this.value);
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
        Texture t = Textures.getTexture("bomb");

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(2f,2f);

        return sprite;
    }

    public Type getType()
    {
        return Type.item;
    }

    public void onInstantiate() {;}

}
