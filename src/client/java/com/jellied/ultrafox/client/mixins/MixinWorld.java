package com.jellied.ultrafox.client.mixins;

import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.monster.EntitySkeleton;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(World.class)
public class MixinWorld {
    @Shadow public Random rand;

    @Inject(method = "entityJoinedWorld", at = @At("HEAD"))
    public void onEntityJoined(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof EntitySkeleton && rand.nextInt(2) == 1) {

        }
    }
}
