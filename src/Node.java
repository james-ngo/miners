public class Node {
    private Point point;
    private Node prior;
    private int fVal;
    public Node(Point p) {
        this.point = p;
    }
    public Point getPoint() {
        return this.point;
    }
    public int getF() {
        return this.fVal;
    }
    public void setF(int f) {
        this.fVal = f;
    }
    public Node getPrior() {
        return this.prior;
    }
    public void setPrior(Node n) {
        this.prior = n;
    }
    public boolean equals(Object other) {
        return other instanceof Node &&
                this.point.equals(((Node)other).getPoint());
    }
}
