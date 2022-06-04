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
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

public class Bomb extends Entity{

    private float timeCreated, timeToLive;
    private Vector2f source;
    private Vector2f destinations[] = new Vector2f[8];
    private Entity bomber;

    public Bomb(Floor floor, Entity bomber, Vector2f source, float ttl){
        super(floor);
        floor.registerEntity(this);
        this.setPos(source);
        this.source = source;
        this.bomber = bomber;

        this.timeCreated = GameTime.getElapsedTime();
        timeToLive = GameTime.getElapsedTime() + ttl;

        initDestinations();
    }

    public void update(float deltaTime) {
        if (GameTime.getElapsedTime() > timeToLive) {
            getFloor().unregisterEntity((Entity) this);
            for (int i = 0; i < 8; i++) {
                Projectile bullet = new Projectile(this.getFloor(), this.bomber, this.getPos(), destinations[i], (int) getProjSpeed(),300, 2, 3*this.bomber.getDamage());
            }
            return;
        }
    }

    public void initDestinations(){
        this.destinations[0] = new Vector2f(source.x-1, source.y-1);
        this.destinations[1] = new Vector2f(source.x-1, source.y);
        this.destinations[2] = new Vector2f(source.x-1, source.y+1);
        this.destinations[3] = new Vector2f(source.x, source.y-1);
        this.destinations[4] = new Vector2f(source.x, source.y+1);
        this.destinations[5] = new Vector2f(source.x+1, source.y-1);
        this.destinations[6] = new Vector2f(source.x+1, source.y);
        this.destinations[7] = new Vector2f(source.x+1, source.y+1);
    }

    public Drawable getDrawable()
    {
        Texture t = Textures.getTexture("bomb"); //add new bomb sprite

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(2,2);

        return sprite;
    }
}
