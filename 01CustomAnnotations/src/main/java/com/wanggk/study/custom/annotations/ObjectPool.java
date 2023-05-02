package com.wanggk.study.custom.annotations;

import android.util.SparseArray;

/**
 * 对象池，用于对象复用，内存的复用
 *
 * @param <T>
 */
public abstract class ObjectPool<T> {
    private SparseArray<T> mFreePool;
    private SparseArray<T> mUsedPool;

    private int mMaxObjectCount;
    private int mDefaultCount;

    private volatile static Object mLock = new Object();

    public ObjectPool(int defaultObjectCount, int maxObjectCount) {
        mDefaultCount = defaultObjectCount;
        mMaxObjectCount = maxObjectCount;
        init();
    }

    private void init() {
        mFreePool = new SparseArray<T>(mDefaultCount);
        for (int index = 0; index < mDefaultCount; index++) {
            mFreePool.put(index, create());
        }
        mUsedPool = new SparseArray<T>(mDefaultCount);
    }

    public abstract T create();

    /**
     * 获取对象
     *
     * @return 对象
     */
    public T getObject() {
        T t = null;
        synchronized (mLock) {
            int freeSize = mFreePool.size();
            for (int index = 0; index < freeSize; index++) {
                int key = mFreePool.keyAt(index);
                t = mFreePool.get(key);
                if (t != null) {
                    mFreePool.remove(key);
                    mUsedPool.put(key, t);
                    return t;
                }
            }

            if (t == null && mUsedPool.size() + freeSize < mMaxObjectCount) {
                t = create();
                mUsedPool.put(mUsedPool.size() + freeSize, t);
                return t;
            }

            return t;
        }
    }

    /**
     * 释放对象
     *
     * @param t 对象
     */
    public void releaseObject(T t){
        if (t == null) {
            return;
        }

        int key = mUsedPool.indexOfValue(t);
        restore(t);

        mFreePool.put(key, t);
        mUsedPool.remove(key);
    }

    public void restore(T t){
    }
}
