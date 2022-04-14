package com.khanhpham.tothemoon.datagen;

import com.khanhpham.tothemoon.Names;
import com.khanhpham.tothemoon.init.ModItems;
import com.khanhpham.tothemoon.utils.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;
import static com.khanhpham.tothemoon.init.ModBlocks.*;

public class ModTagProvider {
    public ModTagProvider(DataGenerator data, ExistingFileHelper fileHelper) {
        ModBlockTagsProvider provider = new ModBlockTagsProvider(data, fileHelper);
        data.addProvider(new ModItemTagsProvider(data, provider, fileHelper));
        data.addProvider(provider);
    }

    public static final class ModItemTagsProvider extends ItemTagsProvider {
        public ModItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, ExistingFileHelper existingFileHelper) {
            super(pGenerator, pBlockTagsProvider, Names.MOD_ID, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags() {
            tag(ModTags.GENERAL_PRESS_MOLDS).addTags(ModTags.PLATES_MOLD, ModTags.ROD_MOLD, ModTags.GEAR_MOLD);
            tag(ModTags.GEAR_MOLD).add(ModItems.GEAR_MOLD.get());
            tag(ModTags.ROD_MOLD).add(ModItems.ROD_MOLD.get());
            tag(ModTags.PLATES_MOLD).add(ModItems.PLATE_MOLD.get());

            this.addTag(Tags.Items.INGOTS, ModItems.STEEL_INGOT, ModItems.URANIUM_INGOT, ModItems.REDSTONE_METAL, ModItems.REDSTONE_STEEL_ALLOY);
            this.addTag(ModTags.INGOTS_URANIUM, ModItems.URANIUM_INGOT);
            this.addTag(ModTags.INGOTS_STEEL, ModItems.STEEL_INGOT);
            this.addTag(ModTags.INGOTS_REDSTONE_METAL, ModItems.REDSTONE_METAL);
            this.addTag(ModTags.INGOTS_REDSTONE_STEEL_ALLOY, ModItems.REDSTONE_STEEL_ALLOY);
        }

        @SafeVarargs
        private void addTag(TagKey<Item> tag, Supplier<? extends Item>... suppliers) {
            var appender = super.tag(tag);
            for (Supplier<? extends Item> supplier : suppliers) {
                appender.add(supplier.get());
            }
        }
    }

    public static final class ModBlockTagsProvider extends BlockTagsProvider {

        public ModBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
            super(pGenerator, Names.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags() {
            TagsProvider.TagAppender<Block> blockTagAppender = tag(BlockTags.MINEABLE_WITH_PICKAXE);
            BLOCK_DEFERRED_REGISTER.getEntries().stream().map(Supplier::get).filter(b -> !b.equals(MOON_DUST.get())).forEach(blockTagAppender::add);

            tag(BlockTags.NEEDS_STONE_TOOL, MOON_ROCK, MOON_ROCK_BRICK, MOON_ROCK_STAIR, MOON_ROCK_BRICK_STAIR, MOON_ROCK_BRICK_SLAB, MOON_ROCK, MOON_IRON_ORE, MOON_QUARTZ_ORE, MOON_DUST);
            tag(BlockTags.NEEDS_IRON_TOOL, COPPER_MACHINE_FRAME, STEEL_MACHINE_FRAME, REDSTONE_MACHINE_FRAME, DEEPSLATE_URANIUM_ORE, MOON_GOLD_ORE, MOON_REDSTONE_ORE, MOON_URANIUM_ORE);
            tag(BlockTags.MINEABLE_WITH_SHOVEL, MOON_DUST);
        }

        @SafeVarargs
        private void tag(TagKey<Block> tag, Supplier<? extends Block>... blockSupplier) {
            var tags = super.tag(tag);
            for (Supplier<? extends Block> supplier : blockSupplier) {
                tags.add(supplier.get());
            }
        }
    }

}
