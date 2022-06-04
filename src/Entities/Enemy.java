package src.Entities;

import org.jsfml.system.Vector2f;
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

public abstract class Enemy extends Entity{

    public enum AttackType
    {
        MELEE,
        RANGED
    }

    private Floor floor;            // The floor this entity is on.
    private float aggroRange;

    public Enemy(Floor floor, Empire empire)
    {
        super(floor);
        this.floor = floor;
        this.aggroRange = 600;
        setEmpire(empire);
        setRectOffsetY(64);
        moveRectToPos();

        setHealth(5);
        this.shotCooldown = 0.6f;
    }

    public void setAggroRange(Float aggroRange)
    {
        this.aggroRange = aggroRange;
    }

    public boolean damageable()
    {
        return true;
    }

    public void update(float deltaTime)
    {
        // Don't update if staggered
        if (GameTime.getElapsedTime() - getTimeLastHurt() < getStaggerTime()) return;

        moveRectToPos();
        if (VectorHelpers.getDistance(getPos(), floor.getPlayer().getPos()) < aggroRange)
        {
            moveTowardsEntity(deltaTime, floor.getPlayer(), true);
        }
        else
        {
            setVel(new Vector2f(0,0));
        }
    }

    /**
     * Attack the player, if possible.
     */
    public void tryAttack(AttackType attack, boolean isAccurate)
    {
        float effectiveRange = 0;
        if (attack == AttackType.MELEE) effectiveRange = meleeRange;
        if (attack == AttackType.RANGED) effectiveRange = projRange;

        if (effectiveRange > aggroRange)
            effectiveRange = aggroRange;

        if (VectorHelpers.getDistance(getCentralPos(), floor.getPlayer().getCentralPos()) <= effectiveRange)
        {
            if (GameTime.getElapsedTime() - getTimeLastShot() > getShotCooldown())
            {
                setTimeLastShot(GameTime.getElapsedTime());

                Player player = floor.getPlayer();

                if (isAccurate)
                {
                    switch (attack)
                    {
                        case MELEE:
                            new Projectile(getFloor(), this, this.getCentralPos(), player.getCentralPos(), meleeSpeed, meleeRange, 1, getDamage());
                            animations.setLastAttackTime();
                            break;
                        case RANGED:
                            new Projectile(getFloor(), this, this.getCentralPos(), player.getCentralPos(), getProjSpeed(), getProjRange(), 0, getDamage());
                            animations.setLastAttackTime();
                            break;
                    }
                }
                else
                {
                    switch (attack)
                    {
                        case MELEE:
                            new Projectile(getFloor(), this, this.getCentralPos(), player.getCentralPos(), meleeSpeed, meleeRange, 1, getDamage());
                            animations.setLastAttackTime();
                            break;
                        case RANGED:
                            new Projectile(getFloor(), this, this.getCentralPos(), EntityHelpers.getVariedDestination(this.getCentralPos(), player.getCentralPos()), getProjSpeed(), getProjRange(), 0, getDamage());
                            animations.setLastAttackTime();
                            break;
                    }
                }


            }
        }
    }

    public void setAggroRange(float aggroRange)
    {
        this.aggroRange = aggroRange;
    }

    public Drawable getDrawable()
    {
        Texture t = Textures.getTexture("roman");

        Sprite sprite = new Sprite(t);//declare new sprite
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(2,2);

        return sprite;
    }
}
