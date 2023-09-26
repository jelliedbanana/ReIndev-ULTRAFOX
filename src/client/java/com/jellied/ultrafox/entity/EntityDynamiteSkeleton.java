package com.jellied.ultrafox.entity;

import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.EntityMob;
import net.minecraft.src.game.entity.other.EntityDynamite;
import net.minecraft.src.game.item.Item;
import net.minecraft.src.game.item.ItemStack;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

public class EntityDynamiteSkeleton extends EntityMob {
    private static final ItemStack defaultHeldItem = new ItemStack(Item.dynamite, 1);
    public EntityDynamiteSkeleton(World world) {
        super(world);
        this.texture = "/mob/monsters/skeleton/skeleton.png";
        this.scoreValue = 30;
    }

    @Override
    public Item getSpawnEgg() {
        return Item.skeletonSpawnEgg;
    }

    @Override
    protected String getLivingSound() {
        return "mob.skeleton";
    }

    @Override
    protected String getHurtSound() {
        return "mob.skeletonhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.skeletonhurt";
    }

    public void onLivingUpdate() {
        float brightness = getEntityBrightness(1F);
        if (worldObj.isDaytime() && brightness > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
            this.fire = 300;
        }
        super.onLivingUpdate();
    }

    protected void attackEntity(Entity target, float distanceToTarget) {
        if (distanceToTarget < 10) {
            double targetXDirection = (target.posX - this.posX);
            double targetZDirection = (target.posZ - this.posZ);

            if (this.attackTime == 0) {
                this.attackTime = 50;

                EntityDynamite tnt = new EntityDynamite(worldObj, this);
                tnt.setPositionAndRotation(this.posX, this.posY + 1, this.posZ, this.rotationYaw, this.rotationPitch);

                double targetYDirection = (target.posY + target.getEyeHeight() - 0.2D - tnt.posY);
                float targetYDirection2 = MathHelper.sqrt_double(targetXDirection * targetXDirection + targetZDirection * targetZDirection) * 0.2F;

                worldObj.playSoundAtEntity((Entity) this, "random.bow", 1.0F, 1.0F / (worldObj.rand.nextFloat() * 0.4F + 0.8F));
                worldObj.playAuxSFX(998, this.posX, this.posY, this.posZ, 0);
                worldObj.entityJoinedWorld(tnt);

                tnt.setFireworkHeading(targetXDirection, targetYDirection + targetYDirection2, targetZDirection, 0.6F, 12F);
            }

            this.rotationYaw = (float) (Math.atan2(targetZDirection, targetXDirection) * 180 / 3.14159) - 90F;
            this.hasAttacked = true;
        }
    }

    protected int getDropItemId() {
        return Item.dynamite.itemID;
    }

    protected String getEntityString() {
        // The superclass's method being overridden here would normally
        // check for an entry in the EntityList map using the EntityDynamiteSkeleton class
        // since EntityDynamiteSkeleton is not an entry in the map, it would return null
        // this is only important so that dynamite skeleton entities are saved
        // without an 'id' tag in the nbt, it can't be saved

        return "DynamiteSkeleton";
    }

    protected void dropFewItems() {
        for(int i = 0; i < rand.nextInt(3); i++) {
            dropItem(Item.dynamite.itemID, 1);
        }

        for(int i = 0; i < rand.nextInt(3); i++) {
            dropItem(Item.bone.itemID, 1);
        }
    }

    @Override
    public ItemStack getHeldItem() {
        return defaultHeldItem;
    }
}
