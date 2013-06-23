package org.hicksst.codejam.cheaters;

import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.util.Iterator;

public class Cheaters
{
  private static final int TOTAL_NUMBERS = 37;

  private static double solve(long budget, long[] betAmounts)
  {
    SortedMultiset<Long> bets = TreeMultiset.create();
    for (long betAmount : betAmounts)
    {
      bets.add(betAmount);
    }
    if (betAmounts.length < TOTAL_NUMBERS)
    {
      // The remainder are all 0
      bets.add(0L, TOTAL_NUMBERS - betAmounts.length);
    }

    SortedMultiset<Long> betDistribution = TreeMultiset.create();
    return findMaxProfit(budget, bets, 0.0, 0L, betDistribution);
  }

  private static double findMaxProfit(long budget, SortedMultiset<Long> bets, double maxProfit, long numBetsPlaced, SortedMultiset<Long> betDistribution)
  {
    Multiset.Entry<Long> min = bets.pollFirstEntry();
    assert min != null;
    Multiset.Entry<Long> next = bets.pollFirstEntry();
    if (next == null)
    {
      return maxProfit;
    }

    long numPossibleLayers = budget / min.getCount();
    long layersBeforeNext = next.getElement() - min.getElement();
    long actualLayers = Math.min(numPossibleLayers, layersBeforeNext - 1);
    // Try filling up to just below next
    if (actualLayers > 0)
    {
      long newNumBetsPlaced = actualLayers * min.getCount() + numBetsPlaced;
      double profit = 36.0 * newNumBetsPlaced / min.getCount() - newNumBetsPlaced;
      maxProfit = Math.max(maxProfit, profit);
    }

    if (!betDistribution.isEmpty())
    {
      double profit = calcProfit(budget, numBetsPlaced, betDistribution, min, actualLayers);
      maxProfit = Math.max(maxProfit, profit);
      if (actualLayers > 0)
      {
        profit = calcProfit(budget, numBetsPlaced, betDistribution, min, actualLayers - 1);
        maxProfit = Math.max(maxProfit, profit);
      }
    }

    // Fill up to the next level - so we're now sharing with other
    // ...can we fill up to there?
    if (numPossibleLayers >= layersBeforeNext)
    {
      long numToPlace = layersBeforeNext * min.getCount();
      long newNumBetsPlaced = numBetsPlaced + numToPlace;
      // we have a bet on all of min.getCount numbers, but not on any of next.getCount numbers yet...
      double profit = 36.0 * newNumBetsPlaced / (min.getCount() + next.getCount()) - newNumBetsPlaced;
      maxProfit = Math.max(maxProfit, profit);

      betDistribution.add(min.getElement(), min.getCount() - betDistribution.size());
      // Update the bets to combine min with next
      bets.add(next.getElement(), min.getCount() + next.getCount());
      profit = findMaxProfit(budget - numToPlace, bets, maxProfit, newNumBetsPlaced, betDistribution);
      maxProfit = Math.max(maxProfit, profit);
    }
    return maxProfit;
  }

  private static double calcProfit(long budget, long numBetsPlaced, SortedMultiset<Long> betDistribution, Multiset.Entry<Long> min, long actualLayers)
  {
    // We can try "covering up" numbers that contain other people's bets, so they're no longer minima
    // Try actualLayers + any left over
    long numUsedInWholeLines = actualLayers * min.getCount();

    // We've moved all the minima up to newMinValue
    long newMinValue = min.getElement() + actualLayers;
    long newNumBetsPlaced = numBetsPlaced + numUsedInWholeLines;

    long numLeftOverFromWholeLines = budget - numUsedInWholeLines;
    // Optimal is to cover up all that contain other people's (???)
    int numZerosAtStart = betDistribution.count(0L);
    int numNumbersWithOtherPeoplesBets = min.getCount() - numZerosAtStart;

    int numNumbersToCover = (int) Math.min(numNumbersWithOtherPeoplesBets, numLeftOverFromWholeLines);
    int numberWithMinimumValue = min.getCount() - numNumbersToCover;

    int numThatHaveMinValue = min.getCount() - betDistribution.size();
    betDistribution.add(min.getElement(), numThatHaveMinValue);

    long countOfOurBetsBeingIgnored = 0;
    Iterator<Multiset.Entry<Long>> iter = betDistribution.descendingMultiset().entrySet().iterator();
    for (int i = 0; i < numNumbersToCover && iter.hasNext(); )
    {
      Multiset.Entry<Long> entry = iter.next();
      int maxNumbersToInclude = numNumbersToCover - i;
      int numToInclude = Math.min(entry.getCount(), maxNumbersToInclude);
      countOfOurBetsBeingIgnored += numToInclude * (newMinValue - entry.getElement());
      i += entry.getCount();
    }

    // Reverse our earlier add which was just for convenience
    betDistribution.remove(min.getElement(), numThatHaveMinValue);

    return 36.0 * (newNumBetsPlaced - countOfOurBetsBeingIgnored) / numberWithMinimumValue - (newNumBetsPlaced + numNumbersToCover);
  }

  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("cheaters-large");
//    main.doMain("cheaters-small");
//    main.doMain("cheaters-test");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      long[] longs = reader.readLongs();
      long B = longs[0];
      int N = (int) longs[1];
      long[] Xs = reader.readLongs();
      assert Xs.length == N;
      return String.valueOf(solve(B, Xs));
    }
  }
}