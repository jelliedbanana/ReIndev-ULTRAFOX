package com.jellied.ultrafox.client.mixins;

import com.jellied.ultrafox.MinecraftAccessor;
import com.jellied.ultrafox.gui.GuiFreezeFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.src.client.renderer.Vec3D;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft implements MinecraftAccessor {
    @Shadow public volatile boolean isGamePaused;

    @Shadow public World theWorld;
    int freezeFrameTicks = 0;
    GuiFreezeFrame freezeFrame = new GuiFreezeFrame();

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/client/renderer/Vec3D;initialize()V"))
    public void onTickBegin() {
        Vec3D.initialize();
        isGamePaused = isGamePaused || (freezeFrameTicks > 0 && !theWorld.multiplayerWorld);
        if (freezeFrameTicks > 0) {
            freezeFrame.drawScreen(0, 0, 0);
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
    void drawFreezeFrame(CallbackInfo ci) {
        if (freezeFrameTicks > 0) {
            freezeFrameTicks--;
        }
    }

    @Override
    public int getFreezeFrameTicks() {
        return freezeFrameTicks;
    }

    @Override
    public void incrementFreezeFrameTicks(int amount) {
        freezeFrameTicks += amount;
        freezeFrame.setDimensions((Minecraft) (Object) this);
    }
}
