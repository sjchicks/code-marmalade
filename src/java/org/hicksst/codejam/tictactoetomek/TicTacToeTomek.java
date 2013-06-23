package org.hicksst.codejam.tictactoetomek;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

import java.io.IOException;

/**
 *
 */
public class TicTacToeTomek
{
  public static void main(String[] args) throws IOException
  {
    new Main().doMain("tictactoetomek-large");
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      char[][] grid = new char[4][4];
      for (int row = 0; row < 4; row++)
      {
        grid[row] = reader.readChars();
      }
      reader.readLine();
      return checkGrid(grid);
    }
  }

  private static String checkGrid(char[][] grid)
  {
    // 0-3 = rows
    // 4-7 = cols
    // 8   = diag (TL to BR)
    // 9   = diag (TR to BL)
    int[] lineSets = new int[10];
    boolean isIncomplete = false;
    for (int r = 0; r < 4; r++)
    {
      for (int c = 0; c < 4; c++)
      {
        char ch = grid[r][c];
        if (ch == '.')
        {
          isIncomplete = true;
        }
        int charBitMask = getBitMask(ch);
        lineSets[r] |= charBitMask;
        lineSets[(c + 4)] |= charBitMask;
        if (c == r)
        {
          lineSets[8] |= charBitMask;
        }
        else if (c == 3 - r)
        {
          lineSets[9] |= charBitMask;
        }
      }
    }
    for (int lineSet : lineSets)
    {
      if (lineSet == 1 || lineSet == 5)
      {
        return "X won";
      }
      if (lineSet == 2 || lineSet == 6)
      {
        return "O won";
      }
    }
    return isIncomplete ? "Game has not completed" : "Draw";
  }

  private static int getBitMask(char c)
  {
    switch (c)
    {
      case 'X':
        return 1;
      case 'O':
        return 2;
      case 'T':
        return 4;
      case '.':
        return 8;
      default:
        throw new IllegalArgumentException("Unknown char '" + c + "'");
    }
  }
}
