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
            result += Math.pow(input.mInputData[0] - output.mWeights[0], 2);
            result += Math.pow(input.mInputData[1] - output.mWeights[1], 2);
            result += Math.pow(input.mInputData[2] - output.mWeights[2], 2);
            result += Math.pow(input.mInputData[3] - output.mWeights[3], 2);
            result += Math.pow(input.mInputData[4] - output.mWeights[4], 2);
            result += Math.pow(input.mInputData[5] - output.mWeights[5], 2);
            result += Math.pow(input.mInputData[6] - output.mWeights[6], 2);
            result += Math.pow(input.mInputData[7] - output.mWeights[7], 2);
            result += Math.pow(input.mInputData[8] - output.mWeights[8], 2);
            result += Math.pow(input.mInputData[9] - output.mWeights[9], 2);
            result += Math.pow(input.mInputData[10] - output.mWeights[10], 2);
            result += Math.pow(input.mInputData[11] - output.mWeights[11], 2);
            result += Math.pow(input.mInputData[12] - output.mWeights[12], 2);
            result += Math.pow(input.mInputData[13] - output.mWeights[13], 2);
            result += Math.pow(input.mInputData[14] - output.mWeights[14], 2);
            result += Math.pow(input.mInputData[15] - output.mWeights[15], 2);
            result += Math.pow(input.mInputData[16] - output.mWeights[16], 2);
            result += Math.pow(input.mInputData[17] - output.mWeights[17], 2);
            result += Math.pow(input.mInputData[18] - output.mWeights[18], 2);
            result += Math.pow(input.mInputData[19] - output.mWeights[19], 2);
            result += Math.pow(input.mInputData[20] - output.mWeights[20], 2);
            result += Math.pow(input.mInputData[21] - output.mWeights[21], 2);
            result += Math.pow(input.mInputData[22] - output.mWeights[22], 2);
            result += Math.pow(input.mInputData[23] - output.mWeights[23], 2);
            result += Math.pow(input.mInputData[24] - output.mWeights[24], 2);
            result += Math.pow(input.mInputData[25] - output.mWeights[25], 2);
            result += Math.pow(input.mInputData[26] - output.mWeights[26], 2);
            result += Math.pow(input.mInputData[27] - output.mWeights[27], 2);
            result += Math.pow(input.mInputData[28] - output.mWeights[28], 2);
            result += Math.pow(input.mInputData[29] - output.mWeights[29], 2);
            result += Math.pow(input.mInputData[30] - output.mWeights[30], 2);
            result += Math.pow(input.mInputData[31] - output.mWeights[31], 2);
            result += Math.pow(input.mInputData[32] - output.mWeights[32], 2);
            result += Math.pow(input.mInputData[33] - output.mWeights[33], 2);
            result += Math.pow(input.mInputData[34] - output.mWeights[34], 2);
            result += Math.pow(input.mInputData[35] - output.mWeights[35], 2);
            result += Math.pow(input.mInputData[36] - output.mWeights[36], 2);
            result += Math.pow(input.mInputData[37] - output.mWeights[37], 2);
            result += Math.pow(input.mInputData[38] - output.mWeights[38], 2);
            result += Math.pow(input.mInputData[39] - output.mWeights[39], 2);
            result += Math.pow(input.mInputData[40] - output.mWeights[40], 2);
            result += Math.pow(input.mInputData[41] - output.mWeights[41], 2);
            result += Math.pow(input.mInputData[42] - output.mWeights[42], 2);
            result += Math.pow(input.mInputData[43] - output.mWeights[43], 2);
            result += Math.pow(input.mInputData[44] - output.mWeights[44], 2);
            result += Math.pow(input.mInputData[45] - output.mWeights[45], 2);
            result += Math.pow(input.mInputData[46] - output.mWeights[46], 2);
            result += Math.pow(input.mInputData[47] - output.mWeights[47], 2);
            result += Math.pow(input.mInputData[48] - output.mWeights[48], 2);
            result += Math.pow(input.mInputData[49] - output.mWeights[49], 2);
            result += Math.pow(input.mInputData[50] - output.mWeights[50], 2);
            result += Math.pow(input.mInputData[51] - output.mWeights[51], 2);
            result += Math.pow(input.mInputData[52] - output.mWeights[52], 2);
            result += Math.pow(input.mInputData[53] - output.mWeights[53], 2);
            result += Math.pow(input.mInputData[54] - output.mWeights[54], 2);
            result += Math.pow(input.mInputData[55] - output.mWeights[55], 2);
            result += Math.pow(input.mInputData[56] - output.mWeights[56], 2);
            result += Math.pow(input.mInputData[57] - output.mWeights[57], 2);
            result += Math.pow(input.mInputData[58] - output.mWeights[58], 2);
            result += Math.pow(input.mInputData[59] - output.mWeights[59], 2);
            result += Math.pow(input.mInputData[60] - output.mWeights[60], 2);
            result += Math.pow(input.mInputData[61] - output.mWeights[61], 2);
            result += Math.pow(input.mInputData[62] - output.mWeights[62], 2);
            result += Math.pow(input.mInputData[63] - output.mWeights[63], 2);
            result += Math.pow(input.mInputData[64] - output.mWeights[64], 2);
            result += Math.pow(input.mInputData[65] - output.mWeights[65], 2);
            result += Math.pow(input.mInputData[66] - output.mWeights[66], 2);
            result += Math.pow(input.mInputData[67] - output.mWeights[67], 2);
            result += Math.pow(input.mInputData[68] - output.mWeights[68], 2);
            result += Math.pow(input.mInputData[69] - output.mWeights[69], 2);
            result += Math.pow(input.mInputData[70] - output.mWeights[70], 2);
            result += Math.pow(input.mInputData[71] - output.mWeights[71], 2);
            result += Math.pow(input.mInputData[72] - output.mWeights[72], 2);
            result += Math.pow(input.mInputData[73] - output.mWeights[73], 2);
            result += Math.pow(input.mInputData[74] - output.mWeights[74], 2);
            result += Math.pow(input.mInputData[75] - output.mWeights[75], 2);
            result += Math.pow(input.mInputData[76] - output.mWeights[76], 2);
            result += Math.pow(input.mInputData[77] - output.mWeights[77], 2);
            result += Math.pow(input.mInputData[78] - output.mWeights[78], 2);
            result += Math.pow(input.mInputData[79] - output.mWeights[79], 2);
            result += Math.pow(input.mInputData[80] - output.mWeights[80], 2);
            result += Math.pow(input.mInputData[81] - output.mWeights[81], 2);
            result += Math.pow(input.mInputData[82] - output.mWeights[82], 2);
            result += Math.pow(input.mInputData[83] - output.mWeights[83], 2);
            result += Math.pow(input.mInputData[84] - output.mWeights[84], 2);
            result += Math.pow(input.mInputData[85] - output.mWeights[85], 2);
            result += Math.pow(input.mInputData[86] - output.mWeights[86], 2);
            result += Math.pow(input.mInputData[87] - output.mWeights[87], 2);
            result += Math.pow(input.mInputData[88] - output.mWeights[88], 2);
            result += Math.pow(input.mInputData[89] - output.mWeights[89], 2);
            result += Math.pow(input.mInputData[90] - output.mWeights[90], 2);
            result += Math.pow(input.mInputData[91] - output.mWeights[91], 2);
            result += Math.pow(input.mInputData[92] - output.mWeights[92], 2);
            result += Math.pow(input.mInputData[93] - output.mWeights[93], 2);
            result += Math.pow(input.mInputData[94] - output.mWeights[94], 2);
            result += Math.pow(input.mInputData[95] - output.mWeights[95], 2);
            result += Math.pow(input.mInputData[96] - output.mWeights[96], 2);
            result += Math.pow(input.mInputData[97] - output.mWeights[97], 2);
            result += Math.pow(input.mInputData[98] - output.mWeights[98], 2);
            result += Math.pow(input.mInputData[99] - output.mWeights[99], 2);
            result += Math.pow(input.mInputData[100] - output.mWeights[100], 2);
            result += Math.pow(input.mInputData[101] - output.mWeights[101], 2);
            result += Math.pow(input.mInputData[102] - output.mWeights[102], 2);
            result += Math.pow(input.mInputData[103] - output.mWeights[103], 2);
            result += Math.pow(input.mInputData[104] - output.mWeights[104], 2);
            result += Math.pow(input.mInputData[105] - output.mWeights[105], 2);
            result += Math.pow(input.mInputData[106] - output.mWeights[106], 2);
            result += Math.pow(input.mInputData[107] - output.mWeights[107], 2);
            result += Math.pow(input.mInputData[108] - output.mWeights[108], 2);
            result += Math.pow(input.mInputData[109] - output.mWeights[109], 2);
            result += Math.pow(input.mInputData[110] - output.mWeights[110], 2);
            result += Math.pow(input.mInputData[111] - output.mWeights[111], 2);
            result += Math.pow(input.mInputData[112] - output.mWeights[112], 2);
            result += Math.pow(input.mInputData[113] - output.mWeights[113], 2);
            result += Math.pow(input.mInputData[114] - output.mWeights[114], 2);
            result += Math.pow(input.mInputData[115] - output.mWeights[115], 2);
            result += Math.pow(input.mInputData[116] - output.mWeights[116], 2);
            result += Math.pow(input.mInputData[117] - output.mWeights[117], 2);
            result += Math.pow(input.mInputData[118] - output.mWeights[118], 2);
            result += Math.pow(input.mInputData[119] - output.mWeights[119], 2);
            result += Math.pow(input.mInputData[120] - output.mWeights[120], 2);
            result += Math.pow(input.mInputData[121] - output.mWeights[121], 2);
            result += Math.pow(input.mInputData[122] - output.mWeights[122], 2);
            result += Math.pow(input.mInputData[123] - output.mWeights[123], 2);
            result += Math.pow(input.mInputData[124] - output.mWeights[124], 2);
            result += Math.pow(input.mInputData[125] - output.mWeights[125], 2);
            result += Math.pow(input.mInputData[126] - output.mWeights[126], 2);
            result += Math.pow(input.mInputData[127] - output.mWeights[127], 2);
            result += Math.pow(input.mInputData[128] - output.mWeights[128], 2);
            result += Math.pow(input.mInputData[129] - output.mWeights[129], 2);
            result += Math.pow(input.mInputData[130] - output.mWeights[130], 2);
            result += Math.pow(input.mInputData[131] - output.mWeights[131], 2);
            result += Math.pow(input.mInputData[132] - output.mWeights[132], 2);
            result += Math.pow(input.mInputData[133] - output.mWeights[133], 2);
            result += Math.pow(input.mInputData[134] - output.mWeights[134], 2);
            result += Math.pow(input.mInputData[135] - output.mWeights[135], 2);
            result += Math.pow(input.mInputData[136] - output.mWeights[136], 2);
            result += Math.pow(input.mInputData[137] - output.mWeights[137], 2);
            result += Math.pow(input.mInputData[138] - output.mWeights[138], 2);
            result += Math.pow(input.mInputData[139] - output.mWeights[139], 2);
            result += Math.pow(input.mInputData[140] - output.mWeights[140], 2);
            result += Math.pow(input.mInputData[141] - output.mWeights[141], 2);
            result += Math.pow(input.mInputData[142] - output.mWeights[142], 2);
            result += Math.pow(input.mInputData[143] - output.mWeights[143], 2);
            result += Math.pow(input.mInputData[144] - output.mWeights[144], 2);
            result += Math.pow(input.mInputData[145] - output.mWeights[145], 2);
            result += Math.pow(input.mInputData[146] - output.mWeights[146], 2);
            result += Math.pow(input.mInputData[147] - output.mWeights[147], 2);
            result += Math.pow(input.mInputData[148] - output.mWeights[148], 2);
            result += Math.pow(input.mInputData[149] - output.mWeights[149], 2);
            result += Math.pow(input.mInputData[150] - output.mWeights[150], 2);
            result += Math.pow(input.mInputData[151] - output.mWeights[151], 2);
            result += Math.pow(input.mInputData[152] - output.mWeights[152], 2);
            result += Math.pow(input.mInputData[153] - output.mWeights[153], 2);
            result += Math.pow(input.mInputData[154] - output.mWeights[154], 2);
            result += Math.pow(input.mInputData[155] - output.mWeights[155], 2);
            result += Math.pow(input.mInputData[156] - output.mWeights[156], 2);
            result += Math.pow(input.mInputData[157] - output.mWeights[157], 2);
            result += Math.pow(input.mInputData[158] - output.mWeights[158], 2);
            result += Math.pow(input.mInputData[159] - output.mWeights[159], 2);
            result += Math.pow(input.mInputData[160] - output.mWeights[160], 2);
            result += Math.pow(input.mInputData[161] - output.mWeights[161], 2);
            result += Math.pow(input.mInputData[162] - output.mWeights[162], 2);
            result += Math.pow(input.mInputData[163] - output.mWeights[163], 2);
            result += Math.pow(input.mInputData[164] - output.mWeights[164], 2);
            result += Math.pow(input.mInputData[165] - output.mWeights[165], 2);
            result += Math.pow(input.mInputData[166] - output.mWeights[166], 2);
            result += Math.pow(input.mInputData[167] - output.mWeights[167], 2);
            result += Math.pow(input.mInputData[168] - output.mWeights[168], 2);
            result += Math.pow(input.mInputData[169] - output.mWeights[169], 2);
            result += Math.pow(input.mInputData[170] - output.mWeights[170], 2);
            result += Math.pow(input.mInputData[171] - output.mWeights[171], 2);
            result += Math.pow(input.mInputData[172] - output.mWeights[172], 2);
            result += Math.pow(input.mInputData[173] - output.mWeights[173], 2);
            result += Math.pow(input.mInputData[174] - output.mWeights[174], 2);
            result += Math.pow(input.mInputData[175] - output.mWeights[175], 2);
            result += Math.pow(input.mInputData[176] - output.mWeights[176], 2);
            result += Math.pow(input.mInputData[177] - output.mWeights[177], 2);
            result += Math.pow(input.mInputData[178] - output.mWeights[178], 2);
            result += Math.pow(input.mInputData[179] - output.mWeights[179], 2);
            result += Math.pow(input.mInputData[180] - output.mWeights[180], 2);
            result += Math.pow(input.mInputData[181] - output.mWeights[181], 2);
            result += Math.pow(input.mInputData[182] - output.mWeights[182], 2);
            result += Math.pow(input.mInputData[183] - output.mWeights[183], 2);
            result += Math.pow(input.mInputData[184] - output.mWeights[184], 2);
            result += Math.pow(input.mInputData[185] - output.mWeights[185], 2);
            result += Math.pow(input.mInputData[186] - output.mWeights[186], 2);
            result += Math.pow(input.mInputData[187] - output.mWeights[187], 2);
            result += Math.pow(input.mInputData[188] - output.mWeights[188], 2);
            result += Math.pow(input.mInputData[189] - output.mWeights[189], 2);
            result += Math.pow(input.mInputData[190] - output.mWeights[190], 2);
            result += Math.pow(input.mInputData[191] - output.mWeights[191], 2);
            result += Math.pow(input.mInputData[192] - output.mWeights[192], 2);
            result += Math.pow(input.mInputData[193] - output.mWeights[193], 2);
            result += Math.pow(input.mInputData[194] - output.mWeights[194], 2);
            result += Math.pow(input.mInputData[195] - output.mWeights[195], 2);
            result += Math.pow(input.mInputData[196] - output.mWeights[196], 2);
            result += Math.pow(input.mInputData[197] - output.mWeights[197], 2);
            result += Math.pow(input.mInputData[198] - output.mWeights[198], 2);
            result += Math.pow(input.mInputData[199] - output.mWeights[199], 2);
            result += Math.pow(input.mInputData[200] - output.mWeights[200], 2);
            result += Math.pow(input.mInputData[201] - output.mWeights[201], 2);
            result += Math.pow(input.mInputData[202] - output.mWeights[202], 2);
            result += Math.pow(input.mInputData[203] - output.mWeights[203], 2);
            result += Math.pow(input.mInputData[204] - output.mWeights[204], 2);
            result += Math.pow(input.mInputData[205] - output.mWeights[205], 2);
            result += Math.pow(input.mInputData[206] - output.mWeights[206], 2);
            result += Math.pow(input.mInputData[207] - output.mWeights[207], 2);
            result += Math.pow(input.mInputData[208] - output.mWeights[208], 2);
            result += Math.pow(input.mInputData[209] - output.mWeights[209], 2);
            result += Math.pow(input.mInputData[210] - output.mWeights[210], 2);
            result += Math.pow(input.mInputData[211] - output.mWeights[211], 2);
            result += Math.pow(input.mInputData[212] - output.mWeights[212], 2);
            result += Math.pow(input.mInputData[213] - output.mWeights[213], 2);
            result += Math.pow(input.mInputData[214] - output.mWeights[214], 2);
            result += Math.pow(input.mInputData[215] - output.mWeights[215], 2);
            result += Math.pow(input.mInputData[216] - output.mWeights[216], 2);
            result += Math.pow(input.mInputData[217] - output.mWeights[217], 2);
            result += Math.pow(input.mInputData[218] - output.mWeights[218], 2);
            result += Math.pow(input.mInputData[219] - output.mWeights[219], 2);
            result += Math.pow(input.mInputData[220] - output.mWeights[220], 2);
            result += Math.pow(input.mInputData[221] - output.mWeights[221], 2);
            result += Math.pow(input.mInputData[222] - output.mWeights[222], 2);
            result += Math.pow(input.mInputData[223] - output.mWeights[223], 2);
            result += Math.pow(input.mInputData[224] - output.mWeights[224], 2);
            result += Math.pow(input.mInputData[225] - output.mWeights[225], 2);
            result += Math.pow(input.mInputData[226] - output.mWeights[226], 2);
            result += Math.pow(input.mInputData[227] - output.mWeights[227], 2);
            result += Math.pow(input.mInputData[228] - output.mWeights[228], 2);
            result += Math.pow(input.mInputData[229] - output.mWeights[229], 2);
            result += Math.pow(input.mInputData[230] - output.mWeights[230], 2);
            result += Math.pow(input.mInputData[231] - output.mWeights[231], 2);
            result += Math.pow(input.mInputData[232] - output.mWeights[232], 2);
            result += Math.pow(input.mInputData[233] - output.mWeights[233], 2);
            result += Math.pow(input.mInputData[234] - output.mWeights[234], 2);
            result += Math.pow(input.mInputData[235] - output.mWeights[235], 2);
            result += Math.pow(input.mInputData[236] - output.mWeights[236], 2);
            result += Math.pow(input.mInputData[237] - output.mWeights[237], 2);
            result += Math.pow(input.mInputData[238] - output.mWeights[238], 2);
            result += Math.pow(input.mInputData[239] - output.mWeights[239], 2);
            result += Math.pow(input.mInputData[240] - output.mWeights[240], 2);
            result += Math.pow(input.mInputData[241] - output.mWeights[241], 2);
            result += Math.pow(input.mInputData[242] - output.mWeights[242], 2);
            result += Math.pow(input.mInputData[243] - output.mWeights[243], 2);
            result += Math.pow(input.mInputData[244] - output.mWeights[244], 2);
            result += Math.pow(input.mInputData[245] - output.mWeights[245], 2);
            result += Math.pow(input.mInputData[246] - output.mWeights[246], 2);
            result += Math.pow(input.mInputData[247] - output.mWeights[247], 2);
            result += Math.pow(input.mInputData[248] - output.mWeights[248], 2);
            result += Math.pow(input.mInputData[249] - output.mWeights[249], 2);
            result += Math.pow(input.mInputData[250] - output.mWeights[250], 2);
            result += Math.pow(input.mInputData[251] - output.mWeights[251], 2);
            result += Math.pow(input.mInputData[252] - output.mWeights[252], 2);
            result += Math.pow(input.mInputData[253] - output.mWeights[253], 2);
            result += Math.pow(input.mInputData[254] - output.mWeights[254], 2);
            result += Math.pow(input.mInputData[255] - output.mWeights[255], 2);
            result += Math.pow(input.mInputData[256] - output.mWeights[256], 2);
            result += Math.pow(input.mInputData[257] - output.mWeights[257], 2);
            result += Math.pow(input.mInputData[258] - output.mWeights[258], 2);
            result += Math.pow(input.mInputData[259] - output.mWeights[259], 2);
            result += Math.pow(input.mInputData[260] - output.mWeights[260], 2);
            result += Math.pow(input.mInputData[261] - output.mWeights[261], 2);
            result += Math.pow(input.mInputData[262] - output.mWeights[262], 2);
            result += Math.pow(input.mInputData[263] - output.mWeights[263], 2);
            result += Math.pow(input.mInputData[264] - output.mWeights[264], 2);
            result += Math.pow(input.mInputData[265] - output.mWeights[265], 2);
            result += Math.pow(input.mInputData[266] - output.mWeights[266], 2);
            result += Math.pow(input.mInputData[267] - output.mWeights[267], 2);
            result += Math.pow(input.mInputData[268] - output.mWeights[268], 2);
            result += Math.pow(input.mInputData[269] - output.mWeights[269], 2);
            result += Math.pow(input.mInputData[270] - output.mWeights[270], 2);
            result += Math.pow(input.mInputData[271] - output.mWeights[271], 2);
            result += Math.pow(input.mInputData[272] - output.mWeights[272], 2);
            result += Math.pow(input.mInputData[273] - output.mWeights[273], 2);
            result += Math.pow(input.mInputData[274] - output.mWeights[274], 2);
            result += Math.pow(input.mInputData[275] - output.mWeights[275], 2);
            result += Math.pow(input.mInputData[276] - output.mWeights[276], 2);
            result += Math.pow(input.mInputData[277] - output.mWeights[277], 2);
            result += Math.pow(input.mInputData[278] - output.mWeights[278], 2);
            result += Math.pow(input.mInputData[279] - output.mWeights[279], 2);
            result += Math.pow(input.mInputData[280] - output.mWeights[280], 2);
            result += Math.pow(input.mInputData[281] - output.mWeights[281], 2);
            result += Math.pow(input.mInputData[282] - output.mWeights[282], 2);
            result += Math.pow(input.mInputData[283] - output.mWeights[283], 2);
            result += Math.pow(input.mInputData[284] - output.mWeights[284], 2);
            result += Math.pow(input.mInputData[285] - output.mWeights[285], 2);
            result += Math.pow(input.mInputData[286] - output.mWeights[286], 2);
            result += Math.pow(input.mInputData[287] - output.mWeights[287], 2);
            result += Math.pow(input.mInputData[288] - output.mWeights[288], 2);
            result += Math.pow(input.mInputData[289] - output.mWeights[289], 2);
            result += Math.pow(input.mInputData[290] - output.mWeights[290], 2);
            result += Math.pow(input.mInputData[291] - output.mWeights[291], 2);
            result += Math.pow(input.mInputData[292] - output.mWeights[292], 2);
            result += Math.pow(input.mInputData[293] - output.mWeights[293], 2);
            result += Math.pow(input.mInputData[294] - output.mWeights[294], 2);
            result += Math.pow(input.mInputData[295] - output.mWeights[295], 2);
            result += Math.pow(input.mInputData[296] - output.mWeights[296], 2);
            result += Math.pow(input.mInputData[297] - output.mWeights[297], 2);
            result += Math.pow(input.mInputData[298] - output.mWeights[298], 2);
            result += Math.pow(input.mInputData[299] - output.mWeights[299], 2);
            result += Math.pow(input.mInputData[300] - output.mWeights[300], 2);
            result += Math.pow(input.mInputData[301] - output.mWeights[301], 2);
            result += Math.pow(input.mInputData[302] - output.mWeights[302], 2);
            result += Math.pow(input.mInputData[303] - output.mWeights[303], 2);
            result += Math.pow(input.mInputData[304] - output.mWeights[304], 2);
            result += Math.pow(input.mInputData[305] - output.mWeights[305], 2);
            result += Math.pow(input.mInputData[306] - output.mWeights[306], 2);
            result += Math.pow(input.mInputData[307] - output.mWeights[307], 2);
            result += Math.pow(input.mInputData[308] - output.mWeights[308], 2);
            result += Math.pow(input.mInputData[309] - output.mWeights[309], 2);
            result += Math.pow(input.mInputData[310] - output.mWeights[310], 2);
            result += Math.pow(input.mInputData[311] - output.mWeights[311], 2);
            result += Math.pow(input.mInputData[312] - output.mWeights[312], 2);
            result += Math.pow(input.mInputData[313] - output.mWeights[313], 2);
            result += Math.pow(input.mInputData[314] - output.mWeights[314], 2);
            result += Math.pow(input.mInputData[315] - output.mWeights[315], 2);
            result += Math.pow(input.mInputData[316] - output.mWeights[316], 2);
            result += Math.pow(input.mInputData[317] - output.mWeights[317], 2);
            result += Math.pow(input.mInputData[318] - output.mWeights[318], 2);
            result += Math.pow(input.mInputData[319] - output.mWeights[319], 2);
            result += Math.pow(input.mInputData[320] - output.mWeights[320], 2);
            result += Math.pow(input.mInputData[321] - output.mWeights[321], 2);
            result += Math.pow(input.mInputData[322] - output.mWeights[322], 2);
            result += Math.pow(input.mInputData[323] - output.mWeights[323], 2);
            result += Math.pow(input.mInputData[324] - output.mWeights[324], 2);
            result += Math.pow(input.mInputData[325] - output.mWeights[325], 2);
            result += Math.pow(input.mInputData[326] - output.mWeights[326], 2);
            result += Math.pow(input.mInputData[327] - output.mWeights[327], 2);
            result += Math.pow(input.mInputData[328] - output.mWeights[328], 2);
            result += Math.pow(input.mInputData[329] - output.mWeights[329], 2);
            result += Math.pow(input.mInputData[330] - output.mWeights[330], 2);
            result += Math.pow(input.mInputData[331] - output.mWeights[331], 2);
            result += Math.pow(input.mInputData[332] - output.mWeights[332], 2);
            result += Math.pow(input.mInputData[333] - output.mWeights[333], 2);
            result += Math.pow(input.mInputData[334] - output.mWeights[334], 2);
            result += Math.pow(input.mInputData[335] - output.mWeights[335], 2);
            result += Math.pow(input.mInputData[336] - output.mWeights[336], 2);
            result += Math.pow(input.mInputData[337] - output.mWeights[337], 2);
            result += Math.pow(input.mInputData[338] - output.mWeights[338], 2);
            result += Math.pow(input.mInputData[339] - output.mWeights[339], 2);
            result += Math.pow(input.mInputData[340] - output.mWeights[340], 2);
            result += Math.pow(input.mInputData[341] - output.mWeights[341], 2);
            result += Math.pow(input.mInputData[342] - output.mWeights[342], 2);
            result += Math.pow(input.mInputData[343] - output.mWeights[343], 2);
            result += Math.pow(input.mInputData[344] - output.mWeights[344], 2);
            result += Math.pow(input.mInputData[345] - output.mWeights[345], 2);
            result += Math.pow(input.mInputData[346] - output.mWeights[346], 2);
            result += Math.pow(input.mInputData[347] - output.mWeights[347], 2);
            result += Math.pow(input.mInputData[348] - output.mWeights[348], 2);
            result += Math.pow(input.mInputData[349] - output.mWeights[349], 2);
            result += Math.pow(input.mInputData[350] - output.mWeights[350], 2);
            result += Math.pow(input.mInputData[351] - output.mWeights[351], 2);
            result += Math.pow(input.mInputData[352] - output.mWeights[352], 2);
            result += Math.pow(input.mInputData[353] - output.mWeights[353], 2);
            result += Math.pow(input.mInputData[354] - output.mWeights[354], 2);
            result += Math.pow(input.mInputData[355] - output.mWeights[355], 2);
            result += Math.pow(input.mInputData[356] - output.mWeights[356], 2);
            result += Math.pow(input.mInputData[357] - output.mWeights[357], 2);
            result += Math.pow(input.mInputData[358] - output.mWeights[358], 2);
            result += Math.pow(input.mInputData[359] - output.mWeights[359], 2);
            result += Math.pow(input.mInputData[360] - output.mWeights[360], 2);
            result += Math.pow(input.mInputData[361] - output.mWeights[361], 2);
            result += Math.pow(input.mInputData[362] - output.mWeights[362], 2);
            result += Math.pow(input.mInputData[363] - output.mWeights[363], 2);
            result += Math.pow(input.mInputData[364] - output.mWeights[364], 2);
            result += Math.pow(input.mInputData[365] - output.mWeights[365], 2);
            result += Math.pow(input.mInputData[366] - output.mWeights[366], 2);
            result += Math.pow(input.mInputData[367] - output.mWeights[367], 2);
            result += Math.pow(input.mInputData[368] - output.mWeights[368], 2);
            result += Math.pow(input.mInputData[369] - output.mWeights[369], 2);
            result += Math.pow(input.mInputData[370] - output.mWeights[370], 2);
            result += Math.pow(input.mInputData[371] - output.mWeights[371], 2);
            result += Math.pow(input.mInputData[372] - output.mWeights[372], 2);
            result += Math.pow(input.mInputData[373] - output.mWeights[373], 2);
            result += Math.pow(input.mInputData[374] - output.mWeights[374], 2);
            result += Math.pow(input.mInputData[375] - output.mWeights[375], 2);
            result += Math.pow(input.mInputData[376] - output.mWeights[376], 2);
            result += Math.pow(input.mInputData[377] - output.mWeights[377], 2);
            result += Math.pow(input.mInputData[378] - output.mWeights[378], 2);
            result += Math.pow(input.mInputData[379] - output.mWeights[379], 2);
            result += Math.pow(input.mInputData[380] - output.mWeights[380], 2);
            result += Math.pow(input.mInputData[381] - output.mWeights[381], 2);
            result += Math.pow(input.mInputData[382] - output.mWeights[382], 2);
            result += Math.pow(input.mInputData[383] - output.mWeights[383], 2);
            result += Math.pow(input.mInputData[384] - output.mWeights[384], 2);
            result += Math.pow(input.mInputData[385] - output.mWeights[385], 2);
            result += Math.pow(input.mInputData[386] - output.mWeights[386], 2);
            result += Math.pow(input.mInputData[387] - output.mWeights[387], 2);
            result += Math.pow(input.mInputData[388] - output.mWeights[388], 2);
            result += Math.pow(input.mInputData[389] - output.mWeights[389], 2);
            result += Math.pow(input.mInputData[390] - output.mWeights[390], 2);
            result += Math.pow(input.mInputData[391] - output.mWeights[391], 2);
            result += Math.pow(input.mInputData[392] - output.mWeights[392], 2);
            result += Math.pow(input.mInputData[393] - output.mWeights[393], 2);
            result += Math.pow(input.mInputData[394] - output.mWeights[394], 2);
            result += Math.pow(input.mInputData[395] - output.mWeights[395], 2);
            result += Math.pow(input.mInputData[396] - output.mWeights[396], 2);
            result += Math.pow(input.mInputData[397] - output.mWeights[397], 2);
            result += Math.pow(input.mInputData[398] - output.mWeights[398], 2);
            result += Math.pow(input.mInputData[399] - output.mWeights[399], 2);
            result += Math.pow(input.mInputData[400] - output.mWeights[400], 2);
            result += Math.pow(input.mInputData[401] - output.mWeights[401], 2);
            result += Math.pow(input.mInputData[402] - output.mWeights[402], 2);
            result += Math.pow(input.mInputData[403] - output.mWeights[403], 2);
            result += Math.pow(input.mInputData[404] - output.mWeights[404], 2);
            result += Math.pow(input.mInputData[405] - output.mWeights[405], 2);
            result += Math.pow(input.mInputData[406] - output.mWeights[406], 2);
            result += Math.pow(input.mInputData[407] - output.mWeights[407], 2);
            result += Math.pow(input.mInputData[408] - output.mWeights[408], 2);
            result += Math.pow(input.mInputData[409] - output.mWeights[409], 2);
            result += Math.pow(input.mInputData[410] - output.mWeights[410], 2);
            result += Math.pow(input.mInputData[411] - output.mWeights[411], 2);
            result += Math.pow(input.mInputData[412] - output.mWeights[412], 2);
            result += Math.pow(input.mInputData[413] - output.mWeights[413], 2);
            result += Math.pow(input.mInputData[414] - output.mWeights[414], 2);
            result += Math.pow(input.mInputData[415] - output.mWeights[415], 2);
            result += Math.pow(input.mInputData[416] - output.mWeights[416], 2);
            result += Math.pow(input.mInputData[417] - output.mWeights[417], 2);
            result += Math.pow(input.mInputData[418] - output.mWeights[418], 2);
            result += Math.pow(input.mInputData[419] - output.mWeights[419], 2);
            result += Math.pow(input.mInputData[420] - output.mWeights[420], 2);
            result += Math.pow(input.mInputData[421] - output.mWeights[421], 2);
            result += Math.pow(input.mInputData[422] - output.mWeights[422], 2);
            result += Math.pow(input.mInputData[423] - output.mWeights[423], 2);
            result += Math.pow(input.mInputData[424] - output.mWeights[424], 2);
            result += Math.pow(input.mInputData[425] - output.mWeights[425], 2);
            result += Math.pow(input.mInputData[426] - output.mWeights[426], 2);
            result += Math.pow(input.mInputData[427] - output.mWeights[427], 2);
            result += Math.pow(input.mInputData[428] - output.mWeights[428], 2);
            result += Math.pow(input.mInputData[429] - output.mWeights[429], 2);
            result += Math.pow(input.mInputData[430] - output.mWeights[430], 2);
            result += Math.pow(input.mInputData[431] - output.mWeights[431], 2);
            result += Math.pow(input.mInputData[432] - output.mWeights[432], 2);
            result += Math.pow(input.mInputData[433] - output.mWeights[433], 2);
            result += Math.pow(input.mInputData[434] - output.mWeights[434], 2);
            result += Math.pow(input.mInputData[435] - output.mWeights[435], 2);
            result += Math.pow(input.mInputData[436] - output.mWeights[436], 2);
            result += Math.pow(input.mInputData[437] - output.mWeights[437], 2);
            result += Math.pow(input.mInputData[438] - output.mWeights[438], 2);
            result += Math.pow(input.mInputData[439] - output.mWeights[439], 2);
            result += Math.pow(input.mInputData[440] - output.mWeights[440], 2);
            result += Math.pow(input.mInputData[441] - output.mWeights[441], 2);
            result += Math.pow(input.mInputData[442] - output.mWeights[442], 2);
            result += Math.pow(input.mInputData[443] - output.mWeights[443], 2);
            result += Math.pow(input.mInputData[444] - output.mWeights[444], 2);
            result += Math.pow(input.mInputData[445] - output.mWeights[445], 2);
            result += Math.pow(input.mInputData[446] - output.mWeights[446], 2);
            result += Math.pow(input.mInputData[447] - output.mWeights[447], 2);
            result += Math.pow(input.mInputData[448] - output.mWeights[448], 2);
            result += Math.pow(input.mInputData[449] - output.mWeights[449], 2);
            result += Math.pow(input.mInputData[450] - output.mWeights[450], 2);
            result += Math.pow(input.mInputData[451] - output.mWeights[451], 2);
            result += Math.pow(input.mInputData[452] - output.mWeights[452], 2);
            result += Math.pow(input.mInputData[453] - output.mWeights[453], 2);
            result += Math.pow(input.mInputData[454] - output.mWeights[454], 2);
            result += Math.pow(input.mInputData[455] - output.mWeights[455], 2);
            result += Math.pow(input.mInputData[456] - output.mWeights[456], 2);
            result += Math.pow(input.mInputData[457] - output.mWeights[457], 2);
            result += Math.pow(input.mInputData[458] - output.mWeights[458], 2);
            result += Math.pow(input.mInputData[459] - output.mWeights[459], 2);
            result += Math.pow(input.mInputData[460] - output.mWeights[460], 2);
            result += Math.pow(input.mInputData[461] - output.mWeights[461], 2);
            result += Math.pow(input.mInputData[462] - output.mWeights[462], 2);
            result += Math.pow(input.mInputData[463] - output.mWeights[463], 2);
            result += Math.pow(input.mInputData[464] - output.mWeights[464], 2);
            result += Math.pow(input.mInputData[465] - output.mWeights[465], 2);
            result += Math.pow(input.mInputData[466] - output.mWeights[466], 2);
            result += Math.pow(input.mInputData[467] - output.mWeights[467], 2);
            result += Math.pow(input.mInputData[468] - output.mWeights[468], 2);
            result += Math.pow(input.mInputData[469] - output.mWeights[469], 2);
            result += Math.pow(input.mInputData[470] - output.mWeights[470], 2);
            result += Math.pow(input.mInputData[471] - output.mWeights[471], 2);
            result += Math.pow(input.mInputData[472] - output.mWeights[472], 2);
            result += Math.pow(input.mInputData[473] - output.mWeights[473], 2);
            result += Math.pow(input.mInputData[474] - output.mWeights[474], 2);
            result += Math.pow(input.mInputData[475] - output.mWeights[475], 2);
            result += Math.pow(input.mInputData[476] - output.mWeights[476], 2);
            result += Math.pow(input.mInputData[477] - output.mWeights[477], 2);
            result += Math.pow(input.mInputData[478] - output.mWeights[478], 2);
            result += Math.pow(input.mInputData[479] - output.mWeights[479], 2);
            result += Math.pow(input.mInputData[480] - output.mWeights[480], 2);
            result += Math.pow(input.mInputData[481] - output.mWeights[481], 2);
            result += Math.pow(input.mInputData[482] - output.mWeights[482], 2);
            result += Math.pow(input.mInputData[483] - output.mWeights[483], 2);
            result += Math.pow(input.mInputData[484] - output.mWeights[484], 2);
            result += Math.pow(input.mInputData[485] - output.mWeights[485], 2);
            result += Math.pow(input.mInputData[486] - output.mWeights[486], 2);
            result += Math.pow(input.mInputData[487] - output.mWeights[487], 2);
            result += Math.pow(input.mInputData[488] - output.mWeights[488], 2);
            result += Math.pow(input.mInputData[489] - output.mWeights[489], 2);
            result += Math.pow(input.mInputData[490] - output.mWeights[490], 2);
            result += Math.pow(input.mInputData[491] - output.mWeights[491], 2);
            result += Math.pow(input.mInputData[492] - output.mWeights[492], 2);
            result += Math.pow(input.mInputData[493] - output.mWeights[493], 2);
            result += Math.pow(input.mInputData[494] - output.mWeights[494], 2);
            result += Math.pow(input.mInputData[495] - output.mWeights[495], 2);
            result += Math.pow(input.mInputData[496] - output.mWeights[496], 2);
            result += Math.pow(input.mInputData[497] - output.mWeights[497], 2);
            result += Math.pow(input.mInputData[498] - output.mWeights[498], 2);
            result += Math.pow(input.mInputData[499] - output.mWeights[499], 2);
            result += Math.pow(input.mInputData[500] - output.mWeights[500], 2);
            result += Math.pow(input.mInputData[501] - output.mWeights[501], 2);
            result += Math.pow(input.mInputData[502] - output.mWeights[502], 2);
            result += Math.pow(input.mInputData[503] - output.mWeights[503], 2);
            result += Math.pow(input.mInputData[504] - output.mWeights[504], 2);
            result += Math.pow(input.mInputData[505] - output.mWeights[505], 2);
            result += Math.pow(input.mInputData[506] - output.mWeights[506], 2);
            result += Math.pow(input.mInputData[507] - output.mWeights[507], 2);
            result += Math.pow(input.mInputData[508] - output.mWeights[508], 2);
            result += Math.pow(input.mInputData[509] - output.mWeights[509], 2);
            result += Math.pow(input.mInputData[510] - output.mWeights[510], 2);
            result += Math.pow(input.mInputData[511] - output.mWeights[511], 2);
            result += Math.pow(input.mInputData[512] - output.mWeights[512], 2);
            result += Math.pow(input.mInputData[513] - output.mWeights[513], 2);
            result += Math.pow(input.mInputData[514] - output.mWeights[514], 2);
            result += Math.pow(input.mInputData[515] - output.mWeights[515], 2);
            result += Math.pow(input.mInputData[516] - output.mWeights[516], 2);
            result += Math.pow(input.mInputData[517] - output.mWeights[517], 2);
            result += Math.pow(input.mInputData[518] - output.mWeights[518], 2);
            result += Math.pow(input.mInputData[519] - output.mWeights[519], 2);
            result += Math.pow(input.mInputData[520] - output.mWeights[520], 2);
            result += Math.pow(input.mInputData[521] - output.mWeights[521], 2);
            result += Math.pow(input.mInputData[522] - output.mWeights[522], 2);
            result += Math.pow(input.mInputData[523] - output.mWeights[523], 2);
            result += Math.pow(input.mInputData[524] - output.mWeights[524], 2);
            result += Math.pow(input.mInputData[525] - output.mWeights[525], 2);
            result += Math.pow(input.mInputData[526] - output.mWeights[526], 2);
            result += Math.pow(input.mInputData[527] - output.mWeights[527], 2);
            result += Math.pow(input.mInputData[528] - output.mWeights[528], 2);
            result += Math.pow(input.mInputData[529] - output.mWeights[529], 2);
            result += Math.pow(input.mInputData[530] - output.mWeights[530], 2);
            result += Math.pow(input.mInputData[531] - output.mWeights[531], 2);
            result += Math.pow(input.mInputData[532] - output.mWeights[532], 2);
            result += Math.pow(input.mInputData[533] - output.mWeights[533], 2);
            result += Math.pow(input.mInputData[534] - output.mWeights[534], 2);
            result += Math.pow(input.mInputData[535] - output.mWeights[535], 2);
            result += Math.pow(input.mInputData[536] - output.mWeights[536], 2);
            result += Math.pow(input.mInputData[537] - output.mWeights[537], 2);
            result += Math.pow(input.mInputData[538] - output.mWeights[538], 2);
            result += Math.pow(input.mInputData[539] - output.mWeights[539], 2);
            result += Math.pow(input.mInputData[540] - output.mWeights[540], 2);
            result += Math.pow(input.mInputData[541] - output.mWeights[541], 2);
            result += Math.pow(input.mInputData[542] - output.mWeights[542], 2);
            result += Math.pow(input.mInputData[543] - output.mWeights[543], 2);
            result += Math.pow(input.mInputData[544] - output.mWeights[544], 2);
            result += Math.pow(input.mInputData[545] - output.mWeights[545], 2);
            result += Math.pow(input.mInputData[546] - output.mWeights[546], 2);
            result += Math.pow(input.mInputData[547] - output.mWeights[547], 2);
            result += Math.pow(input.mInputData[548] - output.mWeights[548], 2);
            result += Math.pow(input.mInputData[549] - output.mWeights[549], 2);
            result += Math.pow(input.mInputData[550] - output.mWeights[550], 2);
            result += Math.pow(input.mInputData[551] - output.mWeights[551], 2);
            result += Math.pow(input.mInputData[552] - output.mWeights[552], 2);
            result += Math.pow(input.mInputData[553] - output.mWeights[553], 2);
            result += Math.pow(input.mInputData[554] - output.mWeights[554], 2);
            result += Math.pow(input.mInputData[555] - output.mWeights[555], 2);
            result += Math.pow(input.mInputData[556] - output.mWeights[556], 2);
            result += Math.pow(input.mInputData[557] - output.mWeights[557], 2);
            result += Math.pow(input.mInputData[558] - output.mWeights[558], 2);
            result += Math.pow(input.mInputData[559] - output.mWeights[559], 2);
        }
        return Math.sqrt(result);
    }
}
