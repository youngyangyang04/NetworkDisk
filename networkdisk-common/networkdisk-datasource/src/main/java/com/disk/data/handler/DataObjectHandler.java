package com.disk.data.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author weikunkun
 */
public class DataObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByNameIfNull("gmtCreate", new Date(), metaObject);
        this.setFieldValByNameIfNull("gmtModified", new Date(), metaObject);
        this.setFieldValByName("deleted", 0, metaObject);
        this.setFieldValByName("lockVersion", 0, metaObject);
    }

    /**
     * 当没有值的时候再设置属性，如果有值则不设置。主要是方便单元测试
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     */
    private void setFieldValByNameIfNull(String fieldName, Object fieldVal, MetaObject metaObject) {
        if (metaObject.getValue(fieldName) == null) {
            this.setFieldValByName(fieldName, fieldVal, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtModified", new Date(), metaObject);
    }
}
