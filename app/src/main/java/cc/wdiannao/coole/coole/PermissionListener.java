package cc.wdiannao.coole.coole;

/**
 * Created by yaque on 2018/4/22.
 */

import java.util.List;

/**
 * 已授权、未授权的接口回调
 */
public interface PermissionListener {

    void onGranted();//已授权

    void onDenied(List<String> deniedPermission);//未授权

}
