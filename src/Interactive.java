import processing.core.PImage;
import java.util.List;

public abstract class Interactive extends Entity {
    private int actionPeriod;
    protected Interactive(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod) {
        super(id, position, images, imageIndex);
        this.actionPeriod = actionPeriod;
    }
    protected int getActionPeriod() { return this.actionPeriod; }
    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    protected abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
