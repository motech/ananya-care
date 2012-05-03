package org.motechproject.care.domain;

import junit.framework.Assert;
import org.junit.Test;

public class ChildTest {

    @Test
    public void shouldBeSetToIsActiveByDefault() {
        Child child = new Child();
        Assert.assertTrue(child.isActive());
    }
}
