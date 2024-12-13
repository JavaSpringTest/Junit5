package org.angelfg.ejemplos.models;

// import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void test_nombre_cuenta() {
        Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        //cuenta.setPersona("Luis");

        String esperando = "Luis";
        String actual = cuenta.getPersona();

        assertEquals(esperando, actual);
        assertTrue(actual.equals("Luis"));
    }

    @Test
    void test_saldo_cuenta() {
        Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0); // menor que cero
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // mayor que cero
    }

}