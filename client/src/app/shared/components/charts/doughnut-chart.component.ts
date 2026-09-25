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

@Component({
  selector: 'app-doughnut-chart',
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
      display: flex;
      justify-content: center;
      align-items: center;
    }
    canvas {
      max-width: 100% !important;
      max-height: 100% !important;
    }
  `]
})
export class DoughnutChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  @Input() labels: string[] = [];
  @Input() data: number[] = [];
  @Input() colors: string[] = [
    '#6366f1', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6', '#06b6d4', '#3b82f6', '#14b8a6'
  ];
  @Input() height = 240;
  @Input() cutout = '70%';
  @Input() showLegend = true;

  private chart?: Chart;

  ngAfterViewInit(): void {
    this.createChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.chart && (changes['labels'] || changes['data'] || changes['colors'])) {
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

    const config: ChartConfiguration<'doughnut'> = {
      type: 'doughnut',
      data: {
        labels: this.labels,
        datasets: [{
          data: this.data,
          backgroundColor: this.colors,
          borderColor: '#1e293b',
          borderWidth: 2,
          hoverOffset: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: this.cutout,
        plugins: {
          legend: {
            display: this.showLegend,
            position: 'right',
            labels: {
              color: '#94a3b8',
              usePointStyle: true,
              pointStyle: 'circle',
              font: { family: 'inherit', size: 12 },
              padding: 12
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
                const total = (context.dataset.data as number[]).reduce((a, b) => a + b, 0);
                const val = context.raw as number;
                const percentage = total > 0 ? Math.round((val / total) * 100) : 0;
                return ` ${context.label}: ${val} (${percentage}%)`;
              }
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
    this.chart.data.datasets[0].data = this.data;
    this.chart.data.datasets[0].backgroundColor = this.colors;
    this.chart.update();
  }
}
