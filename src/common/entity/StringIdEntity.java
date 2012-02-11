/*
 * Copyright Evan Summers
 * 
 */
package common.entity;

/**
 *
 * @author evan
 */
public class StringIdEntity extends AbstractEntity {
    protected String id;
    
    @Override
    public Comparable getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
       
}
