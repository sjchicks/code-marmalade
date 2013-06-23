package org.hicksst.codejam.numbersets;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class NumberSets
{
  private static final boolean DEBUG = false;

  private final long a;
  private final long b;
  private final long p;

  public NumberSets(long a, long b, long p)
  {
    this.a = a;
    this.b = b;
    this.p = p;
  }

  private long solve()
  {
    int lastFactor = (int) b / 2;//Math.ceil(Math.sqrt(b));

    int range = (int) (b - a + 1);
    NumSet[] sets = new NumSet[range];

    boolean[] sieve = new boolean[lastFactor + 1];
    for (int f = 2; f <= lastFactor; f++)
    {
      if (sieve[f])
      {
        // non-prime so ignore
        continue;
      }
      // update the sieve
      for (int multiple = f; multiple <= lastFactor; multiple += f)
      {
        sieve[multiple] = true;
      }
      if (DEBUG)
      {
        System.out.println(f);
      }
      // do the actual stuff
      if (f >= p)
      {
        long div = a / f;
        long mod = a % f;
        long x = mod == 0 ? a : (div + 1) * f;
        if (DEBUG)
        {
          System.out.print("Set " + f + ": ");
        }
        NumSet set = new NumSet(f);
        while (x <= b)
        {
          if (DEBUG)
          {
            System.out.print(x + " ");
          }
          int indexIntoSets = (int) (x - a);
          NumSet existingSet = sets[indexIntoSets];
          if (existingSet != null)
          {
            existingSet.merge(set);
          }
          else
          {
            sets[indexIntoSets] = set;
          }
          x += f;
        }
        if (DEBUG)
        {
          System.out.println();
        }
      }
    }
    System.out.println("Finished finding factors, calculating result...");
    Set<NumSet> distinctSets = new HashSet<>();
    long totalSets = 0;
    for (NumSet set : sets)
    {
      if (set == null)
      {
        totalSets++;
      }
      else if (distinctSets.add(set))
      {
        totalSets++;
        set.addMergedSets(distinctSets);
      }
    }
    return totalSets;
  }

  public static void main(String[] args) throws IOException
  {
//        new Main().doMain("numbersets-small");
    new Main().doMain("numbersets-large");
//        new Main().doMain("numbersets-test");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      long[] abp = reader.readLongs();
      return String.valueOf(new NumberSets(abp[0], abp[1], abp[2]).solve());
    }
  }

  private static class NumSet
  {
    private final int num;

    private Set<NumSet> mergedSets;

    private NumSet(int num)
    {
      this.num = num;
    }

    @Override
    public String toString()
    {
      return "NumSet(" + num + ", " + (mergedSets == null ? "0" : mergedSets.size()) + ")";
    }

    public void merge(NumSet set)
    {
      if (mergedSets == null)
      {
        if (set.mergedSets == null)
        {
          set.mergedSets = mergedSets = new HashSet<>();
          mergedSets.add(set);
        }
        else
        {
          mergedSets = set.mergedSets;
        }
        mergedSets.add(this);
      }
      else
      {
        if (set.mergedSets == null)
        {
          set.mergedSets = mergedSets;
          mergedSets.add(set);
        }
        else
        {
          mergedSets.addAll(set.mergedSets);
          set.mergedSets = mergedSets;
        }
      }
    }

    public void addMergedSets(Set<NumSet> distinctSets)
    {
      if (mergedSets != null)
      {
        distinctSets.addAll(mergedSets);
      }
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }

      NumSet numSet = (NumSet) o;

      if (num != numSet.num)
      {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode()
    {
      return num;
    }
  }

  private static class LinkedListNumSet
  {
    private final int num;

    private LinkedListNumSet next;

    private LinkedListNumSet(int num)
    {
      this.num = num;
      this.next = this;
    }

    @Override
    public String toString()
    {
      return "NumSet(" + num + ")";
    }

    public void merge(LinkedListNumSet set)
    {
      if (set.num != next.num)// Don't add the same thing twice
      {
        set.next = this.next;
        this.next = set;
      }
    }

    public void addMergedSets(Set<LinkedListNumSet> distinctSets)
    {
      LinkedListNumSet current = this;
      do
      {
        distinctSets.add(current);
        current = current.next;
      }
      while (current != this);
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }

      NumSet numSet = (NumSet) o;

      if (num != numSet.num)
      {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode()
    {
      return num;
    }
  }
}
