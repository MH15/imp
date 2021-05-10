package org.imp.jvm.domain.types;

import org.apache.commons.lang3.StringUtils;
import org.imp.jvm.ImpParser;

import java.util.Arrays;
import java.util.Optional;

public class TypeResolver {
    public static Type getFromTypeContext(ImpParser.TypeListContext typeContext) {

        return null;
    }

    public static Type getFromTypeContext(ImpParser.TypeStructContext typeContext) {

        return new ClassType(typeContext.getText());
    }

    public static Type getFromTypeContext(ImpParser.TypePrimitiveContext typeContext) {
        Optional<BuiltInType> builtInType = getBuiltInType(typeContext.getText());
        if (builtInType.isPresent()) {
            return builtInType.get();
        } else {
            return new ClassType(typeContext.getText());
        }
    }

    public static Type getFromTypeContext(ImpParser.TypeContext typeContext) {
        if (typeContext instanceof ImpParser.TypePrimitiveContext) {
            Optional<BuiltInType> builtInType = getBuiltInType(typeContext.getText());
            if (builtInType.isPresent()) {
                return builtInType.get();
            }
        } else if (typeContext instanceof ImpParser.TypeStructContext) {
            ImpParser.TypeStructContext a = (ImpParser.TypeStructContext) typeContext;
            return new ClassType(a.identifier().getText());
        } else if (typeContext instanceof ImpParser.TypeListContext) {
            return null;
        } else {
            System.err.println("ree");
        }
        return null;
    }


    public static Object getValueFromString(String value, BuiltInType t) {
        Object result;
        switch (t) {
            case BOOLEAN -> result = Boolean.valueOf(value);
            case INT -> result = Integer.valueOf(value);
            case FLOAT -> result = Float.valueOf(value);
            case DOUBLE -> result = Double.valueOf(value);
            case STRING -> {
                value = StringUtils.removeStart(value, "\"");
                value = StringUtils.removeEnd(value, "\"");
                result = value;
            }
            default -> throw new AssertionError("Objects not yet implemented!");

        }
        return result;
    }

    private static Optional<BuiltInType> getBuiltInType(String typeName) {
        return Arrays.stream(BuiltInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }

    public static final class TypeChecker {
        public static boolean isInt(Type type) {
            return type == BuiltInType.INT;
        }

        public static boolean isBool(Type type) {
            return type == BuiltInType.BOOLEAN;
        }

        public static boolean isFloat(Type type) {
            return type == BuiltInType.FLOAT;
        }

        public static boolean isDouble(Type type) {
            return type == BuiltInType.DOUBLE;
        }
    }
}
