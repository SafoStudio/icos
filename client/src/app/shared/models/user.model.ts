export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role?: string;
  companyId?: string;
  departmentId?: string;
  permissions?: string[];
}
