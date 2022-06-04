package src.Entities;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import src.Animations.*;

public class MeleeEnemy extends Enemy {

    public MeleeEnemy(Floor floor,Empire empire)
    {
        super(floor, empire);
        setHealth((int)(50 * EntityHelpers.empToDiff(empire)));
        shotCooldown = 1f / EntityHelpers.empToDiff(empire);
        setAnimations();
    }

    @Override
    public void update(float deltaTime)
    {
        if (VectorHelpers.getDistance(getCentralPos(), getFloor().getPlayer().getCentralPos()) > 60)
        {
            super.update(deltaTime);
        }
        tryAttack(Enemy.AttackType.MELEE, true);
    }

    @Override
    public void setAnimations()
    {
        switch (getEmpire())
        {
            case NONE:
                this.animations = new Animations(Textures.getTexture("roman"), Textures.getTexture("romanLFootUp"),
                        Textures.getTexture("romanRFootUp"), Textures.getTexture("romanMelee"), this);
                break;
            case VIKING:
                this.animations = new Animations(Textures.getTexture("viking"), Textures.getTexture("vikingLFootUp"),
                        Textures.getTexture("vikingRFootUp"), Textures.getTexture("vikingMelee"), this);
                break;
            case EGYPTIAN:
                this.animations = new Animations(Textures.getTexture("egyptian"), Textures.getTexture("egyptianLFootUp"),
                        Textures.getTexture("egyptianRFootUp"), Textures.getTexture("egyptianMelee"), this);
                break;
            case GREEK:
                this.animations = new Animations(Textures.getTexture("greek"), Textures.getTexture("greekLFootUp"),
                        Textures.getTexture("greekRFootUp"), Textures.getTexture("greekMelee"), this);
                break;
            case ROMAN:
                this.animations = new Animations(Textures.getTexture("roman"), Textures.getTexture("romanLFootUp"),
                        Textures.getTexture("romanRFootUp"), Textures.getTexture("romanMelee"), this);
                break;
//            case NONE:
//                this.animations = new Animations(Textures.getTexture("roman"), Textures.getTexture("romanLFootUp"),
//                        Textures.getTexture("romanRFootUp"), Textures.getTexture("romanMelee"));
//                break;
        }
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
