package simulation;

public class Link {

    private final Particle p1;
    private final Particle p2;
    private final float restLength;

    public enum Type {
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

    public Type getType() {
        return type;
    }

    public Particle getP1() {
        return p1;
    }

    public Particle getP2() {
        return p2;
    }
}
