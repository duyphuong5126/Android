package duy.phuong.handnote.Recognizer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import duy.phuong.handnote.DTO.ClusterLabel;
import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.StandardImage;
import duy.phuong.handnote.Recognizer.MachineLearning.Input;
import duy.phuong.handnote.Recognizer.MachineLearning.Output;
import duy.phuong.handnote.Recognizer.MachineLearning.PatternLearning;
import duy.phuong.handnote.Recognizer.MachineLearning.SOM;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 08/03/2016.
 */
public class Recognizer {
    protected SOM mMap;
    private ArrayList<ClusterLabel> mMapNames;
    private BitmapProcessor mProcessor;

    private class Neuron {
        public int position;
        public double distance;

        public Neuron(int position, double distance) {
            this.position = position;
            this.distance = distance;
        }
    }

    public Recognizer() {
        mMap = new SOM();
        mMapNames = new ArrayList<>();
    }

    public Recognizer(SOM som, ArrayList<ClusterLabel> MapNames) {
        mMap = som;
        mMapNames = new ArrayList<>();
        mMapNames.addAll(MapNames);
        mProcessor = new BitmapProcessor();
    }

    public Bundle recognize(Character character) {
        Bitmap bitmap = BitmapProcessor.resizeBitmap(character.mBitmap, StandardImage.WIDTH, StandardImage.HEIGHT);
        Input input = normalizeData(bitmap);
        Output[][] outputs = mMap.getOutputs();
        mProcessor.resetMap();

        Neuron[] distance = new Neuron[SOM.NUMBERS_OF_CLUSTER];

        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputs[i].length; j++) {
                double dis = getDistance(input, outputs[i][j]);
                int pos = i * SOM.NUM_OF_COLUMNS + j;
                distance[pos]= new Neuron(pos, dis);
            }
        }

        boolean end = false;
        while (!end) {
            boolean swapped = false;
            for (int i = 1; i < distance.length; i++) {
                if (distance[i].distance < distance[i - 1].distance) {
                    distance[i].position += distance[i - 1].position;
                    distance[i - 1].position = distance[i].position - distance[i - 1].position;
                    distance[i].position -= distance[i - 1].position;

                    double temp = distance[i].distance;
                    distance[i].distance = distance[i - 1].distance;
                    distance[i - 1].distance = temp;
                    swapped = true;
                }
            }

            if (!swapped) {
                end = true;
            }
        }

        int win_neuron_X = -1, win_neuron_Y = -1;
        for (Neuron Distance : distance) {
            int neuron = Distance.position;
            win_neuron_X = neuron % SOM.NUM_OF_COLUMNS;
            win_neuron_Y = neuron / SOM.NUM_OF_COLUMNS;

            Bundle bundle = new Bundle();
            bundle.putSerializable("input", input);
            Log.d("Recognized", mMapNames.get(neuron).toString());
            if (win_neuron_X >= 0 && win_neuron_Y >= 0) {
                bundle.putInt("cordX", win_neuron_X);
                bundle.putInt("cordY", win_neuron_Y);
            }
            String label = mMapNames.get(neuron).getLabel();
            if ((label.length() == 1 && !label.equals("u")) || label.equals("k1") || label.equals("b1")) {
                Log.d("Immediate", "yes");
                bundle.putString("result", label);
                return bundle;
            } else {
                Bundle result = mProcessor.featureExtraction(character, mMapNames.get(neuron).getListLabel());
                label = result.getString("Char");
                if (label.length() == 1 || label.equals("k1") || label.equals("b1")) {
                    bundle.putString("result", label);
                    return bundle;
                }
            }
        }

        int win_neuron = distance[0].position;
        win_neuron_X = win_neuron % SOM.NUM_OF_COLUMNS; win_neuron_Y = win_neuron / SOM.NUM_OF_COLUMNS;
        Bundle bundle = new Bundle();
        bundle.putSerializable("input", input);
        if (win_neuron_X >= 0 && win_neuron_Y >= 0) {
            bundle.putInt("cordX", win_neuron_X);
            bundle.putInt("cordY", win_neuron_Y);
        }
        String label = mMapNames.get(win_neuron).getLabel();

        Log.d("Recognized", mMapNames.get(win_neuron).toString());
        if (label.length() == 1 || label.equals("k1") || label.equals("b1")) {
            Log.d("Immediate", "yes");
            bundle.putString("result", label);
        } else {
            Bundle result = mProcessor.featureExtraction(character, mMapNames.get(win_neuron).getListLabel());
            bundle.putString("result", result.getString("Char"));
        }
        return bundle;
    }

    public void overrideData() {
        SupportUtils.writeFile(mMap.toString(), "Trained", "SOM.txt");
    }

    public void updateSOM(Input input, int x, int y) {
        mMap.updateWeightVector(x, y, input, PatternLearning.INITIAL_LEARNING_RATE, 1);
    }

    protected Input normalizeData(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() != StandardImage.WIDTH || bitmap.getHeight() != StandardImage.HEIGHT) {
            Log.e("Error", "Input data error!");
            return null;
        }

        Input input = new Input();

        int horizontalOffset = Input.VECTOR_DIMENSIONS / bitmap.getHeight();
        for (int i = 0; i < bitmap.getHeight(); i++)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int position = i * horizontalOffset + j;
                input.mInputData[position] = (byte) ((bitmap.getPixel(j, i) == Color.WHITE) ? 0 : 1);
            }

        return input;
    }

    protected double getDistance(Input input, Output output) {
        double result = 0;
        if (input != null && output != null) {
            for (int i = 0; i < Input.VECTOR_DIMENSIONS; i += 35) {
                result += Math.pow(input.mInputData[i] - output.mWeights[i], 2);
                result += Math.pow(input.mInputData[i + 1] - output.mWeights[i + 1], 2);
                result += Math.pow(input.mInputData[i + 2] - output.mWeights[i + 2], 2);
                result += Math.pow(input.mInputData[i + 3] - output.mWeights[i + 3], 2);
                result += Math.pow(input.mInputData[i + 4] - output.mWeights[i + 4], 2);
                result += Math.pow(input.mInputData[i + 5] - output.mWeights[i + 5], 2);
                result += Math.pow(input.mInputData[i + 6] - output.mWeights[i + 6], 2);
                result += Math.pow(input.mInputData[i + 7] - output.mWeights[i + 7], 2);
                result += Math.pow(input.mInputData[i + 8] - output.mWeights[i + 8], 2);
                result += Math.pow(input.mInputData[i + 9] - output.mWeights[i + 9], 2);
                result += Math.pow(input.mInputData[i + 10] - output.mWeights[i + 10], 2);
                result += Math.pow(input.mInputData[i + 11] - output.mWeights[i + 11], 2);
                result += Math.pow(input.mInputData[i + 12] - output.mWeights[i + 12], 2);
                result += Math.pow(input.mInputData[i + 13] - output.mWeights[i + 13], 2);
                result += Math.pow(input.mInputData[i + 14] - output.mWeights[i + 14], 2);
                result += Math.pow(input.mInputData[i + 15] - output.mWeights[i + 15], 2);
                result += Math.pow(input.mInputData[i + 16] - output.mWeights[i + 16], 2);
                result += Math.pow(input.mInputData[i + 17] - output.mWeights[i + 17], 2);
                result += Math.pow(input.mInputData[i + 18] - output.mWeights[i + 18], 2);
                result += Math.pow(input.mInputData[i + 19] - output.mWeights[i + 19], 2);
                result += Math.pow(input.mInputData[i + 20] - output.mWeights[i + 20], 2);
                result += Math.pow(input.mInputData[i + 21] - output.mWeights[i + 21], 2);
                result += Math.pow(input.mInputData[i + 22] - output.mWeights[i + 22], 2);
                result += Math.pow(input.mInputData[i + 23] - output.mWeights[i + 23], 2);
                result += Math.pow(input.mInputData[i + 24] - output.mWeights[i + 24], 2);
                result += Math.pow(input.mInputData[i + 25] - output.mWeights[i + 25], 2);
                result += Math.pow(input.mInputData[i + 26] - output.mWeights[i + 26], 2);
                result += Math.pow(input.mInputData[i + 27] - output.mWeights[i + 27], 2);
                result += Math.pow(input.mInputData[i + 28] - output.mWeights[i + 28], 2);
                result += Math.pow(input.mInputData[i + 29] - output.mWeights[i + 29], 2);
                result += Math.pow(input.mInputData[i + 30] - output.mWeights[i + 30], 2);
                result += Math.pow(input.mInputData[i + 31] - output.mWeights[i + 31], 2);
                result += Math.pow(input.mInputData[i + 32] - output.mWeights[i + 32], 2);
                result += Math.pow(input.mInputData[i + 33] - output.mWeights[i + 33], 2);
                result += Math.pow(input.mInputData[i + 34] - output.mWeights[i + 34], 2);
            }
        }
        return Math.sqrt(result);
    }
}
