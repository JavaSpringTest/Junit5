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

        assertNotNull(actual);
        assertEquals(esperando, actual);
        assertTrue(actual.equals("Luis"));
    }

    @Test
    void test_saldo_cuenta() {
        Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue()); // compara por referencia
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0); // menor que cero
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // mayor que cero
    }

    @Test
    void test_referencia_cuenta() {
        Cuenta cuenta1 = new Cuenta("Angel", BigDecimal.valueOf(8900.9997));
        Cuenta cuenta2 = new Cuenta("Angel", BigDecimal.valueOf(8900.9997));

        // assertNotEquals(cuenta1, cuenta2); // No son referencias iguales (objetos diferentes en memoria) -> true
        assertEquals(cuenta1, cuenta2); // Falla, ahora pasa porque se coloco un equals override
    }

    @Test
    void test_debito_cuenta() {
        Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        cuenta.debito(BigDecimal.valueOf(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900.12345, cuenta.getSaldo().doubleValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void test_credito_cuenta() {
        Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        cuenta.credito(BigDecimal.valueOf(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

}