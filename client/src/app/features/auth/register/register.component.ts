import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService, InviteResponse } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  companyForm!: FormGroup;
  inviteForm!: FormGroup;

  token = signal<string | null>(null);
  inviteInfo = signal<InviteResponse | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const t = params['token'];
      if (t) {
        this.token.set(t);
        this.initInviteForm();
        this.fetchInviteInfo(t);
      } else {
        this.token.set(null);
        this.initCompanyForm();
      }
    });
  }

  private initCompanyForm(): void {
    this.companyForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      companyName: ['', [Validators.required]]
    });
  }

  private initInviteForm(): void {
    this.inviteForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  private fetchInviteInfo(tokenStr: string): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.authService.getInviteInfo(tokenStr).subscribe({
      next: (info) => {
        this.isLoading.set(false);
        this.inviteInfo.set(info);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || err.message || 'Invitation is invalid, canceled, or expired.');
      }
    });
  }

  onCompanySubmit(): void {
    if (this.companyForm.invalid) {
      this.companyForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const { email, password, firstName, lastName, companyName } = this.companyForm.value;

    this.authService.registerCompany(email, password, firstName, lastName, companyName).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Company and administrator registered successfully!');
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1500);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.message || 'Registration failed. The user email may already be in use.');
      }
    });
  }

  onInviteSubmit(): void {
    if (this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }

    const t = this.token();
    if (!t) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const body = this.inviteForm.value;

    this.authService.acceptInvite(t, body).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Welcome! Registration completed successfully.');
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1500);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || err.message || 'Failed to accept invitation.');
      }
    });
  }
}
