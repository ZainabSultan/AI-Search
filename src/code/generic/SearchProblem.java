package code.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public abstract class SearchProblem {
    //a code.generic search problem consists of 5 things:
    //operators
    byte[] operators;
    //initial state
    String initialState;
    //state space
    public abstract String StateSpace(String currentState, byte operator);
    //goal test
    public abstract boolean goalTest(String currentState);
    //path cost function
    public abstract int pathCost(SearchTreeNode node);
    //hashset to store repeated states
    HashSet<String> visitedStates;
    public int numNodesExpanded;

    public SearchProblem(byte[] operators, String initialState, HashSet<String> visitedStates, int  numNodesExpanded){
        this.operators=operators;
        this.initialState=initialState;
        this.visitedStates=visitedStates;
        this.numNodesExpanded =numNodesExpanded;
    }

     public static SearchTreeNode GeneralSearchProcedure(SearchProblem problem, String QING_FUN){
         //returns solution or failure
         //create a search tree node out of the initial state
         SearchTreeNode nodeOfInitialState = new SearchTreeNode(problem.initialState, null, (byte)-1, 0,0);//SearchTreeNode.TurnStateIntoNode(problem.initialState, null,(byte)-1,0,0);
         //create a queue (nodes)and add that node into it
         ArrayList<SearchTreeNode> nodes = new ArrayList<>();
         nodes.add(nodeOfInitialState);
         //if, queue is empty, you failed
         //loop here
         int depthCount=0;//need to perform search procedure as dfs but iterativerly,

         if(QING_FUN =="ID"){

             while(depthCount< Integer.MAX_VALUE){
                 SearchTreeNode solution = DepthLimitedSearch(problem, depthCount);
                 if(solution == null)
                    depthCount+=100;
                 else {
                     problem.visitedStates.clear();
                     return solution;
                 }

             }

         }

             while (true) {
                 if (nodes.size() == 0) {
                     return null;
                 }
                 //node = remove front of the queue
                 SearchTreeNode node = nodes.get(0);
                 //
                 node.heuristic1 = problem.heuristic(1, node);
                 node.heuristic2 = problem.heuristic(2, node);
                 nodes.remove(0);

                 //System.out.println("EXPANDING: " + node.state + " " + problem.numNodesExpanded +" "+node.depth +" "+node.heuristic1 + " OP: "+node.operator);

                 //test using goal test, if yes return the node
                 if (problem.goalTest(node.state)) {
                     nodes.clear();
                     problem.visitedStates.clear();
                     return node;
                 }

                 //else, use the queuing function to add NODES into the queue nodes.
                 // the nodes to be added are basically achieved by applying the operators
                 else {
                     problem.numNodesExpanded++;
                     for (byte operator : problem.operators) {
                         String nextState = problem.StateSpace(node.state, operator);
                         //but before adding, you must check if this is a repeated state
                         //we keep a hashset of all visited states
                         if (!problem.visitedStates.contains(nextState)) {
                             //add the state to visited states
                             problem.visitedStates.add(nextState);
                             SearchTreeNode nextNode = new SearchTreeNode(nextState,node,operator,node.depth+1,0);
                             nextNode.cost =  problem.pathCost(nextNode);

                             if (QING_FUN.equals("BF")) {
                                 //enqueue children at the end

                                 if (nodes.size() > 0) {
                                     nodes.add(nodes.size() - 1, nextNode);
                                 }
                                 else {
                                     nodes.add(nextNode);
                                 }
                             }
                             if (QING_FUN.equals("DF")) {
                                 //enqueue at the front
                                 nodes.add(nextNode);
                             }
                             if (QING_FUN.equals("UC")) {
                                 //enqueue
                                 nodes.add(nextNode);
                                 Collections.sort(nodes, SearchTreeNode.COST);
                             }
                             if (QING_FUN.equals("GR1")) {
                                 //enqueue

                                 nextNode.heuristic1= problem.heuristic(1,nextNode);
                                 //nodes.add(n);
                                 nodes.add(nextNode);
                                 Collections.sort(nodes, SearchTreeNode.H1);

                             }
                             if (QING_FUN.equals("GR2")) {
                                 //enqueue
//                                 SearchTreeNode n = SearchTreeNode.TurnStateIntoNode(nextState, node, operator, node.depth + 1, problem.pathCost(node));
//                                 n.heuristic2= problem.heuristic(2,n);
//                                 nodes.add(n);
                                 nextNode.heuristic2= problem.heuristic(2,nextNode);
                                 //nodes.add(n);
                                 nodes.add(nextNode);
                                 //nodes.add(code.generic.SearchTreeNode.TurnStateIntoNode(nextState, node, operator, node.depth + 1, problem.pathCost(node)));
                                 Collections.sort(nodes, SearchTreeNode.H2);
                             }
                             if (QING_FUN.equals("AS1")) {
//                                 SearchTreeNode n = SearchTreeNode.TurnStateIntoNode(nextState, node, operator, node.depth + 1, problem.pathCost(node));
//                                 n.heuristic1= problem.heuristic(1,n);
//                                 nodes.add(n);
                                 nextNode.heuristic1= problem.heuristic(1,nextNode);
                                 //nodes.add(n);
                                 nodes.add(nextNode);
                                 //enqueue
                                 Collections.sort(nodes, SearchTreeNode.H1_COST);
                                 //sort queue based on cost +h1
                             }
                             if (QING_FUN.equals("AS2")) {
                                 //enqueue
//                                 SearchTreeNode n = SearchTreeNode.TurnStateIntoNode(nextState, node, operator, node.depth + 1, problem.pathCost(node));
//                                 n.heuristic2= problem.heuristic(2,n);
//                                 nodes.add(n);
                                 nextNode.heuristic2= problem.heuristic(2,nextNode);
                                 //nodes.add(n);
                                 nodes.add(nextNode);
                                 Collections.sort(nodes, SearchTreeNode.H2_COST);
                                 //sort queue based on cost+h2
                             }
                         }

                         //apply operators to node

                     }
//                     System.out.println("Nodes in");
//                     for(SearchTreeNode node4: nodes){
//
//                         System.out.print(" "+node4.state+" ");
//                     }
//                     System.out.println();
//                     System.out.println("Nodes done");
                 }
                 //of the problem to the node
                 //end


         }

/*
         * function GENERAL-SEARCH(problem, QING-FUN)
returns a solution, or failure
nodes 􀀀 MAKE-Q(MAKE-NODE(INIT-STATE(problem)))
loop do
If nodes is empty then return failure
node  􀀀 REMOVE-FRONT(nodes)
If GOAL-TEST(problem)(STATE(node)) then return node
nodes 􀀀 QING-FUN(nodes, EXPAND(node, OPER(problem)))
end*/

    }

    public abstract int heuristic(int h, SearchTreeNode node);

    public static SearchTreeNode DepthLimitedSearch(SearchProblem problem, int depth){

        SearchTreeNode nodeOfInitialState = new SearchTreeNode(problem.initialState, null, (byte)-1, 0,0);
        //create a queue (nodes)and add that node into it
        ArrayList<SearchTreeNode> nodes = new ArrayList<>();
        nodes.add(nodeOfInitialState);

        while(true){

            if (nodes.size() == 0) {
                problem.visitedStates.clear();
                return null;
            }
            //node = remove front of the queue
            SearchTreeNode node = nodes.get(0);


            nodes.remove(0);

            //System.out.println("EXPANDING: " + depth);

            //test using goal test, if yes return the node
            if (problem.goalTest(node.state)) {
                return node;
            }
            else {
                problem.numNodesExpanded++;
                if(node.depth<depth ) {

                    for (byte operator : problem.operators) {
                        String nextState = problem.StateSpace(node.state, operator);
                        //but before adding, you must check if this is a repeated state
                        //we keep a hashset of all visited states
                        SearchTreeNode nextNode = new SearchTreeNode(nextState,node,operator,node.depth+1,0);
                        nextNode.cost =  problem.pathCost(nextNode);
                        if (!problem.visitedStates.contains(nextState)) {
                            //add the state to visited states
                            problem.visitedStates.add(nextState);
                            nodes.add(nextNode);
                            //nodes.add(SearchTreeNode.TurnStateIntoNode(nextState, node, operator, node.depth + 1, problem.pathCost(node)));
                        }
                        //apply operators to node
                    }
                }
            }



        }




    }


}
