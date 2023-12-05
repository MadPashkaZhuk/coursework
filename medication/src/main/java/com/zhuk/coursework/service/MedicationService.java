package com.zhuk.coursework.service;

import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.dto.NewMedicationDto;
import com.zhuk.coursework.dto.UpdateQuantityDto;
import com.zhuk.coursework.entity.MedicationEntity;
import com.zhuk.coursework.enums.ApiMessageEnum;
import com.zhuk.coursework.enums.ErrorCodeEnum;
import com.zhuk.coursework.enums.MedicationTypeEnum;
import com.zhuk.coursework.exception.medication.MedicationAlreadyExistsException;
import com.zhuk.coursework.exception.medication.MedicationNotFoundException;
import com.zhuk.coursework.exception.medication.NotEnoughQuantityException;
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

    public MedicationDto saveMedication(NewMedicationDto dto) {
        if(getOptionalEntityByNameAndWeight(dto.getName(), dto.getWeight()).isPresent()) {
            throw new MedicationAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_ALREADY_EXISTS),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_ALREADY_EXISTS));
        }
        MedicationEntity entity = medicationMapper.mapFromNewDto(dto);
        return medicationMapper.map(medicationRepository.save(entity));
    }

    @Transactional
    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }

    @Transactional
    public MedicationDto updateMedication(NewMedicationDto dto) {
        Optional<MedicationEntity> entityOptional = getOptionalEntityByNameAndWeight(dto.getName(), dto.getWeight());
        if(entityOptional.isEmpty()) {
            return saveMedication(dto);
        }
        updateByNameAndWeight(dto);
        Long id = entityOptional.get().getId();
        MedicationEntity entity = medicationMapper.mapFromNewDto(dto).toBuilder().id(id).build();
        return medicationMapper.map(entity);

    }

    private void updateByNameAndWeight(NewMedicationDto dto) {
        medicationRepository.updateByNameAndWeight(dto.getName(), dto.getManufacturer(),
                dto.getWeight(), dto.getQuantity(),
                dto.getAdditionalInfo(), MedicationTypeEnum.valueOf(dto.getType()));
    }

    private MedicationEntity getEntityByIdOrThrowException(Long id) {
        Optional<MedicationEntity> medication = getOptionalEntityById(id);
        return medication.orElseThrow(
                () -> new MedicationNotFoundException(HttpStatus.NOT_FOUND,
                        messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_NOT_FOUND),
                        errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_NOT_FOUND))
        );
    }

    private Optional<MedicationEntity> getOptionalEntityById(Long id) {
        return medicationRepository.findById(id);
    }

    private Optional<MedicationEntity> getOptionalEntityByNameAndWeight(String name, int weight) {
        return medicationRepository.getMedicationEntityByNameAndWeight(name, weight);
    }

    @Transactional
    public void updateQuantity(Long id, UpdateQuantityDto dto) {
        MedicationEntity entity = getEntityByIdOrThrowException(id);
        if(dto.getQuantity() < 0 && entity.getQuantity() < Math.abs(dto.getQuantity())) {
            throw new NotEnoughQuantityException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.NOT_ENOUGH_QUANTITY),
                    errorCodeHelper.getCode(ErrorCodeEnum.NOT_ENOUGH_QUANTITY));
        }
        medicationRepository.updateQuantityById(id, entity.getQuantity() + dto.getQuantity());
    }
}
