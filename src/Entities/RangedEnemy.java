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

public class RangedEnemy extends Enemy {
    public RangedEnemy(Floor floor,Empire empire)
    {
        super(floor, floor.getEmpire());
        setHealth((int)(30 * EntityHelpers.empToDiff(empire)));
        shotCooldown = 1.75f / EntityHelpers.empToDiff(empire);
        projRange = 600;
        projSpeed = (int)(225 * EntityHelpers.empToDiff(empire));
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
        tryAttack(Enemy.AttackType.RANGED, true);
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
                this.animations = new Animations(Textures.getTexture("vikingRange"), Textures.getTexture("vikingRangeLFootUp"),
                        Textures.getTexture("vikingRangeRFootUp"), Textures.getTexture("vikingRangeAttack"), this);
                break;
            case EGYPTIAN:
                this.animations = new Animations(Textures.getTexture("egyptianRange"), Textures.getTexture("egyptianRangeLFootUp"),
                        Textures.getTexture("egyptianRangeRFootUp"), Textures.getTexture("egyptianRangeAttack"), this);
                break;
            case GREEK:
                this.animations = new Animations(Textures.getTexture("greekRange"), Textures.getTexture("greekRangeLFootUp"),
                        Textures.getTexture("greekRangeRFootUp"), Textures.getTexture("greekRangeAttack"), this);
                break;
            case ROMAN:
                this.animations = new Animations(Textures.getTexture("romanRange"), Textures.getTexture("romanRangeLFootUp"),
                        Textures.getTexture("romanRangeRFootUp"), Textures.getTexture("romanRangeAttack"), this);
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
