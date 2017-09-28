package puzzle;

import edu.princeton.cs.algs4.StdRandom;
import java.util.ArrayList;

public class Board {
    private final int [][] blocks;
    private int manhattan;
    private final int n;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        n = blocks.length;
        this.blocks = new int [n][n]; //always a square

        //create local copy for immutability purposes
        System.arraycopy(blocks, 0, this.blocks, 0, n);

        manhattan = computeManhattan ();
    }
    // board dimension n
    public int dimension() {
        return n;
    }

    // number of blocks out of place
    public int hamming() {
        int hamming = 0;

        for (int i = 0; i<n; i++)
            for (int k = 0; k<n; k++) {
                int expected = i*n + k + 1;
                int val = blocks[i][k];
                if ( val != 0 && blocks[i][k] != expected ) {
                    hamming++;
                }
            }
        return hamming;
    }



    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        return manhattan;
    }


    // is this board the goal board?
    public boolean isGoal()  {
        boolean goal = true;

        if (blocks[n-1][n-1] != 0) goal = false;
        else
            for (int i = 0; i<n; i++)
                for (int k = 0; k<n; k++) {
                    if ((i<n-1 && k < n-1) && blocks[i][k] != i*n + k + 1) {
                        goal = false;
                        break;
                    }
                }


        return goal;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin()   {
        //swap
        int srcRow = StdRandom.uniform(n/2);
        int srcCol = StdRandom.uniform(n);

        //avoid processing 0 int
        if (blocks[srcRow][srcCol] == 0)
            if (srcCol > 0)
                srcCol--;
            else
                srcCol ++;


        int trgRow = StdRandom.uniform(n/2, n);
        int trgCol = StdRandom.uniform(n);

        if (blocks[trgRow][trgCol] == 0) {
            if (trgCol > 0)
                trgCol--;
            else
                trgCol ++;
        }

        return swap (srcRow, srcCol, trgRow, trgCol);

    }

    // does this board equal y?
    public boolean equals(Object y) {
        boolean same = true;
        if (y == null) same = false;
        else if (y instanceof Board) {
            Board other = (Board) y;
            if (n != other.n) {
                same = false;
            } else
                for (int i = 0; i < n; i++)
                    for (int k = 0; k < n; k++)
                        if (blocks[i][k] != other.blocks[i][k]) {
                            same = false;
                            break;
                        }

        } else {
            same = false;
        }
        return same;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return () -> {
            ArrayList <Board> neighbors = new ArrayList<>();
            for (int i = 0; i<blocks.length; i++)
                for (int k = 0; k<blocks[i].length; k++)
                    if (blocks[i][k] == 0) {
                        //find all possible swaps, and insert the new board according to it's manhattan priority
                        if (i<blocks.length-1) neighbors.add(swap(i,k,i+1, k));
                        if (i>0) neighbors.add(swap(i,k,i-1, k));
                        if (k<blocks.length-1)neighbors.add(swap(i,k,i, k+1));
                        if (k>0) neighbors.add(swap(i,k,i, k-1));
                        break; //done
                    }

            return neighbors.iterator();
        };
    }

    private Board swap (int srcRow, int srcCol, int trgRow, int trgCol) {
        //copy board
        int [][] copy = new int [n][n];
        for (int i = 0; i<blocks.length; i++)
            for (int k = 0; k<blocks[i].length; k++)
                copy[i][k] = blocks[i][k];

        copy [srcRow][srcCol] = blocks[trgRow][trgCol];
        copy [trgRow][trgCol] = blocks[srcRow][srcCol];

        return new Board(copy);
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder grid = new StringBuilder(n+"\n");

        for (int [] row:blocks) {
            for (int value : row)
                grid.append(" ").append(value).append(" ");
            grid.append("\n");
        }

        return grid.toString();
    }

    private int computeManhattan () {
        int manhattan = 0;

        for (int i = 0; i<n; i++)
            for (int k = 0; k<n; k++) {
                int stepsToPos = getStepsToPosition(i, k);
                manhattan += stepsToPos;
            }

        return manhattan;
    }


    private int getStepsToPosition (int row, int col) {
        int steps=0;
        int expected = row*n + col + 1;
        int val = blocks[row][col];
        if ( val != 0 && blocks[row][col] != expected ) {
            //decrease expected by 1 to get proper indices without the offset
            int rowIndex = (val - 1) / n;
            int colIndex = (val - 1) % n ;
            steps = Math.abs(rowIndex - row)+Math.abs(colIndex - col);
        }


        return steps;
    }

}