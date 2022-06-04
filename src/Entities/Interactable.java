package src.Entities;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;

/**
 * Interface inherited by interactable objects.
 * Interactable should only be inherited by subclasses of Entity, as they must have a position associated with them.
 */
public interface Interactable
{

    /**
     * Method which runs when the interactable is interacted with.
     * Remember to destroy interactable on interact if relevant.
     * @param interactingEnt is the entity interacting with this object, usually the player.
     */
    public void onInteract(Entity interactingEnt);

    /**
     * Returns true if this interactable should be interacted with automatically if the player is in range: coins, etc.
     * @return True if autoInteractable, false otherwise.
     */
    public boolean isAutoInteractable();
}
