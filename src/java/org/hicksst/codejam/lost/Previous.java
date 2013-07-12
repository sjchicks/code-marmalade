package org.hicksst.codejam.lost;

/**
 * @author Steve Hicks
 */
class Previous
{
  final Vertex city;
  final int maxDistance;
  final Route route;

  Previous(Vertex city, int maxDistance, Route route)
  {
    this.city = city;
    this.maxDistance = maxDistance;
    this.route = route;
  }

  @Override
  public String toString()
  {
    return "Previous{" +
        "city=" + city.num +
        ", maxDistance=" + maxDistance +
        ", route=" + route +
        '}';
  }
}
