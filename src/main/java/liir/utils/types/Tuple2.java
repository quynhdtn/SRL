package liir.utils.types;

/**
 * Created by quynhdo on 26/08/15.
 */
public class Tuple2<A,B> {

    A first ;
    B second ;

    public Tuple2 (A a, B b)
    {
        first=a;
        second =b;
    }

    public A getFirst(){
        return first;
    }

    public B getSecond(){
        return second;
    }

    public String toString(){
        return  "[" +  String.valueOf(first) + "," + String.valueOf(second) +"]";
    }
}