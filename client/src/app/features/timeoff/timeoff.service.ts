import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '@services';
import { LeaveType, LeaveBalance, LeaveRequest, CreateLeaveRequestInput } from '@models';

@Injectable({
  providedIn: 'root'
})
export class TimeoffService {
  private readonly api = inject(ApiService);

  getLeaveTypes(): Observable<LeaveType[]> {
    const query = `
      query GetLeaveTypes {
        leaveTypes {
          id
          name
          code
          requiresBalance
          color
          isActive
        }
      }
    `;
    return this.api.graphql<{ leaveTypes: LeaveType[] }>(query).pipe(
      map(res => res.leaveTypes || [])
    );
  }

  getMyLeaveBalances(): Observable<LeaveBalance[]> {
    const query = `
      query GetMyLeaveBalances {
        myLeaveBalances {
          id
          userId
          leaveTypeId
          totalDays
          usedDays
          remainingDays
        }
      }
    `;
    return this.api.graphql<{ myLeaveBalances: LeaveBalance[] }>(query).pipe(
      map(res => res.myLeaveBalances || [])
    );
  }

  getMyLeaveRequests(): Observable<LeaveRequest[]> {
    const query = `
      query GetMyLeaveRequests {
        myLeaveRequests {
          id
          userId
          leaveTypeId
          startDate
          endDate
          totalDays
          status
          reason
        }
      }
    `;
    return this.api.graphql<{ myLeaveRequests: LeaveRequest[] }>(query).pipe(
      map(res => res.myLeaveRequests || [])
    );
  }

  getUserLeaveBalances(userId: string): Observable<LeaveBalance[]> {
    const query = `
      query GetUserLeaveBalances($userId: ID!) {
        userLeaveBalances(userId: $userId) {
          id
          userId
          leaveTypeId
          totalDays
          usedDays
          remainingDays
        }
      }
    `;
    return this.api.graphql<{ userLeaveBalances: LeaveBalance[] }>(query, { userId }).pipe(
      map(res => res.userLeaveBalances || [])
    );
  }

  getUserLeaveRequests(userId: string): Observable<LeaveRequest[]> {
    const query = `
      query GetUserLeaveRequests($userId: ID!) {
        userLeaveRequests(userId: $userId) {
          id
          userId
          leaveTypeId
          startDate
          endDate
          totalDays
          status
          reason
        }
      }
    `;
    return this.api.graphql<{ userLeaveRequests: LeaveRequest[] }>(query, { userId }).pipe(
      map(res => res.userLeaveRequests || [])
    );
  }

  createLeaveRequest(input: CreateLeaveRequestInput): Observable<LeaveRequest> {
    const mutation = `
      mutation CreateLeaveRequest($leaveTypeId: ID!, $startDate: String!, $endDate: String!, $reason: String) {
        createLeaveRequest(leaveTypeId: $leaveTypeId, startDate: $startDate, endDate: $endDate, reason: $reason) {
          id
          userId
          leaveTypeId
          startDate
          endDate
          totalDays
          status
          reason
        }
      }
    `;
    return this.api.graphql<{ createLeaveRequest: LeaveRequest }>(mutation, input).pipe(
      map(res => res.createLeaveRequest)
    );
  }

  approveLeaveRequest(id: string): Observable<LeaveRequest> {
    const mutation = `
      mutation ApproveLeaveRequest($id: ID!) {
        approveLeaveRequest(id: $id) {
          id
          userId
          leaveTypeId
          startDate
          endDate
          totalDays
          status
          reason
        }
      }
    `;
    return this.api.graphql<{ approveLeaveRequest: LeaveRequest }>(mutation, { id }).pipe(
      map(res => res.approveLeaveRequest)
    );
  }

  rejectLeaveRequest(id: string, comment?: string): Observable<LeaveRequest> {
    const mutation = `
      mutation RejectLeaveRequest($id: ID!, $comment: String) {
        rejectLeaveRequest(id: $id, comment: $comment) {
          id
          userId
          leaveTypeId
          startDate
          endDate
          totalDays
          status
          reason
        }
      }
    `;
    return this.api.graphql<{ rejectLeaveRequest: LeaveRequest }>(mutation, { id, comment }).pipe(
      map(res => res.rejectLeaveRequest)
    );
  }

  cancelLeaveRequest(id: string): Observable<LeaveRequest> {
    const mutation = `
      mutation CancelLeaveRequest($id: ID!) {
        cancelLeaveRequest(id: $id) {
          id
          userId
          leaveTypeId
          startDate
          endDate
          totalDays
          status
          reason
        }
      }
    `;
    return this.api.graphql<{ cancelLeaveRequest: LeaveRequest }>(mutation, { id }).pipe(
      map(res => res.cancelLeaveRequest)
    );
  }
}
