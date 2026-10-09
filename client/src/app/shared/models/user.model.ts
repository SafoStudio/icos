import { UserProfile } from './user-profile.model';
import { Department } from './department.model';
import { UserDocument } from './user-document.model';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role?: string;
  profile?: UserProfile;
  companyId: string;
  departmentId?: string;
  department?: Department;
  permissions?: string[];
  status?: string;
  documents?: UserDocument[];
}
