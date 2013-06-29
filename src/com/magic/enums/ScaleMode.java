package com.magic.enums;

/**
 * Created with IntelliJ IDEA.
 * User: haribo
 * Date: 10.05.13
 * Time: 17:35
 */
public enum ScaleMode {
    EQUAL_OR_GREATER(0),
    EQUAL_OR_LOWER(1);

    private Integer id;

    ScaleMode(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
