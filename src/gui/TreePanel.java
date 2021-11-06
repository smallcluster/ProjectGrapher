package gui;

import eval.Evaluator;
import eval.tree.Node;
import eval.tree.TestIF;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class TreePanel extends Animated2DView implements MouseListener {

    private final Evaluator evaluator;
    private final float DELTA = 1.0f / 60.0f;
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final ArrayList<Link> links = new ArrayList<>();
    private Particle selected = null;

    public TreePanel(Evaluator evaluator) {
        this.evaluator = evaluator;
        addMouseListener(this);
        basePixelsPerUnit = 32.0f;
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
                for (int j = i + 1; j < particles.size(); j++) {
                    Particle p1 = particles.get(i);
                    Particle p2 = particles.get(j);
                    p1.repel(p2, delta, 10.0f);
                }
            }
        }
    }

    public void updateParticlesPos() {
        synchronized (particles) {
            for (Particle p : particles) {
                p.update();
            }
        }
    }

    public void updateLinks() {
        // update constraints
        synchronized (links) {
            // links us
            for (Link li : links)
                li.update();
        }
    }

    public Particle createParticlesFromTree(Node tree, boolean root) {
        Particle p;
        if (root) {
            p = new Particle(tree.getName(), 0, 0, 0.5f);
            p.markAsRoot();
        } else {
            p = new Particle(tree.getName(), (float) (2 * Math.random() - 1), (float) (2 * Math.random() - 1), 0.5f);
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
                links.add(new Link(p, condP, 2, Link.Type.COND));
            }
        }
        if (left != null) {
            Particle leftP = createParticlesFromTree(left, false);
            synchronized (particles) {
                particles.add(leftP);
            }
            Link.Type type = tree.getName().equals("IF") ? Link.Type.TRUE : Link.Type.NORMAL;
            synchronized (links) {
                links.add(new Link(p, leftP, 2, type));
            }

        }
        if (right != null) {
            Particle rightP = createParticlesFromTree(right, false);
            synchronized (particles) {
                particles.add(rightP);
            }
            Link.Type type = tree.getName().equals("IF") ? Link.Type.FALSE : Link.Type.NORMAL;
            synchronized (links) {
                links.add(new Link(p, rightP, 2, type));
            }
        }
        return p;
    }

    public void recrush() {
        synchronized (particles) {
            for (Particle p : particles) {
                if (p.isRoot()) {
                    p.x = 0;
                    p.y = 0;
                    p.oldy = 0;
                    p.oldx = 0;
                } else {
                    p.x = (float) (2 * Math.random() - 1);
                    p.y = (float) (2 * Math.random() - 1);
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

        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Axes
        g.setColor(Color.lightGray);
        g.drawLine(0, (int) getScreenY(0), getWidth(), (int) getScreenY(0));
        g.drawLine((int) getScreenX(0), 0, (int) getScreenX(0), getHeight());

        // lock links and particles while rendering
        synchronized (links) {
            drawLinks(g);
        }
        synchronized (particles) {
            drawParticles(g);
        }

        // Draw fps
        drawFps(g);
    }

    private void drawParticles(Graphics g) {
        for (Particle p : particles) {

            // do not draw particle if not visible
            if(!particleIsVisible(p))
                continue;

            if (p.isRoot())
                g.setColor(Color.red);
            else if (p.isFixed())
                g.setColor(Color.blue);
            else
                g.setColor(Color.black);
            g.fillOval((int) (getScreenX(p.x) - p.r * getPixelsPerUnit()),
                    (int) (getScreenY(p.y) - p.r * getPixelsPerUnit()),
                    (int) (p.r * 2 * getPixelsPerUnit()),
                    (int) (p.r * 2 * getPixelsPerUnit()));
            g.setColor(Color.white);
            Font font = new Font("TimesRoman", Font.PLAIN, (int) (0.375 * getPixelsPerUnit()));
            FontMetrics metrics = g.getFontMetrics(font);
            int tw = metrics.stringWidth(p.name);
            int th = metrics.getHeight() + (metrics.getDescent() - metrics.getAscent());
            g.setFont(font);
            g.drawString(p.name, (int) (getScreenX(p.x) - tw / 2.0), (int) (getScreenY(p.y) + th / 2.0));
        }
    }

    private boolean particleIsVisible(Particle p){
        return !(getScreenX(p.x)+p.r*getPixelsPerUnit() < 0 || getScreenX(p.x)-p.r*getPixelsPerUnit() > getWidth()
                || getScreenY(p.y)+p.r*getPixelsPerUnit() < 0 || getScreenY(p.y)-p.r*getPixelsPerUnit() > getHeight());
    }

    private void drawLinks(Graphics g) {
        for (Link li : links) {

            // do not draw link if not visible
            if(!particleIsVisible(li.getP1()) && !particleIsVisible(li.getP2()))
                continue;

            String info = "";
            switch (li.getType()) {
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
            g.drawLine((int) getScreenX(li.getP1().x), (int) getScreenY(li.getP1().y), (int) getScreenX(li.getP2().x), (int) getScreenY(li.getP2().y));

            if (!info.isEmpty()) {
                Font font = new Font("TimesRoman", Font.PLAIN, (int) (0.375 * getPixelsPerUnit()));
                FontMetrics metrics = g.getFontMetrics(font);
                int tw = metrics.stringWidth(info);
                int th = metrics.getHeight() + (metrics.getDescent() - metrics.getAscent());
                float x = (li.getP2().x + li.getP1().x) / 2.0f;
                float y = (li.getP2().y + li.getP1().y) / 2.0f;
                g.setFont(font);
                g.drawString(info, (int) (getScreenX(x) - tw / 2.0), (int) (getScreenY(y) + th / 2.0));
            }
        }
    }

    @Override
    public void updateLogic() {
        moveSelected();
        updateParticlesPos();
        for (int i = 0; i < 10; i++)
            updateLinks();
        repelParticles(DELTA);
    }

    public void moveSelected() {
        if (selected != null) {
            float x = getWorldX(prevMouseX);
            float y = getWorldY(prevMouseY);
            selected.x = x;
            selected.y = y;
            selected.oldx = x;
            selected.oldy = y;
        }
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        if (selected == null) {
            super.mouseDragged(e);
        } else {
            prevMouseX = e.getX();
            prevMouseY = e.getY();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
        synchronized (particles) {
            for (Particle p : particles) {
                float sx = getScreenX(p.x);
                float sy = getScreenY(p.y);
                if ((prevMouseX - sx) * (prevMouseX - sx) + (prevMouseY - sy) * (prevMouseY - sy) <= (p.r * getPixelsPerUnit() * p.r * getPixelsPerUnit())) {
                    selected = p;
                    p.setFixed(true);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
        if (selected != null) {
            if (e.getButton() == MouseEvent.BUTTON1)
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
