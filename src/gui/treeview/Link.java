package gui.treeview;

import java.awt.*;

public class Link {

    private Particle p1, p2;
    private float restLength;

    enum Type {
        NORMAL,
        COND,
        TRUE,
        FALSE
    }

    private Type type;

    public Link(Particle p1, Particle p2) {
        this.p1 = p1;
        this.p2 = p2;
        restLength = getLength();
        type = Type.NORMAL;
    }

    public Link(Particle p1, Particle p2, float restLength) {
        this.p1 = p1;
        this.p2 = p2;
        this.restLength = restLength;
        type = Type.NORMAL;
    }

    public Link(Particle p1, Particle p2, Type type) {
        this.p1 = p1;
        this.p2 = p2;
        restLength = getLength();
        this.type = type;
    }

    public Link(Particle p1, Particle p2, float restLength, Type type) {
        this.p1 = p1;
        this.p2 = p2;
        this.restLength = restLength;
        this.type = type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public float getLength() {
        return (float) Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
    }

    public void update() {

        if (p1.isFixed() && p2.isFixed())
            return;

        // Apply dist constraint
        float length = getLength();
        float d = (length - restLength);

        float dx = d * (p2.x - p1.x) / length;
        float dy = d * (p2.y - p1.y) / length;

        if (p1.isFixed()) {
            p2.x -= dx;
            p2.y -= dy;
        } else if (p2.isFixed()) {
            p1.x += dx;
            p1.y += dy;
        } else {
            dx /= 2.0f;
            dy /= 2.0f;
            p1.x += dx;
            p1.y += dy;
            p2.x -= dx;
            p2.y -= dy;
        }
    }

    public void paint(Graphics g, float offsetX, float offsetY, float zoom) {
        String info = "";
        switch (type) {
            case NORMAL:
                g.setColor(Color.black);
                break;
            case COND:
                g.setColor(Color.blue);
                info = "COND";
                break;
            case TRUE:
                g.setColor(Color.green);
                info = "TRUE";
                break;
            case FALSE:
                g.setColor(Color.red);
                info = "FALSE";
                break;
        }
        g.drawLine((int) (p1.x * zoom + offsetX), (int) (p1.y * zoom + offsetY), (int) (p2.x * zoom + offsetX), (int) (p2.y * zoom + offsetY));

        if (!info.isEmpty()) {
            Font font = new Font("TimesRoman", Font.PLAIN, (int) (24 * zoom));
            FontMetrics metrics = g.getFontMetrics(font);
            int tw = metrics.stringWidth(info);
            int th = metrics.getHeight() + (metrics.getDescent() - metrics.getAscent());
            float x = (p2.x+p1.x)/2.0f;
            float y = (p2.y+p1.y)/2.0f;
            g.setFont(font);
            g.drawString(info, (int) (x * zoom + offsetX - tw / 2.0), (int) (y * zoom + offsetY + th / 2.0));
        }
    }

}
