import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OreBlob extends Movable {
    public OreBlob(String id, Point position,
                   int actionPeriod, int animationPeriod, List<PImage> images) {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }

    protected boolean moveTo(WorldModel world,
                                  Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
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

//    protected Point nextPosition(WorldModel world,
//                               Point destPos) {
//        int horiz = Integer.signum(destPos.x - this.getPosition().x);
//        Point newPos = new Point(this.getPosition().x + horiz,
//                this.getPosition().y);
//
//        Optional<Entity> occupant = world.getOccupant(newPos);
//
//        if (horiz == 0 ||
//                (occupant.isPresent() && !(occupant.get().getClass().getName() == "Ore"))) {
//            int vert = Integer.signum(destPos.y - this.getPosition().y);
//            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);
//            occupant = world.getOccupant(newPos);
//
//            if (vert == 0 ||
//                    (occupant.isPresent() && !(occupant.get().getClass().getName() == "Ore"))) {
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

    public void executeActivity(WorldModel world,
                                       ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> blobTarget = this.getPosition().findNearest(world, "Vein");
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (this.moveTo(world, blobTarget.get(), scheduler)) {
                Interactive quake = new Quake(tgtPos,
                        imageStore.getImageList(Quake.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }
    }
}
