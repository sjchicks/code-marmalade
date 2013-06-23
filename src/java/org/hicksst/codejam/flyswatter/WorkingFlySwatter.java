package org.hicksst.codejam.flyswatter;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.text.DecimalFormat;

public class WorkingFlySwatter
{
  private static double solve(double f, double R, double t, double r, double g)
  {
    // Shortcut if the fly won't fit inside any hole
    if (2 * f >= g)
    {
      return 1.0;
    }

    // inner ring radius
    double S = R - t;

    double distanceToNextHoleStart = 2 * r + g;

    double safeLengthOfWholeHole = g - 2 * f;
    double safeAreaOfWholeHole = safeLengthOfWholeHole * safeLengthOfWholeHole;

    double totalSafeArea = 0;

    // Look at top-right quadrant: bottom-left corner (exact centre of racquet) is at (0, 0)

    // For each 'column' of holes...

    double maxStartX = getOtherCoordinateOnCircle(S, r);

    double holeStartX = r;
    for (; holeStartX < maxStartX; holeStartX += distanceToNextHoleStart)// TODO: will multiplication yield more accurate results?
    {
      double holeEndX = holeStartX + g;

      double holeStartY;
      // How many whole holes are there?
      if (holeEndX < maxStartX)
      {
        // There might be some whole holes...

        // Where will the line drawn up from the right-hand edge of the hole intersect the inner ring?
        double maxYAtHoleEndX = getOtherCoordinateOnCircle(S, holeEndX);

        double i = (maxYAtHoleEndX - r - g) / distanceToNextHoleStart;
        double numWholeHoles = Math.floor(i + 1.0);
        totalSafeArea += numWholeHoles * safeAreaOfWholeHole; // TODO: Sum up the i's and multiply at the end?

        holeStartY = r + numWholeHoles * distanceToNextHoleStart;
      }
      else
      {
        holeStartY = r;
      }

      // How many broken holes are there?
      double maxYAtHoleStartX = getOtherCoordinateOnCircle(S, holeStartX);
      for (; holeStartY < maxYAtHoleStartX; holeStartY += distanceToNextHoleStart)
      {
        double holeEndY = holeStartY + g;

        double[] topLeft = calcIntersectionTL(f, g, S, holeStartX, holeEndX, holeStartY);
        if (topLeft == null)
        {
          continue;
        }
        // Just flip X and Y to get yf...TODO: Needs checking...
        double[] bottomRight = calcIntersectionBR(f, g, S, holeStartX, holeEndY, holeStartY);
//                double[] bottomRight = calcIntersectionTL(f, g, S, holeStartY, holeEndY, holeStartX);
        if (bottomRight == null)
        {
          continue;
        }

        double tlx = topLeft[0];
        double tly = topLeft[1];
        double brx = bottomRight[0];
        double bry = bottomRight[1];

        // Five points:
        // (f, f)
        // (f, tly)
        // (tlx, tly)
        // (brx, f)
        // (brx, bry)
        // (can be only 3 different points, when tlx==f and bry==f)

        double areaOfLeftRectangle = (tlx - f) * (tly - f);
        double areaOfBottomRectangle = (brx - tlx) * (bry - f); // with no intersection with the left rectangle
        totalSafeArea += areaOfLeftRectangle + areaOfBottomRectangle;

        double x0 = holeStartX + tlx;
        double x1 = holeStartX + brx;
        double y0 = holeStartY + bry;
        double y1 = holeStartY + tly;
        double areaToRemoveFromSegment = 0.5 * (x1 * y0 + x0 * y1) - x0 * y0;

        double radiusOfCircle = S - f;// We're one fly's radius away from the edge of the circle

        // result of asin = -pi/2...pi/2
        double proportionOfCircle = (Math.asin(y1 / radiusOfCircle) - Math.asin(y0 / radiusOfCircle)) / (2.0 * Math.PI);
        double areaOfSegment = Math.PI * radiusOfCircle * radiusOfCircle * proportionOfCircle;

        totalSafeArea = totalSafeArea + areaOfSegment - areaToRemoveFromSegment;
      }
    }

    double totalArea = Math.PI * R * R; // of all 4 quadrants
    return (totalArea - 4.0 * totalSafeArea) / totalArea;
  }

  private static double[] calcIntersectionTL(double f, double g, double s, double holeStartX, double holeEndX, double holeStartY)
  {
    // Find where the fly snuggles into the inner ring and the left-hand edge of the hole
    // Result is the distance above holeStartY
    double xf = f;
    double yf = calcYf(s, f, xf, holeStartX, holeStartY);
    if (yf <= f)
    {
      // Fly doesn't fit in this hole at all
      return null;
    }
    if (yf > g - f)
    {
      yf = g - f;
      // How far right do we need to go to hit the curve of the inner ring?
      xf = calcXf(s, f, g - f, holeStartX, holeStartY);
      // Is it overlapping the right-hand edge of the hole? [Is this even possible?]
      if (xf > holeEndX - f)
      {
        xf = holeEndX - f;
      }
    }
    return new double[]{xf, yf};
  }

  private static double[] calcIntersectionBR(double f, double g, double s, double holeStartX, double holeEndY, double holeStartY)
  {
    // Find where the fly snuggles into the inner ring and the left-hand edge of the hole
    // Result is the distance above holeStartY
    double yf = f;
    double xf = calcXf(s, f, yf, holeStartX, holeStartY);
    if (xf <= f)
    {
      // Fly doesn't fit in this hole at all
      return null;
    }
    if (xf > g - f)
    {
      xf = g - f;
      // How far right do we need to go to hit the curve of the inner ring?
      yf = calcYf(s, f, g - f, holeStartX, holeStartY);
      // Is it overlapping the right-hand edge of the hole? [Is this even possible?]
      if (yf > holeEndY - f)
      {
        yf = holeEndY - f;
      }
    }
    return new double[]{xf, yf};
  }

  private static double calcXf(double S, double f, double yf, double x0, double y0)
  {
    double cosTheta = (y0 + yf) / (S - f);
    double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
    double xf = (S - f) * sinTheta - x0;
    return xf;
  }

  private static double calcYf(double S, double f, double xf, double x0, double y0)
  {
    double sinTheta = (x0 + xf) / (S - f);
    double cosTheta = Math.sqrt(1.0 - sinTheta * sinTheta);
    double yf = (S - f) * cosTheta - y0;
    return yf;
  }

  /**
   * Returns the y value of a point on the circle, given the x (or vice versa).
   * Circle is centred at (0, 0)
   */
  private static double getOtherCoordinateOnCircle(double radius, double coord)
  {
    return radius * Math.cos(Math.asin(coord / radius));
  }

  private static class Main extends CodeJamMain
  {
    private static final DecimalFormat format = new DecimalFormat("0.000000");

    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      double[] d = reader.readDoubles();
//            if (t != 2)
//            {
//                return "Ignore";
//            }
      double result = solve(d[0], d[1], d[2], d[3], d[4]);
      return format.format(result);
    }

  }

  public static void main(String[] args) throws IOException
  {
    new Main().doMain("flyswatter-large");
//        new Main().doMain("flyswatter-small");
//        new Main().doMain("flyswatter-test");
  }
}
