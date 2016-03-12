package duy.phuong.handnote.RecognitionAPI;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;

import duy.phuong.handnote.DTO.ClusterLabel;
import duy.phuong.handnote.DTO.StandardImage;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.Input;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.Output;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.SOM;

/**
 * Created by Phuong on 08/03/2016.
 */
public class Recognizer {
    protected SOM mMap;
    private ArrayList<ClusterLabel> mMapNames;

    public Recognizer() {
        mMap = new SOM();
        mMapNames = new ArrayList<>();
    }

    public Recognizer(SOM som, ArrayList<ClusterLabel> MapNames) {
        mMap = som;
        mMapNames = new ArrayList<>();
        mMapNames.addAll(MapNames);
    }

    public String recognize(Bitmap bitmap) {
        Input input = normalizeData(bitmap);
        Output[][] outputs = mMap.getOutputs();

        double min_distance = 10000000;
        int win_neuron = -1;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputs[i].length; j++) {
                double dis = getDistance(input, outputs[i][j]);
                if (dis < min_distance) {
                    min_distance = dis;
                    win_neuron = i * 13 + j;
                }
            }
        }
        if (win_neuron >= 0) {
            return mMapNames.get(win_neuron).toString();
        }
        return "";
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
            for (int i = 0; i < Input.VECTOR_DIMENSIONS; i += 8) {
                double d = input.mInputData[i] - output.mWeights[i];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 1] - output.mWeights[i + 1];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 2] - output.mWeights[i + 2];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 3] - output.mWeights[i + 3];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 4] - output.mWeights[i + 4];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 5] - output.mWeights[i + 5];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 6] - output.mWeights[i + 6];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 7] - output.mWeights[i + 7];
                result += Math.pow(d, 2);
            }
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
