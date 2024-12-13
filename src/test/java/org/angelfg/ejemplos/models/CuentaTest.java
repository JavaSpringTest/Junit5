package org.angelfg.ejemplos.models;

// import org.junit.jupiter.api.Assertions;
import org.angelfg.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // quitar el static para que sea stateless
class CuentaTest {

    private Cuenta cuenta;

    @BeforeEach // Se ejecuta antes de cada metodo
    void initMetodoTest() {
        System.out.println("Iniciando metodo");
        this.cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345)); // reutilizamos
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Test
    @DisplayName(value = "Probando nombre de la cuenta")
    void test_nombre_cuenta() {

        //cuenta.setPersona("Luis");

        String esperando = "Luis";
        String actual = cuenta.getPersona();

        // Incluir mensajes para saber cuando falle, un error mas en concreto
        assertNotNull(actual, "La cuenta no puede ser nula");
        assertEquals(
            esperando,
            actual,
            () -> "El nombre de la cuenta no es el que se esperaba: " + esperando + " sin embargo fue: " + actual
        );
        assertTrue(actual.equals("Luis"), "EL nombre de la cuenta debe ser igual a la actual");
    }

    @Test
    @DisplayName(value = "Probando saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado")
    void test_saldo_cuenta() {
        //this.cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue()); // compara por referencia
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0); // menor que cero
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // mayor que cero
    }

    @Test
    @DisplayName(value = "Testeando referencias que sean iguales con el método equals")
    void test_referencia_cuenta() {
        Cuenta cuenta1 = new Cuenta("Angel", BigDecimal.valueOf(8900.9997));
        Cuenta cuenta2 = new Cuenta("Angel", BigDecimal.valueOf(8900.9997));

        // assertNotEquals(cuenta1, cuenta2); // No son referencias iguales (objetos diferentes en memoria) -> true
        assertEquals(cuenta1, cuenta2); // Falla, ahora pasa porque se coloco un equals override
    }

    @Test
    void test_debito_cuenta() {
        //this.cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        this.cuenta.debito(BigDecimal.valueOf(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900.12345, cuenta.getSaldo().doubleValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void test_credito_cuenta() {
        //Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
        this.cuenta.credito(BigDecimal.valueOf(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void test_dinero_insuficiente_exception() {
        // Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(BigDecimal.valueOf(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";

        assertEquals(esperado, actual);
    }

    @Test
    void test_transferir_dinero_cuentas() {
        Cuenta cuenta1 = new Cuenta("Luis", BigDecimal.valueOf(2500));
        Cuenta cuenta2 = new Cuenta("Angel", BigDecimal.valueOf(1500.8989));

        Banco banco = new Banco();
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, BigDecimal.valueOf(500));

        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    void test_relacion_banco_cuentas() {
        Cuenta cuenta1 = new Cuenta("Luis", BigDecimal.valueOf(2500));
        Cuenta cuenta2 = new Cuenta("Angel", BigDecimal.valueOf(1500.8989));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, BigDecimal.valueOf(500));

        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());
        assertEquals("Banco del estado", cuenta1.getBanco().getNombre());
        assertEquals("Luis", banco.getCuentas()
                .stream()
                .filter(cuenta -> cuenta.getPersona().equals("Luis"))
                .findFirst()
                .get()
                .getPersona()
        );

        assertTrue(banco.getCuentas().stream().anyMatch(cuenta -> cuenta.getPersona().equals("Luis")));
    }

    @Test
    @Disabled // Deshabilitamos el test, se pone en pausa o se ignora
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
    void test_relacion_banco_cuentas_assertAll() {
        fail(); // Fuerza el error
        Cuenta cuenta1 = new Cuenta("Luis", BigDecimal.valueOf(2500));
        Cuenta cuenta2 = new Cuenta("Angel", BigDecimal.valueOf(1500.8989));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, BigDecimal.valueOf(500));

        // Ejecuta todos los assert
        // La ventaja es que muestra todos los errores y no solo el primero que falle
        assertAll(
            () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta 2 no es el esperado"),
            () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),  () -> "El valor del saldo de la cuenta 1 no es el esperado"),
            () -> assertEquals(2, banco.getCuentas().size(), () -> "El banco no tiene las cuentas esperadas"),
            () -> assertEquals("Banco del estado", cuenta1.getBanco().getNombre()),
            () -> {
                assertEquals("Luis", banco.getCuentas()
                        .stream()
                        .filter(cuenta -> cuenta.getPersona().equals("Luis"))
                        .findFirst()
                        .get()
                        .getPersona()
                );
            },
            () -> assertTrue(banco.getCuentas().stream().anyMatch(cuenta -> cuenta.getPersona().equals("Luis")))
        );

    }

    // Pruebas de sistema operativo, etc.
    @Test
    @EnabledOnOs(OS.WINDOWS) // sistema operativo, solo en windows
    void test_solo_windows() {}

    @Test
    @EnabledOnOs({ OS.LINUX, OS.MAC })
    void test_linux_and_mac() {}

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void test_no_windows() {}

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void test_solo_jdk8() {}

    @Test
    @DisabledOnJre(JRE.JAVA_15)
    void test_solo_jdk15() {}

    @Test
    void test_imprimir_system_properties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "17.0.13")
    void test_java_version() {}

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void test_solo_64() {}

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void test_no_64() {}

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "PC")
    void test_username() {}

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev") // Lo configuramos en configuracionde run -ea -DENV=dev
    void test_dev() {}

}