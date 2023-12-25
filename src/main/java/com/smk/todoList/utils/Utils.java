package com.smk.todoList.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Set;

public class Utils {

    public static String[] getNullPropertyNames(Object source) {

        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = wrappedSource.getPropertyDescriptors();

        Set<String> emptyNames = new java.util.HashSet<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object propertyValue = wrappedSource.getPropertyValue(propertyDescriptor.getName());
            if (propertyValue == null) {
                emptyNames.add(propertyDescriptor.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }
}
