package com.xiayiye5.xiayiye5ad;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;


/**
 * @author 刘畅
 * @createdate 2019-06-18
 * @describe
 */
public class ActivityManager {

    private Stack<Activity> mStack;

    private boolean appInBackGround;

    public boolean isAppInBackGround() {
        return appInBackGround;
    }

    public void setAppInBackGround(boolean appInBackGround) {
        this.appInBackGround = appInBackGround;
    }

    private ActivityManager() {
        if(mStack == null){
            mStack = new Stack<>();
        }
    }

    private static final class SingletonHolder {
        private static final ActivityManager INSTANCE = new ActivityManager();
    }

    public static final ActivityManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addActivity(Activity activity){
        if(activity != null){
            mStack.add(activity);
        }
        Log.i("wsc", String.format("ActivityManager addActivity = %s %s ",activity.getLocalClassName(),mStack.size()));


    }
    public void removeActivity(Activity activity){
        if(activity != null){
            mStack.remove(activity);
        }
        Log.i("wsc", String.format("ActivityManager removeActivity = %s %s ",activity.getLocalClassName(),mStack.size()));
    }

    public void finishActivity(Activity activity){
        if(activity != null){
            removeActivity(activity);
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 该方法慎用 容易引起内存泄漏
     * @return
     */
    public Activity getCurrentActivity(){
        Activity activity = null;
        if (!mStack.empty()){
            activity = mStack.lastElement();
        }
        return activity;

    }

    public int getStackSize(){
        return mStack.size();
    }
    public Activity getStackActivity(int index){
        if (index < 0){
            return null;
        }
        if(mStack.size() > index){
            return mStack.get(index);
        }
        return null;
    }


    public Activity getStackActivityDesc(int index){
        if(mStack.size() > index){
            return mStack.get(mStack.size() - 1 - index);
        }
        return null;
    }


    /**
     * 弹出除cls外的所有activity
     *
     * @param cls
     */
    public void finishAllActivityWithOut(Class<? extends Activity> cls) {
        while (true) {
            Activity activity = getCurrentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            removeActivity(activity);
        }
    }

    /**
     *  关闭activity
     * @param count 关闭的数量
     */
    public void finishActivityCount(int count){
        for (int i = 0; i < count; i++) {
            Activity activity = getCurrentActivity();
            finishActivity(activity);
        }
    }

    /**
     * 关闭指定Activity
     * @param clazz Activity类
     */
    public void finishActivity(Class<? extends Activity> clazz){
        for (int i = 0; i < mStack.size(); i++) {
            if(mStack.get(i).getClass().equals(clazz)){
                finishActivity(mStack.get(i));
                break;
            }
        }
    }

    /**
     * 堆栈里是否有这个页面
     * @param clazz
     * @return
     */
    public boolean hasActivity(Class<? extends Activity> clazz){
        for (int i = 0; i < mStack.size(); i++) {
            if(mStack.get(i).getClass().equals(clazz)){
                return true;
            }
        }
        return false;
    }



    /**
     * 关闭这个Activity之前的页面
     * @param cls Activity类
     */
    public void finishActivityWithOut(Class<? extends Activity> cls){
        while (!mStack.empty()) {
            Activity activity = getCurrentActivity();
            if (activity.getClass().equals(cls)) {
                break;
            } else {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束除cls之外的所有activity,执行结果都会清空Stack
     *
     * @param cls
     */
    public void finishAllActivityExceptOne(Class<? extends Activity> cls) {
        while (!mStack.empty()) {
            Activity activity = getCurrentActivity();
            if (activity.getClass().equals(cls)) {
                removeActivity(activity);
            } else {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivity() {
        while (!mStack.empty()) {
            Activity activity = getCurrentActivity();
            finishActivity(activity);
        }
    }
}
