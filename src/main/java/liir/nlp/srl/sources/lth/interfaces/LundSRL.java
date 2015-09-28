package liir.nlp.srl.sources.lth.interfaces;

import liir.nlp.interfaces.preprocessing.Processor;
import liir.nlp.core.representation.io.XMLReader;
import liir.nlp.core.representation.Text;
import liir.nlp.srl.sources.lth.pipeline.PipelineExtended;
import liir.utils.files.IO;
import org.xml.sax.SAXException;
import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.languages.Language;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * Created by quynhdo on 31/08/15.
 */
public class LundSRL extends Processor{

    private SemanticRoleLabeler srl;
    public LundSRL (String modelPath) throws IOException, ClassNotFoundException {
        super("Lund SRL");
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

    public Text processToText(Text txt){
        StringBuilder sb=new StringBuilder();

        try {
            List<se.lth.cs.srl.corpus.Sentence> text = IO.textToSentence(txt);
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



        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        try {
            return XMLReader.readCorpus(sb.toString()).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }



}
