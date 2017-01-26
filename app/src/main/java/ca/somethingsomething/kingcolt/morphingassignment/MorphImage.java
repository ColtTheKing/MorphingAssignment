package ca.somethingsomething.kingcolt.morphingassignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Billy-Bob Joe Jr on 22/01/2017.
 * Holds information about the bitmaps stored in the imageViews and
 * contains methods to modify them.
 */

public class MorphImage
{
    public static final int TOUCH_RADIUS = 50;
    public static final int START_W = 6;
    public static final int END_W = 10;
    private ImageView myView;
    private Bitmap scaledImg, displayedImg;
    private int id;
    private ArrayList<Line> lines = new ArrayList<Line>();
    private Line selectedLine;
    private boolean startEnd; //Which point on the line is selected? true = start; false = end
    private Paint paint = new Paint();

    public MorphImage(ImageView view, int myId)
    {
        myView = view;
        id = myId;
        paint.setStrokeWidth(5);
    }

    public ImageView getView()
    {
        return myView;
    }

    public Bitmap getDisplayed()
    {
        return displayedImg;
    }

    public Bitmap getScaled()
    {
        return scaledImg;
    }

    public int getId()
    {
        return id;
    }

    public Line getSelectedLine()
    {
        return selectedLine;
    }

    /**
     * Returns true if the user selected the start of the line and false if they selected the end.
     */
    public boolean whichPoint()
    {
        return startEnd;
    }

    public void chooseImg(Bitmap bitmap)
    {
        myView.setImageBitmap(bitmap);
        myView.buildDrawingCache();
        scaledImg = myView.getDrawingCache();
        displayedImg = myView.getDrawingCache();
    }

    public void drawLine(int startX, int startY, int endX, int endY)
    {
        Bitmap tempBmp = Bitmap.createBitmap(displayedImg.getWidth(),
                displayedImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBmp);

        lines.add(new Line(startX, startY, endX, endY));

        tempCanvas.drawBitmap(displayedImg, 0, 0, null);
        tempCanvas.drawLine(startX, startY, endX, endY, paint);
        tempCanvas.drawCircle(startX, startY, START_W, paint);
        tempCanvas.drawCircle(endX, endY, END_W, paint);

        myView.setImageBitmap(tempBmp);
        displayedImg = tempBmp;
    }

    private void redrawLines()
    {
        Bitmap tempBmp = Bitmap.createBitmap(scaledImg.getWidth(),
                scaledImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBmp);

        tempCanvas.drawBitmap(scaledImg, 0, 0, null);

        for(Line ln : lines)
        {
            tempCanvas.drawLine(ln.getStart().x, ln.getStart().y, ln.getEnd().x,
                    ln.getEnd().y, paint);
            tempCanvas.drawCircle(ln.getStart().x, ln.getStart().y, START_W, paint);
            tempCanvas.drawCircle(ln.getEnd().x, ln.getEnd().y, END_W, paint);
        }

        if(selectedLine != null)
        {
            paint.setColor(Color.RED);
            tempCanvas.drawLine(selectedLine.getStart().x, selectedLine.getStart().y,
                    selectedLine.getEnd().x, selectedLine.getEnd().y, paint);
            tempCanvas.drawCircle(selectedLine.getStart().x, selectedLine.getStart().y, START_W, paint);
            tempCanvas.drawCircle(selectedLine.getEnd().x, selectedLine.getEnd().y, END_W, paint);
            paint.setColor(Color.BLACK);
        }

        myView.setImageBitmap(tempBmp);
        displayedImg = tempBmp;
    }

    public void deleteLine(int index)
    {

    }

    /**
     * Takes in the point that was touched and finds the closest
     * line ending to that position within TOUCH_RADIUS pixels.
     */
    public int selectLine(int x, int y)
    {
        int distance = TOUCH_RADIUS + 1;
        int index = -1;

        if(selectedLine != null)
        {
            lines.add(selectedLine);
            selectedLine = null;
        }

        for(int i = 0; i < lines.size(); i++)
        {
            int tempDist = lines.get(i).distFromStart(x,y);

            if(tempDist < distance)
            {
                selectedLine = lines.get(i);
                startEnd = true;
                index = i;
                distance = tempDist;
            }

            tempDist = lines.get(i).distFromEnd(x,y);

            if(tempDist < distance)
            {
                selectedLine = lines.get(i);
                startEnd = false;
                index = i;
                distance = tempDist;
            }
        }
        if(distance <= TOUCH_RADIUS)
        {
            lines.remove(index);
        }

        redrawLines();
        return index;
    }

    /**
     * Takes in the index of a line that has been determined to
     * be closest to the point
     */
    public void selectLine(int index, boolean whichPoint)
    {
        if(selectedLine != null)
        {
            lines.add(selectedLine);
        }

        if(index >= 0)
        {
            selectedLine = lines.get(index);
            startEnd = whichPoint;
            lines.remove(index);
        }
        else
        {
            selectedLine = null;
        }
        redrawLines();
    }

    public void moveLine(int x, int y)
    {
        if(startEnd)
            selectedLine.setStart(x, y);
        else
            selectedLine.setEnd(x, y);

        redrawLines();
    }

    public void rotateImg()
    {
        if(scaledImg == null)
            return;

        Matrix mat = new Matrix();
        mat.postRotate(90);

        scaledImg = Bitmap.createBitmap(scaledImg, 0, 0, scaledImg.getWidth(),
                scaledImg.getHeight(), mat, true);
        displayedImg = Bitmap.createBitmap(displayedImg, 0, 0, displayedImg.getWidth(),
                displayedImg.getHeight(), mat, true);

        myView.setImageBitmap(displayedImg);
    }

    public void deleteImage()
    {
        scaledImg = null;
        displayedImg = null;
        selectedLine = null;
        lines.removeAll(lines);
        myView.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}
