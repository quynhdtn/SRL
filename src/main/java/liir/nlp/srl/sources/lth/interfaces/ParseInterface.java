package liir.nlp.srl.sources.lth.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipFile;

import liir.nlp.srl.sources.lth.options.ParseOptionExtended;
import liir.nlp.srl.sources.lth.preprocessor.PreprocessorExtended;
import liir.nlp.srl.sources.lth.languages.EnglishExtended;
import liir.nlp.srl.sources.lth.pipeline.PipelineExtended;
import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.corpus.Yield;
import se.lth.cs.srl.io.CoNLL09Writer;
import se.lth.cs.srl.io.DepsOnlyCoNLL09Reader;
import se.lth.cs.srl.io.SentenceWriter;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.languages.Language.L;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;

import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.pipeline.Step;
import liir.utils.types.Tuple2;
import liir.utils.types.Tuple3;


/**
 * Created by quynhdo on 25/08/15.
 */
public class ParseInterface {

    private PreprocessorExtended pp;
    private SemanticRoleLabeler srl;
    private  CompletePipelineCMDLineOptions options;
    public ParseOptionExtended parseOptions;
    private static ParseInterface instance;



    public static synchronized ParseInterface getInstance(String lemmatizerModel, String posModel, String parserModel, String srlModel,   boolean useReranker, boolean skipPI) throws Exception
    {
        if(instance == null)
        {
            instance = new ParseInterface( lemmatizerModel, posModel, parserModel, srlModel,  useReranker, skipPI);

        }
        return instance;
    }
    private ParseInterface(String lemmatizerModel, String posModel, String parserModel, String srlModel, boolean useReranker, boolean skipPI) throws Exception
    {
        Language.setLanguage(L.eng);
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


    public Map<Integer, Map<String, List<List<Integer>>>> parse(List<String> text) throws IOException
    {
        String[] textt= text.toArray(new String[text.size()]);

        String[] textt2= new String[text.size()+1];
        String[] textt3= new String[text.size()+1];
        String[] textt4= new String[text.size()+1];
        textt2[0]="";
        for (int i=0;i<textt.length;i++)
        {
            String[] tmps=textt[i].split("\\s+");
            if (tmps.length>1)
            {
                textt2[i+1]=tmps[1];
                textt3[i+1]=tmps[3];
                textt4[i+1]=tmps[5];

            }

            else
                textt2[i+1]=tmps[0];
        }


        Sentence s = new Sentence(pp.preprocess(textt2,textt3,textt4),false);



        System.out.println("Start parsing... ");
        srl.parseSentence(s);

        System.out.println("Finish!");
        return getResults(s);

    }


    // text: List of tokens
    public Map<Tuple2<Integer,String>,Map<Tuple3<Integer,Integer, Integer>, String>> parseFull(List<String> text) throws IOException
    {

        String[] textt= text.toArray(new String[text.size()]);

        Sentence s = new Sentence(pp.preprocess(textt),false);

        System.out.println("Start parsing... ");
      //   ((PipelineExtended) srl).predicateIdentify(s);
       //  List<Predicate > preds =  s.getPredicates();
        //HashMap<String,Double> rs =   ((PipelineExtended) srl).PredicateDisambiguatorProb(preds.get(0), s);

        srl.parseSentence(s);

        System.out.println("Finish!");
        return getResults2(s);

    }


    public Tuple2<Map<Tuple2<Integer,String>,Map<Tuple3<Integer,Integer, Integer>, String>>, ArrayList<Integer>> parseFullWithDependencies(List<String> text) throws IOException
    {

        String[] textt= text.toArray(new String[text.size()]);

        Sentence s = new Sentence(pp.preprocess(textt),false);


        srl.parseSentence(s);

        ArrayList<Integer> dependencies = new ArrayList<Integer>();
        for (Word w : s){
            if (!w.isBOS()){
                dependencies.add(w.getHeadId());

            }
        }


        return new Tuple2<Map<Tuple2<Integer,String>,Map<Tuple3<Integer,Integer, Integer>, String>>, ArrayList<Integer>>( getResults2(s), dependencies);

    }


    public List<Map<Integer, Map<String, List<List<Integer>>>>> parse(String pathToFile) throws IOException
    {
        DepsOnlyCoNLL09Reader reader=new DepsOnlyCoNLL09Reader(new File(pathToFile));
        List<Sentence> sens=reader.readAll();
        List<Map<Integer, Map<String, List<List<Integer>>>>> lst=new ArrayList<Map<Integer,Map<String,List<List<Integer>>>>>();

        System.out.println("Start parsing... ");
        for (Sentence s : sens){

            List<String> tokens=new ArrayList<String>();
            List<String> lemmas=new ArrayList<String>();
            List<String> poses=new ArrayList<String>();
            for (Word w : s)
            {
                if (!w.isBOS())
                {
                    tokens.add(w.getForm());
                    lemmas.add(w.getLemma());
                    poses.add(w.getPOS());
                }
            }



            //	srl.parseSentence(s);

            lst.add(parse(tokens));
        }

        System.out.println("Finish!");
        return lst ;

    }

    public void parse(String pathToFile, String output) throws IOException
    {
        DepsOnlyCoNLL09Reader reader=new DepsOnlyCoNLL09Reader(new File(pathToFile));
        List<Sentence> sens=reader.readAll();
        List<Map<Integer, Map<String, List<List<Integer>>>>> lst=new ArrayList<Map<Integer,Map<String,List<List<Integer>>>>>();

        System.out.println("Start parsing... ");
        SentenceWriter writer=new CoNLL09Writer(new File(output));
        for (Sentence s : sens){

            List<String> tokens=new ArrayList<String>();
            List<String> lemmas=new ArrayList<String>();
            List<String> poses=new ArrayList<String>();
            for (Word w : s)
            {
                if (!w.isBOS())
                {
                    tokens.add(w.getForm());
                    lemmas.add(w.getLemma());
                    poses.add(w.getPOS());
                }
            }



            srl.parseSentence(s);
            writer.write(s);

        }

        writer.close();
        System.out.println("Finish!");

    }

	/*
	private Map<Integer, Map<String, List<Integer>>> getResults(Sentence s)
	{
		Map<Integer, Map<String, List<Integer>>> m=new HashMap<Integer, Map<String,List<Integer>>>();

		for (Predicate p : s.getPredicates())
		{
				int idx= s.indexOf(p);

				Map <String, List<Integer>> args=new HashMap<String, List<Integer>>();
				for (Word w : p.getArgMap().keySet())
				{
					String lbl = p.getArgumentTag(w);
					if (args.containsKey(lbl))
					{
						List<Integer> as= args.get(lbl);
						as.add(s.indexOf(w));
					}
					else
					{
						List<Integer> as=new ArrayList<Integer>();
						as.add(s.indexOf(w));
						args.put(lbl, as);
					}
				}

				m.put(idx, args);

		}
		return m;

	}
	*/


    //// Result is a Map of <Predicate_ID , Map{Label: List of all words forming the argument}}

    private Map<Integer, Map<String, List<List<Integer>>>> getResults(Sentence s)
    {
        Map<Integer, Map<String, List<List<Integer>>>> m=new HashMap<Integer, Map<String,List<List<Integer>>>>();

        for (Predicate pred : s.getPredicates()){
            ArrayList<String> roles=new ArrayList<String>();

            Map<String, List<List<Integer>>>  mp=new HashMap<String, List<List<Integer>>>();

            for (Word w : pred.getArgMap().keySet())
            {
                if (!roles.contains(pred.getArgumentTag(w)))
                    roles.add(pred.getArgumentTag(w));
            }
            Collections.sort(roles);




            SortedSet<Yield> yields=new TreeSet<Yield>();
            Map<Word,String> argmap=pred.getArgMap();
            for(Word arg:argmap.keySet()){
                yields.addAll(arg.getYield(pred,argmap.get(arg),argmap.keySet()).explode());
            }

            for (String role: roles){

                List<List<Integer>> lst=new ArrayList<List<Integer>>();
                for(Yield y:yields){

                    if (!y.getArgLabel().equals(role)) continue;

                    List<Integer> l=new ArrayList<Integer>();


                    if(!y.isContinuous()){ //Warn the user if we have discontinuous yields
                        System.out.println("Something wrong, we have discontinous yields!");
                        continue;
                    }

                    int first=s.indexOf(y.first());
                    int end=s.indexOf(y.last());

                    for (int j=first;j<=end;j++)
                    {

                        l.add(j);
                    }


                    lst.add(l);


                }

                mp.put(role, lst);
            }

            m.put(s.indexOf(pred), mp);

        }

        return m;

    }



    private Map<Tuple2<Integer,String>,Map<Tuple3<Integer, Integer,Integer>, String>> getResults2(Sentence s)
    {
        Map<Tuple2<Integer,String>,Map<Tuple3<Integer,Integer, Integer>, String>> rs= new HashMap<Tuple2<Integer,String>, Map<Tuple3<Integer,Integer, Integer>,String>>();
        for (Predicate pred : s.getPredicates()){
            ArrayList<String> roles=new ArrayList<String>();
            for (Word w : pred.getArgMap().keySet())
            {
                if (!roles.contains(pred.getArgumentTag(w)))
                    roles.add(pred.getArgumentTag(w));
            }
            Collections.sort(roles);

            Map<Word,String> argmap=pred.getArgMap();
            Map<Tuple3<Integer,Integer, Integer>, String> argmap2=new HashMap<Tuple3<Integer,Integer, Integer>, String>();

            for(Word arg:argmap.keySet()){
                Collection<Yield> ys = arg.getYield(pred,argmap.get(arg),argmap.keySet()).explode();

                for (Yield y : ys)
                {
                    Tuple3<Integer,Integer, Integer> t = new Tuple3<Integer,Integer, Integer>(y.first().getIdx() , y.last().getIdx(), arg.getIdx());

                    argmap2.put(t, y.getArgLabel());

                }





            }
            rs.put(new Tuple2(pred.getIdx(), pred.getSense()), argmap2);

        }

        return rs;

    }


    public static void main(String[] args) throws Exception
    {
        //MuseInterface mi=new MuseInterface("eng-srl.mdl",false);
        ParseInterface mi=ParseInterface.getInstance(
                "/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model",
                	"/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model",

                "/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.parser.model",
                "/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model",false, false);
        //new MuseInterface("/Users/quynhdo/Documents/MUSE/SRL/models/srl.model",false);
        List<String> lst=new ArrayList<String>();
        lst.add("");
        lst.add("We");
        lst.add("love");
        lst.add("dogs");

        Map<Tuple2<Integer, String>, Map<Tuple3<Integer, Integer, Integer>, String>> tmp=	mi.parseFull(lst);

        //	List<Map<Integer, Map<String, List<List<Integer>>>>> tmp=	mi.parse("/Users/quynhdo/Desktop/TEMP/conllformat2.txt");
        System.out.println(tmp.toString());

    }

}
