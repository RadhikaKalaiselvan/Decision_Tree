import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DecisionTree {

    public Map<Integer, String> decisionTree = new HashMap<Integer, String>();
    public Map<Integer, String> rowMap = new HashMap<Integer, String>();
    public Map<String, Integer> reverseRowMap = new HashMap<String, Integer>();
    public int numNodes;
    public int numLeafNodes;

    public Map<Integer, String> getDecisionTree() {
        return decisionTree;
    }

    public void setDecisionTree(Map<Integer, String> decisionTree) {
        this.decisionTree = decisionTree;
    }

    public int countNodes(Map<Integer, String> decisionTree) throws IOException {
        int nonLeafCount = 0;
        Iterator<Integer> nodeItr = decisionTree.keySet().iterator();
        while (nodeItr.hasNext()) {
            Integer key = nodeItr.next();
            String val = decisionTree.get(key);
            if (!("0".equals(val) || "1".equals(val))) {
                nonLeafCount++;
            }
            numNodes++;
        }
        numLeafNodes = numNodes - nonLeafCount;
        return numNodes;

    }

    public void printTree(int nodePosition, String space, String condition) throws IOException {
        if (decisionTree.containsKey(nodePosition)) {
            if (!decisionTree.containsKey(2 * nodePosition) && !decisionTree.containsKey(2 * nodePosition + 1)) {
                System.out.println(space.substring(1) + condition + decisionTree.get(nodePosition));

            } else {
                System.out.println((space.length() > 0 ? space.substring(1) : space) + condition);
                printTree(nodePosition * 2, space + "| ", decisionTree.get(nodePosition) + " = 0 : ");
                printTree(nodePosition * 2 + 1, space + "| ", decisionTree.get(nodePosition) + " = 1 : ");
            }
        }
    }
}
