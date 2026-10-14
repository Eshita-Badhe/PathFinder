/*
MiniProject

Group:
UCE2026001
UCE2026002
UCE2026004
UCE2026007

Problem Statement: PathFinder – Graph-Based Math Puzzle Game
PathFinder is designed as a Java-based, graph-driven mathematical puzzle game consisting of progressively challenging levels, 
each containing set of questions. 
The game represents questions and locations as nodes and possible movements as directed edges, 
using an adjacency list to store graph connections. 
Players navigate through the graph by selecting answers, with correct and incorrect choices leading to different paths, 
dead ends, or the final destination. 
The system also incorporates hints, score tracking, and a learning mode that demonstrates graph visualization, node searching, 
Breadth-First Search (BFS), Depth-First Search (DFS), and shortest-path finding, making graph algorithms and mathematical 
problem-solving interactive and engaging.
*/


package Miniproject;
import java.util.*;
import java.io.*;

public class PathFinder {
    static class Node {
        String id, type, question;
        String[] options;
        int[] nextNodes;
        int correctOption;
        String hint;

        Node(String id, String type, String question, String[] options, int[] nextNodes, int correctOption, String h) {
            this.id = id;
            this.type = type;
            this.question = question;
            this.options = options;
            this.nextNodes = nextNodes;
            this.correctOption = correctOption;
            this.hint = h;
        }
    }

    static class Level {
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
        int start = 0;
        int destination;

        void addNode(Node node) {
            nodes.add(node);
            graph.add(new ArrayList<Integer>());
        }

        void buildGraph() {
            for (int i = 0; i < nodes.size(); i++) {
                for (int next : nodes.get(i).nextNodes) {
                    if (!graph.get(i).contains(next)) graph.get(i).add(next);
                }
            }
            destination = nodes.size() - 1;
        }
    }

    static Scanner sc = new Scanner(System.in);
    static int totalScore = 0;
    static int completedLevels = 0;

    static Map<String, String> readForm() {
        Map<String, String> form = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Miniproject/pathfinder_menu.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                form.put(parts[0].trim().toUpperCase(), parts[1].trim());
            }
        } catch (IOException e) {
            System.out.println("Could not read pathfinder_menu.txt.");
            System.out.println("Keep it in the same folder as PathFinder.java and run the program from that folder.");
        }
        return form;
    }


    static Level createLevel(int levelNumber) {
        Level level = new Level();
        if (levelNumber == 1) {
                level.addNode(new Node("A", "START", "What is 5 * 3?", new String[]{"15", "10", "20"}, new int[]{1, 2, 3}, 0, "Multiply 5 by 3, or add 5 three times."));
                level.addNode(new Node("B", "RIDDLE", "What is 12 / 3?", new String[]{"4", "5", "6"}, new int[]{4, 2, 3}, 0, "Think about how many groups of 3 make 12."));
                level.addNode(new Node("C", "RIDDLE", "What is 2 raised to the power 3?", new String[]{"6", "8", "10"}, new int[]{3, 4, 3}, 1, "Multiply 2 by itself three times: 2 * 2 * 2."));
                level.addNode(new Node("D", "DEAD END", "", new String[]{}, new int[]{}, -1, ""));
                level.addNode(new Node("E", "RIDDLE", "What is 7 + 6?", new String[]{"12", "13", "14"}, new int[]{3, 5, 3}, 1, "Start from 7 and count forward 6 steps."));
                level.addNode(new Node("F", "DESTINATION", "", new String[]{}, new int[]{}, -1, ""));
        } else if (levelNumber == 2) {
                level.addNode(new Node("A", "START", "What is (6 * 4) - 5?", new String[]{"19", "24", "21"}, new int[]{1, 2, 3}, 0, "Multiply 6 by 4 first, then subtract 5."));
                level.addNode(new Node("B", "RIDDLE", "What is 3 squared + 4?", new String[]{"12", "13", "14"}, new int[]{2, 4, 3}, 1, "Square 3 first, then add 4."));
                level.addNode(new Node("C", "RIDDLE", "What is 25% of 80?", new String[]{"15", "20", "25"}, new int[]{3, 4, 3}, 1, "25% means one-quarter. Divide 80 by 4."));
                level.addNode(new Node("D", "DEAD END", "", new String[]{}, new int[]{}, -1, ""));
                level.addNode(new Node("E", "RIDDLE", "If x + 7 = 19, what is x?", new String[]{"10", "11", "12"}, new int[]{3, 6, 5}, 2, "Subtract 7 from both sides of the equation."));
                level.addNode(new Node("F", "RIDDLE", "What is the next number: 2, 6, 12, 20, __?", new String[]{"28", "30", "32"}, new int[]{3, 6, 4}, 1, "Look at the differences: 4, 6, 8. What comes next?"));
                level.addNode(new Node("G", "DESTINATION", "", new String[]{}, new int[]{}, -1, ""));
        } else {
                level.addNode(new Node("A", "START", "What is 15% of 200?", new String[]{"25", "30", "35"}, new int[]{1, 2, 3}, 1, "10% of 200 is 20, and 5% is half of that."));
                level.addNode(new Node("B", "RIDDLE", "A number doubled and increased by 9 is 25. What is it?", new String[]{"7", "8", "9"}, new int[]{2, 5, 3}, 1, "Subtract 9 from 25, then divide the result by 2."));
                level.addNode(new Node("C", "RIDDLE", "What is the square root of 196?", new String[]{"12", "14", "16"}, new int[]{4, 6, 3}, 1, "Find the number that multiplied by itself gives 196."));
                level.addNode(new Node("D", "DEAD END", "", new String[]{}, new int[]{}, -1, ""));
                level.addNode(new Node("E", "RIDDLE", "What is 2/3 of 30?", new String[]{"10", "15", "20"}, new int[]{3, 6, 5}, 2, "Divide 30 by 3, then multiply by 2."));
                level.addNode(new Node("F", "RIDDLE", "If 5x = 45, what is x?", new String[]{"8", "9", "10"}, new int[]{3, 6, 7}, 1, "Divide both sides by 5."));
                level.addNode(new Node("G", "RIDDLE", "What is 11 squared?", new String[]{"111", "121", "131"}, new int[]{3, 7, 5}, 1, "Multiply 11 by 11."));
                level.addNode(new Node("H", "DESTINATION", "", new String[]{}, new int[]{}, -1, ""));
        }
        level.buildGraph();
        return level;
    }

    static int searchNode(Level level, String id) {
        for (int i = 0; i < level.nodes.size(); i++) {
            if (level.nodes.get(i).id.equalsIgnoreCase(id)) return i;
        }
        return -1;
    }


static ArrayList<Integer> bfs(Level level, int start, int destination) {
    boolean[] visited = new boolean[level.nodes.size()];
    int[] parent = new int[level.nodes.size()];
    Arrays.fill(parent, -1);

    Queue<Integer> queue = new LinkedList<>();
    ArrayList<Integer> path = new ArrayList<>();

    queue.add(start);
    visited[start] = true;

    System.out.println("\n--- BFS STEP-BY-STEP ---");
    System.out.println("BFS uses a Queue (FIFO).");
    System.out.println("Start node: " + level.nodes.get(start).id);
    System.out.println("Queue: " + level.nodes.get(start).id);

    int step = 1;

    while (!queue.isEmpty()) {
        System.out.println("\nStep " + step++);

        int current = queue.remove();
        String currentId = level.nodes.get(current).id;

        System.out.println("Dequeue node: " + currentId);

        if (current == destination) {
            System.out.println("Destination reached!");
            break;
        }

        System.out.println("Checking neighbours of " + currentId + ":");

        for (int next : level.graph.get(current)) {
            String nextId = level.nodes.get(next).id;

            if (!visited[next]) {
                visited[next] = true;
                parent[next] = current;
                queue.add(next);

                System.out.println(
                    "  " + nextId + " is unvisited."
                );
                System.out.println(
                    "  Mark visited, set parent[" + nextId
                    + "] = " + currentId
                );
                System.out.println("  Enqueue " + nextId);
            } else {
                System.out.println(
                    "  " + nextId + " is already visited. Skip."
                );
            }
        }

        System.out.print("Queue now: ");
        printQueue(level, queue);

        System.out.print("Visited nodes: ");
        printVisited(level, visited);
    }

    if (!visited[destination]) {
        System.out.println("\nDestination is unreachable.");
        return path;
    }

    System.out.println("\nReconstructing path using parent array:");

    for (int current = destination;
         current != -1;
         current = parent[current]) {
        path.add(current);
    }

    Collections.reverse(path);

    System.out.print("Shortest path: ");
    displayPath(level, path);

    return path;
}

static void dfs(Level level, int start) {
    boolean[] visited = new boolean[level.nodes.size()];
    Stack<Integer> stack = new Stack<>();

    stack.push(start);

    System.out.println("\n--- DFS STEP-BY-STEP ---");
    System.out.println("DFS uses a Stack (LIFO).");
    System.out.println("Start node: " + level.nodes.get(start).id);

    System.out.print("Initial stack: ");
    printStack(level, stack);

    int step = 1;

    while (!stack.isEmpty()) {
        System.out.println("\nStep " + step++);

        int current = stack.pop();
        String currentId = level.nodes.get(current).id;

        System.out.println("Pop node: " + currentId);

        if (visited[current]) {
            System.out.println(
                "Already visited. Skip processing."
            );
            continue;
        }

        visited[current] = true;

        System.out.println("Visit node: " + currentId);

        if (level.nodes.get(current).type.equals("DEAD END")) {
            System.out.println("This node is a DEAD END.");
        } else if (level.nodes.get(current).type.equals("DESTINATION")) {
            System.out.println("This node is the DESTINATION.");
        }

        System.out.println("Checking neighbours of " + currentId + ":");

        for (int i = level.graph.get(current).size() - 1;
             i >= 0; i--) {

            int next = level.graph.get(current).get(i);
            String nextId = level.nodes.get(next).id;

            if (!visited[next]) {
                stack.push(next);
                System.out.println("  Push " + nextId);
            } else {
                System.out.println(
                    "  " + nextId + " already visited. Skip."
                );
            }
        }

        System.out.print("Stack now: ");
        printStack(level, stack);

        System.out.print("Visited nodes: ");
        printVisited(level, visited);
    }

    System.out.println("\nDFS traversal completed.");
}


static void printQueue(Level level, Queue<Integer> queue) {
    System.out.print("[");
    boolean first = true;

    for (int node : queue) {
        if (!first) System.out.print(", ");
        System.out.print(level.nodes.get(node).id);
        first = false;
    }

    System.out.println("]");
}

static void printStack(Level level, Stack<Integer> stack) {
    System.out.print("[Bottom ");

    for (int i = 0; i < stack.size(); i++) {
        System.out.print(level.nodes.get(stack.get(i)).id);

        if (i < stack.size() - 1) {
            System.out.print(", ");
        }
    }

    System.out.println(" Top]");
}

static void printVisited(Level level, boolean[] visited) {
    System.out.print("{");

    boolean first = true;

    for (int i = 0; i < visited.length; i++) {
        if (visited[i]) {
            if (!first) System.out.print(", ");

            System.out.print(level.nodes.get(i).id);
            first = false;
        }
    }

    System.out.println("}");
}

    static void displayPath(Level level, ArrayList<Integer> path) {
        if (path.isEmpty()) {
            System.out.println("No route to the destination exists.");
            return;
        }
        for (int i = 0; i < path.size(); i++) {
            System.out.print(level.nodes.get(path.get(i)).id);
            if (i < path.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
    }

    static void displayGraph(Level level) {
        System.out.println("\nGraph Adjacency List:");
        for (int i = 0; i < level.nodes.size(); i++) {
            System.out.print(level.nodes.get(i).id + " (" + level.nodes.get(i).type + ") -> ");
            for (int next : level.graph.get(i)) System.out.print(level.nodes.get(next).id + " ");
            System.out.println();
        }
    }

    static void playGame(String playerName, int levelNumber) {
        Level level = createLevel(levelNumber);
        int current = level.start, score = 0, moves = 0, hints = 0;
        ArrayList<Integer> playerPath = new ArrayList<>();
        boolean won = false;
        while (true) {
            Node node = level.nodes.get(current);
            if (playerPath.isEmpty() || playerPath.get(playerPath.size() - 1) != current) playerPath.add(current);
            System.out.println("\n--------------------------------");
            System.out.println("Player: " + playerName + " | Level: " + levelNumber);
            System.out.println("Current node: " + node.id + " | Score: " + score + " | Moves: " + moves);
            if (node.type.equals("DEAD END")) {
                System.out.println("You reached a DEAD END. This attempt is over.");
                break;
            }
            if (node.type.equals("DESTINATION")) {
                System.out.println("Congratulations! You reached the DESTINATION and won!");
                score += Math.max(10, 50 - moves * 3 - hints * 5);
                won = true;
                break;
            }
            System.out.println("Riddle: " + node.question);
            for (int i = 0; i < node.options.length; i++) System.out.println((i + 1) + ". " + node.options[i]);
            System.out.println("H. Hint (-5 points)   P. Show path   Q. Quit level");
            System.out.print("Choose an answer: ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Q")) break;
            if (input.equalsIgnoreCase("P")) {
                System.out.print("Your current path: ");
                displayPath(level, playerPath);
                continue;
            }
            if (input.equalsIgnoreCase("H")) {
                if (node.hint != null && !node.hint.isEmpty()) {
                    System.out.println("Math Hint: " + node.hint);
                    score = Math.max(0, score - 5);
                    hints++;
                } else {
                    System.out.println("No hint available for this node.");
                }
                continue;
            }
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid option number.");
                continue;
            }
            if (choice < 1 || choice > node.options.length) {
                System.out.println("Invalid answer choice.");
                continue;
            }
            int next = node.nextNodes[choice - 1];
            moves++;
            if (choice - 1 == node.correctOption) {
                System.out.println("Correct answer! +10 points.");
                score += 10;
            } else {
                System.out.println("Wrong answer. Taking an alternate path to node " + level.nodes.get(next).id + ".");
                score = Math.max(0, score - 2);
            }
            current = next;
        }
        System.out.print("\nYour path: ");
        displayPath(level, playerPath);
        System.out.println("Moves: " + moves + " | Hints used: " + hints);
        System.out.println("Level score: " + score);
        totalScore += score;
        if (won) completedLevels++;
        System.out.println("Total score: " + totalScore);
        if (!won) System.out.println("Choose Play again from the form file to restart this level.");
    }


static void learn(int levelNumber) {
    Level level = createLevel(levelNumber);

    while (true) {
        System.out.println("\n================================");
        System.out.println("       LEARN PATHFINDER");
        System.out.println("================================");
        System.out.println("Selected Level: " + levelNumber);
        System.out.println("1. Display Level Graph");
        System.out.println("2. Display Adjacency List");
        System.out.println("3. Demonstrate BFS");
        System.out.println("4. Demonstrate DFS");
        System.out.println("5. Search for a Node");
        System.out.println("6. Find Shortest Route");
        System.out.println("7. Learn Graph Concepts");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");

        String input = sc.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Exiting learning mode.");
            return;
        }

        if (!input.matches("[1-7]")) {
            System.out.println("Invalid choice. Try again.");
            continue;
        }

        int choice = Integer.parseInt(input);

        switch (choice) {
            case 1:
                System.out.println("\n--- GAME GRAPH ---");
                displayGraph(level);
                break;

            case 2:
                System.out.println("\n--- ADJACENCY LIST ---");
                displayGraph(level);
                break;

            case 3:
                System.out.println("\n--- BFS DEMONSTRATION ---");

                ArrayList<Integer> bfsPath =
                    bfs(level, level.start, level.destination);

                if (bfsPath.isEmpty()) {
                    System.out.println(
                        "Destination is unreachable."
                    );
                } else {
                    System.out.print("BFS shortest route: ");
                    displayPath(level, bfsPath);
                }
                break;

            case 4:
                System.out.println("\n--- DFS DEMONSTRATION ---");
                dfs(level, level.start);
                break;

            case 5:
                System.out.print("Enter node ID (A, B, C...): ");
                String id = sc.nextLine().trim();

                int index = searchNode(level, id);

                if (index != -1) {
                    Node node = level.nodes.get(index);

                    System.out.println("Node found!");
                    System.out.println("ID: " + node.id);
                    System.out.println("Type: " + node.type);

                    if (!node.question.isEmpty()) {
                        System.out.println(
                            "Question: " + node.question
                        );
                    }

                    System.out.println("Connected nodes: ");

                    for (int next : level.graph.get(index)) {
                        System.out.print(
                            level.nodes.get(next).id + " "
                        );
                    }

                    System.out.println();
                } else {
                    System.out.println("Node not found.");
                }
                break;


            case 6:
                System.out.println("\n--- FIND SHORTEST ROUTE USING BFS ---");

                System.out.print("Enter source node ID (A, B, C...): ");
                String sourceId = sc.nextLine().trim();

                System.out.print("Enter destination node ID (A, B, C...): ");
                String destinationId = sc.nextLine().trim();

                int source = searchNode(level, sourceId);
                int destination = searchNode(level, destinationId);

                if (source == -1 || destination == -1) {
                    System.out.println("Invalid source or destination node ID.");
                    break;
                }

                System.out.println(
                    "\nFinding shortest path from "
                    + level.nodes.get(source).id
                    + " to "
                    + level.nodes.get(destination).id
                );

                ArrayList<Integer> route = bfs(level, source, destination);

                if (!route.isEmpty()) {
                    System.out.println("Shortest route found!");
                    System.out.println(
                        "Number of edges: " + (route.size() - 1)
                    );
                } else {
                    System.out.println(
                        "No path exists between the selected nodes."
                    );
                }
                break;

            case 7:
                System.out.println("\n--- GRAPH CONCEPTS ---");
                System.out.println(
                    "Vertex: Each question or special location."
                );
                System.out.println(
                    "Edge: A connection between two nodes."
                );
                System.out.println(
                    "Directed graph: Edges represent directional movement."
                );
                System.out.println(
                    "Adjacency list: Stores each node's neighbours."
                );
                System.out.println(
                    "BFS: Explores nodes using a queue."
                );
                System.out.println(
                    "DFS: Explores nodes using a stack."
                );
                System.out.println(
                    "Path: A sequence of connected nodes."
                );
                System.out.println(
                    "Reachability: Whether a node can be reached."
                );
                System.out.println(
                    "Dead end: A node where the attempt terminates."
                );
                System.out.println(
                    "Gameplay movement follows the selected answer's "
                    + "predefined nextNodes entry."
                );
                break;
        }
    }
}

    public static void main(String[] args) {
        Map<String, String> form = readForm();
        String playerName = form.getOrDefault("PLAYER_NAME", "Player");
        String action = form.getOrDefault("ACTION", "PLAY").toUpperCase();
        int levelNumber;
        try {
            levelNumber = Integer.parseInt(form.getOrDefault("LEVEL", "1"));
        } catch (NumberFormatException e) {
            levelNumber = 1;
        }
        System.out.println("===== PATHFINDER =====");
        System.out.println("Player: " + playerName);
        System.out.println("Action selected in pathfinder_menu.txt: " + action);
        if (action.equals("LEARN")) {
            if (action.equals("LEARN")) {
                if (levelNumber < 1 || levelNumber > 3) {
                    System.out.println(
                        "Invalid level. Set LEVEL to 1, 2, or 3."
                    );
                } else {
                    learn(levelNumber);
                }
            }
        } else if (action.equals("PLAY")) {
            if (levelNumber < 1 || levelNumber > 3) {
                System.out.println("Invalid level in pathfinder_menu.txt. Set LEVEL to 1, 2, or 3.");
            } else {
                playGame(playerName, levelNumber);
            }
        } else if (action.equals("EXIT")) {
            System.out.println("PathFinder closed from the form settings.");
        } else {
            System.out.println("Invalid ACTION. Set ACTION to PLAY, LEARN, or EXIT in pathfinder_menu.txt.");
        }
        sc.close();
    }
}
