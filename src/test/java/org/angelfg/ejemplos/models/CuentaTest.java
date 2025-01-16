package org.angelfg.ejemplos.models;

// import org.junit.jupiter.api.Assertions;

import org.angelfg.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // quitar el static para que sea stateless
class CuentaTest {

    private Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter reporter;

    @BeforeEach // Se ejecuta antes de cada metodo
    void initMetodoTest(TestInfo testInfo, TestReporter reporter) {
        this.testInfo = testInfo;
        this.reporter = reporter;

        // timestamp = 2025-01-16T13:25:56.924021900, value = Ejecutando: Probando nombre de la cuenta test_nombre_cuenta con las etiquetas[cuenta]
        reporter.publishEntry("Ejecutando: " +
                testInfo.getDisplayName() + " " +
                testInfo.getTestMethod().orElse(null).getName() +
                " con las etiquetas" + testInfo.getTags());

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

    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class CuentaOperacionesTest {

        @Tag("cuenta")
        @Test
        @DisplayName(value = "Probando nombre de la cuenta")
        void test_nombre_cuenta() {

            reporter.publishEntry(testInfo.getTags().toString()); // timestamp = 2025-01-16T13:26:50.159690600, value = [cuenta]
            if (testInfo.getTags().contains("cuenta")) {
                reporter.publishEntry("Hacer algo con la etiqueta cuenta"); // timestamp = 2025-01-16T13:26:50.159690600, value = Hacer algo con la etiqueta cuenta
            }

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

        @Tag("cuenta")
        @Tag("banco")
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
            cuenta.debito(BigDecimal.valueOf(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900.12345, cuenta.getSaldo().doubleValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        void test_credito_cuenta() {
            //Cuenta cuenta = new Cuenta("Luis", BigDecimal.valueOf(1000.12345));
            cuenta.credito(BigDecimal.valueOf(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("error")
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

        @Tag("cuenta")
        @Tag("banco")
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

    }

    // Pruebas de sistema operativo, etc.

    @Nested // Anidar testing en clases
    class SistemaOperativoTest {

        @Test
        @EnabledOnOs(OS.WINDOWS) // sistema operativo, solo en windows
        void test_solo_windows() {}

        @Test
        @EnabledOnOs({ OS.LINUX, OS.MAC })
        void test_linux_and_mac() {}

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void test_no_windows() {}

    }

    @Nested
    class SistemPropertiesTest {

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

    @Nested
    class VariableAmbienteTest {

        @Test
        void test_imprimir_variables_ambientes() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
//    @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "C:\\Program Files\\Java\\jdk1.8.0_202")
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk1.8.0_202.*")
        void test_java_home() {}

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "24")
        void test_procesadores() {}

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev") // poner en configuracion ENVIRONMENT=dev
        void test_env() {}

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod") // poner en configuracion ENVIRONMENT=dev
        void test_env_prod_disabled() {}

    }

    @Tag("cuenta")
    @Nested
    class CuentaNombreSaldoTest {

        @Test
        @DisplayName(value = "Probando saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado")
        void test_saldo_cuenta_dev() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumeTrue(esDev);

            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue()); // compara por referencia
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0); // menor que cero
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // mayor que cero
        }

        @Test
        @DisplayName(value = "Test Saldo Cuenta Dev 2")
        void test_saldo_cuenta_dev_2() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));

            assumingThat(esDev, () -> {
                assertEquals(1000.12345, cuenta.getSaldo().doubleValue()); // compara por referencia
                assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0); // menor que cero
                assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // mayor que cero
                assertNotNull(cuenta.getSaldo());
            });

        }

    }

    @Tag("param")
    @DisplayName("Probando debito cuenta repetir!")
    @RepeatedTest(value = 5, name = "{displayName} - Repetición numero {currentRepetition} de {totalRepetitions}") // Repite el test las veces que las necesitemos
    void test_debito_cuenta_repetir(RepetitionInfo info) { // info obtener informacion DI

        if (info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repeticion " + info.getCurrentRepetition());
        }

        cuenta.debito(BigDecimal.valueOf(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900.12345, cuenta.getSaldo().doubleValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param") // lo integra a todos los metodos
    @Nested
    class PruebasParametrizadasTest {

        // Casos en un mismo test
        @ParameterizedTest(name = "Numero {index} ejecutando con el valor {0} - {argumentsWithNames}")
        @ValueSource(strings = { "100", "200", "300", "500", "700", "1000" })
        void test_debito_cuenta_value_source(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Numero {index} ejecutando con el valor {0} - {argumentsWithNames}")
        @CsvSource({ "1,100", "2,200", "3,300", "4,500", "5,700", "6,1000" }) // index, value
        void test_debito_cuenta_csv_source(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Numero {index} ejecutando con el valor {0} - {argumentsWithNames}")
        @CsvSource({ "200,100", "250,200", "301,300", "510,500", "750,700", "1001,1000" }) // valor de prueba (valor esperado), monto (valor actual)
        void test_debito_cuenta_csv_source_2(String saldo, String monto) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Numero {index} ejecutando con el valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv") // archivo en resource .csv
        void test_debito_cuenta_csv_file_source(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Numero {index} ejecutando con el valor {0} - {argumentsWithNames}")
        @MethodSource("montoList") // metodo montoList()
        void test_debito_cuenta_method_source(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        static List<String> montoList() {
            return Arrays.asList("100", "200", "300", "500", "700", "1000");
        }

    }

    @Nested
    @Tag("timeout")
    class EjemploTimeout {

        @Test
        @Timeout(1) // 5 segundos maximo
        void prueba_timeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void prueba_timeout_2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void prueba_timeout_assertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }

}