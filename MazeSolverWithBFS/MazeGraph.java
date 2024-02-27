import java.util.*;
class MazeGraph {
    private int numRows;
    private int numCols;
    private char[][] maze;
    private int[][] distances;
    private ArrayList<ArrayList<Integer>> paths;

    public MazeGraph(char[][] maze) {
        this.numRows = maze.length;
        this.numCols = maze[0].length;
        this.maze = maze;
        this.distances = new int[numRows][numCols];
        this.paths = new ArrayList<>();
        bfs();
    }

    private void bfs() {
        Queue<int[]> queue = new LinkedList<>();
        int[] entrance = {0, 0};
        distances[entrance[0]][entrance[1]] = 1;
        ArrayList<Integer> entrancePath = new ArrayList<>();
        entrancePath.add(entrance[1]);
        entrancePath.add(entrance[0]);
        paths.add(entrancePath);
        queue.add(entrance);

        while (!queue.isEmpty()) {
            int[] currentCell = queue.poll();
            int currentRow = currentCell[0];
            int currentCol = currentCell[1];

            for (int[] direction : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int nextRow = currentRow + direction[0];
                int nextCol = currentCol + direction[1];

                if (isValidPosition(nextRow, nextCol) && maze[nextRow][nextCol] != '+' && maze[nextRow][nextCol] != '-' && maze[nextRow][nextCol] != '|') {
                    int[] nextCell = {nextRow, nextCol};
                    if (distances[nextCell[0]][nextCell[1]] == 0) {
                        distances[nextCell[0]][nextCell[1]] = distances[currentRow][currentCol] + 1;

                        ArrayList<Integer> nextPath = new ArrayList<>(paths.get((currentRow * numCols) + currentCol));
                        nextPath.add(nextCol);
                        nextPath.add(nextRow);
                        paths.add(nextPath);

                        queue.add(nextCell);
                    }
                }
            }
        }
    }

    public boolean isSolvable() {
        return distances[numRows - 1][numCols - 1] > 0;
    }

    public ArrayList<Integer> getPath(int row, int col) {
        return paths.get(row * numCols + col);
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < numRows && col >= 0 && col < numCols;
    }
}