import { Injectable, inject, computed } from '@angular/core';
import { AuthService } from './auth.service';
import { type User } from '@models';

/**
 * Checks if the department name corresponds to Human Resources.
 */
export function isHrDepartment(name?: string | null): boolean {
  if (!name) return false;
  const normalized = name.trim().toUpperCase();
  return (
    normalized.includes('HR') ||
    normalized.includes('HUMAN RESOURCE') ||
    normalized.includes('RECRUIT') ||
    normalized.includes('КАДР') ||
    normalized.includes('ПЕРСОНАЛ')
  );
}

/**
 * Checks if a given user is an HR member or administrator.
 */
export function isHrUser(user?: User | null): boolean {
  if (!user) return false;
  if (user.role === 'ADMIN' || user.role === 'OWNER' || user.role === 'HR') {
    return true;
  }
  if (isHrDepartment(user.department?.name)) {
    return true;
  }
  const permissions = user.permissions || [];
  return permissions.some(
    (p) =>
      p.startsWith('recruitment:') ||
      p.startsWith('onboarding:') ||
      p.startsWith('offboarding:') ||
      p.startsWith('vacancy:') ||
      p.startsWith('candidate:') ||
      p.startsWith('interview:') ||
      p.startsWith('pipeline:')
  );
}

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private readonly authService = inject(AuthService);

  readonly currentUser = computed(() => this.authService.currentUser());
  readonly role = computed(() => this.currentUser()?.role || 'SPECIALIST');
  readonly department = computed(() => this.currentUser()?.department);
  readonly departmentName = computed(() => this.currentUser()?.department?.name || '');
  readonly permissions = computed(() => this.currentUser()?.permissions || []);

  readonly isOwner = computed(() => this.role() === 'OWNER');
  readonly isAdmin = computed(() => this.role() === 'ADMIN' || this.isOwner());
  readonly isHead = computed(() => this.role() === 'HEAD');
  readonly isSpecialist = computed(() => this.role() === 'SPECIALIST');

  readonly isHrDepartment = computed(() => isHrDepartment(this.departmentName()));

  /**
   * True if user is an Owner, Admin, has HR role, works in an HR department,
   * or holds recruitment/talent permissions.
   */
  readonly isHr = computed(() => isHrUser(this.currentUser()));

  /**
   * True for managers (Owner, Admin, or Department Head).
   */
  readonly isManager = computed(() => this.isAdmin() || this.isHead());

  /**
   * Check if current user has a specific permission.
   */
  hasPermission(permission: string): boolean {
    if (this.isAdmin()) {
      return true;
    }
    return this.permissions().includes(permission);
  }

  /**
   * Check if current user has any of the listed permissions.
   */
  hasAnyPermission(perms: string[]): boolean {
    if (this.isAdmin()) {
      return true;
    }
    const current = this.permissions();
    return perms.some((p) => current.includes(p));
  }

  /**
   * Check if current user has all of the listed permissions.
   */
  hasAllPermissions(perms: string[]): boolean {
    if (this.isAdmin()) {
      return true;
    }
    const current = this.permissions();
    return perms.every((p) => current.includes(p));
  }

  /**
   * Checks whether the current user is allowed to access a specific route or menu item.
   */
  canAccessRoute(route: string): boolean {
    const cleanRoute = route.split('?')[0].split('#')[0];

    // Talent / Recruitment / Onboarding / Offboarding: only HR, Admin, Owner
    if (
      cleanRoute === '/talent' ||
      cleanRoute.startsWith('/talent/') ||
      cleanRoute === '/recruitment' ||
      cleanRoute === '/onboarding' ||
      cleanRoute === '/offboarding' ||
      cleanRoute === '/vacancies' ||
      cleanRoute === '/candidates'
    ) {
      return this.isHr();
    }

    // Organization: Admin, Owner, Department Head, HR, or users with org permissions
    if (cleanRoute === '/organization' || cleanRoute.startsWith('/organization/')) {
      return (
        this.isAdmin() ||
        this.isHead() ||
        this.isHr() ||
        this.hasAnyPermission(['department:read', 'department:manage', 'position:read', 'position:manage'])
      );
    }

    // Settings: Admin, Owner, HR, or users with settings management permissions
    if (cleanRoute === '/settings' || cleanRoute.startsWith('/settings/')) {
      return (
        this.isAdmin() ||
        this.isHr() ||
        this.hasAnyPermission(['company:manage', 'customfield:manage'])
      );
    }

    // Common routes accessible by all authenticated users:
    // /dashboard, /calendar, /timeoff, /tasks, /performance, /members, /profile
    return true;
  }
}
