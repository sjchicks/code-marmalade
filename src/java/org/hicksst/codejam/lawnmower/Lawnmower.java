package org.hicksst.codejam.lawnmower;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Lawnmower
{
  public static void main(String[] args) throws IOException
  {
    new Lawnmower().run();
  }

  private void run() throws IOException
  {
    String inputFileName = "C:\\Documents and Settings\\Steve Hicks\\My Documents\\Downloads\\B-large-practice.in";
    String outputFileName = "C:/temp/lawnmower-output.txt";
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
         BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName)))
    {
      int numTests = Integer.parseInt(reader.readLine());
      for (int t = 0; t < numTests; t++)
      {
        String[] nm = reader.readLine().split(" ");
        int n = Integer.parseInt(nm[0]);
        int m = Integer.parseInt(nm[1]);
        int[][] grid = new int[n][m];
        for (int i = 0; i < n; i++)
        {
          String row = reader.readLine();
          String[] columns = row.split(" ");
          for (int j = 0; j < columns.length; j++)
          {
            grid[i][j] = Integer.parseInt(columns[j]);
          }
        }
        boolean mowable = isMowable(grid, n, m);
        writer.write("Case #" + (t + 1) + ": " + (mowable ? "YES" : "NO") + '\n');
      }
    }
  }

  private boolean isMowable(int[][] grid, int n, int m)
  {
    int[] rowMaxes = new int[n];
    int[] columnMaxes = new int[m];
    for (int i = 0; i < n; i++)
    {
      for (int j = 0; j < m; j++)
      {
        int val = grid[i][j];
        if (val > rowMaxes[i])
        {
          rowMaxes[i] = val;
        }
        if (val > columnMaxes[j])
        {
          columnMaxes[j] = val;
        }
      }
    }
    for (int i = 0; i < n; i++)
    {
      for (int j = 0; j < m; j++)
      {
        int val = grid[i][j];
        if (val < rowMaxes[i] && val < columnMaxes[j])
        {
          return false;
        }
      }
    }

    return true;
  }
}
