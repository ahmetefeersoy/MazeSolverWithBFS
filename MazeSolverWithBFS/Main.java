    import java.io.BufferedReader;
    import java.io.FileReader;
    import java.io.IOException;
    import java.util.*;

    public class Main {
        public static void main(String[] args) {
            try {
                char[][] maze = readMaze();

                if (maze != null) {
                    MazeGraph mazeGraph = new MazeGraph(maze);

                    ArrayList<ArrayList<int[]>> treasurePaths = treasureLoc(mazeGraph.getNumRows(), mazeGraph.getNumCols(), maze);

                    if (!treasurePaths.isEmpty()) {
                        System.out.println(treasurePaths.size() + " treasures are found.");
                        System.out.println();
                        System.out.println("Paths are:");
                        System.out.println();

                        for (int i = 0; i < treasurePaths.size(); i++) {
                            System.out.println((i + 1) + ") " + pathToString(treasurePaths.get(i), maze));
                        }
                    } else {
                        System.out.println("0 treasures are found.");
                    }
                } else {
                    System.out.println("Maze could not be read from the file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static char[][] readMaze() throws IOException {
            Scanner scn = new Scanner(System.in);
            System.out.println("Enter the file name:");
            String name = scn.nextLine().trim();
            String path = name;
        
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                List<char[]> mazeLines = new ArrayList<>();
        
                String line;
                while ((line = reader.readLine()) != null) {
                    mazeLines.add(line.toCharArray());
                }
        
                int numRows = mazeLines.size();
                int numCols = mazeLines.get(0).length;
        
                char[][] maze = new char[numRows][numCols];
        
                for (int i = 0; i < numRows; i++) {
                    char[] rowChars = mazeLines.get(i);
                    System.arraycopy(rowChars, 0, maze[i], 0, Math.min(numCols, rowChars.length));
                }
                
                return maze;
            } catch (IOException e) {
                throw e;
            }
        }
        

        static ArrayList<ArrayList<int[]>> treasureLoc(int n, int m, char[][] maze) {
            ArrayList<ArrayList<int[]>> treasurePaths = new ArrayList<>();
            char target = 'E';
        
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (maze[i][j] == target) {
                        ArrayList<int[]> path = findPath(n, m, maze, new int[]{1,0}, new int[]{i, j});
                        if (path != null) {
                            treasurePaths.add(path);
                        }
                    }
                }
            }
        
            // Sort the treasurePaths based on path length
            treasurePaths.sort(Comparator.comparingInt(ArrayList::size));
        
            return treasurePaths;
        }
        
        

        static ArrayList<int[]> findPath(int n, int m, char[][] maze, int[] start, int[] end) {
            Queue<ArrayList<int[]>> Q = new LinkedList<>();
            Q.add(new ArrayList<>(Arrays.asList(start)));

            boolean[][] visited = new boolean[n][m];
        
            while (!Q.isEmpty()) {
                ArrayList<int[]> path = Q.poll();
                int[] u = path.get(path.size() - 1);
        
                if (u[0] == end[0] && u[1] == end[1]) {
                    return path;
                }
        
                for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                    int i = u[0] + d[0];
                    int j = u[1] + d[1];
        
                    if (isValidPosition(i, j, n, m) && !visited[i][j] && maze[i][j] != 'l' && maze[i][j] != '-' && maze[i][j] != '|' && maze[i][j] != '+') {
                        ArrayList<int[]> newPath = new ArrayList<>(path);
                        newPath.add(new int[]{i, j});
                        Q.add(newPath);
                        visited[i][j] = true;
                    }
                }
            }
            
        
            return null;
        }

        static boolean isValidPosition(int i, int j, int n, int m) {
            return i >= 0 && i < n && j >= 0 && j < m;
        }

        static String pathToString(ArrayList<int[]> path, char[][] maze) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int[] point : path) {
                stringBuilder.append(maze[point[0]][point[1]]);
            }
            return stringBuilder.toString();
        }
    }

    class MazeGraph {
        private int numRows;
        private int numCols;
        private char[][] maze;
        private int[][] distances;
        private ArrayList<ArrayList<int[]>> paths;

        public MazeGraph(char[][] maze) {
            this.numRows = maze.length;
            this.numCols = maze[0].length;
            this.maze = maze;
            this.distances = new int[numRows][numCols];
            this.paths = new ArrayList<>();
            bfs();
        }

    private void bfs() {
        Queue<ArrayList<int[]>> queue = new LinkedList<>();
        int[] entrance = {0, 0};
        distances[entrance[0]][entrance[1]] = 1;

        // Initialize the entrance path with the starting point
        ArrayList<int[]> entrancePath = new ArrayList<>();
        entrancePath.add(entrance);
        paths.add(entrancePath);

        queue.add(entrancePath);

        while (!queue.isEmpty()) {
            ArrayList<int[]> currentPath = queue.poll();
            int[] currentCell = currentPath.get(currentPath.size() - 1); // Get the last cell in the path
            int currentRow = currentCell[0];
            int currentCol = currentCell[1];

            for (int[] direction : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int nextRow = currentRow + direction[0];
                int nextCol = currentCol + direction[1];

                if (isValidPosition(nextRow, nextCol) && maze[nextRow][nextCol] != 'l' && maze[nextRow][nextCol] != '-' && maze[nextRow][nextCol] != '|' && distances[nextRow][nextCol] == 0&& maze[nextRow][nextCol] != '+') {
                    int[] nextCell = {nextRow, nextCol};
                    distances[nextCell[0]][nextCell[1]] = distances[currentRow][currentCol] + 1;

                    // Create a new path by copying the existing path and adding the new cell
                    ArrayList<int[]> nextPath = new ArrayList<>(currentPath);
                    nextPath.add(nextCell);
                    paths.add(nextPath);

                    queue.add(nextPath);
                }
            }
        }
    }

        public boolean isSolvable() {
            return distances[numRows - 1][numCols - 1] > 0;
        }

        public ArrayList<int[]> getPath(int row, int col) {
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
