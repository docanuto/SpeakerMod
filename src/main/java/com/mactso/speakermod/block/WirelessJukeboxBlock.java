package com.mactso.speakermod.block;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.mactso.speakermod.blockentities.WirelessJukeboxBlockEntity;
import com.mactso.speakermod.config.MyConfig;
import com.mactso.speakermod.init.BlockEntityInit;
import com.mactso.speakermod.init.BlockInit;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WirelessJukeboxBlock extends JukeboxBlock {

	public WirelessJukeboxBlock(BlockBehaviour.Properties properties) {

    super(properties);
	int x = 3;
    
	}

	@Nullable	
	@Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
		int x = 3;
		return BlockEntityInit.WIRELESS_JUKEBOX.get().create(pos, blockState);
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		int x = 3;
	    return MyConfig.getJukeboxLightLevel();
	}

	
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		int x = 3;
		return level.isClientSide ? null : 
	    	  createTickerHelper(blockEntityType, BlockEntityInit.WIRELESS_JUKEBOX.get(), WirelessJukeboxBlockEntity::serverTick);
	      // either make two tickers or call it here and check which side in ticker.
	      //	      return  createTickerHelper(blockEntityType, ModTileEntities.WIRELESS_JUKEBOX, WirelessJukeboxTileEntity::serverTick);
// TurtyWurty code below.  $0?  $1?
	      //	        return level.isClientSide() ? null : ($0, $1, $2, blockEntity) -> {
//	            if(blockEntity instanceof CrusherBlockEntity crusher) {
//	                crusher.tick();
//	            }
//	        };
	}


	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
			InteractionHand handIn, BlockHitResult hit) {
	
		if (!(worldIn.isClientSide())) {
			ItemStack stack = player.getItemInHand(handIn);
	
			int discId= Item.getId(stack.getItem());
	
			//		if ((worldIn instanceof ServerWorld)) { } plays both sides I think.
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WirelessJukeboxBlockEntity) {
            	WirelessJukeboxBlockEntity wJTE = (WirelessJukeboxBlockEntity)tileentity;	  
    			if (stack.getItem() == BlockInit.WIRELESS_SPEAKER.get().asItem()) {
    				String jukeboxPos = "( " + pos.getX() + ", "+ pos.getY() + ", "+ pos.getZ() + " )";
     				stack.setHoverName (Component.literal(jukeboxPos));
    				worldIn.playSound(null, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 0.6f, 0.6f);
    			} else if (stack.getItem() instanceof RecordItem) {
	    			wJTE.playEvent((ServerLevel) worldIn, (Player) null, 1010, pos, discId);
	    			wJTE.startSpeakers(discId);
	    			MyConfig.debugMsg(1, pos, "Start New Disc");
    			} else if ((stack.isEmpty()) && player.isShiftKeyDown()){
    				int saveId = wJTE.getDiscId();
    				wJTE.stopSpeakers ();
	            	wJTE.playEvent((ServerLevel) worldIn, (Player) null, 1010, pos, 0);
    				MyConfig.debugMsg(1, pos, "Stop playing Disc");
    				wJTE.setDiscId(saveId);
    			} else {
    				wJTE.stopSpeakers ();
	            	wJTE.playEvent((ServerLevel) worldIn, (Player) null, 1010, pos, 0);
    				MyConfig.debugMsg(1, pos, "Stop playing Disc");
    			}
            }
		}
	
		return InteractionResult.SUCCESS;
	}	

	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
	    if (!state.is(newState.getBlock())) {
	        if (!worldIn.isClientSide) {
	            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
	            if (blockEntity instanceof WirelessJukeboxBlockEntity) {
	            	WirelessJukeboxBlockEntity wJTE = (WirelessJukeboxBlockEntity)blockEntity;	
	            	wJTE.playEvent((ServerLevel) worldIn, (Player) null, 1010, pos, 0);
	    			wJTE.stopSpeakers ();
	            }
	        }
	    	super.onRemove(state, worldIn, pos, newState, isMoving);
	    }
	    
	 }
	
}
