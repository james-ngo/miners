import processing.core.PImage;

import java.util.List;
import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Movable extends Animated implements PathingStrategy {
    protected Movable(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
    }
    protected abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
    protected Point nextPosition(WorldModel world,
                                 Point destPos) {
        List<Point> path = this.computePath(this.getPosition(), destPos, pt -> world.withinBounds(pt) && !world.isOccupied(pt),
                (p1, p2) -> p1.adjacent(p2), CARDINAL_NEIGHBORS);
        if (path.size() != 0) {
            return path.get(path.size() - 1);
        } else {
            return this.getPosition();
        }
    }
    public List<Point> buildPath(Node node, List<Point> path) {
        if (node.getPrior() == null) {
            return path;
        }
        path.add(node.getPoint());
        return buildPath(node.getPrior(), path);
    }
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
//        System.out.printf("Goal x: %d, y: %d\n", end.x, end.y);
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        Node current = new Node(start);
        openList.add(current);
        while (!current.getPoint().adjacent(end)) {
//            System.out.printf("x: %d, y: %d\n" , current.getPoint().x, current.getPoint().y);
            int maxgValue = 0;
            List<Node> neighbors = potentialNeighbors.apply(current.getPoint())
                    .filter(canPassThrough)
                    .map(p -> new Node(p))
                    .collect(Collectors.toList());
            for (Node n : neighbors) {
//                System.out.printf("Neighbor x: %d, y: %d\n", n.getPoint().x, n.getPoint().y);
                n.setPrior(current);
                if (!closedList.contains(n)) {
                    if (!openList.contains(n)) {
//                        System.out.println("Adds to openList");
                        openList.add(n);
                    }
                    int gValue = Math.abs(n.getPoint().x - start.x) + Math.abs(n.getPoint().y - start.y);
                    if (gValue > maxgValue) {
                        maxgValue = gValue;
                        int hValue = Math.abs(n.getPoint().x - end.x) + Math.abs(n.getPoint().y - end.y);
                        current.setF(gValue + hValue);
                    }
                }
            }
            openList.remove(current);
            closedList.add(current);
            if (openList.size() == 0) {
                return new ArrayList<>();
            }
            Node smallestFNode = openList.get(0);
            for (Node n : openList) {
//                System.out.printf("openList x: %d, y: %d\n", n.getPoint().x, n.getPoint().y);
                if (n.getF() < smallestFNode.getF()) {
                    smallestFNode = n;
                }
            }
            current = smallestFNode;
        }
        List<Point> path = new ArrayList<>();
        return buildPath(current, path);
    }
        /* Does not check withinReach.  Since only a single step is taken
         * on each call, the caller will need to check if the destination
         * has been reached.
         */
//        SingleStepPathing
//
//        return potentialNeighbors.apply(start)
//                .filter(canPassThrough)
//                .filter(pt ->
//                        !pt.equals(start)
//                                && !pt.equals(end)
//                                && Math.abs(end.x - pt.x) <= Math.abs(end.x - start.x)
//                                && Math.abs(end.y - pt.y) <= Math.abs(end.y - start.y))
//                .limit(1)
//                .collect(Collectors.toList());
}
