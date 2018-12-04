import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull extends Movable {
    private int resourceLimit;
    private int resourceCount;

    public MinerNotFull(String id, int resourceLimit,
                        Point position, int actionPeriod, int animationPeriod,
                        List<PImage> images) {
        super(id, position, images, 0, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = 0;
    }

    protected boolean moveTo(WorldModel world,
                           Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            this.resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

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

    private boolean transform(WorldModel world,
                                     EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Animated miner = new MinerFull(this.getId(), this.resourceLimit,
                    this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(),
                    this.getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> closer;
        Optional<Entity> notFullOreTarget = this.getPosition().findNearest(world, "Ore");
        Optional<Entity> notFullGoldTarget = this.getPosition().findNearest(world, "GoldOre");
        if (notFullGoldTarget.isPresent() && notFullOreTarget.isPresent() &&
                Functions.distanceSquared(notFullGoldTarget.get().getPosition(), this.getPosition()) <
                        Functions.distanceSquared(notFullOreTarget.get().getPosition(), this.getPosition())) {
            closer = notFullGoldTarget;
        }
        else {
            closer = notFullOreTarget;
        }


        if (!closer.isPresent() ||
                !this.moveTo(world, closer.get(), scheduler) ||
                !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
    }

//    protected Point nextPosition(WorldModel world,
//                               Point destPos) {
//        int horiz = Integer.signum(destPos.x - this.getPosition().x);
//        Point newPos = new Point(this.getPosition().x + horiz,
//                this.getPosition().y);
//
//        if (horiz == 0 || world.isOccupied(newPos)) {
//            int vert = Integer.signum(destPos.y - this.getPosition().y);
//            newPos = new Point(this.getPosition().x,
//                    this.getPosition().y + vert);
//
//            if (vert == 0 || world.isOccupied(newPos)) {
//                newPos = this.getPosition();
//            }
//        }
//
//        return newPos;
//    }

    public void scheduleActions(EventScheduler scheduler,
                                WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this,
                new Animation(this, 0), this.getAnimationPeriod());
    }
}
