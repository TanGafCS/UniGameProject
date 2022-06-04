package src.Animations;

import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.Texture;
import src.Entities.*;
import src.GameView.GameTime;
import src.Sounds.Sounds;

import java.util.concurrent.ThreadLocalRandom;

public class Animations
{
    private Entity ent; // The entity these animations are bound to

    // Anims are the string keys that match the intended frame within textures.txt
    private Texture idleAnim;
    private Texture movAnim1;
    private Texture movAnim2;
    private Texture activeMovAnim;
    private Texture attackAnim;

    private final float frameLength = 0.10f;
    private float lastMoveFrameUpdate;
    private float lastAttackFrameUpdate;

    // Sound related
    private int cycleCount;
    private Sound footstep;

    public Animations(Texture idleAnim, Texture movAnim1, Texture movAnim2, Texture attackAnim, Entity ent)
    {
        this.idleAnim = idleAnim;
        this.movAnim1 = movAnim1;
        this.movAnim2 = movAnim2;
        this.activeMovAnim = movAnim1;
        this.attackAnim = attackAnim;
        this.ent = ent;
        this.cycleCount = 0;            // Number of cycles of animation that have passed

        this.lastMoveFrameUpdate = -10f;
        this.lastAttackFrameUpdate = -10f;

        // Sounds
        this.footstep = new Sound(Sounds.getSound("footstep"));
    }

    public void setLastUpdateTime()
    {
        this.lastMoveFrameUpdate = GameTime.getElapsedTime();
    }

    public void setLastAttackTime()
    {
        this.lastAttackFrameUpdate = GameTime.getElapsedTime();
    }

    public Texture getTexture(boolean isMoving)
    {
        // If attacking, return attacking anim.
        if (GameTime.getElapsedTime() >= lastAttackFrameUpdate && GameTime.getElapsedTime() <= lastAttackFrameUpdate + frameLength)
        {
            return attackAnim;
        }

        // If moving, return one of 2 moving frames.
        if (isMoving)
        {
            // if frameLength seconds has passed since this frame was first shown, cycle to next frame.
            if (GameTime.getElapsedTime() >= lastMoveFrameUpdate + frameLength)
            {
                setLastUpdateTime();
                cycleMovAnim();
            }

            return activeMovAnim;
        }

        // if not in a special animation, return idleFrame.
        return idleAnim;
    }

    private void cycleMovAnim()
    {
        if (activeMovAnim.equals(movAnim1))
        {
            activeMovAnim = movAnim2;
        }
        else
        {
            activeMovAnim = movAnim1;
        }

        cycleCount++;
        playFootsteps();
    }

    private void playFootsteps()
    {
        if (ent instanceof Player)
        {
            if (cycleCount % 2 == 0)
            {
                footstep.setPitch(EntityHelpers.randomiseWithinRange(0.7f, 1.1f));
                footstep.play();
            }
        }
    }
}
