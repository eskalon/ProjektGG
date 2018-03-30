package dev.gg.render;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class TestShader implements Shader {
	ShaderProgram program;
	Camera camera;
	RenderContext context;
	private AssetManager assetManager;
	int u_projViewTrans;
	int u_worldTrans;

	@Asset(Text.class)
	private static final String VERTEX_SHADER_PATH = "shaders/vertex.glsl";
	@Asset(Text.class)
	private static final String FRAGMENT_SHADER_PATH = "shaders/fragment.glsl";

	public TestShader(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	public void init() {
		Text fragment = assetManager.get(FRAGMENT_SHADER_PATH);
		Text vertex = assetManager.get(VERTEX_SHADER_PATH);
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
