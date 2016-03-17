package duy.phuong.handnote.RecognitionAPI.MachineLearning;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Phuong on 01/03/2016.
 */
public class Output {
    public double[] mWeights;
    public double mCurrentMinDistance;
    public String mCurrentLabel;
    public HashMap<String, Integer> mMapNames;

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

    public int getCount() {
        int result = 0;
        for (Map.Entry<String, Integer> entry : mMapNames.entrySet()) {
            result += entry.getValue();
        }
        return result;
    }

    public void resetLabel() {
        mCurrentMinDistance = 1000000000;
        mCurrentLabel = "";
        resetMapName();
    }

    public void resetMapName() {
        if (mMapNames == null) {
            mMapNames = new HashMap<>();
        } else {
            mMapNames.clear();
        }
        mMapNames.put("A", 0);
        mMapNames.put("B", 0);
        mMapNames.put("C", 0);
        mMapNames.put("D", 0);
        mMapNames.put("E", 0);
        mMapNames.put("F", 0);
        mMapNames.put("G", 0);
        mMapNames.put("H", 0);
        mMapNames.put("I", 0);
        mMapNames.put("J", 0);
        mMapNames.put("K", 0);
        mMapNames.put("L", 0);
        mMapNames.put("M", 0);
        mMapNames.put("N", 0);
        mMapNames.put("O", 0);
        mMapNames.put("P", 0);
        mMapNames.put("Q", 0);
        mMapNames.put("R", 0);
        mMapNames.put("S", 0);
        mMapNames.put("T", 0);
        mMapNames.put("W", 0);
        mMapNames.put("U", 0);
        mMapNames.put("V", 0);
        mMapNames.put("X", 0);
        mMapNames.put("Y", 0);
        mMapNames.put("Z", 0);
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

        //get number -0.1 -> 0.1
        temp1 /= 500d;
        temp2 /= 500d;

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

    private int getMaxValue() {
        int max = 0;
        for (Map.Entry<String, Integer> entry : mMapNames.entrySet()) {
            int count = entry.getValue();
            if (count > max) {
                max = count;
            }
        }
        return max;
    }

    public String getLabel() {
        String label = "";
        int max = getMaxValue();
        for (Map.Entry<String, Integer> entry : mMapNames.entrySet()) {
            if (entry.getValue() >= max) {
                label += entry.getKey();
            }
        }

        return label;
    }

    public String getLabelInfo() {
        return mCurrentLabel + ":" + mCurrentMinDistance;
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
