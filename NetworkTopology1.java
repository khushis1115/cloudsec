import java.io.*;
import java.util.*;

class Node {
    int id;
    List<Edge> edges;

    public Node(int id) {
        this.id = id;
        this.edges = new ArrayList<>();
    }
}

class Edge {
    Node from;
    Node to;
    double weight;

    public Edge(Node from, Node to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}

public class NetworkTopology1 {
    Map<Integer, Node> nodes = new HashMap<>();

    public void readTopologyFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            try {
                String[] parts = line.split("\\s+");
                if (parts.length > 6 && parts[6].equals("RT_NODE")) {
                    int nodeId = Integer.parseInt(parts[0]);
                    nodes.put(nodeId, new Node(nodeId));
                    System.out.println("Added node: " + nodeId);
                } else if (parts.length > 6 && parts[8].equals("E_RT")) {
                    int fromId = Integer.parseInt(parts[1]);
                    int toId = Integer.parseInt(parts[2]);
                    double weight = Double.parseDouble(parts[3]);
                    Node fromNode = nodes.get(fromId);
                    Node toNode = nodes.get(toId);
                    if (fromNode != null && toNode != null) {
                        fromNode.edges.add(new Edge(fromNode, toNode, weight));
                        System.out.println("Added edge from " + fromId + " to " + toId + " with weight " + weight);
                    } else {
                        System.out.println("Error: Node(s) not found for edge from " + fromId + " to " + toId);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Error: Line format is incorrect - " + line);
            }
        }
        br.close();
    }

    public void printNodes() {
        System.out.println("Nodes in the topology:");
        for (Integer nodeId : nodes.keySet()) {
            System.out.println("Node ID: " + nodeId);
        }
    }

    public void transferFile(int fromNodeId, int toNodeId, String filePath) {
        Node fromNode = nodes.get(fromNodeId);
        Node toNode = nodes.get(toNodeId);
        if (fromNode == null || toNode == null) {
            System.out.println("Invalid nodes specified for file transfer.");
            return;
        }

        // Simulating file transfer by displaying steps
        System.out.println("Starting file transfer from Node " + fromNodeId + " to Node " + toNodeId);
        transferFileRecursively(fromNode, toNode, new HashSet<>());
        System.out.println("File transfer completed.");
    }

    private void transferFileRecursively(Node currentNode, Node destinationNode, Set<Node> visitedNodes) {
        if (currentNode == destinationNode) {
            System.out.println("Reached destination Node " + destinationNode.id);
            return;
        }

        visitedNodes.add(currentNode);

        for (Edge edge : currentNode.edges) {
            if (!visitedNodes.contains(edge.to)) {
                System.out.println("Transferring through Node " + currentNode.id + " to Node " + edge.to.id);
                transferFileRecursively(edge.to, destinationNode, visitedNodes);
                return;
            }
        }

        System.out.println("No path found from Node " + currentNode.id + " to Node " + destinationNode.id);
    }

    public static void main(String[] args) {
        NetworkTopology1 nt = new NetworkTopology1();
        try {
            nt.readTopologyFile("C:\\Users\\amits\\OneDrive\\Documents\\NetBeansProjects\\CLOUD\\src\\topology1.brite");
            nt.printNodes(); // Print nodes to verify
            nt.transferFile(1, 2, "example.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
