package code.generic;

import java.util.Comparator;

public class SearchTreeNode implements Comparable<SearchTreeNode> {

    //state
    public String state;
    //parent
    public SearchTreeNode parent;
    //operator applied to generate node, encoded in int
    public byte operator;
    //depth in tree
    public int depth;
    //cost from root
    public int cost;
    public int heuristic1;
    public int heuristic2;

    public SearchTreeNode(String state, SearchTreeNode parent, byte operator, int depth, int cost) {
        this.state = state;
        this.parent = parent;
        this.operator = operator;
        this.depth = depth;
        this.cost = cost;

    }

    @Override
    public int compareTo(SearchTreeNode o) {
        return this.cost - o.cost;
    }

    public static Comparator<SearchTreeNode> COST = new Comparator<SearchTreeNode>() {
        @Override
        public int compare(SearchTreeNode o1, SearchTreeNode o2) {

            return o1.cost - o2.cost;
        }
    };
    public static Comparator<SearchTreeNode> H1 = new Comparator<SearchTreeNode>() {
        @Override
        public int compare(SearchTreeNode o1, SearchTreeNode o2) {

            return o1.heuristic1 - o2.heuristic1;
        }
    };

    public static Comparator<SearchTreeNode> H2 = new Comparator<SearchTreeNode>() {
        @Override
        public int compare(SearchTreeNode o1, SearchTreeNode o2) {

            return o1.heuristic2 - o2.heuristic2;
        }
    };
    public static Comparator<SearchTreeNode> H1_COST = new Comparator<SearchTreeNode>() {
        @Override
        public int compare(SearchTreeNode o1, SearchTreeNode o2) {

            return (o1.heuristic1+o1.cost) - (o2.heuristic1+o2.cost);
        }
    };

    public static Comparator<SearchTreeNode> H2_COST = new Comparator<SearchTreeNode>() {
        @Override
        public int compare(SearchTreeNode o1, SearchTreeNode o2) {

            return (o1.heuristic2+o1.cost) - (o2.heuristic2+o2.cost);
        }
    };
}
