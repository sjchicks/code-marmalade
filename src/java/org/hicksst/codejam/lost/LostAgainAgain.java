package org.hicksst.codejam.lost;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.hicksst.codejam.util.CodeJamMain;
import org.hicksst.codejam.util.CodeJamReader;

public class LostAgainAgain
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
    main.doMain("lost-large");
//    main.doMain("lost-small");
//    main.doMain("lost-test");
  }

  private static String solve(int numCities, int[] start, int[] end, int[] min, int[] max, int[] path)
  {
    int numRoutes = start.length;
    System.out.println("Cities: " + numCities);
    System.out.println("Routes: " + numRoutes);

    // Create the city objects
    Vertex[] cities = new Vertex[numCities + 1];
    for (int i = 1; i <= numCities; i++)
    {
      Vertex vertex = new Vertex(i);
      cities[i] = vertex;
    }
    // Add the routes to each city
    for (int i = 0; i < numRoutes; i++)
    {
      Vertex startCity = cities[start[i]];
      Vertex endCity = cities[end[i]];
      int routeNum = i + 1;
      Route route = new Route(routeNum, startCity, endCity, min[i], max[i]);
      startCity.routesAway.put(routeNum, route);
    }

    // Work out which cities we visit by following the suggested path
    Vertex[] citiesInPath = new Vertex[path.length + 1];
    int minDistanceRunningTotal = 0;
    int[] totalMinDistanceFromSource = new int[path.length + 1];
    Vertex city = cities[SOURCE_CITY];
    int i = 0;
    while (i < citiesInPath.length)
    {
      citiesInPath[i] = city;
      totalMinDistanceFromSource[i] = minDistanceRunningTotal;
      if (i >= path.length)
      {
        break;
      }
      Route route = city.routesAway.get(path[i]);
      minDistanceRunningTotal += route.min;
      city = route.endCity;
      i++;
    }

    // Calculate the matrix of shortest maxDistances by repeatedly applying Dijkstra's algorithm
    int[][] shortestMaxDistance = new int[numCities + 1][numCities + 1];
    for (int sourceCityNum = 1; sourceCityNum <= numCities; sourceCityNum++)
    {
      setShortestMaxPathsFromSource(sourceCityNum, numCities, cities);
      for (int destCityNum = 1; destCityNum <= numCities; destCityNum++)
      {
        shortestMaxDistance[sourceCityNum][destCityNum] = cities[destCityNum].maxDistance;
      }
    }

    // Check every 'increasing' pair of points (i.e. the 2nd is after the 1st): is there
    // a path that is guaranteed to be shorter than our suggested path betwen those points?
    // In other words, is the shortest possible maximum distance less than the minimum distance in our path?
    boolean foundBadPath = false;
    for (int startIndex = 0; !foundBadPath && startIndex < citiesInPath.length - 1; startIndex++)
    {
      Vertex startCity = citiesInPath[startIndex];
      for (int endIndex = startIndex + 1; endIndex < citiesInPath.length; endIndex++)
      {
        Vertex endCity = citiesInPath[endIndex];
        int minMaxDistance = shortestMaxDistance[startCity.num][endCity.num];
        int minDistanceForOurPath = totalMinDistanceFromSource[endIndex] - totalMinDistanceFromSource[startIndex];
        // NB: They are on the same path, so we can just subtract to get the total min distance between startIndex and endIndex.
        if (minDistanceForOurPath > minMaxDistance)
        {
          // There is a path which must be shorter than ours (not just "could be")
          int index = endIndex - 1; // the first city to start looking for a successful shortest path is the one before we failed.
          int firstRouteNotPartOfShortestPath = findFirstRouteNotPartOfShortestPath(index, shortestMaxDistance, totalMinDistanceFromSource,
                                                                                    citiesInPath, numCities, path);
          return String.valueOf(firstRouteNotPartOfShortestPath);
        }
      }
    }

    return "Looks Good To Me";
  }

  /**
   * Works back from the city at 'index' in the suggested path (citiesInPath) to find a shortest path.
   *
   * @param index                      The index of the first city to try in citiesInPath
   * @param shortestMaxDistance        Matrix of shortest max distances between pairs of cities.
   * @param totalMinDistanceFromSource The cumulative sum of min distances from the source along the suggested path
   * @param citiesInPath               Cities in the suggested path.
   * @param numCities                  Total number of cities.
   * @param path                       The suggest path, given as route numbers.
   *
   * @return The number of the first route in the suggest path that could not form part of a shortest path.
   */
  private static int findFirstRouteNotPartOfShortestPath(int index, int[][] shortestMaxDistance,
                                                         int[] totalMinDistanceFromSource, Vertex[] citiesInPath, int numCities, int[] path)
  {
    while (index >= 0)
    {
      // Make working copies of the cities in the path and their min distances from the source city.
      Vertex[] currentCitiesInPath = new Vertex[numCities];
      System.arraycopy(citiesInPath, 0, currentCitiesInPath, 0, index + 1);
      int[] currentMinDistFromSource = new int[numCities];
      System.arraycopy(totalMinDistanceFromSource, 0, currentMinDistFromSource, 0, index + 1);
      if (hasShortestPath(currentCitiesInPath, currentMinDistFromSource, index, shortestMaxDistance))
      {
        // If there is a shortest path from this city, the route that caused it to fail is the one leaving this city.
        return path[index];
      }
      index--;
    }
    throw new AssertionError("No path found - should never happen!");
  }

  /**
   * Determines recursively if a path starting at city with index 'index' in currentCitiesInPath could be a shortest path.
   *
   * @param currentCitiesInPath       The list of cities in the current path.
   * @param currentMinDistFromSource  The total min distance from the source for cities in the current path.
   * @param index                     The index of the current city in currentCitiesInPath.
   * @param shortestMaxDistance       Matrix of shortest max distances between pairs of cities.
   *
   * @return true if the destination city (DEST_CITY) is reachable from the 'current' city via a potential shortest path.
   */
  private static boolean hasShortestPath(Vertex[] currentCitiesInPath, int[] currentMinDistFromSource, int index, int[][] shortestMaxDistance)
  {
    Vertex nextCity = currentCitiesInPath[index];

    // Are there any guaranteed shorter paths?
    for (int i = 0; i < index; i++)
    {
      Vertex previousCity = currentCitiesInPath[i];
      int minDistOnOurPath = currentMinDistFromSource[index] - currentMinDistFromSource[i];
      int shortestMaxDist = shortestMaxDistance[previousCity.num][nextCity.num];
      if (shortestMaxDist < minDistOnOurPath)
      {
        return false;
      }
    }

    if (nextCity.num == DEST_CITY)
    {
      return true;
    }

    for (Route route : nextCity.routesAway.values())
    {
      currentCitiesInPath[index + 1] = route.endCity;
      currentMinDistFromSource[index + 1] = currentMinDistFromSource[index] + route.min;
      if (hasShortestPath(currentCitiesInPath, currentMinDistFromSource, index + 1, shortestMaxDistance))
      {
        return true;
      }
    }
    return false;
  }


  /**
   * Uses Dijkstra's algorithm to set city.maxDistance in each city in cities
   * to the shortest 'max' route from sourceCity to that city.
   */
  private static void setShortestMaxPathsFromSource(int sourceCity, int numCities, Vertex[] cities)
  {
    PriorityQueue<Vertex> q = new PriorityQueue<>(numCities, ORDER_BY_MAX_DISTANCE);
    for (int i = 1; i <= numCities; i++)
    {
      Vertex city = cities[i];
      if (city.num == sourceCity)
      {
        city.maxDistance = 0;
      }
      else
      {
        city.maxDistance = INFINITY;
      }
      q.add(city);
    }
    while (!q.isEmpty())
    {
      Vertex source = q.remove();
      for (Route route : source.routesAway.values())
      {
        Vertex dest = route.endCity;
        if (dest.maxDistance > source.maxDistance + route.max)
        {
          dest.maxDistance = source.maxDistance + route.max;
          q.remove(dest);
          q.add(dest);
        }
      }
    }
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
      System.out.println("===============");
      System.out.println("Case " + t + "...");
      System.out.println("===============");
      String result = solve(numCities, start, end, min, max, path);
      System.out.println("  result = " + result);
      return result;
    }
  }

}