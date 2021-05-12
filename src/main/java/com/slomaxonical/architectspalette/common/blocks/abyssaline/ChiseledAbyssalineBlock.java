package com.slomaxonical.architectspalette.common.blocks.abyssaline;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.ToIntFunction;

import static com.slomaxonical.architectspalette.common.blocks.abyssaline.NewAbyssalineBlock.CHARGED;

public class ChiseledAbyssalineBlock extends Block implements IAbyssalineChargeable {

	public final static Item KEY = Items.HEART_OF_THE_SEA;
	private final static BlockPos OFFSET = new BlockPos(0, 0, 0);

	public boolean outputsChargeFrom(BlockState stateIn, Direction faceIn) {
		return stateIn.get(CHARGED);
	}

	public boolean isCharged(BlockState stateIn) {
		return stateIn.get(CHARGED);
	}

	public BlockPos getSourceOffset(BlockState stateIn) {
		return OFFSET;
	}

	public static final IntProperty LIGHT = IntProperty.of("light", 0, 20);

	public ChiseledAbyssalineBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(CHARGED, false));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
		return state;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState();
	}

//	@Override
//	public int getLuminance(BlockState state, BlockView world, BlockPos pos) {
//		return this.isCharged(state) ? 14 : 0;
//	}
public static ToIntFunction<BlockState> getLuminance() {
	return blockState -> blockState.get(AbyssalineBlock.CHARGED) ? 14 : 0;
}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(LIGHT,CHARGED);
	}


	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult traceResult) {
		ItemStack stack = player.getStackInHand(hand);
		if (!this.isCharged(state) && stack.getItem() == KEY) {
			if(!player.isCreative())
				stack.decrement(1);
			world.setBlockState(pos, this.getStateWithCharge(state, true));
			System.out.println("Charged");
			world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_ACTIVATE, SoundCategory.BLOCKS, 0.5F, new Random().nextFloat() * 0.2F + 0.8F);
			return ActionResult.CONSUME;
		}
		else if (this.isCharged(state) && stack.isEmpty()) {
			world.setBlockState(pos, this.getStateWithCharge(state, false));
			System.out.println("UNCharged");
			world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 0.5F, new Random().nextFloat() * 0.2F + 0.8F);
			if(!player.isCreative() || (player.inventory.count(KEY) <= 0))
				player.giveItemStack(new ItemStack(KEY));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
}
