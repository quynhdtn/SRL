package liir.nlp.srl.sources.lth.interfaces;

import is2.data.SentenceData09;
import is2.tag.Tagger;
import liir.nlp.representation.Sentence;
import liir.nlp.representation.Text;
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
public class LundTagger {
    Tagger t;
    public LundTagger(String modelPath) throws IOException {
        t = BohnetHelper.getTagger(new File(modelPath));

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
        List<liir.nlp.representation.Word> lst = Tokenizer.processs(str);

        liir.nlp.sources.stanford.SentenceSplitter sp =new liir.nlp.sources.stanford.SentenceSplitter();
        liir.nlp.representation.Text t = sp.process(lst);
        t.setAutomaticIndexing();
        System.out.print(t.toXMLString());
        String txt=ll.process("<corpus>" + t.toXMLString() + "</corpus>");
        System.out.println(txt);
        txt=
        lt.process("<corpus>" + txt + "</corpus>");
        System.out.println(txt);
        txt=lp.process("<corpus>" + txt + "</corpus>");
        System.out.println(txt);
        txt = srl.process(txt);
        System.out.println(txt);



    }
}
