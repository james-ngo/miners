import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerFull extends Movable {
    private int resourceLimit;
    private int resourceCount;

    public MinerFull(String id, int resourceLimit, Point position, int actionPeriod,
                     int animationPeriod, List<PImage> images) {
        super(id, position, images, 0, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceLimit;
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

    private void transform(WorldModel world,
                               EventScheduler scheduler, ImageStore imageStore) {
        Interactive miner =  new MinerNotFull(this.getId(), this.resourceLimit,
                this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(),
                this.getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    public void executeActivity(WorldModel world,
                                ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = this.getPosition().findNearest(world, "Blacksmith");

        if (fullTarget.isPresent() &&
                this.moveTo(world, fullTarget.get(), scheduler)) {
            this.transform(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
    }

//    protected Point nextPosition(WorldModel world,
//                                    Point destPos) {
//         if (this.computePath(this.getPosition(), destPos, pt -> world.withinBounds(pt) && !world.isOccupied(pt),
//                (p1, p2) -> p1.adjacent(p2),CARDINAL_NEIGHBORS).size() != 0) {
//             return this.computePath(this.getPosition(), destPos, pt -> world.withinBounds(pt) && !world.isOccupied(pt),
//                     (p1, p2) -> p1.adjacent(p2),CARDINAL_NEIGHBORS).get(0);
//         }
//         else {
//             return this.getPosition();
//         }
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
        scheduler.scheduleEvent(this, new Animation(this, 0),
                this.getAnimationPeriod());
    }
}
