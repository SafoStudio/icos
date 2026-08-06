import { Injectable, signal, effect } from '@angular/core';

export type ThemeMode = 'dark' | 'light';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'icos-app-theme';
  readonly currentTheme = signal<ThemeMode>(this.getInitialTheme());

  constructor() {
    effect(() => {
      const mode = this.currentTheme();
      localStorage.setItem(this.THEME_KEY, mode);
      document.documentElement.dataset['theme'] = mode;
      if (mode === 'dark') {
        document.body.classList.add('dark-theme');
        document.body.classList.remove('light-theme');
      } else {
        document.body.classList.add('light-theme');
        document.body.classList.remove('dark-theme');
      }
    });
  }

  private getInitialTheme(): ThemeMode {
    const saved = localStorage.getItem(this.THEME_KEY) as ThemeMode | null;
    if (saved === 'dark' || saved === 'light') {
      return saved;
    }
    return 'dark';
  }

  toggleTheme(): void {
    this.currentTheme.set(this.currentTheme() === 'dark' ? 'light' : 'dark');
  }

  setTheme(mode: ThemeMode): void {
    this.currentTheme.set(mode);
  }

  isDark(): boolean {
    return this.currentTheme() === 'dark';
  }
}
