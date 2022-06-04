package src.Modifiers;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

public abstract class Modifier{

    private static final int MODIFIER_SIZE = 64;

    private float movementSpeed;
    private float attackSpeed;
    private float damage;
    private float projectileSpeed;
    private float multiplier;

    private int xLocation;//unsure if we are loading entities on tiles or on x/y grid
    private int yLocation;
    private Texture t;
    protected Sprite s;
    private IntRect rect;

    public Modifier(float mov, float att, float dmg, float projSpd){
        this.multiplier = 1f;
        this.movementSpeed = mov;
        this.attackSpeed = att;
        this.damage = dmg;
        this.projectileSpeed = projSpd;
        Vector2i size = new Vector2i(MODIFIER_SIZE,MODIFIER_SIZE);
        Vector2i location = new Vector2i(xLocation, yLocation);
        this.rect = new IntRect(location,size);
    }

    public float get_movementSpeed() {return movementSpeed * multiplier;}
    public float get_attackSpeed() {return attackSpeed * multiplier;}
    public float get_damage() {return damage * multiplier;}
    public float get_projectileSpeed() {return projectileSpeed * multiplier;}
    public int get_xLocation() { return xLocation;}
    public int get_yLocation() { return yLocation;}
    public Vector2f getPos()
    {
        return new Vector2f(xLocation, yLocation);
    }
    public void setPos(Vector2f v)
    {
        xLocation = (int)v.x;
        yLocation = (int)v.y;
    }

    public void setMultiplier(float multiplier)
    {
        this.multiplier = multiplier;
    }

    public Drawable getDrawable(){
        s = new Sprite(Textures.getTexture("undefined"));//declare new sprite
        s.setPosition(getPos());
        //s.scale(2f,2f);
        return s;
    }

    public String getLevel()
    {
        String returnStr = "Lv ";
        String number = "";
        if (multiplier >= 2.999f)
        {
            number = "5";
        }
        else if (multiplier >= 2.499f)
        {
            number = "4";
        }
        else if (multiplier >= 1.999f)
        {
            number = "3";
        }
        else if (multiplier >= 1.499f)
        {
            number = "2";
        }
        else if (multiplier >= 0.999f)
        {
            number = "1";
        }

        returnStr += number;
        return returnStr;
    }
}