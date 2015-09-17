package liir.nlp.srl.sources.lth.pipeline;

import se.lth.cs.srl.pipeline.ArgumentClassifier;
import java.util.List;
import se.lth.cs.srl.features.FeatureSet;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.ml.Model;
import se.lth.cs.srl.ml.liblinear.Label;
import java.util.Collection;
import java.util.TreeSet;
import java.util.HashMap;
/**
 * Created by quynhdo on 25/08/15.
 */
public class ArgumentClassifierExtended extends  ArgumentClassifier {
    private List<String> argLabels;
    public ArgumentClassifierExtended(FeatureSet fs, List<String> argLabels) {
        super(fs, argLabels);
        this.argLabels=argLabels;
    }

    /***
     * parse a case with sentence, predicate and argument
     * @param pred
     * @param s
     * @param arg
     * @return List of all possible argument label + prob
     */
    public HashMap<String,Double> parseProb(Predicate pred, Sentence s, Word arg) {
        HashMap<String,Double> rs= new HashMap<String,Double>();
        String POSPrefix=super.getPOSPrefix(pred.getPOS());
        if(POSPrefix==null)
            POSPrefix=super.featureSet.POSPrefixes[0]; //TODO fix me. or discard examples with wrong POS-tags
        Model model=models.get(POSPrefix);
        Collection<Integer> indices=super.collectIndices(pred, arg, POSPrefix, new TreeSet<Integer>());
        List<Label> probs=model.classifyProb(indices);

        for(Label label:probs){
            rs.put(argLabels.get(label.getLabel()), label.getProb());
        }

        return rs;
    }

}
