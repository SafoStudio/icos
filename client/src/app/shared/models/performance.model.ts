export interface Objective {
  id: string;
  title: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  progress?: number;
  status?: string;
}

export interface PerformanceReview {
  id: string;
  employeeId?: string;
  rating?: number;
  summary?: string;
  status?: string;
  finalizedAt?: string;
}

export interface CreateObjectiveInput {
  title: string;
  description?: string;
  startDate: string;
  endDate: string;
  userId?: string;
}

export interface SubmitReviewInput {
  employeeId: string;
  rating: number;
  summary: string;
}
