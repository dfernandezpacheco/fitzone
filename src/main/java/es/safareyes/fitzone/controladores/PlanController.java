package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.conversores.PlanMapper;
import es.safareyes.fitzone.dto.PlanResponseDto;
import es.safareyes.fitzone.repositorios.IPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping({"/planes", "/api/v1/planes"})
@RequiredArgsConstructor
public class PlanController {
    private final IPlanRepository planRepository;
    private final PlanMapper planMapper;

    @GetMapping
    public List<PlanResponseDto> listar() {
        return planMapper.toDtoList(planRepository.findAll());
    }
}
