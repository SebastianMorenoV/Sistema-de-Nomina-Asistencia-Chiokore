-- 1. Insertar roles y tipos de contrato
INSERT IGNORE INTO roles (id, nombre) VALUES (1, 'Trabajador');
INSERT IGNORE INTO roles (id, nombre) VALUES (2, 'Administrador');

INSERT IGNORE INTO tipos_contrato (id, nombre) VALUES (1, 'Pagado');
INSERT IGNORE INTO tipos_contrato (id, nombre) VALUES (2, 'Voluntariado');

-- 2. Insertar Empleados
-- Empleados Pagados
INSERT IGNORE INTO empleados (id, nombre, contrasena, tarifa_hora, requiere_apoyo, activo, rol_id, tipo_contrato_id, tipo_sangre, condiciones_medicas, contacto_emergencia_nombre, url_avatar) 
VALUES (1, 'Sofía Martínez', '1234', 50.00, false, true, 1, 1, 'O+', 'Ninguna', 'Mamá: 555-0192', 'https://ui-avatars.com/api/?name=Sofia+Martinez&background=FDE68A&color=92400E&size=200');

INSERT IGNORE INTO empleados (id, nombre, contrasena, tarifa_hora, requiere_apoyo, activo, rol_id, tipo_contrato_id, tipo_sangre, condiciones_medicas, contacto_emergencia_nombre, url_avatar) 
VALUES (3, 'Valentina Ruiz', '1234', 60.00, false, true, 1, 1, 'B-', 'Asma', 'Papá: 555-1234', 'https://ui-avatars.com/api/?name=Valentina+Ruiz&background=FECACA&color=991B1B&size=200');

INSERT IGNORE INTO empleados (id, nombre, contrasena, tarifa_hora, requiere_apoyo, activo, rol_id, tipo_contrato_id, tipo_sangre, condiciones_medicas, contacto_emergencia_nombre, url_avatar) 
VALUES (4, 'Mario Miranda', '1234', 40.00, false, true, 1, 1, 'A-', 'Ninguna', 'Esposa: 555-1111', 'https://ui-avatars.com/api/?name=Mario+Miranda&background=DBEAFE&color=1E40AF&size=200');

INSERT IGNORE INTO empleados (id, nombre, contrasena, tarifa_hora, requiere_apoyo, activo, rol_id, tipo_contrato_id, tipo_sangre, condiciones_medicas, contacto_emergencia_nombre, url_avatar) 
VALUES (5, 'Renan Cruz', '1234', 45.00, false, true, 1, 1, 'O-', 'Ninguna', 'Hermano: 555-2222', 'https://ui-avatars.com/api/?name=Renan+Cruz&background=D1FAE5&color=065F46&size=200');

-- Empleado Voluntario (Requiere Apoyo)
INSERT IGNORE INTO empleados (id, nombre, contrasena, tarifa_hora, requiere_apoyo, activo, rol_id, tipo_contrato_id, tipo_sangre, condiciones_medicas, contacto_emergencia_nombre, url_avatar) 
VALUES (2, 'Mateo López', '1234', 0.00, true, true, 1, 2, 'A+', 'Autismo, Sensibilidad a ruido', 'Tutor: 555-8821', 'https://ui-avatars.com/api/?name=Mateo+Lopez&background=A7F3D0&color=065F46&size=200');

-- 3. Insertar Horarios
-- Sofía trabaja de Lunes a Viernes de 9:00 a 13:00 (Turno 1)
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (1, 'LUNES', '09:00:00', '13:00:00');
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (1, 'MARTES', '09:00:00', '13:00:00');
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (1, 'MIERCOLES', '09:00:00', '13:00:00');
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (1, 'JUEVES', '09:00:00', '13:00:00');
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (1, 'VIERNES', '09:00:00', '13:00:00');

-- Mario y Renan cubren las tardes de 15:00 a 18:00 (Turno 2)
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (4, 'LUNES', '15:00:00', '18:00:00');
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (5, 'LUNES', '15:00:00', '18:00:00');

-- Mateo (Voluntario) va solo Miércoles y Viernes en la mañana
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (2, 'MIERCOLES', '09:00:00', '13:00:00');
INSERT IGNORE INTO horarios (empleado_id, dia_semana, hora_inicio, hora_fin) VALUES (2, 'VIERNES', '09:00:00', '13:00:00');

-- 4. Insertar Asistencias (Registros históricos para la Nómina)
-- Asumiendo una semana genérica
INSERT IGNORE INTO asistencias (empleado_id, fecha, entrada, salida, horas_calculadas, modificado_manualmente) 
VALUES (1, DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 08:55:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 13:05:00'), 4.16, false);

INSERT IGNORE INTO asistencias (empleado_id, fecha, entrada, salida, horas_calculadas, modificado_manualmente) 
VALUES (1, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 09:02:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 13:00:00'), 3.96, false);

INSERT IGNORE INTO asistencias (empleado_id, fecha, entrada, salida, horas_calculadas, modificado_manualmente) 
VALUES (4, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 14:58:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 18:10:00'), 3.2, false);

-- Registro en proceso (Hoy)
INSERT IGNORE INTO asistencias (empleado_id, fecha, entrada, salida, horas_calculadas, modificado_manualmente) 
VALUES (2, CURRENT_DATE, CONCAT(CURRENT_DATE, ' 09:00:00'), NULL, NULL, false);
