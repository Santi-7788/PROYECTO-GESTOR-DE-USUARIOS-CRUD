package modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el modelo Usuario y su comportamiento en memoria.
 */
public class UsuarioTest {

    @Test
    public void testCrearUsuario() {
        Usuario usuario = new Usuario("USR-01", "Ana Morales", "ana@test.com", "clave123");
        assertEquals("USR-01", usuario.getId());
        assertEquals("Ana Morales", usuario.getNombre());
        assertEquals("ana@test.com", usuario.getCorreo());
        assertEquals("clave123", usuario.getContrasena());
    }

    @Test
    public void testModificarUsuario() {
        Usuario usuario = new Usuario("USR-01", "Ana Morales", "ana@test.com", "clave123");
        usuario.setNombre("Ana Sofía Morales");
        usuario.setCorreo("anasofia@test.com");
        usuario.setContrasena("nuevaClave789");

        assertEquals("Ana Sofía Morales", usuario.getNombre());
        assertEquals("anasofia@test.com", usuario.getCorreo());
        assertEquals("nuevaClave789", usuario.getContrasena());
    }

    @Test
    public void testEqualsPorIdOCorreo() {
        Usuario u1 = new Usuario("USR-01", "Ana Morales", "ana@test.com", "clave123");
        Usuario u2 = new Usuario("USR-01", "Otro Nombre", "otro@test.com", "otraClave");
        Usuario u3 = new Usuario("USR-02", "Tercero", "ana@test.com", "clave999");

        assertEquals(u1, u2, "Deben ser iguales si comparten el mismo ID");
        assertEquals(u1, u3, "Deben ser iguales si comparten el mismo Correo");
    }
}
