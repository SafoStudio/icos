import {
  Component,
  Input,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnChanges,
  OnDestroy,
  SimpleChanges
} from '@angular/core';
import { CommonModule } from '@angular/common';
import Chart, { ChartConfiguration } from 'chart.js/auto';

export interface BarDataset {
  label: string;
  data: number[];
  backgroundColor?: string | string[];
  borderColor?: string | string[];
  borderWidth?: number;
  borderRadius?: number;
}

@Component({
  selector: 'app-bar-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="chart-wrapper" [style.height.px]="height">
      <canvas #chartCanvas></canvas>
    </div>
  `,
  styles: [`
    .chart-wrapper {
      position: relative;
      width: 100%;
    }
    canvas {
      width: 100% !important;
      height: 100% !important;
    }
  `]
})
export class BarChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  @Input() labels: string[] = [];
  @Input() datasets: BarDataset[] = [];
  @Input() horizontal = false;
  @Input() height = 260;
  @Input() showLegend = false;
  @Input() valuePrefix = '';
  @Input() valueSuffix = '';

  private chart?: Chart;

  ngAfterViewInit(): void {
    this.createChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.chart && (changes['labels'] || changes['datasets'] || changes['horizontal'])) {
      this.updateChart();
    }
  }

  ngOnDestroy(): void {
    if (this.chart) {
      this.chart.destroy();
    }
  }

  private createChart(): void {
    if (!this.chartCanvas) return;

    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels: this.labels,
        datasets: this.datasets.map(ds => ({
          ...ds,
          backgroundColor: ds.backgroundColor || '#6366f1',
          borderRadius: ds.borderRadius ?? 6,
          borderWidth: ds.borderWidth ?? 0
        }))
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        indexAxis: this.horizontal ? 'y' : 'x',
        plugins: {
          legend: {
            display: this.showLegend,
            labels: {
              color: '#94a3b8',
              font: { family: 'inherit', size: 12 }
            }
          },
          tooltip: {
            backgroundColor: '#1e293b',
            titleColor: '#f8fafc',
            bodyColor: '#cbd5e1',
            borderColor: 'rgba(255, 255, 255, 0.1)',
            borderWidth: 1,
            padding: 10,
            callbacks: {
              label: (context) => {
                const val = context.raw as number;
                return `${context.dataset.label || ''}: ${this.valuePrefix}${val.toLocaleString()}${this.valueSuffix}`;
              }
            }
          }
        },
        scales: {
          x: {
            grid: {
              color: 'rgba(255, 255, 255, 0.05)'
            },
            ticks: {
              color: '#94a3b8',
              font: { size: 11 }
            }
          },
          y: {
            grid: {
              color: 'rgba(255, 255, 255, 0.05)'
            },
            ticks: {
              color: '#94a3b8',
              font: { size: 11 },
              callback: (value) => `${this.valuePrefix}${value}${this.valueSuffix}`
            }
          }
        }
      }
    };

    this.chart = new Chart(ctx, config);
  }

  private updateChart(): void {
    if (!this.chart) return;
    this.chart.data.labels = this.labels;
    this.chart.data.datasets = this.datasets.map(ds => ({
      ...ds,
      backgroundColor: ds.backgroundColor || '#6366f1',
      borderRadius: ds.borderRadius ?? 6,
      borderWidth: ds.borderWidth ?? 0
    }));
    this.chart.update();
  }
}
