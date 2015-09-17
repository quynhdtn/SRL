package liir.nlp.srl.sources.lth.features;
import se.lth.cs.srl.features.FeatureName;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import java.util.Collection;

/**
 * Created by quynhdo on 27/08/15.
 */
public abstract class NumericSingleFeature extends NumericFeature  {
    private static final long serialVersionUID = 1L;
    protected NumericSingleFeature(FeatureName name,boolean includeArgs,boolean usedForPredicateIdentification,String POSPrefix) {
        super(name,includeArgs,usedForPredicateIdentification,POSPrefix);
    }


    @Override
    public void addFeatures(Sentence s, Collection<Double> indices, int predIndex, int argIndex, boolean allWords) {
            indices.add(getFeatureValue(s, predIndex, argIndex));
    }
    @Override
    public void addFeatures(Collection<Double> indices,Predicate pred,Word arg,boolean allWords){

            indices.add(getFeatureValue(pred,arg));
    }

    public abstract Double getFeatureValue(Sentence s,int predIndex,int argIndex);
    public abstract Double getFeatureValue(Predicate pred,Word arg);
}
