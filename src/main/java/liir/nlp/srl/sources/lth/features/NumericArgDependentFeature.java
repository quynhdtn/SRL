package liir.nlp.srl.sources.lth.features;
import se.lth.cs.srl.features.FeatureName;
import se.lth.cs.srl.features.TargetWord;
import se.lth.cs.srl.corpus.*;

/**
 * Created by quynhdo on 27/08/15.
 */
public abstract class NumericArgDependentFeature extends NumericArgDependentAttrFeature{
    private static final long serialVersionUID = 1L;


    protected NumericArgDependentFeature(FeatureName name,TargetWord tw,String POSPrefix) {
        super(name, null, tw, POSPrefix);

    }

    abstract Double getValue(String w);

    @Override
    public Double getFeatureValue(Sentence s, int predIndex, int argIndex) {
        Word w=wordExtractor.getWord(s, predIndex, argIndex);
        if(w==null)
            return null;
        else
            return getValue(w.getForm());

    }
    @Override
    public Double getFeatureValue(Predicate pred, Word arg) {
        Word w=wordExtractor.getWord(pred, arg);
        if(w==null)
            return null;
        else
            return getValue(w.getForm());
    }
}
