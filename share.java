import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

class DealSyncValidatorTest {

    @Mock
    private IDealStructureFetcher dealStructureFetcher;

    @Mock
    private IDealRepo dealRepo;

    @Mock
    private DealHandlerRepo dealHandlerRepo;

    @Mock
    private DealMapper dealMapper;

    @Mock
    private WaitingEventTransactionService waitingEventTransactionService;

    @InjectMocks
    private DealSyncValidator dealSyncValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validate_whenDataIsInSync_shouldReturnSuccess() {
        // Arrange
        long dealId = 14644L;
        EDealHandler eDealHandler = mock(EDealHandler.class);
        DealDto dealDtoFromCS = mock(DealDto.class);
        EDeal eDeal = mock(EDeal.class);
        DealDto mappedDeal = mock(DealDto.class);
        SyncReport syncReport = mock(SyncReport.class);

        when(dealHandlerRepo.findByObjectIdAndObjectType(dealId, ObjectType.DEAL))
                .thenReturn(Optional.of(eDealHandler));
        when(eDealHandler.getFunctionalId()).thenReturn("fn-id");
        when(eDealHandler.getVersionId()).thenReturn("v1");
        when(dealStructureFetcher.fetchByFnId("fn-id", "v1")).thenReturn(dealDtoFromCS);
        when(dealRepo.findById(dealId)).thenReturn(Optional.of(eDeal));
        when(dealMapper.toCsDealDto(eDeal, new DealDto())).thenReturn(mappedDeal);
        when(ComparisonUtil.compareObjects(mappedDeal, dealDtoFromCS)).thenReturn(syncReport);
        when(syncReport.dataAreInSync()).thenReturn(true);

        // Act
        SyncReport result = dealSyncValidator.validate(dealId);

        // Assert
        assertNotNull(result);
        assertTrue(result.dataAreInSync());
        verify(dealHandlerRepo).findByObjectIdAndObjectType(dealId, ObjectType.DEAL);
        verify(dealStructureFetcher).fetchByFnId("fn-id", "v1");
        verify(dealRepo).findById(dealId);
        verify(dealMapper).toCsDealDto(eDeal, new DealDto());
        verify(ComparisonUtil).compareObjects(mappedDeal, dealDtoFromCS);
        verifyNoInteractions(waitingEventTransactionService); // Ensure no calls to waiting events
    }

    @Test
    void validate_whenDealHandlerIsMissing_shouldThrowException() {
        // Arrange
        long dealId = 14644L;

        when(dealHandlerRepo.findByObjectIdAndObjectType(dealId, ObjectType.DEAL))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> dealSyncValidator.validate(dealId));
        assertEquals("Deal: 14644 does not exist in dealHandler table", exception.getMessage());
        verify(dealHandlerRepo).findByObjectIdAndObjectType(dealId, ObjectType.DEAL);
        verifyNoInteractions(dealStructureFetcher, dealRepo, dealMapper, waitingEventTransactionService);
    }

    @Test
    void validate_whenDataIsNotInSync_shouldFetchWaitingEvents() {
        // Arrange
        long dealId = 14644L;
        EDealHandler eDealHandler = mock(EDealHandler.class);
        DealDto dealDtoFromCS = mock(DealDto.class);
        EDeal eDeal = mock(EDeal.class);
        DealDto mappedDeal = mock(DealDto.class);
        SyncReport syncReport = mock(SyncReport.class);

        when(dealHandlerRepo.findByObjectIdAndObjectType(dealId, ObjectType.DEAL))
                .thenReturn(Optional.of(eDealHandler));
        when(eDealHandler.getFunctionalId()).thenReturn("fn-id");
        when(eDealHandler.getVersionId()).thenReturn("v1");
        when(dealStructureFetcher.fetchByFnId("fn-id", "v1")).thenReturn(dealDtoFromCS);
        when(dealRepo.findById(dealId)).thenReturn(Optional.of(eDeal));
        when(dealMapper.toCsDealDto(eDeal, new DealDto())).thenReturn(mappedDeal);
        when(ComparisonUtil.compareObjects(mappedDeal, dealDtoFromCS)).thenReturn(syncReport);
        when(syncReport.dataAreInSync()).thenReturn(false);
        when(waitingEventTransactionService.fetchWaitingEvents(dealId)).thenReturn(List.of("Event1", "Event2"));

        // Act
        SyncReport result = dealSyncValidator.validate(dealId);

        // Assert
        assertNotNull(result);
        assertFalse(result.dataAreInSync());
        verify(syncReport).setWaitingEvents(List.of("Event1", "Event2"));
        verify(dealHandlerRepo).findByObjectIdAndObjectType(dealId, ObjectType.DEAL);
        verify(dealStructureFetcher).fetchByFnId("fn-id", "v1");
        verify(dealRepo).findById(dealId);
        verify(dealMapper).toCsDealDto(eDeal, new DealDto());
        verify(waitingEventTransactionService).fetchWaitingEvents(dealId);
    }
}
  constructor(private http: HttpClient) {}

  /**
   * Fetches sync status between LDT and CS
   * @param dealId The deal ID to check sync status
   * @returns Observable with the sync status (true if in sync, false if not)
   */
  getSyncStatus(dealId: string): Observable<{ isInSync: boolean }> {
    return this.http.get<{ isInSync: boolean }>(`${this.SYNC_URL}/${dealId}`);
  }
}

3️⃣ sync-status.component.ts

This is the sync status component that shows a circular red/green indicator.

import { Component, Input, OnInit } from '@angular/core';
import { SyncService } from '../../services/sync.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-sync-status',
  templateUrl: './sync-status.component.html',
  styleUrls: ['./sync-status.component.scss']
})
export class SyncStatusComponent implements OnInit {

  @Input() dealId!: string; // Deal ID passed from parent component
  syncStatus$!: Observable<{ isInSync: boolean }>;

  constructor(private syncService: SyncService) {}

  ngOnInit(): void {
    if (this.dealId) {
      this.syncStatus$ = this.syncService.getSyncStatus(this.dealId);
    }
  }
}

4️⃣ sync-status.component.html

This is the HTML file for the sync status indicator.

	•	If in sync, it shows a green circle.
	•	If not in sync, it shows a red circle.

<div *ngIf="syncStatus$ | async as syncStatus">
  <div class="sync-indicator" 
       [ngClass]="{ 'in-sync': syncStatus.isInSync, 'not-in-sync': !syncStatus.isInSync }">
  </div>
</div>

5️⃣ sync-status.component.scss

This is the SCSS file for the sync status circular indicator.

	•	The indicator is a circle with a red or green background.
	•	The indicator has a size of 20px x 20px.

.sync-indicator {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: inline-block;
  border: 2px solid #ccc; 
}

.in-sync {
  background-color: #28a745; // Green
}

.not-in-sync {
  background-color: #dc3545; // Red
}

6️⃣ example-legacy.component.html

This is the legacy component that will now use the Sync Status Component.

<h3>Deal Information</h3>

<!-- Legacy Content -->
<div>
  <p>Deal ID: {{ dealId }}</p>
  <p>Other Deal Info...</p>
</div>

<!-- New Sync Status Indicator (Green/Red Circle) -->
<app-sync-status [dealId]="dealId"></app-sync-status>

7️⃣ example-legacy.component.ts

No major changes are required in the legacy component.
All we need to do is pass the dealId to the sync status component.

import { Component } from '@angular/core';

@Component({
  selector: 'app-existing-legacy',
  templateUrl: './existing-legacy.component.html',
  styleUrls: ['./existing-legacy.component.scss']
})
export class ExistingLegacyComponent {
  dealId = 'D123'; // Example dealId
}

8️⃣ app.module.ts

Finally, make sure the Sync Module is registered in your app.module.ts.
This makes sure the SyncStatusComponent is recognized in the legacy component.

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { ExistingLegacyComponent } from './legacy/existing-legacy.component';
import { SyncModule } from './features/sync/sync.module';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    ExistingLegacyComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    SyncModule // Import new sync module
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

3. Demo

Here’s what happens in the UI:

	1.	Green Circle = LDT and CS are in sync.
	2.	Red Circle = LDT and CS are NOT in sync.

4. Final Folder Structure

src/
  app/
    features/
      sync/
        components/
          sync-status/
            sync-status.component.ts
            sync-status.component.html
            sync-status.component.scss
        services/
          sync.service.ts
        sync.module.ts
    legacy/
      existing-legacy.component.ts
      existing-legacy.component.html
  app.module.ts

5. Full Summary

Feature	Details
Feature Module	SyncModule (isolated, no regressions)
Component	SyncStatusComponent (green/red circle)
Service	SyncService (fetches sync status)
Legacy Code Changes	Minimal (only pass dealId to SyncStatusComponent)
Dependency	Uses HttpClientModule for API calls
Backward Compatible?	✅ No regression risks.

6. Example API Response

The backend API should return a JSON response like this:

{
  "isInSync": true // Or false
}

7. Customization Ideas

	1.	Feature Toggle:
Add an environment variable (like environment.production.ts) to enable/disable the sync indicator.
	2.	Loader for Sync Status:
Show a spinner while the sync status is being fetched from the backend.
	3.	More Detailed Status:
If you want more than just red/green, add tooltips like “X field is different” or “Out of sync because of…”.

8. Commands Recap

Run these Angular CLI commands to generate the structure:

ng generate module features/sync --route sync --module app.module.ts
ng generate service features/sync/services/sync
ng generate component features/sync/components/sync-status

9. Testing

	1.	Unit Tests:
	•	Write unit tests for the SyncService and SyncStatusComponent.
	2.	E2E Tests:
	•	Test if the indicator turns green/red based on API results.
	3.	Integration Testing:
	•	Run API mocks for /api/sync/{dealId} and verify that the circle updates correctly.

10. Result

	•	Green Circle ✅ = In sync
	•	Red Circle ❌ = Not in sync

With this setup, you have a fully modular Sync Status indicator that uses an isolated Sync Module. It has no effect on legacy code and is fully reusable in any other component. Let me know if you’d like any changes, optimizations, or detailed explanations on any part of this!