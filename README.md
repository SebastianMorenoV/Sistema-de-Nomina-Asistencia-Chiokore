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
2. Crea una base de datos vacía llamada exactamente así (si no existe, el backend también la crea):
   ```sql
   CREATE DATABASE nomina_asistencia;
   ```
3. El usuario y la contraseña de MySQL van en tu `application.properties` (ver la siguiente sección).

> **💡 Nota de Seeders:** La primera vez que el backend se ejecute con éxito de conexión, la Base de Datos se llenará automáticamente con datos de prueba gracias a la clase `DataInitializer.java`. ¡No tienes que insertar nada a mano!

> **Si tu base de datos es de antes de octubre 2026:** Hibernate no agranda columnas existentes, así que corre una vez `ALTER TABLE empleados MODIFY condiciones_medicas VARCHAR(1000);` para que la "Condición" acepte el mismo largo que en candidatos.

### 🔐 Configurar el `application.properties`

1. Copia el template:
   ```bash
   cp backend/src/main/resources/application.properties.example backend/src/main/resources/application.properties
   ```
2. Pon tu contraseña de MySQL en `spring.datasource.password` (y cambia `server.port` si no usas `8080`).
3. Listo. El archivo real está en `.gitignore`, solo se sube el `.example`. Las llaves de los tokens se generan solas la primera vez en `backend/keys/`.

---

## 🏃 Instrucciones de Ejecución

Debes arrancar ambos servidores de forma paralela en dos terminales distintas.

### 1. Levantar el Backend (Motor de Java)
Abre una terminal y navega hacia la carpeta backend:
```bash
cd backend
```
Ejecuta el proyecto mediante Maven Wrapper:
- En Windows: `mvnw.cmd spring-boot:run`
- En Mac/Linux: `./mvnw spring-boot:run`
*El servidor arrancará en `http://localhost:8080`. Asegúrate de que termine de decir "Started AsistenciaNominaApplication".*

### 2. Levantar el Frontend (Interfaz React)
Abre otra terminal y navega hacia la carpeta frontend:
```bash
cd frontend
```
Instala las dependencias la primera vez:
```bash
npm install
```
Inicia el servidor de desarrollo Vite:
```bash
npm run dev
```
*La interfaz arrancará en `http://localhost:5173`. ¡Abre esta liga en tu navegador web!*

---

## 🔐 Acceso al panel y a la API

- El panel `/admin` pide usuario y contraseña: `admin` / `1234` (los de los datos de prueba). La contraseña de un administrador se cambia desde su ficha en el panel.
- Las lecturas que usan el kiosco, el POS y nómina (`GET /api/empleados`, `/api/empleados/pos-status`, `/api/asistencias`) son públicas, igual que el registro de asistencia del kiosco. Crear, editar o borrar empleados, candidatos, horarios y huellas, y consultar candidatos, requiere un token de administrador: se obtiene con `POST /api/auth/login` y se manda como `Authorization: Bearer <token>`.

---

## 🤝 Integración con Módulo de Gastos (Otro Equipo)

Para el equipo encargado de la sección de **Gastos y Ventas**, actualmente la comunicación se está simulando. Cuando deseen integrar la nómina como un gasto en su módulo, se deberá consumir el endpoint de liquidación que expondrá el Backend (`POST /api/nomina/liquidar`) pasándole el ID del empleado y el monto de horas. El botón temporal de "Enviar a Gastos" en el Dashboard de Nómina React sirve como ancla visual para su futuro consumo.
