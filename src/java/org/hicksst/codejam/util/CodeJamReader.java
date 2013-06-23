package org.hicksst.codejam.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CodeJamReader implements AutoCloseable
{
  private final BufferedReader m_reader;

  public CodeJamReader(String fileName) throws FileNotFoundException
  {
    m_reader = new BufferedReader(new FileReader(fileName));
  }

  public String readLine() throws IOException
  {
    return m_reader.readLine();
  }

  public int readInt() throws IOException
  {
    return Integer.parseInt(m_reader.readLine());
  }

  public String[] readStrings() throws IOException
  {
    return m_reader.readLine().split(" ");
  }

  public String readString() throws IOException
  {
    return m_reader.readLine();
  }

  public int[] readInts() throws IOException
  {
    String[] line = readStrings();
    int[] ints = new int[line.length];
    for (int i = 0; i < line.length; i++)
    {
      ints[i] = Integer.parseInt(line[i]);
    }
    return ints;
  }

  public double[] readDoubles() throws IOException
  {
    String[] line = readStrings();
    double[] doubles = new double[line.length];
    for (int i = 0; i < line.length; i++)
    {
      doubles[i] = Double.parseDouble(line[i]);
    }
    return doubles;
  }

  public long[] readLongs() throws IOException
  {
    String[] line = m_reader.readLine().split(" ");
    long[] ints = new long[line.length];
    for (int i = 0; i < line.length; i++)
    {
      ints[i] = Long.parseLong(line[i]);
    }
    return ints;
  }

  public char[] readChars() throws IOException
  {
    return m_reader.readLine().toCharArray();
  }

  @Override
  public void close() throws IOException
  {
    m_reader.close();
  }
}
