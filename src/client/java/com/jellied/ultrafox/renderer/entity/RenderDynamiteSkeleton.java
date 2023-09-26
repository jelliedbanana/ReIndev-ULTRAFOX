package com.jellied.ultrafox.renderer.entity;

import com.jellied.ultrafox.entity.EntityDynamiteSkeleton;
import net.minecraft.src.client.model.ModelBiped;
import net.minecraft.src.client.model.ModelSkeleton;
import net.minecraft.src.client.renderer.entity.RenderBiped;
import net.minecraft.src.game.entity.EntityLiving;

// Mostly copied from the RenderSkeleton class
public class RenderDynamiteSkeleton extends RenderBiped {
    private ModelSkeleton modelArmor;

    public RenderDynamiteSkeleton(ModelBiped model, float scale) {
        super(model, scale);
        this.modelArmor = new ModelSkeleton(0.25F);
    }

    public boolean setArmorModel(EntityDynamiteSkeleton entitySkeleton, int armorType, float f) {
        ModelSkeleton model = this.modelArmor;

        this.loadTexture("/mob/monsters/skeleton/tnt_quiver.png");
        model.aimedBow = false;
        model.bipedHead.showModel = armorType == 0;
        model.bipedHeadwear.showModel = armorType == 0;
        model.bipedBody.showModel = armorType == 1 || armorType == 2;
        model.bipedRightArm.showModel = armorType == 1;
        model.bipedLeftArm.showModel = armorType == 1;
        model.bipedRightLeg.showModel = armorType == 2 || armorType == 3;
        model.bipedLeftLeg.showModel = armorType == 2 || armorType == 3;
        this.setRenderPassModel(model);

        return true;
    }

    @Override
    protected boolean shouldRenderPass(EntityLiving entity, int armorType, float f) {
        return this.setArmorModel((EntityDynamiteSkeleton) entity, armorType, f);
    }
}
