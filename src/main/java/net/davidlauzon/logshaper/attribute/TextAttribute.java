package net.davidlauzon.logshaper.attribute;

/**
 * Created by david on 15-11-07.
 */
public class TextAttribute implements Attribute {
    protected String value;

    public TextAttribute(String value)
    {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public boolean isQuoteable() {
        return true;
    }
}
