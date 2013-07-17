package org.hicksst.codejam.ruralplanning;

public interface ProgressCallback
{
  void setRemainingPoint(Point point);

  void fenceUpdated(PointList fence);

  void setProblemBounds(int minX, int minY, int maxX, int maxY);

  void resultComplete(String result);
}
