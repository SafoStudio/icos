import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-radial-gauge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="gauge-container" [style.width.px]="size" [style.height.px]="size">
      <svg [attr.viewBox]="'0 0 ' + size + ' ' + size" class="gauge-svg">
        <circle
          class="gauge-bg"
          [attr.cx]="size / 2"
          [attr.cy]="size / 2"
          [attr.r]="radius"
          [attr.stroke-width]="strokeWidth"
        />
        <circle
          class="gauge-progress"
          [style.stroke]="color"
          [attr.cx]="size / 2"
          [attr.cy]="size / 2"
          [attr.r]="radius"
          [attr.stroke-width]="strokeWidth"
          [attr.stroke-dasharray]="circumference"
          [attr.stroke-dashoffset]="dashOffset"
        />
      </svg>
      <div class="gauge-content">
        <span class="gauge-value" [style.color]="color">{{ value }}%</span>
        @if (label) {
          <span class="gauge-label">{{ label }}</span>
        }
      </div>
    </div>
  `,
  styles: [`
    .gauge-container {
      position: relative;
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }
    .gauge-svg {
      transform: rotate(-90deg);
      width: 100%;
      height: 100%;
    }
    .gauge-bg {
      fill: none;
      stroke: rgba(255, 255, 255, 0.08);
    }
    .gauge-progress {
      fill: none;
      stroke-linecap: round;
      transition: stroke-dashoffset 0.8s cubic-bezier(0.4, 0, 0.2, 1);
    }
    .gauge-content {
      position: absolute;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
      pointer-events: none;
    }
    .gauge-value {
      font-size: 1.5rem;
      font-weight: 700;
      line-height: 1;
      font-feature-settings: "tnum";
    }
    .gauge-label {
      font-size: 0.75rem;
      color: #94a3b8;
      margin-top: 4px;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
  `]
})
export class RadialGaugeComponent {
  @Input() value = 0; // 0 to 100
  @Input() label = '';
  @Input() size = 140;
  @Input() strokeWidth = 10;
  @Input() color = '#10b981';

  get radius(): number {
    return (this.size - this.strokeWidth) / 2;
  }

  get circumference(): number {
    return 2 * Math.PI * this.radius;
  }

  get dashOffset(): number {
    const clamped = Math.max(0, Math.min(100, this.value));
    return this.circumference - (clamped / 100) * this.circumference;
  }
}
