package net.davidlauzon.logshaper.event.attribute;

/**
 * Created by david on 15-11-07.
 */
public class CounterAttribute implements Attribute {
    protected long value;

    public CounterAttribute(long value) {
        this.value = value;
    }

    public void increment(long increment) {
        this.value += increment;
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
}
