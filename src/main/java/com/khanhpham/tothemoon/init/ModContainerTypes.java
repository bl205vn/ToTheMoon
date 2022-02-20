package com.khanhpham.tothemoon.init;

import com.khanhpham.tothemoon.Names;
import com.khanhpham.tothemoon.core.energygenerator.containers.EnergyGeneratorMenu;
import com.khanhpham.tothemoon.core.storageblock.MoonBarrelMenu;
import com.khanhpham.tothemoon.utils.ContainerTypeRegister;
import com.khanhpham.tothemoon.utils.containers.BaseMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Names.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainerTypes {
   // public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Names.MOD_ID);
    public static final ContainerTypeRegister CONTAINERS = new ContainerTypeRegister();

    public static final MenuType<MoonBarrelMenu> STORAGE_BLOCK;

    public static final MenuType<EnergyGeneratorMenu> ENERGY_GENERATOR_CONTAINER;

    static {
        STORAGE_BLOCK = register("moon_storage_container", MoonBarrelMenu::new);
        ENERGY_GENERATOR_CONTAINER = register("energy_generator_container", EnergyGeneratorMenu::new);
    }

    private ModContainerTypes() {
    }

    private static <T extends BaseMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> supplier) {
        return CONTAINERS.register(name, supplier);
    }

    @SubscribeEvent
    public static void init(RegistryEvent.Register<MenuType<?>> event) {
        init(event.getRegistry());
    }

    public static void init(IForgeRegistry<MenuType<?>> reg) {
        CONTAINERS.registerAll(reg);
    }
}
