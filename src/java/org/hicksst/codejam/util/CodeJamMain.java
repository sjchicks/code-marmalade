package org.hicksst.codejam.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class CodeJamMain
{
  private static final String DATA_ROOT_DIR = "data";

  public void doMain(String filePrefix) throws IOException
  {
    long startTime = System.currentTimeMillis();
    String dataDirName = filePrefix.substring(0, filePrefix.indexOf('-'));
    String dataDir = DATA_ROOT_DIR + '/' + dataDirName;
    try (CodeJamReader reader = new CodeJamReader(dataDir + '/' + filePrefix + ".in");
         BufferedWriter writer = new BufferedWriter(new FileWriter(dataDir + '/' + filePrefix + ".out")))
    {
      int numTestCases = reader.readInt();
      for (int t = 1; t <= numTestCases; t++)
      {
        String result = parseAndSolve(t, reader);
        writer.write("Case #" + t + ": " + result + '\n');
        writer.flush();
      }
    }
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    String durationStr = duration < 1000 ? duration + "ms" : (duration / 1000L) + "s";
    System.out.println("Completed: " + durationStr);
  }

  protected abstract String parseAndSolve(int t, CodeJamReader reader) throws IOException;
}
