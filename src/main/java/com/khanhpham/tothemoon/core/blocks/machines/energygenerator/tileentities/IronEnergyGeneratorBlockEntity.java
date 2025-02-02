package com.khanhpham.tothemoon.core.blocks.machines.energygenerator.tileentities;

import com.khanhpham.tothemoon.init.ModBlockEntities;
import com.khanhpham.tothemoon.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class IronEnergyGeneratorBlockEntity extends AbstractEnergyGeneratorBlockEntity {
    public IronEnergyGeneratorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.IRON_ENERGY_GENERATOR_TE.get(), pWorldPosition, pBlockState, 200000, 525, ModBlocks.IRON_ENERGY_GENERATOR.get().getName());
    }
}
