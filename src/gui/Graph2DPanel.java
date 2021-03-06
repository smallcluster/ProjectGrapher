package gui;

import java.awt.*;
import java.awt.event.*;

public class Graph2DPanel extends Animated2DView implements MouseListener {

    private final FunctionList functionList;
    private Function currentFunction = null;

    public Function getCurrentFunction() {
        return currentFunction;
    }

    public void setCurrentFunction(Function currentFunction) {
        this.currentFunction = currentFunction;
    }

    private GraphControl graphControl = null;


    // Axis selection
    private boolean scalingAxisX = false;
    private boolean scalingAxisY = false;

    // ------------------ Controls -----------------------
    private float step = 0.01f;
    private boolean autoStep = true;
    private boolean showGrid = true;

    public boolean isAutoStep(){
        return autoStep;
    }

    public float getStep(){
        return step;
    }

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

    public Graph2DPanel(int w, int h, FunctionList functionList) {
        super();
        this.functionList = functionList;
        setPreferredSize(new Dimension(w, h));

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
        g.setColor(bgColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        if(showGrid)
            drawGrid(g);
        drawAxes(g);
        //Draw functions
        for(Function f : functionList.getFunctions()){
            if(!f.isVisible()) continue;
            if(currentFunction != null)
                if(f.getName().equals(currentFunction.getName()))
                    continue;
            drawFunction(g, f);
        }

        // Draw the function in the input bar
        if( currentFunction != null){
            drawFunction(g, currentFunction);
            drawMouseCoords(g, currentFunction);
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

    public void drawFunction(Graphics g, Function function) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.setColor(function.getColor());
        g2.setStroke(new BasicStroke(2));

        int height = getHeight();
        float endX = getWorldX(getWidth());

        for (float x = getWorldX(0); x < endX; x += step) {
            float y = getScreenY(function.eval(x, time));
            float yy = getScreenY(function.eval(x + step, time));

            // draw line only if visible
            if ((y > 0 && y < height) || (yy > 0 && yy < height))
                g2.drawLine((int) getScreenX(x), (int) y, (int) getScreenX(x + step), (int) yy);
        }

        g2.dispose();
    }

    public void drawMouseCoords(Graphics g, Function function){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // draw mouse coords
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        float y = function.eval(getWorldX(prevMouseX), time);
        // draw only the visible part of the line
        // because we can have freezes when drawing to infinity...
        float sy = getScreenY(y);
        if ((sy > 0) && (sy < getHeight()))
            g2.drawLine((int) clamp(getScreenX(0), 0, getWidth()), (int) clamp(sy, 0, getHeight()),
                    (int) prevMouseX, (int) clamp(sy, 0, getHeight()));

        g2.drawLine((int) prevMouseX, (int) clamp(getScreenY(0), 0, getHeight()),
                (int) prevMouseX, (int) clamp(sy, 0, getHeight()));

        Font font = new Font("TimesRoman", Font.PLAIN, 18);
        g2.setFont(font);
        g2.drawString("(" + getWorldX(prevMouseX) + ", " + y + ")", (int) prevMouseX + 32, (int) prevMouseY + 32);

        g2.dispose();

    }

    public float clamp(float val, float min, float max) {
        return Math.max(Math.min(val, max), min);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e){
        super.mouseMoved(e);
        if( (getScreenY(0)-5 < e.getY() && getScreenY(0)+5 > e.getY()) ||
                (getScreenX(0)-5 < e.getX() && getScreenX(0)+5 > e.getX())
        ){
            setCursor(handCursor);
        } else {
            setCursor(arrowCursor);
        }
    }

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

    public void setShowGrid(boolean selected) {
        showGrid = selected;
    }

}
