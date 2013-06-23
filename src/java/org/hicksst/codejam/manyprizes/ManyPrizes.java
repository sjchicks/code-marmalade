package org.hicksst.codejam.manyprizes;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;

public class ManyPrizes
{
  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("manyprizes-large");
//    main.doMain("manyprizes-small");
//    main.doMain("manyprizes-test");
  }

  private static String solve(int N, long P)
  {
    long maxTeamNum = (1L << N) - 1;

    // biggest num whose worst case gets a prize

    // represent sequence of wins/losses as binary: W=0, L=1
    long maxPrizeNum = P - 1;

    if (maxPrizeNum == maxTeamNum)
    {
      return maxTeamNum + " " + maxTeamNum;
    }

    // All best cases are WWW...LLLL..., so find the first of these <= maxPrizeNum
    int bestCaseNumLs = findNumLsLessThanOrEqualTo(maxPrizeNum);
    int bestCaseNumWs = N - bestCaseNumLs;

    // Highest num with that many Ws
    long x = (1L << bestCaseNumWs) - 1;
    long highestTeamThatCouldWinAPrize = maxTeamNum - x;

    // All worst cases are LLL...WWW..., so find the first of these <= maxPrizeNum
    int worstCaseNumLs = findNumWsLessThanOrEqualTo(maxPrizeNum, N);

    long highestTeamGuaranteedToWinAPrize = (1L << (worstCaseNumLs + 1)) - 2;

    return highestTeamGuaranteedToWinAPrize + " " + highestTeamThatCouldWinAPrize;
  }

  private static int findNumWsLessThanOrEqualTo(long i, int N)
  {
    int currentNumLs = 0;
    long k = 0;
    while (k <= i && currentNumLs <= N)
    {
      k += 1L << (N - 1 - currentNumLs);//This goes crazy when -ve, not ideal
      currentNumLs++;
    }
    return currentNumLs - 1;
  }

  private static int findNumLsLessThanOrEqualTo(long i)
  {
    int bitCount = 0;
    long k = 1;
    while (k - 1 <= i)
    {
      k <<= 1;
      bitCount++;
    }
    return bitCount - 1;
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      long[] longs = reader.readLongs();
      return solve((int) longs[0], longs[1]);
    }
  }
}