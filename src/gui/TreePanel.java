package gui;

import eval.Evaluator;
import eval.tree.Node;
import eval.tree.TestIF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static javax.swing.SwingUtilities.invokeAndWait;

public class TreePanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseListener {

    private final Evaluator evaluator;
    private final float DELTA = 1.0f / 60.0f;
    private float mouseX = 0;
    private float mouseY = 0;
    private float offsetX = 0;
    private float offsetY = 0;
    private float zoom = 0.5f;
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final ArrayList<Link> links = new ArrayList<>();
    private int fps = 60;

    private Particle selected = null;

    final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            update(DELTA);
            repaint();
        }
    };

    public TreePanel(Evaluator evaluator) {
        this.evaluator = evaluator;
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addMouseListener(this);
        Thread drawingThread = new Thread(){
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
                }
            }
        };
        drawingThread.setDaemon(true);
        drawingThread.start();
    }

    public void buildParticleSim() {
        Node tree = evaluator.getExpTree();
        synchronized (particles) {
            particles.clear();
        }
        synchronized (links) {
            links.clear();
        }
        if (tree != null) {
            Particle root = createParticlesFromTree(tree, true);
            particles.add(root);
        }
    }

    public void repelParticles(float delta) {
        synchronized (particles) {
            for (int i = 0; i < particles.size(); i++) {
                for (int j = i+1; j < particles.size(); j++) {
                    Particle p1 = particles.get(i);
                    Particle p2 = particles.get(j);
                    p1.repel(p2, delta);
                }
            }
        }
    }

    public void updateParticlesPos(float delta) {
        synchronized (particles) {
            for (Particle p : particles) {
                p.update(delta);
            }
        }
    }

    public void updateLinks() {
        // update constraints
        synchronized (links) {
            for (Link li : links)
                li.update();
        }
    }

    public Particle createParticlesFromTree(Node tree, boolean root) {
        Particle p;
        if (root) {
            p = new Particle(tree.getName(), 0, 0, 32);
            p.markAsRoot();
        } else {
            p = new Particle(tree.getName(), (float) (2 * Math.random() - 1) * 64.0f,(float) (2 * Math.random() - 1) * 64.0f, 32);
        }
        Node left = tree.getLeft();
        Node right = tree.getRight();

        // check if test
        if (tree.getName().equals("IF")) {
            TestIF test = (TestIF) tree;
            Node cond = test.getCond();
            Particle condP = createParticlesFromTree(cond, false);
            synchronized (particles) {
                particles.add(condP);
            }
            synchronized (links) {
                links.add(new Link(p, condP, 128, Link.Type.COND));
            }
        }
        if (left != null) {
            Particle leftP = createParticlesFromTree(left, false);
            synchronized (particles) {
                particles.add(leftP);
            }
            Link.Type type = tree.getName().equals("IF") ? Link.Type.TRUE : Link.Type.NORMAL;
            synchronized (links) {
                links.add(new Link(p, leftP, 128, type));
            }

        }
        if (right != null) {
            Particle rightP = createParticlesFromTree(right, false);
            synchronized (particles) {
                particles.add(rightP);
            }
            Link.Type type = tree.getName().equals("IF") ? Link.Type.FALSE : Link.Type.NORMAL;
            synchronized (links) {
                links.add(new Link(p, rightP, 128, type));
            }
        }
        return p;
    }

    public void recrush(){
        synchronized (particles){
            for (Particle p : particles) {
                if(p.isRoot()){
                    p.x = 0;
                    p.y = 0;
                    p.oldy = 0;
                    p.oldx = 0;
                } else {
                    p.x = (float) (2 * Math.random() - 1) * 64.0f;
                    p.y = (float) (2 * Math.random() - 1) * 64.0f;
                    p.oldx = p.x;
                    p.oldy = p.y;
                    p.setFixed(false);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        float centerX = getWidth() / 2.0f + offsetX;
        float centerY = getHeight() / 2.0f + offsetY;

        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.lightGray);
        g.drawLine(0, (int) centerY, getWidth(), (int) centerY);
        g.drawLine((int) centerX, 0, (int) centerX, getHeight());

        synchronized (links) {
            for (Link li : links) {
                li.paint(g, centerX, centerY, zoom);
            }
        }
        synchronized (particles) {
            for (Particle p : particles) {
                p.paint(g, centerX, centerY, zoom);
            }
        }

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

    public void moveSelected(){
        if (selected != null) {
            float centerX = getWidth() / 2.0f + offsetX;
            float centerY = getHeight() / 2.0f + offsetY;
            float x = (mouseX - centerX) / zoom;
            float y = (mouseY - centerY) / zoom;
            selected.x = x;
            selected.y = y;
            selected.oldx = x;
            selected.oldy = y;
        }
    }

    public void update(float delta) {
        moveSelected();
        updateParticlesPos(delta);
        for (int i = 0; i < 10; i++)
            updateLinks();
        repelParticles(delta);
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
        if (selected == null) {
            offsetX += mouseX - mx;
            offsetY += mouseY - my;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float centerX = getWidth() / 2.0f + offsetX;
        float centerY = getHeight() / 2.0f + offsetY;
        float mx = (e.getX() - centerX) / zoom;
        float my = (e.getY() - centerY) / zoom;
        zoom -= zoom * e.getWheelRotation() / 10.0f;
        if (zoom <= 0.1f)
            zoom = 0.1f;
        else if (zoom >= 100.0f)
            zoom = 100.0f;
        float newmx = (e.getX() - centerX) / zoom;
        float newmy = (e.getY() - centerY) / zoom;
        offsetX += (newmx - mx) * zoom;
        offsetY += (newmy - my) * zoom;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        float centerX = getWidth() / 2.0f + offsetX;
        float centerY = getHeight() / 2.0f + offsetY;
        synchronized (particles) {
            for (Particle p : particles) {
                float sx = p.x * zoom + centerX;
                float sy = p.y * zoom + centerY;
                if ((mouseX - sx) * (mouseX - sx) + (mouseY - sy) * (mouseY - sy) <= (p.r * zoom * p.r * zoom)) {
                    selected = p;
                    p.setFixed(true);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if(selected != null){
            if(e.getButton() == MouseEvent.BUTTON1)
                selected.setFixed(false);
            selected = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
