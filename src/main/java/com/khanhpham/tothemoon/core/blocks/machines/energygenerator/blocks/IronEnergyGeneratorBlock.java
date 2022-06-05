package com.khanhpham.tothemoon.core.blocks.machines.energygenerator.blocks;

import com.khanhpham.tothemoon.core.blocks.machines.energygenerator.tileentities.IronEnergyGeneratorBlockEntity;
import com.khanhpham.tothemoon.init.ModBlockEntityTypes;
import com.khanhpham.tothemoon.core.blocks.machines.energygenerator.AbstractEnergyGeneratorBlock;
import com.khanhpham.tothemoon.core.blockentities.energygenerator.AbstractEnergyGeneratorBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class IronEnergyGeneratorBlock extends AbstractEnergyGeneratorBlock<IronEnergyGeneratorBlockEntity> {
    public IronEnergyGeneratorBlock(Properties p_49224_, BlockEntityType.BlockEntitySupplier<IronEnergyGeneratorBlockEntity> supplier) {
        super(p_49224_, supplier);
    }

    @Override
    protected BlockEntityType<IronEnergyGeneratorBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.IRON_ENERGY_GENERATOR_TE.get();
    }

    @Override
    protected <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockEntityType<A> pBlockEntityType) {
        return level.isClientSide ? null : createTickerHelper(pBlockEntityType, ModBlockEntityTypes.IRON_ENERGY_GENERATOR_TE.get(), AbstractEnergyGeneratorBlockEntity::serverTick);
    }
}
