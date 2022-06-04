package src.GameView;

import src.DungeonGen.*;
import src.Textures.*;
import src.Sounds.*;
import src.LaunchGUI.*;
import src.GameView.*;
import src.Entities.*;
import src.Modifiers.*;
import org.jsfml.graphics.*;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Image;
import org.jsfml.system.Vector2f;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

public class GuiPainter {

    RectangleShape rectGui;
    Font font = Textures.getFont();
    Texture heartTexture = new Texture();
    Sprite heartSpr = new Sprite();
    Texture denariTexture = new Texture();
    Sprite denariSpr = new Sprite();
    Texture bombsTexture = new Texture();
    Sprite bombsSpr = new Sprite();
    Texture barrelTexture = new Texture();
    Sprite barrelSpr = new Sprite();
    Texture armourTexture = new Texture();
    Sprite armourSpr = new Sprite();

    Texture eyeTexture = new Texture();
    Sprite eyeSpr = new Sprite();
    Texture coinTexture = new Texture();
    Sprite coinSpr = new Sprite();
    Texture gPillTexture = new Texture();
    Sprite gPillSpr = new Sprite();
    Texture bPillTexture = new Texture();
    Sprite bPillSpr = new Sprite();
    Texture sandalsTexture = new Texture();
    Sprite sandalsSpr = new Sprite();

    Text healthTxt;
    Text denariTxt;
    Text bombsTxt;
    Text barrelTxt;
    Text armourTxt;
    Text healthValue;
    Text denariValue;
    Text bombsValue;
    Text barrelValue;
    Text armourValue;
    int health = 0;
    int money = 0;
    int bombs = 0;
    int armour = 0;
    int barrels = 0;

    Text playerAttackTxt;
    Text playerSpeedTxt;
    Text playerMoveTxt;
    Text playerRangeTxt;

    Text playerAttackValue;
    Text playerSpeedValue;
    Text playerMoveValue;
    Text playerRangeValue;

    int position = 0;

    private Vector2f topLeft;       // Position of the top left of the screen in world-space. Used to position GUI.
    private Player player;
    private RenderWindow targetWindow;


    public GuiPainter(RenderWindow targetWindow, Vector2f topLeft, Player player) {

        this.targetWindow = targetWindow;
        // Change texture loads to new method, and REMOVE the throws IOExceptions, we're bubbling up these exceptions unnecessarily all the way to the main method
        this.topLeft = topLeft;
        font = Textures.getFont();
        heartTexture = Textures.getTexture("heart");
        heartSpr.setTexture(heartTexture);
        heartSpr.scale(2,2);
        heartSpr.setPosition(topLeft.x, topLeft.y + 5);
        denariTexture = Textures.getTexture("denari");
        denariSpr.setTexture(denariTexture);
        denariSpr.scale(2,2);
        denariSpr.setPosition(topLeft.x, topLeft.y + 40);
        bombsTexture = Textures.getTexture("bomb");
        bombsSpr.setTexture(bombsTexture);
        bombsSpr.scale(2,2);
        bombsSpr.setPosition(topLeft.x, topLeft.y + 75);
        armourTexture = Textures.getTexture("armour");
        armourSpr.setTexture(armourTexture);
        armourSpr.scale(2,2);
        armourSpr.setPosition(topLeft.x, topLeft.y + 110);
        barrelTexture = Textures.getTexture("barrel");
        barrelSpr.setTexture(barrelTexture);
        barrelSpr.setPosition(topLeft.x + 320, topLeft.y + 5);

        this.player = player;

        healthTxt = new Text("Health:", font);
        denariTxt = new Text("Denari:", font);
        bombsTxt = new Text("Bombs:", font);
        barrelTxt = new Text("Barrels:", font);
        armourTxt = new Text ("Armour:", font);

        healthValue = new Text("",font);
        denariValue = new Text("",font);
        bombsValue = new Text("",font);
        barrelValue = new Text("",font);
        armourValue = new Text("",font);

        healthValue.setString(Integer.toString(player.getHealth()));
        denariValue.setString(Integer.toString(player.getCoins()));
        bombsValue.setString(Integer.toString(player.getBombs()));
        barrelValue.setString(Integer.toString(player.getBarrels()));
        armourValue.setString(Integer.toString(player.getArmour()));

        healthTxt.setPosition(topLeft.x + 30, topLeft.y + 5);
        denariTxt.setPosition(topLeft.x + 30, topLeft.y + 40);
        bombsTxt.setPosition(topLeft.x + 30, topLeft.y + 75);
        armourTxt.setPosition(topLeft.x + 30, topLeft.y + 110);
        barrelTxt.setPosition(topLeft.x + 350, topLeft.y + 5);

        healthValue.setPosition(topLeft.x + 200, topLeft.y + 5);
        denariValue.setPosition(topLeft.x + 200, topLeft.y + 40);
        bombsValue.setPosition(topLeft.x + 200, topLeft.y + 75);
        armourValue.setPosition(topLeft.x + 200, topLeft.y + 110);
        barrelValue.setPosition(topLeft.x + 550, topLeft.y + 5);

        playerAttackTxt = new Text("Attack:", font);
        playerSpeedTxt = new Text("Fire rate:", font);
        playerMoveTxt = new Text("Speed:", font);
        playerRangeTxt = new Text("Range:", font);
        playerAttackValue = new Text("", font);
        playerSpeedValue = new Text("", font);
        playerMoveValue = new Text("", font);
        playerRangeValue = new Text("", font);

        playerAttackValue.setString(Integer.toString(player.getDamage()));
        playerSpeedValue.setString(String.format("%.2f", player.getShotCooldown()));
        playerMoveValue.setString(String.format("%.2f", player.getMovementSpeed()));
        playerRangeValue.setString(Integer.toString((int)player.getProjRange()));

        playerAttackTxt.setPosition(topLeft.x + 1400, topLeft.y + 5);
        playerSpeedTxt.setPosition(topLeft.x + 1400, topLeft.y + 40);
        playerMoveTxt.setPosition(topLeft.x + 1400, topLeft.y + 75);
        playerRangeTxt.setPosition(topLeft.x + 1400, topLeft.y + 110);

        playerAttackValue.setPosition(topLeft.x + 1650, topLeft.y + 5);
        playerSpeedValue.setPosition(topLeft.x + 1650, topLeft.y + 40);
        playerMoveValue.setPosition(topLeft.x + 1650, topLeft.y + 75);
        playerRangeValue.setPosition(topLeft.x + 1650, topLeft.y + 110);

        targetWindow.draw(heartSpr);
        targetWindow.draw(denariSpr);
        targetWindow.draw(bombsSpr);
        targetWindow.draw(armourSpr);
        targetWindow.draw(barrelSpr);
        targetWindow.draw(healthTxt);
        targetWindow.draw(denariTxt);
        targetWindow.draw(bombsTxt);
        targetWindow.draw(barrelTxt);
        targetWindow.draw(armourTxt);
        targetWindow.draw(healthValue);
        targetWindow.draw(denariValue);
        targetWindow.draw(bombsValue);
        targetWindow.draw(armourValue);
        targetWindow.draw(barrelValue);
        targetWindow.draw(playerAttackTxt);
        targetWindow.draw(playerSpeedTxt);
        targetWindow.draw(playerMoveTxt);
        targetWindow.draw(playerRangeTxt);
        targetWindow.draw(playerAttackValue);
        targetWindow.draw(playerSpeedValue);
        targetWindow.draw(playerMoveValue);
        targetWindow.draw(playerRangeValue);

        displayModifiers();
        displayFloor();
    }

    int getHealth()
    {
        return health;
    }
    void addHealth (int amount)
    {
        health = health + amount;
    }
    void removeHealth(int amount)
    {
        health = health - amount;
    }

    int getMoney()
    {
        return money;
    }
    void addMoney(int amount)
    {
        money = money + amount;
    }
    void removeMoney(int amount)
    {
        money = money - amount;
    }

    void addBombs(int amount)
    {
        bombs = bombs + amount;
    }
    void removeBombs(int amount)
    {
        bombs = bombs - amount;
    }
    int getBombs()
    {
        return bombs;
    }

    void addBarrels(int amount)
    {
        barrels = barrels + amount;
    }
    void removeBarrels(int amount)
    {
        barrels = barrels - amount;
    }
    int getBarrels()
    {
        return barrels;
    }

    int getArmour(){return armour;}
    void addArmour(int amount){armour = armour + amount;}
    void removeArmour(int amount){armour = armour - amount;}

    void displayModifiers()
    {
        int modifierCount = player.getModifiers().size();
        float modifiersWidth = modifierCount * 64;
        float xPos = topLeft.x + 1920/2;
        float yPos = topLeft.y + 110;
        float modifierXGap = 80;
        Vector2f baseModifierPos = new Vector2f(xPos, yPos);

        // Draw modifiers and levels
        for (int i = 0; i < 3; i++)
        {
            Modifier mod = player.getModifier(i);
            if (mod instanceof ModifierNull) continue;
            Vector2f modifierPos = new Vector2f(baseModifierPos.x - (modifiersWidth/2) + modifierXGap * i, + baseModifierPos.y);
            Vector2f levelPos = new Vector2f((baseModifierPos.x - (modifiersWidth/2) + modifierXGap * i) - 4, + baseModifierPos.y - 20);

            // Draw "highlight" square behind selected modifier
            if (player.getSelectedModifierIndex() == i + 1)
            {
                RectangleShape highlightRect = new RectangleShape(new Vector2f(40f, 40f));
                highlightRect.setFillColor(new Color(255, 255, 255, 110));
                highlightRect.setPosition(baseModifierPos.x - (modifiersWidth/2) - 4 + modifierXGap * i, + baseModifierPos.y - 4);
                targetWindow.draw(highlightRect);
            }

            // Draw modifiers and levels
            Sprite s = (Sprite) mod.getDrawable();
            s.setPosition(modifierPos);
            Text levelText = new Text(mod.getLevel(), font);
            levelText.scale(0.5f, 0.5f);
            levelText.setPosition(levelPos);
            targetWindow.draw(s);
            targetWindow.draw(levelText);
        }

    }

    void displayFloor()
    {
        float xPos = topLeft.x + 1920/2;
        float yPos = topLeft.y + 50;
        float floorNumberGap = 25;              // Gap between the stage name and floor number.
        Vector2f basePos = new Vector2f(xPos, yPos);

        String empireText = "";
        Empire emp = player.getFloor().getEmpire();

        switch(emp) {
            case EGYPTIAN:
                empireText = "Egyptians";
                break;
            case GREEK:
                empireText = "Greeks";
                break;
            case ROMAN:
                empireText = "Romans";
                break;
            case VIKING:
                empireText = "Vikings";
                break;
            default:
                empireText = "Tutorial";
        }

        Text stageTxt = new Text(empireText, font);

        float xGap = stageTxt.getGlobalBounds().width + floorNumberGap;  // xGap = number of chars printed + 1 * character width

        String floorNum = String.valueOf(player.getFloor().getFloorNum());
        Text floorNumTxt = new Text(floorNum, font);
        floorNumTxt.setPosition(xPos - (stageTxt.getGlobalBounds().width / 2) - (floorNumTxt.getGlobalBounds().width / 2) - (floorNumberGap / 2) + xGap, yPos);
        targetWindow.draw(floorNumTxt);
        stageTxt.setPosition(xPos - (stageTxt.getGlobalBounds().width / 2) - (floorNumTxt.getGlobalBounds().width / 2) - (floorNumberGap / 2) , yPos);
        targetWindow.draw(stageTxt);
    }
}
