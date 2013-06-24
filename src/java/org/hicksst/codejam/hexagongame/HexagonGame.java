package org.hicksst.codejam.hexagongame;

import java.io.IOException;
import java.util.Arrays;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

public class HexagonGame
{
  private final int size;
  private final int[] startingPositions;
  private final int[] values;
  private final int totalSpaces;
  private final Hex[] hexes;
  // 0 = horizontal, 1 = SW diag, 2 = SE diag
  private final int[][] endpointPositions;

  public HexagonGame(int[] startingPositions, int[] values)
  {
    if (values.length != startingPositions.length)
    {
      throw new IllegalArgumentException(Arrays.toString(values) + " != " + Arrays.toString(startingPositions));
    }
    size = values.length;
    if (size % 2 != 1)
    {
      throw new IllegalAccessError(size + " is not odd");
    }
    System.out.println("Starting:" + Arrays.toString(startingPositions));
    System.out.println("Values: " + Arrays.toString(values));
    this.startingPositions = startingPositions;
    this.values = values;
    totalSpaces = size + (size - 1) * (3 * size - 1) / 4;
    hexes = new Hex[totalSpaces + 1];
    endpointPositions = new int[3][size];
  }

  private int solve()
  {
    System.out.println("size: " + size + ", total spaces: " + totalSpaces);
    populateHexes();

    int minSum = Integer.MAX_VALUE;
    for (int[] endpointPosition : endpointPositions)
    {
      System.out.println("Endpoint: " + Arrays.toString(endpointPosition));
      int[][] weightMatrix = createWeightMatrix(endpointPosition);

      HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(weightMatrix);
      int[] assignment = hungarianAlgorithm.execute();
      int sum = calcSum(weightMatrix, assignment);
      System.out.println("Result: " + Arrays.toString(assignment) + ", total weight: " + sum);

      minSum = Math.min(minSum, sum);
    }

    return minSum;
  }

  private int calcSum(int[][] weightMatrix, int[] assignment)
  {
    int sum = 0;
    for (int i = 0; i < assignment.length; i++)
    {
      sum += weightMatrix[i][assignment[i]];
    }
    return sum;
  }

  private int[][] createWeightMatrix(int[] endpointPosition)
  {
    int[][] matrix = new int[size][size];
    for (int s = 0; s < size; s++)
    {
      int startNum = startingPositions[s];
      int value = values[s];

      for (int e = 0; e < size; e++)
      {
        int endNum = endpointPosition[e];
        matrix[s][e] = hexes[startNum].distanceTo(hexes[endNum]) * value;
      }
    }
    return matrix;
  }

  private void populateHexes()
  {
    int middle = (size + 1) / 2;
    int currentRowLengthDelta = +1;
    int currentPosInRow = 1;
    int currentRowLength = (size + 1) / 2;
    int currentRow = 1;
    int currentDiagonalOffset = 0;
    for (int i = 1; i <= totalSpaces; i++)
    {
      int southWestDiagonal = currentPosInRow + currentDiagonalOffset;
      int southEastDiagonal = (currentRow + middle) - southWestDiagonal;
      hexes[i] = new Hex(i, currentRow, southWestDiagonal);
      if (currentRow == middle)
      {
        endpointPositions[0][southWestDiagonal - 1] = i;
      }
      if (southWestDiagonal == middle)
      {
        endpointPositions[1][southEastDiagonal - 1] = i;
      }
      if (southEastDiagonal == middle)
      {
        endpointPositions[2][southWestDiagonal - 1] = i;
      }
      currentPosInRow++;
      if (currentPosInRow > currentRowLength)
      {
        currentRow++;
        if (currentRowLength == size)
        {
          currentRowLengthDelta = -1;
        }
        if (currentRowLengthDelta == -1)
        {
          currentDiagonalOffset++;
        }
        currentRowLength += currentRowLengthDelta;
        currentPosInRow = 1;
      }
    }
  }

  private class Hex
  {
    private int value;
    private final int row;
    private final int diagonal;

    public Hex(int value, int row, int diagonal)
    {
      this.value = value;
      this.row = row;
      this.diagonal = diagonal;
    }

    public int distanceTo(Hex other)
    {
      int rowDiff = other.row - this.row;
      int diagonalDiff = other.diagonal - this.diagonal;
      if (rowDiff < 0 && diagonalDiff > 0 || rowDiff > 0 && diagonalDiff < 0)
      {
        return Math.abs(rowDiff) + Math.abs(diagonalDiff);
      }
      else
      {
        return Math.max(Math.abs(rowDiff), Math.abs(diagonalDiff));
      }
    }

    @Override
    public String toString()
    {
      return value + "[" + row + "," + diagonal + "]";
    }
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      int[] startingPositions = reader.readInts();
      int[] values = reader.readInts();
      int result = new HexagonGame(startingPositions, values).solve();
      return String.valueOf(result);
    }
  }

  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
//    main.doMain("hexagongame-test");
//    main.doMain("hexagongame-small");
    main.doMain("hexagongame-large");
  }
}
