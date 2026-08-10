import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '@services';
import { NgIconComponent } from '@ng-icons/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, NgIconComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  showPassword = signal(false);

  // test credentials
  private readonly credentials = {
    ADMIN: {
      email: 'john.doe@acme.com',
      password: 'password123'
    },
    HR: {
      email: 'john.doe@acme.com',
      password: 'password123'
    },
    MEMBER: {
      email: 'bob.wilson@acme.com',
      password: 'password123'
    }
  };

  autoFill(role: 'ADMIN' | 'HR' | 'MEMBER'): void {
    const creds = this.credentials[role];
    this.loginForm.patchValue({
      email: creds.email,
      password: creds.password
    });
    this.errorMessage.set(null);

     this.onSubmit();
  }

  togglePasswordVisibility(): void {
    this.showPassword.update(value => !value);
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.message || 'Invalid email or password');
      }
    });
  }
}
