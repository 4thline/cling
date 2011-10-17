package example.localservice;

public class MyStringConvertible {

    private String msg;

    public MyStringConvertible(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
