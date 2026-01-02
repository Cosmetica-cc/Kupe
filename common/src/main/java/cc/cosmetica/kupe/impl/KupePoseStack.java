package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.maths.Matrix4;
import cc.cosmetica.kupe.api.maths.Vec3;
import cc.cosmetica.kupe.impl.maths.Mat4f;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.AxisAngle4f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * MatrixStack wrapping blaze3d posestack.
 */
public class KupePoseStack implements MatrixStack {
    public KupePoseStack(PoseStack stack) {
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
        return new Mat4f(this.stack.last().pose());
    }

    @Override
    public void translate(double x, double y, double z) {
        this.stack.translate(x, y, z);
    }

    @Override
    public void rotate(Vec3 axis, float amount, boolean degrees) {
        this.stack.mulPose(new Quaternionf(new AxisAngle4f(
                degrees ? (float)Math.toRadians(amount) : amount,
                new Vector3f((float)axis.getX(), (float)axis.getY(), (float)axis.getZ())
        )));
    }

    @Override
    public void scale(float x, float y, float z) {
        this.stack.scale(x, y, z);
    }

    @Override
    public Matrix3x2fStack getMinecraftStack() {
        throw new UnsupportedOperationException("Not a Matrix3x2fStack");
    }
}