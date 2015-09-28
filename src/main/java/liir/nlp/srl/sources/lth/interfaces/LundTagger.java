package liir.nlp.srl.sources.lth.interfaces;

import is2.data.SentenceData09;
import is2.tag.Tagger;
import liir.nlp.interfaces.preprocessing.Processor;
import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Text;
import liir.nlp.sources.stanford.SentenceSplitter;
import liir.nlp.sources.stanford.Tokenizer;
import liir.utils.files.IO;
import org.xml.sax.SAXException;
import se.lth.cs.srl.util.BohnetHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by quynhdo on 31/08/15.
 */
public class LundTagger extends Processor {
    Tagger t;
    public LundTagger(String modelPath) throws IOException {
        super("Lund Tagger");
        t = BohnetHelper.getTagger(new File(modelPath));

    }

    public Text processToText(Text txt) {
        try {
            List<SentenceData09> data = IO.textToSentenceData09(txt);

                for (int j=0;j<data.size();j++){
                    SentenceData09 sen09= data.get(j);
                    Sentence sen = txt.get(j);
                    t.apply(sen09);

                    for (int  k=1; k<sen09.forms.length; k++)
                        sen.get(k-1).setPos(sen09.ppos[k]);
                }

            return txt;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String process (String xmlContent) throws IOException, SAXException {

        List<List<SentenceData09>> data = IO.readXMLToSentenceData09(xmlContent);
        StringBuilder sb=new StringBuilder();

        sb.append("<text id=\"\">\n");


        for (List<SentenceData09> text : data)
            for (SentenceData09 s : text)
            {
                t.apply(s);
                sb.append(IO.toMuseXML(s));
                sb.append("\n");
            }

        sb.append("</text>");

        return sb.toString();



    }

    public void process (List<Text> txt) throws IOException, SAXException {

        List<List<SentenceData09>> data = IO.textToSentenceData09(txt);

        for (int i =0;i<data.size();i++){
            for (int j=0;j<data.get(i).size();j++){
                SentenceData09 sen09= data.get(i).get(j);
                Sentence sen = txt.get(i).get(j);
                t.apply(sen09);

                for (int  k=0; k<sen09.forms.length; k++)
                    sen.get(k).setPos(sen09.ppos[k]);
            }
        }


    }


    public static void main(String[] args) throws Exception {
        LundLemmatizer ll = new LundLemmatizer("/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model");
        LundTagger lt = new LundTagger("/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model");
        LundParser lp = new LundParser("/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.parser.model");
        LundSRL srl = new LundSRL("/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model");
        String str="The school closes today.";


        Tokenizer tk =new Tokenizer();


        String[] lst = tk.process(str);

        SentenceSplitter ss =new SentenceSplitter();

        Text txt = ss.processToText(lst);


       txt.setAutomaticIndexing();

        ll.processToText(txt);

        lt.processToText(txt);

        System.out.println(txt.toXMLString());


        lp.processToText(txt);

        System.out.println(txt.toXMLString());

       txt = srl.processToText(txt);


    }
}
