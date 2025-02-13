// Date: 30-6-2015 15:38:53
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package me.desht.pneumaticcraft.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.desht.pneumaticcraft.client.util.RenderUtils;
import me.desht.pneumaticcraft.common.minigun.Minigun;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Used in three different places:
 * 1. Drone's minigun (in DroneMinigunLayer)
 * 2. ISTER for the minigun item (RenderItemMinigun)
 * 3. Sentry turrent TER model (RenderSentryTurret)
 */
public class ModelMinigun {
    //fields
    private final ModelRenderer barrel;
    private final ModelRenderer support1;
    private final ModelRenderer support2;
    private final ModelRenderer support3;
    private final ModelRenderer support4;
    private final ModelRenderer support5;
    private final ModelRenderer main;
    private final ModelRenderer magazine;
    private final ModelRenderer mount;
    private final ModelRenderer mount_r1;
    private final ModelRenderer mount_r2;
    private final ModelRenderer magazineColor;

    public ModelMinigun() {
        barrel = new ModelRenderer(64, 32, 30, 15);
        barrel.setPos(0.0F, 20.9667F, -8.0F);
        barrel.texOffs(0, 3).addBox(-0.5F, 1.4333F, -4.0F, 1.0F, 1.0F, 20.0F, 0.0F, false);

        support1 = new ModelRenderer(64, 32, 0, 0);
        support1.setPos(0.0F, 21.0F, -6.0F);
        support1.texOffs(22, 16).addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);
        support1.texOffs(0, 13).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);
        support1.texOffs(0, 13).addBox(-1.5F, -1.5F, 5.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);
        support1.texOffs(0, 13).addBox(-1.5F, -1.5F, 7.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);
        support1.texOffs(0, 13).addBox(-1.5F, -1.5F, 13.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);

        support2 = new ModelRenderer(64, 32, 0, 4);
        support2.setPos(0.0F, 21.0F, -6.0F);
        support2.texOffs(22, 8).addBox(-1.5F, 1.5F, -5.0F, 3.0F, 1.0F, 4.0F, 0.0F, false);
        support2.texOffs(0, 11).addBox(-1.5F, 1.5F, 0.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        support2.texOffs(0, 21).addBox(-1.5F, 1.5F, 5.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        support2.texOffs(0, 21).addBox(-1.5F, 1.5F, 7.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        support2.texOffs(0, 21).addBox(-1.5F, 1.5F, 13.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        support3 = new ModelRenderer(64, 32, 0, 6);
        support3.setPos(0.0F, 21.0F, -6.0F);
        support3.texOffs(22, 8).addBox(-1.5F, -2.5F, -5.0F, 3.0F, 1.0F, 4.0F, 0.0F, false);
        support3.texOffs(0, 11).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        support3.texOffs(0, 11).addBox(-1.5F, -2.5F, 5.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        support3.texOffs(0, 11).addBox(-1.5F, -2.5F, 7.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        support3.texOffs(0, 11).addBox(-1.5F, -2.5F, 13.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        support4 = new ModelRenderer(64, 32, 0, 8);
        support4.setPos(0.0F, 21.0F, -6.0F);
        support4.texOffs(22, 1).addBox(1.5F, -1.5F, -5.0F, 1.0F, 3.0F, 4.0F, 0.0F, false);
        support4.texOffs(0, 17).addBox(1.5F, -1.5F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        support4.texOffs(4, 17).addBox(1.5F, -1.5F, 5.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        support4.texOffs(4, 17).addBox(1.5F, -1.5F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        support4.texOffs(4, 17).addBox(1.5F, -1.5F, 13.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        support5 = new ModelRenderer(64, 32, 0, 11);
        support5.setPos(0.0F, 21.0F, -6.0F);
        support5.texOffs(32, 1).addBox(-2.5F, -1.5F, -5.0F, 1.0F, 3.0F, 4.0F, 0.0F, false);
        support5.texOffs(0, 17).addBox(-2.5F, -1.5F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        support5.texOffs(0, 17).addBox(-2.5F, -1.5F, 5.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        support5.texOffs(4, 17).addBox(-2.5F, -1.5F, 7.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        support5.texOffs(4, 17).addBox(-2.5F, -1.5F, 13.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        main = new ModelRenderer(64, 32, 36, 0);
        main.setPos(-3.0F, 18.0F, 8.0F);
        main.texOffs(36, 18).addBox(0.0F, 0.0F, 1.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);
        main.texOffs(34, 3).addBox(0.5F, 0.5F, 0.0F, 5.0F, 5.0F, 10.0F, 0.0F, false);

        magazine = new ModelRenderer(64, 32, 0, 14);
        magazine.setPos(3.0F, 22.0F, 9.0F);
        magazine.texOffs(0, 0).addBox(-8.0F, -2.0F, 2.5F, 5.0F, 6.0F, 5.0F, 0.0F, true);

        mount = new ModelRenderer(64, 32, 0, 23);
        mount.setPos(-1.0F, 15.0F, 11.0F);


        mount_r1 = new ModelRenderer(64, 32, 0, 23);
        mount_r1.setPos(1.0F, 2.0F, 2.0F);
        mount.addChild(mount_r1);
        setRotationAngle(mount_r1, -0.3927F, 0.0F, 0.0F);
        mount_r1.texOffs(60, 0).addBox(-0.5F, -3.0F, -0.75F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        mount_r2 = new ModelRenderer(64, 32, 0, 23);
        mount_r2.setPos(1.0F, 2.0F, 2.0F);
        mount.addChild(mount_r2);
        setRotationAngle(mount_r2, -0.2618F, 0.0F, 0.0F);
        mount_r2.texOffs(54, 3).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 1.0F, 3.0F, 0.0F, false);
        mount_r2.texOffs(56, 18).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

        magazineColor = new ModelRenderer(64, 32, 8, 0);
        magazineColor.setPos(4.3F, 22.5F, 10.0F);
        magazineColor.texOffs(54, 7).addBox(-9.6F, -2.0F, 2.0F, 1.0F, 2.0F, 4.0F, 0.0F, true);
    }

    public void renderMinigun(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, Minigun minigun, float partialTick, boolean renderMount) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.entityCutout(Textures.MODEL_DRONE_MINIGUN));
        matrixStack.pushPose();

        if (renderMount) {
            matrixStack.pushPose();
            matrixStack.translate(0, 5 / 16D, -12 / 16D);
            mount.render(matrixStack, builder, combinedLight, combinedOverlay);
            matrixStack.popPose();
        }

        float barrelRotation = 0;
        if (minigun != null) {
            barrelRotation = minigun.getOldMinigunRotation() + partialTick * (minigun.getMinigunRotation() - minigun.getOldMinigunRotation());
            float yaw = minigun.oldMinigunYaw + partialTick * Minigun.clampYaw(minigun.minigunYaw - minigun.oldMinigunYaw);
            float pitch = minigun.oldMinigunPitch + partialTick * (minigun.minigunPitch - minigun.oldMinigunPitch);

            matrixStack.translate(0, 23 / 16D, 0);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(yaw));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(pitch));
            matrixStack.translate(0, -18 / 16D, -12 / 16D);
        }
        barrel.yRot = 0;
        barrel.xRot = 0;
        for (int i = 0; i < 6; i++) {
            barrel.zRot = (float) (Math.PI / 3 * i) + barrelRotation;
            barrel.render(matrixStack, builder, combinedLight, combinedOverlay);
        }
        support1.zRot = barrelRotation;
        support2.zRot = barrelRotation;
        support3.zRot = barrelRotation;
        support4.zRot = barrelRotation;
        support5.zRot = barrelRotation;
        support1.render(matrixStack, builder, combinedLight, combinedOverlay);
        support2.render(matrixStack, builder, combinedLight, combinedOverlay);
        support3.render(matrixStack, builder, combinedLight, combinedOverlay);
        support4.render(matrixStack, builder, combinedLight, combinedOverlay);
        support5.render(matrixStack, builder, combinedLight, combinedOverlay);
        magazine.render(matrixStack, builder, combinedLight, combinedOverlay);
        main.render(matrixStack, builder, combinedLight, combinedOverlay);

        float[] cols = RenderUtils.decomposeColorF(minigun != null ? 0xFF000000 | minigun.getAmmoColor() : 0xFF313131);
        magazineColor.render(matrixStack, builder, combinedLight, combinedOverlay, cols[1], cols[2], cols[3], cols[0]);

        matrixStack.popPose();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
