public class Animation implements Action {
    private Animated animated;
    private int repeatCount;
    public Animation(Animated animated, int repeatCount) {
        this.animated = animated;
        this.repeatCount = repeatCount;
    }
    public void executeAction(EventScheduler scheduler) {
        this.animated.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.animated,
                    new Animation(this.animated,
                            Math.max(this.repeatCount - 1, 0)),
                    this.animated.getAnimationPeriod());
        }
    }
}
