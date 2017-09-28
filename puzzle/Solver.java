package puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Comparator;

public class Solver {

    private Stack<Board> solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null){
            throw new IllegalArgumentException ("Initial Board is NULL!");
        }
        solution = new Stack<>();
        solve(initial);
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solution.size()>0;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return solution.size()-1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution.size() == 0? null:() -> solution.iterator();
    }
    private void solve (Board initial) {
        Board twin = initial.twin();

        SearchNode initNode = new SearchNode(initial, 0, null);
        SearchNode twinNode = new SearchNode(twin, 0, null);
        MinPQ <SearchNode> twinPQ = new MinPQ<>(Comparator.comparingInt(SearchNode::getPriority));
        MinPQ <SearchNode> mainPQ = new MinPQ<>(Comparator.comparingInt(SearchNode::getPriority));
        twinPQ.insert(twinNode);
        mainPQ.insert(initNode);

        boolean initGoal = initial.isGoal();
        boolean twinGoal = twin.isGoal();

        while (! (initGoal || twinGoal)) {
            initNode = mainPQ.delMin();
            twinNode = twinPQ.delMin();

            for (SearchNode s : initNode.getNeighbors())
                mainPQ.insert(s);

            for (SearchNode s : twinNode.getNeighbors())
                twinPQ.insert(s);

            initGoal = initNode.board.isGoal();
            twinGoal = twinNode.board.isGoal();

        }

        buildIterator(initNode, initGoal);

    }

    private void buildIterator (SearchNode solutionNode, boolean initGoal) {
        solution = new Stack<>();
        if (initGoal) {
            SearchNode sn = solutionNode;
            do {
                solution.push(sn.board);
                sn = sn.previous;
            }
            while (sn != null);
        }
    }

    private class SearchNode {
        private final Board board;
        private final int moves;
        private final SearchNode previous;
        private final int priority;

        SearchNode (Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            priority = moves + board.manhattan();
        }

        int getPriority () {
            return priority;
        }

        Iterable <SearchNode> getNeighbors () {
            ArrayList<SearchNode> neighbors = new ArrayList<>();
            for (Board b:board.neighbors()) {
                if (previous == null || !b.equals(previous.board)) {
                    neighbors.add( new SearchNode(b, moves+1, this));
                }
            }
            return () -> neighbors.iterator();
        }

        public String toString() {
            return board.toString();
        }
    }
}