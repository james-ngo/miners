import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

final class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point &&
                ((Point) other).x == this.x &&
                ((Point) other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }

    public boolean adjacent(Point other) {
        return (this.x == other.x && Math.abs(this.y - other.y) == 1) ||
                (this.y == other.y && Math.abs(this.x - other.x) == 1);
    }

    private Optional<Entity> nearestEntity(List<Entity> entities) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = Functions.distanceSquared(nearest.getPosition(), this);

            for (Entity other : entities) {
                int otherDistance = Functions.distanceSquared(other.getPosition(), this);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }
    public Optional<Entity> findNearest(WorldModel world, String className) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : world.getEntities()) {
            if (entity.getClass().getName() == className) {
                ofType.add(entity);
            }
        }

        return this.nearestEntity(ofType);
    }

}
