package top.llr2021.wordmemory.listener;

import java.util.List;

public interface PermissionListener {

    //已授权
    void onGranted();

    //未授权
    void onDenied(List<String> deniedPermission);


}
