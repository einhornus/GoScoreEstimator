using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using DataCreator;

namespace GoTests
{
    [TestClass]
    public class Tests
    {
        [TestMethod]
        public void TestGoProcessor1()
        {
            int n = 5;
            List<Move> moves = new List<Move>();
            moves.Add(new Move(2, 2));
            moves.Add(new Move(3, 2));
            moves.Add(new Move(4, 0));
            moves.Add(new Move(2, 1));
            moves.Add(new Move(4, 4));
            moves.Add(new Move(1, 2));
            moves.Add(new Move(0, 0));
            moves.Add(new Move(2, 3));
            moves.Add(new Move(0, 4));

            int[,] expected = new int[,]
            {
                {1,  0,  0,  0,  1},
                {0,  0, -1,  0,  0},
                {0, -1,  0, -1,  0},
                {0,  0, -1,  0,  0},
                {1,  0,  0,  0,  1}
            };


            int[,] res = GoProcessor.Process(moves, n);
            bool equals = ArraysEquals(res, expected);

            Assert.AreEqual(true, equals);
        }


        [TestMethod]
        public void TestGoProcessor2()
        {
            int n = 5;
            List<Move> moves = new List<Move>();
            moves.Add(new Move(0, 0));
            moves.Add(new Move(1, 0));
            moves.Add(new Move(0, 1));
            moves.Add(new Move(1, 1));
            moves.Add(new Move(0, 2));
            moves.Add(new Move(1, 2));
            moves.Add(new Move(4, 4));
            moves.Add(new Move(0, 3));

            int[,] expected = new int[,]
            {
                {  0,  0,  0, -1,  0},
                { -1, -1, -1,  0,  0},
                {  0,  0,  0,  0,  0},
                {  0,  0,  0,  0,  0},
                {  0,  0,  0,  0,  1}
            };


            int[,] res = GoProcessor.Process(moves, n);
            bool equals = ArraysEquals(res, expected);

            Assert.AreEqual(true, equals);
        }

        [TestMethod]
        public void ClearColorTest()
        {
            int n = 5;
            int[,] vals = new int[,]
            {
                {  1,  1,  1, -1,  0},
                { -1, -1, -1,  0,  0},
                {  0,  0,  0,  0,  0},
                {  0,  0,  0,  0,  0},
                {  0,  0,  0,  0,  1}
            };

            GoProcessor.ClearColor(vals, 1, n);


            int[,] expected = new int[,]
            {
                {  0,  0,  0, -1,  0},
                { -1, -1, -1,  0,  0},
                {  0,  0,  0,  0,  0},
                {  0,  0,  0,  0,  0},
                {  0,  0,  0,  0,  1}
            };

            bool equals = ArraysEquals(vals, expected);

            Assert.AreEqual(true, equals);
        }


        public bool ArraysEquals(int[,] a, int[,] b)
        {
            if (a.GetLength(0) != b.GetLength(0))
            {
                return false;
            }

            if (a.GetLength(1) != b.GetLength(1))
            {
                return false;
            }

            int n = a.GetLength(0);
            int m = a.GetLength(1);

            for (int i = 0; i<n; i++)
            {
                for (int j = 0; j<m; j++)
                {
                    if (a[i, j]  != b[i, j])
                    {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
