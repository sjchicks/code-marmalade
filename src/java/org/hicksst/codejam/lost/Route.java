package org.hicksst.codejam.lost;

class Route
{
  final int num;
  final Vertex startCity;
  final Vertex endCity;
  final int min;
  final int max;

  Route(int num, Vertex startCity, Vertex endCity, int min, int max)
  {
    this.num = num;
    this.startCity = startCity;
    this.endCity = endCity;
    this.min = min;
    this.max = max;
  }

  @Override
  public String toString()
  {
    return "Route{" +
        "num=" + num +
        ", start=" + startCity.num +
        ", end=" + endCity.num +
        ", min=" + min +
        ", max=" + max +
        '}';
  }
}
