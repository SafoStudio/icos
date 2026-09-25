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

export interface LineDataset {
  label: string;
  data: number[];
  borderColor?: string;
  backgroundColor?: string;
  fill?: boolean;
  tension?: number;
}

@Component({
  selector: 'app-line-chart',
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
export class LineChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  @Input() labels: string[] = [];
  @Input() datasets: LineDataset[] = [];
  @Input() height = 260;
  @Input() showLegend = true;
  @Input() valuePrefix = '';
  @Input() valueSuffix = '';

  private chart?: Chart;

  ngAfterViewInit(): void {
    this.createChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.chart && (changes['labels'] || changes['datasets'])) {
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

    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels: this.labels,
        datasets: this.datasets.map(ds => ({
          ...ds,
          borderColor: ds.borderColor || '#6366f1',
          backgroundColor: ds.backgroundColor || 'rgba(99, 102, 241, 0.1)',
          tension: ds.tension ?? 0.35,
          fill: ds.fill ?? true,
          pointRadius: 3,
          pointHoverRadius: 6,
          pointBackgroundColor: ds.borderColor || '#6366f1'
        }))
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: this.showLegend,
            position: 'top',
            labels: {
              color: '#94a3b8',
              usePointStyle: true,
              pointStyle: 'circle',
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
      borderColor: ds.borderColor || '#6366f1',
      backgroundColor: ds.backgroundColor || 'rgba(99, 102, 241, 0.1)',
      tension: ds.tension ?? 0.35,
      fill: ds.fill ?? true,
      pointRadius: 3,
      pointHoverRadius: 6,
      pointBackgroundColor: ds.borderColor || '#6366f1'
    }));
    this.chart.update();
  }
}
