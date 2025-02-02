package com.khanhpham.tothemoon.core.blockentities.others;

import com.khanhpham.tothemoon.core.abstracts.EnergyProcessBlockEntity;
import com.khanhpham.tothemoon.core.blocks.machines.metalpress.MetalPressBlock;
import com.khanhpham.tothemoon.core.blocks.machines.metalpress.MetalPressMenu;
import com.khanhpham.tothemoon.core.energy.Energy;
import com.khanhpham.tothemoon.core.energy.MachineEnergy;
import com.khanhpham.tothemoon.core.recipes.metalpressing.IMetalPressBlockEntity;
import com.khanhpham.tothemoon.core.recipes.metalpressing.MetalPressingRecipe;
import com.khanhpham.tothemoon.datagen.tags.ModItemTags;
import com.khanhpham.tothemoon.init.ModBlockEntities;
import com.khanhpham.tothemoon.init.ModBlocks;
import com.khanhpham.tothemoon.init.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class MetalPressBlockEntity extends EnergyProcessBlockEntity implements IMetalPressBlockEntity {
    public static final int MENU_SIZE = 3;
    public static final int DATA_SIZE = 4;
    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> workingTime;
                case 1 -> workingDuration;
                case 2 -> energy.getEnergyStored();
                case 3 -> energy.getMaxEnergyStored();
                default -> throw new IllegalStateException("Unexpected value: " + pIndex);
            };
        }

        @Override
        public void set(int pIndex, int pValue) {

        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public MetalPressBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, Energy energy, @NotNull Component label, int containerSize) {
        super(pType, pWorldPosition, pBlockState, energy, label, containerSize);
    }

    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.METAL_PRESS.get(), blockPos, blockState, new MachineEnergy(200_000), ModBlocks.METAL_PRESS.get().getName(), MENU_SIZE);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, MetalPressBlockEntity e) {
        e.serverTick(level, blockPos, state);
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory) {
        return new MetalPressMenu(this, playerInventory, containerId, this.data);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {

        if (!energy.isEmpty()) {
            @Nullable MetalPressingRecipe recipe = level.getRecipeManager().getRecipeFor(MetalPressingRecipe.RECIPE_TYPE, this, level).orElse(null);
            if (recipe != null && super.isResultSlotFreeForProcess(2, recipe)) {
                if (workingDuration > 0) {
                    if (workingTime < workingDuration) {
                        this.workingTime++;
                        energy.consumeEnergyIgnoreCondition();
                    } else {
                        outputRecipe(recipe, level, pos);
                    }
                } else {
                    workingTime = 0;
                    workingDuration = recipe.getPressingTime();
                    state = super.setNewBlockState(level, pos, state, MetalPressBlock.LIT, true);
                }
            } else state = turnOff(level, pos, state);
        } else state = turnOff(level, pos, state);
        setChanged(level, pos, state);
    }

    @Override
    protected boolean canTakeItem(int index) {
        return index == 2;
    }

    @Override
    public boolean canPlaceItem(int pIndex,@Nonnull ItemStack pStack) {
        return pIndex == 1 ? pStack.is(ModItemTags.GENERAL_PRESS_MOLDS.getMainTag()) : pIndex == 0 && !pStack.is(ModItemTags.GENERAL_PRESS_MOLDS.getMainTag());
    }

    private BlockState turnOff(Level level, BlockPos pos, BlockState state) {
        this.resetTime();
        return super.setNewBlockState(level, pos, state, MetalPressBlock.LIT, false);
    }

    private void outputRecipe(@Nonnull MetalPressingRecipe recipe, Level level, BlockPos pos) {
        super.items.get(0).shrink(1);
        if (super.items.get(2).isEmpty()) {
            super.items.set(2, recipe.assemble(this).copy());
        } else {
            this.items.get(2).grow(1);
        }
        if (recipe.isConsumeMold()) items.get(1).shrink(1);

        this.resetTime();

        level.playSound(null, pos, ModSoundEvents.METAL_PRESS_USED, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
