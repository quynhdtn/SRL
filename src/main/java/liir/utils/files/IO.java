package liir.utils.files;

import is2.data.SentenceData09;
import liir.nlp.representation.Text;
import liir.utils.types.Tuple3;
import org.xml.sax.SAXException;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.corpus.Yield;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.joox.JOOX.$;


/**
 * Created by quynhdo on 31/08/15.
 */
public class IO {
    public static String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    }


    public static String toMuseXML(SentenceData09 se){

        StringBuilder sb=new StringBuilder();
        sb.append("<s id=\"\">\n");

        for (int i=0;i<se.forms.length;i++){

            if (i==0 && se.forms[i].equals(""))
                continue;
            sb.append("\t<w id=\""+ String.valueOf(i)  + "\" str=\"" + se.forms[i] + "\"" + " lemma=\"" + se.plemmas[i] + "\"" + " pos=\"" + se.ppos[i] + "\"" + " head=\"");
            if (se.pheads[i] == -1 )
                sb.append("\"" + " deprel=\"" + se.plabels[i] +    "\" />\n");
            else
                sb.append(String.valueOf(se.pheads[i])+ "\"" + " deprel=\"" + se.plabels[i] +    "\" /> \n");


        }

        sb.append("</s>");

        return sb.toString();
    }

    public static String toMuseXML(Sentence se, String id){

        StringBuilder sb=new StringBuilder();
        sb.append("<s id=\"" + id + "\">" + "\n");

        for (int i=0;i<se.size();i++){

            if (i==0 && se.get(i).getForm().equals(""))
                continue;
            sb.append("\t<w id=\""+ String.valueOf(i) +  "\" str=\"" + se.get(i).getForm() + "\"" + " lemma=\"" + se.get(i).getLemma() + "\"" + " pos=\"" + se.get(i).getPOS() + "\"" + " head=\"");
            if (se.get(i).getHeadId() == -1 )
                sb.append("\"" + " deprel=\"" + se.get(i).getDeprel() +    "\" />\n");
            else
                sb.append(String.valueOf(se.get(i).getHeadId())+ "\"" + " deprel=\"" + se.get(i).getDeprel() +    "\" /> \n");


        }

        sb.append("</s>");

        return sb.toString();
    }

    public static String toMuseXMLSimple(Predicate pred, String senId){

        StringBuilder sb=new StringBuilder();
        Sentence s= pred.getMySentence();

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
                Tuple3<Integer,Integer, Integer> t = new Tuple3<Integer,Integer, Integer>(y.first().getIdx() , y.last().getIdx(), s.indexOf(arg));

                argmap2.put(t, y.getArgLabel());

            }





        }



        sb.append("<frame senId=\"" +senId+ "\" sense=\"" + pred.getSense() + "\">\n");
        sb.append("\t<pred wId=\""+ String.valueOf(s.indexOf(pred)) +"\" />\n");

        for (Tuple3<Integer,Integer, Integer> arg: argmap2.keySet())
        {
            sb.append("\t<argument beginWId=\"" + String.valueOf(arg.getFirst()) +
                    "\"  endWId=\"" +String.valueOf(arg.getSecond()) +
                    "" +
                    "\" headWId=\""+ String.valueOf(arg.getThird()) +"\" label=\"" + argmap2.get(arg) + "\" />\n");
        }

        sb.append("</frame>\n");

        return sb.toString();
    }






    public static List<List<SentenceData09>> readXMLToSentenceData09(String xmlContent) throws IOException, SAXException {
        StringReader sr = new StringReader(  "<musecorpus>" + xmlContent + "</musecorpus>" );

        List<List<SentenceData09>> corpus = new ArrayList<>();
        $(sr)
                .find("text")
                .each(ctx -> {
                    List<SentenceData09> text = new ArrayList<>();

                    $(ctx).find("s").each(stx -> {
                        SentenceData09 instance = new SentenceData09();
                        ArrayList<String> forms = new ArrayList<String>();
                        ArrayList<String> lemmas = new ArrayList<String>();
                        ArrayList<String> poss = new ArrayList<String>();
                        ArrayList<Integer> heads = new ArrayList<Integer>();
                        ArrayList<String> deprels = new ArrayList<String>();
                        forms.add("");
                        lemmas.add("");
                        poss.add("");
                        heads.add(-1);
                        deprels.add("");


                        $(stx).find("w").each(
                                wtx -> {

                                    forms.add($(wtx).attr("str"));
                                    lemmas.add($(wtx).attr("lemma"));
                                    poss.add($(wtx).attr("pos"));
                                    if ($(wtx).attr("head") != null) {
                                        if (!$(wtx).attr("head").equals(""))
                                            heads.add(Integer.parseInt($(wtx).attr("head")));
                                        else
                                            heads.add(-1);
                                    } else
                                        heads.add(-1);

                                    deprels.add($(wtx).attr("deprel"));


                                }


                        );
                        int[] ints = new int[heads.size()];
                        for (int j = 0; j < heads.size(); j++)
                            ints[j] = heads.get(j);
                        instance.init(forms.toArray(new String[forms.size()]));
                        instance.plemmas = lemmas.toArray(new String[lemmas.size()]);
                        instance.ppos = poss.toArray(new String[poss.size()]);
                        instance.pheads = ints;
                        instance.plabels = deprels.toArray(new String[deprels.size()]);

                        instance.pfeats = new String[forms.size()];
                        text.add(instance);

                    });

                    corpus.add(text);

                });

        return corpus;
    }


    public static List<List<Sentence>> readXMLToSentence(String xmlContent) throws IOException, SAXException {
        StringReader sr = new StringReader(  "<musecorpus>" + xmlContent + "</musecorpus>" );

        List<List<Sentence>> corpus = new ArrayList<>();
        $(sr)
                .find("text")
                .each(ctx -> {
                    List<Sentence> text = new ArrayList<>();

                    $(ctx).find("s").each(stx -> {
                        SentenceData09 instance = new SentenceData09();
                        ArrayList<String> forms = new ArrayList<String>();
                        ArrayList<String> lemmas = new ArrayList<String>();
                        ArrayList<String> poss = new ArrayList<String>();
                        ArrayList<Integer> heads = new ArrayList<Integer>();
                        ArrayList<String> deprels = new ArrayList<String>();



                        $(stx).find("w").each(
                                wtx -> {

                                    forms.add($(wtx).attr("str"));
                                    lemmas.add($(wtx).attr("lemma"));
                                    poss.add($(wtx).attr("pos"));
                                    if ($(wtx).attr("head") != null) {
                                        if (!$(wtx).attr("head").equals(""))
                                            heads.add(Integer.parseInt($(wtx).attr("head")));
                                        else
                                            heads.add(-1);
                                    } else
                                        heads.add(-1);

                                    deprels.add($(wtx).attr("deprel"));


                                }


                        );
                        int[] ints = new int[heads.size()];
                        for (int j = 0; j < heads.size(); j++)
                            ints[j] = heads.get(j);
                        instance.init(forms.toArray(new String[forms.size()]));
                        instance.plemmas = lemmas.toArray(new String[lemmas.size()]);
                        instance.ppos = poss.toArray(new String[poss.size()]);
                        instance.pheads = ints;
                        instance.plabels = deprels.toArray(new String[deprels.size()]);

                        instance.pfeats = new String[forms.size()];
                        text.add(new Sentence(instance, false));

                    });

                    corpus.add(text);

                });

        return corpus;
    }


    public static List<List<SentenceData09>> textToSentenceData09(List<Text> texts) throws IOException, SAXException {

        List<List<SentenceData09>> corpus = new ArrayList<>();
        for (Text txt : texts){
                    List<SentenceData09> text = new ArrayList<>();

                    for (liir.nlp.representation.Sentence s : txt){
                        SentenceData09 instance = new SentenceData09();
                        ArrayList<String> forms = new ArrayList<String>();
                        ArrayList<String> lemmas = new ArrayList<String>();
                        ArrayList<String> poss = new ArrayList<String>();
                        ArrayList<Integer> heads = new ArrayList<Integer>();
                        ArrayList<String> deprels = new ArrayList<String>();
                        forms.add("");
                        lemmas.add("");
                        poss.add("");
                        heads.add(-1);
                        deprels.add("");


                        for (liir.nlp.representation.Word w : s){

                                    forms.add(w.getStr());
                                    lemmas.add(w.getLemma());
                                    poss.add(w.getPos());
                                    if (!w.getHead().equals(""))

                                            heads.add(Integer.parseInt(w.getHead()));
                                        else
                                            heads.add(-1);


                                    deprels.add(w.getDeprel());


                                }

                        int[] ints = new int[heads.size()];
                        for (int j = 0; j < heads.size(); j++)
                            ints[j] = heads.get(j);
                        instance.init(forms.toArray(new String[forms.size()]));
                        instance.plemmas = lemmas.toArray(new String[lemmas.size()]);
                        instance.ppos = poss.toArray(new String[poss.size()]);
                        instance.pheads = ints;
                        instance.plabels = deprels.toArray(new String[deprels.size()]);

                        instance.pfeats = new String[forms.size()];
                        text.add(instance);

                    }

                    corpus.add(text);

                }

        return corpus;
    }


    public static List<SentenceData09> textToSentenceData09(Text txt) throws IOException, SAXException {

            List<SentenceData09> text = new ArrayList<>();

            for (liir.nlp.representation.Sentence s : txt){
                SentenceData09 instance = new SentenceData09();
                ArrayList<String> forms = new ArrayList<String>();
                ArrayList<String> lemmas = new ArrayList<String>();
                ArrayList<String> poss = new ArrayList<String>();
                ArrayList<Integer> heads = new ArrayList<Integer>();
                ArrayList<String> deprels = new ArrayList<String>();
                forms.add("");
                lemmas.add("");
                poss.add("");
                heads.add(-1);
                deprels.add("");


                for (liir.nlp.representation.Word w : s){

                    forms.add(w.getStr());
                    lemmas.add(w.getLemma());
                    poss.add(w.getPos());
                    if (!w.getHead().equals(""))

                        heads.add(Integer.parseInt(w.getHead()));
                    else
                        heads.add(-1);


                    deprels.add(w.getDeprel());


                }

                int[] ints = new int[heads.size()];
                for (int j = 0; j < heads.size(); j++)
                    ints[j] = heads.get(j);
                instance.init(forms.toArray(new String[forms.size()]));
                instance.plemmas = lemmas.toArray(new String[lemmas.size()]);
                instance.ppos = poss.toArray(new String[poss.size()]);
                instance.pheads = ints;
                instance.plabels = deprels.toArray(new String[deprels.size()]);

                instance.pfeats = new String[forms.size()];
                text.add(instance);

            }




        return text;
    }
}
