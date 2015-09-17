package liir.nlp.srl.sources.lth.options;
import se.lth.cs.srl.options.ParseOptions;
import java.io.File;


/**
 * Created by quynhdo on 25/08/15.
 */
public class ParseOptionExtended extends ParseOptions {


    public ParseOptionExtended(String[] args){

        super(args);
    }


    @Override
    protected void superParseCmdLine(String[] args) {
        this.modelFile=new File(args[0]);
        this.useReranker=Boolean.valueOf(args[1]);
        this.skipPI=Boolean.valueOf(args[2]);

    }

}
