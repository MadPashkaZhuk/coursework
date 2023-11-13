package com.zhuk.coursework.service;

import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.dto.NewMedicationDto;
import com.zhuk.coursework.entity.MedicationEntity;
import com.zhuk.coursework.enums.ApiMessageEnum;
import com.zhuk.coursework.enums.ErrorCodeEnum;
import com.zhuk.coursework.enums.MedicationTypeEnum;
import com.zhuk.coursework.exception.medication.MedicationAlreadyExistsException;
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

    public MedicationDto saveMedication(NewMedicationDto newMedicationDto) {
        if(isMedicationExists(newMedicationDto.getName(), newMedicationDto.getWeight())) {
            throw new MedicationAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_ALREADY_EXISTS),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_ALREADY_EXISTS));
        }
        MedicationEntity entity = medicationMapper.mapFromNewDto(newMedicationDto);
        return medicationMapper.map(medicationRepository.save(entity));
    }

    @Transactional
    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }

    @Transactional
    public MedicationDto updateMedication(NewMedicationDto newMedicationDto, Long id) {
        getEntityByIdOrThrowException(id);
        updateByIdFromNewMedicationDto(newMedicationDto, id);
        MedicationEntity medication = medicationMapper.mapFromNewDto(newMedicationDto);
        return medicationMapper.map(medication
                .toBuilder()
                .id(id)
                .build());
    }

    private void updateByIdFromNewMedicationDto(NewMedicationDto newMedicationDto, Long id) {
        medicationRepository.updateById(newMedicationDto.getName(), newMedicationDto.getManufacturer(),
                newMedicationDto.getWeight(), newMedicationDto.isRequirePrescription(),
                newMedicationDto.getAdditionalInfo(), MedicationTypeEnum.valueOf(newMedicationDto.getType()), id);
    }

    private MedicationEntity getEntityByIdOrThrowException(Long id) {
        Optional<MedicationEntity> medication = medicationRepository.findById(id);
        return medication.orElseThrow(
                () -> new MedicationNotFoundException(HttpStatus.NOT_FOUND,
                        messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_NOT_FOUND),
                        errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_NOT_FOUND))
        );
    }

    private boolean isMedicationExists(String name, int weight) {
        return medicationRepository.existsByNameAndWeight(name, weight);
    }
}
