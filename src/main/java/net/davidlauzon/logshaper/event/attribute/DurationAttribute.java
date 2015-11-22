package net.davidlauzon.logshaper.event.attribute;

/**
 * Created by david on 15-11-07.
 */
public class DurationAttribute extends CounterAttribute
{
    public DurationAttribute(long value) {
        super(value);
    }

    public float seconds() {
        return value / 1000f;
    }
}
