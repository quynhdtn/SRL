package liir.nlp.srl.sources.lth.interfaces;

import is2.data.SentenceData09;
import liir.nlp.srl.sources.lth.languages.EnglishExtended;
import liir.nlp.srl.sources.lth.options.ParseOptionExtended;
import liir.nlp.srl.sources.lth.pipeline.PipelineExtended;
import liir.nlp.srl.sources.lth.preprocessor.PreprocessorExtended;
import liir.utils.files.IO;
import org.xml.sax.SAXException;
import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.pipeline.Step;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import static org.joox.JOOX.$;

/**
 * Created by quynhdo on 31/08/15.
 * The input format is XML:
 * <text>
 *     <s>
 *         <w id="" str="word form" lemma= "word lemma" pos ="part of speech" head="dependency head" deprel= "dependency deprel"/>
 *         <w id="" str="" lemma= "" pos ="" head="" deprel= ""/>
 *         ...
 *     </s>
 * </text>
 */
public class MuseInterface {

    private PreprocessorExtended pp;
    private SemanticRoleLabeler srl;
    private CompletePipelineCMDLineOptions options;
    public ParseOptionExtended parseOptions;
    private static MuseInterface instance;



    public static synchronized MuseInterface getInstance(String lemmatizerModel, String posModel, String parserModel, String srlModel,   boolean useReranker, boolean skipPI) throws Exception
    {
        if(instance == null)
        {
            instance = new MuseInterface( lemmatizerModel, posModel, parserModel, srlModel,  useReranker, skipPI);

        }
        return instance;
    }
    private MuseInterface(String lemmatizerModel, String posModel, String parserModel, String srlModel, boolean useReranker, boolean skipPI) throws Exception
    {
        Language.setLanguage(Language.L.eng);
        String[] args= {srlModel, String.valueOf( useReranker), String.valueOf(skipPI) };
        parseOptions=new ParseOptionExtended(args);
        options=new CompletePipelineCMDLineOptions();
        //options.reranker=useReranker;
        options=new CompletePipelineCMDLineOptions();
        if (lemmatizerModel!=null)
            options.lemmatizer=new File(lemmatizerModel);

        if (posModel!=null)
            options.tagger=new File(posModel);

        if (parserModel!=null)
            options.parser=new File(parserModel);

        EnglishExtended eng= new EnglishExtended();
        pp= eng.getPreprocessor(options);


        //	String[] args=parseoptions.split("\\s+");
        //	parseOptions=new ParseOptions(args);
        if(parseOptions.useReranker){
            srl = new Reranker(parseOptions);
            //srl = Reranker.fromZipFile(zipFile,parseOptions.skipPI,parseOptions.global_alfa,parseOptions.global_aiBeam,parseOptions.global_acBeam);
        } else {
            ZipFile zipFile=new ZipFile(parseOptions.modelFile);
            srl = parseOptions.skipPI ? PipelineExtended.fromZipFile(zipFile, new Step[]{Step.pd, Step.ai, Step.ac}) : PipelineExtended.fromZipFile(zipFile);



            zipFile.close();

        }
    }

    private Sentence Preprocess(Sentence s){
        String[] forms= new String[s.size()];
        for (int i=0;i<s.size();i++)
            forms[i]= s.get(i).getForm();

        SentenceData09 se = pp.preprocess(forms);
        for (int i=0;i<s.size();i++){
            Word w = s.get(i);

            if (w.getLemma() != "")
                se.plemmas[i]=w.getLemma();
            if (w.getPOS() != "")
                se.ppos[i] = w.getPOS();

       //     if (w.getHeadId() != null)
         //       se.p = w.getPOS();


        }


        return s;
    }

    private List<List<Sentence>> readXMLFile(String xmlPath) throws IOException, SAXException {
        String content = IO.readFile(xmlPath);
        return null;
    }




    public static void main(String[] args) throws Exception {


    }
}

