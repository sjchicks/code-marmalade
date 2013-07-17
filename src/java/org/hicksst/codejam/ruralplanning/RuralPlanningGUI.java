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

class Canvas extends JPanel implements ProgressCallback
{
  private static final boolean SHOW_NUMBERS = false;

  private final int border = 25;

  private int minX;
  private int minY;
  private int maxX;
  private int maxY;
  private int width;
  private int height;

  private PointList fence;
  private Point remainingPoint;

  private void doDrawing(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;

    if (remainingPoint == null || fence == null)
    {
      return;
    }

    Dimension size = getSize();

    width = size.width - 2 * border;
    height = size.height - 2 * border;

    g2d.setColor(Color.lightGray);
    g2d.fillRect(0, 0, size.width, size.height);

    g2d.setColor(Color.darkGray);
    g2d.drawLine(getScreenX(0), 0, getScreenX(0), size.height);
    g2d.drawLine(0, getScreenY(0), size.width, getScreenY(0));

    g2d.setColor(Color.blue);
    for (Point p : fence)
    {
      if (SHOW_NUMBERS)
      {
        g2d.drawString(String.valueOf(p.i), getScreenX(p.x), getScreenY(p.y));
      }
      drawLine(g2d, p.x, p.y, p.next.x, p.next.y);
    }

    g2d.setColor(Color.red);

    if (SHOW_NUMBERS)
    {
      g2d.drawString(String.valueOf(remainingPoint.i), getScreenX(remainingPoint.x), getScreenY(remainingPoint.y));
    }
    drawLine(g2d, remainingPoint.x, remainingPoint.y, remainingPoint.x, remainingPoint.y);
  }

  private void drawLine(Graphics2D g2d, int px1, int py1, int px2, int py2)
  {
    g2d.drawLine(getScreenX(px1), getScreenY(py1), getScreenX(px2), getScreenY(py2));
  }

  private int getScreenY(int modelY)
  {
    return height - ((modelY - minY) * height / (maxY - minY)) + border;
  }

  private int getScreenX(int modelX)
  {
    return (modelX - minX) * width / (maxX - minX) + border;
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
    this.remainingPoint = point;
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
  private static final int DELAY_BETWEEN_MOVES_MS = 0;
  private static final boolean WAIT_FOR_BUTTON_AFTER_TEST_CASE = false;

  private final Canvas canvas = new Canvas();

  private final Semaphore semaphore = new Semaphore(1);

  public RuralPlanningGUI()
  {
    waitOnButton();
    setTitle("Rural Planning");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    SpringLayout layout = new SpringLayout();
    setLayout(layout);

    add(canvas);
    JButton button = new JButton(new MyAbstractAction(semaphore));
    add(button);

    Container contentPane = getContentPane();
    layout.putConstraint(WEST, canvas, 5, WEST, contentPane);
    layout.putConstraint(NORTH, canvas, 5, NORTH, contentPane);
    layout.putConstraint(EAST, contentPane, 5, EAST, canvas);
    layout.putConstraint(NORTH, button, 5, SOUTH, canvas);
    layout.putConstraint(EAST, button, 0, EAST, canvas);
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
    canvas.setRemainingPoint(point);
    canvas.repaint();
  }

  @Override
  public void fenceUpdated(PointList fence)
  {
    canvas.fenceUpdated(fence);
    canvas.repaint();
    waitABit();
  }

  @Override
  public void setProblemBounds(int minX, int minY, int maxX, int maxY)
  {
    canvas.setProblemBounds(minX, minY, maxX, maxY);
  }

  @Override
  public void resultComplete(String result)
  {
    if (WAIT_FOR_BUTTON_AFTER_TEST_CASE)
    {
      waitOnButton();
    }
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
      Thread.sleep(DELAY_BETWEEN_MOVES_MS);
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