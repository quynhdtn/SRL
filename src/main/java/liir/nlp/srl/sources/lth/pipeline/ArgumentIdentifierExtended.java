package liir.nlp.srl.sources.lth.pipeline;
import se.lth.cs.srl.pipeline.ArgumentIdentifier;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;


import se.lth.cs.srl.corpus.Predicate;

import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.features.FeatureSet;

import se.lth.cs.srl.ml.Model;
import se.lth.cs.srl.ml.liblinear.Label;

/**
 * Created by quynhdo on 25/08/15.
 */
public class ArgumentIdentifierExtended extends  ArgumentIdentifier{


    public ArgumentIdentifierExtended(FeatureSet fs) {
        super(fs);
    }

    public double parseProb(Predicate pred, Word arg ) {
        double d=0.0;
        String POSPrefix=super.getPOSPrefix(pred.getPOS());
        if(POSPrefix==null)
            POSPrefix=super.featureSet.POSPrefixes[0]; //TODO fix me. or discard examples with wrong POS-tags
        Model model=models.get(POSPrefix);

        Collection<Integer> indices=super.collectIndices(pred, arg, POSPrefix, new TreeSet<Integer>());
        List<Label> probs=model.classifyProb(indices);

        for(Label label:probs){
            if(label.getLabel().equals(POSITIVE)){
                d=label.getProb();
            }
        }

        return d;

    }


}
