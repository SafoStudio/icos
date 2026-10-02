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

  describe('getHeadcountTrend', () => {
    it('should return headcount trends', (done) => {
      const mockPoints = [
        { period: '2026-08', headcount: 140, hired: 5, fired: 1, turnoverRate: 0.7 },
        { period: '2026-09', headcount: 145, hired: 6, fired: 1, turnoverRate: 0.7 }
      ];
      apiServiceSpy.graphql.and.returnValue(of({ headcountTrend: mockPoints }));

      service.getHeadcountTrend(2).subscribe((res) => {
        expect(res.length).toBe(2);
        expect(res[0].headcount).toBe(140);
        done();
      });
    });
  });

  describe('getTimeOffHeatmap', () => {
    it('should return heatmap daily data', (done) => {
      const mockDays = [
        {
          date: '2026-10-01',
          departmentId: 'd1',
          departmentName: 'Engineering',
          absentCount: 2,
          totalCount: 20,
          loadPercent: 10.0
        }
      ];
      apiServiceSpy.graphql.and.returnValue(of({ timeOffHeatmap: mockDays }));

      service.getTimeOffHeatmap('2026-10-01', '2026-10-02').subscribe((res) => {
        expect(res.length).toBe(1);
        expect(res[0].absentCount).toBe(2);
        done();
      });
    });
  });

  describe('getSalaryDistribution', () => {
    it('should return salary grade buckets', (done) => {
      const mockBuckets = [
        { label: 'Junior', min: 1000, max: 2000, avg: 1500, count: 5 }
      ];
      apiServiceSpy.graphql.and.returnValue(of({ salaryDistribution: mockBuckets }));

      service.getSalaryDistribution().subscribe((res) => {
        expect(res.length).toBe(1);
        expect(res[0].avg).toBe(1500);
        done();
      });
    });
  });

  describe('getObjectiveProgress', () => {
    it('should return OKR progress and key results', (done) => {
      const mockOkrs = [
        {
          id: '1',
          title: 'Infrastructure SLA',
          progress: 88,
          keyResults: [
            { id: '1-1', title: 'Target 99.9%', progress: 90, weight: 1, status: 'ON_TRACK' }
          ]
        }
      ];
      apiServiceSpy.graphql.and.returnValue(of({ objectiveProgress: mockOkrs }));

      service.getObjectiveProgress().subscribe((res) => {
        expect(res.length).toBe(1);
        expect(res[0].progress).toBe(88);
        expect(res[0].keyResults.length).toBe(1);
        done();
      });
    });
  });
});
