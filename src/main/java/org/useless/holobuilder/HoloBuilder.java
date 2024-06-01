package org.useless.holobuilder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.chunk.ChunkPosition;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.CommandHelper;

import java.util.Map;


public class HoloBuilder implements ModInitializer {
    public static final String MOD_ID = "holobuilder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static HoloCache holoCache = new HoloCache();
	public static float r = 0.55f;
	public static float g = 0.75f;
	public static float b = 1f;
	public static float a = 0.5f;
	public static double holoRenderDist = 30 * 30;
    @Override
    public void onInitialize() {
		CommandHelper.createClientCommand((mc) -> new HoloCommand(mc.get()));
        LOGGER.info("HoloBuilder initialized.");
    }

	public static void addSphere(double sphereX, double sphereY, double sphereZ, double radius, int blockId, int meta){
		int radiusCheck = MathHelper.floor_double(radius + 2);
        int blockOrgX = MathHelper.floor_double(sphereX);
		int blockOrgY = MathHelper.floor_double(sphereY);
		int blockOrgZ = MathHelper.floor_double(sphereZ);
		for (int x = blockOrgX - radiusCheck; x <= blockOrgX + radiusCheck; x++) {
			for (int y = blockOrgY - radiusCheck; y <= blockOrgY + radiusCheck; y++) {
				for (int z = blockOrgZ - radiusCheck; z <= blockOrgZ + radiusCheck; z++) {
					if (isPointSphere(x + 0.5, y + 0.5, z + 0.5, sphereX, sphereY, sphereZ, radius)){
						holoCache.setBlock(x, y, z, blockId, meta);
					}
				}
			}
		}
	}
	public static void addCube(int originX, int originY, int originZ, int xSize, int ySize, int zSize, int blockId, int meta){
		int signX = (int) Math.signum(xSize);
		int signY = (int) Math.signum(ySize);
		int signZ = (int) Math.signum(zSize);
		for (int x = 0; x < Math.abs(xSize); x++) {
			for (int y = 0; y < Math.abs(ySize); y++) {
				for (int z = 0; z < Math.abs(zSize); z++) {
					holoCache.setBlock(originX + x * signX, originY + y * signY, originZ + z * signZ, blockId, meta);
				}
			}
		}
	}
	public static void addLine(int x1, int y1, int z1, int x2, int y2, int z2, int blockId, int meta){
		int minX = Math.min(x1, x2);
		int minY = Math.min(y1, y2);
		int minZ = Math.min(z1, z2);
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);
		int maxZ = Math.max(z1, z2);
		double diffX = x2 - x1;
		double diffY = y2 - y1;
		double diffZ = z2 - z1;
		holoCache.setBlock(x1, y1, z1, blockId, meta);
		holoCache.setBlock(x2, y2, z2, blockId, meta);
		double length = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
		double mx = diffX / length;
		double my = diffY / length;
		double mz = diffZ / length;
		double dist = 0;
		while (dist < length){
			double nx = x1 + mx * dist;
			double ny = y1 + my * dist;
			double nz = z1 + mz * dist;
			if (nx < minX || nx > maxX) break;
			if (ny < minY || ny > maxY) break;
			if (nz < minZ || nz > maxZ) break;
			int blockX = MathHelper.floor_double(nx);
			int blockY = MathHelper.floor_double(ny);
			int blockZ = MathHelper.floor_double(nz);
//			double _x = (blockX + 0.5) - nx;
//			double _y = (blockY + 0.5) - ny;
//			double _z = (blockZ + 0.5) - nz;
//            if (_x * _y + _y * _y + _z * _z <= 0.75 * 0.75) {
//                holoCache.setBlock(blockX, blockY, blockZ, blockId, meta);
//            }
			holoCache.setBlock(blockX, blockY, blockZ, blockId, meta);
            dist += 1;
		}
	}
	public static void render(Minecraft mc, float partialTick){
		Map<ChunkPosition, int[]> positions = holoCache.getBlockData();
		TextureRegistry.blockAtlas.bindTexture();
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.activeCamera.getX(partialTick), -mc.activeCamera.getY(partialTick), -mc.activeCamera.getZ(partialTick));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(r, g, b, a);
		Tessellator tessellator = Tessellator.instance;
		RenderBlocks rb = new RenderBlocks(holoCache);
		rb.useInventoryTint = true;
		BlockModel.setRenderBlocks(rb);
		tessellator.startDrawingQuads();
		tessellator.disableColor();
		for (Map.Entry<ChunkPosition, int[]> e : positions.entrySet()){
			double diffX = mc.thePlayer.x - e.getKey().x - 0.5d;
			double diffY = mc.thePlayer.y - e.getKey().y - 0.5d;
			double diffZ = mc.thePlayer.z - e.getKey().z - 0.5d;
			double distSqr = diffX * diffX + diffY * diffY + diffZ * diffZ;
			if (distSqr > holoRenderDist) continue;
			Block b = Block.getBlock(e.getValue()[0]);
			if (b == null) continue;
			BlockModelDispatcher.getInstance().getDispatch(b).renderNoCulling(tessellator, e.getKey().x, e.getKey().y, e.getKey().z);
		}
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
	}
	private static boolean isPointSphere(double pointX, double pointY, double pointZ, double sphereX, double sphereY, double sphereZ, double radius){
		double diffX = pointX - sphereX;
		double diffY = pointY - sphereY;
		double diffZ = pointZ - sphereZ;
		double dist = (diffX * diffX) + (diffY * diffY) + (diffZ * diffZ);
		double nRad = radius + 0.5;
		double pRad = radius - 0.5;
		return dist < nRad * nRad && dist > pRad * pRad;
	}
}
