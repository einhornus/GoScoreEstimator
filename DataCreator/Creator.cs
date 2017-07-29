using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;

namespace DataCreator
{
    public class Sample
    {
        public static int N = 19;
        public static int SAMPLES = 10000;


        public int[] input;
        public double[] output;

        public Sample()
        {
            input = new int[N*N];
            output = new double[N*N];
        }

        public int GetInputLength()
        {
            return 2 * N * N + 1;
        }

        public int GetOutputsLength()
        {
            return N * N;
        }

        public double[] GetInputs()
        {
            double[] res = new double[GetInputLength()];
            for (int i = 0; i<2*N*N; i+=2)
            {
                if(input[i/2] == 0)
                {
                    res[i] = 0;
                    res[i+1] = 0;
                }
                else
                {
                    if (input[i/2] == 1)
                    {
                        res[i] = 1;
                        res[i + 1] = 1;
                    }
                    else
                    {
                        res[i] = 1;
                        res[i + 1] = 0;
                    }
                }
            }
            res[2 * N * N] = 1;
            return res;
        }

        public double[] GetOutputs()
        {
            double[] res = new double[GetOutputsLength()];
            for (int i = 0; i < N * N; i++)
            {
                res[i] = output[i];
            }
            return res;
        }

        public static Sample DecodeSample(string str)
        {
            Sample res = new Sample();
            string[] lines = str.Split('\n');
            for (int i = 0; i<lines.Length; i++)
            {
                string line = lines[i];
                if (!line.Equals(""))
                {
                    string[] vals = line.Split(' ');
                    int stone = int.Parse(vals[0]);
                    double outP = double.Parse(vals[1]);
                    res.input[i] = stone;
                    res.output[i] = outP;
                }
            }
            return res;
        }

        public static string EncodeSample(Sample sample)
        {
            string res = "";
            for (int i = 0; i<N*N; i++)
            {
                string v1 = sample.input[i]+"";
                string v3 = sample.output[i]+"";
                res += v1 + " " + v3 + "\n";
            }
            return res;
        }
    }


    public class Creator
    {
        private static string path = "C:\\Users\\Einhorn\\Downloads\\Territory\\Territory\\data\\";

        public static void Main(string[] args)
        {
            System.IO.DirectoryInfo di = new DirectoryInfo(path);
            foreach (FileInfo file in di.GetFiles())
            {
                file.Delete();
            }
            foreach (DirectoryInfo dir in di.GetDirectories())
            {
                dir.Delete(true);
            }


            for (int i = 0; i<Sample.SAMPLES; i++)
            {
                Sample sample = CreateSample(i);
                string s = Sample.EncodeSample(sample);
                string fname = path + "sample" + i + ".txt";
                File.WriteAllText(fname, s);
            }
        }

        public static List<Sample> DecodeSamples()
        {
            string[] files = Directory.GetFiles(path);
            List<Sample> res = new List<Sample>();
            for (int i = 0; i<files.Length; i++)
            {
                String content = File.ReadAllText(files[i]);
                Sample sample = Sample.DecodeSample(content);
                res.Add(sample);
            }
            return res;
        }

        public static Sample CreateSample(int index)
        {
            Random random = new Random();
            int[,] stones = new int[Sample.N, Sample.N];
            int len = 10;
            for (int i = 0; i<len; i++)
            {
                int x = -1;
                int y = -1;
                while (true)
                {
                    x = random.Next()%Sample.N;
                    y = random.Next() % Sample.N;
                    if (stones[x, y] == 0)
                    {
                        stones[x, y] = i % 2 == 0 ? 1 : -1;
                        break;
                    }
                }
            }

            double[,] territory = ScoreEstimatorLogic.CountTerritory(stones);


            Sample res = new Sample();
            int current = 0;
            for (int i = 0; i<Sample.N; i++)
            {
                for (int j = 0; j<Sample.N; j++)
                {
                    res.input[current] = stones[i, j];
                    res.output[current] = territory[i, j];
                    current++;
                }
            }
            return res;
        }
    }
}
