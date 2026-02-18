package braille;

import java.util.ArrayList;

/**
 * Contains methods to translate Braille to English and English to Braille using
 * a BST.
 * Reads encodings, adds characters, and traverses tree to find encodings.
 * 
 * @author Seth Kelley
 * @author Kal Pandit
 */
public class BrailleTranslator {

    private TreeNode treeRoot;

    /**
     * Default constructor, sets symbols to an empty ArrayList
     */
    public BrailleTranslator() {
        treeRoot = null;
    }

    /**
     * Reads encodings from an input file as follows:
     * - One line has the number of characters
     * - n lines with character (as char) and encoding (as string) space-separated
     * USE StdIn.readChar() to read character and StdIn.readLine() after reading
     * encoding
     * 
     * @param inputFile the input file name
     */
    public void createSymbolTree(String inputFile) {

        /* PROVIDED, DO NOT EDIT */

        StdIn.setFile(inputFile);
        int numberOfChars = Integer.parseInt(StdIn.readLine());
        for (int i = 0; i < numberOfChars; i++) {
            Symbol s = readSingleEncoding();
            addCharacter(s);
        }
    }

    /**
     * Reads one line from an input file and returns its corresponding
     * Symbol object
     * 
     * ONE line has a character and its encoding (space separated)
     * 
     * @return the symbol object
     */
    public Symbol readSingleEncoding() {
        char character = StdIn.readChar();
        String encoding = StdIn.readString();
        StdIn.readLine();

        return new Symbol(character, encoding);
    }

    /**
     * Adds a character into the BST rooted at treeRoot.
     * Traces encoding path (0 = left, 1 = right), starting with an empty root.
     * Last digit of encoding indicates position (left or right) of character within
     * parent.
     * 
     * @param newSymbol the new symbol object to add
     */
    public void addCharacter(Symbol newSymbol) {
        String encoding = newSymbol.getEncoding();
    
        if (treeRoot == null) {
            treeRoot = new TreeNode(new Symbol(""), null, null);
        }
    
        TreeNode current = treeRoot;
        String partialEncoding = "";
    
        for (int i = 0; i < encoding.length() - 1; i++) {
            partialEncoding += encoding.charAt(i);
            char direction = encoding.charAt(i);
    
            if (direction == 'L') {
                if (current.getLeft() == null) {
                    current.setLeft(new TreeNode(new Symbol(partialEncoding), null, null));
                }
                current = current.getLeft();
            } else {
                if (current.getRight() == null) {
                    current.setRight(new TreeNode(new Symbol(partialEncoding), null, null));
                }
                current = current.getRight();
            }
        }
    
        char lastDirection = encoding.charAt(encoding.length() - 1);
        TreeNode newLeaf = new TreeNode(newSymbol, null, null);
    
        if (lastDirection == 'L') {
            current.setLeft(newLeaf);
        } else {
            current.setRight(newLeaf);
        }
    }
    

    /**
     * Given a sequence of characters, traverse the tree based on the characters
     * to find the TreeNode it leads to
     * 
     * @param encoding Sequence of braille (Ls and Rs)
     * @return Returns the TreeNode of where the characters lead to, or null if there is no path
     */
    public TreeNode getSymbolNode(String encoding) {
        if (treeRoot == null) {
            return null;
        }

        TreeNode current = treeRoot;

        for (int i = 0; i < encoding.length(); i++) {
            char direction = encoding.charAt(i);

            if (direction == 'L') {
                if (current.getLeft() == null) {
                    return null;
                }
                current = current.getLeft();
            } else if (direction == 'R') {
                if (current.getRight() == null) {
                    return null;
                }
                current = current.getRight();
            }
        }

        return (current.getSymbol().getCharacter() != Character.MIN_VALUE) ? current : null;
    }


    /**
     * Given a character to look for in the tree will return the encoding of the
     * character
     * 
     * @param character The character that is to be looked for in the tree
     * @return Returns the String encoding of the character
     */
    public String findBrailleEncoding(char character) {
        return findBrailleEncodingHelper(treeRoot, character);
    }

    /**
    * Recursive helper method to find the encoding of a character
    */
    private String findBrailleEncodingHelper(TreeNode node, char character) {
        if (node == null) {
            return null;
        }

        if (node.getSymbol().getCharacter() == character) {
            return node.getSymbol().getEncoding();
        }

        String leftSearch = findBrailleEncodingHelper(node.getLeft(), character);
        if (leftSearch != null) {
            return leftSearch;
        }

        return findBrailleEncodingHelper(node.getRight(), character);
    }

    /**
     * Given a prefix to a Braille encoding, return an ArrayList of all encodings that start with
     * that prefix
     *
     * @param start the prefix to search for
     * @return all Symbol nodes which have encodings starting with the given prefix
     */
    public ArrayList<Symbol> encodingsStartWith(String start) {
        ArrayList<Symbol> result = new ArrayList<>();

        TreeNode startNode = findNodeWithPrefix(start);

        if (startNode == null) {
            return result;
        }

        collectLeafNodes(startNode, result);

        return result;
    }

    /**
     * Helper method to find a node with a given encoding prefix
     */
    private TreeNode findNodeWithPrefix(String prefix) {
        if (treeRoot == null) {
            return null;
        }

        TreeNode current = treeRoot;

        for (int i = 0; i < prefix.length(); i++) {
            char direction = prefix.charAt(i);

            if (direction == 'L') {
                if (current.getLeft() == null) {
                    return null;
                }
                current = current.getLeft();
            } else if (direction == 'R') {
                if (current.getRight() == null) {
                    return null;
                }
                current = current.getRight();
            }
        }

        return current;
    }

    /**
     * Helper method to collect all leaf nodes in a preorder traversal
     */
    private void collectLeafNodes(TreeNode node, ArrayList<Symbol> result) {
        if (node == null) {
            return;
        }

        if (node.getSymbol().getCharacter() != Character.MIN_VALUE) {
            result.add(node.getSymbol());
        }

        collectLeafNodes(node.getLeft(), result);
        collectLeafNodes(node.getRight(), result);
    }

    /**
     * Reads an input file and processes encodings six chars at a time.
     * Then, calls getSymbolNode on each six char chunk to get the
     * character.
     *
     * Return the result of all translations, as a String.
     * @param input the input file
     * @return the translated output of the Braille input
     */
    public String translateBraille(String input) {
        StdIn.setFile(input);
        String brailleEncoding = StdIn.readAll().trim();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < brailleEncoding.length(); i += 6) {
            if (i + 6 <= brailleEncoding.length()) {
                String chunk = brailleEncoding.substring(i, i + 6);

                TreeNode symbolNode = getSymbolNode(chunk);

                if (symbolNode != null) {
                    char character = symbolNode.getSymbol().getCharacter();
                    result.append(character);
                }
            }
        }

        return result.toString();
    }


    /**
     * Given a character, delete it from the tree and delete any encodings not
     * attached to a character (ie. no children).
     *
     * @param symbol the character to delete
     */
    public void deleteSymbol(char symbol) {
        String encoding = findBrailleEncoding(symbol);

        if (encoding == null) {
            return;
        }

        deleteNodeWithEncoding(encoding);
    }

    /**
     * Helper method to delete a node with a given encoding and clean up parent nodes
     */
    private boolean deleteNodeWithEncoding(String encoding) {
        if (encoding.isEmpty()) {
            if (treeRoot != null && treeRoot.getLeft() == null && treeRoot.getRight() == null) {
                treeRoot = null;
                return true;
            }
            return false;
        }

        String parentEncoding = encoding.substring(0, encoding.length() - 1);
        TreeNode parent = (parentEncoding.isEmpty()) ? treeRoot : getParentNode(parentEncoding);

        if (parent == null) {
            return false;
        }

        char lastDirection = encoding.charAt(encoding.length() - 1);
        TreeNode target = (lastDirection == 'L') ? parent.getLeft() : parent.getRight();

        if (target == null) {
            return false;
        }

        if (target.getLeft() == null && target.getRight() == null) {
            if (lastDirection == 'L') {
                parent.setLeft(null);
            } else {
                parent.setRight(null);
            }

            if (parent.getLeft() == null && parent.getRight() == null &&
                    parent.getSymbol().getCharacter() == Character.MIN_VALUE) {
                return deleteNodeWithEncoding(parentEncoding);
            }
        }

        return false;
    }

    /**
     * Helper method to find a parent node with a given encoding
     */
    private TreeNode getParentNode(String encoding) {
        if (treeRoot == null) {
            return null;
        }

        TreeNode current = treeRoot;

        for (int i = 0; i < encoding.length(); i++) {
            char direction = encoding.charAt(i);

            if (direction == 'L') {
                if (current.getLeft() == null) {
                    return null;
                }
                current = current.getLeft();
            } else if (direction == 'R') {
                if (current.getRight() == null) {
                    return null;
                }
                current = current.getRight();
            }
        }

        return current;
    }

    public TreeNode getTreeRoot() {
        return this.treeRoot;
    }

    public void setTreeRoot(TreeNode treeRoot) {
        this.treeRoot = treeRoot;
    }

    public void printTree() {
        printTree(treeRoot, "", false, true);
    }

    private void printTree(TreeNode n, String indent, boolean isRight, boolean isRoot) {
        StdOut.print(indent);

        // Print out either a right connection or a left connection
        if (!isRoot)
            StdOut.print(isRight ? "|+R- " : "--L- ");

        // If we're at the root, we don't want a 1 or 0
        else
            StdOut.print("+--- ");

        if (n == null) {
            StdOut.println("null");
            return;
        }
        // If we have an associated character print it too
        if (n.getSymbol() != null && n.getSymbol().hasCharacter()) {
            StdOut.print(n.getSymbol().getCharacter() + " -> ");
            StdOut.print(n.getSymbol().getEncoding());
        }
        else if (n.getSymbol() != null) {
            StdOut.print(n.getSymbol().getEncoding() + " ");
            if (n.getSymbol().getEncoding().equals("")) {
                StdOut.print("\"\" ");
            }
        }
        StdOut.println();

        // If no more children we're done
        if (n.getSymbol() != null && n.getLeft() == null && n.getRight() == null)
            return;

        // Add to the indent based on whether we're branching left or right
        indent += isRight ? "|    " : "     ";

        printTree(n.getRight(), indent, true, false);
        printTree(n.getLeft(), indent, false, false);
    }

}
