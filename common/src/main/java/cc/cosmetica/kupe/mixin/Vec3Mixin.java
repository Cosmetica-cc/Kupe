package cc.cosmetica.kupe.mixin;

import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Vec3.class)
public abstract class Vec3Mixin implements cc.cosmetica.kupe.api.maths.Vec3 {
	// x(), y(), z() are implemented in Vec3
}