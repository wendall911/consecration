package com.illusivesoulworks.consecration.common;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import com.illusivesoulworks.consecration.common.registry.ConsecrationRegistry;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ConsecrationEvents {

  public static void createCampfireArrow(Player player, ItemStack stack, BlockPos pos,
                                         BiConsumer<Player, ItemStack> giver) {

    if (stack.getItem() == Items.ARROW) {
      Block block = player.level.getBlockState(pos).getBlock();

      if (block == Blocks.CAMPFIRE || block == Blocks.SOUL_CAMPFIRE) {
        stack.shrink(1);
        giver.accept(player, new ItemStack(ConsecrationRegistry.FIRE_ARROW.get()));
      }
    }
  }

  public static float onDamaged(LivingEntity livingEntity, DamageSource source, float damage) {

    if (!livingEntity.getLevel().isClientSide()) {
      float[] result = {damage};
      ConsecrationApi api = ConsecrationApi.getInstance();
      api.getUndying(livingEntity)
          .ifPresentOrElse(undying -> result[0] = undying.onDamaged(source, damage),
              () -> {
                if (source.getDirectEntity() instanceof LivingEntity attacker) {
                  Optional<? extends IUndying> attackerUndying = api.getUndying(attacker);

                  attackerUndying.ifPresent(undying -> {
                    int level = api.getHolyProtectionLevel(attacker, livingEntity, source);

                    if (level > 0 &&
                        livingEntity.getLevel().getRandom().nextFloat() < 0.15F * (float) level) {
                      undying.setVulnerableDuration(
                          ConsecrationConfig.CONFIG.holyVulnerableDuration.get());
                    }
                  });
                }
              });
      return result[0];
    }
    return damage;
  }
}
