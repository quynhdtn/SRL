package liir.nlp.srl.sources.lth.liblinear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
/**
 * Created by quynhdo on 27/08/15.
 */
public abstract class WeightVectorExtended implements Serializable {
    private static final long serialVersionUID = 1L;

    protected double bias;
    protected int features;
    protected int classes;

    public WeightVectorExtended(double bias,int features,int classes){
        this.bias=bias;
        this.classes=classes;
        this.features=features;
    }

    public static WeightVectorExtended parseWeights(BufferedReader in,int features,int classes,double bias,boolean sparse) throws IOException{
        if(sparse){
            if(classes==2)
                return new BinarySparseVectorExtended(in,features,bias);
            else
                return new MultipleSparseVectorExtended(in,features,classes,bias);
        } else {
            if(classes==2)
                return new BinaryLibLinearVectorExtended(in,features,bias);
            else
                return new MultipleLibLinearVectorExtended(in,features,classes,bias);
        }
    }

    public abstract double[] computeAllProbs(Collection<Integer> ints);
    public abstract short computeBestClass(Collection<Integer> ints);

    //For binary classifiers
    public abstract static class BinaryVectorExtended extends WeightVectorExtended {
        private static final long serialVersionUID = 1L;
        public BinaryVectorExtended(double bias, int features, int classes) {
            super(bias, features, classes);
        }
        protected abstract double computeScore(Collection<Integer> ints);
        public double[] computeAllProbs(Collection<Integer> ints){
            double[] ret=new double[2];
            double prob=(1.0/(1.0+Math.exp(-computeScore(ints))));
            ret[0]=prob;
            ret[1]=1.0-prob;
            return ret;
        }
        public short computeBestClass(Collection<Integer> ints) {
            if(computeScore(ints)>0)
                return 0;
            else
                return 1;
        }
    }
    public static class BinaryLibLinearVectorExtended extends BinaryVectorExtended {
        private static final long serialVersionUID = 1L;
        private float[] weights;
        public BinaryLibLinearVectorExtended(BufferedReader in,int features,double bias) throws IOException {
            super(bias,features,2);
            weights=new float[features+1];
            String str;
            for(int i=0;(str=in.readLine())!=null;++i)
                weights[i]=Float.parseFloat(str);
        }
        protected double computeScore(Collection<Integer> ints){
            double sum=(bias>0.0?bias*weights[features]:0.0);
            for(Integer i:ints){
                if((i-1)<features)
                    sum+=weights[i-1];
            }
            return sum;
        }
    }
    public static class BinarySparseVectorExtended extends BinaryVectorExtended {
        private static final long serialVersionUID = 1L;
        private HashMap<Integer,Float> weightMap;
        public BinarySparseVectorExtended(BinaryLibLinearVectorExtended vec){
            super(vec.bias,vec.features,2);
            weightMap=new HashMap<Integer,Float>();
            for(int i=0;i<vec.features;++i){
                if(vec.weights[i]!=0)
                    weightMap.put(i,vec.weights[i]);
            }
            if(bias>0)
                weightMap.put(features,vec.weights[features]); //Bias feature
        }
        public BinarySparseVectorExtended(BufferedReader in,int features,double bias) throws IOException {
            super(bias,features,2);
            weightMap=new HashMap<Integer,Float>();
            String str;

            for(int i=0;(str=in.readLine())!=null;++i){
                Float f=Float.parseFloat(str);
                if(f!=0){
                    weightMap.put(Integer.valueOf(i),f);
                }
            }
        }
        protected double computeScore(Collection<Integer> ints){
            double sum=bias>0?(weightMap.containsKey(features)?bias*weightMap.get(features):0.d):0.d;
            for(Integer i:ints){
                if((i-1)<features && weightMap.containsKey(i-1))
                    sum+=weightMap.get(i-1);
            }
            return sum;
        }
    }


    //For multiclass classifiers
    public abstract static class MultipleVectorExtended extends WeightVectorExtended {
        private static final long serialVersionUID = 1L;
        public MultipleVectorExtended(double bias, int features, int classes) {
            super(bias, features, classes);
        }
        protected abstract double[] computeScores(Collection<Integer> ints);
        public double[] computeAllProbs(Collection<Integer> ints) {
            double[] ret=new double[classes];
            double[] scores=computeScores(ints);
            double sum=0.0;
            for(short i=0;i<classes;++i){
                ret[i]=(1.0/(1.0+Math.exp(-scores[i])));
                sum+=ret[i];
            }
            for(short i=0;i<classes;++i)
                ret[i]/=sum;
            return ret;
        }
        public short computeBestClass(Collection<Integer> ints) {
            short ret=0;
            double[] scores=computeScores(ints);
            for(short i=0;i<classes;++i){
                if(scores[i]>scores[ret])
                    ret=i;
            }
            return ret;
        }

    }
    public static class MultipleLibLinearVectorExtended extends MultipleVectorExtended {
        private static final long serialVersionUID = 1L;
        private float[][] weights;
        public MultipleLibLinearVectorExtended(BufferedReader in,int features,int classes,double bias) throws IOException {
            super(bias,features,classes);
            weights=new float[classes][features+1];
            String str;
            for(int i=0;(str=in.readLine())!=null;++i){
                String[] values=str.split(" ");
                for(int j=0;j<classes;++j)
                    weights[j][i]=Float.parseFloat(values[j]);
            }
        }
        protected double[] computeScores(Collection<Integer> ints){
            double[] ret=new double[classes];
            for(int i=0;i<classes;++i){
                double curvalue=(bias>0?bias*weights[i][features]:0);
                for(Integer in:ints){
                    if((in-1)<features)
                        curvalue+=weights[i][in-1];
                }
                ret[i]=curvalue;
            }
            return ret;
        }
    }

    public static class MultipleSparseVectorExtended extends MultipleVectorExtended {
        private static final long serialVersionUID = 1L;
        private HashMap<Integer,WeightArray> weightMap;
        public MultipleSparseVectorExtended(MultipleLibLinearVectorExtended vec){
            super(vec.bias,vec.features,vec.classes);
            weightMap=new HashMap<Integer,WeightArray>();
            for(int i=0;i<vec.features;++i){
                WeightArray wa=new WeightArray(classes);
                boolean notNull=false;
                for(int j=0;j<vec.classes;++j){
                    wa.weights[j]=vec.weights[j][i];
                    notNull=(notNull || wa.weights[j]!=0);
                }
                if(notNull)
                    weightMap.put(i,wa);
            }
            //Don't forget the bias feature
            if(bias>0){
                WeightArray wa=new WeightArray(classes);
                for(int j=0;j<classes;++j)
                    wa.weights[j]=vec.weights[j][features];
                weightMap.put(features,wa);
            }
        }

        public MultipleSparseVectorExtended(BufferedReader in,int features,int classes,double bias) throws IOException {
            super(bias,features,classes);
            weightMap=new HashMap<Integer,WeightArray>();
            String str;
            for(int i=0;(str=in.readLine())!=null;++i){
                WeightArray weights=new WeightArray(classes);
                int j=0;
                boolean nonZero=false;
                for(String w:str.split(" ")){
                    float f=Float.parseFloat(w);
                    weights.weights[j++]=f;
                    nonZero=nonZero || f!=0;
                }
                if(nonZero)
                    weightMap.put(Integer.valueOf(i),weights);
            }
        }
        protected double[] computeScores(Collection<Integer> ints){
            double[] ret=new double[classes];
            for(int i=0;i<classes;++i){
                double curvalue=bias>0?(weightMap.containsKey(features)?bias*weightMap.get(features).weights[i]:0d):0d;
                for(Integer in:ints){
                    if(weightMap.containsKey(in-1) && (in-1)<features)
                        curvalue+=weightMap.get(in-1).weights[i];
                }
                ret[i]=curvalue;
            }
            return ret;
        }
        private static class WeightArray implements Serializable {
            private static final long serialVersionUID = 1L;
            float[] weights;
            public WeightArray(int size){
                weights=new float[size];
            }
        }
    }
}