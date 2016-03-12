package duy.phuong.handnote.RecognitionAPI.MachineLearning;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import duy.phuong.handnote.DTO.StandardImage;
import duy.phuong.handnote.RecognitionAPI.Recognizer;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 01/03/2016.
 */

/*Kohonen Algorithm*/
public class PatternLearning extends Recognizer {
    private static final double INITIAL_LEARNING_RATE = 0.5;//the initial learning rate
    private static final double MAP_RADIUS = (SOM.NUMBERS_OF_CLUSTER / 4);

    private ArrayList<Input> mSamples;//training set
    private int mEpochs; //learning rate time const (T2)
    private double mNeighborRadius;
    private double mLearningRate;
    private double mNeighbor_Time_Const; //time const (lambda)

    private double mAverageMemberPerCluster;

    public PatternLearning(ArrayList<StandardImage> samples, int epochs) {
        mMap = new SOM();
        mEpochs = epochs; //init time const
        if (mEpochs < 0) {
            mEpochs = 0;
        }
        mSamples = new ArrayList<>();
        for (StandardImage standardImage : samples) {
            Input input = normalizeData(standardImage.getBitmap());
            input.mLabel = standardImage.getName();

            mSamples.add(input);
        }

        //init parameters
        mLearningRate = INITIAL_LEARNING_RATE;
        mNeighborRadius = MAP_RADIUS;
        mNeighbor_Time_Const = mEpochs / Math.log(MAP_RADIUS);
        mAverageMemberPerCluster = ((double) mSamples.size()) / SOM.NUMBERS_OF_CLUSTER;
    }

    public boolean learn() {
        boolean converge = false;
        for (int i = 0; i < mEpochs && !converge; i++) {
            mMap.resetMapName();
            Log.d("Epoch", "" + i);

            ArrayList<Input> inputs = new ArrayList<>();
            inputs.addAll(mSamples);

            int count = 0;
            while (inputs.size() > 0) {
                //1. grab a random input
                Random rd = new Random();
                int position = rd.nextInt(inputs.size());
                Input input = inputs.remove(position);

                //2. find best matching unit
                double min_distance = 1000000000;
                int win_neuron_position_X = -1;
                int win_neuron_position_Y = -1;
                for (int j = 0; j < mMap.getOutputs().length; j++)
                    for (int k = 0; k < mMap.getOutputs()[j].length; k++) {
                        double d = getDistance(input, mMap.getOutputs()[j][k]);
                        if (min_distance > d) {
                            min_distance = d;
                            win_neuron_position_Y = j;
                            win_neuron_position_X = k;
                        }
                    }

                if (win_neuron_position_X < 0 || win_neuron_position_Y < 0) {
                    Log.d("Error", "An error occur");
                    return false;
                }

                //3. find the neighbor area
                long radius = Math.round(mNeighborRadius);
                long lowerBoundary_X = win_neuron_position_X - radius;
                if (lowerBoundary_X < 0) {
                    lowerBoundary_X = 0;
                }
                long upperBoundary_X = win_neuron_position_X + radius;
                if (upperBoundary_X > mMap.getOutputs()[0].length - 1) {
                    upperBoundary_X = mMap.getOutputs()[0].length - 1;
                }
                long lowerBoundary_Y = win_neuron_position_Y - radius;
                if (lowerBoundary_Y < 0) {
                    lowerBoundary_Y = 0;
                }
                long upperBoundary_Y = win_neuron_position_Y + radius;
                if (upperBoundary_Y > mMap.getOutputs().length - 1) {
                    upperBoundary_Y = mMap.getOutputs().length - 1;
                }

                //4. update weight vector
                mMap.updateWeightVector(win_neuron_position_X, win_neuron_position_Y, input, mLearningRate, 1);
                for (long j = lowerBoundary_Y; j <= upperBoundary_Y; j++) {
                    int index_Y = (int) j;
                    for (long k = lowerBoundary_X; k <= upperBoundary_X; k++) {
                        int index_X = (int) k;
                        if (index_X != win_neuron_position_X && index_Y != win_neuron_position_Y) {
                            mMap.updateWeightVector(index_X, index_Y, input, mLearningRate,
                                    neighborInfluence(index_X, index_Y, win_neuron_position_X, win_neuron_position_Y));
                        }
                    }
                }

                //5. update map of names
                mMap.updateLabelForCluster(win_neuron_position_X, win_neuron_position_Y, min_distance, input.mLabel);

                //Log.d("Trained", "bitmap: " + input.mLabel + ", cluster: " + win_neuron_position);
                count++;
            }

            Log.d("Count", "" + count);

            //check converge condition
            converge = checkConverge();

            updateLearningRate(i);
            updateNeighborRadius(i);
        }

        SupportUtils.writeFile(mMap.toString(), "Trained", "som.txt");
        SupportUtils.writeFile(mMap.getMapNames(), "Trained", "map_names.txt");
        SupportUtils.writeFile(mMap.getLabels(), "Trained", "labels.txt");
        return true;
    }

    private boolean checkConverge() {
        for (Output[] outputs : mMap.getOutputs()) {
            for (Output output : outputs) {
                if (!(output.getCount() >= 50)) {
                    Log.d("Label", output.getNameList());
                    return false;
                }
            }
        }
        return true;
    }

    private void updateLearningRate(int iteration) {
        mLearningRate = INITIAL_LEARNING_RATE * Math.exp((-1.d * iteration) / mEpochs);
    }

    private void updateNeighborRadius(int iteration) {
        mNeighborRadius = MAP_RADIUS * Math.exp((-1.d * iteration) / mNeighbor_Time_Const);
        /*int offset = (int) Math.round(mEpochs * 1.d / INITIAL_NEIGHBOR_RADIUS);
        if (iteration % offset == 0) {
            mNeighborRadius--;
        }

        if (mNeighborRadius < 0) {
            mNeighborRadius = 0;
        }*/
    }

    private double neighborInfluence(int neighborIndex_X, int neighborIndex_Y, int BMU_index_X, int BMU_index_Y) {
        double distance = Math.pow(neighborIndex_X - BMU_index_X, 2) + Math.pow(neighborIndex_Y - BMU_index_Y, 2);
        double value = -1 * distance;
        value /= (2 * (Math.pow(mNeighborRadius, 2)));
        return Math.exp(value);
    }
}
