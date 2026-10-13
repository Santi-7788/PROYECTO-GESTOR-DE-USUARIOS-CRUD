package modelo;

import java.util.Objects;

/**
 * Clase que representa el modelo de datos de un Usuario.
 * Contiene los atributos básicos, constructor, getters, setters
 * y métodos de utilidad para su manipulación en memoria.
 */
public class Usuario {

    private String id;
    private String nombre;
    private String correo;
    private String contrasena;

    /**
     * Constructor por defecto.
     */
    public Usuario() {
    }

    /**
     * Constructor con todos los atributos.
     *
     * @param id Identificador único del usuario
     * @param nombre Nombre completo del usuario
     * @param correo Dirección de correo electrónico
     * @param contrasena Contraseña de acceso
     */
    public Usuario(String id, String nombre, String correo, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // ==========================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING
    // ==========================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) || Objects.equals(correo, usuario.correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, correo);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
