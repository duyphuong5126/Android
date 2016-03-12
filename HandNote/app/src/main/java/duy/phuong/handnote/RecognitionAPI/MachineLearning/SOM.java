package duy.phuong.handnote.RecognitionAPI.MachineLearning;

import android.util.Log;

/**
 * Created by Phuong on 01/03/2016.
 */

/*Self-Organizing Map*/
public class SOM {
    private Output[][] mOutputs;
    public static final int NUMBERS_OF_CLUSTER = 26;

    public SOM() {
        init();
    }

    public SOM(double[][] weightMatrix) {
        init(weightMatrix);
    }

    private void init() {
        mOutputs = new Output[2][NUMBERS_OF_CLUSTER / 2];
        for (int i = 0; i < mOutputs.length; i++)
            for (int j = 0; j < mOutputs[i].length; j++) {
                mOutputs[i][j] = new Output();
            }

    }

    private void init(double[][] weightMatrix) {
        if (weightMatrix.length == NUMBERS_OF_CLUSTER && weightMatrix[0].length == Input.VECTOR_DIMENSIONS) {
            mOutputs = new Output[2][NUMBERS_OF_CLUSTER / 2];
            for (int i = 0; i < mOutputs.length; i++)
                for (int j = 0; j < mOutputs[i].length; j++) {
                    mOutputs[i][j] = new Output(weightMatrix[i * 13 + j]);
                }
        }
    }

    public void updateLabelForCluster(int x, int y, double distance, String label) {
        //update label by min distance
        if (mOutputs[y][x].mCurrentMinDistance > distance) {
            mOutputs[y][x].mCurrentMinDistance = distance;
            mOutputs[y][x].mCurrentLabel = label;
        } else {
            if (mOutputs[y][x].mCurrentMinDistance == distance && !mOutputs[y][x].mCurrentLabel.contains(label)) {
                mOutputs[y][x].mCurrentLabel += label;
            }
        }

        //update map name by counting patterns
        if (mOutputs[y][x].mMapNames.get(label) != null) {
            mOutputs[y][x].mMapNames.put(label, mOutputs[y][x].mMapNames.get(label) + 1);
        } else {
            Log.d("Error", "Null at label: " + label);
        }
    }

    public void resetMapName() {
        for (Output[] outputs : mOutputs)
            for (Output output : outputs) {
                output.resetLabel();
            }
    }

    public boolean updateWeightVector(int x, int y, Input input, double learningRate, double neighborInfluence) {
        if (y < 0 || y >= mOutputs.length || learningRate < 0 || input.mInputData.length != mOutputs[y][x].mWeights.length) {
            Log.e("Error", "Input data error!");
            return false;
        }

        for (int i = 0; i < mOutputs[y][x].mWeights.length; i += 8) {
            mOutputs[y][x].mWeights[i] += (input.mInputData[i] - mOutputs[y][x].mWeights[i]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 1] += (input.mInputData[i + 1] - mOutputs[y][x].mWeights[i + 1]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 2] += (input.mInputData[i + 2] - mOutputs[y][x].mWeights[i + 2]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 3] += (input.mInputData[i + 3] - mOutputs[y][x].mWeights[i + 3]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 4] += (input.mInputData[i + 4] - mOutputs[y][x].mWeights[i + 4]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 5] += (input.mInputData[i + 5] - mOutputs[y][x].mWeights[i + 5]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 6] += (input.mInputData[i + 6] - mOutputs[y][x].mWeights[i + 6]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 7] += (input.mInputData[i + 7] - mOutputs[y][x].mWeights[i + 7]) * learningRate * neighborInfluence;
        }
        return true;
    }

    public Output[][] getOutputs() {
        return mOutputs;
    }

    public String getMapNames() {
        String string = "";
        for (Output[] outputs : mOutputs)
            for (Output output : outputs) {
                string += output.getNameList() + "\r\n";
            }
        return string;
    }

    public String getLabels() {
        String string = "";
        for (Output[] outputs : mOutputs)
            for (Output output : outputs) {
                string += output.getLabelInfo() + "\r\n";
            }
        return string;
    }

    @Override
    public String toString() {
        String string = "";
        for (Output[] outputs : mOutputs)
            for (Output output : outputs) {
                string += output.mCurrentLabel + ";" + output.toString() + "|";
            }
        return string;
    }
}
