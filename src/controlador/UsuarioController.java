package controlador;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import modelo.Usuario;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controlador principal de la vista de gestión de usuarios.
 * Gestiona la lógica del CRUD en memoria usando un ArrayList
 * y la sincronización con la interfaz JavaFX.
 */
public class UsuarioController implements Initializable {

    // ==========================================
    // COMPONENTES FXML - FORMULARIO Y CAMPOS
    // ==========================================
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtContrasenaVisible;
    @FXML private Button btnTogglePass;
    @FXML private Label lblModo;

    // ==========================================
    // COMPONENTES FXML - BOTONES DE ACCIÓN
    // ==========================================
    @FXML private Button btnRegistrar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    // ==========================================
    // COMPONENTES FXML - TABLA Y BÚSQUEDA
    // ==========================================
    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colContrasena;

    // ==========================================
    // COMPONENTES FXML - HEADER Y FEEDBACK
    // ==========================================
    @FXML private Label lblTotalUsuarios;
    @FXML private HBox bannerAlerta;
    @FXML private Label lblAlerta;

    // ==========================================
    // ESTRUCTURAS DE DATOS EN MEMORIA
    // ==========================================
    // Lista principal en memoria solicitada (ArrayList)
    private final ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    // Lista observable para la TableView de JavaFX
    private final ObservableList<Usuario> listaObservable = FXCollections.observableArrayList();
    // Lista filtrada para la búsqueda en tiempo real
    private FilteredList<Usuario> listaFiltrada;

    // Expresión regular estándar para validación de correos electrónicos
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Estado del visor de contraseñas
    private boolean mostrandoContrasena = false;

    // Usuario actualmente seleccionado para edición
    private Usuario usuarioSeleccionado = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarEventosSeleccion();
        configurarBusquedaFiltro();
        configurarAnimacionesBotones();
        sincronizarCamposContrasena();
        ocultarAlerta();

        // Cargar usuarios de ejemplo iniciales para que la interfaz luzca viva desde el primer segundo
        cargarUsuariosIniciales();
    }

    /**
     * Configura las columnas de la TableView con las propiedades del modelo Usuario.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        // Ocultar la contraseña con asteriscos en la tabla para mantener la seguridad visual
        colContrasena.setCellValueFactory(new PropertyValueFactory<>("contrasena"));
        colContrasena.setCellFactory(col -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("••••••••");
                }
            }
        });

        listaFiltrada = new FilteredList<>(listaObservable, p -> true);
        SortedList<Usuario> sortedList = new SortedList<>(listaFiltrada);
        sortedList.comparatorProperty().bind(tblUsuarios.comparatorProperty());
        tblUsuarios.setItems(sortedList);
    }

    /**
     * Detecta la selección de filas en la tabla para rellenar automáticamente los campos
     * del formulario y activar el modo edición.
     */
    private void configurarEventosSeleccion() {
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                usuarioSeleccionado = newVal;
                cargarDatosEnFormulario(newVal);
            }
        });
    }

    /**
     * Permite filtrar la tabla en tiempo real por ID, Nombre o Correo.
     */
    private void configurarBusquedaFiltro() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(usuario -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String termino = newValue.toLowerCase().trim();
                boolean coincideId = usuario.getId() != null && usuario.getId().toLowerCase().contains(termino);
                boolean coincideNombre = usuario.getNombre() != null && usuario.getNombre().toLowerCase().contains(termino);
                boolean coincideCorreo = usuario.getCorreo() != null && usuario.getCorreo().toLowerCase().contains(termino);

                return coincideId || coincideNombre || coincideCorreo;
            });
            actualizarContador();
        });
    }

    /**
     * Aplica transiciones suaves de escala (micro-animaciones) al pasar el cursor sobre los botones.
     */
    private void configurarAnimacionesBotones() {
        Button[] botones = {btnRegistrar, btnModificar, btnEliminar, btnLimpiar, btnTogglePass};
        for (Button btn : botones) {
            if (btn == null) continue;

            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), btn);
            scaleUp.setToX(1.04);
            scaleUp.setToY(1.04);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), btn);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            btn.setOnMouseEntered(e -> scaleUp.playFromStart());
            btn.setOnMouseExited(e -> scaleDown.playFromStart());
        }
    }

    /**
     * Mantiene sincronizados los campos de contraseña oculta y visible.
     */
    private void sincronizarCamposContrasena() {
        txtContrasenaVisible.setManaged(false);
        txtContrasenaVisible.setVisible(false);

        txtContrasena.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtContrasenaVisible.getText().equals(newVal)) {
                txtContrasenaVisible.setText(newVal);
            }
        });

        txtContrasenaVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtContrasena.getText().equals(newVal)) {
                txtContrasena.setText(newVal);
            }
        });
    }

    /**
     * Alterna la visibilidad de la contraseña en el formulario (mostrar/ocultar).
     */
    @FXML
    private void toggleVisibilidadContrasena(ActionEvent event) {
        mostrandoContrasena = !mostrandoContrasena;
        if (mostrandoContrasena) {
            txtContrasenaVisible.setText(txtContrasena.getText());
            txtContrasenaVisible.setVisible(true);
            txtContrasenaVisible.setManaged(true);
            txtContrasena.setVisible(false);
            txtContrasena.setManaged(false);
            btnTogglePass.setText("🙈");
            txtContrasenaVisible.requestFocus();
            txtContrasenaVisible.positionCaret(txtContrasenaVisible.getText().length());
        } else {
            txtContrasena.setText(txtContrasenaVisible.getText());
            txtContrasena.setVisible(true);
            txtContrasena.setManaged(true);
            txtContrasenaVisible.setVisible(false);
            txtContrasenaVisible.setManaged(false);
            btnTogglePass.setText("👁️");
            txtContrasena.requestFocus();
            txtContrasena.positionCaret(txtContrasena.getText().length());
        }
    }

    /**
     * Carga los datos de un usuario en los campos del formulario.
     */
    private void cargarDatosEnFormulario(Usuario usuario) {
        txtId.setText(usuario.getId());
        txtId.setDisable(true); // Bloquear ID en edición para proteger clave primaria
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtContrasena.setText(usuario.getContrasena());
        txtContrasenaVisible.setText(usuario.getContrasena());

        lblModo.setText("✏️ Modo: Editando Usuario #" + usuario.getId());
        lblModo.setStyle("-fx-text-fill: #4338CA; -fx-font-weight: bold;");
    }

    /**
     * Carga 3 usuarios demostrativos en el ArrayList para mostrar el diseño y funcionalidad de inmediato.
     */
    private void cargarUsuariosIniciales() {
        listaUsuarios.add(new Usuario("USR-101", "Valentina Gómez", "valentina.gomez@empresa.com", "segura2026"));
        listaUsuarios.add(new Usuario("USR-102", "Carlos Eduardo Mora", "carlos.mora@devmail.org", "claveAlfa99"));
        listaUsuarios.add(new Usuario("USR-103", "Sofía Hernández", "sofia.hdez@cloudservice.net", "passSof1a"));

        sincronizarListaObservable();
        actualizarContador();
    }

    /**
     * Sincroniza la lista observable de JavaFX con el ArrayList principal en memoria.
     */
    private void sincronizarListaObservable() {
        listaObservable.setAll(listaUsuarios);
        actualizarContador();
    }

    /**
     * Actualiza el badge con el conteo de usuarios.
     */
    private void actualizarContador() {
        int total = listaUsuarios.size();
        int filtrados = listaFiltrada != null ? listaFiltrada.size() : total;
        if (txtBuscar != null && !txtBuscar.getText().trim().isEmpty()) {
            lblTotalUsuarios.setText(filtrados + " de " + total + " Usuarios");
        } else {
            lblTotalUsuarios.setText(total + (total == 1 ? " Usuario Activo" : " Usuarios Activos"));
        }
    }

    // ==========================================
    // OPERACIONES CRUD
    // ==========================================

    /**
     * CREAR: Valida y registra un nuevo usuario en la lista en memoria.
     */
    @FXML
    private void handleRegistrar(ActionEvent event) {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = obtenerContrasenaActual().trim();

        // Validación: Campos no vacíos
        if (id.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta(TipoAlerta.ERROR, "Todos los campos son obligatorios. Por favor, completa el formulario.");
            return;
        }

        // Validación: Formato de correo electrónico
        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            mostrarAlerta(TipoAlerta.ADVERTENCIA, "El formato del correo electrónico no es válido (ejemplo: usuario@dominio.com).");
            return;
        }

        // Validación: Longitud mínima de contraseña
        if (contrasena.length() < 6) {
            mostrarAlerta(TipoAlerta.ADVERTENCIA, "La contraseña debe tener un mínimo de 6 caracteres.");
            return;
        }

        // Validación: ID único
        if (existeId(id, null)) {
            mostrarAlerta(TipoAlerta.ERROR, "Ya existe un usuario registrado con el ID '" + id + "'.");
            return;
        }

        // Validación: Correo electrónico único
        if (existeCorreo(correo, null)) {
            mostrarAlerta(TipoAlerta.ERROR, "Ya existe un usuario registrado con el correo '" + correo + "'.");
            return;
        }

        // Creación del usuario y almacenamiento en el ArrayList
        Usuario nuevoUsuario = new Usuario(id, nombre, correo, contrasena);
        listaUsuarios.add(nuevoUsuario);

        // Sincronizar con la interfaz y limpiar
        sincronizarListaObservable();
        mostrarAlerta(TipoAlerta.EXITO, "¡Usuario '" + nombre + "' registrado con éxito en memoria!");
        handleLimpiar(null);

        // Seleccionar y enfocar en la tabla el usuario recién creado
        tblUsuarios.getSelectionModel().select(nuevoUsuario);
    }

    /**
     * EDITAR: Valida y actualiza los datos del usuario seleccionado.
     */
    @FXML
    private void handleModificar(ActionEvent event) {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();

        // Validación: No modificar sin selección
        if (seleccionado == null) {
            mostrarAlerta(TipoAlerta.ADVERTENCIA, "Debes seleccionar un usuario de la tabla para poder modificarlo.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = obtenerContrasenaActual().trim();

        // Validación: Campos no vacíos
        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta(TipoAlerta.ERROR, "Los campos Nombre, Correo y Contraseña no pueden quedar vacíos.");
            return;
        }

        // Validación: Formato de correo electrónico
        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            mostrarAlerta(TipoAlerta.ADVERTENCIA, "El correo ingresado no tiene un formato válido.");
            return;
        }

        // Validación: Longitud mínima de contraseña
        if (contrasena.length() < 6) {
            mostrarAlerta(TipoAlerta.ADVERTENCIA, "La contraseña debe contener al menos 6 caracteres.");
            return;
        }

        // Validación: Correo único (no perteneciente a otro usuario)
        if (existeCorreo(correo, seleccionado.getId())) {
            mostrarAlerta(TipoAlerta.ERROR, "El correo '" + correo + "' ya está en uso por otro usuario.");
            return;
        }

        // Actualizar datos en memoria
        seleccionado.setNombre(nombre);
        seleccionado.setCorreo(correo);
        seleccionado.setContrasena(contrasena);

        // Refrescar tabla y vista
        tblUsuarios.refresh();
        sincronizarListaObservable();
        mostrarAlerta(TipoAlerta.EXITO, "¡Usuario '" + seleccionado.getId() + "' actualizado correctamente!");
        handleLimpiar(null);
    }

    /**
     * ELIMINAR: Pide confirmación al usuario y elimina el registro de memoria.
     */
    @FXML
    private void handleEliminar(ActionEvent event) {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();

        // Validación: No eliminar sin selección
        if (seleccionado == null) {
            mostrarAlerta(TipoAlerta.ADVERTENCIA, "Debes seleccionar un usuario de la tabla para poder eliminarlo.");
            return;
        }

        // Diálogo modal de confirmación con diseño estilizado
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar a " + seleccionado.getNombre() + "?");
        confirmacion.setContentText("ID: " + seleccionado.getId() + "\nCorreo: " + seleccionado.getCorreo() +
                "\n\nEsta acción borrará al usuario de la memoria.");

        // Personalización de botones
        ButtonType btnSi = new ButtonType("Sí, Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnCancelar);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == btnSi) {
            // Eliminar del ArrayList en memoria
            listaUsuarios.remove(seleccionado);
            sincronizarListaObservable();
            mostrarAlerta(TipoAlerta.EXITO, "Usuario '" + seleccionado.getNombre() + "' eliminado de la memoria.");
            handleLimpiar(null);
        }
    }

    /**
     * LIMPIAR: Restablece todos los campos del formulario y cancela la selección.
     */
    @FXML
    private void handleLimpiar(ActionEvent event) {
        txtId.clear();
        txtId.setDisable(false);
        txtNombre.clear();
        txtCorreo.clear();
        txtContrasena.clear();
        txtContrasenaVisible.clear();

        tblUsuarios.getSelectionModel().clearSelection();
        usuarioSeleccionado = null;

        lblModo.setText("➕ Modo: Nuevo Registro");
        lblModo.setStyle("-fx-text-fill: #1E293B; -fx-font-weight: bold;");

        // Si la contraseña estaba visible, volver a modo seguro por defecto
        if (mostrandoContrasena) {
            toggleVisibilidadContrasena(null);
        }

        txtId.requestFocus();
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE VALIDACIÓN Y FEEDBACK
    // ==========================================

    /**
     * Obtiene el valor actual de la contraseña dependiendo del campo que esté activo.
     */
    private String obtenerContrasenaActual() {
        return mostrandoContrasena ? txtContrasenaVisible.getText() : txtContrasena.getText();
    }

    /**
     * Verifica si un ID ya existe en el ArrayList.
     * @param id ID a verificar
     * @param idIgnorado ID a excluir (para modificaciones)
     */
    private boolean existeId(String id, String idIgnorado) {
        for (Usuario u : listaUsuarios) {
            if (idIgnorado != null && u.getId().equalsIgnoreCase(idIgnorado)) {
                continue;
            }
            if (u.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si un correo electrónico ya está registrado en memoria.
     * @param correo Correo a verificar
     * @param idIgnorado ID del usuario actual para permitir mantener su mismo correo
     */
    private boolean existeCorreo(String correo, String idIgnorado) {
        for (Usuario u : listaUsuarios) {
            if (idIgnorado != null && u.getId().equalsIgnoreCase(idIgnorado)) {
                continue;
            }
            if (u.getCorreo().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tipos de mensajes de alerta con estilo visual.
     */
    public enum TipoAlerta {
        EXITO, ADVERTENCIA, ERROR, INFO
    }

    /**
     * Muestra una notificación visual en el banner superior con borde lateral y color vivo.
     */
    private void mostrarAlerta(TipoAlerta tipo, String mensaje) {
        bannerAlerta.setVisible(true);
        bannerAlerta.setManaged(true);
        lblAlerta.setText(mensaje);

        bannerAlerta.getStyleClass().removeAll("alerta-exito", "alerta-advertencia", "alerta-error", "alerta-info");

        switch (tipo) {
            case EXITO:
                bannerAlerta.getStyleClass().add("alerta-exito");
                break;
            case ADVERTENCIA:
                bannerAlerta.getStyleClass().add("alerta-advertencia");
                break;
            case ERROR:
                bannerAlerta.getStyleClass().add("alerta-error");
                break;
            case INFO:
            default:
                bannerAlerta.getStyleClass().add("alerta-info");
                break;
        }

        // Animación suave de aparición
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), bannerAlerta);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Oculta el banner de alertas.
     */
    @FXML
    private void ocultarAlerta() {
        bannerAlerta.setVisible(false);
        bannerAlerta.setManaged(false);
    }
}
