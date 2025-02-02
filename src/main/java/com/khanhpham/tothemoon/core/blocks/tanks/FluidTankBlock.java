package com.khanhpham.tothemoon.core.blocks.tanks;

import com.khanhpham.tothemoon.core.blocks.BaseEntityBlock;
import com.khanhpham.tothemoon.core.blocks.HasCustomBlockItem;
import com.khanhpham.tothemoon.init.ModBlockEntities;
import com.khanhpham.tothemoon.utils.helpers.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class FluidTankBlock extends BaseEntityBlock<FluidTankBlockEntity> implements HasCustomBlockItem {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 15, 0, 16, 16, 16),
            Block.box(0, 1, 0, 1, 15, 16),
            Block.box(15, 1, 0, 16, 15, 16),
            Block.box(5, 16, 5, 11, 18, 11),
            Block.box(1, 1, 0, 15, 15, 1),
            Block.box(1, 1, 15, 15, 15, 16),
            Block.box(0, 0, 0, 16, 1, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public FluidTankBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected BlockEntityType<FluidTankBlockEntity> getBlockEntityType() {
        return ModBlockEntities.FLUID_TANK.get();
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof FluidTankBlockEntity fluidTank) {
            ItemStack handItem = pPlayer.getItemInHand(pHand);
            if (handItem.getItem() instanceof BucketItem bucketItem) {
                if (fluidTank.isFluidSame(bucketItem.getFluid())) {
                    fluidTank.tank.fill(new FluidStack(((BucketItem) handItem.getItem()).getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    if (!pPlayer.isCreative()) pPlayer.setItemInHand(pHand, new ItemStack(Items.BUCKET));
                    return InteractionResult.CONSUME;
                }
            } else {
                LazyOptional<IFluidHandlerItem> fluidHandler = handItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                if (fluidHandler.isPresent()) {
                    fluidTank.tank.fill(fluidHandler.map(tank -> tank.drain(new FluidStack(fluidTank.tank.getFluid(), fluidTank.getTank().getSpace()), IFluidHandler.FluidAction.EXECUTE)).get(), IFluidHandler.FluidAction.EXECUTE);
                    return InteractionResult.CONSUME;
                }
            }
        }
        /*if (pLevel.getBlockEntity(pPos) instanceof FluidTankBlockEntity fluidTank) {
            ItemStack handItem = pPlayer.getMainHandItem();

            //FILL/DRAIN BUCKET ITEM
            if (handItem.getItem() instanceof BucketItem bucketItem) {
                //fill to bucket
                if (isFluidEmpty(bucketItem.getFluid()) && fluidTank.canDrainToExternal(bucketItem.getFluid())) {
                    SoundEvent fillBucketSound = bucketItem.getFluid().getPickupSound().isPresent() ? bucketItem.getFluid().getPickupSound().get() : SoundEvents.BUCKET_EMPTY;
                    fluidTank.tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, ModUtils.getBucketItem(fluidTank.getFluid()));
                    // return CauldronInteraction.fillBucket(pState, pLevel, pPos, pPlayer, pHand, handItem, ModUtils.getBucketItem(fluidTank.getFluid()), this::always, fillBucketSound);
                } else
                    //drain bucket to tank
                    if (!isFluidEmpty(bucketItem.getFluid()) && fluidTank.canFilledFromBucket(bucketItem.getFluid())) {
                        fluidTank.tank.fill(new FluidStack(bucketItem.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                        pPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
                        // return CauldronInteraction.fillBucket(pState, pLevel, pPos, pPlayer, pHand, handItem, new ItemStack(Items.BUCKET), this::always, SoundEvents.BUCKET_EMPTY);
                    }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            } /*else {
                //FILL/DRAIN EXTERNAL TANK ITEM
                LazyOptional<IFluidHandler> fluidCap = handItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                if (fluidCap.isPresent()) {
                    IFluidHandler fluidHandler = fluidCap.orElse(null);
                    var fluidInTank = fluidHandler.getFluidInTank(0).getFluid();
                    if (fluidTank.canFillFromExternalTank(fluidInTank)) {
                        fluidTank.tank.fill(fluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    }

                    if (isFluidEmpty(fluidInTank) && fluidTank.canDrainToExternal()) {
                        fluidTank.tank.drain(fluidHandler.fill(new FluidStack(fluidInTank, 1000), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    }

                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }}*/


        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    @Override
    public FluidTankBlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FluidTankBlockEntity(pPos, pState);
    }

    @Override
    public BlockItem getRawItem() {
        return new TankBlockItem(this, FluidTankBlockEntity.TANK_CAPACITY);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        ModUtils.loadFluidToBlock(pLevel, pPos, pStack);
    }
}
