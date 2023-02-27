In each .txt file there is the MAGMA code for the corresponding algorithm:
- Naive.txt contains the naive method.
- BGStep.txt contains the baby-step giant-step algorithm.
- Schoof.txt contains the Schoof's algorithm.
- Extender.txt contains the algorithm to compute the cardinality in some extension field.
- SecurityTest.txt contains the method to check whether a curve may be secure or not.

In order to open them in a MAGMA console, you can use the command 'load' or just copy and paste the code into it.

For the files Naive.txt, BGStep.txt, Schoof.txt and Extender.txt, the structure is the following:
- Auxiliary functions.
- The main function of the algorithm.
- An example.
- A function called TimeTest, used to test the efficiency of the algorithm.
- A function called CorrectnessTest, used to test the correctness of the algorithm.

Since they are not designed for distribution, the implemented functions do not have any form of input control, so use them carefully.