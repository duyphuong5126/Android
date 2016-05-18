package duy.phuong.handnote.Recognizer.MachineLearning;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Phuong on 01/03/2016.
 */
public class Output {
    public double[] mWeights;
    public HashMap<String, Integer> mMapNames;
    public HashMap<String, Double> mMapDistances;
    public int mCount = 0;

    public Output() {
        mWeights = new double[Input.VECTOR_DIMENSIONS];
        for (int i = 0; i < mWeights.length; i += 16) {
            mWeights[i] = randomWeight();
            mWeights[i + 1] = randomWeight();
            mWeights[i + 2] = randomWeight();
            mWeights[i + 3] = randomWeight();
            mWeights[i + 4] = randomWeight();
            mWeights[i + 5] = randomWeight();
            mWeights[i + 6] = randomWeight();
            mWeights[i + 7] = randomWeight();
            mWeights[i + 8] = randomWeight();
            mWeights[i + 9] = randomWeight();
            mWeights[i + 10] = randomWeight();
            mWeights[i + 11] = randomWeight();
            mWeights[i + 12] = randomWeight();
            mWeights[i + 13] = randomWeight();
            mWeights[i + 14] = randomWeight();
            mWeights[i + 15] = randomWeight();
        }

        resetLabel();
    }

    public Output(double[] weightVector) {
        if (weightVector.length == Input.VECTOR_DIMENSIONS) {
            mWeights = new double[Input.VECTOR_DIMENSIONS];
            for (int i = 0; i < mWeights.length; i += 16) {
                mWeights[i] = weightVector[i];
                mWeights[i + 1] = weightVector[i + 1];
                mWeights[i + 2] = weightVector[i + 2];
                mWeights[i + 3] = weightVector[i + 3];
                mWeights[i + 4] = weightVector[i + 4];
                mWeights[i + 5] = weightVector[i + 5];
                mWeights[i + 6] = weightVector[i + 6];
                mWeights[i + 7] = weightVector[i + 7];
                mWeights[i + 8] = weightVector[i + 8];
                mWeights[i + 9] = weightVector[i + 9];
                mWeights[i + 10] = weightVector[i + 10];
                mWeights[i + 11] = weightVector[i + 11];
                mWeights[i + 12] = weightVector[i + 12];
                mWeights[i + 13] = weightVector[i + 13];
                mWeights[i + 14] = weightVector[i + 14];
                mWeights[i + 15] = weightVector[i + 15];
            }
        }

        resetLabel();
    }

    public void resetLabel() {
        resetMapName();
        resetDistances();
    }

    private void resetDistances() {
        mMapDistances = new HashMap<>();
        mMapDistances.put("A", Double.MAX_VALUE);mMapDistances.put("B", Double.MAX_VALUE);
        mMapDistances.put("C", Double.MAX_VALUE);mMapDistances.put("D", Double.MAX_VALUE);
        mMapDistances.put("E", Double.MAX_VALUE);mMapDistances.put("F", Double.MAX_VALUE);
        mMapDistances.put("G", Double.MAX_VALUE);mMapDistances.put("H", Double.MAX_VALUE);
        mMapDistances.put("I", Double.MAX_VALUE);mMapDistances.put("J", Double.MAX_VALUE);
        mMapDistances.put("K", Double.MAX_VALUE);mMapDistances.put("L", Double.MAX_VALUE);
        mMapDistances.put("M", Double.MAX_VALUE);mMapDistances.put("N", Double.MAX_VALUE);
        mMapDistances.put("O", Double.MAX_VALUE);mMapDistances.put("P", Double.MAX_VALUE);
        mMapDistances.put("Q", Double.MAX_VALUE);mMapDistances.put("R", Double.MAX_VALUE);
        mMapDistances.put("S", Double.MAX_VALUE);mMapDistances.put("T", Double.MAX_VALUE);
        mMapDistances.put("W", Double.MAX_VALUE);mMapDistances.put("U", Double.MAX_VALUE);
        mMapDistances.put("V", Double.MAX_VALUE);mMapDistances.put("X", Double.MAX_VALUE);
        mMapDistances.put("Y", Double.MAX_VALUE);mMapDistances.put("Z", Double.MAX_VALUE);
        mMapDistances.put("a", Double.MAX_VALUE);mMapDistances.put("b", Double.MAX_VALUE);
        mMapDistances.put("b1", Double.MAX_VALUE);mMapDistances.put("d", Double.MAX_VALUE);
        mMapDistances.put("e", Double.MAX_VALUE);mMapDistances.put("f", Double.MAX_VALUE);
        mMapDistances.put("g", Double.MAX_VALUE);mMapDistances.put("h", Double.MAX_VALUE);
        mMapDistances.put("i", Double.MAX_VALUE);mMapDistances.put("j", Double.MAX_VALUE);
        mMapDistances.put("k", Double.MAX_VALUE);mMapDistances.put("k1", Double.MAX_VALUE);
        mMapDistances.put("l", Double.MAX_VALUE);mMapDistances.put("m", Double.MAX_VALUE);
        mMapDistances.put("n", Double.MAX_VALUE);mMapDistances.put("q", Double.MAX_VALUE);
        mMapDistances.put("r", Double.MAX_VALUE);mMapDistances.put("t", Double.MAX_VALUE);
        mMapDistances.put("u", Double.MAX_VALUE);mMapDistances.put("y", Double.MAX_VALUE);
        mMapDistances.put("x", Double.MAX_VALUE);mMapDistances.put("1", Double.MAX_VALUE);
        mMapDistances.put("2", Double.MAX_VALUE);mMapDistances.put("3", Double.MAX_VALUE);
        mMapDistances.put("4", Double.MAX_VALUE);mMapDistances.put("5", Double.MAX_VALUE);
        mMapDistances.put("6", Double.MAX_VALUE);mMapDistances.put("7", Double.MAX_VALUE);
        mMapDistances.put("8", Double.MAX_VALUE);mMapDistances.put("9", Double.MAX_VALUE);
    }

    public void resetMapName() {
        if (mMapNames == null) {
            mMapNames = new HashMap<>();
        } else {
            mMapNames.clear();
        }
        mCount = 0;
        mMapNames.put("A", 0);mMapNames.put("B", 0);
        mMapNames.put("C", 0);mMapNames.put("D", 0);
        mMapNames.put("E", 0);mMapNames.put("F", 0);
        mMapNames.put("G", 0);mMapNames.put("H", 0);
        mMapNames.put("I", 0);mMapNames.put("J", 0);
        mMapNames.put("K", 0);mMapNames.put("L", 0);
        mMapNames.put("M", 0);mMapNames.put("N", 0);
        mMapNames.put("O", 0);mMapNames.put("P", 0);
        mMapNames.put("Q", 0);mMapNames.put("R", 0);
        mMapNames.put("S", 0);mMapNames.put("T", 0);
        mMapNames.put("W", 0);mMapNames.put("U", 0);
        mMapNames.put("V", 0);mMapNames.put("X", 0);
        mMapNames.put("Y", 0);mMapNames.put("Z", 0);
        mMapNames.put("a", 0);mMapNames.put("b", 0);
        mMapNames.put("b1", 0);mMapNames.put("d", 0);
        mMapNames.put("e", 0);mMapNames.put("f", 0);
        mMapNames.put("g", 0);mMapNames.put("h", 0);
        mMapNames.put("i", 0);mMapNames.put("j", 0);
        mMapNames.put("k", 0);mMapNames.put("k1", 0);
        mMapNames.put("l", 0);mMapNames.put("m", 0);
        mMapNames.put("n", 0);mMapNames.put("q", 0);
        mMapNames.put("r", 0);mMapNames.put("t", 0);
        mMapNames.put("u", 0);mMapNames.put("y", 0);
        mMapNames.put("x", 0);mMapNames.put("1", 0);
        mMapNames.put("2", 0);mMapNames.put("3", 0);
        mMapNames.put("4", 0);mMapNames.put("5", 0);
        mMapNames.put("6", 0);mMapNames.put("7", 0);
        mMapNames.put("8", 0);mMapNames.put("9", 0);
    }

    private double randomWeight() {
        double result = 0.5;
        Random rd = new Random();

        //random number 0 -> 100
        double temp1 = rd.nextInt(101);
        double temp2 = rd.nextInt(101);

        //get number -50 -> 50
        temp1 -= 50d;
        temp2 -= 50d;

        //get number -0.2 -> 0.2
        temp1 /= 250d;
        temp2 /= 250d;

        result += temp1 * temp2;

        return result;
    }

    public String getNameList() {
        String string = "";
        for (Map.Entry<String, Integer> entry : mMapNames.entrySet()) {
            string += entry.getKey() + ":" + entry.getValue() + ";";
        }
        return string;
    }

    public String getDistances() {
        String string = "";
        for (Map.Entry<String, Double> entry : mMapDistances.entrySet()) {
            if (entry.getValue() < Double.MAX_VALUE) {
                string += entry.getKey() + ":" + entry.getValue() + ";";
            }
        }
        if (string.length() == 0) {
            string = "empty;";
        }
        return string;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < mWeights.length; j++) {
            builder.append(mWeights[j]);
            if (j < mWeights.length - 1) {
                builder.append(";");
            }
        }

        return builder.toString();
    }
}
