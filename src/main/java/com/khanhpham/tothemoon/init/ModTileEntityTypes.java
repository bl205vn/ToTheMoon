package com.khanhpham.tothemoon.init;

import com.khanhpham.tothemoon.Names;
import com.khanhpham.tothemoon.core.energygenerator.tileentities.CopperEnergyGeneratorTileEntity;
import com.khanhpham.tothemoon.core.storageblock.MoonBarrelTileEntity;
import com.khanhpham.tothemoon.utils.BlockEntityRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;


@Mod.EventBusSubscriber(modid = Names.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntityTypes {
    public static final BlockEntityRegister BLOCK_ENTITIES = new BlockEntityRegister();

    public static final BlockEntityType<MoonBarrelTileEntity> MOON_STORAGE;
    public static final BlockEntityType<CopperEnergyGeneratorTileEntity> COPPER_ENERGY_GENERATOR_TE;

    static {
        MOON_STORAGE = register("moon_storage", MoonBarrelTileEntity::new, ModBlocks.MOON_ROCK_BARREL);
        COPPER_ENERGY_GENERATOR_TE = register("energy_generator_tile_entity", CopperEnergyGeneratorTileEntity::new, ModBlocks.COPPER_ENERGY_GENERATOR);
    }

    private ModTileEntityTypes() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier, Block... validBlocks) {
        return BLOCK_ENTITIES.register(name, supplier, validBlocks);
    }

    @SubscribeEvent
    public static void init(RegistryEvent.Register<BlockEntityType<?>> event) {
        init(event.getRegistry());
    }

    public static void init(IForgeRegistry<BlockEntityType<?>> registry) {
        BLOCK_ENTITIES.registerAll(registry);
    }
}
