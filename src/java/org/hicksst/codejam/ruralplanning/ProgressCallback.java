package org.hicksst.codejam.ruralplanning;

import java.util.Set;

public interface ProgressCallback
{
  void setRemainingPoints(Set<Point> points);

  void fenceUpdated(PointList fence);

  void setBounds(int minX, int minY, int maxX, int maxY);
}
