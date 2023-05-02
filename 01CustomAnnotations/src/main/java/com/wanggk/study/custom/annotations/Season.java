package com.wanggk.study.custom.annotations;

import androidx.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Season {
    public static final String SPRING = "spring";
    public static final String SUMMER = "summer";
    public static final String AUTUMN = "autumn";
    public static final String WINTER = "winter";

    @Documented
    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @StringDef(value = {Season.AUTUMN, Season.SPRING, Season.SUMMER, Season.WINTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Seasons{}

    private @Seasons String mSeason = SPRING;

    public void setSeason(@Seasons String season) {
        mSeason = season;
    }

    public @Seasons String getSeason() {
        return mSeason;
    }
}
