package org.imp.jvm.types;

public enum BuiltInType implements Type {
    BOOLEAN("bool", boolean.class, "Z", TypeSpecificOpcodes.INT, false, false),
    INT("int", int.class, "I", TypeSpecificOpcodes.INT, 0, true),
    FLOAT("float", float.class, "F", TypeSpecificOpcodes.FLOAT, 0.0f, true),
    DOUBLE("double", double.class, "D", TypeSpecificOpcodes.DOUBLE, 0.0d, true),
    STRING("string", String.class, "Ljava/lang/String;", TypeSpecificOpcodes.OBJECT, "", false),
    VOID("void", void.class, "V", TypeSpecificOpcodes.VOID, null, false),

    BOOLEAN_ARR("bool[]", boolean[].class, "[B", TypeSpecificOpcodes.OBJECT, false),
    INT_ARR("int[]", int[].class, "[I", TypeSpecificOpcodes.OBJECT, false),
    FLOAT_ARR("float[]", float[].class, "[F", TypeSpecificOpcodes.OBJECT, false),
    DOUBLE_ARR("double[]", double[].class, "[D", TypeSpecificOpcodes.OBJECT, false),
    STRING_ARR("string[]", String[].class, "[Ljava/lang/String;", TypeSpecificOpcodes.OBJECT, false),

    STRUCT("struct", null, "Ljava/lang/Object;", TypeSpecificOpcodes.OBJECT, false);;

    private final String name;
    private final Class<?> typeClass;
    private final String descriptor;
    private final TypeSpecificOpcodes opcodes;
    private final Object defaultValue;
    private final boolean isNumeric;

    BuiltInType(String name, Class<?> typeClass, String descriptor, TypeSpecificOpcodes opcodes, Object defaultValue, boolean isNumeric) {
        this.name = name;
        this.typeClass = typeClass;
        this.descriptor = descriptor;
        this.opcodes = opcodes;
        this.defaultValue = defaultValue;
        this.isNumeric = isNumeric;
    }

    BuiltInType(String name, Class<?> typeClass, String descriptor, TypeSpecificOpcodes opcodes, boolean isNumeric) {
        this.name = name;
        this.typeClass = typeClass;
        this.descriptor = descriptor;
        this.opcodes = opcodes;
        this.isNumeric = isNumeric;
        this.defaultValue = null;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getTypeClass() {
        return typeClass;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getInternalName() {
        return getDescriptor();
    }

    @Override
    public int getLoadVariableOpcode() {
        return opcodes.getLoad();
    }

    @Override
    public int getStoreVariableOpcode() {
        return opcodes.getStore();
    }

    @Override
    public int getReturnOpcode() {
        return opcodes.getReturn();
    }

    @Override
    public int getAddOpcode() {
        return opcodes.getAdd();
    }

    @Override
    public int getSubstractOpcode() {
        return opcodes.getSubstract();
    }

    @Override
    public int getMultiplyOpcode() {
        return opcodes.getMultiply();
    }

    @Override
    public int getDivideOpcode() {
        return opcodes.getDivide();
    }

    @Override
    public Object getDefaultValue() {
        return this.name;
    }

    @Override
    public boolean isNumeric() {
        return isNumeric;
    }
}
