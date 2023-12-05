import java.util.*;

public class WordDictionary {

    private final Node head;

    // initializes the WordDictionary
    public WordDictionary() {
        head = new Node("");
    }

    // Inserts the word and corresponding definition into the dictionary, if the word already exists, it will override the word and definition
    public void insertWord(String word, String definition) {
        if (definition != null) {
            findNode(head, word, definition, false);
        }
    }

    // Returns the definition for the word
    public String findDefinition(String word) {
        Node node = findNode(head, word, null, false);
        return node == null ? null : node.value;
    }

    private Node findNode(Node subHead, String word, String definition, boolean partialSearch) {
        boolean insert = definition != null;

        //abcd. possibilities: ab->[c->[d,x],z]; ab->[cdef, z]; abc; abc->[e,f,g]
        // there can be just one child that starts with a character. like ab->[cde, cfg] are not possible.
        // It will be ab->c->[de, fg]
        Optional<Node> optionalChild = Optional.empty();
        if (subHead.childNodes != null) {
            optionalChild = subHead.childNodes.parallelStream().filter(node -> word.toUpperCase().charAt(0) == node.key.charAt(0)).findAny();
        }
        if (optionalChild.isEmpty()) {
            // No child element matched. Insert if required.
            if (insert) {
                subHead.childNodes = (subHead.childNodes == null) ? new ArrayList<>() : subHead.childNodes;
                subHead.childNodes.add(new Node(word.toUpperCase(), definition));
                return subHead;
            }
            return null; // no result for findDefinition or partialSearch.
        }

        //First character is same. Let's match the rest of the characters.
        Node childNode = optionalChild.get();

        //'word' is present in the tree. Return and/or Update the value.
        //e.g. word:"abcd", nodes:"a->b->cd->e". subhead="cd"
        if (childNode.key.equalsIgnoreCase(word)) {
            if (insert) {
                childNode.value = definition;
            }
            return childNode;
        }

        int i = 0;
        while (word.toUpperCase().charAt(i) == childNode.key.charAt(i)) {
            i++;

            if (i == word.length()) {
                //word ended. (but not the world!).
                return getNodeOnWordEnd(definition, partialSearch, childNode, insert, i);
            }

            if (i == childNode.key.length()) {
                //node key finished. e.g. ab->'c'->[d,x]. investigate child nodes.
                return findNode(childNode, word.substring(i), definition, partialSearch);
            }
        }

        //partial match till i index.
        //e.g. word:'bcdef', node:'bczzz'
        return getNodeOnPartialMatch(word, definition, insert, childNode, i);
    }

    private static Node getNodeOnPartialMatch(String word, String definition, boolean insert, Node childNode, int i) {
        if (insert) {
            Node node = new Node(childNode.key.substring(i), childNode.value);//(zzz)
            node.childNodes = childNode.childNodes;

            Node wordNode = new Node(word.toUpperCase().substring(i), definition);//(def)

            childNode.key = childNode.key.substring(0, i);//(bc)
            childNode.childNodes = new ArrayList<>();
            childNode.childNodes.add(node);
            childNode.childNodes.add(wordNode);
            return wordNode;
        }

        //partial or exact match.
        return null;
    }

    private static Node getNodeOnWordEnd(String definition, boolean partialSearch, Node childNode, boolean insert, int i) {
        //to insert, divide the node.ab->['cdef', z] to ab->['cd->[ef]', z]
        // for partial search, return this node.
        // exact match not found.
        if (partialSearch) {
            return childNode;
        }
        if (insert) {
            Node node = new Node(childNode.key.substring(i), childNode.value);//(ef)
            node.childNodes = childNode.childNodes;

            childNode.key = childNode.key.substring(0, i);//the 'word' (cd)
            childNode.value = definition;
            childNode.childNodes = Collections.singletonList(node);

            return childNode;
        }
        return null;
    }

    // Returns the definitions for the words that are matched partially
    public List<String> partialSearch(String partialWord) {
        Node node = findNode(head, partialWord, null, true);
        List<String> definitions = new ArrayList<>();
        getDefinitions(node, definitions);
        return definitions;
    }

    private void getDefinitions(Node node, List<String> definitions) {
        if (node == null) {
            return;
        }
        if (node.value != null) {
            definitions.add(node.value);
        }
        if (node.childNodes != null) {
            for (Node child : node.childNodes) {
                getDefinitions(child, definitions);
            }
        }
    }

    // Removes the word with definition from the dictionary
    public void remove(String word) {
        Node node = findNode(head, word, null, false);
        if (node != null) {
            node.value = null;
        }
    }

    private static class Node {
        private String key;
        private String value;
        private List<Node> childNodes;

        Node(String key) {
            this.key = key;
        }

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }
}
