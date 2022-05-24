package top.llr2021.wordmemory.qqLogin;

public class QQStatus {

    public static final int START_LOGIN_APPLY=0x1;//开始申请登录授权  [登录按钮禁止点击]
    public static final int LOGIN_APPLY_CANCEL=0x2;//登录授权取消     [登录按钮恢复点击]
    public static final int LOGIN_APPLY_FAILED_NULL=0x3;//登录授权失败(错误信息对象为null)  [登录按钮恢复点击]
    public static final int LOGIN_APPLY_FAILED=0x4;//登录授权失败(有错误信息) [登录按钮恢复点击]
    public static final int LOGIN_APPLY_PARSE_FAILED_NULL=0x5;//登录授权解析数据失败(解析对象为空) [登录按钮恢复点击]
    public static final int LOGIN_APPLY_PARSE_FAILED=0x6;//登录授权解析数据失败(有解析对象) [登录按钮恢复点击]
    public static final int LOGIN_APPLY_SUCCESS=0x7;//登录授权成功  [登录按钮禁止点击]
    public static final int LOGIN_INVALID=0x8;//未登录或登录过期     [登录按钮恢复点击]
    public static final int LOGIN_INFO_CANCEL=0x9;//拉取用户登录信息已被取消  [登录按钮恢复点击]
    public static final int LOGIN_INFO_FAILED_NULL=0x10;//拉取用户登录信息失败(错误信息对象为null) [登录按钮恢复点击]
    public static final int LOGIN_INFO_FAILED=0x11;//拉取用户登录信息失败(有错误信息)  [登录按钮恢复点击]
    public static final int LOGIN_INFO_PARSE_FAILED_NULL=0x12;//拉取用户登录信息解析失败(解析对象为空)  [登录按钮恢复点击]
    public static final int LOGIN_INFO_PARSE_FAILED=0x13;//拉取用户登录信息解析失败(有解析对象) [登录按钮恢复点击]
    public static final int LOGIN_INFO_SUCCESS=0x14;//拉取用户登录信息成功(qq登录成功) [登录按钮恢复点击]
    public static final int LOGIN_OUT_SUCCESS=0x15;//退出登录成功 [登录按钮恢复点击]

}
