import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * A renderer projects 3D objects onto the viewport and rasterizes them.
 */
public class Renderer {

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    private CameraNode camera;
    private DrawingPanel panel;
    private Graphics gPanel;

    // Image buffers
    private BufferedImage sBuffer;
    private Graphics gBuffer;
    private double[][] zBuffer;

    // A transformation matrix describing a complete world-camera transform and projection.
    private Matrix44 vertTransform;
    private Matrix33 normTransform;

    public enum RenderMode {
        PERSPECTIVE,
        ORTHOGRAPHIC
    }
    RenderMode currentMode = RenderMode.ORTHOGRAPHIC;

    /**
     * Creates a renderer for drawing a 3D scene to the display.
     * 
     * @param camera The camera containing the viewport to draw.
     * @param graphics The Graphics object necessary for drawing.
     */
    public Renderer(CameraNode camera, DrawingPanel panel) {
        this.camera = camera;
        this.panel = panel;
        this.gPanel = panel.getGraphics();

        SCREEN_WIDTH = panel.getWidth();
        SCREEN_HEIGHT = panel.getHeight();

        sBuffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        gBuffer = sBuffer.getGraphics();
    }

    /**
     * Configures the renderer to use the given render mode.
     */
    public void setRenderMode(RenderMode mode) {
        currentMode = mode;
    }

    /**
     * Renders and draws the provided scene to the viewport.
     * The root node should be a simple container, and not a solid.
     * 
     * @param scene The root node containing the scene to render.
     */
    public void renderScene(Node scene) {
        //clear screen
        gBuffer.setColor(Color.BLACK);
        gBuffer.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        zBuffer = new double[SCREEN_HEIGHT][SCREEN_WIDTH];

        // Rotates world to camera view (looking along +z-axis from origin, +y-axis pointing up).       
        Matrix44 worldCamTransform = camera.getGlobalOrientation().conjugate().rotationMatrix()
            .addTranslation(camera.getGlobalPosition().scale(-1));
        Matrix44 projectionTransform = new Matrix44();

        double n = camera.getNearDistance();
        double f = camera.getFarDistance();
        double r = camera.getViewportWidth() / 2;
        // y-axis is inverted, since Java Image coordinates increase as you go down.
        double t = -camera.getViewportHeight() / 2;

        // There's a bug with perspective projection:
        // Objects render facing backwards (along the z axis)
        if (currentMode == RenderMode.PERSPECTIVE) {
            // Use the OpenGL perspective projection matrix:
            // http://www.songho.ca/opengl/gl_projectionmatrix.html
            // Only works when view plane is symmetric (looking down -z-axis)
            projectionTransform = new Matrix44(new double[][] {
                { n / r, 0, 0, 0 },
                { 0, n / t, 0, 0 },
                { 0, 0, -(f + n) / (f - n), -2 * f * n / (f - n) },
                { 0, 0, -1, 0 }
            });
        } else if (currentMode == RenderMode.ORTHOGRAPHIC) {
            // Use the OpenGL orthographic projection matrix:
            // http://www.songho.ca/opengl/gl_projectionmatrix.html
            // Only works when view plane is symmetric (looking down -z-axis)
            projectionTransform = new Matrix44(new double[][] {
                { 1 / r, 0, 0, 0 },
                { 0, 1 / t, 0, 0 },
                { 0, 0, -2 / (f - n), -(f + n) / (f - n) },
                { 0, 0, 0, 1 }
            });
        }
        vertTransform = projectionTransform.multiply(worldCamTransform);
        normTransform = vertTransform.getRotation().inverse().transpose();
        
        checkChildren(scene);
        gPanel.drawImage(sBuffer, 0, 0, null);
    }

    /**
     * Recursively scans through children and renders them appropriately.
     * 
     * @param parent The parent containing potential children nodes.
     */
    private void checkChildren(Node parent) {
        for (Node child : parent.getChildren()) {
            if (child instanceof SolidNode solid) {
                drawSolid(solid);
            }
            checkChildren(child);
        }
    }

    /**
     * Renders and draws the given solid to the viewport.
     * @param solid The solid to render and draw.
     */
    private void drawSolid(SolidNode solid) {
        // Need to calculate current positions of vertices (and their projected positions)
        LinkedList<Vector3> clipVerts = new LinkedList<>();
        LinkedList<Double> clipVertW = new LinkedList<>();
        LinkedList<Vector3> clipNorms = new LinkedList<>();

        Quaternion globalRot = solid.getGlobalOrientation();
        Vector3 globalPos = solid.getGlobalPosition();
        Vector3 globalScale = solid.getScale();

        // Calculate new projected coordinates.
        for (Vector3 vert : solid.getVertices()) {
            // Calculate global position of vertex.
            Vector3 scaleVertPos = vert.scale(globalScale);
            Vector3 rotVertPos = scaleVertPos.rotate(globalRot).add(globalPos);
            // Transforms the vertex to its clip coordinate.
            Vector4 clipVertPos = vertTransform.multiply(rotVertPos, 1);

            clipVerts.add(clipVertPos.toVector3());
            clipVertW.add(clipVertPos.getW());
        }

        // Calculate new projected normals.
        for (Vector3 norm : solid.getNormals()) {
            // Calculate global orientation of vertex.
            Vector3 rotVertNorm = norm.rotate(globalRot);
            // Transforms the normal to its clip orientation.
            Vector3 clipVertNorm = normTransform.multiply(rotVertNorm);
            clipNorms.add(clipVertNorm);
        }

        // Perspective projection involves a fourth w coordinate, and works slightly different.
        if (currentMode == RenderMode.PERSPECTIVE) {
            for (Triangle face : solid.getFaces()) {

                // Get every vertex on the triangle.
                Vector3[] verts = new Vector3[] {
                    clipVerts.get(face.getCoord(0)),
                    clipVerts.get(face.getCoord(1)),
                    clipVerts.get(face.getCoord(2))
                };

                double[] vertW = new double[] {
                    clipVertW.get(face.getCoord(0)),
                    clipVertW.get(face.getCoord(1)),
                    clipVertW.get(face.getCoord(2))
                };
                
                // Perform back-face culling
                Vector3 vA = verts[2].scale(1/vertW[2])
                    .subtract(verts[1].scale(1/vertW[1]));
                Vector3 vB = verts[0].scale(1/vertW[0])
                    .subtract(verts[1].scale(1/vertW[1]));
                // If the area of the face is positive, then we can see it!
                if (vA.cross(vB).magnitude() > 0) {
                    
                    // If every vertex is not normalized, the face might be out of bounds.
                    if (vertW[0] != 1 && vertW[1] != 1 && vertW[2] != 1) {
                        // Get vertices as arrays for quick enumeration.
                        double[] v1C = verts[0].toArray();
                        double[] v2C = verts[1].toArray();
                        double[] v3C = verts[2].toArray();
                        // Remove any faces that lie fully outside of view.
                        // (May add clipping in the future)
                        boolean skipFace = false;
                        for (int axis = 0; axis < 3; axis++) {
                            if ((v1C[axis] > vertW[0] && v2C[axis] > vertW[1] && v3C[axis] > vertW[2]) ||
                                (v1C[axis] < -vertW[0] && v2C[axis] < -vertW[1] && v3C[axis] < -vertW[2])) {
                                skipFace = true;
                                break;
                            }
                        }
                        // Don't bother normalizing coords if all are out of bounds.
                        if (skipFace) {
                            continue;
                        }
                    }

                    // Normalize and remap coords that haven't been yet.
                    for (int vert = 0; vert < 3; vert++) {
                        if (vertW[vert] != 1) {
                            // normalize coordinates
                            verts[vert].set(verts[vert].scale(1/vertW[vert]));
                            // Remap coordinates
                            verts[vert].set(new Vector3(
                                verts[vert].getX() * SCREEN_WIDTH + SCREEN_WIDTH / 2.0,
                                verts[vert].getY() * SCREEN_HEIGHT + SCREEN_HEIGHT / 2.0,
                                verts[vert].getZ()
                            ));
                        }
                    }

                    // Ready to draw face! Remember to keep z-index and vertex colors in mind.
                    rasterizeFace(face, solid, clipVerts, clipNorms);
                }
            }   
        } else if (currentMode == RenderMode.ORTHOGRAPHIC) {
            for (Triangle face : solid.getFaces()) {

                // Get every vertex on the triangle.
                Vector3[] verts = new Vector3[] {
                    clipVerts.get(face.getCoord(0)),
                    clipVerts.get(face.getCoord(1)),
                    clipVerts.get(face.getCoord(2))
                };

                double[] vertW = new double[] {
                    clipVertW.get(face.getCoord(0)),
                    clipVertW.get(face.getCoord(1)),
                    clipVertW.get(face.getCoord(2))
                };
                
                // Perform back-face culling
                Vector3 vA = verts[2].subtract(verts[1]);
                Vector3 vB = verts[0].subtract(verts[1]);
                // If the area of the face is positive, then we can see it!
                if (vA.cross(vB).magnitude() > 0) {
                    
                    // Get vertices as arrays for quick enumeration.
                    double[] v1C = verts[0].toArray();
                    double[] v2C = verts[1].toArray();
                    double[] v3C = verts[2].toArray();
                    // Remove any faces that lie fully outside of view.
                    // (May add clipping in the future)

                    // If every vertex is not normalized, the face might be out of bounds.
                    if (vertW[0] != -1 && vertW[1] != -1 && vertW[2] != -1) {
                        boolean skipFace = false;
                        for (int axis = 0; axis < 3; axis++) {
                            if ((v1C[axis] > 1 && v2C[axis] > 1 && v3C[axis] > 1) ||
                                (v1C[axis] < -1 && v2C[axis] < -1 && v3C[axis] < -1)) {
                                skipFace = true;
                                break;
                            }
                        }
                        // Don't bother remapping coords if all are out of bounds.
                        if (skipFace) {
                            continue;
                        }
                    }

                    // Remap coords that haven't been yet (w=-1 means it's already remapped)
                    for (int vert = 0; vert < 3; vert++) {
                        if (vertW[vert] != -1) {
                            // Remap coordinates
                            verts[vert].set(new Vector3(
                                verts[vert].getX() * SCREEN_WIDTH + SCREEN_WIDTH / 2.0,
                                verts[vert].getY() * SCREEN_HEIGHT + SCREEN_HEIGHT / 2.0,
                                verts[vert].getZ()
                            ));
                            // Set w=-1 to indicate mapping.
                            clipVertW.set(face.getCoord(vert), -1.0);
                        }
                    }

                    // Ready to draw face! Remember to keep z-index and vertex colors in mind.
                    rasterizeFace(face, solid, clipVerts, clipNorms);
                }
            }   
        }
    }

    /**
     * Rasterizes the given triangle face.
     * @param verts An array containing three vertices mapped to the viewport.
     */
    private void rasterizeFace(Triangle face, SolidNode solid, LinkedList<Vector3> verts, LinkedList<Vector3> norms) {

        double[][] vCoords = new double[][] {
            verts.get(face.getCoord(0)).toArray(),
            verts.get(face.getCoord(1)).toArray(),
            verts.get(face.getCoord(2)).toArray(),
        };

        Matrix33 baryMat = calculateBarycentricMatrix(vCoords);

        // Edge-walking rasterization:
        // Inspired by technique described in
        // http://groups.csail.mit.edu/graphics/classes/6.837/F01/Lecture06/Slide07.html
        double[] top;
        double[] mid;
        double[] bot;
        boolean isMidRight;

        boolean yAB = vCoords[0][1] > vCoords[1][1];
        boolean yBC = vCoords[1][1] > vCoords[2][1];
        boolean yCA = vCoords[2][1] > vCoords[0][1];

        // Sorting points from top to bottom
        if (yAB) {
            if (yBC) {
                top = vCoords[0];
                mid = vCoords[1];
                bot = vCoords[2];
            } else if (yCA) {
                top = vCoords[2];
                mid = vCoords[0];
                bot = vCoords[1];
            } else {
                top = vCoords[0];
                mid = vCoords[2];
                bot = vCoords[1];
            }
        } else {
            if (!yBC) {
                top = vCoords[2];
                mid = vCoords[1];
                bot = vCoords[0];
            } else if (yCA) {
                top = vCoords[1];
                mid = vCoords[2];
                bot = vCoords[0];
            } else {
                top = vCoords[1];
                mid = vCoords[0];
                bot = vCoords[2];
            }
        }

        // Ensure y-coordinates are restricted to viewport.
        // If face is entirely out of bounds, the face will not be processed further.
        int yTop = (int)top[1];
        if (yTop >= SCREEN_HEIGHT) {
            yTop = SCREEN_HEIGHT - 1;
        } else if (yTop < 0) {
            return;
        }
        int yBreak = (int)mid[1];
        if (yBreak >= SCREEN_HEIGHT) {
            yBreak = SCREEN_HEIGHT - 1;
        } else if (yBreak < 0) {
            yBreak = 0;
        }
        int yBottom = (int)bot[1];
        if (yBottom < 0) {
            yBottom = 0;
        } else if (yBottom >= SCREEN_HEIGHT) {
            return;
        }

        // Slopes are change in x as y decreases.
        // Either topMidSlope and midBotSlope could potentially be NAN, 
        // but they will be skipped due to the breakpoint existing at an endpoint.
        double topMidSlope = (mid[0] - top[0]) / (top[1] - mid[1]);
        double topBotSlope = (bot[0] - top[0]) / (top[1] - bot[1]);
        double midBotSlope = (bot[0] - mid[0]) / (mid[1] - bot[1]);

        // Is mid located to the right of the line connecting top and bot?
        isMidRight = mid[0] > top[0] + (top[1] - mid[1]) * topBotSlope;

        double leftBoundTopSlope;
        double rightBoundTopSlope;
        double leftBoundBotSlope;
        double rightBoundBotSlope;

        // Slopes used to determine bounds are based on position of middle point
        if (isMidRight) {
            leftBoundTopSlope = topBotSlope;
            rightBoundTopSlope = topMidSlope;
            leftBoundBotSlope = topBotSlope;
            rightBoundBotSlope = midBotSlope;
        } else {
            leftBoundTopSlope = topMidSlope;
            rightBoundTopSlope = topBotSlope;
            leftBoundBotSlope = midBotSlope;
            rightBoundBotSlope = topBotSlope;
        }

        // Top half of triangle
        for (int y = yTop; y > yBreak; y--) {
            int leftBound = (int)Math.floor(
                top[0] + leftBoundTopSlope * (top[1] - y)
            );
            int rightBound = (int)Math.ceil(
                top[0] + rightBoundTopSlope * (top[1] - y)
            );
            drawPixelRow(y, leftBound, rightBound, face, vCoords, baryMat, solid);
        }

        // Middle of triangle
        if (yBreak != yTop && yBreak != yBottom && mid[1] > 0 && mid[1] < SCREEN_HEIGHT) {
            int leftBound;
            int rightBound;
            if (isMidRight) {
                leftBound = (int)Math.floor(
                    top[0] + leftBoundTopSlope * (top[1] - (int)mid[1])
                );
                rightBound = (int)mid[0];
            } else {
                leftBound = (int)mid[0];
                rightBound = (int)Math.ceil(
                    top[0] + rightBoundTopSlope * (top[1] - (int)mid[1])
                );
            }
            drawPixelRow((int)mid[1], leftBound, rightBound, face, vCoords, baryMat, solid);
        }

        // Bottom half of triangle
        for (int y = yBottom; y < yBreak; y++) {
            int leftBound = (int)Math.floor(
                bot[0] + leftBoundBotSlope * (bot[1] - y)
            );
            int rightBound = (int)Math.ceil(
                bot[0] + rightBoundBotSlope * (bot[1] - y)
            );
            drawPixelRow(y, leftBound, rightBound, face, vCoords, baryMat, solid);
        }
    }

    /**
     * Draws a row of pixels to the display.
     * 
     * @param y The y-coordinate.
     * @param leftBound The left-most x-coordinate.
     * @param rightBound The right-most x-coordinate.
     * @param verts The array of involved vertices.
     * @param baryMat The barycentric matrix for converting cartesian coordinates.
     */
    private void drawPixelRow(int y, int leftBound, int rightBound, Triangle face, double[][] verts, 
        Matrix33 baryMat, SolidNode solid) {
        // Ensure no pixels are drawn off screen
        if (leftBound >= SCREEN_WIDTH || rightBound < 0) {
            return;
        }
        if (leftBound < 0) {
            leftBound = 0;
        }
        if (rightBound >= SCREEN_WIDTH) {
            rightBound = SCREEN_WIDTH - 1;
        }

        for (int x = leftBound; x <= rightBound; x++) {
            Vector3 baryCoords = baryMat.multiply(new Vector3(1, x, y));
            double zIndex = baryCoords.getX() * verts[0][2] + 
                baryCoords.getY() * verts[1][2] +
                baryCoords.getZ() * verts[2][2];
            drawPixel(x, y, zIndex, baryCoords, face, verts, solid);
        }
    }

    /**
     * Draws the given pixel to the display.
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param zIndex The z-index of the coordinate.
     * @param baryCoords The barycentric coordinates from the face.
     * @param verts The array of involved vertices.
     */
    private void drawPixel(
        int x, int y, double zIndex, Vector3 baryCoords, Triangle face, double[][] verts, SolidNode solid) {
        if (zBuffer[y][x] == 0 || zBuffer[y][x] > zIndex) {
            zBuffer[y][x] = zIndex;
            Color coordColor;
            if (solid.texture == null) {
                coordColor = new Color(0, 0, 0);
                // Color aC = solid.getColors().get(face.getColor(0));
                // Color bC = solid.getColors().get(face.getColor(1));
                // Color cC = solid.getColors().get(face.getColor(2));
                // coordColor = new Color(
                //     Math.min(Math.max((int)(baryCoords.getX() * aC.getRed() + baryCoords.getY() * 
                //     bC.getRed() + baryCoords.getZ() * cC.getRed()), 0), 255),
                //     Math.min(Math.max((int)(baryCoords.getX() * aC.getGreen() + baryCoords.getY() *
                //     bC.getGreen() + baryCoords.getZ() * cC.getGreen()), 0), 255),
                //     Math.min(Math.max((int)(baryCoords.getX() * aC.getBlue() + baryCoords.getY() * 
                //     bC.getBlue() + baryCoords.getZ() * cC.getBlue()), 0), 255)
                //     );
                // sBuffer.setRGB(x, y, coordColor.getRGB());
            } else {
                Vector3 aT = solid.getTextureCoordinates().get(face.getTexCoord(0));
                Vector3 bT = solid.getTextureCoordinates().get(face.getTexCoord(1));
                Vector3 cT = solid.getTextureCoordinates().get(face.getTexCoord(2));
                coordColor = solid.texture.getPixel(
                    Math.min(Math.max(baryCoords.getX() * aT.getX() + baryCoords.getY() * bT.getX() + 
                        baryCoords.getZ() * cT.getX(), 0), 1),
                        Math.min(Math.max(baryCoords.getX() * aT.getY() + baryCoords.getY() * bT.getY() + 
                        baryCoords.getZ() * cT.getY(), 0), 1)
                );
            }
            sBuffer.setRGB(x, y, coordColor.getRGB());
        }
    }

    /**
     * Creates a 3x3 matrix for converting 2D cartesian coordinates to barycentric coordinates.
     * The matrix can be multiplied with any other vector to obtain the relative barycentric
     * coordinates.
     * Vectors for multiplication must be in the format <1, x, y>.
     * 
     * @param vCoords Array of three vertex coordinates.
     * @return A 3x3 matrix to multiply with cartesian Vector3 coordinates.
     */
    private Matrix33 calculateBarycentricMatrix(double[][] vCoords) {
        // Formula from https://en.wikipedia.org/wiki/Barycentric_coordinate_system#Vertex_approach
        Matrix33 baryMat = new Matrix33(new double[][] {
            {
                vCoords[1][0] * vCoords[2][1] - vCoords[2][0] * vCoords[1][1], 
                vCoords[1][1] - vCoords[2][1], 
                vCoords[2][0] - vCoords[1][0]
            },
            {
                vCoords[2][0] * vCoords[0][1] - vCoords[0][0] * vCoords[2][1], 
                vCoords[2][1] - vCoords[0][1], 
                vCoords[0][0] - vCoords[2][0]
            },
            {
                vCoords[0][0] * vCoords[1][1] - vCoords[1][0] * vCoords[0][1], 
                vCoords[0][1] - vCoords[1][1], 
                vCoords[1][0] - vCoords[0][0]
            }
        });
        // scalar appears in the wiki formula as 1/2A
        double scalar = 1 / (
            vCoords[0][0] * (vCoords[1][1] - vCoords[2][1]) + 
            vCoords[1][0] * (vCoords[2][1] - vCoords[0][1]) + 
            vCoords[2][0] * (vCoords[0][1] - vCoords[1][1])
        );
        return baryMat.scale(scalar);
    }
}
