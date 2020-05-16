package ie.gmit.sw;

public class Tuple<T, U> {

    T _a;
    U _b;

    public Tuple() {
    }

    public Tuple(T t, U u) {
        _a = t;
        _b = u;
    }

    public T get_a() {
        return _a;
    }

    public U get_b() {
        return _b;
    }

}