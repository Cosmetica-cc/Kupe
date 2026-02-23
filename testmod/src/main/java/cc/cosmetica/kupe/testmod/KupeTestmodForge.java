/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "kupe_testmod", name = "Kupe GUI Test Mod", version = "1.0.0")
public class KupeTestmodForge {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        System.out.println("Kupe Test Mod Initialised");
    }
}
