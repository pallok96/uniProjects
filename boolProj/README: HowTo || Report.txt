 Hi everyone! :D

 This brief text-only file is intended as both a guide throughout our Python code
 and a summary overview on the theoretical background underlying the practical a-
 pplication we decided to highlight.
 
 Please also note that a large and significant part of our report can be found o-
 utside of this text file, and in particular within the Python script file, under
 the shape of comments surrounding the actual code. In fact, all the functions we
 wrote are preceded by a detailed description explaining purpose and functioning.
 Moreover, the theory regarding the mathematical objects we faced has been enclo-
 sed in our recapitulatory notes "FWTandBentFunctions.pdf".
 
 We started from the intent of enumerating all the bent functions in a certain "a 
 priori" fixed number of variables. Bent functions are important in the crypt wo-
 rld for preventing linear attacks on the S-box component of a cipher. Indeed, t-
 he safest boolean functions are those that are as "distant" as possible from the
 linear (affine) ones. A bent functions is namely defined as this kind of functi-
 on: the one whose truth table has maximal Hamming distance from those of all the
 affine boolean functions. The theory then says that one of the equivalent condi-
 tions for the bentness of a boolean function is that its Walsh spectrum is cons-
 tant in absolute value. The Walsh spectrum is defined as the vector obtained fr-
 om the Fourier (Hadamard) transform of the polarity truth table of a boolean fu-
 nction. Hence, to achieve our goal, we engaged the problem in a bruteforce mann-
 er focusing for every possibile boolean function in a preset number of variables
 on its Walsh spectrum: a boolean function was included in the set of the bent o-
 nes if and only if this latter was found to be constant besides the sign. Obvio-
 usly, this kind of approach is destined to fail for a large number of variables.
 Actually, n equal to 6 is enough to make the program loop for years. The proced-
 ure runs instead successfully for n equal to 4. For n equal to 6, the best tech-
 inque to count all the bent functions is the one which exploit equivalency betw-
 een boolean functions, and in particular at the level of the corresponding (bip-
 artite) graphs. This kind of approach is well known and has been already implem-
 ented by Marta Fornasier et al. using C++ in combination with Nauty.
 
 The second issue we faced arised while reading a scientific article about boole-
 an functions and their construction (see "ReferencePaper.pdf"). The idea that c-
 ame up was to create a procedure that could return a random bent function in the 
 specified number of variables, and this procedure had to exploit the theory ins-
 ide the paper we read. Since a truly random output is basically impossible beca-
 use of the natural sparse distribution of bent functions among the set of all p-
 ossible boolean ones, we implemented a routine which recursively computes the d-
 esired bent function according to the arguments we learned from the article. The
 immediately after forced step has been to verify by hand that the generated fun-
 ction was bent indeed. To do this, the same argument as in the previous paragra-
 ph has been used, i.e. the computation and inspection of its Walsh spectrum.
 
 These two options leading to different types of computation has been embedded in
 a single main function, which is the core of our program as well as the function
 which is triggered if the Python script is launched as an executable. Our script
 can however be used also as a library from which one can import the needed meth-
 ods. This shall be the case of the functions related to the graph theory associ-
 ated to boolean functions. In facts, since the part pertaining graphs is, in our
 opinion, more dynamic and interactive due to some visual considerations, we dec-
 ided not to design a cumulative function but instead to import the related func-
 tions and play with them on the console together with some Python primitives, in
 order to show some other nice properties on the occasion of our presentation. In
 fact, the third quest we wanted to undertake was to show that the graph associa-
 ted to a bent function is always strongly regular, with the additional condition
 that e = f, where e and f are the standard parameters of s.r.g. Actually, the l-
 ast statement is an equivalence. Anyway, this is quite simple to show in practi-
 ce if one remembers the theorem that says that a graph is s.r. if and only if it
 has exactly 3 distinct eigenvalues.
 
 In conclusion, a brief mention on the structure of the code is due. Many functi-
 ons we wrote are just "propaedeutic" to some others (e.g. "compTT"), and many o-
 thers are necessary in order to handle the several variables and data structures
 we deal with (e.g. "polyForm" or "vectForm"). Some others are then even complet-
 ely useless, in the sense that they don't ever get used further in the code bec-
 ause they have been used as double correctness check for other functions perfor-
 ming the same task (e.g. "compTTbyDef").
 
 Please let us recall our advice: read the comments to the code!
 Type 'python3 boolproj.py' in the command line and get started!
 Enjoy! :D
 
 Authors: Paolo Piasenti & Alberto Ibrisevic
 Date: July 2021 
 
