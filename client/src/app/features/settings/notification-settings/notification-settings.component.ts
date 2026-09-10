import { Component, inject, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { NotificationService } from '@services';
import { type NotificationType, type NotificationPreference } from '@models';

export interface NotificationSettingGroup {
  id: string;
  title: string;
  description: string;
  icon: string;
  types: string[];
}

@Component({
  selector: 'app-notification-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './notification-settings.component.html',
  styleUrls: ['./notification-settings.component.scss']
})
export class NotificationSettingsComponent {
  private readonly notificationService = inject(NotificationService);

  readonly groups: NotificationSettingGroup[] = [
    {
      id: 'calendar',
      title: 'Calendar & Event Invitations',
      description: 'Get notified when meetings, calendar events, or appointments are scheduled with you as a participant.',
      icon: 'tablerCalendarClock',
      types: ['CALENDAR_EVENT_CREATED']
    },
    {
      id: 'tasks_assign',
      title: 'Task & Board Ticket Assignments',
      description: 'Receive notifications whenever a task or board ticket is assigned to you by colleagues or team leads.',
      icon: 'tablerCheckbox',
      types: ['TASK_ASSIGNED']
    },
    {
      id: 'tasks_status',
      title: 'Task Status & Progress Changes',
      description: 'Stay updated when workflow cards or tickets you are assigned to move stages or change status.',
      icon: 'tablerClipboardList',
      types: ['TASK_STATUS_CHANGED']
    },
    {
      id: 'onboarding',
      title: 'Onboarding & Offboarding Checklists',
      description: 'Alerts for operational checklist items and tasks assigned to you during team transitions.',
      icon: 'tablerUserCheck',
      types: ['ONBOARDING_TASK_ASSIGNED']
    },
    {
      id: 'timeoff',
      title: 'Time Off & Leave Requests',
      description: 'Immediate notification when vacation, sick leave, or time-off requests are approved or rejected.',
      icon: 'tablerClock',
      types: ['LEAVE_REQUEST_APPROVED', 'LEAVE_REQUEST_REJECTED', 'LEAVE_REQUEST_CREATED']
    },
    {
      id: 'talent',
      title: 'Recruitment & Candidate Pipelines',
      description: 'Updates when interview candidates advance stages or when offers are accepted.',
      icon: 'tablerBriefcase',
      types: ['CANDIDATE_STAGE_CHANGED', 'CANDIDATE_HIRED', 'EMPLOYEE_TRANSFERRED']
    },
    {
      id: 'performance',
      title: 'Performance Evaluations & Reviews',
      description: 'Notices when quarterly OKRs, self-evaluations, or performance appraisals are completed.',
      icon: 'tablerChartBar',
      types: ['PERFORMANCE_REVIEW_FINALIZED']
    },
    {
      id: 'messages',
      title: 'Direct Messages & System Notices',
      description: 'Company-wide broadcasts, administrative notices, and direct incoming messages.',
      icon: 'tablerMail',
      types: ['GENERAL_MESSAGE']
    }
  ];

  // Local state for snappy UI interaction before saving
  readonly inAppMap = signal<Record<string, boolean>>({});
  readonly emailMap = signal<Record<string, boolean>>({});
  readonly isSaving = signal<boolean>(false);

  constructor() {
    effect(() => {
      const prefs = this.notificationService.preferences();
      if (prefs.length > 0) {
        this.syncFromPreferences(prefs);
      }
    });
  }

  private syncFromPreferences(prefs: NotificationPreference[]): void {
    const inApp: Record<string, boolean> = {};
    const email: Record<string, boolean> = {};

    this.groups.forEach(group => {
      // Group status is true if primary type is enabled
      const primaryType = group.types[0];
      const match = prefs.find(p => p.type === primaryType);

      inApp[group.id] = match ? match.inAppEnabled : true;
      email[group.id] = match ? match.emailEnabled : false;
    });

    this.inAppMap.set(inApp);
    this.emailMap.set(email);
  }

  toggleInApp(groupId: string): void {
    const current = !!this.inAppMap()[groupId];
    const updated = { ...this.inAppMap(), [groupId]: !current };
    this.inAppMap.set(updated);
    this.persistGroup(groupId, !current, !!this.emailMap()[groupId]);
  }

  toggleEmail(groupId: string): void {
    const current = !!this.emailMap()[groupId];
    const updated = { ...this.emailMap(), [groupId]: !current };
    this.emailMap.set(updated);
    this.persistGroup(groupId, !!this.inAppMap()[groupId], !current);
  }

  enableAllInApp(): void {
    const updated: Record<string, boolean> = {};
    this.groups.forEach(g => updated[g.id] = true);
    this.inAppMap.set(updated);
    this.saveAll();
  }

  disableAllInApp(): void {
    const updated: Record<string, boolean> = {};
    this.groups.forEach(g => updated[g.id] = false);
    this.inAppMap.set(updated);
    this.saveAll();
  }

  enableAllEmail(): void {
    const updated: Record<string, boolean> = {};
    this.groups.forEach(g => updated[g.id] = true);
    this.emailMap.set(updated);
    this.saveAll();
  }

  disableAllEmail(): void {
    const updated: Record<string, boolean> = {};
    this.groups.forEach(g => updated[g.id] = false);
    this.emailMap.set(updated);
    this.saveAll();
  }

  private persistGroup(groupId: string, inAppEnabled: boolean, emailEnabled: boolean): void {
    const group = this.groups.find(g => g.id === groupId);
    if (!group) return;

    for (const type of group.types) {
      this.notificationService.updatePreference(type, inAppEnabled, emailEnabled);
    }
  }

  saveAll(): void {
    this.isSaving.set(true);
    const inApp = this.inAppMap();
    const email = this.emailMap();

    const payload: Array<{ type: string; inAppEnabled: boolean; emailEnabled: boolean }> = [];

    this.groups.forEach(g => {
      const inAppVal = inApp[g.id] ?? true;
      const emailVal = email[g.id] ?? false;

      g.types.forEach(t => {
        payload.push({
          type: t,
          inAppEnabled: inAppVal,
          emailEnabled: emailVal
        });
      });
    });

    this.notificationService.updateAllPreferences(payload);
    setTimeout(() => this.isSaving.set(false), 500);
  }
}
