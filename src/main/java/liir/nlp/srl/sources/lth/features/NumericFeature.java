package liir.nlp.srl.sources.lth.features;

import java.io.Serializable;
import java.util.Collection;

import se.lth.cs.srl.Learn;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.features.FeatureName;
/**
 * Created by quynhdo on 27/08/15.
 */
public abstract class NumericFeature implements Serializable {
    private static final long serialVersionUID = 1L;

 //   protected Map<String,Integer> indices=new HashMap<String,Integer>();
 //   protected int indexcounter=1;
 //    protected int predMaxIndex;
    protected FeatureName name;

    protected final boolean includeArgs;
    private final boolean usedForPredicateIdentification;

    protected String POSPrefix;

    public abstract void addFeatures(Sentence s,Collection<Double> indices, int predIndex, int argIndex, boolean allWords); //This way the collection can be both a set or a list (ie one that can allow multiple identical values, or not, depending on choice from above)
    public abstract void addFeatures(Collection<Double> indices,Predicate pred, Word arg,boolean allWords);

    protected NumericFeature(FeatureName name,boolean includeArgs,boolean usedForPredicateIdentification,String POSPrefix){
        this.name=name;
        this.includeArgs=includeArgs;
        this.usedForPredicateIdentification=usedForPredicateIdentification;
        this.POSPrefix=POSPrefix;
    }


    public String toString(){
        StringBuilder ret=new StringBuilder(getName()+", "+this.getClass().toString());

        ret.append(", POSPrefix: "+POSPrefix);
        return ret.toString();
    }
    public String getName() {
        return name.toString();
    }

    public void addPOSPrefix(String prefix) {
        //Prefix is null then do nothing (this is used for QFeatures)
        //or the current prefix is a prefix of the added one
        if(prefix==null)
            return;
        if(POSPrefix==null){
            POSPrefix=prefix;
            return;
        }
        if(prefix.startsWith(POSPrefix))
            return;
        if(POSPrefix.startsWith(prefix)){
            POSPrefix=prefix;
        } else { //We need to find the longest common prefix of the two prefix strings
            int len=0;
            for(int max=Math.min(prefix.length(),POSPrefix.length());len<max;++len){
                if(prefix.charAt(len)!=POSPrefix.charAt(len))
                    break;
            }
            POSPrefix=prefix.substring(0,len);
        }
    }

    protected boolean doExtractFeatures(Word pred){
        return pred.getPOS().startsWith(POSPrefix) || (usedForPredicateIdentification && !Learn.learnOptions.skipNonMatchingPredicates && pred instanceof Predicate);
    }


}
