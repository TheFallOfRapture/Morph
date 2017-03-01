package com.fate.engine.graphics;

import com.fate.engine.core.Game;
import com.fate.engine.core.GameSystem;
import com.fate.engine.entities.Entity;
import com.fate.engine.graphics.components.RenderData;
import com.fate.engine.math.Matrix4f;
import com.fate.engine.newgui.Element;
import com.fate.engine.physics.components.Transform;
import com.fate.engine.tiles.Tile;
import com.fate.engine.tiles.TileEmpty;
import com.fate.engine.tiles.Tilemap;

import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class GLRenderingEngine extends GameSystem {
	public static Matrix4f projectionMatrix = new Matrix4f();
	private List<Entity> gameRenderables;
	private List<Element> guiRenderables;

	public GLRenderingEngine(Game game) {
		super(game);
		this.gameRenderables = new ArrayList<>();
		this.guiRenderables = new ArrayList<>();
	}

	public void render(RenderData data, Transform transform) {
		data.getShader().bind();
		data.getShader().getUniforms().setUniforms(transform, data);

		glBindVertexArray(data.getVertexArrayObject());
		glDrawElements(GL_TRIANGLES, data.getIndices().size(), GL_UNSIGNED_INT, NULL);
		glBindVertexArray(0);

		data.getShader().getUniforms().unbind(transform, data);
		data.getShader().unbind();
	}

	private void render(Entity e) {
		render(e.getComponent(RenderData.class), e.getComponent(Transform.class));
	}

	private void render(Element e) {
		render(e.getRenderData(), e.getTransform());
	}

	public void render(GLDisplay display, List<Entity> entities) {
		glClear(GL_COLOR_BUFFER_BIT);
		entities.stream().filter(this::acceptEntity)
				.forEach(e -> render(e.getComponent(RenderData.class), e.getComponent(Transform.class)));
		display.update();
	}

	public void render(GLDisplay display, List<Entity> entities, Tilemap tilemap) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		entities.stream().filter(this::acceptEntity)
				.forEach(e -> render(e.getComponent(RenderData.class), e.getComponent(Transform.class)));

		renderTilemap(tilemap);

		display.update();
	}

	public void register(Entity e) {
		gameRenderables.add(0, e);
	}

	public boolean unregister(Entity e) {
		return gameRenderables.remove(e);
	}

	public void register(Element e) {
		guiRenderables.add(0, e);
	}

	public void unregister(Element e) {
		guiRenderables.remove(e);
	}

	// TODO: Replace old rendering pattern with new render pattern
	public void render(GLDisplay display) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gameRenderables.forEach(this::render);
		guiRenderables.forEach(this::render);

		display.update();
	}

	private void renderTilemap(Tilemap tilemap) {
		for (int y = 0; y < tilemap.getHeight(); y++) {
			for (int x = 0; x < tilemap.getWidth(); x++) {
				Tile tile = tilemap.getTile(x, y);

				Transform t = tilemap.genTileTransform(x, y);
				RenderData rd = tile.getComponent(RenderData.class);

				if (!(tile instanceof TileEmpty))
					render(rd, t);
			}
		}
	}

	public void setClearColor(Color clearColor) {
		glClearColor(clearColor.getRed(), clearColor.getGreen(), clearColor.getBlue(), clearColor.getAlpha());
	}

	@Override
	public void initSystem() {
		glActiveTexture(GL_TEXTURE0);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void setProjectionMatrix(Matrix4f m) {
		GLRenderingEngine.projectionMatrix = m;
	}

	public static Matrix4f getProjectionMatrix() {
		return GLRenderingEngine.projectionMatrix;
	}

	public void setClearColor(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}

	@Override
	protected boolean acceptEntity(Entity e) {
		return e.hasComponents(RenderData.class, Transform.class);
	}
}