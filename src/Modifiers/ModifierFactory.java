package src.Modifiers;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import java.util.ArrayList;
import java.util.Random;

public class ModifierFactory
{

    ArrayList<Modifier> modifiers;

    public ModifierFactory()
    {
        this.modifiers = new ArrayList<>();
        // instantiate list of possible modifiers
        modifiers.add(new ModifierSandals());
        modifiers.add(new ModifierBionicEye());
        modifiers.add(new ModifierBluePill());
        modifiers.add(new ModifierCaesarsCoin());
        modifiers.add(new ModifierGreenPill());
        modifiers.add(new ModifierRedPill());
        modifiers.add(new ModifierYellowPill());

    }

    public ModifierEntityWrapper createRandomModifier(Floor floor, boolean raiseLevel)
    {
        ModifierEntityWrapper modEnt = null;

        Random rng = new Random();
        int randomNum = rng.nextInt(modifiers.size());
        String modName = modifiers.get(randomNum).getClass().getName();
        try
        {
            Object modObject = Class.forName(modName).newInstance();
            Modifier modifier = (Modifier) modObject;
            float mult = EntityHelpers.empToDiff(floor.getEmpire());
            if (raiseLevel)
            {
                mult += 0.5f;
            }
            modifier.setMultiplier(mult);
            modEnt = new ModifierEntityWrapper(floor, modifier);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (modEnt == null)
            System.out.println("ERROR in ModifierFactory createRandomModifier.");
        return modEnt;
    }
}
