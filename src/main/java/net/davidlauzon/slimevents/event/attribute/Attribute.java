package net.davidlauzon.slimevents.event.attribute;

/**
 * Created by david on 15-11-07.
 */
public abstract class Attribute {
    protected AttributeType type;

    public AttributeType type() {
        return type;
    }

    public abstract String stringValue();
}
