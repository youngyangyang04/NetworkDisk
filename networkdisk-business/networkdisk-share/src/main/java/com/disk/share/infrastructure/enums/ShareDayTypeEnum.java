package com.disk.share.infrastructure.enums;

import com.disk.share.exception.ShareException;

import java.util.Arrays;
import java.util.Objects;

import static com.disk.share.exception.ShareErrorCode.SHARE_DAY_TYPE_ERROR;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public enum ShareDayTypeEnum {
    PERMANENT("永久有效", -1),
    ONE_DAY("1天", 1),
    SEVEN_DAY("7天", 7),
    THIRTY_DAY("30天", 30),
    ;

    private final String type;

    private final Integer day;

    ShareDayTypeEnum(String type, Integer day) {
        this.type = type;
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    public String getType() {
        return type;
    }

    public static Integer getDayByType(String type) {
        return Arrays.stream(values()).filter(item -> Objects.equals(type, item.getType())).map(ShareDayTypeEnum::getDay).findFirst()
                .orElseThrow(() -> new ShareException(SHARE_DAY_TYPE_ERROR));
    }
}
