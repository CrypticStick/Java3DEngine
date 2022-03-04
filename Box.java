import java.util.*;
import java.awt.Color;

/**
 * A box in 3D space.
 */
public class Box extends SolidNode {

    /** Creates a box in 3D space at the given point with the given dimensions.
     * 
     * @param center The center of the box.
     * @param scale The scale of the box's dimensions.
     */
    public Box(Vector3 center, Vector3 scale) {
        // Default origin is the center of mass
        lTranslation = center;
        lOrientation = new Quaternion();

        // Build the 8 corners of the cube.
        for (int i = 0; i < 8; i++) {
            vertices.add(new Vector3(
                scale.getX() / 2 * (i % 2 == 1 ? 1 : -1),
                scale.getY() / 2 * (i % 4 > 1 ?  1 : -1),
                scale.getZ() / 2 * (i > 3 ? 1 : -1)
            ));
            // Will calculate normals at the end.
            normals.add(new Vector3());
            // Have a different color each corner.
            // colors.add(new Color(
            //     (i % 2 == 1 ? 255 : 0),
            //     (i % 4 > 1 ?  255 : 0),
            //     (i > 3 ? 255 : 0)
            // ));
            // Kinda wonky texture mapping
            texCoords.add(new Vector3(
                (i % 2 == 1 ? (i > 3 ? 0 : 1) : (i > 3 ? 1 : 0)),
                (i % 4 > 1 ?  (i > 3 ? 0 : 1) : (i > 3 ? 1 : 0)),
                0
            ));
        }

        // Manually creating the triangle faces (perhaps there's a way to loop through this?)
        faces.add(new Triangle(new int[] {0, 1, 2}));
        faces.add(new Triangle(new int[] {2, 1, 3}));
        faces.add(new Triangle(new int[] {3, 1, 5}));
        faces.add(new Triangle(new int[] {5, 7, 3}));
        faces.add(new Triangle(new int[] {3, 7, 2}));
        faces.add(new Triangle(new int[] {2, 7, 6}));
        faces.add(new Triangle(new int[] {6, 4, 2}));
        faces.add(new Triangle(new int[] {2, 4, 0}));
        faces.add(new Triangle(new int[] {0, 4, 1}));
        faces.add(new Triangle(new int[] {1, 4, 5}));
        faces.add(new Triangle(new int[] {5, 4, 7}));
        faces.add(new Triangle(new int[] {7, 4, 6}));

        calculateNormals();
    }
}
