package com.jellied.ultrafox.client.mixins;

import com.jellied.ultrafox.SkeletonAccessor;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.monster.EntitySkeleton;
import net.minecraft.src.game.entity.other.EntityDynamite;
import net.minecraft.src.game.item.Item;
import net.minecraft.src.game.item.ItemStack;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EntitySkeleton.class)
public class MixinSkeleton implements SkeletonAccessor {
    public boolean isDynamiteSkeleton = false;
    ItemStack heldItem = null;

    @Overwrite
    public ItemStack getHeldItem() {
        if (heldItem == null) {
            heldItem = isDynamiteSkeleton ? new ItemStack(Item.dynamite, 1) : new ItemStack(Item.bow, 1);
        }

        return heldItem;
    }

    //@Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;isDaytime()Z"))
    //public boolean isDaytime(World world) {
        //return false;
    //}

    @Inject(method = "attackEntity", at = @At("HEAD"))
    protected void attackEntity(Entity target, float distanceFromTarget, CallbackInfo ci) {
        EntitySkeleton skeleton = (EntitySkeleton) (Object) this;

        if (isDynamiteSkeleton && skeleton.attackTime == 0 && distanceFromTarget < 15) {
            skeleton.attackTime = 50;

            EntityDynamite tnt = new EntityDynamite(skeleton.worldObj, skeleton);
            tnt.setPositionAndRotation(skeleton.posX, skeleton.posY + 1, skeleton.posZ, skeleton.rotationYaw, skeleton.rotationPitch);

            double targetXDirection = (target.posX - skeleton.posX);
            double targetZDirection = (target.posZ - skeleton.posZ);

            double targetYDirection = (target.posY + target.getEyeHeight() - 0.2D - tnt.posY);
            float targetYDirection2 = MathHelper.sqrt_double(targetXDirection * targetXDirection + targetZDirection * targetZDirection) * 0.2F;

            skeleton.worldObj.playSoundAtEntity((Entity) skeleton, "random.bow", 1.0F, 1.0F / (skeleton.worldObj.rand.nextFloat() * 0.4F + 0.8F));
            skeleton.worldObj.playAuxSFX(998, skeleton.posX, skeleton.posY, skeleton.posZ, 0);
            skeleton.worldObj.entityJoinedWorld(tnt);
            tnt.setFireworkHeading(targetXDirection, targetYDirection + targetYDirection2, targetZDirection, 0.6F, 12F);
        }
    }

    @Overwrite
    protected void dropFewItems() {
        EntitySkeleton skeleton = (EntitySkeleton) (Object) this;
        Random rand = skeleton.worldObj.rand;

        if (isDynamiteSkeleton) {
            for(int i = 0; i < rand.nextInt(3); i++) {
                skeleton.dropItem(Item.dynamite.itemID, 1);
            }
        }
        else {
            for(int i = 0; i < rand.nextInt(3); i++) {
                skeleton.dropItem(Item.arrow.itemID, 1);
            }
        }

        for(int i = 0; i < rand.nextInt(3); i++) {
            skeleton.dropItem(Item.bone.itemID, 1);
        }
    }


    @Override
    public void setIsDynamiteSkeleton(boolean set) {
        isDynamiteSkeleton = set;
    }

    @Override
    public boolean getIsDynamiteSkeleton() {
        return isDynamiteSkeleton;
    }
}
