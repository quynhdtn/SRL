package liir.nlp.srl.sources.lth.interfaces;

import is2.data.SentenceData09;
import is2.lemmatizer.*;
import liir.nlp.representation.Sentence;
import liir.nlp.representation.Text;
import liir.nlp.sources.stanford.Tokenizer;
import liir.utils.files.IO;
import org.xml.sax.SAXException;
import se.lth.cs.srl.util.BohnetHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


/**
 * Created by quynhdo on 31/08/15.
 */
public class LundLemmatizer {
    Lemmatizer l ;

    public LundLemmatizer(String modelPath) throws IOException {
        l = BohnetHelper.getLemmatizer(new File(modelPath));

    }

    public String process (String xmlContent) throws IOException, SAXException {

        List<List<SentenceData09>> data = IO.readXMLToSentenceData09(xmlContent);
        StringBuilder sb=new StringBuilder();

        sb.append("<text id=\"\">\n");


        for (List<SentenceData09> text : data)
            for (SentenceData09 s : text)
            {
                l.apply(s);
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
                l.apply(sen09);

                for (int  k=0; k<sen09.forms.length; k++)
                    sen.get(k).setLemma(sen09.plemmas[k]);
            }
        }


    }

    public void process (Text txt) throws IOException, SAXException {

        List<SentenceData09> data = IO.textToSentenceData09(txt);

        for (int i =0;i<data.size();i++){
                SentenceData09 sen09= data.get(i);
                Sentence sen = txt.get(i);
                l.apply(sen09);

                for (int  k=0; k<sen09.forms.length; k++)
                    sen.get(k).setLemma(sen09.plemmas[k]);
            }



    }

    public static void main(String[] args) throws Exception {
        LundLemmatizer ll = new LundLemmatizer("/Users/quynhdo/Downloads/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model");
        String str="Mary loves Peter. Dogs love us";
        List<liir.nlp.representation.Word> lst = Tokenizer.processs(str);

        liir.nlp.sources.stanford.SentenceSplitter sp =new liir.nlp.sources.stanford.SentenceSplitter();
        liir.nlp.representation.Text t = sp.process(lst);
        t.setAutomaticIndexing();
        System.out.print(t.toXMLString());

        System.out.println(ll.process("<corpus>"+t.toXMLString()+"</corpus>"));
    }
}
