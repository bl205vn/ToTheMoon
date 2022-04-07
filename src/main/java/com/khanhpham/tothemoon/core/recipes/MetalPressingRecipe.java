package com.khanhpham.tothemoon.core.recipes;

import com.google.gson.JsonObject;
import com.khanhpham.tothemoon.JsonNames;
import com.khanhpham.tothemoon.core.machines.metalpress.MetalPressBlockEntity;
import com.khanhpham.tothemoon.init.ModBlocks;
import com.khanhpham.tothemoon.init.ModRecipes;
import com.khanhpham.tothemoon.utils.ModUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MetalPressingRecipe implements Recipe<MetalPressBlockEntity> {
    public static final RecipeType<MetalPressingRecipe> RECIPE_TYPE = ModUtils.registerRecipeType(ModRecipeLocations.METAL_PRESSING.getPath());

    private final Ingredient ingredient;
    private final Ingredient pressPlateIngredient;
    private final ItemStack result;
    private final int pressingTime;
    private final ResourceLocation recipeId;


    public MetalPressingRecipe(ResourceLocation recipeId, Ingredient ingredient, Ingredient pressPlateIngredient, int pressingTime, ItemStack result) {
        this.ingredient = ingredient;
        this.pressPlateIngredient = pressPlateIngredient;
        this.result = result;
        this.pressingTime = pressingTime;
        this.recipeId = recipeId;
    }

    public int getPressingTime() {
        return pressingTime;
    }

    @Override
    public boolean matches(MetalPressBlockEntity pContainer, Level pLevel) {
        return this.ingredient.test(pContainer.items.get(0)) && this.pressPlateIngredient.test(pContainer.items.get(1)) ;
   }

    @Override
    public ItemStack assemble(MetalPressBlockEntity pContainer) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.METAL_PRESSING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    public static final class Serializer extends BaseRecipeSerializer<MetalPressingRecipe> {

        public Serializer() {
            super.setRegistryName(ModRecipeLocations.METAL_PRESSING);
        }

        @Override
        protected ItemLike getIcon() {
            return ModBlocks.METAL_PRESS.get();
        }


        /**
         * @see net.minecraft.world.item.crafting.SimpleCookingSerializer
         */
        @Override
        public MetalPressingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(pSerializedRecipe.getAsJsonObject(JsonNames.INGREDIENT));
            Ingredient press = Ingredient.fromJson(pSerializedRecipe.getAsJsonObject(JsonNames.MOLD));
            ItemStack result = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject(JsonNames.RESULT));
            int pressingTime = GsonHelper.getAsInt(pSerializedRecipe, JsonNames.PROCESS_TIME, 100);
            return new MetalPressingRecipe(pRecipeId, ingredient, press, pressingTime, result);
        }

        @Override
        public MetalPressingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            Ingredient press = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            int pressingTime = pBuffer.readInt();
            return new MetalPressingRecipe(pRecipeId, ingredient, press, pressingTime, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MetalPressingRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pRecipe.pressPlateIngredient.toNetwork(pBuffer);
            pBuffer.writeItemStack(pRecipe.result, false);
            pBuffer.writeInt(pRecipe.pressingTime);
        }
    }
}
