package com.jellied.ultrafox.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.client.gui.GuiScreen;
import net.minecraft.src.client.gui.ScaledResolution;

public class GuiFreezeFrame extends GuiScreen {
    public boolean allowUserInput = true;
    final int WHITE = (255/4) << 24 | 255 << 16 | 255 << 8 | 255;
    // I have no fucking clue what bit shifting is or how it works
    // But from reading the source code I found that with the above algorithm,
    // you can plug in an alpha, red, green, and blue value and get some random number
    // which can then be bit shifted back into the original rgba values
    // again, no idea how, but it works

    public GuiFreezeFrame() {}

    public void setDimensions(Minecraft minecraft) {
        ScaledResolution scaledDisplay = ScaledResolution.instance;
        scaledDisplay.setDimensions(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight);
        setWorldAndResolution(minecraft, scaledDisplay.getScaledWidth(), scaledDisplay.getScaledHeight());
    }

    public void drawScreen(int var1, int var2, float dt) {
        drawGradientRect(0, 0, this.width, this.height, WHITE, WHITE);
    }

    // A bunch of empty methods because otherwise an exception is thrown.
    public void updateScreen() {}

    public void handleMouseInput() {}

    public void handleKeyboardInput() {}

    protected void keyTyped(char eventChar, int eventKey) {}
}
