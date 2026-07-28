package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public final class Vector3d extends HolderBase<net.minecraft.world.phys.Vec3> {

	public Vector3d(net.minecraft.world.phys.Vec3 data) {
		super(data);
	}

	@MappedMethod
	public Vector3d(double x, double y, double z) {
		super(new net.minecraft.world.phys.Vec3(x, y, z));
	}

	@MappedMethod
	public static Vector3d cast(HolderBase<?> data) {
		return new Vector3d((net.minecraft.world.phys.Vec3) data.data);
	}

	@MappedMethod
	public static boolean isInstance(@Nullable HolderBase<?> data) {
		return data != null && data.data instanceof net.minecraft.world.phys.Vec3;
	}

	@MappedMethod
	public boolean equals(@Nullable Object data) {
		return data instanceof HolderBase<?> && this.data.equals(((HolderBase<?>) data).data);
	}

	@MappedMethod
	public double getXMapped() {
		return this.data.x;
	}

	@MappedMethod
	public double getYMapped() {
		return this.data.y;
	}

	@MappedMethod
	public double getZMapped() {
		return this.data.z;
	}

	@Nonnull
	@MappedMethod
	public static Vector3d getZeroMapped() {
		return new Vector3d(net.minecraft.world.phys.Vec3.ZERO);
	}

	@MappedMethod
	public Vector3d rotateX(float pitch) {
		return new Vector3d(this.data.xRot(pitch));
	}

	@MappedMethod
	public Vector3d rotateY(float yaw) {
		return new Vector3d(this.data.yRot(yaw));
	}

	@MappedMethod
	public Vector3d rotateZ(float roll) {
		return new Vector3d(this.data.zRot(roll));
	}
@MappedMethod public double squaredDistanceTo(Vector3d pos){return this.data.distanceToSqr(pos.data);}

@Nonnull@MappedMethod public Vector3d add(double x,double y,double z){return new Vector3d(this.data.add(x,y,z));}
@Nonnull@MappedMethod public Vector3d add(Vector3d vec){return new Vector3d(this.data.add(vec.data));}
@Nonnull@MappedMethod public Vector3d subtract(double x,double y,double z){return new Vector3d(this.data.subtract(x,y,z));}
@Nonnull@MappedMethod public Vector3d subtract(Vector3d vec){return new Vector3d(this.data.subtract(vec.data));}
@Nonnull@MappedMethod public Vector3d multiply(double value){return new Vector3d(this.data.scale(value));}
@MappedMethod public double distanceTo(Vector3d other){return this.data.distanceTo(other.data);}}