package duy.phuong.handnote.RecognitionAPI.MachineLearning;

import android.util.Log;

/**
 * Created by Phuong on 01/03/2016.
 */

/*Self-Organizing Map*/
public class SOM {
    private Output[] mOutputs;
    public static final int NUMBERS_OF_CLUSTER = 26;

    public SOM() {
        init();
    }

    public SOM(double[][] weightMatrix) {
        init(weightMatrix);
    }

    private void init() {
        mOutputs = new Output[NUMBERS_OF_CLUSTER];
        for (int i = 0; i < mOutputs.length; i++) {
            mOutputs[i] = new Output();
        }
    }

    private void init(double[][] weightMatrix) {
        if (weightMatrix.length == NUMBERS_OF_CLUSTER && weightMatrix[0].length == Input.VECTOR_DIMENSIONS) {
            mOutputs = new Output[NUMBERS_OF_CLUSTER];
            for (int i = 0; i < mOutputs.length; i += 2) {
                mOutputs[i] = new Output(weightMatrix[i]);
                mOutputs[i + 1] = new Output(weightMatrix[i + 1]);
            }
        }
    }

    public void updateLabelForCluster(int index, double distance, String label) {
        //update label by min distance
        if (mOutputs[index].mCurrentMinDistance > distance) {
            mOutputs[index].mCurrentMinDistance = distance;
            mOutputs[index].mCurrentLabel = label;
        } else {
            if (mOutputs[index].mCurrentMinDistance == distance && !mOutputs[index].mCurrentLabel.contains(label)) {
                mOutputs[index].mCurrentLabel += label;
            }
        }

        //update map name by counting patterns
        if (mOutputs[index].mMapNames.get(label) != null) {
            mOutputs[index].mMapNames.put(label, mOutputs[index].mMapNames.get(label) + 1);
        } else {
            Log.d("Error", "Null at label: " + label);
        }
    }

    public void resetMapName() {
        for (Output output : mOutputs) {
            output.resetLabel();
        }
    }

    public boolean updateWeightVector(int position, Input input, double learningRate, double neighborInfluence) {
        if (position < 0 || position >= mOutputs.length || learningRate < 0 || input.mInputData.length != mOutputs[position].mWeights.length) {
            Log.e("Error", "Input data error!");
            return false;
        }

        for (int i = 0; i < mOutputs[position].mWeights.length; i += 4) {
            mOutputs[position].mWeights[i] += (input.mInputData[i] - mOutputs[position].mWeights[i]) * learningRate * neighborInfluence;
            mOutputs[position].mWeights[i + 1] += (input.mInputData[i + 1] - mOutputs[position].mWeights[i + 1]) * learningRate * neighborInfluence;
            mOutputs[position].mWeights[i + 2] += (input.mInputData[i + 2] - mOutputs[position].mWeights[i + 2]) * learningRate * neighborInfluence;
            mOutputs[position].mWeights[i + 3] += (input.mInputData[i + 3] - mOutputs[position].mWeights[i + 3]) * learningRate * neighborInfluence;
        }
        return true;
    }

    public Output[] getOutputs() {
        return mOutputs;
    }

    public String getMapNames() {
        String string = "";
        for (Output output : mOutputs) {
            string += output.getNameList() + "\r\n";
        }
        return string;
    }

    public String getLabels() {
        String string = "";
        for (Output output : mOutputs) {
            string += output.getLabelInfo() + "\r\n";
        }
        return string;
    }

    @Override
    public String toString() {
        String string = "";
        for (Output output : mOutputs) {
            string += output.mCurrentLabel + ";" + output.toString() + "|";
        }
        return string;
    }
}
