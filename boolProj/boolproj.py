
from sympy import symbols, trunc, Poly, Matrix
from numpy import add, subtract, linalg, array
from random import shuffle, randrange
from scipy.sparse.linalg import eigsh
from scipy.sparse import csr_matrix
from igraph import plot, Graph 
from copy import copy




#*************************************************************************#
#                                                                         #
#  polyForm: returns the polynomial form of a boolean function starting   #
#            from the coefficients vector of its ANF.                     #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This routine computes the polynomial representation of a boolean     #
#    function from its coefficients. For all i in [0, 2^n - 1], coef[i]   #
#    is examined. If it's 0, then skip, if instead it's 1, then position  #
#    i is binary represented with the least significant bit on the left,  #
#    and the corresponding monomial is created by appending a certain     #
#    variable if and only if there is a 1 in the corresponding position   #
#    of the binary representation of i (for example, x[1] is related to   #
#    a leftmost 1 and x[n] to a rightmost 1). The monomial is then added  #
#    to the outcoming polynomial. Clearly, this procedure makes sense     #
#    because of the bijection between the integers in [0, 2^n - 1] and    #
#    the set of all possible monomials in n variables.                    #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: vector "coef", the coefficients of the function.              #
#    Output: symbolic polynomial "pol", the polynomial representation of  #
#            the function whose coefficients are "coef".                  #
#                                                                         #
#*************************************************************************#

def polyForm(n, coef):
  x = symbols('x1:%d' %(n + 1))
  pol = 0
  if (coef[0]):
    pol = 1
  for i in range(1, 2**n):
    if (coef[i]):
      j = i
      pos = 0
      monom = 1
      while (j):
        if (j & 1):  # bitwise check the presence of a certain variable x[i] within the monomial by examining the position of the 1s
          monom *= x[pos]
        pos += 1
        j >>= 1
      pol += monom
  return pol




#*************************************************************************#
#                                                                         #
#  vectForm: computes the coefficients of the ANF of a boolean function   #
#            starting from its polynomial representaiton.                 #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This function computes the coefficients vector corresponding to a    #
#    boolean function by exploiting the same bijection described in the   #
#    previous function, but naturally backwards. Thanks to the use of     #
#    the Python primitive "monoms()", the task turns out to be a child's  #
#    play.                                                                #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: symbolic polynomial "pol", the polynomial representation of   #
#           the function.                                                 #
#    Output: vector "coef", the coefficients of the function "pol".       #
#                                                                         #
#*************************************************************************#

def vectForm(n, pol):
  coef = [0 for i in range(2**n)]  
  if (pol == 0):
    return coef
  else:
    x = symbols('x1:%d' %(n + 1))
    p = Poly(pol, x, modulus = 2)
    monoms = p.monoms()
    for m in monoms:
      m = m[::-1]  # reverse the tuple to read it correctly in the next line as the right binary integer corresponding to the processed monomial
      pos = int("".join(str(bit) for bit in m), 2)
      coef[pos] = 1
  return coef




#*************************************************************************#
#                                                                         #
#  compTT: computes the Truth Table of a boolean function.                #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This routine uses the famous Butterfly Algorithm to compute either   #
#    the Truth Table of a boolean function or the coefficients vector of  #
#    its ANF, depending on what input you feed it (respectively vector    #
#    of coefficients or TT). Indeed, this function is surprisingly the    #
#    actual inverse of itself.                                            #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: vector "coef", either coefficients or TT.                     #
#    Output: vector "TT", either TT or coefficients respectively.         #
#                                                                         #
#*************************************************************************#

def compTT(n, coef):
  blocksize = 1
  TT = copy(coef)
  for step in range(1, n + 1):
    source = 0
    while (source < 2**n):
      target = source + blocksize
      for i in range(blocksize):
        TT[target + i] ^= TT[source + i]
      source += 2*blocksize
    blocksize *= 2
  return TT




#*************************************************************************#
#                                                                         #
#  compTTbyDef: computes the Truth Table of a boolean function.           #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This function outputs the same outcome as the previous one but with  #
#    a different procedure. The approach in this case is to follow the    #
#    definition, for which TT[i] is equal to the image of the binary      #
#    representation of i (with the least significant bit on the left)     #
#    under the function. Trivially, the mutuality between coefficients    #
#    and TT does not sussist in this case. The purpose of this double     #
#    function is basically for us developers both as correctness check    #
#    and exercise.                                                        #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: vector "coef", the coefficients of the function.              #
#    Output: vector "TT", the Truth Table.                                #
#                                                                         #
#*************************************************************************#

def compTTbyDef(n, coef):
  x = symbols('x1:%d' %(n + 1))
  TT = [0 for j in range(2**n)]
  pol = polyForm(n, coef)
  for i in range(2**n):
    y = [int(bit) for bit in (bin(i)[2:]).zfill(n)]
    y.reverse()
    TT[i] = (Poly(pol, x, modulus = 2).eval(y))
  return TT




#*************************************************************************#
#                                                                         #
#  FWT: performs a Fast Walsh Transform.                                  #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This routine performs a Fast Walsh Transform on an boolean function  #
#    f given in input under the form of its coefficients. By definition,  #
#    the Walsh Transform of a boolean function is the Fourier Transform   #
#    applied to the function (-1)^f(x). A trivial observation leads to    #
#    infer that the last expression is nothing more than the so called    #
#.   "Polarity Thruth Table" of f, i.e. its Truth Table in which all the  #
#    1s have become -1s and all the 0s have become 1s. Hence all the job  #
#    comes down to compute a Fast Fourier Transform on the PTT (which in  #
#    this specific case turns into a Fast Hadamard Transform, being the   #
#    base group (Z/2Z)^n). To achieve this task, a classic "divide and    #
#    conquer" approach is used, in particular by means of the Butterfly   #
#    Algorithm once again. The theoretical background upon which this     #
#    procedure is built is the special shape of the 2^n-Hadamard matrix,  #
#    which is recursively generated as the Kronecker product of the       #
#    2^(n - 1)-Hadamard matrix and the 2-Hadamard matrix. Thanks to this  #
#    block structure, many calculations can be avoided as they repeat     #
#    themselves within the overall computation and can hence be shared.   #
#    This trick allows to lower the computational complexity from O(n^2)  #
#    of the naive algorithm to O(n*log(n)) of the fast version.           #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: vector "coef", the coefficients of the function.              #
#    Output: vector "PTT", the Walsh Transform of the function.           #
#                                                                         #
#*************************************************************************#

def FWT(n, coef):
  size = 1
  ONEs = [1 for j in range(2**n)]
  TT = compTT(n, coef)
  PTT = list(subtract(ONEs, add(TT, TT)))
  while (size < 2**n):
    for pos in range(size - 1, 2**n, 2*size):
      for j in range(pos + 1, pos + size + 1):
        a = PTT[j - size]
        b = PTT[j]
        PTT[j - size] = a + b
        PTT[j] = a - b
    size *= 2
  return PTT




#*************************************************************************#
#                                                                         #
#  WTbyDef: computes the Walsh Transform of a boolean function.           #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This routine performs a Walsh Transform of a boolean function f by   #
#    virtue of its definition, which is the following: the Z-valued map   #
#    W_f that, for every z binary string of length 2^n, sends vector z    #
#    in Σ on {x in (F2)^n} of (-1)^(f(x) + z·x). The function returns it  #
#    in the form of a vector WT whose i-th entry WT[i] carries the image  #
#    under the transform of the binary vectorial representation of i. As  #
#    for the "compTTbyDef" function, the aim of this double function is   #
#    basically for us students both as correctness check and challenge.   #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: vector "coef", the coefficients of the function.              #
#    Output: vector "WT", the Walsh Transform of the function.            #
#                                                                         #
#*************************************************************************#

def WTbyDef(n, coef):
  TT = compTT(n, coef)
  WT = [0 for j in range(2**n)]
  for a in range(2**n):
    res = 0
    for x in range(2**n):
      dot_prod = bin(a & x).count("1")  # smart bitwise way to compute scalar product
      exp = (TT[x] + dot_prod) % 2
      if (exp):
        res -= 1
      else:
        res += 1 
    WT[a] = res
  return WT




#*************************************************************************#
#                                                                         #
#  isBent: checks whether a boolean function is bent or not.              #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This function checks if a boolean function given by means fo its     #
#    coefficients vector is bent or not. To do that, one of the several   #
#    equivalent conditions of being bent is used, such as for every i,    #
#    the i-th value of the Walsh Transform of the boolean function is     #
#    in absolute value equal to 2^(n/2), so either + or - this quantity.  #
#    For more details about the other conditions, consult any book about  #
#    boolean functions.                                                   #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: vector "coef", the coefficients of the function.              #
#    Output: boolean "bent", True if the function is bent, False if not.  #
#                                                                         #
#*************************************************************************#

def isBent(n ,coef):
  WT = FWT(n, coef)
  bent = True
  i = 0
  while (bent and i < 2**n):
    if (WT[i]**2 != 2**n):
      bent = False
    else:
      i += 1
  return bent




#*************************************************************************#
#                                                                         #
#  genRandBentFun: computes a random bent function.                       #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This function looks for a bent function among all possible boolean   #
#    functions in a certain number of variables and returns it. Here, we  #
#    proceed with a random brute-force strategy, in the following sense:  #
#    for every possible binary vector of length 2^n, which is the same    #
#    as for every possible integer in [0, 2^(2^n) - 1], the associated    #
#    boolean function whose coefficients are exactly those indicated by   #
#    the vector is considered; the bent property is then checked: if it   #
#    turns out that it is bent, then it is retured, otherwise, another    #
#    integer-vector is processed. The key point here is that, instead of  #
#    reviewing all the integers-vectors in increasing order, which would  #
#    lead to a deterministic behaviour of the search (since the first     #
#    bent function to be encountered would always be the same), a random  #
#    method of analyzing them all is used, so that a nondeterministic     #
#    effect can be achieved. As expected, since bent functions are quite  #
#    sporadic in the set of all bent functions, this function is very     #
#    inefficient: n = 6 is already a fatal input. But if you think about  #
#    it, this approach is not much different from the exhaustive search   #
#    of all the bent functions in n variables, so it makes sense that     #
#    both the algorithms get stuck at the same input.                     #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Output: vector "rand_coef", coefficients of the random generated     #
#            bent function.                                               #
#                                                                         #
#*************************************************************************#

def genRandBentFunc(n):
  N = 2**(2**n)
  bent_found = False
  while (not bent_found):
    rand = randrange(0, N)
    rand_coef = [int(bit) for bit in (bin(rand)[2:]).zfill(2**n)]
    if (isBent(n, rand_coef)):
      bent_found = True
  return rand_coef




#*************************************************************************#
#                                                                         #
#  genRecursBentFun: computes an "almost random" bent function.           #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This function returns a bent function in the specified number of     #
#    variables. Unlike the previous function, which attacks the problem   #
#    by attempts, this one exploits a theoretical result, which is well   #
#    explained in the attached paper (Theorem 2). In a nutshell, it is    #
#    possible to generate a new bent function in n + 2 variables by       #
#    combining two bent functions in n variables along with some small    #
#    polynomials containing the two new variables. The locution "almost   #
#    random" means that the returned function cannot range over all       #
#    the whole set of possible bent function, but only over a subset of   #
#    of it. In other words, as clearly explained in the "Introduction"    #
#    section as well as in the "Counting" section of the attached paper,  #
#    there exist bent functions which will never be returned by this      #
#    procedure because they cannot be generated through this recursive    #
#    machinery. This function has been designed using a self-referential  #
#    call, as usual for routines having a recursive structure. The base   #
#    case of the induction has been chosen to be n = 4, the max integer   #
#    for which a true random pick is possible (through our own function   #
#    "genRandBentFunc"). It has been necessary to handle the case n = 2   #
#    separately, since preceeding the base case.                          #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Output: symbolic polynomial "r", the symbolic polynomic form of the  #
#            recursively random generated bent function.                  #
#                                                                         #
#*************************************************************************#

def genRecursBentFunc(n):
  if (n == 2):
    return polyForm(n, genRandBentFunc(2))
  elif (n == 4):
    return polyForm(n, genRandBentFunc(4))
  else:
    y = symbols('x%d:%d' %((n - 1), (n + 1)))
    m0 = y[0]*y[1] + y[0] + y[1] + 1
    m1 = y[0] + y[0]*y[1]
    m2 = y[1] + y[0]*y[1]
    m3 = y[0]*y[1]
    m = [m0, m1, m2, m3]
    shuffle(m)
    f = genRecursBentFunc(n - 2)
    g = genRecursBentFunc(n - 2)
    r = trunc((m[0] + m[1])*f + m[2]*g + m[3]*(1 + g), 2)  # multivariate polynomial operations mod 2
    return r  




#*************************************************************************#
#                                                                         #
#  boolGraph: computes the graph related to the input boolean function.   #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This routine computes the graph associated to a boolean function by  #
#    its definition, i.e. the graph whose vertices are all the possible   #
#    2^n binary n-vectors and whose edges are the pairs (v,w) such that   #
#    w is obtained via v by xoring v and any s in the support of the      #
#    function. The support Ω_f of the function is defined as the set of   #
#    all binary n-vectors whose image under f is 1.                       #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: integer "n", the number of variables.                         #
#    Input: vector "coef", the coefficients of the function.              #
#    Output: graph "G_f", the graph associated to the boolean function.   #
#                                                                         #
#*************************************************************************#

def boolGraph(n, coef):
  N = 2**n
  TT = compTT(n, coef)
  Omega_f = [x for x in range(N) if TT[x] == 1]  # compute the support of the boolean function
  E = set()
  for v in range(N):
    for s in Omega_f:
      w = v ^ s  # efficient bitwise xor between n-vectors without the need to convert (and reverse) them effectively
      edge = (v, w) if v < w else (w, v)  # use a set to store just one edge for every pair of adjacent vertices in order to avoid duplicates (simple graph)
      E.update({edge})
  G_f = Graph()
  G_f.add_vertices(N)
  G_f.add_edges(E)
  return G_f




#*************************************************************************#
#                                                                         #
#  compSymSpec: computes the spectrum of the adjacency matrix associated  #
#               to the input graph in a symbolic way.                     #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    This simple function exploits some Python primitives to find out     #
#    all the eigenvalues of the adjacency matrix of the input graph. The  #
#    choice of the use of a dictionary as type of return allows one to    #
#    conveniently take into account the multiplicities too. In this case  #
#    the problem of finding the eigenvalues is addressed in a symbolic    #
#    way, which means that a purely algebraic resolution method has been  #
#    applied and no approximation technique has been exploited. Thus one  #
#    gets the exact form of each eigenvalue, not a numerical derivation   #
#    of it (as happens for the next function).                            #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: graph "G", the graph whose spectrum is to be computed.        #
#    Output: dictionary "spec", the spectrum of the graph, given as set   #
#            of pairings between eigenvalue and related multiplicity.     #
#                                                                         #
#*************************************************************************#

def compSymSpec(G):
  A = Matrix(G.get_adjacency())  # a cast is needed in order to use the subsequent Python primitive
  spec = A.eigenvals() 
  return spec




#*************************************************************************#
#                                                                         #
#  compNumSpec: computes the spectrum of the adjacency matrix associated  #
#               to the input graph in a numerical way.                    #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    In contrast with the previous function, this one computes the same   #
#    algebraic object (i.e. the spectrum of the input graph), but with    #
#    the difference that, in this case, all the eigenvalues of the graph  #
#    are computed through a numerical method which exploits the renowed   #
#    Python library "scipy" and, in particular, as data structures, the   #
#    so called "sparse" matrices. After some annoying casts, a sparse     #
#    matrix containing the information of the adjacency one is created.   #
#    On this specific matrix, the high-performance method "eigsh()" can   #
#    be fired, which definitely shall rely some sort of numerical         #
#    algorithm for the approximate calculation of the eigenvalues of the  #
#    input matrix. Since it is well-known that the resulting eigenvalues  #
#    for a boolean graph are all integers, every λ numerically obtained   #
#    has to be approximate to the closest integer, so that, for example,  #
#    15,9998 and 16,0001 result to be regarded as the same eigenvalue     #
#    instead of two diffenent ones (which would clearly be a huge albeit  #
#    self-evident mistake).                                               #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: graph "G", the graph whose spectrum is to be computed.        #
#    Output: set "spec", the spectrum of the graph.                       #
#                                                                         #
#*************************************************************************#

def compNumSpec(G):
  A = Matrix(G.get_adjacency())  # the more one uses technical and specialized stuff, the more the polymorphism begins to lack
  Dense = array(A)
  Sparse = csr_matrix(Dense.astype(float))
  eigenVals, eigenVects = eigsh(Sparse, k = G.vcount() - 1)
  spec = {round(l) for l in eigenVals}  # here we go with the approx, in combo with the use of a set which prevents repetitions
  return spec




#*************************************************************************#
#                                                                         #
#  nofCommonNeighbors: computes the number of the neighbour nodes shared  #
#                      between the two input nodes of the input graph.    #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    A straightforward deployment of the "igraph"-library primitive that  #
#    goes under the name of "neighbors()" leads to this nice and compact  #
#    implementation of this function, which is very helpful to test and   #
#    to refute the strong regularity of a graph.                          #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: graph "G", the graph whose nodes are under exam.              #
#    Input: integer "v", a node of the graph "G".                         #
#    Input: integer "w", another node of the graph "G".                   #
#    Output: integer "e_f", the number of neighbors "v" and "w" have in   #
#            common (reminding the s.r.g. notation).                      #
#                                                                         #
#*************************************************************************#

def nofCommonNeighbors(G, v, w):
  NBhood_v = G.neighbors(v)
  NBhood_w = G.neighbors(w)
  sharedNBhood = [node for node in NBhood_v if node in NBhood_w]
  e_f = len(sharedNBhood)
  return e_f




#*************************************************************************#
#                                                                         #
#  plotGraph: shows an image including the graphic representation of the  #
#             input graph.                                                #
#                                                                         #
#  Description: ---                                                       #
#                                                                         #
#  Parameters:                                                            #
#                                                                         #
#    Input: graph "G", the graph to be plotted.                           #
#    Output: an image displaying the graph.                               #
#                                                                         #
#*************************************************************************#

def plotGraph(G):
  layout = G.layout_circle()
  plot(G, layout = layout)




#*************************************************************************#
#                                                                         #
#  main: contains the actual program.                                     #
#                                                                         #
#  Description:                                                           #
#                                                                         #
#    The main, as usual, is the core of the whole script. Our project is  #
#    conceived to work as follows: when started, an interactive console   #
#    is shown, and the user can choose between 2 features: the former     #
#    can write all the bent function in a specified number of variables   #
#    on a filetext, while the latter can return a nearly random bent      #
#    function in a specified number of variables. Since the first task    #
#    is implemented via a brute-force approach, it only works for n = 2   #
#    or n = 4. For n = 6 the algorithm already fails, because there are   #
#    2^(2^6) ~ 10^19 possible cases, which are definitely too many. The   #
#    second functionality, instead, works fine up to at least n = 24,     #
#    the max we tried with. For what concerns the "almost casuality" of   #
#    the bent function generated via method 2, the term "almost" stands   #
#    for the following fact: fixed a number of variables, the set of the  #
#    bent function which can be generated in this way DOESN'T correspond  #
#    to the entire set of all the bent functions. This means that there   #
#    exist (and they are actually many) bent functions that cannot be     #
#    obtained through this recursive protocol. For more details, see the  #
#    paper attached to this project. In conclusion, exceptions handling   #
#    and some funny interactions with the user have been included.        #
#                                                                         #
#  Parameters: ---                                                        #
#                                                                         #
#*************************************************************************#

def main():
  choice = input("\nThis program has been designed to perform two main tasks:\n[1] compute all the bent functions in n variables;\n[2] compute an 'almost random' bent function in n variables.\nPlease choose an option by typing the corresponding number: ")
  while (not ((choice == '1') or (choice == '2'))):
    choice = input("Please type either 1 or 2: ")
  valid_input_n = False
  if (choice == '1'):
    enne = input("\nThis sub-program computes all the bent functions in a certain number of variables by an exhaustive search.\nEnter the number of variables: ")
    while (not valid_input_n):                                                                             #\
      try:                                                                                                 # \
        n = int(enne)                                                                                      #  \
        if ((n <= 0) or (n % 2)):                                                                          #   \ 
          enne = input("Input n must be an even number strictly greater than 0. Please enter a valid n: ") #    } exceptions handling
        else:                                                                                              #   /
          valid_input_n = True                                                                             #  /
      except:                                                                                              # /
        enne = input("Input n must be an even number strictly greater than 0. Please enter a valid n: ")   #/
    file = open("result.txt", "w")
    pol_str = ''
    counter = 0
    print("Computing...")
    for k in range(2**(2**n)):                               #\
      coef = [int(bit) for bit in (bin(k)[2:]).zfill(2**n)]  # \
      if (isBent(n, coef)):                                  #  \
        pol = polyForm(n, coef)                              #   \
        pol_str = str(pol)                                   #    } main calculation for option 1
        file.write(pol_str)                                  #   /
        file.write('\n\n')                                   #  /
        counter += 1                                         # /
    file.close()                                             #/
    print("\nDone! All the %d bent functions in %d variables have been written in the output file named 'result.txt'.\n" %(counter, n))
  else:
    enne = input("\nThis sub-program computes a quite generic bent functions in a certain number of variables by a recursive approach based on some theoretical results.\nEnter the number of variables: ")
    while (not valid_input_n):                                                                              #\
      try:                                                                                                  # \
        n = int(enne)                                                                                       #  \
        if ((n <= 0) or (n % 2)):                                                                           #   \
          enne = input("Input n must be an even number strictly greater than 0. Please enter a valid n: ")  #    } exceptions handling
        else:                                                                                               #   /
          valid_input_n = True                                                                              #  /
      except:                                                                                               # /
        enne = input("Input n must be an even number strictly greater than 0. Please enter a valid n: ")    #/
    print("Computing...")
    f = genRecursBentFunc(n)             #\
    x = symbols('x1:%d' %(n + 1))        # \
    print("\nf", x, sep = '', end = '')  #  } main calculation for option 2
    print(" = ", end = '')               # /
    print(f)                             #/
    YN = input("\nHey! What's wrong?\nYou seem kind of skeptical!\nIf you don't trust the theoretical argument and you want a further proof just to be sure, type Y, otherwise if you think you will sleep soundly anyway tonight, type N: ")
    while (not ((YN == 'Y') or (YN == 'N'))):
      YN = input("Please type either Y or N: ")
    if (YN == 'Y'):
      coef = vectForm(n, f)
      if (isBent(n, coef)):
        print("\nA direct check has shown that the function is bent indeed. Don't worry, math never lies.\n")
      else:
        print("\nThis sentence will never be printed. If you see this message on console, you are the Chosen One! You just proved math to be inconsistent...\n")  # just a funny Easter egg sentence for the fearless who read all the code
    else:
      print("\nLaziness... the best virtue of a mathematician!\n")






if __name__ == "__main__":
  main()


