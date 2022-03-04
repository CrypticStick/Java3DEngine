import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SolidNode extends Node {
    
    protected Vector3 lScale;
    protected LinkedList<Vector3> vertices;
    protected LinkedList<Vector3> normals;
    protected LinkedList<Vector3> texCoords;
    protected LinkedList<Triangle> faces;
    protected Texture texture;

    /**
     * Creates a 3D solid from an .obj file.
     * 
     * @param model The .obj file to open.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public SolidNode(File model) {
        // Using format descriptions from 
        // https://en.wikipedia.org/wiki/Wavefront_.obj_file#File_format
        
        try (BufferedReader in = new BufferedReader(new FileReader(model))) {

            lScale = new Vector3(1, 1, 1);
            vertices = new LinkedList<>();
            normals = new LinkedList<>();
            texCoords = new LinkedList<>();
            faces = new LinkedList<>();

            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                String[] elements = line.split(" ");

                switch (elements[0]) {
                    case "v":
                        if (elements.length >= 4) {
                            vertices.add(new Vector3(
                                Double.parseDouble(elements[1]),
                                Double.parseDouble(elements[2]),
                                Double.parseDouble(elements[3])
                            ));
                            // w coordinates are not currently supported.
                        }
                        break;
                    case "vt":
                        // This engine requires a minimum of two coordinates.
                        if (elements.length == 3) {
                            texCoords.add(new Vector3(
                                Double.parseDouble(elements[1]),
                                Double.parseDouble(elements[2]),
                                0
                            ));
                        } else if (elements.length > 3) {
                            texCoords.add(new Vector3(
                                Double.parseDouble(elements[1]),
                                Double.parseDouble(elements[2]),
                                Double.parseDouble(elements[3])
                            ));
                        }
                        break;
                    case "vn":
                        if (elements.length >= 4) {
                            normals.add(new Vector3(
                                Double.parseDouble(elements[1]),
                                Double.parseDouble(elements[2]),
                                Double.parseDouble(elements[3])
                            ));
                        }
                        break;
                    case "f":
                        if (elements.length >= 4) {
                            int vertCount = elements.length == 4 ? 3 : 4;
                            int[] _verts = new int[vertCount];
                            int[] _texCoords = new int[vertCount];
                            int[] _normals = new int[vertCount];
                            // Face indices start at 1, so we subtract by 1 to directly address arrays.
                            for (int i = 1; i <= vertCount; i++) {
                                String[] faceElements = elements[i].split("/");
                                _verts[i-1] = Integer.parseInt(faceElements[0]) - 1;
                                if (faceElements.length > 1) {
                                    // texCoord might be skipped, check that it exists
                                    if (!faceElements[1].isEmpty()) {
                                        _texCoords[i-1] = Integer.parseInt(faceElements[1]) - 1;
                                    }
                                    if (faceElements.length > 2) {
                                        _normals[i-1] = Integer.parseInt(faceElements[2]) - 1;
                                    }
                                }
                            }
                            if (vertCount == 3) {
                                faces.add(new Triangle(_verts, _texCoords, _normals));
                            } else {
                                int[] _vertsTri1 = new int[] {_verts[0], _verts[1], _verts[2]};
                                int[] _texCoordsTri1 = new int[] {_texCoords[0], _texCoords[1], _texCoords[2]};
                                int[] _normalsTri1 = new int[] {_normals[0], _normals[1], _normals[2]};
                                faces.add(new Triangle(_vertsTri1, _texCoordsTri1, _normalsTri1));

                                int[] _vertsTri2 = new int[] {_verts[0], _verts[2], _verts[3]};
                                int[] _texCoordsTri2 = new int[] {_texCoords[0], _texCoords[2], _texCoords[3]};
                                int[] _normalsTri2 = new int[] {_normals[0], _normals[2], _normals[3]};
                                faces.add(new Triangle(_vertsTri2, _texCoordsTri2, _normalsTri2));
                            }
                            
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException ex) {
            // This is a fatal error.
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Creates an empty 3D solid.
     */
    public SolidNode() {
        lScale = new Vector3(1, 1, 1);
        vertices = new LinkedList<>();
        normals = new LinkedList<>();
        texCoords = new LinkedList<>();
        faces = new LinkedList<>();
    }

    /**
     * Calculates new normals from the vertices.
     * The faces are updated to contain normal indexes.
     */
    public void calculateNormals() {
        // Using fast normalization technique from
        // https://www.iquilezles.org/www/articles/normals/normals.htm
        
        for (Vector3 normal : normals) {
            normal.set(new Vector3());
        }

        for (Triangle face : faces) {
            Vector3 v1 = vertices.get(face.getCoord(0));
            Vector3 v2 = vertices.get(face.getCoord(1));
            Vector3 v3 = vertices.get(face.getCoord(2));

            Vector3 d1 = v2.subtract(v1);
            Vector3 d2 = v3.subtract(v1); 
            Vector3 no = d1.cross(d2);

            Vector3 v1N = normals.get(face.getNorm(0));
            Vector3 v2N = normals.get(face.getNorm(1));
            Vector3 v3N = normals.get(face.getNorm(2));

            v1N.set(v1N.add(no));
            v2N.set(v2N.add(no));
            v3N.set(v3N.add(no));
        }

        for (Vector3 normal : normals) {
            normal.set(normal.normalize());
        }
    }

    /**
     * Gets the scale of the solid.
     * 
     * @return The scale of the solid.
     */
    public Vector3 getScale() {
        return lScale;
    }

    /**
     * Sets the scale of the solid.
     * 
     * @param scalar The uniform scale factor.
     */
    public void setScale(double scalar) {
        lScale = new Vector3(scalar, scalar, scalar);
    }

    /**
     * Gets the vertices of the solid.
     * 
     * @return The solid's vertices.
     */
    public List<Vector3> getVertices() {
        return vertices;
    }

    /**
     * Gets the normals of the solid.
     * 
     * @return The solid's normals.
     */
    public List<Vector3> getNormals() {
        return normals;
    }

    /**
     * Gets the texture coordinates of the solid.
     * 
     * @return The solid's texture coordinates.
     */
    public List<Vector3> getTextureCoordinates() {
        return texCoords;
    }

    /**
     * Gets the faces of the solid.
     * 
     * @return The solid's faces.
     */
    public List<Triangle> getFaces() {
        return faces;
    }

    /**
     * Gets the texture of the solid.
     * 
     * @return The solid's texture.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the texture of the solid.
     * 
     * @return The solid's texture.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
