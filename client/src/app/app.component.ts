import { Component, inject, computed, ViewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from '@services';
import { ToastContainerComponent, SidebarComponent } from '@components';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, ToastContainerComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  private readonly authService = inject(AuthService);
  isLoggedIn = computed(() => this.authService.isLoggedIn());
  isCollapsed = true;

  @ViewChild(SidebarComponent) sidebar!: SidebarComponent;

  onOverlayClick(): void {
    if (this.sidebar) {
      this.sidebar.closeSidebar();
    }
  }
}
