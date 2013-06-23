package org.hicksst.codejam.flyswatter;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.text.DecimalFormat;

public class FlySwatter
{
  private static double solve(final double f, final double R, final double t, final double r, final double g)
  {
    // Shortcut if the fly won't fit inside any hole
    if (2 * f >= g)
    {
      return 1.0;
    }

    // inner ring radius
    final double S = R - t;

    final double radiusOfCircle = S - f;// We're one fly's radius away from the edge of the circle
    final double radiusOfCircleSquared = radiusOfCircle * radiusOfCircle;

    final double distanceToNextHoleStart = 2 * r + g;

    final double safeLengthOfWholeHole = g - 2 * f;
    final double safeAreaOfWholeHole = safeLengthOfWholeHole * safeLengthOfWholeHole;

    final double gMinusF = g - f;

    double totalSafeArea = 0;

    final double maxStartX = getOtherCoordinateOnCircle(S, r);

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

        double tly = calcYf(S, f, f, holeStartX, holeStartY);
        if (tly <= f)
        {
          continue;
        }
        double tlx;
        if (tly > gMinusF)
        {
          tly = gMinusF;
          tlx = calcXf(S, f, gMinusF, holeStartX, holeStartY);
          if (tlx > holeEndX - f)
          {
            tlx = holeEndX - f;
          }
        }
        else
        {
          tlx = f;
        }
        double brx = calcYf(S, f, f, holeStartY, holeStartX);
        if (brx <= f)
        {
          continue;
        }
        double bry;
        if (brx > gMinusF)
        {
          brx = gMinusF;
          bry = calcXf(S, f, gMinusF, holeStartY, holeStartX);
          if (bry > holeEndY - f)
          {
            bry = holeEndY - f;
          }
        }
        else
        {
          bry = f;
        }

        double areaOfLeftRectangle = (tlx - f) * (tly - f);
        double areaOfBottomRectangle = (brx - tlx) * (bry - f); // with no intersection with the left rectangle
        totalSafeArea += areaOfLeftRectangle + areaOfBottomRectangle;

        double x0 = holeStartX + tlx;
        double x1 = holeStartX + brx;
        double y0 = holeStartY + bry;
        double y1 = holeStartY + tly;
        double areaToRemoveFromSegment = 0.5 * (x1 * y0 + x0 * y1) - x0 * y0;

        // result of asin = -pi/2...pi/2
        double proportionOfCircle = 0.5 * (Math.asin(y1 / radiusOfCircle) - Math.asin(y0 / radiusOfCircle));
        double areaOfSegment = radiusOfCircleSquared * proportionOfCircle;

        totalSafeArea = totalSafeArea + areaOfSegment - areaToRemoveFromSegment;
      }
    }

    double totalArea = Math.PI * R * R; // of all 4 quadrants
    return (totalArea - 4.0 * totalSafeArea) / totalArea;
  }

  private static double calcXf(double S, double f, double yf, double x0, double y0)
  {
    double cosTheta = (y0 + yf) / (S - f);
    double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
    return (S - f) * sinTheta - x0;
  }

  private static double calcYf(double S, double f, double xf, double x0, double y0)
  {
    double sinTheta = (x0 + xf) / (S - f);
    double cosTheta = Math.sqrt(1.0 - sinTheta * sinTheta);
    return (S - f) * cosTheta - y0;
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
