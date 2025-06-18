# Procesador de Lenguajes JS-- - Grupo 80

María Ruiz Castro (220269)
Gabriel Peña Sánchez (220267)

## Estructura del Proyecto

- **`src/`**  
  Contiene el código fuente del proyecto en Java.

- **`examples/`**  
  Carpeta con los 10 casos de prueba requeridos. Cada archivo contiene un ejemplo de entrada que puede ser procesado por el ejecutable.

- **`data/`**  
  Contiene los resultados generados durante la ejecución del programa. Aquí se almacenan archivos de salida o errores.

- **`Procesador.jar`**  
  Ejecutable Java del proyecto. Puede ser ejecutado directamente desde la línea de comandos.

---

## Instrucciones de Ejecución

Para ejecutar el programa, asegúrese de tener Java instalado. Luego abra una terminal (PowerShell, CMD o Terminal) y ejecuta el siguiente comando desde el directorio donde está el archivo `Procesador.jar`:

```bash
 java -jar ./Procesador.jar ./examples/Caso7.txt

```
En caso de error en la ejecución debido al empleo de una versión de Java diferente, se puede generar el ejecutable con el siguiente comando

```bash
 jar cfe Procesador.jar procesador.Procesador -C bin .

