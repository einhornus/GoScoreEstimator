using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataCreator
{
    public class ScoreEstimatorLogic
    {
        public static int WALK_SERIES_LENGTH = 100;
        public static int WALK_LENGTH = 40;
        public static Random random = new Random();


        private static double GetSuccessRate(int[,] stones, int index, int i, int j)
        {
            int n = stones.GetLength(0);
            int success = 0;
            for (int k = 0; k < WALK_SERIES_LENGTH; k++)
            {
                int currentX = i;
                int currentY = j;
                int steps = 0;
                int q = 0;
                while (steps < WALK_LENGTH)
                {
                    q++;
                    int rnd = Math.Abs(random.Next()) % 4;
                    int nextX = currentX;
                    int nextY = currentY;
                    if (rnd == 0)
                    {
                        nextX += 1;
                    }

                    if (rnd == 1)
                    {
                        nextX -= 1;
                    }

                    if (rnd == 2)
                    {
                        nextY += 1;
                    }

                    if (rnd == 3)
                    {
                        nextY -= 1;
                    }

                    if (nextX >= 0 && nextX < n)
                    {
                        if (nextY >= 0 && nextY < n)
                        {
                            currentX = nextX;
                            currentY = nextY;
                            steps++;

                            if (stones[currentX, currentY] == index)
                            {
                                success += 1;
                                break;
                            }

                            if (stones[currentX, currentY] == -index)
                            {
                                break;
                            }
                        }
                    }
                }
            }
            double res = (double)success / (double)WALK_SERIES_LENGTH;
            return res;
        }

        private static void DFS(int[,] map, int x, int y, int color, int[,] colors)
        {
            colors[x, y] = color;
            int n = map.GetLength(0);
            int[] dx = { 0, 1, 0, -1 };
            int[] dy = { 1, 0, -1, 0 };
            for (int i = 0; i < dx.Length; i++)
            {
                int newX = x + dx[i];
                int newY = y + dy[i];
                if (newX >= 0 && newX < n)
                {
                    if (newY >= 0 && newY < n)
                    {
                        if (colors[newX, newY] == 0)
                        {
                            if (map[x, y] == map[newX, newY])
                            {
                                DFS(map, newX, newY, color, colors);
                            }
                        }
                    }
                }
            }
        }

        public static double[,] CountTerritory(int[,] stones)
        {
            int n = stones.GetLength(0);

            double[,] res = new double[n, n];

            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (stones[i, j] == 0)
                    {
                        double firstAmount = GetSuccessRate(stones, -1, i, j);
                        double secondAmount = GetSuccessRate(stones, 1, i, j);
                        double r = secondAmount - firstAmount;
                        res[i, j] = r;
                    }
                    else
                    {
                        res[i, j] = stones[i, j];
                    }
                }
            }
            return res;
        }


        public static double[,] CountInfluence(int[,] stones)
        {
            int n = stones.GetLength(0);
            double ladderK = 3;
            double powerK = 60;
            double[,] res = new double[n, n];
            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (stones[i, j] != 0)
                    {
                        for (int x = 0; x < n; x++)
                        {
                            for (int y = 0; y < n; y++)
                            {
                                if (IsVisible(i, j, x, y, stones))
                                {
                                    double sqDist = (x - i) * (x - i) + (y - j) * (y - j) + 20;
                                    double power = stones[i, j] * powerK / sqDist;
                                    res[x, y] += power;
                                }
                            }
                        }
                    }
                }
            }


            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (stones[i, j] != 0)
                    {
                        for (int x = 0; x < n; x++)
                        {
                            for (int y = 0; y < n; y++)
                            {
                                if (IsVisible(i, j, x, y, stones))
                                {
                                    int dx = Math.Abs(x - i);
                                    int dy = Math.Abs(y - j);

                                    if (Math.Abs(dx - dy) < 3)
                                    {
                                        double power = ladderK * stones[i, j];
                                        res[x, y] += power;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            double max = 20;
            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (Math.Abs(res[i, j]) <= max)
                    {
                        res[i, j] /= max;
                    }
                    else
                    {
                        res[i, j] = res[i, j] > 0 ? 1 : -1;
                    }
                }
            }

            return res;
        }



        public static bool IsVisible(int x1, int y1, int x2, int y2, int[,] stones)
        {
            if (x1 == x2 && y1 == y2)
            {
                return false;
            }


            int iters = 100;
            for (int i = 0; i < iters; i++)
            {
                double phase = (double)i / (double)iters;
                double x = x1 * (1 - phase) + x2 * phase;
                double y = y1 * (1 - phase) + y2 * phase;
                int _x = (int)(Math.Round(x));
                int _y = (int)(Math.Round(y));
                if (_x == x1 && _y == y1)
                {
                    continue;
                }
                if (_x == x2 && _y == y2)
                {
                    continue;
                }
                if (stones[_x, _y] != 0)
                {
                    return false;
                }
            }

            return true;
        }

    }

}
