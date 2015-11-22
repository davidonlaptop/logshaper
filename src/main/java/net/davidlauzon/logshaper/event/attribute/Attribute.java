package net.davidlauzon.logshaper.event.attribute;

/**
 * Created by david on 15-11-22.
 */
public interface Attribute
{
    String stringValue();

    /**
     * @return True if this attribue's value should be enclosed in quotes
     */
    boolean isQuoteable();
}
