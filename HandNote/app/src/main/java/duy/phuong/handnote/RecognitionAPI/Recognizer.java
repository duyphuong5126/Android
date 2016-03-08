package duy.phuong.handnote.RecognitionAPI;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import duy.phuong.handnote.RecognitionAPI.MachineLearning.Input;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.Output;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.SOM;

/**
 * Created by Phuong on 08/03/2016.
 */
public class Recognizer {
    protected SOM mMap;
    private ArrayList<String> mMapNames;

    public Recognizer() {
        mMap = new SOM();
        mMapNames = new ArrayList<>();
    }

    public Recognizer(SOM som, ArrayList<String> MapNames) {
        mMap = som;
        mMapNames = new ArrayList<>();
        mMapNames.addAll(MapNames);
    }

    public String recognize(Bitmap bitmap) {
        Input input = normalizeData(bitmap);
        Output[] outputs = mMap.getOutputs();

        double min_distance = 10000000;
        int win_neuron = -1;
        for (int i = 0; i < outputs.length; i++) {
            double dis = getDistance(input, outputs[i]);
            if (dis < min_distance) {
                min_distance = dis;
                win_neuron = i;
            }
        }
        if (win_neuron >= 0) {
            return mMapNames.get(win_neuron);
        }
        return "";
    }

    protected Input normalizeData(Bitmap bitmap) {
        if (bitmap.getWidth() * bitmap.getHeight() != Input.VECTOR_DIMENSIONS) {
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
        for (int i = 0; i < Input.VECTOR_DIMENSIONS; i += 4) {
            double d = input.mInputData[i] - output.mWeights[i];
            result += Math.pow(d, 2);

            d = input.mInputData[i + 1] - output.mWeights[i + 1];
            result += Math.pow(d, 2);

            d = input.mInputData[i + 2] - output.mWeights[i + 2];
            result += Math.pow(d, 2);

            d = input.mInputData[i + 3] - output.mWeights[i + 3];
            result += Math.pow(d, 2);
        }
        return Math.sqrt(result);
    }

    protected double getDistance(Output output1, Output output2) {
        double result = 0;
        for (int i = 0; i < Input.VECTOR_DIMENSIONS; i += 4) {
            double d = output1.mWeights[i] - output2.mWeights[i];
            result += Math.pow(d, 2);

            d = output1.mWeights[i + 1] - output2.mWeights[i + 1];
            result += Math.pow(d, 2);

            d = output1.mWeights[i + 2] - output2.mWeights[i + 2];
            result += Math.pow(d, 2);

            d = output1.mWeights[i + 3] - output2.mWeights[i + 3];
            result += Math.pow(d, 2);
        }
        return Math.sqrt(result);
    }
}
