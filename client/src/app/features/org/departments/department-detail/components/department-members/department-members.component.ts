import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { type DepartmentMember } from '@models';

@Component({
  selector: 'app-department-members',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './department-members.component.html',
  styleUrls: ['./department-members.component.scss']
})
export class DepartmentMembersComponent {
  @Input() members: DepartmentMember[] = [];
  @Output() memberClick = new EventEmitter<DepartmentMember>();

  onRowClick(user: DepartmentMember): void {
    this.memberClick.emit(user);
  }
}
