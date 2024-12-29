package com.yufung.demo.transactional;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedisTransactional {

    /**
     * 获取锁的最长等待时间，以秒为单位
     * 如果不指定，则会一直等待
     */
    int acquireTimeout() default 0;


    /**
     * 锁的超时时间，以秒为单位
     * 如果不指定，则不会超时，只能手工解锁时才会释放
     */
    int lockTimeout() default 0 ;


    /**
     * 锁名字，如果不指定，则使用当前方法的全路径名
     * @return
     */
    String lockName() default "";


}
