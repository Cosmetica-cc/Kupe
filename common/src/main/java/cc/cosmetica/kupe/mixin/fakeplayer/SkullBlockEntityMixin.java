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

package cc.cosmetica.kupe.mixin.fakeplayer;

import cc.cosmetica.kupe.impl.fakeplayer.PlayerUtils;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

/**
 * Refresh the profile cache for Kupe GUI's {@link cc.cosmetica.kupe.api.gui.GUIPlayer}.
 */
@Mixin(SkullBlockEntity.class)
public class SkullBlockEntityMixin {
    @Inject(at = @At("HEAD"), method="setup")
    private static void onSetMinecraftSession(Services services, Executor executor, CallbackInfo ci) {
        PlayerUtils.createNewCache(services.sessionService());
    }
}
