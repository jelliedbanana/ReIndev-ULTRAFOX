package com.jellied.ultrafox.client.mixins;

import com.jellied.ultrafox.DynamiteAccessor;
import com.jellied.ultrafox.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.fox2code.ChatColors;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.entity.other.EntityDynamite;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity  {
    @Shadow public World worldObj;

    @Shadow public abstract boolean attackEntityFrom(Entity entity, int arg2);

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    public void attackEntityFrom(Entity attacker, int damage, CallbackInfoReturnable<Boolean> cir) {
        Entity actuallyThis = (Entity) (Object) this;

        if (!(actuallyThis instanceof EntityDynamite) || attacker == null) {
            return;
        }

        EntityDynamite tnt = (EntityDynamite) actuallyThis;
        ((DynamiteAccessor) tnt).resetTicksInAir();

        // +PARRY

        float pi = 3.1415927F;

        float pitch = attacker.rotationPitch;
        float yaw = attacker.rotationYaw;

        double newPosX = attacker.posX - (MathHelper.cos(yaw / 180 * pi) * 0.16F);
        double newPosY = attacker.posY - 0.1D;
        double newPosZ = attacker.posZ - (MathHelper.sin(yaw / 180 * pi) * 0.16F);

        tnt.setPositionAndRotation(newPosX, newPosY, newPosZ, yaw, pitch);

        tnt.motionX = (-MathHelper.sin(yaw / 180F * pi) * MathHelper.cos(pitch / 180F * pi));
        tnt.motionZ = (MathHelper.cos(yaw / 180F * pi) * MathHelper.cos(pitch / 180F * pi));
        tnt.motionY = -MathHelper.sin(pitch / 180F * pi);
        (tnt).setFireworkHeading(tnt.motionX, tnt.motionY, tnt.motionZ, 4F, 1.0F);

        ((DynamiteAccessor) tnt).setIsBoosted(true);
        ((MinecraftAccessor) Minecraft.theMinecraft).incrementFreezeFrameTicks(20 / 4);

        worldObj.playSoundAtEntity(attacker, "random.clang", 3.0F, 1.0F / (worldObj.rand.nextFloat() * 0.2F + 0.4F));

        if (((DynamiteAccessor) tnt).getThrower() != attacker) {
            worldObj.playSoundAtEntity(attacker, "random.levelup", 2.0F, 1.0F / (worldObj.rand.nextFloat() * 0.3F + 0.6F));
            ((DynamiteAccessor) tnt).setThrower((EntityLiving) attacker);
            ((DynamiteAccessor) tnt).setIsScoreAwarded();

            ((EntityLiving) attacker).heal(20 - ((EntityLiving) attacker).health);
            ((EntityPlayer) attacker).addChatMessage(ChatColors.GREEN + "+PARRY");
            attacker.addToPlayerScore(null, 10);
        }
    }
}
