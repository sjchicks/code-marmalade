package org.hicksst.codejam.pong;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;

/**
 * TODO: Needs to be simplified via symmetry, then use Euclid to solve.
 */
public class MultiplayerPong
{
  private static final String DRAW = "DRAW";

  private static final long DONT_CHECK_DIFF = Long.MAX_VALUE;

  private final long height;
  private final long width;
  private final long leftPlayers;
  private final long rightPlayers;
  private final long leftSpeed;
  private final long rightSpeed;
  private final long by;
  private final long bx;
  private final long vy;
  private final long vx;
  private final long heightModulo;

  public MultiplayerPong(long height, long width,
                         long leftPlayers, long rightPlayers,
                         long leftSpeed, long rightSpeed,
                         long By, long Bx,
                         long Vy, long Vx)

  {

    this.height = height;
    this.width = width;
    this.leftPlayers = leftPlayers;
    this.rightPlayers = rightPlayers;
    this.leftSpeed = leftSpeed;
    this.rightSpeed = rightSpeed;
    by = By;
    bx = Bx;
    vy = Vy;
    vx = Vx;
    heightModulo = height * vx;
  }

  private static enum Wall
  {
    LEFT, RIGHT
  }

  private String solve()
  {
    System.out.println("height: " + height + ", width: " + width);
    System.out.println("leftPlayers: " + leftPlayers + ", rightPlayers: " + rightPlayers);
    System.out.println("leftSpeed=" + leftSpeed + ", rightSpeed=" + rightSpeed + ", by=" + by + ", bx=" + bx + ", vy=" + vy + ", vx=" + vx);

    if (vy == 0)
    {
      // As long as > 0 players
      return DRAW;
    }

    Wall firstWall;
    // First wall hit
    long xDistanceToFirstWallHit;
    if (vx == 0)
    {
      return DRAW;
    }
    else if (vx > 0)
    {
      xDistanceToFirstWallHit = width - bx;
      firstWall = Wall.RIGHT;
    }
    else
    {
      xDistanceToFirstWallHit = bx;
      firstWall = Wall.LEFT;
    }
    long firstWallHitTimeByVx = xDistanceToFirstWallHit;
    long initYValueByVx = by * vx;

    // Starting point from end vertically
    long initialBallDistanceToEndByVx = vy > 0 ? height * vx - initYValueByVx : initYValueByVx;
    // Now get to the first wall hit
    long firstWallHitDistanceToEndByVx = mod(initialBallDistanceToEndByVx, vy * firstWallHitTimeByVx, DONT_CHECK_DIFF);
    long secondWallHitDistanceToEndByVx = mod(firstWallHitDistanceToEndByVx, width * Math.abs(vy), DONT_CHECK_DIFF);

    long firstYDistanceToEndLeftByVx;
    long firstYDistanceToEndRightByVx;
    if (firstWall == Wall.LEFT)
    {
      firstYDistanceToEndLeftByVx = firstWallHitDistanceToEndByVx;
      firstYDistanceToEndRightByVx = secondWallHitDistanceToEndByVx;
    }
    else
    {
      firstYDistanceToEndRightByVx = firstWallHitDistanceToEndByVx;
      firstYDistanceToEndLeftByVx = secondWallHitDistanceToEndByVx;
    }

    System.out.println("Left...");
    TeamResult leftResult = getTeamResult(leftPlayers, leftSpeed, firstYDistanceToEndLeftByVx);
    System.out.println("Left: " + leftResult);
    System.out.println("Right...");
    TeamResult rightResult = getTeamResult(rightPlayers, rightSpeed, firstYDistanceToEndRightByVx);
    System.out.println("Right: " + rightResult);

    String result;
    if (leftResult.isNeverFails() && rightResult.isNeverFails())
    {
      result = "DRAW";
    }
    else if (leftResult.isNeverFails())
    {
      result = "LEFT " + rightResult.getNum();
    }
    else if (rightResult.isNeverFails())
    {
      result = "RIGHT " + leftResult.getNum();
    }
    else
    {
      long rightNum = rightResult.getNum();
      long leftNum = leftResult.getNum();
      if (rightNum < leftNum)
      {
        result = "LEFT " + rightNum;
      }
      else if (rightNum > leftNum)
      {
        result = "RIGHT " + leftNum;
      }
      else
      {
        result = (firstWall == Wall.LEFT ? "RIGHT " : "LEFT ") + leftNum;
      }
    }

    System.out.println("========== Result: " + result);

    return result;
  }

  private TeamResult getTeamResult(long numPlayers, long speed, long firstYDistanceToEndByVx)
  {
    long timeBetweenHitsForOnePlayerByVx = 2 * width * numPlayers;
    long maxPossibleDistanceByVx = timeBetweenHitsForOnePlayerByVx * speed;

    long distanceToAddForOnePlayerByVx = timeBetweenHitsForOnePlayerByVx * Math.abs(vy);
    long distanceToAddForNextPlayerOnTeamByVx = 2 * width * Math.abs(vy);

    System.out.println("maxPossibleDistanceByVx: " + maxPossibleDistanceByVx);
    System.out.println("move to next hit for single player: " + distanceToAddForOnePlayerByVx);
    System.out.println("move to next hit for next player on team: " + distanceToAddForNextPlayerOnTeamByVx);

    if (distanceToAddForOnePlayerByVx <= maxPossibleDistanceByVx)
    {
      System.out.println("Shortcut never gonna fail.");
      return TeamResult.neverFails();
    }

    long mod = distanceToAddForOnePlayerByVx % heightModulo;
    long div = distanceToAddForOnePlayerByVx / heightModulo;
    System.out.println("mod=" + mod + ", div=" + div);
    if (div % 2 == 0)
    {
      // We're an even number of 'heights' away, so we start moving 'mod' places from where we started.
      System.out.println("max diff=" + mod);
      if (mod <= maxPossibleDistanceByVx)
      {
        System.out.println("Shortcut 2 never gonna fail.");
        return TeamResult.neverFails();
      }
      // max diff occurs when d >= mod, min when d = mod/2
      if (firstYDistanceToEndByVx >= mod)
      {
        // we can fit in a full 'mod', so we fail
        return TeamResult.failsAfter(numPlayers);
      }
      // at d = 0, diff = mod
      long minDiff;
      long minD;
      // if mod%2=0, at d = mod/2, diff = 0
      if (mod % 2 == 0)
      {
        minD = mod / 2;
        minDiff = 0;
      }
      else// if mod%2=1, at d = mod/2 and mod/2+2, diff = 1
      {
        minD = mod / 2;
        minDiff = 1;
      }
      // at d = mod

      // what range are we below maxPossibleDistanceByVx?
      long x = minD * (mod - maxPossibleDistanceByVx);
      long y = mod - minDiff;
      long lowerBound = x / y;
      if (x % y == 0)// If it divides exactly, move to the next one down, so we're using '<='
      {
        lowerBound--;
      }
      long upperBound = mod - lowerBound;
    }
    else
    {
      // We're an odd number of 'heights' away, so we start moving 'mod' places from a 'mirror' position.
      System.out.println("dist without modulo=2*d-" + heightModulo);
    }

    long currentEarliestNumSuccessfulHits = Long.MAX_VALUE;

    for (long player = 0; player < numPlayers; player++)
    {
      long distanceToEndByVx = firstYDistanceToEndByVx;
      System.out.println("player " + player + ": start=" + distanceToEndByVx);
      for (int k = 0; k < 15; k++)
      {
        try
        {
          distanceToEndByVx = mod(distanceToEndByVx, distanceToAddForOnePlayerByVx, maxPossibleDistanceByVx);
        }
        catch (RuntimeException e)
        {
          // Always make the first change, k = 0 ==> succeeds on 1st, fails on 2nd hit
          long failedOnPlayerHit = k + 2;
          // full rounds + individual players before this player
          long numTeamHits = (failedOnPlayerHit - 1) * numPlayers + player;
          System.out.println("player " + player + " pulled out at " + k + ", failedOnPlayerHit: " + failedOnPlayerHit + ", numTeamHits: " + numTeamHits);
          currentEarliestNumSuccessfulHits = Math.min(numTeamHits, currentEarliestNumSuccessfulHits);
          break;
        }
      }
      firstYDistanceToEndByVx = mod(firstYDistanceToEndByVx, distanceToAddForNextPlayerOnTeamByVx, DONT_CHECK_DIFF);// increase the starting point for the next player
    }

    if (currentEarliestNumSuccessfulHits == Long.MAX_VALUE)
    {
      return TeamResult.neverFails();
    }
    else
    {
      return TeamResult.failsAfter(currentEarliestNumSuccessfulHits);
    }
  }

  private static class TeamResult
  {
    private final long num;

    public TeamResult(long num)
    {
      this.num = num;
    }

    public long getNum()
    {
      return num;
    }

    public boolean isNeverFails()
    {
      return num == -1;
    }

    @Override
    public String toString()
    {
      return isNeverFails() ? "NEVER FAILS" : "FAILS AFTER " + num + " SUCCESSES";
    }

    private static TeamResult failsAfter(long num)
    {
      return new TeamResult(num);
    }

    private static TeamResult neverFails()
    {
      return new TeamResult(-1);
    }
  }

  private long mod(long distanceToEndByVx, long change, long checkDiffAgainst)
  {
    long newVal = distanceToEndByVx - change;
    long diffFromLast;//Amount to add to distanceToEndByVx
    if (newVal > 0)
    {
      diffFromLast = -change;
      if (checkDiffAgainst != DONT_CHECK_DIFF)
      {
        System.out.println(newVal + ", diff: " + diffFromLast);
        if (Math.abs(diffFromLast) > checkDiffAgainst)
        {
          throw new RuntimeException("THE END!");
        }
      }
      return newVal;
    }
    long mod = newVal % heightModulo;
    long div = newVal / heightModulo;
    long oppOrSame = div % 2;
    long newDTEBVx = heightModulo + mod;
    if (oppOrSame == 0)
    {
      // Going in opposite dir
      diffFromLast = (heightModulo - newDTEBVx) - distanceToEndByVx;
      if (checkDiffAgainst != DONT_CHECK_DIFF)
      {
        System.out.println(newDTEBVx + ", diff: " + diffFromLast);
        if (Math.abs(diffFromLast) > checkDiffAgainst)
        {
          throw new RuntimeException("THE END!");
        }
      }
    }
    else
    {
      // Going in same dir
      diffFromLast = newDTEBVx - distanceToEndByVx;
      if (checkDiffAgainst != DONT_CHECK_DIFF)
      {
        System.out.println(newDTEBVx + ", diff: " + diffFromLast);
        if (Math.abs(diffFromLast) > checkDiffAgainst)
        {
          throw new RuntimeException("THE END!");
        }
      }
    }
    return newDTEBVx;
  }

  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
//    main.doMain("pong-small");
    main.doMain("pong-test");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      System.out.println("======== Test " + t + " ===========");
      long[] longs = reader.readLongs();
      long height = longs[0];
      long width = longs[1];
      longs = reader.readLongs();
      long leftPlayers = longs[0];
      long rightPlayers = longs[1];
      longs = reader.readLongs();
      long leftSpeed = longs[0];
      long rightSpeed = longs[1];
      longs = reader.readLongs();
      long ballY = longs[0];
      long ballX = longs[1];
      long ballSpeedY = longs[2];
      long ballSpeedX = longs[3];
      return new MultiplayerPong(height, width, leftPlayers, rightPlayers, leftSpeed, rightSpeed, ballY, ballX, ballSpeedY, ballSpeedX).solve();
    }

  }
}
