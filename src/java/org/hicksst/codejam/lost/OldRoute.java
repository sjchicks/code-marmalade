package org.hicksst.codejam.lost;

class OldRoute
{
  final int num;
  final OldVertex dest;
  final int min;
  final int max;

  OldRoute(int num, OldVertex dest, int min, int max)
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
