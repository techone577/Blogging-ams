package com.blogging.ams.support.utils;

/**
 * @author techoneduan
 * @date 2019/1/22
 */
public class IdGenerator {


    private static final String SALT = "SALT";

    private static final String IMAGE = "IM";

    private static final String USER = "BU";


    public static String geerateSalt() {
        return SALT + new SnowflakeIdGenerator(0, 0).nextId();
    }

    public static String generateImageId() {
        return IMAGE + new SnowflakeIdGenerator(0, 0).nextId();
    }

    public static String generateMemberId() {
        return USER + new SnowflakeIdGenerator(0, 0).nextId();
    }
}
