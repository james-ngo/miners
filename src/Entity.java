import processing.core.PImage;
import java.util.List;

public abstract class Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    protected Entity(String id, Point position, List<PImage> images, int imageIndex) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = imageIndex;
    }

    protected String getId() { return this.id; }
    protected Point getPosition() {
        return this.position;
    }
    protected void setPosition(Point pos) {
        this.position = pos;
    }
    protected List<PImage> getImages() { return this.images; }
    protected PImage getCurrentImage() {
        return this.images.get(this.imageIndex);
    }
    protected void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }
}
