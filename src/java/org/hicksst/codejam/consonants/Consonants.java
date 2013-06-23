package org.hicksst.codejam.consonants;

import org.hicksst.codejam.util.CodeJamReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Consonants
{
  private final String inFile;
  private final String outFile;

  public Consonants(String filePrefix)
  {
    inFile = filePrefix + ".in";
    outFile = filePrefix + ".out";
  }

  private void run() throws IOException
  {
    long startTime = System.currentTimeMillis();
    try (CodeJamReader reader = new CodeJamReader(inFile);
         BufferedWriter writer = new BufferedWriter(new FileWriter(outFile)))
    {
      int numTests = reader.readInt();
      for (int t = 0; t < numTests; t++)
      {
        String[] strings = reader.readStrings();
        long result = solve(strings[0], Integer.parseInt(strings[1]));
        writer.write("Case #" + (t + 1) + ": " + result + '\n');
      }
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Completed in " + (endTime - startTime) + "ms");
  }

  private long solve(String word, int n)
  {
    int i = 0;
    int start = -1;
    long totalToSubtract = 0;
    int ccc = 0;

    while (i < word.length())
    {
      if (isConsonant(word.charAt(i)))
      {
        ccc++;
        if (ccc == n && start != -1)
        {
          int end = i - 1;
          totalToSubtract += calcTotal(end - start + 1, n);
          start = -2;
        }
      }
      else
      {
        if (ccc >= n)
        {
          start = i - n + 1;
        }
        ccc = 0;
        if (start == -1)
        {
          start = 0;
        }
      }
      i++;
    }

    if (start >= 0)
    {
      totalToSubtract += calcTotal(word.length() - start, n);
    }

    return calcTotal(word.length(), n) - totalToSubtract;
  }

  private boolean isConsonant(char ch)
  {
    return ch != 'a' && ch != 'e' && ch != 'i' && ch != 'o' && ch != 'u';
  }

  private static long calcTotal(int length, int n)
  {
    if (length < n)
    {
      return 0;
    }
    long t = length - n + 1;
    return t * (t + 1) / 2;
  }

  public static void main(String[] args) throws IOException
  {
//        new Consonants("C:/temp/consonants/consonants-test").run();
//        new Consonants("C:/temp/consonants/consonants-small").run();
    new Consonants("C:/temp/consonants/consonants-large").run();
  }
}
