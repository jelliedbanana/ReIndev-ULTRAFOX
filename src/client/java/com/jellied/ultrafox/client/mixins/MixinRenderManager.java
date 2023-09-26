package com.jellied.ultrafox.client.mixins;

import com.jellied.ultrafox.entity.EntityDynamiteSkeleton;
import com.jellied.ultrafox.renderer.entity.RenderDynamiteSkeleton;
import net.minecraft.src.client.model.ModelSkeleton;
import net.minecraft.src.client.renderer.entity.Render;
import net.minecraft.src.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RenderManager.class)
public class MixinRenderManager {
    @Shadow private Map<Class<?>, Render> entityRenderMap;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        Render dynamiteSkeletonRenderer = new RenderDynamiteSkeleton(new ModelSkeleton(), 0.5F);
        this.entityRenderMap.put(EntityDynamiteSkeleton.class, dynamiteSkeletonRenderer);
        dynamiteSkeletonRenderer.setRenderManager((RenderManager) (Object) this);
    }
}
