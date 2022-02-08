package com.khanhpham.tothemoon.utils.containers;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

/**
 * @see net.minecraft.world.inventory.AbstractFurnaceMenu
 * @see net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen
 */
public abstract class BaseContainer extends AbstractContainerMenu {

    protected final int endInvIndex = 27;
    protected final int endHotBarIndex = 36;
    protected final int inventorySize;
    protected final Inventory playerInventory;

    protected BaseContainer(@Nullable MenuType<?> pMenuType, Container externalContainer, Inventory playerInventory, int pContainerId) {
        super(pMenuType, pContainerId);
        this.inventorySize = externalContainer.getContainerSize();
        this.playerInventory = playerInventory;
    }

    protected void addPlayerInventorySlots(int beginX, int beginY) {
        int i,j;

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                this.addSlot(playerInventory, j + i * 9 + 9, beginX + j * 18, beginY + i * 18);
            }
        }

        for (i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, beginX + i * 18, beginY + 58));
        }
    }

    protected void addSlot(Container container, int index, int x, int y) {
        super.addSlot(new Slot(container, index, x, y));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return !(pPlayer instanceof FakePlayer);
    }
}
