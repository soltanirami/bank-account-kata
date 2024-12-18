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