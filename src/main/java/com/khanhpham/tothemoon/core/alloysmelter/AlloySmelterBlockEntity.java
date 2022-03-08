package com.khanhpham.tothemoon.core.alloysmelter;

import com.khanhpham.tothemoon.ModUtils;
import com.khanhpham.tothemoon.init.ModBlockEntityTypes;
import com.khanhpham.tothemoon.init.ModItems;
import com.khanhpham.tothemoon.utils.energy.Energy;
import com.khanhpham.tothemoon.utils.recipes.AlloySmeltingRecipe;
import com.khanhpham.tothemoon.utils.te.EnergyItemCapableBlockEntity;
import com.khanhpham.tothemoon.utils.te.energygenerator.AbstractEnergyGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * @see AbstractEnergyGeneratorBlockEntity
 */
public class AlloySmelterBlockEntity extends EnergyItemCapableBlockEntity {
    public static final int MENU_SIZE = 3;
    public static final TranslatableComponent LABEL = ModUtils.translate("gui.tothemoon.alloy_smelter");
    public static final int DATA_CAPACITY = 4;
    private int workingTime;
    private int workingDuration;
    private final int[] datas = new int[]{workingTime, workingDuration, energy.getEnergyStored(), energy.getMaxEnergyStored()};
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return datas[pIndex];
        }

        @Override
        public void set(int pIndex, int pValue) {
        }

        @Override
        public int getCount() {
            return datas.length;
        }
    };

    public AlloySmelterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, Energy energy, @NotNull Component label, int containerSize) {
        super(pType, pWorldPosition, pBlockState, energy, label, containerSize);
    }

    public AlloySmelterBlockEntity(BlockPos blockPos, BlockState state) {
        this(ModBlockEntityTypes.ALLOY_SMELTER, blockPos, state, new Energy(175000, 500, 1) {
            @Override
            public boolean canExtract() {
                return false;
            }
        }, LABEL, MENU_SIZE);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlloySmelterBlockEntity blockEntity) {

        blockEntity.receiveEnergyFromOther(level, pos);

        if (!blockEntity.energy.isEmpty()) {
            ItemStack resultSlot = blockEntity.items.get(2);
            AlloySmeltingRecipe recipe = level.getRecipeManager().getRecipeFor(AlloySmeltingRecipe.RECIPE_TYPE, blockEntity, level).orElse(null);
            if (recipe != null) {
                if (!resultSlot.isEmpty()) {
                    blockEntity.processRecipe(level, recipe);
                    blockEntity.setNewBlockState(level, pos, state, AlloySmelterBlock.LIT, Boolean.TRUE);
                } else {
                    state = blockEntity.setNewBlockState(level, pos, state, AlloySmelterBlock.LIT, Boolean.FALSE);
                }

                if (resultSlot.is(recipe.getResultItem().getItem()) && resultSlot.getCount() <= 64 - recipe.getResultItem().getCount()) {
                    blockEntity.processRecipe(level, recipe);
                    state = blockEntity.setNewBlockState(level, pos, state, AlloySmelterBlock.LIT, Boolean.TRUE);
                } else {
                    state = blockEntity.setNewBlockState(level, pos, state, AlloySmelterBlock.LIT, Boolean.FALSE);
                }
            } else {
                state = blockEntity.setNewBlockState(level, pos, state, AlloySmelterBlock.LIT, Boolean.FALSE);
            }
        }

        if (!blockEntity.energy.isEmpty() && blockEntity.workingTime > 0) {
            blockEntity.workingTime--;
            blockEntity.energy.extractEnergy();
        }

        if (blockEntity.energy.isEmpty()) {
            state = blockEntity.setNewBlockState(level, pos, state, AlloySmelterBlock.LIT, Boolean.FALSE);
        }

        markDirty(level, pos, state);
    }

    //TODO : this
    private void processRecipe(Level level, AlloySmeltingRecipe recipe) {
        //if (canProcess()) {
        if (recipe.matches(this, level)) {
            System.out.println("RECIPE MATCHED, processing");
            if (workingTime <= 0) {
                this.energy.extractEnergy();
                this.workingDuration = recipe.getAlloyingTime();
                this.workingTime = 0;
            }
        } else {
            this.workingTime = 0;
            this.workingDuration = 0;
        }
        //}
    }

    /*private boolean canProcess() {
        return workingTime <= 0;
    }*/

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory) {
        return new AlloySmelterMenu(containerId, playerInventory, this, this.data);
    }
}
