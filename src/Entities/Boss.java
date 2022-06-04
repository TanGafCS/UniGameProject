package src.Entities;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import src.Animations.Animations;
import src.DungeonGen.Floor;
import src.Textures.Textures;

public class Boss extends Enemy
{
    public Boss(Floor floor)
    {
        super(floor, floor.getEmpire());

        // set hitbox
        Vector2i size = new Vector2i(128, 192);
        this.rect = new IntRect(new Vector2i(getPos()), size);

        setHealth((int)(300 * EntityHelpers.empToDiff(getEmpire())));
        shotCooldown = 0.4f / EntityHelpers.empToDiff(getEmpire());
        projRange = 1200;
        projSpeed = (int)(300 * EntityHelpers.empToDiff(getEmpire()));
        coins = (int)(5 * EntityHelpers.empToDiff(getEmpire()));
        setAggroRange(5000f);
        setAnimations();
    }

    @Override
    public void update(float deltaTime)
    {
        // Only move towards the player if not already too close.
        if (VectorHelpers.getDistance(getCentralPos(), getFloor().getPlayer().getCentralPos()) > 240)
        {
            super.update(deltaTime);
        }
        else
        {
            setVel(new Vector2f(0f,0f));
        }
        tryAttack(Enemy.AttackType.RANGED, false);
    }

    @Override
    public void die() {
        super.die();
        // When the boss dies, spawn a ladder so the player can progress.
        spawnLadder();
    }

    private void spawnLadder()
    {
        InteractableLadder ladder = new InteractableLadder(getFloor(), false);
        ladder.setPos(new Vector2f(getCentralPos().x, getPosYBottom()));
        ladder.setEmpire(Empire.NONE);
        getFloor().registerEntity(ladder);
    }

    @Override
    public void setAnimations()
    {
        switch (getEmpire())
        {
            case NONE:
                this.animations = new Animations(Textures.getTexture("romanBoss"), Textures.getTexture("romanBossLFootUp"),
                        Textures.getTexture("romanBossRFootUp"), Textures.getTexture("romanBossMelee"), this);
                break;
            case VIKING:
                this.animations = new Animations(Textures.getTexture("vikingBoss"), Textures.getTexture("vikingBossLFootUp"),
                        Textures.getTexture("vikingBossRFootUp"), Textures.getTexture("vikingBossMelee"), this);
                break;
            case EGYPTIAN:
                this.animations = new Animations(Textures.getTexture("egyptianBoss"), Textures.getTexture("egyptianBossLFootUp"),
                        Textures.getTexture("egyptianBossRFootUp"), Textures.getTexture("egyptianBossRange"), this);
                break;
            case GREEK:
                this.animations = new Animations(Textures.getTexture("greekBoss"), Textures.getTexture("greekBossLFootUp"),
                        Textures.getTexture("greekBossRFootUp"), Textures.getTexture("greekBossRange"), this);
                break;
            case ROMAN:
                this.animations = new Animations(Textures.getTexture("romanBoss"), Textures.getTexture("romanBossLFootUp"),
                        Textures.getTexture("romanBossRFootUp"), Textures.getTexture("romanBossMelee"), this);
                break;
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
}
