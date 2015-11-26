package net.davidlauzon.logshaper.attribute;

/**
 * Created by david on 15-11-26.
 */
public class BooleanAttribute implements Attribute
{
    protected boolean value;

    public BooleanAttribute(boolean value)
    {
        this.value = value;
    }

    public boolean value() {
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

    @Override
    public Attribute add(long increment)
    {
        // Does not make sense to add a long to a boolean

        return this;
    }

    @Override
    public Attribute add(double increment)
    {
        // Does not make sense to add a double to a boolean

        return this;
    }
}
