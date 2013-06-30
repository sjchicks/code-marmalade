package org.hicksst.codejam.ruralplanning;

import org.hicks.codejam.util.CodeJamMain;
import org.hicks.codejam.util.CodeJamReader;

import java.io.IOException;

public class RuralPlanning
{
  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("RuralPlanning-test");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      return "Ignored";
    }
  }
}