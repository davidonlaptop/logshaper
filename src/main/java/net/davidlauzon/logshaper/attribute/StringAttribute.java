package net.davidlauzon.logshaper.attribute;

/**
 * Created by david on 15-11-07.
 */
public class StringAttribute implements Attribute
{
    protected String value;

    public StringAttribute(String value)
    {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String stringValue() {
        return value;
    }

    @Override
    public boolean isQuoteable() {
        return true;
    }

    @Override
    public Attribute add(long increment)
    {
        this.value += increment;

        return this;
    }

    @Override
    public Attribute add(double increment)
    {
        this.value += increment;

        return this;
    }
}
