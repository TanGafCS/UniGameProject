package src.Entities;

import org.jsfml.graphics.*;
import src.DungeonGen.Floor;
import src.GameView.GameTime;
import src.Textures.Textures;

public class Spawner extends Entity {

    private float spawnTime;
    private float period;
    private EntityFactory entFact;

    /**
     * This class spawns an enemy at regular intervals.
     * @param period period of time between each spawn
     */
    public Spawner(Floor floor, float period)
    {
        super(floor);
        this.period = period;
        this.entFact = new EntityFactory();
        this.spawnTime = GameTime.getElapsedTime();
    }

    @Override
    public Type getType() {
        return Type.fixed;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (GameTime.getElapsedTime() >= spawnTime)
        {
            this.spawnTime = GameTime.getElapsedTime() + period;

            Entity entity = entFact.createRandomEnemy(getFloor());
            Enemy enemy = (Enemy) entity;
            enemy.setPos(this.getPos());
            enemy.setAggroRange(5000f);
            getFloor().registerEntity(enemy);
        }
    }

    @Override
    public Drawable getDrawable() {

        RectangleShape rect = new RectangleShape();
        rect.setFillColor(new Color(0, 0, 0, 0));

        rect.setPosition(getPosX(), getPosY());

        return rect;
    }
}
