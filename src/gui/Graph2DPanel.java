package gui;

import eval.Evaluator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.InvocationTargetException;

import static javax.swing.SwingUtilities.invokeAndWait;

public class Graph2DPanel extends JPanel implements MouseMotionListener, MouseWheelListener {

    private float mouseX = 0;
    private float mouseY = 0;
    private float offsetX = 0;
    private float offsetY = 0;
    private float zoom = 1.0f;
    private float fixedPixelsPerUnit = 64;
    private float time = 0.0f;
    private int fps = 60;

    private Evaluator currentEval;


    final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            repaint();
        }
    };


    public Graph2DPanel(int w, int h, Evaluator evaluator){
        setPreferredSize(new Dimension(w,h));
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        currentEval = evaluator;

        Thread anim = new Thread() {
            public void run(){
                while (true){
                    long startTime = System.nanoTime();
                    try {
                        invokeAndWait(refresh);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    double delta = ((System.nanoTime()-startTime)/1000000000.0);
                    double remainingTime = Math.max(1.0/60.0 - delta, 0);
                    long sleepTime = (int) (remainingTime*1000.0);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    delta = ((System.nanoTime()-startTime)/1000000000.0);
                    fps = (int) (1.0/delta);
                    time += (float) delta;
                }
            }
        };
        anim.setDaemon(true);
        anim.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int width = getWidth();
        int height = getHeight();
        float centerX = width/2.0f + offsetX;
        float centerY = height/2.0f + offsetY;
        float pixelsPerUnit = (fixedPixelsPerUnit*zoom);

        // background
        g.setColor(Color.white);
        g.fillRect(0,0, width, height);

        drawGrid(g, centerX, centerY, pixelsPerUnit, width, height);
        drawAxes(g, centerX, centerY, pixelsPerUnit, width, height);

        // Draw function
        if(currentEval.isExpValid())
            drawFunction(g, centerX, centerY, pixelsPerUnit, width);

        // Draw fps
        drawFps(g);
    }

    private void drawFps(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font font = new Font("TimesRoman", Font.BOLD, 16);
        FontMetrics metrics = g2.getFontMetrics(font);
        String fpsinfo = fps+" FPS";
        int th = metrics.getHeight() + (metrics.getDescent() - metrics.getAscent());
        g2.setColor(Color.black);
        g2.setFont(font);
        g2.drawString(fpsinfo, 4, th*2);
        g2.dispose();
    }

    private void drawAxes(Graphics g, float centerX, float centerY, float pixelsPerUnit, int width, int height){

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setStroke(new BasicStroke(2));

        float halfGradSize = pixelsPerUnit/4;
        g2.setColor(Color.black);
        // X axis
        g2.drawLine(0, (int) centerY, width , (int) centerY);
        for(int i=1; i <= centerX/pixelsPerUnit; i++)
            g2.drawLine((int) (centerX-pixelsPerUnit*i), (int) (centerY-halfGradSize), (int) (centerX-pixelsPerUnit*i), (int) (centerY+halfGradSize));
        for(int i=1; i <= (width-centerX)/pixelsPerUnit; i++)
            g2.drawLine((int) (centerX+pixelsPerUnit*i), (int) (centerY-halfGradSize), (int) (centerX+pixelsPerUnit*i), (int) (centerY+halfGradSize));

        // Y axis
        g2.drawLine((int) centerX, 0, (int) centerX , height);
        for(int i=1; i <= centerY/pixelsPerUnit; i++)
            g2.drawLine((int) (centerX-halfGradSize), (int) (centerY-pixelsPerUnit*i), (int) (centerX+halfGradSize), (int) (centerY-pixelsPerUnit*i));
        for(int i=1; i <= (height-centerY)/pixelsPerUnit; i++)
            g2.drawLine((int) (centerX-halfGradSize), (int) (centerY+pixelsPerUnit*i), (int) (centerX+halfGradSize), (int) (centerY+pixelsPerUnit*i));

        g2.dispose();
    }

    private void drawGrid(Graphics g, float centerX, float centerY, float pixelsPerUnit, int width, int height){
        g.setColor(Color.getHSBColor(0, 0, 0.8f));
        // vertical
        for(int i=1; i <= (centerX/pixelsPerUnit); i++)
            g.drawLine((int) (centerX-pixelsPerUnit*i), 0, (int) (centerX-pixelsPerUnit*i), height);
        for(int i=1; i <= (width-centerX)/pixelsPerUnit; i++)
            g.drawLine((int) (centerX+pixelsPerUnit*i), 0, (int) (centerX+pixelsPerUnit*i), height);

        // horizontal
        for(int i=1; i <= (int) (centerY/pixelsPerUnit); i++)
            g.drawLine(0, (int) (centerY-pixelsPerUnit*i), width, (int) (centerY-pixelsPerUnit*i));
        for(int i=1; i <= (height-centerY)/pixelsPerUnit; i++)
            g.drawLine(0, (int) (centerY+pixelsPerUnit*i), width, (int) (centerY+pixelsPerUnit*i));
    }

    public void drawFunction(Graphics g, float centerX, float centerY, float pixelsPerUnit, float width){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(2));
        float d = 1.0f/ (pixelsPerUnit);
        float x = - centerX / pixelsPerUnit;

        while (x <  (width-centerX) / pixelsPerUnit){
            float y = -(currentEval.eval(x, time));
            float yy = -(currentEval.eval(x+d, time));
            g2.drawLine((int) (centerX + x*pixelsPerUnit), (int)(centerY+y*pixelsPerUnit), (int)( centerX +(x+d)*pixelsPerUnit), (int) (centerY+yy*pixelsPerUnit));
            x += d;
        }

        // draw mouse coords
        float mx = (mouseX-centerX) / pixelsPerUnit;
        float my = (mouseY-centerY) / pixelsPerUnit;
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        float y = -(currentEval.eval(mx, time));
        g2.drawLine((int) (centerX+mx*pixelsPerUnit), (int) centerY, (int) (centerX+mx*pixelsPerUnit), (int) (centerY+y*pixelsPerUnit));
        g2.drawLine((int) centerX, (int) (centerY+y*pixelsPerUnit), (int) (centerX+mx*pixelsPerUnit), (int) (centerY+y*pixelsPerUnit));
        g2.drawString("("+mx+", "+(-y)+")", (int) (centerX+mx*pixelsPerUnit)+32, (int)(centerY+my*pixelsPerUnit)+32);

        g2.dispose();
    }


    public void recenter(){
        offsetY = 0;
        offsetX = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float mx = mouseX;
        float my = mouseY;
        mouseX = e.getX();
        mouseY = e.getY();
        offsetX += mouseX-mx;
        offsetY += mouseY-my;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        int width = getWidth();
        int height = getHeight();
        float centerX = width/2.0f + offsetX;
        float centerY = height/2.0f + offsetY;

        int pixelsPerUnit = (int) (fixedPixelsPerUnit*zoom);
        float mx = (e.getX()-centerX) / (float) pixelsPerUnit;
        float my = (e.getY()-centerY) / (float) pixelsPerUnit;

        zoom -= zoom*e.getWheelRotation()/10.0f;
        if(zoom <= 0.1f)
            zoom = 0.1f;
        else if(zoom >= 100.0f)
            zoom = 100.0f;
        int newPixelsPerUnit = (int) (fixedPixelsPerUnit*zoom);
        float newmx = (e.getX()-centerX) / (float) newPixelsPerUnit;
        float newmy = (e.getY()-centerY) / (float) newPixelsPerUnit;

        offsetX += (newmx-mx)*newPixelsPerUnit;
        offsetY += (newmy-my)*newPixelsPerUnit;
    }
}
