package de.gg.camera;

import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * A wrapper for the game camera to easily allow transformations, etc.
 */
public class CameraWrapper {

    private PerspectiveCamera camera;

    public CameraWrapper(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public void translate(float x, float y, float z) {
        this.camera.translate(x, y, z);
    }

}
