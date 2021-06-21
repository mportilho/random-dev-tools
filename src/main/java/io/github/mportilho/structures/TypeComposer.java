package io.github.mportilho.structures;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public interface TypeComposer {

    static <T> Type typeOf(Class<T> rawClass, Class<?>... typeArguments) {
        Type[] actualTypeArgs = typeArguments == null || typeArguments.length == 0 ? null :
                Arrays.copyOfRange(typeArguments, 0, typeArguments.length);
        return new ParameterizedType() {
            public Type getRawType() {
                return rawClass;
            }

            public Type getOwnerType() {
                return null;
            }

            public Type[] getActualTypeArguments() {
                return actualTypeArgs;
            }
        };
    }

    static <T> Type listOf(Class<T> typeArgument) {
        return typeOf(List.class, typeArgument);
    }

    static <T> Type setOf(Class<T> typeArgument) {
        return typeOf(Set.class, typeArgument);
    }

    static <T> Type optionalOf(Class<T> typeArgument) {
        return typeOf(Optional.class, typeArgument);
    }

    static <K, V> Type mapOf(Class<K> keyTypeArgument, Class<V> valueTypeArgument) {
        return typeOf(Map.class, keyTypeArgument, valueTypeArgument);
    }

}
