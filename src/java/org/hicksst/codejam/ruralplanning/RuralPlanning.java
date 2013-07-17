package org.hicksst.codejam.ruralplanning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

public class RuralPlanning
{
  private static final Comparator<Point> INCREASING_Y = new Comparator<Point>()
  {
    @Override
    public int compare(Point o1, Point o2)
    {
      return Integer.compare(o1.y, o2.y);
    }
  };
  private static final Comparator<Point> DECREASING_Y = new Comparator<Point>()
  {
    @Override
    public int compare(Point o1, Point o2)
    {
      return Integer.compare(o2.y, o1.y);
    }
  };
  private static final Comparator<Point> INCREASING_X = new Comparator<Point>()
  {
    @Override
    public int compare(Point o1, Point o2)
    {
      return Integer.compare(o1.x, o2.x);
    }
  };
  private static final Comparator<Point> DECREASING_X = new Comparator<Point>()
  {
    @Override
    public int compare(Point o1, Point o2)
    {
      return Integer.compare(o2.x, o1.x);
    }
  };

  private static String solve(int n, Point[] points, ProgressCallback callback)
  {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;

    Set<Point> remainingPoints = new HashSet<>();
    for (Point point : points)
    {
      minX = Math.min(minX, point.x);
      minY = Math.min(minY, point.y);
      maxX = Math.max(maxX, point.x);
      maxY = Math.max(maxY, point.y);
      remainingPoints.add(point);
    }

    int avgX = (minX + maxX) / 2;
    int avgY = (minY + maxY) / 2;

    for (Point point : remainingPoints)
    {
      double xDist = point.x - avgX;
      double yDist = point.y - avgY;
      point.distFromCentre = Math.sqrt(xDist * xDist + yDist * yDist);
    }

    callback.setProblemBounds(minX, minY, maxX, maxY);

    Set<Point> topHoriz = new TreeSet<>(INCREASING_X);
    Set<Point> bottomHoriz = new TreeSet<>(DECREASING_X);
    Set<Point> leftVert = new TreeSet<>(INCREASING_Y);
    Set<Point> rightVert = new TreeSet<>(DECREASING_Y);
    for (Point point : points)
    {
      if (point.y == maxY)
      {
        topHoriz.add(point);
      }
      else if (point.x == maxX)
      {
        rightVert.add(point);
      }
      else if (point.y == minY)
      {
        bottomHoriz.add(point);
      }
      else if (point.x == minX)
      {
        leftVert.add(point);
      }
    }

    PointList fence = new PointList();
    fence.addAll(topHoriz);
    fence.addAll(rightVert);
    fence.addAll(bottomHoriz);
    fence.addAll(leftVert);

    callback.fenceUpdated(fence);

    for (Point point : fence)
    {
      remainingPoints.remove(point);
    }

    Point minStart = null;
    int i = 1;

    List<Point> sortedPoints = new ArrayList<>(remainingPoints);
    Collections.sort(sortedPoints, new Comparator<Point>()
    {
      @Override
      public int compare(Point o1, Point o2)
      {
        return Double.compare(o2.distFromCentre, o1.distFromCentre);
      }
    });
    for (Point remainingPoint : sortedPoints)
    {
      logIt("Remaining point " + (i++) + " of " + remainingPoints.size() + ": " + remainingPoint);
      logIt("Fence: " + fence);
      callback.setRemainingPoint(remainingPoint);
      double minDist = Double.MAX_VALUE;
      for (Point point : fence)
      {
        double[] distAndIntersection = calcDistance(remainingPoint, point, point.next);
        logIt(point + " --> " + point.next + ": " + Arrays.toString(distAndIntersection));
        double dist = distAndIntersection[0];
        if (dist < minDist && doesNotIntersect(point, point.next, remainingPoint, fence))
        {
          minDist = dist;
          minStart = point;
        }
      }
      logIt("Putting " + remainingPoint + " after " + minStart);
      fence.insertAfter(remainingPoint, minStart);
      callback.fenceUpdated(fence);
    }
    String result = fence.toString();
    callback.resultComplete(result);
    return result;
  }

  private static boolean doesNotIntersect(Point beforePointOnFence, Point afterPointOnFence, Point newPoint, PointList fence)
  {
    for (Point point : fence)
    {
      if (!(point == beforePointOnFence && point.next == afterPointOnFence))
      {
        if (intersects(beforePointOnFence, afterPointOnFence, newPoint, point, point.next))
        {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean intersects(Point before, Point after, Point newPoint, Point lastPoint, Point point)
  {
    boolean intBeforeNew;
    if (lastPoint == before || lastPoint == newPoint || point == before || point == newPoint)
    {
      intBeforeNew = false;
    }
    else
    {
      intBeforeNew = intersects(lastPoint.x, lastPoint.y, point.x, point.y, before.x, before.y, newPoint.x, newPoint.y);
    }
    boolean intNewAfter;
    if (lastPoint == after || lastPoint == newPoint || point == after || point == newPoint)
    {
      intNewAfter = false;
    }
    else
    {
      intNewAfter = intersects(lastPoint.x, lastPoint.y, point.x, point.y, newPoint.x, newPoint.y, after.x, after.y);
    }
    return intBeforeNew || intNewAfter;
  }

  private static final double ERROR = 1E-8;

  private static boolean intersects(long a, long b, long c, long d, long e, long f, long g, long h)
  {
    if ((h - f) * (c - a) == (g - e) * (d - b))
    {
      // Same gradient, so only intersect if the lines are coincident
      //TODO: this needs to do further checks
      return false;
    }
    double top = (b - f) * (c - a) + (e - a) * (d - b);
    double bottom = (h - f) * (c - a) - (g - e) * (d - b);
    double q = top / bottom;
    double p;
    if (c != a)
    {
      p = (e + q * (g - e) - a) / (c - a);
    }
    else // invariant: d != b if c == a, because we're never dealing with single point "lines".
    {
      p = (f + q * (h - f) - b) / (d - b);
    }
    return q >= -ERROR && q <= 1.0 + ERROR && p >= -ERROR && p <= 1.0 + ERROR;
  }

  private static void logIt(String s)
  {
//    System.out.println(s);
  }

  private static double[] calcDistance(Point p, Point fenceStart, Point fenceEnd)
  {
    double[] intersectXY = new double[2];
    double intersectPointOnFence = intersectPoint(fenceStart.x, fenceStart.y, fenceEnd.x, fenceEnd.y, p.x, p.y, intersectXY);
    double dist = dist(p, intersectXY[0], intersectXY[1]);
    return new double[]{dist, intersectPointOnFence};
  }

  private static double dist(Point p, double x, double y)
  {
    double xd = p.x - x;
    double yd = p.y - y;
    return Math.sqrt(xd * xd + yd * yd);
  }

  private static double intersectPoint(int a, int b, int c, int d, int xP, int yP, double[] intersectXY)
  {
    // NB: 0 <= p <= 1 means it intersects within the bit of fence
    double p;
    double aMinusC = c - a;
    if (a == c)
    {
      p = (double) (yP - b) / (d - b);
    }
    else if (b == d)
    {
      p = (double) (xP - a) / aMinusC;
    }
    else
    {
      double bMinusD = b - d;
      double pPrime = bMinusD * (b - yP) + (xP - a) * aMinusC;
      p = pPrime / (bMinusD * bMinusD + aMinusC * aMinusC);
    }

    if (p < 0.0)
    {
      p = 0.0;
    }
    if (p > 1.0)
    {
      p = 1.0;
    }

    double intersectX = a + p * aMinusC;
    double intersectY = b + p * (d - b);
    intersectXY[0] = intersectX;
    intersectXY[1] = intersectY;
    return p;
  }

  public static void main(String[] args) throws IOException
  {
    Main main = new Main(new NullProgressCallback());
    main.doMain("ruralplanning-large");
//    main.doMain("ruralplanning-small");
//    main.doMain("ruralplanning-test");
  }

  public static class Main extends CodeJamMain
  {
    private ProgressCallback callback;

    public Main(ProgressCallback callback)
    {
      this.callback = callback;
    }

    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      int N = reader.readInt();
      Point[] points = new Point[N];
      for (int i = 0; i < N; i++)
      {
        int[] xAndY = reader.readInts();
        points[i] = new Point(i, xAndY[0], xAndY[1]);
      }
      System.out.println("=== Test Case " + t + " ===");
      return solve(N, points, callback);
    }
  }

  private static class NullProgressCallback implements ProgressCallback
  {
    @Override
    public void setRemainingPoint(Point point)
    {
    }

    @Override
    public void fenceUpdated(PointList fence)
    {
    }

    @Override
    public void setProblemBounds(int minX, int minY, int maxX, int maxY)
    {
    }

    @Override
    public void resultComplete(String result)
    {
    }
  }
}