package com.khanhpham.tothemoon.core.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.khanhpham.tothemoon.JsonNames;
import com.khanhpham.tothemoon.compat.jei.DisplayRecipe;
import com.khanhpham.tothemoon.core.items.HammerItem;
import com.khanhpham.tothemoon.core.menus.containers.WorkbenchCraftingContainer;
import com.khanhpham.tothemoon.init.ModItems;
import com.khanhpham.tothemoon.init.ModRecipes;
import com.khanhpham.tothemoon.utils.helpers.ModUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class WorkbenchCraftingRecipe implements DisplayRecipe<WorkbenchCraftingContainer> {
    private static final ResourceLocation LOCATION = ModUtils.modLoc("workbench_crafting");
    public static final RecipeType<WorkbenchCraftingRecipe> RECIPE_TYPE = ModUtils.registerRecipeType(LOCATION);
    private final Ingredient hammer;
    private final Ingredient extraInput;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final ResourceLocation recipeId;

    public WorkbenchCraftingRecipe(ResourceLocation pId, NonNullList<Ingredient> pRecipeItems, ItemStack pResult, Ingredient hammer, Ingredient extraInput) {
        this.hammer = hammer;
        this.extraInput = extraInput;
        this.ingredients = pRecipeItems;
        this.result = pResult;
        this.recipeId = pId;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(hammer);
        ingredients.add(extraInput);
        ingredients.addAll(this.ingredients);
        return ingredients;
    }

    @Override
    public boolean matches(WorkbenchCraftingContainer pContainer, Level pLevel) {
        return pContainer.isRecipeMatches(this.hammer, this.extraInput, this.ingredients);
    }

    @Override
    public ItemStack assemble(WorkbenchCraftingContainer pContainer) {
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
        return this.recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WORKBENCH_CRAFTING;
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    public Ingredient getHammerIngredient() {
        return this.hammer;
    }

    public Ingredient getExtraIngredient() {
        return this.extraInput;
    }


    public static final class Serializer extends SimpleRecipeSerializer<WorkbenchCraftingRecipe> {
        @Override
        protected String getRecipeName() {
            return LOCATION.getPath();
        }

        @Override
        public WorkbenchCraftingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient hammer = this.getHammerIngredient(pSerializedRecipe);
            Ingredient extraRequirement = super.getShortenIngredient(GsonHelper.getAsString(pSerializedRecipe, "extra", ""));
            NonNullList<Ingredient> nonNullList = this.extractJsonToArray(pSerializedRecipe.getAsJsonArray("craftingPattern"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, JsonNames.RESULT));
            return new WorkbenchCraftingRecipe(pRecipeId, nonNullList, result, hammer, extraRequirement);
        }

        private Ingredient getHammerIngredient(JsonObject pSerializedJson) {
            if (pSerializedJson.has("hammer")) {
                if (pSerializedJson.get("hammer").isJsonPrimitive()) {
                    String hammerName = pSerializedJson.get("hammer").getAsString();
                    return super.isTag(hammerName) ? super.getShortenIngredient(hammerName) : this.getHammers(hammerName);
                }
            }
            return Ingredient.of(HammerItem.getStrongerHammers(ModItems.WOODEN_HAMMER.get()));
        }

        private Ingredient getHammers(String hammerName) {
            Optional<Item> hammerItem = Registry.ITEM.getOptional(new ResourceLocation(hammerName));
            if (hammerItem.isPresent()) {
                return Ingredient.of(HammerItem.getStrongerHammers(hammerItem.get()));
            }
            throw new JsonSyntaxException(hammerName + " is not a valid item name");
        }

        private NonNullList<Ingredient> extractJsonToArray(JsonElement jsonObject) {
            if (!jsonObject.isJsonNull()) {
                if (jsonObject.isJsonArray()) {
                    JsonArray array = jsonObject.getAsJsonArray();
                    if (array.size() == 25) {
                        NonNullList<Ingredient> ingredients = NonNullList.withSize(25, Ingredient.EMPTY);
                        int index = 0;
                        for (JsonElement jsonElement : array) {
                            ingredients.set(index, super.getShortenIngredient(jsonElement.getAsString()));
                            index++;
                        }
                        return ingredients;
                    } else
                        throw new JsonSyntaxException("Pattern should have 25 items/tag, but " + array.size() + " was read");
                }
                throw new JsonSyntaxException("Expected to find an array");
            }
            throw new JsonSyntaxException("Object not found");
        }

        private NonNullList<Ingredient> getIngredientFromNetwork(FriendlyByteBuf buffer) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(25, Ingredient.EMPTY);
            for (int i = 0; i < 25; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            return ingredients;
        }

        @Override
        public WorkbenchCraftingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient hammer = Ingredient.fromNetwork(pBuffer);
            Ingredient extraInput = Ingredient.fromNetwork(pBuffer);
            NonNullList<Ingredient> ingredients = this.getIngredientFromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            return new WorkbenchCraftingRecipe(pRecipeId, ingredients, result, hammer, extraInput);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, WorkbenchCraftingRecipe pRecipe) {
            pRecipe.hammer.toNetwork(pBuffer);
            pRecipe.extraInput.toNetwork(pBuffer);
            this.sendIngredientsToNetwork(pRecipe.ingredients, pBuffer);
            pBuffer.writeItem(pRecipe.result);
        }

        private void sendIngredientsToNetwork(NonNullList<Ingredient> ingredients, FriendlyByteBuf buffer) {
            for (int i = 0; i < 25; i++) {
                ingredients.get(i).toNetwork(buffer);
            }
        }
    }
}
