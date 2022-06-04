package src.Entities;

import org.jsfml.audio.Sound;
import src.Animations.Animations;
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

public class Player extends Entity {

    private final int startingHealth = 10;
    private final int startingArmour = 5;

    public static int MAX_MODIFIERS = 3;
    private float acceleration = 64 * 32;
    private int bombs, barrels;
    public float fuseLength;
    private int meleeSpeed, meleeRange;
    private Vector2f posCentre;
    private ArrayList<Modifier> modifiers;
    private int selectedModifierIndex;
    private GameView gameView;                  // Used to proceed to next floor etc.

    //
    private Sound attackSound1;
    private Sound attackSound2;
    private Sound hurtSound1;
    private Sound hurtSound2;
    private float lastTimeSoundPlayed;      // the time a sound was last played.

    public Player(Floor floor, GameView gameView)
    {
        super(floor);

        this.selectedModifierIndex = -1;
        this.gameView = gameView;
        // register player with floor's entity list.
        floor.registerEntity(this);

        Vector2i size = new Vector2i(50, 64);
        this.rect = new IntRect(new Vector2i(getPos()), size);
        setDrag(1 * 64);
        setMaxVel(5 * 64);

        //setRectOffsetY(64f);
        setRectOffsetY(48f);
        setRectOffsetX(18f);

        // Initial player values
        this.shotCooldown = 0.3f;
        this.projSpeed = 1000;
        this.projRange = 500;
        meleeSpeed = 1500;
        meleeRange = 100;
        baseDamage = 10;

        resetState();
        modifiers = new ArrayList<>();

        this.bombs = 5;
        this.barrels = 5;
        this.fuseLength = 1.5f;
        setHealth(10);
        setArmour(5);

        this.attackSound1 = new Sound(Sounds.getSound("ha1"));
        attackSound1.setVolume(65f);
        this.attackSound2 = new Sound(Sounds.getSound("ha2"));
        attackSound2.setVolume(65f);
        this.hurtSound1 = new Sound(Sounds.getSound("oof1"));
        this.hurtSound2 = new Sound(Sounds.getSound("oof2"));
        this.lastTimeSoundPlayed = -10f;
    }

    /**
     * If an auto-interaction is available, execute it.
     * (e.g., give visual prompts for interactables, pick up coins.)
     */
    private void tryPassiveActions()
    {
        if (getNearbyInteractables().size() > 0)
        {
            tryAutoPickup();
            promptInteraction();
        }
    }

    @Override
    public boolean damageable()
    {
        return true;
    }

    /**
     * If an interactable is in range, pick it up if it is auto-pickup enabled.
     */
    private void tryAutoPickup()
    {
        ArrayList<Interactable> interactables = getNearbyInteractables();

        for (Interactable interactable: interactables)
        {
            if (interactable.isAutoInteractable())
            {
                interactable.onInteract(this);
            }
        }
    }

    /**
     * Visually prompt the player that an interaction is available.
     */
    private void promptInteraction()
    {
        if (getNearbyInteractables().size() > 0)
        {
            Texture t = Textures.getTexture("interactionAvailable");

            Sprite sprite = new Sprite(t);//declare new sprite
            // We can later change this to being figured out from the View - cleaner solution
            sprite.setPosition(getPosX() + 8, getPosY() - get_rect().height);
            sprite.scale(1, 1);

            getFloor().getVisuals().add(sprite);
        }
    }

    public GameView getGameView()
    {
        return gameView;
    }

    @Override
    public void damage(int dmg) {
        tryPlayHurtSound();
        super.damage(dmg);
    }

    private void tryPlayHurtSound()
    {
        if (GameTime.getElapsedTime() > lastTimeSoundPlayed + 0.4f)
        {
            if (Math.random() > 0.5)
            {
                hurtSound1.setPitch(EntityHelpers.randomiseWithinRange(0.85f,1.0f));
                hurtSound1.play();
            }
            else
            {
                hurtSound2.setPitch(EntityHelpers.randomiseWithinRange(0.85f,1.0f));
                hurtSound2.play();
            }
            lastTimeSoundPlayed = GameTime.getElapsedTime();
        }
    }

    private void tryPlayAttackSound()
    {
        if (GameTime.getElapsedTime() > lastTimeSoundPlayed + 0.4f)
        {
            if (Math.random() > 0.5)
            {
                attackSound1.setPitch(EntityHelpers.randomiseWithinRange(0.85f,1.0f));
                attackSound1.play();
            }
            else
            {
                attackSound2.setPitch(EntityHelpers.randomiseWithinRange(0.85f,1.0f));
                attackSound2.play();
            }
            lastTimeSoundPlayed = GameTime.getElapsedTime();
        }
    }

    public void update(float deltaTime, boolean xMov, boolean yMov)
    {
        setMaxVel(getBaseMaxVel() * getMovementSpeed());
        positionUpdate(deltaTime, xMov, yMov, false);
        posCentre = new Vector2f(getPosX()+32,getPosY()+64);
        tryPassiveActions();
    }


    public void moveUp(float deltaTime)
    {
        setVelY(getVelY() - (getAcceleration() * deltaTime));
    }

    public void moveDown(float deltaTime)
    {
        setVelY(getVelY() + (getAcceleration() * deltaTime));
    }

    public void moveLeft(float deltaTime)
    {
        setVelX(getVelX() - (getAcceleration() * deltaTime));
    }

    public void moveRight(float deltaTime)
    {
        setVelX(getVelX() + (getAcceleration() * deltaTime));
    }

    public Projectile attack(Vector2f mousePos){
        if (GameTime.getElapsedTime() - getTimeLastShot() > getShotCooldown())
        {
            setTimeLastShot(GameTime.getElapsedTime());
            Projectile bullet = new Projectile(this.getFloor(), this, posCentre, mousePos, (int)getProjSpeed(), (int)getProjRange(), 0, getDamage());
            animations.setLastAttackTime();
            return bullet;
        }
        return null;
    }

    public Projectile melee(Vector2f mousePos){
        if (GameTime.getElapsedTime() - getTimeLastShot() > getShotCooldown())
        {
            setTimeLastShot(GameTime.getElapsedTime());
            Projectile meleeAttack = new Projectile(this.getFloor(), this, posCentre, mousePos, meleeSpeed, meleeRange, 1, (int)(getDamage() * 2f));
            animations.setLastAttackTime();
            tryPlayAttackSound();
            return meleeAttack;
        }
        return null;
    }

    public void placeBomb(){
        if (bombs > 0 && GameTime.getElapsedTime() - getTimeLastExplosive() > getShotCooldown())
        {
            setTimeLastExplosive(GameTime.getElapsedTime());
            Bomb bomb = new Bomb(this.getFloor(), this, this.getCentre(), fuseLength);
            bombs--;
        }
    }

    public void placeBarrel(){
        if (barrels > 0 && GameTime.getElapsedTime() - getTimeLastExplosive() > getShotCooldown())
        {
            setTimeLastExplosive(GameTime.getElapsedTime());
            Barrel barrel = new Barrel(this.getFloor(), this, this.getCentre(), fuseLength);
            barrels--;
        }
    }

    @Override
    public Entity.Type getType()
    {
        return Entity.Type.player;
    }

    @Override
    public int getDamage()
    {
        float dmg = baseDamage;
        for (Modifier mod : modifiers)
        {
            dmg += mod.get_damage();
        }
        return (int)dmg;
    }

//    @Override
//    public int getProjRange() {
//        return super.getProjRange() * ;
//    }

    /**
     * Called when this entity dies.
     */
    @Override
    public void die()
    {
        //Display game over screen, then reset game state.
        gameView.setGameOver(true, GameTime.getElapsedTime() + 5f);
        gameOver();
    }

    private void gameOver()
    {
        resetState();
        gameView.resetGame();
    }

    /**
     * get multiplier for maxVel that should be used.
     * @return multiplier for movement speed
     */
    @Override
    public float getMovementSpeed()
    {
        float mult = 1;
        for (Modifier mod : modifiers)
        {
            mult += mod.get_movementSpeed();
        }
        return mult;
    }

    @Override
    public int getProjRange() {
        float mult = 1;
        for (Modifier mod : modifiers)
        {
            mult += mod.get_projectileSpeed();
        }
        float projRange = super.getProjRange();
        return (int)(mult * projRange);
    }

    @Override
    public int getProjSpeed() {
        float mult = 1;
        for (Modifier mod : modifiers)
        {
            mult += mod.get_projectileSpeed();
        }
        float projSpeed = super.getProjSpeed();
        return (int)(mult * projSpeed);
    }

    @Override
    public float getShotCooldown()
    {
        float mult = 1;
        for (Modifier mod : modifiers)
        {
            mult += mod.get_attackSpeed();
        }
        return shotCooldown / mult;
    }

    public void clampVel()
    {
        float maxVel = getMaxVel();

        if (getVelX() > maxVel)
        {
            setVelX(maxVel);
        }
        else if (getVelX() < -maxVel)
        {
            setVelX(-maxVel);
        }

        if (getVelY() > maxVel)
        {
            setVelY(maxVel);
        }
        else if (getVelY() < -maxVel)
        {
            setVelY(-maxVel);
        }
    }

    @Override
    public Drawable getDrawable()
    {
        Texture t = animations.getTexture(isMoving());

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(2,2);

        return sprite;
    }

    public Vector2f getCentre(){
        return posCentre;
    }
    public ArrayList<Modifier> getModifiers()
    {
        return modifiers;
    }

    public void addModifier(Modifier mod)
    {
        if (modifiers.size() < MAX_MODIFIERS)
        {
            modifiers.add(mod);
        }
        else
        {
            if (selectedModifierIndex == -1)
                selectedModifierIndex = 3;

            // Create new list with the references in the correct order
            ArrayList<Modifier> tempModifiers = new ArrayList<>();

            // Recreate the modifier list.
            for (int i = 0; i < modifiers.size(); i++)
            {
                if (selectedModifierIndex == i + 1) {
                    tempModifiers.add(mod);
                }
                else
                {
                    tempModifiers.add(modifiers.get(i));
                }

            }

            // This line below is temporary, until we add an option to select modifier slots.
            Modifier selectedModifier = modifiers.get(selectedModifierIndex - 1);
            // Drop selected modifier onto ground, and remove from player's list
            dropModifier(selectedModifier);

            // Swap in new modifier list.
            this.modifiers = tempModifiers;
        }
    }

    private void dropModifier(Modifier mod)
    {
        Vector2f dropPos = new Vector2f(getPos().x + get_rect().width / 2, getPos().y + get_rect().height / 2);
        mod.setPos(dropPos);
        ModifierEntityWrapper dropMod = new ModifierEntityWrapper(getFloor(), mod);
        getFloor().registerEntity(dropMod);
    }


    /**
     * Indexed 1-3, as these are the buttons the player uses to select the modifiers.
     * @param index
     */
    public void setSelectedModifier(int index)
    {
        selectedModifierIndex = index;
    }


    public int getSelectedModifierIndex()
    {
        return selectedModifierIndex;
    }

    public Modifier getSelectedModifier()
    {
        return modifiers.get(selectedModifierIndex - 1);
    }

    public float getAcceleration()
    {
        // scale accel with movement speed.
        return acceleration * (getMovementSpeed() * 1.1f);
    }

    public Modifier getModifier(int index)
    {
        if (modifiers.size() > index)
        {
            Modifier returnMod = modifiers.get(index);
            if (returnMod != null)
            {
                return returnMod;
            }
        }

        return new ModifierNull();
    }

    public void resetState()
    {
        this.setHealth(startingHealth);
        this.setArmour(startingArmour);
        this.coins = 0;
        this.modifiers = new ArrayList<Modifier>();
    }

    public int getBombs()
    {
        return bombs;
    }

    public void addBombs(int bombs)
    {
        this.bombs += bombs;
    }

    public int getBarrels()
    {
        return barrels;
    }

    public void addBarrels(int barrels)
    {
        this.barrels += barrels;
    }

    @Override
    public void setAnimations()
    {
        this.animations = new Animations(Textures.getTexture("player"), Textures.getTexture("playerLFootUp"),
                Textures.getTexture("playerRFootUp"), Textures.getTexture("playerMelee1"), this);
//        switch (getEmpire())
//        {
//            case NONE:
//                this.animations = new Animations(Textures.getTexture("player"), Textures.getTexture("playerLFootUp"),
//                        Textures.getTexture("playerRFootUp"), Textures.getTexture("playerMelee1"));
//                break;
//            case VIKING:
//                this.animations = new Animations(Textures.getTexture("roman"), Textures.getTexture("romanLFootUp"),
//                        Textures.getTexture("romanRFootUp"), Textures.getTexture("romanMelee"));
//                break;
//            case EGYPTIAN:
//                this.animations = new Animations(Textures.getTexture("roman"), Textures.getTexture("romanLFootUp"),
//                        Textures.getTexture("romanRFootUp"), Textures.getTexture("romanMelee"));
//                break;
//            case GREEK:
//                this.animations = new Animations(Textures.getTexture("roman"), Textures.getTexture("romanLFootUp"),
//                        Textures.getTexture("romanRFootUp"), Textures.getTexture("romanMelee"));
//                break;
//            case ROMAN:
//                this.animations = new Animations(Textures.getTexture("roman"), Textures.getTexture("romanLFootUp"),
//                        Textures.getTexture("romanRFootUp"), Textures.getTexture("romanMelee"));
//                break;
//        }
    }

}
