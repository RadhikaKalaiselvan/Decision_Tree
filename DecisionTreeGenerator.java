import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class DecisionTreeGenerator {
    DataSet trainingSet, testSet;
    DecisionTree tree;

    public DecisionTreeGenerator(DataSet trainingDataSet, DataSet testDataSet) {
        try {
            tree = new DecisionTree();
            this.trainingSet = trainingDataSet;
            trainingSet.setData(loadData(trainingSet.path));
            this.testSet = testDataSet;
            testSet.setData(loadData(testSet.path));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int[][] getTestData() {
        return this.testSet.data;
    }

    public Map<Integer, String> getRowInfo() {
        return this.tree.rowMap;
    }

    public int[][] loadData(String dataSetPath) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataSetPath));
        int[][] data;
        String line = "";
        int numColumns = 0;
        int numRows = 0;
        String attrNames = bufferedReader.readLine();
        while (bufferedReader.readLine() != null) {
            numRows++;
        }
        bufferedReader.close();
        StringTokenizer st = new StringTokenizer(attrNames, ",");
        int i = 0;
        String str = "";
        while (st.hasMoreTokens()) {
            str = st.nextToken();
            this.tree.rowMap.put(i, str);
            this.tree.reverseRowMap.put(str, i++);
        }
        numColumns = this.tree.rowMap.size();
        data = new int[numRows][numColumns];
        bufferedReader = new BufferedReader(new FileReader(dataSetPath));
        bufferedReader.readLine();
        numRows = 0;
        while ((line = bufferedReader.readLine()) != null) {
            numColumns = 0;
            st = new StringTokenizer(line, ",");
            while (st.hasMoreTokens()) {
                data[numRows][numColumns++] = Integer.parseInt(st.nextToken());
            }
            numRows++;
        }
        return data;
    }

    public void generateTree() throws Exception {
        generateTree(getRowInfo(), trainingSet.data, 1);
    }

    public void generateTree(Map<Integer, String> row, int[][] data, int nodeIdx) throws Exception {

        if (nodeIdx == 1) {
            this.tree.setDecisionTree(new HashMap<Integer, String>());
        }

        double parentEntropy = calculateRootEntropy(data);

        if (parentEntropy == 0) {
            this.tree.getDecisionTree().put(nodeIdx, "" + data[0][data[0].length - 1]);
        } else if (row.size() == 1) {
            int negatives = 0, positives = 0;
            for (int i = 0; i < data.length; i++) {
                if (data[i][data[i].length - 1] == 0) {
                    negatives++;
                } else {
                    positives++;
                }
            }
            if (negatives > positives) {
                this.tree.getDecisionTree().put(nodeIdx, "0");
            } else {
                this.tree.getDecisionTree().put(nodeIdx, "1");
            }
        } else {
            double infoGain = -2;
            int infoGainX = -1;
            int infoGainLeft = -1;
            int infoGainRight = -1;
            int infoGainMajorityClass = -1;

            for (int i = 0; i < data[0].length - 1; i++) {
                if (!row.containsKey(i))
                    continue;
                int zero = 0, one = 0, zeroZero = 0, zeroOne = 0, oneZero = 0, oneOne = 0;
                for (int j = 0; j < data.length; j++) {
                    if (data[j][i] == 0) {
                        zero++;
                        if (data[j][data[j].length - 1] == 0) {
                            zeroZero++;
                        } else {
                            zeroOne++;
                        }
                    } else {
                        one++;
                        if (data[j][data[j].length - 1] == 0) {
                            oneZero++;
                        } else {
                            oneOne++;
                        }
                    }
                }
                double negativeEntropy, positiveEntropy, entropy, gain;

                negativeEntropy = calculateEntropy(zeroZero, zeroOne, zero);
                positiveEntropy = calculateEntropy(oneZero, oneOne, one);
                entropy = ((double) zero / (zero + one)) * negativeEntropy + ((double) one / (zero + one)) * positiveEntropy;
                gain = parentEntropy - entropy;
                if (gain > infoGain) {
                    infoGain = gain;
                    infoGainX = i;
                    infoGainLeft = zero;
                    infoGainRight = one;
                    infoGainMajorityClass = ((zeroZero + oneZero) > (zeroOne + oneOne)) ? 0 : 1;
                }
            }
            this.tree.getDecisionTree().put(nodeIdx, row.get(infoGainX));
            int[][] leftData = new int[infoGainLeft][data[0].length], rightData = new int[infoGainRight][data[0].length];
            Map<Integer, String> leftBranch = (Map<Integer, String>) ((HashMap<Integer, String>) row).clone();
            leftBranch.remove(infoGainX);
            Map<Integer, String> rightBranch = (Map<Integer, String>) ((HashMap<Integer, String>) row).clone();
            rightBranch.remove(infoGainX);
            int leftBranchArray = 0, rightBranchArray = 0;
            for (int i = 0; i < data.length; i++) {
                if (data[i][infoGainX] == 0) {
                    for (int j = 0; j < data[i].length; j++) {
                        leftData[leftBranchArray][j] = data[i][j];
                    }
                    leftBranchArray++;
                } else {
                    for (int j = 0; j < data[0].length; j++) {
                        rightData[rightBranchArray][j] = data[i][j];
                    }
                    rightBranchArray++;
                }
            }
            if (infoGainLeft == 0) {
                this.tree.getDecisionTree().put(nodeIdx * 2, "" + infoGainMajorityClass);
            } else {
                generateTree(leftBranch, leftData, nodeIdx * 2);
            }
            if (infoGainRight == 0) {
                this.tree.getDecisionTree().put(nodeIdx * 2 + 1,
                        "" + infoGainMajorityClass);
            } else {
                generateTree(rightBranch, rightData, nodeIdx * 2 + 1);
            }
        }
    }



    public double calculateRootEntropy(int[][] data) throws Exception {
        int negatives = 0;
        int positives = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i][data[i].length - 1] == 0) {
                negatives++;
            } else positives++;

        }
        return calculateEntropy(negatives, positives, data.length);
    }

    private static double getLogBase2(double fraction) {
        return Math.log10(fraction) / Math.log10(2);
    }

    public double calculateEntropy(int negatives, int positives, int totalInstances) throws Exception {

        if (!(totalInstances == 0)) {
            double negativeEntropy = 0;
            double positiveEntropy = 0;
            if (negatives != 0) {
                negativeEntropy = -(((double) negatives / totalInstances) * getLogBase2((double) negatives / totalInstances));
            }
            if (positives != 0) {
                positiveEntropy = -(((double) positives / totalInstances) * getLogBase2((double) positives / totalInstances));
            }
            return (-(negativeEntropy) - (positiveEntropy));
        } else return 0;

    }


    public Map<Integer, String> pruneDecisionTree(double pruneFactor) throws Exception {

        Map<Integer, String> decisionTreeBest = new HashMap<Integer, String>();
        decisionTreeBest.putAll(this.tree.getDecisionTree());
        long numberPrunes = Math.round(pruneFactor * decisionTreeBest.size());
        for (int i = 1; i <= numberPrunes; i++) {
            Map<Integer, String> decisionTreeTemp = new HashMap<Integer, String>();
            decisionTreeTemp.putAll(this.tree.getDecisionTree());

            List<Integer> nonLeafNodes = new ArrayList<Integer>();
            int nonLeafCount = 0;
            Iterator<Integer> nodeItr = decisionTreeTemp.keySet().iterator();
            while (nodeItr.hasNext()) {
                Integer key = nodeItr.next();
                String val = decisionTreeTemp.get(key);
                if (!("0".equals(val) || "1".equals(val))) {
                    nonLeafCount++;
                    nonLeafNodes.add(key);
                }
            }
            int p = 0;
            for (int j = 1; j <= (int) Math.round(Math.random()); j++) {
                p = (int) Math.floor(Math.random() * nonLeafCount);
                while (p == 0) {
                    p = (int) Math.floor(Math.random() * nonLeafCount);
                }
                prune(nonLeafNodes.get(p), decisionTreeTemp);
                nonLeafNodes.remove(p);
                nonLeafCount--;
            }

        }

        return decisionTreeBest;
    }


    public void prune(int node, Map<Integer, String> tree) throws Exception {
        int i = node;
        int isOdd = i % 2;
        Map<Integer, Integer> rule = new HashMap<Integer, Integer>();
        while ((i = i / 2) > 0) {
            rule.put(this.tree.reverseRowMap.get(tree.get(i)), isOdd);
            isOdd = i % 2;
        }
        boolean isRecordMatching = true;
        int pCount = 0, nCount = 0;

        for (int j = 0; j < trainingSet.data.length; j++) {
            isRecordMatching = true;
            for (int k = 0; k < trainingSet.data[0].length - 1
                    && rule.containsKey(k); k++) {
                if (trainingSet.data[j][k] != rule.get(k)) {
                    isRecordMatching = false;
                    break;
                }
            }
            if (isRecordMatching) {
                if (trainingSet.data[j][trainingSet.data[0].length - 1] == 0) {
                    nCount++;
                } else {
                    pCount++;
                }
            }
        }

        tree.put(node, "" + (nCount > pCount ? 0 : 1));
    }

    public double getAccuracy(Map<Integer, String> decisionTree, int[][] testData) throws IOException {
        int correct = 0;
        boolean accurate;

        for (int i = 0; i < testData.length; i++) {
            accurate = testAccuracy(decisionTree, testData[i]);
            if (accurate)
                correct++;

        }

        return ((double) correct / testData.length) * 100;
    }

    public boolean testAccuracy(Map<Integer, String> decisionTree, int[] testSample) throws IOException {
        int idx = 1;
        while (!decisionTree.get(idx).equals("0") && !decisionTree.get(idx).equals("1")) {
            idx = testSample[this.tree.reverseRowMap.get(decisionTree.get(idx))] == 0 ? idx * 2 : idx * 2 + 1;
        }
        if (decisionTree.get(idx).equals("" + testSample[testSample.length - 1]))
            return true;
        else {
            String str = "";
            for (int i = 0; i < testSample.length; i++) {
                str = str + testSample[i] + ",";
            }
            return false;
        }
    }

    public void printTree() throws IOException {
        tree.printTree(1, "", "");
    }


}

