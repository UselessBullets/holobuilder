package org.useless.holobuilder;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.core.world.season.SeasonManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HoloCache implements WorldSource {
	private final Map<ChunkPosition, int[]> blockData = new HashMap<>();
	public HoloCache(){
	}
	public void resetData(){
		blockData.clear();
	}
	public Collection<ChunkPosition> getBlockPositions(){
		return blockData.keySet();
	}
	public Map<ChunkPosition, int[]> getBlockData(){
		return blockData;
	}
	@Override
	public int getBlockId(int i, int j, int k) {
		ChunkPosition c = new ChunkPosition(i, j, k);
		if (blockData.containsKey(c)){
			return blockData.get(c)[0];
		}
		return 0;
	}
	public void setBlock(int x, int y, int z, int id, int meta){
		blockData.put(new ChunkPosition(x, y, z), new int[]{id, meta});
	}

	@Override
	public Block getBlock(int i, int j, int k) {
		return Block.getBlock(getBlockId(i, j, k));
	}

	@Override
	public TileEntity getBlockTileEntity(int i, int j, int k) {
		return null;
	}

	@Override
	public float getBrightness(int i, int j, int k, int l) {
		return 15;
	}

	@Override
	public int getLightmapCoord(int i, int j, int k, int l) {
		return LightmapHelper.getLightmapCoord(15, 15);
	}

	@Override
	public float getLightBrightness(int i, int j, int k) {
		return 15;
	}

	@Override
	public int getBlockMetadata(int i, int j, int k) {
		ChunkPosition c = new ChunkPosition(i, j, k);
		if (blockData.containsKey(c)){
			return blockData.get(c)[1];
		}
		return 0;
	}

	@Override
	public Material getBlockMaterial(int i, int j, int k) {
		Block b = getBlock(i, j, k);
		if (b != null) return b.blockMaterial;
		return Material.air;
	}

	@Override
	public boolean isBlockOpaqueCube(int i, int j, int k) {
		Block b = getBlock(i, j, k);
		if (b == null) {
			return false;
		}
		return b.isSolidRender();
	}

	@Override
	public boolean isBlockNormalCube(int i, int j, int k) {
		Block b = getBlock(i, j, k);
		if (b == null) {
			return false;
		}
		return b.blockMaterial.blocksMotion() && b.renderAsNormalBlock();
	}

	@Override
	public double getBlockTemperature(int i, int j) {
		return 0;
	}

	@Override
	public double getBlockHumidity(int i, int j) {
		return 0;
	}

	@Override
	public SeasonManager getSeasonManager() {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		if (mc.theWorld == null) return null;
		return mc.theWorld.getSeasonManager();
	}

	@Override
	public Biome getBlockBiome(int i, int j, int k) {
		return Biomes.OVERWORLD_PLAINS;
	}
}
