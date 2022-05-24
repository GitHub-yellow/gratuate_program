package top.llr2021.wordmemory.config;

public class ServerData {

    public final static String SERVER_ADDRESS = "http://119.29.184.12:9065/app";//

    public final static String SERVER_LOGIN_ADDRESS = SERVER_ADDRESS + "/login";
    public final static String SERVER_UPLOAD_RECORD_ADDRESS = SERVER_ADDRESS + "/backups";
    public final static String SERVER_UPLOAD_INFO_ADDRESS = SERVER_ADDRESS + "/backups";
    public final static String SERVER_RETURN_BOOKS_ADDRESS = SERVER_ADDRESS + "/recovery";

    public final static String LOGIN_QQ_NUM = "QQNum";
    public final static String LOGIN_QQ_NAME = "QQName";
    public final static String TYPE_NAME = "updateType";
    public final static String UPLOAD_FILE = "file";//uploadedFile
    public final static String UPLOAD_TYPE = "1";
    public final static String RECOVER_TYPE = "2";

}
