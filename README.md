# 👥 Gestor de Usuarios - CRUD en Memoria (JavaFX & CSS)

Aplicación de escritorio moderna desarrollada en **Java con JavaFX** y **CSS personalizado**, diseñada siguiendo el patrón arquitectónico **MVC (Modelo - Vista - Controlador)**.

Los datos se administran en memoria mediante una estructura `ArrayList<Usuario>`, sin necesidad de bases de datos externas ni persistencia en disco.

---

## 🎨 Paleta de Colores Vivos y Modernos

| Elemento | Color | Hexadecimal |
| :--- | :--- | :--- |
| 🔵 Azul Principal | Primario vibrante | `#2563EB` |
| 🔵 Azul Oscuro | Encabezados y sombras | `#1D4ED8` |
| 🟦 Azul Claro | Botón Modificar / Enlaces | `#3B82F6` |
| 🟣 Índigo Oscuro | Barra superior y modo edición | `#4338CA` |
| 🟢 Verde Brillante | Botón Registrar / Éxito | `#10B981` |
| 🔴 Rojo Vivo | Botón Eliminar / Errores | `#EF4444` |
| 🟠 Naranja Brillante | Botón Limpiar / Advertencias | `#F97316` |
| ⚪ Fondo Suave | Fondo degradado ventana | `#F0F4F8` |
| ⚫ Texto Principal | Títulos y etiquetas | `#1E293B` |
| ⚪ Texto Claro | Encabezados y botones | `#F8FAFC` |

---

## ✨ Características y Diseño Visual

- **Fondo degradado:** Fondo suave con transición diagonal.
- **Tarjetas redondeadas (16px):** Sombras difuminadas y bordes sutiles que generan profundidad (*elevation*).
- **Botones interactivos (10px):** Gradientes vivos con micro-animaciones al pasar el cursor (`hover`) y presionar (`pressed`).
- **Campos de formulario (8px):** Resplandor (*focus glow*) en azul vibrante al enfocar un campo.
- **Tabla estilizada:** Encabezado con degradado azul sólido, texto blanco y filas alternas con efecto hover.
- **Visor de contraseña:** Alternancia entre contraseña oculta (`••••••••`) y visible con un solo clic.
- **Buscador en tiempo real:** Filtro instantáneo por ID, Nombre o Correo.
- **Banners de notificación:** Alertas dinámicas con borde lateral decorativo según el estado (éxito, advertencia, error).

---

## 🎯 Funcionalidades CRUD y Validaciones

1. **Crear usuario:**
   - Valida que ningún campo esté vacío.
   - Valida formato de correo con expresión regular.
   - Valida longitud mínima de contraseña (≥ 6 caracteres).
   - Valida que el ID y el correo no existan previamente en el `ArrayList`.
2. **Consultar / Filtrar:**
   - Visualización tabular con contador activo de usuarios.
   - Búsqueda dinámica en tiempo real.
3. **Modificar usuario:**
   - Requiere seleccionar una fila de la tabla.
   - Permite actualizar Nombre, Correo y Contraseña protegiendo el ID único.
   - Valida duplicidad de correo contra otros usuarios.
4. **Eliminar usuario:**
   - Requiere seleccionar un registro.
   - Muestra cuadro de diálogo modal de confirmación antes de borrar del `ArrayList`.
5. **Limpiar formulario:**
   - Restablece todos los campos y deselecciona la tabla.

---

## 📁 Estructura del Proyecto

```text
CRUD GESTOR USUARIOS/
├── pom.xml                               # Configuración Maven con JavaFX 21
├── README.md                             # Documentación del proyecto
├── resources/
│   └── estilos.css                       # Hoja de estilos CSS
└── src/
    ├── modelo/
    │   └── Usuario.java                  # Modelo de datos
    ├── controlador/
    │   └── UsuarioController.java        # Controlador CRUD y eventos FXML
    ├── vista/
    │   └── MainView.fxml                 # Interfaz visual FXML
    ├── aplicacion/
    │   ├── Main.java                     # Inicializador de la ventana JavaFX
    │   └── Launcher.java                 # Punto de entrada para ejecución estándar
    └── main/
        ├── java/                         # Fuentes estándar Maven
        └── resources/                    # Recursos empaquetados Maven
```

---

## 🚀 Instrucciones de Ejecución

### Opción 1: Desde IntelliJ IDEA (Recomendada)
1. Abre el proyecto en **IntelliJ IDEA**.
2. En la barra superior de ejecución, selecciona una de las configuraciones ya preparadas:
   - **`Ejecutar Gestor Usuarios (Launcher)`** o
   - **`Maven javafx:run`**
3. Presiona el botón verde de **Run** (o `Shift + F10`).

### Opción 2: Desde la terminal con Maven
Abre una terminal en la raíz del proyecto y ejecuta:
```bash
mvn javafx:run
```

Si usas el Maven integrado de IntelliJ:
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" javafx:run
```

### Opción 3: Compilar y empaquetar JAR
```bash
mvn clean package
```
El archivo JAR ejecutable se generará en la carpeta `target/`.
