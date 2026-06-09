# Sistema de Nómina y Asistencia - Chiokore Bazar con Causa

Bienvenido a la Prueba de Concepto (PoC) del módulo de **Nómina y Asistencia** para Chiokore Bazar. Este proyecto forma parte del nuevo Punto de Venta integral y está diseñado para funcionar en pantallas táctiles con mecanismos de seguridad, cálculos automatizados y generación de tickets.

---

## 🏗 Arquitectura del Sistema

El proyecto está dividido en dos partes principales y funciona como una arquitectura Cliente-Servidor separada:

1. **Frontend (React + Vite + TailwindCSS):** 
   - Ubicado en la carpeta `/frontend`.
   - Interfaz gráfica moderna, "Modo Acompañante" y Panel Administrativo.
   - Generación de tickets de 80mm e inactividad por Screensaver.
   - Exportación de Excel en el navegador (`xlsx`).

2. **Backend (Java 17 + Spring Boot + JPA):**
   - Ubicado en la carpeta `/backend`.
   - Motor Matemático: Calcula la diferencia exacta entre las horas de `ENTRADA` y `SALIDA`, generando la sábana de nómina dinámica multiplicada por la tarifa del empleado.
   - Conexión a Base de Datos Relacional mediante ORM (Hibernate).

3. **Base de Datos (MySQL):**
   - Almacenamiento persistente de Catálogos (Empleados, Roles, Tipos de Contrato) y Transacciones (Asistencias).

---

## 🚀 Prerrequisitos

Para que cualquier desarrollador pueda levantar el sistema en su máquina local, es necesario tener instalados los siguientes programas:

- **Java Development Kit (JDK) 17** o superior.
- **Node.js** (versión 18 o superior).
- **MySQL Server** (versión 8.0 o MariaDB equivalente).

---

## ⚙️ Configuración de la Base de Datos (Muy Importante)

Para evitar problemas de conexión al momento de arrancar el backend, tu servidor local de MySQL **debe estar configurado** de la siguiente manera:

1. Abre tu cliente de MySQL (Workbench, DBeaver, o consola).
2. Crea una base de datos vacía llamada exactamente así:
   \`\`\`sql
   CREATE DATABASE nomina_asistencia;
   \`\`\`
3. Las credenciales de acceso están configuradas (en `backend/src/main/resources/application.properties`):
   - **Usuario:** `root`
   - **Contraseña:** `itson` *(Esta es la contraseña configurada actualmente en el proyecto. Si tu MySQL local tiene otra, cámbiala en el archivo)*.

> **💡 Nota de Seeders:** La primera vez que el backend se ejecute con éxito de conexión, la Base de Datos se llenará automáticamente con datos de prueba gracias a la clase `DataInitializer.java`. ¡No tienes que insertar nada a mano!

### 🔐 Configurar el `application.properties`

1. Copiá el template:
   ```bash
   cp backend/src/main/resources/application.properties.example backend/src/main/resources/application.properties
   ```
2. Llená estos 3 campos:
   - `spring.datasource.password` → tu contraseña de MySQL
   - `jwt.secret` → generá uno con `openssl rand -base64 32`
   - `server.port` → ajustá el puerto si no usás `8080`
3. Listo. El archivo real está en `.gitignore`, solo se sube el `.example`.

---

## 🏃 Instrucciones de Ejecución

Debes arrancar ambos servidores de forma paralela en dos terminales distintas.

### 1. Levantar el Backend (Motor de Java)
Abre una terminal y navega hacia la carpeta backend:
\`\`\`bash
cd Software/backend
\`\`\`
Ejecuta el proyecto mediante Maven Wrapper:
- En Windows: `mvnw.cmd spring-boot:run`
- En Mac/Linux: `./mvnw spring-boot:run`
*El servidor arrancará en `http://localhost:8080`. Asegúrate de que termine de decir "Started AsistenciaNominaApplication".*

### 2. Levantar el Frontend (Interfaz React)
Abre otra terminal y navega hacia la carpeta frontend:
\`\`\`bash
cd Software/frontend
\`\`\`
Instala las dependencias la primera vez:
\`\`\`bash
npm install
\`\`\`
Inicia el servidor de desarrollo Vite:
\`\`\`bash
npm run dev
\`\`\`
*La interfaz arrancará en `http://localhost:5173`. ¡Abre esta liga en tu navegador web!*

---

## 🤝 Integración con Módulo de Gastos (Otro Equipo)

Para el equipo encargado de la sección de **Gastos y Ventas**, actualmente la comunicación se está simulando. Cuando deseen integrar la nómina como un gasto en su módulo, se deberá consumir el endpoint de liquidación que expondrá el Backend (`POST /api/nomina/liquidar`) pasándole el ID del empleado y el monto de horas. El botón temporal de "Enviar a Gastos" en el Dashboard de Nómina React sirve como ancla visual para su futuro consumo.
