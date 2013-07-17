package org.hicksst.codejam.ruralplanning;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PointList implements Iterable<Point>
{
  Point first;
  Point last;

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    Point p = first;
    while (p != null)
    {
      if (sb.length() != 0)
      {
        sb.append(' ');
      }
      sb.append(p.i);
      p = p.next;
      if (p == first)
      {
        break;
      }
    }
    return sb.toString();
  }

  public void addAll(Collection<Point> points)
  {
    for (Point point : points)
    {
      add(point);
    }
  }


  public void add(Point point)
  {
    if (first == null)
    {
      first = last = point;
      point.next = point;
    }
    else
    {
      insertAfter(point, last);
    }
  }

  public void insertAfter(Point toInsert, Point pointBefore)
  {
    toInsert.next = pointBefore.next;
    pointBefore.next = toInsert;
    if (toInsert.next == first)
    {
      last = toInsert;
    }
  }

  @Override
  public Iterator<Point> iterator()
  {
    return new PointIterator();
  }

  private class PointIterator implements Iterator<Point>
  {
    private Point current;

    @Override
    public boolean hasNext()
    {
      if (current == null)
      {
        return first != null;
      }
      else
      {
        return current != first;
      }
    }

    @Override
    public Point next()
    {
      if (!hasNext())
      {
        throw new NoSuchElementException();
      }
      if (current == null)
      {
        current = first;
      }
      Point next = current;
      current = current.next;
      return next;
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}
