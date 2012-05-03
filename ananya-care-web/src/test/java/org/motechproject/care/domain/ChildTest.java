package org.motechproject.care.domain;

import junit.framework.Assert;
import org.junit.Test;

public class ChildTest {

    @Test
    public void shouldBeSetToIsActiveByDefault() {
        Child child = new Child();
        Assert.assertTrue(child.isActive());
    }

    @Test
    public void shouldBeInactiveIfClosedByCommcare() {
        Child child = new Child();
        child.setExpired(false);
        child.setClosedByCommcare(true);
        Assert.assertFalse(child.isActive());
    }

    @Test
    public void shouldBeInactiveIfExpired() {
        Child child = new Child();
        child.setExpired(true);
        child.setClosedByCommcare(false);
        Assert.assertFalse(child.isActive());
    }
}
