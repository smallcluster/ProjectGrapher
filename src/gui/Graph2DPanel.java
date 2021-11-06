package gui;

import eval.Evaluator;

import java.awt.*;

public class Graph2DPanel extends Animated2DView {

    private Evaluator currentEval;

    public Graph2DPanel(int w, int h, Evaluator evaluator) {
        setPreferredSize(new Dimension(w, h));
        currentEval = evaluator;
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
        if (currentEval.isExpValid())
            drawFunction(g);
        // Draw fps
        drawFps(g);
    }

    @Override
    public void updateLogic() {
    }

    private void drawAxes(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(2));

        float halfGradSize = getPixelsPerUnit() / 4;
        g2.setColor(Color.black);
        // X axis
        g2.drawLine(0, (int) getScreenY(0), getWidth(), (int) getScreenY(0));
        for (float x = (float) Math.floor(getWorldX(0)); x < getWorldX(getWidth()); x++)
            g2.drawLine((int) getScreenX(x), (int) (getScreenY(0) - halfGradSize), (int) getScreenX(x), (int) (getScreenY(0) + halfGradSize));
        // Y axis
        g2.drawLine((int) getScreenX(0), 0, (int) getScreenX(0), getHeight());
        for (float y = (float) Math.floor(getWorldY(0)); y > getWorldY(getHeight()); y--)
            g2.drawLine((int) (getScreenX(0) - halfGradSize), (int) getScreenY(y), (int) (getScreenX(0) + halfGradSize), (int) getScreenY(y));
        g2.dispose();
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.getHSBColor(0, 0, 0.8f));
        for (float x = (float) Math.floor(getWorldX(0)); x < getWorldX(getWidth()); x++)
            g.drawLine((int) getScreenX(x), 0, (int) getScreenX(x), getHeight());
        for (float y = (float) Math.floor(getWorldY(0)); y > getWorldY(getHeight()); y--)
            g.drawLine((int) 0, (int) getScreenY(y), getWidth(), (int) getScreenY(y));
    }

    public void drawFunction(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(2));

        float d = 1.0f / getPixelsPerUnit();

        int height = getHeight();
        float endX = getWorldX(getWidth());
        for (float x = getWorldX(0); x < endX; x += d) {
            float y = getScreenY(currentEval.eval(x, time));
            float yy = getScreenY(currentEval.eval(x + d, time));

            // draw line only if visible
            if ((y > 0 && y < height) || (yy > 0 && yy < height))
                g2.drawLine((int) getScreenX(x), (int) y, (int) getScreenX(x + d), (int) yy);
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
        if (val < min)
            return min;
        else if (val > max)
            return max;
        else
            return val;
    }
}
