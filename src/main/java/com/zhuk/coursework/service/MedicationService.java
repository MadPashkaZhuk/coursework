package com.zhuk.coursework.service;

import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.dto.NewMedicationDto;
import com.zhuk.coursework.entity.MedicationEntity;
import com.zhuk.coursework.enums.ApiMessageEnum;
import com.zhuk.coursework.enums.ErrorCodeEnum;
import com.zhuk.coursework.enums.MedicationTypeEnum;
import com.zhuk.coursework.exception.medication.MedicationNotFoundException;
import com.zhuk.coursework.mapper.MedicationMapper;
import com.zhuk.coursework.repository.MedicationRepository;
import com.zhuk.coursework.utils.ErrorCodeHelper;
import com.zhuk.coursework.utils.MessageSourceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationMapper medicationMapper;
    private final MedicationRepository medicationRepository;
    private final MessageSourceWrapper messageSourceWrapper;
    private final ErrorCodeHelper errorCodeHelper;

    public List<MedicationDto> findAll() {
        return medicationRepository.findAll().stream()
                .map(medicationMapper::map)
                .toList();
    }

    public MedicationDto findById(Long id) {
        return medicationMapper.map(getEntityByIdOrThrowException(id));
    }

    private MedicationEntity getEntityByIdOrThrowException(Long id) {
        Optional<MedicationEntity> medication = medicationRepository.findById(id);
        return medication.orElseThrow(
                () -> new MedicationNotFoundException(HttpStatus.NOT_FOUND,
                        messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_NOT_FOUND),
                        errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_NOT_FOUND))
        );
    }

    public MedicationDto saveMedication(NewMedicationDto newMedicationDto) {
        MedicationEntity entity = createEntityFromDto(newMedicationDto);
        return medicationMapper.map(medicationRepository.save(entity));
    }

    private MedicationEntity createEntityFromDto(NewMedicationDto newMedicationDto) {
        return MedicationEntity.builder()
                .name(newMedicationDto.getName())
                .manufacturer(newMedicationDto.getManufacturer())
                .type(MedicationTypeEnum.valueOf(newMedicationDto.getType()))
                .weight(newMedicationDto.getWeight())
                .requirePrescription(newMedicationDto.isRequirePrescription())
                .additionalInfo(newMedicationDto.getAdditionalInfo())
                .build();
    }

    @Transactional
    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }

    public MedicationDto updateMedication(NewMedicationDto newMedicationDto, Long id) {
        medicationRepository.updateById(newMedicationDto.getName(), newMedicationDto.getManufacturer(),
                newMedicationDto.getWeight(), newMedicationDto.isRequirePrescription(),
                newMedicationDto.getAdditionalInfo(), newMedicationDto.getType(), id);
        return medicationMapper.map(getEntityByIdOrThrowException(id)
                .toBuilder()
                .name(newMedicationDto.getName())
                .manufacturer(newMedicationDto.getManufacturer())
                .weight(newMedicationDto.getWeight())
                .requirePrescription(newMedicationDto.isRequirePrescription())
                .additionalInfo(newMedicationDto.getAdditionalInfo())
                .type(MedicationTypeEnum.valueOf(newMedicationDto.getType()))
                .build());
    }
}
