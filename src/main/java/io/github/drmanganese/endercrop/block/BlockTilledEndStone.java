package io.github.drmanganese.endercrop.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import io.github.drmanganese.endercrop.init.ModBlocks;
import io.github.drmanganese.endercrop.reference.Names;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockTilledEndStone extends BlockFarmland {

    public BlockTilledEndStone() {
        super();
        this.setUnlocalizedName(Names.Blocks.TILLED_END_STONE);
        this.setRegistryName(Names.Blocks.TILLED_END_STONE);

        this.setLightOpacity(255);
        this.useNeighborBrightness = true;
        this.setHardness(0.6F);
        this.setHarvestLevel("pickaxe", 1);

        ModBlocks.BLOCKS.add(this);
    }

    @Override
    public void updateTick(@Nullable World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn == null) return;

        int i = state.getValue(MOISTURE);
        if (!this.hasWater(worldIn, pos) && !worldIn.isRainingAt(pos.up())) {
            if (i > 0) {
                worldIn.setBlockState(pos, state.withProperty(MOISTURE, i - 1), 2);
            } else if (!this.hasCrops(state, worldIn, pos)) {
                worldIn.setBlockState(pos, Blocks.END_STONE.getDefaultState());
            }
        } else if (i < 7) {
            worldIn.setBlockState(pos, state.withProperty(MOISTURE, 7), 2);
        }
    }

    private boolean hasCrops(IBlockState state, World worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        return block instanceof IPlantable && canSustainPlant(state, worldIn, pos, EnumFacing.UP, (IPlantable) block);
    }

    private boolean hasWater(World worldIn, BlockPos pos) {
        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.WATER) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canSustainPlant(@Nonnull IBlockState state, @Nonnull IBlockAccess world, BlockPos pos, @Nonnull EnumFacing direction, IPlantable plantable) {
        IBlockState plant = plantable.getPlant(world, pos.offset(direction));
        return plant.getBlock() instanceof BlockCropEnder;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos pos2) {
        super.neighborChanged(state, world, pos, block, pos2);

        if (world.getBlockState(pos.up()).getMaterial().isSolid()) {
            world.setBlockState(pos, Blocks.END_STONE.getDefaultState());
        }
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn instanceof EntityLivingBase) {
            if (!worldIn.isRemote && worldIn.rand.nextFloat() < fallDistance - 0.5F) {
                if (!(entityIn instanceof EntityPlayer) && !worldIn.getGameRules().getBoolean("mobGriefing")) {
                    return;
                }

                worldIn.setBlockState(pos, Blocks.END_STONE.getDefaultState());
            }

            entityIn.fall(fallDistance, 1.0F);
        }
    }

    @Override
    public boolean isFertile(@Nonnull World world, @Nonnull BlockPos pos) {
        return (world.getBlockState(pos).getValue(BlockTilledEndStone.MOISTURE)) > 0;
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, @Nullable Random rand, int fortune) {
        return Blocks.END_STONE.getItemDropped(Blocks.END_STONE.getDefaultState(), rand, fortune);
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        if (player != null)
            return super.getPickBlock(state, target, world, pos, player);
        else
            return new ItemStack(this);
    }

}
