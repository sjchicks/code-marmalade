package org.hicksst.codejam.lost;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

public class Lost
{
  /**
   * Bigger than any possible distance, but not so big that we get integer overflow.
   */
  private static final int INFINITY = Integer.MAX_VALUE - 2000000;//TODO: may need correction?

  private static final int SOURCE_CITY = 1;
  private static final int DEST_CITY = 2;

  private static final Comparator<Vertex> ORDER_BY_DISTANCE = new Comparator<Vertex>()
  {
    @Override
    public int compare(Vertex o1, Vertex o2)
    {
      return Integer.compare(o1.minDistance, o2.minDistance);
    }
  };

  public static void main(String[] args) throws IOException
  {
    Main main = new Main();
    main.doMain("lost-large");
//    main.doMain("lost-small");
//    main.doMain("lost-test");
  }

  private static String solve(int numCities, int[] start, int[] end, int[] min, int[] max, int[] path)
  {
    System.out.println(numCities + "\n path: " + Arrays.toString(path));

    // Find the shortest path: sum(min) and sum(max)
    Vertex[] cities = new Vertex[numCities + 1];
    Set<Vertex> s = new HashSet<>();
    PriorityQueue<Vertex> q = new PriorityQueue<>(numCities, ORDER_BY_DISTANCE);
    for (int i = 1; i <= numCities; i++)
    {
      Vertex vertex = new Vertex(i);
      if (i != SOURCE_CITY)
      {
        vertex.minDistance = INFINITY;
      }
      q.add(vertex);
      cities[i] = vertex;
    }
    int numRoutes = start.length;
    for (int i = 0; i < numRoutes; i++)
    {
      Vertex vertex = cities[start[i]];
      int routeNum = i + 1;
      Route route = new Route(routeNum, cities[end[i]], min[i], max[i]);
      System.out.println(start[i] + " --> " + route);
      vertex.adj.put(routeNum, route);
    }

    while (!q.isEmpty())
    {
      Vertex source = q.remove();
      s.add(source);
      for (Route route : source.adj.values())
      {
        Vertex dest = route.dest;
        if (dest.minDistance > source.minDistance + route.min)
        {
          dest.minDistance = source.minDistance + route.min;
          dest.maxDistance = source.maxDistance + route.max;
          dest.previous = source;
          q.remove(dest);
          q.add(dest);
        }
      }
    }

    Vertex destCity = cities[DEST_CITY];
    System.out.println("Destination: " + destCity);

    Set<Vertex> citiesInShortestPath = new LinkedHashSet<>();
    Vertex current = destCity.previous;
    while (current.num != SOURCE_CITY)
    {
      citiesInShortestPath.add(current);
      current = current.previous;
    }

    RecursiveSolver solver = new RecursiveSolver(new int[numRoutes], new boolean[numRoutes + 1], path, destCity.maxDistance, citiesInShortestPath);
    try
    {
      boolean result = solver.findShortestPaths(cities[SOURCE_CITY], 0, 0, true);
      System.out.println("Result: " + result);
    }
    catch (SuccessException ignored)
    {
    }
    if (solver.lastFailedSuggestedRoute != null)
    {
      return String.valueOf(solver.lastFailedSuggestedRoute);
    }
    else
    {
      return "Looks Good To Me";
    }
  }

  private static class RecursiveSolver
  {
    private final int[] path;
    private final boolean[] routesUsed;
    private final int[] suggestedPath;
    private final int maxDistance;
    private final Set<Vertex> citiesInShortestPath;

    private Integer lastFailedSuggestedRoute;

    private RecursiveSolver(int[] path, boolean[] routesUsed, int[] suggestedPath, int maxDistance, Set<Vertex> citiesInShortestPath)
    {
      this.path = path;
      this.routesUsed = routesUsed;
      this.suggestedPath = suggestedPath;
      this.maxDistance = maxDistance;
      this.citiesInShortestPath = citiesInShortestPath;
    }

    private boolean findShortestPaths(Vertex city, int currentMin, int nextRouteIndex, boolean inSuggestedPath) throws SuccessException
    {
      if (currentMin > maxDistance)
      {
        return false;
      }

      // If we get to a city in the shortest path, from there on the length is the same, so we
      // have to check that we are in bounds now
      if (citiesInShortestPath.contains(city) && currentMin > city.maxDistance)
      {
        return false;
      }

      if (city.num == DEST_CITY)
      {
        if (!inSuggestedPath || nextRouteIndex == suggestedPath.length)
        {
          System.out.println("Found it: " + Lost.toString(path, nextRouteIndex));
          return true;
        }
        else
        {
          lastFailedSuggestedRoute = suggestedPath[nextRouteIndex];
          throw new SuccessException();
        }
      }

      int nextRouteInSuggestedPath = -100;
      if (nextRouteIndex < suggestedPath.length)
      {
        // Try this first
        nextRouteInSuggestedPath = suggestedPath[nextRouteIndex];

        Route suggestedRoute = city.adj.get(nextRouteInSuggestedPath);
        if (suggestedRoute != null)
        {
          boolean result = tryThisRoute(currentMin, nextRouteIndex, suggestedRoute, inSuggestedPath);
          if (result)
          {
            // One we've just tried worked, but didn't find any success further up the chain of suggested paths
            throw new SuccessException();
          }
          else
          {
            lastFailedSuggestedRoute = nextRouteInSuggestedPath;
          }
        }
      }

      for (Route route : city.adj.values())
      {
        if (route.num != nextRouteInSuggestedPath) // Don't do suggested route twice
        {
          boolean result = tryThisRoute(currentMin, nextRouteIndex, route, false);
          if (result)
          {
            return true;
          }
        }
      }
      return false;
    }

    private boolean tryThisRoute(int currentMin, int nextRouteIndex, Route route, boolean inSuggestedPath) throws SuccessException
    {
      boolean result = false;
      int routeNum = route.num;
      if (!routesUsed[routeNum])
      {
        routesUsed[routeNum] = true;
        path[nextRouteIndex] = routeNum;
        result = findShortestPaths(route.dest, currentMin + route.min, nextRouteIndex + 1, inSuggestedPath);
        routesUsed[routeNum] = false;
      }
      return result;
    }

  }

  private static class SuccessException extends Throwable
  {
  }

  private static class Main extends CodeJamMain
  {
    @Override
    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
    {
      System.out.println("Case " + t + "...");
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
//      if (t != 4)
//      {
//        return "Ignored";
//      }
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