package liir.nlp.srl.sources.lth.ml;

import se.lth.cs.srl.ml.LearningProblem;
import java.util.Collection;
/**
 * Created by quynhdo on 27/08/15.
 */
public interface LearningProblemExtended  extends LearningProblem {
    void addInstance(int label, Collection<Integer> indices, Collection<Double> numeric_indices, int offset);

}
