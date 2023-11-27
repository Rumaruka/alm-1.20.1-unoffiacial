package me.haydenb.assemblylinemachines.crafting;

import com.google.gson.JsonObject;
import me.haydenb.assemblylinemachines.block.machines.BlockGreenhouse.TEGreenhouse;
import me.haydenb.assemblylinemachines.plugins.jei.RecipeCategoryBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

import java.util.List;

public class GreenhouseFertilizerCrafting implements Recipe<TEGreenhouse>, RecipeCategoryBuilder.IRecipeCategoryBuilder {

	public static final RecipeType<GreenhouseFertilizerCrafting> FERTILIZER_RECIPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "assemblylinemachines:greenhouse_fertilizer";
		}
	};

	public static final GreenhouseFertilizerSerializer SERIALIZER = new GreenhouseFertilizerSerializer();

	private final ResourceLocation recipeId;
	private final Lazy<Ingredient> fertilizer;
	public final int multiplication;
	public final int usesPerItem;

	public GreenhouseFertilizerCrafting(ResourceLocation recipeId, Lazy<Ingredient> fertilizer, int multiplication, int usesPerItem) {
		this.recipeId = recipeId;
		this.fertilizer = fertilizer;
		this.multiplication = multiplication;
		this.usesPerItem = usesPerItem;
	}


	@Override
	public boolean matches(TEGreenhouse pContainer, Level pLevel) {
		return fertilizer.get().test(pContainer.getItem(2));
	}

	@Override
	public ItemStack assemble(TEGreenhouse pContainer, RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return recipeId;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return FERTILIZER_RECIPE;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public List<Ingredient> getJEIComponents() {
		return List.of(fertilizer.get());
	}

	public static class GreenhouseFertilizerSerializer implements RecipeSerializer<GreenhouseFertilizerCrafting>{

		@Override
		public GreenhouseFertilizerCrafting fromJson(ResourceLocation recipeId, JsonObject json) {
			Lazy<Ingredient> input = Lazy.of(() -> Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "fertilizer")));
			int multiplication = GsonHelper.isValidNode(json, "outputMultiplication") ? GsonHelper.getAsInt(json, "outputMultiplication") : 1;
			int usesPerItem = GsonHelper.isValidNode(json, "usesPerItem") ? GsonHelper.getAsInt(json, "usesPerItem") : 1;

			return new GreenhouseFertilizerCrafting(recipeId, input, multiplication, usesPerItem);
		}

		@Override
		public GreenhouseFertilizerCrafting fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient input = Ingredient.fromNetwork(buffer);
			int multiplication = buffer.readInt();
			int usesPerItem = buffer.readInt();

			return new GreenhouseFertilizerCrafting(recipeId, Lazy.of(() -> input), multiplication, usesPerItem);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, GreenhouseFertilizerCrafting recipe) {
			recipe.fertilizer.get().toNetwork(buffer);
			buffer.writeInt(recipe.multiplication);
			buffer.writeInt(recipe.usesPerItem);
		}

	}
}