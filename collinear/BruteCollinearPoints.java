package collinear;
import java.util.Arrays;

public class BruteCollinearPoints {
    // finds all line segments containing 4 points
    private final ResizingArray segments;
    private final Point [] points;

    public BruteCollinearPoints(Point[] points) {
        if (points != null) {
            this.points = new Point [points.length];
            for (int i = 0; i<points.length; i++) {
                this.points[i] = points[i];
            }
        } else {
            throw new IllegalArgumentException("null value provided! ");
        }

        if ( !isValidSortedInput (this.points)) throw new IllegalArgumentException("invalid value provided! ");
        segments = new ResizingArray();
        processPoints(this.points);

    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.getArray().length;
    }

    // the line segments
    public LineSegment[] segments()   {
        return segments.getArray();
    }

    private void processPoints (Point [] points) {
        if (points.length >= 4) {
            //for each point, check if there  are 4 other points with same slope
            for (int i = 0; i<points.length; i++) {

                for (int k = i+1; k < points.length; k++) {
                    if (i!=k)
                        for (int j = k+1; j < points.length; j++) {
                            if (j!=k)
                                for (int h = j+1; h < points.length; h++) {
                                    if (j!=h)
                                        if (i!=k && i!=j && i!=h) {
                                            double slope1 = points[i].slopeTo(points[k]);
                                            double slope2 = points[i].slopeTo(points[j]);
                                            double slope3 = points[i].slopeTo(points[h]);

                                            if (slope1 == slope2 && slope1 == slope3) {
                                                LineSegment seg = new LineSegment(points[i], points[h]);
                                                segments.addSegment(seg);
                                            }
                                        }
                                }
                        }
                }

            }
        }
    }

    private boolean isValidSortedInput (Point [] points) {
        boolean ret = true;
        for (Point point:points) {
            if (point == null) {
                ret = false;
                break;
            }
        }
        if (ret) {
            Arrays.sort(points);
            for (int i = 0; i < points.length - 1; i++) {
                if (points[i].compareTo(points[i + 1]) == 0) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

    private class ResizingArray {
        private LineSegment [] segments;
        ResizingArray () {
            segments = new LineSegment[0];
        }
        void addSegment (LineSegment seg){
            LineSegment [] temp = new LineSegment[segments.length+1];
            for (int i = 0; i<segments.length; i++) {
                temp[i]=segments[i];
                segments[i]=null;
            }
            temp[segments.length]=seg;
            segments = temp;
            temp = null;
        }
        LineSegment [] getArray () {
            LineSegment [] segs = new LineSegment[segments.length];
            System.arraycopy(segments, 0, segs, 0, segments.length);
            return segs;
        }
    }

}