package com.jellied.ultrafox.client.mixins;

import java.util.List;

import com.jellied.ultrafox.DynamiteAccessor;
import net.minecraft.fox2code.ChatColors;
import net.minecraft.src.client.physics.AxisAlignedBB;
import net.minecraft.src.client.physics.MovingObjectPosition;
import net.minecraft.src.client.renderer.Vec3D;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.entity.other.EntityDynamite;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.Explosion;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.level.features.WorldGenFireMolotov;
import net.minecraft.src.game.nbt.NBTTagCompound;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityDynamite.class)
public class MixinEntityDynamite implements DynamiteAccessor {
    @Shadow private int ticksInAirFirework = 0;
    public EntityLiving thrower;
    public boolean isBoosted = false;
    public boolean isScoreAwarded = false;
    public boolean hasExploded = false;

    @Inject(method = "<init>(Lnet/minecraft/src/game/level/World;Lnet/minecraft/src/game/entity/EntityLiving;)V", at = @At("TAIL"))
    public void onInit(World world, EntityLiving owner, CallbackInfo ci) {
        thrower = owner;
        if (owner.fire > 0) {
            // DESTRUCTIVE
            ((EntityDynamite) (Object) this).fire = 300;
        }
    }

    @Redirect(method = "<init>(Lnet/minecraft/src/game/level/World;Lnet/minecraft/src/game/entity/EntityLiving;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/other/EntityDynamite;setSize(FF)V"))
    public void redirectSize(EntityDynamite instance, float width, float height) {
        instance.width = 0.75F;
        instance.height = 0.75F;
    }

    @Override
    public EntityLiving getThrower() {
        return thrower;
    }

    @Override
    public void setThrower(EntityLiving newThrower) {
        thrower = newThrower;
    }

    @Override
    public boolean isBoosted() {
        return isBoosted;
    }

    @Override
    public void setIsBoosted(boolean newIsBoosted) {
        isBoosted = newIsBoosted;
    }

    @Override
    public void resetTicksInAir() {
        ticksInAirFirework = 0;
    }

    @Override
    public void setIsScoreAwarded() {
        isScoreAwarded = true;
    }

    @Redirect(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;spawnParticle(Ljava/lang/String;DDDDDD)V"))
    public void onParticleSpawn(World world, String particleName, double x, double y, double z, double arg1, double arg2, double arg3) {
        if (particleName == "smoke" && isBoosted) {
            particleName = "flame";
        }

        world.spawnParticle(particleName, x, y, z, arg1, arg2, arg3);
    }

    @Inject(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;rayTraceBlocks(Lnet/minecraft/src/client/renderer/Vec3D;Lnet/minecraft/src/client/renderer/Vec3D;)Lnet/minecraft/src/client/physics/MovingObjectPosition;"), cancellable = true)
    public void onRaytrace(CallbackInfo ci) {
        // This inject allows dynamite to explode on contact with an entity

        EntityDynamite tnt = (EntityDynamite) (Object) this;
        Vec3D position = Vec3D.createVector(tnt.posX, tnt.posY, tnt.posZ);
        Vec3D anticipatedPosition = Vec3D.createVector(tnt.posX + tnt.motionX, tnt.posY + tnt.motionY, tnt.posZ + tnt.motionZ);
        MovingObjectPosition hitResult = tnt.worldObj.rayTraceBlocks_do_do(position, anticipatedPosition, false, true);

        // For some fucking reason this shit needs to be assigned twice in the same reading
        // Why? fuck if i know
        // Hit detection is unreliable without this second iteration
        position = Vec3D.createVector(tnt.posX, tnt.posY, tnt.posZ);
        anticipatedPosition = Vec3D.createVector(tnt.posX + tnt.motionX, tnt.posY + tnt.motionY, tnt.posZ + tnt.motionZ);
        if (hitResult != null) {
            anticipatedPosition = Vec3D.createVector(hitResult.hitVec.xCoord, hitResult.hitVec.yCoord, hitResult.hitVec.zCoord);
        }

        List<?> entities = tnt.worldObj.getEntitiesWithinAABBExcludingEntity(tnt, tnt.boundingBox.addCoord(tnt.motionX, tnt.motionY, tnt.motionZ).expand(1, 1, 1));
        double closest = 0;
        Entity target = null;

        for (Object o : entities) {
            Entity entity = (Entity) o;

            if (entity.canBeCollidedWith() && (entity != thrower || ticksInAirFirework >= 5)) {
                AxisAlignedBB targetBB = entity.boundingBox.expand(0.3F, 0.3F, 0.3F);
                MovingObjectPosition targetPos = targetBB.func_1169_a(position, anticipatedPosition);

                if (targetPos == null) {
                    continue;
                }

                double distanceToTarget = position.distanceTo(targetPos.hitVec);
                if (closest == 0 || distanceToTarget < closest) {
                    target = entity;
                    closest = distanceToTarget;
                    anticipatedPosition = targetPos.hitVec;
                }
            }
        }

        if (target != null) {
            hasExploded = true;
            tnt.setPositionAndRotation(anticipatedPosition.xCoord, anticipatedPosition.yCoord, anticipatedPosition.zCoord, tnt.rotationYaw, tnt.rotationPitch);

            if (!isBoosted) {
                for (int i = 0; i < 8; i++) {
                    tnt.worldObj.spawnParticle("lava", tnt.posX, tnt.posY, tnt.posZ, 0, 0, 0);
                    tnt.worldObj.createSmallExplosion(null, tnt.posX, tnt.posY, tnt.posZ, 1);
                }
            }
            else {
                tnt.worldObj.newExplosion(null, tnt.posX, tnt.posY, tnt.posZ, 2F, false);
                for (int i = 0; i < 2 && tnt.fire > 0; i++) {
                    (new WorldGenFireMolotov()).generate(tnt.worldObj, tnt.worldObj.rand, (int) tnt.posX, (int) tnt.posY, (int) tnt.posZ);
                }

                for (int i = 0; i < 16; i++) {
                    tnt.worldObj.spawnParticle("lava", tnt.posX, tnt.posY, tnt.posZ, 0, 0, 0);
                }

                if (thrower instanceof EntityPlayer && !isScoreAwarded) {
                    thrower.addToPlayerScore(null, 3);
                    ((EntityPlayer) thrower).addChatMessage(ChatColors.GREEN + "+PROJECTILE BOOST");
                }
            }

            tnt.setEntityDead();
            ci.cancel();
        }
    }

    @Redirect(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;createSmallExplosion(Lnet/minecraft/src/game/entity/Entity;DDDF)Lnet/minecraft/src/game/level/Explosion;"))
    public Explosion onDetonate(World world, Entity entity, double x, double y, double z, float magnitude) {
        EntityDynamite tnt = (EntityDynamite) (Object) this;

        if (hasExploded) {
            return null;
        }

        if (!isBoosted) {
            return world.createSmallExplosion(entity, x, y, z, magnitude);
        }

        hasExploded = true;
        Explosion explosion = world.newExplosion(null, x, y, z, 2F, false);

        for (int i = 0; i < 16; i++) {
            world.spawnParticle("lava", (int) x, (int) y, (int) z, 0, 0, 0);
        }

        for (int i = 0; i < 2 && tnt.fire > 0; i++) {
            (new WorldGenFireMolotov()).generate(world, world.rand, (int) x, (int) y, (int) z);
        }

        return explosion;
    }
}
