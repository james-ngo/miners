import processing.core.PImage;

import java.util.List;

public abstract class Miner extends Movable {
    private int resourceLimit;
    protected Miner(String id, Point position, List<PImage> images, int imageIndex,
                    int actionPeriod, int animationPeriod, int resourceLimit) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
    }
    public void transformGold(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        GoldMiner goldMiner = new GoldMiner(this.getId(), this.getPosition(), this.getActionPeriod(),
                this.getAnimationPeriod(), imageStore.getImageList(VirtualWorld.GOLD_MINER_KEY), this.resourceLimit);
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(goldMiner);
        goldMiner.scheduleActions(scheduler, world, imageStore);
    }
}
