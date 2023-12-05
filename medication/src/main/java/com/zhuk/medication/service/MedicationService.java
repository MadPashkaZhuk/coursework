package com.zhuk.medication.service;

import com.zhuk.medication.dto.MedicationDto;
import com.zhuk.medication.dto.NewMedicationDto;
import com.zhuk.medication.dto.UpdateQuantityDto;
import com.zhuk.medication.entity.MedicationEntity;
import com.zhuk.medication.entity.UserEntity;
import com.zhuk.medication.enums.ApiMessageEnum;
import com.zhuk.medication.enums.ErrorCodeEnum;
import com.zhuk.medication.enums.MedicationTypeEnum;
import com.zhuk.medication.enums.UserRoleEnum;
import com.zhuk.medication.exception.medication.MedicationAlreadyExistsException;
import com.zhuk.medication.exception.medication.MedicationNoRightsException;
import com.zhuk.medication.exception.medication.MedicationNotFoundException;
import com.zhuk.medication.exception.medication.MedicationNotEnoughQuantityException;
import com.zhuk.medication.exception.user.UserUnknownException;
import com.zhuk.medication.mapper.MedicationMapper;
import com.zhuk.medication.repository.MedicationRepository;
import com.zhuk.medication.security.CustomUserDetails;
import com.zhuk.medication.utils.ErrorCodeHelper;
import com.zhuk.medication.utils.MessageSourceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;
    public List<MedicationDto> findAll() {
        List<MedicationEntity> medicationEntities = getMedicationEntityListForCurrentUser();
        return medicationEntities.stream()
                .map(medicationMapper::map)
                .toList();
    }

    public MedicationDto findById(Long id) {
        validateMedicationForCurrentUser(id);
        return medicationMapper.map(getEntityByIdOrThrowException(id));
    }

    public MedicationDto saveMedication(NewMedicationDto dto) {
        if(getOptionalEntityByNameAndWeight(dto.getName(), dto.getWeight()).isPresent()) {
            throw new MedicationAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_ALREADY_EXISTS),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_ALREADY_EXISTS_CODE));
        }
        MedicationEntity entity = MedicationEntity.builder()
                .name(dto.getName())
                .type(MedicationTypeEnum.valueOf(dto.getType()))
                .additionalInfo(dto.getAdditionalInfo())
                .manufacturer(dto.getManufacturer())
                .quantity(dto.getQuantity())
                .weight(dto.getWeight())
                .user(userService.getUserEntityByUsernameOrThrowException(getCurrentUser().getUsername()))
                .build();
        return medicationMapper.map(medicationRepository.save(entity));
    }

    @Transactional
    public void deleteMedication(Long id) {
        Optional<MedicationEntity> entityOptional = getOptionalEntityById(id);
        if(entityOptional.isEmpty()) {
            return;
        }
        validateMedicationForCurrentUser(id);
        medicationRepository.deleteById(id);
    }

    @Transactional
    public MedicationDto updateMedication(NewMedicationDto dto) {
        Optional<MedicationEntity> entityOptional = getOptionalEntityByNameAndWeight(dto.getName(), dto.getWeight());
        if(entityOptional.isEmpty()) {
            return saveMedication(dto);
        }
        Long id = entityOptional.get().getId();
        validateMedicationForCurrentUser(id);
        updateByNameAndWeight(dto);
        MedicationEntity entity = MedicationEntity.builder()
                .id(id)
                .type(MedicationTypeEnum.valueOf(dto.getType()))
                .additionalInfo(dto.getAdditionalInfo())
                .manufacturer(dto.getManufacturer())
                .quantity(dto.getQuantity())
                .weight(dto.getWeight())
                .user(userService.getUserEntityByUsernameOrThrowException(getCurrentUser().getUsername()))
                .build();
        return medicationMapper.map(entity);
    }

    @Transactional
    public void updateQuantity(Long id, UpdateQuantityDto dto) {
        MedicationEntity entity = getEntityByIdOrThrowException(id);
        validateMedicationForCurrentUser(id);
        if(dto.getQuantity() < 0 && entity.getQuantity() < Math.abs(dto.getQuantity())) {
            throw new MedicationNotEnoughQuantityException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_NOT_ENOUGH_QUANTITY),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_NOT_ENOUGH_QUANTITY_CODE));
        }
        medicationRepository.updateQuantityById(id, entity.getQuantity() + dto.getQuantity());
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
                        errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_NOT_FOUND_CODE))
        );
    }

    private Optional<MedicationEntity> getOptionalEntityById(Long id) {
        return medicationRepository.findById(id);
    }

    private Optional<MedicationEntity> getOptionalEntityByNameAndWeight(String name, int weight) {
        return medicationRepository.getMedicationEntityByNameAndWeight(name, weight);
    }

    private List<MedicationEntity> getMedicationEntityListForCurrentUser() {
        UserEntity user = userService.getUserEntityByUsernameOrThrowException(getCurrentUser().getUsername());
        if(user.getRole() == UserRoleEnum.ROLE_ADMIN) {
            return medicationRepository.findAll();
        }
        return user.getMedications();
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UserUnknownException(HttpStatus.FORBIDDEN,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.USER_UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.USER_UNKNOWN_EXCEPTION_CODE));
        }
        return ((CustomUserDetails) authentication.getPrincipal());
    }

    private void validateMedicationForCurrentUser(Long medicationId) {
        MedicationEntity medicationEntity = getEntityByIdOrThrowException(medicationId);
        UserEntity user = userService.getUserEntityByUsernameOrThrowException(getCurrentUser().getUsername());
        if(!medicationEntity.getUser().equals(user)) {
            throw new MedicationNoRightsException(HttpStatus.FORBIDDEN,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_NO_RIGHTS),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_NO_RIGHTS_CODE));
        }
    }
}
