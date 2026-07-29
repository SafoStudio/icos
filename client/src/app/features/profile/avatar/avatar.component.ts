import {
  Component,
  Input,
  Output,
  EventEmitter,
  inject,
  signal,
  computed,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';
import { AvatarService } from './avatar.service';
import { AvatarModalComponent } from './avatar-modal/avatar-modal.component';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule, NgIconComponent, AvatarModalComponent],
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss']
})
export class AvatarComponent implements OnChanges {
  @Input({ required: true }) userId!: string;
  @Input() fullName = '';
  @Input() avatarPath?: string;
  @Input() editable = true;

  @Output() avatarChanged = new EventEmitter<void>();

  private readonly avatarService = inject(AvatarService);

  readonly isModalOpen = signal<boolean>(false);
  readonly timestamp = signal<number>(Date.now());
  readonly imageError = signal<boolean>(false);

  readonly initialLetter = computed(() => {
    return this.fullName ? this.fullName.charAt(0).toUpperCase() : '?';
  });

  readonly avatarUrl = computed(() => {
    return this.avatarService.getAvatarUrl(this.userId, this.avatarPath, this.timestamp());
  });

  ngOnChanges(changes: SimpleChanges) {
    if (changes['avatarPath'] || changes['userId']) {
      this.imageError.set(false);
    }
  }

  onImageError() {
    this.imageError.set(true);
  }

  openModal() {
    if (this.editable) {
      this.isModalOpen.set(true);
    }
  }

  closeModal() {
    this.isModalOpen.set(false);
  }

  onAvatarSaved() {
    this.timestamp.set(Date.now());
    this.imageError.set(false);
    this.avatarChanged.emit();
  }

  onAvatarDeleted() {
    this.timestamp.set(Date.now());
    this.imageError.set(true);
    this.avatarChanged.emit();
  }
}
