package es.safareyes.fitzone.conversores;

import es.safareyes.fitzone.dto.PlanResponseDto;
import es.safareyes.fitzone.modelos.Plan;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanMapper {

    public PlanResponseDto toDto(Plan plan) {
        if (plan == null) return null;
        return new PlanResponseDto(
                plan.getId(),
                plan.getNombre(),
                plan.getPrecioMesual()
        );
    }

    public List<PlanResponseDto> toDtoList(List<Plan> planes) {
        return planes.stream().map(this::toDto).toList();
    }
}