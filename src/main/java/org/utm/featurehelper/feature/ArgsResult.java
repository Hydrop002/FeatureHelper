package org.utm.featurehelper.feature;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ArgsResult {

    public final Constructor<?> constructor;
    public final Object[] parsedArgs;

    public ArgsResult(Constructor<?> constructor) {
        this.constructor = constructor;
        this.parsedArgs = new Object[constructor.getParameterTypes().length];
    }

    public boolean isEmpty() {
        return this.parsedArgs.length == 0;
    }

    public boolean isDone() {
        for (Object arg : this.parsedArgs)
            if (arg == null) return false;
        return true;
    }

    public Message getArgsTooltip(int start) {
        Class<?>[] classList = this.constructor.getParameterTypes();
        Type[] genericTypeList = this.constructor.getGenericParameterTypes();
        String[] classNameList = new String[classList.length - start];
        for (int i = start; i < classList.length; ++i) {
            classNameList[i - start] = classList[i].getSimpleName();
            if (genericTypeList[i] instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) genericTypeList[i]).getActualTypeArguments();
                for (int j = 0; j < types.length; ++j) {
                    if (j == 0) classNameList[i - start] += "<";
                    classNameList[i - start] += ((Class<?>) types[j]).getSimpleName();
                    if (j < types.length - 1) classNameList[i - start] += ",";
                    if (j == types.length - 1) classNameList[i - start] += ">";
                }
            }
        }
        return new LiteralMessage(Arrays.toString(classNameList));
    }

}
