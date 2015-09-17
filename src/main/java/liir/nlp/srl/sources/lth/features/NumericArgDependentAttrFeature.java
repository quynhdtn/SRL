package liir.nlp.srl.sources.lth.features;

import se.lth.cs.srl.corpus.Word.WordData;
import se.lth.cs.srl.features.TargetWord;
import se.lth.cs.srl.features.FeatureName;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import java.util.Collection;

/**
 * Created by quynhdo on 27/08/15.
 */
public class NumericArgDependentAttrFeature extends NumericAttrFeature {
    private static final long serialVersionUID = 1L;

    protected NumericArgDependentAttrFeature(FeatureName name, WordData attr, TargetWord tw, String POSPrefix) {
        super(name, attr, tw, true, false, POSPrefix);
    }



    @Override
    public Double getFeatureValue(Sentence s, int predIndex, int argIndex) {
        Word w=wordExtractor.getWord(s, predIndex, argIndex);
        if(w==null)
            return null;
        else
            return Double.parseDouble(w.getAttr(attr));
    }

    @Override
    public Double getFeatureValue(Predicate pred, Word arg) {
        Word w=wordExtractor.getWord(pred, arg);
        if(w==null)
            return null;
        else
            return Double.parseDouble(w.getAttr(attr));
    }

    @Override
    public void addFeatures(Sentence s, Collection<Double> indices, int predIndex, int argIndex, boolean allWords) {
        addFeatures(indices,getFeatureValue(s,predIndex,argIndex),allWords);

    }
    @Override
    public void addFeatures(Collection<Double> indices,Predicate pred,Word arg, boolean allWords){
        addFeatures(indices,getFeatureValue(pred,arg), allWords);
    }
    private void addFeatures(Collection<Double> indices,Double featureVal,boolean allWords){

            indices.add(featureVal);
    }
}
