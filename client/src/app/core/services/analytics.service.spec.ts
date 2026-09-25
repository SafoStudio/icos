import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AnalyticsService } from './analytics.service';
import { ApiService } from './api.service';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let apiServiceSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('ApiService', ['graphql', 'get', 'post', 'delete']);

    TestBed.configureTestingModule({
      providers: [
        AnalyticsService,
        { provide: ApiService, useValue: spy }
      ]
    });

    service = TestBed.inject(AnalyticsService);
    apiServiceSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getExecutiveAnalytics', () => {
    it('should return executive analytics from GraphQL response', (done) => {
      const mockGraphQLResponse = {
        executiveAnalytics: {
          totalEmployees: 180,
          activeProjects: 10,
          turnoverRate: 2.8,
          monthlyPayroll: 310000,
          overallOkrProgress: 85
        }
      };

      apiServiceSpy.graphql.and.returnValue(of(mockGraphQLResponse));

      service.getExecutiveAnalytics().subscribe((result) => {
        expect(result.totalEmployees).toBe(180);
        expect(result.activeProjects).toBe(10);
        expect(result.turnoverRate).toBe(2.8);
        expect(result.headcountTrends.length).toBeGreaterThan(0);
        expect(result.departmentDistribution.length).toBeGreaterThan(0);
        done();
      });
    });

    it('should return default fallback data when GraphQL call fails', (done) => {
      apiServiceSpy.graphql.and.throwError(new Error('Network error'));

      service.getExecutiveAnalytics().subscribe((result) => {
        expect(result.totalEmployees).toBe(156);
        expect(result.overallOkrProgress).toBe(78);
        expect(result.headcountTrends.length).toBe(9);
        done();
      });
    });
  });

  describe('getHrAnalytics', () => {
    it('should return HR recruitment analytics with funnel stages', (done) => {
      const mockHrResponse = {
        recruitmentAnalytics: {
          openVacancies: 12,
          totalCandidates: 160,
          avgTimeToHireDays: 21,
          offerAcceptanceRate: 92
        }
      };

      apiServiceSpy.graphql.and.returnValue(of(mockHrResponse));

      service.getHrAnalytics().subscribe((result) => {
        expect(result.openVacancies).toBe(12);
        expect(result.totalCandidates).toBe(160);
        expect(result.funnelStages.length).toBe(6);
        expect(result.candidateSources.length).toBe(4);
        done();
      });
    });
  });

  describe('getMemberAnalytics', () => {
    it('should return member self-service metrics', (done) => {
      service.getMemberAnalytics().subscribe((result) => {
        expect(result.personalOkrProgress).toBe(82);
        expect(result.tasksCompleted).toBe(24);
        expect(result.leaveDaysRemaining).toBe(18);
        done();
      });
    });
  });
});
