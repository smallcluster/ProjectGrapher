package gui;

import java.awt.*;

public class Particle {

    private String name;

    // Verlet physics
    public float oldx;
    public float oldy;
    public float x;
    public float y;
    public float r;

    private boolean isFixed = false;
    private boolean isRoot = false;

    public Particle(String name, float x, float y, float r){
        this.name = name;
        this.x = x;
        this.y = y;
        oldy = y;
        oldx = x;
        this.r = r;
    }

    public void markAsRoot(){
        isRoot = true;
    }

    public boolean isFixed(){
        return isFixed || isRoot;
    }

    public boolean isRoot(){
        return isRoot;
    }

    public void setFixed(boolean fixed){
        isFixed = fixed;
    }

    public void paint(Graphics g, float offsetX, float offsetY, float zoom){

        if(isRoot)
            g.setColor(Color.red);
        else if(isFixed)
            g.setColor(Color.blue);
        else
            g.setColor(Color.black);

        g.fillOval((int) (x*zoom+offsetX-r*zoom), (int) (y*zoom+offsetY-r*zoom),(int) (r*2*zoom),(int) (r*2*zoom));
        g.setColor(Color.white);
        Font font = new Font("TimesRoman", Font.PLAIN, (int)(24*zoom));
        FontMetrics metrics = g.getFontMetrics(font);
        int tw = metrics.stringWidth(name);
        int th = metrics.getHeight() + (metrics.getDescent() - metrics.getAscent());
        g.setFont(font);
        g.drawString(name, (int) (x*zoom+offsetX-tw/2.0), (int) (y*zoom+offsetY+th/2.0));

    }

    public void update(float delta){
        if(isFixed())
            return;
        float dx = (x-oldx) * 0.2f;
        float dy = (y-oldy) * 0.2f;
        oldx = x;
        oldy = y;
        x += dx;
        y += dy;
    }

    private void applyForces(Particle other, float fx, float fy){
        if (isFixed()) {
            other.x += fx;
            other.y += fy;
        } else if (other.isFixed()) {
            x -= fx;
            y -= fy;
        } else {
            fx /= 2.0f;
            fy /= 2.0f;
            other.x += fx;
            other.y += fy;
            x -= fx;
            y -= fy;
        }
    }

    public void repel(Particle other, float delta){
        if(isFixed() && other.isFixed())
            return;
        float dd = (other.x - x) * (other.x - x) + (other.y - y) * (other.y - y);
        if (dd == 0)
            dd = 0.1f; // to prevent division by zero
        float fx = delta*70000 * (other.x - x) /dd;
        float fy = delta*70000 * (other.y - y) /dd;

        applyForces(other, fx, fy);
    }

    public void staticCollision(Particle other){
        if(isFixed() && other.isFixed())
            return;
        float dd = (other.x - x) * (other.x - x) + (other.y - y) * (other.y - y);
        if (dd == 0)
            dd = 0.1f; // to prevent division by zero
        if(dd <= (r+ other.r)*(r+ other.r)){
            float d = (float) Math.sqrt(dd);
            float diff = r+ other.r-d;
            float fx = diff * (other.x - x) /d;
            float fy = diff * (other.y - y) /d;
            applyForces(other, fx, fy);
        }
    }
}
