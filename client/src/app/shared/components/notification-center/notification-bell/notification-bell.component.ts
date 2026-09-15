import {
  Component,
  OnDestroy,
  OnInit,
  ChangeDetectionStrategy,
  signal,
  computed,
  input,
} from '@angular/core';
import { NgClass } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';

type BellSize = 'xs' | 'sm' | 'md' | 'lg';

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [NgClass, NgIconComponent],
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationBellComponent implements OnInit, OnDestroy {
  readonly deadline = input<string | Date | undefined>(undefined);
  readonly hasUnread = input(false);
  readonly size = input<BellSize>('sm');

  private readonly ACTIVE_TIME = 30;
  private intervalId?: ReturnType<typeof setInterval>;
  private readonly cycleTick = signal(0);

  readonly isDeadlineMode = computed(() => !!this.deadline());

  private readonly daysLeft = computed(() => {
    const d = this.deadline();
    if (!d) return Number.POSITIVE_INFINITY;
    const dl = this.toStartOfDay(new Date(d));
    const t = this.toStartOfDay(new Date());
    return Math.round((dl.getTime() - t.getTime()) / 86_400_000);
  });

  readonly isCritical = computed(() => {
    if (!this.isDeadlineMode()) return false;
    const dl = this.toStartOfDay(new Date(this.deadline()!));
    const today = this.toStartOfDay(new Date());
    return this.daysLeft() === 0 || dl.getTime() < today.getTime();
  });

  readonly isUrgent = computed(() => {
    if (!this.isDeadlineMode()) return false;
    const d = this.daysLeft();
    return d > 0 && d <= 2;
  });

  private readonly cycleTime = computed(() => {
    if (this.isCritical()) return 120;
    return 180;
  });

  readonly animationSpeed = computed(() => {
    if (this.isCritical()) return '1.2s';
    if (this.isUrgent()) return '1.4s';
    if (!this.isDeadlineMode() && this.hasUnread()) return '1.6s';
    return '1.5s';
  });

  readonly showPulse = computed(
    () =>
      this.isCritical() ||
      this.isUrgent() ||
      (this.hasUnread() && !this.isDeadlineMode()),
  );

  readonly isActiveCycle = computed(() => {
    this.cycleTick();
    const secondsInEpoch = Math.floor(Date.now() / 1000);
    return secondsInEpoch % this.cycleTime() < this.ACTIVE_TIME;
  });

  readonly wrapperClasses = computed<Record<string, boolean>>(() => {
    const sizeClass = `notif-bell--${this.size()}`;
    if (this.isDeadlineMode()) {
      if (this.isCritical())
        return { [sizeClass]: true, 'notif-bell--critical': true };
      if (this.isUrgent())
        return { [sizeClass]: true, 'notif-bell--urgent': true };
      return { [sizeClass]: true, 'notif-bell--idle': true };
    }
    if (this.hasUnread())
      return { [sizeClass]: true, 'notif-bell--unread': true };
    return { [sizeClass]: true, 'notif-bell--idle': true };
  });

  readonly bellStyles = computed<Record<string, string>>(() => ({
    '--bell-animation-duration': this.animationSpeed(),
  }));

  ngOnInit(): void {
    this.intervalId = setInterval(
      () => this.cycleTick.update((v) => v + 1),
      1000,
    );
  }

  ngOnDestroy(): void {
    if (this.intervalId) clearInterval(this.intervalId);
  }

  private toStartOfDay(d: Date): Date {
    const x = new Date(d);
    x.setHours(0, 0, 0, 0);
    return x;
  }
}
