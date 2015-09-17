package liir.nlp.srl.sources.lth.pipeline;
import liir.nlp.srl.sources.lth.features.FeatureSetExtended;
import liir.nlp.srl.sources.lth.ml.LearningProblemExtended;
import se.lth.cs.srl.pipeline.PredicateIdentifier;
import se.lth.cs.srl.features.FeatureSet;
import se.lth.cs.srl.features.Feature;
import se.lth.cs.srl.corpus.Sentence;

import java.util.Collection;
import java.util.TreeSet;
import java.util.ArrayList;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.Learn;
import se.lth.cs.srl.corpus.Predicate;
import  liir.nlp.srl.sources.lth.features.NumericFeature;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import liir.nlp.srl.sources.lth.liblinear.LibLinearLearningProblemExtended;
/**
 * Created by quynhdo on 26/08/15.
 */
public class PredicateIdentifierExtended  extends  PredicateIdentifier{

    protected FeatureSetExtended numeric_featureSet;
    protected Map<String,LearningProblemExtended> learningProblems;

    public PredicateIdentifierExtended(FeatureSet fs) {
        super(fs);
    }

    public void getNumericFeatureSet(FeatureSetExtended fs){
        numeric_featureSet = fs;
    }

    public void prepareLearning(String filePrefix){
        learningProblems=new HashMap<String,LearningProblemExtended>();
        for(String POS:featureSet.POSPrefixes){
            File dataFile=new File(Learn.learnOptions.tempDir,filePrefix+POS);
            System.out.println(dataFile.getAbsolutePath());
            learningProblems.put(POS, new LibLinearLearningProblemExtended(dataFile,false));
        }
    }


    public void extractInstances(Sentence s){
		/*
		 * We add an instance if it
		 * 1) Is a predicate. Then either to its specific classifier, or the fallback one. (if fallback behavior is specified, i.e. skipNonMatchingPredicates=false
		 * 2) Is not a predicate, but matches the POS-tag
		 */
        for(int i=1,size=s.size();i<size;++i){
            Word potentialPredicate=s.get(i);
            String POS=potentialPredicate.getPOS();
            String POSPrefix=null;
            for(String prefix:featureSet.POSPrefixes){
                if(POS.startsWith(prefix)){
                    POSPrefix=prefix;
                    break;
                }
            }
            if(POSPrefix==null){ //It matches a prefix, we will use it for sure.
                if(!Learn.learnOptions.skipNonMatchingPredicates && potentialPredicate instanceof Predicate){
                    POSPrefix=featureSet.POSPrefixes[0];
                } else {
                    continue; //Its just some word we dont care about
                }
            }
            Integer label= potentialPredicate instanceof Predicate ? POSITIVE : NEGATIVE;
                addInstance(s,i,POSPrefix,label);
        }
    }

    private void addInstance(Sentence s, int i, String POSPrefix,Integer label) {
        LearningProblemExtended lp=learningProblems.get(POSPrefix);
        Collection<Integer> indices=new TreeSet<Integer>();
        ArrayList<Double> numeric_indices= new ArrayList<Double>();
        Integer offset=0;
        for(Feature f:featureSet.get(POSPrefix)){
            f.addFeatures(s,indices,i,-1,offset,true);
            offset+=f.size(true);
        }

        for(NumericFeature f: numeric_featureSet.get(POSPrefix)){
            f.addFeatures(s, numeric_indices,i, -1,true);
        }
        lp.addInstance(label, indices, numeric_indices, offset);

      //  lp.addInstance(label, indices);

    }
}
