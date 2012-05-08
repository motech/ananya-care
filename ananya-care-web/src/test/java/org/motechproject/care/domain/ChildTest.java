package org.motechproject.care.domain;

import junit.framework.Assert;
import org.junit.Test;

public class ChildTest {

    @Test
    public void shouldBeSetToIsActiveByDefault() {
        Child child = new Child();
        child.setAlive(true);
        Assert.assertTrue(child.isActive());
    }

    @Test
    public void shouldBeSetToInActiveIfNotAlive() {
        Child child = new Child();
        child.setAlive(false);
        Assert.assertFalse(child.isActive());
    }

    @Test
    public void shouldBeInactiveIfClosedByCommcare() {
        Child child = new Child();
        child.setAlive(true);
        child.setClosedByCommcare(true);
        Assert.assertFalse(child.isActive());
    }

    @Test
    public void shouldBeInactiveIfExpired() {
        Child child = new Child();
        child.setExpired(true);
        Assert.assertFalse(child.isActive());
    }
}
