public class Protocol {
    public static final String GENERATE_KEY_MSG = "generate_new_key";
    public static final String SHUTDOWN_MSG = "off";
    public static final String SENDING_NEW_KEY = "sending_new_key";
    public static final String OK = "ok";
    public static final String SENDING_NEW_FILE = "sending_new_file";
    public static final String SEND_ME_NEW_FILE = "send_me_new_file";


    private Protocol() {
        throw new IllegalStateException();
    }
}
