package src.Entities;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import src.Animations.*;

public class ChaserEnemy extends RangedEnemy {

    public ChaserEnemy(Floor floor,Empire empire)
    {
        super(floor, empire);
        setHealth((int)(150 * EntityHelpers.empToDiff(empire)));
        shotCooldown = 1f / EntityHelpers.empToDiff(empire);
        moveSpeed = getFloor().getPlayer().getMaxVel() + 25;
        this.coins = (int)(2 * EntityHelpers.empToDiff(empire));
        this.baseDamage = 3;
        setAnimations();
    }

    @Override
    public void update(float deltaTime)
    {
        moveRectToPos();
        moveTowardsEntity(deltaTime, getFloor().getPlayer(), false);
        tryAttack(Enemy.AttackType.MELEE, true);
    }

    @Override
    public void setAnimations()
    {
        this.animations = new Animations(Textures.getTexture("player"), Textures.getTexture("playerLFootUp"),
                Textures.getTexture("playerRFootUp"), Textures.getTexture("playerMelee1"), this);
    }

    @Override
    public Drawable getDrawable()
    {
        Texture t = animations.getTexture(isMoving());

        Sprite sprite = new Sprite(t);
        sprite.setPosition(getPosX(), getPosY());
        sprite.scale(2,2);

        return sprite;
    }
}
