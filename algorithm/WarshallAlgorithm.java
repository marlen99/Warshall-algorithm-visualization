import java.util.*;

class WarshallAlgorithm {
    private Graph g;

    public WarshallAlgorithm(String edges) {
        setGraphData(edges);
    }

    public void setGraphData(String data) {
        Scanner s = new Scanner(data);
        s.useDelimiter(System.lineSeparator());
        String token;
        StringBuilder alp = new StringBuilder();
        Vector<Graph.Edge> edges = new Vector<Graph.Edge>();
        int i;
        char source, destination;
        while(s.hasNext(" *[a-z] +[a-z] *")) {
            token = s.next(" *[a-z] +[a-z] *");
            i = 0;
            while(Character.isSpaceChar(token.charAt(i)))
                ++i;
            source = token.charAt(i);
            if(alp.indexOf(String.valueOf(source)) == -1)
                alp.append(source);
            i++;
            while(Character.isSpaceChar(token.charAt(i)))
                ++i;
            destination = token.charAt(i);
            if(alp.indexOf(String.valueOf(destination)) == -1)
                alp.append(destination);
            edges.add(new Graph.Edge(source, destination));
        }
        g = new Graph(alp.toString().toCharArray(), edges);
    }

    public void transitiveClosure() {
        g.transitiveClosure();
    }

    public Graph.Edge[] getEdges() {
        return g.getEdges();
    }

    public String toString() {
        return g.toString();
    }
}
        
            
