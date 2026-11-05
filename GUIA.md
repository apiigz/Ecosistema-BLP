# GUÍA — Simulador de Ecosistema IE2

Lo que ya está hecho (y probado) en el zip: **modelo, dao, controlador y main**.
Lo que falta y lo hacen ustedes: **la Vista (en el diseñador de NetBeans)** y **el Figma**.

> ⚠️ Importante: la profe evalúa en el video que **cada uno entienda y explique su parte**, y pide
> documentar el uso de IA. Guarden el link de esta conversación completa (o capturas) en la carpeta
> de documentación, **cada uno la suya**, y lean el código de la parte que les toque.

---

## 1. Cómo es el esquema (la analogía del restaurante)

| Capa | Es como... | Qué hace |
|---|---|---|
| **Vista** | el mozo | Muestra cosas y toma pedidos. No piensa: cada clic se lo pasa al controlador. |
| **Controlador** | el encargado | Recibe el pedido, verifica que esté bien (validar), le habla a la cocina y al archivo, y le devuelve al mozo lo que tiene que mostrar. |
| **Modelo** | la cocina | Las reglas del juego (plantas, conejos, lobos, clima, turnos). No habla con nadie. |
| **DAO** | la heladera/archivero | Guarda y lee el `.txt`. Solo eso. |

Flujo de "Avanzar turno": `botón → controlador.avanzarTurno() → modelo.procesarTurno() → dao.guardar() → controlador actualiza la vista`.

---

## 2. Pasar el código a su proyecto de NetBeans

1. Abrí el proyecto del repo (`Ecosistema-BLP`) en NetBeans.
2. En *Source Packages* **borrá el paquete `ecosistema.blp`** (el viejo).
3. Copiá las carpetas del zip (`modelo`, `dao`, `controlador`, `principal`) dentro de la carpeta `src` del proyecto. En NetBeans: clic derecho en el proyecto → *Reload* / cerrar y abrir.
4. Clic derecho en el proyecto → **Properties → Run → Main Class** → elegir `principal.Main`.
5. Poné `nbproject/private/` en un `.gitignore` (tiene rutas de tu compu y la consigna prohíbe rutas absolutas).
6. **Commit** con un mensaje descriptivo, por ejemplo: `Migra modelo, dao y controlador a MVC`.

Todavía no compila hasta que exista `vista.VentanaPrincipal` (paso siguiente): `Main` la necesita.

---

## 3. La Vista paso a paso (diseñador de NetBeans)

**Regla de oro:** nada de `new JButton()`, `add()`, `setLayout()`, `setBounds()`. Todo lo arrastrás desde la paleta.
El layout por defecto del diseñador (Free Design) **sí** vale; "Absolute Layout" **no**.

### 3.1 Crear la ventana
- Clic derecho en el proyecto → *New → JFrame Form* → nombre **`VentanaPrincipal`**, paquete **`vista`**.
- Arrastrá un **JTabbedPane** que ocupe toda la ventana. Cambiale el nombre de variable a **`pestanias`** (clic derecho → *Change Variable Name*).
- Agregá 3 pestañas (arrastrando un JPanel a cada una) y poneles título: *Configuración*, *Simulación*, *Reporte*.

> Cambiar el *nombre de variable* de cada componente es **obligatorio**: el código de abajo usa esos nombres exactos.

### 3.2 Pestaña "Configuración"
| Componente | Nombre de variable | Notas |
|---|---|---|
| JTextField | `txtPlantas` | rotulalo con un JLabel "Plantas (5-30)" |
| JTextField | `txtConejos` | "Conejos (2-15)" |
| JTextField | `txtLobos` | "Lobos (1-5)" |
| JTextField | `txtTurnos` | "Turnos (10-50)" |
| JComboBox | `cmbClima` | *Properties → model*: `Soleado, Lluvioso, Sequía, Invierno` |
| JButton | `btnIniciar` | texto "Iniciar simulación" |
| JLabel | `lblErrorConfig` | texto vacío, color de letra rojo. Acá salen los errores del formulario |

### 3.3 Pestaña "Simulación" (la distribución sugerida por la profe)
Usá un panel con **BorderLayout** (clic derecho en el panel → *Set Layout → Border Layout*) y arrastrá paneles a cada zona:

- **Norte** — 5 JLabel: `lblTurno`, `lblClima`, `lblPlantas`, `lblConejos`, `lblLobos`.
- **Centro** — un **JScrollPane** con un **JTable** adentro llamado `tblEntidades`.
  *Properties → model → Table Settings*: 5 columnas con título `Tipo, Nombre, Energía, Edad, Detalle`, **0 filas**, y destildá *Editable* en todas.
- **Este** — un **JScrollPane** con un **JList** adentro llamado `lstEventos` (poné un título "Eventos del turno").
- **Sur** — paneles con:
  - `btnAvanzar` ("Avanzar turno")
  - Panel "Intervención": `cmbClimaIntervencion` (mismos 4 climas) + `btnCambiarClima`, y `cmbTipoEntidad` (items `Planta, Conejo, Lobo`) + `btnAgregarEntidad`
  - `btnGuardar`, `btnNueva` ("Nueva simulación"), `btnVerReporte` ("Ver reporte")
  - `lblEstado`: mensajes de estado ("próxima intervención en el turno 6")

### 3.4 Pestaña "Reporte"
- JScrollPane + JTable `tblReporte` con 2 columnas: `Dato, Valor`, 0 filas, no editable.
- JButton `btnNuevaDesdeReporte` ("Nueva simulación").

### 3.5 Conectar los botones
Doble clic en cada botón → NetBeans crea el método `...ActionPerformed`. Adentro solo va una línea que llama al controlador (están todos en el código de abajo).

### 3.6 Pegar el código
1. Arriba del archivo, agregá los imports y hacé que la clase implemente `IVista`:
```java
package vista;

import controlador.ControladorSimulacion;
import controlador.IVista;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class VentanaPrincipal extends javax.swing.JFrame implements IVista {
```
2. Pegá esto **dentro de la clase** (debajo del constructor). Si NetBeans ya generó los `...ActionPerformed` vacíos, reemplazá el cuerpo de cada uno por la línea que corresponde (no los dupliques):

```java
    private ControladorSimulacion controlador;

    public void setControlador(ControladorSimulacion controlador) {
        this.controlador = controlador;
    }

    // ---------- navegación ----------
    @Override public void mostrarPantallaConfiguracion() { pestanias.setSelectedIndex(0); }
    @Override public void mostrarPantallaSimulacion() { lblErrorConfig.setText(""); pestanias.setSelectedIndex(1); }
    @Override public void mostrarPantallaReporte() { pestanias.setSelectedIndex(2); }

    // ---------- datos ----------
    @Override
    public void mostrarContadores(int turno, int turnosTotales, String clima, int plantas, int conejos, int lobos) {
        lblTurno.setText("Turno: " + turno + " / " + turnosTotales);
        lblClima.setText("Clima: " + clima);
        lblPlantas.setText("Plantas: " + plantas);
        lblConejos.setText("Conejos: " + conejos);
        lblLobos.setText("Lobos: " + lobos);
    }

    @Override
    public void mostrarEntidades(Object[][] filas) {
        DefaultTableModel modelo = (DefaultTableModel) tblEntidades.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    @Override
    public void mostrarEventos(List<String> eventos) {
        lstEventos.setListData(eventos.toArray(new String[0]));
    }

    @Override
    public void mostrarReporte(String[][] filas) {
        DefaultTableModel modelo = (DefaultTableModel) tblReporte.getModel();
        modelo.setRowCount(0);
        for (String[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    @Override public void mostrarMensajeEstado(String mensaje) { lblEstado.setText(mensaje); }

    // ---------- habilitar / deshabilitar ----------
    @Override public void habilitarAvance(boolean habilitado) { btnAvanzar.setEnabled(habilitado); }

    @Override
    public void habilitarIntervencion(boolean habilitada) {
        cmbClimaIntervencion.setEnabled(habilitada);
        btnCambiarClima.setEnabled(habilitada);
        cmbTipoEntidad.setEnabled(habilitada);
        btnAgregarEntidad.setEnabled(habilitada);
    }

    @Override public void habilitarReporte(boolean habilitado) { btnVerReporte.setEnabled(habilitado); }

    // ---------- mensajes ----------
    @Override
    public void mostrarErrorConfiguracion(String mensaje) {
        lblErrorConfig.setText("<html>" + mensaje.replace("\n", "<br>") + "</html>");
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ---------- eventos de botones (NetBeans genera el método al hacer doble clic en el botón) ----------
    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        controlador.iniciarSimulacion(txtPlantas.getText(), txtConejos.getText(), txtLobos.getText(),
                txtTurnos.getText(), (String) cmbClima.getSelectedItem());
    }
    private void btnAvanzarActionPerformed(java.awt.event.ActionEvent evt) { controlador.avanzarTurno(); }
    private void btnCambiarClimaActionPerformed(java.awt.event.ActionEvent evt) {
        controlador.cambiarClima((String) cmbClimaIntervencion.getSelectedItem());
    }
    private void btnAgregarEntidadActionPerformed(java.awt.event.ActionEvent evt) {
        controlador.agregarEntidad((String) cmbTipoEntidad.getSelectedItem());
    }
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) { controlador.guardar(); }
    private void btnNuevaActionPerformed(java.awt.event.ActionEvent evt) { controlador.nuevaSimulacion(); }
    private void btnNuevaDesdeReporteActionPerformed(java.awt.event.ActionEvent evt) { controlador.nuevaSimulacion(); }
    private void btnVerReporteActionPerformed(java.awt.event.ActionEvent evt) { controlador.verReporte(); }
```

3. Tamaño mínimo de columnas y *scroll*: si algo se ve cortado, ajustá el ancho de las columnas en el *Table Settings* y asegurate de que la tabla/lista estén dentro de un **JScrollPane**.

### 3.7 Cosas que NO pueden hacer (anulan el criterio)
- Código Swing en `modelo`, `dao` o `controlador` (incluido `JOptionPane`).
- `Scanner` o `System.out.println` en el modelo.
- Que la vista lea el modelo o el archivo directamente.
- Lógica del juego en la vista (la vista solo muestra y avisa).
- Crear componentes por código.
- La única excepción: si hay algo que el diseñador no permite, lo hacen acotado **y lo explican en el video**.

### 3.8 Bonus fáciles
- **JMenuBar**: arrastrá un *Menu Bar* al frame; menús "Simulación → Nueva" (llama `controlador.nuevaSimulacion()`) y "Reportes → Ver reporte" (`controlador.verReporte()`).
- **ImageIcon**: se puede poner el ícono desde las *Properties → icon* de cada JLabel en el diseñador.

---

## 4. Figma: qué tienen que hacer (en orden)

Figma es gratis: figma.com → crear cuenta → *New design file*. **Uno** crea el archivo e invita a los demás (*Share* → email, permiso de edición).

### Paso 1 — Persona y escenario (un cuadro de texto en la portada)
Texto para adaptar:
> **Persona:** Lucía, 16 años, estudia Biología y quiere entender cómo cambia un ecosistema cuando cambian las condiciones.
> **Escenario:** Tiene 10 minutos en clase. Configura un ecosistema pequeño, avanza turno a turno viendo quién come a quién, y al turno 3 prueba cambiar el clima a sequía para ver qué les pasa a los conejos. Al final lee el reporte para sacar conclusiones.

### Paso 2 — Crear las pantallas (herramienta Frame, tecla `F`, tamaño Desktop 1280×720)
Hagan **8 frames**, así cubren los estados que pide la profe:

| # | Frame | Estado que cubre |
|---|---|---|
| 1 | Configuración | normal, formulario vacío |
| 2 | Configuración con error | **error**: campos en rojo + mensaje |
| 3 | Simulación sin partida | **sin datos**: tabla vacía + mensaje "No hay una simulación en curso" |
| 4 | Simulación en curso | intervención deshabilitada (en gris) + texto de cuándo es la próxima |
| 5 | Simulación — turno 3 | intervención habilitada |
| 6 | Simulación con confirmación | **confirmación**: aviso verde "Clima cambiado a Sequía" |
| 7 | Diálogo "¿Empezar nueva simulación?" | confirmación de acción destructiva |
| 8 | Reporte final | tabla Dato / Valor, ordenada y legible |

**Diseñen pensando en lo que NetBeans puede hacer:** pestañas, campos de texto, combos, botones, tablas con scroll, listas. Si diseñan algo muy raro, después no lo pueden programar con el diseñador.

Usen la misma distribución en Simulación que en la consigna: **Norte** contadores · **Centro** tabla de entidades · **Este** eventos · **Sur** botones e intervención.

### Paso 3 — Que se vea coherente (lo que más puntúa)
- Definan **estilos**: 3–4 colores (principal, fondo, error rojo, éxito verde) y 3 tamaños de texto (título, subtítulo, cuerpo). Panel derecho → *Styles* (+).
- Hagan el botón **una sola vez**, convertilo en componente (`Ctrl+Alt+K`) y duplicalo: así todos son iguales.

### Paso 4 — Escribir las decisiones (mínimo 3, en un frame aparte "Decisiones")
Formato: **qué decidimos → principio → por qué.** Ejemplos (usen los nombres de principios que hayan visto en clase):
1. **Consistencia:** mismo color de botón principal y misma tipografía en todas las pantallas → la persona aprende una vez cómo se usa.
2. **Feedback:** errores en rojo debajo del formulario y confirmaciones en verde → siempre sabe si lo que hizo funcionó.
3. **Prevención de errores:** los botones de intervención están deshabilitados salvo en los turnos 3, 6, 9... y un texto dice cuándo es la próxima → no puede equivocarse.
4. **Jerarquía / proximidad:** contadores juntos arriba, tabla al centro, y "Avanzar turno" es el botón más grande → lo más importante se ve primero.

### Paso 5 — Entregar
- *Share* → **"Anyone with the link can view"** → copiar link → ponerlo en el `README.md` del repo.
- Seleccionar los frames → *Export* (PNG) → guardar las capturas en la carpeta de documentación.
- **Bonus (prototipo navegable):** pestaña *Prototype* → arrastrar desde el botón al siguiente frame (*Navigate to*) → Configuración → Simulación → Reporte → botón ▶.
- Cuando terminen la app: comparen con el Figma y anoten qué cambió y por qué (se explica en el video).

---

## 5. Cambios respecto a la IE1 (para contar en el video: "conflictos encontrados")

1. **Se sacaron todos los `println`/`Scanner` del modelo.** Ahora cada cosa que pasa se guarda como evento en una lista y el controlador se la pasa a la vista.
2. **Ids únicos** por entidad (antes `"Conejo " + (size+1)` repetía nombres cuando había muertes).
3. **Contadores de nacimientos y muertes**: en la IE1 nunca se sumaban.
4. **Récords del reporte** (más longevo por tipo, lobo con más cacerías, turno de mayor actividad): se registran antes de borrar a los muertos.
5. **Los lobos ya no cazan conejos que murieron ese mismo turno.**
6. **El máximo de 5 lobos cuenta también a los iniciales.**
7. **Rangos aleatorios**: en la IE1 `nextInt(100 - 50 + 1)` daba 0–50 (no 50–100). Ahora suman el mínimo.
8. **Balance del juego:** en la IE1 el ecosistema colapsaba en ~3 turnos casi siempre (cada conejo comía una planta por turno y se reproducía siempre), así que nunca se llegaba a la primera intervención. Ahora el conejo **solo busca plantas si tiene hambre (energía < 60)** y se reproduce con 25 % de probabilidad gastando 20 de energía. Son 4 constantes arriba de `clsConejo`; ahí mismo está escrito cómo volver a la regla exacta de la IE1 si prefieren.
9. **Una sola intervención por ciclo de 3 turnos** (antes: una acción por menú; ahora el modelo lo aplica y la vista solo lo muestra).

## 6. El archivo de guardado
`datos/simulacion.txt` (ruta **relativa** a la carpeta del proyecto; se crea solo). Se guarda **después de cada acción**. Formato: líneas `clave=valor` y una línea por entidad viva (`PLANTA|id|nombre|energia|edad|tamanio`, etc.). Si no existe, la app abre en Configuración; si está roto, avisa con un mensaje y arranca de cero.

## 7. Sugerencia de reparto (para que todos tengan commits propios)
- **A — Modelo:** leer/explicar `modelo`, y agregar la **planta venenosa** (bonus).
- **B — DAO + controlador:** explicar `dao` y `controlador`, probar archivo inexistente/roto.
- **C — Vista:** pestañas Configuración y Simulación en el diseñador.
- **D — Figma + Vista Reporte + README + menú (bonus).**
Cada uno commitea **desde su propia cuenta**, con mensajes descriptivos y seguido (no todo el último día).

## 8. Checklist final antes de entregar
- [ ] Abre y corre en NetBeans sin tocar nada, `principal.Main` como clase principal
- [ ] Sin rutas absolutas
- [ ] Todos los integrantes con commits, desde su cuenta
- [ ] Repo público, o privado **con acceso dado a la docente**
- [ ] Link de Figma dentro del repo
- [ ] Video 10–15 min, todos con cámara, mostrando su código
- [ ] Documentación de IA/páginas consultadas, **individual** y rotulada
- [ ] Figma con ≥3 decisiones justificadas y capturas en documentación
