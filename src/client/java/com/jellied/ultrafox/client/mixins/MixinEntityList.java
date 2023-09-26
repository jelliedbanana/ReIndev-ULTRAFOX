package com.jellied.ultrafox.client.mixins;

import com.jellied.ultrafox.entity.EntityDynamiteSkeleton;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.EntityList;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityList.class)
public class MixinEntityList {
    @Inject(method = "createEntityFromNBT", at = @At("HEAD"), cancellable = true)
    private static void getEntityClass(NBTTagCompound nbt, World world, CallbackInfoReturnable<Entity> cir) {
        if (nbt.hasKey("id") && nbt.getString("id").equals("DynamiteSkeleton")) {
            EntityDynamiteSkeleton skeleton = new EntityDynamiteSkeleton(world);
            skeleton.readFromNBT(nbt);

            cir.setReturnValue(skeleton);
            cir.cancel();
        }
    }
}
