package liir.nlp.srl.sources.lth.preprocessor;
import se.lth.cs.srl.preprocessor.Preprocessor;
/**
 * Created by quynhdo on 26/08/15.
 */
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import is2.data.SentenceData09;
import java.util.Arrays;


public class PreprocessorExtended extends Preprocessor {

    public PreprocessorExtended (Tokenizer tokenizer,Lemmatizer lemmatizer,Tagger tagger,is2.mtag.Tagger mtagger,Parser parser){
        super(tokenizer, lemmatizer, tagger, mtagger, parser);
    }

    public SentenceData09 preprocess(String[] forms, String[] lemmas, String[] poses){
        SentenceData09 instance=new SentenceData09();
        instance.init(forms);
        instance.ppos=poses;
        instance.plemmas=lemmas;

        return preprocess(instance);
    }

    public SentenceData09 preprocess(SentenceData09 instance){
        if(lemmatizer!=null){
            long start=System.currentTimeMillis();
            lemmatizer.apply(instance);
            lemmatizeTime+=System.currentTimeMillis()-start;
        }
        if(tagger!=null){
            long start=System.currentTimeMillis();
            tagger.apply(instance);
            tagTime+=System.currentTimeMillis()-start;
        }
        if(mtagger!=null){
            long start=System.currentTimeMillis();
            mtagger.apply(instance);
            //XXX
            //Need to split the feats and put them in the right place for the parser.
            for(int i=1;i<instance.pfeats.length;++i){
                if(instance.pfeats[i]!=null && !instance.pfeats[i].equals("_"))
                    instance.feats[i]=instance.pfeats[i].split("\\|");
            }
            mtagTime+=System.currentTimeMillis()-start;
        } else {
            instance.pfeats=new String[instance.forms.length];
            Arrays.fill(instance.pfeats, "_");
        }
        if(parser!=null){
            synchronized(parser){
                long start=System.currentTimeMillis();
                instance=parser.apply(instance);
                dpTime+=System.currentTimeMillis()-start;
            }
        } else { //If there is no parser, we have to recreate the sentence object so the root dummy gets thrown out (as in the parser)
            instance=new SentenceData09(instance);
        }
        return instance;
    }
}
