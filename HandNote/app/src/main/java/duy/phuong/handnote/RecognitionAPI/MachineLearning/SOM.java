package duy.phuong.handnote.RecognitionAPI.MachineLearning;

import android.util.Log;

/**
 * Created by Phuong on 01/03/2016.
 */

/*Self-Organizing Map*/
public class SOM {
    private Output[][] mOutputs;
    public static final int NUMBERS_OF_CLUSTER = 55;
    public static final int NUM_OF_ROW = 5;

    public SOM() {
        init();
    }

    public SOM(double[][] weightMatrix) {
        init(weightMatrix);
    }

    private void init() {
        mOutputs = new Output[NUM_OF_ROW][NUMBERS_OF_CLUSTER / NUM_OF_ROW];
        for (int i = 0; i < mOutputs.length; i++)
            for (int j = 0; j < mOutputs[i].length; j++) {
                mOutputs[i][j] = new Output();
            }

    }

    private void init(double[][] weightMatrix) {
        if (weightMatrix.length == NUMBERS_OF_CLUSTER && weightMatrix[0].length == Input.VECTOR_DIMENSIONS) {
            mOutputs = new Output[NUM_OF_ROW][NUMBERS_OF_CLUSTER / NUM_OF_ROW];
            for (int i = 0; i < mOutputs.length; i++)
                for (int j = 0; j < mOutputs[i].length; j++) {
                    mOutputs[i][j] = new Output(weightMatrix[i * 11 + j]);
                }
        }
    }

    public void updateLabelForCluster(int x, int y, String label) {
        //update map name by counting patterns
        if (mOutputs[y][x].mMapNames.get(label) != null) {
            mOutputs[y][x].mMapNames.put(label, mOutputs[y][x].mMapNames.get(label) + 1);
            mOutputs[y][x].mCount++;
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

        for (int i = 0; i < mOutputs[y][x].mWeights.length; i += 16) {
            mOutputs[y][x].mWeights[i] += (input.mInputData[i] - mOutputs[y][x].mWeights[i]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 1] += (input.mInputData[i + 1] - mOutputs[y][x].mWeights[i + 1]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 2] += (input.mInputData[i + 2] - mOutputs[y][x].mWeights[i + 2]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 3] += (input.mInputData[i + 3] - mOutputs[y][x].mWeights[i + 3]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 4] += (input.mInputData[i + 4] - mOutputs[y][x].mWeights[i + 4]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 5] += (input.mInputData[i + 5] - mOutputs[y][x].mWeights[i + 5]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 6] += (input.mInputData[i + 6] - mOutputs[y][x].mWeights[i + 6]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 7] += (input.mInputData[i + 7] - mOutputs[y][x].mWeights[i + 7]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 8] += (input.mInputData[i + 8] - mOutputs[y][x].mWeights[i + 8]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 9] += (input.mInputData[i + 9] - mOutputs[y][x].mWeights[i + 9]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 10] += (input.mInputData[i + 10] - mOutputs[y][x].mWeights[i + 10]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 11] += (input.mInputData[i + 11] - mOutputs[y][x].mWeights[i + 11]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 12] += (input.mInputData[i + 12] - mOutputs[y][x].mWeights[i + 12]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 13] += (input.mInputData[i + 13] - mOutputs[y][x].mWeights[i + 13]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 14] += (input.mInputData[i + 14] - mOutputs[y][x].mWeights[i + 14]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 15] += (input.mInputData[i + 15] - mOutputs[y][x].mWeights[i + 15]) * learningRate * neighborInfluence;
        }
        return true;
    }

    public Output[][] getOutputs() {
        return mOutputs;
    }

    public String getMapNames() {
        StringBuilder builder = new StringBuilder();
        for (Output[] outputs : mOutputs)
            for (Output output : outputs) {
                builder.append(output.getNameList()).append("\r\n");
            }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Output[] outputs : mOutputs)
            for (Output output : outputs) {
                builder.append(output.toString()).append("|");
            }
        return builder.toString();
    }
}
