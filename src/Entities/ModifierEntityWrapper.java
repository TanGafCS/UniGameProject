package src.Entities;

import org.jsfml.graphics.*;
import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.system.Vector2f;

/**
 * Entity wrapper for modifiers. Uses adjusted getPos().
 */
public class ModifierEntityWrapper extends Entity implements Interactable
{
    private Modifier modiferRef;

    public ModifierEntityWrapper(Floor floor, Modifier modifier)
    {
        super(floor);
        this.modiferRef = modifier;
        this.setPos(modifier.getPos());
    }

    /**
     * For modifier wrappers, on interact, give the player the modifer.
     */
    public void onInteract(Entity interactingEnt)
    {
        if (interactingEnt == getFloor().getPlayer())
        {
            // Give the player the modifier
            getFloor().getPlayer().addModifier(this.modiferRef);
            // Remove this modifier wrapper from the floor
            getFloor().unregisterEntity(this);
        }
    }

    public boolean isAutoInteractable()
    {
        return false;
    }

    public Drawable getDrawable()
    {
        modiferRef.setPos(this.getPos());
        return modiferRef.getDrawable();
    }

    public float getPosYBottom()
    {
        // Give this a negative value, as we always want it to be drawn in the background.
        return -1000;
    }

    @Override
    public void update(float deltaTime)
    {
        tryDisplayLevel(200);
    }

    private void tryDisplayLevel(float range)
    {
        Player player = getFloor().getPlayer();

        if(VectorHelpers.getDistance(player.getCentralPos(), this.getCentralPos()) < range)
        {
            Vector2f levelPos = new Vector2f(getPosX() - 4, getPosY() - 20);
            Text levelText = new Text(modiferRef.getLevel(), Textures.getFont());
            levelText.scale(0.5f, 0.5f);
            levelText.setPosition(levelPos);

            getFloor().getVisuals().add(levelText);
        }
    }

    public Type getType()
    {
        return Type.item;
    }
}
