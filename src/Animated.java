import processing.core.PImage;
import java.util.List;

public abstract class Animated extends Interactive {
    private int animationPeriod;
    protected Animated(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod) {
        super(id, position, images, imageIndex, actionPeriod);
        this.animationPeriod = animationPeriod;
    }
    protected int getAnimationPeriod() {
        return this.animationPeriod;
    }
}
