import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Midas extends Movable {
    public Midas(String id, Point pos, List<PImage> images, int animationPeriod, int actionPeriod) {
        super(id, pos, images, 0, animationPeriod, actionPeriod);
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
        Optional<Entity> closer;
        Optional<Entity> notFullTarget = this.getPosition().findNearest(world, "MinerNotFull");
        Optional<Entity> FullTarget = this.getPosition().findNearest(world, "MinerFull");
        if (notFullTarget.isPresent() && FullTarget.isPresent() &&
                Functions.distanceSquared(notFullTarget.get().getPosition(), this.getPosition()) <
                        Functions.distanceSquared(FullTarget.get().getPosition(), this.getPosition())) {
            closer = notFullTarget;
        }
        else {
            closer = FullTarget;
        }

        if (!closer.isPresent() ||
                !this.moveTo(world, closer.get(), scheduler)) {

            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
        else if (this.moveTo(world, closer.get(), scheduler)) {
            ((Miner)closer.get()).transformGold(world, scheduler, imageStore);
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
