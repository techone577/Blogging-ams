package com.blogging.ams.support.strategy;

/**
 * bean匹配
 * @author xiehui
 *
 */
public interface MatchingBean<T> {

    boolean matching (T factor);
    
}
