package liir.nlp.srl.sources.lth.pipeline;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.PredicateReference;
import se.lth.cs.srl.features.Feature;
import se.lth.cs.srl.features.FeatureSet;
import se.lth.cs.srl.ml.Model;
import se.lth.cs.srl.ml.liblinear.Label;
import se.lth.cs.srl.pipeline.PredicateDisambiguator;

/**
 * Created by quynhdo on 25/08/15.
 */
public class PredicateDisambiguatorExtended extends PredicateDisambiguator {

    private FeatureSet featureSet;
    private PredicateReference predicateReference;

    public PredicateDisambiguatorExtended(FeatureSet featureSet, PredicateReference predicateReference) {
        super(featureSet, predicateReference);
        this.featureSet=featureSet;
        this.predicateReference=predicateReference;
    }


    public HashMap<String,Double> parseProb(Predicate pred) {

        HashMap<String,Double> senses = new HashMap<String,Double>();
        //	for(Predicate pred:s.getPredicates()){
        String POSPrefix=getPOSPrefix(pred);
        String lemma=pred.getLemma();
        String sense;
        if(POSPrefix==null){
            sense=predicateReference.getSimpleSense(pred, null);
            senses.put(sense, 1.0);

        } else {

            String filename=predicateReference.getFileName(lemma,POSPrefix);
            if(filename==null){
                sense=predicateReference.getSimpleSense(pred,POSPrefix);

                senses.put(sense, 1.0);
            } else {
                Model m=getModel(filename);
                Collection<Integer> indices=new TreeSet<Integer>();
                Integer offset=0;
                for(Feature f:featureSet.get(POSPrefix)){
                    f.addFeatures(indices,pred,null,offset,false);
                    offset+=f.size(false);
                }
                //	Integer label=m.classify(indices);
                List<Label> labels = m.classifyProb(indices);
                for (Label l : labels){
                    String sese = predicateReference.getSense(lemma,POSPrefix,l.getLabel());
                    senses.put(sese, l.getProb());
                }

            }
        }

        return senses;

    }

    private String getPOSPrefix(Predicate pred) {
        for(String prefix:featureSet.POSPrefixes){
            if(pred.getPOS().startsWith(prefix))
                return prefix;
        }
        return null;
    }


    private Model getModel(String filename){
        return models.get(filename);
    }

}
