import java.util.*;

public class Graph{
    public static class Edge{
        public char source;
        public char destination;
        public Edge(char source, char destination) {
            this.source = source;
            this.destination = destination;
        }
    }
    public class Memento {
        public String vertices;
        public BoolMatrix matr;
        public Memento(String vertices, BoolMatrix matr) {
            matr = new BoolMatrix(matr);
            this.vertices = new String(vertices);
        }
    }

    private String vertices;
    private BoolMatrix m;

    public Graph(char[] vertices) {
        this.vertices = String.valueOf(vertices);
        m = new BoolMatrix(vertices.length, vertices.length);
    }

    public Graph(char[] vertices, Collection<Edge> edges) {
        this.vertices = String.valueOf(vertices);
        m = new BoolMatrix(vertices.length, vertices.length);
        Iterator<Edge> i = edges.iterator();
        while(i.hasNext())
            add(i.next());
    }

    public void add(Edge edge) {
        m.set(vertices.indexOf(edge.source), vertices.indexOf(edge.destination), true);
    }

    public int verticesCount() {
        return vertices.length();
    }

    public char getVertex(int Index) {
        return vertices.charAt(Index);
    }

    public Edge[] getEdges() {
        Vector<Edge> edges = new Vector<Edge>();
        for(int i=0; i<vertices.length(); ++i)
            for(int j=0; j<vertices.length(); ++j)
                if(m.get(i, j))
                    edges.add(new Edge(vertices.charAt(i), vertices.charAt(j)));
        return (Edge[])edges.toArray();
    }

    public BoolMatrix getMatrix() {
        return new BoolMatrix(m);
    }

    public Memento save() {
        return new Memento(vertices, m);
    }

    public void restore(Memento state) {
        m = new BoolMatrix(state.matr);
        vertices = new String(state.vertices);
    }

    public String toString() {
        return m.toString();
    }

    public void transitiveClosure() {
        BoolMatrix prev;
        do {
            prev = m;
            m = m.multiply(m);
            m.add(prev);
        } while(!m.equals(prev));
    }

    public boolean stepTransitiveClosure() {
        BoolMatrix prev;
        prev = m;
        m = m.multiply(m);
        m.add(prev);
        return prev.equals(m);
    }
}
