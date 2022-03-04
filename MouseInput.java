import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.*;

public class MouseInput implements MouseInputListener {

    CameraNode camera;
    DrawingPanel panel;
    Point lastPoint;
    boolean isMovingCamera = true;

    public MouseInput(CameraNode camera, DrawingPanel panel) {
        this.camera = camera;
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastPoint = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isMovingCamera && lastPoint != null) {
            double xDif = e.getX() - lastPoint.getX();
            double yDif = e.getY() - lastPoint.getY();
            Quaternion rotation = new Quaternion(
                new Vector3(
                    yDif * 2/(Math.PI * panel.getHeight()), xDif * 2/(Math.PI * panel.getWidth()), 0
                )
            );
            camera.rotate(rotation);
        }
        lastPoint = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Do nothing
    }

}