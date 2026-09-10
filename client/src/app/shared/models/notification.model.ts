export type NotificationType =
  | 'LEAVE_REQUEST_CREATED'
  | 'LEAVE_REQUEST_APPROVED'
  | 'LEAVE_REQUEST_REJECTED'
  | 'CANDIDATE_STAGE_CHANGED'
  | 'CANDIDATE_HIRED'
  | 'EMPLOYEE_TRANSFERRED'
  | 'ONBOARDING_TASK_ASSIGNED'
  | 'CALENDAR_EVENT_CREATED'
  | 'TASK_ASSIGNED'
  | 'TASK_STATUS_CHANGED'
  | 'PERFORMANCE_REVIEW_FINALIZED'
  | 'GENERAL_MESSAGE';

export interface AppNotification {
  id: string;
  userId: string;
  type: NotificationType | string;
  title: string;
  message?: string;
  isRead: boolean;
  createdAt: string;
}

export interface NotificationPreference {
  id?: string;
  userId: string;
  type: NotificationType | string;
  inAppEnabled: boolean;
  emailEnabled: boolean;
}

export interface NotificationCategoryMeta {
  type: NotificationType;
  title: string;
  description: string;
  icon: string;
  category: string;
}
