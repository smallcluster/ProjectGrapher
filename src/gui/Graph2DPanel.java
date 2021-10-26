package gui;

import eval.Evaluator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


// TODO : fix viewport interval
public class Graph2DPanel extends JPanel implements MouseMotionListener, MouseWheelListener, Runnable {

    private float mouseX = 0;
    private float mouseY = 0;
    private float offsetX = 0;
    private float offsetY = 0;
    private float zoom = 1.0f;
    private float fixedPixelsPerUnit = 64;
    private float time = 0.0f;

    private Evaluator currentEval;

    public Graph2DPanel(int w, int h, Evaluator evaluator){
        setPreferredSize(new Dimension(w,h));
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        currentEval = evaluator;

        Thread anim = new Thread(this);
        anim.setDaemon(true);
        anim.start();
    }

    // TODO : Draw only the visible part !

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        long startTime = System.nanoTime();

        int width = getWidth();
        int height = getHeight();

        float centerX = width/2.0f + offsetX;
        float centerY = height/2.0f + offsetY;

        int pixelsPerUnit = (int) (fixedPixelsPerUnit*zoom);
        int halfGradSize = pixelsPerUnit/4;

        g.setColor(Color.black);
        g.fillRect(0,0, width, height);

        // Draw grid
        g.setColor(Color.getHSBColor(0, 0, 0.1f));

        // vertical
        for(int i=1; i <= ((int)centerX)/(pixelsPerUnit/2); i++)
            g.drawLine((int) centerX-(pixelsPerUnit/2)*i, 0, (int) centerX-(pixelsPerUnit/2)*i, height);
        for(int i=1; i <= 2*(width-centerX)/pixelsPerUnit; i++)
            g.drawLine((int) centerX+(pixelsPerUnit/2)*i, 0, (int) centerX+(pixelsPerUnit/2)*i, height);

        // horizontal
        for(int i=1; i <= ((int) centerY)/(pixelsPerUnit/2); i++)
            g.drawLine(0, (int) centerY-(pixelsPerUnit/2)*i, width, (int) centerY-(pixelsPerUnit/2)*i);
        for(int i=1; i <= 2*(height-centerY)/(pixelsPerUnit); i++)
            g.drawLine(0, (int) centerY+(pixelsPerUnit/2)*i, width, (int) centerY+(pixelsPerUnit/2)*i);


        // draw Axes
        g.setColor(Color.red);
        g.drawLine(0, (int) centerY, width , (int) centerY);

        for(int i=1; i <= ((int)centerX)/pixelsPerUnit; i++)
            g.drawLine((int) centerX-pixelsPerUnit*i, (int) centerY-halfGradSize, (int) centerX-pixelsPerUnit*i, (int) centerY+halfGradSize);
        for(int i=1; i <= ((int)width-centerX)/pixelsPerUnit; i++)
            g.drawLine((int) centerX+pixelsPerUnit*i, (int) centerY-halfGradSize, (int) centerX+pixelsPerUnit*i, (int) centerY+halfGradSize);

        g.setColor(Color.green);
        g.drawLine((int) centerX, 0, (int) centerX , height);

        for(int i=1; i <= ((int) centerY)/pixelsPerUnit; i++)
            g.drawLine((int) centerX-halfGradSize, (int) centerY-pixelsPerUnit*i, (int) centerX+halfGradSize, (int) centerY-pixelsPerUnit*i);
        for(int i=1; i <= ((int) height-centerY)/pixelsPerUnit; i++)
            g.drawLine((int) centerX-halfGradSize, (int) centerY+pixelsPerUnit*i, (int) centerX+halfGradSize, (int) centerY+pixelsPerUnit*i);

        // Draw function
        if(!currentEval.isExpValid())
            return;

        g.setColor(Color.white);

        float d = 1.0f/ (float) (pixelsPerUnit*10.0);
        float x = - centerX / (float) pixelsPerUnit;

        while (x <  (width-centerX) / (float) pixelsPerUnit){
            float y = -(currentEval.eval(x, time));
            float yy = -(currentEval.eval(x+d, time));
            g.drawLine((int) (centerX + x*pixelsPerUnit), (int)(centerY+y*pixelsPerUnit), (int)( centerX +(x+d)*pixelsPerUnit), (int) (centerY+yy*pixelsPerUnit));
            x += d;
        }

        // draw mouse coords
        float mx = (mouseX-centerX) / (float) pixelsPerUnit;
        float my = (mouseY-centerY) / (float) pixelsPerUnit;
        g.setColor(Color.cyan);
        float y = -(currentEval.eval(mx, time));
        g.drawLine((int) (centerX+mx*pixelsPerUnit), (int) centerY, (int) (centerX+mx*pixelsPerUnit), (int) (centerY+y*pixelsPerUnit));
        g.drawLine((int) centerX, (int) (centerY+y*pixelsPerUnit), (int) (centerX+mx*pixelsPerUnit), (int) (centerY+y*pixelsPerUnit));

        g.drawString("("+mx+", "+y+")", (int) (centerX+mx*pixelsPerUnit)+32, (int)(centerY+my*pixelsPerUnit)+32);

        double delta = (System.nanoTime()-startTime)/1000000000.0;
        time += (float) delta;
    }

    public void recenter(){
        offsetY = 0;
        offsetX = 0;
        //repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float mx = mouseX;
        float my = mouseY;
        mouseX = e.getX();
        mouseY = e.getY();
        offsetX += mouseX-mx;
        offsetY += mouseY-my;
        //repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        //repaint();
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

        //repaint();
    }

    @Override
    public void run() {
        while (true) {
            repaint();
        }
    }

}
