package org.hicksst.codejam.lost;

import java.util.HashMap;
import java.util.Map;

class Vertex
{
  final int num;
  final Map<Integer, Route> routesAway = new HashMap<>();
  int maxDistance;

  Vertex(int num)
  {
    this.num = num;
  }

  @Override
  public String toString()
  {
    return "Vertex{" +
        "num=" + num +
        ", routesAway=" + routesAway +
        ", maxDistance=" + maxDistance +
        '}';
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

    Vertex vertex = (Vertex) o;

    if (num != vertex.num)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return num;
  }
}
