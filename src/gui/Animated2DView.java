package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

import static javax.swing.SwingUtilities.invokeAndWait;

public abstract class Animated2DView extends JPanel implements MouseMotionListener, MouseWheelListener {

    protected float offsetX = 0;
    protected float offsetY = 0;
    protected float prevMouseX = 0;
    protected float prevMouseY = 0;
    protected int fps = 60;
    protected float time = 0.0f;
    protected float basePixelsPerUnit = 64.0f;
    protected float zoom = 1.0f;
    protected int frameCap = 60;


    // --------- Threading ----------------
    private Thread thread;
    private boolean updating = false;
    private Runnable actions = new Runnable() {
        @Override
        public void run() {
            updateLogic();
            repaint();
        }
    };

    public void stop(){
        if(thread == null)
            return;
        updating = false;
        thread = null;
    }

    public void restart() {
        if(thread != null)
            stop();
        updating = true;
        thread = new Thread(() -> {
            while (updating){
                long startTime = System.nanoTime();
                // Do stuff synchronously
                try {
                    invokeAndWait(actions);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                // sleep
                double delta = (System.nanoTime()-startTime)/1000000000.0;
                double remainingTime = Math.max(1.0/frameCap - delta, 0);
                long sleepTime = (int) (remainingTime*1000.0);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Update time and fps
                delta = (System.nanoTime()-startTime)/1000000000.0;
                fps = (int) (1.0/delta);
                time += (float) delta;
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public Animated2DView(){
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public abstract void updateLogic();


    public float getScreenX(float x){
        return x*getPixelsPerUnit() +0.5f*getWidth()+offsetX;
    }

    public float getScreenY(float y){
        return -y*getPixelsPerUnit() +0.5f*getHeight()+offsetY;
    }

    public float getWorldX(float x){
        return (x-offsetX-0.5f*getWidth())/getPixelsPerUnit();
    }

    public float getWorldY(float y){
        return -(y-offsetY-0.5f*getHeight())/getPixelsPerUnit();
    }

    public float getPixelsPerUnit(){
        return basePixelsPerUnit *zoom;
    }


    public void drawFps(Graphics g){
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

    public void recenter(){
        offsetY = 0;
        offsetX = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        offsetX += e.getX()-prevMouseX;
        offsetY += e.getY()-prevMouseY;
        prevMouseX = e.getX();
        prevMouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float mx = getWorldX(e.getX());
        float my = getWorldY(e.getY());

        zoom -= zoom*e.getWheelRotation()/10.0f;
        if(zoom <= 0.1f)
            zoom = 0.1f;
        else if(zoom >= 100.0f)
            zoom = 100.0f;

        float newmx = getWorldX(e.getX());
        float newmy = getWorldY(e.getY());

        offsetX += getScreenX(newmx)-getScreenX(mx);
        offsetY += getScreenY(newmy)-getScreenY(my);
    }
}
