package net.joedoe.traffictracker.ml;

public class DecisionTreeClassifier {
    private int[] lChilds;
    private int[] rChilds;
    private double[] thresholds;
    private int[] indices;
    private int[][] classes;

    public DecisionTreeClassifier(int[] lChilds, int[] rChilds, double[] thresholds, int[] indices, int[][] classes) {
        this.lChilds = lChilds;
        this.rChilds = rChilds;
        this.thresholds = thresholds;
        this.indices = indices;
        this.classes = classes;
    }

    public int predict(double[] features) {
        return this.predict(features, 0);
    }

    public int predict(double[] features, int node) {
        if (this.thresholds[node] != -2) {
            if (features[this.indices[node]] <= this.thresholds[node]) {
                return predict(features, this.lChilds[node]);
            } else {
                return predict(features, this.rChilds[node]);
            }
        }
        return findMax(this.classes[node]);
    }

    private int findMax(int[] nums) {
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            index = nums[i] > nums[index] ? i : index;
        }
        return index;
    }
}