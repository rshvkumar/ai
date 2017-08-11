package neuralnet;

public class NeuralNetwork {
    double[][] neuron;
    double[][] bias;
    double[][] biasChange;
    double[][] error;
    double[][][] weight;
    double[][][] weightChange;
    
    public NeuralNetwork(int ... size) {
        neuron = new double[size.length][];
        bias = new double[size.length][];
        biasChange = new double[size.length][];
        error = new double[neuron.length][];
        
        for (int i = 0 ; i < size.length ; i++) {
            neuron[i] = new double[size[i]];
            bias[i] = new double[size[i]];
            biasChange[i] = new double[size[i]];
            error[i] = new double[size[i]];
            
            for (int k = 0 ; k < size[i] ; k++) {
                bias[i][k] = Math.random() * 2 - 1;
            }
        }
        
        weight = new double[size.length][][];
        weightChange = new double[size.length][][];
        
        for (int i = 1 ; i < size.length ; i++) {
            weight[i] = new double[size[i]][size[i - 1]];
            weightChange[i] = new double[size[i]][size[i - 1]];
            
            for (int j = 0 ; j < size[i] ; j++) {
                for (int k = 0 ; k < size[i - 1] ; k++) {
                    weight[i][j][k] = Math.random() * 2 - 1;
                }
            }
        }
    }
    
    public double[] compute(double ... input) {
        for (int i = 0 ; i < input.length ; i++) {
            neuron[0][i] = input[i];
        }
        for (int l = 1 ; l < neuron.length ; l++) {
            for (int n = 0 ; n < neuron[l].length ; n++) {
                double total = bias[l][n];
                for (int p = 0 ; p < neuron[l - 1].length ; p++) {
                    total += neuron[l - 1][p] * weight[l][n][p];
                }
                
                neuron[l][n] = 1 / (1 + Math.pow(Math.E, -total));
            }
        }
        
        return neuron[neuron.length - 1];
    }
    
    public void propagate(double[] expected, double rate) {
    	propagate(expected, rate, 0);
    }
    
    public void propagate(double[] expected, double rate, double momentum) {
        int output = neuron.length - 1;
        for (int o = 0 ; o < neuron[output].length ; o++) {
            double outval = neuron[output][o];
            //error[output][o] = (expected[o] - outval) * (1 - Math.pow(Math.tanh(outval), 2)); //outval * (1 - outval);
            error[output][o] = (expected[o] - outval) * outval * (1 - outval);
        }
        
        for (int l = neuron.length - 2 ; l > 0 ; l--) {
            for (int n = 0 ; n < neuron[l].length ; n++) {
                double sum = 0;
                for (int f = 0 ; f < neuron[l + 1].length ; f++) {
                    sum += weight[l + 1][f][n] * error[l + 1][f];
                }
                //error[l][n] = (1 - Math.pow(Math.tanh(neuron[l][n]), 2)) * sum;
                error[l][n] = neuron[l][n] * (1 - neuron[l][n]) * sum;
            }
        }
        
        for (int l = neuron.length - 1 ; l > 0 ; l--) {
            for (int n = 0 ; n < neuron[l].length ; n++) {
                biasChange[l][n] = rate * error[l][n] + momentum * biasChange[l][n];
                bias[l][n] += biasChange[l][n];
                        
                for (int w = 0 ; w < weight[l][n].length ; w++) {
                    weightChange[l][n][w] = rate * error[l][n] * neuron[l - 1][w] + 
                            momentum * weightChange[l][n][w]; 
                    weight[l][n][w] += weightChange[l][n][w];
                }
            }
        }
    }
}