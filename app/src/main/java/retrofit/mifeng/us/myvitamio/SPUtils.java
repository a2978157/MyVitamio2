package retrofit.mifeng.us.myvitamio;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 21903 on 2017/6/30.
 */

public class SPUtils {
    private String name="jianbao";
    /*
	 * 保存数据的方法
	 * */
    public void saveShared(String key,long value,Context ctx){
        SharedPreferences shared=ctx.getSharedPreferences(name,0);
        SharedPreferences.Editor edit = shared.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    /*
     * 从本地获取数据
     * */
    public long getShared(Context ctx,String key){
        SharedPreferences shared = ctx.getSharedPreferences(name, 0);
        long aLong = shared.getLong(key, 0);
        return aLong;
    }
}
