package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

/**
 * MatrixStack wrapping blaze3d posestack.
 */
public class KupeStack implements MatrixStack {
	public KupeStack(PoseStack stack) {
		this.stack = stack;
	}

	private PoseStack stack;

	@Override
	public void push() {
		this.stack.pushPose();
	}

	@Override
	public void pop() {
		this.stack.popPose();
	}

	@Override
	public Matrix4 peek() {
		return (Matrix4) (Object) this.stack.last().pose();
	}

	@Override
	public void translate(double x, double y, double z) {
		this.stack.translate(x, y, z);
	}

	@Override
	public void rotate(Vec3 axis, float amount, boolean degrees) {
		this.stack.mulPose(new Quaternion(
				new Vector3f((float)axis.getX(), (float)axis.getY(), (float)axis.getZ()),
				amount,
				degrees
		));
	}

	@Override
	public void scale(float x, float y, float z) {
		this.stack.scale(x, y, z);
	}

	@Override
	public PoseStack getMinecraftStack() {
		return this.stack;
	}
}
