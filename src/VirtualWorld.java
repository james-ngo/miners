import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


import processing.core.*;

public final class VirtualWorld
        extends PApplet {
    public static final int TIMER_ACTION_PERIOD = 100;

    public static final int VIEW_WIDTH = 640 * 2;
    public static final int VIEW_HEIGHT = 480 * 2;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int WORLD_WIDTH_SCALE = 2;
    public static final int WORLD_HEIGHT_SCALE = 2;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final String GOLD_IMAGE_NAME = "gold";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String LOAD_FILE_NAME = "gaia.sav";

    public static final String GOLD_MINER_KEY = "goldminer";

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    private long next_time;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
                TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= next_time) {
            this.scheduler.updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
        }

        view.drawViewport();
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            view.shiftView(dx, dy);
        }
    }

    public void mouseClicked() {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                Point clickPos = new Point(mouseX / 32 + x + this.view.getViewport().getCol(),
                        mouseY / 32 + y + this.view.getViewport().getRow());
                if (world.withinBounds(clickPos)) {
                    world.setBackgroundCell(clickPos, new Background(GOLD_IMAGE_NAME, imageStore.getImageList(GOLD_IMAGE_NAME)));
                }
                if (world.getOccupancyCell(clickPos) instanceof MinerFull ||
                    world.getOccupancyCell(clickPos) instanceof MinerNotFull) {
                    Movable miner = (Movable)world.getOccupancyCell(clickPos);
                    GoldMiner goldMiner = new GoldMiner(miner.getId(), miner.getPosition(), miner.getActionPeriod(),
                        miner.getAnimationPeriod(), imageStore.getImageList(GOLD_MINER_KEY));
                    world.removeEntity(miner);
                    scheduler.unscheduleAllEvents(miner);

                    world.addEntity(goldMiner);
                    goldMiner.scheduleActions(scheduler, world, imageStore);
                }
            }
        }
    }

    private static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    private static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private static void loadImages(String filename, ImageStore imageStore,
                                   PApplet screen) {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, screen);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void loadWorld(WorldModel world, String filename,
                                  ImageStore imageStore) {
        try {
            Scanner in = new Scanner(new File(filename));
            world.load(in, imageStore);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void scheduleActions(WorldModel world,
                                        EventScheduler scheduler, ImageStore imageStore) {
        for (Interactive interactive : world.getInteractives()) {
            interactive.scheduleActions(scheduler, world, imageStore);
        }
    }

    private static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
