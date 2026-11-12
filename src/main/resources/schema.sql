-- Ajustes para una base de datos ya poblada.
-- Sincronizan los contadores SERIAL con el último id real de cada tabla.
-- Así PostgreSQL no intenta reutilizar ids existentes al crear nuevos registros.

ALTER TABLE fitzone.socio
    ALTER COLUMN matricula DROP NOT NULL;

ALTER TABLE fitzone.socio
    ALTER COLUMN id_usuario DROP NOT NULL;

SELECT setval(pg_get_serial_sequence('fitzone.usuario', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.usuario), 0) + 1, false);

SELECT setval(pg_get_serial_sequence('fitzone.socio', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.socio), 0) + 1, false);

SELECT setval(pg_get_serial_sequence('fitzone.plan', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.plan), 0) + 1, false);

SELECT setval(pg_get_serial_sequence('fitzone.actividad', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.actividad), 0) + 1, false);

SELECT setval(pg_get_serial_sequence('fitzone.reserva', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.reserva), 0) + 1, false);

SELECT setval(pg_get_serial_sequence('fitzone.cuota', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.cuota), 0) + 1, false);

SELECT setval(pg_get_serial_sequence('fitzone.acceso', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.acceso), 0) + 1, false);

SELECT setval(pg_get_serial_sequence('fitzone.monitor', 'id'),
              COALESCE((SELECT MAX(id) FROM fitzone.monitor), 0) + 1, false);
