
import java.util.*;
import java.io.*;

public class proj2 {
    static final int CONST9 = 9;
    static ArrayList<String> pretrav = new ArrayList<String>();
    static ArrayList<String> posttrav = new ArrayList<String>();
    static ArrayList<String> relationship = new ArrayList<String>();

    boolean foundAll = false;
    boolean foundRelation = false;
    int countAll = 0;
    int countRelation = 0;

    public proj2() {
        Scanner console = new Scanner(System.in);
        System.out.print("Enter a filename or Q to quit: ");
        String filename = console.next().toLowerCase();

        Scanner input = null;
        PrintStream output = null;
        while (!(filename.equals("q"))) {
            if (filename.endsWith("input.txt")) {
                input = getInputScanner(filename);
                if (input != null) {
                    output = getOutputPrintStream(console, filename);
                    if (output != null) {
                        process(input);
                        Tree tree = new Tree();
                        Node build = tree.buildTree(pretrav.size(), 0, 0);

                        while (!relationship.isEmpty()) {
                            String a = relationship.remove(0);
                            String b = relationship.remove(0);
                            int node1;
                            int node2;
                            ArrayList<String> l = tree.first(build, a);
                            node1 = tree.second(build, b, l);
                            ArrayList<String> l2 = tree.first(build, b);
                            node2 = tree.second(build, a, l2);
                            output.println(tree.relationships(node1, node2, a, b));

                        }

                        tree.levelOrder(build, output);

                    }
                }
            } else {
                System.out.println("Invalid filename");
            }
            System.out.print("Enter a filename or Q to quit: ");
            filename = console.next().toLowerCase();
        }
    }

    public static void main(String[] args) {
        new proj2();
    }

    // Returns Scanner for an input file
    // Returns null if the file does not exist
    /**
     * getInputScanner method is used to create a scanner of the input file the user
     * entered. It will return null and a FileNotFoundException if the user enters a
     * file that does not exist.
     *
     *
     * @param filename The string is the filename that you prompted the user to
     *                 enter
     * @return returns Scanner of the filename or null if there is no such file.
     */
    public Scanner getInputScanner(String filename) {
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return fileScanner;
    }

    // Returns PrintStream for an output file
    // If the output file already exists, asks the user if it is OK to overwrite the
    // file
    // If it is not OK to overwrite the file or a FileNotFoundException occurs, null
    // is
    // returned instead of a PrintStream
    /**
     * getOutputPrintStream method is used to create a output files with the
     * opposite extentions. If the file already exists then it will prompt the user
     * if they want to overwrite the file If they respond no then the output will
     * return null. If file is unable to be written then it will prompt the user of
     * the error
     *
     * @param console  scanner that is used to prompt user to answer if they want to
     *                 overwrite file
     * @param filename file that the user inputted and depending extention, it is
     *                 used to strip and put on the opposite extention for the
     *                 output file.
     * @return returns PrintStream of the filename with the opposite extention or
     *         null if file could not be written
     */
    public PrintStream getOutputPrintStream(Scanner console, String filename) {
        PrintStream output = null;
        if (filename.endsWith("input.txt")) {
            filename = filename.substring(0, filename.length() - CONST9);
            filename = filename + "output.txt";

        }
        File file = new File(filename);
        try {
            if (!file.exists()) {
                output = new PrintStream(file);
            } else {
                System.out.print(filename + " exists - OK to overwrite(y,n)?: ");
                String reply = console.next().toLowerCase();
                if (reply.startsWith("y")) {
                    output = new PrintStream(file);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File unable to be written " + e);
        }
        return output;
    }

    public void process(Scanner input) {
        String word = "";
        while (input.hasNextLine()) {
            String line = input.nextLine();
            Scanner lineScan = new Scanner(line);
            if (lineScan.hasNext()) {
                String first = lineScan.next();
                if (first.equals("<")) {
                    while (lineScan.hasNext()) {
                        word = lineScan.next();
                        pretrav.add(word.substring(0, 1));
                    }
                } else if (first.equals(">")) {
                    while (lineScan.hasNext()) {
                        word = lineScan.next();
                        posttrav.add(word.substring(0, 1));
                    }
                } else if (first.equals("?")) {
                    while (lineScan.hasNext()) {
                        word = lineScan.next();
                        relationship.add(word.substring(0, 1));
                    }
                }
            }
        }
    }

    public class Tree {
        Node root;

        public Tree() {
            root = null;
        }

        public Node buildTree(int size, int pre, int post) {

            Node n = new Node(pretrav.get(pre));
            if (pre == 0) {
                root = n;
            }
            if (size == 1) {
                Node lonelyNode = new Node(posttrav.get(post));
                return lonelyNode;
            }

            int oldPre = pre + 1;
            String comp = pretrav.get(oldPre);
            int count = 0;
            int children = 0;
            for (int i = post; i < post + size; i++) {
                count++;
                if (comp.equals(posttrav.get(i))) {

                    oldPre += count;
                    count = 0;
                    children++;
                    if (oldPre >= pretrav.size()) {
                        break;
                    }
                    comp = pretrav.get(oldPre);
                }
            }

            Node[] childs = new Node[children];

            for (Node each : childs) {
                int prestart = pre + 1;
                int poststart = post;
                int subtree = 1;
                for (int i = post; i < pretrav.size(); i++) {
                    if (posttrav.get(poststart).equals(pretrav.get(prestart))) {
                        break;
                    }
                    poststart++;
                    subtree++;
                }

                each = buildTree(subtree, prestart, post);
                pre = pre + subtree;
                post = post + subtree;

                n.children.add(each);
                each.parent = n;

            }

            return n;
        }

        public String relationships(int A, int B, String node1, String node2) {
            String relat = "";
            if (A == 0 && B == 0) {
                relat = node1 + " is " + node2;
            }
            if (A == 0 && B == 1) {
                relat = node1 + " is " + node2 + "'s parent";
            }
            if (A == 0 && B == 2) {
                relat = node1 + " is " + node2 + "'s grandparent";
            }
            if (A == 0 && B > 3) {
                relat = (node1 + " is " + node2 + "'s " + (B - 2) + "great-grandparent");
            }
            if (A == 1 && B == 0) {
                relat = node1 + " is " + node2 + "'s child";
            }
            if (A == 2 && B == 0) {
                relat = node1 + " is " + node2 + "'s grandchild";
            }
            if (A >= 3 && B == 0) {
                relat = (node1 + " is " + node2 + "'s " + (A - 2) + "great-grandchild");
            }
            if (A == 1 && B == 1) {
                relat = node1 + " is " + node2 + "'s sibling";
            }
            if (A == 1 && B == 2) {
                relat = node1 + " is " + node2 + "'s aunt/uncle";
            }
            if (A == 1 && B >= 2) {
                relat = (node1 + " is " + node2 + "'s " + (B - 2) + "great-aunt/uncle");
            }
            if (A == 2 && B == 1) {
                relat = node1 + " is " + node2 + "'s niece/nephew";
            }
            if (A >= 2 && B == 1) {
                relat = (node1 + " is " + node2 + "'s " + (A - 2) + "great-niece/nephew");
            }
            if (A >= 2 && B >= 2) {
                relat = (node1 + " is " + node2 + "'s " + (Math.min(B, A) - 1) + "th cousin" + Math.abs(A - B)
                        + " times removed.");
            }
            return relat;
        }

        public int second(Node n, String value, ArrayList<String> list) {
            ArrayList<String> list2 = new ArrayList<String>();
            Node node = lookup(n, value);
            int count = 0;
            while (node != null) {
                list2.add(node.data);
                node = node.parent;
            }
            for (int i = 0; i < list2.size(); i++) {
                String letter = list2.get(i);
                if (list.contains(letter)) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).equals(letter)) {
                            return count;
                        }
                        count++;
                    }
                }
            }
            return count;
        }

        public ArrayList<String> first(Node n, String value) {
            ArrayList<String> list = new ArrayList<String>();
            Node node = lookup(n, value);
            while (node != null) {
                list.add(node.data);
                node = node.parent;
            }
            return list;

        }

        public void postOrder(Node n) {
            for (Node each : n.children) {
                postOrder(each);
            }
            System.out.print(n.data);

        }

        public void print(Node n) {
            for (Node each : n.children) {
                print(each);
            }
            System.out.println("Node: " + n.data + " Mark: " + n.mark);
        }

        public Node lookup(Node n, String value) {
            if (n.data.equals(value)) {
                return n;
            }
            for (Node each : n.children) {
                if (each.data.equals(value)) {
                    return each;
                }
            }
            return n;
        }

        public void levelOrder(Node n, PrintStream out) {
            Queue<Node> q = new LinkedList<Node>();
            if (n == null) {
                return;
            }
            q.add(n);
            while (true) {
                int size = q.size();
                if (size == 0) {
                    break;
                }
                while (size > 0) {
                    Node node = q.peek();
                    if (node == null) {
                        break;
                    }
                    out.print(node.data + " ");
                    q.remove();
                    for (Node each : node.children) {
                        q.add(each);
                    }
                }
                System.out.println();
            }
        }
    }

    public class Node {
        public ArrayList<Node> children;
        public String data;
        public int mark;
        public Node parent = null;

        public Node(String element) {
            data = element;
            mark = 0;
            children = new ArrayList<Node>();
        }

    }
}