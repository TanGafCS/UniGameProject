package src.Entities;

import org.jsfml.audio.Sound;
import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.ArrayList;

public class Projectile extends Entity {
    private Vector2f source, destination, newPos;
    private float projectileAngle;
    private int speed, range;
    private float travelled = 0;
    private int ifMelee = 0;
    private int damage;
    private Entity shooter;         // Entity which this proj belongs to
    private Sound projSound;

    public Projectile(Floor floor, Entity shooter, Vector2f source, Vector2f destination, int speed, int range, int ifMelee, int damage) {
        super(floor);
        floor.registerEntity(this);
        this.shooter = shooter;
        this.setPos(source);
        this.setPosX(getPosX());
        this.setPosY(getPosY());
        this.source = source;
        this.damage = damage;

        Vector2i size = new Vector2i(32, 32);
        if(this.ifMelee == 1) {size = new Vector2i(64, 16);};
        this.rect = new IntRect(new Vector2i(getPos()), size);

        this.destination = destination;

        //calculate angle between source and destination
        newPos = Vector2f.sub(this.destination, this.source);
        projectileAngle = (float) Math.atan2(newPos.y, newPos.x);

        this.speed = speed;
        this.range = range;

        this.ifMelee = ifMelee;
        this.projSound = new Sound(Sounds.getSound("proj"));
        projSound.setPitch(EntityHelpers.randomiseWithinRange(0.85f,1.0f));
        projSound.setVolume(10);
        projSound.play();
    }

    private Entity tryGetCollidingEntity()
    {
        for (Entity ent: getFloor().getAllEntities())
        {
            // if there is an intersection and it's not between the shooter and the proj
            if (ent.get_rect().intersection(this.get_rect()) != null && shooter != ent && ent != this && ent.damageable())
            {
                // if the shooter isn't the player, don't return hits on other mob types.
                if (shooter != getFloor().getPlayer() && ent.getType() == Type.mob)
                {
                    return null;
                }
                return ent;
            }
        }
        return null;
    }

    private Tile tryGetCollidingWall()
    {
        for (Tile tile: getFloor().getTilesAroundPos(getPos(), 1))
        {
            if (tile instanceof WallTile && tile.get_rect().intersection(this.get_rect()) != null)
            {
                return tile;
            }
        }
        return null;
    }

    public void update(float deltaTime) {
        // Damage colliding entities
        Entity collidingEnt = tryGetCollidingEntity();
        if (collidingEnt != null)
        {
            collidingEnt.damage(getDamage());
            getFloor().unregisterEntity(this);
        }

        // this condition is a quick-fix so that if the projectile is spawned near a wall, it's not despawned.
        if (getTravelled() > 50)
        {
            // Destroy projectiles upon collision with walls
            Tile collidingWall = tryGetCollidingWall();
            if (collidingWall != null)
            {
                getFloor().unregisterEntity(this);
            }
        }

        // If projectile has travelled further than its range, remove it.
        if (getTravelled() >= getRange())
        {
            getFloor().unregisterEntity((Entity)this);
            return;
        }

        checkCollisions();
        // Calculate distance travelled by projectile this update
        float distanceTravelledX = (float)(deltaTime * speed * Math.cos(projectileAngle));
        float distanceTravelledY = (float)(deltaTime * speed * Math.sin(projectileAngle));
        float distanceTravelled = VectorHelpers.getMagnitude(new Vector2f(distanceTravelledX, distanceTravelledY));

        this.setPosX(this.getPosX()+ (float)(deltaTime * speed * Math.cos(projectileAngle)));
        this.setPosY(this.getPosY()+ (float)(deltaTime * speed * Math.sin(projectileAngle)));
        travelled += distanceTravelled;
    }

    public int getDamage()
    {
        return damage;
    }

    @Override
    public Drawable getDrawable()
    {
        Texture t = Textures.getTexture("bullet");
        if(ifMelee == 1){ t = Textures.getTexture("melee"); }
        if(ifMelee == 2){ t = Textures.getTexture("bombBits"); }

        Sprite sprite = new Sprite(t);//declare new sprite
        if(ifMelee == 1){ sprite.setOrigin(32,8);}
        else{ sprite.setOrigin(16,16);}
        sprite.setPosition(getPosX(), getPosY());
        sprite.rotate((float)(projectileAngle * (180/Math.PI))+90);

        return sprite;
    }

    public int getRange(){
        return range;
    }

    public float getTravelled(){
        return travelled;
    }

    public Type getType()
    {
        return Type.projectile;
    }

    public void onInstantiate() {;}
}
