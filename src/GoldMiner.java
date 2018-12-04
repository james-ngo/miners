import processing.core.PImage;
import java.util.Optional;
import java.util.List;


public class GoldMiner extends Movable {
    public GoldMiner(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images) {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }
    protected boolean moveTo(WorldModel world,
                             Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> notFullTarget = this.getPosition().findNearest(world, "Ore");

        if (!notFullTarget.isPresent() ||
                !this.moveTo(world, notFullTarget.get(), scheduler)) {
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
        else if (this.moveTo(world, notFullTarget.get(), scheduler)) {
            ((Ore)notFullTarget.get()).transform(world, scheduler, imageStore);
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
    }
    public void scheduleActions(EventScheduler scheduler,
                                WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this,
                new Animation(this, 0), this.getAnimationPeriod());
    }
}

