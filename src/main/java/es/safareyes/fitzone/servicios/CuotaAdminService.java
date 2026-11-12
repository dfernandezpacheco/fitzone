package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.CuotaAdminDto;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuotaAdminService {

    private final ICuotaRepository cuotaRepository;

    @Transactional(readOnly = true)
    public Page<CuotaAdminDto> listarCuotas(Pageable pageable) {
        return cuotaRepository.listarCuotasAdmin(pageable);
    }
}