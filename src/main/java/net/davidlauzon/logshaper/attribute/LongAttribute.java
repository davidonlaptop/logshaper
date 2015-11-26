package net.davidlauzon.logshaper.attribute;

/**
 * Created by david on 15-11-26.
 */
public class LongAttribute implements Attribute
{
    protected long value;

    public LongAttribute(long value)
    {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public boolean isQuoteable() {
        return false;
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
