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