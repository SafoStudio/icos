import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from '../../../../../../shared/models/user.model';

@Component({
  selector: 'app-department-members',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './department-members.component.html',
  styleUrls: ['./department-members.component.scss']
})
export class DepartmentMembersComponent {
  @Input() members: User[] = [];
  @Input() headUser: User | null = null;
  @Output() memberClick = new EventEmitter<User>();

  getRole(user: User): string {
    if (this.headUser?.id === user.id) {
      return 'HEAD';
    }
    return 'SPECIALIST';
  }

  onRowClick(user: User): void {
    this.memberClick.emit(user);
  }
}
