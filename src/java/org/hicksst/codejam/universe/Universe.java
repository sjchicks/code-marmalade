package org.hicksst.codejam.universe;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

public class Universe
{
  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("universe-large");
//    main.doMain("universe-small");
//    main.doMain("universe-test");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      int numEngines = reader.readInt();
      String[] engines = new String[numEngines];
      for (int i = 0; i < numEngines; i++)
      {
        engines[i] = reader.readString().intern();
      }
      int numQueries = reader.readInt();
      String[] queries = new String[numQueries];
      for (int i = 0; i < numQueries; i++)
      {
        queries[i] = reader.readString().intern();
      }
      return Integer.toString(solve(engines, queries));
    }

  }

  private static int solve(String[] engines, String[] queries)
  {
    Object dummyValue = new Object();
    Map<String, Object> seenEngines = new IdentityHashMap<>(3 * engines.length);
    int numSwitches = 0;
    for (String query : queries)
    {
      seenEngines.put(query, dummyValue);
      if (seenEngines.size() == engines.length)
      {
        numSwitches++;
        seenEngines.clear();
        seenEngines.put(query, dummyValue);
      }
    }
    return numSwitches;
  }
}
