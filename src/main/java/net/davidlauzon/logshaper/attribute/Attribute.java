package net.davidlauzon.logshaper.attribute;

/**
 * Created by david on 15-11-22.
 */
public interface Attribute
{
    public String stringValue();

    /**
     * @return True if this attribue's value should be enclosed in quotes
     */
    public boolean isQuoteable();

    public Attribute add( long increment );
    public Attribute add( double increment );
}
