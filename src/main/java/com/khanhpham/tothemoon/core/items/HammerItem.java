package com.khanhpham.tothemoon.core.items;

import com.khanhpham.tothemoon.ToTheMoon;
import com.khanhpham.tothemoon.utils.helpers.RegistryEntries;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class HammerItem extends HandheldItem {
    public static final List<HammerItem> HAMMERS = new LinkedList<>();
    private static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public HammerItem(int durability) {
        this(new Properties().tab(ToTheMoon.TAB).durability(durability));
    }

    public HammerItem(Properties pProperties) {
        super(pProperties);
        HAMMERS.add(this);
    }

    @SuppressWarnings("deprecation")
    public static HammerItem[] getStrongerHammers(Item item) {
        if (item instanceof HammerItem hammerItem) {
            return HAMMERS.stream().filter(hammer -> hammerItem.getMaxDamage() <= hammer.getMaxDamage()).toArray(HammerItem[]::new);
        }
        throw new IllegalStateException(String.format("Item %s is not a HammerItem", RegistryEntries.ITEM.getKey(item)));
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        if (copy.hurt(1, RANDOM_SOURCE, null)) {
            return ItemStack.EMPTY;
        }
        return copy;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }
}
