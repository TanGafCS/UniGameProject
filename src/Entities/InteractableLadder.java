package src.Entities;

import org.jsfml.audio.Sound;
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

public class InteractableLadder extends Entity implements Interactable {

    private float spawnChaserTime;
    private boolean spawnedChaser;
    private boolean started;
    private Sound climbSound;

    public InteractableLadder(Floor floor, boolean spawnChasers)
    {
        super(floor);
        this.started = false;
        this.spawnedChaser = false;
        this.spawnChaserTime = GameTime.getElapsedTime() + EntityHelpers.getChaserTime();
        if (!spawnChasers) this.spawnChaserTime = Float.MAX_VALUE;

        this.climbSound = new Sound(Sounds.getSound("climb"));
        climbSound.setPitch(EntityHelpers.randomiseWithinRange(0.85f,1.0f));
        climbSound.setVolume(30);
        setEmpire(getFloor().getEmpire());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (!started)
        {
            started = true;
            spawnChaserTime = GameTime.getElapsedTime() + EntityHelpers.getChaserTime();
        }
        if (GameTime.getElapsedTime() >= spawnChaserTime && spawnedChaser == false )
        {
            this.spawnChaserTime = GameTime.getElapsedTime() + EntityHelpers.getChaserTime();
            ChaserEnemy chaser = new ChaserEnemy(getFloor(), Empire.NONE);
            chaser.setPos(this.getPos());
            getFloor().registerEntity(chaser);
        }
    }

    public void onInstantiate()
    {
        ;
    }

    public void onInteract(Entity interactingEnt)
    {
        if (interactingEnt == getFloor().getPlayer())
        {
            getFloor().getPlayer().getGameView().advanceFloor();
            climbSound.play();
        }
    }

    public boolean isAutoInteractable()
    {
        return false;
    }

    public Drawable getDrawable()
    {
        String ladderTexture = "";
        switch (getEmpire())
        {
            case NONE:
                ladderTexture = "ladderTile1";
                break;
            case VIKING:
                ladderTexture = "vikingLadder";
                break;
            case EGYPTIAN:
                ladderTexture = "egyptianLadder";
                break;
            case GREEK:
                ladderTexture = "greekLadder";
                break;
            case ROMAN:
                ladderTexture = "romanLadder";
                break;
        }
        Sprite s = new Sprite(Textures.getTexture(ladderTexture));//declare new sprite
        s.setPosition(getPos());
        s.scale(2f,2f);
        return s;
    }

    public float getPosYBottom()
    {
        // Give this a negative value, as we always want it to be drawn in the background.
        return -1001;
    }

    public Type getType()
    {
        return Type.fixed;
    }
}
