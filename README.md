# Simulador de Ecosistema — versión Swing (IE2)

## Ejecución
- NetBeans: 31 · JDK: 26 · Instalar la librería "Absolute Layout".
- Abrir el proyecto en NetBeans.
- El archivo de guardado se crea solo en `datos/simulacion.txt`.

## Estructura
- `modelo/`: entidades (herencia, clase abstracta, interfaces), clima y ecosistema. Sin Swing ni consola.
- `dao/`: `SimulacionDAO`, guarda y lee el `.txt`.
- `controlador/`: `ControladorSimulacion` (valida y coordina) e `IVista`.
- `vista/`: `VentanaPrincipal`, hecha con el diseñador de NetBeans.
- `principal/`: `Main`.

## Integrantes
- Barbero, Gianella
- Lujan, Federico
- Paz, Gastón

## Figma
https://www.figma.com/design/JgUDDxgXwTe9f9Cvv7IDPK/Sin-t%C3%ADtulo?node-id=0-1&t=HqYlXzoWWIdd8qeb-1

## Video explicativo
https://drive.google.com/file/d/1O18xuHo36G9cim7fbXqnSX0wc6FkwH3j/view?usp=sharing

## Desafíos encontrados
- Reestructurar el proyecto con lo visto en clases. Teníamos clases disperas, con enums en el mismo estado, y darle a todo un orden fue, en principio, complicado.
