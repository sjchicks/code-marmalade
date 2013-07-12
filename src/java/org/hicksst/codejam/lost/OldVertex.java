package org.hicksst.codejam.lost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OldVertex
{
  final int num;
  final Map<Integer, Route> adj = new HashMap<>();
  final Map<Integer, Route> routesByDestCity = new HashMap<>();
  final List<Previous> previousInShortestPath = new ArrayList<>();
  int minDistance;
  int maxDistance;

  OldVertex(int num)
  {
    this.num = num;
  }

  @Override
  public String toString()
  {
    return "Vertex{" +
        "num=" + num +
        ", adj=" + adj +
        ", previous=" + previousInShortestPath +
        ", minDistance=" + minDistance +
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

    OldVertex vertex = (OldVertex) o;

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
