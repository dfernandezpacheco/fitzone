package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.conversores.PlanMapper;
import es.safareyes.fitzone.dto.GuardarPlanDto;
import es.safareyes.fitzone.dto.PlanResponseDto;
import es.safareyes.fitzone.exception.RecursoNoEncontradoException;
import es.safareyes.fitzone.exception.ReglaNegocioException;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.repositorios.IPlanRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanAdminService {

    private final IPlanRepository planRepository;
    private final PlanMapper planMapper;

    // LISTAR PLANES
    @Transactional(readOnly = true)
    public List<PlanResponseDto> listarPlanes() {

        return planMapper.toDtoList(planRepository.findAll());
    }

    // CREAR PLAN
    @Transactional
    public PlanResponseDto crearPlan(GuardarPlanDto dto) {

        validarDatos(dto);

        String nombre = dto.nombre().trim();

        if (planRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException(
                    "Ya existe un plan con ese nombre"
            );
        }

        Plan plan = new Plan();

        plan.setNombre(nombre);
        plan.setPrecioMesual(dto.precioMensual());
        plan.setTipo(dto.tipo());

        Plan guardado = planRepository.save(plan);

        return planMapper.toDto(guardado);
    }

    // ACTUALIZAR PLAN
    @Transactional
    public PlanResponseDto actualizarPlan(
            Integer id,
            GuardarPlanDto dto
    ) {

        validarDatos(dto);

        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se ha encontrado el plan solicitado"
                        )
                );

        String nombre = dto.nombre().trim();

        if (planRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException(
                    "Ya existe otro plan con ese nombre"
            );
        }

        plan.setNombre(nombre);
        plan.setPrecioMesual(dto.precioMensual());
        plan.setTipo(dto.tipo());

        Plan actualizado = planRepository.save(plan);

        return planMapper.toDto(actualizado);
    }

    // VALIDAR DATOS
    private void validarDatos(GuardarPlanDto dto) {

        if (dto == null) {
            throw new ReglaNegocioException(
                    "Debes proporcionar los datos del plan"
            );
        }

        if (dto.nombre() == null || dto.nombre().isBlank()) {
            throw new ReglaNegocioException(
                    "El nombre del plan es obligatorio"
            );
        }

        if (dto.precioMensual() == null
                || !Double.isFinite(dto.precioMensual())
                || dto.precioMensual() <= 0) {

            throw new ReglaNegocioException(
                    "El precio mensual debe ser mayor que cero"
            );
        }

        if (dto.tipo() == null) {
            throw new ReglaNegocioException(
                    "Debes seleccionar un tipo de plan"
            );
        }
    }
}