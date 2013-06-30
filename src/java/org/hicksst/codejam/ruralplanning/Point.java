package org.hicksst.codejam.ruralplanning;

class Point
{
  private final int i;
  private final int x;
  private final int y;

  Point(int i, int x, int y)
  {
    this.i = i;
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString()
  {
    return i + ": (" + x + ", " + y + ")";
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

    Point point = (Point) o;

    return i == point.i;
  }

  @Override
  public int hashCode()
  {
    return i;
  }
}