public class Activity implements Action {
    private Interactive interactive;
    private WorldModel world;
    private ImageStore imageStore;
    public Activity(Interactive interactive, WorldModel world, ImageStore imageStore) {
        this.interactive = interactive;
        this.world = world;
        this.imageStore = imageStore;
    }
    public void executeAction(EventScheduler scheduler) {
        (this.interactive).executeActivity(this.world, this.imageStore, scheduler);
    }
}
