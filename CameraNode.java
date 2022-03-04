/**
 * A camera defines the user's viewing frustum.
 */
public class CameraNode extends Node {
    // Viewport dimensions
    private double aspect;
    private double yFov;

    // Frustum variables
    private double nearDist;
    private double farDist;

    /**
     * Creates a camera viewing frustum in 3D space.
     * 
     * @param translation The translation of the camera.
     * @param rotation The rotation of the camera (in radians).
     * @param aspect The aspect ratio of the viewport.
     * @param yFov The vertical field of view.
     * @param nearDist The closest distance that the camera will render.
     * @param farDist The furthest distance that the camera will render.
     */
    public CameraNode(Vector3 translation, Quaternion orientation, double aspect,
                  double yFov, double nearDist, double farDist) {
        this.lTranslation = translation;
        this.lOrientation = orientation;
        this.aspect = aspect;
        this.yFov = yFov;
        this.nearDist = nearDist;
        this.farDist = farDist;
    }

    /**
     * Translates the camera.
     * 
     * @param translation The distance to move the camera.
     */
    public void translate(Vector3 translation) {
        this.lTranslation = this.lTranslation.add(translation);
    }
    
    /**
     * Gets the nearest clipping distance from the camera.
     * 
     * @return The nearest clipping distance.
     */
    public double getNearDistance() {
        return nearDist;
    }

    /**
     * Gets the furthest clipping distance from the camera.
     * 
     * @return The furthest clipping distance.
     */
    public double getFarDistance() {
        return farDist;
    }

    /**
     * Gets the height of the viewport in 3D space.
     * 
     * @return The viewport's height.
     */
    public double getViewportHeight() {
        return Math.abs(Math.tan(yFov/2) * nearDist * 2);
    }

    /**
     * Gets the width of the viewport in 3D space.
     * 
     * @return The viewport's width.
     */
    public double getViewportWidth() {
        return Math.abs(getViewportHeight() * aspect);
    }

    /**
     * Tests whether the given bounds exist within the viewing frustum.
     * @param bounds The bounding box to test.
     * @return Whether the box at least partially exists within the viewing frustum.
     */
    public boolean seesBoundingBox(Box bounds) {
        // Not currently implemented, assumes visibility.
        return true;
    }
}
