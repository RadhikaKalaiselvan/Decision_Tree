import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class DataSet {
    public String path;
    public int[][] data;
    public static int numInstances;
    public static int numAttributes;

    public DataSet(String path) {
        this.path = path;
    }

    public void setData(int[][] data) {
        this.data = data;
        if (data.length != 0) {
            numInstances = data.length;
            numAttributes= data[0].length - 1;
        }
    }




}
