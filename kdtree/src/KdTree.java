import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

//KdTree implementation. Check Brute force implemented PointSET to compare the results.
public class KdTree {

    private Node first;
    private int size;

    // construct an empty set of points
    public KdTree() {
        first = null;
    }

    // is the set empty?
    public boolean isEmpty() {
        return first == null;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (first == null) {
            size++;
            first = new Node(p.x(), p.y());
            return;
        }
        insert(first, p, true);
    }

    private Node insert(Node lFirst, Point2D p, boolean checkX) {
        if (lFirst == null) {
            size++;
            return new Node(p.x(), p.y());
        }
        if (lFirst.x == p.x() && lFirst.y == p.y()) {
            return lFirst;
        }
        if (checkX ? lFirst.x > p.x() : lFirst.y > p.y()) {
            lFirst.left = insert(lFirst.left, p, !checkX);
        }
        else {
            lFirst.right = insert(lFirst.right, p, !checkX);
        }
        return lFirst;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return contains(first, p, true);
    }

    private boolean contains(Node lFirst, Point2D p, boolean checkX) {
        if (lFirst == null) {
            return false;
        }
        if (lFirst.x == p.x() && lFirst.y == p.y()) {
            return true;
        }
        if (checkX ? lFirst.x > p.x() : lFirst.y > p.y()) {
            return contains(lFirst.left, p, !checkX);
        }
        else {
            return contains(lFirst.right, p, !checkX);
        }
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenRadius(0.01);
        draw(first, true, 1);
        StdDraw.show();
    }

    private void draw(Node node, boolean checkX, double limit) {
        if (node == null) {
            return;
        }
        Point2D p = new Point2D(node.x, node.y);
        if (!checkX) {
            StdDraw.setPenColor(Color.BLUE);
            if (p.x() < limit) {
                StdDraw.line(0, p.y(), limit, p.y());
            }
            else {
                StdDraw.line(limit, p.y(), 1, p.y());
            }

            draw(node.left, true, p.y());
            draw(node.right, true, p.y());
        }
        else {
            StdDraw.setPenColor(Color.RED);
            if (p.y() < limit) {
                StdDraw.line(p.x(), 0, p.x(), limit);
            }
            else {
                StdDraw.line(p.x(), limit, p.x(), 1);
            }

            draw(node.left, false, p.x());
            draw(node.right, false, p.x());
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        p.draw();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Queue<Point2D> queue = new Queue<>();
        RectHV rectHV = new RectHV(0, 0, 1, 1);
        range(first, rect, queue, true, rectHV);
        return queue;
    }

    private void range(Node node, RectHV rect, Queue<Point2D> queue, boolean checkX,
                       RectHV rectHV) {
        if (node == null) {
            return;
        }

        if (rectHV != null && rect.intersects(rectHV)) {
            Point2D point2D = new Point2D(node.x, node.y);

            if (rect.contains(point2D)) {
                queue.enqueue(point2D);
            }
            RectHV smallerRect = null;
            RectHV largerRect = null;

            if (checkX) {
                if (node.left != null) {
                    smallerRect = new RectHV(rectHV.xmin(), rectHV.ymin(), point2D.x(),
                                             rectHV.ymax());
                }
                if (node.right != null) {
                    largerRect = new RectHV(point2D.x(), rectHV.ymin(), rectHV.xmax(),
                                            rectHV.ymax());
                }
            }
            else {
                if (node.left != null) {
                    smallerRect = new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(),
                                             point2D.y());
                }
                if (node.right != null) {
                    largerRect = new RectHV(rectHV.xmin(), point2D.y(), rectHV.xmax(),
                                            rectHV.ymax());
                }
            }
            if (node.left != null) {
                range(node.left, rect, queue, !checkX, smallerRect);
            }
            if (node.right != null) {
                range(node.right, rect, queue, !checkX, largerRect);
            }
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (size == 0) {
            return null;
        }
        RectHV rectHV = new RectHV(0, 0, 1, 1);
        return nearest(p, first, rectHV, true, 3, new Point2D(first.x, first.y));
    }

    private Point2D nearest(Point2D p, Node node, RectHV rectHV, boolean checkX, double minD,
                            Point2D nearestPoint) {
        if (node == null) {
            return null;
        }
        Point2D currentPoint = new Point2D(node.x, node.y);
        double distanceToNode = p.distanceSquaredTo(currentPoint);
        if (minD > distanceToNode) {
            minD = distanceToNode;
            nearestPoint = currentPoint;
        }
        RectHV leftRect;
        RectHV rightRect;
        if (checkX) {
            leftRect = new RectHV(rectHV.xmin(), rectHV.ymin(), node.x, rectHV.ymax());
            rightRect = new RectHV(node.x, rectHV.ymin(), rectHV.xmax(), rectHV.ymax());
        }
        else {
            leftRect = new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(), node.y);
            rightRect = new RectHV(rectHV.xmin(), node.y, rectHV.xmax(), rectHV.ymax());
        }

        Point2D p1 = null;
        Point2D p2 = null;
        if (leftRect.contains(p) || minD > leftRect.distanceSquaredTo(p)) {
            p1 = nearest(p, node.left, leftRect, !checkX, minD, nearestPoint);
        }
        if (rightRect.contains(p) || minD > rightRect.distanceSquaredTo(p)) {
            p2 = nearest(p, node.right, rightRect, !checkX, minD, nearestPoint);
        }
        double p1Distance = p1 == null ? 3 : p1.distanceSquaredTo(p);
        double p2Distance = p2 == null ? 3 : p2.distanceSquaredTo(p);
        if (p1Distance < p2Distance) {
            if (p1Distance < minD) {
                return p1;
            }
            return nearestPoint;
        }
        else {
            if (p2Distance < minD) {
                return p2;
            }
            return nearestPoint;
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree st = new KdTree();
        st.insert(new Point2D(1.0, 1.0));
        st.insert(new Point2D(0.7, 0.9));
        System.out.println(st.size());
        System.out.println(st.nearest(new Point2D(0.0, 0.0)));
    }

    private class Node {
        private double x;
        private double y;
        private Node left;
        private Node right;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

}
