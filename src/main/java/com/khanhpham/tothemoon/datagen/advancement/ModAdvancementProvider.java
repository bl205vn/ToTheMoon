package com.khanhpham.tothemoon.datagen.advancement;

import com.khanhpham.tothemoon.ToTheMoon;
import com.khanhpham.tothemoon.advancements.AnvilCrushingTrigger;
import com.khanhpham.tothemoon.advancements.MultiblockFormedTrigger;
import com.khanhpham.tothemoon.core.items.tool.ModArmorMaterial;
import com.khanhpham.tothemoon.core.items.tool.ModToolTiers;
import com.khanhpham.tothemoon.datagen.lang.ModLanguage;
import com.khanhpham.tothemoon.datagen.recipes.provider.ModRecipeProvider;
import com.khanhpham.tothemoon.init.ModBlocks;
import com.khanhpham.tothemoon.init.ModItems;
import com.khanhpham.tothemoon.utils.helpers.ModUtils;
import com.khanhpham.tothemoon.utils.text.TextUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static com.khanhpham.tothemoon.datagen.lang.ModLanguage.*;

//I hate IntelliJ warnings. I have OCD :PP
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement root = Advancement.Builder.advancement().display(ModItems.REDSTONE_STEEL_MATERIALS.getIngot(), ROOT, ROOT_DESCRIPTION, new ResourceLocation(ToTheMoon.MOD_ID, "textures/block/steel_sheet_block.png"), FrameType.CHALLENGE, false, false, false).addCriterion("tick", ModRecipeProvider.tick()).save(consumer, loc("root"), fileHelper);
        Advancement heavyCrushing = display(Items.ANVIL, HEAVY_CRUSHING, FrameType.GOAL).addCriterion("req", AnvilCrushingTrigger.TriggerInstance.crushItem()).parent(root).parent(root).save(consumer, loc("anvil_crushing"), fileHelper);
        Advancement aHeatedTopic = display(ModBlocks.NETHER_BRICK_FURNACE_CONTROLLER.get(), A_HEATED_TOPIC, FrameType.GOAL).addCriterion("req", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.NETHER_BRICK_FURNACE_CONTROLLER.get())).parent(heavyCrushing).save(consumer, loc("a_heated_topic"), fileHelper);
        Advancement highHeatSmelting = display(Items.NETHER_BRICK, HIGH_HEAT_SMELTING, FrameType.GOAL)
                .addCriterion("multiblock_formed", MultiblockFormedTrigger.TriggerInstance.multiblock(MultiblockFormedTrigger.MultiblockType.NETHER_BRICK_FURNACE))
                .parent(aHeatedTopic)
                .save(consumer, loc("multiblock_nether_furnace"), fileHelper);

        Advancement gettingATrueUpgrade = display(ModItems.STEEL_MATERIAL.getIngot(), GETTING_A_TRUE_UPGRADE, FrameType.GOAL)
                .parent(highHeatSmelting)
                .addCriterion("gather_steel", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STEEL_MATERIAL.getIngot()))
                .save(consumer, loc("getting_a_true_upgrade"), fileHelper);

        Advancement.Builder coverMeWithCarbonizedIronBuilder = Advancement.Builder.advancement()
                .parent(gettingATrueUpgrade)
                .display(ModItems.STEEL_MATERIAL.getArmorItem(EquipmentSlot.CHEST), COVER_ME_WITH_CARBONIZED_IRON, TextUtils.translatable(COVER_ME_WITH_CARBONIZED_IRON.getString() + ".description"), null, FrameType.TASK, true, true, true);
        ModItems.STEEL_MATERIAL.getArmorItems().forEach(armor -> {
            String armorId = armor.getId().getPath();
            coverMeWithCarbonizedIronBuilder.addCriterion(armorId, InventoryChangeTrigger.TriggerInstance.hasItems(armor.get()));
        });
        Advancement steelArmors = coverMeWithCarbonizedIronBuilder.requirements(RequirementsStrategy.AND).save(consumer, loc("cover_me_with_carbonized_iron"), fileHelper);

        TieredItem steelPickaxeItem = ModItems.ALL_TOOLS.get(ModToolTiers.ToolType.PICKAXE, ModToolTiers.STEEL.tier()).get();
        Advancement isntThisSteelPickaxe = this.display(steelPickaxeItem, ISNT_THIS_STEEL_PICKAXE, FrameType.TASK, true, true, true).addCriterion("req", InventoryChangeTrigger.TriggerInstance.hasItems(steelPickaxeItem)).parent(steelArmors).save(consumer, loc("steel_pickaxe"), fileHelper);

        Advancement.Builder radiationProtected = this.display(ModItems.URANIUM_MATERIAL.getArmorItem(EquipmentSlot.CHEST), RADIATION_PROTECTED, FrameType.TASK, true, true, true).parent(steelArmors);

        Advancement benchWorking = this.display(ModBlocks.WORKBENCH.get(), ModLanguage.BENCH_WORKING, FrameType.TASK).parent(gettingATrueUpgrade).addCriterion("req", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.WORKBENCH.get())).save(consumer, ModUtils.modLoc("bench_working"), fileHelper);
        Advancement automateTheFuel = this.display(ModBlocks.NETHER_BRICKS_FLUID_ACCEPTOR.get(), ModLanguage.AUTOMATE_THE_FUEL, FrameType.TASK).parent(aHeatedTopic).addCriterion("req", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.NETHER_BRICKS_FLUID_ACCEPTOR.get(), ModBlocks.BLACKSTONE_FLUID_ACCEPTOR.get())).requirements(RequirementsStrategy.OR).save(consumer, ModUtils.modLoc("automate_the_fuel"), fileHelper);
        Advancement machineExoskeleton = this.display(ModBlocks.COPPER_MACHINE_FRAME.get(), MACHINE_EXOSKELETON, FrameType.GOAL).parent(benchWorking).addCriterion("req", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.COPPER_MACHINE_FRAME.get())).save(consumer, loc("machine_exoskeleton"), fileHelper);
        Advancement energizeTheFuel = this.display(ModBlocks.COPPER_ENERGY_GENERATOR.get(), ENERGIZE_THE_FUEL, FrameType.GOAL).parent(machineExoskeleton).addCriterion("energize_the_fuel", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.COPPER_MACHINE_FRAME.get())).save(consumer, ModUtils.modLoc("energize_the_fuel"), fileHelper);
        Advancement storingEnergy = this.display(ModBlocks.BATTERY.get(), STORING_ENERGY, FrameType.TASK).parent(energizeTheFuel).addCriterion("craft", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.BATTERY.get())).addCriterion("place", PlacedBlockTrigger.TriggerInstance.placedBlock(ModBlocks.BATTERY.get())).requirements(RequirementsStrategy.OR).save(consumer, loc("storing_energy"), fileHelper);

        Advancement hardenFramed = this.display(ModBlocks.STEEL_MACHINE_FRAME.get(), ModLanguage.HARDEN_FRAMED, FrameType.GOAL).parent(machineExoskeleton).addCriterion("craft", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.STEEL_MACHINE_FRAME.get())).save(consumer, loc("harden_framed"), fileHelper);
        Advancement burningEnergy = this.display(ModBlocks.ENERGY_SMELTER.get(), BURNING_ENERGY, FrameType.GOAL).parent(hardenFramed).addCriterion("req", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ENERGY_SMELTER.get())).save(consumer, loc("burning_energy"), fileHelper);

        Advancement combiningMaterials = this.display(ModBlocks.ALLOY_SMELTER.get(), COMBINING_MATERIALS, FrameType.GOAL).parent(burningEnergy).addCriterion("req", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ALLOY_SMELTER.get())).save(consumer, loc("combining_materials"), fileHelper);

        Advancement fullyEnergized = this.display(ModBlocks.DIAMOND_ENERGY_GENERATOR.get(), FULLY_ENERGIZED, FrameType.CHALLENGE, true, true, true).addCriterion("req", PlacedBlockTrigger.TriggerInstance.placedBlock(ModBlocks.DIAMOND_ENERGY_GENERATOR.get())).parent(energizeTheFuel).save(consumer, loc("fully_energized"), fileHelper);

        this.invisibleAdvancements(consumer, fileHelper, root);
    }


    private void invisibleAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper, Advancement root) {
        Advancement ironBlock = Advancement.Builder.advancement().addCriterion("get_item", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BLOCK)).parent(root).save(consumer, ModUtils.modLoc("hidden/iron_block"), fileHelper);
    }

    private Advancement.Builder display(ItemLike icon, MutableComponent advancementComponent, FrameType frame) {
        return this.display(icon, advancementComponent, frame, true, true, false);
    }

    private Advancement.Builder display(ItemLike icon, MutableComponent advancementComponent, FrameType frame, boolean toast, boolean toChat, boolean hide) {
        return Advancement.Builder.advancement().display(icon, advancementComponent, Component.translatable(advancementComponent.getString() + ".description"), null, frame, toast, toChat, hide);
    }

    private ResourceLocation loc(String loc) {
        return ModUtils.modLoc(loc);
    }
}
