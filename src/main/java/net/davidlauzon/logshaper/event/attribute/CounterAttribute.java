package net.davidlauzon.logshaper.event.attribute;

/**
 * Created by david on 15-11-07.
 */
public class CounterAttribute extends Attribute {
    protected long value;

    public CounterAttribute(long value) {
        this.value = value;
        this.type = AttributeType.Counter;
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
}
