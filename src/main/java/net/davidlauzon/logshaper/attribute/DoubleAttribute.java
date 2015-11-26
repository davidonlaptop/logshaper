package net.davidlauzon.logshaper.attribute;

/**
 * Created by david on 15-11-26.
 */

public class DoubleAttribute implements Attribute
{
    protected double value;

    public DoubleAttribute(double value)
    {
        this.value = value;
    }

    public double value() {
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
