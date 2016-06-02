package duy.phuong.handnote.Recognizer.MachineLearning;

/**
 * Created by Phuong on 01/03/2016.
 */

/*Self-Organizing Map*/
public class SOM {
    private Output[][] mOutputs;
    public static final int NUMBERS_OF_CLUSTER = 56;
    public static final int NUM_OF_ROWS = 7;
    public static final int NUM_OF_COLUMNS = NUMBERS_OF_CLUSTER / NUM_OF_ROWS;

    public SOM() {
        init();
    }
    public SOM(double[][] weightMatrix) {
        init(weightMatrix);
    }

    public SOM(SOM som) {
        init(som);
    }

    private void init(SOM som) {
        mOutputs = new Output[NUM_OF_ROWS][NUMBERS_OF_CLUSTER / NUM_OF_ROWS];
        if (som.mOutputs.length == mOutputs.length && som.mOutputs[0].length == mOutputs[0].length) {
            for (int i = 0; i < mOutputs.length; i++)
                for (int j = 0; j < mOutputs[i].length; j++) {
                    mOutputs[i][j] = new Output(som.mOutputs[i][j].mWeights);
                }
        }
    }

    private void init() {
        mOutputs = new Output[NUM_OF_ROWS][NUMBERS_OF_CLUSTER / NUM_OF_ROWS];
        for (int i = 0; i < mOutputs.length; i++)
            for (int j = 0; j < mOutputs[i].length; j++) {
                mOutputs[i][j] = new Output();
            }

    }

    private void init(double[][] weightMatrix) {
        if (weightMatrix.length == NUMBERS_OF_CLUSTER && weightMatrix[0].length == Input.VECTOR_DIMENSIONS) {
            mOutputs = new Output[NUM_OF_ROWS][NUMBERS_OF_CLUSTER / NUM_OF_ROWS];
            for (int i = 0; i < mOutputs.length; i++)
                for (int j = 0; j < mOutputs[i].length; j++) {
                    mOutputs[i][j] = new Output(weightMatrix[i * NUM_OF_COLUMNS + j]);
                }
        }
    }

    public void updateLabelForCluster(int x, int y, String label) {
        //update map name by counting patterns
        mOutputs[y][x].mMapNames.put(label, mOutputs[y][x].mMapNames.get(label) + 1);
        mOutputs[y][x].mCount++;
    }

    public void resetMapName() {
        for (Output[] outputs : mOutputs)
            for (Output output : outputs) {
                output.resetLabel();
            }
    }

    public boolean updateWeightVector(int x, int y, Input input, double learningRate, double neighborInfluence) {
        for (int i = 0; i < mOutputs[y][x].mWeights.length; i += 35) {
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
            mOutputs[y][x].mWeights[i + 16] += (input.mInputData[i + 16] - mOutputs[y][x].mWeights[i + 16]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 17] += (input.mInputData[i + 17] - mOutputs[y][x].mWeights[i + 17]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 18] += (input.mInputData[i + 18] - mOutputs[y][x].mWeights[i + 18]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 19] += (input.mInputData[i + 19] - mOutputs[y][x].mWeights[i + 19]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 20] += (input.mInputData[i + 20] - mOutputs[y][x].mWeights[i + 20]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 21] += (input.mInputData[i + 21] - mOutputs[y][x].mWeights[i + 21]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 22] += (input.mInputData[i + 22] - mOutputs[y][x].mWeights[i + 22]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 23] += (input.mInputData[i + 23] - mOutputs[y][x].mWeights[i + 23]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 24] += (input.mInputData[i + 24] - mOutputs[y][x].mWeights[i + 24]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 25] += (input.mInputData[i + 25] - mOutputs[y][x].mWeights[i + 25]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 26] += (input.mInputData[i + 26] - mOutputs[y][x].mWeights[i + 26]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 27] += (input.mInputData[i + 27] - mOutputs[y][x].mWeights[i + 27]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 28] += (input.mInputData[i + 28] - mOutputs[y][x].mWeights[i + 28]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 29] += (input.mInputData[i + 29] - mOutputs[y][x].mWeights[i + 29]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 30] += (input.mInputData[i + 30] - mOutputs[y][x].mWeights[i + 30]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 31] += (input.mInputData[i + 31] - mOutputs[y][x].mWeights[i + 31]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 32] += (input.mInputData[i + 32] - mOutputs[y][x].mWeights[i + 32]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 33] += (input.mInputData[i + 33] - mOutputs[y][x].mWeights[i + 33]) * learningRate * neighborInfluence;
            mOutputs[y][x].mWeights[i + 34] += (input.mInputData[i + 34] - mOutputs[y][x].mWeights[i + 34]) * learningRate * neighborInfluence;
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
