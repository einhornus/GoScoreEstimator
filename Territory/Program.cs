using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Accord.Neuro;
using Accord.Math;
using Accord.MachineLearning;
using Accord.Neuro.Learning;
using Accord.Statistics;
using DataCreator;

namespace Territory
{
    class Program
    {
        static void Main(string[] args)
        {
            DataCreator.Creator.Main(args);

            List<DataCreator.Sample> samples = DataCreator.Creator.DecodeSamples();

            int inSize = 0;
            int outSize = 0;

            int VALS = 1;

            double[][] inputs = new double[samples.Count-VALS][];
            double[][] outputs = new double[samples.Count - VALS][];



            for (int i = 0; i < samples.Count-VALS; i++)
            {
                double[] input = samples[i].GetInputs();
                double[] output = samples[i].GetOutputs();
                inSize = input.Length;
                outSize = output.Length;
                inputs[i] = input;
                outputs[i] = output;

                for (int j = 0; j<input.Length; j++)
                {
                    if (j % 2 == 0)
                    {
                        Console.Write(" ");
                    }
                    Console.Write(input[j]+" ");
                    if (j%2 == 1)
                    {
                        Console.Write("");
                    }
                }

                Console.WriteLine();
                for (int j = 0; j < output.Length; j++)
                {
                    if (output[j]>=0) {
                        Console.Write(String.Format("+{0:0.0}", output[j]) + " ");
                    }
                    else
                    {
                        Console.Write(String.Format("-{0:0.0}", -output[j]) + " ");
                    }
                }

                Console.WriteLine();
                Console.WriteLine();
                Console.WriteLine();
            }



            ActivationNetwork network = new ActivationNetwork(new BipolarSigmoidFunction(), inSize, 5, outSize);

            NguyenWidrow initializer = new NguyenWidrow(network);
            initializer.Randomize();


            network.Randomize();
            BackPropagationLearning teacher = new BackPropagationLearning(network);

            Random random = new Random();
            for (int i = 0; i < 1000; i++)
            {
                int rnd = random.Next() % inputs.Length;
                double[] _i = inputs[rnd];
                double[] _o = outputs[rnd];

                double error = teacher.RunEpoch(inputs, outputs);
                Console.WriteLine(error/(double)Sample.SAMPLES);
            }

            double[] errors = new double[VALS];
            for (int i = samples.Count - VALS; i < samples.Count; i++)
            {
                double[] input = samples[i].GetInputs();
                double[] output = samples[i].GetOutputs();
                double error = teacher.Run(input, output);
                errors[i-(samples.Count - VALS)] = error;
            }

            double avError = errors.Average();

            Console.WriteLine("Validation");
            Console.WriteLine(avError);


            Console.WriteLine("Sample");
            Sample sample = samples[samples.Count - 1];
            Console.WriteLine("Expected");
            PrintSample(sample);
            Console.WriteLine("Actual");
            PrintSample(sample, network.Compute(sample.GetInputs()));
        }

        static void PrintSample(DataCreator.Sample sample, double[] outputs=null)
        {
            int current = 0;
            for (int i = 0; i < DataCreator.Sample.N; i++)
            {
                for (int j = 0; j < DataCreator.Sample.N; j++)
                {
                    if (sample.input[current] != 0)
                    {
                        if (sample.input[current] == 1)
                        {
                            Console.Write('*');
                        }
                        if (sample.input[current] == -1)
                        {
                            Console.Write('x');
                        }
                    }
                    else
                    {
                        double v = sample.output[current];
                        if (outputs!=null)
                        {
                            v = outputs[current];
                        }
                        double val = 4 + v * 4;
                        int g = (int)Math.Round(val);
                        Console.Write(g);
                    }
                    current++;
                }
                Console.WriteLine();
            }
        }
    }
}
