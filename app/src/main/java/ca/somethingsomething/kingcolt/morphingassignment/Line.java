package ca.somethingsomething.kingcolt.morphingassignment;

import android.graphics.Point;

/**
 * Created by Billy-Bob Joe Jr on 20/01/2017.
 * Holds the important information of a line that the user is drawing/has drawn.
 */

public class Line
{
    private Point pStart;
    private Point pEnd;

    public Line(int startX, int startY, int endX, int endY)
    {
        pStart = new Point(startX, startY);
        pEnd = new Point(endX, endY);
    }

    public Point getStart()
    {
        return pStart;
    }

    public void setStart(int x, int y)
    {
        pStart = new Point(x, y);
    }

    public Point getEnd()
    {
        return pEnd;
    }

    public void setEnd(int x, int y)
    {
        pEnd = new Point(x, y);
    }

    public int distFromStart(int x, int y)
    {
        int a = x - pStart.x;
        int b = y - pStart.y;

        return (int)Math.sqrt(a*a + b*b);
    }

    public int distFromEnd(int x, int y)
    {
        int a = x - pEnd.x;
        int b = y - pEnd.y;

        return (int)Math.sqrt(a*a + b*b);
    }
}
