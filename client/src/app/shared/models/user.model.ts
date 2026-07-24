import {UserProfile} from './user-profile.model'

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role?: string;
  profile?: UserProfile;
  companyId: string;
  departmentId?: string;
  permissions?: string[];
  status?: string;
}
