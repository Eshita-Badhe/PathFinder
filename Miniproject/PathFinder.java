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
        queue.add(start);
        visited[start] = true;
        while (!queue.isEmpty()) {
            int current = queue.remove();
            if (current == destination) break;
            for (int next : level.graph.get(current)) {
                if (!visited[next]) {
                    visited[next] = true;
                    parent[next] = current;
                    queue.add(next);
                }
            }
        }
        ArrayList<Integer> path = new ArrayList<>();
        if (!visited[destination]) return path;
        for (int current = destination; current != -1; current = parent[current]) path.add(current);
        Collections.reverse(path);
        return path;
    }

    static void dfs(Level level, int start) {
        boolean[] visited = new boolean[level.nodes.size()];
        Stack<Integer> stack = new Stack<>();
        stack.push(start);
        System.out.print("DFS traversal: ");
        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (!visited[current]) {
                visited[current] = true;
                System.out.print(level.nodes.get(current).id + " ");
                for (int i = level.graph.get(current).size() - 1; i >= 0; i--) {
                    int next = level.graph.get(current).get(i);
                    if (!visited[next]) stack.push(next);
                }
            }
        }
        System.out.println();
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

    static void learn() {
        System.out.println("\n===== LEARN PATHFINDER =====");
        System.out.println("Goal: Start at A, solve math riddles, and reach DESTINATION.");
        System.out.println("A correct answer gives points and follows its mapped edge.");
        System.out.println("A wrong answer loses points and follows a different mapped edge.");
        System.out.println("Some edges lead to a DEAD END, where the attempt ends.");
        System.out.println("Adjacency List: stores the neighbours of each graph node.");
        System.out.println("BFS: uses a queue to find a route with the fewest edges.");
        System.out.println("DFS: uses a stack to explore reachable nodes.");
        System.out.println("ArrayList: stores nodes and the player's path.");
        System.out.println("Searching: finds a node by its ID.");
        System.out.println("Scoring: correct answers, wrong-answer penalties, hints, moves, and winning bonus.");
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
            learn();
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
