package org.hicksst.codejam.lost;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

public class LostAgain
{
  /**
   * Bigger than any possible distance, but not so big that we get integer overflow.
   */
  private static final int INFINITY = Integer.MAX_VALUE - 2000000;//TODO: may need correction?

  private static final int SOURCE_CITY = 1;
  private static final int DEST_CITY = 2;

  private static final Comparator<Vertex> ORDER_BY_MAX_DISTANCE = new Comparator<Vertex>()
  {
    @Override
    public int compare(Vertex o1, Vertex o2)
    {
      return Integer.compare(o1.maxDistance, o2.maxDistance);
    }
  };

  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
//    main.doMain("lost-large");
//    main.doMain("lost-small");
    main.doMain("lost-test");
  }

  private static String solve(int numCities, int[] start, int[] end, int[] min, int[] max, int[] path)
  {
    int numRoutes = start.length;
    System.out.println("Cities: " + numCities);
    System.out.println("Routes: " + numRoutes);
    System.out.println(" path: " + Arrays.toString(path));

    // Find the minimum total 'max' for each path from the source.
    Vertex[] cities = new Vertex[numCities + 1];
    PriorityQueue<Vertex> q = new PriorityQueue<>(numCities, ORDER_BY_MAX_DISTANCE);
    for (int i = 1; i <= numCities; i++)
    {
      Vertex vertex = new Vertex(i);
      if (i != SOURCE_CITY)
      {
        vertex.minDistance = INFINITY;
        vertex.maxDistance = INFINITY;
      }
      q.add(vertex);
      cities[i] = vertex;
    }
    for (int i = 0; i < numRoutes; i++)
    {
      Vertex vertex = cities[start[i]];
      int routeNum = i + 1;
      Route route = new Route(routeNum, cities[end[i]], min[i], max[i]);
      System.out.println(route.num + ": " + start[i] + " --> " + route.dest.num + " (" + route.min + "-" + route.max + ")");
      vertex.adj.put(routeNum, route);
    }

    while (!q.isEmpty())
    {
      Vertex source = q.remove();
      for (Route route : source.adj.values())
      {
        Vertex dest = route.dest;
        if (dest.maxDistance > source.maxDistance + route.max)
        {
          dest.minDistance = source.minDistance + route.min;
          dest.maxDistance = source.maxDistance + route.max;
          q.remove(dest);
          q.add(dest);
        }
      }
    }

    Vertex destCity = cities[DEST_CITY];
    System.out.println("Destination: " + destCity);

    Vertex[] citiesInPath = new Vertex[path.length + 1];
    Vertex city = cities[SOURCE_CITY];
    int i = 0;
    while (i < citiesInPath.length)
    {
      citiesInPath[i] = city;
      if (i >= path.length)
      {
        break;
      }
      city = city.adj.get(path[i]).dest;
      i++;
    }

    System.out.println("Cities in path: ");
    for (Vertex cityInPath : citiesInPath)
    {
      System.out.println(cityInPath);
    }

    for (int startIndex = 0; startIndex < citiesInPath.length - 1; startIndex++)
    {
      Vertex startCity = citiesInPath[startIndex];
      for (int endIndex = startIndex + 1; endIndex < citiesInPath.length; endIndex++)
      {
        Vertex endCity = citiesInPath[endIndex];
        int minMaxDistance = endCity.maxDistance - startCity.maxDistance;
        System.out.println(startCity.num + " --> " + endCity.num +" : " + minMaxDistance);
      }
    }

    return "Ignore";
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      int[] ints = reader.readInts();
      int numCities = ints[0];
      int numRoutes = ints[1];
      int pathLength = ints[2];
      int[] start = new int[numRoutes];
      int[] end = new int[numRoutes];
      int[] min = new int[numRoutes];
      int[] max = new int[numRoutes];
      for (int i = 0; i < numRoutes; i++)
      {
        ints = reader.readInts();
        start[i] = ints[0];
        end[i] = ints[1];
        min[i] = ints[2];
        max[i] = ints[3];
      }
      int[] path = reader.readInts();
      assert path.length == pathLength;
//      if (t != 3)
//      {
//        return "Ignored";
//      }
      System.out.println("Case " + t + "...");
      System.out.println("===============");
      String result = solve(numCities, start, end, min, max, path);
      System.out.println("  result = " + result);
      return result;
    }
  }

  private static String toString(int[] a, int endIndex)
  {
    if (a == null)
    {
      return "null";
    }
    int iMax = endIndex - 1;
    if (iMax == -1)
    {
      return "[]";
    }

    StringBuilder b = new StringBuilder();
    b.append('[');
    for (int i = 0; ; i++)
    {
      b.append(a[i]);
      if (i == iMax)
      {
        return b.append(']').toString();
      }
      b.append(", ");
    }
  }
}