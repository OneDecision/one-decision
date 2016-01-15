package io.onedecision.engine.decisions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class IdHelperTest {

    @Test
    public void testHandleSpace() {
        assertEquals("Chocolate_Orange",
                IdHelper.toIdentifier("Chocolate Orange"));
    }

    @Test
    public void testHandleApostrophes() {
        assertEquals("The_Best_Identifiers",
                IdHelper.toIdentifier("The 'Best' Identifiers"));
    }

    @Test
    public void testHandleQuotes() {
        assertEquals("I_call_this_variable_George",
                IdHelper.toIdentifier("I call this variable \"George\""));
    }

    @Test
    public void testHandleReservedWords() {
        assertEquals("_default", IdHelper.toIdentifier("default"));
    }

    @Test
    public void testHandleNull() {
        try {
            IdHelper.toIdentifier(null);
            fail("Identifier must not be null");
        } catch (Exception e) {
            ; // expected
        }
    }

    @Test
    public void testHandleFileNameToName() {
        assertEquals("Alternate Loan Origination Model",
                IdHelper.toName("AlternateLoanOriginationModel.dmn"));
    }
}
