package liir.nlp.srl.sources.lth.features;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quynhdo on 27/08/15.
 */
public class FeatureSetExtended extends HashMap<String,List<NumericFeature>>{
    private static final long serialVersionUID = 1L;
    /**
     * The prefixes of this Map, sorted in reverse order, i.e. longer
     * prefixes go before shorter. And the empty string comes last.
     */
    public final String[] POSPrefixes;
    /**
     * Constructs a featureset based on the argument.
     * The POSPrefixes are extracted are sorted in reverse order on construction.
     *
     * @param featureSet the featureset
     */
    public FeatureSetExtended(Map<String,List<NumericFeature>> featureSet){
        super(featureSet);
        POSPrefixes=this.keySet().toArray(new String[0]);
        //Arrays.sort(POSPrefixes,Collections.reverseOrder());
        Arrays.sort(POSPrefixes);
    }
}