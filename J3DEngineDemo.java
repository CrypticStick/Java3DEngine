import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// Jakob Stickles
// 11/25/2021
// J3DEngineDemo.java

/**
 * Demonstrates the basic features of my Java-based 3D engine!
 */
public class J3DEngineDemo {

    /*
    Several mathematical formulas and techniques for 3D transformation have been obtained from the
    internet, and have been cited accordingly.
    The rest of this code has been written independently. I am quite proud of it! :)
    */
    public static void main(String[] args) {

        // These dimensions are fixed after starting the program.
        // If new dimensions must be used, create a new Renderer.
        DrawingPanel panel = new DrawingPanel(1280, 720);

        // Aiming for 60 FPS
        double millisPerFrame = 1000.0/60.0;

        // Creating the required elements for rendering
        CameraNode camera = new CameraNode(
            new Vector3(),              // Translation (None)
            new Quaternion(),           // Orientation (Default)
            (double)panel.getWidth()    // Aspect Ratio (16/9)
            / panel.getHeight(),              
            Math.toRadians(160),         // Vertical FOV (45 deg) 
            10,                         // Near-Plane Distance
            1000                        // Far-Plane Distance
        );

        Renderer renderer = new Renderer(camera, panel);

        // Adding mouse camera control
        MouseInput mouseListener = new MouseInput(camera, panel);
        panel.addMouseListener(mouseListener);

        // Create the elements of the scene
        Node root = new Node();
        Box closeCube = new Box(new Vector3(0, 0, -120), new Vector3(10, 10, 10));
        // Box cubeHat = new Box(new Vector3(0, 6.5, 0), new Vector3(12, 3, 12));
        // SolidNode teapot = new SolidNode(new File("C:/Users/jakob/Downloads/teapot.obj"));
        // teapot.setScale(5);
        // teapot.translate(new Vector3(0, 0, -40));

        try {
            closeCube.setTexture(new Texture(ImageIO.read(new File("C:/Users/jakob/Downloads/brick.jpg"))));
            // teapot.setTexture(new Texture(ImageIO.read(new File("C:/Users/jakob/Downloads/brick.jpg"))));

        } catch (IOException ex) {
            // Can't load texture, just use vertex colors.
        }
        // closeCube.addChild(cubeHat);
        root.addChild(closeCube);
        // root.addChild(teapot);

        // Initialize time variables
        double startTime = System.currentTimeMillis();
        double currentTime = startTime;
        double deltaT = 0;

        //run for an hour (will eventually add better end condition)
        while (currentTime < startTime + 1000 * 3600) {

            //wacky rotation
            closeCube.rotate( 
                new Quaternion(
                    new Vector3(
                        Math.PI * deltaT/8000,
                        Math.PI * deltaT/16000,
                        Math.PI * deltaT/8000
                    )
                )
            );

            // cubeHat.rotate( 
            //     new Quaternion(
            //         new Vector3(
            //             0, 
            //             Math.PI * deltaT/-4000,
            //             0
            //         )
            //     )
            // );
            
            // Save time since last transformation
            currentTime = System.currentTimeMillis();
            // Render the scene
            renderer.renderScene(root);

            // Maintain the target framerate, if lag permits
            int millisToSleep = (int)Math.max(millisPerFrame - (System.currentTimeMillis() - currentTime), 0);
            panel.sleep(millisToSleep);
            // Store time passed after rendering and sleeping.
            deltaT = System.currentTimeMillis() - currentTime;
        }
    }
}