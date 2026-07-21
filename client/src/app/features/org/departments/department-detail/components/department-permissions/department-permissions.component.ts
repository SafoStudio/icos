import { Component, Input, Output, EventEmitter, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Permission } from '@models';

export interface PermissionGroup {
  name: string;
  permissions: Permission[];
}

@Component({
  selector: 'app-department-permissions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './department-permissions.component.html',
  styleUrls: ['./department-permissions.component.scss']
})
export class DepartmentPermissionsComponent {
  @Input() allPermissions: Permission[] = [];
  @Input() headPermissions: Permission[] = [];
  @Input() specialistPermissions: Permission[] = [];
  @Input() canEdit: boolean = false;
  @Input() saving: boolean = false;
  @Output() togglePermission = new EventEmitter<{ permission: Permission; role: 'HEAD' | 'SPECIALIST' }>();

  readonly groupedPermissions = computed(() => {
    const groups: Map<string, Permission[]> = new Map();

    this.allPermissions.forEach(permission => {
      const prefix = permission.name.split(':')[0];
      if (!groups.has(prefix)) {
        groups.set(prefix, []);
      }
      groups.get(prefix)!.push(permission);
    });

    const sortedGroups: PermissionGroup[] = [];
    const groupNames = Array.from(groups.keys()).sort();

    groupNames.forEach(name => {
      const perms = groups.get(name)!.sort((a, b) => a.name.localeCompare(b.name));
      sortedGroups.push({ name, permissions: perms });
    });

    return sortedGroups;
  });

  readonly groupLabels: Record<string, string> = {
    'company': 'Company',
    'department': 'Department',
    'position': 'Position',
    'recruitment': 'Recruitment',
    'vacancy': 'Vacancy',
    'candidate': 'Candidate',
    'application': 'Application',
    'interview': 'Interview',
    'pipeline': 'Pipeline',
    'onboarding': 'Onboarding',
    'profile': 'Profile',
    'user': 'User',
    'role': 'Role',
    'membership': 'Membership',
    'invite': 'Invite',
    'compensation': 'Compensation',
    'salary': 'Salary',
    'timeoff': 'Time Off',
    'performance': 'Performance',
    'calendar': 'Calendar',
    'task': 'Task',
    'kanban': 'Kanban',
    'report': 'Report',
    'customfield': 'Custom Field',
    'document': 'Document',
    'notification': 'Notification',
    'audit': 'Audit',
    'approval': 'Approval'
  };

  isPermissionAssigned(permission: Permission, role: 'HEAD' | 'SPECIALIST'): boolean {
    const list = role === 'HEAD' ? this.headPermissions : this.specialistPermissions;
    return list.some(p => p.id === permission.id);
  }

  onToggle(permission: Permission, role: 'HEAD' | 'SPECIALIST'): void {
    this.togglePermission.emit({ permission, role });
  }

  getGroupLabel(groupName: string): string {
    return this.groupLabels[groupName] || groupName.charAt(0).toUpperCase() + groupName.slice(1);
  }
}
