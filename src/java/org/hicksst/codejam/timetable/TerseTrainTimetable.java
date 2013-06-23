package org.hicksst.codejam.timetable;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.util.Arrays;

public class TerseTrainTimetable
{
  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("timetable-large");
//        main.doMain("timetable-small");
//        main.doMain("timetable-test");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      int turnaroundTime = reader.readInt();
      int[] ints = reader.readInts();

      int numA = ints[0], numB = ints[1];

      int[] aTimes = new int[numA + numB];
      int[] bTimes = new int[numA + numB];

      int a = 0, b = 0;
      for (int i = 0; i < numA; i++)
      {
        String[] times = reader.readStrings();
        int depart = parseToMins(times[0]);
        aTimes[a++] = depart * 2 + 1; // Departures are odd
        int arrive = parseToMins(times[1]) + turnaroundTime;
        bTimes[b++] = arrive * 2; // Arrivals are even
      }

      for (int i = 0; i < numB; i++)
      {
        String[] times = reader.readStrings();
        int depart = parseToMins(times[0]);
        bTimes[b++] = depart * 2 + 1;
        int arrive = parseToMins(times[1]) + turnaroundTime;
        aTimes[a++] = arrive * 2;
      }

      Arrays.sort(aTimes);
      Arrays.sort(bTimes);

      int aTrainsRequired = calcRequired(aTimes);
      int bTrainsRequired = calcRequired(bTimes);

      return aTrainsRequired + " " + bTrainsRequired;
    }

    private int calcRequired(int[] times)
    {
      int maxRequired = 0;
      int n = 0;
      for (int time : times)
      {
        n += (time % 2) * 2 - 1; // departure (odd) --> add 1; arrival (even) --> subtract 1
        maxRequired = Math.max(n, maxRequired);
      }
      return maxRequired;
    }
  }

  private static int parseToMins(String time)
  {
    String[] split = time.split(":");
    return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
  }
}