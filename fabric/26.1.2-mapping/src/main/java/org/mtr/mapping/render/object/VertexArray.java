package org.mtr.mapping.render.object;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.Mesh;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.tool.OffHeapAllocator;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.Vertex;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

/**
 * GPU-resident mesh for Minecraft 26.1.
 * <p>
 * ENTITY pipelines use {@link VertexFormat.Mode#QUADS}, which is drawn as <b>two triangles
 * (6 indices) per 4 sequential vertices</b>. Soft path emits degenerate quads into a buffer
 * and lets {@link RenderSystem#getSequentialBuffer} supply those indices. We mirror that:
 * expand tris to sequential (v0,v1,v2,v2) verts and use the shared QUADS index buffer.
 */
public final class VertexArray implements AutoCloseable {

	public final MaterialProperties materialProperties;
	public final GpuBuffer vertexBuffer;
	/** Null when using shared sequential QUADS indices. */
	@Nullable
	public final GpuBuffer indexBuffer;
	public final int indexCount;
	public final VertexFormat.IndexType indexType;
	public final boolean sequentialQuads;
	public final VertexAttributeMapping mapping;

	public VertexArray(Mesh mesh, VertexAttributeMapping mapping) {
		throw new UnsupportedOperationException("Use VertexArray.upload(RawMesh) on 26.1");
	}

	public VertexArray(VertexArray other, MaterialProperties materialProperties) {
		this.materialProperties = materialProperties;
		this.vertexBuffer = other.vertexBuffer;
		this.indexBuffer = other.indexBuffer;
		this.indexCount = other.indexCount;
		this.indexType = other.indexType;
		this.sequentialQuads = other.sequentialQuads;
		this.mapping = other.mapping;
	}

	private VertexArray(
			MaterialProperties materialProperties,
			GpuBuffer vertexBuffer,
			@Nullable GpuBuffer indexBuffer,
			int indexCount,
			VertexFormat.IndexType indexType,
			boolean sequentialQuads
	) {
		this.materialProperties = materialProperties;
		this.vertexBuffer = vertexBuffer;
		this.indexBuffer = indexBuffer;
		this.indexCount = indexCount;
		this.indexType = indexType;
		this.sequentialQuads = sequentialQuads;
		this.mapping = null;
	}

	public static VertexArray upload(RawMesh rawMesh) {
		rawMesh.distinct();

		int packedVerts = 0;
		for (final var face : rawMesh.faces) {
			final int n = face.vertices.length;
			if (n < 3) {
				continue;
			}
			if (n == 4) {
				packedVerts += 4;
			} else {
				packedVerts += (n - 2) * 4;
			}
		}
		if (packedVerts == 0) {
			packedVerts = 4;
		}

		final int vertexSize = DefaultVertexFormat.ENTITY.getVertexSize();
		final ByteBuffer vertices = OffHeapAllocator.allocate(packedVerts * vertexSize);
		try {
			for (final var face : rawMesh.faces) {
				final int[] idx = face.vertices;
				if (idx.length == 3) {
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[0]));
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[1]));
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[2]));
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[2]));
				} else if (idx.length == 4) {
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[0]));
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[1]));
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[2]));
					writeEntityVertex(vertices, rawMesh.vertices.get(idx[3]));
				} else if (idx.length > 4) {
					for (int i = 2; i < idx.length; i++) {
						writeEntityVertex(vertices, rawMesh.vertices.get(idx[0]));
						writeEntityVertex(vertices, rawMesh.vertices.get(idx[i - 1]));
						writeEntityVertex(vertices, rawMesh.vertices.get(idx[i]));
						writeEntityVertex(vertices, rawMesh.vertices.get(idx[i]));
					}
				}
			}
			vertices.flip();

			final int indexCount = VertexFormat.Mode.QUADS.indexCount(packedVerts);
			final GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(
					() -> "mtr_mesh_vertices", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, vertices);
			// Indices come from RenderSystem shared sequential QUADS buffer at draw time.
			return new VertexArray(
					rawMesh.materialProperties,
					vertexBuffer,
					null,
					indexCount,
					VertexFormat.IndexType.INT,
					true
			);
		} finally {
			OffHeapAllocator.free(vertices);
		}
	}

	private static void writeEntityVertex(ByteBuffer vertices, Vertex vertex) {
		vertices.putFloat(vertex.position.data.x);
		vertices.putFloat(vertex.position.data.y);
		vertices.putFloat(vertex.position.data.z);
		vertices.put((byte) 0xFF).put((byte) 0xFF).put((byte) 0xFF).put((byte) 0xFF);
		vertices.putFloat(vertex.u);
		vertices.putFloat(vertex.v);
		final int overlay = OverlayTexture.NO_OVERLAY;
		vertices.putShort((short) (overlay & 0xFFFF));
		vertices.putShort((short) (overlay >>> 16));
		final int light = 0x00F000F0;
		vertices.putShort((short) (light & 0xFFFF));
		vertices.putShort((short) (light >>> 16));
		final Vector3f normal = Utilities.copy(vertex.normal);
		normal.data.normalize();
		vertices.put((byte) (normal.data.x * 0x7F));
		vertices.put((byte) (normal.data.y * 0x7F));
		vertices.put((byte) (normal.data.z * 0x7F));
		vertices.put((byte) 0);
	}

	public void bind() {
	}

	public static void unbind() {
	}

	public void draw() {
		throw new UnsupportedOperationException("Use BatchManager / ShaderManager RenderPass draw");
	}

	@Override
	public void close() {
		if (RenderSystem.isOnRenderThread()) {
			closeBuffers();
		} else {
			RenderSystem.queueFencedTask(this::closeBuffers);
		}
	}

	private void closeBuffers() {
		try {
			if (vertexBuffer != null && !vertexBuffer.isClosed()) {
				vertexBuffer.close();
			}
		} catch (Exception e) {
			DummyClass.logException(e);
		}
		try {
			if (indexBuffer != null && !indexBuffer.isClosed()) {
				indexBuffer.close();
			}
		} catch (Exception e) {
			DummyClass.logException(e);
		}
	}
}
