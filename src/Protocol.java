public class Protocol {
    public static final String GENERATE_KEY_MSG = "gnk";
    public static final String SHUTDOWN_MSG = "off";
    public static final String SENDING_NEW_KEY = "snk";
    public static final String OK = "oka";
    public static final String SENDING_NEW_FILE = "snf";
    public static final String SEND_ME_NEW_FILE = "smnf";
    public static final String EOF = "eof";


    private Protocol() {
        throw new IllegalStateException();
    }
}
