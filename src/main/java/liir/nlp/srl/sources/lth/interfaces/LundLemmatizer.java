package liir.nlp.srl.sources.lth.interfaces;

import is2.data.SentenceData09;
import is2.lemmatizer.*;
import liir.nlp.interfaces.preprocessing.Processor;
import liir.nlp.core.representation.Sentence;
import liir.nlp.core.representation.Text;
import liir.utils.files.IO;
import org.xml.sax.SAXException;
import se.lth.cs.srl.util.BohnetHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by quynhdo on 31/08/15.
 */
public class LundLemmatizer extends Processor{
    Lemmatizer l ;

    public LundLemmatizer(String modelPath) throws IOException {
        super("Lund Lemmatizer");
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

    public Text processToText (Text txt)  {

        try {
            List<SentenceData09> data = IO.textToSentenceData09(txt);

            for (int i = 0; i < data.size(); i++) {
                SentenceData09 sen09 = data.get(i);
                Sentence sen = txt.get(i);
                l.apply(sen09);

                for (int k = 1; k < sen09.forms.length; k++)
                    sen.get(k-1).setLemma(sen09.plemmas[k]);
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return txt;

    }

    public static void main(String[] args) throws Exception {
    }
}
