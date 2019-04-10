package com.example.conferencerommapp.ValidateField


import junit.framework.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ValidateInputFieldsTest {

    /**
     * test for correct or valid input
     */
    @Test
    fun validateInputFields_correctInput_returnTrue() {
        val name = "Narcissus"
        assertSame(true, ValidateInputFields.validateInputForEmpty(name))
    }

    /**
     * test for input only contains white spaces
     */
    @Test
    fun validateInputFields_inputOnlySpaces_returnFalse() {
        val name = "   "
        assertSame(false, ValidateInputFields.validateInputForEmpty(name))
    }

    /**
     * test for input contains white spaces at start
     */
    @Test
    fun validateInputFields_inputWithSpacesAtStart_returnTrue() {
        val name = "   Narcissus"
        assertSame(true, ValidateInputFields.validateInputForEmpty(name))
    }

    /**
     * test for input contains white spaces at end
     */
    @Test
    fun validateInputFields_inputWithSpacesAtEnd_returnTrue() {
        val name = "Narcissus   "
        assertSame(true, ValidateInputFields.validateInputForEmpty(name))
    }

    /**
     * test for input contains white spaces at both ends
     */
    @Test
    fun validateInputFields_inputWithSpacesAtBothEnd_returnTrue() {
        val name = "   Narcissus   "
        assertSame(true, ValidateInputFields.validateInputForEmpty(name))
    }
}