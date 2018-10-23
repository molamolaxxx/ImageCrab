package com.mola.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.input.InputManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 2018/8/7.
 */

public class InputUtils {
    public static void closeInputWriter(Activity context){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context
                            .getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        } catch (Exception e) {
            // TODO: handle exception
            Log.d("", "关闭输入法异常");
        }

    }
    private static Boolean isInputOpen(){
        return false;
    }
}
