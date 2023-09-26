package com.jellied.ultrafox.client.mixins;

import com.jellied.ultrafox.entity.EntityDynamiteSkeleton;
import net.minecraft.src.game.entity.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(EntityList.class)
public class MixinEntityList {
    @Redirect(method = "createEntityFromNBT", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object getEntityClass(Map instance, Object o) {
        // Again, there's no EntityDynamiteSkeleton entry in the entity list map

        if (o.equals("DynamiteSkeleton")) {
            return EntityDynamiteSkeleton.class;
        }

        return instance.get(o);
    }
}
