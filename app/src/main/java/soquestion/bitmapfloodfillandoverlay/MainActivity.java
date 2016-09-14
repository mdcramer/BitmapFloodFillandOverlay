package soquestion.bitmapfloodfillandoverlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends Activity {

    Bitmap mask, background, filledMask, overlay;
    Canvas c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mask = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        // get the mask, copy it to filledMask and then flood from the center with CYAN
        filledMask = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);
        c = new Canvas(filledMask);
        c.drawBitmap(mask, 0, 0, new Paint());
        Point center = new Point(filledMask.getWidth() / 2, filledMask.getHeight() / 2);
        floodFill(filledMask, center, Color.TRANSPARENT, Color.WHITE);


        // create new overlay Bitmap, draw the filledMask and then add the background using PorterDuff
        overlay = Bitmap.createBitmap(filledMask.getWidth(), filledMask.getHeight(), Config.ARGB_8888);
        c = new Canvas(overlay);
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        c.drawBitmap(filledMask, 0, 0, new Paint());
        c.drawBitmap(background, 0, 0, p);

        DrawView drawView = new DrawView(this);
        // set background to light blue in order to see transparent areas
        drawView.setBackgroundColor(0xffd2d7fe);
        setContentView(drawView);
        drawView.requestFocus();
    }

    public class DrawView extends View {
        Paint p = new Paint();

        public DrawView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mask, 0, 0, p);
            int top = mask.getHeight();

            canvas.drawBitmap(filledMask, 0, top, p);
            top += filledMask.getHeight();

            canvas.drawBitmap(background, 0, top, p);
            top += background.getHeight();

            canvas.drawBitmap(overlay, 0, top, p);
        }
    }

    // method from http://stackoverflow.com/a/8925653/852795
    public void floodFill(Bitmap bmp, Point pt, int targetColor, int replacementColor) {

        Queue<Point> q = new LinkedList<>();
        q.add(pt);
        while (q.size() > 0) {
            Point n = q.poll();
            if (bmp.getPixel(n.x, n.y) != targetColor) continue;

            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                bmp.setPixel(w.x, w.y, replacementColor);
                if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor)) q.add(new Point(w.x, w.y - 1));
                if ((w.y < bmp.getHeight() - 1) && (bmp.getPixel(w.x, w.y + 1) == targetColor)) q.add(new Point(w.x, w.y + 1));
                w.x--;
            }
            while ((e.x < bmp.getWidth() - 1) && (bmp.getPixel(e.x, e.y) == targetColor)) {
                bmp.setPixel(e.x, e.y, replacementColor);

                if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor)) q.add(new Point(e.x, e.y - 1));
                if ((e.y < bmp.getHeight() - 1) && (bmp.getPixel(e.x, e.y + 1) == targetColor)) q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }
    }
}
