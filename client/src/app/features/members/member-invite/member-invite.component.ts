import { Component, EventEmitter, Input, Output, OnInit, OnChanges, SimpleChanges, inject, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ROLES } from '@models';

interface Department {
  id: string;
  name: string;
}

interface Member {
  id: string;
  firstName: string;
  lastName: string;
  role: string;
}

@Component({
  selector: 'app-member-invite',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './member-invite.component.html',
  styleUrls: ['./member-invite.component.scss']
})
export class MemberInviteComponent implements OnInit, OnChanges, AfterViewInit {
  @Input() departments: Department[] = [];
  @Input() members: Member[] = [];
  @Input() availableRoles: string[] = [];
  @Input() isInviting = false;
  @Input() error: string | null = null;
  @Input() success: string | null = null;

 @Output() closeModal = new EventEmitter<void>();
  @Output() submitInvite = new EventEmitter<any>();

  @ViewChild('inviteDialog') dialogRef!: ElementRef<HTMLDialogElement>;

  inviteForm!: FormGroup;
  private readonly fb = inject(FormBuilder);

  ngOnInit(): void {
    this.inviteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: [ROLES.SPECIALIST, [Validators.required]],
      departmentId: ['', [Validators.required]],
      managerId: ['']
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['departments'] && this.departments.length && this.inviteForm) {
      if (!this.inviteForm.get('departmentId')?.value) {
        this.inviteForm.patchValue({
          departmentId: this.departments[0]?.id || ''
        });
      }
    }
  }

  ngAfterViewInit(): void {
    if (this.dialogRef) {
      this.dialogRef.nativeElement.showModal();
    }
  }

  onSubmit(): void {
    if (this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }
    this.submitInvite.emit(this.inviteForm.value);
  }

  onClose(): void {
    this.closeModal.emit();
  }

  onDialogKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.onClose();
    }
    event.stopPropagation();
  }
}
