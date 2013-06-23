package org.hicksst.codejam.croptriangles;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;

public class CropTriangles
{
  public static void main(String[] args) throws IOException
  {
//        new Main().doMain("croptriangles-small");
    new Main().doMain("croptriangles-large");
//        new Main().doMain("croptriangles-test");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
//            System.out.println("=== Test case " + t);
      int[] ints = reader.readInts();
//            if (t != 3)
//            {
//                return "Ignore";
//            }
      return new CropTriangles().solve(ints[0], ints[1], ints[2], ints[3], ints[4], ints[5], ints[6], ints[7]);
    }
  }

  public String solve(long n, long a, long b, long c, long d, long x0, long y0, long m)
  {
//        System.out.println(n + ", " + a + ", " + b + ", " + c + ", " + d);
    long[] count = new long[9];
    long x = x0;
    long y = y0;
    for (long t = 0; t < n; t++)
    {
//            System.out.println(x + ", " + y);
      int i = (int) (x % 3) * 3 + (int) (y % 3);
      count[i]++;
      x = (a * x + b) % m;
      y = (c * y + d) % m;
    }

    long totalCount = 0;
    for (int i1 = 0; i1 < 9; i1++)
    {
      long count1 = count[i1];
      if (count1 == 0)
      {
        continue;
      }
      count[i1]--;
      for (int i2 = i1; i2 < 9; i2++)
      {
        long count2 = count[i2];
        if (count2 == 0)
        {
          continue;
        }
        count[i2]--;
        int x1 = i1 / 3;
        int y1 = i1 % 3;
        int x2 = i2 / 3;
        int y2 = i2 % 3;
        int x3 = (3 - (x1 + x2) % 3) % 3;
        int y3 = (3 - (y1 + y2) % 3) % 3;
        int i3 = (x3 % 3) * 3 + (y3 % 3);
        long count3 = count[i3];
        if (i3 >= i2 && count3 > 0)
        {
          // either i1==i2==i3 OR i1!=i2!=i3; you can never have only two i's the same.
          if (i1 == i2 && i2 == i3)
          {
            totalCount += calcCombinations(count1);//the first count value is the genuine count for that x,y coordinate.
          }
          else
          {
            totalCount += count1 * count2 * count3;
          }
        }
        count[i2]++;
      }
      count[i1]++;
    }

    return String.valueOf(totalCount);
  }

  private long calcCombinations(long n)
  {
    long r = n * (n - 1) * (n - 2) / 6;
    return r;
  }
}
