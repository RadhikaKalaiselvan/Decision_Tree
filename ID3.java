public class ID3 {
    public static void main(String[] args) throws Exception {

        double pruneFactor = 0;
        String trainingDataSet = "";
        String testDataSet = "";
        try {

            pruneFactor = Double.parseDouble(args[2]);
            trainingDataSet = args[0];
            testDataSet = args[1];


        } catch (Exception e) {
            System.out.println(e + " Caused due to illegal program arguments");
        }

        DataSet trainingSet = new DataSet(trainingDataSet);
        DataSet testSet = new DataSet(testDataSet);

        DecisionTreeGenerator treeGenerator= new DecisionTreeGenerator(trainingSet, testSet);


        try {


            treeGenerator.generateTree();


            System.out.println("");
            System.out.println("Decision Tree Before Pruning: ");
            treeGenerator.printTree();

            double beforePruningAccuracy = treeGenerator.getAccuracy(treeGenerator.tree.getDecisionTree(), treeGenerator.getTestData()); // Decision treeGenerator with test data

            treeGenerator.tree.setDecisionTree(treeGenerator.pruneDecisionTree(pruneFactor));


            System.out.println();
            System.out.println("Decision Tree After Pruning: ");
            treeGenerator.printTree();

            double afterPruningAccuracy = treeGenerator.getAccuracy(treeGenerator.tree.getDecisionTree(), treeGenerator.getTestData());    // Decision treeGenerator after pruning

            System.out.println();
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("Pre-Pruned Accuracy");
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("No of training instances = " + trainingSet.numInstances);
            System.out.println("No of training attributes = " + trainingSet.numAttributes);
            System.out.println("Total number of nodes in the tree = " + treeGenerator.tree.countNodes(treeGenerator.tree.decisionTree));
            System.out.println("Total number of leaf nodes in the tree = " + treeGenerator.tree.numLeafNodes);
            System.out.println("No of testing instances = " + testSet.numInstances);
            System.out.println("No of testing attributes = " + testSet.numAttributes);
            System.out.println("-------------------------------------------------------------------------------------------");


            System.out.println();
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("Post-Pruned Accuracy");
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("No of training instances = " + trainingSet.numInstances);
            System.out.println("No of training attributes = " + trainingSet.numAttributes);
            System.out.println("Total number of nodes in the tree = " + treeGenerator.tree.numNodes);
            System.out.println("Total number of leaf nodes in the tree = " + treeGenerator.tree.numLeafNodes);
            System.out.println("No of testing instances = " + testSet.numInstances);
            System.out.println("No of testing attributes = " + testSet.numAttributes);

            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println();
            System.out.println("Before Pruning Accuracy =  " + beforePruningAccuracy);
            System.out.println("After Pruning Accuracy = " + afterPruningAccuracy);
            System.out.println("-------------------------------------------------------------------------------------------");

        } catch (Exception e) {
            throw e;
        }

    }


}

