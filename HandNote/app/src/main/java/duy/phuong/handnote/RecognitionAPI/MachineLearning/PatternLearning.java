package duy.phuong.handnote.RecognitionAPI.MachineLearning;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import duy.phuong.handnote.DTO.TrainingImage;
import duy.phuong.handnote.RecognitionAPI.Recognizer;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 01/03/2016.
 */

/*Kohonen Algorithm*/
public class PatternLearning extends Recognizer{
    private static final double INITIAL_LEARNING_RATE = 0.5;
    private static final double INITIAL_NEIGHBOR_RADIUS = SOM.NUMBERS_OF_CLUSTER - 1; //the epsilon(0)

    private ArrayList<TrainingImage> mSamples;
    private int mEpochs; //learning rate time const (T2)
    private double mNeighborRadius;
    private double mLearningRate;
    private double mNeighbor_Time_Const; //neighbor function time const (T1)

    public PatternLearning(ArrayList<TrainingImage> samples, int epochs) {
        mMap = new SOM();
        mEpochs = epochs; //init T2
        if (mEpochs < 0) {
            mEpochs = 0;
        }
        mSamples = new ArrayList<>();
        mSamples.addAll(samples);

        //init parameters
        mLearningRate = INITIAL_LEARNING_RATE;
        mNeighborRadius = INITIAL_NEIGHBOR_RADIUS;
        mNeighbor_Time_Const = mEpochs / Math.log(INITIAL_NEIGHBOR_RADIUS);
    }

    public boolean learn() {
        boolean converge = false;
        for (int i = 0; i < mEpochs && !converge; i++) {
            mMap.resetMapName();
            Log.d("Epoch", "" + i);

            ArrayList<TrainingImage> bitmaps = new ArrayList<>();
            bitmaps.addAll(mSamples);

            while (bitmaps.size() > 0) {
                //1. grab a random input
                Random rd = new Random();
                int position = rd.nextInt(bitmaps.size());
                TrainingImage trainingImage = bitmaps.remove(position);
                Input input = normalizeData(trainingImage.getBitmap());

                //2. find best matching unit
                double min_distance = 1000000;
                int win_neuron_position = -1;
                for (int j = 0; j < mMap.getOutputs().length; j++) {
                    double d = getDistance(input, mMap.getOutputs()[j]);
                    if (min_distance > d) {
                        min_distance = d;
                        win_neuron_position = j;
                    }
                }

                if (win_neuron_position < 0) {
                    Log.d("Error", "An error occur");
                    return false;
                }

                //3. find the neighbor area
                long radius = Math.round(mNeighborRadius);
                long lowerBoundary = win_neuron_position - radius;
                if (lowerBoundary < 0) {
                    lowerBoundary = 0;
                }
                long upperBoundary = win_neuron_position + radius;
                if (upperBoundary > mMap.getOutputs().length - 1) {
                    upperBoundary = mMap.getOutputs().length - 1;
                }

                //4. update weight vector
                mMap.updateWeightVector(win_neuron_position, input, mLearningRate, 1);
                for (long j = lowerBoundary; j <= upperBoundary; j++) {
                    int index = (int) j;
                    if (index != win_neuron_position) {
                        mMap.updateWeightVector(index, input, mLearningRate, neighborInfluence(index, win_neuron_position));
                    }
                }

                //5. update map of names
                mMap.updateLabelForCluster(win_neuron_position, trainingImage.getName());

                Log.d("Trained", "bitmap: " + trainingImage.getName() + ", cluster: " + win_neuron_position);
            }

            //check converge condition
            converge = checkConverge();

            //Log.d("Names", mMap.getMapNames());

            updateLearningRate(i);
            updateNeighborRadius(i);
        }

        SupportUtils.writeFile(mMap.toString(), "Trained", "SOM.txt");
        SupportUtils.writeFile(mMap.getMapNames(), "Trained", "MapNames.txt");
        return true;
    }

    private boolean checkConverge() {
        Output[] outputs = mMap.getOutputs();
        for (int j = 0; j < outputs.length - 1; j++) {
            String label = outputs[j].getLabel();
            if ("".equals(label) || label.length() != 1) {
                return false;
            } else {
                for (int k = j + 1; k < outputs.length; k++) {
                    if (outputs[k].getLabel().contains(label) || "".equals(outputs[k].getLabel()) || outputs[k].getLabel().length() != 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void updateLearningRate(int iteration) {
        mLearningRate = INITIAL_LEARNING_RATE * Math.exp((-1.d * iteration) / mEpochs);
    }

    private void updateNeighborRadius(int iteration) {
        mNeighborRadius = INITIAL_NEIGHBOR_RADIUS * Math.exp((-1.d * iteration) / mNeighbor_Time_Const);
        /*int offset = (int) Math.round(mEpochs * 1.d / INITIAL_NEIGHBOR_RADIUS);
        if (iteration % offset == 0) {
            mNeighborRadius--;
        }

        if (mNeighborRadius < 0) {
            mNeighborRadius = 0;
        }*/
    }

    private double neighborInfluence(int neighborIndex, int BMU_index) {
        double distance = /*getDistance(mMap.getOutputs()[neighborIndex], mMap.getOutputs()[BMU_index])*/ BMU_index - neighborIndex;
        double value = -1 * Math.pow(distance, 2);
        value /= (2 * (Math.pow(mNeighborRadius, 2)));
        return Math.exp(value);
    }
}
