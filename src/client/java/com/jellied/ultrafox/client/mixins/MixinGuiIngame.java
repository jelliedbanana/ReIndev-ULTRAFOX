package com.jellied.ultrafox.client.mixins;

import net.minecraft.src.client.gui.GuiIngame;
import net.minecraft.src.game.stats.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Redirect(method = "drawScoreAndDebug", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/stats/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;"))
    public String onTranslate(String input) {
        if (input.equals("deathScreen.score")) {
            return "STYLE: ";
        }

        return StatCollector.translateToLocal(input);
    }
}
