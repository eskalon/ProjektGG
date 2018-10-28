package de.gg.game.ui.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.asset.Text;

public class TestShader implements Shader {

	public ShaderProgram program;
	Camera camera;
	RenderContext context;
	int u_projViewTrans;
	int u_worldTrans;

	@InjectAsset("shaders/vertex.glsl")
	private Text vertex;
	@InjectAsset("shaders/fragment.glsl")
	private Text fragment;

	@Override
	public void init() {
		program = new ShaderProgram(vertex.getString(), fragment.getString());

		if (!program.isCompiled())
			throw new GdxRuntimeException(program.getLog());

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
	}

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		program.begin();
		program.setUniformMatrix(u_projViewTrans, camera.combined);
	}

	@Override
	public void render(Renderable renderable) {
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		renderable.meshPart.render(program);
	}

	@Override
	public void end() {
		program.end();
	}

	@Override
	public void dispose() {
		program.dispose();
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}

}
