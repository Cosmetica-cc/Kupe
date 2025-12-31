/*
 * Copyright 2024, 2025 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.impl;

import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

/**
 * Player model with elytra.
 */
public class ExtendedPlayerModel<T extends LivingEntity> extends PlayerModel<T> {
    public ExtendedPlayerModel(ModelPart modelPart, ElytraModel<T> elytraModel, boolean bl) {
        super(modelPart, bl);
        this.elytra = elytraModel;
    }

    private final ElytraModel<T> elytra;

    public ElytraModel<T> getElytra() {
        return this.elytra;
    }
}
