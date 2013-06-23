package org.hicksst.codejam.pogo;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;

public class Pogo
{
  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
//    main.doMain("pogo-test");
//    main.doMain("pogo-small");
    main.doMain("pogo-large");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      int[] ints = reader.readInts();
      return solve(ints[0], ints[1]);
    }
  }

  private static void checkIt(int x, int y, String result)
  {
    int cx = 0;
    int cy = 0;
    for (int i = 0; i < result.length(); i++)
    {
      int num = i + 1;
      char ch = result.charAt(i);
      switch (ch)
      {
        case 'N':
          cy += num;
          break;
        case 'S':
          cy -= num;
          break;
        case 'E':
          cx += num;
          break;
        case 'W':
          cx -= num;
          break;
        default:
          throw new IllegalArgumentException("Noooo: " + ch);
      }
    }
    if (cx != x || cy != y)
    {
      System.out.println("FAIL: result = " + result + ", expected " + x + "," + y + " - actual  " + cx + "," + cy);
    }
  }

  private static String solve(int x, int y)
  {
//        System.out.println("Solving: " + x + "," + y);
    int numMoves = Math.max(getMinMoves(x + y), getMinMoves(x - y));
//        System.out.println("Min moves: " + numMoves);
    int[] xPlusY = decompose(x + y, numMoves);
//        System.out.println("X+Y from -1/1s: " + Arrays.toString(xPlusY) + ": " + showNum(xPlusY));
    int[] xMinusY = decompose(x - y, numMoves);
//        System.out.println("X-Y from -1/1s: " + Arrays.toString(xMinusY) + ": " + showNum(xMinusY));
    return createRoute(xPlusY, xMinusY);
  }

  private static int showNum(int[] ints)
  {
    int result = 0;
    for (int i = 0; i < ints.length; i++)
    {
      result += ints[i] * (i + 1);
    }
    return result;
  }

  private static int getMinMoves(int i)
  {
    int absI = Math.abs(i);
    int iOddness = absI % 2;
    int guess = (int) Math.round(Math.ceil(0.5 * (Math.sqrt(1 + 8 * absI) - 1)));
    while (iOddness != calcMaxValue(guess) % 2)
    {
      guess++;
    }
//        System.out.println("For " + i + ": " + guess);
    return guess;
  }

  private static String createRoute(int[] xPlusY, int[] xMinusY)
  {
    char[] sb = new char[xPlusY.length];
    for (int i = 0; i < xPlusY.length; i++)
    {
      // 0, 1 --> N
      // 0, -1 --> S
      // 1, 0 --> E
      // -1, 0 --> W
      if (xPlusY[i] == -1)
      {
        if (xMinusY[i] == -1)
        {
          sb[i] = 'W';
        }
        else
        {
          sb[i] = 'S';
        }
      }
      else
      {
        if (xMinusY[i] == -1)
        {
          sb[i] = 'N';
        }
        else
        {
          sb[i] = 'E';
        }
      }
    }
    return new String(sb);
  }

  private static int[] decompose(int i, int numMoves)
  {
    int absI = Math.abs(i);
    int maxValue = calcMaxValue(numMoves);
    int[] result = new int[numMoves];
    for (int j = 0; j < result.length; j++)
    {
      result[j] = i > 0 ? 1 : -1;
    }
    int maxAvailable = numMoves;
    int totalToFlip = (maxValue - absI) / 2;
    while (totalToFlip > 0)
    {
      int nextToFlip;
      if (totalToFlip > maxAvailable)
      {
        nextToFlip = maxAvailable;
        maxAvailable--;
      }
      else
      {
        nextToFlip = totalToFlip;
      }
      result[nextToFlip - 1] *= -1;
      totalToFlip -= nextToFlip;
    }
    return result;
  }

  private static int calcMaxValue(int n)
  {
    return n * (n + 1) / 2;
  }

}
