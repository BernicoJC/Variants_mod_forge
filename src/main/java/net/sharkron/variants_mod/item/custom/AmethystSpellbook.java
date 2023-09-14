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
import net.sharkron.variants_mod.entity.custom.AmethystShardBolt;

public class AmethystSpellbook extends Item{
    public AmethystSpellbook(Properties pProperties){
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int manaCost = 5;
        int maxUseBeforeBroken = itemstack.getMaxDamage() - manaCost;

        if (level.isClientSide || itemstack.getDamageValue() >= maxUseBeforeBroken) {
            return InteractionResultHolder.fail(itemstack);
        }
        else{
            Vec3 look = player.getLookAngle(); // Get the player's look vector
            // double spawnX = player.getX();
            // double spawnY = player.getY() + player.getEyeHeight();
            // double spawnZ = player.getZ();

            AmethystShardBolt proj1 = new AmethystShardBolt(level, player, look.x, look.y, look.z);

            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.addFreshEntity(proj1);
            player.getCooldowns().addCooldown(this, 3);
            itemstack.hurtAndBreak(manaCost, player, 
                    p -> p.broadcastBreakEvent(hand));

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean canBeDepleted(){
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Consumes 5 Mana"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
