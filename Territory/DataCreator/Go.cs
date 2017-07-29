using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataCreator
{
    public class Move
    {
        public int X { get; set; }
        public int Y { get; set; }
        public Move(int x, int y)
        {
            this.X = x;
            this.Y = y;
        }
    }

    public class GoProcessor{
        public static int[,] Process(List<Move> moves, int n)
        {
            int[,] res = new int[n, n];
            for (int i = 0; i<moves.Count; i++)
            {
                Move move = moves[i];
                int player = i % 2 == 0 ? 1 : -1;
                int opponent = -player;
                res[move.X, move.Y] = player;
                ClearColor(res, opponent, n);
                ClearColor(res, player, n);
            }
            return res;
        }

        public static int[,] CopyMatrix(int[,] matrix, int n)
        {
            int[,] res = new int[n, n];
            for (int i = 0; i<n; i++)
            {
                for (int j = 0; j<n; j++)
                {
                    res[i, j] = matrix[i, j];
                }
            }
            return res;
        }

        public static void ClearColor(int[,] matrix, int who, int n)
        {
            int[,] copy = CopyMatrix(matrix, n);
            for (int i = 0; i<n; i++)
            {
                for (int j = 0; j<n; j++)
                {
                    if (matrix[i, j] == who)
                    {
                        Move move = new Move(i, j);
                        bool isAvailable = ColorAvailable(copy, move, 0, n);
                        if (!isAvailable)
                        {
                            matrix[i, j] = 0;
                        }
                    }
                }
            }
        }

        public static bool ColorAvailable(int[,] matrix, Move point, int color, int n)
        {
            bool[,] visited = new bool[n, n];
            DFS(matrix, point.X, point.Y, visited, n, color);
            for (int i = 0; i<n; i++)
            {
                for (int j = 0; j<n; j++)
                {
                    if (visited[i, j] && matrix[i, j] == color)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public static void DFS(int[,] matrix, int x, int y, bool[,] visited, int n, int color)
        {
            visited[x, y] = true;
            int[] dx = new int[] {1, 0, -1, 0};
            int[] dy = new int[] {0, 1, 0, -1};
            for (int i = 0; i<dx.Length; i++)
            {
                int newX = x + dx[i];
                int newY = y + dy[i];
                if (newX >= 0 && newX < n)
                {
                    if (newY >= 0 && newY < n)
                    {
                        if (!visited[newX, newY])
                        {
                            if (matrix[newX, newY] == matrix[x, y])
                            {
                                DFS(matrix, newX, newY, visited, n, color);
                            }
                            if (matrix[newX, newY] == color)
                            {
                                visited[newX, newY] = true;
                            }
                        }
                    }
                }
            }
        }

        private static int Hash(int[,] matrix)
        {
            int n = matrix.GetLength(0);
            int res = 0;
            for (int i = 0; i<n; i++)
            {
                for (int j = 0; j<n; j++)
                {
                    res = res ^ matrix[i, j];
                }
            }
            return res;
        }
    }

    public class SGFContent
    {
        public List<Move> moves = new List<Move>();
        public List<Move> whiteTerritory = new List<Move>();
        public List<Move> blackTerritory = new List<Move>();



        public Sample ToSample(int applyMoves)
        {
            throw new NotImplementedException();
            /*
            List<Move> actualMoves = new List<Move>();
            for (int i = 0; i<applyMoves; i++)
            {
                actualMoves.Add(moves[i]);
            }
            int n = 19;
            int[,] matrix = GoProcessor.Process(actualMoves, n);


            double[,] territoryMap = new double[n, n];
            for (int i = 0; i<n; i++)
            {
                for (int j = 0; j<n; j++)
                {
                    if ()
                    {

                    }
                }
            }


            Sample res = new Sample();
            int current = 0;
            for (int i = 0; i < Sample.N; i++)
            {
                for (int j = 0; j < Sample.N; j++)
                {
                    res.input[current] = matrix[i, j];
                    res.output[current] = territory[i, j];
                    current++;
                }
            }
            return res;
            */
        }
    }

    public class SGFParser
    {
        public static SGFContent GetMoves(string content)
        {
            throw new NotImplementedException();
        }
    }
}
