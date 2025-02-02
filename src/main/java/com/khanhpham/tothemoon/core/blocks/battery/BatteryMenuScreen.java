package com.khanhpham.tothemoon.core.blocks.battery;

import com.khanhpham.tothemoon.core.abstracts.BaseMenuScreen;
import com.khanhpham.tothemoon.utils.helpers.ModUtils;
import com.khanhpham.tothemoon.utils.text.TextUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

public class BatteryMenuScreen extends BaseMenuScreen<BatteryMenu> {
    public static final ResourceLocation GUI = ModUtils.modLoc("textures/gui/battery.png");

    public BatteryMenuScreen(BatteryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, GUI);
        setImageSize(176, 183);
    }

    @Override
    protected void renderExtra(PoseStack pPoseStack) {
        blit(pPoseStack, leftPos + 15, topPos + 78, 14, 183, menu.getEnergyBar() + 1, 12);
        super.font.draw(pPoseStack, TextUtils.translateEnergy(menu.getEnergyStored()), leftPos + 36, topPos + 20, Objects.requireNonNull(ChatFormatting.GREEN.getColor()));
    }
}
