package example.log;

public class Log {
    private static final StringBuilder sb = new StringBuilder();
    public static void d(String msg){
        d("",msg);
    }
    public static void e(String msg){
        e("",msg);
    }
    public static void i(String msg){
        i("",msg);
    }

    public static void d(Object tag, String msg){
        buildDefaultMessageLine(sb,tag.getClass().getSimpleName(),msg);
        System.out.println(sb);
    }
    public static void e(Object tag, String msg){
        buildDefaultMessageLine(sb,tag.getClass().getSimpleName(),msg);
        System.out.println(sb);
    }
    public static void i(Object tag, String msg){
        buildDefaultMessageLine(sb,tag.getClass().getSimpleName(),msg);
        System.out.println(sb);
    }

    public static void d(String tag, String msg){
        buildDefaultMessageLine(sb,tag,msg);
        System.out.println(sb);
    }
    public static void e(String tag, String msg){
        buildDefaultMessageLine(sb,tag,msg);
        System.out.println(sb);
    }
    public static void i(String tag, String msg){
        buildDefaultMessageLine(sb,tag,msg);
        System.out.println(sb);
    }
    private static void buildDefaultMessageLine(StringBuilder sb, String tag, String msg) {
        sb.setLength(0);
        if (tag.length() > 0) {
            sb.append(tag).append(": ");
        }
        sb.append(msg);
    }
}
