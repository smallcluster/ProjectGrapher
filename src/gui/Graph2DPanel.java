package gui;

import eval.Evaluator;

import java.awt.*;
import java.awt.event.*;

public class Graph2DPanel extends Animated2DView implements MouseListener {

    private Evaluator currentEval;
    private GraphControl graphControl = null;

    // Axis selection
    private boolean scalingAxisX = false;
    private boolean scalingAxisY = false;

    // ------------------ Controls -----------------------
    private float step = 0.01f;
    private boolean autoStep = true;

    //  SETTERS
    public void setAutoStep(boolean b){
        autoStep = b;
        updateAutoStep();
    }
    public void setStep(float v){
        if(!autoStep)
            step = v;
    }
    public void setRegion(float minx, float maxx, float miny, float maxy){
        setPixelsPerUnitX(getWidth() / (maxx - minx));
        offsetX = -getPixelsPerUnitX()*(maxx+minx)/2.0f;
        setPixelsPerUnitY(getHeight() / (maxy - miny));
        offsetY = getPixelsPerUnitY()*(maxy+miny)/2.0f;
        updateAutoStep();
    }

    public void setGraphControl(GraphControl graphControl){
        this.graphControl = graphControl;
    }
    // UPDATE CONTROLS PANEL
    public void updateAutoStep(){
        if(autoStep){
            step = 1.0f / getPixelsPerUnitX();
            if(graphControl != null)
                graphControl.setStep(step);
        }
    }

    // set view bounds in world coordinates
    private void updateRegion(){
        if(graphControl == null)
            return;
        graphControl.setRegion(getWorldX(0), getWorldX(getWidth()), getWorldY(getHeight()), getWorldY(0));
    }

    public Graph2DPanel(int w, int h, Evaluator evaluator) {
        super();
        setPreferredSize(new Dimension(w, h));
        currentEval = evaluator;

        addMouseListener(this);
        // Detect resize change
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                updateRegion();
            }
        });
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // background
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        drawGrid(g);
        drawAxes(g);
        //Draw function
        if (currentEval.isExpValid()){
            drawFunction(g);
        }
        // Draw fps
        drawFps(g);
    }

    @Override
    public void updateLogic() {}

    private void drawAxes(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(2));

        float halfGradSizeX = getPixelsPerUnitX() / 4;
        float halfGradSizeY = getPixelsPerUnitY() / 4;

        // X axis
        if(scalingAxisX)
            g2.setColor(Color.BLUE);
        else if((getScreenY(0)-5 < prevMouseY && getScreenY(0)+5 > prevMouseY))
            g2.setColor(Color.ORANGE);
        else
            g2.setColor(Color.black);

        g2.drawLine(0, (int) getScreenY(0), getWidth(), (int) getScreenY(0));
        for (float x = (float) Math.floor(getWorldX(0)); x < getWorldX(getWidth()); x++)
            g2.drawLine((int) getScreenX(x), (int) (getScreenY(0) - halfGradSizeX), (int) getScreenX(x), (int) (getScreenY(0) + halfGradSizeX));

        // Y axis
        if(scalingAxisY)
            g2.setColor(Color.BLUE);
        else if((getScreenX(0)-5 < prevMouseX && getScreenX(0)+5 > prevMouseX))
            g2.setColor(Color.ORANGE);
        else
            g2.setColor(Color.black);

        g2.drawLine((int) getScreenX(0), 0, (int) getScreenX(0), getHeight());
        for (float y = (float) Math.floor(getWorldY(0)); y > getWorldY(getHeight()); y--)
            g2.drawLine((int) (getScreenX(0) - halfGradSizeY), (int) getScreenY(y), (int) (getScreenX(0) + halfGradSizeY), (int) getScreenY(y));
        g2.dispose();
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.getHSBColor(0, 0, 0.8f));
        for (float x = (float) Math.floor(getWorldX(0)); x < getWorldX(getWidth()); x++)
            g.drawLine((int) getScreenX(x), 0, (int) getScreenX(x), getHeight());
        for (float y = (float) Math.floor(getWorldY(0)); y > getWorldY(getHeight()); y--)
            g.drawLine( 0, (int) getScreenY(y), getWidth(), (int) getScreenY(y));
    }

    public void drawFunction(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(2));

        int height = getHeight();
        float endX = getWorldX(getWidth());

        for (float x = getWorldX(0); x < endX; x += step) {
            float y = getScreenY(currentEval.eval(x, time));
            float yy = getScreenY(currentEval.eval(x + step, time));

            // draw line only if visible
            if ((y > 0 && y < height) || (yy > 0 && yy < height))
                g2.drawLine((int) getScreenX(x), (int) y, (int) getScreenX(x + step), (int) yy);
        }

        // draw mouse coords
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        float y = currentEval.eval(getWorldX(prevMouseX), time);
        // draw only the visible part of the line
        // because we can have freeze when drawing to infinity...
        float sy = getScreenY(y);
        if ((sy > 0) && (sy < getHeight()))
            g2.drawLine((int) clamp(getScreenX(0), 0, getWidth()), (int) clamp(sy, 0, getHeight()),
                    (int) prevMouseX, (int) clamp(sy, 0, getHeight()));

        g2.drawLine((int) prevMouseX, (int) clamp(getScreenY(0), 0, getHeight()),
                (int) prevMouseX, (int) clamp(sy, 0, getHeight()));

        g2.drawString("(" + getWorldX(prevMouseX) + ", " + y + ")", (int) prevMouseX + 32, (int) prevMouseY + 32);

        g2.dispose();
    }

    public float clamp(float val, float min, float max) {
        return Math.max(Math.min(val, max), min);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
        scalingAxisX = (getScreenY(0)-5 < prevMouseY && getScreenY(0)+5 > prevMouseY);
        scalingAxisY = (getScreenX(0)-5 < prevMouseX && getScreenX(0)+5 > prevMouseX);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
        scalingAxisX = false;
        scalingAxisY = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!scalingAxisX && !scalingAxisY) {
            super.mouseDragged(e);
            updateRegion();
            return;
        }
        // update base pixels per unit
        if(scalingAxisX && scalingAxisY){
            float dx = (getWorldX(e.getX())-getWorldX(prevMouseX))*getPixelsPerUnitX();
            setPixelsPerUnitX(getPixelsPerUnitX()+dx);
            setPixelsPerUnitY(getPixelsPerUnitY()+dx);
        } else if(scalingAxisX){
            float x = getWorldX(prevMouseX);
            if( x < 0.001 && x > -0.001){
                float dx = (getWorldX(e.getX())-getWorldX(prevMouseX))*getPixelsPerUnitX();
                setPixelsPerUnitX(getPixelsPerUnitX()+dx);
            } else {
                float ratio = getWorldX(e.getX())/getWorldX(prevMouseX);
                setPixelsPerUnitX(getPixelsPerUnitX()*ratio);
            }
        } else {
            float y = getWorldY(prevMouseY);
            if( y < 0.001 && y > -0.001){
                float dy = (getWorldY(e.getY())-getWorldY(prevMouseY))*getPixelsPerUnitY();
                setPixelsPerUnitY(getPixelsPerUnitY()+dy);
            } else {
                float ratio = getWorldY(e.getY())/getWorldY(prevMouseY);
                setPixelsPerUnitY(getPixelsPerUnitY()*ratio);
            }
        }

        if(getPixelsPerUnitX() < 2)
            setPixelsPerUnitX(2);
        if(getPixelsPerUnitY() < 2)
            setPixelsPerUnitY(2);

        prevMouseX = e.getX();
        prevMouseY = e.getY();
        updateRegion();
        updateAutoStep();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        super.mouseWheelMoved(e);
        updateRegion();
        updateAutoStep();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

}
