package src.Entities;

import org.jsfml.system.Vector2f;
import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import java.util.ArrayList;
import java.util.Random;

public class EntityFactory {
    ArrayList<Entity> enemies;
    ArrayList<Entity> loot;

    public EntityFactory()
    {
        this.enemies = new ArrayList<>();
        this.loot = new ArrayList<>();

        instantiateEnemies();
        instantiateLoot();
    }

    public Entity createRandomEnemy(Floor floor)
    {
        Entity ent = null;

        Random rng = new Random();
        int randomNum = rng.nextInt(enemies.size());
        String entName = enemies.get(randomNum).getClass().getName();
        try
        {
            Object entObject = Class.forName(entName).getDeclaredConstructor(Floor.class, Empire.class).newInstance(floor, floor.getEmpire());
            ent = (Entity) entObject;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (ent == null)
            System.out.println("ERROR in ModifierFactory createRandomModifier.");
        return ent;
    }

    public Entity createRandomLoot(Floor floor)
    {
        Entity ent = null;

        Random rng = new Random();
        int randomNum = rng.nextInt(loot.size());
        String entName = loot.get(randomNum).getClass().getName();
        try
        {
            Object entObject = Class.forName(entName).getDeclaredConstructor(Floor.class, Vector2f.class, int.class).newInstance(floor, new Vector2f(0f,0f), 1);
            ent = (Entity) entObject;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (ent == null)
            System.out.println("ERROR in ModifierFactory createRandomModifier.");
        return ent;
    }

    /**
     * instantiate list of possible enemies
     */
    private void instantiateEnemies()
    {
        Floor floorTemp = new Floor(5, 5, Empire.NONE, -1);
        enemies.add(new MeleeEnemy(floorTemp, floorTemp.getEmpire()));
        enemies.add(new RangedEnemy(floorTemp, floorTemp.getEmpire()));
    }

    /**
     * instantiate list of possible loot drops
     */
    private void instantiateLoot()
    {
        Floor floorTemp = new Floor(5, 5, Empire.NONE, -1);
        loot.add(new ArmourEntity(floorTemp, new Vector2f(0f,0f), 1));
        loot.add(new BombEntity(floorTemp, new Vector2f(0f,0f), 1));
        loot.add(new BarrelEntity(floorTemp, new Vector2f(0f,0f), 1));
        loot.add(new HealthEntity(floorTemp, new Vector2f(0f,0f), 1));
        loot.add(new Coin(floorTemp, new Vector2f(0f,0f), 1));
    }
}
