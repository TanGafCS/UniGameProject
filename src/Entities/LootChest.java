package src.Entities;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2i;

public class LootChest extends Entity implements Interactable
{
    protected Entity heldEntity;                  // The entity inside this chest.

    public LootChest(Floor floor, Entity ent)
    {
        super(floor);
        this.heldEntity = ent;

        Vector2i location = new Vector2i(0,0);
        Vector2i size = new Vector2i(96, 96);
        this.rect = new IntRect(location, size);
    }

    /**
     * For modifier wrappers, on interact, give the player the modifer.
     */
    public void onInteract(Entity interactingEnt)
    {
        if (interactingEnt == getFloor().getPlayer())
        {
            // Drop held entity on the ground, where this chest is.
            getFloor().registerEntity(heldEntity);
            heldEntity.setPos(getCentralPos());
            // Remove this chest from the floor
            getFloor().unregisterEntity(this);
        }
    }

    public boolean isAutoInteractable()
    {
        return false;
    }

    public Drawable getDrawable()
    {
        Texture t = Textures.getTexture("chest");

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(3,3);

        return sprite;
    }

    public Type getType()
    {
        return Type.item;
    }
}
