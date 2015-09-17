package liir.utils.types;

/**
 * Created by quynhdo on 26/08/15.
 */
public class Tuple3<A,B, C> {

    A first ;
    B second ;
    C third;

    public Tuple3 (A a, B b, C c)
    {
        first=a;
        second =b;
        third= c;
    }

    public A getFirst(){
        return first;
    }

    public B getSecond(){
        return second;
    }

    public C getThird(){
        return third;
    }

    public String toString(){
        return  "[" +  String.valueOf(first) + "," + String.valueOf(second) + "," + String.valueOf(third)+"]";
    }
}