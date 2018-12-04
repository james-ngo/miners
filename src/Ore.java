import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Ore extends Interactive {
    private static final Random rand = new Random();
    private static final String BLOB_KEY = "blob";
    private static final String BLOB_ID_SUFFIX = " -- blob";
    private static final int BLOB_PERIOD_SCALE = 4;
    private static final int BLOB_ANIMATION_MIN = 50;
    private static final int BLOB_ANIMATION_MAX = 150;

    private static final String GOLD_ORE_KEY = "goldore";

    public Ore(String id, Point position, int actionPeriod,
               List<PImage> images) {
        super(id, position, images, 0, actionPeriod);
    }

    public void scheduleActions(EventScheduler scheduler,
                                WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
    }

    public void executeActivity(WorldModel world,
                                ImageStore imageStore, EventScheduler scheduler) {
        Point pos = this.getPosition();  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Animated blob = new OreBlob(this.getId() + BLOB_ID_SUFFIX,
                pos, this.getActionPeriod() / BLOB_PERIOD_SCALE,
                BLOB_ANIMATION_MIN +
                        rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
                imageStore.getImageList(BLOB_KEY));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

    public void transform(WorldModel world,
                           EventScheduler scheduler, ImageStore imageStore) {
        Interactive goldOre =  new GoldOre(this.getId(), this.getPosition(),
                this.getActionPeriod(), imageStore.getImageList("goldore"));

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(goldOre);
        goldOre.scheduleActions(scheduler, world, imageStore);
    }

}
