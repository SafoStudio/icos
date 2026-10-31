import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, PermissionService } from '@services';
import { MemberService, Member } from '../members/member.service';
import { PerformanceTabComponent } from './performance-tab/performance-tab.component';

@Component({
  selector: 'app-performance',
  standalone: true,
  imports: [CommonModule, FormsModule, PerformanceTabComponent],
  templateUrl: './performance.component.html',
  styleUrls: ['./performance.component.scss']
})
export class PerformanceComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly permissionService = inject(PermissionService);
  private readonly memberService = inject(MemberService);

  readonly currentUser = computed(() => this.authService.currentUser());
  readonly selectedMemberId = signal<string | null>(null);
  readonly members = signal<Member[]>([]);

  readonly canManage = computed(() => {
    return this.permissionService.isManager() || this.permissionService.isHr();
  });

  ngOnInit(): void {
    const user = this.currentUser();
    if (user?.id) {
      this.selectedMemberId.set(user.id);
    }

    if (user?.companyId && this.canManage()) {
      this.memberService.getMembers(user.companyId).subscribe({
        next: (list) => {
          this.members.set(list || []);
        },
        error: (err) => console.error('Error fetching members for performance selector:', err)
      });
    }
  }

  onMemberSelect(memberId: string): void {
    this.selectedMemberId.set(memberId);
  }
}
