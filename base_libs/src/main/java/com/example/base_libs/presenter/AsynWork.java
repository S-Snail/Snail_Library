package com.example.base_libs.presenter;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
interface AsynWork<T> {
    T doWork() throws Exception;
}
