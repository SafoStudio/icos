export interface Compensation {
  id: string;
  userId: string;
  baseSalary: number;
  currency: string;
  payFrequency: string;
  effectiveDate?: string;
}

export interface SalaryRevision {
  id: string;
  userId: string;
  previousSalary?: number;
  newSalary: number;
  reason?: string;
  effectiveDate?: string;
}

export interface UpdateSalaryInput {
  userId: string;
  newSalary: number;
  reason: string;
  effectiveDate: string;
}
