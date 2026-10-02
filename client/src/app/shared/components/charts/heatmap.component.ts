import { Component, Input, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TimeOffDay } from '@services';

interface HeatmapCell {
  date: string;
  departmentId: string;
  departmentName: string;
  absentCount: number;
  totalCount: number;
  loadPercent: number;
}

interface HeatmapRow {
  departmentId: string;
  departmentName: string;
  cells: HeatmapCell[];
}

@Component({
  selector: 'app-heatmap',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="heatmap-wrapper">
      @if (title) {
        <div class="heatmap-title">{{ title }}</div>
      }

      <div class="heatmap-scroll-container">
        <table class="heatmap-table">
          <thead>
            <tr>
              <th class="dept-header-cell">Department</th>
              @for (date of uniqueDates; track date) {
                <th class="date-header-cell" [title]="date">
                  <div class="date-day">{{ formatDay(date) }}</div>
                  <div class="date-weekday">{{ formatWeekday(date) }}</div>
                </th>
              }
            </tr>
          </thead>
          <tbody>
            @for (row of heatmapRows; track row.departmentId) {
              <tr>
                <td class="dept-name-cell" [title]="row.departmentName">
                  {{ row.departmentName }}
                </td>
                @for (cell of row.cells; track cell.date) {
                  <td class="cell-wrapper">
                    <div
                      class="heatmap-cell"
                      [style.background-color]="getCellColor(cell.loadPercent)"
                      (mouseenter)="setHoveredCell(cell, $event)"
                      (mouseleave)="clearHoveredCell()"
                    >
                      @if (cell.absentCount > 0) {
                        <span class="cell-label">{{ cell.absentCount }}</span>
                      }
                    </div>
                  </td>
                }
              </tr>
            }
          </tbody>
        </table>
      </div>

      <!-- Hover Tooltip -->
      @if (hoveredCell()) {
        <div
          class="heatmap-tooltip"
          [style.left.px]="tooltipX()"
          [style.top.px]="tooltipY()"
        >
          <div class="tooltip-header">{{ hoveredCell()?.departmentName }}</div>
          <div class="tooltip-date">{{ hoveredCell()?.date }}</div>
          <div class="tooltip-detail">
            <span>Absent:</span>
            <strong>{{ hoveredCell()?.absentCount }} / {{ hoveredCell()?.totalCount }}</strong>
          </div>
          <div class="tooltip-detail">
            <span>Load / Impact:</span>
            <strong [style.color]="getCellTextColor(hoveredCell()?.loadPercent || 0)">
              {{ hoveredCell()?.loadPercent }}%
            </strong>
          </div>
        </div>
      }

      <!-- Heatmap Legend -->
      <div class="heatmap-legend">
        <span class="legend-title">Time-off Intensity:</span>
        <div class="legend-scale">
          <div class="legend-item">
            <span class="legend-box level-0"></span>
            <span>0%</span>
          </div>
          <div class="legend-item">
            <span class="legend-box level-1"></span>
            <span>1-15%</span>
          </div>
          <div class="legend-item">
            <span class="legend-box level-2"></span>
            <span>16-30%</span>
          </div>
          <div class="legend-item">
            <span class="legend-box level-3"></span>
            <span>31-50%</span>
          </div>
          <div class="legend-item">
            <span class="legend-box level-4"></span>
            <span>&gt;50%</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .heatmap-wrapper {
      width: 100%;
      position: relative;
      background: var(--bg-surface, #1e293b);
      border-radius: 12px;
      padding: 16px;
      overflow: hidden;
    }
    .heatmap-title {
      font-size: 0.95rem;
      font-weight: 600;
      color: var(--text-primary, #f8fafc);
      margin-bottom: 12px;
    }
    .heatmap-scroll-container {
      overflow-x: auto;
      max-width: 100%;
      scrollbar-width: thin;
      scrollbar-color: rgba(255, 255, 255, 0.2) transparent;
    }
    .heatmap-table {
      width: 100%;
      border-collapse: separate;
      border-spacing: 4px;
      min-width: 500px;
    }
    .dept-header-cell {
      text-align: left;
      font-size: 0.75rem;
      color: #94a3b8;
      padding: 6px 10px;
      min-width: 140px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
    .date-header-cell {
      padding: 4px;
      text-align: center;
      min-width: 32px;
    }
    .date-day {
      font-size: 0.75rem;
      font-weight: 600;
      color: var(--text-primary, #f8fafc);
    }
    .date-weekday {
      font-size: 0.65rem;
      color: #94a3b8;
      text-transform: uppercase;
    }
    .dept-name-cell {
      font-size: 0.82rem;
      font-weight: 500;
      color: var(--text-primary, #f8fafc);
      padding: 6px 10px;
      white-space: nowrap;
    }
    .cell-wrapper {
      padding: 2px;
    }
    .heatmap-cell {
      width: 32px;
      height: 32px;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: transform 0.15s ease, filter 0.15s ease;
      position: relative;
    }
    .heatmap-cell:hover {
      transform: scale(1.15);
      filter: brightness(1.2);
      z-index: 2;
    }
    .cell-label {
      font-size: 0.7rem;
      font-weight: 700;
      color: #ffffff;
      text-shadow: 0 1px 2px rgba(0,0,0,0.5);
    }
    .heatmap-tooltip {
      position: fixed;
      z-index: 1000;
      background: #0f172a;
      border: 1px solid #334155;
      border-radius: 8px;
      padding: 8px 12px;
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.4);
      pointer-events: none;
      transform: translate(-50%, -115%);
      min-width: 140px;
    }
    .tooltip-header {
      font-size: 0.8rem;
      font-weight: 600;
      color: #f8fafc;
    }
    .tooltip-date {
      font-size: 0.7rem;
      color: #94a3b8;
      margin-bottom: 6px;
    }
    .tooltip-detail {
      font-size: 0.72rem;
      color: #cbd5e1;
      display: flex;
      justify-content: space-between;
      gap: 8px;
      margin-top: 2px;
    }
    .heatmap-legend {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-top: 14px;
      padding-top: 12px;
      border-top: 1px solid rgba(255, 255, 255, 0.08);
      font-size: 0.75rem;
      color: #94a3b8;
    }
    .legend-scale {
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .legend-item {
      display: flex;
      align-items: center;
      gap: 5px;
    }
    .legend-box {
      width: 12px;
      height: 12px;
      border-radius: 3px;
    }
    .level-0 { background-color: rgba(255, 255, 255, 0.05); }
    .level-1 { background-color: rgba(16, 185, 129, 0.4); }
    .level-2 { background-color: rgba(56, 189, 248, 0.55); }
    .level-3 { background-color: rgba(245, 158, 11, 0.7); }
    .level-4 { background-color: rgba(239, 68, 68, 0.85); }
  `]
})
export class HeatmapComponent {
  @Input() data: TimeOffDay[] = [];
  @Input() title = '';

  readonly hoveredCell = signal<HeatmapCell | null>(null);
  readonly tooltipX = signal<number>(0);
  readonly tooltipY = signal<number>(0);

  get uniqueDates(): string[] {
    const dates = Array.from(new Set(this.data.map(d => d.date)));
    return dates.sort();
  }

  get heatmapRows(): HeatmapRow[] {
    const dates = this.uniqueDates;
    const deptsMap = new Map<string, { id: string; name: string; cellsMap: Map<string, HeatmapCell> }>();

    for (const item of this.data) {
      if (!deptsMap.has(item.departmentId)) {
        deptsMap.set(item.departmentId, {
          id: item.departmentId,
          name: item.departmentName || 'General',
          cellsMap: new Map()
        });
      }
      deptsMap.get(item.departmentId)!.cellsMap.set(item.date, {
        date: item.date,
        departmentId: item.departmentId,
        departmentName: item.departmentName,
        absentCount: item.absentCount,
        totalCount: item.totalCount,
        loadPercent: item.loadPercent
      });
    }

    const rows: HeatmapRow[] = [];
    deptsMap.forEach(dept => {
      const cells: HeatmapCell[] = dates.map(date => {
        return (
          dept.cellsMap.get(date) || {
            date,
            departmentId: dept.id,
            departmentName: dept.name,
            absentCount: 0,
            totalCount: 20,
            loadPercent: 0
          }
        );
      });
      rows.push({
        departmentId: dept.id,
        departmentName: dept.name,
        cells
      });
    });

    return rows;
  }

  formatDay(dateStr: string): string {
    try {
      const d = new Date(dateStr);
      return `${d.getDate()}`;
    } catch {
      return dateStr;
    }
  }

  formatWeekday(dateStr: string): string {
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString('en-US', { weekday: 'narrow' });
    } catch {
      return '';
    }
  }

  getCellColor(percent: number): string {
    if (percent <= 0) return 'rgba(255, 255, 255, 0.05)';
    if (percent <= 15) return 'rgba(16, 185, 129, 0.45)';
    if (percent <= 30) return 'rgba(56, 189, 248, 0.55)';
    if (percent <= 50) return 'rgba(245, 158, 11, 0.7)';
    return 'rgba(239, 68, 68, 0.85)';
  }

  getCellTextColor(percent: number): string {
    if (percent <= 15) return '#34d399';
    if (percent <= 30) return '#38bdf8';
    if (percent <= 50) return '#fbbf24';
    return '#f87171';
  }

  setHoveredCell(cell: HeatmapCell, event: MouseEvent): void {
    this.hoveredCell.set(cell);
    this.tooltipX.set(event.clientX);
    this.tooltipY.set(event.clientY);
  }

  clearHoveredCell(): void {
    this.hoveredCell.set(null);
  }
}
