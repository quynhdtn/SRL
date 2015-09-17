package liir.nlp.srl.sources.lth.languages;
import liir.nlp.srl.sources.lth.preprocessor.PreprocessorExtended;
import se.lth.cs.srl.languages.English;

import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;

import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import java.io.IOException;
import java.io.File;

/**
 * Created by quynhdo on 26/08/15.
 */
public class EnglishExtended extends English{

    public PreprocessorExtended getPreprocessor(FullPipelineOptions options) throws IOException {
        Tokenizer tokenizer=(options.loadPreprocessorWithTokenizer ? getTokenizer(options.tokenizer): null);
        Lemmatizer lemmatizer=getLemmatizer(options.lemmatizer);
        Tagger tagger=options.tagger==null?null:BohnetHelper.getTagger(options.tagger);
        is2.mtag.Tagger mtagger=options.morph==null?null:BohnetHelper.getMTagger(options.morph);
        Parser parser=options.parser==null?null:BohnetHelper.getParser(options.parser);
        PreprocessorExtended pp=new PreprocessorExtended(tokenizer, lemmatizer, tagger, mtagger, parser);
        return pp;
    }

    Lemmatizer getLemmatizer(File lemmaModelFile) throws IOException{
        if(lemmaModelFile==null)
            return null;
        return BohnetHelper.getLemmatizer(lemmaModelFile);
    }



}
