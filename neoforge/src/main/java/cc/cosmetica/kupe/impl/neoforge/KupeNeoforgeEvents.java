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

package cc.cosmetica.kupe.impl.neoforge;

import cc.cosmetica.kupe.Kupe;
import cc.cosmetica.kupe.impl.fakeplayer.FakePlayerGuiRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid= Kupe.MOD_ID)
public class KupeNeoforgeEvents {
    @SubscribeEvent
    public static void registerPip(RegisterPictureInPictureRenderersEvent event) {
        event.register(FakePlayerGuiRenderer.State.class, FakePlayerGuiRenderer::new);
    }
}
