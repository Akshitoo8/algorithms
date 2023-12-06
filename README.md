# Algorithms
Developing DS and Algorithms from scratch.

**1. [WordDictionary] A word dictionary to insert, search, partially search and remove words and their definitions. (without HashMap)**
- The DS of interest to implement the given problem is a Tree.
- Among trees, BST and Trie are popular choices with Trie being more optimized for the given use case.
- But Tries come with the disadvantage of creating a lot of extra nodes and their references.
- To overcome this, I have implemented a hybrid of Tree and Trie.
- Like Trie, it divides the 'key' and stores them in subsequent nodes. In my implementation, it is not necessarily at the character level.
- If there is just one combination possible down a node, store the remaining substring in the same node. This avoids creating extra nodes.\
_Example:_
- insert("ORGANIZATION");\
  [""]->[ORGANIZATION]
- insert("ZZZ");\
  [""]->[ORGANIZATION, ZZZ]
- insert(ORGANISM);\
  [""]->[ORGANI->[ZATION, SM], ZZZ]
- insert(ORGAN);\
  [""]->[ORGAN->[I]->[ZATION, SM], ZZZ]
