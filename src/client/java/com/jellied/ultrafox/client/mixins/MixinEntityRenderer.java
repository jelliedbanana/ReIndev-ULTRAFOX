package com.jellied.ultrafox.client.mixins;

import net.minecraft.src.client.renderer.EntityRenderer;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.other.EntityDynamite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/Entity;canBeCollidedWith()Z"))
    public boolean collisionRedirect(Entity entity) {
        if (entity instanceof EntityDynamite) {
            return true;
        }
        return entity.canBeCollidedWith();
    }
}
