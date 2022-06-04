package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Paints the state of the game onto a renderingWindow with the paint() method.
 */
public class StatePainter {

    private Floor floor;
    private Shader entityShader;            // Shader used for entities
    private RenderStates entityRender;      // Render state set up with shader used for entities.


    public StatePainter(Floor floor)
    {
        this.floor = floor;

        // Instantiate shaders
        entityShader = new Shader();
        try
        {
            entityShader.loadFromFile(Paths.get(System.getProperty("user.dir") + "/src/Textures/shaders/entity.glsl"), Shader.Type.FRAGMENT);
        }
        catch (ShaderSourceException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        entityRender = new RenderStates(entityShader);
    }

    /**
     * Draw the floor and all entities on it to the target window.
     * @param targetWindow
     */
    public void paint(RenderWindow targetWindow)
    {
        ArrayList<Tile> tiles = floor.getTiles();
        
        for (Tile tile: tiles)
        {
            targetWindow.draw(tile.getDrawable());
        }

        ArrayList<Entity> allEntities = new ArrayList<>();
        allEntities.addAll(floor.getEntities());
        allEntities.add(floor.getPlayer());
        // Sort entities by desired painting order: if entity A is above entity B and identical, when overlapping
        // the desired behaviour is to have entity A, then B, being draw such that B appears to be "on top".
        // Sort allEntities in the order described above
        Collections.sort(allEntities);

        for (Entity ent: allEntities)
        {
            // Pass in entity params
            entityShader.setParameter("elapsedTime", GameTime.getElapsedTime());
            entityShader.setParameter("timeLastHurt", ent.getTimeLastHurt());
            entityShader.setParameter("isGlint", isGlint(ent));
            entityShader.setParameter("isPaletteSwapped", isPaletteSwapped(ent));
            targetWindow.draw(ent.getDrawable(), entityRender);
        }

        for (Drawable drawable: floor.getVisuals())
        {
            targetWindow.draw(drawable);
        }
        floor.getVisuals().clear();     // Clear after visuals have been drawn.
    }

    private float isGlint(Entity ent)
    {
        if (ent.getType() == Entity.Type.item)
            return 1f;
        return 0f;
    }

    private float isPaletteSwapped(Entity ent)
    {
        if (ent instanceof ChaserEnemy)
            return 1f;
        return 0f;
    }

    public void setFloor(Floor floor)
    {
        this.floor = floor;
    }


}
