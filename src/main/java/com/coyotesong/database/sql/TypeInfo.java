package com.coyotesong.database.sql;

public class TypeInfo {
    private String name;
    private int type;
    private int precision;
    private String literalPrefix;
    private String literalSuffix;
    private String createParams;
    private short nullable;
    private boolean caseSensitive;
    private short searchable;
    private boolean unsignedAttribute;
    private boolean fixedPrecScale;
    private boolean autoIncrement;
    private String localTypeName;
    private short minimumScale;
    private short maximumScale;
    private int numPrecRadix;

    public TypeInfo() {
    }

    public TypeInfo(String name, int type, int precision, boolean autoIncrement) {
        this.name = name;
        this.type = type;
        this.precision = precision;
        this.autoIncrement = autoIncrement;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getPrecision() {
        return precision;
    }

    public String getLiteralPrefix() {
        return literalPrefix;
    }

    public String getLiteralSuffix() {
        return literalSuffix;
    }

    public String getCreateParams() {
        return createParams;
    }

    public short getNullable() {
        return nullable;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public short getSearchable() {
        return searchable;
    }

    public boolean isUnsignedAttribute() {
        return unsignedAttribute;
    }

    public boolean isFixedPrecScale() {
        return fixedPrecScale;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public String getLocalTypeName() {
        return localTypeName;
    }

    public short getMinimumScale() {
        return minimumScale;
    }

    public short getMaximumScale() {
        return maximumScale;
    }

    public int getNumPrecRadix() {
        return numPrecRadix;
    }
}
