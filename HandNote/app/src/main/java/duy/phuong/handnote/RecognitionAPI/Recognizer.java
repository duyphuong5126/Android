package duy.phuong.handnote.RecognitionAPI;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;

import duy.phuong.handnote.DTO.ClusterLabel;
import duy.phuong.handnote.DTO.FloatingImage;
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
    private BitmapProcessor mProcessor;

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

    public String recognize(FloatingImage floatingImage) {
        Bitmap bitmap = BitmapProcessor.resizeBitmap(floatingImage.mBitmap, StandardImage.WIDTH, StandardImage.HEIGHT);
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
            String result = mMapNames.get(win_neuron).getClusterLabel();
            if (result.length() == 1) {
                return result;
            } else {
                return mProcessor.featureExtraction(floatingImage, result);
            }
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
            for (int i = 0; i < Input.VECTOR_DIMENSIONS; i += 16) {
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

                d = input.mInputData[i + 8] - output.mWeights[i + 8];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 9] - output.mWeights[i + 9];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 10] - output.mWeights[i + 10];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 11] - output.mWeights[i + 11];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 12] - output.mWeights[i + 12];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 13] - output.mWeights[i + 13];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 14] - output.mWeights[i + 14];
                result += Math.pow(d, 2);

                d = input.mInputData[i + 15] - output.mWeights[i + 15];
                result += Math.pow(d, 2);
            }
        }
        return Math.sqrt(result);
    }
}
