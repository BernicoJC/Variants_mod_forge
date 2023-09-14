package net.sharkron.variants_mod.item.custom;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.sharkron.variants_mod.entity.custom.GenericBullet;
import net.sharkron.variants_mod.item.ModItems;

public class Handgun extends Item{

    public Handgun(Properties pProperties){
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (level.isClientSide || !hasAmmo(player)) {
            return InteractionResultHolder.fail(itemstack);
        }

        else{
            Vec3 look = player.getLookAngle(); // Get the player's look vector

            GenericBullet proj1 = new GenericBullet(level, player, look.x, look.y, look.z, 7.0F, 5, 6.0F, 7, 4.0F, 10);

            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1.0F, 1.0F);
            
            level.addFreshEntity(proj1); // adding the entity into the world level
            consumeAmmo(player);
            player.getCooldowns().addCooldown(this, 20);
            itemstack.hurtAndBreak(1, player, 
                    p -> p.broadcastBreakEvent(hand));

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private boolean hasAmmo(Player player){
        if(player.getAbilities().instabuild){
            return true;
        }

        for(int i = 0; i < player.getInventory().getContainerSize(); i++){
            ItemStack stack = player.getInventory().getItem(i); // the stack item that's currently being looked at
            if(!stack.isEmpty() && stack.is(ModItems.BOULETS.get())){
                return true;
            }
        }
        return false;
    }

    private void consumeAmmo(Player player){
        for(int i = 0; i < player.getInventory().getContainerSize(); i++){
            ItemStack stack = player.getInventory().getItem(i); // the stack item that's currently being looked at
            if(!stack.isEmpty() && stack.is(ModItems.BOULETS.get())){
                if (!player.getAbilities().instabuild) { // if not creative
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.getInventory().setItem(i, ItemStack.EMPTY); // Clear the slot if empty
                    }
                }
                return;
            }
        }
    }

    @Override
    public int getEnchantmentValue(){
        return 1;
    }

    @Override
    public boolean canBeDepleted(){
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Consumes Bullet"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
