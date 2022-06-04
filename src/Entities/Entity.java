package src.Entities;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import src.Animations.*;
import org.jsfml.graphics.*;
import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class Entity implements DrawableEntity, Comparable<Entity> {

    // concrete classes should have an entity type.
    public enum Type
    {
        player,
        mob,            // e.g. enemies
        item,           // e.g. pre-placed modifiers, etc.
        projectile,
        fixed,           // e.g. ladders
        instructions    //for instructions
    }

    protected IntRect rect;
    private Floor floor;                                // Reference to the floor this entity is on. Used for collision checking.
    private float maxVel;
    private float drag;                                 // Velocity tends to 0 by drag amount each second.
    private float posX, posY;
    private float velX, velY, lastVelX, lastVelY, baseMaxVel;
    private float rectOffsetY, rectOffsetX;             // The offset from the entity's position the rect should be at. (e.g. offset y = 32, posY = 0, rect.y = 32.)
    private float timeLastHurt;                         // The time at which this entity last took damage
    private float timeLastShot, timeLastExplosive;
    protected float moveSpeed;                            // The gamespace units moved by the entity per second.
    private Empire empire;
    // Interaction variables
    private float interactRadius;                       // Radius to check around the entity when TryInteract() is called.
    // Combat values
    private int health;
    private int armour;
    protected int coins;
    private float staggerTime;                          // Time in seconds this entity is staggered for upon taking damage.
    protected float shotCooldown;
    protected int baseDamage;
    protected int projSpeed, projRange;
    protected int meleeSpeed, meleeRange;

    protected Animations animations;

    public Entity(Floor floor)
    {
        this.floor = floor;
        Vector2i location = new Vector2i((int)posX, (int)posY);
        Vector2i size = new Vector2i(64, 64);
        this.rect = new IntRect(location, size);

        this.empire = Empire.NONE;

        this.baseMaxVel = 360;
        this.posX = 0;
        this.posY = 0;
        this.velX = 0;
        this.velY = 0;
        this.lastVelX = 0;
        this.lastVelY = 0;
        this.drag = 0;

        this.rectOffsetX = 0;
        this.rectOffsetY = 0;
        this.timeLastHurt = -10f;                       // If entity not hurt yet, set to a negative value (so that it plays nice with entity.glsl)
        this.timeLastShot = -10f;
        this.moveSpeed = 120f;

        this.interactRadius = 96f;

        // Combat stats
        this.health = 1;
        this.armour = 0;
        this.coins = 1;
        this.staggerTime = 0.1f;
        this.shotCooldown = 0.6f;
        this.baseDamage = 1;
        this.projSpeed = 300;
        this.projRange = 400;
        this.meleeSpeed = 250;
        this.meleeRange = 100;

        setAnimations();
    }

    public Type getType()
    {
        return Type.mob;
    }

    public boolean damageable()
    {
        return false;
    }

    /**
     * Update the entity (call every frame)
     * @param deltaTime change in time since last frame
     */
    public void update(float deltaTime)
    {
        positionUpdateLinear(deltaTime);
    }

    /**
     * Checks for collisions between this entity's rect and nearby impassable tiles.
     * N.B. does not check entity on entity collision.
     */
    protected boolean checkCollisions()
    {
        moveRectToPos();

        Vector2i rectPos = new Vector2i(get_rect().left + get_rect().width/2, get_rect().top + get_rect().height/2);

        for (Tile tile: floor.getTilesAroundPos(this.getPos(), 1))
        {
            if (tile.isCollideable() && tile.get_rect().intersection(get_rect()) != null)
            {
                //System.out.println("intersection");
                return true;
            }
        }

        return false;
    }

    /**
     * Checks for collisions between this entity's rect after a move of moveVector and nearby impassable tiles.
     * Return true if colliding, false otherwise.
     * N.B. does not check entity on entity collision.
     */
    protected boolean checkCollisions(Vector2f moveVector, boolean respectEntities)
    {
        moveRectToPos();
        // calculate where the rect will be after a translation of moveVector and create a new rectPos
        float movedRectX = (get_rect().left) + moveVector.x;
        float movedRectY = (get_rect().top) + moveVector.y;
        Vector2i rectPos = new Vector2i((int)movedRectX, (int)movedRectY);

        IntRect movedRect = new IntRect(rectPos, new Vector2i(get_rect().width, get_rect().height));

        for (Tile tile: floor.getTilesAroundPos(this.getPos(), 5))
        {
            if (tile.isCollideable() && movedRect.intersection(tile.get_rect()) != null)
            {
                //System.out.println("intersection");
                return true;
            }
        }

        // Check for collisions with entities too, if parameter respectEntities is true
        if (respectEntities == true)
        {
            for (Entity ent: floor.getEntities())
            {
                if (!ent.equals(this) && ent.getType() == Type.mob && movedRect.intersection(ent.get_rect()) != null)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected void moveRectToPos()
    {
        Vector2i location = new Vector2i((int)(posX + rectOffsetX), (int)(posY + rectOffsetY));
        Vector2i size = new Vector2i(rect.width, rect.height);
        rect = new IntRect(location, size);
    }

    /**
     * Update position of entity, disregarding accel/deaccel.
     * @param deltaTime
     */
    protected void positionUpdateLinear(float deltaTime)
    {
        posX += velX * deltaTime;
        posY += velY * deltaTime;

        lastVelX = velX;
        lastVelY = velY;
    }

    /**
     * Update position of entity, with collision checking.
     * xMov and yMov are true if there is movement in the x or y axis. (in other words,
     * if A/D or W/S are still held)
     * To ALWAYS apply deaccel, set xMov and yMov true.
     */
    protected void positionUpdate(float deltaTime, boolean xMov, boolean yMov, boolean respectEntities)
    {
        // Set padding to act as a "skin", to avoid entities getting stuck in others.
        float paddingX = (velX > 0) ? 1.5f : -1.5f;
        float paddingY = (velY > 0) ? 1.5f : -1.5f;

        Vector2f moveX = new Vector2f(velX * deltaTime + paddingX, 0);
        Vector2f moveY = new Vector2f(0, velY * deltaTime + paddingY);
        boolean collided = false;   // have we collided this update?
        // Check that we are free to move on the X axis
        if (!checkCollisions(moveX, respectEntities))
        {
            posX += velX * deltaTime;
        }
        else
        {
            collided = true;
            velX = 0;
        }

        // Check that we are free to move on the Y axis
        if (!checkCollisions(moveY, respectEntities))
        {
            posY += velY * deltaTime;
        }
        else
        {
            collided = true;
            velY = 0;
        }

        // use an effective drag, so that we can change the drag value local to this function.
        float effectiveDrag = drag;
        if (collided)
            effectiveDrag = 9999f;
        // Take vel down by DRAG amount
        // Only apply drag if the entity's velocity isn't changing
        if ( (lastVelX == velX && !xMov) )
        {
            if (velX > effectiveDrag)
            {
                velX -= effectiveDrag;
            }
            else if (velX < -effectiveDrag)
            {
                velX += effectiveDrag;
            }
            else
            {
                velX = 0;
            }
        }

        if ( (lastVelY == velY && !yMov)  )
        {
            if (velY > effectiveDrag)
            {
                velY -= effectiveDrag;
            }
            else if (velY < -effectiveDrag)
            {
                velY += effectiveDrag;
            }
            else
            {
                velY = 0;
            }
        }

        lastVelX = velX;
        lastVelY = velY;
    }

    /**
     * Move this entity towards the specified entity. Call each frame.
     * @param target
     */
    protected void moveTowardsEntity(float deltaTime, Entity target, boolean respectWalls)
    {
        moveTowardsPos(deltaTime, target.getCentralPos(), respectWalls);
    }

    /**
     * Move this entity towards target world position.
     * @param target
     */
    protected void moveTowardsPos(float deltaTime, Vector2f target, boolean respectWalls)
    {
        Vector2f directionVector = Vector2f.sub(target, getCentralPos());

        if (Float.isNaN(directionVector.x) || Float.isNaN(directionVector.y) || (directionVector.x == 0f && directionVector.y == 0f))
        {
            //directionVector = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);
            directionVector = new Vector2f(0.01f, 0.01f);
        }

        float magnitude = (float)Math.sqrt((float)directionVector.x * directionVector.x + directionVector.y * directionVector.y );
        Vector2f normalisedVector = Vector2f.div(directionVector, magnitude);
        //System.out.println(normalisedVector);

        setVel(Vector2f.mul(normalisedVector, getMovementSpeed()));
        if (respectWalls)
        {
            positionUpdate(deltaTime, true, true, true);
        }
        else
        {
            positionUpdateLinear(deltaTime);
        }
    }

    /**
     * Try to interact with the nearest entity.
     * @return true if interaction succeeded with a nearby interactable, false otherwise.
     */
    public boolean tryInteract()
    {
        Interactable target = getNearestInteractable();

        if (target == null) return false;

        target.onInteract(this);
        return true;
    }

    /**
     * Set this entity's active floor.
     * @param floor
     */
    public void setFloor(Floor floor)
    {
        this.floor = floor;
    }

    /**
     * Get nearest interactable WITHIN interactRadius.
     * @return the nearest interactable this entity within interactRadius OR NULL if none within radius.
     */
    protected Interactable getNearestInteractable()
    {
        Interactable nearestInteractable = null;
        float distanceToInteractable = Float.MAX_VALUE;
        // Build list of only nearby interactables
        for (Interactable interactable: floor.getInteractables())
        {
            // all interactables are, so we can cast
            Entity ent = (Entity) interactable;
            float distance = VectorHelpers.getDistance(ent.getCentralPos(), getCentralPos());
            if (distance <= interactRadius)
            {
                if (nearestInteractable == null)
                {
                    nearestInteractable = interactable;
                    distanceToInteractable = distance;
                }
                else
                {
                    if (distance < distanceToInteractable)
                    {
                        distanceToInteractable = distance;
                        nearestInteractable = interactable;
                    }
                }
            }
        }
        return nearestInteractable;
    }

    /**
     * Get interactables near entity.
     * @return list of interactables within interactRadius
     */
    protected ArrayList<Interactable> getNearbyInteractables()
    {
        ArrayList<Interactable> nearbyInteractables = new ArrayList<>();
        // Build list of only nearby interactables
        for (Interactable interactable: floor.getInteractables())
        {
            // all interactables are, so we can cast
            Entity ent = (Entity) interactable;
            if (VectorHelpers.getDistance(ent.getCentralPos(), getCentralPos()) <= interactRadius)
            {
                nearbyInteractables.add(interactable);
            }
        }
        return nearbyInteractables;
    }


    /**
     * Get other entities near this entity.
     * @return list of entities within range
     * @param range in which entities will be returned
     * @return ArrayList of entities
     */
    public ArrayList<Entity> getNearbyEntities(float range)
    {
        ArrayList<Entity> nearbyEntities = new ArrayList<>();
        // Build list of only nearby interactables
        for (Entity ent: floor.getEntities())
        {
            if (VectorHelpers.getDistance(ent.getCentralPos(), getCentralPos()) <= range)
            {
                nearbyEntities.add(ent);
            }
        }
        return nearbyEntities;
    }

    public IntRect get_rect()
    {
        return rect;
    }

    /**
     * Deal damage to this entity, and check if dead
     * @param dmg set to a negative number to deal damage.
     */
    public void damage(int dmg)
    {
        // Damage armour
        int damageLeftToDeal = 0;
        armour -= dmg;

        // if armour "bottomed out", take excessive damage and deal it to wlth.
        if (armour < 0)
        {
            damageLeftToDeal = Math.abs(armour);
            armour = 0;
        }
        health -= damageLeftToDeal;

        // mark entity as hurt
        setTimeLastHurt(GameTime.getElapsedTime());
        if (health <= 0)
        {
            die();
        }
    }

    public Empire getEmpire() {
        return empire;
    }

    public void setEmpire(Empire empire)
    {
        this.empire = empire;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public void setArmour(int armour)
    {
        this.armour = armour;
    }

    public float getStaggerTime()
    {
        return staggerTime;
    }

    /**
     * Called when this entity dies.
     */
    public void die()
    {
        dropCoins();
        floor.unregisterEntity(this);
    }

    private void dropCoins()
    {
        if (coins > 0) floor.registerEntity(new Coin(floor, getCentralPos(), coins));
    }

    /**
     * Returns a placeholder drawable.
     * @return magenta square drawable
     */
    public Drawable getDrawable()
    {
        RectangleShape fillerRect = new RectangleShape(new Vector2f(64.0f, 64.0f));
        fillerRect.setPosition(posX, posY);
        fillerRect.setFillColor(Color.MAGENTA);
        return fillerRect;
    }

    public Vector2f getPos() {
        return new Vector2f(posX, posY);
    }

    /**
     * Return the center of this entity's rect.
     * @return center of this entity's rect.
     */
    public Vector2f getCentralPos()
    {
        moveRectToPos();
        IntRect rect = get_rect();
        float xPos = rect.left + rect.width/2;
        float yPos = rect.top + rect.height/2;
        return new Vector2f(xPos, yPos);
    }

    public void setPos(Vector2f pos) {
        this.posX = pos.x;
        this.posY = pos.y;
    }

    // Set rect's default y value to the expected y value for this sprite (.top) + input float y.
    protected void setRectOffsetY(float y)
    {
        this.rectOffsetY = y;
    }

    protected void setRectOffsetX(float x)
    {
        this.rectOffsetX = x;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public void setVel(Vector2f vec)
    {
        this.velX = vec.x;
        this.velY = vec.y;
    }

    public void setDrag(float drag)
    {
        this.drag = drag;
    }

    public float getDrag()
    {
        return drag;
    }

    public float getMaxVel() {
        return maxVel;
    }

    public void setMaxVel(float maxVel) {
        this.maxVel = maxVel;
    }

    public Floor getFloor() { return floor;}

    public float getTimeLastHurt()
    {
        return timeLastHurt;
    }

    public int getProjRange()
    {
        return projRange;
    }

    public int getProjSpeed()
    {
        return projSpeed;
    }

    public int getDamage()
    {
        return baseDamage;
    }

    public float getMovementSpeed()
    {
        return moveSpeed;
    }

    /**
     * Return time in seconds after firing a shot which the entity must wait for before shooting again.
     * @return shot cooldown in seconds.
     */
    public float getShotCooldown()
    {
        return shotCooldown;
    }

    public void setTimeLastShot(float time)
    {
        timeLastShot = time;
    }

    public float getTimeLastShot()
    {
        return timeLastShot;
    }

    public void setTimeLastExplosive(float time)
    {
        timeLastExplosive = time;
    }

    public float getTimeLastExplosive()
    {
        return timeLastExplosive;
    }

    public void setTimeLastHurt(float timeLastHurt)
    {
        this.timeLastHurt = timeLastHurt;
    }

    /**
     * Add value to entity's coins.
     * @param value may be positive or negative.
     */
    public void addCoins(int value)
    {
        this.coins += value;
    }

    public int getHealth()
    {
        return health;
    }

    public int getArmour()
    {
        return armour;
    }

    public int getCoins()
    {
        return coins;
    }

    public void addArmour(int armour)
    {
        this.armour += armour;
    }

    public float getBaseMaxVel()
    {
        return baseMaxVel;
    }

    /**
     * Used for figuring out what order to draw entities in.
     * N.B.: bit hacky, but set this to return a negative value for entities we want drawn in the background, e.g. modifierEntityWrappers.
     * @return the world-position of the bottom of this entity.
     */
    public float getPosYBottom()
    {
        return getPosY() + get_rect().height + rectOffsetY;
    }

    @Override
    public int compareTo(Entity ent) {
        return Float.compare(this.getPosYBottom(), ent.getPosYBottom());
    }

    public void setAnimations()
    {
        animations = null;
    }

    protected boolean isMoving()
    {
        if (Math.abs(getVelX()) > 10 || Math.abs(getVelY()) > 10)
        {
            return true;
        }
        return false;
    }
}