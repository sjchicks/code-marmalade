package org.hicksst.codejam.flyswatter;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.text.DecimalFormat;

public class MoreSymmetricalFlySwatter
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
    double radiusOfCircle = S - f;// We're one fly's radius away from the edge of the circle
    double radiusOfCircleSquared = radiusOfCircle * radiusOfCircle;

    double distanceToNextHoleStart = 2 * r + g;

    double safeLengthOfWholeHole = g - 2 * f;
    double safeAreaOfWholeHole = safeLengthOfWholeHole * safeLengthOfWholeHole;

    double totalSafeArea = 0;

    // Look at top-right quadrant: bottom-left corner (exact centre of racquet) is at (0, 0)

    // For each 'column' of holes...

    double Ssquared = S * S;
    double maxStartX = Math.sqrt(Ssquared - r * r);

    int holeNumToStartWith = 0;

    double holeStartX = r;
    for (; holeStartX < maxStartX; holeStartX += distanceToNextHoleStart)
    {
      double totalAddedInThisColumn = 0;
      double areaOfFirstHole = 0;

      double holeEndX = holeStartX + g;

      double minHoleStartY = r + holeNumToStartWith * distanceToNextHoleStart;
      double maxYAtHoleStartX = Math.sqrt(Ssquared - holeStartX * holeStartX);
      if (minHoleStartY >= maxYAtHoleStartX)
      {
        break;
      }

      double holeStartY;
      if (holeEndX < maxStartX)
      {
        double maxYAtHoleEndX = Math.sqrt(Ssquared - holeEndX * holeEndX);

        double i = (maxYAtHoleEndX - r - g) / distanceToNextHoleStart;
        double numWholeHoles = Math.floor(i + 1.0);

        if ((int) numWholeHoles > holeNumToStartWith)
        {
          // TODO: could do this better
          areaOfFirstHole = safeAreaOfWholeHole;
          totalAddedInThisColumn += (numWholeHoles - holeNumToStartWith) * safeAreaOfWholeHole;
        }

        holeStartY = Math.max(minHoleStartY, r + numWholeHoles * distanceToNextHoleStart);
      }
      else
      {
        holeStartY = Math.max(minHoleStartY, r);
      }

      for (; holeStartY < maxYAtHoleStartX; holeStartY += distanceToNextHoleStart)
      {
        double holeEndY = holeStartY + g;

        double topLeftY = calcYf(S, f, f, holeStartX, holeStartY);
        if (topLeftY <= f)
        {
          continue;
        }
        double bottomRightX = calcXf(S, f, f, holeStartX, holeStartY);
        if (bottomRightX <= f)
        {
          continue;
        }
        double topLeftX;
        if (topLeftY > g - f)
        {
          topLeftY = g - f;
          topLeftX = calcXf(S, f, g - f, holeStartX, holeStartY);
          if (topLeftX > holeEndX - f)
          {
            topLeftX = holeEndX - f;
          }
        }
        else
        {
          topLeftX = f;
        }
        double bottomRightY;
        if (bottomRightX > g - f)
        {
          bottomRightX = g - f;
          bottomRightY = calcYf(S, f, g - f, holeStartX, holeStartY);
          if (bottomRightY > holeEndY - f)
          {
            bottomRightY = holeEndY - f;
          }
        }
        else
        {
          bottomRightY = f;
        }

        double x0 = holeStartX + topLeftX;
        double x1 = holeStartX + bottomRightX;
        double y0 = holeStartY + bottomRightY;
        double y1 = holeStartY + topLeftY;
        double areaToRemoveFromSegment = 0.5 * (x1 * y0 + x0 * y1) - x0 * y0;

        double proportionOfCircle = 0.5 * (Math.asin(y1 / radiusOfCircle) - Math.asin(y0 / radiusOfCircle));

        double areaOfSegment = radiusOfCircleSquared * proportionOfCircle;

        double holeArea = (topLeftX - f) * (topLeftY - f) + (bottomRightX - topLeftX) * (bottomRightY - f) + areaOfSegment - areaToRemoveFromSegment;

        if (areaOfFirstHole == 0.0)
        {
          areaOfFirstHole = holeArea;
        }

        totalAddedInThisColumn = totalAddedInThisColumn + holeArea;
      }
      totalSafeArea += totalAddedInThisColumn * 2.0 - areaOfFirstHole;
      holeNumToStartWith++;
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

  private static class Main extends CodeJamMain
  {
    private static final DecimalFormat format = new DecimalFormat("0.000000");

    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      double[] d = reader.readDoubles();
      double result = solve(d[0], d[1], d[2], d[3], d[4]);
      return format.format(result);
    }
  }

  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("flyswatter-large");
//        new Main().doMain("flyswatter-small");
//        new Main().doMain("flyswatter-test");
  }
}
