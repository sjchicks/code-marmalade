package org.hicksst.codejam.pong;

public class Euclid
{
  public static void main(String[] args)
  {
    Euclid euclid = new Euclid();
    check(11, euclid.find(7, 2 * 15, 17, 18));
    check(6, euclid.find(7, 2 * 15, 11, 12));
    check(5, euclid.find(7, 2 * 15, 2, 5));
    check(10, euclid.find(7, 2 * 15, 9, 10));
    check(3, euclid.find(7, 2 * 15, 21, 28));
    check(11, euclid.find(23, 2 * 15, 12, 13));
    check(3, euclid.find(23, 2 * 15, 2, 9));
  }

  private static void check(int expected, int actual)
  {
    if (expected != actual)
    {
      System.out.println("Fail: " + expected + " <> " + actual);
    }
    else
    {
      System.out.println("Success: " + expected + " = " + actual);
    }
  }

  /**
   * Determines the count (starting at 0) of the first number in the sequence (s * k % m) that falls within [a, b]
   */
  private int find(int s, int m, int a, int b)
  {
    System.out.println(s + " * k % " + m + " : [" + a + ", " + b + "]");

    if (s > m / 2)
    {
      // Reduce the problem by at least half - flip vertically
      return find(m - s, m, m - b, m - a);
    }

    // Do we hit the interval before wrapping?
    int k = findWithoutWrapping(s, a, b);
    if (k != -1)
    {
      return k;
    }

    // The first value after wrapping round is p, which is < s
    // On the next wrap, it will be 2p%s, 3p%s, etc.

    // We don't hit the interval without wrapping, so how many wraps do we need before we can hit it?
    int w = find(s - (m % s), s, a % s, b % s);
    // After w wraps, the first value is...
    int firstVal = s - (w * m) % s;
    // We know that we hit the interval after this many wraps, so find the exact place without wrapping
    int h = findWithoutWrapping(s, a - firstVal, b - firstVal);
    // Work out how many lots of 's' it took to get here.
    return (w * m + firstVal) / s + h;
  }

  /**
   * Find min k for which ks falls within [a, b]
   * NB: a,b can be < 0
   */
  private int findWithoutWrapping(int s, int a, int b)
  {
    int k;
    if (a < 0)
    {
      k = 0;
    }
    else
    {
      k = a % s == 0 ? a / s : a / s + 1;
    }
    return k * s <= b ? k : -1;
  }
}

