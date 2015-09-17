package liir.nlp.srl.sources.lth.features;
import se.lth.cs.srl.features.FeatureName;
import se.lth.cs.srl.features.TargetWord;
import se.lth.cs.srl.corpus.*;
import se.lth.cs.srl.corpus.Word.WordData;

/**
 * Created by quynhdo on 27/08/15.
 */
public class NumericPredDependentAttrFeature extends NumericAttrFeature {
    private static final long serialVersionUID = 1L;

    protected NumericPredDependentAttrFeature(FeatureName name, WordData attr,	TargetWord tw, boolean usedForPredicateIdentification, String POSPrefix) {
        super(name, attr, tw, false, usedForPredicateIdentification, POSPrefix);
    }

    @Override
    public Double getFeatureValue(Sentence s, int predIndex, int argIndex) {
        return Double.parseDouble(wordExtractor.getWord(s, predIndex, argIndex).getAttr(attr));
    }

    @Override
    public Double getFeatureValue(Predicate pred, Word arg) {
        return Double.parseDouble(wordExtractor.getWord(pred, arg).getAttr(attr));
    }

}
