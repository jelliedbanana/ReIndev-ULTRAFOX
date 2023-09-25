package com.jellied.ultrafox.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.client.gui.GuiScreen;
import net.minecraft.src.client.gui.ScaledResolution;

public class GuiFreezeFrame extends GuiScreen {
    public GuiFreezeFrame() {
        this.allowUserInput = true;
    }

    public void setDimensions(Minecraft minecraft) {
        ScaledResolution scaledDisplay = ScaledResolution.instance;
        scaledDisplay.setDimensions(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight);
        setWorldAndResolution(minecraft, scaledDisplay.getScaledWidth(), scaledDisplay.getScaledHeight());
    }

    public void drawScreen(int var1, int var2, float dt) {
        drawGradientRect(0, 0, this.width, this.height, 1073741823, 1073741823);
    }

    public void updateScreen() {
        return;
    }

    public void handleMouseInput() {
        return;
    }

    public void handleKeyboardInput() {
        return;
    }

    protected void keyTyped(char eventChar, int eventKey) {
        return;
    }
}
