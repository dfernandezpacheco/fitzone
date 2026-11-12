-- Ajustes necesarios para cumplir el modelo del proyecto FitZone.
-- Ejecutar una vez sobre la base de datos fitzone antes de probar el registro.

-- La matrícula es opcional según las reglas del proyecto.
ALTER TABLE fitzone.socio
    ALTER COLUMN matricula DROP NOT NULL;

-- La relación socio-usuario también es opcional según el modelo.
ALTER TABLE fitzone.socio
    ALTER COLUMN id_usuario DROP NOT NULL;
