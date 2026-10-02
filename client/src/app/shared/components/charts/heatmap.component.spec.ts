import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeatmapComponent } from './heatmap.component';

describe('HeatmapComponent', () => {
  let component: HeatmapComponent;
  let fixture: ComponentFixture<HeatmapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeatmapComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(HeatmapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create heatmap component', () => {
    expect(component).toBeTruthy();
  });

  it('should process unique dates and department rows', () => {
    component.data = [
      {
        date: '2026-10-01',
        departmentId: 'dept-1',
        departmentName: 'Engineering',
        absentCount: 1,
        totalCount: 10,
        loadPercent: 10.0
      },
      {
        date: '2026-10-02',
        departmentId: 'dept-1',
        departmentName: 'Engineering',
        absentCount: 2,
        totalCount: 10,
        loadPercent: 20.0
      },
      {
        date: '2026-10-01',
        departmentId: 'dept-2',
        departmentName: 'Design',
        absentCount: 0,
        totalCount: 5,
        loadPercent: 0.0
      }
    ];

    expect(component.uniqueDates).toEqual(['2026-10-01', '2026-10-02']);
    expect(component.heatmapRows.length).toBe(2);
    expect(component.heatmapRows[0].cells.length).toBe(2);
  });

  it('should return appropriate cell colors based on load percent', () => {
    expect(component.getCellColor(0)).toContain('rgba(255, 255, 255');
    expect(component.getCellColor(10)).toContain('rgba(16, 185, 129');
    expect(component.getCellColor(25)).toContain('rgba(56, 189, 248');
    expect(component.getCellColor(45)).toContain('rgba(245, 158, 11');
    expect(component.getCellColor(60)).toContain('rgba(239, 68, 68');
  });
});
