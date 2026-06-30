import { Component, signal, computed, inject, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';

interface MainLink {
  label: string;
  icon: string;
  notifications?: number;
}

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  badge?: number;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, NgIconComponent],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  private readonly router = inject(Router);
  private readonly isCollapsedSignal = signal(false);
  isCollapsed = computed(() => this.isCollapsedSignal());
  collapsedChange = output<boolean>();

  userName = signal('John Doe');
  userEmail = signal('john.doe@hrplatform.com');
  userAvatar = signal('A');

  mainLinks: MainLink[] = [
    { label: 'Activity', icon: 'tablerBulb', notifications: 1 },
    { label: 'Tasks', icon: 'tablerCheckbox', notifications: 4 },
    { label: 'Messages', icon: 'tablerMail', notifications: 3 },
  ];

  menuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'tablerDashboard', route: '/dashboard' },
    { label: 'Candidates', icon: 'tablerUserCheck', route: '/candidates' },
    { label: 'Vacancies', icon: 'tablerBriefcase', route: '/vacancies' },
    { label: 'Members', icon: 'tablerUsers', route: '/members' },
    { label: 'Interviews', icon: 'tablerCalendarEvent', route: '/interviews' },
    { label: 'Reports', icon: 'tablerChartBar', route: '/reports' },
    { label: 'Tasks', icon: 'tablerClipboardList', route: '/tasks' },
    { label: 'Settings', icon: 'tablerSettings', route: '/settings' }
  ];

  toggleSidebar(): void {
    this.isCollapsedSignal.set(!this.isCollapsed());
    this.collapsedChange.emit(this.isCollapsed());
  }

  isActive(route: string): boolean {
    return this.router.url.includes(route);
  }

  logout(): void {
    console.log('Logout clicked');
  }
}
