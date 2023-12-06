import edu.princeton.cs.algs4.Point2D;

public class Main {
    public static void main(String[] args) {
        //Utilize Visualizer and Generator classes to better understand the concept and test various scenarios.
        //Use files in resources for test data.

        KdTree st = new KdTree();
        st.insert(new Point2D(1.0, 1.0));
        st.insert(new Point2D(0.7, 0.9));
        System.out.println(st.size());
        System.out.println(st.nearest(new Point2D(0.0, 0.0)));
    }
}