/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple util class to get/set bean properties.
 * 
 * @author Greg Orlowski
 */
public class BeanUtil {

    private static final Pattern DOTTED_PATH_SPLITTER = Pattern.compile("\\.");
    private static final Pattern SEQUENCE_PATTERN = Pattern.compile("^(\\w+)\\[(\\d+)\\]$");

    private static enum AccessorType {
        GETTER,
        SETTER
    }

    public static Object getProperty(Object bean, String propName) {
        Object ret = bean;
        String[] parts = DOTTED_PATH_SPLITTER.split(propName);
        try {
            for (String part : parts) {
                Matcher matcher = null;
                if ((matcher = partContainsIndex(part)) != null) {
                    part = matcher.group(1);
                    int idx = Integer.parseInt(matcher.group(2));

                    Method m = determineGetterMethod(ret, part);
                    ret = m.invoke(ret);

                    if (ret instanceof List<?>) {
                        ret = ((List<?>) ret).get(idx);
                    } else {
                        ret = Array.get(ret, idx);
                    }
                } else {
                    Method m = determineGetterMethod(ret, part);
                    ret = m.invoke(ret);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    private static Matcher partContainsIndex(String part) {
        Matcher m = null;
        if (part.endsWith("]") && (m = SEQUENCE_PATTERN.matcher(part)).find()) {
            return m;
        }
        return null;
    }

    @SuppressWarnings({ "unchecked" })
    public static void setProperty(Object bean, String propName, Object value) {
        String[] parts = DOTTED_PATH_SPLITTER.split(propName);
        Method m = null;
        try {
            for (int i = 0; i < parts.length - 1; i++) {
                bean = getProperty(bean, parts[i]);
            }
            String lastPart = parts[parts.length - 1];
            Matcher matcher = null;
            if ((matcher = partContainsIndex(lastPart)) != null) {
                bean = getProperty(bean, matcher.group(1));
                int idx = Integer.parseInt(matcher.group(2));
                if (bean instanceof List<?>) {
                    ((List<Object>) bean).set(idx, value);
                } else if (bean.getClass().isArray()) {
                    /*
                     * TODO: add support for primitive arrays. This currently only supports Object
                     * arrays and lists.
                     */
                    Array.set(bean, idx, value);
                } else {
                    throw new UnsupportedOperationException("Cannot set indexed property for bean: "
                            + bean + " of class: " + bean.getClass().getName());
                }
            } else {
                m = determineSetterMethod(bean, lastPart, value != null ? value.getClass() : null);
                m.invoke(bean, value);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method determineGetterMethod(Object bean, String beanPropName)
            throws IntrospectionException {
        return determineAccessorMethod(bean, beanPropName, null, AccessorType.GETTER);
    }

    public static Method determineSetterMethod(Object bean, String beanPropName)
            throws IntrospectionException {
        return determineAccessorMethod(bean, beanPropName, null, AccessorType.SETTER);
    }

    public static Method determineSetterMethod(Object bean, String beanPropName, Class<?> paramClass)
            throws IntrospectionException {
        return determineAccessorMethod(bean, beanPropName, paramClass, AccessorType.SETTER);
    }

    public static Method determineGetterMethod(Object bean, String beanPropName, Class<?> paramClass)
            throws IntrospectionException {
        return determineAccessorMethod(bean, beanPropName, paramClass, AccessorType.GETTER);
    }

    private static Method determineAccessorMethod(Object bean, String beanPropName,
            Class<?> parameterClass, AccessorType accessorType) throws IntrospectionException {

        Method ret = null;
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        PD_ITER: for (PropertyDescriptor pd : propertyDescriptors) {
            if (beanPropName.equals(pd.getName())) {

                // If we do not specify a param type, match by name-only
                if (parameterClass == null) {
                    return accessorType == AccessorType.SETTER
                            ? pd.getWriteMethod()
                            : pd.getReadMethod();
                }

                Method m = null;
                Class<?> methodParamType = null;

                switch (accessorType) {
                    case SETTER:
                        m = pd.getWriteMethod();
                        Class<?>[] paramTypes = m.getParameterTypes();
                        if (paramTypes.length == 1) {
                            methodParamType = paramTypes[0];
                        }
                        break;
                    case GETTER:
                        m = pd.getReadMethod();

                        if (m != null) {
                            methodParamType = m.getReturnType();
                        }
                        break;
                }

                if (m == null || methodParamType == null) {
                    continue PD_ITER;
                }

                // e.g., Number isAssignableFrom Integer
                if (methodParamType.equals(parameterClass) || methodParamType.isAssignableFrom((parameterClass))) {
                    ret = m;
                    break PD_ITER;
                } else if (methodParamType.isPrimitive()) {
                    if (Number.class.isAssignableFrom(parameterClass)) {
                        if ((Integer.class == parameterClass && Integer.TYPE == methodParamType)
                                || (Short.class == parameterClass && Short.TYPE == methodParamType)
                                || (Double.class == parameterClass && Double.TYPE == methodParamType)
                                || (Long.class == parameterClass && Long.TYPE == methodParamType)
                                || (Byte.class == parameterClass && Byte.TYPE == methodParamType)
                                || (Float.class == parameterClass && Float.TYPE == methodParamType)) {
                            ret = m;
                            break PD_ITER;
                        }
                    } else if ((Character.class == parameterClass && Character.TYPE == methodParamType)
                            || (Boolean.class == parameterClass && Boolean.TYPE == methodParamType)) {
                        ret = m;
                        break PD_ITER;
                    }
                }

            }
        }
        if (ret == null) {
            throw new IllegalArgumentException("Bean has no writable property named: " + beanPropName);
        }
        return ret;
    }

}
