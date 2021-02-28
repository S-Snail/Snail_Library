package com.example.base_libs.presenter;

import androidx.annotation.NonNull;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
class ViewCallback<T> {

    /**
     * 流程开始
     */
    public void onStart() {

    }

    /**
     * 成功返回
     * @param t
     */
    public void onSuccess(T t){

    }

    /**
     * 失败返回
     */
    public void onFailed(@NonNull SimpleMsg simpleMsg){

    }

    /**
     * 流程完成时执行，在onSuccess(),或者onFailed()之后执行
     */
    public void onFinish(){

    }


}
