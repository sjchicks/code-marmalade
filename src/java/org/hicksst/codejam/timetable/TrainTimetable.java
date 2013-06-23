package org.hicksst.codejam.timetable;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrainTimetable
{
  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("timetable-large");
//    main.doMain("timetable-small")
    //    main.doMain("timetable-test")
  }

  private static class Main extends CodeJamMain
  {
    private void addTime(TrainTime time, List<TrainTime> aTimes, List<TrainTime> bTimes)
    {
      if (time.appliesToA)
      {
        aTimes.add(time);
      }
      else
      {
        bTimes.add(time);
      }
    }

    private void addAllTimes(List<Schedule> schedules, int turnaroundTime, List<TrainTime> aTimes, List<TrainTime> bTimes)
    {
      for (Schedule schedule : schedules)
      {
        addTime(schedule.depart, aTimes, bTimes);
        addTime(schedule.arrive.add(turnaroundTime), aTimes, bTimes);
      }
    }

    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      int turnaroundTime = reader.readInt();
      int[] ints = reader.readInts();
      int numA = ints[0];
      List<Schedule> aSchedules = readSchedules(numA, reader, true);
      int numB = ints[1];
      List<Schedule> bSchedules = readSchedules(numB, reader, false);

      System.out.println(aSchedules);
      System.out.println(bSchedules);

      List<TrainTime> aTimes = new ArrayList<>(numA + numB);
      List<TrainTime> bTimes = new ArrayList<>(numA + numB);

      addAllTimes(aSchedules, turnaroundTime, aTimes, bTimes);
      addAllTimes(bSchedules, turnaroundTime, aTimes, bTimes);

      Comparator<TrainTime> comparator = new Comparator<TrainTime>()
      {
        @Override
        public int compare(TrainTime o1, TrainTime o2)
        {
          int c = Integer.compare(o1.inMins, o2.inMins);
          if (c != 0)
          {
            return c;
          }
          return o1.isArrival && !o2.isArrival ? -1 : (!o1.isArrival && o2.isArrival ? 1 : 0);
        }
      };
      Collections.sort(aTimes, comparator);
      Collections.sort(bTimes, comparator);

      int aTrainsRequired = calcRequired(aTimes);
      int bTrainsRequired = calcRequired(bTimes);

      return aTrainsRequired + " " + bTrainsRequired;
    }

    private int calcRequired(List<TrainTime> trains)
    {
      int maxRequired = 0;
      int n = 0;
      for (TrainTime time : trains)
      {
        if (time.isArrival)
        {
          n = n - 1;
        }
        else
        {
          n = n + 1;
          if (n > maxRequired)
          {
            maxRequired = n;
          }
        }
      }
      return maxRequired;
    }
  }

  private static List<Schedule> readSchedules(int count, CodeJamReader reader, boolean startsAtA) throws IOException
  {
    List<Schedule> schedules = new ArrayList<>(count);
    for (int i = 0; i < count; i++)
    {
      String[] times = reader.readStrings();
      TrainTime depart = new TrainTime(times[0], false, startsAtA);
      TrainTime arrive = new TrainTime(times[1], true, !startsAtA);
      schedules.add(new Schedule(depart, arrive));
    }
    return schedules;
  }

  private static class Schedule
  {
    private final TrainTime arrive;
    private final TrainTime depart;

    private Schedule(TrainTime arrive, TrainTime depart)
    {
      this.arrive = arrive;
      this.depart = depart;
    }

    @Override
    public String toString()
    {
      return "Schedule{" +
          "arrive=" + arrive +
          ", depart=" + depart +
          '}';
    }
  }

  private static class TrainTime
  {
    private final int hours;
    private final int minutes;
    private final boolean isArrival;
    private final boolean appliesToA;
    private final int inMins;

    private TrainTime(int hours, int minutes, boolean isArrival, boolean appliesToA)
    {
      this.hours = hours;
      this.minutes = minutes;
      this.isArrival = isArrival;
      this.appliesToA = appliesToA;
      this.inMins = 60 * hours + minutes;
    }

    public TrainTime(String time, boolean isArrival, boolean appliesToA)
    {
      this(Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1]), isArrival, appliesToA);
    }

    public TrainTime add(int minutes)
    {
      int newMinutes = this.minutes + minutes;
      return new TrainTime(this.hours + newMinutes / 60, newMinutes % 60, this.isArrival, this.appliesToA);
    }

    @Override
    public String toString()
    {
      return hours + ":" + minutes + "(" + (isArrival ? "->" : "<-") + (appliesToA ? "A" : "B") + ")";
    }
  }
}