/*
 * This file is part of pnc-repressurized.
 *
 *     pnc-repressurized is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with pnc-repressurized.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.desht.pneumaticcraft.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.desht.pneumaticcraft.common.tileentity.TileEntityAssemblyDrill;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class RenderAssemblyDrill extends AbstractTileModelRenderer<TileEntityAssemblyDrill> {
    private final ModelRenderer baseTurn;
    private final ModelRenderer baseTurn2;
    private final ModelRenderer armBase;
    private final ModelRenderer armMiddle;
    private final ModelRenderer drillBase;
    private final ModelRenderer drill;

    public RenderAssemblyDrill(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);

        baseTurn = new ModelRenderer(64, 64, 0, 0);
        baseTurn.setPos(-3.5F, 22.0F, -3.5F);
        baseTurn.texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 9.0F, 1.0F, 9.0F, 0.0F, true);

        baseTurn2 = new ModelRenderer(64, 64, 0, 0);
        baseTurn2.setPos(-2.0F, 17.0F, -2.0F);
        baseTurn2.texOffs(0, 30).addBox(-2.0F, -0.5F, 0.5F, 2.0F, 6.0F, 3.0F, 0.2F, false);
        baseTurn2.texOffs(0, 10).addBox(-2.0F, 3.75F, -2.0F, 2.0F, 2.0F, 8.0F, 0.0F, true);
        baseTurn2.texOffs(10, 30).addBox(4.0F, -0.5F, 0.5F, 2.0F, 6.0F, 3.0F, 0.2F, true);
        baseTurn2.texOffs(0, 20).addBox(4.0F, 3.75F, -2.0F, 2.0F, 2.0F, 8.0F, 0.0F, true);

        armBase = new ModelRenderer(64, 64, 0, 0);
        armBase.setPos(-3.0F, 17.0F, -1.0F);
        armBase.texOffs(0, 49).addBox(2.0F, 0.0F, 1.0F, 2.0F, 2.0F, 5.0F, 0.3F, true);
        armBase.texOffs(0, 43).addBox(1.5F, -0.5F, -0.5F, 3.0F, 3.0F, 3.0F, 0.0F, true);
        armBase.texOffs(12, 43).addBox(1.5F, -0.5F, 5.5F, 3.0F, 3.0F, 3.0F, 0.0F, true);
        armBase.texOffs(0, 39).addBox(-1.5F, 0.0F, 0.0F, 9.0F, 2.0F, 2.0F, 0.0F, true);

        armMiddle = new ModelRenderer(64, 64, 0, 0);
        armMiddle.setPos(-4.0F, 2.0F, 5.0F);
        armMiddle.texOffs(20, 10).addBox(0.0F, 2.0F, 0.0F, 2.0F, 13.0F, 2.0F, 0.0F, true);
        armMiddle.texOffs(12, 24).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.3F, true);
        armMiddle.texOffs(0, 24).addBox(0.0F, 15.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.3F, true);
        armMiddle.texOffs(14, 52).addBox(-0.5F, 15.0F, 0.0F, 3.0F, 2.0F, 2.0F, 0.0F, true);

        drillBase = new ModelRenderer(64, 64, 0, 0);
        drillBase.setPos(-3.0F, 2.0F, 4.5F);
        drillBase.texOffs(46, 0).addBox(1.0F, -1.0F, -1.0F, 4.0F, 4.0F, 5.0F, 0.0F, true);
        drillBase.texOffs(56, 9).addBox(1.5F, -0.5F, -2.0F, 3.0F, 3.0F, 1.0F, 0.0F, true);

        drill = new ModelRenderer(64, 64, 0, 0);
        drill.setPos(-2.5F, 2.5F, 1.0F);
        drill.texOffs(50, 9).addBox(2.0F, 0.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, true);
    }

    @Override
    void renderModel(TileEntityAssemblyDrill te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float[] angles = new float[5];
        for (int i = 0; i < 4; i++) {
            angles[i] = MathHelper.lerp(partialTicks, te.oldAngles[i], te.angles[i]);
        }
        angles[4] = MathHelper.lerp(partialTicks, te.oldDrillRotation, te.drillRotation);

        IVertexBuilder builder = bufferIn.getBuffer(RenderType.entityCutout(Textures.MODEL_ASSEMBLY_LASER_AND_DRILL));

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(angles[0]));

        baseTurn.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn);
        baseTurn2.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn);

        matrixStackIn.translate(0, 18 / 16F, 0);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(angles[1]));
        matrixStackIn.translate(0, -18 / 16F, 0);

        armBase.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn);

        matrixStackIn.translate(0, 18 / 16F, 6 / 16F);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(angles[2]));
        matrixStackIn.translate(0, -18 / 16F, -6 / 16F);

        armMiddle.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn);

        matrixStackIn.translate(0, 3 / 16F, 6 / 16F);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(angles[3]));
        matrixStackIn.translate(0, -3 / 16F, -6 / 16F);

        drillBase.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn);

        matrixStackIn.translate(0, 3 / 16F, 0);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(angles[4]));
        matrixStackIn.translate(0, -3 / 16F, 0);

        drill.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn);
    }
}
