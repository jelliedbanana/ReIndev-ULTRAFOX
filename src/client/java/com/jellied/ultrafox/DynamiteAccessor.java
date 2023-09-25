package com.jellied.ultrafox;

import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.entity.other.EntityDynamite;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface DynamiteAccessor {
    public EntityLiving getThrower();
    public void setThrower(EntityLiving newThrower);

    public boolean isBoosted();
    public void setIsBoosted(boolean newIsBoosted);

    public void resetTicksInAir();
    public void setIsScoreAwarded();
}
