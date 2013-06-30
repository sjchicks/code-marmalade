package org.hicksst.codejam.ruralplanning;


import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

class Surface extends JPanel implements ProgressCallback
{
  private final int border = 25;
  private PointList fence;
  private int minX;
  private int minY;
  private int maxX;
  private int maxY;
  private Point point;

  private void doDrawing(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;

    if (point == null || fence == null)
    {
      return;
    }

    Dimension size = getSize();

    int width = size.width - 2 * border;
    int height = size.height - 2 * border;


    g2d.setColor(Color.lightGray);
    g2d.fillRect(0, 0, size.width, size.height);

    g2d.setColor(Color.darkGray);
    g2d.drawLine(getX(width, 0), 0, getX(width, 0), size.height);
    g2d.drawLine(0, getY(height, 0), size.width, getY(height, 0));

    g2d.setColor(Color.blue);
    for (Point p : fence)
    {
//      g2d.drawString(String.valueOf(p.i), getX(width, p.x), getY(height, p.y));
      drawLine(g2d, width, height, p.x, p.y, p.next.x, p.next.y);
    }

    g2d.setColor(Color.red);

    g2d.drawString(String.valueOf(point.i), getX(width, point.x), getY(height, point.y));
    drawLine(g2d, width, height, point.x, point.y, point.x, point.y);

  }

  private void drawLine(Graphics2D g2d, int width, int height, int px1, int py1, int px2, int py2)
  {
    int x1 = getX(width, px1);
    int y1 = getY(height, py1);
    int x2 = getX(width, px2);
    int y2 = getY(height, py2);
    g2d.drawLine(x1, y1, x2, y2);
  }

  private int getY(int height, int py1)
  {
    return height - ((py1 - minY) * height / (maxY - minY)) + border;
  }

  private int getX(int width, int px1)
  {
    return (px1 - minX) * width / (maxX - minX) + border;
  }

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    doDrawing(g);
  }

  @Override
  public void setRemainingPoint(Point point)
  {
    this.point = point;
  }

  @Override
  public void fenceUpdated(PointList fence)
  {
    this.fence = fence;
  }

  @Override
  public void setProblemBounds(int minX, int minY, int maxX, int maxY)
  {
    this.minX = minX;
    this.minY = minY;
    this.maxX = maxX;
    this.maxY = maxY;
  }

  @Override
  public void resultComplete(String result)
  {
  }
}

public class RuralPlanningGUI extends JFrame implements ProgressCallback
{
  private final Surface surface = new Surface();

  private final Semaphore semaphore = new Semaphore(1);

  public RuralPlanningGUI()
  {
    waitOnButton();
    setTitle("Rural Planning");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    SpringLayout layout = new SpringLayout();
    setLayout(layout);

    add(surface);
    JButton button = new JButton(new MyAbstractAction(semaphore));
    add(button);

    Container contentPane = getContentPane();
    layout.putConstraint(WEST, surface, 5, WEST, contentPane);
    layout.putConstraint(NORTH, surface, 5, NORTH, contentPane);
    layout.putConstraint(EAST, contentPane, 5, EAST, surface);
    layout.putConstraint(NORTH, button, 5, SOUTH, surface);
    layout.putConstraint(EAST, button, 0, EAST, surface);
    layout.putConstraint(SOUTH, contentPane, 5, SOUTH, button);

    setSize(500, 500);
    setLocationRelativeTo(null);
  }

  public static void main(String[] args) throws IOException
  {
    final RuralPlanningGUI ps = new RuralPlanningGUI();
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        ps.setVisible(true);
      }
    });
    RuralPlanning.Main main = new RuralPlanning.Main(ps);
    main.doMain("ruralplanning-large");
//    main.doMain("ruralplanning-small");
//    main.doMain("ruralplanning-test");
  }

  @Override
  public void setRemainingPoint(Point point)
  {
    surface.setRemainingPoint(point);
    surface.repaint();
  }

  @Override
  public void fenceUpdated(PointList fence)
  {
    surface.fenceUpdated(fence);
    surface.repaint();
    waitABit();
  }

  @Override
  public void setProblemBounds(int minX, int minY, int maxX, int maxY)
  {
    surface.setProblemBounds(minX, minY, maxX, maxY);
  }

  @Override
  public void resultComplete(String result)
  {
    waitOnButton();
  }

  private void waitOnButton()
  {
    try
    {
      semaphore.acquire();
    }
    catch (InterruptedException ignored)
    {
    }
  }

  private void waitABit()
  {
    try
    {
      Thread.sleep(1);
    }
    catch (InterruptedException ignored)
    {
    }
  }

  private static class MyAbstractAction extends AbstractAction
  {
    private final Semaphore semaphore;

    private MyAbstractAction(Semaphore semaphore)
    {
      super("next");
      this.semaphore = semaphore;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      semaphore.release();
    }
  }
}