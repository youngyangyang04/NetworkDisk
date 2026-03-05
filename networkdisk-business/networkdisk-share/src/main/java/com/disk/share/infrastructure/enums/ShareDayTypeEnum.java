package com.disk.share.infrastructure.enums;

import com.disk.share.exception.ShareException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.disk.share.exception.ShareErrorCode.SHARE_DAY_TYPE_ERROR;

/**
 * Share expiration type.
 */
public enum ShareDayTypeEnum {
    PERMANENT(-1),
    ONE_DAY(1),
    SEVEN_DAY(7),
    THIRTY_DAY(30);

    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+");

    private final Integer day;

    ShareDayTypeEnum(Integer day) {
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    /**
     * Supports: "-1" / "1" / "7" / "30" / "permanent" / "1day" / "7days" / "30days"
     * Also supports legacy Chinese style values such as "永久有效", "1天", "7天", "30天".
     */
    public static Integer getDayByType(String type) {
        if (StringUtils.isBlank(type)) {
            throw new ShareException(SHARE_DAY_TYPE_ERROR);
        }

        String normalized = type.trim().toLowerCase();
        if ("permanent".equals(normalized) || normalized.contains("permanent") || normalized.contains("永久")) {
            return PERMANENT.day;
        }

        if ("1day".equals(normalized) || "one_day".equals(normalized)) {
            return ONE_DAY.day;
        }
        if ("7days".equals(normalized) || "seven_day".equals(normalized)) {
            return SEVEN_DAY.day;
        }
        if ("30days".equals(normalized) || "thirty_day".equals(normalized)) {
            return THIRTY_DAY.day;
        }

        Matcher matcher = NUMBER_PATTERN.matcher(normalized);
        if (matcher.find()) {
            int day = Integer.parseInt(matcher.group());
            if (day == PERMANENT.day || day == ONE_DAY.day || day == SEVEN_DAY.day || day == THIRTY_DAY.day) {
                return day;
            }
        }

        throw new ShareException(SHARE_DAY_TYPE_ERROR);
    }
}
