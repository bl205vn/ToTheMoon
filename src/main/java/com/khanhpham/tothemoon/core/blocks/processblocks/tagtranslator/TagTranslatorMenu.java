package com.khanhpham.tothemoon.core.blocks.processblocks.tagtranslator;

import com.google.common.collect.Lists;
import com.khanhpham.tothemoon.core.recipes.TagTranslatingRecipe;
import com.khanhpham.tothemoon.init.ModBlocks;
import com.khanhpham.tothemoon.init.ModMenuTypes;
import com.khanhpham.tothemoon.utils.helpers.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public final class TagTranslatorMenu extends AbstractContainerMenu {
    final Slot inputSlot;
    final Slot resultSlot;
    final ResultContainer resultContainer = new ResultContainer();
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    Runnable slotUpdateListener = () -> {
    };
    private List<ItemStack> items = Lists.newArrayList();
    private final Consumer<TagTranslatingRecipe> recipeToItemsConverter;
    private ItemStack input = ItemStack.EMPTY;
    public final Container container = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
            slotUpdateListener.run();
        }
    };

    public TagTranslatorMenu(int pContainerId, Inventory playerInventory) {
        this(ModMenuTypes.TAG_TRANSLATOR, pContainerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public TagTranslatorMenu(int pContainerId, Inventory playerInventory, ContainerLevelAccess access) {
        this(ModMenuTypes.TAG_TRANSLATOR, pContainerId, playerInventory, access);
    }

    public TagTranslatorMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(pMenuType, pContainerId);

        this.inputSlot = this.addSlot(new Slot(container, 0, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            public boolean mayPlace(ItemStack p_40362_) {
                return false;
            }

            public void onTake(Player player, ItemStack item) {
                item.onCraftedBy(player.level, player, item.getCount());
                ItemStack itemstack = inputSlot.remove(1);
                if (!itemstack.isEmpty()) {
                    setupResultSlot();
                }
                super.onTake(player, item);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
        this.access = access;
        this.level = playerInventory.player.level;
        this.recipeToItemsConverter = tagTranslatingRecipe -> this.items = ModUtils.getItemsForTags(tagTranslatingRecipe.getTag(), this.inputSlot.getItem());
    }

    private void setupResultSlot() {
        if (!this.items.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            ItemStack selectedItem = this.items.get(this.selectedRecipeIndex.get());
            this.resultSlot.set(selectedItem);
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    private boolean isValidRecipeIndex(int pRecipeIndex) {
        return pRecipeIndex >= 0 && pRecipeIndex < this.items.size();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, ModBlocks.TAG_TRANSLATOR.get());
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<ItemStack> getPresentItems() {
        return this.items;
    }


    public int getNumRecipes() {
        return this.items.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.items.isEmpty();
    }

    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (this.isValidRecipeIndex(pId)) {
            this.selectedRecipeIndex.set(pId);
            this.setupResultSlot();
        }

        return true;
    }

    public void slotsChanged(Container pInventory) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.setupRecipeList(pInventory, itemstack);
        }

    }

    private void setupRecipeList(Container pContainer, ItemStack pStack) {
        this.items.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!pStack.isEmpty()) {
            this.level.getRecipeManager().getRecipeFor(TagTranslatingRecipe.RECIPE_TYPE, pContainer, this.level).ifPresent(this.recipeToItemsConverter);
        }
    }

    public void registerUpdateListener(Runnable pListener) {
        this.slotUpdateListener = pListener;
    }

    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != this.resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 1) {
                item.onCraftedBy(itemstack1, pPlayer.level, pPlayer);
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(TagTranslatingRecipe.RECIPE_TYPE, new SimpleContainer(itemstack1), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 2 && pIndex < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 29 && pIndex < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((p_40313_, p_40314_) -> {
            this.clearContainer(pPlayer, this.container);
        });
    }
}


