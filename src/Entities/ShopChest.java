package src.Entities;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;

import java.nio.file.Paths;

public class ShopChest extends LootChest {

    private int cost;

    public ShopChest(Floor floor, Entity ent, int cost)
    {
        super(floor, ent);
        this.cost = cost;
    }

    public void onInteract(Entity interactingEnt)
    {
        if (interactingEnt == getFloor().getPlayer())
        {
            if (interactingEnt.getCoins() >= cost)
            {
                // Add negative coins, awkward method, whoops
                interactingEnt.addCoins(-cost);
                super.onInteract(interactingEnt);
            }
        }
    }

    public void update(float deltaTime)
    {
        tryPromptPrice(300);
    }

    /**
     * Show the player the cost of this shop, if they are nearby.
     */
    private void tryPromptPrice(float range)
    {
        Player player = getFloor().getPlayer();

        if(VectorHelpers.getDistance(player.getCentralPos(), this.getCentralPos()) < range)
        {
            Texture t = Textures.getTexture("denari");

            // Chest sprite
            Sprite sprite = new Sprite(t);
            sprite.setPosition(getPosX() + 8, getPosY() - get_rect().height);
            sprite.scale(2, 2);


            String costStr = String.valueOf(cost);
            Font font = Textures.getFont();
            Text costTxt = new Text(costStr, font);
            costTxt.setPosition(getPosX() + 42, getPosY() - get_rect().height);

            heldEntity.setPos(new Vector2f(getPosX() + 32, getPosY() - 10));
            getFloor().getVisuals().add(heldEntity.getDrawable());
            getFloor().getVisuals().add(sprite);
            getFloor().getVisuals().add(costTxt);
        }
    }

    @Override
    public Drawable getDrawable()
    {
        Texture t = Textures.getTexture("shopChest");

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(3,3);

        return sprite;
    }

}
