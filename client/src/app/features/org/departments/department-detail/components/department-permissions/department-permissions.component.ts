import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Permission } from '../../../../../../shared/models/permission.model';

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
  @Output() togglePermission  = new EventEmitter<{ permission: Permission; role: 'HEAD' | 'SPECIALIST' }>();

  isPermissionAssigned(permission: Permission, role: 'HEAD' | 'SPECIALIST'): boolean {
    const list = role === 'HEAD' ? this.headPermissions : this.specialistPermissions;
    return list.some(p => p.id === permission.id);
  }

  onToggle(permission: Permission, role: 'HEAD' | 'SPECIALIST'): void {
    this.togglePermission.emit({ permission, role });
  }
}
