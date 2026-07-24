import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProfileService, ProfileUpdateInput, UserUpdateInput } from './profile.service';
import { AuthService, ToastService } from '@services';
import { type User, type UserProfile, type Department } from '@models';
import { NgIconComponent } from '@ng-icons/core';
import { TabsComponent, type TabItem } from '@components';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent, TabsComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  private readonly profileService = inject(ProfileService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  readonly loading = signal<boolean>(false);
  readonly saving = signal<boolean>(false);
  readonly user = signal<User | null>(null);
  readonly isEditing = signal<boolean>(false);
  readonly activeTabId = signal<string>('personal');
  readonly departments = signal<Department[]>([]);

  readonly tabs: TabItem[] = [
    { id: 'personal', label: 'Personal Information', icon: 'tablerUser' },
    { id: 'compensation', label: 'Compensation', icon: 'tablerCoin' },
    { id: 'documents', label: 'Documents', icon: 'tablerFile' }
  ];

  readonly fullName = computed(() => {
    const user = this.user();
    return user ? `${user.firstName} ${user.lastName}` : '';
  });

readonly departmentName = computed(() => {
  const u = this.user();
  if (!u?.departmentId) return 'Unassigned / General';
  const dept = this.departments().find(d => d.id === u.departmentId);
  return dept ? dept.name : 'Unassigned';
});

  readonly roleLabel = computed(() => {
    const user = this.user();
    if (!user) return '';

    const labels: Record<string, string> = {
      'OWNER': 'Company Owner',
      'ADMIN': 'System Administrator',
      'HEAD': 'Department Head',
      'SPECIALIST': 'Specialist'
    };

    return user.role && labels[user.role] ? labels[user.role] : user.role;
  });

  editUser: Partial<User> = {};
  editProfile: Partial<UserProfile> = {};

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.loading.set(true);
    this.profileService.getMe().subscribe({
      next: (user) => {
        this.user.set(user);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.toastService.error('Failed to load profile');
        this.loading.set(false);
      }
    });
  }

  startEdit() {
    const user = this.user();
    if (!user) return;

    this.editUser = {
      firstName: user.firstName,
      lastName: user.lastName
    };

    this.editProfile = {
      phoneNumber: user.profile?.phoneNumber || '',
      address: user.profile?.address || '',
      birthDate: user.profile?.birthDate || ''
    };

    this.isEditing.set(true);
  }

  cancelEdit() {
    this.isEditing.set(false);
    this.editUser = {};
    this.editProfile = {};
  }

  saveProfile() {
    const user = this.user();
    if (!user) return;

    this.saving.set(true);

    const userUpdate: UserUpdateInput = {};
    if (this.editUser.firstName && this.editUser.firstName !== user.firstName) {
      userUpdate.firstName = this.editUser.firstName;
    }
    if (this.editUser.lastName && this.editUser.lastName !== user.lastName) {
      userUpdate.lastName = this.editUser.lastName;
    }

    const profileUpdate: ProfileUpdateInput = {
      userId: user.id
    };

    if (this.editProfile.phoneNumber !== user.profile?.phoneNumber) {
      profileUpdate.phoneNumber = this.editProfile.phoneNumber;
    }
    if (this.editProfile.address !== user.profile?.address) {
      profileUpdate.address = this.editProfile.address;
    }
    if (this.editProfile.birthDate !== user.profile?.birthDate) {
      profileUpdate.birthDate = this.editProfile.birthDate;
    }

    const updates: Promise<any>[] = [];

    if (Object.keys(userUpdate).length > 0) {
      updates.push(
        this.profileService.updateUser(user.id, userUpdate).toPromise()
      );
    }

    if (Object.keys(profileUpdate).length > 1) {
      updates.push(
        this.profileService.updateProfile(profileUpdate).toPromise()
      );
    }

    Promise.all(updates)
      .then(() => {
        this.toastService.success('Profile updated successfully');
        this.isEditing.set(false);
        this.loadProfile();
        this.saving.set(false);
      })
      .catch((error) => {
        console.error('Error updating profile:', error);
        this.toastService.error('Failed to update profile');
        this.saving.set(false);
      });
  }

  formatDate(date?: string): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
