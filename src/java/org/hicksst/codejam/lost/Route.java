package org.hicksst.codejam.lost;

class Route
{
  final int num;
  final Vertex dest;
  final int min;
  final int max;

  Route(int num, Vertex dest, int min, int max)
  {
    this.num = num;
    this.dest = dest;
    this.min = min;
    this.max = max;
  }

  @Override
  public String toString()
  {
    return "Route{" +
        "num=" + num +
        ", dest=" + dest.num +
        ", min=" + min +
        ", max=" + max +
        '}';
  }
}
