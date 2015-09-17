package liir.nlp.srl.sources.lth.features;

import se.lth.cs.srl.corpus.Word.WordData;
import se.lth.cs.srl.features.WordExtractor;
import se.lth.cs.srl.features.TargetWord;
import se.lth.cs.srl.features.FeatureName;

/**
 * Created by quynhdo on 27/08/15.
 */
public abstract class NumericAttrFeature  extends NumericSingleFeature {
    private static final long serialVersionUID = 1;

    protected WordData attr;
    protected WordExtractor wordExtractor;

    protected NumericAttrFeature(FeatureName name,WordData attr,TargetWord tw,boolean includeArgs,boolean usedForPredicateIdentification,String POSPrefix) {
        super(name,includeArgs,usedForPredicateIdentification,POSPrefix);
        this.attr=attr;
        this.wordExtractor=WordExtractor.getExtractor(tw);
    }

}