package liir.nlp.srl.sources.lth.interfaces;

import is2.data.SentenceData09;
import liir.nlp.representation.Sentence;
import liir.nlp.representation.Text;
import liir.nlp.srl.sources.lth.pipeline.PipelineExtended;
import liir.utils.files.IO;
import org.xml.sax.SAXException;
import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.pipeline.Step;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by quynhdo on 31/08/15.
 */
public class LundSRL {

    private SemanticRoleLabeler srl;
    public LundSRL (String modelPath) throws IOException, ClassNotFoundException {
        Language.setLanguage(Language.L.eng);
        ZipFile zipFile=new ZipFile(new File(modelPath));
        srl =  PipelineExtended.fromZipFile(zipFile);
        zipFile.close();

    }

    public String process (String xmlContent) throws IOException, SAXException {

        List<List<se.lth.cs.srl.corpus.Sentence>> data = IO.readXMLToSentence(xmlContent);
        StringBuilder sb=new StringBuilder();


        for (List<se.lth.cs.srl.corpus.Sentence> text : data) {
            sb.append("<text id=\"\">\n");


            for ( int i= 0; i<text.size(); i++) {
                se.lth.cs.srl.corpus.Sentence se = text.get(i);

                srl.parseSentence(se);

                sb.append(IO.toMuseXML(se, String.valueOf(i)));
                sb.append("\n");
            }

            sb.append("<frames>");
            for (int i=0;i<text.size();i++)
            {
                se.lth.cs.srl.corpus.Sentence se = text.get(i);

                for (Predicate pred : se.getPredicates()){
                    sb.append(IO.toMuseXMLSimple(pred, String.valueOf(i)));
                }
            }
            sb.append("</frames>");

                sb.append("</text>");

        }


        return sb.toString();



    }





}
