package com.ysy.mindmap.bases;

/**
 * 获取Fragment的状态的功能
 * Created by Sylvester on 17/4/18.
 */
interface IUIState {

    boolean isPaused();

    boolean isDestroyed();

    /**
     * Fragment是否Detached
     * 对Activity来讲，返回值同isDestroyed()
     */
    boolean isDetached();

    boolean isStopped();

    /**
     * Fragment是否被隐藏，对Activity来讲，返回值同isDestroyed()
     */
    boolean isFragmentHidden();

    /**
     * 判断是否对用户可见，对Activity来讲和isPaused()方法返回值相反
     * 对Fragment来讲和setUserVisibleHint()的参数值一致
     */
    boolean isVisibleToUser();
}
