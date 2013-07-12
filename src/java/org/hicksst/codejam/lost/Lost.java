package org.hicksst.codejam.lost;

public class Lost
{
//  /**
//   * Bigger than any possible distance, but not so big that we get integer overflow.
//   */
//  private static final int INFINITY = Integer.MAX_VALUE - 2000000;//TODO: may need correction?
//
//  private static final int SOURCE_CITY = 1;
//  private static final int DEST_CITY = 2;
//
//  private static final Comparator<Vertex> ORDER_BY_MIN_DISTANCE = new Comparator<Vertex>()
//  {
//    @Override
//    public int compare(Vertex o1, Vertex o2)
//    {
//      return Integer.compare(o1.minDistance, o2.minDistance);
//    }
//  };
//
//  public static void main(String[] args) throws IOException
//  {
//    Main main = new Main();
//    main.doMain("lost-large");
////    main.doMain("lost-small");
////    main.doMain("lost-test");
//  }
//
//  private static String solve(int numCities, int[] start, int[] end, int[] min, int[] max, int[] path)
//  {
//    int numRoutes = start.length;
//    System.out.println("Cities: " + numCities);
//    System.out.println("Routes: " + numRoutes);
//    System.out.println(" path: " + Arrays.toString(path));
//
//    // Find the shortest path: sum(min) and sum(max)
//    Vertex[] cities = new Vertex[numCities + 1];
//    PriorityQueue<Vertex> q = new PriorityQueue<>(numCities, ORDER_BY_MIN_DISTANCE);
//    for (int i = 1; i <= numCities; i++)
//    {
//      Vertex vertex = new Vertex(i);
//      if (i != SOURCE_CITY)
//      {
//        vertex.minDistance = INFINITY;
//      }
//      q.add(vertex);
//      cities[i] = vertex;
//    }
//    for (int i = 0; i < numRoutes; i++)
//    {
//      Vertex vertex = cities[start[i]];
//      int routeNum = i + 1;
//      Route route = new Route(routeNum, cities[end[i]], min[i], max[i]);
//      System.out.println(route.num + ": " + start[i] + " --> " + route.dest.num + " (" + route.min + "-" + route.max + ")");
//      vertex.adj.put(routeNum, route);
//      Route existing = vertex.routesByDestCity.put(route.dest.num, route);
//      if (existing != null)
//      {
//        System.out.println("******** TADA::: " + existing + " and " + route);
//      }
//    }
//
//    while (!q.isEmpty())
//    {
//      Vertex source = q.remove();
//      for (Route route : source.adj.values())
//      {
//        Vertex dest = route.dest;
//        if (dest.minDistance > source.minDistance + route.min)
//        {
//          dest.minDistance = source.minDistance + route.min;
//          dest.maxDistance = source.maxDistance + route.max;
//          dest.previousInShortestPath.clear();
//          dest.previousInShortestPath.add(new Previous(source, source.maxDistance + route.max, route));
//          q.remove(dest);
//          q.add(dest);
//        }
//        else if (dest.minDistance == source.minDistance + route.min)
//        {
//          dest.previousInShortestPath.add(new Previous(source, source.maxDistance + route.max, route));
//        }
//      }
//    }
//
//    Vertex destCity = cities[DEST_CITY];
//    System.out.println("Destination: " + destCity);
//
//    List<Set<Integer>> shortestRoutes = new ArrayList<>();
//    findShortestPaths(destCity, new LinkedHashSet<Integer>(), shortestRoutes, -1);
//    if (shortestRoutes.size() != 1)
//    {
//      throw new RuntimeException("Nooooo - can't cope with multiple shortest paths yet.");
//    }
//    System.out.println(shortestRoutes.size() + " shortest routes: " + shortestRoutes);
//
//    RecursiveSolver solver = new RecursiveSolver(cities, new int[numRoutes], new boolean[numRoutes + 1], new int[numCities + 1], path, destCity.maxDistance, shortestRoutes.iterator().next());
//    try
//    {
//      boolean result = solver.isPotentialShortestPath(cities[SOURCE_CITY], 0, 0, true);
//      System.out.println("Result: " + result);
//    }
//    catch (SuccessException ignored)
//    {
//    }
//    if (solver.lastFailedSuggestedRoute != null)
//    {
//      return String.valueOf(solver.lastFailedSuggestedRoute);
//    }
//    else
//    {
//      return "Looks Good To Me";
//    }
//  }
//
//  private static void findShortestPaths(Vertex city, Set<Integer> routes, List<Set<Integer>> shortestRoutes, int maxDistance)
//  {
//    if (city.num == SOURCE_CITY)
//    {
//      System.out.println("Found route: " + routes + " max dist = " + maxDistance);
//      shortestRoutes.add(new LinkedHashSet<>(routes));
//      return;
//    }
//    for (Previous previous : city.previousInShortestPath)
//    {
//      routes.add(previous.route.num);
//      int maxDistToUse;
//      if (city.num == DEST_CITY)
//      {
//        assert maxDistance == -1;
//        maxDistToUse = previous.maxDistance;
//      }
//      else
//      {
//        assert maxDistance != -1;
//        maxDistToUse = maxDistance;
//      }
//      findShortestPaths(previous.city, routes, shortestRoutes, maxDistToUse);
//      routes.remove(previous.route.num);
//    }
//  }
//
//  private static class RecursiveSolver
//  {
//    private final Vertex[] cities;
//    private final int[] path;
//    private final boolean[] routesUsed;
//    private final int[] cityVisitCount;
//    private final int[] suggestedPath;
//    private final int maxDistance;
//    private final Set<Integer> routesInShortestPath;
//
//    private Integer lastFailedSuggestedRoute;
//
//    private RecursiveSolver(Vertex[] cities, int[] path, boolean[] routesUsed, int[] cityVisitCount, int[] suggestedPath, int maxDistance, Set<Integer> routesInShortestPath)
//    {
//      this.cities = cities;
//      this.path = path;
//      this.routesUsed = routesUsed;
//      this.cityVisitCount = cityVisitCount;
//      this.suggestedPath = suggestedPath;
//      this.maxDistance = maxDistance;
//      this.routesInShortestPath = routesInShortestPath;
//    }
//
//    private boolean isPotentialShortestPath(Vertex city, int currentMin, int nextRouteIndex, boolean inSuggestedPath) throws SuccessException
//    {
//      if (currentMin > maxDistance)
//      {
//        return false;
//      }
//
//      if (city.num == DEST_CITY)
//      {
//        if (!inSuggestedPath || nextRouteIndex == suggestedPath.length)
//        {
//          System.out.println("Found it: " + Lost.toString(path, nextRouteIndex));
//          showPathDetails(path, nextRouteIndex);
//          return true;
//        }
//        else
//        {
//          lastFailedSuggestedRoute = suggestedPath[nextRouteIndex];
//          throw new SuccessException();
//        }
//      }
//
//      if (inSuggestedPath && cityVisitCount[city.num] > 1)
//      {
//        System.out.println("Visiting count# " + cityVisitCount[city.num] + ": " + city);
//        return false;
//      }
//
//      int nextRouteInSuggestedPath = -100;
//      if (inSuggestedPath && nextRouteIndex < suggestedPath.length)
//      {
//        // Try this first
//        nextRouteInSuggestedPath = suggestedPath[nextRouteIndex];
//
//        Route suggestedRoute = city.adj.get(nextRouteInSuggestedPath);
//        if (suggestedRoute != null)
//        {
//          boolean result = tryThisRoute(currentMin, nextRouteIndex, suggestedRoute, inSuggestedPath);
//          if (result)
//          {
//            // One we've just tried worked, but didn't find any success further up the chain of suggested paths
//            throw new SuccessException();
//          }
//          else
//          {
//            lastFailedSuggestedRoute = nextRouteInSuggestedPath;
//          }
//        }
//      }
//
//      for (Route route : city.adj.values())
//      {
//        if (route.num != nextRouteInSuggestedPath) // Don't do suggested route twice
//        {
//          boolean result = tryThisRoute(currentMin, nextRouteIndex, route, false);
//          if (result)
//          {
//            return true;
//          }
//        }
//      }
//      return false;
//    }
//
//    private void showPathDetails(int[] path, int nextRouteIndex)
//    {
//      Vertex city = cities[SOURCE_CITY];
//      int totalMin = 0;
//      int totalMax = 0;
//      for (int i = 0; i < nextRouteIndex; i++)
//      {
//        int routeNum = path[i];
//        if (routeNum == 0)
//        {
//          break;
//        }
//        Route route = city.adj.get(routeNum);
//        totalMin += route.min;
//        totalMax += route.max;
//        city = route.dest;
//      }
//      System.out.println("Total min: " + totalMin + ", max: " + totalMax);
//    }
//
//    private boolean tryThisRoute(int currentMin, int nextRouteIndex, Route route, boolean inSuggestedPath) throws SuccessException
//    {
//      boolean result = false;
//      int routeNum = route.num;
//      if (!routesUsed[routeNum])
//      {
//        routesUsed[routeNum] = true;
//        path[nextRouteIndex] = routeNum;
//        cityVisitCount[route.dest.num]++;
//        int newCurrentMinDistance;
//        if (routesInShortestPath.contains(routeNum))
//        {
//          // What we're actually doing is removing this route from our calculations as it must be the same our suggested path and the shortest path.
//          // This means not incrementing our currentMin by route's min and decrementing the shortestMax by the route's max.
//          // Instead, I just increment currentMin by route's max - this has the same effect.
//          newCurrentMinDistance = currentMin + route.max;
//        }
//        else
//        {
//          newCurrentMinDistance = currentMin + route.min;
//        }
//        result = isPotentialShortestPath(route.dest, newCurrentMinDistance, nextRouteIndex + 1, inSuggestedPath);
//        cityVisitCount[route.dest.num]++;
//        routesUsed[routeNum] = false;
//      }
//      return result;
//    }
//
//  }
//
//  private static class SuccessException extends Throwable
//  {
//  }
//
//  private static class Main extends CodeJamMain
//  {
//    @Override
//    protected String parseAndSolve(int t, CodeJamReader reader) throws IOException
//    {
//      int[] ints = reader.readInts();
//      int numCities = ints[0];
//      int numRoutes = ints[1];
//      int pathLength = ints[2];
//      int[] start = new int[numRoutes];
//      int[] end = new int[numRoutes];
//      int[] min = new int[numRoutes];
//      int[] max = new int[numRoutes];
//      for (int i = 0; i < numRoutes; i++)
//      {
//        ints = reader.readInts();
//        start[i] = ints[0];
//        end[i] = ints[1];
//        min[i] = ints[2];
//        max[i] = ints[3];
//      }
//      int[] path = reader.readInts();
//      assert path.length == pathLength;
//      if (t != 3)
//      {
//        return "Ignored";
//      }
//      System.out.println("Case " + t + "...");
//      System.out.println("===============");
//      String result = solve(numCities, start, end, min, max, path);
//      System.out.println("  result = " + result);
//      return result;
//    }
//  }
//
//  private static String toString(int[] a, int endIndex)
//  {
//    if (a == null)
//    {
//      return "null";
//    }
//    int iMax = endIndex - 1;
//    if (iMax == -1)
//    {
//      return "[]";
//    }
//
//    StringBuilder b = new StringBuilder();
//    b.append('[');
//    for (int i = 0; ; i++)
//    {
//      b.append(a[i]);
//      if (i == iMax)
//      {
//        return b.append(']').toString();
//      }
//      b.append(", ");
//    }
//  }
}