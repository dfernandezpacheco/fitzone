package es.safareyes.fitzone.conversores;

import es.safareyes.fitzone.dto.CrearSocioDto;
import es.safareyes.fitzone.dto.SocioResponseDto;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.modelos.Socio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocioMapper {

    private final PlanMapper planMapper;

    public Socio toEntity(CrearSocioDto dto, Plan plan) {
        if (dto == null) return null;

        Socio socio = new Socio();
        socio.setDni(dto.dni().toUpperCase());
        socio.setNombre(dto.nombre());
        socio.setApellidos(dto.apellidos());
        socio.setEmail(dto.email());
        socio.setTelefono(dto.telefono());
        socio.setMatricula(dto.matricula() != null ? dto.matricula().toUpperCase() : null);
        socio.setActivo(true);
        socio.setFechaAlta(LocalDate.now());
        socio.setPlan(plan);
        return socio;
    }

    public SocioResponseDto toDto(Socio socio) {
        if (socio == null) return null;

        return new SocioResponseDto(
                socio.getId(),
                socio.getDni(),
                socio.getNombre(),
                socio.getApellidos(),
                socio.getEmail(),
                socio.getTelefono(),
                socio.getMatricula(),
                socio.getActivo(),
                socio.getFechaAlta(),
                planMapper.toDto(socio.getPlan())
        );
    }

    public List<SocioResponseDto> toDtoList(List<Socio> socios) {
        return socios.stream().map(this::toDto).toList();
    }
}