/*
 * Copyright (C) 2017-2023 Illusive Soulworks
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.consecration.common.integration;

import java.util.function.BiFunction;
import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.ConsecrationImc;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

public class TConstructModule extends AbstractCompatibilityModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", ConsecrationImc.HOLY_ATTACK.getId(),
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {
          Entity source = damageSource.getDirectEntity();

          if (source instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) source).getMainHandItem();
            MaterialIdNBT nbt = MaterialIdNBT.from(stack);
            for (MaterialVariantId material : nbt.getMaterials()) {

              if (ConsecrationApi.getInstance().isHolyMaterial(material.getId().getPath())) {
                return true;
              }
            }
          }
          return false;
        });
  }
}
