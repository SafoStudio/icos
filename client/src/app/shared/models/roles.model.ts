export type UserRole = 'OWNER' | 'ADMIN' | 'HEAD' | 'SPECIALIST';

export const ROLES = {
  OWNER: 'OWNER' as const,
  ADMIN: 'ADMIN' as const,
  HEAD: 'HEAD' as const,
  SPECIALIST: 'SPECIALIST' as const,
} as const;

export const ROLE_LABELS: Record<UserRole, string> = {
  OWNER: 'Company Owner',
  ADMIN: 'System Administrator',
  HEAD: 'Department Head',
  SPECIALIST: 'Specialist',
};

export const ROLE_LABELS_SHORT: Record<UserRole, string> = {
  OWNER: 'Owner',
  ADMIN: 'Admin',
  HEAD: 'Head',
  SPECIALIST: 'Specialist',
};

export const ALL_ROLES: UserRole[] = ['OWNER', 'ADMIN', 'HEAD', 'SPECIALIST'];

export const ROLE_ORDER = {
  OWNER: 0,
  ADMIN: 1,
  HEAD: 2,
  SPECIALIST: 3,
} as const;

export const ROLE_COLORS: Record<UserRole, string> = {
  OWNER: '#8B5CF6', // purple
  ADMIN: '#3B82F6', // blue
  HEAD: '#10B981', // green
  SPECIALIST: '#F59E0B', // yellow
};

export const ROLE_CSS_CLASSES: Record<UserRole, string> = {
  OWNER: 'role-owner',
  ADMIN: 'role-admin',
  HEAD: 'role-head',
  SPECIALIST: 'role-specialist',
};

export function getRoleLabel(role: UserRole): string {
  return ROLE_LABELS[role] || role;
}

export function getRoleShortLabel(role: UserRole): string {
  return ROLE_LABELS_SHORT[role] || role;
}

export function getRoleColor(role: UserRole): string {
  return ROLE_COLORS[role] || '#6B7280';
}

export function getRoleCssClass(role: UserRole): string {
  return ROLE_CSS_CLASSES[role] || 'role-default';
}

export function isRole(role: string): role is UserRole {
  return ALL_ROLES.includes(role as UserRole);
}
