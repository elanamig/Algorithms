package collinear;
import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private final ArrayList <LineSegment> segments;  //use ArrayList for it's dynamic properties
    private final Point [] points; //internal storage array for immutability purposes.

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points)  {
        if (points != null) {
            this.points = new Point [points.length];
            System.arraycopy(points, 0, this.points, 0, points.length);
        } else {
            throw new IllegalArgumentException("null value provided! ");
        }
        if ( !isValidSortedInput (this.points)) throw new IllegalArgumentException("invalid value provided! ");
        segments = new ArrayList<>();
        processPoints(this.points);

    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.size();
    }

    private void processPoints (Point [] points) {

        if (points.length >= 4) {
            //for each point, check if there  are 3+ other points with same slope.
            //Create a copy that's not going to be modified and use it for the top iterator.
            Point [] pointsRef = new Point [points.length];
            System.arraycopy(points, 0, pointsRef, 0, points.length);

            for (Point segStart : pointsRef){

                Arrays.sort (points);  //sort the points in ascending order so that your start and end segment points will be lowest and highest, respectively.
                Arrays.sort (points, segStart.slopeOrder()); //re-sort by secondary key using slope order
                int pointCounter = 2;  //start point counter at 2 (because each segment by default will have at least 2 pts.
                boolean duplicate = false; //this var will control whether the segment is a duplicate (same points, diff order)

                double slopePrev = Double.NaN; //init prev slope value
                Point segEnd; //declare the end Point of a segment

                //the first point in sorted array will always be itself (negative infinity), so might as well skip it.
                //We will also be peeking at the next item, so set loop condition to 1 less then array size
                for (int i = 1; i < points.length-1; i++) {
                    Point q = points[i];
                    double slopeCurr = segStart.slopeTo(q);
                    //process only if starting point is SMALLER than current point.  Otherwise we'll end up with duplicate segments with same points in different order
                    if (q.compareTo(segStart) > 0) {
                        if (slopePrev == slopeCurr ) {
                            if (! duplicate ) {
                                segEnd = q;
                                pointCounter++;
                                //Peek at the next item.
                                //if next item changes slope, this line segment is finished
                                Point next = points[i+1];
                                double slopeNext = segStart.slopeTo(next);

                                //if we're looking at second to last item, and the next/last item is collinear, add the segment now.
                                //pointCounter will be one less, since we haven't looked at the last item yet.
                                if ( i == points.length-2 && slopeNext == slopeCurr && next.compareTo(segStart) > 0 && pointCounter > 2) {
                                   segments.add(new LineSegment(segStart, next));

                                }
                                //if it's not second to last item, but the slope changes and we have more than 3 pts, we got a segment
                                else if (pointCounter > 3 && slopeCurr != slopeNext ){
                                   segments.add(new LineSegment(segStart, segEnd));
                                   pointCounter = 2;
                                   duplicate = false; //reset
                                }
                            } //if it's a duplicate point, just ignore and continue
                        } else {
                            //we don't care about smaller value if it's not collinear, so just reset
                            duplicate = false;
                            pointCounter = 2;
                        }
                    } else {
                        //if the point we are considering is smaller than the reference segStart, we are looking at a duplicate segment.
                        duplicate = true;
                    }
                    slopePrev = slopeCurr;  //done with current slope.  It now becomes previous in prep for the next iteration.
                }
            }
        }
    }

    // the line segments
    public LineSegment[] segments()   {
        LineSegment [] segs = new LineSegment[segments.size()];
        for (int i = 0; i<segs.length; i++){
            segs[i] = segments.get(i);
        }
        return segs;
    }

    //validate input.
    private boolean isValidSortedInput (Point [] points) {
        boolean ret = true;
        for (int i = 0; i<points.length; i++) {
            if (points[i] == null) {
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



}