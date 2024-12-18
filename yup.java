package com.cacib.loanscape.ldt.features.sync.mapper;

import com.cacib.loanscape.gateway.types.*;
import com.cacib.loanscape.ldt.features.sync.model.EStepCreditCommittee;
import com.cacib.loanscape.ldt.features.sync.model.EDeal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DealMapperImplTest {

    private final DealMapperImpl dealMapper = new DealMapperImpl();

    @Test
    void toCSDDealDto_ShouldMapAllFieldsCorrectly() {
        // Arrange
        EDeal eDeal = new EDeal();
        eDeal.setSti3("123");
        eDeal.setStiName("Test Deal");
        eDeal.setComment("Short description");
        eDeal.setCurrency("USD");
        eDeal.setMaxTenor(12);
        eDeal.setSyndicationType("SYN1");
        eDeal.setGlobalAmountAtStructuration(1000000.0);
        eDeal.setProbabilityOfSuccess(0.8);

        // Act
        DealDto result = dealMapper.toCSDDealDto(eDeal);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getSti());
        assertEquals("Test Deal", result.getStiName());
        assertEquals("Short description", result.getShortDealDescription());
        assertEquals("USD", result.getMainCurrency());
        assertEquals(12, result.getMaxTenor());
        assertEquals(1000000.0, result.getGlobalAmountAtStructuration());
        assertEquals(0.8, result.getOriginatorGlossary().getProbabilityOfSuccess());
    }

    @Test
    void toCSDDealDto_ShouldReturnNullWhenInputIsNull() {
        // Act
        DealDto result = dealMapper.toCSDDealDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDealLenderDto_ShouldMapFieldsCorrectly() {
        // Arrange
        EDeal eDeal = new EDeal();
        eDeal.setAnticipatedBookingDate("2024-12-31");

        // Act
        DealLenderDto result = dealMapper.toDealLenderDto(eDeal);

        // Assert
        assertNotNull(result);
        assertEquals("2024-12-31", result.getAnticipatedClosedDate());
    }

    @Test
    void toDealLenderDto_ShouldHandleNullInput() {
        // Act
        DealLenderDto result = dealMapper.toDealLenderDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toCreditLimitDto_ShouldMapFieldsProperly() {
        // Arrange
        EStepCreditCommittee creditCommittee = new EStepCreditCommittee();
        creditCommittee.setUnderwritingApproved(true);
        creditCommittee.setFinalHoldMeter(200000.0);
        creditCommittee.setBestEffort(150000.0);

        // Act
        CreditLimitDto result = dealMapper.toCreditLimitDto(creditCommittee);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUnderwritingApproved());
        assertEquals(200000.0, result.getFinalHoldMeter());
        assertEquals(150000.0, result.getBestEffortAmount());
    }

    @Test
    void toCreditLimitDto_ShouldHandleNullInput() {
        // Act
        CreditLimitDto result = dealMapper.toCreditLimitDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toFinalTakePeriodDto_ShouldMapFieldsCorrectly() {
        // Arrange
        EStepCreditCommittee creditCommittee = new EStepCreditCommittee();
        creditCommittee.setSellDownPeriodUnitNumber(10);
        creditCommittee.setSellDownPeriodUnitType("MONTHS");
        creditCommittee.setFinalTake(500000.0);

        // Act
        FinalTakePeriodDto result = dealMapper.toFinalTakePeriodDto(creditCommittee);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getSellDownPeriod());
        assertEquals("MONTHS", result.getSellDownPeriodUnit());
        assertEquals(500000.0, result.getApprovedFinalTake());
    }

    @Test
    void toFinalTakePeriodDto_ShouldHandleNullInput() {
        // Act
        FinalTakePeriodDto result = dealMapper.toFinalTakePeriodDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void eDealToOriginatorGlossaryDto_ShouldMapFieldsCorrectly() {
        // Arrange
        EDeal eDeal = new EDeal();
        eDeal.setTransactionPI(3.14);
        eDeal.setState("CA");
        eDeal.setProbabilityOfSuccess(0.85);

        // Act
        OriginatorGlossaryDto result = dealMapper.eDealToOriginatorGlossaryDto(eDeal);

        // Assert
        assertNotNull(result);
        assertEquals(3.14, result.getTransactionPI());
        assertEquals("CA", result.getOriginationState());
        assertEquals(0.85, result.getProbabilityOfSuccess());
    }

    @Test
    void eDealToOriginatorGlossaryDto_ShouldHandleNullInput() {
        // Act
        OriginatorGlossaryDto result = dealMapper.eDealToOriginatorGlossaryDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toCSDDealDto_ShouldMapListFieldsCorrectly() {
        // Arrange
        EDeal eDeal = new EDeal();
        eDeal.setExternalReferences(List.of("Ref1", "Ref2"));

        // Act
        DealDto result = dealMapper.toCSDDealDto(eDeal);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getExternalReferences().size());
        assertEquals("Ref1", result.getExternalReferences().get(0));
    }
}


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cacib.loanscape.ldt.features.sync.mapper.DealLenderMapper;
import com.cacib.loanscape.ldt.features.sync.models.CreditCommitteeDto;
import com.cacib.loanscape.ldt.features.sync.models.DealLenderDto;
import com.cacib.loanscape.ldt.features.sync.models.EDeal;
import com.cacib.loanscape.ldt.features.sync.models.EStepCreditCommittee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mapstruct.factory.Mappers;

import java.util.List;

class DealLenderMapperTest {

    private DealLenderMapper dealLenderMapper;

    @BeforeEach
    void setUp() {
        dealLenderMapper = Mappers.getMapper(DealLenderMapper.class);
    }

    @Test
    void toCreditCommitteeByLenderDto_ShouldMapFieldsCorrectly() {
        // Arrange
        EStepCreditCommittee committee = new EStepCreditCommittee();
        committee.setCreditCommitteeType("COMMITTEE_TYPE");
        committee.setCommitteeDate("2024-05-01");
        committee.setUnderwritingApproved("APPROVED");
        committee.setFinalHoldLetter("HOLD_LETTER");

        // Act
        CreditCommitteeDto result = dealLenderMapper.toCreditCommitteeByLenderDto(committee);

        // Assert
        assertNotNull(result);
        assertEquals("COMMITTEE_TYPE", result.getCreditCommitteeCode());
        assertEquals("2024-05-01", result.getCreditCommitteeApprovalDate());
        assertEquals("APPROVED", result.getUnderwritingApproved());
        assertEquals("HOLD_LETTER", result.getFinalHoldLetter());
    }

    @Test
    void toDealLenderDto_ShouldMapFieldsCorrectly() {
        // Arrange
        EDeal eDeal = new EDeal();
        eDeal.setAnticipatedBookingDate("2024-05-15");

        // Act
        DealLenderDto result = dealLenderMapper.toDealLenderDto(eDeal);

        // Assert
        assertNotNull(result);
        assertEquals("2024-05-15", result.getAnticipatedBookingDate());
    }

    @Test
    void toCSDDealLenders_WithNullInput_ShouldReturnEmptyList() {
        // Arrange
        EDeal eDeal = new EDeal();
        eDeal.setCreditCommitteeStep(null);

        // Act
        List<CreditCommitteeDto> result = dealLenderMapper.toCSDDealLenders(eDeal);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toCSDDealLenders_WithValidInput_ShouldMapCorrectly() {
        // Arrange
        EStepCreditCommittee committee1 = new EStepCreditCommittee();
        committee1.setCreditCommitteeType("TYPE_1");

        EStepCreditCommittee committee2 = new EStepCreditCommittee();
        committee2.setCreditCommitteeType("TYPE_2");

        EDeal eDeal = new EDeal();
        eDeal.setCreditCommitteeStep(List.of(committee1, committee2));

        // Act
        List<CreditCommitteeDto> result = dealLenderMapper.toCSDDealLenders(eDeal);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TYPE_1", result.get(0).getCreditCommitteeCode());
        assertEquals("TYPE_2", result.get(1).getCreditCommitteeCode());
    }
}



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cacib.loanscape.ldt.features.sync.mapper.TrancheMapper;
import com.cacib.loanscape.ldt.features.sync.models.TrancheDto;
import com.cacib.loanscape.ldt.features.sync.models.ETranche;
import com.cacib.loanscape.ldt.features.sync.models.EDealHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

class TrancheMapperTest {

    private TrancheMapper trancheMapper;

    @BeforeEach
    void setUp() {
        trancheMapper = Mappers.getMapper(TrancheMapper.class);
    }

    @Test
    void toTrancheDto_ShouldMapFieldsCorrectly() {
        // Arrange
        ETranche eTranche = new ETranche();
        eTranche.setSti3("STI3_VALUE");
        eTranche.setComment("Description_Value");
        eTranche.setExternalProductCode("PRODUCT_CODE");
        eTranche.setAmount(1000.55);
        eTranche.setCurrency("EUR");
        eTranche.setTerm("12");
        eTranche.setTermUnitType("MONTHS");
        eTranche.setAverageLife(24.75);

        // Act
        TrancheDto trancheDto = trancheMapper.toTrancheDto(eTranche);

        // Assert
        assertNotNull(trancheDto);
        assertEquals("STI3_VALUE", trancheDto.getSti());
        assertEquals("Description_Value", trancheDto.getDescription());
        assertEquals("PRODUCT_CODE", trancheDto.getCommercialProductCode());
        assertEquals(1000.55, trancheDto.getAmountAtSignOff());
        assertEquals("EUR", trancheDto.getMainCurrency());
        assertEquals("12", trancheDto.getDuration());
        assertEquals("MONTHS", trancheDto.getDurationUnit());
        assertEquals(24.75, trancheDto.getCurrentWAL());
    }

    @Test
    void setFuncIdAndVersionId_ShouldSetFieldsCorrectly() {
        // Arrange
        TrancheDto trancheDto = new TrancheDto();
        EDealHandler eDealHandler = mock(EDealHandler.class);
        when(eDealHandler.getFunctionalId()).thenReturn("FUNC_ID");
        when(eDealHandler.getVersionId()).thenReturn(10);

        Optional<EDealHandler> dealHandler = Optional.of(eDealHandler);

        // Act
        trancheMapper.setFuncIdAndVersionId(trancheDto, dealHandler);

        // Assert
        assertEquals("FUNC_ID", trancheDto.getFunctionalId());
        assertEquals(10, trancheDto.getFunctionalVersion());
    }

    @Test
    void setFuncIdAndVersionId_WithEmptyOptional_ShouldNotSetFields() {
        // Arrange
        TrancheDto trancheDto = new TrancheDto();
        Optional<EDealHandler> dealHandler = Optional.empty();

        // Act
        trancheMapper.setFuncIdAndVersionId(trancheDto, dealHandler);

        // Assert
        assertNull(trancheDto.getFunctionalId());
        assertNull(trancheDto.getFunctionalVersion());
    }
}


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cacib.loanscape.ldt.features.sync.DealSyncValidator;
import com.cacib.loanscape.ldt.features.sync.services.*;
import com.cacib.loanscape.ldt.features.sync.models.*;
import com.cacib.loanscape.ldt.features.sync.report.SyncReport;
import com.cacib.loanscape.ldt.features.sync.comparator.ComparisonData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class DealSyncValidatorTest {

    @Mock
    private WaitingEventTransactionService waitingEventTransactionService;

    @Mock
    private DealSyncService dealSyncService;

    @Mock
    private DealPositionSyncService dealPositionSyncService;

    @Mock
    private IDealRepo dealRepo;

    @Mock
    private IDealHandlerRepo dealHandlerRepo;

    @InjectMocks
    private DealSyncValidator dealSyncValidator;

    private final Long dealId = 12345L;
    private final EDeal eDeal = new EDeal();
    private final EDealHandler eDealHandler = new EDealHandler();

    private final ComparisonData<DealDto> dealComparisonData =
        new ComparisonData<>(new DealDto(), new DealDto(), "LDT", "CS", "Deal", Set.of());
    private final ComparisonData<DealPositionDto> dealPositionComparisonData =
        new ComparisonData<>(new DealPositionDto(), new DealPositionDto(), "LDT", "CS", "DealPosition", Set.of());

    @BeforeEach
    void setUp() {
        eDeal.setId(dealId);
    }

    @Test
    void checkSync_WhenDealExists_ShouldReturnSyncReport() {
        // Arrange
        when(dealRepo.findById(dealId)).thenReturn(Optional.of(eDeal));
        when(dealHandlerRepo.findByObjectIdAndObjectType(dealId, ObjectType.DEAL)).thenReturn(Optional.of(eDealHandler));

        when(dealSyncService.prepareComparisonData(eDeal, eDealHandler)).thenReturn(dealComparisonData);
        when(dealPositionSyncService.prepareComparisonData(eDeal, eDealHandler)).thenReturn(dealPositionComparisonData);

        // Act
        SyncReport report = dealSyncValidator.checkSync(dealId);

        // Assert
        assertNotNull(report);
        verify(dealSyncService).prepareComparisonData(eDeal, eDealHandler);
        verify(dealPositionSyncService).prepareComparisonData(eDeal, eDealHandler);

        assertEquals(0, report.getMismatches().size(), "No mismatches expected");
    }

    @Test
    void checkSync_WhenDealDoesNotExist_ShouldThrowException() {
        // Arrange
        when(dealRepo.findById(dealId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            dealSyncValidator.checkSync(dealId);
        });

        assertEquals("Deal: " + dealId + " does not exist in LDT", exception.getMessage());
        verify(dealRepo).findById(dealId);
        verifyNoInteractions(dealSyncService, dealPositionSyncService);
    }

    @Test
    void checkSync_WhenMismatchesExist_ShouldAddToReport() {
        // Arrange
        var mismatches = Set.of("Field 'positionType' mismatch - LDT: 'A', CS: 'B'");
        when(dealRepo.findById(dealId)).thenReturn(Optional.of(eDeal));
        when(dealHandlerRepo.findByObjectIdAndObjectType(dealId, ObjectType.DEAL)).thenReturn(Optional.of(eDealHandler));

        when(dealSyncService.prepareComparisonData(eDeal, eDealHandler)).thenReturn(dealComparisonData);
        when(dealPositionSyncService.prepareComparisonData(eDeal, eDealHandler)).thenReturn(dealPositionComparisonData);

        var compareToolMock = mock(CompareTool.class);
        when(compareToolMock.compare(any(), any(), any())).thenReturn(mismatches);

        // Act
        SyncReport report = dealSyncValidator.checkSync(dealId);

        // Assert
        assertNotNull(report);
        verify(dealSyncService).prepareComparisonData(eDeal, eDealHandler);
        verify(dealPositionSyncService).prepareComparisonData(eDeal, eDealHandler);

        assertFalse(report.getMismatches().isEmpty(), "Mismatches should be present");
    }
}


.sync-indicator-wrapper {
  display: flex;
  align-items: center;
}

.sync-indicator {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 10px;
}

.sync-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #007bff; /* Sync icon color */
}

.sync-button:hover {
  color: #0056b3;
}

.in-sync {
  background-color: #28a745; // Green
}

.not-in-sync {
  background-color: #dc3545; // Red
}

.not-in-sync-waiting {
  background-color: #ffc107; // Orange
}

.unknown-status {
  background-color: #6c757d; // Grey
}
                                    import { Component, Input, OnInit } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { SyncService } from '../services/sync.service';
import { SyncReport, SyncReportStatus } from '../models/sync-report.model';
import { tap, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-sync-status',
  standalone: true,
  imports: [],
  template: `
    <div class="sync-indicator-wrapper">
      <div
        class="sync-indicator"
        [ngClass]="syncStatusClass$ | async"
      ></div>
      <button class="sync-button" (click)="triggerSync()">
        <i class="fa fa-sync-alt"></i>
      </button>
    </div>
  `,
  styleUrls: ['./sync-status.component.scss'],
})
export class SyncStatusComponent implements OnInit {
  @Input() dealId!: number;
  syncStatusClass$!: Observable<string>;

  private refreshTrigger$ = new BehaviorSubject<void>(undefined);

  constructor(private readonly syncService: SyncService) {}

  ngOnInit(): void {
    if (this.dealId) {
      this.syncStatusClass$ = this.refreshTrigger$.pipe(
        switchMap(() =>
          this.syncService.getSyncStatus(this.dealId).pipe(
            tap((value) => console.log('SyncReport: ', value)),
            map((report) => this.getStatusClass(report.status))
          )
        )
      );
    }
  }

  triggerSync(): void {
    this.refreshTrigger$.next(); // Trigger sync check
  }

  private getStatusClass(status: SyncReportStatus): string {
    switch (status) {
      case SyncReportStatus.IN_SYNC:
        return 'in-sync';
      case SyncReportStatus.OUT_OF_SYNC:
        return 'not-in-sync';
      case SyncReportStatus.PENDING:
        return 'not-in-sync-waiting';
      default:
        return 'unknown-status';
    }
  }
}