import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Wizard extends Movable {
    public Wizard(String id, Point pos, List<PImage> images, int animationPeriod, int actionPeriod) {
        super(id, pos, images, 0, actionPeriod, animationPeriod);
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
        Optional<Entity> wizardTarget = this.getPosition().findNearest(world, "GoldMiner");

        if (!wizardTarget.isPresent() ||
                !this.moveTo(world, wizardTarget.get(), scheduler)) {
            
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
        else if (this.moveTo(world, wizardTarget.get(), scheduler)) {
            ((GoldMiner)wizardTarget.get()).transform(world, scheduler, imageStore);
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
