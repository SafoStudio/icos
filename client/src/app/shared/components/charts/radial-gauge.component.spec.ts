import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RadialGaugeComponent } from './radial-gauge.component';

describe('RadialGaugeComponent', () => {
  let component: RadialGaugeComponent;
  let fixture: ComponentFixture<RadialGaugeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RadialGaugeComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(RadialGaugeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create radial gauge component', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate correct radius and circumference', () => {
    component.size = 140;
    component.strokeWidth = 10;
    expect(component.radius).toBe(65);
    expect(component.circumference).toBeCloseTo(2 * Math.PI * 65, 2);
  });

  it('should calculate dashOffset based on percentage value', () => {
    component.value = 50;
    const fullCircumference = component.circumference;
    expect(component.dashOffset).toBeCloseTo(fullCircumference * 0.5, 2);

    component.value = 100;
    expect(component.dashOffset).toBeCloseTo(0, 2);

    component.value = 0;
    expect(component.dashOffset).toBeCloseTo(fullCircumference, 2);
  });

  it('should render percentage label in template', () => {
    component.value = 78;
    component.label = 'Completion';
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const valueEl = compiled.querySelector('.gauge-value');
    const labelEl = compiled.querySelector('.gauge-label');

    expect(valueEl?.textContent?.trim()).toBe('78%');
    expect(labelEl?.textContent?.trim()).toBe('Completion');
  });
});
