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

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.model.player.PlayerModel;

/**
 * Player model with elytra.
 */
public class ExtendedPlayerModel extends PlayerModel {
    public ExtendedPlayerModel(ModelPart modelPart, ElytraModel elytraModel, PlayerCapeModel capeModel, boolean bl) {
        super(modelPart, bl);
        this.elytra = elytraModel;
        this.cape = capeModel;
    }

    private final ElytraModel elytra;
    private final PlayerCapeModel cape;

    public ElytraModel getElytra() {
        return this.elytra;
    }

    public PlayerCapeModel getCape() {
        return this.cape;
    }
}
